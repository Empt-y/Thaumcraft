package thaumcraft.api.golems.parts;
import net.minecraft.resources.Identifier;
import net.minecraft.client.resources.language.I18n;
import thaumcraft.api.golems.EnumGolemTrait;


public class GolemLeg
{
    protected static GolemLeg[] legs;
    public byte id;
    public String key;
    public String[] research;
    public Identifier icon;
    public Object[] components;
    public EnumGolemTrait[] traits;
    public ILegFunction function;
    public PartModel model;
    private static byte lastID;
    
    public GolemLeg(String key, String[] research, Identifier icon, PartModel model, Object[] comp, EnumGolemTrait[] tags) {
        this.key = key;
        this.research = research;
        this.icon = icon;
        components = comp;
        traits = tags;
        this.model = model;
        function = null;
    }
    
    public GolemLeg(String key, String[] research, Identifier icon, PartModel model, Object[] comp, ILegFunction function, EnumGolemTrait[] tags) {
        this(key, research, icon, model, comp, tags);
        this.function = function;
    }
    
    public static void register(GolemLeg thing) {
        thing.id = GolemLeg.lastID;
        ++GolemLeg.lastID;
        if (thing.id >= GolemLeg.legs.length) {
            GolemLeg[] temp = new GolemLeg[thing.id + 1];
            System.arraycopy(GolemLeg.legs, 0, temp, 0, GolemLeg.legs.length);
            GolemLeg.legs = temp;
        }
        GolemLeg.legs[thing.id] = thing;
    }
    
    public String getLocalizedName() {
        return I18n.get("golem.leg." + key.toLowerCase());
    }
    
    public String getLocalizedDescription() {
        return I18n.get("golem.leg.text." + key.toLowerCase());
    }
    
    public static GolemLeg[] getLegs() {
        return GolemLeg.legs;
    }
    
    static {
        GolemLeg.legs = new GolemLeg[1];
        GolemLeg.lastID = 0;
    }
    
    public interface ILegFunction extends IGenericFunction
    {
    }
}
