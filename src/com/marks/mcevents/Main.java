package com.marks.mcevents;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.marks.mcevents.commands.*;
import com.marks.mcevents.events.*;
import com.marks.mcevents.games.GameManager;

public class Main extends JavaPlugin {
	
	private static Plugin plugin;
	
	@Override
	public void onEnable() {
		plugin = this;
		registerCommands();
		registerEvents();
	}
	
	@Override
	public void onDisable() {
		GameManager.stopGame();
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}
	
	private void registerCommands() {
		this.getCommand("begin").setExecutor(new CommandBegin());
		this.getCommand("leave").setExecutor(new CommandLeave());
		this.getCommand("stopgame").setExecutor(new CommandStopgame());
		
		this.getCommand("begin").setTabCompleter(new BeginTabCompleter());
	}
	
	private void registerEvents() {
		//getServer().getPluginManager().registerEvents(new EventPlayerConnection(), this);
		//getServer().getPluginManager().registerEvents(new EventPlayerFly(), this);
		getServer().getPluginManager().registerEvents(new EventPlayerDeath(), this);
		//getServer().getPluginManager().registerEvents(new EventPlayerHit(), this);
		getServer().getPluginManager().registerEvents(new EventPlayerClick(), this);
		//getServer().getPluginManager().registerEvents(new EventProjectiles(), this);
	}

}
