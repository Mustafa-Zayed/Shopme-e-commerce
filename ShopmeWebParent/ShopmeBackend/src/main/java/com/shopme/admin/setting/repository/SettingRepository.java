package com.shopme.admin.setting.repository;

import com.shopme.common.entity.Setting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SettingRepository extends CrudRepository<Setting, String>, PagingAndSortingRepository<Setting, String> {
}

