package net.moppzarella.mzguns.item.custom;

import net.minecraft.core.Holder;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.moppzarella.mzguns.Config;
import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.component.ModDataComponents;
import net.moppzarella.mzguns.entity.custom.HitscanPelletEntity;
import net.moppzarella.mzguns.util.GunState;
import net.neoforged.neoforge.common.extensions.IItemExtension;

import static net.moppzarella.mzguns.item.ModItems.universalDeployTime;


public class BaseGunItem extends Item implements IItemExtension {

    public final float damagePerPellet;
    public final int clipSize;
    public final int firingInterval;
    public final int reloadTime;
    public final int deployTime;

    public final int xpCostPerShot;

    private final int stateTimerLimit = 100;
    private final int useTimerLimit = 20;

    public BaseGunItem(float damagePerPellet, int clipSize, int firingInterval, int xpCostPerShot, int reloadTime, Properties properties) {
        super(properties);

        this.damagePerPellet = damagePerPellet;
        this.clipSize = clipSize;
        this.firingInterval = firingInterval;
        this.reloadTime = reloadTime;
        this.xpCostPerShot = xpCostPerShot;
        this.deployTime = universalDeployTime;
    }

    public BaseGunItem(float damagePerPellet, int clipSize, int firingInterval, int xpCostPerShot, int reloadTime, int deployTime, Properties properties) {
        super(properties);

        this.damagePerPellet = damagePerPellet;
        this.clipSize = clipSize;
        this.firingInterval = firingInterval;
        this.reloadTime = reloadTime;
        this.xpCostPerShot = xpCostPerShot;
        this.deployTime = deployTime;
    }

    //If this BaseGunItem is in the off-hand, perform primaryFire
    //If it's in the main hand, do nothing.
//    @Override
//    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
//        if ((!level.isClientSide) && (usedHand == InteractionHand.OFF_HAND)){
//            primaryFire(level, player, usedHand);
//            player.startUsingItem(InteractionHand.OFF_HAND);
//            return InteractionResultHolder.success(player.getItemInHand(usedHand));
//        }
//        return InteractionResultHolder.fail(player.getItemInHand(usedHand));
//
//    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if ((!level.isClientSide) && (usedHand == InteractionHand.OFF_HAND)){
            player.startUsingItem(InteractionHand.OFF_HAND);
            setIsUseKeyPressed(player.getOffhandItem(), true);
            return InteractionResultHolder.success(player.getItemInHand(usedHand));
        }
        return InteractionResultHolder.fail(player.getItemInHand(usedHand));
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 200;
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if((!level.isClientSide) && (livingEntity instanceof Player player) && (player.getOffhandItem().equals(stack))) {
            primaryFire(level, player, InteractionHand.OFF_HAND);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if ((!level.isClientSide) && (livingEntity instanceof Player player)) {
            setIsUseKeyPressed(stack, false);
        }
    }

    public void primaryFire(Level level, Player player, InteractionHand usedHand) {
        ItemStack stackInHand = player.getItemInHand(usedHand);
        GunState current_state = getState(stackInHand);
        GunState prev_state = getPrevState(stackInHand);
        int state_timer = getStateTimer(stackInHand);
        if(!level.isClientSide && canPrimaryFire(stackInHand)) {
            shoot(level, player, stackInHand);
            setState(stackInHand, GunState.ACTIVE_FIRING);
        }
    }

    public void shoot(Level level, Player player, ItemStack stack) {
        playFiringSound(level, player);
        shootPellets(level, player, stack);
        if (!(Config.INFINITE_CLIP_IN_CREATIVE.getAsBoolean() && player.isCreative() && clipSize != -1)) {
            setCurrentClip(stack, this.getCurrentClip(stack) - 1);
        }
    }

    public void playFiringSound(Level level, Player player) {
        level.playSound(
                null,
                player.getX(),
                player.getY(),
                player.getZ(),
                SoundEvents.BREEZE_WIND_CHARGE_BURST,
                SoundSource.NEUTRAL,
                0.25F,
                0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );
    }
    public void shootPellets(Level level, Player player, ItemStack stack) {
        spawnPellet(level, player, stack, 0, 0, damagePerPellet);
    }

    public void spawnPellet(Level level, Player player, ItemStack stack, double yawOffset, double pitchOffset, float damage) {
        HitscanPelletEntity temp_bullet = new HitscanPelletEntity(level, player, stack, damage);
        temp_bullet.setPos(player.getX(), player.getEyeY() - 0.25, player.getZ());

        temp_bullet.setYRot(player.getYRot() + (float)yawOffset);
        temp_bullet.setXRot(player.getXRot() + (float)pitchOffset);

        level.addFreshEntity(temp_bullet);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        if(!level.isClientSide) {

            GunState current_state = getState(stack);
            GunState prev_state = getPrevState(stack);
            Player player = (Player) entity;

            runStateLogic(stack, level, player, current_state, prev_state);

            if (this.shouldPlayHitsound(stack)) {
                //MZGuns.LOGGER.info("Playing Hitsound");
                ((ServerPlayer)entity).connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                this.updateHitsoundChecK(stack, false);
            }
        }
    }

    public boolean canPrimaryFire(ItemStack stack) {
        GunState current_state = getState(stack);
        GunState prev_state = getPrevState(stack);
        int state_timer = getStateTimer(stack);
        if (current_state == GunState.HOLSTERED || current_state == GunState.DEPLOYING || current_state == GunState.ACTIVE_FIRING) {
            return false;
        }
        return getCurrentClip(stack) > 0;
    }

    public void runStateLogic(ItemStack stack, Level level, Player player, GunState current_state, GunState prev_state) {
        incrementStateTimer(stack);
        int state_timer = getStateTimer(stack);
        //MZGuns.LOGGER.info("STATE: {}\nTIMER: {}",current_state, state_timer);
        int current_clip = getCurrentClip(stack);
        switch(current_state) {
            case GunState.HOLSTERED -> {
                if (player.getMainHandItem().equals(stack) || player.getOffhandItem().equals(stack)) {
                    setState(stack, GunState.DEPLOYING);
                }
            }
            case GunState.DEPLOYING -> {
                if (!(player.getMainHandItem().equals(stack) || player.getOffhandItem().equals(stack))) {
                    setState(stack, GunState.HOLSTERED);
                    break;
                }
                if (state_timer >= deployTime) {
                    if (current_clip < clipSize && ((stack.getDamageValue() + xpCostPerShot) <= stack.getMaxDamage()) && !Config.INFINITE_RESERVE_AMMO.getAsBoolean()) {
                        setState(stack, GunState.ACTIVE_RELOAD);
                    }
                    else {
                        setState(stack, GunState.ACTIVE_IDLE);
                    }
                    break;
                }
            }
            case GunState.ACTIVE_IDLE -> {
                if (!(player.getMainHandItem().equals(stack) || player.getOffhandItem().equals(stack))) {
                    setState(stack, GunState.HOLSTERED);
                    break;
                } else {
                    if (current_clip < clipSize && ((stack.getDamageValue() + xpCostPerShot) <= (stack.getMaxDamage() - stack.getDamageValue()) && !Config.INFINITE_RESERVE_AMMO.getAsBoolean())) {
                        setState(stack, GunState.ACTIVE_RELOAD);
                    }
                }
            }
            case GunState.ACTIVE_FIRING -> {
                if (!(player.getMainHandItem().equals(stack) || player.getOffhandItem().equals(stack))) {
                    setState(stack, GunState.HOLSTERED);
                    break;
                }
                if (state_timer >= firingInterval) {
                    if (current_clip < clipSize && ((stack.getDamageValue() + xpCostPerShot)) <= (stack.getMaxDamage() - stack.getDamageValue()) && !Config.INFINITE_RESERVE_AMMO.getAsBoolean()) {
                        setState(stack, GunState.ACTIVE_RELOAD);
                    }
                    else {
                        setState(stack, GunState.ACTIVE_IDLE);
                    }
                    break;
                }
            }
            case GunState.ACTIVE_RELOAD -> {
                if (!(player.getMainHandItem().equals(stack) || player.getOffhandItem().equals(stack))) {
                    setState(stack, GunState.HOLSTERED);
                    break;
                }
                if (state_timer >= reloadTime) {
                    if (Config.INFINITE_RESERVE_AMMO.getAsBoolean()) {
                        setCurrentClip(stack, clipSize);
                    } else {

                        int availableReserve = Mth.clamp((stack.getMaxDamage() - stack.getDamageValue()) / xpCostPerShot, 0, clipSize);
                        stack.setDamageValue(stack.getDamageValue() + (availableReserve * xpCostPerShot));
                        setCurrentClip(stack, availableReserve);

                    }
                    setState(stack, GunState.ACTIVE_IDLE);
                }
            }

        }
    }

