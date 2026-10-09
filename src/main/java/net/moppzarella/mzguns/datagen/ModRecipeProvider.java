package net.moppzarella.mzguns.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.moppzarella.mzguns.item.ModItems;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.BETA_GUN.get())
                .pattern("121")
                .pattern("131")
                .pattern("141")
                .define('1', Items.IRON_INGOT)
                .define('2', Items.ANCIENT_DEBRIS)
                .define('3', Items.SCULK_CATALYST)
                .define('4', Items.STICK)
                .unlockedBy("has_sculk_catalyst", has(Items.SCULK_CATALYST)).save(recipeOutput);
    }
}
