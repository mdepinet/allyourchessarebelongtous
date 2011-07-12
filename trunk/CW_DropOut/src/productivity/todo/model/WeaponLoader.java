package productivity.todo.model;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class WeaponLoader {
	private static String defaultWeaponSet = "resource/default.ws";
	private static Map<String,WeaponDefinition> definitions;
	
	public static boolean load(Weapon wep, String name){
		if (definitions == null || definitions.isEmpty()) loadAll();
		WeaponDefinition def = definitions.get(name);
		wep.setProperties(def);
		return def == null;
	}
	private static void loadAll(){
		definitions = new TreeMap<String,WeaponDefinition>();
		Scanner scan = null;
		try{
			scan = new Scanner(new File(defaultWeaponSet));
			while (scan.hasNext()){
				WeaponDefinition wepDef = new WeaponDefinition(scan.nextLine());
				definitions.put(wepDef.getName(), wepDef);
			}
		} catch (IOException ex){
			ex.printStackTrace();
		}
	}
}
