package com.safaan.roundball;

import com.safaan.roundball.command.RoundBallCommands;
import com.safaan.roundball.entity.ModEntities;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RoundBallAssistant implements ModInitializer {
    public static final String MOD_ID = "roundballassistant";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ModEntities.initialize();
        RoundBallCommands.register();
        LOGGER.info("Round Ball Assistant initialized for Minecraft 1.21.1 / Fabric 0.19.3");
    }
}
