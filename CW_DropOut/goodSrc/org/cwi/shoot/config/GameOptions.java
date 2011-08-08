package org.cwi.shoot.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.cwi.shoot.model.Weapon;
import org.cwi.shoot.profile.Profile;
import org.cwi.shoot.util.NameGenerator;
import org.cwi.shoot.util.OtherNameGenerator;


public class GameOptions {
	public static final String NAME_RESOURCE = "resource/names/namePartsNigerian.txt";
	public static final String MAP_RESOURCE = "resource/maps/default.map";
	public static final String DEFAULT_WEAPON_SET = "resource/weapon_sets/default.ws";
	private static final int DEFAULT_MAP_HEIGHT = 30;
	private static final int DEFAULT_MAP_WIDTH = 30;
	public static final char BLANK_CHARACTER = '_';
	public static final char WALL_CHARACTER = 'X';
	
	private GameMode mode;
	private NameGenerator nameGen;
	private File mapFile;
	private int numTeams;
	private int playersPerTeam;
	private int playerTeam;
	private List<Character> validChars;
	private Profile profile;
	private static String weaponSet;
	
	public GameOptions(GameMode mode, File mapFile, File nameGenFile, int numTeams, int playerTeam, Profile profile, int playersPerTeam, String weaponSet){
		this.mode = mode;
		this.mapFile = mapFile;
		this.numTeams = Math.max(numTeams, 2); //Minimum 2 teams
		this.playerTeam = playerTeam;
		this.playersPerTeam = playersPerTeam;
		this.profile = profile;
		setWeaponSet(weaponSet);
		if (mapFile == null || !mapFile.exists()) mapFile = new File(MAP_RESOURCE);
		try {
			if(nameGenFile.getAbsolutePath().contains("\\bonus\\"))
				this.nameGen = new OtherNameGenerator(nameGenFile.getAbsolutePath());
			else this.nameGen = new NameGenerator(nameGenFile.getAbsolutePath());
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (this.nameGen == null)
			try {
				if(nameGenFile.getPath().contains("/bouns/"))
					this.nameGen = new OtherNameGenerator(nameGenFile.getPath());
				else this.nameGen = new NameGenerator(nameGenFile.getPath());
			} catch (IOException e) {
				e.printStackTrace();
			}
		validChars = new ArrayList<Character>();
		for (char c : Weapon.DEFAULT_WEAPONS){
			validChars.add(c);
		}
		validChars.add(BLANK_CHARACTER);
		validChars.add(WALL_CHARACTER);
		for (char c : mode.getAdditionalMapChars()){
			validChars.add(c);
		}
		for (char c = '1'; c<='9'; c++){
			validChars.add(c);
		}
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
		return profile.getName();
	}
	public int getPlayersPerTeam() {
		return playersPerTeam;
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
		return rotate(map);
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
			if (!validChars.contains(nextChar)) result[i] = BLANK_CHARACTER;
			else result[i] = nextChar;
		}
		for (;i<length; i++){
			result[i] = BLANK_CHARACTER;
		}
		return result;
	}
	
	private char[][] rotate(char[][] map){
		char[][] rotated = new char[map[0].length][map.length];
		for (int r = 0; r<map.length; r++){
			for (int c = 0; c<map[r].length; c++){
				rotated[c][r] = map[r][c];
			}
		}
		return rotated;
	}

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public static String getWeaponSet() {
		return weaponSet != null ? weaponSet : DEFAULT_WEAPON_SET;
	}

	public void setWeaponSet(String weaponSet) {
		this.weaponSet = weaponSet;
	}
}
