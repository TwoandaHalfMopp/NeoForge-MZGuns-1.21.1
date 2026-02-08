package net.moppzarella.mzguns.mixin;


import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.item.custom.GunItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {
    @Shadow @Nullable public LocalPlayer player;
    @Shadow @Nullable public abstract ClientPacketListener getConnection();

    //@Inject(method = "startAttack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;getItemInHand(Lnet/minecraft/world/InteractionHand;)Lnet/minecraft/world/item/ItemStack;", shift = At.Shift.AFTER), cancellable = true)
    @Inject(method = "startAttack", at = @At(value = "HEAD"), cancellable = true)
    private void startPrimaryFire(CallbackInfoReturnable<Boolean> cir) {
        ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (itemStack.getItem() instanceof GunItem) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "continueAttack", at = @At(value = "HEAD"), cancellable = true)
    private void continuePrimaryFire(boolean leftClick, CallbackInfo ci){
        ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (itemStack.getItem() instanceof GunItem) {
            if (leftClick) {
                MZGuns.LOGGER.info("continuePrimaryFire");
            }
            ci.cancel();
        }

    }
}
