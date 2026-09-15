package net.moppzarella.mzguns.client.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.moppzarella.mzguns.MZGuns;
import net.moppzarella.mzguns.item.custom.BaseGunItem;
import net.moppzarella.mzguns.util.GunState;
import net.neoforged.neoforge.client.IItemDecorator;

public class StackCooldownRenderer implements LayeredDraw.Layer, IItemDecorator {

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {

    }

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack itemStack, int x, int y) {
        if (itemStack.getItem() instanceof BaseGunItem gunStack) {
            guiGraphics.pose().pushPose();
            if (gunStack.getState(itemStack) == GunState.DEPLOYING && gunStack.getStateTimer(itemStack) < gunStack.deployTime) {
                float remainingCooldown = 1.0F - ((float) gunStack.getStateTimer(itemStack) / (float) gunStack.deployTime);
                if (remainingCooldown > 0.0F) {
                    int barFloor = y + Mth.floor(16.0F * (1.0F - remainingCooldown));
                    int barCeiling = barFloor + Mth.ceil(16.0F * remainingCooldown);
                    guiGraphics.fill(RenderType.GUI_OVERLAY, x, barFloor, x + 16, barCeiling, Integer.MAX_VALUE);
                }
            }
            guiGraphics.pose().popPose();
            return true;
        }
        return false;
    }
}
