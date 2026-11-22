package com.crazyalloy.revival.client.screen;

import com.crazyalloy.revival.item.ElementalBookItem;
import com.crazyalloy.revival.item.ElementalBookItem.Element;
import com.crazyalloy.revival.item.WizardStaffItem;
import com.crazyalloy.revival.menu.StaffMenu;
import com.crazyalloy.revival.registry.ModItems;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * The Wizard Staff's power menu: the six elements, each with its book's icon. Learned elements can be clicked to select
 * them; the selected one has a coloured frame; elements not learned yet are greyed out under a padlock, and their
 * tooltip says to learn them with the matching book.
 */
public class StaffScreen extends AbstractContainerScreen<StaffMenu> {
    private static final int W = 176, H = 124;
    private static final int TILE_W = 80, TILE_H = 24;
    private final List<Button> buttons = new ArrayList<>();

    public StaffScreen(StaffMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, W, H);
    }

    private static Item book(Element e) {
        return switch (e) {
            case FIRE -> ModItems.FIRE_BOOK.get();
            case WATER -> ModItems.WATER_BOOK.get();
            case POISON -> ModItems.POISON_BOOK.get();
            case AIR -> ModItems.AIR_BOOK.get();
            case LIGHTNING -> ModItems.LIGHTNING_BOOK.get();
            case EARTH -> ModItems.EARTH_BOOK.get();
        };
    }

    private int tileX(int i) {
        return this.leftPos + 6 + (i % 2) * (TILE_W + 4);
    }

    private int tileY(int i) {
        return this.topPos + 20 + (i / 2) * (TILE_H + 4);
    }

    @Override
    protected void init() {
        super.init();
        this.buttons.clear();
        for (Element e : Element.values()) {
            int i = e.ordinal();
            Button b = this.addRenderableWidget(Button.builder(e.displayName(),
                            btn -> this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, i))
                    .bounds(tileX(i) + 22, tileY(i) + 2, TILE_W - 24, TILE_H - 4).build());
            this.buttons.add(b);
        }
        refresh();
    }

    private boolean learned(Element e) {
        return ElementalBookItem.hasLearned(this.minecraft.player, e);
    }

    private void refresh() {
        Element selected = WizardStaffItem.selected(this.menu.staff());
        for (Element e : Element.values()) {
            Button b = this.buttons.get(e.ordinal());
            boolean ok = learned(e);
            b.active = ok && e != selected;
            b.setTooltip(Tooltip.create(!ok ? Component.translatable("gui.crazyalloy_revival.wizard_staff.locked", e.displayName())
                    : e == selected ? Component.translatable("gui.crazyalloy_revival.wizard_staff.current", e.displayName())
                    : Component.translatable("gui.crazyalloy_revival.wizard_staff.choose", e.displayName())));
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        refresh();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        int x = this.leftPos, y = this.topPos;
        g.fill(x, y, x + W, y + H, 0xFF2A2338);
        g.outline(x, y, W, H, 0xFF6A58A0);
        Element selected = WizardStaffItem.selected(this.menu.staff());
        for (Element e : Element.values()) {
            int i = e.ordinal();
            int tx = tileX(i), ty = tileY(i);
            boolean ok = learned(e);
            g.fill(tx, ty, tx + TILE_W, ty + TILE_H, ok ? 0xFF3C3352 : 0xFF241E30);
            if (e == selected) {
                g.outline(tx - 1, ty - 1, TILE_W + 2, TILE_H + 2, e.color);
                g.outline(tx, ty, TILE_W, TILE_H, e.color);
            }
            g.item(new ItemStack(book(e)), tx + 3, ty + 4);
            if (!ok) {
                g.fill(tx + 3, ty + 4, tx + 19, ty + 20, 0xB0181420);
                padlock(g, tx + 6, ty + 6);
            }
        }
    }

    /** A small padlock drawn from rectangles (10 x 12). */
    private static void padlock(GuiGraphicsExtractor g, int x, int y) {
        int metal = 0xFFC8C0A0, dark = 0xFF6A6450;
        g.fill(x + 2, y, x + 8, y + 1, metal);
        g.fill(x + 1, y + 1, x + 2, y + 5, metal);
        g.fill(x + 8, y + 1, x + 9, y + 5, metal);
        g.fill(x, y + 5, x + 10, y + 12, metal);
        g.fill(x + 4, y + 7, x + 6, y + 10, dark);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        g.text(this.font, this.title, 8, 7, 0xFFE8DDF8, false);
        g.text(this.font, Component.translatable("gui.crazyalloy_revival.wizard_staff.hint"), 8, H - 13, 0xFF9A90B0, false);
    }
}
