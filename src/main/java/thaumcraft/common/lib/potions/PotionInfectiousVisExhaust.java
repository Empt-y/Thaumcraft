package thaumcraft.common.lib.potions;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.potions.PotionVisExhaust;
import net.minecraft.world.entity.Mob;


public class PotionInfectiousVisExhaust extends MobEffect
{
    public static MobEffect instance;
    private int statusIconIndex;
    static Identifier rl;
    
    public PotionInfectiousVisExhaust(boolean par2, int par3) {
        super(net.minecraft.world.effect.MobEffectCategory.HARMFUL, par3);
        statusIconIndex = -1;
        setIconIndex(0, 0);
        setPotionName("potion.infvisexhaust");
        setIconIndex(6, 1, par2);
        setEffectiveness(0.25);
    }
    
    public boolean isBadEffect() {
        return true;
    }
    
    @OnlyIn(Dist.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getInstance().renderEngine.bindTexture(PotionInfectiousVisExhaust.rl);
        return super.getStatusIconIndex();
    }
    
    public void performEffect(LivingEntity target, int par2) {
        List<LivingEntity> targets = target.level().getEntitiesOfClass(LivingEntity.class, target.getBoundingBox().inflate(4.0, 4.0, 4.0));
        if (targets.size() > 0) {
            for (LivingEntity e : targets) {
                if (!e.hasEffect(net.minecraft.core.Holder.direct(PotionInfectiousVisExhaust.instance))) {
                    if (par2 > 0) {
                        e.addEffect(new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionInfectiousVisExhaust.instance), 6000, par2 - 1, false, true));
                    }
                    else {
                        e.addEffect(new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionVisExhaust.instance), 6000, 0, false, true));
                    }
                }
            }
        }
    }
    
    public boolean isReady(int par1, int par2) {
        return par1 % 40 == 0;
    }
    
    static {
        PotionInfectiousVisExhaust.instance = null;
        rl = Identifier.fromNamespaceAndPath("thaumcraft", "textures/misc/potions.png");
    }
}
