package nekiplay.meteorplus.mixin.minecraft.entity;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import meteordevelopment.meteorclient.MeteorClient;
import nekiplay.main.events.PlayerUseMultiplierEvent;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = ClientPlayerEntity.class, priority = 1003)
public abstract class ClientPlayerEntityMixin {

    @WrapOperation(method = "applyMovementSpeedFactors", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec2f;multiply(F)Lnet/minecraft/util/math/Vec2f;", ordinal = 1))
    private Vec2f hookCustomMultiplier(Vec2f instance, float value, Operation<Vec2f> original) {
        PlayerUseMultiplierEvent playerUseMultiplier = new PlayerUseMultiplierEvent(value, value);
        MeteorClient.EVENT_BUS.post(playerUseMultiplier);
        return new Vec2f(
            instance.x * playerUseMultiplier.getSideways(),
            instance.y * playerUseMultiplier.getForward()
        );
    }
}
