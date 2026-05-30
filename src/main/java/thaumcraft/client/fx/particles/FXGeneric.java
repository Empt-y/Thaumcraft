package thaumcraft.client.fx.particles;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;


public class FXGeneric extends Particle
{
    boolean doneFrames;
    boolean flipped;
    double windX;
    double windZ;
    int layer;
    float dr;
    float dg;
    float db;
    boolean loop;
    float rotationSpeed;
    int startParticle;
    int numParticles;
    int particleInc;
    float[] scaleKeys;
    float[] scaleFrames;
    float[] alphaKeys;
    float[] alphaFrames;
    double slowDown;
    float randomX;
    float randomY;
    float randomZ;
    int[] finalFrames;
    boolean angled;
    float angleYaw;
    float anglePitch;
    int gridSize;
    // MC 26: declare fields not present in Particle base
    protected float rCol;
    protected float gCol;
    protected float bCol;
    protected float particleScale;
    protected float particleAlpha;
    protected float particleGravity;
    protected float particleAngle;
    protected float prevParticleAngle;
    protected int particleTextureIndexX;
    protected int particleTextureIndexY;
    public FXGeneric(Level world, double x, double y, double z, double xx, double yy, double zz) {
        super((ClientLevel) world, x, y, z, xx, yy, zz);
        doneFrames = false;
        flipped = false;
        layer = 0;
        dr = 0.0f;
        dg = 0.0f;
        db = 0.0f;
        loop = false;
        rotationSpeed = 0.0f;
        startParticle = 0;
        numParticles = 1;
        particleInc = 1;
        scaleKeys = new float[] { 1.0f };
        scaleFrames = new float[] { 0.0f };
        alphaKeys = new float[] { 1.0f };
        alphaFrames = new float[] { 0.0f };
        slowDown = 0.9800000190734863;
        finalFrames = null;
        angled = false;
        gridSize = 64;
        xo = x;
        yo = y;
        zo = z;
        xd = xx;
        yd = yy;
        zd = zz;
    }

    public FXGeneric(Level world, double x, double y, double z) {
        super((ClientLevel) world, x, y, z, 0.0, 0.0, 0.0);
        doneFrames = false;
        flipped = false;
        layer = 0;
        dr = 0.0f;
        dg = 0.0f;
        db = 0.0f;
        loop = false;
        rotationSpeed = 0.0f;
        startParticle = 0;
        numParticles = 1;
        particleInc = 1;
        scaleKeys = new float[] { 1.0f };
        scaleFrames = new float[] { 0.0f };
        alphaKeys = new float[] { 1.0f };
        alphaFrames = new float[] { 0.0f };
        slowDown = 0.9800000190734863;
        finalFrames = null;
        angled = false;
        gridSize = 64;
        xo = x;
        yo = y;
        zo = z;
    }

    void calculateFrames() {
        doneFrames = true;
        if (alphaKeys == null) {
            particleAlpha = 1.0f;
        }
        alphaFrames = new float[lifetime + 1];
        float inc = (alphaKeys.length - 1) / (float) lifetime;
        float is = 0.0f;
        for (int a = 0; a <= lifetime; ++a) {
            int isF = Mth.floor(is);
            float diff = (isF < alphaKeys.length - 1) ? (alphaKeys[isF + 1] - alphaKeys[isF]) : 0.0f;
            alphaFrames[a] = alphaKeys[isF] + diff * (is - isF);
            is += inc;
        }
        if (scaleKeys == null) {
            particleScale = 1.0f;
        }
        scaleFrames = new float[lifetime + 1];
        inc = (scaleKeys.length - 1) / (float) lifetime;
        is = 0.0f;
        for (int a = 0; a <= lifetime; ++a) {
            int isF = Mth.floor(is);
            float diff = (isF < scaleKeys.length - 1) ? (scaleKeys[isF + 1] - scaleKeys[isF]) : 0.0f;
            scaleFrames[a] = scaleKeys[isF] + diff * (is - isF);
            is += inc;
        }
    }

