package info.nukepowered.impressivelogic.common.registry;

import info.nukepowered.impressivelogic.ImpressiveLogic;

import info.nukepowered.impressivelogic.common.item.ItemDebug;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
public class ItemRegistry {

	public static final DeferredRegister<Item> ITEMS;
	static {
		ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ImpressiveLogic.MODID);
	}

	public static final RegistryObject<ItemDebug> DEBUGGER = ITEMS.register("debug", ItemDebug::new);

	public static void init() {
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
