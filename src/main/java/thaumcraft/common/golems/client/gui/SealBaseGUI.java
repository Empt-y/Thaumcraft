package thaumcraft.common.golems.client.gui;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.seals.ISealConfigFilter;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.client.gui.plugins.GuiHoverButton;
import thaumcraft.client.gui.plugins.GuiPlusMinusButton;


public class SealBaseGUI extends AbstractContainerScreen<SealBaseContainer> {

    ISealEntity seal;
    int middleX;
    int middleY;
    int category;
    int[] categories;
    Identifier tex;

    public SealBaseGUI(SealBaseContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title, 176, 232);
        this.seal = container.seal;
        this.category = -1;
        this.tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_base.png");
        if (seal != null && seal.getSeal() instanceof ISealGui) {
            categories = ((ISealGui) seal.getSeal()).getGuiCategories();
        } else {
            categories = new int[]{0, 4};
        }
        this.middleX = this.imageWidth / 2;
        this.middleY = (this.imageHeight - 72) / 2 - 8;
    }

    @Override
    protected void init() {
        super.init();
        setupCategories();
    }

    void setupCategories() {
        clearWidgets();
        int c = 0;
        float slice = 60.0f / categories.length;
        float start = -180.0f + (categories.length - 1) * slice / 2.0f;
        if (slice > 24.0f) slice = 24.0f;
        if (slice < 12.0f) slice = 12.0f;

        for (int cat : categories) {
            if (category < 0) category = cat;
            if (categories.length > 1) {
                final int finalCat = cat;
                final int finalC = c;
                int xx = (int)(Mth.cos((start - c * slice) / 180.0f * (float)Math.PI) * 86.0f);
                int yy = (int)(Mth.sin((start - c * slice) / 180.0f * (float)Math.PI) * 86.0f);
                this.addRenderableWidget(new GuiGolemCategoryButton(c,
                        this.leftPos + middleX + xx, this.topPos + middleY + yy,
                        16, 16, "button.category." + cat, cat, category == cat,
                        btn -> {
                            category = finalCat;
                            ((SealBaseContainer) menu).category = finalCat;
                            ((SealBaseContainer) menu).setupCategories();
                            Objects.requireNonNull(minecraft).gameMode
                                    .handleInventoryButtonClick(menu.containerId, finalC);
                            setupCategories();
                        }));
            }
            ++c;
        }

        // Redstone button always added
        int xxR = (int)(Mth.cos((start - c * slice) / 180.0f * (float)Math.PI) * 86.0f);
        int yyR = (int)(Mth.sin((start - c * slice) / 180.0f * (float)Math.PI) * 86.0f);
        this.addRenderableWidget(new GuiGolemRedstoneButton(27,
                this.leftPos + middleX + xxR - 8, this.topPos + middleY + yyR - 8,
                16, 16, seal,
                btn -> {
                    seal.setRedstoneSensitive(!seal.isRedstoneSensitive());
                    Objects.requireNonNull(minecraft).gameMode
                            .handleInventoryButtonClick(menu.containerId, seal.isRedstoneSensitive() ? 27 : 28);
                }));

        switch (category) {
            case 0 -> {
                this.addRenderableWidget(new GuiPlusMinusButton(80,
                        this.leftPos + middleX - 5 - 14, this.topPos + middleY - 17, 10, 10, true,
                        btn -> {
                            if (seal.getPriority() > -5) {
                                seal.setPriority((byte)(seal.getPriority() - 1));
                                Objects.requireNonNull(minecraft).gameMode
                                        .handleInventoryButtonClick(menu.containerId, 80);
                            }
                        }));
                this.addRenderableWidget(new GuiPlusMinusButton(81,
                        this.leftPos + middleX - 5 + 14, this.topPos + middleY - 17, 10, 10, false,
                        btn -> {
                            if (seal.getPriority() < 5) {
                                seal.setPriority((byte)(seal.getPriority() + 1));
                                Objects.requireNonNull(minecraft).gameMode
                                        .handleInventoryButtonClick(menu.containerId, 81);
                            }
                        }));
                this.addRenderableWidget(new GuiPlusMinusButton(82,
                        this.leftPos + middleX + 18 - 12, this.topPos + middleY + 4, 10, 10, true,
                        btn -> {
                            if (seal.getColor() > 0) {
                                seal.setColor((byte)(seal.getColor() - 1));
                                Objects.requireNonNull(minecraft).gameMode
                                        .handleInventoryButtonClick(menu.containerId, 82);
                            }
                        }));
                this.addRenderableWidget(new GuiPlusMinusButton(83,
                        this.leftPos + middleX + 18 + 11, this.topPos + middleY + 4, 10, 10, false,
                        btn -> {
                            if (seal.getColor() < 16) {
                                seal.setColor((byte)(seal.getColor() + 1));
                                Objects.requireNonNull(minecraft).gameMode
                                        .handleInventoryButtonClick(menu.containerId, 83);
                            }
                        }));
                this.addRenderableWidget(new GuiGolemLockButton(25,
                        this.leftPos + middleX - 32, this.topPos + middleY, 16, 16, seal,
                        btn -> {
                            if (minecraft != null && minecraft.player != null
                                    && seal.getOwner().equals(minecraft.player.getStringUUID())) {
                                seal.setLocked(!seal.isLocked());
                                minecraft.gameMode.handleInventoryButtonClick(menu.containerId, seal.isLocked() ? 25 : 26);
                            }
                        }));
            }
            case 1 -> {
                if (seal.getSeal() instanceof ISealConfigFilter cp) {
                    int s = cp.getFilterSize();
                    int sy = 16 + (s - 1) / 3 * 12;
                    this.addRenderableWidget(new GuiGolemBWListButton(20,
                            this.leftPos + middleX - 8, this.topPos + middleY + (s - 1) / 3 * 24 - sy + 27,
                            16, 16, cp,
                            btn -> {
                                cp.setBlacklist(!cp.isBlacklist());
                                Objects.requireNonNull(minecraft).gameMode
                                        .handleInventoryButtonClick(menu.containerId, cp.isBlacklist() ? 20 : 21);
                            }));
                }
            }
            case 2 -> {
                this.addRenderableWidget(new GuiPlusMinusButton(90,
                        this.leftPos + middleX - 5 - 14, this.topPos + middleY - 25, 10, 10, true,
                        btn -> {
                            if (seal.getArea().getY() > 1) {
                                seal.setArea(seal.getArea().offset(0, -1, 0));
                                Objects.requireNonNull(minecraft).gameMode
                                        .handleInventoryButtonClick(menu.containerId, 90);
                            }
                        }));
                this.addRenderableWidget(new GuiPlusMinusButton(91,
                        this.leftPos + middleX - 5 + 14, this.topPos + middleY - 25, 10, 10, false,
                        btn -> {
                            if (seal.getArea().getY() < 8) {
                                seal.setArea(seal.getArea().offset(0, 1, 0));
                                Objects.requireNonNull(minecraft).gameMode
                                        .handleInventoryButtonClick(menu.containerId, 91);
                            }
                        }));
                this.addRenderableWidget(new GuiPlusMinusButton(92,
                        this.leftPos + middleX - 5 - 14, this.topPos + middleY, 10, 10, true,
                        btn -> {
                            if (seal.getArea().getX() > 1) {
                                seal.setArea(seal.getArea().offset(-1, 0, 0));
                                Objects.requireNonNull(minecraft).gameMode
                                        .handleInventoryButtonClick(menu.containerId, 92);
                            }
                        }));
                this.addRenderableWidget(new GuiPlusMinusButton(93,
                        this.leftPos + middleX - 5 + 14, this.topPos + middleY, 10, 10, false,
                        btn -> {
                            if (seal.getArea().getX() < 8) {
                                seal.setArea(seal.getArea().offset(1, 0, 0));
                                Objects.requireNonNull(minecraft).gameMode
                                        .handleInventoryButtonClick(menu.containerId, 93);
                            }
                        }));
                this.addRenderableWidget(new GuiPlusMinusButton(94,
                        this.leftPos + middleX - 5 - 14, this.topPos + middleY + 25, 10, 10, true,
                        btn -> {
                            if (seal.getArea().getZ() > 1) {
                                seal.setArea(seal.getArea().offset(0, 0, -1));
                                Objects.requireNonNull(minecraft).gameMode
                                        .handleInventoryButtonClick(menu.containerId, 94);
                            }
                        }));
                this.addRenderableWidget(new GuiPlusMinusButton(95,
                        this.leftPos + middleX - 5 + 14, this.topPos + middleY + 25, 10, 10, false,
                        btn -> {
                            if (seal.getArea().getZ() < 8) {
                                seal.setArea(seal.getArea().offset(0, 0, 1));
                                Objects.requireNonNull(minecraft).gameMode
                                        .handleInventoryButtonClick(menu.containerId, 95);
                            }
                        }));
            }
            case 3 -> {
                if (seal.getSeal() instanceof ISealConfigToggles cp) {
                    ISealConfigToggles.SealToggle[] toggles = cp.getToggles();
                    int s2 = (toggles.length < 4) ? 8 : ((toggles.length < 6) ? 7 : ((toggles.length < 9) ? 6 : 5));
                    int h = (toggles.length - 1) * s2;
                    int w = 12;
                    for (ISealConfigToggles.SealToggle prop : toggles) {
                        int ww = 12 + Math.min(100, this.font.width(I18n.get(prop.getName())));
                        ww /= 2;
                        if (ww > w) w = ww;
                    }
                    for (int p = 0; p < toggles.length; p++) {
                        final int fp = p;
                        ISealConfigToggles.SealToggle prop = toggles[p];
                        this.addRenderableWidget(new GuiGolemPropButton(30 + p,
                                this.leftPos + middleX - w, this.topPos + middleY - 5 - h + p * (s2 * 2),
                                8, 8, prop.getName(), prop,
                                btn -> {
                                    cp.setToggle(fp, !cp.getToggles()[fp].getValue());
                                    int id = 30 + fp + (cp.getToggles()[fp].getValue() ? 0 : 30);
                                    Objects.requireNonNull(minecraft).gameMode
                                            .handleInventoryButtonClick(menu.containerId, id);
                                }));
                    }
                }
            }
            case 4 -> {
                EnumGolemTrait[] required = seal.getSeal().getRequiredTags();
                if (required != null) {
                    for (int p = 0; p < required.length; p++) {
                        EnumGolemTrait tag = required[p];
                        this.addRenderableWidget(new GuiHoverButton(this, 500 + p,
                                this.leftPos + middleX + p * 18 - (required.length - 1) * 9,
                                this.topPos + middleY - 8, 16, 16,
                                tag.getLocalizedName(), tag.getLocalizedDescription(), tag.icon));
                    }
                }
                EnumGolemTrait[] forbidden = seal.getSeal().getForbiddenTags();
                if (forbidden != null) {
                    for (int p = 0; p < forbidden.length; p++) {
                        EnumGolemTrait tag = forbidden[p];
                        this.addRenderableWidget(new GuiHoverButton(this, 600 + p,
                                this.leftPos + middleX + p * 18 - (forbidden.length - 1) * 9,
                                this.topPos + middleY + 24, 16, 16,
                                tag.getLocalizedName(), tag.getLocalizedDescription(), tag.icon));
                    }
                }
            }
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = this.leftPos;
        int y = this.topPos;

        // Circular background dial
        graphics.blit(RenderPipelines.GUI_TEXTURED, tex, x + middleX - 80, y + middleY - 80, 96, 0, 160, 160, 256, 256);
        // Player inventory background
        graphics.blit(RenderPipelines.GUI_TEXTURED, tex, x, y + 143, 0, 167, 176, 89, 256, 256);

        // Category label
        String catLabel = I18n.get("button.category." + category);
        graphics.text(this.font, catLabel, x + middleX - this.font.width(catLabel) / 2, y + middleY - 64, 0xFFFFFF, false);

        // Category-specific overlays
        switch (category) {
            case 0 -> drawCat0(graphics, x, y, mouseX - x, mouseY - y);
            case 1 -> drawCat1(graphics, x, y);
            case 2 -> drawCat2(graphics, x, y);
            case 4 -> drawCat4(graphics, x, y);
        }
    }

    private void drawCat0(GuiGraphicsExtractor graphics, int x, int y, int mx, int my) {
        // Color selector icon
        graphics.blit(RenderPipelines.GUI_TEXTURED, tex, x + middleX + 17, y + middleY + 3, 2, 18, 12, 12, 256, 256);
        if (seal.getColor() >= 1 && seal.getColor() <= 16) {
            DyeColor dye = DyeColor.byId(seal.getColor() - 1);
            int rgb = dye.getFireworkColor();
            graphics.fill(x + middleX + 20, y + middleY + 6, x + middleX + 26, y + middleY + 12, 0xFF000000 | rgb);
        }
        // Color tooltip
        if (mx >= middleX + 5 && mx <= middleX + 41 && my >= middleY + 3 && my <= middleY + 15) {
            String colorStr;
            if (seal.getColor() >= 1 && seal.getColor() <= 16) {
                DyeColor dye = DyeColor.byId(seal.getColor() - 1);
                String key = "color." + dye.getName();
                String template = I18n.get("golem.prop.color");
                colorStr = template.replace("%s", I18n.get(key));
            } else {
                colorStr = I18n.get("golem.prop.colorall");
            }
            graphics.text(this.font, colorStr, x + middleX + 23 - this.font.width(colorStr) / 2, y + middleY + 17, 0xFFFFFF, false);
        }
        // Priority
        String prioLabel = I18n.get("golem.prop.priority");
        graphics.text(this.font, prioLabel, x + middleX - this.font.width(prioLabel) / 2, y + middleY - 28, 0xBBFFBB, false);
        String prioVal = "" + seal.getPriority();
        graphics.text(this.font, prioVal, x + middleX - this.font.width(prioVal) / 2, y + middleY - 16, 0xFFFFFF, false);
        // Owner
        if (minecraft != null && minecraft.player != null && seal.getOwner().equals(minecraft.player.getStringUUID())) {
            String ownerLabel = I18n.get("golem.prop.owner");
            graphics.text(this.font, ownerLabel, x + middleX - this.font.width(ownerLabel) / 2, y + middleY + 32, 0xBBFFBB, false);
        }
    }

    private void drawCat1(GuiGraphicsExtractor graphics, int x, int y) {
        if (seal.getSeal() instanceof ISealConfigFilter cp) {
            int s = cp.getFilterSize();
            int sx = 16 + (s - 1) % 3 * 12;
            int sy = 16 + (s - 1) / 3 * 12;
            for (int a = 0; a < s; a++) {
                int fx = a % 3;
                int fy = a / 3;
                graphics.blit(RenderPipelines.GUI_TEXTURED, tex,
                        x + middleX + fx * 24 - sx, y + middleY + fy * 24 - sy, 0, 56, 32, 32, 256, 256);
            }
        }
    }

    private void drawCat2(GuiGraphicsExtractor graphics, int x, int y) {
        String ly = I18n.get("button.caption.y");
        String lx = I18n.get("button.caption.x");
        String lz = I18n.get("button.caption.z");
        graphics.text(this.font, ly, x + middleX - this.font.width(ly) / 2, y + middleY - 24 - 9, 0xDDDDFF, false);
        graphics.text(this.font, lx, x + middleX - this.font.width(lx) / 2, y + middleY - 9, 0xDDDDFF, false);
        graphics.text(this.font, lz, x + middleX - this.font.width(lz) / 2, y + middleY + 24 - 9, 0xDDDDFF, false);
        String vy = "" + seal.getArea().getY();
        String vx = "" + seal.getArea().getX();
        String vz = "" + seal.getArea().getZ();
        graphics.text(this.font, vy, x + middleX - this.font.width(vy) / 2, y + middleY - 24, 0xFFFFFF, false);
        graphics.text(this.font, vx, x + middleX - this.font.width(vx) / 2, y + middleY, 0xFFFFFF, false);
        graphics.text(this.font, vz, x + middleX - this.font.width(vz) / 2, y + middleY + 24, 0xFFFFFF, false);
    }

    private void drawCat4(GuiGraphicsExtractor graphics, int x, int y) {
        String req = I18n.get("button.caption.required");
        String forb = I18n.get("button.caption.forbidden");
        graphics.text(this.font, req, x + middleX - this.font.width(req) / 2, y + middleY - 26, 0xDDDDFF, false);
        graphics.text(this.font, forb, x + middleX - this.font.width(forb) / 2, y + middleY + 6, 0xDDDDFF, false);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (category == 1 && seal.getSeal() instanceof ISealConfigFilter cp && !cp.isBlacklist()) {
            for (int i = 0; i < cp.getFilterSize(); i++) {
                Slot slot = menu.slots.get(i);
                if (slot.isActive() && !slot.getItem().isEmpty()) {
                    int sz = cp.getFilterSlotSize(i);
                    String s = sz == 0 ? "§e*" : String.valueOf(sz);
                    int tx = slot.x + 19 - 2 - this.font.width(s);
                    int ty = slot.y + 6 + 3;
                    graphics.text(this.font, s, tx, ty, 0xFFFFFF, true);
                }
            }
        }
    }
}
