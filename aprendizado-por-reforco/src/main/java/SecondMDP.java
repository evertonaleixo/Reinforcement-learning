import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.domain.singleagent.graphdefined.GraphStateNode;
import burlap.domain.singleagent.graphdefined.GraphTF;
import burlap.mdp.auxiliary.common.NullTermination;
import burlap.mdp.core.Domain;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.statehashing.discretized.DiscretizingHashableStateFactory;

public class SecondMDP {
	private GraphDefinedDomain dg;
	private SADomain domain;
	private State initState;
	private RewardFunction rf;
	private TerminalFunction tf;
	private DiscretizingHashableStateFactory hashFactory;
	private int numStates;
	
	
    public SecondMDP(double p1, double p2) {
    	this.numStates = 6;
    	this.dg = new GraphDefinedDomain(numStates);
    	this.domain = dg.generateDomain();
    	
    	// actions for state 0
    	// Saindo do 'estado 0' pegando o 'primeiro vertice' volta para o 'estado 0' com propabilidade 'p1'
    	((GraphDefinedDomain) this.dg).setTransition(0, 0, 0, p1);
    	((GraphDefinedDomain) this.dg).setTransition(0, 0, 1, (1-p1));
    	((GraphDefinedDomain) this.dg).setTransition(0, 1, 2, 1.);
    	
    	// actions for state 1
    	((GraphDefinedDomain) this.dg).setTransition(1, 0, 3, (1-p2));
    	((GraphDefinedDomain) this.dg).setTransition(1, 0, 5, p1);
    	((GraphDefinedDomain) this.dg).setTransition(1, 1, 4, 1.);
    	
    	// actions for state 2
    	((GraphDefinedDomain) this.dg).setTransition(2, 0, 1, 1.);
    	
    	// actions for state 3
    	((GraphDefinedDomain) this.dg).setTransition(3, 0, 1, 1.);
    	
    	// actions for state 4
    	((GraphDefinedDomain) this.dg).setTransition(4, 0, 5, 1.);
    	
    	this.initState = new GraphStateNode();
		((GraphStateNode)this.initState).setId(0);
    	
    	this.tf = new GraphTF(5);
    	
    	this.rf = new RewardFunction() {
			
			public double reward(State src, Action action, State dest) {
				int sid = ((GraphStateNode)src).getId();
				int did = ((GraphStateNode)dest).getId();
				
				if(sid==0 && did==0) {
					return -1;
				}
				if(sid==0 && did==1) {
					return 3;
				}
				if(sid==0 && did==2) {
					return 1;
				}
				if(sid==1 && did==3) {
					return 1;
				}
				if(sid==1 && did==5) {
					return 0;
				}
				if(sid==1 && did==4) {
					return 2;
				}
				if(sid==2 && did==1) {
					return 0;
				}
				if(sid==3 && did==1) {
					return 0;
				}
				if(sid==4 && did==5) {
					return 0;
				}
				
				return 0;
			}
		};
    	
    	this.hashFactory = new DiscretizingHashableStateFactory(10);
    }
    
    private ValueIteration computeValue(double gamma, State state) {
    	double maxDelta = 0.0001;
		int maxIterations = 1000;
		ValueIteration vi = new ValueIteration((SADomain)this.domain, gamma, this.hashFactory, maxDelta, maxIterations);
		
		//vi.planFromState(state);
		vi.performReachabilityFrom(state);
		
		return vi;
    }
    
	public String bestActions(double gamma) {
        // Return one of the following Strings
        // "a,c"
        // "a,d"
        // "b,c" 
        // "b,d"
        // based on the optimal actions at states S0 and S1. If 
        // there is a tie, break it in favor of the letter earlier in
        // the alphabet (so if "a,c" and "a,d" would both be optimal, 
        // return "a,c").
		ValueIteration vi = this.computeValue(gamma, this.initState);
		double V[] = new double[this.numStates];
		
		for(int i=0 ; i<2; i++) {
			GraphStateNode stateNode = new GraphStateNode(i);
			State s = stateNode;
			
			V[i] = vi.value(s); 
		}
		
		String actionName = "a";
		if(V[2] >= V[1]) actionName = "b";
		
		vi = this.computeValue(gamma, new GraphStateNode(1));
		for(int i=0 ; i<numStates; i++) {
			GraphStateNode stateNode = new GraphStateNode(i);
			State s = stateNode;
			
			V[i] = vi.value(s); 
		}
		
		if(V[4] >= V[5]) actionName += ",d";
		else actionName += ",c";
		
		return actionName;
    }
	
	public static void main(String[] args) {
		double p1 = 0.5;
		double p2 = 0.5;
		SecondMDP mdp = new SecondMDP(p1,p2);
		
		double gamma = 0.5;
		System.out.println("Best actions: " + mdp.bestActions(gamma));
	}
}