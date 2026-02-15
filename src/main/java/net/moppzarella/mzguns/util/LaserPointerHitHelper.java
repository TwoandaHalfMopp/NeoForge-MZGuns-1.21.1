package net.moppzarella.mzguns.util;


import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.*;
import net.moppzarella.mzguns.entity.custom.HitscanPelletEntity;

//THIS WAS STOLEN FROM DRG LASER POINTER BY linngdu664
//GITHUB: https://github.com/linngdu664/DRG-Laser-Pointer-Mod
//MODRINTH: https://modrinth.com/mod/drg-laser-pointer
//I WAS GENUINELY TOO LAZY TO FIGURE THIS OUT
//IN DUE TIME I WILL REWRITE THIS BUT LIKE IT WORKS PERFECT

public class LaserPointerHitHelper {
    public static final double LASER_RANGE = 100;
    public static final double LASER_RANGE_SQ = LASER_RANGE * LASER_RANGE;

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
        HitResult hitResult = pellet.pick(LASER_RANGE, partialTick, false);
        Vec3 traceBegin = pellet.getEyePosition(partialTick);
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

    public HitResult getHitResult(HitscanPelletEntity pellet) {
        calcHitResult(pellet, 0);
        return hitResult;
    }

    public float getLaserDistance() {
        return laserDistance;
    }
}