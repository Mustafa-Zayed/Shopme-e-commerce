package com.shopme.common.entity.setting.general.utility;

import com.shopme.common.entity.setting.general.Setting;

import java.util.List;

/**
 * Utility class to get general settings
 */
public class GeneralSettingBag extends SettingBag {
    public GeneralSettingBag(List<Setting> settings) {
        super(settings);
    }

    public void updateSiteLogo(String value) {
        super.update("SITE_LOGO", value);
    }

    public void updateCurrencySymbol(String value) {
        super.update("CURRENCY_SYMBOL", value);
    }
}
