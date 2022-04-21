package info.nukepowered.impressivelogic.common.registry;

import info.nukepowered.impressivelogic.ImpressiveLogic;
import info.nukepowered.impressivelogic.api.logic.INetworkPart.PartType;
import info.nukepowered.impressivelogic.common.block.BaseNetworkEntityHolder;
import info.nukepowered.impressivelogic.common.block.BaseNetworkEntityHolder.Builder;
import info.nukepowered.impressivelogic.common.block.wire.NetworkCableBlock;
import info.nukepowered.impressivelogic.common.logic.network.blockentity.io.BooleanOutputEntity;
import info.nukepowered.impressivelogic.common.util.ExtendedProps;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class BlockRegistry {
	
	public static final DeferredRegister<Block> BLOCKS;
	static {
		BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ImpressiveLogic.MODID);
	}
	
	public static final RegistryObject<NetworkCableBlock> NETWORK_CABLE = registerBlock("network_cable", ImpressiveLogicTabs.MAIN, NetworkCableBlock::new);
	public static final RegistryObject<BaseNetworkEntityHolder<BooleanOutputEntity>> LOGIC_LAMP;
	static {
		LOGIC_LAMP = registerBlock("logic_lamp", ImpressiveLogicTabs.MAIN, () -> Builder.of((pos, state) -> new BooleanOutputEntity(TileEntityRegistry.LOGIC_LAMP.get(), pos, state))
			.type(PartType.IO)
			.addState(BooleanOutputEntity.ACTIVE_PROPERTY, false)
			.blockProperties(
				(ExtendedProps) ExtendedProps.of(Material.BUILDABLE_GLASS)
					.lightLevel(state -> state.getValue(BooleanOutputEntity.ACTIVE_PROPERTY) ? 15 : 0)
					.sound(SoundType.GLASS)
					.strength(0.5F))
			.tickable()
			.build());
	}

	public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> supplier) {
		return registerBlock(name, null, supplier);
	}

	public static <T extends Block> RegistryObject<T> registerBlock(String name, CreativeModeTab tab, Supplier<T> supplier) {
		var object = BLOCKS.register(name, supplier);
		var props = new Item.Properties()
				.tab(tab);
		ItemRegistry.ITEMS.register(name, () -> new BlockItem(object.get(), props));
		return object;
	}

	public static void init() {
		BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
