package thaumcraft.common.lib.utils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.EncoderException;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.tags.BlockTags;
import thaumcraft.api.internal.WeightedRandomLoot;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.vec.Rotation;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketBiomeChange;


public class Utils
{
    public static HashMap<List<Object>, ItemStack> specialMiningResult;
    public static HashMap<List<Object>, Float> specialMiningChance;
    public static String[] colorNames;
    public static int[] colors;
    public static ArrayList<List> oreDictLogs;

    public static boolean isChunkLoaded(Level world, int x, int z) {
        LevelChunk chunk = world.getChunkSource().getChunkNow(x >> 4, z >> 4);
        return chunk != null && !chunk.isEmpty();
    }

    public static boolean useBonemealAtLoc(Level world, Player player, BlockPos pos) {
        ItemStack is = new ItemStack(Items.BONE_MEAL, 1);
        return net.minecraft.world.item.BoneMealItem.applyBonemeal(is, world, pos, player);
    }

    public static boolean hasColor(byte[] colors) {
        for (byte col : colors) {
            if (col >= 0) {
                return true;
            }
        }
        return false;
    }

    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            destFile.createNewFile();
        }
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0L, source.size());
        }
        finally {
            if (source != null) {
                source.close();
            }
            if (destination != null) {
                destination.close();
            }
        }
    }

    public static void addSpecialMiningResult(ItemStack in, ItemStack out, float chance) {
        Utils.specialMiningResult.put(Arrays.asList(in.getItem(), in.getDamageValue()), out);
        Utils.specialMiningChance.put(Arrays.asList(in.getItem(), in.getDamageValue()), chance);
    }

    public static ItemStack findSpecialMiningResult(ItemStack is, float chance, net.minecraft.util.RandomSource rand) {
        ItemStack dropped = is.copy();
        float r = rand.nextFloat();
        List ik = Arrays.asList(is.getItem(), is.getDamageValue());
        if (Utils.specialMiningResult.containsKey(ik) && r <= chance * Utils.specialMiningChance.get(ik)) {
            dropped = Utils.specialMiningResult.get(ik);
            dropped.setCount(dropped.getCount() * is.getCount());
        }
        return dropped;
    }

    public static float clamp_float(float par0, float par1, float par2) {
        return (par0 < par1) ? par1 : ((par0 > par2) ? par2 : par0);
    }

    public static void setBiomeAt(Level world, BlockPos pos, Biome biome) {
        setBiomeAt(world, pos, biome, true);
    }

    public static void setBiomeAt(Level world, BlockPos pos, Biome biome, boolean sync) {
        // Biome array modification removed in MC 1.18+ — no-op
    }

    public static boolean resetBiomeAt(Level world, BlockPos pos) {
        return resetBiomeAt(world, pos, true);
    }

    public static boolean resetBiomeAt(Level world, BlockPos pos, boolean sync) {
        // Biome array modification removed in MC 1.18+ — always returns false
        return false;
    }

    public static boolean isWoodLog(BlockGetter world, BlockPos pos) {
        BlockState bs = world.getBlockState(pos);
        return bs.is(BlockTags.LOGS) || bs.is(BlockTags.LOGS_THAT_BURN);
    }

    public static boolean isOreBlock(Level world, BlockPos pos) {
        // OreDictionary removed; use block tag check
        BlockState bs = world.getBlockState(pos);
        return bs.is(net.minecraft.tags.BlockTags.COAL_ORES)
            || bs.is(net.minecraft.tags.BlockTags.IRON_ORES)
            || bs.is(net.minecraft.tags.BlockTags.GOLD_ORES)
            || bs.is(net.minecraft.tags.BlockTags.DIAMOND_ORES)
            || bs.is(net.minecraft.tags.BlockTags.REDSTONE_ORES)
            || bs.is(net.minecraft.tags.BlockTags.LAPIS_ORES)
            || bs.is(net.minecraft.tags.BlockTags.EMERALD_ORES)
            || bs.is(net.minecraft.tags.BlockTags.COPPER_ORES);
    }

    public static int setNibble(int data, int nibble, int nibbleIndex) {
        int shift = nibbleIndex * 4;
        return (data & ~(15 << shift)) | nibble << shift;
    }

    public static int getNibble(int data, int nibbleIndex) {
        return data >> (nibbleIndex << 2) & 0xF;
    }

    public static boolean getBit(int value, int bit) {
        return (value & 1 << bit) != 0x0;
    }

    public static int setBit(int value, int bit) {
        return value | 1 << bit;
    }

    public static int clearBit(int value, int bit) {
        return value & ~(1 << bit);
    }

    public static int toggleBit(int value, int bit) {
        return value ^ 1 << bit;
    }

    public static byte pack(boolean... vals) {
        byte result = 0;
        for (boolean bit : vals) {
            result = (byte)(result << 1 | ((bit & true) ? 1 : 0));
        }
        return result;
    }

    public static boolean[] unpack(byte val) {
        boolean[] result = new boolean[8];
        for (int i = 0; i < 8; ++i) {
            result[i] = ((byte)(val >> 7 - i & 0x1) == 1);
        }
        return result;
    }

    public static byte[] intToByteArray(int value) {
        return new byte[] { (byte)(value >>> 24), (byte)(value >>> 16), (byte)(value >>> 8), (byte)value };
    }

    public static int byteArraytoInt(byte[] bytes) {
        return bytes[0] << 24 | bytes[1] << 16 | bytes[2] << 8 | bytes[3];
    }

    public static byte[] shortToByteArray(short value) {
        return new byte[] { (byte)(value >>> 8), (byte)value };
    }

    public static short byteArraytoShort(byte[] bytes) {
        return (short)(bytes[0] << 8 | bytes[1]);
    }

    public static boolean isLyingInCone(double[] x, double[] t, double[] b, float aperture) {
        double halfAperture = aperture / 2.0f;
        double[] apexToXVect = dif(t, x);
        double[] axisVect = dif(t, b);
        boolean isInInfiniteCone = dotProd(apexToXVect, axisVect) / magn(apexToXVect) / magn(axisVect) > Math.cos(halfAperture);
        if (!isInInfiniteCone) {
            return false;
        }
        boolean isUnderRoundCap = dotProd(apexToXVect, axisVect) / magn(axisVect) < magn(axisVect);
        return isUnderRoundCap;
    }

    public static double dotProd(double[] a, double[] b) {
        return a[0] * b[0] + a[1] * b[1] + a[2] * b[2];
    }

    public static double[] dif(double[] a, double[] b) {
        return new double[] { a[0] - b[0], a[1] - b[1], a[2] - b[2] };
    }

    public static double magn(double[] a) {
        return Math.sqrt(a[0] * a[0] + a[1] * a[1] + a[2] * a[2]);
    }

    public static Vec3 calculateVelocity(Vec3 from, Vec3 to, double heightGain, double gravity) {
        double endGain = to.y - from.y;
        double horizDist = Math.sqrt(distanceSquared2d(from, to));
        double gain = heightGain;
        double maxGain = (gain > endGain + gain) ? gain : (endGain + gain);
        double a = -horizDist * horizDist / (4.0 * maxGain);
        double b = horizDist;
        double c = -endGain;
        double slope = -b / (2.0 * a) - Math.sqrt(b * b - 4.0 * a * c) / (2.0 * a);
        double vy = Math.sqrt(maxGain * gravity);
        double vh = vy / slope;
        double dx = to.x - from.x;
        double dz = to.z - from.z;
        double mag = Math.sqrt(dx * dx + dz * dz);
        double dirx = dx / mag;
        double dirz = dz / mag;
        double vx = vh * dirx;
        double vz = vh * dirz;
        return new Vec3(vx, vy, vz);
    }

    public static double distanceSquared2d(Vec3 from, Vec3 to) {
        double dx = to.x - from.x;
        double dz = to.z - from.z;
        return dx * dx + dz * dz;
    }

    public static double distanceSquared3d(Vec3 from, Vec3 to) {
        double dx = to.x - from.x;
        double dy = to.y - from.y;
        double dz = to.z - from.z;
        return dx * dx + dy * dy + dz * dz;
    }

    public static ItemStack generateLoot(int rarity, net.minecraft.util.RandomSource rand) {
        ItemStack is = ItemStack.EMPTY;
        if (rarity > 0 && rand.nextFloat() < 0.025f * rarity) {
            is = genGear(rarity, rand);
            if (is.isEmpty()) {
                is = generateLoot(rarity, rand);
            }
        }
        else {
            List<WeightedRandomLoot> bag;
            switch (rarity) {
                case 1: bag = WeightedRandomLoot.lootBagUncommon; break;
                case 2: bag = WeightedRandomLoot.lootBagRare; break;
                default: bag = WeightedRandomLoot.lootBagCommon; break;
            }
            if (!bag.isEmpty()) {
                is = net.minecraft.util.random.WeightedRandom
                    .getRandomItem(rand, bag, w -> w.itemWeight)
                    .map(w -> w.item.copy())
                    .orElse(ItemStack.EMPTY);
            }
        }
        return is.isEmpty() ? is : is.copy();
    }

    private static ItemStack genGear(int rarity, net.minecraft.util.RandomSource rand) {
        ItemStack is = ItemStack.EMPTY;
        int quality = rand.nextInt(2);
        if (rand.nextFloat() < 0.2f) { ++quality; }
        if (rand.nextFloat() < 0.15f) { ++quality; }
        if (rand.nextFloat() < 0.1f) { ++quality; }
        if (rand.nextFloat() < 0.095f) { ++quality; }
        if (rand.nextFloat() < 0.095f) { ++quality; }
        Item item = getGearItemForSlot(rand.nextInt(5), quality);
        if (item != null) {
            is = new ItemStack(item.asItem(), 1);
            return is.copy();
        }
        return ItemStack.EMPTY;
    }

    private static Item getGearItemForSlot(int slot, int quality) {
        switch (slot) {
            case 4: {
                if (quality == 0) return Items.LEATHER_HELMET;
                if (quality == 1) return Items.GOLDEN_HELMET;
                if (quality == 2) return Items.CHAINMAIL_HELMET;
                if (quality == 3) return Items.IRON_HELMET;
                if (quality == 4) return ItemsTC.thaumiumHelm;
                if (quality == 5) return Items.DIAMOND_HELMET;
                if (quality == 6) return ItemsTC.voidHelm;
            }
            case 3: {
                if (quality == 0) return Items.LEATHER_CHESTPLATE;
                if (quality == 1) return Items.GOLDEN_CHESTPLATE;
                if (quality == 2) return Items.CHAINMAIL_CHESTPLATE;
                if (quality == 3) return Items.IRON_CHESTPLATE;
                if (quality == 4) return ItemsTC.thaumiumChest;
                if (quality == 5) return Items.DIAMOND_CHESTPLATE;
                if (quality == 6) return ItemsTC.voidChest;
            }
            case 2: {
                if (quality == 0) return Items.LEATHER_LEGGINGS;
                if (quality == 1) return Items.GOLDEN_LEGGINGS;
                if (quality == 2) return Items.CHAINMAIL_LEGGINGS;
                if (quality == 3) return Items.IRON_LEGGINGS;
                if (quality == 4) return ItemsTC.thaumiumLegs;
                if (quality == 5) return Items.DIAMOND_LEGGINGS;
                if (quality == 6) return ItemsTC.voidLegs;
            }
            case 1: {
                if (quality == 0) return Items.LEATHER_BOOTS;
                if (quality == 1) return Items.GOLDEN_BOOTS;
                if (quality == 2) return Items.CHAINMAIL_BOOTS;
                if (quality == 3) return Items.IRON_BOOTS;
                if (quality == 4) return ItemsTC.thaumiumBoots;
                if (quality == 5) return Items.DIAMOND_BOOTS;
                if (quality == 6) return ItemsTC.voidBoots;
            }
            case 0: {
                if (quality == 0) return Items.IRON_AXE;
                if (quality == 1) return Items.IRON_SWORD;
                if (quality == 2) return Items.GOLDEN_AXE;
                if (quality == 3) return Items.GOLDEN_SWORD;
                if (quality == 4) return ItemsTC.thaumiumSword;
                if (quality == 5) return Items.DIAMOND_SWORD;
                if (quality == 6) return ItemsTC.voidSword;
                break;
            }
        }
        return null;
    }

    public static void writeItemStackToBuffer(ByteBuf bb, ItemStack stack) {
        // Simplified: write empty marker only — full Item ID codec removed in 1.20+
        bb.writeBoolean(!stack.isEmpty());
        if (!stack.isEmpty()) {
            try {
                CompoundTag tag = new CompoundTag();
                NbtIo.write(tag, (DataOutput) new java.io.DataOutputStream(new ByteBufOutputStream(bb)));
            } catch (IOException e) {
                throw new EncoderException(e);
            }
        }
    }

    public static ItemStack readItemStackFromBuffer(ByteBuf bb) {
        boolean present = bb.readBoolean();
        if (present) {
            try {
                NbtIo.read((DataInput) new java.io.DataInputStream(new ByteBufInputStream(bb)), NbtAccounter.unlimitedHeap());
            } catch (IOException e) {
                // ignore
            }
        }
        return ItemStack.EMPTY;
    }

    public static void writeCompoundTagToBuffer(ByteBuf bb, CompoundTag nbt) {
        if (nbt == null) {
            bb.writeByte(0);
        }
        else {
            try {
                NbtIo.write(nbt, (DataOutput) new java.io.DataOutputStream(new ByteBufOutputStream(bb)));
            }
            catch (IOException ioexception) {
                throw new EncoderException(ioexception);
            }
        }
    }

    public static CompoundTag readCompoundTagFromBuffer(ByteBuf bb) {
        int i = bb.readerIndex();
        byte b0 = bb.readByte();
        if (b0 == 0) {
            return null;
        }
        bb.readerIndex(i);
        try {
            return NbtIo.read((DataInput) new java.io.DataInputStream(new ByteBufInputStream(bb)), NbtAccounter.create(2097152L));
        }
        catch (IOException ex) {
            return null;
        }
    }

    public static Vec3 rotateAsBlock(Vec3 vec, Direction side) {
        return rotate(vec.subtract(0.5, 0.5, 0.5), side).add(0.5, 0.5, 0.5);
    }

    public static Vec3 rotateAsBlockRev(Vec3 vec, Direction side) {
        return revRotate(vec.subtract(0.5, 0.5, 0.5), side).add(0.5, 0.5, 0.5);
    }

    public static Vec3 rotate(Vec3 vec, Direction side) {
        switch (side) {
            case DOWN:  return new Vec3(vec.x, -vec.y, -vec.z);
            case UP:    return new Vec3(vec.x, vec.y, vec.z);
            case NORTH: return new Vec3(vec.x, vec.z, -vec.y);
            case SOUTH: return new Vec3(vec.x, -vec.z, vec.y);
            case WEST:  return new Vec3(-vec.y, vec.x, vec.z);
            case EAST:  return new Vec3(vec.y, -vec.x, vec.z);
            default:    return null;
        }
    }

    public static Vec3 revRotate(Vec3 vec, Direction side) {
        switch (side) {
            case DOWN:  return new Vec3(vec.x, -vec.y, -vec.z);
            case UP:    return new Vec3(vec.x, vec.y, vec.z);
            case NORTH: return new Vec3(vec.x, -vec.z, vec.y);
            case SOUTH: return new Vec3(vec.x, vec.z, -vec.y);
            case WEST:  return new Vec3(vec.y, -vec.x, vec.z);
            case EAST:  return new Vec3(-vec.y, vec.x, vec.z);
            default:    return null;
        }
    }

    public static Vec3 rotateAroundX(Vec3 vec, float angle) {
        float var2 = (float)(Mth.cos(angle));
        float var3 = (float)(Mth.sin(angle));
        return new Vec3(vec.x, vec.y * var2 + vec.z * var3, vec.z * var2 - vec.y * var3);
    }

    public static Vec3 rotateAroundY(Vec3 vec, float angle) {
        float var2 = (float)(Mth.cos(angle));
        float var3 = (float)(Mth.sin(angle));
        return new Vec3(vec.x * var2 + vec.z * var3, vec.y, vec.z * var2 - vec.x * var3);
    }

    public static Vec3 rotateAroundZ(Vec3 vec, float angle) {
        float var2 = (float)(Mth.cos(angle));
        float var3 = (float)(Mth.sin(angle));
        return new Vec3(vec.x * var2 + vec.y * var3, vec.y * var2 - vec.x * var3, vec.z);
    }

    public static HitResult rayTrace(Level worldIn, Entity entityIn, boolean useLiquids) {
        double d3 = 4.5;
        if (entityIn instanceof ServerPlayer sp) {
            d3 = sp.blockInteractionRange();
        }
        return rayTrace(worldIn, entityIn, useLiquids, d3);
    }

    public static HitResult rayTrace(Level worldIn, Entity entityIn, boolean useLiquids, double range) {
        float f = entityIn.getXRot();
        float f2 = entityIn.getYRot();
        double d0 = entityIn.getX();
        double d2 = entityIn.getY() + entityIn.getEyeHeight();
        double d3 = entityIn.getZ();
        Vec3 vec3d = new Vec3(d0, d2, d3);
        float f3 = Mth.cos(-f2 * 0.017453292f - 3.1415927f);
        float f4 = Mth.sin(-f2 * 0.017453292f - 3.1415927f);
        float f5 = -Mth.cos(-f * 0.017453292f);
        float f6 = Mth.sin(-f * 0.017453292f);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        Vec3 vec3d2 = vec3d.add(f7 * range, f6 * range, f8 * range);
        ClipContext.Fluid fluidMode = useLiquids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE;
        return worldIn.clip(new ClipContext(vec3d, vec3d2, ClipContext.Block.COLLIDER, fluidMode, entityIn));
    }

    public static HitResult rayTrace(Level worldIn, Entity entityIn, Vec3 lookvec, boolean useLiquids, double range) {
        double d0 = entityIn.getX();
        double d2 = entityIn.getY() + entityIn.getEyeHeight();
        double d3 = entityIn.getZ();
        Vec3 vec3d = new Vec3(d0, d2, d3);
        Vec3 vec3d2 = vec3d.add(lookvec.x * range, lookvec.y * range, lookvec.z * range);
        ClipContext.Fluid fluidMode = useLiquids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE;
        return worldIn.clip(new ClipContext(vec3d, vec3d2, ClipContext.Block.COLLIDER, fluidMode, entityIn));
    }

    public static Field getField(Class clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        }
        catch (NoSuchFieldException e) {
            Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            }
            return getField(superClass, fieldName);
        }
    }

    public static AABB rotateBlockAABB(AABB aabb, Direction facing) {
        Cuboid6 c = new Cuboid6(aabb).add(new Vector3(-0.5, -0.5, -0.5)).apply(Rotation.sideRotations[facing.getIndex()]).add(new Vector3(0.5, 0.5, 0.5));
        return c.aabb();
    }

    static {
        Utils.specialMiningResult = new HashMap<List<Object>, ItemStack>();
        Utils.specialMiningChance = new HashMap<List<Object>, Float>();
        colorNames = new String[] { "White", "Orange", "Magenta", "Light Blue", "Yellow", "Lime", "Pink", "Gray", "Light Gray", "Cyan", "Purple", "Blue", "Brown", "Green", "Red", "Black" };
        colors = new int[] { 15790320, 15435844, 12801229, 6719955, 14602026, 4312372, 14188952, 4408131, 10526880, 2651799, 8073150, 2437522, 5320730, 3887386, 11743532, 1973019 };
        Utils.oreDictLogs = new ArrayList<List>();
    }
}
