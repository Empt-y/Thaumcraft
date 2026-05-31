package thaumcraft.proxies;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.client.gui.GuiArcaneBore;
import thaumcraft.client.gui.GuiArcaneWorkbench;
import thaumcraft.client.gui.GuiFocalManipulator;
import thaumcraft.client.gui.GuiFocusPouch;
import thaumcraft.client.gui.GuiGolemBuilder;
import thaumcraft.client.gui.GuiHandMirror;
import thaumcraft.client.gui.GuiLogistics;
import thaumcraft.client.gui.GuiPech;
import thaumcraft.client.gui.GuiPotionSprayer;
import thaumcraft.client.gui.GuiResearchBrowser;
import thaumcraft.client.gui.GuiResearchTable;
import thaumcraft.client.gui.GuiSmelter;
import thaumcraft.client.gui.GuiSpa;
import thaumcraft.client.gui.GuiThaumatorium;
import thaumcraft.client.gui.GuiTurretAdvanced;
import thaumcraft.client.gui.GuiTurretBasic;
import thaumcraft.client.gui.GuiVoidSiphon;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.common.container.ContainerArcaneBore;
import thaumcraft.common.container.ContainerArcaneWorkbench;
import thaumcraft.common.container.ContainerFocalManipulator;
import thaumcraft.common.container.ContainerFocusPouch;
import thaumcraft.common.container.ContainerGolemBuilder;
import thaumcraft.common.container.ContainerHandMirror;
import thaumcraft.common.container.ContainerLogistics;
import thaumcraft.common.container.ContainerPech;
import thaumcraft.common.container.ContainerPotionSprayer;
import thaumcraft.common.container.ContainerResearchTable;
import thaumcraft.common.container.ContainerSmelter;
import thaumcraft.common.container.ContainerSpa;
import thaumcraft.common.container.ContainerThaumatorium;
import thaumcraft.common.container.ContainerTurretAdvanced;
import thaumcraft.common.container.ContainerTurretBasic;
import thaumcraft.common.container.ContainerVoidSiphon;
import thaumcraft.common.entities.construct.EntityArcaneBore;
import thaumcraft.common.entities.construct.EntityTurretCrossbow;
import thaumcraft.common.entities.construct.EntityTurretCrossbowAdvanced;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.golems.ItemGolemBell;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;
import thaumcraft.common.tiles.crafting.TileGolemBuilder;
import thaumcraft.common.tiles.crafting.TileResearchTable;
import thaumcraft.common.tiles.crafting.TileThaumatorium;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;
import thaumcraft.common.tiles.devices.TilePotionSprayer;
import thaumcraft.common.tiles.devices.TileSpa;
import thaumcraft.common.tiles.essentia.TileSmelter;


public class ProxyGUI
{
    public Object getClientGuiElement(int ID, Player player, Level world, int x, int y, int z) {
        if (world instanceof net.minecraft.client.multiplayer.ClientLevel) {
            switch (ID) {
                case 13: {
                    return new GuiArcaneWorkbench(player.getInventory(), (TileArcaneWorkbench)world.getBlockEntity(new BlockPos(x, y, z)));
                }
                case 12: {
                    return new GuiResearchBrowser();
                }
                case 10: {
                    return new GuiResearchTable(player, (TileResearchTable)world.getBlockEntity(new BlockPos(x, y, z)));
                }
                case 9: {
                    return new GuiSmelter(player.getInventory(), (TileSmelter)world.getBlockEntity(new BlockPos(x, y, z)));
                }
                case 1: {
                    return new GuiPech(player.getInventory(), world, (EntityPech) world.getEntity(x));
                }
                case 16: {
                    return new GuiTurretBasic(player.getInventory(), world, (EntityTurretCrossbow) world.getEntity(x));
                }
                case 17: {
                    return new GuiTurretAdvanced(player.getInventory(), world, (EntityTurretCrossbowAdvanced) world.getEntity(x));
                }
                case 3: {
                    return new GuiThaumatorium(player.getInventory(), (TileThaumatorium)world.getBlockEntity(new BlockPos(x, y, z)));
                }
                case 14: {
                    return new GuiArcaneBore(player.getInventory(), world, (EntityArcaneBore) world.getEntity(x));
                }
                case 4: {
                    return new GuiHandMirror(player.getInventory(), world, x, y, z);
                }
                case 5: {
                    return new GuiFocusPouch(player.getInventory(), world, x, y, z);
                }
                case 6: {
                    return new GuiSpa(player.getInventory(), (TileSpa)world.getBlockEntity(new BlockPos(x, y, z)));
                }
                case 7: {
                    return new GuiFocalManipulator(player.getInventory(), (TileFocalManipulator)world.getBlockEntity(new BlockPos(x, y, z)));
                }
                case 19: {
                    return new GuiGolemBuilder(player.getInventory(), (TileGolemBuilder)world.getBlockEntity(new BlockPos(x, y, z)));
                }
                case 21: {
                    return new GuiPotionSprayer(player.getInventory(), (TilePotionSprayer)world.getBlockEntity(new BlockPos(x, y, z)));
                }
                case 22: {
                    return new GuiVoidSiphon(player.getInventory(), (TileVoidSiphon)world.getBlockEntity(new BlockPos(x, y, z)));
                }
                case 18: {
                    ISealEntity se = ItemGolemBell.getSeal(player);
                    if (se != null) {
                        return se.getSeal().returnGui(world, player, new BlockPos(x, y, z), se.getSealPos().face, se);
                    }
                    break;
                }
                case 20: {
                    HitResult ray = RayTracer.retrace(player);
                    BlockPos target = null;
                    Direction side = null;
                    if (ray != null && ray.getType() == HitResult.Type.BLOCK) {
                        target = ((net.minecraft.world.phys.BlockHitResult)ray).getBlockPos();
                        side = ((net.minecraft.world.phys.BlockHitResult)ray).getDirection();
                    }
                    return new GuiLogistics(player.getInventory(), world, target, side);
                }
            }
        }
        return null;
    }
    
