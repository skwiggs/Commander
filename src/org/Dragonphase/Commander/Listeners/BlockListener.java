package org.Dragonphase.Commander.Listeners;

import java.util.logging.Logger;

import org.Dragonphase.Commander.Commander;
import org.Dragonphase.Commander.Util.Device;
import org.Dragonphase.Commander.Util.Permissions;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Button;
import org.bukkit.material.Lever;
import org.bukkit.material.PressurePlate;

public class BlockListener implements Listener{
	public final Logger logger = Logger.getLogger("Minecraft");
	public static Commander plugin;
	
	public BlockListener(Commander instance){
		plugin = instance;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		new Device(plugin);
		final Player player = event.getPlayer();
		Permissions permissions = new Permissions(plugin);
		permissions.setPlayer(player);
		Block block = event.getClickedBlock();
		
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK){
			if (block.getState().getData() instanceof Button || block.getState().getData() instanceof Lever){
				String deviceID = Device.getDeviceID(block);
				for (String key : Device.getCommands()){
					if (key.equalsIgnoreCase(deviceID)){
						if (permissions.hasPermission(Permissions.COMMANDER_DEVICE_USE_PLAYER)) Device.parseCommand("player", Device.getCommandList(deviceID + ".player.commands"), player);
						if (permissions.hasPermission(Permissions.COMMANDER_DEVICE_USE_OP)) Device.parseCommand("op", Device.getCommandList(deviceID + ".op.commands"), player);
						if (permissions.hasPermission(Permissions.COMMANDER_DEVICE_USE_CONSOLE)) Device.parseCommand("console", Device.getCommandList(deviceID + ".console.commands"), player);
					}
				}
			}
		}
		
		if (event.getAction() == Action.PHYSICAL){
			if (block.getState().getData() instanceof PressurePlate){
				String deviceID = Device.getDeviceID(block);
				for (String key : Device.getCommands()){
					if (key.equalsIgnoreCase(deviceID)){
						if (permissions.hasPermission(Permissions.COMMANDER_DEVICE_USE_PLAYER)) Device.parseCommand("player", Device.getCommandList(deviceID + ".player.commands"), player);
						if (permissions.hasPermission(Permissions.COMMANDER_DEVICE_USE_OP)) Device.parseCommand("op", Device.getCommandList(deviceID + ".op.commands"), player);
						if (permissions.hasPermission(Permissions.COMMANDER_DEVICE_USE_CONSOLE)) Device.parseCommand("console", Device.getCommandList(deviceID + ".console.commands"), player);
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		Player player = event.getPlayer();
		Permissions permissions = new Permissions(plugin);
		permissions.setPlayer(player);
		Block block = event.getBlock();
		
		for (String key : Device.getCommands()){
			String blockID = Device.getDeviceID(block);
			if (key.equalsIgnoreCase(blockID)){
				if (permissions.hasPermission(Permissions.COMMANDER_DEVICE_BREAK)){
					Device.clear(block, player);
				}else{
					player.sendMessage(ChatColor.RED + "You do not have permission to break this device!");
					event.setCancelled(true);
				}
			}
		}
	}
}
