package info.nukepowered.impressivelogic;

import info.nukepowered.impressivelogic.common.logic.network.LogicNetworkRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.nukepowered.impressivelogic.common.registry.BlockRegistry;
import info.nukepowered.impressivelogic.common.registry.ItemRegistry;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Copyright (c) Nukepowered 2022.
 *
 * @author TheDarkDnKTv
 */
@Mod(ImpressiveLogic.MODID)
public class ImpressiveLogic {

	public static final String MODID = "impressivelogic";
	public static final Logger LOGGER = LoggerFactory.getLogger(ImpressiveLogic.class);
	public static final Marker COMMON_MARKER = MarkerFactory.getMarker("COMMON");

	public ImpressiveLogic() {
		FMLJavaModLoadingContext.get().getModEventBus()
			.addListener(this::setup);
		MinecraftForge.EVENT_BUS.register(new LogicNetworkRegistry());

		BlockRegistry.init();
		ItemRegistry.init();
	}

	private void setup(final FMLCommonSetupEvent event) {

	}
}
