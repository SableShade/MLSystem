package me.Sable.MLMobCombat;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;

public class MCEntityListener extends EntityListener {

	MLMobCombat plugin;

	public MCEntityListener(MLMobCombat instance) {
		this.plugin = instance;
	}

	@SuppressWarnings("static-access")
	public void onEntityDamage(EntityDamageEvent event) {
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent entEvent = (EntityDamageByEntityEvent) event;
			if ((entEvent.getDamager() instanceof Player)
					&& !(entEvent.getEntity() instanceof Player)) {
				Player damager = (Player) entEvent.getDamager();
				int damage = entEvent.getDamage();
				int playerStrength = plugin.mlManager.playerList.get(damager)
						.getStrength();
				damage = damage * (playerStrength / 100);
				entEvent.setDamage(damage);
				return;
			}
			if (!(entEvent.getDamager() instanceof Player)
					&& (entEvent.getEntity() instanceof Player)) {
				if (plugin.permissionHandler.has((Player) entEvent.getEntity(),
						"mlsystem.homezone")) {
					return;
				} else {
					int damage = entEvent.getDamage();
					damage = damage * 2;
					entEvent.setDamage(damage);
				}
				return;
			}
		}
		return;
	}

	// Giving Players XP for killing mobs.
	@SuppressWarnings("static-access")
	public void onEntityDeath(EntityDeathEvent event) {

		if ((event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
			EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event
					.getEntity().getLastDamageCause();
			if ((e.getDamager() instanceof Player)
					&& !(e.getEntity() instanceof Player)) {
				Player p = (Player) e.getDamager();
				int amount = 15;
				
				if (plugin.permissionHandler.has(p,	"mlsystem.homezone")) {
					amount=8;
				}
				
				plugin.mlManager.playerFunctions.addXP(p, amount);
				p.sendMessage("You earned "+amount+"XP!");
				return;
			}
		}
	}
}
