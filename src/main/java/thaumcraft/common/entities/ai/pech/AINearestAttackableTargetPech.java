package thaumcraft.common.entities.ai.pech;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import thaumcraft.common.entities.monster.EntityPech;


public class AINearestAttackableTargetPech extends NearestAttackableTargetGoal
{
    public AINearestAttackableTargetPech(PathfinderMob p_i45878_1_, Class p_i45878_2_, boolean p_i45878_3_) {
        super(p_i45878_1_, p_i45878_2_, p_i45878_3_);
    }
    
    @Override
        public boolean canUse() {
        return (!(mob instanceof EntityPech) || ((EntityPech) mob).getAnger() != 0) && super.canUse();
    }
}
