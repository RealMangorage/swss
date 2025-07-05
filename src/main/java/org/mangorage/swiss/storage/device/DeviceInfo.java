package org.mangorage.swiss.storage.device;

import java.util.UUID;

public record DeviceInfo(UUID owner, String savedPassword) {}
