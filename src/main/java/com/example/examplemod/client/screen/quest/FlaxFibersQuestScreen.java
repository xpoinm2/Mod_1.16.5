package com.example.examplemod.client.screen.quest;

import com.example.examplemod.ModItems;
import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.client.screen.main.ScrollArea;
import com.example.examplemod.quest.QuestManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlaxFibersQuestScreen extends AbstractQuestScreen {

    public FlaxFibersQuestScreen(Screen parent) {
        super(parent, "Волокна льна");
    }

    @Override
    protected int renderDescription(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                    int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Лён нужен в", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "изготовлении множества", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "предметов.", 0xFFFFFF00);
        return y;
    }

    @Override
    protected int renderGoals(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                              int mouseX, int mouseY, float partialTicks) {
        y = drawParagraph(ms, x, y, innerWidth, "Сделать 10 волокон", 0xFFFFFF00);
        y = drawParagraph(ms, x, y, innerWidth, "льна", 0xFFFFFF00);
        ItemStack stack = new ItemStack(ModItems.FLAX_FIBERS.get(), 10);
        if (GuiUtil.renderItemWithTooltip(this, ms, stack, x, y, mouseX, mouseY)) {
            hoveredStack = stack;
        }
        return y + 24;
    }

    @Override
    protected int renderInstructions(ScrollArea area, MatrixStack ms, int x, int y, int innerWidth,
                                     int mouseX, int mouseY, float partialTicks) {
        String instruction = "Лён растение. Растет в биомах равнин, лесов и болот. Замочите лён в воде. "
                + "Вымоченный лён повесьте на листву. Через 2 минуты он высохнет. Гребнем соберите волокна.";
        for (IReorderingProcessor line : this.font.split(new StringTextComponent(instruction), innerWidth)) {
            this.font.draw(ms, line, x, y, 0xFFFFFF00);
            y += this.font.lineHeight + 2;
        }
        return y;
    }

    @Override
    protected boolean hasRequiredItems() {
        return this.minecraft.player != null
                && this.minecraft.player.inventory.countItem(ModItems.FLAX_FIBERS.get()) >= 10;
    }

    @Override
    protected boolean isQuestCompleted() {
        return QuestManager.isFlaxFibersCompleted();
    }

    @Override
    protected void markCompleted() {
        QuestManager.setFlaxFibersCompleted(true);
    }
}
