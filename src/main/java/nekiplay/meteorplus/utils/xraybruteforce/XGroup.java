package nekiplay.meteorplus.utils.xraybruteforce;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.render.blockesp.ESPBlockData;
import meteordevelopment.meteorclient.utils.misc.UnorderedArrayList;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import nekiplay.meteorplus.features.modules.world.XrayBruteforce;
import net.minecraft.block.Block;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Set;

public class XGroup {
	private static final XrayBruteforce search = Modules.get().get(XrayBruteforce.class);

	private final Block block;

	public final UnorderedArrayList<XBlock> blocks = new UnorderedArrayList<>();

	private double sumX, sumY, sumZ;

	public XGroup(Block block) {
		this.block = block;
	}

	public void add(XBlock block, boolean removeFromOld, boolean splitGroup) {
		blocks.add(block);
		sumX += block.x;
		sumY += block.y;
		sumZ += block.z;

		if (block.group != null && removeFromOld) block.group.remove(block, splitGroup);
		block.group = this;
	}

	public void add(XBlock block) {
		add(block, true, true);
	}

	public void remove(XBlock block, boolean splitGroup) {
		blocks.remove(block);
		sumX -= block.x;
		sumY -= block.y;
		sumZ -= block.z;

		if (blocks.isEmpty()) search.removeGroup(block.group);
		else if (splitGroup) {
			trySplit(block);
		}
	}

	public void remove(XBlock block) {
		remove(block, true);
	}

	private void trySplit(XBlock block) {
		Set<XBlock> neighbours = new ObjectOpenHashSet<>(6);

		for (int side : XBlock.SIDES) {
			if ((block.neighbours & side) == side) {
				XBlock neighbour = block.getSideBlock(side);
				if (neighbour != null) neighbours.add(neighbour);
			}
		}
		if (neighbours.size() <= 1) return;

		Set<XBlock> remainingBlocks = new ObjectOpenHashSet<>(blocks);
		Queue<XBlock> blocksToCheck = new ArrayDeque<>();

		blocksToCheck.offer(blocks.get(0));
		remainingBlocks.remove(blocks.get(0));
		neighbours.remove(blocks.get(0));

		loop: {
			while (!blocksToCheck.isEmpty()) {
				XBlock b = blocksToCheck.poll();

				for (int side : XBlock.SIDES) {
					if ((b.neighbours & side) != side) continue;
					XBlock neighbour = b.getSideBlock(side);

					if (neighbour != null && remainingBlocks.contains(neighbour)) {
						blocksToCheck.offer(neighbour);
						remainingBlocks.remove(neighbour);

						neighbours.remove(neighbour);
						if (neighbours.isEmpty()) break loop;
					}
				}
			}
		}

		if (neighbours.size() > 0) {
			XGroup group = search.newGroup(this.block);
			group.blocks.ensureCapacity(remainingBlocks.size());

			blocks.removeIf(remainingBlocks::contains);

			for (XBlock b : remainingBlocks) {
				group.add(b, false, false);

				sumX -= b.x;
				sumY -= b.y;
				sumZ -= b.z;
			}

			if (neighbours.size() > 1) {
				block.neighbours = 0;

				for (XBlock b : neighbours) {
					int x = b.x - block.x;
					if (x == 1) block.neighbours |= XBlock.RI;
					else if (x == -1) block.neighbours |= XBlock.LE;

					int y = b.y - block.y;
					if (y == 1) block.neighbours |= XBlock.TO;
					else if (y == -1) block.neighbours |= XBlock.BO;

					int z = b.z - block.z;
					if (z == 1) block.neighbours |= XBlock.FO;
					else if (z == -1) block.neighbours |= XBlock.BA;
				}

				group.trySplit(block);
			}
		}
	}

	public void merge(XGroup group) {
		blocks.ensureCapacity(blocks.size() + group.blocks.size());
		for (XBlock block : group.blocks) add(block, false, false);
		search.removeGroup(group);
	}

	public void render(Render3DEvent event) {
		ESPBlockData blockData = search.getBlockData(block);

		if (blockData.tracer) {
			event.renderer.line(RenderUtils.center.x, RenderUtils.center.y, RenderUtils.center.z, sumX / blocks.size() + 0.5, sumY / blocks.size() + 0.5, sumZ / blocks.size() + 0.5, blockData.tracerColor);
		}
	}
}
