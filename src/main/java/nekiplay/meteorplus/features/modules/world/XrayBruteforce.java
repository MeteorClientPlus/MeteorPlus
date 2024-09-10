package nekiplay.meteorplus.features.modules.world;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.events.meteor.KeyEvent;
import meteordevelopment.meteorclient.events.meteor.MouseButtonEvent;
import meteordevelopment.meteorclient.systems.modules.Categories;
import meteordevelopment.meteorclient.systems.modules.render.blockesp.ESPBlock;
import meteordevelopment.meteorclient.systems.modules.render.blockesp.ESPBlockData;
import meteordevelopment.meteorclient.systems.modules.render.blockesp.ESPChunk;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.misc.UnorderedArrayList;
import meteordevelopment.meteorclient.utils.misc.input.KeyAction;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.RainbowColors;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.meteorclient.utils.world.TickRate;
import nekiplay.meteorplus.MeteorPlusAddon;
import nekiplay.meteorplus.utils.GenerationBlock;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

import meteordevelopment.meteorclient.events.entity.player.BreakBlockEvent;
import meteordevelopment.meteorclient.events.render.Render2DEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.BlockUpdateEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.Utils;
import meteordevelopment.meteorclient.utils.entity.EntityUtils;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import nekiplay.meteorplus.utils.xraybruteforce.XBlock;
import nekiplay.meteorplus.utils.xraybruteforce.XChunk;
import nekiplay.meteorplus.utils.xraybruteforce.XGroup;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;

public class XrayBruteforce extends Module {
    public XrayBruteforce() {
        super(Categories.World, "xray-bruteForce", "Bypasses anti-xray.");
		RainbowColors.register(this::onTickRainbow);
    }
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private final SettingGroup sgSVisual = settings.createGroup("Scaner");
    private final SettingGroup sgRVisual = settings.createGroup("Scaner Render Visuals");
	private final SettingGroup sgSRenderer = settings.createGroup("Scanned Renderer");
	private final SettingGroup sgSaver = settings.createGroup("Scanned Saver");
	private final SettingGroup sgDelayer = settings.createGroup("Scanned Delayer");

	public final Setting<Boolean> autoSave = sgSaver.add(new BoolSetting.Builder()
		.name("Auto save")
		.description("Save rendered ores.")
		.defaultValue(true)
		.build()
	);

	public final Setting<Boolean> save = sgSaver.add(new BoolSetting.Builder()
		.name("Save")
		.description("Save rendered ores.")
		.defaultValue(false)
		.onChanged((c) -> {
			if (isActive()) {
				Thread saveth = new Thread(() ->
				{
					for (RenderOre ore : ores.toArray(new RenderOre[0]))
						saveRenderOre(ore);
					info("Saving complete");
				});
				saveth.start();
			}
		})
		.build()
	);

    public final Setting<Boolean> load = sgSaver.add(new BoolSetting.Builder()
		.name("Load")
		.description("Load rendered ores.")
		.defaultValue(false)
		.onChanged((c) -> {
			if (isActive()) {
				loadSaveOres();
				info("Loaded");
			}
		})
		.build()
	);

