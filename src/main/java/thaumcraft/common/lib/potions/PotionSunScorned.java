package thaumcraft.common.lib.potions;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.world.entity.Mob;


public class PotionSunScorned extends MobEffect
{
    public static MobEffect instance;
    private int statusIconIndex;
    static Identifier rl;
    
    public PotionSunScorned(boolean par2, int par3) {
        super(net.minecraft.world.effect.MobEffectCategory.HARMFUL, par3);
        statusIconIndex = -1;
        setIconIndex(0, 0);
        setPotionName("potion.sunscorned");
        setIconIndex(6, 2, par2);
        setEffectiveness(0.25);
    }
    
    public boolean isBadEffect() {
        return true;
    }
    
    @OnlyIn(Dist.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getInstance().renderEngine.bindTexture(PotionSunScorned.rl);
        return super.getStatusIconIndex();
    }
    
    public void performEffect(LivingEntity target, int par2) {
        if (!target.level().isClientSide()) {
            float f = target.getBrightness();
            if (f > 0.5f && target.getRandom().nextFloat() * 30.0f < (f - 0.4f) * 2.0f && target.level().canBlockSeeSky(new BlockPos(Mth.floor(target.getX()), Mth.floor(target.getY()), Mth.floor(target.getZ())))) {
                target.setFire(4);
            }
            else if (f < 0.25f && target.getRandom().nextFloat() > f * 2.0f) {
                target.heal(1.0f);
            }
        }
    }
    
    public boolean isReady(int par1, int par2) {
        return par1 % 40 == 0;
    }
    
    static {
        PotionSunScorned.instance = null;
        rl = Identifier.fromNamespaceAndPath("thaumcraft", "textures/misc/potions.png");
    }
}
