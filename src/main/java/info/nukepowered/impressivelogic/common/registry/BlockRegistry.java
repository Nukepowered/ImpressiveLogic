package info.nukepowered.impressivelogic.common.registry;

import info.nukepowered.impressivelogic.ImpressiveLogic;
import info.nukepowered.impressivelogic.common.block.io.LogicLampBlock;
import info.nukepowered.impressivelogic.common.block.wire.NetworkCableBlock;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Properties;
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
	
	public static final RegistryObject<NetworkCableBlock> NETWORK_CABLE = registerBlock("network_cable", NetworkCableBlock::new, ImpressiveLogicTabs.MAIN);
	public static final RegistryObject<LogicLampBlock> LOGIC_LAMP = registerBlock("logic_lamp", LogicLampBlock::new, ImpressiveLogicTabs.MAIN);

	public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> supplier) {
		return registerBlock(name, supplier, null);
	}

	public static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> supplier, CreativeModeTab tab) {
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
