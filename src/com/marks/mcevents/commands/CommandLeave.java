package com.marks.mcevents.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.marks.mcevents.games.GameManager;

public class CommandLeave implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		
		if (! GameManager.isGameActive()) {
			sender.sendMessage("There is no active game!");
			return false;
		}
		
		if (! GameManager.removePlayer((Player) sender)) {
			sender.sendMessage("You are not in a game!");
			return false;
		}
		
		sender.sendMessage("Successfully left.");
		
		return true;
		
	}

}
