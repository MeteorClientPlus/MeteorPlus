package nekiplay.meteorplus.mixin.minecraft.entity;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerboundMovePlayerPacket.class)
public interface ServerboundMovePlayerPacketAccessor {
	@Mutable
	@Accessor("y")
	void setY(double y);

	@Accessor("y")
	double getY();

	@Mutable
	@Accessor("onGround")
	void setOnGround(boolean onGround);

	@Accessor("onGround")
	boolean getOnGround();
}
