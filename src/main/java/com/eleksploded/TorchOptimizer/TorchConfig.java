package com.eleksploded.TorchOptimizer;

import java.util.ArrayList;

import net.minecraftforge.common.ForgeConfigSpec;

public class TorchConfig {
	private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);

    public static class General {
		public final ForgeConfigSpec.ConfigValue<Boolean> Optimize;
        public final ForgeConfigSpec.ConfigValue<Integer> optimizeValue;
		public final ForgeConfigSpec.ConfigValue<Integer> pollingInterval;
		public final ForgeConfigSpec.ConfigValue<Integer> chunkRadius;
		public final ForgeConfigSpec.ConfigValue<Integer> displayMode;
		public final ForgeConfigSpec.ConfigValue<Boolean> useSkyLight;
		
		public ArrayList<String> displayModeName = new ArrayList<String>();
		public ArrayList<String> displayModeDesc = new ArrayList<String>();

        public General(ForgeConfigSpec.Builder builder) {
        	
        	displayModeName.add("Standard");
    		displayModeName.add("Advanced");
    		displayModeName.add("Minimal");
    		displayModeDesc.add("Show green (safe) and red (spawnable) areas.");
    		displayModeDesc.add("Show green (safe), red (always spawnable) and yellow (currently safe, but will be spawnable at night) areas.");
    		displayModeDesc.add("Do not show green area. For other areas, use standard mode when not counting sky light, or advanced mode otherwise.");
        	
    		String comment = "How to display numbers? ";
    		for (int i = 0; i < displayModeName.size(); i++) {
    			comment += "\n" + String.valueOf(i) + " - ";
    			comment += displayModeName.get(i) + ": ";
    			comment += displayModeDesc.get(i);
    		}
    		
            builder.push("General");
            Optimize = builder
                    .comment("Show optimimal torch placement")
                    .translation("config.optimize")
                    .define("optimize", true);
            optimizeValue = builder
            		.comment("Shows an asterisk on the ground to indicate where to put your torch.")
            		.translation("config.optimizeValue")
            		.defineInRange("optimizeValue", 2, 1, 15);
            pollingInterval = builder
            		.comment("The update interval (in milliseconds) of light level. Farther chunks update less frequently.")
            		.translation("config.pollingInterval")
            		.defineInRange("pollingInterval", 200, 1, 20000);
            chunkRadius = builder
            		.comment("The distance (in chunks) of rendering radius.")
            		.translation("config.chunkRadius")
            		.defineInRange("chunkRadius", 3, 1, 16);
            displayMode = builder
            		.comment(comment)
            		.translation("config.displayMode")
            		.defineInRange("displayMode", 0, 0, 5);
            useSkyLight = builder
            		.comment("If set to true, the sunlight/moonlight will be counted in light level.")
            		.translation("config.useSkyLight")
            		.define("useSkyLight", false);
            builder.pop();
        }
    }
    
    public static final ForgeConfigSpec spec = BUILDER.build();
}
