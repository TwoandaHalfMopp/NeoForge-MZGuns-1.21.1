package net.moppzarella.mzguns.item.custom;

import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.moppzarella.mzguns.Config;
import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.component.ModDataComponents;
import net.moppzarella.mzguns.entity.custom.HitscanPelletEntity;

public class BaseGunItem extends Item {
    private final float baseDamage; //The amount of damage one pellet deals, with no falloff. If this is set to -1, pellets are considered instant-kill
    private final int clipSize; //How many shots in a clip. If this is set to -1, it's counted as an infinite clip.
    private final int firingInterval; //How many ticks between shots. Cannot be less than 4. Must be divisible by 4.
    private final int reloadInitialCycleLength; //How many ticks it takes to perform the initial reload.
    private final int reloadConsecutiveCycleLength; //How many ticks it takes to perform consecutive reload (only used for items that reload one bullet at a time)
    private final int reloadCycleAmount; //How many shots get reloaded per reload cycle.

    private final double bloomPerShot; //How much Bloom gets added per shot (Bloom goes from 0-1. 0 means no bloom, 1 means max bloom).
    private final double bloomRefreshRate; //Bloom decreases by this amount each tick.
    private final double maxBloomRadius; //Maximum radius for bloom (inaccuracy). I don't know if this is degrees or radians.


    public BaseGunItem(int clipSize, float baseDamage, int firingInterval, int reloadInitialCycleLength, int reloadConsecutiveCycleLength, int reloadCycleAmount, double bloomPerShot, double bloomRefreshRate, double maxBloomRadius, Properties properties) {
        super(properties);

        this.baseDamage = baseDamage;
        this.clipSize = clipSize;
        this.firingInterval = firingInterval;
        this.reloadInitialCycleLength = reloadInitialCycleLength;
        this.reloadConsecutiveCycleLength = reloadConsecutiveCycleLength;
        this.reloadCycleAmount = reloadCycleAmount;
        this.bloomPerShot = bloomPerShot;
        this.bloomRefreshRate = bloomRefreshRate;
        this.maxBloomRadius = maxBloomRadius;
    }

