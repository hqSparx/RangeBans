package me.hqSparx.RangeBans;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class RBPlayerListener implements Listener {
	
	/* DEBUG
	Calendar cal;
	long start, stop;
	*/
   
	public static RangeBans plugin;
	public static boolean hostnames = false;
	public RBPlayerListener(RangeBans instance) {
		plugin = instance;
	}
	
	public byte checkByte(String word) {
		if (Integer.parseInt(word)>127) 
			return (byte)(Integer.parseInt(word)-256);
		else 
			return Byte.parseByte(word);
		}
	
	public void enableHostnames(boolean boo){
		hostnames = boo;
	}
	
	@EventHandler
	public void onPlayerLogin (PlayerLoginEvent event)
	{
		/* DEBUG
		cal = Calendar.getInstance();  
		start = cal.getTimeInMillis();
		*/
		
        if (!event.getResult().equals(Result.ALLOWED))
            return;
        
		String ipstring  = event.getKickMessage();
		String name = event.getPlayer().getName();

			
		if (ipstring == "" || ipstring == null) {
			plugin.logger.info("[RangeBans] Warning! Couldn't load "+name
				+"'s IP: " + ipstring); 
			return;
		}
		//plugin.logger.info("[RangeBans] " + name + " connected. Detected ip: " 
		//						+ ipstring);
		String[] split = ipstring.split("\\.");
		byte[] ip = new byte[4];
		ip[0] = checkByte(split[0]);
		ip[1] = checkByte(split[1]);
		ip[2] = checkByte(split[2]);
		ip[3] = checkByte(split[3]);

			if (plugin.checkIP(ip[0], ip[1], ip[2], ip[3])) {
				if (plugin.checkexception(name)) {
					plugin.strings.BroadcastPass(name, ipstring);
					return;
				} else {
					plugin.strings.BroadcastBlock(name, ipstring);
					event.disallow(Result.KICK_OTHER, plugin.strings.kick());
					event.setResult(Result.KICK_OTHER);
					return;
				}
			}
			
	if(hostnames){
				String hostname = "";
		try {
			hostname = InetAddress.getByName(ipstring).getHostName();
			}
		    catch (UnknownHostException e) {}
		
			if (hostname.length() > 0 && plugin.checkhostname(hostname)) {
				if (plugin.checkexception(name)) {
					plugin.strings.BroadcastPass(name, hostname);
					return;
			}	else {
					plugin.strings.BroadcastBlock(name, hostname);
					event.disallow(Result.KICK_OTHER, plugin.strings.kick());
					event.setResult(Result.KICK_OTHER);
					return;
				}
			
			}
	}
			
			/* DEBUG
			cal = Calendar.getInstance();  
			stop = cal.getTimeInMillis();
			*/
	}
	
	
}
	

