package info.nukepowered.impressivelogic.common.registry;

import info.nukepowered.impressivelogic.ImpressiveLogic;
import info.nukepowered.impressivelogic.common.logic.network.blockentity.BaseNetworkEntity;
import info.nukepowered.impressivelogic.common.logic.network.blockentity.io.BooleanOutputEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class TileEntityRegistry {

    private static Block[] ENTITY_HOLDER_BLOCKS;
    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ImpressiveLogic.MODID);

    // Registry STARTS \\
    public static final RegistryObject<BlockEntityType<BooleanOutputEntity>> LOGIC_LAMP = register("logic_lamp", (pos, state) -> new BooleanOutputEntity(TileEntityRegistry.LOGIC_LAMP.get(), pos, state));

    public static void init() {
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static <T extends BaseNetworkEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> supplier) {
        return ENTITIES.register(name, () -> BlockEntityType.Builder.of(supplier, initOrGetHolders()).build(null));
    }

    private static Block[] initOrGetHolders() {
        if (ENTITY_HOLDER_BLOCKS == null) {
            // Probably only lamp is required since its type of holder, if any child class - put instance here
            ENTITY_HOLDER_BLOCKS = new Block[] {
                    BlockRegistry.LOGIC_LAMP.get()
            };
        }

        return ENTITY_HOLDER_BLOCKS;
    }
}
