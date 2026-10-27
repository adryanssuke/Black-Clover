package com.exemplo.blackclover.client;

import com.exemplo.blackclover.BlackCloverMod;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

/** Eventos do cliente que usam o barramento do mod: tecla e HUD. */
@EventBusSubscriber(modid = BlackCloverMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientModEvents {

    public static final KeyMapping CAST_KEY = new KeyMapping(
            "key.blackclover.cast", GLFW.GLFW_KEY_Z, "key.categories.blackclover");

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(CAST_KEY);
    }

    @SubscribeEvent
    public static void registerLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(
                ResourceLocation.fromNamespaceAndPath(BlackCloverMod.MODID, "mana_bar"),
                new ManaHudLayer());
    }
}
