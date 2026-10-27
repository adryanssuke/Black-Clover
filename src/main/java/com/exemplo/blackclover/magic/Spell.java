package com.exemplo.blackclover.magic;

import com.exemplo.blackclover.data.Attr;
import com.exemplo.blackclover.data.ModAttachments;
import com.exemplo.blackclover.data.PlayerMagicData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/** Classe base de todo feitiço. Para criar um novo: estenda e implemente execute(). */
public abstract class Spell {
    public enum Result { SUCCESS, NO_MANA, ON_COOLDOWN, FAILED }

    private final ResourceLocation id;
    private final float manaCost;
    private final int cooldownTicks;

    protected Spell(ResourceLocation id, float manaCost, int cooldownTicks) {
        this.id = id;
        this.manaCost = manaCost;
        this.cooldownTicks = cooldownTicks;
    }

    /** Lógica do feitiço. Retorne false se falhar (nesse caso não gasta mana). */
    protected abstract boolean execute(ServerPlayer player, PlayerMagicData data);

    public final Result tryCast(ServerPlayer player) {
        PlayerMagicData data = player.getData(ModAttachments.MAGIC);
        long now = player.serverLevel().getGameTime();

        if (data.cooldowns.getOrDefault(id.toString(), 0L) > now) return Result.ON_COOLDOWN;
        if (data.mana < getCost(data)) return Result.NO_MANA;
        if (!execute(player, data)) return Result.FAILED;

        data.consumeMana(getCost(data));
        int cd = Math.round(cooldownTicks * (1f - data.cooldownReduction()));
        data.cooldowns.put(id.toString(), now + cd);
        return Result.SUCCESS;
    }

    protected float getCost(PlayerMagicData data) {
        float reduction = Math.min(0.5f, data.attr(Attr.MANA_CONTROL) * 0.0025f);
        return manaCost * (1f - reduction);
    }

    public ResourceLocation id() { return id; }
}
