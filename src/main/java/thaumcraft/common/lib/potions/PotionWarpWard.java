package thaumcraft.common.lib.potions;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.world.entity.Mob;


public class PotionWarpWard extends MobEffect
{
    public static MobEffect instance;
    private int statusIconIndex;
    static Identifier rl;
    
    public PotionWarpWard(boolean par2, int par3) {
        super(net.minecraft.world.effect.MobEffectCategory.HARMFUL, par3);
        statusIconIndex = -1;
        setIconIndex(0, 0);
        setPotionName("potion.warpward");
        setIconIndex(3, 2, par2);
        setEffectiveness(0.25);
    }
    
    public boolean isBadEffect() {
        return false;
    }
    
    @OnlyIn(Dist.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getInstance().renderEngine.bindTexture(PotionWarpWard.rl);
        return super.getStatusIconIndex();
    }
    
    public void performEffect(LivingEntity target, int par2) {
    }
    
    static {
        PotionWarpWard.instance = null;
        rl = Identifier.fromNamespaceAndPath("thaumcraft", "textures/misc/potions.png");
    }
}
