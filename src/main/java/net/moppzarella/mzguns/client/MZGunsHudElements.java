package net.moppzarella.mzguns.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.moppzarella.mzguns.Config;
import net.moppzarella.mzguns.item.custom.BaseGunItem;
import net.moppzarella.mzguns.util.GunState;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

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
        if(!(Config.INFINITE_CLIP_IN_CREATIVE.getAsBoolean() && player.isCreative())) renderGunHudElements(guiGraphics, deltaTracker, mc, player);
    }

    public void renderGunHudElements(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker, @NotNull Minecraft mc, @NotNull Player player) {

        for (InteractionHand hand : InteractionHand.values()) {
            if (!player.getItemInHand(hand).isEmpty()) {
                ItemStack stack = player.getItemInHand(hand);
                if (stack.getItem() instanceof BaseGunItem baseGunItem) {
                    GunState currentState = baseGunItem.getState(stack);
                    int stateTimer = baseGunItem.getStateTimer(stack);

                    Vector2i guiElementPosition = new Vector2i(0,0);

                    guiElementPosition.x = guiGraphics.guiWidth() / 2;
                    guiElementPosition.y = guiGraphics.guiHeight() - mc.font.lineHeight;

                    switch (Config.GUN_GUI_LOCATIONS.get()) {
                        case ABOVE_HOTBAR: {
                            int distFromCenter = 91 / 2;
                            switch (hand) {
                                case MAIN_HAND: {
                                    guiElementPosition.x += distFromCenter;
                                    guiElementPosition.y -= mc.gui.rightHeight;
                                    break;
                                }
                                case OFF_HAND: {
                                    guiElementPosition.x -= distFromCenter;
                                    guiElementPosition.y -= mc.gui.leftHeight;
                                    break;
                                }
                            }

                            guiGraphics.drawCenteredString(mc.font, String.valueOf(currentState), guiElementPosition.x, guiElementPosition.y, 0xFFFFFF);
                            guiGraphics.drawCenteredString(mc.font, String.valueOf(stateTimer), guiElementPosition.x, guiElementPosition.y + mc.font.lineHeight, 0x808080);
                            break;
                        }
                        case HOTBAR_SIDE: {
                            int padding = 1;
                            int distFromCenter = 91 + padding;
                            guiElementPosition.y -= 24;
                            switch (hand) {
                                case MAIN_HAND: {
                                    guiElementPosition.x += distFromCenter;
                                    break;
                                }
                                case OFF_HAND: {
                                    guiElementPosition.x -= distFromCenter + mc.font.width(String.valueOf(currentState)) + padding;
                                    break;
                                }
                            }
                            guiGraphics.drawCenteredString(mc.font, String.valueOf(currentState), guiElementPosition.x, guiElementPosition.y, 0xFFFFFF);
                            guiGraphics.drawCenteredString(mc.font, String.valueOf(stateTimer), guiElementPosition.x, guiElementPosition.y + mc.font.lineHeight, 0x808080);
                            break;
                        }
                    }

                }
            }
        }

    }
}
