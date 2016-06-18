package com.comze_instancelabs.mgflyingcars;

import com.comze_instancelabs.minigamesapi.config.ClassesConfig;

public class IClassesConfig extends ClassesConfig {

	public IClassesConfig(Main m){
		super(m, true);
		this.getConfig().options().header("Used for saving classes. Default class:");
		
		// default (stone)
    	this.getConfig().addDefault("config.kits.default.name", "default");
    	this.getConfig().addDefault("config.kits.default.items", "291#ARROW_DAMAGE:1*1;385*3");
    	this.getConfig().addDefault("config.kits.default.lore", "Stone");
    	this.getConfig().addDefault("config.kits.default.requires_money", false);
    	this.getConfig().addDefault("config.kits.default.requires_permission", false);
    	this.getConfig().addDefault("config.kits.default.money_amount", 100);
    	this.getConfig().addDefault("config.kits.default.permission_node", "minigames.kits.default");
    	
    	// iron
    	this.getConfig().addDefault("config.kits.iron.name", "iron");
    	this.getConfig().addDefault("config.kits.iron.items", "292#ARROW_DAMAGE:2*1;385*5");
    	this.getConfig().addDefault("config.kits.iron.lore", "Iron");
    	this.getConfig().addDefault("config.kits.iron.requires_money", false);
    	this.getConfig().addDefault("config.kits.iron.requires_permission", false);
    	this.getConfig().addDefault("config.kits.iron.money_amount", 100);
    	this.getConfig().addDefault("config.kits.iron.permission_node", "minigames.kits.iron");
    	
    	// diamond
    	this.getConfig().addDefault("config.kits.diamond.name", "diamond");
    	this.getConfig().addDefault("config.kits.diamond.items", "293#ARROW_DAMAGE:3*1;385*10");
    	this.getConfig().addDefault("config.kits.diamond.lore", "Diamond");
    	this.getConfig().addDefault("config.kits.diamond.requires_money", false);
    	this.getConfig().addDefault("config.kits.diamond.requires_permission", false);
    	this.getConfig().addDefault("config.kits.diamond.money_amount", 100);
    	this.getConfig().addDefault("config.kits.diamond.permission_node", "minigames.kits.diamond");

    	this.getConfig().options().copyDefaults(true);
    	this.saveConfig();
	}
	
}
