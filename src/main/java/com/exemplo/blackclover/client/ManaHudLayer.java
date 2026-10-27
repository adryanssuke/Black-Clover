package com.exemplo.blackclover.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

/** Desenha a barra de mana no canto inferior esquerdo da tela. */
public class ManaHudLayer implements LayeredDraw.Layer {
    private static final int BAR_W = 100;
    private static final int BAR_H = 8;

    @Override
    public void render(GuiGraphics g, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        if (ClientMagicData.maxMana <= 0) return;

        int x = 10;
        int y = mc.getWindow().getGuiScaledHeight() - 30;
        float pct = Math.min(1f, ClientMagicData.mana / ClientMagicData.maxMana);

        g.fill(x - 1, y - 1, x + BAR_W + 1, y + BAR_H + 1, 0xFF000000);       // moldura
        g.fill(x, y, x + BAR_W, y + BAR_H, 0xFF1A1A2E);                        // fundo
        g.fill(x, y, x + (int) (BAR_W * pct), y + BAR_H, 0xFF7B2CFF);          // mana (roxo)

        String mana = (int) ClientMagicData.mana + " / " + (int) ClientMagicData.maxMana;
        g.drawString(mc.font, mana, x + 2, y - 10, 0xFFFFFFFF, true);
        g.drawString(mc.font, "Nv. " + ClientMagicData.level, x, y + 11, 0xFFFFD700, true);
        if (ClientMagicData.points > 0) {
            g.drawString(mc.font, "Pontos livres: " + ClientMagicData.points + "  (/bc ponto)",
                    x + 40, y + 11, 0xFF55FF55, true);
        }
    }
}
