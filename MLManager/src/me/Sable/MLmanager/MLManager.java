package me.Sable.MLmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/*
 * MLManager is the central plugin for the MLSystem group of plugins and bolt-ons.
 * 
 * It contains the majority of the databasing, as well as holding the key player data such as stats and xp.
 * 
 * All bolt-ons and extension plugins will hook into MLManager as a point of reference, allowing xp to be given for a variety of things.
 */
public class MLManager extends JavaPlugin {

	public HashMap<Player, PlayerAtt> playerList = new HashMap<Player, PlayerAtt>();
	public ArrayList<Integer> playerLevelXP = new ArrayList<Integer>();

	public static PermissionHandler permissionHandler;
	private static final Logger log = Logger.getLogger("Minecraft");
	private final MLManagerPlayerListener playerListener = new MLManagerPlayerListener(
			this);
	public final PlayerFunctions playerFunctions = new PlayerFunctions(this);
	public final DBManager dbManager = new DBManager(this);

	// DB Stuff.
	public String database;
	public String MySqlDir;
	public String MySqlPass;
	public String MySqlUser;

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();

		// Player Events.
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener,
				Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener,
				Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_KICK, playerListener,
				Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_TELEPORT, playerListener,
				Priority.High, this);

		loadDBConfig();
		loadLevels();
		setupPermissions();

		dbManager.prepare();
		
		Player[] onlinePlayers = this.getServer().getOnlinePlayers();
		
		for(int x=0;x<onlinePlayers.length;x++){
			PlayerAtt p = dbManager.getPlayerData(onlinePlayers[x]);
			playerList.put(onlinePlayers[x],p);
		}
		
		System.out.println("[MLManager] Enabled.");
	}

	public void onDisable() {
		
		System.out.println("[MLManager] Saving Player Data to DB.");
		
		for(PlayerAtt p : playerList.values()){
			dbManager.commitPlayerData(p);
		}
		
		System.out.println("[MLManager] Emptying Players from List.");
		playerList.clear();
		
		System.out.println("[MLManager] Disabled.");
	}

	public void loadLevels() {
		playerLevelXP.clear();
		
		Integer baselevel = 0;
		
		int levels = loadLvLConfig();
		
		playerLevelXP.add(baselevel);
		
		for(int x=0;x<levels;x++){
			//Level ^ 3 + 3( Level ^ 2 ) + 9Level + 9
			int xp = 2*(((x+1)*(x+1)*(x+1))+3*((x+1)*(x+1))+ 9*(x+1)+9);
			
			playerLevelXP.add(xp);
		}
		System.out.println("[MLManager] XP amounts calculated and stored.");
		
	}
	
	public int loadLvLConfig() {
		File config = new File(getDataFolder() + "/LvLConfig.txt");

		int levels = 0;

		try {

			BufferedReader br = new BufferedReader(new FileReader(config));
			String line = null;
			while ((line = br.readLine()) != null) {
				if(line.contains("Levels:")){
					String l = line;
					l = l.replace("Levels:", "");
					l = l.trim();
					levels = Integer.parseInt(l);
				}
			}
			System.out.println("[MLManager] LvLConfig.txt found and loaded.");
			br.close();

		} catch (FileNotFoundException ex) {
			System.out.println("[MLManager] LvLConfig.txt not found.");
		} catch (IOException ex) {
			System.out.println("[MLManager] LvLConfig.txt not found.");
		}
		return levels;
	}

	public void loadDBConfig() {
		File config = new File(getDataFolder() + "/DBConfig.txt");

		database = null;
		MySqlDir = null;
		MySqlPass = null;
		MySqlUser = null;

		try {

			BufferedReader br = new BufferedReader(new FileReader(config));
			String line = null;
			while ((line = br.readLine()) != null) {
				if (line.contains("database:")) {
					database=line;
					database = database.replace("database:","");
					database = database.trim();
				}
				else if (line.contains("MySqlDir:")) {
					MySqlDir=line;
					MySqlDir = MySqlDir.replace("MySqlDir:","");
					MySqlDir = MySqlDir.trim();
				}
				else if (line.contains("MySqlPass:")) {
					MySqlPass=line;
					MySqlPass =MySqlPass.replace("MySqlPass:","");
					MySqlPass =MySqlPass.trim();
				}
				else if (line.contains("MySqlUser:")) {
					MySqlUser=line;
					MySqlUser = MySqlUser.replace("MySqlUser:","");
					MySqlUser = MySqlUser.trim();
				}
			}
			System.out.println("[MLManager] DBConfig.txt found and loaded.");
			br.close();

		} catch (FileNotFoundException ex) {
			System.out.println("[MLManager] DBConfig.txt not found.");
		} catch (IOException ex) {
			System.out.println("[MLManager] DBConfig.txt not found.");
		}
	}

	public void setupPermissions() {
		Plugin permissionsPlugin = this.getServer().getPluginManager()
				.getPlugin("Permissions");
		if (permissionHandler == null) {
			if (permissionsPlugin != null) {
				permissionHandler = ((Permissions) permissionsPlugin)
						.getHandler();
			} else {
				log.info("Permission system not detected, defaulting to OP");
			}
		}
	}

	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {

		// Admin command to give XP.
		if (commandLabel.equalsIgnoreCase("givexp") && args.length>1) {
			
			if(permissionHandler.has((Player)sender,"mlsystem.xpgive")){
				Player recipient = this.getServer().getPlayer(args[0]);
				try{
				int amount = Integer.parseInt(args[1]);
				if(recipient==null){
					sender.sendMessage(args[0] + " is not Online.");
				}
				if(recipient!=null){
				recipient.sendMessage("You have received " + amount + "XP.");
				playerFunctions.addXP(recipient, amount);
				}
				}catch(NumberFormatException e){
					sender.sendMessage("Format should be /givexp name amount.");
				}
				return true;
			}	
			return true;
		}
		// Lets a player view their Level and XP.
		if (commandLabel.equalsIgnoreCase("level")) {
			playerFunctions.viewLevel((Player) sender);
			return true;
		}
		
		// Lets a player assign spare stat points.
		if (commandLabel.equalsIgnoreCase("assign") && args.length>1) {
			try{
			int amount = Integer.parseInt(args[1]);
			playerFunctions.assignStat((Player) sender, args[0], amount);
			}catch(NumberFormatException e){
				sender.sendMessage("Format should be /assign stat amount.");
			}
			
			return true;
		}
		// Lets a player view their Level and XP.
		if (commandLabel.equalsIgnoreCase("stats")) {
			playerFunctions.viewStats((Player) sender);
			return true;
		} 
			
		return false;
	
	}

}
