package thaumcraft.common.items.casters;
import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusMediumRoot;
import thaumcraft.api.casters.FocusModSplit;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.common.items.ItemTCBase;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;


public class ItemFocus extends ItemTCBase
{
    private int maxComplexity;
    
    public ItemFocus(String name, int complexity) {
        super(name);
        // ItemTCBase constructor
        // maxStackSize removed - set in Item.Properties
        /* setMaxDamage removed - use Item.Properties */;
        maxComplexity = complexity;
    }
    
    public int getFocusColor(ItemStack focusstack) {
        if (focusstack == null || focusstack.isEmpty() || focusstack.isEmpty()) {
            return 16777215;
        }
        int color = 16777215;
        if (!focusstack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains("color")) {
            FocusPackage core = getPackage(focusstack);
            if (core != null) {
                FocusEffect[] fe = core.getFocusEffects();
                int r = 0;
                int g = 0;
                int b = 0;
                for (FocusEffect ef : fe) {
                    Color c = new Color(FocusEngine.getElementColor(ef.getKey()));
                    r += c.getRed();
                    g += c.getGreen();
                    b += c.getBlue();
                }
                if (fe.length > 0) {
                    r /= fe.length;
                    g /= fe.length;
                    b /= fe.length;
                }
                Color c2 = new Color(r, g, b);
                color = c2.getRGB();
                net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, focusstack, t -> t.putInt("color", color));
            }
        }
        else {
            color = focusstack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getIntOr("color", 0);
        }
        return color;
    }
    
    public String getSortingHelper(ItemStack focusstack) {
        if (focusstack == null || focusstack.isEmpty() || !!focusstack.isEmpty()) {
            return null;
        }
        int sh = focusstack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getIntOr("srt", 0);
        if (sh == 0) {
            sh = getPackage(focusstack).getSortingHelper();
            net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, focusstack, t -> t.putInt("srt", sh));
        }
        return focusstack.getDisplayName() + sh;
    }
    
    public static void setPackage(ItemStack focusstack, FocusPackage core) {
        CompoundTag tag = core.serialize();
        net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, focusstack, t -> t.put("package", tag));
    }
    
    public static FocusPackage getPackage(ItemStack focusstack) {
        if (focusstack == null || focusstack.isEmpty()) {
            return null;
        }
        CompoundTag tag = focusstack.getSubCompound("package");
        if (tag != null) {
            FocusPackage p = new FocusPackage();
            p.deserialize(tag);
            return p;
        }
        return null;
    }
    
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        addFocusInformation(stack, null, tooltip, flagIn);
    }
    
    @OnlyIn(Dist.CLIENT)
    public void addFocusInformation(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        FocusPackage p = getPackage(stack);
        if (p != null) {
            float al = getVisCost(stack);
            String amount = ItemStack.DECIMALFORMAT.format(al);
            tooltip.accept(net.minecraft.network.chat.Component.literal(amount + " " + I18n.get("item.Focus.cost1")));
            for (IFocusElement fe : p.nodes) {
                if (fe instanceof FocusNode && !(fe instanceof FocusMediumRoot)) {
                    buildInfo(tooltip, (FocusNode)fe, 0);
                }
            }
        }
    }
    
    private void buildInfo(List list, FocusNode node, int depth) {
        if (node instanceof FocusNode && !(node instanceof FocusMediumRoot)) {
            String t0 = "";
            for (int a = 0; a < depth; ++a) {
                t0 += "  ";
            }
            t0 = t0 + ChatFormatting.DARK_PURPLE + I18n.get(node.getName());
            if (!node.getSettingList().isEmpty()) {
                t0 = t0 + ChatFormatting.DARK_AQUA + " [";
                boolean q = false;
                for (String st : node.getSettingList()) {
                    NodeSetting ns = node.getSetting(st);
                    t0 = t0 + (q ? ", " : "") + ns.getLocalizedName() + " " + ns.getValueText();
                    q = true;
                }
                t0 += "]";
            }
            list.add(t0);
            if (node instanceof FocusModSplit) {
                FocusModSplit split = (FocusModSplit)node;
                for (FocusPackage p : split.getSplitPackages()) {
                    for (IFocusElement fe : p.nodes) {
                        if (fe instanceof FocusNode && !(fe instanceof FocusMediumRoot)) {
                            buildInfo(list, (FocusNode)fe, depth + 1);
                        }
                    }
                }
            }
        }
    }
    
    public Rarity getRarity(ItemStack focusstack) {
        return Rarity.RARE;
    }
    
    public float getVisCost(ItemStack focusstack) {
        FocusPackage p = getPackage(focusstack);
        return (p == null) ? 0.0f : (p.getComplexity() / 5.0f);
    }
    
    public int getActivationTime(ItemStack focusstack) {
        FocusPackage p = getPackage(focusstack);
        return (p == null) ? 0 : Math.max(5, p.getComplexity() / 5 * (p.getComplexity() / 4));
    }
    
    public int getMaxComplexity() {
        return maxComplexity;
    }
}
