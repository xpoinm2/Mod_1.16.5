package com.example.examplemod.jei;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModBlocks;
import com.example.examplemod.ModItems;
import com.example.examplemod.container.ClayPotContainer;
import com.example.examplemod.container.FirepitContainer;
import com.example.examplemod.container.PechugaContainer;
import com.example.examplemod.item.MetalChunkItem;
import com.example.examplemod.jei.category.CobblestoneAnvilRecipeCategory;
import com.example.examplemod.jei.category.ClayMassRecipeCategory;
import com.example.examplemod.jei.category.ClayPotRecipeCategory;
import com.example.examplemod.jei.category.FirepitRecipeCategory;
import com.example.examplemod.jei.category.PechugaRecipeCategory;
import com.example.examplemod.jei.category.SlabDryingRecipeCategory;
import com.example.examplemod.jei.recipe.CobblestoneAnvilRecipe;
import com.example.examplemod.jei.recipe.ClayMassRecipe;
import com.example.examplemod.jei.recipe.ClayPotRecipe;
import com.example.examplemod.jei.recipe.FirepitRecipe;
import com.example.examplemod.jei.recipe.SlabDryingRecipe;
import com.example.examplemod.tileentity.CobblestoneAnvilTileEntity;
import com.example.examplemod.tileentity.ClayPotTileEntity;
import com.example.examplemod.tileentity.FirepitTileEntity;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class ExampleModJEIPlugin implements IModPlugin {
    public static final ResourceLocation PLUGIN_UID = new ResourceLocation(ExampleMod.MODID, "jei_plugin");

    public static final ResourceLocation CLAY_POT_CATEGORY_UID = new ResourceLocation(ExampleMod.MODID, "clay_pot_washing");
    public static final ResourceLocation FIREPIT_CATEGORY_UID = new ResourceLocation(ExampleMod.MODID, "firepit_cooking");
    public static final ResourceLocation PECHUGA_CATEGORY_UID = new ResourceLocation(ExampleMod.MODID, "pechuga_cooking");
    public static final ResourceLocation CLAY_MASS_CATEGORY_UID = new ResourceLocation(ExampleMod.MODID, "clay_mass_crafting");
    public static final ResourceLocation SLAB_DRYING_CATEGORY_UID = new ResourceLocation(ExampleMod.MODID, "slab_drying");
    public static final ResourceLocation COBBLESTONE_ANVIL_CATEGORY_UID = new ResourceLocation(ExampleMod.MODID, "cobblestone_anvil");

    @Override
    public ResourceLocation getPluginUid() {
        return PLUGIN_UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new ClayPotRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new FirepitRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new PechugaRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new ClayMassRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new SlabDryingRecipeCategory(registration.getJeiHelpers().getGuiHelper()),
                new CobblestoneAnvilRecipeCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        // Рецепты для глиняного горшка (промывка руды)
        List<ClayPotRecipe> clayPotRecipes = new ArrayList<>();
        clayPotRecipes.add(new ClayPotRecipe(
                new ItemStack(ModItems.TIN_ORE_GRAVEL.get()),
                new ItemStack(ModItems.CLEANED_GRAVEL_TIN_ORE.get())
        ));
        clayPotRecipes.add(new ClayPotRecipe(
                new ItemStack(ModItems.GOLD_ORE_GRAVEL.get()),
                new ItemStack(ModItems.CLEANED_GRAVEL_GOLD_ORE.get())
        ));
        clayPotRecipes.add(new ClayPotRecipe(
                new ItemStack(ModItems.IRON_ORE_GRAVEL.get()),
                new ItemStack(ModItems.PURE_IRON_ORE.get())
        ));

        registration.addRecipes(clayPotRecipes, CLAY_POT_CATEGORY_UID);

        // Рецепты для кострища (плавка/приготовление)
        List<FirepitRecipe> firepitRecipes = new ArrayList<>();

        // Руды (все по 400 тиков = 20 секунд)
        firepitRecipes.add(new FirepitRecipe(
                new ItemStack(ModItems.PURE_IRON_ORE.get()),
                new ItemStack(ModItems.CALCINED_IRON_ORE.get()),
                400
        ));
        firepitRecipes.add(new FirepitRecipe(
                new ItemStack(ModItems.IRON_ORE_GRAVEL.get()),
                new ItemStack(ModItems.CALCINED_IRON_ORE.get()),
                400
        ));
        firepitRecipes.add(new FirepitRecipe(
                new ItemStack(ModItems.CLEANED_GRAVEL_TIN_ORE.get()),
                new ItemStack(ModItems.CALCINED_TIN_ORE.get()),
                400
        ));
        firepitRecipes.add(new FirepitRecipe(
                new ItemStack(ModItems.TIN_ORE_GRAVEL.get()),
                new ItemStack(ModItems.CALCINED_TIN_ORE.get()),
                400
        ));
        firepitRecipes.add(new FirepitRecipe(
                new ItemStack(ModItems.CLEANED_GRAVEL_GOLD_ORE.get()),
                new ItemStack(ModItems.CALCINED_GOLD_ORE.get()),
                400
        ));
        firepitRecipes.add(new FirepitRecipe(
                new ItemStack(ModItems.GOLD_ORE_GRAVEL.get()),
                new ItemStack(ModItems.CALCINED_GOLD_ORE.get()),
                400
        ));

        // Глина (первый этап - 400 тиков, второй этап пережаривания - 300 тиков)
        firepitRecipes.add(new FirepitRecipe(
                new ItemStack(ModItems.RAW_CLAY_CUP.get()),
                new ItemStack(ModItems.CLAY_CUP.get()),
                400
        ));
        firepitRecipes.add(new FirepitRecipe(
                new ItemStack(ModItems.CLAY_CUP.get()),
                new ItemStack(ModItems.CLAY_SHARDS.get()),
                300
        ));
        firepitRecipes.add(new FirepitRecipe(
                new ItemStack(ModItems.RAW_CLAY_POT.get()),
                new ItemStack(ModItems.CLAY_POT.get()),
                400
        ));
        firepitRecipes.add(new FirepitRecipe(
                new ItemStack(ModItems.CLAY_POT.get()),
                new ItemStack(ModItems.CLAY_SHARDS.get()),
                300
        ));
        
        // Кирпичи (сушеный -> обоженный: 800 тиков)
        firepitRecipes.add(new FirepitRecipe(
                new ItemStack(ModItems.DRIED_CLAY_BRICK.get()),
                new ItemStack(ModItems.FIRED_BRICK.get()),
                800
        ));
        firepitRecipes.add(new FirepitRecipe(
                new ItemStack(ModItems.FIRED_BRICK.get()),
                new ItemStack(ModItems.CLAY_SHARDS.get()),
                400
        ));

        // Древесный уголь (ванильный аналог: бревно -> древесный уголь)
        firepitRecipes.add(new FirepitRecipe(
                new ItemStack(net.minecraft.item.Items.OAK_LOG),
                new ItemStack(net.minecraft.item.Items.CHARCOAL),
                200
        ));

        registration.addRecipes(firepitRecipes, FIREPIT_CATEGORY_UID);

        // Рецепты для кирпичной печи (расширенные рецепты кострища + губчатые металлы)
        List<FirepitRecipe> pechugaRecipes = new ArrayList<>(firepitRecipes);

        // Обжженная руда -> губчатые металлы (30 секунд = 600 тиков, только в кирпичной печи)
        pechugaRecipes.add(new FirepitRecipe(
                new ItemStack(ModItems.CALCINED_IRON_ORE.get()),
                new ItemStack(ModItems.SPONGE_IRON.get()),
                600
        ));
        pechugaRecipes.add(new FirepitRecipe(
                new ItemStack(ModItems.CALCINED_TIN_ORE.get()),
                new ItemStack(ModItems.SPONGE_TIN.get()),
                600
        ));
        pechugaRecipes.add(new FirepitRecipe(
                new ItemStack(ModItems.CALCINED_GOLD_ORE.get()),
                new ItemStack(ModItems.SPONGE_GOLD.get()),
                600
        ));
        pechugaRecipes.add(new FirepitRecipe(
                createChunkWithTemperature(ModItems.IRON_CHUNK.get(), MetalChunkItem.TEMP_COLD),
                createChunkWithTemperature(ModItems.IRON_CHUNK.get(), MetalChunkItem.TEMP_HOT),
                400
        ));
        pechugaRecipes.add(new FirepitRecipe(
                createChunkWithTemperature(ModItems.TIN_CHUNK.get(), MetalChunkItem.TEMP_COLD),
                createChunkWithTemperature(ModItems.TIN_CHUNK.get(), MetalChunkItem.TEMP_HOT),
                400
        ));
        pechugaRecipes.add(new FirepitRecipe(
                createChunkWithTemperature(ModItems.GOLD_CHUNK.get(), MetalChunkItem.TEMP_COLD),
                createChunkWithTemperature(ModItems.GOLD_CHUNK.get(), MetalChunkItem.TEMP_HOT),
                400
        ));

        registration.addRecipes(pechugaRecipes, PECHUGA_CATEGORY_UID);

        // Рецепт глиняной массы
        registration.addRecipes(java.util.Arrays.asList(ClayMassRecipe.create()), CLAY_MASS_CATEGORY_UID);

        // Рецепты сушки на плитняках (сырой кирпич -> сушеный кирпич: 500 тиков = 25 секунд)
        List<SlabDryingRecipe> slabDryingRecipes = new ArrayList<>();
        slabDryingRecipes.add(new SlabDryingRecipe(
                new ItemStack(ModItems.RAW_CLAY_BRICK.get()),
                new ItemStack(ModItems.DRIED_CLAY_BRICK.get()),
                500
        ));
        registration.addRecipes(slabDryingRecipes, SLAB_DRYING_CATEGORY_UID);

        // Рецепты каменной наковальни (губчатый металл -> металлический кусок)
        List<CobblestoneAnvilRecipe> cobblestoneAnvilRecipes = new ArrayList<>();
        java.util.List<ItemStack> hammerTools = java.util.Arrays.asList(
                new ItemStack(ModItems.STONE_HAMMER.get()),
                new ItemStack(ModItems.BONE_HAMMER.get())
        );
        cobblestoneAnvilRecipes.add(new CobblestoneAnvilRecipe(
                new ItemStack(ModItems.SPONGE_IRON.get()),
                createDefaultChunk(ModItems.IRON_CHUNK.get()),
                hammerTools,
                CobblestoneAnvilTileEntity.MAX_PROGRESS
        ));
        cobblestoneAnvilRecipes.add(new CobblestoneAnvilRecipe(
                new ItemStack(ModItems.SPONGE_TIN.get()),
                createDefaultChunk(ModItems.TIN_CHUNK.get()),
                hammerTools,
                CobblestoneAnvilTileEntity.MAX_PROGRESS
        ));
        cobblestoneAnvilRecipes.add(new CobblestoneAnvilRecipe(
                new ItemStack(ModItems.SPONGE_GOLD.get()),
                createDefaultChunk(ModItems.GOLD_CHUNK.get()),
                hammerTools,
                CobblestoneAnvilTileEntity.MAX_PROGRESS
        ));
        cobblestoneAnvilRecipes.add(new CobblestoneAnvilRecipe(
                createChunkWithTemperature(ModItems.IRON_CHUNK.get(), MetalChunkItem.TEMP_HOT),
                new ItemStack(ModItems.RAW_IRON_BLANK.get()),
                hammerTools,
                CobblestoneAnvilTileEntity.MAX_PROGRESS
        ));
        cobblestoneAnvilRecipes.add(new CobblestoneAnvilRecipe(
                createChunkWithTemperature(ModItems.TIN_CHUNK.get(), MetalChunkItem.TEMP_HOT),
                new ItemStack(ModItems.RAW_TIN_BLANK.get()),
                hammerTools,
                CobblestoneAnvilTileEntity.MAX_PROGRESS
        ));
        cobblestoneAnvilRecipes.add(new CobblestoneAnvilRecipe(
                createChunkWithTemperature(ModItems.GOLD_CHUNK.get(), MetalChunkItem.TEMP_HOT),
                new ItemStack(ModItems.RAW_GOLD_BLANK.get()),
                hammerTools,
                CobblestoneAnvilTileEntity.MAX_PROGRESS
        ));
        registration.addRecipes(cobblestoneAnvilRecipes, COBBLESTONE_ANVIL_CATEGORY_UID);

        // Регистрируем информацию о предметах
        registration.addIngredientInfo(new ItemStack(ModBlocks.CLAY_POT.get()), VanillaTypes.ITEM,
                "jei.examplemod.clay_pot.description");
        registration.addIngredientInfo(new ItemStack(ModBlocks.FIREPIT_BLOCK.get()), VanillaTypes.ITEM,
                "jei.examplemod.firepit.description");
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(ClayPotContainer.class, CLAY_POT_CATEGORY_UID,
                ClayPotTileEntity.INV_SLOTS, ClayPotTileEntity.INV_SLOTS, ClayPotTileEntity.TOTAL_SLOTS, 36);

        registration.addRecipeTransferHandler(FirepitContainer.class, FIREPIT_CATEGORY_UID,
                0, FirepitTileEntity.GRID_SLOT_COUNT, FirepitTileEntity.GRID_SLOT_COUNT + 1, 36);

        registration.addRecipeTransferHandler(PechugaContainer.class, PECHUGA_CATEGORY_UID,
                0, FirepitTileEntity.GRID_SLOT_COUNT, FirepitTileEntity.GRID_SLOT_COUNT + 1, 36);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CLAY_POT.get()), CLAY_POT_CATEGORY_UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.CLAY_POT.get()), CLAY_MASS_CATEGORY_UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.FIREPIT_BLOCK.get()), FIREPIT_CATEGORY_UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.PECHUGA_BLOCK.get()), PECHUGA_CATEGORY_UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.OAK_SLAB.get()), SLAB_DRYING_CATEGORY_UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.BIRCH_SLAB.get()), SLAB_DRYING_CATEGORY_UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.SPRUCE_SLAB.get()), SLAB_DRYING_CATEGORY_UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COBBLESTONE_SLAB.get()), SLAB_DRYING_CATEGORY_UID);
        registration.addRecipeCatalyst(new ItemStack(ModBlocks.COBBLESTONE_ANVIL.get()), COBBLESTONE_ANVIL_CATEGORY_UID);
    }

    private static ItemStack createDefaultChunk(net.minecraft.item.Item chunkItem) {
        ItemStack stack = new ItemStack(chunkItem);
        MetalChunkItem.setState(stack, MetalChunkItem.STATE_MEDIUM);
        return stack;
    }

    private static ItemStack createChunkWithTemperature(net.minecraft.item.Item chunkItem, int temperature) {
        ItemStack stack = createDefaultChunk(chunkItem);
        MetalChunkItem.setTemperature(stack, temperature);
        return stack;
    }
}
