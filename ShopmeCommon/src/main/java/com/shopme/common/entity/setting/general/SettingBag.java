package com.shopme.common.entity.setting.general;

import lombok.AllArgsConstructor;

import java.util.List;

/**
 * Utility class to store a list of settings and provide utility methods to access settings by key, value, etc.
 */
@AllArgsConstructor
public class SettingBag {
    private List<Setting> settings;

    /**
     * Alternative: use List#indexOf, but it needs to implement `equals(Object)` and `hashCode()` methods in
     * Setting class because indexOf method uses the equals() method to compare objects.
     * <p>
     * Example:
     * <pre>{@code
     * int index = settings.indexOf(Setting.builder().key(key).build());
     * return (index >= 0) ? settings.get(index) : null;
     * }</pre>
     */
    public Setting get(String key) {
        return settings.stream()
                .filter(setting -> setting.getKey().equals(key))
                .findFirst()
                .orElse(null);
    }

    public String getValue(String key) {
        Setting setting = get(key);
        return setting != null ? setting.getValue() : null;
    }

    public void update(String key, String value) {
        Setting setting = get(key);
        if (setting != null && value != null) {
            setting.setValue(value);
        }
    }

    public List<Setting> findAll() {
        return settings;
    }
}
