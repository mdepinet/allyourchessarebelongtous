package productivity.todo.ai.path;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import productivity.todo.model.GameMap;

public class MapGraph {
	private List<Vertex> vertices;
	public MapGraph()
	{
		vertices = new LinkedList<Vertex>();
	}
	
	
	public void createGraph(char[][] map){
		if (!vertices.isEmpty()) return;
		for(int c = 0; c < map.length; c++)
		{
			for(int r = 0; r < map[c].length; r++)
			{
				if(map[r][c] != 'X'){
					Vertex v = new Vertex(r,c);
					Vertex other = getVertexByLocation(r,c-1); //left
					if (other != null){
						other.addVertex(v,10000);
						v.addVertex(other, 10000);
					}
					other = getVertexByLocation(r-1,c-1); //up left
					if (other != null){
						other.addVertex(v,14142);
						v.addVertex(other, 14142);
					}
					other = getVertexByLocation(r-1,c); //up
					if (other != null){
						other.addVertex(v,10000);
						v.addVertex(other, 10000);
					}
					other = getVertexByLocation(r-1,c+1); //up right
					if (other != null){
						other.addVertex(v,14142);
						v.addVertex(other, 14142);
					}
					//Unnecessary code...
					other = getVertexByLocation(r,c+1); //right
					if (other != null){
						other.addVertex(v,10000);
						v.addVertex(other, 10000);
					}
					other = getVertexByLocation(r+1,c+1); //down right
					if (other != null){
						other.addVertex(v,14142);
						v.addVertex(other, 14142);
					}
					other = getVertexByLocation(r+1,c); //down
					if (other != null){
						other.addVertex(v,10000);
						v.addVertex(other, 10000);
					}
					other = getVertexByLocation(r+1,c-1); //down left
					if (other != null){
						other.addVertex(v,14142);
						v.addVertex(other, 14142);
					}
					//End unnecessary
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
		if (vertices.isEmpty()) return null;
		
		computePaths(getVertexByLocation(curr.x,curr.y));
		List<Vertex> path = getShortestPath(getVertexByLocation(curr.x,curr.y), getVertexByLocation(goal.x,goal.y));
		Point p = path.size() > 1 ? new Point(path.get(1).row,path.get(1).col) : new Point(path.get(0).row,path.get(0).col);
		System.out.println("Path from "+curr+" to "+goal+".  Next loc is "+p);
		return p;
	}
	
	public void computePaths(Vertex source) {
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
					v.distance = distanceThroughU;
					v.prev = u;
				}
			}
		}
	}
	
	public List<Vertex> getShortestPath(Vertex start, Vertex target) {
		List<Vertex> path = new ArrayList<Vertex>();
		for (Vertex vertex = target; vertex != null; vertex = vertex.prev)
			path.add(vertex);
		
		if (!path.contains(start)) System.err.println("Cannot get from "+start+" to "+target);
		Collections.reverse(path);
		return path;
	}
	
	private List<Vertex> getPath(Point start, Point end){
		if (vertices.isEmpty()) return null;
		
		computePaths(getVertexByLocation(start.x,start.y));
		return getShortestPath(getVertexByLocation(start.x,start.y), getVertexByLocation(end.x,end.y));
		
	}
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
	
	
	public static void main(String[] args){
		//TEST
		MapGraph test = new MapGraph();
		char[][] map = new GameMap().getMap();
		test.createGraph(map);
		test.printPath(test.getPath(new Point(0,0),new Point(29,29)), map);
	}
	
}
