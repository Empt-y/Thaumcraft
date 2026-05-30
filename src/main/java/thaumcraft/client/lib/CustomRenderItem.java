package thaumcraft.client.lib;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

// TODO: RenderItem was removed in 1.9+; this is a stub that no-ops all rendering calls.
public class CustomRenderItem
{
    public CustomRenderItem() {
    }

    public void registerItems() {
    }

    public void renderItemOverlayIntoGUI(Font fr, ItemStack stack, int xPosition, int yPosition, String text) {
    }

    protected void registerItem(Item itm, int subType, String identifier) {
    }

    protected void registerBlock(Block blk, int subType, String identifier) {
    }

    public Object getItemModelMesher() {
        return null;
    }

    public void renderItem(ItemStack stack, Object model) {
    }

    public boolean shouldRenderItemIn3D(ItemStack stack) {
        return false;
    }

    // renderItem(ItemStack, TransformType) stub removed (duplicate)

    public Object getItemModelWithOverrides(ItemStack stack, Level level, LivingEntity entity) {
        return null;
    }

    public void renderItem(ItemStack stack, LivingEntity entity, Object transformType, boolean b) {
    }

    public void renderItemAndEffectIntoGUI(LivingEntity entity, ItemStack stack, int x, int y) {
    }

    public void renderItemIntoGUI(ItemStack stack, int x, int y) {
    }

    public void renderItemAndEffectIntoGUI(ItemStack stack, int x, int y) {
    }

    public void renderItemOverlays(Font fr, ItemStack stack, int x, int y) {
    }

    public void onResourceManagerReload(Object resourceManager) {
    }
}
