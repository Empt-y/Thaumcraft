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
            for (MobEffectInstance potioneffect : e.getActiveEffects()) {
                if (potioneffect.getEffect().value() == potion) {
                    return potioneffect;
                }
            }
        }
        else {
            ItemStack is = ScanningManager.getItemFromParms(player, obj);
            if (is != null && !is.isEmpty()) {
                net.minecraft.world.item.alchemy.PotionContents pc = is.get(net.minecraft.core.component.DataComponents.POTION_CONTENTS);
                if (pc != null) {
                    for (MobEffectInstance potioneffect : pc.getAllEffects()) {
                        if (potioneffect.getEffect().value() == potion) {
                            return potioneffect;
                        }
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
