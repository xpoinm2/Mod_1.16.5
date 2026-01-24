package com.example.examplemod.client.screen.container;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.client.HammerButton;
import com.example.examplemod.container.CobblestoneAnvilContainer;
import com.example.examplemod.network.CobblestoneAnvilHammerPacket;
import com.example.examplemod.network.ModNetworkHandler;
import com.example.examplemod.tileentity.CobblestoneAnvilTileEntity;
import com.example.examplemod.item.SpongeMetalItem;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class CobblestoneAnvilScreen extends ContainerScreen<CobblestoneAnvilContainer> {
    private static final ResourceLocation BACKGROUND =
            new ResourceLocation(ExampleMod.MODID, "textures/gui/cobblestone_anvil.png");

    private static final ResourceLocation[] PROGRESS_FRAMES = new ResourceLocation[CobblestoneAnvilTileEntity.MAX_PROGRESS];

    static {
        for (int i = 0; i < CobblestoneAnvilTileEntity.MAX_PROGRESS; i++) {
            PROGRESS_FRAMES[i] = new ResourceLocation(ExampleMod.MODID,
                    String.format(java.util.Locale.ROOT, "textures/gui/cobblestone_anvil_progress/frame_%02d.png", i + 1));
        }
    }

    private HammerButton hammerButton;

    public CobblestoneAnvilScreen(CobblestoneAnvilContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();

        // Кнопка с молоточком между слотами 2 и 3 (инструмент и выход)
        int buttonX = this.leftPos + 108; // Между слотами
        int buttonY = this.topPos + 43;   // На уровне слотов
        this.hammerButton = new HammerButton(buttonX, buttonY, 16, 16, new StringTextComponent("⚒"), button -> {
            // Логика нажатия кнопки
            hammerPressed();
        });
        this.addButton(this.hammerButton);
    }

    private void hammerPressed() {
        // Отправляем пакет на сервер для удара молотом
        BlockPos anvilPos = getAnvilPosition();
        if (anvilPos != null) {
            ModNetworkHandler.CHANNEL.sendToServer(new CobblestoneAnvilHammerPacket(anvilPos));
        }
    }

    private BlockPos getAnvilPosition() {
        // Получаем позицию наковальни из контейнера
        if (this.menu instanceof CobblestoneAnvilContainer) {
            return ((CobblestoneAnvilContainer) this.menu).getBlockPos();
        }
        return null;
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(BACKGROUND);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);

        // Рисуем предметы, лежащие на наковальне
        renderItemsOnAnvil(matrixStack, i, j);

        // Рендерим анимацию прогресса между слотами 1 и 2
        renderProgressAnimation(matrixStack, i, j);
    }

    private void renderProgressAnimation(MatrixStack matrixStack, int guiLeft, int guiTop) {
        // Получаем текущий прогресс из TileEntity
        int progress = getCurrentProgress();

        if (progress > 0 && progress <= CobblestoneAnvilTileEntity.MAX_PROGRESS) {
            // Позиция анимации между слотами металла и инструмента
            int animX = guiLeft + 52; // Сдвинуто влево ещё на 5 пикселей от предыдущей позиции
            int animY = guiTop + 43;  // На уровне слотов

            // Сбрасываем цвет перед рендерингом анимации
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            
            // Привязываем текстуру прогресса
            this.minecraft.getTextureManager().bind(PROGRESS_FRAMES[progress - 1]);

            int frameSize = 16;
            // Рендерим кадр анимации (размер текстуры 16x16 пикселей)
            this.blit(matrixStack, animX, animY, 0, 0, frameSize, frameSize, frameSize, frameSize);
        }
    }

    /**
     * Отрисовка предметов, визуально лежащих на наковальне.
     */
    private void renderItemsOnAnvil(MatrixStack matrixStack, int guiLeft, int guiTop) {
        BlockPos anvilPos = getAnvilPosition();
        if (anvilPos == null || this.minecraft == null || this.minecraft.level == null) {
            return;
        }

        TileEntity tileEntity = this.minecraft.level.getBlockEntity(anvilPos);
        if (!(tileEntity instanceof CobblestoneAnvilTileEntity)) {
            return;
        }

        CobblestoneAnvilTileEntity anvil = (CobblestoneAnvilTileEntity) tileEntity;
        ItemStack metalStack = anvil.getInventory().getStackInSlot(CobblestoneAnvilTileEntity.METAL_SLOT);
        ItemStack toolStack = anvil.getInventory().getStackInSlot(CobblestoneAnvilTileEntity.TOOL_SLOT);

        // Координаты “поверх наковальни” (подбираются под текстуру cobblestone_anvil.png)
        int baseX = guiLeft + 48;
        int baseY = guiTop + 32;

        if (!metalStack.isEmpty()) {
            this.itemRenderer.renderAndDecorateItem(metalStack, baseX, baseY);
        }

        if (!toolStack.isEmpty()) {
            this.itemRenderer.renderAndDecorateItem(toolStack, baseX + 28, baseY + 2);
        }
    }

    private int getCurrentProgress() {
        // Получаем прогресс из TileEntity
        BlockPos anvilPos = getAnvilPosition();
        if (anvilPos != null && this.minecraft.level != null) {
            TileEntity tileEntity = this.minecraft.level.getBlockEntity(anvilPos);
            if (tileEntity instanceof CobblestoneAnvilTileEntity) {
                return ((CobblestoneAnvilTileEntity) tileEntity).getProgress();
            }
        }
        return 0;
    }

    /**
     * Проверка, есть ли в левом слоте валидный предмет для крафта (губчатый металл).
     */
    private boolean hasValidInput() {
        BlockPos anvilPos = getAnvilPosition();
        if (anvilPos == null || this.minecraft == null || this.minecraft.level == null) {
            return false;
        }

        TileEntity tileEntity = this.minecraft.level.getBlockEntity(anvilPos);
        if (!(tileEntity instanceof CobblestoneAnvilTileEntity)) {
            return false;
        }

        CobblestoneAnvilTileEntity anvil = (CobblestoneAnvilTileEntity) tileEntity;
        ItemStack metalStack = anvil.getInventory().getStackInSlot(CobblestoneAnvilTileEntity.METAL_SLOT);
        return !metalStack.isEmpty() && metalStack.getItem() instanceof SpongeMetalItem;
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY) {
        // Заголовок (белый цвет)
        this.font.draw(matrixStack, this.title, (float)(this.imageWidth / 2 - this.font.width(this.title) / 2), 6.0F, 0xFFFFFFFF);
        // Инвентарь игрока (белый цвет)
        this.font.draw(matrixStack, this.inventory.getDisplayName(), 8.0F, (float)(this.imageHeight - 96 + 2), 0xFFFFFFFF);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // Обновляем активность кнопки:
        // 1) в левом слоте должен быть валидный предмет
        // 2) кнопка не должна быть в кулдауне
        if (this.hammerButton != null) {
            this.hammerButton.active = hasValidInput() && !this.hammerButton.isOnCooldown();
        }

        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }
}