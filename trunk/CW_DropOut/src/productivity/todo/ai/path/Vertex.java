package productivity.todo.ai.path;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

public class Vertex implements Comparable<Vertex>{
	private Map<Vertex,Integer> adjacents;
	public int distance, row, col;
	Vertex prev;
	
	public Vertex(int r, int c) {
		row = r; col = c;
		adjacents = new HashMap<Vertex,Integer>();
	}
	
	public void addVertex(Vertex v, int weight){
		adjacents.put(v,weight);
	}
	
	public Map<Vertex,Integer> getAdjacents(){
		return adjacents;
	}
	
	public int compareTo(Vertex other){
		return distance - other.distance;
	}
	
	public String toString(){
		return "Vertex<"+row+","+col+">";
	}
	
	public boolean equals(Object obj){
		return hashCode() == obj.hashCode();
	}
	public Point getPoint(){
		return new Point(row,col);
	}
	public int hashCode(){
		return row*31+col;
	}
}