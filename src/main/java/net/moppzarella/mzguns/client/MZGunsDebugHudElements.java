package net.moppzarella.mzguns.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

public class MZGunsDebugHudElements implements LayeredDraw.Layer {
    public static MZGunsDebugHudElements MZGUNS_DEBUG_HUD_ELEMENTS_INSTANCE;
    public static void init() {
        MZGUNS_DEBUG_HUD_ELEMENTS_INSTANCE =  new MZGunsDebugHudElements();
    }

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {

    }
}
