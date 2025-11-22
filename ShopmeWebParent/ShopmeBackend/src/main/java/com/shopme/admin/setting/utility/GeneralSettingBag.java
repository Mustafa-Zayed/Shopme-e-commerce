package com.shopme.admin.setting.utility;

import com.shopme.common.entity.setting.Setting;
import com.shopme.common.entity.setting.SettingBag;

import java.util.List;

/**
 * Utility class to update site logo and currency symbol
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
