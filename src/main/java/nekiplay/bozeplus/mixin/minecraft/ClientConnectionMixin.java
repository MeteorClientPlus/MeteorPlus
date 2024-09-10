package nekiplay.bozeplus.mixin.minecraft;

import nekiplay.bozeplus.BozePlusAddon;
import nekiplay.main.events.packets.PacketEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
	@Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
	private static <T extends PacketListener> void onHandlePacket(Packet<T> packet, PacketListener listener, CallbackInfo info) {

		if (packet instanceof BundleS2CPacket bundle) {
			for (Iterator<Packet<ClientPlayPacketListener>> it = bundle.getPackets().iterator(); it.hasNext(); ) {
				if (BozePlusAddon.getEventBus().post(PacketEvent.Receive.get(it.next(), listener)).isCancelled())
					it.remove();
			}
		} else if (BozePlusAddon.getEventBus().post(PacketEvent.Receive.get(packet, listener)).isCancelled()) info.cancel();
	}

	@Inject(at = @At("HEAD"), method = "send(Lnet/minecraft/network/packet/Packet;)V", cancellable = true)
	private void onSendPacketHead(Packet<?> packet, CallbackInfo info) {
		if (BozePlusAddon.getEventBus().post(PacketEvent.Send.get(packet)).isCancelled()) info.cancel();
	}

	@Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("TAIL"))
	private void onSendPacketTail(Packet<?> packet, CallbackInfo info) {
		BozePlusAddon.getEventBus().post(PacketEvent.Sent.get(packet));
	}
}
