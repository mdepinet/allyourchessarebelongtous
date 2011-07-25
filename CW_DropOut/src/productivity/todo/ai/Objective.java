package productivity.todo.ai;

import java.awt.Point;

public class Objective implements Comparable<Objective> {
	private Point location;
	private double weight;
	private double cost;
	public Objective(Point p, double w, double c) {
		location = p;
		weight = w;
		cost = c;
	}

	public Point getLocation() {
		return location;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
	public boolean equals(Object o) {
		return ((Objective)o).getLocation().equals(getLocation());
	}
	public int compareTo(Objective other) {
		return (int)((weight-cost) - (other.getWeight()-other.getCost()));
	}
}
