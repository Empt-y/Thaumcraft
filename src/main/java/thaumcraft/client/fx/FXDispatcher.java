package thaumcraft.client.fx;
import java.awt.Color;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.beams.FXArc;
import thaumcraft.client.fx.beams.FXBeamBore;
import thaumcraft.client.fx.beams.FXBeamWand;
import thaumcraft.client.fx.beams.FXBolt;
import thaumcraft.client.fx.other.FXBlockWard;
import thaumcraft.client.fx.other.FXBoreStream;
import thaumcraft.client.fx.other.FXEssentiaStream;
import thaumcraft.client.fx.other.FXShieldRunes;
import thaumcraft.client.fx.other.FXVoidStream;
import thaumcraft.client.fx.particles.FXBlockRunes;
import thaumcraft.client.fx.particles.FXBoreParticles;
import thaumcraft.client.fx.particles.FXBoreSparkle;
import thaumcraft.client.fx.particles.FXBreakingFade;
import thaumcraft.client.fx.particles.FXFireMote;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.client.fx.particles.FXGenericGui;
import thaumcraft.client.fx.particles.FXGenericP2E;
import thaumcraft.client.fx.particles.FXPlane;
import thaumcraft.client.fx.particles.FXSmokeSpiral;
import thaumcraft.client.fx.particles.FXSwarm;
import thaumcraft.client.fx.particles.FXVent2;
import thaumcraft.client.fx.particles.FXVent;
import thaumcraft.client.fx.particles.FXVisSparkle;
import thaumcraft.client.fx.particles.FXWispEG;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.crafting.TileCrucible;


public class FXDispatcher
{
    public static FXDispatcher INSTANCE;
    static int q;

    public ClientLevel getLevel() {
        return Minecraft.getInstance().level;
    }

    public void drawFireMote(float x, float y, float z, float vx, float vy, float vz, float r, float g, float b, float alpha, float scale) {
        boolean bb = getLevel().getRandom().nextBoolean();
        FXFireMote glow = new FXFireMote(getLevel(), x, y, z, vx, vy, vz, r, g, b, bb ? (scale / 3.0f) : scale, bb ? 1 : 0);
        glow.setAlphaF(alpha);
        ParticleEngine.addEffect(getLevel(), glow);
    }

    public void drawAlumentum(float x, float y, float z, float vx, float vy, float vz, float r, float g, float b, float alpha, float scale) {
        FXFireMote glow = new FXFireMote(getLevel(), x, y, z, vx, vy, vz, r, g, b, scale, 1);
        glow.setAlphaF(alpha);
        ParticleEngine.addEffect(getLevel(), glow);
    }

