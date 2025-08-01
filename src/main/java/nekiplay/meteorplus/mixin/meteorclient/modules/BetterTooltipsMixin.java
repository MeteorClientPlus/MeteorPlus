package nekiplay.meteorplus.mixin.meteorclient.modules;

import meteordevelopment.meteorclient.events.render.TooltipDataEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.systems.modules.render.BetterTooltips;
import meteordevelopment.meteorclient.utils.misc.Keybind;
import meteordevelopment.meteorclient.utils.player.EChestMemory;
import meteordevelopment.meteorclient.utils.tooltip.ContainerTooltipComponent;
import meteordevelopment.meteorclient.utils.tooltip.TextTooltipComponent;
import nekiplay.meteorplus.utils.ColorRemover;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static meteordevelopment.meteorclient.systems.modules.render.BetterTooltips.ECHEST_COLOR;

@Mixin(value = BetterTooltips.class)
public class BetterTooltipsMixin extends Module {
	@Shadow
	@Final
	private Setting<Keybind> keybind;

	public BetterTooltipsMixin(Category category, String name, String description, String... aliases) {
		super(category, name, description, aliases);
	}

	private final SettingGroup sgGeneral = settings.getDefaultGroup();
	private final SettingGroup sgPreviews = settings.getGroup("Previews");

	public final Setting<Boolean> echest = (Setting<Boolean>)sgPreviews.get("echests");

	private final Setting<BetterTooltips.DisplayWhen> displayWhen = (Setting<BetterTooltips.DisplayWhen>)sgGeneral.get("display-when");


	@ModifyArgs(method = "appendTooltip", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/events/game/ItemStackTooltipEvent;appendStart(Lnet/minecraft/text/Text;)V"))
	private void appendTooltipStart(Args args) {
		Text text = args.get(0);
		String str = ColorRemover.GetVerbatim(text.getString());
		if (str.startsWith("Honey level:")) {
			Pattern pattern = Pattern.compile("Honey level: (.*)");
			Matcher matcher = pattern.matcher(str);
			if (matcher.find()) {
				String val = matcher.group(1).replaceAll("\\.", "");
				args.set(0, Text.literal(I18n.translate("modules.meteor-client.better-tooltips.beehive.honey-level", val)));
			}
		}
		else if (str.startsWith("Bees:")) {
			Pattern pattern = Pattern.compile("Bees: (.*)");
			Matcher matcher = pattern.matcher(str);
			if (matcher.find()) {
				String val = matcher.group(1).replaceAll("\\.", "");
				args.set(0, Text.literal(I18n.translate("modules.meteor-client.better-tooltips.beehive.bees", val)));
			}
		}
	}

	@ModifyArgs(method = "appendTooltip", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/events/game/ItemStackTooltipEvent;appendEnd(Lnet/minecraft/text/Text;)V"))
	private void appendTooltipEnd(Args args) {
		Text text = args.get(0);
		String str = ColorRemover.GetVerbatim(text.getString());

		if (str.endsWith("kb")) {
			Pattern pattern = Pattern.compile("(.*) kb");
			Matcher matcher = pattern.matcher(str);
			if (matcher.find()) {
				String val = matcher.group(1);
				args.set(0, Text.literal(I18n.translate("modules.meteor-client.better-tooltips.kilobytes", val)));
			}
		}
		else if (str.endsWith("bytes")) {
			Pattern pattern = Pattern.compile("(.*) bytes");
			Matcher matcher = pattern.matcher(str);
			if (matcher.find()) {
				String val = matcher.group(1);
				args.set(0, Text.literal(I18n.translate("modules.meteor-client.better-tooltips.bytes", val)));
			}
		}
		else if (str.equals("Error getting bytes.")) {
			args.set(0, Text.literal(I18n.translate("modules.meteor-client.better-tooltips.error-getting-bytes")));
		}
	}

	@ModifyArgs(method = "appendPreviewTooltipText", at = @At(value = "INVOKE", target = "Lmeteordevelopment/meteorclient/events/game/ItemStackTooltipEvent;appendEnd(Lnet/minecraft/text/Text;)V"))
	private void appendPreviewTooltipTextEnd(Args args) {
		Text text = args.get(0);
		String str = ColorRemover.GetVerbatim(text.getString());
		if (str.endsWith("to preview")) {
			args.set(0, Text.literal(I18n.translate("modules.meteor-client.better-tooltips.hold-to-preview", keybind)));
		}
	}

	@Inject(method = "getTooltipData", at = @At("RETURN"), remap = false)
	private void getTooltipData(TooltipDataEvent event, CallbackInfo ci) {
		if (event.itemStack.getItem() == Items.ENDER_CHEST && previewEChest()) {
			event.tooltipData = EChestMemory.isKnown()
				? new ContainerTooltipComponent(EChestMemory.ITEMS.toArray(new ItemStack[27]), ECHEST_COLOR)
				: new TextTooltipComponent(Text.literal(I18n.translate("modules.meteor-client.better-tooltips.unknown-inventory")));
		}
	}

	@Shadow
	private boolean previewEChest() {
		return isPressed() && echest.get();
	}

	@Shadow
	private boolean isPressed() {
		return (keybind.get().isPressed() && displayWhen.get() == BetterTooltips.DisplayWhen.Keybind) || displayWhen.get() == BetterTooltips.DisplayWhen.Always;
	}
}
