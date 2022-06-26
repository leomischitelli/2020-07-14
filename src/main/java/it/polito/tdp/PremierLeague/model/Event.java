package it.polito.tdp.PremierLeague.model;

import java.time.LocalDateTime;

public class Event implements Comparable<Event> {
	
	public enum EventType{
		PROMOZIONE,
		BOCCIATURA,
		REGOLARE
	}
	
	
	private LocalDateTime time;
	private EventType type;
	private Team teamScelto;
	private Team altroTeam;
	
	
	
	
	public Event(LocalDateTime time, EventType type, Team teamScelto, Team altroTeam) {
		super();
		this.time = time;
		this.type = type;
		this.teamScelto = teamScelto;
		this.altroTeam = altroTeam;
	}

	


	public LocalDateTime getTime() {
		return time;
	}




	public EventType getType() {
		return type;
	}

	public Team getTeamScelto() {
		return teamScelto;
	}




	public Team getAltroTeam() {
		return altroTeam;
	}




	@Override
	public int compareTo(Event o) {
		// TODO Auto-generated method stub
		return this.time.compareTo(o.getTime());
	}
	
	

}
