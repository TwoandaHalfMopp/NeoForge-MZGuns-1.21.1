package net.moppzarella.mzguns.client.event;

import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.client.gui.MZGunsHudElements;
import net.moppzarella.mzguns.client.gui.StackCooldownRenderer;
import net.moppzarella.mzguns.item.ModItems;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = MZGuns.MODID)
public class stackCooldownOverlayEvent {

    @SubscribeEvent
    public static void stackCooldownOverlay(RegisterItemDecorationsEvent event) {

        event.register(ModItems.BETA_GUN, new StackCooldownRenderer());
        event.register(ModItems.PISTOL, new StackCooldownRenderer());
        event.register(ModItems.SHOTGUN, new StackCooldownRenderer());
        event.register(ModItems.BURST_SMG, new StackCooldownRenderer());

    }


}
