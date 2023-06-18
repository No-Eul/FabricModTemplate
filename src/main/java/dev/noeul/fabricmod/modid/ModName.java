package dev.noeul.fabricmod.modid;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModName implements ModInitializer {
	public static final ModContainer mod = FabricLoader.getInstance()
			.getModContainer("centaur")
			.orElseThrow(NullPointerException::new);
	public static final String ID = mod.getMetadata().getId();
	public static final String NAME = mod.getMetadata().getName();
	public static final Logger logger = LoggerFactory.getLogger(NAME);

	@Override
	public void onInitialize() {
	}
}
