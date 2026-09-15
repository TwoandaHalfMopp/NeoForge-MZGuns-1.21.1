package net.moppzarella.mzguns.client.event;

import net.moppzarella.mzguns.Config;
import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.client.gui.MZGunsHudElements;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = MZGuns.MODID)
public class ClientGuiEvent {

    @SubscribeEvent
    public static void registerGuiOverlays(RenderGuiEvent.Post event) {
        MZGunsHudElements.MZGUNS_HUD_ELEMENTS_INSTANCE.render(event.getGuiGraphics(), event.getPartialTick());
        if(Config.SHOW_DEBUG_HUD_ELEMENTS.getAsBoolean()) {
            MZGunsHudElements.MZGUNS_HUD_ELEMENTS_INSTANCE.render(event.getGuiGraphics(), event.getPartialTick());
        }
    }

}
