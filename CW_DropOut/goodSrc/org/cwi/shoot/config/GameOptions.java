package org.cwi.shoot.config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import org.cwi.shoot.util.NameGenerator;


public class GameOptions {
	private static final String NAME_RESOURCE = "resource/namePartsArab.txt";
	private static final String MAP_RESOURCE = "resource/maps/default.map";
	private static final int DEFAULT_MAP_HEIGHT = 30;
	private static final int DEFAULT_MAP_WIDTH = 30;
	public static final char BLANK_CHARACTER = '_';
	
	private GameMode mode;
	private NameGenerator nameGen;
	private File mapFile;
	private int numTeams;
	private int playerTeam;
	private String playerName;
	
	public GameOptions(GameMode mode, File mapFile, NameGenerator nameGen, int numTeams, int playerTeam, String playerName){
		this.mode = mode;
		this.mapFile = mapFile;
		this.nameGen = nameGen;
		this.numTeams = Math.max(numTeams, 2); //Minimum 2 teams
		this.playerTeam = playerTeam;
		this.playerName = playerName;
		if (mapFile == null || !mapFile.exists()) mapFile = new File(MAP_RESOURCE);
		if (nameGen == null) try {nameGen = new NameGenerator(NAME_RESOURCE);} catch (IOException e) {e.printStackTrace();}
	}
	
	public GameMode getMode() {
		return mode;
	}
	public NameGenerator getNameGen() {
		return nameGen;
	}
	public File getMapFile() {
		return mapFile;
	}
	public int getNumTeams() {
		return numTeams;
	}
	public int getPlayerTeam() {
		return playerTeam;
	}
	public String getPlayerName() {
		return playerName;
	}
	
	public char[][] loadMap() {
		char[][] map = null;
		try{
			Scanner scan = new Scanner(mapFile);
			int lineIndex = 0;
			String line = scan.nextLine();
			if (line.matches("[\\d\\s]+")){
				map = new char[Integer.parseInt(line.split("\\s")[0])][Integer.parseInt(line.split("\\s")[1])];
			}
			else{
				map = new char[DEFAULT_MAP_HEIGHT][DEFAULT_MAP_WIDTH];
				map[lineIndex++] = processLine(line, map[lineIndex].length);
			}
			for (;lineIndex<map.length;lineIndex++){
				if (scan.hasNextLine()) map[lineIndex] = processLine(scan.nextLine(), map[lineIndex].length);
				else map[lineIndex] = processLine(null, map[lineIndex].length);
			}
		} catch (IOException ex){
			ex.printStackTrace();
		}
		return map;
	}
	private char[] processLine(String line, int length){
		char[] result = new char[length];
		if (line == null){
			for (int i = 0; i<length; i++) result[i] = BLANK_CHARACTER;
			return result;
		}
		String[] chars = line.split("\\s");
		int i;
		for (i = 0; i<chars.length && i<length; i++){
			char nextChar = chars[i].charAt(0);
			if (Arrays.asList(mode.getIgnoredMapChars()).contains(nextChar)) result[i] = BLANK_CHARACTER;
			else result[i] = nextChar;
		}
		for (;i<length; i++){
			result[i] = BLANK_CHARACTER;
		}
		return result;
	}
}
