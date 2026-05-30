package thaumcraft.common.golems.parts;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.parts.GolemLeg;
import thaumcraft.client.fx.FXDispatcher;


public class GolemLegLevitator implements GolemLeg.ILegFunction
{
    @Override
    public void onUpdateTick(IGolemAPI golem) {
        if (golem.getGolemWorld().isClientSide() && (!golem.getGolemEntity().onGround() || golem.getGolemEntity().tickCount % 5 == 0)) {
            FXDispatcher.INSTANCE.drawGolemFlyParticles(golem.getGolemEntity().getX(), golem.getGolemEntity().getY() + 0.1, golem.getGolemEntity().getZ(), golem.getGolemWorld().getRandom().nextGaussian() / 100.0, -0.1, golem.getGolemWorld().getRandom().nextGaussian() / 100.0);
        }
    }
}
