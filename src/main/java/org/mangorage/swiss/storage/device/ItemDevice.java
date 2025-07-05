package org.mangorage.swiss.storage.device;

import net.neoforged.neoforge.items.IItemHandler;

/**
 * Anything that wants to link items to a network or
 * put items into a network
 */
public interface ItemDevice extends IDeviceWithHandler {
    IItemHandler getItemHandler();
}
