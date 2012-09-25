package org.Dragonphase.Commander.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.Dragonphase.Commander.Commander;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Button;
import org.bukkit.material.Lever;
import org.bukkit.material.PressurePlate;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Device {
	public final static Logger logger = Logger.getLogger("Minecraft");
	public static Commander plugin;
	public static YamlConfiguration devices;
	
	
	
    public static final HashSet<Byte> transparentBlocks = new HashSet<Byte>();
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
	
    
    
	public Device(Commander instance){
		plugin = instance;
		loadCommands();
		saveCommands();
	}
	
	
	
	public static String getDeviceID(Block block){
		String ID = "";

		ID = ID + block.getType().toString() + "_";
		ID = ID + block.getX() + "_";
		ID = ID + block.getY() + "_";
		ID = ID + block.getZ() + "_";
		ID = ID + block.getWorld().getName();
		
		return ID;
	}
	
	
	
	public static String capitalize(String string){
		String[] NameList = string.toLowerCase().replace("_", " ").split(" ");
		String Name = "";
		
		for (String Word : NameList){
			Name += Word.substring(0, 1).toUpperCase() + Word.substring(1).toLowerCase() + " ";
		}
		
		if (Name.endsWith(" ")) Name = Name.substring(0, Name.length()-1);
		return Name;
	}
	
	
	
	public static String capitalize(Block block){
		return capitalize(block.getType().toString());
	}
	
	
	
	public static boolean isValid(Block block){
		if (block.getState().getData() instanceof Button || block.getState().getData() instanceof Lever || block.getState().getData() instanceof PressurePlate){
			return true;
		}else{
			return false;
		}
	}
	
	
	
	public static void saveCommands(){
		try {
			devices.save(new File(plugin.getDataFolder(), "devices.yml"));
		} catch (Exception ex) {}
	}
	
	
	
	public static void loadCommands(){
		devices = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "devices.yml"));
	}
	
	
	
	public static List<String> getCommandList(String path){
		try{
			return devices.getStringList(path);
		}catch (Exception ex){
			return new ArrayList<String>();
		}
	}
	
	
	
	public static Set<String> getCommands(){
		try{
			return devices.getKeys(false);
		}catch (Exception ex){
			return null;
		}
	}
	
	
	
	public static void set(String path, Object object){
		devices.set(path, object);
		saveCommands();
	}
	
	
	
	public static String translateCommands(String[] commands){
		String command = "";
		
		for (int i = 2; i < commands.length; i ++){
			
			if (commands[i].startsWith("/") && command == ""){
				if (i == commands.length-1){
					command += commands[i].substring(1).replace(">/", ">");
				}else{
					command += commands[i].substring(1).replace(">/", ">") + " ";
				}
			}else{
				if (i == commands.length-1){
					command += commands[i].replace(">/", ">").replace("|/", "|");
				}else{
					command += commands[i].replace(">/", ">").replace("|/", "|") + " ";
				}
			}
		}
		
		return command;
	}
	
	
	
	public static void addCommands(String type, Block block, String commands, Player player){
		List<String> commandList = new ArrayList<String>();
		commandList = getCommandList(getDeviceID(block) + "." + type + ".commands");
		
		for (String command : commands.split(Pattern.quote("|"))){
			commandList.add(command);
		}
		
		set(getDeviceID(block) + "." + type + ".commands", commandList);

		String message = commands + ChatColor.GREEN + " to " + capitalize(block) + ". It will be run as " + type + ".";
		
		if (!message.startsWith("@")){
			message = ChatColor.GREEN + "Added " + ChatColor.AQUA + "/" + message;
		}else{
			message = ChatColor.GREEN + "Added " + ChatColor.AQUA + message;
		}
		player.sendMessage(message);
	}
	
	
	
	public static void removeCommands(String type, Block block, String commands, Player player){
		if (commands.equalsIgnoreCase("*")){
			set(getDeviceID(block) + "." + type + ".commands", null);
			
			player.sendMessage(ChatColor.RED + "Removed all " + type + " commands from " + capitalize(block) + ".");
			
			return;
		}
		
		if (type.equalsIgnoreCase("*")){
			set(getDeviceID(block) + ".player.commands", null);
			set(getDeviceID(block) + ".op.commands", null);
			set(getDeviceID(block) + ".console.commands", null);
			
			player.sendMessage(ChatColor.RED + "Removed all commands from " + capitalize(block) + ".");
			
			return;
		}
		
		List<String> commandList = new ArrayList<String>();
		commandList = getCommandList(getDeviceID(block) + "." + type + ".commands");
		
		for (String command : commands.split(Pattern.quote("|"))){
			commandList.remove(command);
		}
		
		set(getDeviceID(block) + "." + type + ".commands", commandList);

		String message = commands + ChatColor.RED + " from " + capitalize(block) + ".";
		
		if (!message.startsWith("@")){
			message = ChatColor.RED + "Removed " + ChatColor.AQUA + "/" + message;
		}else{
			message = ChatColor.RED + "Removed " + ChatColor.AQUA + message;
		}
		player.sendMessage(message);
	}
	
	
	
	public static void add(Block block, String sender, String[] commands, Player player){
		
		String command = translateCommands(commands);
		
		if (sender.equalsIgnoreCase("player") || sender.equalsIgnoreCase("op") || sender.equalsIgnoreCase("console")){
			addCommands(sender, block, command, player);
		}else{
			player.sendMessage(ChatColor.RED + "Sender must be player, op or console.");
		}
		
		saveCommands();
		
	}
	
	
	
	public static void remove(Block block, String sender, String[] commands, Player player){

		String command = translateCommands(commands);

		if (sender.equalsIgnoreCase("player") || sender.equalsIgnoreCase("op") || sender.equalsIgnoreCase("console")){
			removeCommands(sender, block, command, player);
		}else if (sender.equalsIgnoreCase("*")){
			removeCommands("*", block, command, player);
		}else if (commands[2].equalsIgnoreCase("*")){
			removeCommands(sender, block, commands[2], player);
		}
		
	}
	
	
	
	public static void clear(Block block, Player player){
		set(getDeviceID(block), null);
		player.sendMessage(ChatColor.GREEN + "Command interface cleared from " + capitalize(block) + ".");
	}
	
	
	
	public static boolean check(Block block, Player player){
		String deviceID = getDeviceID(block);
		boolean hasCommands = false;
		for (String key : devices.getKeys(false)){
			if (key.equalsIgnoreCase(deviceID)){
				String types = new String("player,op,console");
				for (String comType : types.split(",")){
					List<String> commands = getCommandList(deviceID + "." + comType + ".commands");
					if (commands.size() == 0) continue;
					player.sendMessage("  " + ChatColor.GREEN + capitalize(comType) + " commands for this " + capitalize(block) + ":");
					
					for (String command : commands){
						if (command.startsWith("@")){
							player.sendMessage(ChatColor.AQUA + "    " + command);
						}else{
							player.sendMessage(ChatColor.AQUA + "    /" + command);
						}
					}
					hasCommands = true;
				}
			}
		}
		if (hasCommands) return true;
		return false;
	}
	
	
	
	public static void list(CommandSender sender){
		for (World world : plugin.getServer().getWorlds()){
			
			boolean sendTitle = true;
			int worldCommands = 0;
			
			for (String key : devices.getKeys(false)){
				
				int numCommands = 0;

				try{
					numCommands += getCommandList(key + ".player.commands").size();
					worldCommands += getCommandList(key + ".player.commands").size();
				}catch (Exception ex){}
				try{
					numCommands += getCommandList(key + ".op.commands").size();
					worldCommands += getCommandList(key + ".op.commands").size();
				}catch (Exception ex){}
				try{
					numCommands += getCommandList(key + ".console.commands").size();
					worldCommands += getCommandList(key + ".console.commands").size();
				}catch (Exception ex){}
				
				if (numCommands == 0) continue;
				
				String[] deviceID = key.split("_");
				
				if (deviceID.length > 5){
					String deviceName = capitalize(deviceID[0] + "_" + deviceID[1]);
					int deviceX = Integer.parseInt(deviceID[2]);
					int deviceY = Integer.parseInt(deviceID[3]);
					int deviceZ = Integer.parseInt(deviceID[4]);
					String deviceWorld = deviceID[5];
					
					if (world.getName().equalsIgnoreCase(deviceWorld)){
						
						if (sendTitle){
							sender.sendMessage(ChatColor.GREEN + "Devices in " + ChatColor.AQUA + capitalize(deviceWorld) + ChatColor.GREEN + ":");
							sendTitle = false;
						}
						sender.sendMessage(ChatColor.RED + "  " + ChatColor.BOLD + deviceName + ChatColor.RESET + " > " + ChatColor.AQUA + "   X: " + deviceX + "    Y: " + deviceY + "    Z: " + deviceZ + "    Commands: " + numCommands);
						
					}
				}else{
					String deviceName = capitalize(deviceID[0]);
					int deviceX = Integer.parseInt(deviceID[1]);
					int deviceY = Integer.parseInt(deviceID[2]);
					int deviceZ = Integer.parseInt(deviceID[3]);
					String deviceWorld = deviceID[4];
					
					if (world.getName().equalsIgnoreCase(deviceWorld)){
						
						if (sendTitle){
							sender.sendMessage(ChatColor.GREEN + "Devices in " + ChatColor.AQUA + capitalize(deviceWorld) + ChatColor.GREEN + ":");
							sendTitle = false;
						}
						sender.sendMessage(ChatColor.RED + "  " + ChatColor.BOLD + deviceName + ChatColor.RESET + " > " + ChatColor.AQUA + "   X: " + deviceX + "    Y: " + deviceY + "    Z: " + deviceZ + "    Commands: " + numCommands);
						
					}
				}	
			}
			
			if (worldCommands == 0){
				sender.sendMessage(ChatColor.RED + "No devices were found in " + capitalize(world.getName()));
			}
			
		}
	}
	
	
	
	public static void parseCommand(String type, List<String> list, Player player){
		new Reference(plugin);
		int delay = 0;
		for (String command : list){

			if (command.startsWith("@delay")){
				String[] delayArgs = command.split(" ");
				delay = Integer.parseInt(delayArgs[1]);
				continue;
			}else if (command.contains("@delay")){
				String[] delayArgs = command.substring(command.indexOf("@delay")-1).split(" ");
				delay = Integer.parseInt(delayArgs[1]);
				continue;
			}

			if (command.startsWith("@@")){
				player.sendMessage(Reference.getReferences(ChatColor.translateAlternateColorCodes('&', command.substring(2)), player));
			}else if (command.startsWith("@check") && command.contains(">")){
				
				Permissions permissions = new Permissions(plugin);
				permissions.setPlayer(player);
				
				String[] checkSplit = command.split(">");
				String checkNode = checkSplit[0].substring(checkSplit[0].indexOf(" ")+1);
				String checkCommand = checkSplit[1];
				String checkReturn = "";
				
				if (checkSplit.length > 2){
					checkReturn = checkSplit[2];
				}
				
				if (permissions.hasPermission(checkNode)){
					if (checkCommand.startsWith("@return")) return;
					if (checkCommand.startsWith("@continue")) continue;
					
					performCommand(type, checkCommand, player, delay);
				}else{
					if (checkReturn != ""){
						if (checkReturn.startsWith("@return")) return;
						if (checkReturn.startsWith("@continue")) continue;
						
						performCommand(type, checkReturn, player, delay);
					}
				}
				
			}else if (command.startsWith("@in") && command.contains(">")){
				
				Permissions permissions = new Permissions(plugin);
				permissions.setPlayer(player);
				
				String[] checkSplit = command.split(">");
				String checkGroup = checkSplit[0].substring(checkSplit[0].indexOf(" ")+1);
				String checkCommand = checkSplit[1];
				String checkReturn = "";
				
				if (checkSplit.length > 2){
					checkReturn = checkSplit[2];
				}

				if (permissions.inGroup(checkGroup)){
					if (checkCommand.startsWith("@return")) return;
					if (checkCommand.startsWith("@continue")) continue;

					performCommand(type, checkCommand, player, delay);
				}else{
					if (checkReturn != ""){
						if (checkReturn.startsWith("@return")) return;
						if (checkReturn.startsWith("@continue")) continue;
						
						performCommand(type, checkReturn, player, delay);
					}
				}
				
			}else if (command.startsWith("@has") && command.contains(">")){
				
				String[] checkSplit = command.split(">");
				String checkItem = checkSplit[0].substring(checkSplit[0].indexOf(" ")+1);
				String checkCommand = checkSplit[1];
				String checkReturn = "";
				
				if (checkSplit.length > 2){
					checkReturn = checkSplit[2];
				}
				
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
				
				if (hasItem){
					if (checkCommand.startsWith("@return")) return;
					if (checkCommand.startsWith("@continue")) continue;

					performCommand(type, checkCommand, player, delay);
				}else{
					if (checkReturn != ""){
						if (checkReturn.startsWith("@return")) return;
						if (checkReturn.startsWith("@continue")) continue;
						
						performCommand(type, checkReturn, player, delay);
					}
				}
				
			}else{
				performCommand(type, command, player, delay);
			}
			
			delay = 0;
		}
	}
	
	
	
	public static void performCommand(String type, String command, Player player, int delay){
		String Command = command;
		if (Command.startsWith("@@")){
			player.sendMessage(Reference.getReferences(ChatColor.translateAlternateColorCodes('&', Command.substring(2)), player));
		}else{
			if (Command.startsWith("/")) Command = Command.substring(1);
			if (type.equalsIgnoreCase("player")) runCommand((CommandSender)player, Reference.getReferences(Command, player), delay);
			if (type.equalsIgnoreCase("op")){
				if (!player.isOp()){
					player.setOp(true);
					runCommand((CommandSender)player, Reference.getReferences(Command, player), delay);
					player.setOp(false);
				}else{
					runCommand((CommandSender)player, Reference.getReferences(Command, player), delay);
				}
			}
			if (type.equalsIgnoreCase("console")) runCommand(plugin.getServer().getConsoleSender(), Reference.getReferences(Command, player), delay);
		}
	}
	
	
	
	public static void runCommand(final CommandSender sender, final String command, int delay){
		if (delay > 0){
			plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable(){
				public void run(){
					plugin.getServer().dispatchCommand(sender, command);
				}
			}, delay*20);
		}else{
			plugin.getServer().dispatchCommand(sender, command);
		}
	}	
}
