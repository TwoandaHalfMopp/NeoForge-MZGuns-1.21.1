package net.moppzarella.mzguns.event;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.moppzarella.mzguns.item.custom.BaseGunItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class PreventAttackWhenHoldingGun {

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        ItemStack itemInHand = event.getEntity().getItemInHand(InteractionHand.MAIN_HAND);
        if(itemInHand.getItem() instanceof BaseGunItem) {
            event.setCanceled(true);
        }
    }
}
