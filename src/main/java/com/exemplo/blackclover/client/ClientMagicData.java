package com.exemplo.blackclover.client;

import com.exemplo.blackclover.network.MagicSyncPayload;

/** Cópia dos dados de mana no cliente (só números, usada pelo HUD). */
public class ClientMagicData {
    public static float mana = 0;
    public static float maxMana = 0;
    public static int level = 1;
    public static int points = 0;

    public static void update(MagicSyncPayload p) {
        mana = p.mana();
        maxMana = p.maxMana();
        level = p.level();
        points = p.points();
    }
}
