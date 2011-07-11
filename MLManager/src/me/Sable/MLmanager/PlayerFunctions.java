package me.Sable.MLmanager;

import org.bukkit.entity.Player;

public class PlayerFunctions {

	MLManager plugin;

	public PlayerFunctions(MLManager instance) {
		this.plugin = instance;
	}

	public void addXP(Player player, int amount){
		
		//Fetching PlayerAtt from the HashMap.
		PlayerAtt p = plugin.playerList.get(player);		
		int xp = p.getXP();
		
		xp = xp + amount;
		
		if(xp<p.getXPNext()){
			p.setXP(xp);
			return;
		}
		
		while(xp>=p.getXPNext()){
			
			int l = p.getLevel();
			l++;
			
			if(l<plugin.playerLevelXP.size()){
			
			if(l+1<plugin.playerLevelXP.size()){
			int newXPaim = plugin.playerLevelXP.get(l+1);
			p.setXPNext(newXPaim);
			}else{	
			p.setXPNext(xp+1);
			}
			p.setAtMax(false);
			p.setXP(xp);
			p.setLevel(l);
			
			p.setSpare(p.getSpare()+5);
			player.sendMessage("Congratulations! You have just advanced to Level " + l + "!");
			player.sendMessage("You have 5 Stat Points to assign using /assign <stat> <amount>");
			
			}
			
			if(l>=plugin.playerLevelXP.size()){
				if(p.getAtMax()==false){
					player.sendMessage("You cannot advance another level or gain XP!");
					p.setAtMax(true);
				}
				return;
			}
		}
		return;
	}
	
	public void assignStat(Player player, String s, int amount){
		
		//Fetching PlayerAtt from the HashMap.
		PlayerAtt p = plugin.playerList.get(player);
		
		if(!s.equalsIgnoreCase("strength") && !s.equalsIgnoreCase("vitality") && !s.equalsIgnoreCase("dexterity") && !s.equalsIgnoreCase("agility") && !s.equalsIgnoreCase("spirit")){
			player.sendMessage(s + " is not a valid Stat.");
			return;
		}		

		if(p.getSpare()<amount){
			player.sendMessage("You do not have that many Stat Points to assign.");
			return;
		}
		
		p.setSpare(p.getSpare() - amount);
		
		if (s.equalsIgnoreCase("Strength")){
			p.setStrength(p.getStrength() + amount);
			player.sendMessage("Strength increased to " + p.getStrength());
			return;
		}
		if (s.equalsIgnoreCase("Vitality")){
			p.setVitality(p.getVitality() + amount);
			player.sendMessage("Vitality increased to " + p.getVitality());
			return;
		}
		if (s.equalsIgnoreCase("Dexterity")){
			p.setDexterity(p.getDexterity() + amount);
			player.sendMessage("Dexterity increased to " + p.getDexterity());
			return;
		}
		if (s.equalsIgnoreCase("Agility")){
			p.setAgility(p.getAgility() + amount);
			player.sendMessage("Agility increased to " + p.getAgility());
			return;
		}
		if (s.equalsIgnoreCase("Spirit")){
			p.setSpirit(p.getSpirit() + amount);
			player.sendMessage("Spirit increased to " + p.getSpirit());
			return;
		}
		
	}
	
	public void levelUp(Player player){
		
		//Fetching PlayerAtt from the HashMap.
		PlayerAtt p = plugin.playerList.get(player);	
		
		int l = p.getLevel();
		l++;
		
		if(l<plugin.playerLevelXP.size()){
			
		int newXPaim = plugin.playerLevelXP.get(l+1);
		
		p.setAtMax(false);
		p.setXP(plugin.playerLevelXP.get(l));
		p.setLevel(l);
		p.setXPNext(newXPaim);
		p.setSpare(p.getSpare()+5);
		player.sendMessage("Congratulations! You have just advanced to Level " + l + "!");
		player.sendMessage("You have 5 Stat Points to assign using /assign <stat> <amount>");
		return;
		}
		
		if(l>=plugin.playerLevelXP.size()){
			if(p.getAtMax()==false){
				player.sendMessage("You cannot advance another level!");
				p.setAtMax(true);
			}
			return;
		}
	}
	
	public void viewStats(Player player){
	
		//Fetching PlayerAtt from the HashMap.
		PlayerAtt p = plugin.playerList.get(player);
		
		player.sendMessage("~~~~~~~~~~~~~~~~~~~~~~~~~");
		player.sendMessage("Player Stats: ");
		player.sendMessage("Strength: " + p.getStrength());
		player.sendMessage("Vitality: " + p.getVitality());
		player.sendMessage("Dexterity: " + p.getDexterity());
		player.sendMessage("Agility: " + p.getAgility());
		player.sendMessage("Spirit: " + p.getSpirit());
		player.sendMessage("You have " + p.getSpare() + " points left to assign.");
		player.sendMessage("~~~~~~~~~~~~~~~~~~~~~~~~~");
	}
	
	public void viewLevel(Player player){
		
		//Fetching PlayerAtt from the HashMap.
		PlayerAtt p = plugin.playerList.get(player);
		
		int xptonext=p.getXPNext()-p.getXP();
		
		player.sendMessage("~~~~~~~~~~~~~~~~~~~~~~~~~");
		player.sendMessage("Player XP: ");
		player.sendMessage("Level: " + p.getLevel());
		player.sendMessage("Total XP: " + p.getXP());
		player.sendMessage("XP to Next Level: " + xptonext);
		player.sendMessage("~~~~~~~~~~~~~~~~~~~~~~~~~");
	}
}
