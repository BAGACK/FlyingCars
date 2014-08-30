package com.comze_instancelabs.mgflyingcars;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.util.Util;

public class IArena extends Arena {

	Main m = null;

	public IArena(Main m, String arena) {
		super(m, arena);
		this.m = m;
	}

	@Override
	public void spectate(String playername) {
		Util.clearInv(Bukkit.getPlayer(playername));
		super.spectate(playername);
	}

	@Override
	public void started() {
		for (String p_ : this.getAllPlayers()) {
			final Player p = Bukkit.getPlayer(p_);
			if (p != null) {
				if (!p.isInsideVehicle()) {
					final Minecart mc = (Minecart) p.getWorld().spawnEntity(p.getLocation().add(0D, 1D, 0D), EntityType.MINECART);
					Bukkit.getScheduler().runTaskLater(m, new Runnable() {
						public void run() {
							mc.setPassenger(p);
						}
					}, 20L);
				}
				p.getInventory().addItem(new ItemStack(Material.BLAZE_ROD));
				p.updateInventory();
			}
		}
	}

	@Override
	public void start(boolean tp) {
		super.start(tp);
		for (String p_ : this.getAllPlayers()) {
			final Player p = Bukkit.getPlayer(p_);
			if (p != null) {
				final Minecart mc = (Minecart) p.getWorld().spawnEntity(p.getLocation().add(0D, 1D, 0D), EntityType.MINECART);
				Bukkit.getScheduler().runTaskLater(m, new Runnable() {
					public void run() {
						mc.setPassenger(p);
					}
				}, 20L);
			}
		}
	}

	@Override
	public void leavePlayer(final String playername, final boolean fullLeave) {
		Player p = Bukkit.getPlayer(playername);
		if (p.isInsideVehicle()) {
			Entity ent = p.getVehicle();
			p.leaveVehicle();
			ent.remove();
		}
		super.leavePlayer(playername, fullLeave);
	}

}
