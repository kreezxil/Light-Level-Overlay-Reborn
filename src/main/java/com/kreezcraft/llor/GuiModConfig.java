package com.kreezcraft.llor;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;

public class GuiModConfig extends GuiConfig {
	public GuiModConfig(GuiScreen parent) {
		super(
			parent,
			new ConfigElement(Llor.instance.config.file.getCategory("general")).getChildElements(),
			Llor.MODID,
			false,
			false,
			GuiConfig.getAbridgedConfigPath(Llor.instance.config.file.toString())
		);
	}
}