package net.moppzarella.mzguns.item.custom;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.util.GunState;

public class BurstSMGItem extends PistolItem{

    final float baseDamage;
    public final int clipSize;
    public final int firingInterval;
    public final int reloadTime;
    public final int deployTime;

    public final int maxBloomTime;
    public final int bloomPerShot;
    public final float bloomAngle;
    public final int burstInterval;
    public final int shotsPerBurst;

    public BurstSMGItem(float baseDamage, int clipSize, int firingInterval, int reloadTime, int deployTime, int shotsPerReload, int maxBloomTime, int bloomPerShot, float bloomAngle, int burstInterval, int shotsPerBurst, Properties properties) {
        super(baseDamage, clipSize, firingInterval, reloadTime, deployTime, shotsPerReload, maxBloomTime, bloomPerShot, bloomAngle, properties);

        this.baseDamage = baseDamage;
        this.clipSize = clipSize;
        this.firingInterval = firingInterval;
        this.reloadTime = reloadTime;
        this.deployTime = deployTime;

        this.maxBloomTime = maxBloomTime;
        this.bloomPerShot = bloomPerShot;
        this.bloomAngle = bloomAngle;

        this.burstInterval = burstInterval;
        this.shotsPerBurst = shotsPerBurst;
    }

    @Override
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
                    if (current_clip < clipSize) {
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
                    if (current_clip < clipSize) {
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
                    if (current_clip < clipSize) {
                        setState(stack, GunState.ACTIVE_RELOAD);
                    }
                    else {
                        setState(stack, GunState.ACTIVE_IDLE);
                    }
                    break;
                } else {
                    //MZGuns.LOGGER.info("TIMER: {}",state_timer);
                    if(state_timer % burstInterval == 0 && state_timer <= (shotsPerBurst-1) * burstInterval) {
                        //MZGuns.LOGGER.info("Burst Shot Fired");
                        shoot(level, player, stack);
                        }
                }
            }
            case GunState.ACTIVE_RELOAD -> {
                if (!(player.getMainHandItem().equals(stack) || player.getOffhandItem().equals(stack))) {
                    setState(stack, GunState.HOLSTERED);
                    break;
                }
                if (state_timer >= reloadTimeInitial) {
                    int clipToAdd = Math.clamp(current_clip + shotsPerReload, 0, clipSize);
                    setCurrentClip(stack, clipToAdd);
                    if(clipToAdd < clipSize) {
                        setState(stack, GunState.ACTIVE_RELOAD_CONSECUTIVE);
                        break;
                    } else {
                        setState(stack, GunState.ACTIVE_IDLE);
                    }
                }
            }
            case GunState.ACTIVE_RELOAD_CONSECUTIVE -> {
                if (!(player.getMainHandItem().equals(stack) || player.getOffhandItem().equals(stack))) {
                    setState(stack, GunState.HOLSTERED);
                    break;
                }
                if (state_timer >= reloadTimeConsecutive) {
                    int clipToAdd = Math.clamp(current_clip + shotsPerReload, 0, clipSize);
                    setCurrentClip(stack, clipToAdd);
                    if(clipToAdd < clipSize) {
                        setState(stack, GunState.ACTIVE_RELOAD_CONSECUTIVE);
                        break;
                    } else {
                        setState(stack, GunState.ACTIVE_IDLE);
                    }
                }
            }

        }
    }
}
