package net.moppzarella.mzguns.mixin;


import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.moppzarella.mzguns.item.custom.GunItem;
import net.moppzarella.mzguns.network.PrimaryFire.PrimaryFirePayload;
import net.neoforged.neoforge.network.PacketDistributor;
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

    @Inject(method = "startAttack", at = @At(value = "HEAD"), cancellable = true)
    private void startPrimaryFire(CallbackInfoReturnable<Boolean> cir) {
        assert player != null;
        ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (itemStack.getItem() instanceof GunItem) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "continueAttack", at = @At(value = "HEAD"), cancellable = true)
    private void continuePrimaryFire(boolean leftClick, CallbackInfo ci){
        assert player != null;
        ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (itemStack.getItem() instanceof GunItem) {
            if (leftClick) {
                PacketDistributor.sendToServer(new PrimaryFirePayload(player.getId()));
                //MZGuns.LOGGER.info("continuePrimaryFire");
            }
            ci.cancel();
        }

    }
}
