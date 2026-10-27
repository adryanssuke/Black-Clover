package com.exemplo.blackclover.data;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

/** Dados de RPG de cada jogador: nível, EXP, atributos e mana. Salvos junto com o jogador. */
public class PlayerMagicData implements INBTSerializable<CompoundTag> {

    public int level = 1;
    public long exp = 0;
    public int points = 0;
    public final int[] attrs = new int[Attr.values().length];
    public float mana;
    public boolean dirty = true;

    /** Cooldowns na memória: id do feitiço -> tick em que fica liberado. */
    public final Map<String, Long> cooldowns = new HashMap<>();

    public PlayerMagicData() {
        this.mana = maxMana();
    }

    public int attr(Attr a) { return attrs[a.ordinal()]; }

    // ---------- Atributos derivados ----------
    public float maxMana() { return 100f + attr(Attr.MANA_RESERVE) * 15f; }

    public float manaRegenPerSecond() { return 1.0f + attr(Attr.MANA_CONTROL) * 0.05f; }

    public float cooldownReduction() {
        return Math.min(0.5f, attr(Attr.MANA_CONTROL) * 0.0025f + attr(Attr.LUCK) * 0.0005f);
    }

    public float magicDamageMultiplier() { return 1f + attr(Attr.MAGIC) * 0.008f; }

    // ---------- Mana ----------
    public boolean consumeMana(float amount) {
        if (mana < amount) return false;
        mana -= amount;
        dirty = true;
        return true;
    }

    /** Chamado 1 vez por tick (20x por segundo) no servidor. */
    public void tickRegen() {
        float max = maxMana();
        if (mana < max) {
            mana = Math.min(max, mana + manaRegenPerSecond() / 20f);
            dirty = true;
        }
    }

    // ---------- EXP e nível ----------
    public static long expForLevel(int lvl) {
        return Math.round(100 * Math.pow(lvl, 1.5));
    }

    public void addExp(long amount) {
        exp += amount;
        while (level < 100 && exp >= expForLevel(level)) {
            exp -= expForLevel(level);
            level++;
            points += 3 + (level % 10 == 0 ? 5 : 0);
        }
        dirty = true;
    }

    public boolean spendPoint(Attr a) {
        if (points <= 0) return false;
        attrs[a.ordinal()]++;
        points--;
        dirty = true;
        return true;
    }

    // ---------- Salvar / carregar ----------
    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("level", level);
        tag.putLong("exp", exp);
        tag.putInt("points", points);
        tag.putIntArray("attrs", attrs);
        tag.putFloat("mana", mana);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        level = Math.max(1, tag.getInt("level"));
        exp = tag.getLong("exp");
        points = tag.getInt("points");
        int[] saved = tag.getIntArray("attrs");
        for (int i = 0; i < attrs.length && i < saved.length; i++) attrs[i] = saved[i];
        mana = Math.min(maxMana(), tag.getFloat("mana"));
        dirty = true;
    }
}
