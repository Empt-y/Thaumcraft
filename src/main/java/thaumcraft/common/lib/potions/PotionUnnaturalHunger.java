package thaumcraft.common.lib.potions;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.world.entity.Mob;


public class PotionUnnaturalHunger extends MobEffect
{
    public static MobEffect instance;
    private int statusIconIndex;
    static Identifier rl;
    
    public PotionUnnaturalHunger(boolean par2, int par3) {
        super(net.minecraft.world.effect.MobEffectCategory.HARMFUL, par3);
        statusIconIndex = -1;
        setIconIndex(0, 0);
        setPotionName("potion.unhunger");
        setIconIndex(7, 1, par2);
        setEffectiveness(0.25);
    }
    
    public boolean isBadEffect() {
        return true;
    }
    
    @OnlyIn(Dist.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getInstance().renderEngine.bindTexture(PotionUnnaturalHunger.rl);
        return super.getStatusIconIndex();
    }
    
    public void performEffect(LivingEntity target, int par2) {
        if (!target.level().isClientSide() && target instanceof Player) {
            ((Player)target).addExhaustion(0.025f * (par2 + 1));
        }
    }
    
    public boolean isReady(int par1, int par2) {
        return true;
    }
    
    static {
        PotionUnnaturalHunger.instance = null;
        rl = Identifier.fromNamespaceAndPath("thaumcraft", "textures/misc/potions.png");
    }
}
