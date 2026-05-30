package thaumcraft.common.items.tools;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ScanningManager;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.client.lib.events.RenderEventHandler;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketAuraToClient;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;


public class ItemThaumometer extends ItemTCBase
{
    public ItemThaumometer() {
        super("thaumometer");
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.UNCOMMON;
    }
    
    public InteractionResult use(Level world, Player p, InteractionHand hand) {
        if (world.isClientSide()) {
            drawFX(world, p);
            p.level().playSound(null, p.getX(), p.getY(), p.getZ(), SoundsTC.scan, SoundSource.PLAYERS, 0.5f, 1.0f);
        }
        else {
            doScan(world, p);
        }
        return InteractionResult.SUCCESS;
    }
    
    public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @javax.annotation.Nullable net.minecraft.world.entity.EquipmentSlot itemSlot) {
        boolean held = isSelected || itemSlot == 0;
        if (held && !world.isClientSide() && entity.tickCount % 20 == 0 && entity instanceof net.minecraft.server.level.ServerPlayer) {
            updateAura(stack, world, (net.minecraft.server.level.ServerPlayer)entity);
        }
        if (held && world.isClientSide() && entity.tickCount % 5 == 0 && entity instanceof Player) {
            Entity target = EntityUtils.getPointedEntity(world, entity, 1.0, 16.0, 5.0f, true);
            if (target != null && ScanningManager.isThingStillScannable((Player)entity, target)) {
                FXDispatcher.INSTANCE.scanHighlight(target);
            }
            RenderEventHandler.thaumTarget = target;
            HitResult mop = getHitResultFromPlayerWild(world, (Player)entity, true);
            if (mop != null && ((net.minecraft.world.phys.BlockHitResult)mop).getBlockPos() != null && ScanningManager.isThingStillScannable((Player)entity, ((net.minecraft.world.phys.BlockHitResult)mop).getBlockPos())) {
                FXDispatcher.INSTANCE.scanHighlight(((net.minecraft.world.phys.BlockHitResult)mop).getBlockPos());
            }
        }
    }
    
    protected HitResult getHitResultFromPlayerWild(Level worldIn, Player playerIn, boolean useLiquids) {
        float f = playerIn.xRotO + (playerIn.getXRot() - playerIn.xRotO) + worldIn.getRandom().nextInt(25) - worldIn.getRandom().nextInt(25);
        float f2 = playerIn.yRotO + (playerIn.getYRot() - playerIn.yRotO) + worldIn.getRandom().nextInt(25) - worldIn.getRandom().nextInt(25);
        double d0 = playerIn.xOld + (playerIn.getX() - playerIn.xOld);
        double d2 = playerIn.yOld + (playerIn.getY() - playerIn.yOld) + playerIn.getEyeHeight();
        double d3 = playerIn.zOld + (playerIn.getZ() - playerIn.zOld);
        Vec3 vec3 = new Vec3(d0, d2, d3);
        float f3 = Mth.cos(-f2 * 0.017453292f - 3.1415927f);
        float f4 = Mth.sin(-f2 * 0.017453292f - 3.1415927f);
        float f5 = -Mth.cos(-f * 0.017453292f);
        float f6 = Mth.sin(-f * 0.017453292f);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d4 = 16.0;
        Vec3 vec4 = vec3.add(f7 * d4, f6 * d4, f8 * d4);
        return worldIn.clip(new net.minecraft.world.level.ClipContext(vec3, vec4, useLiquids, !useLiquids, false, net.minecraft.world.level.ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.NONE, null));
    }
    
    private void updateAura(ItemStack stack, Level world, net.minecraft.server.level.ServerPlayer player) {
        AuraChunk ac = AuraHandler.getAuraChunk((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), player.blockPosition().getX() >> 4, player.blockPosition().getZ() >> 4);
        if (ac != null) {
            if ((ac.getFlux() > ac.getVis() || ac.getFlux() > ac.getBase() / 3) && !ThaumcraftCapabilities.knowsResearch(player, "FLUX")) {
                ResearchManager.startResearchWithPopup(player, "FLUX");
                player.sendOverlayMessage(net.minecraft.network.chat.Component.literal(ChatFormatting.DARK_PURPLE + I18n.get("research.FLUX.warn")));
            }
            PacketHandler.sendToPlayer(new PacketAuraToClient(ac), (net.minecraft.server.level.ServerPlayer)player);
        }
    }
    
    private void drawFX(Level worldIn, Player playerIn) {
        Entity target = EntityUtils.getPointedEntity(worldIn, playerIn, 1.0, 9.0, 0.0f, true);
        if (target != null) {
            for (int a = 0; a < 10; ++a) {
                FXDispatcher.INSTANCE.blockRunes(target.getX() - 0.5, target.getY() + target.getEyeHeight() / 2.0f, target.getZ() - 0.5, 0.3f + worldIn.getRandom().nextFloat() * 0.7f, 0.0f, 0.3f + worldIn.getRandom().nextFloat() * 0.7f, (int)(target.getBbHeight() * 15.0f), 0.03f);
            }
        }
        else {
            HitResult mop = rayTrace(worldIn, playerIn, true);
            if (mop != null && ((net.minecraft.world.phys.BlockHitResult)mop).getBlockPos() != null) {
                for (int a2 = 0; a2 < 10; ++a2) {
                    FXDispatcher.INSTANCE.blockRunes(((net.minecraft.world.phys.BlockHitResult)mop).getBlockPos().getX(), ((net.minecraft.world.phys.BlockHitResult)mop).getBlockPos().getY() + 0.25, ((net.minecraft.world.phys.BlockHitResult)mop).getBlockPos().getZ(), 0.3f + worldIn.getRandom().nextFloat() * 0.7f, 0.0f, 0.3f + worldIn.getRandom().nextFloat() * 0.7f, 15, 0.03f);
                }
            }
        }
    }
    
    public void doScan(Level worldIn, Player playerIn) {
        if (!worldIn.isClientSide()) {
            Entity target = EntityUtils.getPointedEntity(worldIn, playerIn, 1.0, 9.0, 0.0f, true);
            if (target != null) {
                ScanningManager.scanTheThing(playerIn, target);
            }
            else {
                HitResult mop = rayTrace(worldIn, playerIn, true);
                if (mop != null && ((net.minecraft.world.phys.BlockHitResult)mop).getBlockPos() != null) {
                    ScanningManager.scanTheThing(playerIn, ((net.minecraft.world.phys.BlockHitResult)mop).getBlockPos());
                }
                else {
                    ScanningManager.scanTheThing(playerIn, null);
                }
            }
        }
    }
}
