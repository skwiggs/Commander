package org.Dragonphase.Commander.Listeners;

import static com.sk89q.worldguard.bukkit.BukkitUtil.toVector;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import org.Dragonphase.Commander.Commander;
import org.Dragonphase.Commander.Permissions.VaultPerms;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Button;
import org.bukkit.material.Lever;
import org.bukkit.material.PressurePlate;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class BlockListener implements Listener{
	public final Logger logger = Logger.getLogger("Minecraft");
	public static Commander plugin;
	public static YamlConfiguration commandConfig;
	public static VaultPerms perms;
	
	public BlockListener(Commander instance){
		plugin = instance;
		perms = new VaultPerms(plugin);
	}
	
	private static WorldGuardPlugin getWorldGuard(){
		Plugin wgPlugin = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
		
		if (wgPlugin == null || !(wgPlugin instanceof WorldGuardPlugin)){
			return null;
		}
		return (WorldGuardPlugin) wgPlugin;
	}
	
	public boolean isWithinRegion(Player player, String region){
		return isWithinRegion(player.getLocation(), region);
	}

	public boolean isWithinRegion(Block block, String region){
		return isWithinRegion(block.getLocation(), region);
	}

	public boolean isWithinRegion(Location loc, String region)
	{
	    WorldGuardPlugin guard = getWorldGuard();
	    Vector v = toVector(loc);
	    RegionManager manager = guard.getRegionManager(loc.getWorld());
	    ApplicableRegionSet set = manager.getApplicableRegions(v);
	    for (ProtectedRegion each : set)
	        if (each.getId().equalsIgnoreCase(region))
	            return true;
	    return false;
	}
	
	public String replaceKeywords(String string, Player player){
		String sentence = string;
		sentence = sentence.replace("@player", player.getName());
		sentence = sentence.replace("@world", player.getWorld().getName());
		sentence = sentence.replace("@server", plugin.getServer().getIp());
		sentence = sentence.replace("@health", "" + player.getHealth());
		sentence = sentence.replace("@food", "" + player.getFoodLevel());
		sentence = sentence.replace("@level", "" + player.getLevel());
		sentence = sentence.replace("@exp", "" + player.getTotalExperience());
		sentence = sentence.replace("@time", "" + player.getWorld().getTime());
		sentence = sentence.replace("@dn", player.getDisplayName());
		sentence = sentence.replace("@ip", "" + player.getAddress());
		sentence = sentence.replace("@location", "" + player.getLocation().getX() + " " + player.getLocation().getY() + " " + player.getLocation().getZ());
		sentence = sentence.replace("@gm", player.getGameMode().name());
		return sentence;
	}
	
	public void denyBreak(BlockBreakEvent event, Block block, Player player, String key){
		commandConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "commands.yml"));
		if (!perms.hasPermission("commander.break")){
			player.sendMessage(ChatColor.RED + "You may not break this block!");
			event.setCancelled(true);
			return;
		}
		commandConfig.set(key, null);
		try {
			commandConfig.save(new File(plugin.getDataFolder(), "commands.yml"));
		} catch (Exception ex) {}
		player.sendMessage(ChatColor.RED + "You broke a " + block.getType().toString().toLowerCase().replace("_", " ") + ". All commands have been removed.");
		
		logger.info("[Commander] " + block.getType().toString() + " was broken at X: " + block.getX() + ", Y: " + block.getY() + ", Z: " + block.getZ());
	}
	
	public void performCommand(final String command, final CommandSender sender, final Player player, int delay){
		if (delay > 0){
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
				public void run(){
					if (command.startsWith("@@")){
						player.sendMessage(replaceKeywords(ChatColor.translateAlternateColorCodes('&', command.substring(2)), player));
					}else{
						plugin.getServer().dispatchCommand(sender, replaceKeywords(command, player));
					}
				}
			}, delay*20);
		}else{
			if (command.startsWith("@@")){
				player.sendMessage(replaceKeywords(ChatColor.translateAlternateColorCodes('&', command.substring(2)), player));
			}else{
				plugin.getServer().dispatchCommand(sender, replaceKeywords(command, player));
			}
		}
	}
	
	public void parseCommand(List<String> list, CommandSender sender, Player player, Boolean op){
		perms.setPlayer(player);
		
		int delay = 0;
		boolean notOp = true;
		for (final String command : list){
			if (op && player.isOp() && !notOp){
				player.setOp(false);
			}
			if (command.startsWith("@delay")){
				String[] delayArgs = command.split(" ");
				delay = Integer.parseInt(delayArgs[1]);
				continue;
			}else if (command.contains("@delay")){
				String[] delayArgs = command.substring(command.indexOf("@delay")-1).split(" ");
				delay = Integer.parseInt(delayArgs[1]);
				continue;
			}
	
			if (command.startsWith("@has") && command.contains(">")){
				String[] commandCheckItem = command.split(">");
				String checkItem = commandCheckItem[0].substring(commandCheckItem[0].indexOf(" ")+1);
				String itemCommand = commandCheckItem[1];
				
				boolean hasItem = false;
				for (ItemStack stack : player.getInventory().getContents()){
					try{
						if (stack.getTypeId() == Integer.parseInt(checkItem) && stack.getType() != Material.AIR){
							hasItem = true;
							break;
						}else{
							hasItem = false;
						}
					}catch (Exception ex){
						continue;
					}
				}
				
				try{
					String itemReturn = commandCheckItem[2];
					if (hasItem){
						if (op && !player.isOp()){
							if (!perms.hasPermission("commander.op")) return;
							player.setOp(true);
							notOp = false;
						}
						if (itemCommand.contains("/") && !itemCommand.contains("//")){
							performCommand(itemCommand.replace("/", ""), sender, player, delay);
							delay = 0;
							if (op && player.isOp() && !notOp){
								player.setOp(false);
							}
							continue;
						}else if (itemCommand.contains("//")){
							performCommand(itemCommand.replace("//", "/"), sender, player, delay);
							delay = 0;
							if (op && player.isOp() && !notOp){
								player.setOp(false);
							}
							continue;
						}else{
							if (itemCommand.startsWith("@return")) return;
							if (itemCommand.startsWith("@continue")) continue;
							performCommand(itemCommand, sender, player, delay);
							delay = 0;
							if (op && player.isOp() && !notOp){
								player.setOp(false);
							}
							continue;
						}
					}else{
						if (op && !player.isOp()){
							if (!perms.hasPermission("commander.op")) return;
							player.setOp(true);
							notOp = false;
						}
						if (itemReturn.contains("/") && !itemReturn.contains("//")){
							performCommand(itemReturn.replace("/", ""), sender, player, delay);
							delay = 0;
							if (op && player.isOp() && !notOp){
								player.setOp(false);
							}
							continue;
						}else if (itemReturn.contains("//")){
							performCommand(itemReturn.replace("//", "/"), sender, player, delay);
							delay = 0;
							if (op && player.isOp() && !notOp){
								player.setOp(false);
							}
							continue;
						}else{
							if (itemReturn.startsWith("@return")) return;
							if (itemReturn.startsWith("@continue")) continue;
							performCommand(itemReturn, sender, player, delay);
							delay = 0;
							if (op && player.isOp() && !notOp){
								player.setOp(false);
							}
							continue;
						}
					}
				}catch (Exception ex){
					if (hasItem){
						if (op && !player.isOp()){
							if (!perms.hasPermission("commander.op")) return;
							player.setOp(true);
							notOp = false;
						}
						if (itemCommand.contains("/") && !itemCommand.contains("//")){
							performCommand(itemCommand.replace("/", ""), sender, player, delay);
							delay = 0;
							if (op && player.isOp() && !notOp){
								player.setOp(false);
							}
							continue;
						}else if (itemCommand.contains("//")){
							performCommand(itemCommand.replace("//", "/"), sender, player, delay);
							delay = 0;
							if (op && player.isOp() && !notOp){
								player.setOp(false);
							}
							continue;
						}else{
							if (itemCommand.startsWith("@return")) return;
							if (itemCommand.startsWith("@continue")) continue;
							performCommand(itemCommand, sender, player, delay);
							delay = 0;
							if (op && player.isOp() && !notOp){
								player.setOp(false);
							}
							continue;
						}
					}
				}
			}
			if (command.startsWith("@check") && command.contains(">")){
				String[] commandCheckNode = command.split(">");
				String checkNode = commandCheckNode[0].substring(commandCheckNode[0].indexOf(" ")+1);
				String nodeCommand = commandCheckNode[1];
				try{
					String nodeReturn = commandCheckNode[2];
					if (perms.hasPermission(checkNode)){
						if (op && !player.isOp()){
							if (!perms.hasPermission("commander.op")) return;
							player.setOp(true);
							notOp = false;
						}
						if (nodeCommand.contains("/") && !nodeCommand.contains("//")){
							performCommand(nodeCommand.replace("/", ""), sender, player, delay);
							delay = 0;
							if (op && player.isOp() && !notOp){
								player.setOp(false);
							}
							continue;
						}else if (nodeCommand.contains("//")){
							performCommand(nodeCommand.replace("//", "/"), sender, player, delay);
							delay = 0;
							if (op && player.isOp() && !notOp){
								player.setOp(false);
							}
							continue;
						}else{
							if (nodeCommand.startsWith("@return")) return;
							if (nodeCommand.startsWith("@continue")) continue;
							performCommand(nodeCommand, sender, player, delay);
							delay = 0;
							if (op && player.isOp() && !notOp){
								player.setOp(false);
							}
							continue;
						}
					}else{
						if (op && !player.isOp()){
							if (!perms.hasPermission("commander.op")) return;
							player.setOp(true);
							notOp = false;
						}
						if (nodeReturn.contains("/") && !nodeReturn.contains("//")){
							performCommand(nodeReturn.replace("/", ""), sender, player, delay);
							delay = 0;
							if (op && player.isOp() && !notOp){
								player.setOp(false);
							}
							continue;
						}else if (nodeReturn.contains("//")){
							performCommand(nodeReturn.replace("//", "/"), sender, player, delay);
							delay = 0;
							if (op && player.isOp() && !notOp){
								player.setOp(false);
							}
							continue;
						}else{
							if (nodeReturn.startsWith("@return")) return;
							if (nodeReturn.startsWith("@continue")) continue;
							performCommand(nodeReturn, sender, player, delay);
							delay = 0;
							if (op && player.isOp() && !notOp){
								player.setOp(false);
							}
							continue;
						}
					}
				}catch (Exception ex){
					if (perms.hasPermission(checkNode)){
						if (op && !player.isOp()){
							if (!perms.hasPermission("commander.op")) return;
							player.setOp(true);
							notOp = false;
						}
						if (nodeCommand.contains("/") && !nodeCommand.contains("//")){
							performCommand(nodeCommand.replace("/", ""), sender, player, delay);
							delay = 0;
							if (op && player.isOp() && !notOp){
								player.setOp(false);
							}
							continue;
						}else if (nodeCommand.contains("//")){
							performCommand(nodeCommand.replace("//", "/"), sender, player, delay);
							delay = 0;
							if (op && player.isOp() && !notOp){
								player.setOp(false);
							}
							continue;
						}else{
							if (nodeCommand.startsWith("@return")) return;
							if (nodeCommand.startsWith("@continue")) continue;
							performCommand(nodeCommand, sender, player, delay);
							delay = 0;
							if (op && player.isOp() && !notOp){
								player.setOp(false);
							}
							continue;
						}
					}
				}
			}else{
				if (command.startsWith("@return")) return;
				if (command.startsWith("@continue")) continue;
				if (op && !player.isOp()){
					if (!perms.hasPermission("commander.op")) return;
					player.setOp(true);
					notOp = false;
				}
				performCommand(command, sender, player, delay);
				delay = 0;
				continue;
			}
		}
	}
	
	public void performAllCommands(Block block, Player player){
		commandConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "commands.yml"));
		for (String key : commandConfig.getKeys(false)){
			String blockID = block.getType().toString() + "_" + block.getX() + "_" + block.getY() + "_" + block.getZ() + "_" + block.getWorld().getName();
			
			if (key.equalsIgnoreCase(blockID)){
				// Player commands
				try{
					if (!perms.hasPermission("commander.player")) return;
					
					parseCommand(commandConfig.getStringList(blockID + ".player.commands"), (CommandSender)player, player, false);
				}catch (Exception ex){}
				
				// Op commands
				try{
					parseCommand(commandConfig.getStringList(blockID + ".op.commands"), (CommandSender)player, player, true);
				}catch (Exception ex){}
				
				// Console commands
				try{
					if (!perms.hasPermission("commander.console")) return;
					
					parseCommand(commandConfig.getStringList(blockID + ".console.commands"), plugin.getServer().getConsoleSender(), player, false);
				}catch (Exception ex){}
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		Player player = event.getPlayer();
		perms.setPlayer(player);
		Block block = event.getBlock();
		commandConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "commands.yml"));
		for (String key : commandConfig.getKeys(false)){
			String blockID = block.getType().toString() + "_" + block.getX() + "_" + block.getY() + "_" + block.getZ() + "_" + block.getWorld().getName();
			if (key.equalsIgnoreCase(blockID)){
				denyBreak(event, block, player, key);
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		final Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
			if (block.getState().getData() instanceof Button || block.getState().getData() instanceof Lever){
				performAllCommands(block, player);
			}
		}
		
		if (event.getAction() == Action.PHYSICAL){
			if (block.getState().getData() instanceof PressurePlate){
				performAllCommands(block, player);
			}
		}
	}
	
	@EventHandler
	public void onBlockRedstone(BlockRedstoneEvent event){
		//Block block = event.getBlock();
		//performAllCommands(block, player);
	}
}
