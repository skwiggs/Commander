package org.Dragonphase.Commander;

import java.util.logging.Logger;

import org.Dragonphase.Commander.Listeners.BlockListener;
import org.Dragonphase.Commander.Util.Device;
import org.Dragonphase.Commander.Util.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Commander extends JavaPlugin{
	public final Logger logger = Logger.getLogger("Minecraft");
	public static Commander plugin;
	public static Permissions permissions;
	
	
	
	@Override
	public void onDisable(){
		logger.info(getDescription().getName() + " disabled.");
	}
	
	
	@Override
	public void onEnable(){
		logger.info(getDescription().getName() + " version " + getDescription().getVersion() + " enabled.");
		getServer().getPluginManager().registerEvents(new BlockListener(this), this);
		
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		
		permissions = new Permissions(this);
	}
	
	
	
	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] commandArgs){
		
		Player player = null;
		new Device(this);
		
		if (sender instanceof Player){
			player = (Player)sender;
			permissions.setPlayer(player);
			
		}
		
		if (command.equalsIgnoreCase("commander") || command.equalsIgnoreCase("com")){
			if (commandArgs.length == 0){
				sender.sendMessage(ChatColor.GREEN + "Commander " + getDescription().getVersion() + ":");
				sender.sendMessage(ChatColor.AQUA + "  /com add <p|o|c> <command1>[|command2|...]");
				sender.sendMessage(ChatColor.AQUA + "  /com rem <p|o|c> <command1>[|command2|...]");
				sender.sendMessage(ChatColor.AQUA + "  /com rem <p|o|c> *");
				sender.sendMessage(ChatColor.AQUA + "  /com rem *");
				sender.sendMessage(ChatColor.AQUA + "  /com clear");
				sender.sendMessage(ChatColor.AQUA + "  /com check");
				sender.sendMessage(ChatColor.AQUA + "  /com list");
				sender.sendMessage(ChatColor.AQUA + "p" + ChatColor.GREEN + ", " + ChatColor.AQUA + "o" + ChatColor.GREEN + " and " + ChatColor.AQUA + "c" + ChatColor.GREEN + " are short for " + ChatColor.AQUA + "player" + ChatColor.GREEN + ", " + ChatColor.AQUA + "op" + ChatColor.GREEN + " and " + ChatColor.AQUA + "console" + ChatColor.GREEN + ".");
			}else if (commandArgs.length > 0){
				
				if (commandArgs[0].equalsIgnoreCase("reload")){
					if (permissions.hasPermission(Permissions.COMMANDER_ADMIN_RELOAD)){
						try {
							this.reloadConfig();
							sender.sendMessage(ChatColor.GREEN + "Commander " + getDescription().getVersion() + " reloaded.");
						} catch (Exception e) {
							sender.sendMessage(ChatColor.RED + "Commander " + getDescription().getVersion() + " could not be reloaded.");
						}
					}else{
						sender.sendMessage(ChatColor.RED + "You do not have permission!");
					}
				}
				
				if (commandArgs[0].equalsIgnoreCase("add")){
					if (permissions.hasPermission(Permissions.COMMANDER_ADMIN_ADD)){
						Block block = player.getTargetBlock(Device.transparentBlocks, getConfig().getInt("max-target-block-distance"));
	
						if (!Device.isValid(block)){
							sender.sendMessage(ChatColor.RED + "This device is not a button, lever or pressure plate.");
							return false;
						}
						
						if (commandArgs.length > 2){
							if (commandArgs[1].equalsIgnoreCase("player") || commandArgs[1].equalsIgnoreCase("p")){
								Device.add(block, "player", commandArgs, player);
							}
							if (commandArgs[1].equalsIgnoreCase("op") || commandArgs[1].equalsIgnoreCase("o")){
								Device.add(block, "op", commandArgs, player);
							}
							if (commandArgs[1].equalsIgnoreCase("console") || commandArgs[1].equalsIgnoreCase("c")){
								Device.add(block, "console", commandArgs, player);
							}
						}else{
							sender.sendMessage(ChatColor.RED + "/com add <p|o|c> <command1>[|command2|...]");
						}
					}else{
						sender.sendMessage(ChatColor.RED + "You do not have permission!");
					}
					
				}
				
				if (commandArgs[0].startsWith("rem")){
					if (permissions.hasPermission(Permissions.COMMANDER_ADMIN_REM)){
						Block block = player.getTargetBlock(Device.transparentBlocks, getConfig().getInt("max-target-block-distance"));
	
						if (!Device.isValid(block)){
							sender.sendMessage(ChatColor.RED + "This device is not a button, lever or pressure plate.");
							return false;
						}
						
						if (commandArgs.length > 2){
							if (commandArgs[1].equalsIgnoreCase("player") || commandArgs[1].equalsIgnoreCase("p")){
								Device.remove(block, "player", commandArgs, player);
							}
							if (commandArgs[1].equalsIgnoreCase("op") || commandArgs[1].equalsIgnoreCase("o")){
								Device.remove(block, "op", commandArgs, player);
							}
							if (commandArgs[1].equalsIgnoreCase("console") || commandArgs[1].equalsIgnoreCase("c")){
								Device.remove(block, "console", commandArgs, player);
							}
						}else{
							if (commandArgs.length > 1){
								if (commandArgs[1].equalsIgnoreCase("*")){
									Device.remove(block, "*", commandArgs, player);
								}else{
									sender.sendMessage(ChatColor.RED + "/com rem <p|o|c> <command1>[|command2|...]");
								}
							}else{
								sender.sendMessage(ChatColor.RED + "/com rem <p|o|c> <command1>[|command2|...]");
							}
						}
					}else{
						sender.sendMessage(ChatColor.RED + "You do not have permission!");
					}
					
				}
				
				if (commandArgs[0].startsWith("clear")){
					if (permissions.hasPermission(Permissions.COMMANDER_ADMIN_CLEAR)){
						Block block = player.getTargetBlock(Device.transparentBlocks, getConfig().getInt("max-target-block-distance"));
						
						if (!Device.isValid(block)){
							sender.sendMessage(ChatColor.RED + "This device is not a button, lever or pressure plate.");
							return false;
						}
						
						Device.clear(block, player);
					}else{
						sender.sendMessage(ChatColor.RED + "You do not have permission!");
					}
					
				}

				if (commandArgs[0].startsWith("check")){
					if (permissions.hasPermission(Permissions.COMMANDER_ADMIN_CHECK)){
						Block block = player.getTargetBlock(Device.transparentBlocks, getConfig().getInt("max-target-block-distance"));
	
						if (!Device.isValid(block)){
							sender.sendMessage(ChatColor.RED + "This device is not a button, lever or pressure plate.");
							return false;
						}
					
						if (!Device.check(block, player)){
							sender.sendMessage(ChatColor.RED + "This device has no associated commands or interface.");
						}
					}else{
						sender.sendMessage(ChatColor.RED + "You do not have permission!");
					}
					
				}
				
				if (commandArgs[0].startsWith("list")){
					if (permissions.hasPermission(Permissions.COMMANDER_ADMIN_LIST)){
						Device.list(sender);
					}else{
						sender.sendMessage(ChatColor.RED + "You do not have permission!");
					}
					
				}
				
			}
			
		}
		
		return false;
	}
	
	
	
	
}
