package com.comze_instancelabs.mgflyingcars;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Minecart;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.event.vehicle.VehicleUpdateEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.comze_instancelabs.minigamesapi.Arena;
import com.comze_instancelabs.minigamesapi.ArenaConfigStrings;
import com.comze_instancelabs.minigamesapi.ArenaSetup;
import com.comze_instancelabs.minigamesapi.ArenaState;
import com.comze_instancelabs.minigamesapi.MinigamesAPI;
import com.comze_instancelabs.minigamesapi.PluginInstance;
import com.comze_instancelabs.minigamesapi.config.ArenasConfig;
import com.comze_instancelabs.minigamesapi.config.DefaultConfig;
import com.comze_instancelabs.minigamesapi.config.MessagesConfig;
import com.comze_instancelabs.minigamesapi.config.StatsConfig;
import com.comze_instancelabs.minigamesapi.util.Util;
import com.comze_instancelabs.minigamesapi.util.Validator;

public class Main extends JavaPlugin implements Listener {

	// give players items like grenades (drop fireballs) that explode in the air

	MinigamesAPI api = null;
	PluginInstance pli = null;
	static Main m = null;
	static int global_arenas_size = 30;

	ICommandHandler cmdhandler = new ICommandHandler();

	private HashMap<String, Integer> pusage = new HashMap<String, Integer>();

	public void onEnable() {
		m = this;
		MinigamesAPI.getAPI();
		api = MinigamesAPI.setupAPI(this, "flyingcars", IArena.class, new ArenasConfig(this), new MessagesConfig(this), new IClassesConfig(this), new StatsConfig(this, false), new DefaultConfig(this, false), false);
		PluginInstance pinstance = api.pinstances.get(this);
		pinstance.addLoadedArenas(loadArenas(this, pinstance.getArenasConfig()));
		Bukkit.getPluginManager().registerEvents(this, this);
		pinstance.getArenaListener().loseY = 100;
		pli = pinstance;
	}

	public static ArrayList<Arena> loadArenas(JavaPlugin plugin, ArenasConfig cf) {
		ArrayList<Arena> ret = new ArrayList<Arena>();
		FileConfiguration config = cf.getConfig();
		if (!config.isSet("arenas")) {
			return ret;
		}
		for (String arena : config.getConfigurationSection(ArenaConfigStrings.ARENAS_PREFIX).getKeys(false)) {
			if (Validator.isArenaValid(plugin, arena, cf.getConfig())) {
				ret.add(initArena(arena));
			}
		}
		return ret;
	}

	public static IArena initArena(String arena) {
		IArena a = new IArena(m, arena);
		MinigamesAPI.getAPI();
		ArenaSetup s = MinigamesAPI.pinstances.get(m).arenaSetup;
		a.init(Util.getSignLocationFromArena(m, arena), Util.getAllSpawns(m, arena), Util.getMainLobby(m), Util.getComponentForArena(m, arena, "lobby"), s.getPlayerCount(m, arena, true), s.getPlayerCount(m, arena, false), s.getArenaVIP(m, arena));
		return a;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return cmdhandler.handleArgs(this, "mgflyingcars", "/" + cmd.getName(), sender, args);
	}

