package info.nukepowered.impressivelogic;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.nukepowered.impressivelogic.common.registry.BlockRegistry;
import info.nukepowered.impressivelogic.common.registry.ItemRegistry;

/**
 * 
 * @author TheDarkDnKTv
 *
 */
@Mod(ImpressiveLogic.MODID)
public class ImpressiveLogic {

	public static final String MODID = "impressivelogic";
	public static final Logger LOGGER = LoggerFactory.getLogger(ImpressiveLogic.class);

	public ImpressiveLogic() {
		FMLJavaModLoadingContext.get().getModEventBus()
			.addListener(this::setup);
		
		BlockRegistry.init();
		ItemRegistry.init();
	}

	private void setup(final FMLCommonSetupEvent event) {
		// mod setup
	}
}
