package me.hqSparx.RangeBans;

import org.bukkit.command.CommandSender;

public class RBCommandHandler {
   
	public static RangeBans plugin;
	public static String regex = "[0-9\\.\\-\\*]*";
	public RBCommandHandler(RangeBans instance) {
		plugin = instance;
	}

	public byte checkByte(String word) {
		if (Integer.parseInt(word)>127) 
			return (byte)(Integer.parseInt(word)-256);
		else 
			return Byte.parseByte(word);
	}
	
	public String mergeargs(String[] tomerge) {
		String output = "";
		for(int i = 1; i < tomerge.length; i++)
			output += tomerge[i].trim();
		
		return output;
	}
	
	public RBIPFields checkIP(String ip) {
		ip = ip.replace(" ", "");
		byte[] min = new byte[4];
		byte[] max = new byte[4];
		String[] split = ip.split("\\.");
		String ip2 = "";
		
		if (split.length > 5) {
			String[] separated = ip.split("\\-");
			String[] one = separated[0].split("\\.");
			String[] two = separated[1].split("\\.");
			String toip1 = ""; String toip2 = "";
			for(int i = 0; i < 4; i++) {	
				min[i] = checkByte(one[i]);
				max[i] = checkByte(two[i]);
				toip1 += one[i]; toip2 += two[i];
				if (i<3) {
					toip1 += "."; 
					toip2 += ".";
				}
			}
			ip2 = toip1 + " - " + toip2;
		} else {
			for(int i = 0; i < 4; i++) {		
				if (split.length - 1 < i) {
					min[i] = 0; max[i] = -128;	
					ip2 += "*";	
				} else if (split[i] == null || split[i].contentEquals("*") 
							|| split[i].contentEquals("")) {
					min[i] = 0; max[i] = -128;	
					ip2 += "*";
				} else if (split[i].contains("-")){
					String[] split2 = split[i].split("\\-");
					min[i] = checkByte(split2[0]);
					max[i] = checkByte(split2[1]);
					ip2 += split2[0] + "-" + split2[1];
				} else {
					min[i] = checkByte(split[i]);
					max[i] = checkByte(split[i]);
					ip2 += split[i];
				}
				/*
				plugin.logger.info(min[0]+"-"+max[0]+"."+min[1]+"-"+max[1]+"."+
						min[2]+"-"+max[2]+"."+min[3]+"-"+max[3]);
				*/
				if (i < 3)
					ip2 += ".";
			}
		}
		return new RBIPFields(min, max, ip2);
	}
	
	public void ban(CommandSender sender, String[] args) {
		String ip = mergeargs(args);
		if (ip.matches(regex)) {
			RBIPFields range = checkIP(ip);
			if (plugin.add(range)) {
				try {
					plugin.saveLists();
				} catch (Exception e) { e.printStackTrace(); }
				plugin.strings.msg(sender, "&eBanning IP range: " + range.Address);
				plugin.logger.info(sender.getName() + " banned IP range: " + range.Address);
			} else 
				plugin.strings.msg(sender, "&cFailed to ban: " + range.Address);
		} else 
			plugin.strings.msg(sender, "&cWrong IP range, check syntax");
	}
	
	public void unban(CommandSender sender, String[] args) {
		String ip = mergeargs(args);
		if (ip.matches(regex)) {
			RBIPFields range = checkIP(ip);
			if (plugin.remove(range)) {
				try {
					plugin.saveLists();
				} catch (Exception e) { e.printStackTrace(); }
				plugin.strings.msg(sender, "&eUnbanning IP range: " + range.Address);
				plugin.logger.info(sender.getName() + " unbanned IP range: " + range.Address);
			} else 
				plugin.strings.msg(sender, "&cFailed to unban: " + range.Address);
		} else 
			plugin.strings.msg(sender, "&cWrong IP range, check syntax");
	}
	
	public void exception(CommandSender sender, String name) {
		 if (plugin.addexception(name)) {
	    	try {
	    		plugin.saveLists();
	    	} catch (Exception e) { e.printStackTrace(); }
	    	plugin.strings.msg(sender, ("&eAdding nickname exception: " + name));
	    	plugin.logger.info(sender.getName() + " added nickname exception: " + name);
		 } else 
			 plugin.strings.msg(sender, ("&cFailed to add nickname exception: " + name));
	}
	
	public void removeexception(CommandSender sender, String name) {
		if (plugin.removeexception(name)) {
			try {
	    	 plugin.saveLists();
	    	} catch (Exception e) { e.printStackTrace(); }
			plugin.strings.msg(sender, ("&eRemoving nickname exception: " + name));
			plugin.logger.info(sender.getName() + " removed nickname exception: " + name);
		} else plugin.strings.msg(sender, ("&cFailed to remove nickname exception: " + name));
	}
	
	public void checkip(CommandSender sender, String name) {
		String ip = "";
		try {
			ip = plugin.getServer().getPlayer(name).getAddress().getAddress().getHostAddress();
			plugin.strings.msg(sender, ("&7" + plugin.getServer().getPlayer(name).getName() + "'s IP: &a" + ip));
		} catch (Exception e) {
			if (ip == null || ip == "") 
				plugin.strings.msg(sender, ("&cFailed to load player's IP: " + name));
		}
	}
	
	public void bansList(CommandSender sender, String pagestr) {
		final int PER_PAGE = 10;
		int page = Integer.parseInt(pagestr);
		int pos = PER_PAGE * (page - 1);
		int size = plugin.size();
		
		String header = "&6Bans list (page " + page + "/" +  ( size / PER_PAGE + 1 ) + ")";	
		plugin.strings.msg(sender, header);
		
		for (int i = pos; i < pos + PER_PAGE; i++) {
			String line = "";
			if (i < plugin.size())
				line = "&7#" + (i + 1) + " &a" + plugin.get(i);
			plugin.strings.msg(sender, line);
		}
	}
	
	public void exceptionsList(CommandSender sender, String pagestr) {
		final int PER_PAGE = 10;
		int page = Integer.parseInt(pagestr);
		int pos = PER_PAGE * (page - 1);
		int size = plugin.exceptionssize();
		
		String header = "&6Exceptions list (page " + page + "/" + ( size / PER_PAGE + 1 ) + ")";		
		plugin.strings.msg(sender, header);
		
		for (int i = pos; i < pos + PER_PAGE; i++) {
			String line = "";
			if (i < plugin.exceptionssize())
				line = "&7#" + (i + 1) + " &a" + plugin.getException(i);
			plugin.strings.msg(sender, line);
		}
	}
	

}
