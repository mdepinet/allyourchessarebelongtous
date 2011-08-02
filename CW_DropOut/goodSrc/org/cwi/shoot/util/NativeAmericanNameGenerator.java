package org.cwi.shoot.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class NativeAmericanNameGenerator extends NameGenerator {
	ArrayList<String> pre = new ArrayList<String>();
    ArrayList<String> mid = new ArrayList<String>();
    ArrayList<String> sur = new ArrayList<String>();
    
	private String fileName;
    
    public NativeAmericanNameGenerator(String fileName) throws IOException{
    	super();
        this.fileName = fileName;
        refresh();
    }
    public void refresh() throws IOException{
        
        FileReader input = null;
        BufferedReader bufRead;
        String line;
        
        input = new FileReader(fileName);
        
        bufRead = new BufferedReader(input);
        line="";   
              
        while(line != null){        
            line = bufRead.readLine();        
            if(line != null && !line.equals("")){
                if(line.charAt(0) == '-'){
                    pre.add(line.substring(1));                    
                }
                else if(line.charAt(0) == '+'){
                    sur.add(line.substring(1));                    
                }
                else{ 
                    mid.add(line);                    
                }
            }
        }        
        bufRead.close();
    }
    
    public boolean requiresMid(String s) {
    	if(s.substring(1).contains("-m")) return true;
    	return false;
    }
    
    public String compose(int syls){
    	int number = syls;
    	if(syls > 2 && mid.size() == 0) throw new RuntimeException("You are trying to create a name with more than 3 parts, which requires middle parts, " +
                "which you have none in the file "+fileName+". You should add some. Every word, which doesn't have + or - for a prefix is counted as a middle part.");
        if(pre.size() == 0) throw new RuntimeException("You have no prefixes to start creating a name. add some and use \"-\" prefix, to identify it as a prefix for a name. (example: -asd)");
        if(sur.size() == 0) throw new RuntimeException("You have no suffixes to end a name. add some and use \"+\" prefix, to identify it as a suffix for a name. (example: +asd)");
        if(syls < 1) throw new RuntimeException("compose(int syls) can't have less than 1 syllable");
        String name;
        int a = (int)(Math.random() * pre.size());
        if(requiresMid(pre.get(a))) {
        	pre.set(a, pre.get(a).substring(0,pre.get(a).length()-3));
        	number++;
        }
        int c = (int)(Math.random() * sur.size());
        if(requiresMid(sur.get(c))) {
        	sur.set(c, sur.get(c).substring(0,sur.get(c).length()-3));
        	number++;
        }
        int[] b = new int[number];
        for(int i = 0; i < b.length; i++) {
        	b[i] = (int)(Math.random() * mid.size());
        }
        
        name = pre.get(a) + " ";
        for(int i = 0; i < b.length-2; i++)
        	name = name + mid.get(b[i]) + " ";
        if(syls>1) name = name + sur.get(c);
    	return name;
    }
}
