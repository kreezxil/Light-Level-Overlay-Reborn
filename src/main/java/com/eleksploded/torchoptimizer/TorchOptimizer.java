package com.eleksploded.torchoptimizer;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.PointOfView;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("torchoptimizer")
public class TorchOptimizer
{
	// Directly reference a log4j logger.
	private static final Logger LOGGER = LogManager.getLogger();

	public static TorchOptimizer instance;

	public static int value;

	public boolean active;
	public OverlayRender renderer;
	public OverlayPoller poller;
	public KeyBinding hotkey;
	public KeyBinding plusOne, minusOne;
	public String message;
	public double messageRemainingTicks;



	public TorchOptimizer() {
		instance = this;

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TorchConfig.spec);

		MinecraftForge.EVENT_BUS.register(this);

		//Make sure the mod being absent on the other network side does not cause the client to display the server as incompatible
		ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.DISPLAYTEST, () -> Pair.of(
				() -> "Trans Rights Are Human Rights",
				(remoteVersionString,networkBool) -> networkBool
		));
	}

	private void setup(final FMLClientSetupEvent event) {
		value = TorchConfig.GENERAL.optimizeValue.get();
		active = false;
		renderer = new OverlayRender();
		poller = new OverlayPoller();

		hotkey = new KeyBinding("key.torchoptimizer.hotkey", GLFW.GLFW_KEY_F7, "key.categories.torchoptimizer");
		ClientRegistry.registerKeyBinding(hotkey);

		plusOne = new KeyBinding("key.torchoptimizer.plusone", GLFW.GLFW_KEY_LEFT_BRACKET, "key.categories.torchoptimizer");
		ClientRegistry.registerKeyBinding(plusOne);

		minusOne = new KeyBinding("key.torchoptimizer.minusone", GLFW.GLFW_KEY_RIGHT_BRACKET,
				"key.categories.torchoptimizer");
		ClientRegistry.registerKeyBinding(minusOne);

		launchPoller();
	}

	private void launchPoller() {
		if (poller.isAlive())
			return;
		try {
			poller.start();
		} catch (Exception e) {
			e.printStackTrace();
			poller = new OverlayPoller();
		}
	}

	// You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
	// Event bus for receiving Registry Events)
	@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
	public static class RegistryEvents {
		@SubscribeEvent
		public static void onBlocksRegistry(final RegistryEvent.Register<Block> blockRegistryEvent) {
		    // register a new block here
		    LOGGER.info("HELLO from Register Block");
		}
	}


	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onKeyInputEvent(KeyInputEvent event) {


		if (hotkey.isPressed()) {
			active = !active;
			launchPoller();
		}


		if (minusOne.isPressed()) {
			if (active) {
				value=value-1;
				renderer = new OverlayRender();
			} else {
				active = !active;
				launchPoller();
			}
		}

		if (plusOne.isPressed()) {
			if (active) {
				value=value+1;
				renderer = new OverlayRender();
			} else {
				active = !active;
				launchPoller();
			}
		}
	}
    
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
		if (active) {
			ClientPlayerEntity player = Minecraft.getInstance().player;
			if (player == null)
				return;

			IRenderTypeBuffer.Impl renderTypeBuffer = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
			MatrixStack matrixStack = event.getMatrixStack();
			matrixStack.push();
			renderer.render(matrixStack, renderTypeBuffer, poller.overlays);
			matrixStack.pop();
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onRenderGameOverlayEventText(RenderGameOverlayEvent.Text event) {
		if (messageRemainingTicks > 0) {
			messageRemainingTicks -= event.getPartialTicks();
			event.getLeft().add(message);
		}
	}
}
