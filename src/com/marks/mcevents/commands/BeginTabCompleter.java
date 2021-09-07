package com.marks.mcevents.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class BeginTabCompleter implements TabCompleter {
	
	private static List<String> games = Arrays.asList("blockshuffle", "buildswap", "deathswap", "solospeedrun", "trapcraft");

	@Override
	public List<String> onTabComplete(CommandSender sender, Command com, String alias, String[] args) {
		if (! com.getName().equals("begin") || ! (sender instanceof Player))
			return null;
		return games;
	}

}
