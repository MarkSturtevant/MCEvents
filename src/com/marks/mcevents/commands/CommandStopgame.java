package com.marks.mcevents.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.marks.mcevents.games.GameManager;

public class CommandStopgame implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (! GameManager.isGameActive()) {
			sender.sendMessage("There is no active game!");
			return false;
		}
		
		GameManager.stopGame();
		
		sender.sendMessage("Game successfully stopped.");
		
		return true;
		
	}

}
