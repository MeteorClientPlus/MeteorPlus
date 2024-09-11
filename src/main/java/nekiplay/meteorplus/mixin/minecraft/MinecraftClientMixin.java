package nekiplay.meteorplus.mixin.minecraft;

import meteordevelopment.meteorclient.systems.modules.Modules;
import nekiplay.meteorplus.features.modules.misc.MultiTasks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
	@Shadow
	public @Nullable ClientPlayerEntity player;
	@Shadow
	@Final
	public GameOptions options;
	@Shadow
	private boolean doAttack() {
		return false;
	}
	@Shadow
	private void doItemUse() {}
	@Shadow
	@Nullable
	public ClientWorld world;

	@Redirect(method = "handleBlockBreaking", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"), require = 0)
	public boolean breakBlock(ClientPlayerEntity clientPlayer) {
		MultiTasks multiTasks = Modules.get().get(MultiTasks.class);
		if(multiTasks != null && multiTasks.isActive()) {
			return false;
		}
		return clientPlayer.isUsingItem();
	}

	@Redirect(method = "doItemUse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;isBreakingBlock()Z"), require = 0)
	public boolean itemBreak(ClientPlayerInteractionManager clientPlayerInteractionManager) {
		MultiTasks multiTasks = Modules.get().get(MultiTasks.class);
		if(multiTasks != null && multiTasks.isActive()) {
			return false;
		}
		return clientPlayerInteractionManager.isBreakingBlock();
	}

	@Redirect(method = "handleInputEvents", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isUsingItem()Z"), require = 0)
	public boolean attackCheck(ClientPlayerEntity instance) {
		MultiTasks multiTasks = Modules.get().get(MultiTasks.class);
		if(multiTasks == null) return player.isUsingItem();
		if(multiTasks.isActive()) {
			while(this.options.attackKey.wasPressed()) {
				this.doAttack();
			}

			while(this.options.useKey.wasPressed()) {
				this.doItemUse();
			}
		}
		return player.isUsingItem();
	}

}
