package net.moppzarella.mzguns.item.custom;

import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.moppzarella.mzguns.Config;
import net.moppzarella.mzguns.component.ModDataComponents;
import net.moppzarella.mzguns.util.GunState;

public class PistolItem extends BaseGunItem{

    public final float baseDamage;
    public final int clipSize;
    public final int firingInterval;
    public final int reloadTime;
    public final int deployTime;

    public final int maxBloomTime;
    public final int bloomPerShot;
    public final float bloomAngle;

    public PistolItem(float baseDamage, int clipSize, int firingInterval, int reloadTime, int deployTime, int shotsPerReload, int maxBloomTime, int bloomPerShot, float bloomAngle, Properties properties) {
        super(baseDamage, clipSize, firingInterval, reloadTime, deployTime, shotsPerReload, properties);

        this.baseDamage = baseDamage;
        this.clipSize = clipSize;
        this.firingInterval = firingInterval;
        this.reloadTime = reloadTime;
        this.deployTime = deployTime;

        this.maxBloomTime = maxBloomTime;
        this.bloomPerShot = bloomPerShot;
        this.bloomAngle = bloomAngle;
    }

    @Override
    public void shoot(Level level, Player player, ItemStack stack) {
        playFiringSound(level, player);
        shootPellets(level, player, stack);
        if (!(Config.INFINITE_CLIP_IN_CREATIVE.getAsBoolean() && player.isCreative() && clipSize != -1)) {
            setCurrentClip(stack, this.getCurrentClip(stack) - 1);
        }
        setState(stack, GunState.ACTIVE_FIRING);
    }
    @Override
    public void shootPellets(Level level, Player player, ItemStack stack) {
        float randPitch = (float) ((Math.random() - 0.5) * 2 * (getBloomPercentile(stack) * this.bloomAngle));
        float randYaw = (float) ((Math.random() - 0.5) * 2 * (getBloomPercentile(stack) * this.bloomAngle));
        spawnPellet(level, player, stack, randYaw, randPitch, baseDamage);
        int current_bloom_time = getBloomTime(stack);
        int bloom_to_add = Math.clamp(current_bloom_time + bloomPerShot, 0, maxBloomTime);
        setBloomTime(stack,bloom_to_add);
    }

    public float getBloomPercentile(ItemStack stack) {
        int current_bloom = getBloomTime(stack);
        return ((float) current_bloom) / ((float) maxBloomTime);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if(!level.isClientSide) {
            GunState current_state = getState(stack);
            GunState prev_state = getPrevState(stack);
            Player player = (Player) entity;

            runStateLogic(stack, level, player, current_state, prev_state);
            int current_bloom_time = getBloomTime(stack);
            if (current_bloom_time > 0 && getState(stack) != GunState.ACTIVE_FIRING) {
                incrementBloomTime(stack);
            }

            if (this.shouldPlayHitsound(stack)) {
                //MZGuns.LOGGER.info("Playing Hitsound");
                ((ServerPlayer)entity).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                this.updateHitsoundChecK(stack, false);
            }
        }

    }
    public int getBloomTime(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.BLOOM_TIMER, 0);
    }
    public void incrementBloomTime(ItemStack stack) {
        int current_bloom_time = getBloomTime(stack);
        setBloomTime(stack, current_bloom_time - 1);
    }
    public void setBloomTime(ItemStack stack, int i) {
        stack.set(ModDataComponents.BLOOM_TIMER, i);
    }
}
