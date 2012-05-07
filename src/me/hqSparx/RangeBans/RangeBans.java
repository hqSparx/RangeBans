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
//import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
//import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RangeBans extends JavaPlugin {
	
	public static RangeBans plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public final RBStrings strings = new RBStrings(this);
	public final RBCommandHandler commandhandler = new RBCommandHandler(this);
	public final RBPlayerListener listener = new RBPlayerListener(this);
	
	public static List<RBIPFields> list = new ArrayList<RBIPFields>(1024);
	public static List<String> exceptions = new ArrayList<String>(1024);
	public static List<String> hostnames = new ArrayList<String>(1024);
	public static boolean whitelist = false;
	
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " is now disabled.");
	}
	
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
	    pm.registerEvents(this.listener, this);
		PluginDescriptionFile pdfFile = this.getDescription();
		
		try {
			loadConfiguration();
			loadLists();
		} catch (Exception e) { 
			e.printStackTrace();
		}
		this.logger.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is now enabled.");
		
	}
	
    public void loadConfiguration() throws IOException {
    	File cfgFile = new File(this.getDataFolder() + "/config.yml");
    	YamlConfiguration config = YamlConfiguration.loadConfiguration(cfgFile);
    	config.addDefault("broadcast-kicks", true);
    	config.addDefault("broadcast-passes", true);
    	config.addDefault("use-hostnames-as-whitelist", false);
    	config.addDefault("ban-msg", "&cSorry, you are banned from this server.");
    	config.addDefault("enable-hostname-bans", false);
    	config.options().copyDefaults(true);
        config.save(cfgFile);
	    strings.SetBroadcastBlocks(config.getBoolean("broadcast-kicks"));  
	    strings.SetBroadcastPasses(config.getBoolean("broadcast-passes"));  
	    whitelist = config.getBoolean("use-hostnames-as-whitelist");
	    strings.SetBanMsg(config.getString("ban-msg")); 
	    listener.enableHostnames(config.getBoolean("enable-hostname-bans"));  
	 }
    
    //TODO refactor it
    public void loadLists() throws IOException {
    	File bansFile = new File(this.getDataFolder() + "/bans.txt");
    	File exceptionsFile = new File(this.getDataFolder() + "/exceptions.txt");
    	File hostnamesFile = new File(this.getDataFolder() + "/hostnames.txt");

    	try {
    		BufferedReader input =  new BufferedReader(new FileReader(bansFile));
	    	try {
		        String line;
		        while ((line = input.readLine()) != null) {
		        	line = line.trim();
			        if (line.length() > 0) {
			        	boolean dont = false;
			        	for(int i = 0; i < list.size(); i++) {
			        		if(line.contains(list.get(i).Address)) dont = true;
			        	}
			        	if(!dont) add(commandhandler.checkIP(line));
			        }
			     }
	    	 } finally {
	    		 input.close();
	    	 }
	    } catch (Exception e) { logger.info("[RangeBans] Cant load bans.txt"); }
    	 
    	
    	try {
        	BufferedReader input =  new BufferedReader(new FileReader(exceptionsFile));
	    	try {
		        String line;
		        while ((line = input.readLine()) != null) {
		        	line = line.trim();
			        if (line.length() > 0) {
			        	boolean dont = false;
			        	for(int i = 0; i < exceptions.size(); i++) {
			        		if(line.contains(exceptions.get(i))) dont = true;
			        	}
			        	if(!dont) exceptions.add(line);
			        }
		        }
	    	} finally {
	    		input.close();
     	    }
    	} catch (Exception e) { logger.info("[RangeBans] Cant load exceptions.txt"); } 	
    	
    	try {
        	BufferedReader input =  new BufferedReader(new FileReader(hostnamesFile));
	    	try {
		        String line;
		        while ((line = input.readLine()) != null) {
		        	line = line.trim();
			        if (line.length() > 0) {
			        	boolean dont = false;
			        	for(int i = 0; i < hostnames.size(); i++) {
			        		if(line.contains(hostnames.get(i))) dont = true;
			        	}
			        	if(!dont) hostnames.add(line);
			        }
		        }
	    	} finally {
	    		input.close();
     	    }
    	} catch (Exception e) { logger.info("[RangeBans] Cant load hostnames.txt"); } 	
    	
    }
    
  //TODO refactor it
    public void saveLists() throws IOException {
    	File bansFile = new File(this.getDataFolder() + "/bans.txt");
    	File exceptionsFile = new File(this.getDataFolder() + "/exceptions.txt");
    	File hostnamesFile = new File(this.getDataFolder() + "/hostnames.txt");
    	
    	try {
    		BufferedWriter output =  new BufferedWriter(new FileWriter(bansFile));
    		try {
    			List<String> written = new ArrayList<String>(1024);
    			for (int i = 0; i < list.size(); i++) {
    				boolean dont = false;
    				for (int j = 0; j < written.size(); j++) {
		    		   if (written.get(j).contentEquals(list.get(i).Address)) dont = true;
    				}
					if(!dont) {
					output.write(list.get(i).Address + "\r\n");
					written.add(list.get(i).Address);
					}
    			}
    		} finally {
    			output.close();
		    }
    	} catch (Exception e) { e.printStackTrace(); }
	    	 
	   	try {
		   	BufferedWriter output =  new BufferedWriter(new FileWriter(exceptionsFile));
			try {
				List<String> written = new ArrayList<String>(1024);
				for (int i = 0; i < exceptions.size(); i++) {
    				boolean dont = false;
					for (int j = 0; j<written.size(); j++) {
						if (written.get(j).contentEquals(exceptions.get(i))) dont = true;
					}
					if(!dont) {
						output.write(exceptions.get(i) + "\r\n");
						written.add(exceptions.get(i));
					}
					}
			    } finally {
			    	output.close();
			    }
			} catch (Exception e) { e.printStackTrace(); }	
	   	
	   	try {
		   	BufferedWriter output =  new BufferedWriter(new FileWriter(hostnamesFile));
			try {
				List<String> written = new ArrayList<String>(1024);
				for (int i = 0; i < hostnames.size(); i++) {
					boolean dont = false;
					for (int j = 0; j<written.size(); j++) {
						if (written.get(j).contentEquals(hostnames.get(i))) dont = true;
				}
					if(!dont) {
					output.write(hostnames.get(i) + "\r\n");
					written.add(hostnames.get(i));
					}
					}
			    } finally {
			    	output.close();
			    }
			} catch (Exception e) { e.printStackTrace(); }	
	   	
    }
    
    public boolean doReload(CommandSender sender){
    	PluginDescriptionFile pdfFile = getDescription();
    	String reloaded = pdfFile.getName() + " version " + pdfFile.getVersion() + " reloaded.";
    	try {
    		loadConfiguration();
    	} catch (Exception e) { e.printStackTrace(); }
    	try {
    		loadLists();
    	} catch (Exception e) { e.printStackTrace(); }		
    	strings.msg(sender, "&a" + reloaded);
		return true;
    }
    
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return commandhandler.command(sender, args);
	}
	
	public boolean add(RBIPFields addr) {
		if (list.add(addr)) 
			return true;
		else 
			return false;
	}
	
	public boolean remove(RBIPFields addr) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).Address.contentEquals(addr.Address)) {
				list.remove(i); 
				return true;	
			}
		}
		return false;
	}
	
	public String getBan(int i){
		return list.get(i).Address;
	}
	
	public String getException(int i) {
		return exceptions.get(i);
	}
	
	public String getHost(int i) {
		return hostnames.get(i);
	}

	public boolean checkIP(byte a, byte b, byte c, byte d) {
		for (int i = 0; i < bansSize(); i++) {
			if (list.get(i).checkmin(a, b, c, d) && list.get(i).checkmax(a, b, c, d)) 
				return true;
		}
		return false;
	}
	
	public int bansSize() {
		return list.size();	
	}
	
	public int exceptionsSize() {
		return exceptions.size();	
	}
	
	public int hostsSize() {
		return hostnames.size();	
	}
	
	public boolean addexception(String name) {
		if (exceptions.add(name)) 
			return true;
		else 
			return false;
	}
	
	public boolean removeexception(String name) {
		for (int i = 0; i < exceptions.size(); i++) {
			if (exceptions.get(i).contentEquals(name)) {
				exceptions.remove(i); return true;	
			}
		}
		return false;
	}
	
	public boolean checkexception(String name) {
		for (int i = 0; i < exceptions.size(); i++) {
			if (exceptions.get(i).contentEquals(name))
				return true;		
			}
		
		return false;	
	}	
	
	public boolean addhostname(String hostname) {
		if (hostnames.add(hostname)) 
			return true;
		else 
			return false;
	}
	
	public boolean removehostname(String hostname) {
		for (int i = 0; i < hostnames.size(); i++) {
			if (hostnames.get(i).contentEquals(hostname)) {
				hostnames.remove(i); return true;	
			}
		}
		return false;
	}
	
	public boolean checkhostname(String hostname) {
		String[] split = hostname.split("\\.");
		for(int i = split.length - 1; i >= 0; i--){
			
			//backward
			String merged = ""; 
			for(int j = i; j < split.length; j++) {
				merged += split[j];
				if(j < split.length - 1) merged += ".";
			}
			
			for (int k = 0; k < hostnames.size(); k++) {;
				if (hostnames.get(k).equalsIgnoreCase(merged))
					if(whitelist) return false;
					else return true;
			}
		}
		if(whitelist) return true;
		else return false;
	}

	
}