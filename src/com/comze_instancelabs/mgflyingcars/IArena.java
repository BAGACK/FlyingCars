package com.comze_instancelabs.mgflyingcars;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaState;
import com.comze_instancelabs.minigamesapi.Classes;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.util.Util;
import com.comze_instancelabs.minigamesapi.util.Validator;

public class IArena extends Arena {

	Main m = null;
	
	public IArena(Main m, String arena) {
		super(m, arena);
		this.m = m;
	}

	@Override
	public void started(){
		for(String p_ : this.getAllPlayers()){
			final Player p = Bukkit.getPlayer(p_);
			final Minecart mc = (Minecart) p.getWorld().spawnEntity(p.getLocation().add(0D, 1D, 0D), EntityType.MINECART);
			Bukkit.getScheduler().runTaskLater(m, new Runnable(){
				public void run(){
					mc.setPassenger(p);
				}
			}, 20L);
		}
	}
	
	@Override
	public void leavePlayer(final String playername, final boolean fullLeave){
		Player p = Bukkit.getPlayer(playername);
		if(p.isInsideVehicle()){
			Entity ent = p.getVehicle();
			p.leaveVehicle();
			ent.remove();
		}
		super.leavePlayer(playername, fullLeave);
	}

}
