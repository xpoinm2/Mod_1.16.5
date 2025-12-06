package com.example.examplemod.client.screen;

import com.example.examplemod.ModItems;
import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CleanedGravelTinOreQuestScreen extends AbstractQuestScreen {

    public CleanedGravelTinOreQuestScreen(Screen parent) {
        super(parent, "Очищённая гравийная оловянная руда");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Гравий с примесями приводит к некачественной руде.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Очищение даёт более стабильный металл и пригодно для кузни.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Набрать 16 очищённых гравийных блоков оловянной руды.", 0xFFFFFF00);
        y += 6;
        ItemStack stack = new ItemStack(ModItems.CLEANED_GRAVEL_TIN_ORE.get(), 16);
        if (GuiUtil.renderItemWithTooltip(this, ms, stack, x, y, mouseX, mouseY)) {
            hoveredStack = stack;
        }
        y += 22;
        return y;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Разбивайте молотом неочищенные блоки гравийной руды, чтобы получить пачки с грязным оловянным гравием.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Держите грязный гравий в руке и нажимайте ПКМ на воду — поток смывает примеси и превращает каждый блок в очищённый.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Альтернативный рецепт: три гравийных блока → один `tin_ore_gravel`, очищённые дают больше металла на выходе.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.countItem(ModItems.CLEANED_GRAVEL_TIN_ORE.get()) >= 16;
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isCleanedGravelTinOreCompleted();
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isUnrefinedTinOreCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setCleanedGravelTinOreCompleted(true);
    }
}

