package com.marks.mcevents.games;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class SoloSpeedrun extends Game {

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
	}
	
	public void onRightClickEvent(Event event) {
		PlayerInteractEvent e = (PlayerInteractEvent) event;
		PlayerInventory pi = e.getPlayer().getInventory();
		if (pi.getItemInMainHand().getType() != Material.GOLD_INGOT)
			return;
		pi.getItemInMainHand().setAmount(pi.getItemInMainHand().getAmount() - 1);
		pi.addItem(new ItemStack(Material.ENDER_PEARL));
	}

	@Override
	public boolean removePlayer(Player p) {
		// TODO Auto-generated method stub
		return false;
	}

}
