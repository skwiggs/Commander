package org.Dragonphase.Commander.Util;

import net.milkbowl.vault.permission.Permission;

import org.Dragonphase.Commander.Commander;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class Permissions {
	public static Commander plugin;
	public static Permission permission = null;
	public static Player player = null;

	private static String COMMANDER = "commander";
	private static String COMMANDER_DEVICE = COMMANDER + ".device";
	private static String COMMANDER_ADMIN = COMMANDER + ".admin";
	private static String COMMANDER_DEVICE_USE = COMMANDER_DEVICE + ".use";
	
	public static String COMMANDER_DEVICE_USE_PLAYER = COMMANDER_DEVICE_USE + ".player";
	public static String COMMANDER_DEVICE_USE_OP = COMMANDER_DEVICE_USE + ".op";
	public static String COMMANDER_DEVICE_USE_CONSOLE = COMMANDER_DEVICE_USE + ".console";
	
	public static String COMMANDER_DEVICE_BREAK = COMMANDER_DEVICE + ".break";
	
	public static String COMMANDER_ADMIN_ADD = COMMANDER_ADMIN + ".add";
	public static String COMMANDER_ADMIN_REMOVE = COMMANDER_ADMIN + ".remove";
	public static String COMMANDER_ADMIN_REM = COMMANDER_ADMIN_REMOVE;
	public static String COMMANDER_ADMIN_CLEAR = COMMANDER_ADMIN + ".clear";
	public static String COMMANDER_ADMIN_CHECK = COMMANDER_ADMIN + ".check";
	public static String COMMANDER_ADMIN_LIST = COMMANDER_ADMIN + ".list";
	public static String COMMANDER_ADMIN_RELOAD = COMMANDER_ADMIN + ".reload";

	public Permissions(Commander instance){
		plugin = instance;
		setupPermissions();
	}
	
    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = plugin.getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        
        return (permission != null);
    }
    
    public void setPlayer(Player player){
    	Permissions.player = player;
    }

    public boolean hasPermission(String permission){
    	return Permissions.permission.playerHas(player, permission);
    }
    
    public boolean inGroup(String group){
    	return Permissions.permission.playerInGroup(player, group);
    }
    
    public String getGroup(Player player){
    	return Permissions.permission.getPrimaryGroup(player);
    }
}
