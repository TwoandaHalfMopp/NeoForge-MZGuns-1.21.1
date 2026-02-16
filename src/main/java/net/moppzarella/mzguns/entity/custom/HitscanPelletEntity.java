package net.moppzarella.mzguns.entity.custom;

import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.entity.ModEntities;
import net.moppzarella.mzguns.item.custom.GunItem;
import net.moppzarella.mzguns.util.LaserPointerHitHelper;
import net.moppzarella.mzguns.util.ModDamageTypes;
import org.joml.Vector3f;

public class HitscanPelletEntity extends Projectile {
    private float baseDamage = 8.0F;

    public HitscanPelletEntity(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    public HitscanPelletEntity(Level level, LivingEntity shooter, float baseDamage) {
        super(ModEntities.HITSCAN_PELLET.get(), level);
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
        InteractionHand pUsedHand = InteractionHand.MAIN_HAND;
        ItemStack itemStack = pPlayer.getMainHandItem();
        if (!pLevel.isClientSide) {
            HitResult hitResult = LaserPointerHitHelper.getInstance().getHitResult(this);
            //MZGuns.LOGGER.info("\nPLAYER ROTATION: {},{}\nPELLET ROT: {}, {}",pPlayer.getXRot(), pPlayer.getYRot(), this.getXRot(), this.getYRot());


            //pLevel.addParticle(ParticleTypes.ELECTRIC_SPARK,this.getX(),this.getY(),this.getZ(),0,0,0);
            if (hitResult.getType() == HitResult.Type.BLOCK) {
                BlockHitResult blockHitResult = (BlockHitResult) hitResult;
                //MZGuns.LOGGER.info(blockHitResult.getBlockPos().toString());
            } else if (hitResult.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHitResult = (EntityHitResult) hitResult;

                Entity target = entityHitResult.getEntity();
                MZGuns.LOGGER.info(target.toString());

                target.hurt(ModDamageTypes.causeBulletDamage(pLevel.registryAccess(),target,this), this.baseDamage * calculateDamageFalloff(distanceTo(target)));
                //MZGuns.LOGGER.info("{} Blocks: {}x Damage",this.distanceTo(target),String.valueOf(calculateDamageFalloff(distanceTo(target))));
            }

            if (pLevel instanceof ServerLevel) {

                Vec3 lookDir = new Vec3(this.calculateViewVector(this.getXRot(),this.getYRot()).toVector3f());
                double particleInterval = 0.25;
                //MZGuns.LOGGER.info("lookDir: {}, {}, {}", lookDir.x, lookDir.y, lookDir.z);
                for(double i = 0; i < LaserPointerHitHelper.getInstance().getLaserDistance();i += particleInterval) {
                    //MZGuns.LOGGER.info("Particle {}: {}, {}, {}",i,this.getX() + (lookDir.x * i),this.getY() + (lookDir.y + i),this.getZ() + (lookDir.z + i));
                    ((ServerLevel) pLevel).sendParticles(
                            new DustParticleOptions(new Vector3f(1,1,1), 0.1F),
                            this.getX() + (lookDir.x * i),
                            this.getY() + (lookDir.y * i),
                            this.getZ() + (lookDir.z * i),
                            1,0,0,0,0);
                }
            }

        }

        //Perform On-Hit Logic
        //NOT IMPLEMENTED YET

        //Delete Self
        this.discard();

    }

    public float calculateDamageFalloff(float distance) {
        double result;

        if (distance <= 5) {
            result = 1;
        }
        else if (distance <= 10) {
            double pi = Math.PI;
            double numerator = pi * (distance - 5);

            double cosinAmount = numerator / 5;

            result = (0.25 * Math.cos(cosinAmount)) + 0.75;
        } else {result = 0.5;}

        return (float)result;
    }





}