package me.hqSparx.RangeBans;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RangeBans extends JavaPlugin {
	
	public static RangeBans plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public final RBStrings strings = new RBStrings(this);
	public final RBCommandHandler commandhandler = new RBCommandHandler(this);
	public final RBPlayerListener listener = new RBPlayerListener(this);
	
	public static List<RBIPFields> list = new ArrayList<RBIPFields>(1024);
	public static List<String> exceptions = new ArrayList<String>(1024);
	
	
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
	}
	
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_LOGIN, this.listener, Event.Priority.Lowest, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, this.listener, Event.Priority.Lowest, this);
		PluginDescriptionFile pdfFile = this.getDescription();
		
		try{
		loadConfiguration();}
		catch (Exception e) {e.printStackTrace();}
		try{
		loadLists();}
		catch (Exception e) {e.printStackTrace();}
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is now enabled.");
		
	}
	
    private void loadConfiguration() throws IOException {
		 File cfgFile = new File(this.getDataFolder() + "/config.yml");
		 YamlConfiguration config = YamlConfiguration.loadConfiguration(cfgFile);
		 config.addDefault("broadcast-kicks", true);
		 config.addDefault("broadcast-passes", true);
		 config.addDefault("ban-msg", "&cSorry, your IP range is banned from this server.");
	     config.options().copyDefaults(true);
        config.save(cfgFile);
	    strings.SetBroadcastBlocks(config.getBoolean("broadcast-kicks"));  
	    strings.SetBroadcastPasses(config.getBoolean("broadcast-passes"));  
	    strings.SetBanMsg(config.getString("ban-msg")); 
	 }
	
    public void loadLists() throws IOException {
    	File bansFile = new File(this.getDataFolder() + "/bans.txt");
    	File exceptionsFile = new File(this.getDataFolder() + "/exceptions.txt");
    	
    	try{
    	BufferedReader input =  new BufferedReader(new FileReader(bansFile));

    	 try {
        String line;
        while((line = input.readLine()) != null){
        	line = line.trim();
        if (line.length() > 0) {
            boolean dont = false;
        	for(int i = 0; i < list.size(); i++) if(line.contentEquals(list.get(i).Address)) dont = true;
        	if(!dont) add(commandhandler.checkIP(line));
        }}
    	 }
    	 finally {
    		 input.close();
    	      }}
    	catch (Exception e) {logger.info("Cant load bans.txt");}
    	 
    	
    	try{
        	BufferedReader input =  new BufferedReader(new FileReader(exceptionsFile));
    	 try {
        String line;
        while((line = input.readLine()) != null){
        	line = line.trim();
        if (line.length() > 0) {
            boolean dont = false;
        	for(int i = 0; i < exceptions.size(); i++) if(line.contentEquals(exceptions.get(i))) dont = true;
        	if(!dont) exceptions.add(line);
        }
         }
     	 }
     	 finally {
     		 input.close();
     	      }}
    	catch (Exception e) {logger.info("Cant load exceptions.txt");}
           	
    }
    
    
    public void saveLists() throws IOException {
    	File bansFile = new File(this.getDataFolder() + "/bans.txt");
    	File exceptionsFile = new File(this.getDataFolder() + "/exceptions.txt");
   	 try {
   	BufferedWriter output =  new BufferedWriter(new FileWriter(bansFile));
	 try {
		 List<String> written = new ArrayList<String>(1024);
		   for(int i = 0; i<list.size(); i++){
	    	   boolean dont = false;
			for(int j = 0; j<written.size(); j++) if(written.get(j).contentEquals(list.get(i).Address)) dont = true;
			if(!dont) output.write(list.get(i).Address + "\r\n");
		       }
	        }
	     	 finally {
	     		 output.close();
	     	      }}
	    	catch (Exception e) {e.printStackTrace();}
    	 
   	 try {
   	BufferedWriter output =  new BufferedWriter(new FileWriter(exceptionsFile));
	 try {
		 List<String> written = new ArrayList<String>(1024);
	       for(int i = 0; i<exceptions.size(); i++){
	    	   boolean dont = false;
			for(int j = 0; j<written.size(); j++) if(written.get(j).contentEquals(exceptions.get(i))) dont = true;
			if(!dont) output.write(exceptions.get(i) + "\r\n");
	       }
	     	 }
	     	 finally {
	     		 output.close();
	     	      }}
	    	catch (Exception e) {e.printStackTrace();}
    	
    	
    }
    
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if(sender.isOp() || sender.hasPermission("RB.access")){
			if(args.length == 0 || args.length > 4) {
				strings.sendHelp(sender);
				return true;
							}
			if(args[0].equals("reload")){
				PluginDescriptionFile pdfFile = this.getDescription();
				String reloaded = pdfFile.getName() + " version " + pdfFile.getVersion() + " reloaded.";
				try{
				loadConfiguration();}
				catch (Exception e) {e.printStackTrace();}
				try{
				loadLists();}
				catch (Exception e) {e.printStackTrace(); }		
				strings.msg(sender, "&a" + reloaded);
				return true;
			}
			if(args[0].equals("ban") && args.length >= 2) {
				commandhandler.ban(sender, args);
				return true;
		  					}
			if(args[0].equals("unban") && args.length >= 2) {
				commandhandler.unban(sender, args);
				return true;
		  					}
			if(args[0].equals("exception") && args.length == 2) {
				commandhandler.exception(sender, args[1]);
				return true;
		  					}
			if(args[0].equals("removeexception") && args.length == 2) {
				commandhandler.removeexception(sender, args[1]);
				return true;
		  					}
			if(args[0].equals("ip") && args.length == 2) {
				commandhandler.checkip(sender, args[1]);
				return true;
		  					}
			strings.msg(sender, "Oops! Wrong syntax, check /rb for help.");
				}
		
		else strings.msg(sender, "Sorry, you cannot access this command.");	
	return false;
	}
		public boolean add(RBIPFields addr)
		{
		if(list.add(addr)) return true;
		else return false;
		}
		public boolean remove(RBIPFields addr)
		{
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).Address.contentEquals(addr.Address)){
			list.remove(i); return true;	
			}}
			return false;
		}
		public boolean checkmin(int i, byte a, byte b, byte c, byte d)
		{
		//	logger.info("bmin0:" + list.get(i).bMin[0] + " bmin1:" + list.get(i).bMin[1] + " bmin2:" + list.get(i).bMin[2] + " bmin3:" + list.get(i).bMin[3]);
		//	logger.info("bmax0:" + list.get(i).bMax[0] + " bmax1:" + list.get(i).bMax[1] + " bmax2:" + list.get(i).bMax[2] + " bmax3:" + list.get(i).bMax[3]);
		if(list.get(i).checkmin(a, b, c, d)) return true;
		return false;
		}
		public boolean checkmax(int i, byte a, byte b, byte c, byte d)
		{
		if(list.get(i).checkmax(a, b, c, d)) return true;
		return false;
		}
		public int size()
		{
		return list.size();	
		}
		public int exceptionssize()
		{
		return exceptions.size();	
		}
		public boolean addexception(String name)
		{
		if(exceptions.add(name)) return true;
		else return false;
		}
		public boolean removeexception(String name)
		{
			for(int i = 0; i < exceptions.size(); i++){
				if(exceptions.get(i).contentEquals(name)){
				exceptions.remove(i); return true;	
				}}
				return false;
		}
		public boolean checkexception(String name)
		{
			for(int i = 0; i < exceptions.size(); i++){
				if(exceptions.get(i).contentEquals(name))return true;		
			}
			return false;	
		}
		
		
}
