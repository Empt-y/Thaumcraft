package thaumcraft.client.fx.particles;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.TerrainParticle;
import net.minecraft.world.level.Level;


public class FXDigging extends TerrainParticle
{
    public FXDigging(Level worldIn, double p_i46280_2_, double p_i46280_4_, double p_i46280_6_, double p_i46280_8_, double p_i46280_10_, double p_i46280_12_, BlockState p_i46280_14_) {
        super((ClientLevel) worldIn, p_i46280_2_, p_i46280_4_, p_i46280_6_, p_i46280_8_, p_i46280_10_, p_i46280_12_, p_i46280_14_);
    }
}
