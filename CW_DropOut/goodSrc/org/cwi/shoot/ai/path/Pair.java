package org.cwi.shoot.ai.path;

public class Pair<T1, T2> {
	private T1 first;
	private T2 second;
	
	public Pair(T1 first, T2 second){
		this.first = first;
		this.second = second;
	}

	public T1 getFirst() {
		return first;
	}

	public void setFirst(T1 first) {
		this.first = first;
	}

	public T2 getSecond() {
		return second;
	}
	public Pair<T2, T1> flip()
	{
		return new Pair<T2,T1>(second, first);
	}
	public void setSecond(T2 second) {
		this.second = second;
	}
	public boolean equals(Object p){
		return hashCode() == p.hashCode();
	}
	public int hashCode(){
		return (first == null ? 0 : first.hashCode())*31+(second == null ? 0 : second.hashCode());
	}
	
	public String toString(){
		return getFirst()+" "+getSecond();
	}
	
}