    public void drawTaintParticles(float x, float y, float z, float vx, float vy, float vz, float scale) {
        FXGeneric fb = new FXGeneric(getLevel(), x, y, z, vx, vy, vz);
        fb.setMaxAge(80 + getLevel().getRandom().nextInt(20));
        fb.setRBGColorF(0.4f + getLevel().getRandom().nextFloat() * 0.2f, 0.1f + getLevel().getRandom().nextFloat() * 0.3f, 0.5f + getLevel().getRandom().nextFloat() * 0.2f);
        fb.setAlphaF(0.75f, 0.0f);
        fb.setGridSize(16);
        fb.setParticles(57 + getLevel().getRandom().nextInt(3), 1, 1);
        fb.setScale(scale, scale / 4.0f);
        fb.setLayer(1);
        fb.setSlowDown(0.9750000238418579);
        fb.setGravity(0.2f);
        fb.setRotationSpeed(getLevel().getRandom().nextFloat(), getLevel().getRandom().nextBoolean() ? -1.0f : 1.0f);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void drawLightningFlash(double x, double y, double z, float r, float g, float b, float alpha, float scale) {
        FXGeneric fb = new FXGeneric(getLevel(), x, y, z, 0.0, 0.0, 0.0);
        fb.setMaxAge(5 + getLevel().getRandom().nextInt(5));
        fb.setGridSize(16);
        fb.setRBGColorF(r, g, b);
        fb.setAlphaF(alpha, 0.0f);
        fb.setParticles(108 + getLevel().getRandom().nextInt(4), 1, 1);
        fb.setScale(scale);
        fb.setLayer(0);
        fb.setRotationSpeed(getLevel().getRandom().nextFloat(), 0.0f);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void drawGenericParticles(double x, double y, double z, double mx, double my, double mz, GenPart part) {
        FXGeneric fb = new FXGeneric(getLevel(), x, y, z, mx, my, mz);
        fb.setMaxAge(part.age);
        fb.setRBGColorF(part.redStart, part.greenStart, part.blueStart, part.redEnd, part.greenEnd, part.blueEnd);
        fb.setAlphaF(part.alpha);
        fb.setLoop(part.loop);
        fb.setParticles(part.partStart, part.partNum, part.partInc);
        fb.setScale(part.scale);
        fb.setLayer(part.layer);
        fb.setRotationSpeed(part.rotstart, part.rot);
        fb.setSlowDown(part.slowDown);
        fb.setGravity(part.grav);
        fb.setGridSize(part.grid);
        ParticleEngine.addEffectWithDelay(getLevel(), fb, part.delay);
    }

    public void drawGenericParticles(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) {
        FXGeneric fb = new FXGeneric(getLevel(), x, y, z, x2, y2, z2);
        fb.setMaxAge(age);
        fb.setRBGColorF(r, g, b);
        fb.setAlphaF(alpha);
        fb.setLoop(loop);
        fb.setParticles(start, num, inc);
        fb.setScale(scale);
        fb.setLayer(layer);
        fb.setRotationSpeed(rot);
        ParticleEngine.addEffectWithDelay(getLevel(), fb, delay);
    }

    public void drawGenericParticles16(double x, double y, double z, double x2, double y2, double z2, float r, float g, float b, float alpha, boolean loop, int start, int num, int inc, int age, int delay, float scale, float rot, int layer) {
        FXGeneric fb = new FXGeneric(getLevel(), x, y, z, x2, y2, z2);
        fb.setGridSize(16);
        fb.setMaxAge(age);
        fb.setRBGColorF(r, g, b);
        fb.setAlphaF(alpha);
        fb.setLoop(loop);
        fb.setParticles(start, num, inc);
        fb.setScale(scale);
        fb.setLayer(layer);
        fb.setRotationSpeed(rot);
        ParticleEngine.addEffectWithDelay(getLevel(), fb, delay);
    }

    public void drawLevitatorParticles(double x, double y, double z, double x2, double y2, double z2) {
        FXGeneric fb = new FXGeneric(getLevel(), x, y, z, x2, y2, z2);
        fb.setMaxAge(200 + getLevel().getRandom().nextInt(100));
        fb.setRBGColorF(0.5f, 0.5f, 0.2f);
        fb.setAlphaF(0.3f, 0.0f);
        fb.setGridSize(16);
        fb.setParticles(56, 1, 1);
        fb.setScale(2.0f, 5.0f);
        fb.setLayer(0);
        fb.setSlowDown(1.0);
        fb.setRotationSpeed(getLevel().getRandom().nextFloat(), getLevel().getRandom().nextBoolean() ? -1.0f : 1.0f);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void drawStabilizerParticles(double x, double y, double z, double x2, double y2, double z2, int life) {
        FXGeneric fb = new FXGeneric(getLevel(), x, y, z, x2, y2, z2);
        fb.setMaxAge(life + getLevel().getRandom().nextInt(life));
        fb.setRBGColorF(0.5f, 0.2f, 0.5f);
        fb.setAlphaF(0.3f, 0.0f);
        fb.setGridSize(16);
        fb.setParticles(72 + getLevel().getRandom().nextInt(4), 1, 1);
        fb.setScale(1.0f, 10.0f);
        fb.setLayer(0);
        fb.setSlowDown(1.01);
        fb.setRotationSpeed(getLevel().getRandom().nextFloat(), getLevel().getRandom().nextBoolean() ? -1.0f : 1.0f);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void drawGolemFlyParticles(double x, double y, double z, double x2, double y2, double z2) {
        try {
            FXGeneric fb = new FXGeneric(getLevel(), x, y, z, x2, y2, z2);
            fb.setMaxAge(20 + getLevel().getRandom().nextInt(5));
            fb.setAlphaF(0.3f, 0.0f);
            fb.setGridSize(16);
            fb.setParticles(56, 1, 1);
            fb.setScale(1.5f, 3.0f, 8.0f);
            fb.setLayer(0);
            fb.setSlowDown(1.0);
            fb.setWind(0.001);
            fb.setRotationSpeed(getLevel().getRandom().nextFloat(), getLevel().getRandom().nextBoolean() ? -1.0f : 1.0f);
            ParticleEngine.addEffect(getLevel(), fb);
        }
        catch (Exception ex) {}
    }

    public void drawPollutionParticles(BlockPos p) {
        float x = p.getX() + 0.2f + getLevel().getRandom().nextFloat() * 0.6f;
        float y = p.getY() + 0.2f + getLevel().getRandom().nextFloat() * 0.6f;
        float z = p.getZ() + 0.2f + getLevel().getRandom().nextFloat() * 0.6f;
        FXGeneric fb = new FXGeneric(getLevel(), x, y, z, (getLevel().getRandom().nextFloat() - getLevel().getRandom().nextFloat()) * 0.005, 0.02, (getLevel().getRandom().nextFloat() - getLevel().getRandom().nextFloat()) * 0.005);
        fb.setMaxAge(100 + getLevel().getRandom().nextInt(60));
        fb.setRBGColorF(1.0f, 0.3f, 0.9f);
        fb.setAlphaF(0.5f, 0.0f);
        fb.setGridSize(16);
        fb.setParticles(56, 1, 1);
        fb.setScale(2.0f, 5.0f);
        fb.setLayer(1);
        fb.setSlowDown(1.0);
        fb.setWind(0.001);
        fb.setRotationSpeed(getLevel().getRandom().nextFloat(), getLevel().getRandom().nextBoolean() ? -1.0f : 1.0f);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void drawBlockSparkles(BlockPos p, Vec3 start) {
        net.minecraft.world.phys.shapes.VoxelShape bsShape = getWorld().getBlockState(p).getShape(getLevel(), p);
        AABB bs = bsShape.isEmpty() ? new AABB(0, 0, 0, 1, 1, 1) : bsShape.bounds();
        int num = (int)(((bs.getXsize() + bs.getYsize() + bs.getZsize()) / 3.0) * 20.0);
        for (Direction face : Direction.values()) {
            BlockState state = getWorld().getBlockState(p.relative(face));
            if (!state.canOcclude()) {
                boolean rx = face.getStepX() == 0;
                boolean ry = face.getStepY() == 0;
                boolean rz = face.getStepZ() == 0;
                double mx = 0.5 + face.getStepX() * 0.51;
                double my = 0.5 + face.getStepY() * 0.51;
                double mz = 0.5 + face.getStepZ() * 0.51;
                for (int a = 0; a < num * 2; ++a) {
                    double x = mx;
                    double y = my;
                    double z = mz;
                    if (rx) x += getLevel().getRandom().nextGaussian() * 0.6;
                    if (ry) y += getLevel().getRandom().nextGaussian() * 0.6;
                    if (rz) z += getLevel().getRandom().nextGaussian() * 0.6;
                    x = Mth.clamp(x, bs.minX, bs.maxX);
                    y = Mth.clamp(y, bs.minY, bs.maxY);
                    z = Mth.clamp(z, bs.minZ, bs.maxZ);
                    float r = 1.0f;
                    float g = (getLevel().getRandom().nextInt(67) + 189) / 255.0f;
                    float b = (getLevel().getRandom().nextInt(192) + 64) / 255.0f;
                    Vec3 v1 = new Vec3(p.getX() + x, p.getY() + y, p.getZ() + z);
                    double delay = getLevel().getRandom().nextInt(5) + v1.distanceTo(start) * 16.0;
                    drawSimpleSparkle(getLevel().getRandom(), p.getX() + x, p.getY() + y, p.getZ() + z, 0.0, 0.0025, 0.0, 0.4f + (float) getLevel().getRandom().nextGaussian() * 0.1f, r, g, b, (int)delay, 1.0f, 0.01f, 16);
                }
            }
        }
    }

    public void drawLineSparkle(RandomSource rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) {
        boolean sp = rand.nextFloat() < 0.2;
        FXGeneric fb = new FXGeneric(getLevel(), x, y, z, x2, y2, z2);
        int age = baseAge * 4 + getLevel().getRandom().nextInt(baseAge);
        fb.setMaxAge(age);
        fb.setRBGColorF(r, g, b);
        fb.setAlphaF(0.0f, 1.0f, 0.0f);
        fb.setParticles(sp ? 320 : 512, 16, 1);
        fb.setLoop(true);
        fb.setGravity(grav);
        fb.setScale(scale, scale * 2.0f, scale);
        fb.setLayer(0);
        fb.setSlowDown(decay);
        fb.setRandomMovementScale(5.0E-5f, 0.0f, 5.0E-5f);
        ParticleEngine.addEffectWithDelay(getLevel(), fb, delay);
    }

    public void drawSimpleSparkle(RandomSource rand, double x, double y, double z, double x2, double y2, double z2, float scale, float r, float g, float b, int delay, float decay, float grav, int baseAge) {
        boolean sp = rand.nextFloat() < 0.2;
        FXGeneric fb = new FXGeneric(getLevel(), x, y, z, x2, y2, z2);
        int age = baseAge * 4 + getLevel().getRandom().nextInt(baseAge);
        fb.setMaxAge(age);
        fb.setRBGColorF(r, g, b);
        float[] alphas = new float[6 + rand.nextInt(age / 3)];
        for (int a = 1; a < alphas.length - 1; ++a) {
            alphas[a] = rand.nextFloat();
        }
        fb.setAlphaF(alphas);
        fb.setParticles(sp ? 320 : 512, 16, 1);
        fb.setLoop(true);
        fb.setGravity(grav);
        fb.setScale(scale, scale * 2.0f);
        fb.setLayer(0);
        fb.setSlowDown(decay);
        fb.setRandomMovementScale(5.0E-4f, 0.001f, 5.0E-4f);
        fb.setWind(5.0E-4);
        ParticleEngine.addEffectWithDelay(getLevel(), fb, delay);
    }

    public void drawSimpleSparkleGui(RandomSource rand, double x, double y, double x2, double y2, float scale, float r, float g, float b, int delay, float decay, float grav) {
        boolean sp = rand.nextFloat() < 0.2;
        FXGenericGui fb = new FXGenericGui(getLevel(), x, y, 0.0, x2, y2, 0.0);
        fb.setMaxAge(32 + getLevel().getRandom().nextInt(8));
        fb.setRBGColorF(r, g, b);
        fb.setAlphaF(0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f);
        fb.setParticles(sp ? 320 : 512, 16, 1);
        fb.setLoop(true);
        fb.setGravity(grav);
        fb.setScale(scale, scale * 2.0f);
        fb.setNoClip(false);
        fb.setLayer(4);
        fb.setSlowDown(decay);
        fb.setRandomMovementScale(0.025f, 0.025f, 0.0f);
        ParticleEngine.addEffectWithDelay(getLevel(), fb, delay);
    }

    public void drawBlockMistParticles(BlockPos p, int c) {
        net.minecraft.world.phys.shapes.VoxelShape bsShape2 = getWorld().getBlockState(p).getShape(getLevel(), p);
        AABB bs = bsShape2.isEmpty() ? new AABB(0, 0, 0, 1, 1, 1) : bsShape2.bounds();
        Color color = new Color(c);
        for (int a = 0; a < 8; ++a) {
            double x = p.getX() + bs.minX + getLevel().getRandom().nextFloat() * (bs.maxX - bs.minX);
            double y = p.getY() + bs.minY + getLevel().getRandom().nextFloat() * (bs.maxY - bs.minY);
            double z = p.getZ() + bs.minZ + getLevel().getRandom().nextFloat() * (bs.maxZ - bs.minZ);
            FXGeneric fb = new FXGeneric(getLevel(), x, y, z, getLevel().getRandom().nextGaussian() * 0.01, getLevel().getRandom().nextFloat() * 0.075, getLevel().getRandom().nextGaussian() * 0.01);
            fb.setMaxAge(50 + getLevel().getRandom().nextInt(25));
            fb.setRBGColorF(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
            fb.setAlphaF(0.0f, 0.5f, 0.4f, 0.3f, 0.2f, 0.1f, 0.0f);
            fb.setGridSize(16);
            fb.setParticles(56, 1, 1);
            fb.setScale(5.0f, 1.0f);
            fb.setLayer(0);
            fb.setSlowDown(1.0);
            fb.setGravity(0.1f);
            fb.setWind(0.001);
            fb.setRotationSpeed(getLevel().getRandom().nextFloat(), getLevel().getRandom().nextBoolean() ? -1.0f : 1.0f);
            ParticleEngine.addEffect(getLevel(), fb);
        }
    }

    public void drawFocusCloudParticle(double x, double y, double z, double mx, double my, double mz, int c) {
        Color color = new Color(c);
        FXGeneric fb = new FXGeneric(getLevel(), x, y, z, mx, my, mz);
        fb.setMaxAge(20 + getLevel().getRandom().nextInt(10));
        fb.setRBGColorF(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
        fb.setAlphaF(0.0f, 0.66f, 0.0f);
        fb.setGridSize(16);
        fb.setParticles(56 + getLevel().getRandom().nextInt(4), 1, 1);
        fb.setScale(5.0f + getLevel().getRandom().nextFloat(), 10.0f + getLevel().getRandom().nextFloat());
        fb.setLayer(0);
        fb.setSlowDown(0.99);
        fb.setWind(0.001);
        fb.setRotationSpeed(getLevel().getRandom().nextFloat(), getLevel().getRandom().nextBoolean() ? -0.25f : 0.25f);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void drawWispyMotesOnBlock(BlockPos pp, int age, float grav) {
        drawWispyMotes(pp.getX() + getLevel().getRandom().nextFloat(), pp.getY(), pp.getZ() + getLevel().getRandom().nextFloat(), 0.0, 0.0, 0.0, age, 0.4f + getLevel().getRandom().nextFloat() * 0.6f, 0.6f + getLevel().getRandom().nextFloat() * 0.4f, 0.6f + getLevel().getRandom().nextFloat() * 0.4f, grav);
    }

    public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float grav) {
        drawWispyMotes(d, e, f, vx, vy, vz, age, 0.25f + getLevel().getRandom().nextFloat() * 0.75f, 0.25f + getLevel().getRandom().nextFloat() * 0.75f, 0.25f + getLevel().getRandom().nextFloat() * 0.75f, grav);
    }

    public void drawWispyMotes(double d, double e, double f, double vx, double vy, double vz, int age, float r, float g, float b, float grav) {
        FXGeneric fb = new FXGeneric(getLevel(), d, e, f, vx, vy, vz);
        fb.setMaxAge((int)(age + age / 2 * getLevel().getRandom().nextFloat()));
        fb.setRBGColorF(r, g, b);
        fb.setAlphaF(0.0f, 0.6f, 0.6f, 0.0f);
        fb.setGridSize(64);
        fb.setParticles(512, 16, 1);
        fb.setScale(1.0f, 0.5f);
        fb.setLoop(true);
        fb.setWind(0.001);
        fb.setGravity(grav);
        fb.setRandomMovementScale(0.0025f, 0.0f, 0.0025f);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void drawBlockMistParticlesFlat(BlockPos p, int c) {
        Color color = new Color(c);
        for (int a = 0; a < 6; ++a) {
            double x = p.getX() + getLevel().getRandom().nextFloat();
            double y = p.getY() + getLevel().getRandom().nextFloat() * 0.125f;
            double z = p.getZ() + getLevel().getRandom().nextFloat();
            FXGeneric fb = new FXGeneric(getLevel(), x, y, z, (getLevel().getRandom().nextFloat() - getLevel().getRandom().nextFloat()) * 0.005, 0.005, (getLevel().getRandom().nextFloat() - getLevel().getRandom().nextFloat()) * 0.005);
            fb.setMaxAge(400 + getLevel().getRandom().nextInt(100));
            fb.setRBGColorF(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
            fb.setAlphaF(1.0f, 0.0f);
            fb.setGridSize(8);
            fb.setParticles(24, 1, 1);
            fb.setScale(2.0f, 5.0f);
            fb.setLayer(0);
            fb.setSlowDown(1.0);
            fb.setWind(0.001);
            fb.setRotationSpeed(getLevel().getRandom().nextFloat(), getLevel().getRandom().nextBoolean() ? -1.0f : 1.0f);
            ParticleEngine.addEffect(getLevel(), fb);
        }
    }

    public void crucibleBubble(float x, float y, float z, float cr, float cg, float cb) {
        FXGeneric fb = new FXGeneric(getLevel(), x, y, z, 0.0, 0.0, 0.0);
        fb.setMaxAge(15 + getLevel().getRandom().nextInt(10));
        fb.setScale(getLevel().getRandom().nextFloat() * 0.3f + 0.3f);
        fb.setRBGColorF(cr, cg, cb);
        fb.setRandomMovementScale(0.002f, 0.002f, 0.002f);
        fb.setGravity(-0.001f);
        fb.setParticle(64);
        fb.setFinalFrames(65, 66, 66);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void crucibleBoil(BlockPos pos, TileCrucible tile, int j) {
        for (int a = 0; a < 2; ++a) {
            FXGeneric fb = new FXGeneric(getLevel(), pos.getX() + 0.2f + getLevel().getRandom().nextFloat() * 0.6f, pos.getY() + 0.1f + tile.getFluidHeight(), pos.getZ() + 0.2f + getLevel().getRandom().nextFloat() * 0.6f, 0.0, 0.002, 0.0);
            fb.setMaxAge((int)(7.0 + 8.0 / (Math.random() * 0.8 + 0.2)));
            fb.setScale(getLevel().getRandom().nextFloat() * 0.3f + 0.2f);
            if (tile.aspects.size() == 0) {
                fb.setRBGColorF(1.0f, 1.0f, 1.0f);
            }
            else {
                Color color = new Color(tile.aspects.getAspects()[getLevel().getRandom().nextInt(tile.aspects.getAspects().length)].getColor());
                fb.setRBGColorF(color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f);
            }
            fb.setRandomMovementScale(0.001f, 0.001f, 0.001f);
            fb.setGravity(-0.025f * j);
            fb.setParticle(64);
            fb.setFinalFrames(65, 66);
            ParticleEngine.addEffect(getLevel(), fb);
        }
    }

    public void crucibleFroth(float x, float y, float z) {
        FXGeneric fb = new FXGeneric(getLevel(), x, y, z, 0.0, 0.0, 0.0);
        fb.setMaxAge(4 + getLevel().getRandom().nextInt(3));
        fb.setScale(getLevel().getRandom().nextFloat() * 0.2f + 0.2f);
        fb.setRBGColorF(0.5f, 0.5f, 0.7f);
        fb.setRandomMovementScale(0.001f, 0.001f, 0.001f);
        fb.setGravity(0.1f);
        fb.setParticle(64);
        fb.setFinalFrames(65, 66);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void crucibleFrothDown(float x, float y, float z) {
        FXGeneric fb = new FXGeneric(getLevel(), x, y, z, 0.0, 0.0, 0.0);
        fb.setMaxAge(12 + getLevel().getRandom().nextInt(12));
        fb.setScale(getLevel().getRandom().nextFloat() * 0.2f + 0.4f);
        fb.setRBGColorF(0.25f, 0.0f, 0.75f);
        fb.setAlphaF(0.8f);
        fb.setRandomMovementScale(0.001f, 0.001f, 0.001f);
        fb.setGravity(0.05f);
        fb.setNoClip(false);
        fb.setParticle(73);
        fb.setFinalFrames(65, 66);
        fb.setLayer(1);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void drawBamf(BlockPos p, boolean sound, boolean flair, Direction side) {
        drawBamf(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, sound, flair, side);
    }

    public void drawPedestalShield(BlockPos pos) {
        FXShieldRunes fb = new FXShieldRunes(getLevel(), pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, null, 8, 0.0f, 90.0f);
        Minecraft.getInstance().particleEngine.add(fb);
    }

    public void drawBamf(BlockPos p, float r, float g, float b, boolean sound, boolean flair, Direction side) {
        drawBamf(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, r, g, b, sound, flair, side);
    }

    public void drawBamf(BlockPos p, int color, boolean sound, boolean flair, Direction side) {
        drawBamf(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5, color, sound, flair, side);
    }

    public void drawBamf(double x, double y, double z, int color, boolean sound, boolean flair, Direction side) {
        Color c = new Color(color);
        float r = c.getRed() / 255.0f;
        float g = c.getGreen() / 255.0f;
        float b = c.getBlue() / 255.0f;
        drawBamf(x, y, z, r, g, b, sound, flair, side);
    }

    public void drawBamf(double x, double y, double z, boolean sound, boolean flair, Direction side) {
        drawBamf(x, y, z, 0.5f, 0.1f, 0.6f, sound, flair, side);
    }

    public void drawBamf(double x, double y, double z, float r, float g, float b, boolean sound, boolean flair, Direction side) {
        if (sound) {
            getLevel().playLocalSound(x, y, z, SoundsTC.poof, SoundSource.BLOCKS, 0.4f, 1.0f + (float) getLevel().getRandom().nextGaussian() * 0.05f, false);
        }
        for (int a = 0; a < 6 + getLevel().getRandom().nextInt(3) + 2; ++a) {
            double vx = (0.05f + getLevel().getRandom().nextFloat() * 0.05f) * (getLevel().getRandom().nextBoolean() ? -1 : 1);
            double vy = (0.05f + getLevel().getRandom().nextFloat() * 0.05f) * (getLevel().getRandom().nextBoolean() ? -1 : 1);
            double vz = (0.05f + getLevel().getRandom().nextFloat() * 0.05f) * (getLevel().getRandom().nextBoolean() ? -1 : 1);
            if (side != null) {
                vx += side.getStepX() * 0.1f;
                vy += side.getStepY() * 0.1f;
                vz += side.getStepZ() * 0.1f;
            }
            FXGeneric fb2 = new FXGeneric(getLevel(), x + vx * 2.0, y + vy * 2.0, z + vz * 2.0, vx / 2.0, vy / 2.0, vz / 2.0);
            fb2.setMaxAge(20 + getLevel().getRandom().nextInt(15));
            fb2.setRBGColorF(Mth.clamp(r * (1.0f + (float) getLevel().getRandom().nextGaussian() * 0.1f), 0.0f, 1.0f), Mth.clamp(g * (1.0f + (float) getLevel().getRandom().nextGaussian() * 0.1f), 0.0f, 1.0f), Mth.clamp(b * (1.0f + (float) getLevel().getRandom().nextGaussian() * 0.1f), 0.0f, 1.0f));
            fb2.setAlphaF(1.0f, 0.1f);
            fb2.setGridSize(16);
            fb2.setParticles(123, 5, 1);
            fb2.setScale(3.0f, 4.0f + getLevel().getRandom().nextFloat() * 3.0f);
            fb2.setLayer(1);
            fb2.setSlowDown(0.7);
            fb2.setRotationSpeed(getLevel().getRandom().nextFloat(), getLevel().getRandom().nextBoolean() ? -1.0f : 1.0f);
            ParticleEngine.addEffect(getLevel(), fb2);
        }
        if (flair) {
            for (int a = 0; a < 2 + getLevel().getRandom().nextInt(3); ++a) {
                double vx = (0.025f + getLevel().getRandom().nextFloat() * 0.025f) * (getLevel().getRandom().nextBoolean() ? -1 : 1);
                double vy = (0.025f + getLevel().getRandom().nextFloat() * 0.025f) * (getLevel().getRandom().nextBoolean() ? -1 : 1);
                double vz = (0.025f + getLevel().getRandom().nextFloat() * 0.025f) * (getLevel().getRandom().nextBoolean() ? -1 : 1);
                drawWispyMotes(x + vx * 2.0, y + vy * 2.0, z + vz * 2.0, vx, vy, vz, 15 + getLevel().getRandom().nextInt(10), -0.01f);
            }
            FXGeneric fb3 = new FXGeneric(getLevel(), x, y, z, 0.0, 0.0, 0.0);
            fb3.setMaxAge(10 + getLevel().getRandom().nextInt(5));
            fb3.setRBGColorF(1.0f, 0.9f, 1.0f);
            fb3.setAlphaF(1.0f, 0.0f);
            fb3.setGridSize(16);
            fb3.setParticles(77, 1, 1);
            fb3.setScale(10.0f + getLevel().getRandom().nextFloat() * 2.0f, 0.0f);
            fb3.setLayer(0);
            fb3.setRotationSpeed(getLevel().getRandom().nextFloat(), (float) getLevel().getRandom().nextGaussian());
            ParticleEngine.addEffect(getLevel(), fb3);
        }
        for (int a = 0; a < (flair ? 2 : 0) + getLevel().getRandom().nextInt(3); ++a) {
            drawCurlyWisp(x, y, z, 0.0, 0.0, 0.0, 1.0f, (0.9f + getLevel().getRandom().nextFloat() * 0.1f + r) / 2.0f, (0.1f + g) / 2.0f, (0.5f + getLevel().getRandom().nextFloat() * 0.1f + b) / 2.0f, 0.75f, side, a, 0, 0);
        }
    }

    public void drawCurlyWisp(double x, double y, double z, double vx, double vy, double vz, float scale, float r, float g, float b, float a, Direction side, int seed, int layer, int delay) {
        if (getLevel() == null) {
            return;
        }
        vx += (0.0025f + getLevel().getRandom().nextFloat() * 0.005f) * (getLevel().getRandom().nextBoolean() ? -1 : 1);
        vy += (0.0025f + getLevel().getRandom().nextFloat() * 0.005f) * (getLevel().getRandom().nextBoolean() ? -1 : 1);
        vz += (0.0025f + getLevel().getRandom().nextFloat() * 0.005f) * (getLevel().getRandom().nextBoolean() ? -1 : 1);
        if (side != null) {
            vx += side.getStepX() * 0.025f;
            vy += side.getStepY() * 0.025f;
            vz += side.getStepZ() * 0.025f;
        }
        FXGeneric fb2 = new FXGeneric(getLevel(), x + vx * 5.0, y + vy * 5.0, z + vz * 5.0, vx, vy, vz);
        if (seed > 0 && getLevel().getRandom().nextBoolean()) {
            fb2.setAngles(90.0f * (float) getLevel().getRandom().nextGaussian(), 90.0f * (float) getLevel().getRandom().nextGaussian());
        }
        fb2.setMaxAge(25 + getLevel().getRandom().nextInt(20 + 20 * seed));
        fb2.setRBGColorF(r, g, b, 0.1f, 0.0f, 0.1f);
        fb2.setAlphaF(a, 0.0f);
        fb2.setGridSize(16);
        fb2.setParticles(60 + getLevel().getRandom().nextInt(4), 1, 1);
        fb2.setScale(5.0f * scale, (10.0f + getLevel().getRandom().nextFloat() * 4.0f) * scale);
        fb2.setLayer(layer);
        fb2.setRotationSpeed(getLevel().getRandom().nextFloat(), getLevel().getRandom().nextBoolean() ? (-2.0f - getLevel().getRandom().nextFloat() * 2.0f) : (2.0f + getLevel().getRandom().nextFloat() * 2.0f));
        ParticleEngine.addEffectWithDelay(getLevel(), fb2, delay);
    }

    public void pechsCurseTick(double x, double y, double z) {
        FXGeneric fb2 = new FXGeneric(getLevel(), x, y, z, 0.0, 0.0, 0.0);
        fb2.setAngles(90.0f * (float) getLevel().getRandom().nextGaussian(), 90.0f * (float) getLevel().getRandom().nextGaussian());
        fb2.setMaxAge(50 + getLevel().getRandom().nextInt(50));
        fb2.setRBGColorF(0.9f, 0.1f, 0.5f, 0.1f + getLevel().getRandom().nextFloat() * 0.1f, 0.0f, 0.5f + getLevel().getRandom().nextFloat() * 0.1f);
        fb2.setAlphaF(0.75f, 0.0f);
        fb2.setGridSize(8);
        fb2.setParticles(28 + getLevel().getRandom().nextInt(4), 1, 1);
        fb2.setScale(3.0f, 5.0f + getLevel().getRandom().nextFloat() * 2.0f);
        fb2.setLayer(0);
        fb2.setRotationSpeed(getLevel().getRandom().nextFloat(), getLevel().getRandom().nextBoolean() ? (-3.0f - getLevel().getRandom().nextFloat() * 3.0f) : (3.0f + getLevel().getRandom().nextFloat() * 3.0f));
        ParticleEngine.addEffect(getLevel(), fb2);
        drawWispyMotes(x, y, z, 0.0, 0.0, 0.0, 10 + getLevel().getRandom().nextInt(10), -0.01f);
    }

    public void scanHighlight(BlockPos p) {
        net.minecraft.world.phys.shapes.VoxelShape scanShape = getWorld().getBlockState(p).getShape(getLevel(), p);
        AABB bb = (scanShape.isEmpty() ? new AABB(0, 0, 0, 1, 1, 1) : scanShape.bounds()).move(p);
        scanHighlight(bb);
    }

    public void scanHighlight(Entity e) {
        AABB bb = e.getBoundingBox();
        scanHighlight(bb);
    }

    public void scanHighlight(AABB bb) {
        int num = Mth.ceil(((bb.getXsize() + bb.getYsize() + bb.getZsize()) / 3.0) * 2.0);
        double ax = (bb.minX + bb.maxX) / 2.0;
        double ay = (bb.minY + bb.maxY) / 2.0;
        double az = (bb.minZ + bb.maxZ) / 2.0;
        for (Direction face : Direction.values()) {
            double mx = 0.5 + face.getStepX() * 0.51;
            double my = 0.5 + face.getStepY() * 0.51;
            double mz = 0.5 + face.getStepZ() * 0.51;
            for (int a = 0; a < num * 2; ++a) {
                double x = mx;
                double y = my;
                double z = mz;
                x += getLevel().getRandom().nextGaussian() * (bb.maxX - bb.minX);
                y += getLevel().getRandom().nextGaussian() * (bb.maxY - bb.minY);
                z += getLevel().getRandom().nextGaussian() * (bb.maxZ - bb.minZ);
                x = Mth.clamp(x, bb.minX - ax, bb.maxX - ax);
                y = Mth.clamp(y, bb.minY - ay, bb.maxY - ay);
                z = Mth.clamp(z, bb.minZ - az, bb.maxZ - az);
                float r = (getLevel().getRandom().nextInt(17) + 16) / 255.0f;
                float g = (getLevel().getRandom().nextInt(34) + 132) / 255.0f;
                float b = (getLevel().getRandom().nextInt(17) + 223) / 255.0f;
                drawSimpleSparkle(getLevel().getRandom(), ax + x, ay + y, az + z, 0.0, 0.0, 0.0, 0.4f + (float) getLevel().getRandom().nextGaussian() * 0.1f, r, g, b, getLevel().getRandom().nextInt(10), 1.0f, 0.0f, 4);
            }
        }
    }

    public void sparkle(float x, float y, float z, float r, float g, float b) {
        if (getLevel().getRandom().nextInt(6) < 4) {
            drawGenericParticles(x, y, z, 0.0, 0.0, 0.0, r, g, b, 0.9f, true, 320, 16, 1, 6 + getLevel().getRandom().nextInt(4), 0, 0.6f + getLevel().getRandom().nextFloat() * 0.2f, 0.0f, 0);
        }
    }

    public void visSparkle(int x, int y, int z, int x2, int y2, int z2, int color) {
        FXVisSparkle fb = new FXVisSparkle(getLevel(), x + getLevel().getRandom().nextFloat(), y + getLevel().getRandom().nextFloat(), z + getLevel().getRandom().nextFloat(), x2 + 0.4 + getLevel().getRandom().nextFloat() * 0.2f, y2 + 0.4 + getLevel().getRandom().nextFloat() * 0.2f, z2 + 0.4 + getLevel().getRandom().nextFloat() * 0.2f);
        Color c = new Color(color);
        fb.setRBGColorF(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void voidStreak(double x, double y, double z, double x2, double y2, double z2, int seed, float scale) {
        FXVoidStream fb = new FXVoidStream(getLevel(), x, y, z, x2, y2, z2, seed, scale);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void splooshFX(Entity e) {
        float f = getLevel().getRandom().nextFloat() * 3.1415927f * 2.0f;
        float f2 = getLevel().getRandom().nextFloat() * 0.5f + 0.5f;
        float f3 = Mth.sin(f) * 2.0f * 0.5f * f2;
        float f4 = Mth.cos(f) * 2.0f * 0.5f * f2;
        FXBreakingFade fx = new FXBreakingFade(getLevel(), e.getX() + f3, e.getY() + getLevel().getRandom().nextFloat() * e.getBbHeight(), e.getZ() + f4, Items.SLIME_BALL, 0);
        if (getLevel().getRandom().nextBoolean()) {
            fx.setRBGColorF(0.6f, 0.0f, 0.3f);
            fx.setAlphaF(0.4f);
        }
        else {
            fx.setRBGColorF(0.3f, 0.0f, 0.3f);
            fx.setAlphaF(0.6f);
        }
        fx.setParticleMaxAge((int)(66.0f / (getLevel().getRandom().nextFloat() * 0.9f + 0.1f)));
        Minecraft.getInstance().particleEngine.add(fx);
    }

    public void taintsplosionFX(Entity e) {
        FXBreakingFade fx = new FXBreakingFade(getLevel(), e.getX(), e.getY() + getLevel().getRandom().nextFloat() * e.getBbHeight(), e.getZ(), Items.SLIME_BALL);
        if (getLevel().getRandom().nextBoolean()) {
            fx.setRBGColorF(0.6f, 0.0f, 0.3f);
            fx.setAlphaF(0.4f);
        }
        else {
            fx.setRBGColorF(0.3f, 0.0f, 0.3f);
            fx.setAlphaF(0.6f);
        }
        fx.setSpeed(Math.random() * 2.0 - 1.0, Math.random() * 2.0 - 1.0, Math.random() * 2.0 - 1.0);
        fx.boom();
        fx.setParticleMaxAge((int)(66.0f / (getLevel().getRandom().nextFloat() * 0.9f + 0.1f)));
        Minecraft.getInstance().particleEngine.add(fx);
    }

    public void tentacleAriseFX(Entity e) {
        for (int j = 0; j < 2.0f * e.getBbHeight(); ++j) {
            float f = getLevel().getRandom().nextFloat() * 3.1415927f * e.getBbHeight();
            float f2 = getLevel().getRandom().nextFloat() * 0.5f + 0.5f;
            float f3 = Mth.sin(f) * e.getBbHeight() * 0.25f * f2;
            float f4 = Mth.cos(f) * e.getBbHeight() * 0.25f * f2;
            FXBreakingFade fx = new FXBreakingFade(getLevel(), e.getX() + f3, e.getY(), e.getZ() + f4, Items.SLIME_BALL);
            fx.setRBGColorF(0.4f, 0.0f, 0.4f);
            fx.setAlphaF(0.5f);
            fx.setParticleMaxAge((int)(66.0f / (getLevel().getRandom().nextFloat() * 0.9f + 0.1f)));
            Minecraft.getInstance().particleEngine.add(fx);
        }
    }

    public void slimeJumpFX(Entity e, int i) {
        float f = getLevel().getRandom().nextFloat() * 3.1415927f * 2.0f;
        float f2 = getLevel().getRandom().nextFloat() * 0.5f + 0.5f;
        float f3 = Mth.sin(f) * i * 0.5f * f2;
        float f4 = Mth.cos(f) * i * 0.5f * f2;
        FXBreakingFade fx = new FXBreakingFade(getLevel(), e.getX() + f3, (e.getBoundingBox().minY + e.getBoundingBox().maxY) / 2.0, e.getZ() + f4, Items.SLIME_BALL, 0);
        fx.setRBGColorF(0.7f, 0.0f, 1.0f);
        fx.setAlphaF(0.4f);
        fx.setParticleMaxAge((int)(66.0f / (getLevel().getRandom().nextFloat() * 0.9f + 0.1f)));
        Minecraft.getInstance().particleEngine.add(fx);
    }

    public void taintLandFX(Entity e) {
        float f = getLevel().getRandom().nextFloat() * 3.1415927f * 2.0f;
        float f2 = getLevel().getRandom().nextFloat() * 0.5f + 0.5f;
        float f3 = Mth.sin(f) * 2.0f * 0.5f * f2;
        float f4 = Mth.cos(f) * 2.0f * 0.5f * f2;
        FXBreakingFade fx = new FXBreakingFade(getLevel(), e.getX() + f3, (e.getBoundingBox().minY + e.getBoundingBox().maxY) / 2.0, e.getZ() + f4, Items.SLIME_BALL);
        fx.setRBGColorF(0.1f, 0.0f, 0.1f);
        fx.setAlphaF(0.4f);
        fx.setParticleMaxAge((int)(66.0f / (getLevel().getRandom().nextFloat() * 0.9f + 0.1f)));
        Minecraft.getInstance().particleEngine.add(fx);
    }

    public void drawInfusionParticles1(double x, double y, double z, BlockPos pos, ItemStack stack) {
        FXBoreParticles fb = new FXBoreParticles(getLevel(), x, y, z, pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5, (float) getLevel().getRandom().nextGaussian() * 0.03f, (float) getLevel().getRandom().nextGaussian() * 0.03f, (float) getLevel().getRandom().nextGaussian() * 0.03f, stack).getObjectColor(pos);
        fb.setAlphaF(0.3f);
        Minecraft.getInstance().particleEngine.add(fb);
    }

    public void drawInfusionParticles2(double x, double y, double z, BlockPos pos, BlockState id, int md) {
        FXBoreParticles fb = new FXBoreParticles(getLevel(), x, y, z, pos.getX() + 0.5, pos.getY() - 0.5, pos.getZ() + 0.5, id, md).getObjectColor(pos);
        fb.setAlphaF(0.3f);
        Minecraft.getInstance().particleEngine.add(fb);
    }

    public void drawInfusionParticles3(double x, double y, double z, int x2, int y2, int z2) {
        FXBoreSparkle fb = new FXBoreSparkle(getLevel(), x, y, z, x2 + 0.5, y2 - 0.5, z2 + 0.5);
        fb.setRBGColorF(0.4f + getLevel().getRandom().nextFloat() * 0.2f, 0.2f, 0.6f + getLevel().getRandom().nextFloat() * 0.3f);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void drawInfusionParticles4(double x, double y, double z, int x2, int y2, int z2) {
        FXBoreSparkle fb = new FXBoreSparkle(getLevel(), x, y, z, x2 + 0.5, y2 - 0.5, z2 + 0.5);
        fb.setRBGColorF(0.2f, 0.6f + getLevel().getRandom().nextFloat() * 0.3f, 0.3f);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void drawVentParticles(double x, double y, double z, double x2, double y2, double z2, int color) {
        FXVent fb = new FXVent(getLevel(), x, y, z, x2, y2, z2, color);
        fb.setAlphaF(0.4f);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void drawVentParticles(double x, double y, double z, double x2, double y2, double z2, int color, float scale) {
        FXVent fb = new FXVent(getLevel(), x, y, z, x2, y2, z2, color);
        fb.setAlphaF(0.4f);
        fb.setScale(scale);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void drawVentParticles2(double x, double y, double z, double x2, double y2, double z2, int color, float scale) {
        FXVent2 fb = new FXVent2(getLevel(), x, y, z, x2, y2, z2, color);
        fb.setAlphaF(0.4f);
        fb.setScale(scale);
        ParticleEngine.addEffect(getLevel(), fb);
        if (getLevel().getRandom().nextInt(6) < 2) {
            drawGenericParticles(x, y, z, x2 / 2.0, y2 / 2.0, z2 / 2.0, 1.0f, 0.7f, 0.2f, 0.9f, true, 320, 16, 1, 10 + getLevel().getRandom().nextInt(4), 0, 0.25f + getLevel().getRandom().nextFloat() * 0.1f, 0.0f, 0);
        }
    }

    public void spark(double d, double e, double f, float size, float r, float g, float b, float a) {
        FXGeneric fb = new FXGeneric(getLevel(), d, e, f, 0.0, 0.0, 0.0);
        fb.setMaxAge(5 + getLevel().getRandom().nextInt(5));
        fb.setAlphaF(a);
        fb.setRBGColorF(r, g, b);
        fb.setGridSize(16);
        fb.setParticles(8 + getLevel().getRandom().nextInt(3) * 16, 8, 1);
        fb.setScale(size);
        fb.setFlipped(getLevel().getRandom().nextBoolean());
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void smokeSpiral(double x, double y, double z, float rad, int start, int miny, int color) {
        FXSmokeSpiral fx = new FXSmokeSpiral(getLevel(), x, y, z, rad, start, miny);
        Color c = new Color(color);
        fx.setRBGColorF(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);
        ParticleEngine.addEffect(getLevel(), fx);
    }

    public void wispFXEG(double x, double y, double z, Entity target) {
        for (int a = 0; a < 2; ++a) {
            FXWispEG ef = new FXWispEG(getLevel(), x, y, z, target);
            ParticleEngine.addEffect(getLevel(), ef);
        }
    }

    public void burst(double sx, double sy, double sz, float size) {
        FXGeneric fb = new FXGeneric(getLevel(), sx, sy, sz, 0.0, 0.0, 0.0);
        fb.setMaxAge(31);
        fb.setGridSize(16);
        fb.setParticles(208, 31, 1);
        fb.setScale(size);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void excavateFX(BlockPos pos, LivingEntity p, int progress) {
        Minecraft.getInstance().levelRenderer.destroyBlockProgress(p.getId(), pos, progress);
    }

    public Object beamCont(LivingEntity p, double tx, double ty, double tz, int type, int color, boolean reverse, float endmod, Object input, int impact) {
        FXBeamWand beamcon = null;
        Color c = new Color(color);
        if (input instanceof FXBeamWand) {
            beamcon = (FXBeamWand)input;
        }
        if (beamcon == null || !beamcon.isAlive()) {
            beamcon = new FXBeamWand(getLevel(), p, tx, ty, tz, c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 8);
            beamcon.setType(type);
            beamcon.setEndMod(endmod);
            beamcon.setReverse(reverse);
            Minecraft.getInstance().particleEngine.add(beamcon);
        }
        else {
            beamcon.updateBeam(tx, ty, tz);
            beamcon.setEndMod(endmod);
            beamcon.impact = impact;
        }
        return beamcon;
    }

    public Object beamBore(double px, double py, double pz, double tx, double ty, double tz, int type, int color, boolean reverse, float endmod, Object input, int impact) {
        FXBeamBore beamcon = null;
        Color c = new Color(color);
        if (input instanceof FXBeamBore) {
            beamcon = (FXBeamBore)input;
        }
        if (beamcon == null || !beamcon.isAlive()) {
            beamcon = new FXBeamBore(getLevel(), px, py, pz, tx, ty, tz, c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, 8);
            beamcon.setType(type);
            beamcon.setEndMod(endmod);
            beamcon.setReverse(reverse);
            Minecraft.getInstance().particleEngine.add(beamcon);
        }
        else {
            beamcon.updateBeam(px, py, pz, tx, ty, tz);
            beamcon.setEndMod(endmod);
            beamcon.impact = impact;
        }
        return beamcon;
    }

    public void boreDigFx(int x, int y, int z, Entity e, BlockState bi, int md, int delay) {
        float p = 50.0f;
        for (int a = 0; a < p / delay; ++a) {
            if (getLevel().getRandom().nextInt(4) == 0) {
                FXBoreSparkle fb = new FXBoreSparkle(getLevel(), x + getLevel().getRandom().nextFloat(), y + getLevel().getRandom().nextFloat(), z + getLevel().getRandom().nextFloat(), e);
                ParticleEngine.addEffect(getLevel(), fb);
            }
            else {
                FXBoreParticles fb2 = new FXBoreParticles(getLevel(), x + getLevel().getRandom().nextFloat(), y + getLevel().getRandom().nextFloat(), z + getLevel().getRandom().nextFloat(), e.getX(), e.getY(), e.getZ(), bi, md);
                fb2.setTarget(e);
                Minecraft.getInstance().particleEngine.add(fb2);
            }
        }
    }

    public void essentiaTrailFx(BlockPos p1, BlockPos p2, int count, int color, float scale, int ext) {
        FXEssentiaStream fb = new FXEssentiaStream(getLevel(), p1.getX() + 0.5, p1.getY() + 0.5, p1.getZ() + 0.5, p2.getX() + 0.5, p2.getY() + 0.5, p2.getZ() + 0.5, count, color, scale, ext, 0.0);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void boreTrailFx(BlockPos p1, Entity e, int count, int color, float scale, int ext) {
        FXBoreStream fb = new FXBoreStream(getLevel(), p1.getX() + 0.5, p1.getY() + 0.5, p1.getZ() + 0.5, e, count, color, scale, ext, 0.0);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void essentiaDropFx(double x, double y, double z, float r, float g, float b, float alpha) {
        FXGeneric fb = new FXGeneric(getLevel(), x, y, z, getLevel().getRandom().nextGaussian() * 0.004999999888241291, getLevel().getRandom().nextGaussian() * 0.004999999888241291, getLevel().getRandom().nextGaussian() * 0.004999999888241291);
        fb.setMaxAge(20 + getLevel().getRandom().nextInt(10));
        fb.setRBGColorF(r, g, b);
        fb.setAlphaF(alpha);
        fb.setLoop(false);
        fb.setParticles(25, 1, 1);
        fb.setScale(0.4f + getLevel().getRandom().nextFloat() * 0.2f, 0.2f);
        fb.setLayer(1);
        fb.setGravity(0.01f);
        fb.setRotationSpeed(0.0f);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void jarSplashFx(double x, double y, double z) {
        FXGeneric fb = new FXGeneric(getLevel(), x + getLevel().getRandom().nextGaussian() * 0.07500000298023224, y, z + getLevel().getRandom().nextGaussian() * 0.07500000298023224, getLevel().getRandom().nextGaussian() * 0.014999999664723873, 0.075f + getLevel().getRandom().nextFloat() * 0.05f, getLevel().getRandom().nextGaussian() * 0.014999999664723873);
        fb.setMaxAge(20 + getLevel().getRandom().nextInt(10));
        Color c = new Color(2650102);
        fb.setRBGColorF(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);
        fb.setAlphaF(0.5f);
        fb.setLoop(false);
        fb.setParticles(73, 1, 1);
        fb.setScale(0.4f + getLevel().getRandom().nextFloat() * 0.3f, 0.0f);
        fb.setLayer(1);
        fb.setGravity(0.3f);
        fb.setRotationSpeed(0.0f);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void waterTrailFx(BlockPos p1, BlockPos p2, int count, int color, float scale) {
        FXEssentiaStream fb = new FXEssentiaStream(getLevel(), p1.getX() + 0.5, p1.getY() + 0.66, p1.getZ() + 0.5, p2.getX() + 0.5, p2.getY() + 0.5, p2.getZ() + 0.5, count, color, scale, 0, 0.2);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void furnaceLavaFx(int x, int y, int z, int facingX, int facingZ) {
        // ParticleLava.Factory removed in MC 26 - stub
    }

    public void blockRunes(double x, double y, double z, float r, float g, float b, int dur, float grav) {
        FXBlockRunes fb = new FXBlockRunes(getLevel(), x + 0.5, y + 0.5, z + 0.5, r, g, b, dur);
        fb.setGravity(grav);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void blockRunes2(double x, double y, double z, float r, float g, float b, int dur, float grav) {
        FXBlockRunes fb = new FXBlockRunes(getLevel(), x + 0.5, y + 0.5, z + 0.5, r, g, b, dur);
        fb.setGravity(grav);
        fb.setScale((float)(0.5 + getLevel().getRandom().nextGaussian() * 0.10000000149011612));
        fb.setOffsetX(0.0);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void drawSlash(double x, double y, double z, double x2, double y2, double z2, int dur) {
        FXPlane fb = new FXPlane(getLevel(), x, y, z, x2, y2, z2, dur);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void blockWard(double x, double y, double z, Direction side, float f, float f1, float f2) {
        FXBlockWard fb = new FXBlockWard(getLevel(), x + 0.5, y + 0.5, z + 0.5, side, f, f1, f2);
        Minecraft.getInstance().particleEngine.add(fb);
    }

    public FXSwarm swarmParticleFX(Entity targetedEntity, float f1, float f2, float pg) {
        FXSwarm fx = new FXSwarm(getLevel(), targetedEntity.getX() + (getLevel().getRandom().nextFloat() - getLevel().getRandom().nextFloat()) * 2.0f, targetedEntity.getY() + (getLevel().getRandom().nextFloat() - getLevel().getRandom().nextFloat()) * 2.0f, targetedEntity.getZ() + (getLevel().getRandom().nextFloat() - getLevel().getRandom().nextFloat()) * 2.0f, targetedEntity, 0.8f + getLevel().getRandom().nextFloat() * 0.2f, getLevel().getRandom().nextFloat() * 0.4f, 1.0f - getLevel().getRandom().nextFloat() * 0.2f, f1, f2, pg);
        ParticleEngine.addEffect(getLevel(), fx);
        return fx;
    }

    public void bottleTaintBreak(double x, double y, double z) {
        getLevel().playLocalSound(x, y, z, SoundEvents.SPLASH_POTION_BREAK, SoundSource.NEUTRAL, 1.0f, getLevel().getRandom().nextFloat() * 0.1f + 0.9f, false);
    }

    public void arcLightning(double x, double y, double z, double tx, double ty, double tz, float r, float g, float b, float h) {
        if (h <= 0.0f) {
            h = 0.1f;
        }
        FXArc efa = new FXArc(getLevel(), x, y, z, tx, ty, tz, r, g, b, h);
        Minecraft.getInstance().particleEngine.add(efa);
    }

    public void arcBolt(double x, double y, double z, double tx, double ty, double tz, float r, float g, float b, float width) {
        FXBolt efa = new FXBolt(getLevel(), x, y, z, tx, ty, tz, r, g, b, width);
        Minecraft.getInstance().particleEngine.add(efa);
    }

    public void cultistSpawn(double x, double y, double z, double a, double b, double c) {
        FXGeneric fb = new FXGeneric(getLevel(), x, y, z, a, b, c);
        fb.setMaxAge(10 + getLevel().getRandom().nextInt(10));
        fb.setRBGColorF(1.0f, 1.0f, 1.0f, 0.6f, 0.0f, 0.0f);
        fb.setAlphaF(0.8f);
        fb.setGridSize(16);
        fb.setParticles(160, 6, 1);
        fb.setScale(3.0f + getLevel().getRandom().nextFloat() * 2.0f);
        fb.setLayer(1);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void drawWispyMotesEntity(double x, double y, double z, Entity e, float r, float g, float b) {
        FXGenericP2E fb = new FXGenericP2E(getLevel(), x, y, z, e);
        fb.setRBGColorF(r, g, b);
        fb.setAlphaF(0.6f);
        fb.setParticles(512, 16, 1);
        fb.setLoop(true);
        fb.setWind(0.001);
        fb.setRandomMovementScale(0.0025f, 0.0f, 0.0025f);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void drawWispParticles(double x, double y, double z, double x2, double y2, double z2, int color, int a) {
        FXGeneric fb = new FXGeneric(getLevel(), x, y, z, x2, y2, z2);
        fb.setMaxAge(10 + getLevel().getRandom().nextInt(5));
        Color c = new Color(color);
        fb.setRBGColorF(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);
        fb.setAlphaF(0.5f);
        fb.setLoop(true);
        fb.setGridSize(64);
        fb.setParticles(264, 8, 1);
        fb.setScale(1.0f + getLevel().getRandom().nextFloat() * 0.25f, 0.05f);
        fb.setWind(2.5E-4);
        fb.setRandomMovementScale(0.0025f, 0.0f, 0.0025f);
        ParticleEngine.addEffectWithDelay(getLevel(), fb, a);
    }

    public void drawNitorCore(double x, double y, double z, double x2, double y2, double z2) {
        FXGeneric fb = new FXGeneric(getLevel(), x, y, z, x2, y2, z2);
        fb.setMaxAge(10);
        fb.setRBGColorF(1.0f, 1.0f, 1.0f);
        fb.setAlphaF(1.0f);
        fb.setParticles(457, 1, 1);
        fb.setScale(1.0f, 1.0f + (float) getLevel().getRandom().nextGaussian() * 0.1f, 1.0f);
        fb.setLayer(1);
        fb.setRandomMovementScale(2.0E-4f, 2.0E-4f, 2.0E-4f);
        ParticleEngine.addEffect(getLevel(), fb);
    }

    public void drawNitorFlames(double x, double y, double z, double x2, double y2, double z2, int color, int a) {
        FXGeneric fb = new FXGeneric(getLevel(), x, y, z, x2, y2, z2);
        fb.setMaxAge(10 + getLevel().getRandom().nextInt(5));
        Color c = new Color(color);
        fb.setRBGColorF(c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f);
        fb.setAlphaF(0.66f);
        fb.setLoop(true);
        fb.setGridSize(64);
        fb.setParticles(264, 8, 1);
        fb.setScale(3.0f + getLevel().getRandom().nextFloat(), 0.05f);
        fb.setRandomMovementScale(0.0025f, 0.0f, 0.0025f);
        ParticleEngine.addEffectWithDelay(getLevel(), fb, a);
    }

    static {
        FXDispatcher.INSTANCE = new FXDispatcher();
        FXDispatcher.q = 0;
    }

    public static class GenPart
    {
        public int grid;
        public int age;
        public float redStart;
        public float greenStart;
        public float blueStart;
        public float redEnd;
        public float greenEnd;
        public float blueEnd;
        public float[] alpha;
        public float[] scale;
        public float rot;
        public float rotstart;
        public boolean loop;
        public int partStart;
        public int partNum;
        public int partInc;
        public int layer;
        public double slowDown;
        public float grav;
        public int delay;

        public GenPart() {
            grid = 64;
            age = 0;
            redStart = 1.0f;
            greenStart = 1.0f;
            blueStart = 1.0f;
            redEnd = 1.0f;
            greenEnd = 1.0f;
            blueEnd = 1.0f;
            alpha = new float[] { 1.0f };
            scale = new float[] { 1.0f };
            rotstart = 0.0f;
            loop = false;
            partStart = 0;
            partNum = 1;
            partInc = 1;
            layer = 0;
            slowDown = 0.9800000190734863;
            grav = 0.0f;
            delay = 0;
        }
    }
}
