package com.exemplo.blackclover.events;

import com.exemplo.blackclover.BlackCloverMod;
import com.exemplo.blackclover.data.Attr;
import com.exemplo.blackclover.data.ModAttachments;
import com.exemplo.blackclover.data.PlayerMagicData;
import com.mojang.brigadier.arguments.StringArgumentType;
import java.util.Arrays;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

/**
 * /bc status           -> mostra nível, EXP, pontos e atributos
 * /bc ponto <atributo> -> gasta 1 ponto (forca, agilidade, vitalidade, reserva, controle, magica, sorte)
 */
@EventBusSubscriber(modid = BlackCloverMod.MODID)
public class ModCommands {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("bc")
                .then(Commands.literal("status").executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    PlayerMagicData d = player.getData(ModAttachments.MAGIC);
                    StringBuilder sb = new StringBuilder();
                    sb.append("Nível ").append(d.level)
                      .append(" | EXP ").append(d.exp).append("/").append(PlayerMagicData.expForLevel(d.level))
                      .append(" | Pontos livres: ").append(d.points)
                      .append(" | Mana ").append((int) d.mana).append("/").append((int) d.maxMana());
                    for (Attr a : Attr.values()) {
                        sb.append("\n  ").append(a.key).append(": ").append(d.attr(a));
                    }
                    String text = sb.toString();
                    ctx.getSource().sendSuccess(() -> Component.literal(text), false);
                    return 1;
                }))
                .then(Commands.literal("ponto")
                        .then(Commands.argument("atributo", StringArgumentType.word())
                                .suggests((c, b) -> SharedSuggestionProvider.suggest(
                                        Arrays.stream(Attr.values()).map(a -> a.key), b))
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    PlayerMagicData d = player.getData(ModAttachments.MAGIC);
                                    Attr attr = Attr.byKey(StringArgumentType.getString(ctx, "atributo"));
                                    if (attr == null) {
                                        ctx.getSource().sendFailure(Component.literal("Atributo inválido."));
                                        return 0;
                                    }
                                    if (!d.spendPoint(attr)) {
                                        ctx.getSource().sendFailure(Component.literal("Você não tem pontos livres."));
                                        return 0;
                                    }
                                    String msg = attr.key + " agora é " + d.attr(attr);
                                    ctx.getSource().sendSuccess(() -> Component.literal(msg), false);
                                    return 1;
                                }))));
    }
}
