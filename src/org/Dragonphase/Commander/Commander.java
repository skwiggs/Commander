package org.Dragonphase.Commander;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.Dragonphase.Commander.Listeners.BlockListener;
import org.Dragonphase.Commander.Permissions.VaultPerms;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.material.Button;
import org.bukkit.material.Lever;
import org.bukkit.material.PressurePlate;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class Commander extends JavaPlugin{
	public final Logger logger = Logger.getLogger("Minecraft");
	public static Commander plugin;
	public static YamlConfiguration commandConfig;
	public static VaultPerms perms;

    static final HashSet<Byte> transparentBlocks = new HashSet<Byte>();
    static {
    	transparentBlocks.add((byte)Material.AIR.getId());
    	transparentBlocks.add((byte)Material.WATER.getId());
    	transparentBlocks.add((byte)Material.STATIONARY_WATER.getId());
    	transparentBlocks.add((byte)Material.SIGN.getId());
    	transparentBlocks.add((byte)Material.WALL_SIGN.getId());
    	transparentBlocks.add((byte)Material.STEP.getId());
    	transparentBlocks.add((byte)Material.TRAP_DOOR.getId());
    	transparentBlocks.add((byte)Material.TORCH.getId());
    	transparentBlocks.add((byte)Material.BROWN_MUSHROOM.getId());
    	transparentBlocks.add((byte)Material.RED_MUSHROOM.getId());
    	transparentBlocks.add((byte)Material.YELLOW_FLOWER.getId());
    	transparentBlocks.add((byte)Material.RED_ROSE.getId());
    	transparentBlocks.add((byte)Material.IRON_FENCE.getId());
    	transparentBlocks.add((byte)Material.FENCE.getId());
    	transparentBlocks.add((byte)Material.NETHER_FENCE.getId());
    	transparentBlocks.add((byte)Material.BED_BLOCK.getId());
    	transparentBlocks.add((byte)Material.PAINTING.getId());
    	transparentBlocks.add((byte)Material.WATER_LILY.getId());
    	transparentBlocks.add((byte)Material.THIN_GLASS.getId());
    	transparentBlocks.add((byte)Material.VINE.getId());
    	transparentBlocks.add((byte)Material.SNOW.getId());
    	transparentBlocks.add((byte)Material.LADDER.getId());
    	transparentBlocks.add((byte)Material.SAPLING.getId());
    	transparentBlocks.add((byte)Material.LONG_GRASS.getId());
    	transparentBlocks.add((byte)Material.DEAD_BUSH.getId());
    }
	
	@Override
	public void onDisable(){
		PluginDescriptionFile PDF = this.getDescription();
		logger.info(PDF.getName() + " disabled.");
	}

	@Override
	public void onEnable(){
		PluginDescriptionFile PDF = this.getDescription();
		logger.info(PDF.getName() + " version " + PDF.getVersion() + " enabled.");
		getServer().getPluginManager().registerEvents(new BlockListener(this), this);
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		perms = new VaultPerms(this);
	}
	
	public String conditionaliseString(String commands, boolean addSlash){
		String conCommands = "";
		if (addSlash){
			conCommands = "/" + commands;
		}else{
			conCommands = commands;
		}
		conCommands = conCommands.replaceAll(">", ChatColor.LIGHT_PURPLE + ">" + ChatColor.AQUA);
		
		for (String separateCommand : conCommands.split(">")){
			if (separateCommand.startsWith("@@")){
				conCommands = conCommands.replace(separateCommand, ChatColor.GOLD + "@@" + ChatColor.BLUE + separateCommand.substring(2));
			}else if (separateCommand.startsWith("@") || separateCommand.substring(2).startsWith("@")){
				logger.info(separateCommand);
				conCommands = conCommands.replace(separateCommand, ChatColor.GOLD + separateCommand);
			}else if (separateCommand.startsWith("/") || separateCommand.substring(2).startsWith("/")){
				conCommands = conCommands.replace(separateCommand, ChatColor.DARK_GREEN + separateCommand);
			}
		}
		
		return ChatColor.AQUA + conCommands;
	}

	public void addCommand(Block block, String sender, String[] args, Player player){
		String command = "";
		String blockID = block.getType().toString() + "_" + block.getX() + "_" + block.getY() + "_" + block.getZ() + "_" + block.getWorld().getName();
		for (int i = 2; i < args.length; i ++){
			if (args[i].startsWith("/") && !args[i].startsWith("//") && command == ""){
				if (args[i] == args[args.length-1]){
					command = command + args[i].replace("/", "");
				}else{
					command = command + args[i].replace("/", "") + " ";
				}
			}else{
				if (args[i] == args[args.length-1]){
					if (args[i].contains(">//") || args[i].contains(">/")){
						command = command + args[i];
					}else if (args[i].contains(">") && !args[i].contains("@") && !args[i].contains(">/")){
						command = command + args[i].replace(">", ">/");
					}else{
						command = command + args[i].replace("|/", "|").replace("//", "/");
					}
				}else{
					if (args[i].contains(">//") || args[i].contains(">/")){
						command = command + args[i] + " ";
					}else if (args[i].contains(">") && !args[i].contains("@") && !args[i].contains(">/")){
						command = command + args[i].replace(">", ">/") + " ";
					}else{
						command = command + args[i].replace("|/", "|").replace("//", "/") + " ";
					}
				}
			}
		}
		
		if (sender.equalsIgnoreCase("player")){
			List<String> commands = new ArrayList<String>();
			try{
				commands = commandConfig.getStringList(blockID + ".player.commands");
			} catch (Exception ex){}
			
			for (String separateCommand : command.split(Pattern.quote("|"))){
				commands.add(separateCommand);
			}

			commandConfig.set(blockID + ".player.commands", commands);
		}
		
		if (sender.equalsIgnoreCase("op")){
			List<String> commands = new ArrayList<String>();
			try{
				commands = commandConfig.getStringList(blockID + ".op.commands");
			} catch (Exception ex){}

			for (String separateCommand : command.split(Pattern.quote("|"))){
				commands.add(separateCommand);
			}

			commandConfig.set(blockID + ".op.commands", commands);
		}
		
		if (sender.equalsIgnoreCase("console")){
			List<String> commands = new ArrayList<String>();
			try{
				commands = commandConfig.getStringList(blockID + ".console.commands");
			} catch (Exception ex){}

			for (String separateCommand : command.split(Pattern.quote("|"))){
				commands.add(separateCommand);
			}

			commandConfig.set(blockID + ".console.commands", commands);
		}
		
		String message = ChatColor.GREEN + "Added " + conditionaliseString(command, true) + ChatColor.GREEN + " to " + block.getType().toString().toLowerCase().replace("_", " ") + ". It will be run as " + sender + ".";
		if (message.contains("/@")){
			player.sendMessage(message.replace("/@", "@"));
		}else{
			player.sendMessage(message);
		}
		
		try {
			commandConfig.save(new File(getDataFolder(), "commands.yml"));
		} catch (Exception ex) {}
	}
	
	public void removeCommand(Block block, String sender, String[] args, Player player){
		String command = "";
		String blockID = block.getType().toString() + "_" + block.getX() + "_" + block.getY() + "_" + block.getZ() + "_" + block.getWorld().getName();
		int argNum = 0;
		if (args.length > 2){
			argNum = 2;
			if (args[2].equalsIgnoreCase("*")){
				if (sender.equalsIgnoreCase("player")){
					commandConfig.set(blockID + ".player.commands", null);
				}
				if (sender.equalsIgnoreCase("op")){
					commandConfig.set(blockID + ".op.commands", null);
				}
				if (sender.equalsIgnoreCase("console")){
					commandConfig.set(blockID + ".console.commands", null);
				}
				try {
					commandConfig.save(new File(getDataFolder(), "commands.yml"));
				} catch (Exception ex) {}
				player.sendMessage(ChatColor.RED + "Removed all " + sender + " commands from " + block.getType().toString().toLowerCase().replace("_", " ") + ".");

				return;
			}
		}else{
			argNum = 1;
			if (args[1].equalsIgnoreCase("*")){
				if (sender.equalsIgnoreCase("all")){
					try {
						commandConfig.set(blockID + ".player.commands", null);
						commandConfig.set(blockID + ".op.commands", null);
						commandConfig.set(blockID + ".console.commands", null);
					
						commandConfig.save(new File(getDataFolder(), "commands.yml"));
					} catch (Exception ex) {}
				}
				player.sendMessage(ChatColor.RED + "Removed all commands from " + block.getType().toString().toLowerCase().replace("_", " ") + ".");

				return;
			}
		}
		for (int i = argNum; i < args.length; i ++){
			if (args[i].startsWith("/") && !args[i].startsWith("//") && command == ""){
				if (args[i] == args[args.length-1]){
					command = command + args[i].replace("/", "");
				}else{
					command = command + args[i].replace("/", "") + " ";
				}
			}else{
				if (args[i] == args[args.length-1]){
					command = command + args[i].replace("//", "/").replace("|/", "|");
				}else{
					command = command + args[i].replace("//", "/").replace("|/", "|") + " ";
				}
			}
		}
		
		if (sender.equalsIgnoreCase("player")){
			List<String> commands = new ArrayList<String>();
			try{
				commands = commandConfig.getStringList(blockID + ".player.commands");
			} catch (Exception ex){}
			
			for (String separateCommand : command.split(Pattern.quote("|"))){
				commands.remove(separateCommand);
			}

			commandConfig.set(blockID + ".player.commands", commands);
		}
		
		if (sender.equalsIgnoreCase("op")){
			List<String> commands = new ArrayList<String>();
			try{
				commands = commandConfig.getStringList(blockID + ".op.commands");
			} catch (Exception ex){}

			for (String separateCommand : command.split(Pattern.quote("|"))){
				commands.remove(separateCommand);
			}

			commandConfig.set(blockID + ".op.commands", commands);
		}
		
		if (sender.equalsIgnoreCase("console")){
			List<String> commands = new ArrayList<String>();
			try{
				commands = commandConfig.getStringList(blockID + ".console.commands");
			} catch (Exception ex){}

			for (String separateCommand : command.split(Pattern.quote("|"))){
				commands.remove(separateCommand);
			}
			
			commandConfig.set(blockID + ".console.commands", commands);
		}

		String message = ChatColor.RED + "Removed " + conditionaliseString(command, true) + ChatColor.RED + " from " + block.getType().toString().toLowerCase().replace("_", " ") + ".";
		if (message.contains("/@")){
			player.sendMessage(message.replace("/@", "@"));
		}else{
			player.sendMessage(message);
		}

		try {
			commandConfig.save(new File(getDataFolder(), "commands.yml"));
		} catch (Exception ex) {}
	}
	
	public void clearCommand(Block block, Player player){
		String blockID = block.getType().toString() + "_" + block.getX() + "_" + block.getY() + "_" + block.getZ() + "_" + block.getWorld().getName();
		for (String key : commandConfig.getKeys(false)){
			if (key.equalsIgnoreCase(blockID)){
				commandConfig.set(blockID, null);
				try {
					commandConfig.save(new File(getDataFolder(), "commands.yml"));
				} catch (Exception ex) {}
				player.sendMessage(ChatColor.GREEN + "This device has been cleared of all commands.");
				return;
			}else{
				continue;
			}
		}
		player.sendMessage(ChatColor.RED + "This device has not been commanded!");
	}
	
	public void getCommands(CommandSender sender, Block block){
		commandConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "commands.yml"));
		String blockID = block.getType().toString() + "_" + block.getX() + "_" + block.getY() + "_" + block.getZ() + "_" + block.getWorld().getName();
		
		boolean commanded = false;
		for (String key : commandConfig.getKeys(false)){
			boolean pCom = false;
			boolean oCom = false;
			boolean cCom = false;
			if (key.equalsIgnoreCase(blockID)){
				try{
					List<String> pCommands = new ArrayList<String>();
					pCommands = commandConfig.getStringList(blockID + ".player.commands");

					if (pCommands.size() > 0){
						sender.sendMessage(ChatColor.GREEN + "  Player commands for this " + block.getType().toString().toLowerCase().replace("_", " ") + ":");
						for (String command : pCommands){
							if (command.startsWith("@")){
								sender.sendMessage(ChatColor.AQUA + "    " + conditionaliseString(command, false));
							}else{
								sender.sendMessage(ChatColor.AQUA + "    /" + conditionaliseString(command, false));
							}
						}
						pCom = true;
					}
				} catch (Exception ex){}
				try{
					List<String> oCommands = new ArrayList<String>();
					oCommands = commandConfig.getStringList(blockID + ".op.commands");
					
					if (oCommands.size() > 0){
						sender.sendMessage(ChatColor.GREEN + "  Operator commands for this " + block.getType().toString().toLowerCase().replace("_", " ") + ":");
						for (String command : oCommands){
							if (command.startsWith("@")){
								sender.sendMessage(ChatColor.AQUA + "    " + conditionaliseString(command, false));
							}else{
								sender.sendMessage(ChatColor.AQUA + "    /" + conditionaliseString(command, false));
							}
						}
						oCom = true;
					}
				} catch (Exception ex){}
				try{
					List<String> cCommands = new ArrayList<String>();
					cCommands = commandConfig.getStringList(blockID + ".console.commands");
					
					if (cCommands.size() > 0){
						sender.sendMessage(ChatColor.GREEN + "  Console commands for this " + block.getType().toString().toLowerCase().replace("_", " ") + ":");
						for (String command : cCommands){
							if (command.startsWith("@")){
								sender.sendMessage(ChatColor.AQUA + "    " + conditionaliseString(command, false));
							}else{
								sender.sendMessage(ChatColor.AQUA + "    /" + conditionaliseString(command, false));
							}
						}
						cCom = true;
					}
					
				} catch (Exception ex){}
				if (!pCom && !oCom && !cCom){
					sender.sendMessage(ChatColor.RED + "  This device has no commands!");
					return;
				}
				commanded = true;
				break;
			}else{
				commanded = false;
				continue;
			}
		}
		if (!commanded) sender.sendMessage(ChatColor.RED + "This device has not been commanded!");
	}
	
	public void getCommandList(CommandSender sender, Boolean deep){
		commandConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "commands.yml"));
		
		for (World serverWorld : getServer().getWorlds()){
			boolean sendMainMessage = true;
			for (String key : commandConfig.getKeys(false)){
				int numCommands = 0;
				try{
					numCommands += commandConfig.getStringList(key + ".player.commands").size();
				}catch (Exception ex){}
				try{
					numCommands += commandConfig.getStringList(key + ".op.commands").size();
				}catch (Exception ex){}
				try{
					numCommands += commandConfig.getStringList(key + ".console.commands").size();
				}catch (Exception ex){}
				String[] blockID = key.split("_");
				if (blockID.length > 5){
					String blockName = blockID[0].toLowerCase().substring(0, 1).toUpperCase() + blockID[0].substring(1).toLowerCase() + " " + blockID[1].toLowerCase().substring(0, 1).toUpperCase() + blockID[1].substring(1).toLowerCase();
					int blockX = Integer.parseInt(blockID[2]);
					int blockY = Integer.parseInt(blockID[3]);
					int blockZ = Integer.parseInt(blockID[4]);
					
					String world = blockID[5];
					
					if (world.equalsIgnoreCase(serverWorld.getName())){
						if (sendMainMessage){
							sender.sendMessage(ChatColor.GREEN + "Commanded devices in " + ChatColor.AQUA + serverWorld.getName() + ChatColor.RESET + ":");
							sendMainMessage = false;
						}
						sender.sendMessage("");
						if (deep){
							sender.sendMessage(ChatColor.RED + "  " + ChatColor.BOLD + blockName + ChatColor.RESET + " > " + ChatColor.AQUA + "   X: " + blockX + "    Y: " + blockY + "    Z: " + blockZ + "    Commands: " + numCommands + ChatColor.RESET + ":");
							getCommands(sender, getServer().getWorld(world).getBlockAt(new Location(serverWorld, blockX, blockY, blockZ)));
						}else{
							sender.sendMessage(ChatColor.RED + "  " + ChatColor.BOLD + blockName + ChatColor.RESET + " > " + ChatColor.AQUA + "   X: " + blockX + "    Y: " + blockY + "    Z: " + blockZ + "    Commands: " + numCommands);
						}
					}
				}else{
					String blockName = blockID[0].toLowerCase().substring(0, 1).toUpperCase() + blockID[0].substring(1).toLowerCase();
					int blockX = Integer.parseInt(blockID[1]);
					int blockY = Integer.parseInt(blockID[2]);
					int blockZ = Integer.parseInt(blockID[3]);
					
					String world = blockID[4];
					
					if (world.equalsIgnoreCase(serverWorld.getName())){
						if (sendMainMessage){
							sender.sendMessage(ChatColor.GREEN + "Commanded devices in " + ChatColor.AQUA + serverWorld.getName() + ChatColor.RESET + ":");
							sendMainMessage = false;
						}
						sender.sendMessage("");
						if (deep){
							sender.sendMessage(ChatColor.RED + "  " + ChatColor.BOLD + blockName + ChatColor.RESET + " > " + ChatColor.AQUA + "   X: " + blockX + "    Y: " + blockY + "    Z: " + blockZ + "    Commands: " + numCommands + ChatColor.RESET + ":");
							getCommands(sender, getServer().getWorld(world).getBlockAt(new Location(serverWorld, blockX, blockY, blockZ)));
						}else{
							sender.sendMessage(ChatColor.RED + "  " + ChatColor.BOLD + blockName + ChatColor.RESET + " > " + ChatColor.AQUA + "   X: " + blockX + "    Y: " + blockY + "    Z: " + blockZ + "    Commands: " + numCommands);
						}
					}
				}
			}
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (sender instanceof Player) perms.setPlayer((Player) sender);
		if (commandLabel.equalsIgnoreCase("commander") || commandLabel.equalsIgnoreCase("com")){
			if (args.length == 0){
				if (!perms.hasPermission("commander") && !perms.hasPermission("commander.reload") && !perms.hasPermission("commander.add") && !perms.hasPermission("commander.remove") && !perms.hasPermission("commander.check") && !perms.hasPermission("commander.clear") || !perms.hasPermission("commander.list")){
					sender.sendMessage(ChatColor.GREEN + "Commander version " + getDescription().getVersion() + ".");
					return false;
				}
				sender.sendMessage(ChatColor.GREEN + "Commander version " + getDescription().getVersion() + ":");
				sender.sendMessage(ChatColor.GREEN + "Commands can be run as the player, an op or the console.");
				sender.sendMessage(ChatColor.GREEN + "You can use " + ChatColor.AQUA + "player" + ChatColor.GREEN + " or " + ChatColor.AQUA + "p" + ChatColor.GREEN + ", " + ChatColor.AQUA + "op" + ChatColor.GREEN + " or " + ChatColor.AQUA + "o" + ChatColor.GREEN + ", " + ChatColor.AQUA + "console" + ChatColor.GREEN + " or " + ChatColor.AQUA + "c" + ChatColor.GREEN + ".");
				sender.sendMessage(ChatColor.AQUA + "  /commander add <p|o|c> <command 1>[|command 2|...]");
				sender.sendMessage(ChatColor.AQUA + "  /commander remove <p|o|c> <command 1>[|command 2|...]");
				sender.sendMessage(ChatColor.AQUA + "  /commander remove *");
				sender.sendMessage(ChatColor.AQUA + "  /commander check");
				sender.sendMessage(ChatColor.AQUA + "  /commander clear");
				sender.sendMessage(ChatColor.AQUA + "  /commander list");
				sender.sendMessage(ChatColor.AQUA + "  /commander alist");
				sender.sendMessage(ChatColor.GREEN + "Adding a / in commands is optional. Commands with two (//) such as worldedit commands will still work.");
			}else if (args.length > 0){
				if (args[0].equalsIgnoreCase("reload")){
					if (perms.hasPermission("commander.reload")){
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
				if (args[0].equalsIgnoreCase("add")){
					if (!(sender instanceof Player)){
						sender.sendMessage(ChatColor.RED + "You cannot run this command from the console!");
						return false;
					}
					if (!perms.hasPermission("commander.add")){
						sender.sendMessage(ChatColor.RED + "You do not have permission!");
						return false;
					}
					Player player = (Player)sender;
					if (args.length > 1){
						if (args.length > 2){
							Block block = player.getTargetBlock(transparentBlocks, getConfig().getInt("max-target-block-distance"));
							commandConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "commands.yml"));
							if (args[1].equalsIgnoreCase("player") || args[1].equalsIgnoreCase("p")){
								if (block.getState().getData() instanceof Button || block.getState().getData() instanceof Lever || block.getState().getData() instanceof PressurePlate){
									addCommand(block, "player", args, player);
									return false;
								}
							}
							if (args[1].equalsIgnoreCase("op") || args[1].equalsIgnoreCase("o")){
								if (block.getState().getData() instanceof Button || block.getState().getData() instanceof Lever || block.getState().getData() instanceof PressurePlate){
									addCommand(block, "op", args, player);
									return false;
								}
							}
							if (args[1].equalsIgnoreCase("console") || args[1].equalsIgnoreCase("c")){
								if (block.getState().getData() instanceof Button || block.getState().getData() instanceof Lever || block.getState().getData() instanceof PressurePlate){
									addCommand(block, "console", args, player);
									return false;
								}
							}
						}else{
							sender.sendMessage(ChatColor.RED + "Usage: /commander add <player|op|console> <command>");
						}
					}else{
						sender.sendMessage(ChatColor.RED + "Usage: /commander add <player|op|console> <command>");
					}
				}
				if (args[0].startsWith("rem")){
					if (!(sender instanceof Player)){
						sender.sendMessage(ChatColor.RED + "You cannot run this command from the console!");
						return false;
					}
					if (!perms.hasPermission("commander.remove")){
						sender.sendMessage(ChatColor.RED + "You do not have permission!");
						return false;
					}
					Player player = (Player)sender;
					if (args.length > 1){
						Block block = player.getTargetBlock(transparentBlocks, getConfig().getInt("max-target-block-distance"));
						if (args.length > 2){
							commandConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "commands.yml"));
							if (args[1].equalsIgnoreCase("player") || args[1].equalsIgnoreCase("p")){
								if (block.getState().getData() instanceof Button || block.getState().getData() instanceof Lever || block.getState().getData() instanceof PressurePlate){
									removeCommand(block, "player", args, player);
									return false;
								}
							}
							if (args[1].equalsIgnoreCase("op") || args[1].equalsIgnoreCase("o")){
								if (block.getState().getData() instanceof Button || block.getState().getData() instanceof Lever || block.getState().getData() instanceof PressurePlate){
									removeCommand(block, "op", args, player);
									return false;
								}
							}
							if (args[1].equalsIgnoreCase("console") || args[1].equalsIgnoreCase("c")){
								if (block.getState().getData() instanceof Button || block.getState().getData() instanceof Lever || block.getState().getData() instanceof PressurePlate){
									removeCommand(block, "console", args, player);
									return false;
								}
							}
						}else{
							if (args[1] != null && args[1].equalsIgnoreCase("*")){
								removeCommand(block, "all", args, player);
								return false;
							}else{
								sender.sendMessage(ChatColor.RED + "Usage: /commander remove <player|op|console> <command>");
							}
						}
					}else{
						sender.sendMessage(ChatColor.RED + "Usage: /commander remove <player|op|console> <command>");
					}
				}
				if (args[0].equalsIgnoreCase("check")){
					if (!(sender instanceof Player)){
						sender.sendMessage(ChatColor.RED + "You cannot run this command from the console!");
						return false;
					}
					if (!perms.hasPermission("commander.check")){
						sender.sendMessage(ChatColor.RED + "You do not have permission!");
						return false;
					}
					getCommands(sender, ((Player) sender).getTargetBlock(transparentBlocks, getConfig().getInt("max-target-block-distance")));
				}
				if (args[0].equalsIgnoreCase("clear")){
					if (!(sender instanceof Player)){
						sender.sendMessage(ChatColor.RED + "You cannot run this command from the console!");
						return false;
					}
					if (!perms.hasPermission("commander.clear")){
						sender.sendMessage(ChatColor.RED + "You do not have permission!");
						return false;
					}
					Player player = (Player)sender;
					Block block = player.getTargetBlock(transparentBlocks, getConfig().getInt("max-target-block-distance"));
					commandConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "commands.yml"));
					clearCommand(block, player);
				}
				if (args[0].equalsIgnoreCase("list")){
					try{
						if (!perms.hasPermission("commander.list")){
							sender.sendMessage(ChatColor.RED + "You do not have permission!");
							return false;
						}
					}catch (Exception ex){}
					getCommandList(sender, false);
				}
				if (args[0].equalsIgnoreCase("alist")){
					try{
						if (!perms.hasPermission("commander.list")){
							sender.sendMessage(ChatColor.RED + "You do not have permission!");
							return false;
						}
					}catch (Exception ex){}
					getCommandList(sender, true);
				}
			}
		}
		return false;
	}
}
