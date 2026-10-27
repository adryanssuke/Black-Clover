package com.exemplo.blackclover.events;

import com.exemplo.blackclover.BlackCloverMod;
import com.exemplo.blackclover.data.ModAttachments;
import com.exemplo.blackclover.data.PlayerMagicData;
import com.exemplo.blackclover.network.ModNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = BlackCloverMod.MODID)
public class CommonEvents {

    /** Regenera mana todo tick e sincroniza com o cliente a cada 5 ticks se mudou. */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        PlayerMagicData d = player.getData(ModAttachments.MAGIC);
        d.tickRegen();
        if (d.dirty && player.tickCount % 5 == 0) {
            ModNetwork.sync(player);
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ModNetwork.sync(player);
        }
    }

    /** Matar monstros dá EXP (igual à vida máxima do monstro, mínimo 5). */
    @SubscribeEvent
    public static void onKill(LivingDeathEvent event) {
        Entity killer = event.getSource().getEntity();
        if (killer instanceof ServerPlayer player && event.getEntity() instanceof Enemy) {
            PlayerMagicData d = player.getData(ModAttachments.MAGIC);
            d.addExp((long) Math.max(5, event.getEntity().getMaxHealth()));
        }
    }
}
