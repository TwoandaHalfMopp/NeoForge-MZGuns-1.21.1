package net.moppzarella.mzguns.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.moppzarella.mzguns.item.custom.GunItem;
import org.jetbrains.annotations.NotNull;

public class MZGunsHudElements implements LayeredDraw.Layer {
    public static MZGunsHudElements MZGUNS_HUD_ELEMENTS_INSTANCE;

    public static void init() {
        MZGUNS_HUD_ELEMENTS_INSTANCE =  new MZGunsHudElements();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        assert player != null;
        ItemStack heldItemStack = player.getMainHandItem();

        if (heldItemStack.getItem() instanceof GunItem gunItem) {
            int currentClip = gunItem.getCurrentClip(heldItemStack);
            int clipSize = gunItem.clipSize;

            int reloadPercentile = gunItem.getReloadPercentile(heldItemStack);

            int reloadPercentage = (int) (((double)reloadPercentile / (double)gunItem.clipReloadThreshold) * 100D);

            PoseStack matrixStack = guiGraphics.pose();
            matrixStack.pushPose();
            matrixStack.translate(0, 0, 0);
            guiGraphics.drawString(mc.font, currentClip + " / " + clipSize, guiGraphics.guiWidth() / 2 + 91, guiGraphics.guiHeight() - mc.gui.rightHeight, Integer.parseInt("FFFFFF", 16));

            if (reloadPercentile != 0) {
                guiGraphics.drawString(mc.font, reloadPercentage + "%", guiGraphics.guiWidth() / 2 + 91, guiGraphics.guiHeight() - mc.gui.rightHeight + mc.font.lineHeight, Integer.parseInt("808080", 16));
            }

            matrixStack.popPose();
        }
    }

}
