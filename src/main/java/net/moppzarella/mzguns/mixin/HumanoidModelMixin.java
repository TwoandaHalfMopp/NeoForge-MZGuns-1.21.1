package net.moppzarella.mzguns.mixin;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.item.custom.BaseGunItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Array;
import java.util.Map;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin<T extends LivingEntity> {
    @Shadow @Final public ModelPart leftArm;
    @Shadow @Final public ModelPart rightArm;
    @Shadow @Final public ModelPart head;

    @Inject(at = @At("TAIL"), method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V")
    private void setLimbsIfHoldingGun(T livingEntity, float f, float g, float h, float i, float j, CallbackInfo ci) {
        if (livingEntity instanceof Player player) {

            if (player.getMainHandItem().getItem() instanceof BaseGunItem) {
                switch (player.getMainArm()) {
                    case LEFT:
                        leftArm.xRot = (float) Math.toRadians(player.getXRot()-90);
                        leftArm.yRot = head.yRot;
                        break;
                    case RIGHT:
                        rightArm.xRot = (float) Math.toRadians(player.getXRot()-90);
                        rightArm.yRot = head.yRot;
                        break;
                }
            }
            if (player.getOffhandItem().getItem() instanceof BaseGunItem) {
                switch (player.getMainArm()) {
                    case RIGHT:
                        leftArm.xRot = (float) Math.toRadians(player.getXRot()-90);
                        leftArm.yRot = head.yRot;
                        break;
                    case LEFT:
                        rightArm.xRot = (float) Math.toRadians(player.getXRot()-90);
                        rightArm.yRot = head.yRot;
                        break;
                }
            }

        }
    }
}
