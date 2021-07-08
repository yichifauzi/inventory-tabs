package com.kqp.inventorytabs.tabs.provider;

import java.util.List;

import com.kqp.inventorytabs.tabs.tab.Tab;

import net.minecraft.client.network.ClientPlayerEntity;

/**
 * Base interface for exposing tabs to the player.
 */
public interface TabProvider {
    void addAvailableTabs(ClientPlayerEntity player, List<Tab> tabs);
}
