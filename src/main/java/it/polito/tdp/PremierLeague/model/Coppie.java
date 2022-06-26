package it.polito.tdp.PremierLeague.model;

public class Coppie implements Comparable<Coppie> {

	private Team team;
	private int differenza;
	
	
	public Coppie(Team team, int differenza) {
		super();
		this.team = team;
		this.differenza = differenza;
	}
	
	
	public Team getTeam() {
		return team;
	}


	public void setTeam(Team team) {
		this.team = team;
	}


	public int getDifferenza() {
		return differenza;
	}
	public void setDifferenza(int differenza) {
		this.differenza = differenza;
	}


	@Override
	public int compareTo(Coppie o) {
		// TODO Auto-generated method stub
		return this.differenza - o.getDifferenza();
	}


	@Override
	public String toString() {
		return team.getName()+"("+differenza+")";
	}
	
	
	
	
}
