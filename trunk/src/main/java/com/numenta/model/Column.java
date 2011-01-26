package com.numenta.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.numenta.model.helper.CellHelper;


public class Column {
	
	
	public static int CELLS_PER_COLUMN;
	private int boost;
	private int overlap;
	public static  int MINIMAL_OVERLAP=2;//TODO choose reasonable overlap
	private double minimalDesiredDutyCycle;
	private Map<CellHelper, Boolean> predictiveState=new HashMap<CellHelper, Boolean>();

	public double getMinimalDesiredDutyCycle() {
		return minimalDesiredDutyCycle;
	}

	public void setMinimalDesiredDutyCycle(double minimalDesiredDutyCycle) {
		this.minimalDesiredDutyCycle = minimalDesiredDutyCycle;
	}

	
	public Map getPredictiveState() {
		return predictiveState;
	}

	public void setPredictiveState(Map predictiveState) {
		this.predictiveState = predictiveState;
	}


	private Column[] neigbours;
	
	
	private Synapse[] potentialSynapses;

	public int getOverlap() {
		return overlap;
	}

	public void setOverlap(int overlap) {
		this.overlap = overlap;
	}

	public Synapse[] getPotentialSynapses() {
		return potentialSynapses;
	}

	public void setPotentialSynapses(Synapse[] potentialSynapses) {
		this.potentialSynapses = potentialSynapses;
	}

	

	public Column[] getNeigbours() {
		return neigbours;
	}

	public void setNeigbours(Column[] neigbours) {
		this.neigbours = neigbours;
	}

	public Synapse[] getConnectedSynapses() {
		ArrayList<Synapse> connectedSynapses=new ArrayList<Synapse>();
		for(int i=0;i<potentialSynapses.length;i++){
			Synapse potentialSynapse=potentialSynapses[i];
			if(potentialSynapse.isActive()){
				connectedSynapses.add(potentialSynapse);
			}
		}
		return (Synapse[])connectedSynapses.toArray();
	}

	

	public int getBoost() {
		return boost;
	}

	public void setBoost(int boost) {
		this.boost = boost;
	}

	public double getConnectedPerm() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void increasePermanance(double d) {
		// TODO Auto-generated method stub
		
	}

}
