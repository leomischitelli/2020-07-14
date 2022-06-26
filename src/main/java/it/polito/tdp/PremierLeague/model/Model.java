package it.polito.tdp.PremierLeague.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.PremierLeague.db.PremierLeagueDAO;

public class Model {
	
	private PremierLeagueDAO dao;
	private Graph<Team, DefaultWeightedEdge> grafo;
	private List<Team> teams;
	private List<Match> matches;
	private Map<Integer, Team> idMapTeams;
	private Simulator sim;
	
	public Model() {
		this.dao = new PremierLeagueDAO();
	}
	
	public void creaGrafo() {
		
		this.grafo = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		
		if(this.teams == null) {
			this.teams = new ArrayList<>(this.dao.listAllTeams());
		}
		
		this.idMapTeams = new TreeMap<>();
		
		for(Team t : this.teams) {
			idMapTeams.put(t.getTeamID(), t);
		}
		
		Graphs.addAllVertices(this.grafo, this.teams);
		
		if(this.matches == null)
			this.matches = new ArrayList<>(this.dao.listAllMatches());
		
		for(Match m : this.matches) {
			Team home = idMapTeams.get(m.getTeamHomeID());
			Team away = idMapTeams.get(m.getTeamAwayID());
			if(m.getResultOfTeamHome() == 1)
				home.increasePoints(3);
			else if(m.getResultOfTeamHome() == -1)
				away.increasePoints(3);
			else {
				home.increasePoints(1);
				away.increasePoints(1);
			}
		}
		
		Collections.sort(this.teams);
		
		for(Team t1 : this.teams) {
			for(Team t2 : this.teams) {
				if(t1.compareTo(t2) < 0) {
					Graphs.addEdge(this.grafo, t1, t2, t1.getPoints() - t2.getPoints());
				}
			}
			
		}
		
		
	}
	
	
	
	
	public List<Team> listAllTeams() {
		return this.dao.listAllTeams();
	}
	
	public List<Match> listAllMatches(){
		return this.dao.listAllMatches();
	}
	
	public int getNumeroVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public int getNumeroArchi() {
		return this.grafo.edgeSet().size();
	}

	public List<Coppie> squadrePeggiori(Team team) {
		Team teamPoints = idMapTeams.get(team.getTeamID());
//		Map<Team, Integer> result = new LinkedHashMap<>();
		
		List<Coppie> output = new ArrayList<>();
//		Collections.sort(this.teams);
		for(Team t : this.teams) {
			if(teamPoints.compareTo(t) < 0) {
				
				output.add(new Coppie(t, teamPoints.getPoints() - t.getPoints()));
			} 
			
		}
		
		
		return output;
	}

	public List<Coppie> squadreMigliori(Team team) {
		Team teamPoints = idMapTeams.get(team.getTeamID());
		List<Coppie> output = new ArrayList<>();
//		Collections.sort(this.teams);
		for(Team t : this.teams) {
			if(teamPoints.compareTo(t) > 0) {
				output.add(new Coppie(t, t.getPoints() - teamPoints.getPoints()));
			} 
			
		}
		//devo invertire l'ordine delle squadre migliori per differenza crescente
		Collections.sort(output);
		
		
		return output;
	}
	
	public void simula(int N, int soglia) {
		this.sim = new Simulator(this.grafo, this, this.idMapTeams);
		sim.init(N, soglia);
		sim.run();
	}
	
	public double getMediaReporter() {
		return this.sim.getMediaReporter();
	}

	public int getSfori() {
		return this.sim.getSfori();
	}
}
