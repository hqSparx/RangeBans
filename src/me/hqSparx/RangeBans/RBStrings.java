package me.hqSparx.RangeBans;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RBStrings {
   
	public static RangeBans plugin;
	public static String banmsg = "&cSorry, you are banned from this server.";
	public static boolean broadcastblock = true;
	public static boolean broadcastpass = true;
	
	public RBStrings(RangeBans instance) {
		plugin = instance;
	}
	
	public String colorizeString(String toColor) {
        if (toColor != null)
            return toColor.replaceAll("&([0-9a-f])", "\u00A7$1");
        else
            return "";
    }
	
	public void sendHelp(CommandSender sender) {
		sender.sendMessage(colorizeString("&6" + plugin.getDescription().getName() + " " +
				plugin.getDescription().getVersion() + " by " + 
				plugin.getDescription().getAuthors().get(0)));
		sender.sendMessage(colorizeString("&a/rb /rban /rangeban                  &7  lists avaliable commands"));
		sender.sendMessage(colorizeString("&a/rb ban [IP Range]                       &7 bans given ip range"));
		sender.sendMessage(colorizeString("&a/rb unban [IP Range]                     &7unbans given ip range"));
		sender.sendMessage(colorizeString("&7ex.  /rb ban 80.9.128-192.*   /rb ban 80.9.128.0 - 80.9.192.255"));
		sender.sendMessage(colorizeString("&a/rb banhost [Hostname]                  &7 bans given hostname"));
		sender.sendMessage(colorizeString("&a/rb unbanhost [Hostname]                &7unbans given hostname"));
		sender.sendMessage(colorizeString("&7ex.  /rb banhost RU   /rb banhost blabla.someprovider.us"));
		sender.sendMessage(colorizeString("&a/rb ip [nick]                                &7 check player's ip"));
		sender.sendMessage(colorizeString("&a/rb exception [nick]                       &7adds exception"));
		sender.sendMessage(colorizeString("&a/rb removeexception [nick]              &7removes exception"));
		sender.sendMessage(colorizeString("&a/rb reload                                   &7reloads plugin"));
		sender.sendMessage(colorizeString("&a/rb listbans [page]                        &7list bans"));
		sender.sendMessage(colorizeString("&a/rb listexceptions [page]                &7list exceptions"));
		sender.sendMessage(colorizeString("&a/rb listhosts [page]                      &7list hostname bans"));

	}
	
	public void msg(CommandSender sender, String string) {
		sender.sendMessage(colorizeString(string));
	}
	
	public String kick() {
		return colorizeString(banmsg);
	}
	
	public void BroadcastBlock(String name, String ip) {
		String msg = "&7Player " + name + "(" + 
			ip + ") was kicked by RangeBans.";
	
		if (broadcastblock) {
			Player players[] = plugin.getServer().getOnlinePlayers();
			for (int i = 0; i < players.length; i++) {
				if (players[i].isOp() || plugin.commandhandler.checkPerm(players[i], "rb.broadcast"))
					players[i].sendMessage(colorizeString(msg));
			}
			plugin.logger.info(colorizeString(msg));
		}
	}
	
	public void BroadcastPass(String name, String ip) {
		String msg = "&7Player " + name + "(" + 
			ip + ") found on exceptions list. Passing.";
	
		if (broadcastpass) {
			Player players[] = plugin.getServer().getOnlinePlayers();
			for (int i = 0; i < players.length; i++) {
				if (players[i].isOp() || plugin.commandhandler.checkPerm(players[i], "rb.broadcast"))
					players[i].sendMessage(colorizeString(msg));
			}
			plugin.logger.info(colorizeString(msg));
		}
	}
	
	public void SetBroadcastBlocks(boolean set) {
		broadcastblock = set;
	}
	
	public void SetBroadcastPasses(boolean set) {
		broadcastpass = set;
	}
	
	public void SetBanMsg(String set) {
		banmsg = set;
	}
	
}
