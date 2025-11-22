package com.shopme.admin.setting.repository;

import com.shopme.common.entity.SettingCategory;
import com.shopme.common.entity.setting.Setting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface SettingRepository extends CrudRepository<Setting, String>, PagingAndSortingRepository<Setting, String> {
    List<Setting> findByCategory(SettingCategory category);
}

