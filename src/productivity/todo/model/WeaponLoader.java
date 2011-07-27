package productivity.todo.model;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class WeaponLoader {
	private static String defaultWeaponSet = "resource/default.ws";
	private static Map<String,WeaponDefinition> definitions;
	private static Map<Character,WeaponDefinition> chars;
	private static WeaponDefinition defaultWeapon;
	
	public static boolean load(Weapon wep, String name){
		if (definitions == null || definitions.isEmpty()) loadAll();
		if (name.equalsIgnoreCase("Default")){
			wep.setProperties(defaultWeapon);
			return true;
		}
		WeaponDefinition def = definitions.get(name);
		wep.setProperties(def);
		return def != null;
	}
	public static boolean load(Weapon wep, char c){
		if (definitions == null || definitions.isEmpty()) loadAll();
		WeaponDefinition def = getWeaponDef(c);
		wep.setProperties(def);
		return def != null;
	}
	private static void loadAll(){
		definitions = new TreeMap<String,WeaponDefinition>();
		chars = new TreeMap<Character,WeaponDefinition>();
		Scanner scan = null;
		try{
			scan = new Scanner(new File(defaultWeaponSet));
			while (scan.hasNext()){
				WeaponDefinition wepDef = new WeaponDefinition(scan.nextLine());
				if (defaultWeapon == null) defaultWeapon = wepDef;
				definitions.put(wepDef.getName(), wepDef);
				chars.put(wepDef.getRepresentativeChar(), wepDef);
			}
		} catch (IOException ex){
			ex.printStackTrace();
		}
	}
	
	public static WeaponDefinition getWeaponDef(char c){
		if (chars == null || chars.isEmpty()) loadAll();
		return chars.get(c);
	}
	public static WeaponDefinition getWeaponDef(String s){
		if (definitions == null || definitions.isEmpty()) loadAll();
		if (s.equalsIgnoreCase("Default")) return defaultWeapon;
		return definitions.get(s);
	}
}
