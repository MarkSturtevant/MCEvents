package com.marks.mcevents.games;

import org.bukkit.entity.Player;

public abstract class Game {

	public abstract void start();
	public abstract void stop();
	public abstract boolean removePlayer(Player p);
	
}
