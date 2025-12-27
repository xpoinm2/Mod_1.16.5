package com.example.examplemod.client.screen.quest;

import com.example.examplemod.ModItems;
import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.client.screen.main.ScrollArea;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CleanedGravelGoldOreQuestScreen extends AbstractQuestScreen {

    public CleanedGravelGoldOreQuestScreen(Screen parent) {
        super(parent, "Очищённая гравийная золотая руда");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Золото любит чистую гравийную жилу без примесей.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Очистка помогает избежать потерь при плавке.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Подготовить 16 очищённых блоков золотого гравия.", 0xFFFFFF00);
        y += 6;
        ItemStack stack = new ItemStack(ModItems.CLEANED_GRAVEL_GOLD_ORE.get(), 16);
        if (GuiUtil.renderItemWithTooltip(this, ms, stack, x, y, mouseX, mouseY)) {
            hoveredStack = stack;
        }
        y += 22;
        return y;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Молотом разбивайте неочищенные блоки золотой гравийной руды — с каждого падает пачка грязного гравия.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Держите грязную гравийную руду в руке и нажмите ПКМ на поверхность воды, чтобы смыть примеси и получить очищённые блоки.", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "Альтернативный рецепт: три гравийных блока дают `gold_ore_gravel`, очищённые дают больше металла на выходе", 0xFFFFFF00);
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.countItem(ModItems.CLEANED_GRAVEL_GOLD_ORE.get()) >= 16;
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isCleanedGravelGoldOreCompleted();
    }

    @Override
    protected boolean isQuestUnlocked() {
        return QuestManager.isUnrefinedGoldOreCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setCleanedGravelGoldOreCompleted(true);
    }
}

