package com.example.examplemod.client.screen.quest;

import com.example.examplemod.ModBlocks;
import com.example.examplemod.client.GuiUtil;
import com.example.examplemod.client.FramedButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class FirepitStructureScreen extends Screen {

    private final Screen parent;
    private float rotationY = 45.0f;
    private float rotationX = 30.0f;
    private boolean isDragging = false;
    private double lastMouseX, lastMouseY;

    public FirepitStructureScreen(Screen parent) {
        super(new StringTextComponent("Структура кострища"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        this.addButton(new FramedButton(5, 5, 20, 20, "<", 0xFFFFFF00, 0xFFFFFFFF,
                b -> this.minecraft.setScreen(parent)));
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(ms);
        GuiUtil.drawPanel(ms, 10, 10, this.width - 20, this.height - 20);

        drawCenteredString(ms, this.font, this.title, this.width / 2, 30, 0xFF00FFFF);

        // Инструкция
        String instruction = "Удерживайте левую кнопку мыши и двигайте для вращения";
        drawCenteredString(ms, this.font, instruction, this.width / 2, this.height - 60, 0xFFFFFF00);

        // Рендерим 3D модель
        renderStructure(ms, mouseX, mouseY, partialTicks);

        super.render(ms, mouseX, mouseY, partialTicks);
    }

    private void renderStructure(MatrixStack ms, int mouseX, int mouseY, float partialTicks) {
        // Центрируем область рендеринга
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int renderSize = Math.min(this.width, this.height) / 3;
        float modelScale = renderSize / 2.0f;

        // Очищаем область
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(515);
        RenderSystem.depthMask(true);
        RenderSystem.disableCull();

        ms.pushPose();

        // Перемещаемся в центр экрана
        ms.translate(centerX, centerY, 400.0D);

        // Масштабируем
        ms.scale(modelScale, modelScale, modelScale);

        // Поворачиваем
        ms.mulPose(Vector3f.YP.rotationDegrees(rotationY));
        ms.mulPose(Vector3f.XP.rotationDegrees(rotationX));

        // Центрируем структуру (4x4)
        ms.translate(-2.0D, 0.0D, -2.0D);

        // Рендерим блоки кострища
        renderFirepit(ms);

        ms.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableDepthTest();
    }

    private void renderFirepit(MatrixStack ms) {
        BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

        // Состояния блоков
        BlockState brushwoodState = ModBlocks.BRUSHWOOD.get().defaultBlockState();
        BlockState oakSlabState = ModBlocks.OAK_SLAB.get().defaultBlockState();
        BlockState cobblestoneSlabState = ModBlocks.COBBLESTONE_SLAB.get().defaultBlockState();

        // Структура кострища: 4x4 на уровне y=0
        // - Углы: хворост (тонкие ветки)
        // - Края: деревянные плиты
        // - Центр: каменные плиты

        IRenderTypeBuffer.Impl bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        for (int x = 0; x < 4; x++) {
            for (int z = 0; z < 4; z++) {
                boolean isCorner = (x == 0 || x == 3) && (z == 0 || z == 3);
                boolean isEdge = (x == 0 || x == 3 || z == 0 || z == 3) && !isCorner;

                BlockState blockState;
                if (isCorner) {
                    blockState = brushwoodState;
                } else if (isEdge) {
                    blockState = oakSlabState;
                } else {
                    blockState = cobblestoneSlabState;
                }

                ms.pushPose();
                ms.translate(x, 0, z);
                blockRenderer.renderBlock(blockState, ms, bufferSource, 0xF000F0, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
                ms.popPose();
            }
        }

        bufferSource.endBatch();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) { // Левая кнопка мыши
            isDragging = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDragging && button == 0) {
            double deltaX = mouseX - lastMouseX;
            double deltaY = mouseY - lastMouseY;

            rotationY += (float) deltaX * 0.5f;
            rotationX += (float) deltaY * 0.5f;

            // Ограничиваем угол наклона
            rotationX = Math.max(-90.0f, Math.min(90.0f, rotationX));

            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isDragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.minecraft.setScreen(parent);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}