	private void loadSaveOres() {
		Thread loadth = new Thread(() ->
		{
			File dir = new File(MeteorClient.FOLDER, "xray-bruteforce");
			if (dir.exists()) {
				File dir2 = new File(dir, Utils.getWorldName());
				if (dir2.exists()) {
					File[] arrFiles = dir2.listFiles();
					List<File> lst = Arrays.asList(arrFiles);
					for (File file : lst) {
						FileReader fr = null;
						try {
							fr = new FileReader(file);
							BufferedReader reader = new BufferedReader(fr);
							try {
								String json = reader.readLine();
								Gson gson = new Gson();
								Type type = new TypeToken<Map<String, Integer>>(){}.getType();
								Map<String, Integer> read = gson.fromJson(json, type);
								BlockPos pos = new BlockPos(read.get("X"), read.get("Y"), read.get("Z"));
                                if (EntityUtils.isInRenderDistance(pos)) {
                                    addBlock(pos, true);
                                }

							} catch (IOException e) {
								e.printStackTrace();
							}
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		loadth.start();
	}

	private void saveRenderOre(RenderOre ore) {
		File dir = new File(MeteorClient.FOLDER, "xray-bruteforce");
		if (!dir.exists()) {
			dir.mkdir();
		}
		File dir2 = new File(dir, Utils.getWorldName());
		if (!dir2.exists()) {
			dir2.mkdir();
		}
		Gson gson = new Gson();
		Map<String, Integer> map = new LinkedHashMap<>();
		map.put("X", ore.blockPos.getX());
		map.put("Y", ore.blockPos.getY());
		map.put("Z", ore.blockPos.getZ());
		String json = gson.toJson(map);

		File file = new File(dir2.getPath(), "x={" + ore.blockPos.getX() + "}, y={" + ore.blockPos.getY() + "}, {z={" + ore.blockPos.getZ() + "}");
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileWriter fileWriter = new FileWriter(file);
			PrintWriter printWriter = new PrintWriter(fileWriter);
			printWriter.print(json);
			printWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public final Setting<Boolean> new_render = sgSRenderer.add(new BoolSetting.Builder()
		.name("New Render")
		.description("New Render.")
		.defaultValue(true)
		.build()
	);

    private final Setting<List<Block>> whblocks = sgGeneral.add(new BlockListSetting.Builder()
        .name("whitelist")
        .description("Which blocks to show x-rayed.")
        .defaultValue(
            Blocks.DIAMOND_ORE,
            Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.ANCIENT_DEBRIS
        )
        .onChanged(v -> scanned.clear())
        .build()
    );


	private final Setting<ESPBlockData> defaultBlockConfig = sgGeneral.add(new GenericSetting.Builder<ESPBlockData>()
		.name("whitelist-default-config")
		.description("Default block config.")
		.defaultValue(
			new ESPBlockData(
				ShapeMode.Lines,
				new SettingColor(0, 255, 200),
				new SettingColor(0, 255, 200, 25),
				false,
				new SettingColor(0, 255, 200, 125)
			)
		)
		.build()
	);

	private final Setting<Map<Block, ESPBlockData>> blockConfigs = sgGeneral.add(new BlockDataSetting.Builder<ESPBlockData>()
		.name("whitelist-block-configs")
		.description("Config for each block.")
		.defaultData(defaultBlockConfig)
		.onChanged(v -> {
			synchronized (ores) {
				for (RenderOre ore : ores) {
					ESPBlockData data = getBlockData(ore.block);
					ore.linecolor = data.lineColor;
					ore.sidecolor = data.sideColor;
					ore.tracercolor = data.tracerColor;
					ore.shapeMode = data.shapeMode;
				}
			}
		})
		.build()
	);

    private final Setting<PacketMode> packet_first = sgGeneral.add(new EnumSetting.Builder<PacketMode>()
        .name("packet-#1")
        .description("First packet.")
        .defaultValue(PacketMode.Start)
        .build()
    );

	private final Setting<PacketMode> packet_two = sgGeneral.add(new EnumSetting.Builder<PacketMode>()
		.name("packet-#2")
		.description("Two packet.")
		.defaultValue(PacketMode.Abort)
		.build()
	);

	private final Setting<GenerationType> generationType = sgGeneral.add(new EnumSetting.Builder<GenerationType>()
		.name("Generation-type")
		.description("Ores generation type.")
		.defaultValue(GenerationType.Old)
		.build()
	);

    public enum PacketMode
    {
		None,
        Start,
        Abort,
		Stop,
    }

    public final Setting<Boolean> clear_cache_blocks = sgGeneral.add(new BoolSetting.Builder()
        .name("Clear-cache")
        .description("Clear saved cache.")
        .defaultValue(false)
        .build()
    );

    public final Setting<Boolean> clear_cache_ores = sgGeneral.add(new BoolSetting.Builder()
        .name("Clear-rendered-ores-cache")
        .description("Clear saved ores cache.")
        .defaultValue(false)
        .build()
    );

	public final Setting<Integer> rescanerDelay = sgDelayer.add(new IntSetting.Builder()
		.name("rescaner-delay")
		.description("Deley for rechecking blobk.")
		.defaultValue(2000)
		.build()
	);

	public final Setting<Boolean> tps_sync = sgDelayer.add(new BoolSetting.Builder()
		.name("TPS-sync")
		.description("TPS sync scaning.")
		.defaultValue(true)
		.build()
	);

    public final Setting<Boolean> fps_sync = sgDelayer.add(new BoolSetting.Builder()
        .name("FPS-sync")
        .description("FPS sync scaning.")
        .defaultValue(false)
        .build()
    );

    public final Setting<Boolean> auto_height = sgSVisual.add(new BoolSetting.Builder()
        .name("Auto-height")
        .description("Auto detect height.")
        .defaultValue(false)
        .build()
    );

	public final Setting<Boolean> auto_dimension = sgSVisual.add(new BoolSetting.Builder()
		.name("Auto-dimension")
		.description("Auto detect dimension.")
		.defaultValue(true)
		.visible(auto_height::get)
		.build()
	);

	public final Setting<Boolean> expanded = sgSVisual.add(new BoolSetting.Builder()
		.name("exposed")
		.description("Scan exposed blocks.")
		.defaultValue(true)
		.build()
	);

	private final Setting<ScanPriority> scanPriority = sgSVisual.add(new EnumSetting.Builder<ScanPriority>()
		.name("Scan priority")
		.description("Ores generation type.")
		.defaultValue(ScanPriority.Normal)
		.build()
	);

	private final Setting<Integer> cavesRange = sgSVisual.add(new IntSetting.Builder()
		.name("Caves range")
		.description("Caves range.")
		.defaultValue(2)
		.visible(() -> scanPriority.get() == ScanPriority.Caves)
		.build()
	);

	private final Setting<Integer> cavesRangeY = sgSVisual.add(new IntSetting.Builder()
		.name("Caves Y range")
		.description("Caves y range.")
		.defaultValue(2)
		.visible(() -> scanPriority.get() == ScanPriority.Caves)
		.build()
	);

	public enum ScanPriority {
		Caves,
		Normal
	}

    private final Setting<Integer> range = sgSVisual.add(new IntSetting.Builder()
        .name("range")
        .description("Bruteforce range.")
        .defaultValue(40)
        .min(3)
        .sliderRange(3, 512)
        .build()
    );
    private final Setting<Integer> y_range = sgSVisual.add(new IntSetting.Builder()
        .name("y-range")
        .description("Bruteforce range.")
        .defaultValue(13)
        .min(3)
        .sliderRange(3, 255)
        .build()
    );

    private final Setting<Integer> delaymin = sgDelayer.add(new IntSetting.Builder()
        .name("Scan delay min")
        .description("Bruteforce delay min .")
        .defaultValue(30)
        .min(0)
        .sliderRange(0, 150)
        .build()
    );

	private final Setting<Integer> delaymax = sgDelayer.add(new IntSetting.Builder()
		.name("Scan delay max")
		.description("Bruteforce delay max .")
		.defaultValue(35)
		.min(0)
		.sliderRange(0, 150)
		.build()
	);

	private final Setting<Boolean> pauseBind = sgGeneral.add(new BoolSetting.Builder()
		.name("pause-bind")
		.description("Allow use pause bind.")
		.defaultValue(true)
		.build()
	);

	private final Setting<Keybind> pausekeybind = sgGeneral.add(new KeybindSetting.Builder()
		.name("pause-keybind")
		.description("The bind for pause.")
		.defaultValue(Keybind.fromKey(GLFW_KEY_X))
		.visible(pauseBind::get)
		.build()
	);

	private final Setting<Boolean> outline = sgRVisual.add(new BoolSetting.Builder()
		.name("outline")
		.description("Outline to block.")
		.defaultValue(true)
		.build()
	);

	private final Setting<SettingColor> outlineColor = sgRVisual.add(new ColorSetting.Builder()
		.name("outline-color")
		.description("Outline color to block.")
		.defaultValue(new SettingColor(255, 255, 0, 255))
		.visible(outline::get)
		.build()
	);

	private final Setting<Boolean> tracer = sgRVisual.add(new BoolSetting.Builder()
		.name("tracer")
		.description("Tracer to block.")
		.defaultValue(true)
		.build()
	);

	private final Setting<SettingColor> tracerColor = sgRVisual.add(new ColorSetting.Builder()
		.name("tracer-color")
		.description("Tracer color to block.")
		.defaultValue(new SettingColor(255, 255, 0, 255))
		.visible(tracer::get)
		.build()
	);

	private final Setting<Integer> clusterRange = sgSVisual.add(new IntSetting.Builder()
		.name("Cluster range")
		.description("Cluster range.")
		.defaultValue(2)
		.min(1)
		.sliderRange(1, 2)
		.build()
	);

	private boolean isPressed() {
		return (pausekeybind.get().isPressed() && pauseBind.get());
	}

    private BlockPos currentScanBlock;

	private boolean pause_toggle = true;

	private void onTickRainbow() {
		if (isActive()) {
			try {
				if (defaultBlockConfig != null && blockConfigs != null && defaultBlockConfig.get() != null) {
					defaultBlockConfig.get().tickRainbow();
					if (blockConfigs.get() != null) {
						Collection<ESPBlockData> datas = blockConfigs.get().values();
						if (datas.size() > 0) {
							for (ESPBlockData blockData : datas) {
								if (blockData != null) {
									blockData.tickRainbow();
								}
							}
						}
					}
				}
			}
			catch (NullPointerException ignore) { }
		}
	}

	@EventHandler
	private void onKeyEvent(KeyEvent event)
	{
		if (event.action == KeyAction.Press && isPressed()) {
			pause_toggle = !pause_toggle;
			if (pause_toggle) {
				currentScanBlock = null;
				info("§c" + "Paused");
			}
			else {
				info("§a" + "Un paused");
			}
		}
	}

	@EventHandler
	private void onMouseButtonEvent(MouseButtonEvent event) {
		if (event.action == KeyAction.Press && isPressed()) {
			pause_toggle = !pause_toggle;
			if (pause_toggle) {
				currentScanBlock = null;
				info("§c" + "Paused");
			}
			else {
				info("§a" + "Un paused");
			}
		}
	}

	public class RenderOre {
        public Block block = Blocks.AIR;
        public BlockPos blockPos = null;

		public RenderOre(BlockPos pos) {
			this.blockPos = pos;
		}

		public XBlock sBlock = null;

		/* Visual settings */
		public SettingColor linecolor = null;
		public SettingColor sidecolor = null;
		public SettingColor tracercolor = null;
		public ShapeMode shapeMode = null;
    }

	private final List<XGroup> groups = new UnorderedArrayList<>();
	public HashMap<BlockPos, XBlock> oresV3 = new HashMap<BlockPos, XBlock>();
	public XBlock getSBlockData(BlockPos pos) {
		if (oresV3.containsKey(pos))
		{
			return oresV3.get(pos);
		}
		return null;
	}

	public XGroup newGroup(Block block) {
		synchronized (chunks) {
			XGroup group = new XGroup(block);
			groups.add(group);
			return group;
		}
	}

	public void removeGroup(XGroup group) {
		synchronized (chunks) {
			groups.remove(group);
		}
	}

	private final Long2ObjectMap<XChunk> chunks = new Long2ObjectOpenHashMap<>();

	public XBlock getBlock(int x, int y, int z) {
		XChunk chunk = chunks.get(ChunkPos.toLong(x >> 4, z >> 4));
		return chunk == null ? null : chunk.get(x, y, z);
	}

	private boolean setColors(RenderOre ore)
	{
		if (ore != null && ore.block != null && ore.linecolor == null && ore.sidecolor == null && ore.tracercolor == null && ore.shapeMode == null && ore.sBlock == null) {
			assert mc.world != null;
			BlockState state = mc.world.getBlockState(ore.blockPos);
			if (ore.block == Blocks.AIR) {
				ore.block = state.getBlock();
			}
			ESPBlockData blockdata = getBlockData(state.getBlock());
			XBlock sbp = new XBlock(ore.blockPos.getX(), ore.blockPos.getY(), ore.blockPos.getZ());

			ore.sBlock = sbp;
			ore.sBlock.update();
			ore.linecolor = blockdata.lineColor;
			ore.sidecolor = blockdata.sideColor;
			ore.tracercolor = blockdata.tracerColor;
			ore.shapeMode = blockdata.shapeMode;
			return true;
		}
		return false;
	}

    public static final List<RenderOre> ores = new ArrayList<>();
    private RenderOre getRenderOre(BlockPos pos) {
		synchronized (ores) {
			for (RenderOre cur : ores) {
				if (cur != null && cur.block != null && cur.blockPos != null && cur.blockPos.equals(pos)) {
					return cur;
				}
			}
			return null;
		}
	}
	private void removeRenderOre(BlockPos pos) {
		synchronized (ores) {
			ores.removeIf(cur -> cur != null && cur.block != null && cur.blockPos != null && cur.blockPos.equals(pos));
		}
	}
    private void addRenderBlock(BlockPos blockPos) {
        synchronized (ores) {
            RenderOre ore = getRenderOre(blockPos);
            if (ore == null) {
                RenderOre ne = new RenderOre(blockPos);
                ne.blockPos = blockPos;
                ores.add(ne);
            }
			else if (ore.sBlock != null) {
				ore.sBlock.update();
			}
        }
    }

	public enum GenerationType {
		Old,
		New,
	}

	public ArrayList<BlockScanned> need_rescan = new ArrayList<BlockScanned>();

	public class BlockScanned {
		public BlockPos pos;
		public long rescanTime;
	}

	private void addNeedRescan(BlockPos pos, int delay) {
		synchronized (need_rescan) {
			BlockScanned scanned = new BlockScanned();
			scanned.pos = pos;
			scanned.rescanTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + delay;
			need_rescan.add(scanned);
		}
	}

    @EventHandler
    public void blockUpdateEvent(BlockUpdateEvent event) {
		if (!event.oldState.isAir()) {
			if (scanned.contains(event.pos)) {
				if (whblocks.get().contains(event.newState.getBlock()) && !pause_toggle) {
					addBlock(event.pos, true);
					List<BlockPos> post = getBlocks(event.pos, cavesRange.get(), cavesRangeY.get());
					for (BlockPos pos2 : post) {
						addBlock(pos2, true);
					}
				}
			}
		}
		if (event.newState.isAir()) {
			RenderOre render = getRenderOre(event.pos);
			if (render != null) {
				synchronized (ores) {
					ores.remove(render);
				}
			}
		}
    }

    @EventHandler
    private void minedBlock(BreakBlockEvent event) {
        new Thread(() -> {
            RenderOre ore = getRenderOre(event.blockPos);
            if (ore != null) {
                ore.block = Blocks.AIR;
				updateRenderedOres();
				synchronized (ores) {
					ores.remove(ore);
				}
            }
        }).start();
    }


    private void addBlock(BlockPos pos, Boolean ignore) {
        if (!scanned.contains(pos) && !ignore) {
            blocks.add(pos);
			scanned.add(pos);
        } else if (ignore) {
			blocks.add(pos);
			if (!scanned.contains(pos)) {
				scanned.add(pos);
			}
		}
    }

	public ESPBlockData getBlockData(Block block) {
		ESPBlockData blockData = blockConfigs.get().get(block);
		return blockData == null ? defaultBlockConfig.get() : blockData;
	}

	private void updateRenderedOres() {
		if (ores.size() > 0) {
			for (RenderOre pos : ores.toArray(new RenderOre[0])) {
				BlockState state = mc.world.getBlockState(pos.blockPos);
				if (state.getBlock() == pos.block) {
					pos.sBlock.update();
				}
			}
		}
	}

    private void renderOres(Render3DEvent event) {
		int renderBlocks = 0;
		if (ores.size() > 0) {
			for (RenderOre pos : ores.toArray(new RenderOre[0])) {
				if (setColors(pos))
				{
					if (autoSave.get()) {
						Thread saveth = new Thread(() -> {
							for (RenderOre ore : ores.toArray(new RenderOre[0])) {
								saveRenderOre(ore);
							}
						});
						saveth.start();
					}
				}
				if (!new_render.get()) {
					if (EntityUtils.isInRenderDistance(pos.blockPos) && pos.block != null && whblocks.get().contains(pos.block)) {
						renderOreBlock(event, pos);
						renderBlocks++;
					}
				}
				else {
					if (pos.sBlock != null) {
						if (EntityUtils.isInRenderDistance(pos.blockPos) && pos.block != null && whblocks.get().contains(pos.block)) {
							pos.sBlock.render(event, pos);
							if (pos.sBlock.group != null) {
								pos.sBlock.group.render(event);
							}
							renderBlocks++;
						}
					}
				}
			}
		}
		renderedBlocks = renderBlocks;
	}
    private int renderedBlocks = 0;
    private void renderOreBlock(Render3DEvent event, RenderOre ore)
    {
        if (ore.block != null && mc.world != null && ore.tracercolor != null && ore.sidecolor != null && ore.linecolor != null) {
			BlockState state = mc.world.getBlockState(ore.blockPos);
            VoxelShape shape = state.getOutlineShape(mc.world, ore.blockPos);
			ESPBlockData blockdata = getBlockData(ore.block);
			if (shape.isEmpty()) return;
            for (Box b : shape.getBoundingBoxes()) {
                event.renderer.box(ore.blockPos.getX() + b.minX, ore.blockPos.getY() + b.minY, ore.blockPos.getZ() + b.minZ, ore.blockPos.getX() + b.maxX, ore.blockPos.getY() + b.maxY, ore.blockPos.getZ() + b.maxZ, ore.sidecolor, ore.linecolor, blockdata.shapeMode, 0);
            }
			if (blockdata.tracer) {
				event.renderer.line(RenderUtils.center.x, RenderUtils.center.y, RenderUtils.center.z, ore.blockPos.getX(), ore.blockPos.getY(), ore.blockPos.getZ(), ore.tracercolor);
			}
        }
    }
    private boolean lagging = false;
    @EventHandler
    private void render2d(Render2DEvent event)
    {
        lagging = false;
    }
    @EventHandler
    private void onRender(Render3DEvent event) {
        if (clear_cache_blocks.get())
        {
            scanned.clear();
			need_rescan.clear();
            clear_cache_blocks.set(false);
            info("Cache checked blocks cleared");
        }
        if (clear_cache_ores.get())
        {
            ores.clear();
            clear_cache_ores.set(false);
            info("Cache render ores cleared");
        }
		if (currentScanBlock != null) {
			BlockPos bp = currentScanBlock;
			assert mc.world != null;
			BlockState state = mc.world.getBlockState(bp);
			VoxelShape shape = state.getOutlineShape(mc.world, bp);

			if (shape.isEmpty()) return;
			if (outline.get()) {
				for (Box b : shape.getBoundingBoxes()) {
					event.renderer.box(bp.getX() + b.minX, bp.getY() + b.minY, bp.getZ() + b.minZ, bp.getX() + b.maxX, bp.getY() + b.maxY, bp.getZ() + b.maxZ, new SettingColor(255, 255, 255, 255), outlineColor.get(), ShapeMode.Lines, 0);
				}
			}
			if (tracer.get()) {
				event.renderer.line(RenderUtils.center.x, RenderUtils.center.y, RenderUtils.center.z, bp.getX(), bp.getY(), bp.getZ(), tracerColor.get());
			}
		}
    }

	@EventHandler
	private void onRenderOres(Render3DEvent event) {
		renderOres(event);
	}
	private void addCaves(Chunk chunk) {
		if (scanPriority.get() == ScanPriority.Caves) {
			ArrayList<Block> caf = new ArrayList<Block>();
			caf.add(Blocks.AIR);
			ESPChunk s = ESPChunk.searchChunk(chunk, caf);
			if (s.blocks != null) {
				for (ESPBlock sBlock : s.blocks.values()) {
					for (Block bp : whblocks.get()) {
						boolean newGeneration = generationType.get() == GenerationType.New;
						GenerationBlock generationBlock = GenerationBlock.getGenerationBlock(bp, newGeneration);
						if (generationBlock != null) {
							BlockPos pos = new BlockPos(sBlock.x, sBlock.y, sBlock.z);
							if (sBlock.y >= generationBlock.min_height && sBlock.y <= generationBlock.max_height) {
								List<BlockPos> post = getBlocks(pos, cavesRange.get(), cavesRangeY.get());
								for (BlockPos pos2 : post) {
									if (!isExposedOre(pos2) && pos2.getY() >= generationBlock.min_height && pos2.getY() <= generationBlock.max_height) {
										addBlock(pos2, false);
									}
								}
							}
						}
					}
				}
			}
		}
	}
	private void addExposedBlocks() {
		if (mc.world != null) {
			Iterable<Chunk> chunks = Utils.chunks();
			for (Chunk chunk : chunks) {
				if (expanded.get()) {
					ESPChunk s = ESPChunk.searchChunk(chunk, whblocks.get());
					if (s.blocks != null) {
						for (ESPBlock sBlock : s.blocks.values()) {
							BlockPos pos = new BlockPos(sBlock.x, sBlock.y, sBlock.z);
							if (whblocks.get().contains(mc.world.getBlockState(pos).getBlock())) {
								if (isExposedOre(pos)) {
									if (auto_dimension.get()) {
										GenerationBlock generationBlock = GenerationBlock.getGenerationBlock(mc.world.getBlockState(pos).getBlock(), false);
										if (generationBlock != null && generationBlock.dimension == PlayerUtils.getDimension()) {
											addBlock(pos, false);
											List<BlockPos> post = getBlocks(pos, clusterRange.get(), clusterRange.get());
											for (BlockPos pos2 : post) {
												addBlock(pos2, false);
											}
										}
									} else {
										addBlock(pos, false);
										List<BlockPos> post = getBlocks(pos, clusterRange.get(), clusterRange.get());
										for (BlockPos pos2 : post) {
											addBlock(pos2, false);
										}
									}
								}
							}
						}
					}
				}
				addCaves(chunk);
				try {
					Thread.sleep(5);
				} catch (InterruptedException ignore) {

				}
			}
		}
	}

	private boolean isExposedBlock(Block block) {
		return (block == Blocks.AIR || block == Blocks.WATER || block == Blocks.LAVA);
	}

	private boolean isExposedBlock(BlockState state) {
		Block block = state.getBlock();
		return isExposedBlock(block);
	}

	private boolean isExposedOre(BlockPos pos) {
		if (mc.world != null) {
			BlockState def = mc.world.getBlockState(pos);
			if (whblocks.get().contains(def.getBlock())) {
				if (isExposedBlock(mc.world.getBlockState(pos.add(0, 1, 0))))
					return true;
				else if (isExposedBlock(mc.world.getBlockState(pos.add(0, -1, 0))))
					return true;
				else if (isExposedBlock(mc.world.getBlockState(pos.add(1, 0, 0))))
					return true;
				else if (isExposedBlock(mc.world.getBlockState(pos.add(-1, 0, 0))))
					return true;
				else if (isExposedBlock(mc.world.getBlockState(pos.add(0, 0, 1))))
					return true;
				else if (isExposedBlock(mc.world.getBlockState(pos.add(-0, 0, -1))))
					return true;
			}
		}
		return false;
	}

    private List<BlockPos> blocks = new ArrayList<>();
    @Override
    public String getInfoString() {
		if (pause_toggle) {
			return "paused";
		}
        else {
			return renderedBlocks + "b, " + timescan + "t";
		}
    }

    private boolean send(BlockPos blockpos)
    {
		boolean sucess = true;
        if (blockpos == null) {
			sucess = false;
		}
        ClientPlayNetworkHandler conn = mc.getNetworkHandler();
        if (conn == null) {
			sucess = false;
		}
		if (mc.world == null) {
			sucess = false;
		}
		else {
			BlockState state = mc.world.getBlockState(blockpos);
			if (state.getBlock() == Blocks.WALL_TORCH || state.getBlock() == Blocks.TORCH || state.getBlock() == Blocks.AIR || state.getBlock() == Blocks.LAVA || state.getBlock() == Blocks.WATER) {
				sucess = false;
			}
		}

		if (!EntityUtils.isInRenderDistance(blockpos)) {
			sucess = false;
		}

		if (sucess) {
			currentScanBlock = blockpos;
			PlayerActionC2SPacket packet_one = getPacket(blockpos, packet_first);
			if (packet_one != null) {
				conn.sendPacket(packet_one);
			}
			PlayerActionC2SPacket packet_tw = getPacket(blockpos, packet_two);
			if (packet_tw != null) {
				conn.sendPacket(packet_tw);
			}
			addNeedRescan(blockpos, rescanerDelay.get());
		}
		else {
			currentScanBlock = 	null;
		}
		if (sucess && !scanned.contains(blockpos)) {
			scanned.add(blockpos);
		}
        return sucess;
    }

	private PlayerActionC2SPacket getPacket(BlockPos blockpos, Setting<PacketMode> setting) {
		if (setting.get() == PacketMode.Abort) {
			PlayerActionC2SPacket abort = new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.ABORT_DESTROY_BLOCK, new BlockPos(blockpos), Direction.UP, 0);
			return abort;
		}
		else if (setting.get() == PacketMode.Start) {
			PlayerActionC2SPacket start = new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, new BlockPos(blockpos), Direction.UP, 0);
			return start;
		}
		else if (setting.get() == PacketMode.Stop) {
			PlayerActionC2SPacket stop = new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, new BlockPos(blockpos), Direction.UP, 0);
			return stop;
		}
		return null;
	}

	long timescan = 0;
	long millis = 0;
    private void checker()
    {
		float timeSinceLastTick = TickRate.INSTANCE.getTimeSinceLastTick();
        if (LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() >= millis && !pause_toggle)
        {
            if (blocks != null && blocks.size() > 0) {
				if (tps_sync.get() && timeSinceLastTick <= 1f) {
					work();
				}
				else if (!tps_sync.get()) {
					work();
				}
            }
            else
            {
				if (tps_sync.get() && timeSinceLastTick <= 1f) {
					addRandomBlock();
					work();
				}
				else if (!tps_sync.get()) {
					addRandomBlock();
					work();
				}
            }
        }
    }

    private void work()
    {
        try {
			long start = System.currentTimeMillis();
            Iterator<BlockPos> blocksIterator = blocks.iterator();
            if (blocksIterator.hasNext()) {
                if (fps_sync.get()) {
                    if (!lagging) {
                        BlockPos block = blocksIterator.next();
                        if (send(block)) {
                            millis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + Utils.random(delaymin.get(), delaymax.get());
                            lagging = true;
                        }
						blocksIterator.remove();
                    }
                }
                else
                {
                    BlockPos block = blocksIterator.next();
                    blocksIterator.remove();
                    if (send(block)) {
                        millis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + Utils.random(delaymin.get(), delaymax.get());
                        lagging = true;
                    }
                }
            }
			long finish = System.currentTimeMillis();
			timescan = finish - start;
        } catch (Exception ignored) { }
    }

    private void addRandomBlock() {
		assert mc.player != null;
		int x = Utils.random(mc.player.getBlockPos().getX() -range.get(), mc.player.getBlockPos().getX() + range.get());
        int y;
        int z = Utils.random(mc.player.getBlockPos().getZ() -range.get(), mc.player.getBlockPos().getZ() + range.get());

        if (auto_height.get())
        {
			List<Block> findBlocks = whblocks.get();
			boolean newGeneration = false;
			if (generationType.get() == GenerationType.New) {
				newGeneration = true;
			}
			for (Block block : findBlocks) {
				GenerationBlock b = GenerationBlock.getGenerationBlock(block, newGeneration);
				if (b != null && b.block == block) {
					y = Utils.random(b.min_height, b.max_height);
					if (auto_dimension.get() && PlayerUtils.getDimension() == b.dimension) {
						addBlock(new BlockPos(x, y, z), false);
					}
					else if (!auto_dimension.get()) {
						addBlock(new BlockPos(x, y, z), false);
					}
				}
				else if (b == null) {
					y = Utils.random(mc.player.getBlockPos().getY() -y_range.get(), mc.player.getBlockPos().getY() + y_range.get());
					if (!scanned.contains(new BlockPos(x, y, z))) {
						addBlock(new BlockPos(x, y, z), false);
					}
				}
			}
        }
		else {
			y = Utils.random(mc.player.getBlockPos().getY() -y_range.get(), mc.player.getBlockPos().getY() + y_range.get());
			if (!scanned.contains(new BlockPos(x, y, z))) {
				if (auto_dimension.get() && PlayerUtils.getDimension() == Dimension.Overworld) {
					addBlock(new BlockPos(x, y, z), false);
				}
				else if (!auto_dimension.get()) {
					addBlock(new BlockPos(x, y, z), false);
				}
			}
		}
    }
    private Thread clickerThread;

	@EventHandler
	private void onGameLeft(GameLeftEvent event) {
		need_rescan.clear();
	}

	private void reScaner()
	{
		synchronized (need_rescan) {
			float timeSinceLastTick = TickRate.INSTANCE.getTimeSinceLastTick();
			Iterator<BlockScanned> iterator = need_rescan.iterator();
			while (iterator.hasNext()) {
				BlockScanned blockscanned = iterator.next();
				if (timeSinceLastTick <= 1f) {
					if (LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() >= blockscanned.rescanTime && mc.world != null) {
						BlockState state = mc.world.getBlockState(blockscanned.pos);
						if (whblocks.get().contains(state.getBlock())) {
							addRenderBlock(blockscanned.pos);
							for (BlockPos pos : getBlocks(blockscanned.pos, clusterRange.get(), clusterRange.get())) {
								addBlock(pos, true);
								if (!scanned.contains(pos)) {
									addNeedRescan(pos, rescanerDelay.get());
								}
							}
						}
						else {
							RenderOre ore = getRenderOre(blockscanned.pos);
							if (ore != null) {
								removeRenderOre(blockscanned.pos);
							}
						}
						iterator.remove();
					}
				}
				else if (timeSinceLastTick > 1) {

					int[] piArray = String.valueOf(1 / timeSinceLastTick)
						.replaceAll("\\D", "")
						.chars()
						.map(Character::getNumericValue)
						.toArray();

					blockscanned.rescanTime += Arrays.stream(piArray).skip(1).max().getAsInt();
				}
			}
		}
	}
	private Thread exposedthread = null;
    @Override
    public void onActivate() {
		scan = true;
        millis = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        clickerThread = new Thread(() -> {
			addRandomBlock();
            while (scan)
            {
                checker();
				reScaner();
            }
        });
		clickerThread.start();

		exposedthread = new Thread(() -> {
			while (scan)
			{
				addExposedBlocks();
				updateRenderedOres();
				try {
					Thread.sleep(25);
				} catch (InterruptedException ignore) {

				}
			}
		});
		exposedthread.start();
    }
	private boolean scan = false;
    @Override
    public void onDeactivate() {
		scan = false;
        blocks.clear();
        currentScanBlock = null;
        if (clickerThread != null)
        {
            clickerThread = null;
        }
		if (exposedthread != null)
		{
			exposedthread = null;
		}
    }

    private static final List<BlockPos> scanned = new ArrayList<>();

    private List<BlockPos> getBlocks(BlockPos startPos, int y_radius, int radius)
    {
        List<BlockPos> temp = new ArrayList<>();
        for (int dy = -y_radius; dy <= y_radius; dy++) {
            if ((startPos.getY() + dy) < -60 || (startPos.getY() + dy) > 360) continue;
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dx = -radius; dx <= radius; dx++) {
                    BlockPos blockPos = startPos.add(dx, dy, dz);
					BlockState state = mc.world.getBlockState(blockPos);
					boolean isInRenderDistance = EntityUtils.isInRenderDistance(blockPos);
					boolean isBlockPosNotInList = !scanned.contains(blockPos);
                    if (isInRenderDistance && isBlockPosNotInList) {
						temp.add(blockPos);
                    }
                }
            }
        }
        return temp;
    }
}
