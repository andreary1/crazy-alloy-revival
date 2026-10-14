package com.crazyalloy.revival.client.screen;

import com.crazyalloy.revival.CrazyAlloyRevival;
import com.crazyalloy.revival.block.entity.MineralConverterBlockEntity;
import com.crazyalloy.revival.menu.MineralConverterMenu;
import com.crazyalloy.revival.registry.ModItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/**
 * Mineral Converter screen: the material slot, an arrow, the Money and Coin slots with what one unit adds under
 * each, and the Convert button (active only when a conversion is possible). The "?" lists the default table.
 */
public class MineralConverterScreen extends AbstractContainerScreen<MineralConverterMenu> {
    private static final Identifier TEXTURE = CrazyAlloyRevival.id("textures/gui/container/mineral_converter.png");
    private static final int HELP_X = 158, HELP_Y = 5, HELP_SIZE = 12;
    private static final int BUTTON_X = 94, BUTTON_Y = 56, BUTTON_W = 64, BUTTON_H = 18;
    private static final int GHOST = 0x998B8B8B;

    private Button convertButton;

    public MineralConverterScreen(MineralConverterMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        this.convertButton = this.addRenderableWidget(Button.builder(Component.translatable("gui.crazyalloy_revival.mineral_converter.convert"),
                        b -> this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, MineralConverterMenu.BUTTON_CONVERT))
                .bounds(this.leftPos + BUTTON_X, this.topPos + BUTTON_Y, BUTTON_W, BUTTON_H).build());
        this.convertButton.active = this.menu.ready();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.convertButton.active = this.menu.ready();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = this.leftPos;
        int y = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        ghost(graphics, MineralConverterBlockEntity.SLOT_MONEY, new ItemStack(ModItems.MONEY.get()), MineralConverterMenu.MONEY_X, MineralConverterMenu.MONEY_Y);
        ghost(graphics, MineralConverterBlockEntity.SLOT_COIN, new ItemStack(ModItems.COIN.get()), MineralConverterMenu.COIN_X, MineralConverterMenu.COIN_Y);
    }

    private void ghost(GuiGraphicsExtractor graphics, int slot, ItemStack stack, int sx, int sy) {
        if (!this.menu.getSlot(slot).getItem().isEmpty()) {
            return;
        }
        int x = this.leftPos + sx;
        int y = this.topPos + sy;
        graphics.fakeItem(stack, x, y);
        graphics.fill(x, y, x + 16, y + 16, GHOST);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractLabels(graphics, mouseX, mouseY);
        int money = menu.money();
        int coin = menu.coin();
        if (money > 0 || coin > 0) {
            centered(graphics, Component.literal("+" + money), MineralConverterMenu.MONEY_X + 8, MineralConverterMenu.MONEY_Y + 20, 0xFF2E7D32);
            centered(graphics, Component.literal("+" + coin), MineralConverterMenu.COIN_X + 8, MineralConverterMenu.COIN_Y + 20, 0xFF9A6A00);
        } else if (menu.hasInput()) {
            centered(graphics, Component.translatable("gui.crazyalloy_revival.mineral_converter.rejected"),
                    MineralConverterMenu.INPUT_X + 8, MineralConverterMenu.INPUT_Y + 20, 0xFFB3261E);
        }
    }

    private void centered(GuiGraphicsExtractor graphics, Component text, int cx, int y, int color) {
        graphics.text(this.font, text, cx - this.font.width(text) / 2, y, color, false);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        int relX = mouseX - this.leftPos;
        int relY = mouseY - this.topPos;
        if (relX >= HELP_X && relX < HELP_X + HELP_SIZE && relY >= HELP_Y && relY < HELP_Y + HELP_SIZE) {
            List<Component> lines = new ArrayList<>();
            String base = "gui.crazyalloy_revival.mineral_converter.help.";
            lines.add(Component.translatable(base + "title"));
            for (int i = 1; Language.getInstance().has(base + i); i++) {
                lines.add(Component.translatable(base + i));
            }
            graphics.setComponentTooltipForNextFrame(this.font, lines, mouseX, mouseY);
        }
    }
}