    public Object getServerGuiElement(int ID, Player player, Level world, int x, int y, int z) {
        switch (ID) {
            case 13: {
                return new ContainerArcaneWorkbench(player.getInventory(), (TileArcaneWorkbench)world.getBlockEntity(new BlockPos(x, y, z)));
            }
            case 10: {
                return new ContainerResearchTable(player.getInventory(), (TileResearchTable)world.getBlockEntity(new BlockPos(x, y, z)));
            }
            case 9: {
                return new ContainerSmelter(player.getInventory(), (TileSmelter)world.getBlockEntity(new BlockPos(x, y, z)));
            }
            case 1: {
                return new ContainerPech(player.getInventory(), world, (EntityPech) world.getEntity(x));
            }
            case 16: {
                return new ContainerTurretBasic(player.getInventory(), world, (EntityTurretCrossbow) world.getEntity(x));
            }
            case 17: {
                return new ContainerTurretAdvanced(player.getInventory(), world, (EntityTurretCrossbowAdvanced) world.getEntity(x));
            }
            case 3: {
                return new ContainerThaumatorium(player.getInventory(), (TileThaumatorium)world.getBlockEntity(new BlockPos(x, y, z)));
            }
            case 5: {
                return new ContainerFocusPouch(player.getInventory(), world, x, y, z);
            }
            case 14: {
                return new ContainerArcaneBore(player.getInventory(), world, (EntityArcaneBore) world.getEntity(x));
            }
            case 4: {
                return new ContainerHandMirror(player.getInventory(), world, x, y, z);
            }
            case 6: {
                return new ContainerSpa(player.getInventory(), (TileSpa)world.getBlockEntity(new BlockPos(x, y, z)));
            }
            case 7: {
                return new ContainerFocalManipulator(player.getInventory(), (TileFocalManipulator)world.getBlockEntity(new BlockPos(x, y, z)));
            }
            case 19: {
                return new ContainerGolemBuilder(player.getInventory(), (TileGolemBuilder)world.getBlockEntity(new BlockPos(x, y, z)));
            }
            case 21: {
                return new ContainerPotionSprayer(player.getInventory(), (TilePotionSprayer)world.getBlockEntity(new BlockPos(x, y, z)));
            }
            case 22: {
                return new ContainerVoidSiphon(player.getInventory(), (TileVoidSiphon)world.getBlockEntity(new BlockPos(x, y, z)));
            }
            case 18: {
                ISealEntity se = ItemGolemBell.getSeal(player);
                if (se != null) {
                    return se.getSeal().returnContainer(world, player, new BlockPos(x, y, z), se.getSealPos().face, se);
                }
                break;
            }
            case 20: {
                return new ContainerLogistics(player.getInventory(), world);
            }
        }
        return null;
    }
}