    @Override
    public void tick() {
        if (!doneFrames) {
            calculateFrames();
        }
        xo = x;
        yo = y;
        zo = z;
        if (age++ >= lifetime) {
            remove();
            return;
        }
        prevParticleAngle = particleAngle;
        particleAngle += 3.1415927f * rotationSpeed * 2.0f;
        yd -= 0.04 * particleGravity;
        move(xd, yd, zd);
        xd *= slowDown;
        yd *= slowDown;
        zd *= slowDown;
        xd += random.nextGaussian() * randomX;
        yd += random.nextGaussian() * randomY;
        zd += random.nextGaussian() * randomZ;
        xd += windX;
        zd += windZ;
        if (onGround && slowDown != 1.0) {
            xd *= 0.699999988079071;
            zd *= 0.699999988079071;
        }
    }

    public void render(VertexConsumer buffer, Camera camera, float partialTick) {
        // Rendering stub - FXGeneric visual not ported to MC 26 render API
    }

    @Override
    public ParticleRenderType getGroup() {
        return ParticleRenderType.NO_RENDER;
    }

    public boolean isFlipped() { return flipped; }
    public void setFlipped(boolean flip) { flipped = flip; }

    public void setWind(double d) {
        double angle = net.minecraft.util.RandomSource.create().nextInt(360) / 180.0 * Math.PI;
        windX = Math.cos(angle) * d * 0.1;
        windZ = Math.sin(angle) * d * 0.1;
    }

    public void setLayer(int layer) { this.layer = layer; }

    public void setRBGColorF(float r, float g, float b) {
        rCol = r; gCol = g; bCol = b;
        dr = r; dg = g; db = b;
    }

    public void setRBGColorF(float r, float g, float b, float r2, float g2, float b2) {
        rCol = r; gCol = g; bCol = b;
        dr = r2; dg = g2; db = b2;
    }

    public int getFXLayer() { return layer; }
    public void setLoop(boolean loop) { this.loop = loop; }

    public void setRotationSpeed(float rot) {
        rotationSpeed = (float)(rot * 0.017453292519943);
    }

    public void setRotationSpeed(float start, float rot) {
        particleAngle = (float)(start * 3.141592653589793 * 2.0);
        rotationSpeed = (float)(rot * 0.017453292519943);
    }

    public void setMaxAge(int max) { lifetime = max; }

    public void setParticles(int startParticle, int numParticles, int particleInc) {
        this.numParticles = numParticles;
        this.particleInc = particleInc;
        setParticleTextureIndex(this.startParticle = startParticle);
    }

    public void setParticle(int startParticle) {
        numParticles = 1;
        particleInc = 1;
        setParticleTextureIndex(this.startParticle = startParticle);
    }

    public void setScale(float... scale) {
        particleScale = scale[0];
        scaleKeys = scale;
    }

    public void setAlphaF(float... a1) {
        particleAlpha = a1[0];
        alphaKeys = a1;
    }

    public void setAlphaF(float a1) {
        particleAlpha = a1;
        alphaKeys = new float[] { a1 };
    }

    public void setSlowDown(double slowDown) { this.slowDown = slowDown; }

    public void setRandomMovementScale(float x, float y, float z) {
        randomX = x; randomY = y; randomZ = z;
    }

    public void setFinalFrames(int... frames) { finalFrames = frames; }

    public void setAngles(float yaw, float pitch) {
        angleYaw = yaw; anglePitch = pitch; angled = true;
    }

    public void setGravity(float g) { particleGravity = g; }

    public void setParticleTextureIndex(int index) {
        if (index < 0) index = 0;
        particleTextureIndexX = index % gridSize;
        particleTextureIndexY = index / gridSize;
    }

    public void setGridSize(int gridSize) { this.gridSize = gridSize; }

    public void setNoClip(boolean clip) { hasPhysics = clip; }
}
