package com.example.examplemod.datagen;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraft.block.SlabBlock;
import net.minecraftforge.fml.RegistryObject;
import net.minecraft.block.Block;

/**
 * Генератор состояний блоков и моделей
 */
public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, ExampleMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // Генерация моделей для полублоков
        generateSlabModels();

        // Генерация моделей для других блоков
        generateOtherBlockModels();
    }

    /**
     * Генерирует модели для всех типов полублоков
     */
    private void generateSlabModels() {
        // Маппинг типов дерева к блокам
        java.util.Map<String, RegistryObject<Block>> woodSlabs = new java.util.HashMap<>();
        woodSlabs.put("oak", ModBlocks.OAK_SLAB);
        woodSlabs.put("spruce", ModBlocks.SPRUCE_SLAB);
        woodSlabs.put("birch", ModBlocks.BIRCH_SLAB);
        woodSlabs.put("jungle", ModBlocks.JUNGLE_SLAB);
        woodSlabs.put("acacia", ModBlocks.ACACIA_SLAB);
        woodSlabs.put("dark_oak", ModBlocks.DARK_OAK_SLAB);
        woodSlabs.put("crimson", ModBlocks.CRIMSON_SLAB);
        woodSlabs.put("warped", ModBlocks.WARPED_SLAB);

        for (java.util.Map.Entry<String, RegistryObject<Block>> entry : woodSlabs.entrySet()) {
            String woodType = entry.getKey();
            RegistryObject<Block> slabBlock = entry.getValue();
            String slabName = woodType + "_slab";
            String textureName = "minecraft:block/" + woodType + "_planks";

            // Создаем модели для полублока
            models().slab(slabName, mcLoc(textureName), mcLoc(textureName), mcLoc(textureName));
            models().slabTop(slabName + "_top", mcLoc(textureName), mcLoc(textureName), mcLoc(textureName));

            // Создаем состояния блока
            slabBlock((SlabBlock) slabBlock.get(),
                     modLoc("block/" + slabName),
                     modLoc("block/" + slabName + "_top"));
        }

        // Полублок булыжника
        models().slab("cobblestone_slab", mcLoc("block/cobblestone"), mcLoc("block/cobblestone"), mcLoc("block/cobblestone"));
        models().slabTop("cobblestone_slab_top", mcLoc("block/cobblestone"), mcLoc("block/cobblestone"), mcLoc("block/cobblestone"));
        slabBlock((SlabBlock) ModBlocks.COBBLESTONE_SLAB.get(),
                 modLoc("block/cobblestone_slab"),
                 modLoc("block/cobblestone_slab_top"));

        // Полублок хвороста
        models().slab("brushwood_slab", modLoc("block/brushwood_block"), modLoc("block/brushwood_block"), modLoc("block/brushwood_block"));
        models().slabTop("brushwood_slab_top", modLoc("block/brushwood_block"), modLoc("block/brushwood_block"), modLoc("block/brushwood_block"));
        slabBlock((SlabBlock) ModBlocks.BRUSHWOOD.get(),
                 modLoc("block/brushwood_slab"),
                 modLoc("block/brushwood_slab_top"));
    }

    /**
     * Генерирует модели для других блоков
     */
    private void generateOtherBlockModels() {
        // TODO: Реализовать генерацию моделей других блоков
    }
}
