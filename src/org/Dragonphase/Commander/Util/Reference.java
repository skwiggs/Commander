package org.Dragonphase.Commander.Util;

import java.util.logging.Logger;

import org.Dragonphase.Commander.Commander;
import org.bukkit.entity.Player;

public class Reference {
	public final Logger logger = Logger.getLogger("Minecraft");
	public static Commander plugin;
	
	public Reference(Commander instance){
		plugin = instance;
	}
	
	public static String getReferences(String input, Player player){
		String output = input;
		Permissions permissions = new Permissions(plugin);
		permissions.setPlayer(player);
		
		output = output.replace("@player", player.getName());
		output = output.replace("@name", player.getName());
		output = output.replace("@group", permissions.getGroup(player));
		output = output.replace("@server", plugin.getServer().getIp());
		output = output.replace("@world", player.getWorld().getName());
		output = output.replace("@time", "" + player.getWorld().getTime());
		output = output.replace("@level", "" + player.getLevel());
		output = output.replace("@exp", "" + player.getTotalExperience());
		output = output.replace("@xp", "" + player.getTotalExperience());
		output = output.replace("@health", "" + player.getHealth());
		output = output.replace("@food", "" + player.getFoodLevel());
		output = output.replace("@mode", player.getGameMode().name());
		output = output.replace("@gm", player.getGameMode().name());
		output = output.replace("@location", "" + player.getLocation().getX() + " " + player.getLocation().getY() + " " + player.getLocation().getZ());
		output = output.replace("@loc", "" + player.getLocation().getX() + " " + player.getLocation().getY() + " " + player.getLocation().getZ());
		
		return output;
	}
}
