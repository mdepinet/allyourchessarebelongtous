package org.cwi.shoot.profile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.cwi.shoot.profile.Achievement.AchievementType;

public class Profile {
	public static final String PROFILE_LOCATION = "resource/profiles/";
	private String rank;
	private String name;
	private Map<String, Object> data;
	private int score;
	private int kills;
	private int deaths;
	private int bulletsShot;
	private List<String> achievements;
	
	public Profile(String profileName) {
		name = profileName;
		data = new HashMap<String, Object>();
		Scanner scan = null;

		if(!profileName.equals("")) {
			try {
				scan = new Scanner(new File(PROFILE_LOCATION+profileName+".pprf"));
				while(scan.hasNextLine()) {
					processLine(scan.nextLine());
				}
				scan.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void processLine(String line) {
		Scanner scan = new Scanner(line);
		String item = scan.next();
		if(item.equals("TotalScore:")) {
			score = Integer.parseInt(scan.next());
			if(score > 9000) rank="Private";
			data.put(item, score);
		}
		else if(item.equals("Kills:")) {
			kills = Integer.parseInt(scan.next());
			data.put(item, kills);
		}
		else if(item.equals("Deaths:")) {
			deaths = Integer.parseInt(scan.next());
			data.put(item, deaths);
		}
		else if(item.equals("Achievements:")) {
			achievements = new ArrayList<String>();
			while(scan.hasNext()) {
				achievements.add(AchievementType.valueOf(scan.next()).toString());
			}
			data.put(item, achievements);
		}
		else if(item.equals("Bullets-shot:")) {
			bulletsShot = Integer.parseInt(scan.next());
			data.put(item.replace("-", " "), bulletsShot);
		}
		
	}
	
	public String getName() {
		return name;
	}
	public String getRankAndName() {
		return (rank!=null ? rank + " " : "") + name;
	}
	public int getScore() {
		return score;
	}
	public int getKills() {
		return kills;
	}
	public int getDeaths() {
		return deaths;
	}
	public Map<String, Object> getData() {
		return data;
	}
	
	public void addStats(Map<String, Object> stats) {
		for(String s : stats.keySet()) {
			if(data.containsKey(s) && data.get(s) instanceof Collection) ((Collection)data.get(s)).addAll((Collection) stats.get(s));
			else if(data.containsKey(s)) {
				data.put(s, ((Integer)data.get(s))+(Integer)stats.get(s));
			}
			else data.put(s, stats.get(s));
		}
	}
	public void writeToFile() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(PROFILE_LOCATION+name+".pprf"));
			for(String s : data.keySet()) {
				if(data.get(s) instanceof Collection) {
					writer.write(s+" ");
					for(Object o : (Collection)data.get(s)) writer.write(o.toString()+" ");
					writer.write("\n");
				}
				else {
					writer.write(s.replace(" ", "-")+" ");
					writer.write(data.get(s)+"\n");
				}
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}