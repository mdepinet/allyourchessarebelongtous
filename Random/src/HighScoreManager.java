import java.io.*;
import java.util.*;

public class HighScoreManager {
	public static final String fileName = "scores.scr";
	public ArrayList<Score> scores;
	
	public HighScoreManager()
	{
		scores = new ArrayList<Score>();
		readScoresFromFile();
	}
	public void readScoresFromFile()
	{
		try {
			Score s = null;
			FileInputStream fis = null;
			ObjectInputStream in = null;
			try
			{
				fis = new FileInputStream(fileName);
				in = new ObjectInputStream(fis);
//				String ss = "";
//	            while ((ss = (String)in.readObject()) != null) {
//	            	s = new Score(Integer.parseInt(ss.substring(ss.indexOf("=/"+1))));
//	            	s.setPlayer(ss.substring(0,ss.indexOf("=/")));
				while((s = ((Score)in.readObject())) !=null) {
	            	scores.add(s);
	            }
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}
			catch(ClassNotFoundException ex)
			{
				ex.printStackTrace();
			}
			finally{
				try{in.close();}catch(IOException ex){}
			}
		}
		catch(Exception e) {}
	}
	public void writeScoresToFile()
	{
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try
		{
		    fos = new FileOutputStream(fileName);
		    out = new ObjectOutputStream(fos);
		    for(Score s:scores) {
//		    	if(s.isDidCheat()) s.setPlayer(s.getPlayer()+"*");
		    	out.writeObject(s);
		    }
		    }
		 catch(IOException ex)
		 {
		    ex.printStackTrace();
		 }
		 finally{
			 try {
				out.close();
			} catch (IOException e) {
			}
		 }
	}
	public boolean addScore(Score s)
	{
		scores.add(s);
		Collections.sort(scores);
		if(scores.size()>10)
		{
			Score removed = scores.remove(scores.size()-1);
			if(removed.equals(s))
				return false;
		}
		return true;
	}
	public boolean isHighScore(Score s)
	{
		return (scores.size()<10 || s.compareTo(scores.get(scores.size()-1))<0);
	}
	public ArrayList<Score> getScores() {
		return scores;
	}
	public void setScores(ArrayList<Score> scores) {
		this.scores = scores;
		Collections.sort(this.scores);
		writeScoresToFile();
	}
}
