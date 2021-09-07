package com.marks.mcevents.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.marks.mcevents.games.GameManager;

public class EventPlayerDeath implements Listener {

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e) {
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		GameManager.onEvent("onDeathEvent", e);
	}
	
}
