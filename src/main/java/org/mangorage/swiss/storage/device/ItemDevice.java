package org.mangorage.swiss.storage.device;

import net.neoforged.neoforge.items.IItemHandler;

public interface ItemDevice extends IDevice {
    IItemHandler getItemHandler();

    boolean canInsert();
    boolean canExtract();
}
