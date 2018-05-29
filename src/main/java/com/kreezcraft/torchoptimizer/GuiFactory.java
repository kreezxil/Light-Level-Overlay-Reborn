package com.kreezcraft.torchoptimizer;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

public class GuiFactory implements IModGuiFactory {

	@Override
	public void initialize(Minecraft minecraftInstance) {
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		// TODO Auto-generated method stub
		return null;
	}

	public Class<? extends GuiScreen> mainConfigGuiClass() {
		return GuiModConfig.class;
	}
//
	public boolean hasConfigGui() {
		return true;
	}
//
	@Override
	public RuntimeOptionGuiHandler getHandlerFor(RuntimeOptionCategoryElement element) {
		// TODO Auto-generated method stub
		return null;
	}

}
