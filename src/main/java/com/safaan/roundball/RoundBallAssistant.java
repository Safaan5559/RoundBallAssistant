package com.safaan.roundball;

import com.safaan.roundball.entity.ModEntities;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Main server/common entry point for Round Ball Assistant. */
public final class RoundBallAssistant implements ModInitializer {
    public static final String MOD_ID = "roundballassistant";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModEntities.initialize();
        LOGGER.info("Round Ball Assistant initialized for Minecraft 1.21.1");
    }
}
