package info.nukepowered.impressivelogic.common.registry;

import info.nukepowered.impressivelogic.ImpressiveLogic;

import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * @author TheDarkDnKTv
 *
 */
public class ItemRegistry {

	public static final DeferredRegister<Item> ITEMS;
	static {
		ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ImpressiveLogic.MODID);
	}

	public static void init() {
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
}
