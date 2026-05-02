package nekiplay.meteorplus.mixin.minecraft.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import meteordevelopment.meteorclient.MeteorClient;
import nekiplay.main.events.PlayerUseMultiplierEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec2;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = LocalPlayer.class, priority = 1003)
public abstract class LocalPlayerMixin {

    @WrapOperation(method = "modifyInput", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec2;scale(F)Lnet/minecraft/world/phys/Vec2;", ordinal = 1))
    private Vec2 hookCustomMultiplier(Vec2 instance, float value, Operation<Vec2> original) {
        PlayerUseMultiplierEvent playerUseMultiplier = new PlayerUseMultiplierEvent(value, value);
        MeteorClient.EVENT_BUS.post(playerUseMultiplier);
        return new Vec2(
            instance.x * playerUseMultiplier.getSideways(),
            instance.y * playerUseMultiplier.getForward()
        );
    }
}
