package com.shopme.admin.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;

@Service
public class SettingService {
	
	@Autowired
	private SettingRepository settingRepo;
	
	public List<Setting> listAllSettings() {
		return (List<Setting>) settingRepo.findAll();
	}
	
	public GeneralSettingBag getGeneralSettingBag() {
		List<Setting> settings = new ArrayList<>();
		List<Setting> settingsGenaral = settingRepo.findByCategory(SettingCategory.GENERAL);
		List<Setting> settingsCurrency = settingRepo.findByCategory(SettingCategory.CURRENCY);
		
		settings.addAll(settingsGenaral);
		settings.addAll(settingsCurrency);
		
		return new GeneralSettingBag(settings);
		
	}
	
	public void saveAll(Iterable<Setting> settings) {
		settingRepo.saveAll(settings);
	}
}
