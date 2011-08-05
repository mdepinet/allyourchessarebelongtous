package org.cwi.shoot.profile;

import java.util.HashMap;
import java.util.Map;

public class Achievement {
	public enum AchievementType {
		DOUBLE_KILL, TRIPLE_KILL, KILL_JOY
	}
	private static Map<AchievementType, Integer> achievementValues; 
	public Achievement(String name) {
		achievementValues = new HashMap<AchievementType, Integer>();
	}
	public Achievement(AchievementType type) {
		achievementValues = new HashMap<AchievementType, Integer>();
	}
	
	public static AchievementType matchStringToEnum(String name) {
		if(name.equals("doubleKill")) return AchievementType.DOUBLE_KILL;
		if(name.equals("tripleKill")) return AchievementType.TRIPLE_KILL;
		if(name.equals("killJoy")) return AchievementType.KILL_JOY;
		return null;
	}
	
	public static int getAchievementValue(String name) {
		return achievementValues.get(matchStringToEnum(name));
	}
	public static int getAchievementValue(AchievementType type) {
		return achievementValues.get(type);
	}
	public static String getStringFromType(AchievementType type) {
		return (type.toString().replace("_", " ")).toLowerCase();
	}
}
