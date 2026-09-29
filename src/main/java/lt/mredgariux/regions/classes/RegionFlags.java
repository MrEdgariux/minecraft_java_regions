package lt.mredgariux.regions.classes;

import lt.mredgariux.regions.enums.RegionFlagEnum;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class RegionFlags {

    private final Map<RegionFlagEnum, Object> flags =
            new EnumMap<>(RegionFlagEnum.class);

    private boolean isDirty = false;

    public RegionFlags() {
        for (RegionFlagEnum flag : RegionFlagEnum.values()) {
            flags.put(flag, flag.getDefaultValue());
        }
    }

    public Map<RegionFlagEnum, Object> getFlags() {
        return flags;
    }

    public boolean getBoolean(RegionFlagEnum flag) {
        Object value = flags.get(flag);
        if (!(value instanceof Boolean)) {
            throw new IllegalArgumentException(flag.toString() + " does not contain a boolean");
        }
        return (Boolean) value;
    }

    public String getString(RegionFlagEnum flag) {
        Object value = flags.get(flag);
        if (!(value instanceof String)) {
            throw new IllegalArgumentException(flag.toString() + " does not contain a string");
        }
        return (String) value;
    }

    public String[] getStringArray(RegionFlagEnum flag) {
        Object value = flags.get(flag);
        if (!(value instanceof String[])) {
            throw new IllegalArgumentException(flag.toString() + " does not contain a string array");
        }
        return (String[]) value;
    }

    public List<String> getStringList(RegionFlagEnum flag) {
        Object value = flags.get(flag);
        if (!(value instanceof String[] array)) {
            throw new IllegalArgumentException(
                    flag + " does not contain a string array"
            );
        }
        return new ArrayList<>(List.of(array));
    }

    public void set(RegionFlagEnum flag, Object value) {
        flags.put(flag, value);
        isDirty = true;
    }

    public void setStringArray(RegionFlagEnum flag, String[] value) {
        if (flag.getType() != String[].class) {
            throw new IllegalArgumentException(flag.name() + " does not accept a string array");
        }
        this.set(flag, value);
    }

    public void setBoolean(RegionFlagEnum flag, boolean value) {
        if (flag.getType() != Boolean.class) {
            throw new IllegalArgumentException(flag.name() + " does not accept a boolean");
        }
        this.set(flag, value);
    }

    public void setString(RegionFlagEnum flag, String value) {
        if (flag.getType() != String.class) {
            throw new IllegalArgumentException(flag.name() + " does not accept a string");
        }
        this.set(flag, value);
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void resetDirty(boolean dirty) {
        isDirty = dirty;
    }
}