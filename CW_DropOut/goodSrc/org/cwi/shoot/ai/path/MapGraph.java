package org.cwi.shoot.ai.path;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

public class MapGraph {
	private Collection<Vertex> vertices;
	private HashMap<Pair<Vertex,Vertex>,Vertex> answers;
	private Vertex lastSource = null;
	public MapGraph()
	{
		vertices = new LinkedList<Vertex>();
		answers = new HashMap<Pair<Vertex,Vertex>,Vertex>();
	}
	
	
	public void createGraph(char[][] map){
		if (!vertices.isEmpty()) return;
		for(int r = 0; r < map.length; r++)
		{
			for(int c = 0; c < map[r].length; c++)
			{
				if(map[r][c] != 'X'){
					Vertex v = new Vertex(r,c);
					Vertex other = getVertexByLocation(r,c-1); //left
					if (other != null){
						other.addVertex(v,10000);
						v.addVertex(other, 10000);
					}
					other = getVertexByLocation(r-1,c-1); //up left
					if (other != null && (getVertexByLocation(r,c-1) != null || getVertexByLocation(r-1,c) != null)){
						other.addVertex(v,14142);
						v.addVertex(other, 14142);
					}
					other = getVertexByLocation(r-1,c); //up
					if (other != null){
						other.addVertex(v,10000);
						v.addVertex(other, 10000);
					}
					other = getVertexByLocation(r-1,c+1); //up right
					if (other != null && (getVertexByLocation(r,c+1) != null || getVertexByLocation(r-1,c) != null)){
						other.addVertex(v,14142);
						v.addVertex(other, 14142);
					}
					vertices.add(v);
				}
					
			}
		}
	}
	
	private Vertex getVertexByLocation(int r, int c){
		for (Vertex v : vertices){
			if (v.row == r && v.col == c) return v;
		}
		return null;
	}
	
	public Point getNextLocation(Point curr, Point goal){
		if (goal == null) return curr;
		if (vertices.isEmpty()) return null;
		if(curr.equals(goal)) return curr;
		Vertex startV = getVertexByLocation(curr.x,curr.y);
		Vertex endV = getVertexByLocation(goal.x,goal.y);
		Pair<Vertex,Vertex> key = new Pair<Vertex, Vertex>(startV,endV);
		if(answers.containsKey(key)) {
				return answers.get(key).getPoint();
		}
		computePaths(startV);
		List<Vertex> path = getShortestPath(startV, endV);
		Vertex last = path.size() > 1 ? path.get(1) : path.get(0);
		List<Vertex> pathCopy = new LinkedList<Vertex>();
		pathCopy.addAll(path);
		answers.put(key,last);
		if (path.size() >= 2) answers.put(key.flip(), path.get(path.size()-2)); //path.get(path.size()-1 should be endV
		while (pathCopy.size()>1){
			Vertex v = pathCopy.remove(0);
			key = new Pair<Vertex, Vertex>(v,endV);
			answers.put(key,pathCopy.get(0));
			if (pathCopy.size() >= 2) answers.put(key.flip(),pathCopy.get(pathCopy.size()-2)); //path.get(path.size()-1 should be endV
		}
		return new Point(last.row,last.col);
	}
	
	public void computePaths(Vertex source) {
		if(source == null) return;
		if (lastSource!=null && source.equals(lastSource)) return;
		else lastSource = source;
		for (Vertex v : vertices){
			v.distance = Integer.MAX_VALUE;
			v.prev = null;
		}
		source.distance = 0;
		PriorityQueue<Vertex> vertexQueue = new PriorityQueue<Vertex>();
		vertexQueue.addAll(vertices);
		
		while (!vertexQueue.isEmpty()) {
			Vertex u = vertexQueue.poll();
			if (u.distance == Integer.MAX_VALUE) break; //Disconnected graph
			// Visit each edge exiting u
			for (Map.Entry<Vertex, Integer> entry : u.getAdjacents().entrySet()) {
				Vertex v = entry.getKey();
				if (!vertexQueue.contains(v)) continue;
				int weight = entry.getValue();
				int distanceThroughU = u.distance + weight;
				if (distanceThroughU < v.distance) {
					vertexQueue.remove(v); //v is a variable in the adjacents, you must remove it from the queue
					v.distance = distanceThroughU;
					v.prev = u;
					vertexQueue.add(v); // then add it back here to update it
				}
			}
		}
	}
	
	public List<Vertex> getShortestPath(Vertex start, Vertex target) {
		List<Vertex> path = new ArrayList<Vertex>();
		if (target == null){
			path.add(start);
			return path;
		}
		for (Vertex vertex = target; vertex != null; vertex = vertex.prev)
			path.add(vertex);
		
		if (!path.contains(start)) System.err.println("Cannot get from "+start+" to "+target);
		Collections.reverse(path);
		return path;
	}
	
//	private List<Vertex> getPath(Point start, Point end){
//		if (vertices.isEmpty()) return null;
//		
//		Vertex startV = getVertexByLocation(start.x,start.y), endV = getVertexByLocation(end.x,end.y);
//		Pair<Vertex, Vertex> ends = new Pair<Vertex, Vertex>(startV,endV);
//		if (answers.containsKey(ends)) return answers.get(ends);
//		computePaths(getVertexByLocation(start.x,start.y));
//		List<Vertex> path = getShortestPath(startV, endV);
//		List<Vertex> pathCopy = new LinkedList<Vertex>();
//		Collections.copy(pathCopy, path);
//		answers.put(ends,path);
//		while (!pathCopy.isEmpty()){
//			Vertex v = pathCopy.remove(0);
//			ends = new Pair<Vertex, Vertex>(v,endV);
//			answers.put(ends,pathCopy);
//		}
//		
//		return path;
//	}
	public void printPath(List<Vertex> path, char[][] map)
	{
		for(int i = 0; i < 30;i++)
		{
			OUTTER: for(int j = 0; j < 30; j++)
			{
				if(map[i][j] == 'X') {
					System.out.print("X "); continue;
				}
				for (Vertex v : path){
					if (v.row == i && v.col == j) { System.out.print("O "); continue OUTTER; }
				}
				System.out.print("_ ");
			}
			System.out.println();
		}
	}	
}
