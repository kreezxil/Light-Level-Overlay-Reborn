package com.kreezcraft.torchoptimizer;

import java.io.File;
import java.util.ArrayList;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ConfigManager {
	
	public Configuration file;
	
	public Property useSkyLight;
	public Property displayMode;
	public Property chunkRadius;
	public Property pollingInterval;
	public Property optimizeEnable;
	public Property optimizedPlacement;
	
	public ArrayList<String> displayModeName = new ArrayList<String>();
	public ArrayList<String> displayModeDesc = new ArrayList<String>();
	
	public ConfigManager(File configDir) {
		file = new Configuration(new File(configDir, "TorchOptimizer.cfg"));
		displayModeName.add("Standard");
		displayModeName.add("Advanced");
		displayModeName.add("Minimal");
		displayModeDesc.add("Show green (safe) and red (spawnable) areas.");
		displayModeDesc.add("Show green (safe), red (always spawnable) and yellow (currently safe, but will be spawnable at night) areas.");
		displayModeDesc.add("Do not show green area. For other areas, use standard mode when not counting sky light, or advanced mode otherwise.");
		reload();
	}
	
	@SubscribeEvent
	public void onConfigurationChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		if (event.getModID().equals(TorchOptimizer.MODID)) update();
	}
	
	void reload() {
		file.load();
		String comment = "How to display numbers? (default: 0)";
		for (int i = 0; i < displayModeName.size(); i++) {
			comment += "\n" + String.valueOf(i) + " - ";
			comment += displayModeName.get(i) + ": ";
			comment += displayModeDesc.get(i);
		}
		useSkyLight = file.get("general", "useSkyLight", false, "If set to true, the sunlight/moonlight will be counted in light level. (default: false)"); 
		displayMode = file.get("general", "displayMode", 0, comment);
		chunkRadius = file.get("general", "chunkRadius", 3, "The distance (in chunks) of rendering radius. (default: 3)");
		pollingInterval = file.get("general", "pollingInterval", 200, "The update interval (in milliseconds) of light level. Farther chunks update less frequently. (default: 200)");
		optimizeEnable = file.get("general", "optimizeEnable", false, "If enabled, optimizedPlacement will be used to help you place your torches. (default: false)");
		optimizedPlacement = file.get("general", "optimizedPlacement", 2, "Shows an asterisk on the ground to indicate where to put your torch. (default: 2)");
		update();
	}
	
	public void update() {
		useSkyLight.set(useSkyLight.getBoolean(false));
		displayMode.set(Math.min(Math.max(displayMode.getInt(0), 0), displayModeName.size() - 1));
		chunkRadius.set(Math.min(Math.max(chunkRadius.getInt(3), 1), 9));
		pollingInterval.set(Math.min(Math.max(pollingInterval.getInt(200), 10), 60000));
		optimizeEnable.set(optimizeEnable.getBoolean(false));
		if(optimizedPlacement.getInt() < 0 || optimizedPlacement.getInt() > 15) optimizedPlacement.set(2);
		optimizedPlacement.set(optimizedPlacement.getInt(2));
		file.save();
		if(TorchOptimizer.instance.renderer != null) TorchOptimizer.instance.renderer.switchTexture(optimizedPlacement.getInt());
	}
	
}
