package com.crazyalloy.revival.client.screen;

import com.crazyalloy.revival.menu.WizardTradingMenu;
import com.crazyalloy.revival.menu.WizardTradingMenu.Cost;
import com.crazyalloy.revival.menu.WizardTradingMenu.Recipe;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

/**
 * Wizard Trading: a grey panel with the two recipes in rows (materials with their counts, an arrow, the reward), each
 * with a Trade button, above the player's inventory. Revival additions: the reward's name is written under each row,
 * with how many of each material the player has and what is still missing; hovering a material shows the same.
 */
public class WizardTradingScreen extends AbstractContainerScreen<WizardTradingMenu> {
    private static final int W = 176, H = 222;
    private static final int ROW_Y0 = 18, ROW_H = 44;
    private final List<Button> tradeButtons = new ArrayList<>();

    public WizardTradingScreen(WizardTradingMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, W, H);
        this.inventoryLabelY = WizardTradingMenu.INV_Y - 11;
    }

    @Override
    protected void init() {
        super.init();
        this.tradeButtons.clear();
        for (int i = 0; i < WizardTradingMenu.RECIPES.size(); i++) {
            int id = i;
            this.tradeButtons.add(this.addRenderableWidget(Button.builder(Component.translatable("gui.crazyalloy_revival.wizard_trading.trade"),
                            b -> this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id))
                    .bounds(this.leftPos + 124, this.topPos + ROW_Y0 + i * ROW_H, 44, 18).build()));
        }
        this.addRenderableWidget(Button.builder(Component.translatable("gui.crazyalloy_revival.wizard_trading.other"),
                        b -> this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, WizardTradingMenu.BUTTON_OTHER_TRADES))
                .bounds(this.leftPos + 8, this.topPos + ROW_Y0 + 2 * ROW_H - 2, 90, 16).build());
        refresh();
    }

    private void refresh() {
        for (int i = 0; i < this.tradeButtons.size(); i++) {
            this.tradeButtons.get(i).active = WizardTradingMenu.canAfford(this.minecraft.player, WizardTradingMenu.RECIPES.get(i));
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        refresh();
    }

    private static int costX(int k) {
        return 8 + k * 24;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        int x = this.leftPos, y = this.topPos;
        g.fill(x, y, x + W, y + H, 0xFFC6C6C6);
        g.outline(x, y, W, H, 0xFF555555);
        for (int i = 0; i < WizardTradingMenu.RECIPES.size(); i++) {
            Recipe r = WizardTradingMenu.RECIPES.get(i);
            int ry = y + ROW_Y0 + i * ROW_H;
            g.fill(x + 4, ry - 3, x + W - 4, ry + ROW_H - 6, 0xFFB4B4B4);
            for (int k = 0; k < r.costs().size(); k++) {
                slot(g, x + costX(k), ry);
            }
            int ax = x + costX(2) + 2;   // the arrow after the two material slots
            g.fill(ax, ry + 7, ax + 14, ry + 9, 0xFF6E6E6E);
            g.fill(ax + 10, ry + 4, ax + 12, ry + 12, 0xFF6E6E6E);
            g.fill(ax + 12, ry + 6, ax + 14, ry + 10, 0xFF6E6E6E);
            slot(g, x + costX(2) + 22, ry);
        }
        // Inventory slots.
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                slot(g, x + 8 + col * 18, y + WizardTradingMenu.INV_Y + row * 18);
            }
        }
        for (int col = 0; col < 9; col++) {
            slot(g, x + 8 + col * 18, y + WizardTradingMenu.INV_Y + 58);
        }
    }

    private static void slot(GuiGraphicsExtractor g, int x, int y) {
        g.fill(x - 1, y - 1, x + 17, y + 17, 0xFF8B8B8B);
        g.fill(x - 1, y - 1, x + 16, y, 0xFF373737);
        g.fill(x - 1, y - 1, x, y + 16, 0xFF373737);
        g.fill(x, y + 16, x + 17, y + 17, 0xFFFFFFFF);
        g.fill(x + 16, y, x + 17, y + 17, 0xFFFFFFFF);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        super.extractLabels(g, mouseX, mouseY);
        var player = this.minecraft.player;
        for (int i = 0; i < WizardTradingMenu.RECIPES.size(); i++) {
            Recipe r = WizardTradingMenu.RECIPES.get(i);
            int ry = ROW_Y0 + i * ROW_H;
            List<Component> missing = new ArrayList<>();
            for (int k = 0; k < r.costs().size(); k++) {
                Cost c = r.costs().get(k);
                ItemStack icon = new ItemStack(c.item().get());
                g.item(icon, costX(k), ry);
                g.itemDecorations(this.font, icon, costX(k), ry, String.valueOf(c.count()));
                int have = WizardTradingMenu.carried(player, c.item().get());
                if (have < c.count()) {
                    missing.add(Component.translatable("gui.crazyalloy_revival.wizard_trading.count", c.count() - have, icon.getHoverName()));
                }
            }
            ItemStack result = r.result().get();
            g.item(result, costX(2) + 22, ry);
            g.text(this.font, result.getHoverName(), 8, ry + 20, 0xFF3A2A6A, false);
            Component status = missing.isEmpty() ? Component.translatable("gui.crazyalloy_revival.wizard_trading.ready")
                    : Component.translatable("gui.crazyalloy_revival.wizard_trading.missing", joined(missing));
            g.text(this.font, this.font.plainSubstrByWidth(status.getString(), W - 16), 8, ry + 30, missing.isEmpty() ? 0xFF2E7D32 : 0xFFB3261E, false);
        }
    }

    private static Component joined(List<Component> parts) {
        var out = Component.empty();
        for (int i = 0; i < parts.size(); i++) {
            if (i > 0) {
                out.append(", ");
            }
            out.append(parts.get(i));
        }
        return out;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(g, mouseX, mouseY, partialTick);
        int relX = mouseX - this.leftPos, relY = mouseY - this.topPos;
        var player = this.minecraft.player;
        for (int i = 0; i < WizardTradingMenu.RECIPES.size(); i++) {
            Recipe r = WizardTradingMenu.RECIPES.get(i);
            int ry = ROW_Y0 + i * ROW_H;
            if (relY < ry || relY >= ry + 16) {
                continue;
            }
            for (int k = 0; k < r.costs().size(); k++) {
                if (relX >= costX(k) && relX < costX(k) + 16) {
                    Cost c = r.costs().get(k);
                    ItemStack icon = new ItemStack(c.item().get());
                    int have = WizardTradingMenu.carried(player, c.item().get());
                    g.setComponentTooltipForNextFrame(this.font, List.of(icon.getHoverName(),
                            Component.translatable("gui.crazyalloy_revival.wizard_trading.have", have, c.count())), mouseX, mouseY);
                }
            }
            if (relX >= costX(2) + 22 && relX < costX(2) + 38) {
                g.setTooltipForNextFrame(this.font, r.result().get(), mouseX, mouseY);
            }
        }
    }
}
