package com.exemplo.blackclover.data;

import com.exemplo.blackclover.BlackCloverMod;
import java.util.function.Supplier;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, BlackCloverMod.MODID);

    /** Uso: player.getData(ModAttachments.MAGIC) */
    public static final Supplier<AttachmentType<PlayerMagicData>> MAGIC =
            ATTACHMENTS.register("magic", () ->
                    AttachmentType.serializable(PlayerMagicData::new)
                            .copyOnDeath()   // mantém nível e atributos ao morrer
                            .build());
}
