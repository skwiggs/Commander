package org.Dragonphase.Commander.Permissions;

import org.Dragonphase.Commander.Commander;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.permission.Permission;

public class VaultPerms {
	public static Commander plugin;
	public static Permission permission = null;
	public static Player player = null;
	
	public VaultPerms(Commander instance){
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
    	VaultPerms.player = player;
    }
    
    public boolean hasPermission(String permission){
    	return VaultPerms.permission.playerHas(player, permission);
    }
}
