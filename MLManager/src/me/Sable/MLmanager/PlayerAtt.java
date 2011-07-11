package me.Sable.MLmanager;

import org.bukkit.entity.Player;

public class PlayerAtt {

	// For reference with relevant Player.
	private Player player;
	private int playerID;
	
	MLManager plugin;

	// Core Player Level and XP.
	private int playerLevel;
	private int xpNext;
	private int xpTotal;
	private boolean atMax;

	// Core Player Stats.
	private int strength;
	private int vitality;
	private int dexterity;
	private int agility;
	private int spirit;

	// Spare Stat Points.
	private int spare;

	public PlayerAtt(Player p, int pid, int xs, int spa, MLManager instance) {
		this.player=p;
		this.playerID=pid;
		this.xpTotal=xs;
		this.atMax=false;
		this.spare=spa;
		plugin=instance;
		
		int count = plugin.playerLevelXP.size()-1;
		boolean found = false;
		while(found==false){		
			if(xpTotal>=plugin.playerLevelXP.get(count-1)){
				setLevel(count);
				found=true;
				if(plugin.playerLevelXP.get(count)>xpTotal){
				setXPNext(plugin.playerLevelXP.get(count));
				}else{
					setXPNext(xpTotal + 1);
				}
			}
			count--;
		}
		
	}
	
	public Player getPlayer(){
		return this.player;
	}
	
	public int getPlayerID(){
		return this.playerID;
	}
	
	public int getLevel(){
		return this.playerLevel;
	}
	
	public void setLevel(int l){
		this.playerLevel=l;
	}
	
	public int getXP(){
		return this.xpTotal;
	}
	
	public void setXP(int x){
		this.xpTotal=x;
	}
	
	public int getXPNext(){
		return this.xpNext;
	}
	
	public void setXPNext(int x){
		this.xpNext=x;
	}
	
	public boolean getAtMax(){
		return this.atMax;
	}
	
	public void setAtMax(boolean b){
		this.atMax=b;
	}
	
	public int getStrength(){
		return this.strength;
	}
	
	public void setStrength(int x){
		this.strength=x;
	}
	
	public int getVitality(){
		return this.vitality;
	}
	
	public void setVitality(int x){
		this.vitality=x;
	}
	
	public int getDexterity(){
		return this.dexterity;
	}
	
	public void setDexterity(int x){
		this.dexterity=x;
	}
	
	public int getAgility(){
		return this.agility;
	}
	
	public void setAgility(int x){
		this.agility=x;
	}
	
	public int getSpirit(){
		return this.spirit;
	}
	
	public void setSpirit(int x){
		this.spirit=x;
	}
	
	public int getSpare(){
		return this.spare;
	}
	
	public void setSpare(int x){
		this.spare=x;
	}
	
	
	

}
