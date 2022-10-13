package ru.qoqqi.qcraft;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

import ru.qoqqi.qcraft.advancements.ModCriteriaTriggers;
import ru.qoqqi.qcraft.blockentities.ModBlockEntityTypes;
import ru.qoqqi.qcraft.blockentities.renderers.LootBoxGeneratorBlockEntityRenderer;
import ru.qoqqi.qcraft.blocks.ModBlocks;
import ru.qoqqi.qcraft.config.Config;
import ru.qoqqi.qcraft.containers.ModMenus;
import ru.qoqqi.qcraft.items.ModItems;
import ru.qoqqi.qcraft.loot.GlobalLootModifiers;
import ru.qoqqi.qcraft.particles.ModParticleTypes;
import ru.qoqqi.qcraft.screens.PuzzleBoxScreen;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("q_craft")
public class QCraft {
	
	public static final String MOD_ID = "q_craft";
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public QCraft() {
		IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		eventBus.addListener(this::setup);
		eventBus.addListener(this::loadComplete);
		eventBus.addListener(this::enqueueIMC);
		eventBus.addListener(this::processIMC);
		eventBus.addListener(this::doClientStuff);
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
		
		MinecraftForge.EVENT_BUS.register(this);
		
		ModBlocks.register(eventBus);
		ModBlockEntityTypes.register(eventBus);
		ModItems.register(eventBus);
		ModParticleTypes.register(eventBus);
		ModMenus.register(eventBus);
		ModCriteriaTriggers.register();
		
		GlobalLootModifiers.register(eventBus);
	}
	
	private void setup(final FMLCommonSetupEvent event) {
		LOGGER.info("HELLO FROM PREINIT");
	}
	
	private void doClientStuff(final FMLClientSetupEvent event) {
		MenuScreens.register(ModMenus.PUZZLE_BOX_MENU.get(), PuzzleBoxScreen::new);
		BlockEntityRenderers.register(ModBlockEntityTypes.LOOT_BOX_GENERATOR.get(), LootBoxGeneratorBlockEntityRenderer::new);
		
		LOGGER.info("Got game settings {}", Minecraft.getInstance().options);
	}
	
	private void loadComplete(final FMLLoadCompleteEvent event) {
	
	}
	
	private void enqueueIMC(final InterModEnqueueEvent event) {
		
		InterModComms.sendTo("q_craft", "helloworld", () -> {
			LOGGER.info("Hello level from the MDK");
			return "Hello world";
		});
	}
	
	private void processIMC(final InterModProcessEvent event) {
		
		LOGGER.info("Got IMC {}", event.getIMCStream().
				map(m -> m.getMessageSupplier().get()).
				collect(Collectors.toList()));
	}
	
	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {
		LOGGER.info("HELLO from server starting");
	}
}