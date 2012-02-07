package me.hqSparx.RangeBans;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

public class RBPlayerListener extends PlayerListener {
   
	public static RangeBans plugin;
	public RBPlayerListener(RangeBans instance) {
		plugin = instance;
	}
	
	public byte checkByte(String word){
		if(Integer.parseInt(word)>127) return (byte)(Integer.parseInt(word)-256);
		else return Byte.parseByte(word);
		}
	
	boolean alternative = false;
	
	@Override
	public void onPlayerLogin (PlayerLoginEvent event)
	{
        if (!event.getResult().equals(Result.ALLOWED)) {
            return;
        }
        alternative = false;
		String ipstring  = event.getKickMessage();
		String name = event.getPlayer().getName();
		if(ipstring == "" || ipstring == null){plugin.logger.info("[RangeBans] Warning! Couldn't load "+name+"'s IP. Possibly plugins conflict. Using alternative method."); alternative = true; return;}
		plugin.logger.info("[RangeBans] Detected ip: " + ipstring);
		String[] split = ipstring.split("\\.");
		byte[] ip = new byte[4];
		ip[0] = checkByte(split[0]);
		ip[1] = checkByte(split[1]);
		ip[2] = checkByte(split[2]);
		ip[3] = checkByte(split[3]);

		if(plugin.size() > 0) for(int i = 0; i < plugin.size(); i++)
		{
			
			if(plugin.checkmin(i, ip[0], ip[1], ip[2], ip[3]) && plugin.checkmax(i, ip[0], ip[1], ip[2], ip[3])){
			if(plugin.checkexception(name))
			{
				plugin.strings.BroadcastPass(name, ipstring);
				return;
					}	
			else{

				plugin.strings.BroadcastBlock(name, ipstring);
			event.disallow(Result.KICK_OTHER, plugin.strings.kick());
				event.setResult(Result.KICK_OTHER);
				return;}}
		}

		
	}
	
	@Override
	public void onPlayerJoin (PlayerJoinEvent event)
	{
        if(alternative == true){
		String ipstring  = event.getPlayer().getAddress().getAddress().getHostAddress();
		String name = event.getPlayer().getName();
		if(ipstring == "" || ipstring == null){plugin.logger.info("[RangeBans] Warning! Couldn't load "+name+"'s IP using alternative method."); return;}
		plugin.logger.info("[RangeBans] Detected ip: " + ipstring);
		String[] split = ipstring.split("\\.");
		byte[] ip = new byte[4];
		ip[0] = checkByte(split[0]);
		ip[1] = checkByte(split[1]);
		ip[2] = checkByte(split[2]);
		ip[3] = checkByte(split[3]);

		if(plugin.size() > 0) for(int i = 0; i < plugin.size(); i++)
		{
			
			if(plugin.checkmin(i, ip[0], ip[1], ip[2], ip[3]) && plugin.checkmax(i, ip[0], ip[1], ip[2], ip[3])){
			if(plugin.checkexception(name))
			{
				plugin.strings.BroadcastPass(name, ipstring);
				return;
					}	
			else{
				plugin.strings.BroadcastBlock(name, ipstring);
				event.getPlayer().kickPlayer(plugin.strings.kick());
				return;}}
		}

        }
	}
	
	
	/*
	@Override
	public void onPlayerKick (PlayerKickEvent event) {
		if((event.getReason()).equals(plugin.strings.kick())) 
		event.setLeaveMessage(null);
	}
	*/
	
}
	

