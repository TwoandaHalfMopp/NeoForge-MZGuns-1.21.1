package net.moppzarella.mzguns.item.custom;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.moppzarella.mzguns.MZGuns;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class GunItem extends ProjectileWeaponItem {
    public GunItem(Properties properties) {
        super(properties);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return null;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 0;
    }

    public void primaryFire(Player player) {
        if (!(player instanceof ServerPlayer)) {return;}
        MZGuns.LOGGER.info("GunItem: PrimaryFire");
        
    }

    @Override
    protected void shootProjectile(LivingEntity livingEntity, Projectile projectile, int i, float v, float v1, float v2, @Nullable LivingEntity livingEntity1) {

    }
}
