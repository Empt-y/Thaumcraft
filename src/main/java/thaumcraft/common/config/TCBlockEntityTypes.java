package thaumcraft.common.config;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.common.tiles.crafting.*;
import thaumcraft.common.tiles.devices.*;
import thaumcraft.common.tiles.essentia.*;
import thaumcraft.common.tiles.misc.*;

@SuppressWarnings("unchecked")
public class TCBlockEntityTypes {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Thaumcraft.MODID);

    private static final Map<Class<?>, DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>>> CLASS_TO_TYPE = new HashMap<>();

    @FunctionalInterface
    public interface TileFactory<T extends BlockEntity> {
        T create(BlockEntityType<T> type, BlockPos pos, BlockState state);
    }

    // Crafting
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileArcaneWorkbench>> ARCANE_WORKBENCH =
            reg("arcane_workbench", TileArcaneWorkbench.class, TileArcaneWorkbench::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileCrucible>> CRUCIBLE =
            reg("crucible", TileCrucible.class, TileCrucible::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileFocalManipulator>> FOCAL_MANIPULATOR =
            reg("focal_manipulator", TileFocalManipulator.class, TileFocalManipulator::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileGolemBuilder>> GOLEM_BUILDER =
            reg("golem_builder", TileGolemBuilder.class, TileGolemBuilder::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileInfusionMatrix>> INFUSION_MATRIX =
            reg("infusion_matrix", TileInfusionMatrix.class, TileInfusionMatrix::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TilePatternCrafter>> PATTERN_CRAFTER =
            reg("pattern_crafter", TilePatternCrafter.class, TilePatternCrafter::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TilePedestal>> PEDESTAL =
            reg("pedestal", TilePedestal.class, TilePedestal::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileResearchTable>> RESEARCH_TABLE =
            reg("research_table", TileResearchTable.class, TileResearchTable::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileThaumatorium>> THAUMATORIUM =
            reg("thaumatorium", TileThaumatorium.class, TileThaumatorium::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileThaumatoriumTop>> THAUMATORIUM_TOP =
            reg("thaumatorium_top", TileThaumatoriumTop.class, TileThaumatoriumTop::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileVoidSiphon>> VOID_SIPHON =
            reg("void_siphon", TileVoidSiphon.class, TileVoidSiphon::new);

    // Devices
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileArcaneEar>> ARCANE_EAR =
            reg("arcane_ear", TileArcaneEar.class, TileArcaneEar::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileBellows>> BELLOWS =
            reg("bellows", TileBellows.class, TileBellows::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileCondenser>> CONDENSER =
            reg("condenser", TileCondenser.class, TileCondenser::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileDioptra>> DIOPTRA =
            reg("dioptra", TileDioptra.class, TileDioptra::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileHungryChest>> HUNGRY_CHEST =
            reg("hungry_chest", TileHungryChest.class, TileHungryChest::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileInfernalFurnace>> INFERNAL_FURNACE =
            reg("infernal_furnace", TileInfernalFurnace.class, TileInfernalFurnace::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileJarBrain>> JAR_BRAIN =
            reg("jar_brain", TileJarBrain.class, TileJarBrain::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileLampArcane>> LAMP_ARCANE =
            reg("lamp_arcane", TileLampArcane.class, TileLampArcane::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileLampFertility>> LAMP_FERTILITY =
            reg("lamp_fertility", TileLampFertility.class, TileLampFertility::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileLampGrowth>> LAMP_GROWTH =
            reg("lamp_growth", TileLampGrowth.class, TileLampGrowth::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileLevitator>> LEVITATOR =
            reg("levitator", TileLevitator.class, TileLevitator::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileMirror>> MIRROR =
            reg("mirror", TileMirror.class, TileMirror::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileMirrorEssentia>> MIRROR_ESSENTIA =
            reg("mirror_essentia", TileMirrorEssentia.class, TileMirrorEssentia::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TilePotionSprayer>> POTION_SPRAYER =
            reg("potion_sprayer", TilePotionSprayer.class, TilePotionSprayer::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileRechargePedestal>> RECHARGE_PEDESTAL =
            reg("recharge_pedestal", TileRechargePedestal.class, TileRechargePedestal::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileRedstoneRelay>> REDSTONE_RELAY =
            reg("redstone_relay", TileRedstoneRelay.class, TileRedstoneRelay::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileSpa>> SPA =
            reg("spa", TileSpa.class, TileSpa::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileStabilizer>> STABILIZER =
            reg("stabilizer", TileStabilizer.class, TileStabilizer::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileVisGenerator>> VIS_GENERATOR =
            reg("vis_generator", TileVisGenerator.class, TileVisGenerator::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileWaterJug>> WATER_JUG =
            reg("water_jug", TileWaterJug.class, TileWaterJug::new);

    // Essentia
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileAlembic>> ALEMBIC =
            reg("alembic", TileAlembic.class, TileAlembic::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileCentrifuge>> CENTRIFUGE =
            reg("centrifuge", TileCentrifuge.class, TileCentrifuge::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEssentiaInput>> ESSENTIA_INPUT =
            reg("essentia_input", TileEssentiaInput.class, TileEssentiaInput::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileEssentiaOutput>> ESSENTIA_OUTPUT =
            reg("essentia_output", TileEssentiaOutput.class, TileEssentiaOutput::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileJarFillable>> JAR_FILLABLE =
            reg("jar_fillable", TileJarFillable.class, TileJarFillable::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileJarFillableVoid>> JAR_FILLABLE_VOID =
            reg("jar_fillable_void", TileJarFillableVoid.class, TileJarFillableVoid::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileSmelter>> SMELTER =
            reg("smelter", TileSmelter.class, TileSmelter::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileTube>> TUBE =
            reg("tube", TileTube.class, TileTube::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileTubeBuffer>> TUBE_BUFFER =
            reg("tube_buffer", TileTubeBuffer.class, TileTubeBuffer::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileTubeFilter>> TUBE_FILTER =
            reg("tube_filter", TileTubeFilter.class, TileTubeFilter::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileTubeOneway>> TUBE_ONEWAY =
            reg("tube_oneway", TileTubeOneway.class, TileTubeOneway::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileTubeRestrict>> TUBE_RESTRICT =
            reg("tube_restrict", TileTubeRestrict.class, TileTubeRestrict::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileTubeValve>> TUBE_VALVE =
            reg("tube_valve", TileTubeValve.class, TileTubeValve::new);

    // Misc
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileBanner>> BANNER =
            reg("banner", TileBanner.class, TileBanner::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileBarrierStone>> BARRIER_STONE =
            reg("barrier_stone", TileBarrierStone.class, TileBarrierStone::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileHole>> HOLE =
            reg("hole", TileHole.class, TileHole::new);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TileNitor>> NITOR =
            reg("nitor", TileNitor.class, TileNitor::new);

    private static <T extends BlockEntity>
    DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> reg(
            String name, Class<T> clazz, TileFactory<T> factory) {
        @SuppressWarnings("unchecked")
        DeferredHolder<BlockEntityType<?>, BlockEntityType<T>>[] holderRef = new DeferredHolder[1];
        DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> holder =
                (DeferredHolder<BlockEntityType<?>, BlockEntityType<T>>)(Object)
                BLOCK_ENTITY_TYPES.register(name, () ->
                    new BlockEntityType<>(
                        (pos, state) -> factory.create(holderRef[0].get(), pos, state),
                        java.util.Set.of()
                    ));
        holderRef[0] = holder;
        CLASS_TO_TYPE.put(clazz, holder);
        return holder;
    }

    public static BlockEntityType<?> getTypeForClass(Class<?> clazz) {
        DeferredHolder<BlockEntityType<?>, ? extends BlockEntityType<?>> holder = CLASS_TO_TYPE.get(clazz);
        return holder != null ? holder.get() : null;
    }

    public static void register(IEventBus bus) {
        BLOCK_ENTITY_TYPES.register(bus);
    }
}
