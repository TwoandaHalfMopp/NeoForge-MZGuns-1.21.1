package net.moppzarella.mzguns.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.item.custom.GunItem;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = MZGuns.MODID)
public class ClientPreventAttackWhenHoldingGun {

    @SubscribeEvent
    public static void onClickInput(InputEvent.InteractionKeyMappingTriggered event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if(player == null) return;

        ItemStack itemInHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        if(itemInHand.getItem() instanceof GunItem) {
            HitResult hitResult = Minecraft.getInstance().hitResult;
            if (hitResult instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() instanceof ItemFrame) {
                return;
            }
            event.setSwingHand(false);
            event.setCanceled(true);
        }
    }

}