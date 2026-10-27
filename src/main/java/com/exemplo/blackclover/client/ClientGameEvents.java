package com.exemplo.blackclover.client;

import com.exemplo.blackclover.BlackCloverMod;
import com.exemplo.blackclover.network.CastSpellPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/** Eventos do cliente no barramento do jogo: lê a tecla Z. */
@EventBusSubscriber(modid = BlackCloverMod.MODID, value = Dist.CLIENT)
public class ClientGameEvents {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        while (ClientModEvents.CAST_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new CastSpellPayload());
        }
    }
}
