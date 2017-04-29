import burlap.behavior.singleagent.planning.stochastic.valueiteration.ValueIteration;
import burlap.domain.singleagent.graphdefined.GraphDefinedDomain;
import burlap.domain.singleagent.graphdefined.GraphStateNode;
import burlap.mdp.auxiliary.common.NullTermination;
import burlap.mdp.core.Domain;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.statehashing.discretized.DiscretizingHashableStateFactory;

public class FirstMDP {

	private GraphDefinedDomain dg;
	private Domain domain;
	private State initState;
	private RewardFunction rf;
	private TerminalFunction tf;
	private DiscretizingHashableStateFactory hashFactory;
	private int numStates;
	
	public FirstMDP(double p1, double p2, double p3, double p4) {
		numStates = 6;
		
		this.dg = new GraphDefinedDomain(numStates);
		this.domain = this.dg.generateDomain();
		
		// actions for initial state 0
	    ((GraphDefinedDomain) this.dg).setTransition(0,0,1,1.); //action a
	    ((GraphDefinedDomain) this.dg).setTransition(0,1,2,1.); //action b
	    ((GraphDefinedDomain) this.dg).setTransition(0,2,3,1.); //action c

	    // actions for all the other states
	    ((GraphDefinedDomain) this.dg).setTransition(1,0,1,1.); //action for state 1
	    ((GraphDefinedDomain) this.dg).setTransition(2,0,4,1.); //action for state 2
	    ((GraphDefinedDomain) this.dg).setTransition(3,0,5,1.); //action for state 3
	    ((GraphDefinedDomain) this.dg).setTransition(4,0,2,1.); //action for state 4
	    ((GraphDefinedDomain) this.dg).setTransition(5,0,5,1.); //action for state 5    
		
		this.initState = new GraphStateNode();
		((GraphStateNode)this.initState).setId(0);
		
		this.rf = new FourParamRF(p1,p2,p3,p4);
		this.tf = new NullTermination();
		this.hashFactory = new DiscretizingHashableStateFactory(1);
	}
	
	public static class FourParamRF implements RewardFunction {
		double p1;
		double p2;
		double p3;
		double p4;
		
		public FourParamRF(double p1, double p2, double p3, double p4) {
			this.p1 = p1;
			this.p2 = p2;
			this.p3 = p3;
			this.p4 = p4;
		}

		public double reward(State src, Action action, State dest) {
			double r = 0;
			int sid = ((GraphStateNode)src).getId();

			if(sid==0 || sid==3) {
				r = 0;
			} else if(sid == 1) {
				r = this.p1;
			} else if(sid == 2) {
				r = this.p2;
			} else if(sid == 4) {
				r = this.p3;
			} else if (sid ==5) {
				r = this.p4;
			}
			
			return r;
		}
		
    }
	
	private ValueIteration computeValue(double gamma) {
		double maxDelta = 0.0001;
		int maxIterations = 1000;
		ValueIteration vi = new ValueIteration((SADomain)this.domain, gamma, this.hashFactory, maxDelta, maxIterations);
		
		//vi.planFromState(initState);
		vi.performReachabilityFrom(initState);
		
		return vi;
	}
	
	public String bestFirstAction(double gamma) {
		ValueIteration vi = this.computeValue(gamma);
		double V[] = new double[this.numStates];
		
		for(int i=0 ; i<this.numStates ; i++) {
			GraphStateNode stateNode = new GraphStateNode(i);
			State s = stateNode;
			
			V[i] = vi.value(s); 
		}
		
		String actionName = "action a";
		
		if(V[2] >= V[1] && V[2] >= V[3]) actionName = "action b";
		else if (V[3] >=V[1] && V[3] >= V[2]) actionName = "action c";
		
		return actionName;
	}
	
	public static void main(String[] args) {
		FirstMDP firstMDP = new FirstMDP(5, 6, 3, 7);
		
		String bestFirstAction = firstMDP.bestFirstAction(.99);
		System.out.println(bestFirstAction);
	}
}
