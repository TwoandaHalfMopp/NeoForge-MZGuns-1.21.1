package net.moppzarella.mzguns.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.item.custom.BaseGunItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    @Shadow
    private int rightClickDelay;

    @Inject(method = "startUseItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/InteractionResult;shouldSwing()Z", ordinal = 2))
    private void mzguns$rightClickDelayCancel(final CallbackInfo ci, @Local final ItemStack itemStack) {
        //MZGuns.LOGGER.info("test1");
        if (itemStack.getItem() instanceof BaseGunItem) {
            //MZGuns.LOGGER.info("test2");
            this.rightClickDelay = 0;
        }
    }
}
