package net.moppzarella.mzguns;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.item.ItemStack;
import net.moppzarella.mzguns.entity.ModEntities;
import net.moppzarella.mzguns.entity.client.HitscanPelletRenderer;
import net.moppzarella.mzguns.item.custom.GunItem;
import net.moppzarella.mzguns.network.PrimaryFireHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.network.PacketDistributor;

@Mod(value = MZGuns.MODID, dist = Dist.CLIENT)

@EventBusSubscriber(modid = MZGuns.MODID, value = Dist.CLIENT)
public class MZGunsClient {
    public MZGunsClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            ItemStack stackInMainHand = player.getMainHandItem();
            if (Minecraft.getInstance().options.keyAttack.isDown() && stackInMainHand.getItem() instanceof GunItem) {
                PacketDistributor.sendToServer(new PrimaryFireHandler.IsPrimaryFireKeyPressed(true));
            }
        }
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(ModEntities.HITSCAN_PELLET.get(), HitscanPelletRenderer::new);
    }
}
