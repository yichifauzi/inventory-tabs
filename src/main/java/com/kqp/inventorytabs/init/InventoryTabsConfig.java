package com.kqp.inventorytabs.init;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;

@Config(name = "inventory_tabs")
public class InventoryTabsConfig implements ConfigData {
    public boolean doSightChecksFlag = false;
    public boolean rotatePlayer = false;
    public boolean targetAllScreenHandledBlocks = false;

    @Environment(EnvType.CLIENT)
    public boolean doSightChecks() {
        if (MinecraftClient.getInstance().isIntegratedServerRunning()) {
            return doSightChecksFlag;
        } else {
            return InventoryTabsServerConfig.doSightChecks;
        }
    }
}
