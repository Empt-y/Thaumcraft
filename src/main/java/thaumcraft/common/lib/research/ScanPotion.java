package thaumcraft.common.lib.research;
import java.util.Iterator;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import thaumcraft.api.research.IScanThing;
import thaumcraft.api.research.ScanningManager;


public class ScanPotion implements IScanThing
{
    MobEffect potion;
    
    public ScanPotion(MobEffect potion) {
        this.potion = potion;
    }
    
    @Override
    public boolean checkThing(Player player, Object obj) {
        return getMobEffectInstance(player, obj) != null;
    }
    
    private MobEffectInstance getMobEffectInstance(Player player, Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof LivingEntity) {
            LivingEntity e = (LivingEntity)obj;
            for (MobEffectInstance potioneffect : e.getActiveMobEffectInstances()) {
                if (potioneffect.getPotion() == potion) {
                    return potioneffect;
                }
            }
        }
        else {
            ItemStack is = ScanningManager.getItemFromParms(player, obj);
            if (is != null && !is.isEmpty()) {
                for (MobEffectInstance potioneffect : PotionUtils.getEffectsFromStack(is)) {
                    if (potioneffect.getPotion() == potion) {
                        return potioneffect;
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public String getResearchKey(Player player, Object obj) {
        return "!" + potion.getDescriptionId();
    }
}
