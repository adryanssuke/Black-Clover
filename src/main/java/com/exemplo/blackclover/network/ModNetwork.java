package com.exemplo.blackclover.network;

import com.exemplo.blackclover.client.ClientMagicData;
import com.exemplo.blackclover.data.ModAttachments;
import com.exemplo.blackclover.data.PlayerMagicData;
import com.exemplo.blackclover.magic.Spell;
import com.exemplo.blackclover.magic.SpellRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModNetwork {

    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar reg = event.registrar("1");

        // Cliente pede para lançar o feitiço -> o servidor decide
        reg.playToServer(CastSpellPayload.TYPE, CastSpellPayload.CODEC, (payload, ctx) ->
                ctx.enqueueWork(() -> {
                    if (!(ctx.player() instanceof ServerPlayer player)) return;
                    Spell.Result result = SpellRegistry.DEMON_SLAYER_SLASH.tryCast(player);
                    if (result == Spell.Result.NO_MANA) {
                        player.displayClientMessage(Component.literal("Mana insuficiente!"), true);
                    } else if (result == Spell.Result.ON_COOLDOWN) {
                        player.displayClientMessage(Component.literal("Feitiço em recarga..."), true);
                    }
                }));

        // Servidor manda os dados para o HUD
        reg.playToClient(MagicSyncPayload.TYPE, MagicSyncPayload.CODEC, (payload, ctx) ->
                ctx.enqueueWork(() -> ClientMagicData.update(payload)));
    }

    public static void sync(ServerPlayer player) {
        PlayerMagicData d = player.getData(ModAttachments.MAGIC);
        PacketDistributor.sendToPlayer(player,
                new MagicSyncPayload(d.mana, d.maxMana(), d.level, d.points));
        d.dirty = false;
    }
}
