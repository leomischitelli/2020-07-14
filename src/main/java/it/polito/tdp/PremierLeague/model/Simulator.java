package it.polito.tdp.PremierLeague.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

import it.polito.tdp.PremierLeague.model.Event.EventType;

public class Simulator {
	
	//coda degli eventi
	private Queue<Event> queue;
	
	private Graph<Team, DefaultWeightedEdge> grafo;
	private List<Match> matches;
	private Model model;
	
	private int N;
	private int soglia;
	
	private int sfori;
	private Map<Team, Integer> stato;
	private Map<Integer, Team> idMapTeams;
	
	private int nPartite;
	private int nReporter;
	
	
	
	public Simulator(Graph<Team, DefaultWeightedEdge> grafo, Model model, Map<Integer, Team> idMapTeams) {
		super();
		this.grafo = grafo;
		this.model = model;
		this.idMapTeams = idMapTeams;
	}



	public void init(int N, int soglia) {
		this.queue = new PriorityQueue<>();
		if(matches == null)
			this.matches = new ArrayList<>(this.model.listAllMatches());
		this.N = N;
		this.soglia = soglia;
		this.sfori = 0;
		this.stato = new HashMap<>();
		for(Team t : this.grafo.vertexSet())
			this.stato.put(t, this.N);
		this.nPartite = 0;
		this.nReporter = 0;
		
		for(Match m : this.matches) {
			Team home = idMapTeams.get(m.getTeamHomeID());
			Team away = idMapTeams.get(m.getTeamAwayID());
			
			
			if(m.getResultOfTeamHome() == 1) { 
				
				calcoloProb(m, home, away); //home vincente
			} else if (m.getResultOfTeamHome() == -1) {
				
				calcoloProb(m, away, home); //away vincente
			} else {
				this.queue.add(new Event(m.getDate(), EventType.REGOLARE, home, away)); //distinzione indifferente
			}
			
			
			
		}
	}



	private void contaSpettatori(Team t1, Team t2) {
		this.nPartite++;
		int spettatori = stato.get(t1) + stato.get(t2);
		if(spettatori < this.soglia)
			this.sfori++;
		this.nReporter+=spettatori;
	}



	private void calcoloProb(Match match, Team vincente, Team perdente) {
		
		if(Math.random() < 0.50) { 
			//per vincente
			this.queue.add(new Event(match.getDate(), EventType.PROMOZIONE, vincente, perdente));
		} else {
			this.queue.add(new Event(match.getDate(), EventType.REGOLARE, vincente, perdente)); //distinzione irrilevante tra vinc e perd in questo caso
		}
		
		if(Math.random() < 0.20) { 
			//per perdente
			this.queue.add(new Event(match.getDate(), EventType.BOCCIATURA, perdente, vincente));
		} else {
			this.queue.add(new Event(match.getDate(), EventType.REGOLARE, vincente, perdente)); //distinzione irrilevante tra vinc e perd in questo caso
		}
		
	}
	
	
	public void run() {
		while(!this.queue.isEmpty()) {
			Event e = this.queue.poll();
			processEvent(e);
		}
	}



	private void processEvent(Event e) {
		
		LocalDateTime time = e.getTime();
		Team teamScelto = e.getTeamScelto();
		Team altroTeam = e.getAltroTeam();
		EventType type = e.getType();
		
		contaSpettatori(teamScelto, altroTeam);
		
		//fine partita, faccio le scelte
		switch(type) {
		
		case PROMOZIONE:
			//associo un reporter a squadra migliore in campionato
			if(this.stato.get(teamScelto) > 0) {
				List<Coppie> migliori = this.model.squadreMigliori(teamScelto);
				if(!migliori.isEmpty()) {
					int indice = (int) (Math.random() * (migliori.size() - 1));
					Team scelto = migliori.get(indice).getTeam();
					this.stato.put(scelto, (this.stato.get(scelto) + 1));
					this.stato.put(teamScelto, (this.stato.get(teamScelto) - 1));
				}
			}
			
			break;
			
		case BOCCIATURA:
			//assegno un numero casuale di reportere a squadra peggiore in campionato
			if(this.stato.get(teamScelto) > 0) {
				List<Coppie> peggiori = this.model.squadrePeggiori(teamScelto);
				if(!peggiori.isEmpty()) {
					int quantita = (int) (Math.random() * (this.stato.get(teamScelto) - 1)) + 1;
					int indice = (int) (Math.random() * (peggiori.size() - 1));
					Team scelto = peggiori.get(indice).getTeam();
					this.stato.put(scelto, (this.stato.get(scelto) + quantita));
					this.stato.put(teamScelto, (this.stato.get(teamScelto) - quantita));
				}
			}
			break;
			
		case REGOLARE:
			//non faccio niente
			break;
		}
		
		
	}

	public double getMediaReporter() {
		return ((double)nReporter) / nPartite;
	}

	public int getSfori() {
		return sfori;
	}
	
	
	
	

}
