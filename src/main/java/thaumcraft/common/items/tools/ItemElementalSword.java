package thaumcraft.common.items.tools;
import net.minecraft.world.item.ToolMaterial;
import java.util.List;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
// import net.minecraft.world.item.Item /* SwordItem removed */; // removed
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;
import thaumcraft.common.lib.utils.EntityUtils;


public class ItemElementalSword extends Item /* ItemSword removed */ implements IThaumcraftItems
{
    public ItemElementalSword(ToolMaterial enumtoolmaterial) {
        super(new net.minecraft.world.item.Item.Properties());
        // ItemTCBase constructor
        ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
    }
    
    public Item getItem() {
        return this;
    }
    
    public String[] getVariantNames() {
        return new String[] { "normal" };
    }
    
    public int[] getVariantMeta() {
        return new int[] { 0 };
    }
    
    public Object /* ItemMeshDefinition removed */ getCustomMesh() {
        return null;
    }
    
    public Object /* ModelResourceLocation removed */ getCustomModelResourceLocation(String variant) {
        return null /* removed */;
    }
    
    public void getSubItems(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == null /* SEARCH tab removed */) {
            ItemStack w1 = new ItemStack(this);
            EnumInfusionEnchantment.addInfusionEnchantment(w1, EnumInfusionEnchantment.ARCING, 2);
            items.add(w1);
        }
        else {
            super.getSubItems(tab, items);
        }
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.RARE;
    }
    
    public boolean isValidRepairItem(ItemStack repairItem) {
        return ItemStack.isSameItem(repairItem, new ItemStack(ItemsTC.ingots, 1)) || false;
    }
    
    public ItemUseAnimation getItemUseAction(ItemStack stack) {
        return ItemUseAnimation.NONE;
    }
    
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }
    
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand hand) {
        playerIn.startUsingItem(hand);
        return InteractionResult.SUCCESS;
    }
    
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        super.onUsingTick(stack, player, count);
        int ticks = getMaxItemUseDuration(stack) - count;
        if (player.getDeltaMovement().y < 0.0) {
            player.setDeltaMovement(player.getDeltaMovement().multiply(1));  // FIXME: /= not supported on Vec3
            player.fallDistance /= 1.2f;
        }
        player.setDeltaMovement(player.getDeltaMovement().x, player.getDeltaMovement().y + 0.07999999821186066, player.getDeltaMovement().z);
        if (player.getDeltaMovement().y > 0.5) {
            player.setDeltaMovement(player.getDeltaMovement().x, 0.20000000298023224, player.getDeltaMovement().z);
        }
        if (player instanceof net.minecraft.server.level.ServerPlayer) {
            EntityUtils.resetFloatCounter((net.minecraft.server.level.ServerPlayer)player);
        }
        List<Entity> targets = player.level().getEntitiesWithinAABBExcludingEntity(player, player.getBoundingBox().inflate(2.5, 2.5, 2.5));
        if (targets.size() > 0) {
            for (int var9 = 0; var9 < targets.size(); ++var9) {
                Entity entity = targets.get(var9);
                if (!(entity instanceof Player)) {
                    if (entity instanceof LivingEntity) {
                        if (((LivingEntity)entity).isAlive()) {
                            if (player.getRidingEntity() == null || player.getRidingEntity() != entity) {
                                Vec3 p = new Vec3(player.getX(), player.getY(), player.getZ());
                                Vec3 t = new Vec3(entity.getX(), entity.getY(), entity.getZ());
                                double distance = p.distanceTo(t) + 0.1;
                                Vec3 r = new Vec3(t.x - p.x, t.y - p.y, t.z - p.z);
                                Entity entity2 = entity;
                                entity2.setDeltaMovement(entity2.getDeltaMovement().x + r.x / 2.5 / distance, entity2.getDeltaMovement().y, entity2.getDeltaMovement().z);
                                Entity entity3 = entity;
                                entity3.setDeltaMovement(entity3.getDeltaMovement().x, entity3.getDeltaMovement().y + r.y / 2.5 / distance, entity3.getDeltaMovement().z);
                                Entity entity4 = entity;
                                entity4.setDeltaMovement(entity4.getDeltaMovement().x, entity4.getDeltaMovement().y, entity4.getDeltaMovement().z + r.z / 2.5 / distance);
                            }
                        }
                    }
                }
            }
        }
        if (player.level().isClientSide()) {
            int miny = (int)(player.getBoundingBox().minY - 2.0);
            if (player.onGround()) {
                miny = Mth.floor(player.getBoundingBox().minY);
            }
            for (int a = 0; a < 5; ++a) {
                FXDispatcher.INSTANCE.smokeSpiral(player.getX(), player.getBoundingBox().minY + player.getBbHeight() / 2.0f, player.getZ(), 1.5f, player.level().getRandom().nextInt(360), miny, 14540253);
            }
            if (player.onGround()) {
                float r2 = player.level().getRandom().nextFloat() * 360.0f;
                float mx = -Mth.sin(r2 / 180.0f * 3.1415927f) / 5.0f;
                float mz = Mth.cos(r2 / 180.0f * 3.1415927f) / 5.0f;
                player.level().spawnParticle(null /* nested removed */, player.getX(), player.getBoundingBox().minY + 0.10000000149011612, player.getZ(), mx, 0.0, mz);
            }
        }
        else if (ticks == 0 || ticks % 20 == 0) {
            player.playSound(SoundsTC.wind, 0.5f, 0.9f + player.level().getRandom().nextFloat() * 0.2f);
        }
        if (ticks % 20 == 0) {
            stack.hurtAndBreak(1, player, null, (i) -> {});
        }
    }
}
