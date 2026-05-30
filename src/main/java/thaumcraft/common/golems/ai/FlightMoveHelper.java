package thaumcraft.common.golems.ai;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import thaumcraft.common.golems.EntityThaumcraftGolem;


public class FlightMoveHelper extends MoveControl {

    private EntityThaumcraftGolem golem;

    public FlightMoveHelper(EntityThaumcraftGolem golem) {
        super(golem);
        this.golem = golem;
    }

    @Override
    public void tick() {
        // FIXME: stub - flight movement not implemented
        super.tick();
    }
}
