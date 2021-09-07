package com.marks.mcevents.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import com.marks.mcevents.games.GameManager;

public class EventPlayerClick implements Listener {

	@EventHandler
	public void playerClick(PlayerInteractEvent e) {
		if (e.getHand() == null || e.getHand().equals(EquipmentSlot.OFF_HAND))
			return;
		boolean rightClick = e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK);
		GameManager.onEvent(rightClick ? "onRightClickEvent" : "onLeftClickEvent", e);
	}
	
	@EventHandler
	public void playerEntityClick(PlayerInteractAtEntityEvent e) {
		if (e.getHand() == null || e.getHand().equals(EquipmentSlot.OFF_HAND))
			return;
		GameManager.onEvent("onEntityClickEvent", e);
	}
	
}