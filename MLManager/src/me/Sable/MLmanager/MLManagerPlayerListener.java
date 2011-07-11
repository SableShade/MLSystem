package me.Sable.MLmanager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class MLManagerPlayerListener extends PlayerListener{
	
 private final MLManager plugin;
 
 public MLManagerPlayerListener(MLManager instance){
	 this.plugin=instance;
 }
 
 public void onPlayerJoin(PlayerJoinEvent event){
	 Player p = event.getPlayer();
	 
	 //Checking that player is in DB.
	 boolean dbhas = plugin.dbManager.contains(p);
	 
	 if(dbhas==false){
		 plugin.dbManager.newP(p);
	 }
	 	//Getting PlayerAtt from DB.
	 	PlayerAtt playerAtt = plugin.dbManager.getPlayerData(p);
	 
	 	//Putting in the playerList.
	 	plugin.playerList.put(p,playerAtt);
 }
 
 public void onPlayerQuit(PlayerQuitEvent event){
	 Player p = event.getPlayer();
	 
	 //Getting PlayerAtt from playerList.
	 PlayerAtt playerAtt = plugin.playerList.get(p);
	 
	 //Committing to DB.
	 plugin.dbManager.commitPlayerData(playerAtt);
	 
	 //Removing Player and PlayerAtt from playerList.
	 plugin.playerList.remove(p);
 }
 
 public void onPlayerKick(PlayerKickEvent event){
	 Player p = event.getPlayer();
	 
	 //Getting PlayerAtt from playerList.
	 PlayerAtt playerAtt = plugin.playerList.get(p);
	 
	 //Committing to DB.
	 plugin.dbManager.commitPlayerData(playerAtt);
	 
	 //Removing Player and PlayerAtt from playerList.
	 plugin.playerList.remove(p);
 }
 
 //Notifies Players of the World they enter.
 public void onPlayerTeleport(PlayerTeleportEvent event){
	 Player player = event.getPlayer();
	 
	 String world = player.getWorld().toString();
	 world=world.replace("CraftWorld{name=", "");
	 world=world.replace("}","");
	 world=world.trim();
	 
	 player.sendMessage(ChatColor.GOLD + "You have entered " +world+ ".");
 }

}

