package net.moppzarella.mzguns.util;


import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import net.moppzarella.mzguns.Config;
import net.moppzarella.mzguns.entity.custom.HitscanPelletEntity;

//THIS WAS STOLEN FROM DRG LASER POINTER BY linngdu664
//GITHUB: https://github.com/linngdu664/DRG-Laser-Pointer-Mod
//MODRINTH: https://modrinth.com/mod/drg-laser-pointer
//I WAS GENUINELY TOO LAZY TO FIGURE THIS OUT
//IN DUE TIME I WILL REWRITE THIS BUT LIKE IT WORKS PERFECT

public class LaserPointerHitHelper {
    public static final double LASER_RANGE = Config.HITSCAN_PELLET_RANGE.getAsInt();
    public static final double LASER_RANGE_SQ = Math.pow(LASER_RANGE, 2);

    private static class SingletonHandler {
        private static final LaserPointerHitHelper instance = new LaserPointerHitHelper();
    }

    private HitResult hitResult;
    private float laserDistance;

    private LaserPointerHitHelper() {}

    public static LaserPointerHitHelper getInstance() {
        return SingletonHandler.instance;
    }

    public void calcHitResult(HitscanPelletEntity pellet, float partialTick) {
        HitResult hitResult = pick2(pellet, LASER_RANGE, partialTick, false);
        Vec3 traceBegin = pellet.getPosition(partialTick);
        traceBegin = traceBegin.add(0, pellet.getEyeHeight(), 0);
        double distance = hitResult.getLocation().distanceTo(traceBegin);
        Vec3 scaledViewVec = pellet.getViewVector(partialTick).scale(LASER_RANGE);
        Vec3 traceEnd = traceBegin.add(scaledViewVec);
        AABB aabb = pellet.getBoundingBox().expandTowards(scaledViewVec).inflate(1.0);
        EntityHitResult entityHitResult = ProjectileUtil.getEntityHitResult(pellet, traceBegin, traceEnd, aabb, p -> !p.isSpectator() && (p instanceof LivingEntity) && (p != pellet.getOwner()), LASER_RANGE_SQ);
        if (entityHitResult != null && entityHitResult.getLocation().distanceTo(traceBegin) < distance) {
            hitResult = entityHitResult;
        }
        this.hitResult = hitResult;
        this.laserDistance = (float) hitResult.getLocation().distanceTo(pellet.getEyePosition(partialTick));
    }

    public HitResult pick2(HitscanPelletEntity pellet,double hitDistance, float partialTicks, boolean hitFluids) {
        Vec3 vec3 = pellet.getEyePosition(partialTicks);
        Vec3 vec31 = pellet.getViewVector(partialTicks);
        Vec3 vec32 = vec3.add(vec31.x * hitDistance, vec31.y * hitDistance, vec31.z * hitDistance);
        return pellet.level().clip(new ClipContext(vec3, vec32, ClipContext.Block.COLLIDER, hitFluids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE, pellet));
    }

    public HitResult getHitResult(HitscanPelletEntity pellet) {
        calcHitResult(pellet, 0);
        return hitResult;
    }

    public float getLaserDistance() {
        return laserDistance;
    }
}