package info.nukepowered.impressivelogic.common.registry;

import info.nukepowered.impressivelogic.ImpressiveLogic;
import info.nukepowered.impressivelogic.common.block.BaseNetworkEntityHolder;
import info.nukepowered.impressivelogic.common.blockentity.BaseNetworkEntity;
import info.nukepowered.impressivelogic.common.blockentity.io.BooleanInputEntity;
import info.nukepowered.impressivelogic.common.blockentity.io.BooleanOutputEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.function.TriFunction;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class TileEntityRegistry {

    private static final DeferredRegister<BlockEntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, ImpressiveLogic.MODID);

    // Registry STARTS \\
    public static final RegistryObject<BlockEntityType<BooleanOutputEntity>> LOGIC_LAMP = register("logic_lamp", (pos, state) -> new BooleanOutputEntity(TileEntityRegistry.LOGIC_LAMP.get(), pos, state));
    public static final RegistryObject<BlockEntityType<BooleanInputEntity>> LOGIC_LEVER = register("logic_lever", BooleanInputEntity::new, TileEntityRegistry.LOGIC_LEVER);

    public static void init() {
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    public static <T extends BaseNetworkEntity> RegistryObject<BlockEntityType<T>> register(String name, TriFunction<BlockEntityType<T>, BlockPos, BlockState, T> constructor, RegistryObject<BlockEntityType<T>> object) {
        return register(name, (pos, state) -> constructor.apply(object.get(), pos, state));
    }

    public static <T extends BaseNetworkEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<T> supplier) {
        return ENTITIES.register(name, BaseNetworkEntityHolder.entityType(supplier));
    }
}
