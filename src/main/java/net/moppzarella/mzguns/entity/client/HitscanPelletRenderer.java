package net.moppzarella.mzguns.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.moppzarella.mzguns.entity.custom.HitscanPelletEntity;

public class HitscanPelletRenderer extends EntityRenderer<HitscanPelletEntity> {

    public HitscanPelletRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(HitscanPelletEntity pEntity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(pEntity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(HitscanPelletEntity hitscanPelletEntity) {
        return null;
    }
}