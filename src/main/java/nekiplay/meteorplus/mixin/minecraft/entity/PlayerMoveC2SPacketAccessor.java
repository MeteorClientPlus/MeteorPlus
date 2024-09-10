package nekiplay.meteorplus.mixin.minecraft.entity;

import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerMoveC2SPacket.class)
public interface PlayerMoveC2SPacketAccessor {
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
