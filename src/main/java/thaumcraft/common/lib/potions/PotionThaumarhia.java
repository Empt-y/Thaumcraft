package thaumcraft.common.lib.potions;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.blocks.BlocksTC;
import net.minecraft.world.entity.Mob;


public class PotionThaumarhia extends MobEffect
{
    public static MobEffect instance;
    private int statusIconIndex;
    static Identifier rl;
    
    public PotionThaumarhia(boolean par2, int par3) {
        super(net.minecraft.world.effect.MobEffectCategory.HARMFUL, par3);
        statusIconIndex = -1;
        setIconIndex(0, 0);
        setPotionName("potion.thaumarhia");
        setIconIndex(7, 2, par2);
        setEffectiveness(0.25);
    }
    
    public boolean isBadEffect() {
        return true;
    }
    
    @OnlyIn(Dist.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getInstance().renderEngine.bindTexture(PotionThaumarhia.rl);
        return super.getStatusIconIndex();
    }
    
    public void performEffect(LivingEntity target, int par2) {
        if (!target.level().isClientSide() && target.getRandom().nextInt(15) == 0 && target.level().isEmptyBlock(new BlockPos(target))) {
            target.level().setBlockAndUpdate(new BlockPos(target), BlocksTC.fluxGoo.defaultBlockState());
        }
    }
    
    public boolean isReady(int par1, int par2) {
        return par1 % 20 == 0;
    }
    
    static {
        PotionThaumarhia.instance = null;
        rl = Identifier.fromNamespaceAndPath("thaumcraft", "textures/misc/potions.png");
    }
}
