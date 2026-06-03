package thaumcraft.common.golems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import thaumcraft.Thaumcraft;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.golems.ISealDisplayer;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.common.container.ContainerLogistics;
import thaumcraft.common.container.TCMenuTypes;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;


public class ItemGolemBell extends ItemTCBase implements ISealDisplayer
{
    public ItemGolemBell() {
        super("golem_bell");
    }
    
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand hand) {
        playerIn.swing(hand);
        if (!worldIn.isClientSide()) {
            HitResult mop = RayTracer.retrace(playerIn);
            if (mop != null && (mop.getType() == HitResult.Type.BLOCK || mop.getType() == HitResult.Type.ENTITY)) {
                ISealEntity se = getSeal(playerIn);
                if (se != null) {
                    if (playerIn.isCrouching()) {
                        SealHandler.removeSealEntity(playerIn.level(), se.getSealPos(), false);
                        worldIn.playSound(null, se.getSealPos().pos, SoundsTC.zap, SoundSource.BLOCKS, 0.5f, 1.0f);
                    }
                    else {
                        if (playerIn instanceof net.minecraft.server.level.ServerPlayer sp) {
                            final ISealEntity finalSe = se;
                            sp.openMenu(new net.minecraft.world.MenuProvider() {
                                @Override
                                public net.minecraft.network.chat.Component getDisplayName() { return net.minecraft.network.chat.Component.empty(); }
                                @Override
                                public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inv, net.minecraft.world.entity.player.Player p) {
                                    return new thaumcraft.common.golems.client.gui.SealBaseContainer(id, inv, finalSe);
                                }
                            }, buf -> {
                                buf.writeBlockPos(finalSe.getSealPos().pos);
                                buf.writeByte(finalSe.getSealPos().face.ordinal());
                            });
                        }
                    }
                }
                return InteractionResult.SUCCESS;
            }
            if (playerIn.isCrouching() && ThaumcraftCapabilities.knowsResearch(playerIn, "GOLEMLOGISTICS")) {
                if (playerIn instanceof net.minecraft.server.level.ServerPlayer sp) {
                    sp.openMenu(new net.minecraft.world.MenuProvider() {
                        @Override
                        public net.minecraft.network.chat.Component getDisplayName() { return net.minecraft.network.chat.Component.empty(); }
                        @Override
                        public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inv, net.minecraft.world.entity.player.Player p) {
                            return new ContainerLogistics(id, inv, p.level());
                        }
                    });
                }
                return InteractionResult.SUCCESS;
            }
        }
        else {
            playerIn.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.6f, 1.0f + worldIn.getRandom().nextFloat() * 0.1f);
        }
        return super.use(worldIn, playerIn, hand);
    }
    
    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, net.minecraft.world.item.context.UseOnContext context) {
        Player player = context.getPlayer();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction side = context.getClickedFace();
        if (player == null) return InteractionResult.PASS;
        player.swing(context.getHand());
        if (!world.isClientSide()) {
            int dimHash = (world instanceof net.minecraft.server.level.ServerLevel sl) ? sl.dimension().identifier().hashCode() : 0;
            ISealEntity se = SealHandler.getSealEntity(dimHash, new SealPos(pos, side));
            if (se != null) {
                if (player.isCrouching()) {
                    SealHandler.removeSealEntity(world, se.getSealPos(), false);
                    world.playSound(null, pos, SoundsTC.zap, SoundSource.BLOCKS, 0.5f, 1.0f);
                } else if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
                    final ISealEntity finalSe = se;
                    sp.openMenu(new net.minecraft.world.MenuProvider() {
                        @Override
                        public net.minecraft.network.chat.Component getDisplayName() { return net.minecraft.network.chat.Component.empty(); }
                        @Override
                        public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inv, net.minecraft.world.entity.player.Player p) {
                            return new thaumcraft.common.golems.client.gui.SealBaseContainer(id, inv, finalSe);
                        }
                    }, buf -> {
                        buf.writeBlockPos(finalSe.getSealPos().pos);
                        buf.writeByte(finalSe.getSealPos().face.ordinal());
                    });
                }
                return InteractionResult.SUCCESS;
            }
            if (player.isCrouching() && ThaumcraftCapabilities.knowsResearch(player, "GOLEMLOGISTICS")) {
                if (player instanceof net.minecraft.server.level.ServerPlayer sp) {
                    sp.openMenu(new net.minecraft.world.MenuProvider() {
                        @Override
                        public net.minecraft.network.chat.Component getDisplayName() { return net.minecraft.network.chat.Component.empty(); }
                        @Override
                        public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inv, net.minecraft.world.entity.player.Player p) {
                            return new ContainerLogistics(id, inv, p.level());
                        }
                    });
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
    
    public static ISealEntity getSeal(Player playerIn) {
        float f = playerIn.getXRot();
        float f2 = playerIn.getYRot();
        double d0 = playerIn.getX();
        double d2 = playerIn.getY() + playerIn.getEyeHeight();
        double d3 = playerIn.getZ();
        Vec3 vec0 = new Vec3(d0, d2, d3);
        float f3 = Mth.cos(-f2 * 0.017453292f - 3.1415927f);
        float f4 = Mth.sin(-f2 * 0.017453292f - 3.1415927f);
        float f5 = -Mth.cos(-f * 0.017453292f);
        float f6 = Mth.sin(-f * 0.017453292f);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d4 = 5.0;
        Vec3 vec2 = vec0.add(f7 * d4, f6 * d4, f8 * d4);
        Vec3 vec3 = new Vec3(f7 * d4, f6 * d4, f8 * d4);
        Vec3 vec4 = vec0.add(vec3.x / 10.0, vec3.y / 10.0, vec3.z / 10.0);
        for (int a = 0; a < vec3.length() * 10.0; ++a) {
            BlockPos pos = BlockPos.containing(vec4);
            HitResult mop = collisionRayTrace(playerIn.level(), pos, vec0, vec2);
            if (mop != null) {
                ISealEntity se = SealHandler.getSealEntity((playerIn.level() instanceof net.minecraft.server.level.ServerLevel ? ((playerIn.level() instanceof net.minecraft.server.level.ServerLevel) ? ((net.minecraft.server.level.ServerLevel)playerIn.level()).dimension().identifier().hashCode() : 0) : 0), new SealPos(pos, ((net.minecraft.world.phys.BlockHitResult)mop).getDirection()));
                if (se != null) {
                    return se;
                }
            }
            vec4 = vec4.add(vec3.x / 10.0, vec3.y / 10.0, vec3.z / 10.0);
        }
        return null;
    }
    
    private static boolean isVecInsideYZBounds(Vec3 point, BlockPos pos) {
        return point != null && (point.y >= pos.getY() && point.y <= pos.getY() + 1 && point.z >= pos.getZ() && point.z <= pos.getZ() + 1);
    }
    
    private static boolean isVecInsideXZBounds(Vec3 point, BlockPos pos) {
        return point != null && (point.x >= pos.getX() && point.x <= pos.getX() + 1 && point.z >= pos.getZ() && point.z <= pos.getZ() + 1);
    }
    
    private static boolean isVecInsideXYBounds(Vec3 point, BlockPos pos) {
        return point != null && (point.x >= pos.getX() && point.x <= pos.getX() + 1 && point.y >= pos.getY() && point.y <= pos.getY() + 1);
    }
    
    private static Vec3 intermediateWithX(Vec3 s, Vec3 e, double x) {
        double t = (x - s.x) / (e.x - s.x);
        return (t < 0.0 || t > 1.0) ? null : new Vec3(x, s.y + (e.y - s.y) * t, s.z + (e.z - s.z) * t);
    }
    private static Vec3 intermediateWithY(Vec3 s, Vec3 e, double y) {
        double t = (y - s.y) / (e.y - s.y);
        return (t < 0.0 || t > 1.0) ? null : new Vec3(s.x + (e.x - s.x) * t, y, s.z + (e.z - s.z) * t);
    }
    private static Vec3 intermediateWithZ(Vec3 s, Vec3 e, double z) {
        double t = (z - s.z) / (e.z - s.z);
        return (t < 0.0 || t > 1.0) ? null : new Vec3(s.x + (e.x - s.x) * t, s.y + (e.y - s.y) * t, z);
    }

    private static HitResult collisionRayTrace(Level worldIn, BlockPos pos, Vec3 start, Vec3 end) {
        return worldIn.getBlockState(pos).getShape(worldIn, pos).clip(start, end, pos);
    }
}
