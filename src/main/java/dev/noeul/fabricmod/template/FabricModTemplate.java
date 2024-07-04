package dev.noeul.fabricmod.template;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FabricModTemplate implements ModInitializer {
	public static final ModContainer MOD = FabricLoader.getInstance()
			.getModContainer("template")
			.orElseThrow(NullPointerException::new);
	public static final String ID = MOD.getMetadata().getId();
	public static final String NAME = MOD.getMetadata().getName();
	public static final Logger LOGGER = LoggerFactory.getLogger(NAME);

	@Override
	public void onInitialize() {
	}
}
