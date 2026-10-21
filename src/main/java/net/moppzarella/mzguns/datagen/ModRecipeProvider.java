package net.moppzarella.mzguns.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.moppzarella.mzguns.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        SmithingTransformRecipeBuilder.smithing(
                Ingredient.of(Items.IRON_INGOT),
                Ingredient.of(Items.ANCIENT_DEBRIS),
                Ingredient.of(Items.SCULK_CATALYST),
                RecipeCategory.COMBAT,
                ModItems.BETA_GUN.asItem())
                .unlocks("has_ancient_debris", has(Items.ANCIENT_DEBRIS))
                .save(recipeOutput, "beta_gun_smithing");
    }
}
