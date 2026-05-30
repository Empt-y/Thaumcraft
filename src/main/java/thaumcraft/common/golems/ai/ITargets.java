package thaumcraft.common.golems.ai;
import net.minecraft.world.entity.animal.Animal;


public interface ITargets
{
    boolean getTargetAnimal();
    
    void setTargetAnimal(boolean p0);
    
    boolean getTargetMob();
    
    void setTargetMob(boolean p0);
    
    boolean getTargetPlayer();
    
    void setTargetPlayer(boolean p0);
    
    boolean getTargetFriendly();
    
    void setTargetFriendly(boolean p0);
}
