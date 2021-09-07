package com.marks.mcevents.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import com.marks.mcevents.games.GameManager;

public class EventInventoryClick implements Listener {
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		GameManager.onEvent("onInventoryClickEvent", e);
	}

}
