package com.exemplo.blackclover.magic.spells;

import com.exemplo.blackclover.BlackCloverMod;
import com.exemplo.blackclover.data.Attr;
import com.exemplo.blackclover.data.PlayerMagicData;
import com.exemplo.blackclover.magic.Spell;
import java.util.List;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Corte anti-magia: raycast em linha, dano, remove efeitos, destrói bolas de fogo. */
public class DemonSlayerSlashSpell extends Spell {
    private static final double RANGE = 12.0;

    public DemonSlayerSlashSpell() {
        super(ResourceLocation.fromNamespaceAndPath(BlackCloverMod.MODID, "demon_slayer_slash"),
                20f,        // custo de mana
                20 * 4);    // cooldown: 4 segundos
    }

    @Override
    protected boolean execute(ServerPlayer player, PlayerMagicData data) {
        ServerLevel level = player.serverLevel();
        Vec3 start = player.getEyePosition();
        Vec3 look = player.getLookAngle();

        // 1) Raycast contra blocos: o corte para na primeira parede
        Vec3 maxEnd = start.add(look.scale(RANGE));
        BlockHitResult blockHit = level.clip(new ClipContext(start, maxEnd,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        double reach = blockHit.getType() == HitResult.Type.MISS
                ? RANGE : blockHit.getLocation().distanceTo(start);
        Vec3 end = start.add(look.scale(reach));

        // 2) Alvos: entidades cuja hitbox cruza a linha
        AABB area = new AABB(start, end).inflate(1.5);
        List<LivingEntity> targets = level.getEntitiesOfClass(LivingEntity.class, area,
                e -> e != player && e.isAlive()
                        && e.getBoundingBox().inflate(0.6).clip(start, end).isPresent());

        // 3) Dano
        float damage = (8f + data.attr(Attr.STRENGTH) * 0.15f) * data.magicDamageMultiplier();
        DamageSource source = player.damageSources().playerAttack(player);
        for (LivingEntity target : targets) {
            target.hurt(source, damage);
            target.removeAllEffects();
        }

        // 4) Destrói projéteis mágicos (bolas de fogo etc.)
        List<AbstractHurtingProjectile> projectiles = level.getEntitiesOfClass(
                AbstractHurtingProjectile.class, area,
                p -> p.getBoundingBox().inflate(0.5).clip(start, end).isPresent());
        for (AbstractHurtingProjectile p : projectiles) {
            p.discard();
        }

        // 5) Partículas ao longo da linha
        int steps = (int) (reach * 2);
        for (int i = 0; i <= steps; i++) {
            Vec3 pos = start.add(look.scale(i * 0.5));
            level.sendParticles(ParticleTypes.SQUID_INK, pos.x, pos.y - 0.2, pos.z, 2, 0.05, 0.05, 0.05, 0.01);
            if (i % 4 == 0) {
                level.sendParticles(ParticleTypes.SWEEP_ATTACK, pos.x, pos.y - 0.2, pos.z, 1, 0, 0, 0, 0);
            }
        }

        // 6) Sons
        level.playSound(null, player.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS, 1.0f, 0.6f);
        level.playSound(null, player.blockPosition(), SoundEvents.WITHER_SHOOT,
                SoundSource.PLAYERS, 0.4f, 1.6f);
        return true;
    }
}
