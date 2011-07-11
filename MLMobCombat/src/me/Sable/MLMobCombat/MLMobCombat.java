package me.Sable.MLMobCombat;


import me.Sable.MLmanager.MLManager;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class MLMobCombat extends JavaPlugin {
	
	private final MCEntityListener entityListener = new MCEntityListener(this);
	public static PermissionHandler permissionHandler;
	public MLManager mlManager;
	
	public void onEnable(){
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvent(Event.Type.ENTITY_DAMAGE,entityListener,Priority.High,this);
		
		setupPermissions();
		
		mlManager = (MLManager)this.getServer().getPluginManager().getPlugin("MLManager");
		
		if (mlManager == null) {
			System.out.println("[MLMobCombat] Could not link to MLManager...Disabling.");
			this.getServer().getPluginManager().disablePlugin(this);
		}		

		System.out.println("[MLMobCombat] Enabled.");
	}
	
	public void onDisable(){
		System.out.println("[MLMobCombat] Disabled.");
	}
	
	public void setupPermissions() {
		Plugin permissionsPlugin = this.getServer().getPluginManager()
				.getPlugin("Permissions");
		if (permissionHandler == null) {
			if (permissionsPlugin != null) {
				permissionHandler = ((Permissions) permissionsPlugin)
						.getHandler();
			} else {
				System.out.println("Permission system not detected, defaulting to OP");
			}
		}
	}

}
