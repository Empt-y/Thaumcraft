package thaumcraft.common.world.aura;
import java.lang.ref.WeakReference;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;


public class AuraChunk
{
    ChunkPos loc;
    short base;
    float vis;
    float flux;
    WeakReference<LevelChunk> chunkRef;
    
    public AuraChunk(ChunkPos loc) {
        this.loc = loc;
    }
    
    public AuraChunk(LevelChunk chunk, short base, float vis, float flux) {
        if (chunk != null) {
            loc = chunk.getPos();
            chunkRef = new WeakReference<LevelChunk>(chunk);
        }
        this.base = base;
        this.vis = vis;
        this.flux = flux;
    }
    
    public boolean isModified() {
        return chunkRef != null && chunkRef.get() != null && chunkRef.get().isUnsaved();
    }
    
    public short getBase() {
        return base;
    }
    
    public void setBase(short base) {
        this.base = base;
    }
    
    public float getVis() {
        return vis;
    }
    
    public void setVis(float vis) {
        this.vis = Math.min(32766.0f, Math.max(0.0f, vis));
    }
    
    public float getFlux() {
        return flux;
    }
    
    public void setFlux(float flux) {
        this.flux = Math.min(32766.0f, Math.max(0.0f, flux));
    }
    
    public ChunkPos getLoc() {
        return loc;
    }
    
    public void setLoc(ChunkPos loc) {
        this.loc = loc;
    }
}
