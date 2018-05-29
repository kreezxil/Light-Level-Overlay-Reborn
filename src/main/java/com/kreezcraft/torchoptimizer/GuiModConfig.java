package com.kreezcraft.torchoptimizer;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;

public class GuiModConfig extends GuiConfig {
	public GuiModConfig(GuiScreen parent) {
		super(
			parent,
			new ConfigElement(TorchOptimizer.instance.config.file.getCategory("general")).getChildElements(),
			TorchOptimizer.MODID,
			false,
			false,
			GuiConfig.getAbridgedConfigPath(TorchOptimizer.instance.config.file.toString())
		);
	}
}