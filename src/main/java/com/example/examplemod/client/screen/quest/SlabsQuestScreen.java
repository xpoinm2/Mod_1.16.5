package com.example.examplemod.client.screen.quest;

import com.example.examplemod.ModItems;
import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.client.screen.main.ScrollArea;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SlabsQuestScreen extends AbstractQuestScreen {

    private static final ResourceLocation SLABS_TAG = new ResourceLocation("examplemod", "slabs");

    public SlabsQuestScreen(Screen parent) {
        super(parent, "Плиты");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Плиты бывают", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "деревянные и каменные", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Для задания нужно скрафтить", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "6 плит любого вида", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Скрафтить 6 плит любого вида", 0xFFFFFF00);
        y += 6;
        int itemsPerRow = Math.max(1, innerWidth / 20);
        int index = 0;
        // Показываем примеры плит из мода
        ItemStack[] exampleSlabs = {
            new ItemStack(ModItems.BRUSHWOOD_SLAB.get(), 6),
            new ItemStack(ModItems.OAK_SLAB.get(), 6),
            new ItemStack(ModItems.BIRCH_SLAB.get(), 6),
            new ItemStack(ModItems.SPRUCE_SLAB.get(), 6),
            new ItemStack(ModItems.COBBLESTONE_SLAB.get(), 6)
        };
        for (ItemStack stack : exampleSlabs) {
            int itemX = x + (index % itemsPerRow) * 20;
            int itemY = y + (index / itemsPerRow) * 22;
            if (GuiUtil.renderItemWithTooltip(this, ms, stack, itemX, itemY, mouseX, mouseY)) {
                hoveredStack = stack;
            }
            index++;
        }
        if (index > 0) {
            int rows = (index + itemsPerRow - 1) / itemsPerRow;
            y += rows * 22;
        }
        return y + 4;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Крафт плит на верстаке", 0xFFFFFF00);
        return drawParagraph(ms, x, y, innerWidth, "Выбирайте любые плиты (деревянные или каменные)", 0xFFFFFF00);
    }

    @Override
    protected boolean hasRequiredItems() {
        if (this.minecraft.player == null) {
            return false;
        }
        int count = 0;
        ITag<net.minecraft.item.Item> slabsTag = ItemTags.getAllTags().getTagOrEmpty(SLABS_TAG);
        for (ItemStack stack : this.minecraft.player.inventory.items) {
            if (stack.getItem().is(slabsTag)) {
                count += stack.getCount();
                if (count >= 6) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isSlabsCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setSlabsCompleted(true);
    }
}
