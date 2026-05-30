package thaumcraft.common.items.baubles;
import net.minecraft.world.entity.EquipmentSlot;
// baubles import removed
// baubles import removed
// baubles import removed
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.items.ItemTCBase;
import net.minecraft.client.renderer.rendertype.RenderType;


public class ItemCuriosityBand extends ItemTCBase 
{
    Identifier tex;
    
    public ItemCuriosityBand() {
        super("curiosity_band");
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/items/curiosity_band_worn.png");
        // maxStackSize removed - set in Item.Properties
        // canRepair field removed
        /* setMaxDamage removed - use Item.Properties */;
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.RARE;
    }
    
    public Object /* BaubleType removed */ getBaubleType(ItemStack itemstack) {
        return null /* nested removed */;
    }
    
    @OnlyIn(Dist.CLIENT)
    public void onPlayerBaubleRender(ItemStack stack, Player player, float ticks) {
        if (type == null) {
            boolean helmetEmpty = !player.getItemBySlot(EquipmentSlot.HEAD).isEmpty();
            Minecraft.getInstance().renderEngine.bindTexture(tex);
            /* null call removed */;
            /* null call removed */;
            /* null call removed */;
            /* TODO: use PoseStack */ // RenderSystem.rotate(180.0f, 0.0f, 1.0f, 0.0f);
            /* TODO: use PoseStack */ // RenderSystem.translate(-0.5, -0.5, armor ? 0.11999999731779099 : 0.0);
            UtilsFX.renderTextureIn3D(0.0f, 0.0f, 1.0f, 1.0f, 16, 26, 0.1f);
        }
    }
}