	@EventHandler
	public void onPlayerPickup(PlayerPickupItemEvent event) {
		if (pli.global_players.containsKey(event.getPlayer().getName())) {
			if (event.getItem().getItemStack().getType() == Material.FIREBALL) {
				if (!event.getItem().getItemStack().getItemMeta().getDisplayName().equalsIgnoreCase(event.getPlayer().getName())) {
					try {
						Bukkit.getPlayer(event.getItem().getItemStack().getItemMeta().getDisplayName()).sendMessage(ChatColor.GREEN + "Your bomb killed " + ChatColor.DARK_GREEN + event.getPlayer().getName() + ChatColor.GREEN + "!");
					} catch (Exception e) {
					}
					pli.global_players.get(event.getPlayer().getName()).spectate(event.getPlayer().getName());
					Util.clearInv(event.getPlayer());
				} else {
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent event) {
		if (pli.global_players.containsKey(event.getPlayer().getName())) {
			if (event.getItemDrop().getItemStack().getType() != Material.FIREBALL) {
				event.setCancelled(true);
			} else {
				ItemMeta im = event.getItemDrop().getItemStack().getItemMeta();
				im.setDisplayName(event.getPlayer().getName());
				ItemStack item = new ItemStack(event.getItemDrop().getItemStack().getType());
				item.setItemMeta(im);
				event.getItemDrop().setItemStack(item);
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player) {
			Player p = (Player) event.getWhoClicked();
			if (pli.global_players.containsKey(p.getName())) {
				Arena a = pli.global_players.get(p.getName());
				if (a != null) {
					if (a.getArenaState() == ArenaState.INGAME) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		final Player p = event.getPlayer();
		if (pli.global_players.containsKey(p.getName())) {
			if (event.hasItem()) {
				if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
					final ItemStack item = event.getItem();
					if (item.getType() == Material.STONE_HOE) {
						shoot(item, event.getPlayer(), 0, 124, 12, 1);
					} else if (item.getType() == Material.IRON_HOE) {
						shoot(item, event.getPlayer(), 1, 242, 16, 2);
					} else if (item.getType() == Material.DIAMOND_HOE) {
						shoot(item, event.getPlayer(), 2, 1554, 24, 2);
					}
				}
				if (event.getAction() == Action.RIGHT_CLICK_AIR && event.getItem().getType() == Material.BLAZE_ROD) {
					if (p.isInsideVehicle()) {
						p.getVehicle().setVelocity(p.getVehicle().getVelocity().add(new Vector(0D, 1.75D, 0D)));
					}
				}
			}
			if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (p.isInsideVehicle()) {
					p.getVehicle().setVelocity(p.getVehicle().getVelocity().add(new Vector(0D, 1.75D, 0D)));
				}
			}
		}
	}

	public void shoot(ItemStack item, final Player p, int id, int durability, int durability_temp, int eggcount) {
		if (item.getDurability() < durability) { // 124
			for (int i = 0; i < eggcount; i++) {
				Arrow a = p.getLocation().getWorld().spawnArrow(p.getLocation().add(p.getLocation().getDirection().multiply(2D)).add(0D, 2D, 0D), p.getLocation().getDirection(), 2F, 0.5F);
				a.setShooter(p);
			}
			item.setDurability((short) (item.getDurability() + durability_temp)); // 6
		} else {
			if (!pusage.containsKey(p.getName())) {
				p.sendMessage(ChatColor.RED + "Please wait 3 seconds before using this gun again!");
				Bukkit.getScheduler().runTaskLater(m, new Runnable() {
					public void run() {
						p.updateInventory();
						p.getInventory().clear();
						p.updateInventory();
						pli.getClassesHandler().getClass(p.getName());
						if (pusage.containsKey(p.getName())) {
							pusage.remove(p.getName());
						}
					}
				}, 20L * 3);
				pusage.put(p.getName(), id);
				Bukkit.getScheduler().runTaskLater(m, new Runnable() {
					public void run() {
					
						p.getInventory().addItem(new ItemStack(Material.BLAZE_ROD));
						p.updateInventory();
					}
				}, 20L * 4);
			}
		}
	}

	public Vector v;

	@EventHandler
	public void onPlayerMove(VehicleMoveEvent event) {
		Entity passenger = event.getVehicle().getPassenger();
		if (passenger instanceof Player) {
			Player p = (Player) passenger;
			if (pli.global_players.containsKey(p.getName())) {
				v = p.getLocation().getDirection().multiply(9.0D);
				event.getVehicle().getLocation().setDirection(v);
				event.getVehicle().setVelocity(new Vector(v.getX(), 0.0001D, v.getZ()));
			}
		}
	}

	@EventHandler
	public void onVehicleUpdate(VehicleUpdateEvent event) {
		Vehicle vehicle = event.getVehicle();
		Entity passenger = vehicle.getPassenger();
		if (!(passenger instanceof Player)) {
			return;
		}
		Player p = (Player) passenger;
		if (pli.global_players.containsKey(p.getName())) {
			if (vehicle instanceof Minecart) {
				Minecart car = (Minecart) vehicle;

				if (v == null) {
					v = p.getLocation().getDirection();
				}
				Vector dir = v.multiply(8.5D);
				Vector dir_ = new Vector(dir.getX(), 0.0001D, dir.getZ());

				car.setVelocity(dir_);
			}
		}
	}

	@EventHandler
	public void onVehicleExit(VehicleExitEvent event) {
		if (event.getExited() instanceof Player) {
			Player p = (Player) event.getExited();
			if (pli.global_players.containsKey(p.getName())) {
				p.getVehicle().setVelocity(p.getVehicle().getVelocity().add(new Vector(0D, -1D, 0D)));
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onVehicleDestroy(VehicleDestroyEvent event) {
		if (event.getAttacker() instanceof Player && event.getVehicle().getPassenger() instanceof Player) {
			Player p = (Player) event.getVehicle().getPassenger();
			if (pli.global_players.containsKey(p.getName())) {
				Player p2 = (Player) event.getAttacker();
				if (p2.getName().equalsIgnoreCase(p.getName())) {
					event.setCancelled(true); // disallow shooting your own minecart
				} else {
					pli.global_players.get(p.getName()).spectate(p.getName());
				}
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (pli.global_players.containsKey(p.getName())) {
				Arena a = pli.global_players.get(p.getName());
				// if (a.getArenaState() == ArenaState.INGAME) {
				if (event.getCause() == DamageCause.ENTITY_ATTACK || event.getCause() == DamageCause.PROJECTILE || event.getCause() == DamageCause.FALL) {
					p.setHealth(20D);
					event.setCancelled(true);
					return;
				}
				// }
			}
		}
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (pli.global_players.containsKey(event.getPlayer().getName())) {
			event.setCancelled(true);
		}
	}

}
