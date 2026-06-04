package thaumcraft.client.fx.particles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import thaumcraft.client.fx.ParticleEngine;


public class FXGeneric extends SingleQuadParticle
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
    int particleTextureIndexX;
    int particleTextureIndexY;

    private static TextureAtlasSprite getOrFallback(int index) {
        TextureAtlasSprite s = ParticleEngine.getSprite(index);
        if (s != null) return s;
        // Return any available sprite as a placeholder
        s = ParticleEngine.getSprite(56);
        if (s != null) return s;
        // No sprites loaded yet - return first available
        if (!ParticleEngine.particleSprites.isEmpty()) {
            return ParticleEngine.particleSprites.values().iterator().next();
        }
        return null;
    }

    public FXGeneric(Level world, double x, double y, double z, double xx, double yy, double zz) {
        super((ClientLevel) world, x, y, z, xx, yy, zz, getOrFallback(56));
        initDefaults(x, y, z, xx, yy, zz);
    }

    public FXGeneric(Level world, double x, double y, double z) {
        super((ClientLevel) world, x, y, z, 0.0, 0.0, 0.0, getOrFallback(56));
        initDefaults(x, y, z, 0.0, 0.0, 0.0);
    }

    private void initDefaults(double x, double y, double z, double xx, double yy, double zz) {
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
        quadSize = 0.1f;
        rCol = 1.0f;
        gCol = 1.0f;
        bCol = 1.0f;
        alpha = 1.0f;
    }

    void calculateFrames() {
        doneFrames = true;
        alphaFrames = new float[lifetime + 1];
        float inc = alphaKeys.length < 2 ? 0 : (alphaKeys.length - 1.0f) / lifetime;
        float is = 0.0f;
        for (int a = 0; a <= lifetime; ++a) {
            int isF = Mth.floor(is);
            if (isF >= alphaKeys.length) isF = alphaKeys.length - 1;
            float diff = (isF < alphaKeys.length - 1) ? (alphaKeys[isF + 1] - alphaKeys[isF]) : 0.0f;
            alphaFrames[a] = alphaKeys[isF] + diff * (is - isF);
            is += inc;
        }
        scaleFrames = new float[lifetime + 1];
        inc = scaleKeys.length < 2 ? 0 : (scaleKeys.length - 1.0f) / lifetime;
        is = 0.0f;
        for (int a = 0; a <= lifetime; ++a) {
            int isF = Mth.floor(is);
            if (isF >= scaleKeys.length) isF = scaleKeys.length - 1;
            float diff = (isF < scaleKeys.length - 1) ? (scaleKeys[isF + 1] - scaleKeys[isF]) : 0.0f;
            scaleFrames[a] = scaleKeys[isF] + diff * (is - isF);
            is += inc;
        }
    }

    @Override
    public void tick() {
        if (!doneFrames) calculateFrames();
        xo = x;
        yo = y;
        zo = z;
        if (age >= lifetime) {
            remove();
            return;
        }

        // Update animation frame
        int currentParticle;
        if (numParticles <= 1) {
            currentParticle = startParticle;
        } else {
            currentParticle = startParticle + (age * numParticles / lifetime % numParticles) * particleInc;
        }
        TextureAtlasSprite s = ParticleEngine.getSprite(currentParticle);
        if (s != null) this.sprite = s;

        // Update scale and alpha from keyframe curves
        int a = Math.min(age, scaleFrames.length - 1);
        quadSize = scaleFrames[a];
        alpha = alphaFrames[Math.min(age, alphaFrames.length - 1)];

        age++;
        oRoll = roll;
        roll += (float)(Math.PI * 2.0 * rotationSpeed);
        gravity -= 0.04 * this.gravity;
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

    @Override
    protected Layer getLayer() {
        return Layer.TRANSLUCENT;
    }

    @Override
    public ParticleRenderType getGroup() {
        return sprite != null ? ParticleRenderType.SINGLE_QUADS : ParticleRenderType.NO_RENDER;
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
        roll = (float)(start * Math.PI * 2.0);
        rotationSpeed = (float)(rot * 0.017453292519943);
    }

    public void setMaxAge(int max) { lifetime = max; }

    public void setParticles(int startParticle, int numParticles, int particleInc) {
        this.numParticles = numParticles;
        this.particleInc = particleInc;
        this.startParticle = startParticle;
        TextureAtlasSprite s = ParticleEngine.getSprite(startParticle);
        if (s != null) this.sprite = s;
    }

    public void setParticle(int startParticle) {
        numParticles = 1;
        particleInc = 1;
        this.startParticle = startParticle;
        TextureAtlasSprite s = ParticleEngine.getSprite(startParticle);
        if (s != null) this.sprite = s;
    }

    public void setScale(float... scale) {
        quadSize = scale[0];
        scaleKeys = scale;
    }

    public void setAlphaF(float... a1) {
        alpha = a1[0];
        alphaKeys = a1;
    }

    public void setAlphaF(float a1) {
        alpha = a1;
        alphaKeys = new float[] { a1 };
    }

    public void setSlowDown(double slowDown) { this.slowDown = slowDown; }
    public void setRandomX(float x) { randomX = x; }
    public void setRandomY(float y) { randomY = y; }
    public void setRandomZ(float z) { randomZ = z; }
    public void setFinalFrames(int... frames) { finalFrames = frames; }

    public void setAngles(float yaw, float pitch) {
        angleYaw = yaw; anglePitch = pitch; angled = true;
    }

    public void setGravity(float g) { gravity = g; }

    public void setParticleTextureIndex(int index) {
        if (index < 0) index = 0;
        particleTextureIndexX = index % gridSize;
        particleTextureIndexY = index / gridSize;
        TextureAtlasSprite s = ParticleEngine.getSprite(index);
        if (s != null) this.sprite = s;
    }

    public void setGridSize(int gridSize) { this.gridSize = gridSize; }

    public void setParticleMaxAge(int max) { lifetime = max; }

    public void setNoClip(boolean clip) { hasPhysics = clip; }

    public void setRandomMovementScale(float x, float y, float z) {
        randomX = x; randomY = y; randomZ = z;
    }

    // Compatibility aliases for subclasses that used old field names
    /** @deprecated use {@code gravity} directly */
    protected float getParticleGravity() { return gravity; }
    /** @deprecated use {@code gravity} directly */
    protected void setParticleGravity(float g) { gravity = g; }
    /** @deprecated use {@code quadSize} directly */
    protected float getParticleScale() { return quadSize; }
    /** @deprecated use {@code quadSize} directly */
    protected void setParticleScaleField(float s) { quadSize = s; }
}
