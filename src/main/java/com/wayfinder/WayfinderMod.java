package com.wayfinder;

import com.wayfinder.minecraft.command.WayfinderCommands;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod(WayfinderMod.MOD_ID)
public final class WayfinderMod {
    public static final String MOD_ID = "wayfinder";
    public static final Logger LOGGER = LogUtils.getLogger();

    public WayfinderMod(IEventBus modBus, ModContainer container) {
        LOGGER.info("Wayfinder initialized.");
        NeoForge.EVENT_BUS.addListener(WayfinderCommands::register);
    }
}
