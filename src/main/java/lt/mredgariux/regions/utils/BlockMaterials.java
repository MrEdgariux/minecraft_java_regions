package lt.mredgariux.regions.utils;

import org.bukkit.Material;

public final class BlockMaterials {
    private BlockMaterials() {
    }

    public static boolean isPlaceableBlock(Material material) {
        return material.isBlock() && material.isItem() && !material.isAir() && !material.isLegacy();
    }
}
