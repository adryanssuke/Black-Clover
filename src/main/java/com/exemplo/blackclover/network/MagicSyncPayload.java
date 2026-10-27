package com.exemplo.blackclover.network;

import com.exemplo.blackclover.BlackCloverMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Servidor -> cliente: envia mana, nível e pontos para o HUD. */
public record MagicSyncPayload(float mana, float maxMana, int level, int points)
        implements CustomPacketPayload {
    public static final Type<MagicSyncPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(BlackCloverMod.MODID, "magic_sync"));
    public static final StreamCodec<ByteBuf, MagicSyncPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, MagicSyncPayload::mana,
            ByteBufCodecs.FLOAT, MagicSyncPayload::maxMana,
            ByteBufCodecs.VAR_INT, MagicSyncPayload::level,
            ByteBufCodecs.VAR_INT, MagicSyncPayload::points,
            MagicSyncPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
