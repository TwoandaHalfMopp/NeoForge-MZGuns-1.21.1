package net.moppzarella.mzguns.item.custom;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.moppzarella.mzguns.entity.custom.HitscanPelletEntity;

public class GunItem extends Item {
    public GunItem(Properties properties) {
        super(properties);
    }

    public void primaryFire(Level level, Player player, InteractionHand usedHand) {
        ItemStack stackInHand = player.getItemInHand(usedHand);
        if (player.getCooldowns().isOnCooldown(stackInHand.getItem())) return;
        player.getCooldowns().addCooldown(this, 20);
        if(!level.isClientSide && usedHand == InteractionHand.MAIN_HAND) {

            level.playSound(
                    null,
                    player.getX(),
                    player.getY(),
                    player.getZ(),
                    SoundEvents.SNOWBALL_THROW,
                    SoundSource.NEUTRAL,
                    0.5F,
                    0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));

            spawnPellet(level, player, usedHand, 0.0F, 0.0F);
        }
    }

    public void spawnPellet(Level level, Player player, InteractionHand usedHand, float yawOffset, float pitchOffset) {
        HitscanPelletEntity temp_bullet = new HitscanPelletEntity(level, player);
        temp_bullet.setPos(player.getX(), player.getEyeY(), player.getZ());
        temp_bullet.setXRot(player.getXRot() + pitchOffset);
        temp_bullet.setYRot(player.getYRot() + yawOffset);
        level.addFreshEntity(temp_bullet);
    }

}