    //If the use key is pressed while a gun item is in the off-hand, perform the primary fire for that Gun.
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide && usedHand == InteractionHand.OFF_HAND){
            primaryFire(level, player, usedHand);
            return InteractionResultHolder.pass(player.getItemInHand(usedHand));
        }
        return InteractionResultHolder.fail(player.getItemInHand(usedHand));

    }

    public void primaryFire(Level level, Player player, InteractionHand usedHand) {
        ItemStack stackInHand = player.getItemInHand(usedHand);
        if(!level.isClientSide) {
            if (isOnFiringCooldown(stackInHand) || getCurrentClip(stackInHand) == 0) return;
            double current_bloom = this.getCurrentBloom(stackInHand);

            playFiringSound(level, player, usedHand);
            shootPellets(level, player, usedHand, stackInHand);


            this.setFiringCooldown(stackInHand, getFiringInterval());
            this.setCurrentBloom(stackInHand, Math.clamp((current_bloom + bloomPerShot), 0D, 1D));
            if (!(Config.INFINITE_CLIP_IN_CREATIVE.getAsBoolean() && player.isCreative() && getClipSize() != -1)) this.setCurrentClip(stackInHand, this.getCurrentClip(stackInHand) - 1);
            this.setReloadProgress(stackInHand, 0);
            this.setReloadStatus(stackInHand, true);

        }
    }

    public void playFiringSound(Level level, Player player, InteractionHand usedHand) {

    }
    public void shootPellets(Level level, Player player, InteractionHand usedHand, ItemStack stack) {

    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {

        if (oldStack.getItem() instanceof BaseGunItem baseGunItem && oldStack.getItem().equals(newStack.getItem())) {
            int firing_cooldown = baseGunItem.getFiringCooldown(oldStack); //if this is true, that would imply it was just fired.
            return firing_cooldown == getFiringInterval();
        }
        return !oldStack.equals(newStack);
    }

    public void spawnPellet(Level level, Player player, InteractionHand usedHand, double yawOffset, double pitchOffset, float damage) {
        HitscanPelletEntity temp_bullet = new HitscanPelletEntity(level, player, player.getItemInHand(usedHand), damage);
        temp_bullet.setPos(player.getX(), player.getEyeY() - 0.25, player.getZ());

        temp_bullet.setYRot(player.getYRot() + (float)yawOffset);
        temp_bullet.setXRot(player.getXRot() + (float)pitchOffset);

        level.addFreshEntity(temp_bullet);
    }

    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if (!level.isClientSide) {

            if (isInitialReload(stack) && !(((Player) entity).getItemInHand(InteractionHand.MAIN_HAND).equals(stack) || ((Player) entity).getItemInHand(InteractionHand.OFF_HAND).equals(stack))) {
                setReloadStatus(stack, true);
            }

            this.shouldCauseReequipAnimation(stack, stack, false);

            double current_bloom = this.getCurrentBloom(stack);
            int firing_cooldown = this.getFiringCooldown(stack);

            if (this.getCurrentBloom(stack) > 0D && this.getFiringCooldown(stack) <= 0) {
                this.setCurrentBloom(stack, Math.clamp((current_bloom - getBloomRefreshRate()), 0, 1));
            }
            if (firing_cooldown > 0) {
                this.setFiringCooldown(stack, firing_cooldown - 1);
            }

            if ((getCurrentClip(stack) < getClipSize()) && this.getFiringCooldown(stack) <= 0D && !this.isOnFiringCooldown(stack) && (((Player) entity).getMainHandItem() == stack || ((Player) entity).getOffhandItem() == stack) && getClipSize() != -1) {
                int reloadProgress = getReloadProgress(stack);
                //MZGuns.LOGGER.info(String.valueOf(getCurrentReloadCycleLength(stack)));
                if (reloadProgress >= getCurrentReloadCycleLength(stack)) {
                    setReloadProgress(stack, 0);
                    int currentClip = getCurrentClip(stack);
                    int clipToAdd = Math.clamp(currentClip + getReloadCycleAmount(), 0, getClipSize());
                    setCurrentClip(stack, clipToAdd);
                    setReloadStatus(stack, false);
                } else {
                    setReloadProgress(stack, reloadProgress + 1);}
            } else if (getReloadProgress(stack) != 0 && !(((Player) entity).getMainHandItem() == stack || ((Player) entity).getOffhandItem() == stack)) {
                setReloadProgress(stack, 0);
            }

            if (this.shouldPlayHitsound(stack)) {
                //MZGuns.LOGGER.info("Playing Hitsound");
                ((ServerPlayer)entity).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                this.updateHitsoundChecK(stack, false);
            }

        }
    }

    public final float getBaseDamage() {return this.baseDamage;}
    public final int getClipSize() {return this.clipSize;}
    public final int getFiringInterval() {return this.firingInterval;}
    public final int getCurrentReloadCycleLength(ItemStack stack) {
        //MZGuns.LOGGER.info(String.valueOf(isInitialReload(stack)));
        if (isInitialReload(stack)) {
            return getReloadInitialCycleLength();}
        else {return getReloadConsecutiveCycleLength();}
    }
    public boolean isInitialReload(ItemStack stack) {return stack.getOrDefault(ModDataComponents.IS_INITIAL_RELOAD, false);}
    public void setReloadStatus(ItemStack stack, boolean value) {stack.set(ModDataComponents.IS_INITIAL_RELOAD, value);}
    public final int getReloadInitialCycleLength() {return this.reloadInitialCycleLength;}
    public final int getReloadConsecutiveCycleLength() {return this.reloadConsecutiveCycleLength;}
    public final int getReloadCycleAmount() {return this.reloadCycleAmount;}

    public final double getBloomPerShot() {return this.bloomPerShot;}
    public final double getBloomRefreshRate() {return this.bloomRefreshRate;}
    public final double getMaxBloomRadius() {return this.maxBloomRadius;}

    public void setFiringCooldown(ItemStack stack, int newValue) {stack.set(ModDataComponents.CURRENT_FIRING_COOLDOWN, newValue);}

    public int getFiringCooldown(ItemStack stack) {return stack.getOrDefault(ModDataComponents.CURRENT_FIRING_COOLDOWN, 0);}

    public void setCurrentBloom(ItemStack stack, double newValue) {stack.set(ModDataComponents.CURRENT_BLOOM, newValue);}

    public double getCurrentBloom(ItemStack stack) {return stack.getOrDefault(ModDataComponents.CURRENT_BLOOM, 0D);}

    public boolean isOnFiringCooldown(ItemStack stack) {return getFiringCooldown(stack) > 0;}

    public void setCurrentClip(ItemStack stack, int newValue) {stack.set(ModDataComponents.CURRENT_CLIP, newValue);}

    public int getCurrentClip(ItemStack stack) {return stack.getOrDefault(ModDataComponents.CURRENT_CLIP, 0);}

    public void setReloadProgress(ItemStack stack, int newValue) {stack.set(ModDataComponents.RELOAD_PERCENTILE, newValue);}

    public int getReloadProgress(ItemStack stack) {return stack.getOrDefault(ModDataComponents.RELOAD_PERCENTILE, 0);}

    public boolean shouldPlayHitsound(ItemStack stack) {return stack.getOrDefault(ModDataComponents.SHOULD_PLAY_HITSOUND, false);}

    public void updateHitsoundChecK(ItemStack stack, boolean newValue) {stack.set(ModDataComponents.SHOULD_PLAY_HITSOUND, newValue);}
}
