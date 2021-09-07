package com.marks.mcevents.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.marks.mcevents.games.GameManager;

public class CommandBegin implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (args.length == 0) {
			sender.sendMessage("Need to specify game name!");
			return false;
		}
		
		if (GameManager.isGameActive()) {
			sender.sendMessage("There is already an active game!");
			return false;
		}
		
		if (! GameManager.launchGame(args[0].toLowerCase())) {
			sender.sendMessage("Not a valid game name!");
			return false;
		}
		
		sender.sendMessage("Game successfully launched.");
		
		return true;
		
	}

}
