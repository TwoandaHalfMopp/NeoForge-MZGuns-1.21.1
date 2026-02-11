package net.moppzarella.mzguns.network.PrimaryFire;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.moppzarella.mzguns.item.custom.GunItem;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PrimaryFirePayloadHandler {

    public static void handleOnMain(final PrimaryFirePayload payload, final IPayloadContext context) {
        Player player = context.player();

        if (player instanceof ServerPlayer) {
            Item main_hand_item = player.getItemInHand(InteractionHand.MAIN_HAND).getItem();

            if (main_hand_item instanceof GunItem) {
                ((GunItem) main_hand_item).primaryFire(player);
            }
        }
    }

}
