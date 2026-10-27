package com.exemplo.blackclover.network;

import com.exemplo.blackclover.BlackCloverMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

/** Cliente -> servidor: "o jogador apertou a tecla de feitiço". */
public record CastSpellPayload() implements CustomPacketPayload {
    public static final Type<CastSpellPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(BlackCloverMod.MODID, "cast_spell"));
    public static final StreamCodec<ByteBuf, CastSpellPayload> CODEC =
            StreamCodec.unit(new CastSpellPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
