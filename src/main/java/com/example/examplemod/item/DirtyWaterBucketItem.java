package com.example.examplemod.item;

import com.example.examplemod.ModCreativeTabs;
import com.example.examplemod.ModFluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;

public class DirtyWaterBucketItem extends BucketItem {
    public DirtyWaterBucketItem() {
        super(() -> ModFluids.DIRTY_WATER.get(),
                new Item.Properties()
                        .stacksTo(1)
                        .tab(ModCreativeTabs.EXAMPLE_TAB));
    }
}
