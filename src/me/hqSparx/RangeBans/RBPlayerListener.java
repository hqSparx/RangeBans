package me.hqSparx.RangeBans;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class RBPlayerListener extends PlayerListener {
	
	/* DEBUG
	Calendar cal;
	long start, stop, start2, stop2;
	*/
   
	public static RangeBans plugin;
	public RBPlayerListener(RangeBans instance) {
		plugin = instance;
	}
	
	public byte checkByte(String word) {
		if (Integer.parseInt(word)>127) 
			return (byte)(Integer.parseInt(word)-256);
		else 
			return Byte.parseByte(word);
		}
	
	boolean alternative = false;
	
	@Override
	public void onPlayerLogin (PlayerLoginEvent event)
	{
		/* DEBUG
		cal = Calendar.getInstance();  
		start = cal.getTimeInMillis();
		*/
		
        if (!event.getResult().equals(Result.ALLOWED))
            return;
        
        alternative = false;
		String ipstring  = event.getKickMessage();
		String name = event.getPlayer().getName();
		String hostname = "";
		if (ipstring == "" || ipstring == null) {
			plugin.logger.info("[RangeBans] Warning! Couldn't load "+name
				+"'s IP. Possibly plugins conflict. Using alternative method."); 
			alternative = true; 
			return;
		}
		try {
			hostname = InetAddress.getByName(ipstring).getHostName();
		} catch (UnknownHostException e) {}
		
		plugin.logger.info("[RangeBans] " + name + " connected. Detected ip: " 
								+ ipstring + " Detected hostname: " + hostname);
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
			
			/* DEBUG
			cal = Calendar.getInstance();  
			stop = cal.getTimeInMillis();
			*/
	}
	
	@Override
	public void onPlayerJoin (PlayerJoinEvent event) {
		
		/* DEBUG
		cal = Calendar.getInstance();  
		start2 = cal.getTimeInMillis();
		*/
		
        if (alternative == true) {
			String ipstring  = event.getPlayer().getAddress().getAddress().getHostAddress();
			String name = event.getPlayer().getName();
			String hostname = "";
			if (ipstring == "" || ipstring == null) {
				plugin.logger.info("[RangeBans] Warning! Couldn't load "+name
					+"'s IP using alternative method."); 
				return;
			}
			try {
				hostname = InetAddress.getByName(ipstring).getHostName();
			} catch (UnknownHostException e) {}
			
			plugin.logger.info("[RangeBans] " + name + " connected. Detected ip: " 
									+ ipstring + " Detected hostname: " + hostname);
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
						event.getPlayer().kickPlayer(plugin.strings.kick());
						return;
					}
				}
				
				if (hostname.length() > 0 && plugin.checkhostname(hostname)) {
					if (plugin.checkexception(name)) {
						plugin.strings.BroadcastPass(name, hostname);
						return;
				}	else {
						plugin.strings.BroadcastBlock(name, hostname);
						event.getPlayer().kickPlayer(plugin.strings.kick());
						return;
					}
				
				}
        }
        /* DEBUG
        cal = Calendar.getInstance();  
        stop2 = cal.getTimeInMillis();
        this.plugin.logger.info("[RangeBans] onLogin took: " + (stop-start) + " ms.");
        this.plugin.logger.info("[RangeBans] onJoin took: " + (stop2-start2) + " ms.");
        this.plugin.logger.info("[RangeBans] Total (all plugins): " + (stop2-start) + " ms.");
        */
        }
	
	/* removes "x left the game"
	@Override
	public void onPlayerKick (PlayerKickEvent event) {
		if((event.getReason()).equals(plugin.strings.kick())) 
		event.setLeaveMessage(null);
	}
	*/
	
}
	