//    @Override
//    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
//
//        if (oldStack.getItem() instanceof BaseGunItem baseGunItem && oldStack.getItem().equals(newStack.getItem())) {
//            GunState current_state = getState(newStack);
//            int state_timer = getStateTimer(newStack);
//            return (current_state == GunState.ACTIVE_FIRING && state_timer == 0);
//
//        }
//        return false;
//    }
@Override
public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
    return false;
}

    public GunState getState(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.GUN_STATE, GunState.byId(0));
    }
    public GunState getPrevState(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.PREV_STATE, GunState.byId(0));
    }

    public void setState(ItemStack stack, GunState new_state) {
        GunState current_state = getState(stack);
        if (current_state != new_state) {
        stack.set(ModDataComponents.PREV_STATE, current_state);
        stack.set(ModDataComponents.GUN_STATE, new_state);
        resetStateTimer(stack);
        }
    }

    public int getStateTimer(ItemStack stack) {return stack.getOrDefault(ModDataComponents.STATE_TIMER, 0);}

    public void setStateTimer(ItemStack stack, int i) {stack.set(ModDataComponents.STATE_TIMER, i);}
    public void changeStateTimer(ItemStack stack, int i) {
        int state_timer = getStateTimer(stack);
        setStateTimer(stack, state_timer+i);
    }
    public void incrementStateTimer(ItemStack stack) {
        int state_timer = getStateTimer(stack);
        if(state_timer < stateTimerLimit) {
            changeStateTimer(stack, 1);
        }
    }
    public void resetStateTimer(ItemStack stack) {setStateTimer(stack, 0);}

    public void setCurrentClip(ItemStack stack, int i) {
        stack.set(ModDataComponents.CURRENT_CLIP, i);
    }

    public int getCurrentClip(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.CURRENT_CLIP, 0);
    }

    public boolean isUseKeyPressed(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.IS_USE_KEY_PRESSED, false);
    }

    public void setIsUseKeyPressed(ItemStack stack, boolean i) {
        stack.set(ModDataComponents.IS_USE_KEY_PRESSED, i);
    }


    public boolean shouldPlayHitsound(ItemStack stack) {return stack.getOrDefault(ModDataComponents.SHOULD_PLAY_HITSOUND, false);}

    public void updateHitsoundChecK(ItemStack stack, boolean newValue) {stack.set(ModDataComponents.SHOULD_PLAY_HITSOUND, newValue);}

    @Override
    public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
        return enchantment.is(Enchantments.MENDING);
    }
}