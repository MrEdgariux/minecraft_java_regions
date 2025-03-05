package lt.mredgariux.regions.classes;

import java.util.ArrayList;
import java.util.List;

public class RegionFlags {
    public boolean breakBlocks = false;
    public boolean buildBlocks = false;

    public boolean destroyPaintings = false;
    public boolean destroyItemFrames = false;

    public boolean eatCake = false;
    public boolean pvp = false;
    public boolean tnt = false;
    public boolean enderDragonDestroyBlocks = false;
    public boolean editSigns = false;

    public boolean usePressurePlates = false;
    public boolean useButtons = false;
    public boolean useChest = false;
    public boolean useFurnace = false;
    public boolean useCraftingTable = false;
    public boolean useEnderChest = false;
    public boolean useContainerBlocks = false;
    public boolean useItemFrames = false;
    public boolean useBuckets = false;
    public boolean fireSpread = false;
    public boolean useWorldEdit = false;
    public boolean useThrowablePotions = false;

    public boolean enter = true;
    public boolean leave = true;

    public String enterPermission = "";
    public String leavePermission = "";

    public List<String> allowBreakSpecificBlocks = new ArrayList<>();
    public List<String> allowPlaceSpecificBlocks = new ArrayList<>();

    public String enterMessage = "";
    public String leaveMessage = "";
}
