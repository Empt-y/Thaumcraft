package thaumcraft.common.items.baubles;
// baubles import removed
// baubles import removed
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
// removed: 
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketPlayerFlagToServer;


public class ItemCloudRing extends ItemTCBase 
{
    public static HashMap<String, Boolean> jumpList;
    
    public ItemCloudRing() {
        super("cloud_ring");
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
    
    public void onWornTick(ItemStack itemstack, LivingEntity player) {
        if (player.level().isClientSide()) {
            boolean spacePressed = Minecraft.getInstance().gameSettings.keyBindJump.isPressed();
            if (spacePressed && !ItemCloudRing.jumpList.containsKey(player.getName())) {
                ItemCloudRing.jumpList.put(player.getName(), true);
            }
            if (spacePressed && !player.onGround() && !player.isInWater() && player.jumpTicks == 0 && ItemCloudRing.jumpList.containsKey(player.getName()) && ItemCloudRing.jumpList.get(player.getName())) {
                FXDispatcher.INSTANCE.drawBamf(player.getX(), player.getY() + 0.5, player.getZ(), 1.0f, 1.0f, 1.0f, false, false, Direction.UP);
                player.level().playSound(player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.PLAYERS, 0.1f, 1.0f + (float)player.level().getRandom().nextGaussian() * 0.05f, false);
                ItemCloudRing.jumpList.put(player.getName());
                player.setDeltaMovement(player.getDeltaMovement().x, 0.75, player.getDeltaMovement().z);
                if (player.hasEffect(MobEffects.JUMP_BOOST)) {
                    player.setDeltaMovement(player.getDeltaMovement().x, player.getDeltaMovement().y + (player.getEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1f, player.getDeltaMovement().z);
                }
                if (player.isSprinting()) {
                    float f = player.getYRot() * 0.017453292f;
                    player.setDeltaMovement(player.getDeltaMovement().x - (Mth.sin(f) * 0.2f), player.getDeltaMovement().y, player.getDeltaMovement().z);
                    player.setDeltaMovement(player.getDeltaMovement().x, player.getDeltaMovement().y, player.getDeltaMovement().z + Mth.cos(f) * 0.2f);
                }
                player.fallDistance = 0.0f;
                PacketHandler.sendToServer(new PacketPlayerFlagToServer(player, 1));
                ForgeHooks.onLivingJump(player);
            }
            if (player.onGround() && player.jumpTicks == 0) {
                ItemCloudRing.jumpList.remove(player.getName());
            }
        }
    }
    
    static {
        ItemCloudRing.jumpList = new HashMap<String, Boolean>();
    }
}
