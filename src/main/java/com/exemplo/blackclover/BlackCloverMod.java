package com.exemplo.blackclover;

import com.exemplo.blackclover.data.ModAttachments;
import com.exemplo.blackclover.network.ModNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(BlackCloverMod.MODID)
public class BlackCloverMod {
    public static final String MODID = "blackclover";

    public BlackCloverMod(IEventBus modBus) {
        ModAttachments.ATTACHMENTS.register(modBus);
        modBus.addListener(ModNetwork::registerPayloads);
    }
}
