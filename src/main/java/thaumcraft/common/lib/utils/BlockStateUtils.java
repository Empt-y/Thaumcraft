package thaumcraft.common.lib.utils;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;


public class BlockStateUtils
{
    public static Direction getFacing(BlockState state) {
        if (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING)) {
            return state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
        }
        if (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING)) {
            return state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING);
        }
        return Direction.NORTH;
    }

    public static Direction getFacing(int meta) {
        return Direction.from3DDataValue(meta & 0x7);
    }

    public static boolean isEnabled(BlockState state) {
        if (state.hasProperty(net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED)) {
            return state.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED);
        }
        return true;
    }

    public static boolean isEnabled(int meta) {
        return (meta & 0x8) != 0x8;
    }

    public static int getBlockMetadata(BlockState state) {
        return 0; // Metadata removed in modern MC
    }
    
    public static Property getPropertyByName(BlockState blockState, String propertyName) {
        for (Property property : blockState.getProperties()) {
            if (property.getName().equals(propertyName)) {
                return property;
            }
        }
        return null;
    }
    
    public static boolean isValidPropertyName(BlockState blockState, String propertyName) {
        return getPropertyByName(blockState, propertyName) != null;
    }
    
    public static Comparable getPropertyValueByName(BlockState blockState, Property<? extends Comparable> property, String valueName) {
        for (Comparable value : property.getPossibleValues()) {
            if (value.toString().equals(valueName)) {
                return value;
            }
        }
        return null;
    }
    
    public static ImmutableSet<BlockState> getValidStatesForProperties(BlockState baseState, Property... properties) {
        if (properties == null) {
            return null;
        }
        Set<BlockState> validStates = Sets.newHashSet();
        PropertyIndexer propertyIndexer = new PropertyIndexer(properties);
        do {
            BlockState currentState = baseState;
            for (Property property : properties) {
                IndexedProperty indexedProperty = propertyIndexer.getIndexedProperty(property);
                currentState = currentState.setValue(property, indexedProperty.getCurrentValue());
            }
            validStates.add(currentState);
        } while (propertyIndexer.increment());
        return (ImmutableSet<BlockState>)ImmutableSet.copyOf((Collection)validStates);
    }
    
    private static class PropertyIndexer
    {
        private HashMap<Property, IndexedProperty> indexedProperties;
        private Property finalProperty;
        
        private PropertyIndexer(Property... properties) {
            indexedProperties = new HashMap<Property, IndexedProperty>();
            finalProperty = properties[properties.length - 1];
            IndexedProperty previousIndexedProperty = null;
            for (Property property : properties) {
                IndexedProperty indexedProperty = new IndexedProperty(property);
                if (previousIndexedProperty != null) {
                    indexedProperty.parent = previousIndexedProperty;
                    previousIndexedProperty.child = indexedProperty;
                }
                indexedProperties.put(property, indexedProperty);
                previousIndexedProperty = indexedProperty;
            }
        }
        
        public boolean increment() {
            return indexedProperties.get(finalProperty).increment();
        }
        
        public IndexedProperty getIndexedProperty(Property property) {
            return indexedProperties.get(property);
        }
    }
    
    private static class IndexedProperty
    {
        private ArrayList<Comparable> validValues;
        private int maxCount;
        private int counter;
        private IndexedProperty parent;
        private IndexedProperty child;
        
        private IndexedProperty(Property property) {
            (validValues = new ArrayList<Comparable>()).addAll(property.getPossibleValues());
            maxCount = validValues.size() - 1;
        }
        
        public boolean increment() {
            if (counter < maxCount) {
                ++counter;
                return true;
            }
            if (hasParent()) {
                resetSelfAndChildren();
                return parent.increment();
            }
            return false;
        }
        
        public void resetSelfAndChildren() {
            counter = 0;
            if (hasChild()) {
                child.resetSelfAndChildren();
            }
        }
        
        public boolean hasParent() {
            return parent != null;
        }
        
        public boolean hasChild() {
            return child != null;
        }
        
        public int getCounter() {
            return counter;
        }
        
        public int getMaxCount() {
            return maxCount;
        }
        
        public Comparable getCurrentValue() {
            return validValues.get(counter);
        }
    }
}
