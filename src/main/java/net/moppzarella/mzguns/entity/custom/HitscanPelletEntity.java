package net.moppzarella.mzguns.entity.custom;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.*;
import net.moppzarella.mzguns.Config;
import net.moppzarella.mzguns.entity.ModEntities;
import net.moppzarella.mzguns.item.custom.BaseGunItem;
import net.moppzarella.mzguns.util.LaserPointerHitHelper;
import net.moppzarella.mzguns.util.ModDamageTypes;
import org.joml.Vector3f;

public class HitscanPelletEntity extends Projectile {
    private float baseDamage;
    private ItemStack sourceGun;
    private final float particleRenderDistanceThreshold = 1.5F;

    public HitscanPelletEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    public HitscanPelletEntity(Level level, LivingEntity shooter, ItemStack sourceGun, float baseDamage) {
        super(ModEntities.HITSCAN_PELLET.get(), level);
        this.sourceGun = sourceGun;
        this.baseDamage = baseDamage;
        this.setOwner(shooter);
    }


    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {

    }

    @Override
    public void tick() {
        super.tick();
        //MZGuns.LOGGER.info("HitscanPelletEntity.tick");
        Player pPlayer = (Player) this.getOwner();

        if (pPlayer == null) return;
        Level pLevel = this.level();
        if (!pLevel.isClientSide) {
            HitResult hitResult = LaserPointerHitHelper.getInstance().getHitResult(this);

            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                //MZGuns.LOGGER.info(blockHitResult.getBlockPos().toString());
            } else if (hitResult.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHitResult = (EntityHitResult) hitResult;

                Entity target = entityHitResult.getEntity();
                //MZGuns.LOGGER.info(target.toString());

                if (target instanceof LivingEntity livingTarget) {
                    livingTarget.setLastHurtByPlayer(pPlayer);
                    double damageMultiplier = 1;
                    Vec3 hitpos = entityHitResult.getLocation();
                    if(isHeadshot(livingTarget, hitpos)) {
                        damageMultiplier = Config.HEADSHOT_MULTIPLIER.getAsDouble();
                    } else {
                        damageMultiplier = calculateDamageFalloff(distanceTo(target));
                    }
                    if (this.baseDamage == -1) {
                        livingTarget.kill();
                    } else {
                        livingTarget.hurt(ModDamageTypes.causeBulletDamage(pLevel.registryAccess(),target,pPlayer), this.baseDamage * (float) damageMultiplier);
                    }
                    if (livingTarget != pPlayer && pPlayer instanceof ServerPlayer && !this.isSilent()) {
                        if (sourceGun.getItem() instanceof BaseGunItem gunItem) {
                            gunItem.updateHitsoundChecK(sourceGun, true);
                        }
                    }
                }
            }

            if (pLevel instanceof ServerLevel) {

                Vec3 lookDir = new Vec3(this.getViewVector(1f).toVector3f());
                //lookDir = lookDir.add(0, 0.25, 0);
                double particleInterval = 0.25;
                //MZGuns.LOGGER.info("lookDir: {}, {}, {}", lookDir.x, lookDir.y, lookDir.z);
                for(double i = 0; i < LaserPointerHitHelper.getInstance().getLaserDistance();i += particleInterval) {
                    //MZGuns.LOGGER.info("Particle {}: {}, {}, {}",i,this.getX() + (lookDir.x * i),this.getY() + (lookDir.y + i),this.getZ() + (lookDir.z + i));
                    if (i >= particleRenderDistanceThreshold) {
                        ((ServerLevel) pLevel).sendParticles(
                                new DustParticleOptions(new Vector3f(0F,1F,0F), 0.5F),
                                this.getX() + (lookDir.x * i),
                                this.getEyeY() + (lookDir.y * i),
                                this.getZ() + (lookDir.z * i),
                                1,0,0,0,0);
                    }
                }
                Vec3 hitPos = hitResult.getLocation();
                ((ServerLevel) pLevel).sendParticles(ParticleTypes.SMALL_GUST, hitPos.x,hitPos.y,hitPos.z,1,0,0,0,0);

                pLevel.playSound(
                        null,
                        hitPos.x,
                        hitPos.y,
                        hitPos.z,
                        SoundEvents.ANCIENT_DEBRIS_BREAK,
                        SoundSource.NEUTRAL,
                        0.25F,
                        0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F));
            }

        }

        //Perform On-Hit Logic
        //NOT IMPLEMENTED YET

        //Delete Self
        this.discard();

    }

    public static boolean isHeadshot(LivingEntity target, Vec3 hitpos) {
       double eyeY = target.getEyeY();
       double someConstant = 1D;
       AABB aabb = target.getHitbox();
       return !(target instanceof Player)
               || Math.abs(aabb.getCenter().y - eyeY) > someConstant * aabb.getYsize()
               || hitpos.y() >= eyeY - 0.3D;
    }

    public float calculateDamageFalloff(float distance) {
        double result;

        float r1 = (float)Config.FALLOFF_RANGE_1.getAsInt();
        float r2 = (float)Config.FALLOFF_RANGE_2.getAsInt();
        float r3 = (float)Config.FALLOFF_RANGE_3.getAsInt();
        float rm = (float)Config.FALLOFF_RANGE_MAX.getAsInt();

        double num;

        if (distance <= r1) {
            return 1;
        }
        if (r1 < distance && distance <= r2) {
            return (float)((Math.cos((Math.PI * (distance - r1)) / (r2-r1)) + 3D)/4D);
        }

        if (r2 < distance && distance <= r3) {
            return 0.5F;
        }

        if (r3 < distance && distance < rm) {
            return (float)(Math.sqrt((1D - Math.pow((distance - r3)/(rm-r3), 2)) / 4D));
        }
        return 0F;
    }





}