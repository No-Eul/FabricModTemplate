package dev.noeul.fabricmod.modid.gui.screen;

import dev.noeul.fabricmod.modid.ModName;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ConfigScreen extends Screen {
	private final Screen previous;

	public ConfigScreen(Screen previous) {
		super(Text.translatable("modid.gui.screen.config.title", ModName.NAME));
		this.previous = previous;
	}

	@Override
	public void close() {
		this.client.setScreen(this.previous);
	}
}
