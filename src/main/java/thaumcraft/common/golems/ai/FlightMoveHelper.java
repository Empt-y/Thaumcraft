package thaumcraft.common.golems.ai;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.Vec3;
import thaumcraft.common.golems.EntityThaumcraftGolem;


public class FlightMoveHelper extends MoveControl {

    public FlightMoveHelper(EntityThaumcraftGolem golem) {
        super(golem);
    }

    @Override
    public void tick() {
        if (this.operation != Operation.MOVE_TO || this.mob.getNavigation().isDone()) {
            this.mob.setSpeed(0.0f);
            return;
        }
        this.operation = Operation.WAIT;
        double dx = this.wantedX - this.mob.getX();
        double dy = this.wantedY - this.mob.getY();
        double dz = this.wantedZ - this.mob.getZ();
        double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);
        dy /= dist;
        float yaw = (float)(Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0f;
        this.mob.setYRot(this.rotlerp(this.mob.getYRot(), yaw, 30.0f));
        this.mob.yBodyRot = this.mob.getYRot();
        float speed = (float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED));
        this.mob.setSpeed(this.mob.getSpeed() + (speed - this.mob.getSpeed()) * 0.125f);
        double cosYaw = Math.cos(this.mob.getYRot() * (float)(Math.PI / 180.0));
        double sinYaw = Math.sin(this.mob.getYRot() * (float)(Math.PI / 180.0));
        double bob = Math.sin((this.mob.tickCount + this.mob.getId()) * 0.5) * 0.05;
        double bob2 = Math.sin((this.mob.tickCount + this.mob.getId()) * 0.75) * 0.05;
        Vec3 delta = this.mob.getDeltaMovement();
        this.mob.setDeltaMovement(
            delta.x + bob * cosYaw,
            delta.y + bob2 * (sinYaw + cosYaw) * 0.25 + this.mob.getSpeed() * dy * 0.1,
            delta.z + bob * sinYaw
        );
        this.mob.getLookControl().setLookAt(
            this.mob.getX() + dx / dist * 2.0,
            this.mob.getEyeY() + dy,
            this.mob.getZ() + dz / dist * 2.0,
            10.0f, 40.0f
        );
    }
}
