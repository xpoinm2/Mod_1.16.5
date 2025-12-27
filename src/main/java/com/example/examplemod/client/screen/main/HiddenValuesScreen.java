package com.example.examplemod.client.screen.main;

import com.example.examplemod.client.FramedButton;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HiddenValuesScreen extends Screen {
    private static final int WIDTH = 150;
    private static final int HEIGHT = 150;
    private final Screen parent;
    private TextFieldWidget passwordField;
    private boolean unlocked = false;

    public HiddenValuesScreen(Screen parent) {
        super(new StringTextComponent("Скрытые значения"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int x0 = 5;
        int y0 = 5;
        this.addButton(new FramedButton(x0 + 5, y0 + 5, 20, 20, "<", 0xFFFFFF00, 0xFFFFFFFF,
                b -> this.minecraft.setScreen(parent)));
        if (!unlocked) {
            passwordField = new TextFieldWidget(this.font, x0 + 15, y0 + 40, 120, 20, new StringTextComponent(""));
            this.children.add(passwordField);
            this.setFocused(passwordField);
            this.addButton(new FramedButton(x0 + 15, y0 + 70, 120, 20, "OK", 0xFFFFFF00, 0xFFFF0000,
                    b -> checkPassword()));
        }
        super.init();
    }

    private void checkPassword() {
        if ("1234".equals(passwordField.getValue())) {
            unlocked = true;
            clearWidgets();
            this.init();
        }
    }

    /**
     * Remove all widgets from this screen.
     * <p>
     * Forge's {@code Screen} class in 1.16.5 does not expose a helper for
     * clearing the widget lists.  The method below mimics the behaviour of the
     * later {@code clearWidgets()} utility by wiping both the {@code buttons}
     * and {@code children} collections.  This allows the screen to be
     * reinitialised without leaving orphaned widgets behind when the password is
     * entered correctly.
     */
    private void clearWidgets() {
        this.buttons.clear();
        this.children.clear();
    }

    private int getAmbientTemperature(PlayerEntity player) {
        if (player == null) return 0;
        World world = player.level;

        // Dimensions take precedence over biome categories
        if (world.dimension() == World.NETHER) {
            return 666;
        }
        if (world.dimension() == World.END) {
            return -666;
        }

        Biome biome = world.getBiome(player.blockPosition());
        Biome.Category cat = biome.getBiomeCategory();

        switch (cat) {
            case PLAINS:
                return 23;
            case DESERT:
            case MESA:
                return 37;
            case SAVANNA:
                return 30;
            case FOREST:
                return 17;
            case JUNGLE:
                return 30;
            case SWAMP:
                return -13;
            case TAIGA:
                return -25;
            case EXTREME_HILLS:
                return -10;
            case ICY:
                return -40;
            case BEACH:
            case RIVER:
                return 10;
            case OCEAN:
                return 6;
            case MUSHROOM:
                return 0;
            case NETHER:
                return 666;
            case THEEND:
                return -666;
            default:
                return 0;
        }
    }

    @Override
    public void render(MatrixStack ms, int mouseX, int mouseY, float pt) {
        this.renderBackground(ms);
        int x0 = 5;
        int y0 = 5;
        fill(ms, x0 - 1, y0 - 1, x0 + WIDTH + 1, y0 + HEIGHT + 1, 0xFF00FF00);
        fill(ms, x0, y0, x0 + WIDTH, y0 + HEIGHT, 0xFF000000);
        drawCenteredString(ms, this.font, this.title, x0 + WIDTH / 2, y0 + 10, 0xFF00FFFF);

        if (!unlocked) {
            passwordField.render(ms, mouseX, mouseY, pt);
        } else {
            long time = this.minecraft.level.getDayTime();
            long dayTime = time % 24000L;
            int hour = (int) ((dayTime / 1000L + 6) % 24);
            int minute = (int) ((dayTime % 1000L) * 60L / 1000L);
            String text = String.format("Игровое время: %02d:%02d", hour, minute);
            this.font.draw(ms, text, x0 + 10, y0 + 40, 0xFFFFFF);


        PlayerEntity player = this.minecraft.player;
        int temp = getAmbientTemperature(player);
        String tempText = String.format("Температура: %d C", temp);
        this.font.draw(ms, tempText, x0 + 10, y0 + 55, 0xFFFFFF);

            if (player != null) {
                Biome biome = player.level.getBiome(player.blockPosition());
                String biomeName = biome.getRegistryName() != null
                        ? biome.getRegistryName().getPath()
                        : "unknown";
                String biomeText = String.format("Биом: %s", biomeName);
                this.font.draw(ms, biomeText, x0 + 10, y0 + 70, 0xFFFFFF);
            }
    }

        super.render(ms, mouseX, mouseY, pt);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null && this.minecraft.player != null && this.minecraft.level != null && parent != null) {
            this.minecraft.setScreen(parent);
        } else {
            super.onClose();
        }
    }
}