package net.moppzarella.mzguns.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.moppzarella.mzguns.item.custom.BaseGunItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin extends Player {

    public LocalPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @Shadow
    public abstract boolean isUsingItem();

    @Shadow
    public abstract InteractionHand getUsedItemHand();

    @Definition(id = "isUsingItem", method = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z")
    @Expression("this.isUsingItem()")
    @ModifyExpressionValue(at = @At("MIXINEXTRAS:EXPRESSION"), method = "aiStep")
    private boolean dontSlowIfGun(boolean original) {
        return original && !usedItemIsOffhandGun();
    }

    @Definition(id = "isUsingItem", method = "Lnet/minecraft/client/player/LocalPlayer;isUsingItem()Z")
    @Expression("this.isUsingItem()")
    @ModifyExpressionValue(at = @At("MIXINEXTRAS:EXPRESSION"), method = "canStartSprinting")
    private boolean dontDisableSprintIfGun(boolean original) {
        return original ^ (usedItemIsOffhandGun());
    }

    @Unique
    private boolean usedItemIsOffhandGun() {
        return this.isUsingItem()
                && this.getUsedItemHand() == InteractionHand.OFF_HAND
                && this.getOffhandItem().getItem() instanceof BaseGunItem;
    }
}
