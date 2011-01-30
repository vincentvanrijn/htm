package com.numenta.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.numenta.model.helper.CellHelper;


public class Column {
	
	
	
		
	///private double[] timesActive;
	private ArrayList<Boolean> activeList= new ArrayList<Boolean>();


	private ArrayList <Boolean> timesGreaterOverlapThanMinOverlap=new ArrayList<Boolean>();
	

	private boolean active=false;
	private boolean greaterThanMinimalOverlap=false;
	public boolean isGreaterThanMinimalOverlap() {
		return greaterThanMinimalOverlap;
	}

	public void setGreaterThanMinimalOverlap(boolean greaterThanMinimalOverlap) {
		this.greaterThanMinimalOverlap = greaterThanMinimalOverlap;
	}

	public static int CELLS_PER_COLUMN;
	private double boost;
	private double overlap;
	public static  int MINIMAL_OVERLAP=2;//TODO choose reasonable overlap
	private double minimalDesiredDutyCycle;
/*	A sliding average representing how often column c has 
	been active after inhibition (e.g. over the last 1000 
	iterations).*/
	private double activeDutyCycle;
public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	/*	A sliding average representing how often column c has had 
	significant overlap (i.e. greater than minOverlap) with its 
	inputs (e.g. over the last 1000 iterations).*/		
	private double overlapDutyCycle;
	
	public double getActiveDutyCycle() {
		return activeDutyCycle;
	}

	

	private Map<CellHelper, Boolean> predictiveState=new HashMap<CellHelper, Boolean>();
	private Map<CellHelper, Boolean> activeState=new HashMap<CellHelper, Boolean>();

	public Map<CellHelper, Boolean> getActiveState() {
		return activeState;
	}

	public void setActiveState(Map<CellHelper, Boolean> activeState) {
		this.activeState = activeState;
	}

	public double getMinimalDesiredDutyCycle() {
		return minimalDesiredDutyCycle;
	}

	public void setMinimalDesiredDutyCycle(double minimalDesiredDutyCycle) {
		this.minimalDesiredDutyCycle = minimalDesiredDutyCycle;
	}

	
	public Map<CellHelper, Boolean> getPredictiveState() {
		return predictiveState;
	}

	public void setPredictiveState(Map<CellHelper, Boolean> predictiveState) {
		this.predictiveState = predictiveState;
	}


	private Column[] neigbours;
	
	
	private Synapse[] potentialSynapses;

	public double getOverlap() {
		return overlap;
	}

	public void setOverlap(double d) {
		this.overlap = d;
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

	

	public double getBoost() {
		return boost;
	}

	public void setBoost(double boost) {
		this.boost = boost;
	}

	public double getConnectedPerm() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void increasePermanance(double d) {
		// TODO Auto-generated method stub
		
	}

	public Segment getActiveSegment(int cell, int time, String activeState) {
		// TODO Auto-generated method stub
		return null;
	}
	public double updateOverlapDutyCycle() {
		
		this.timesGreaterOverlapThanMinOverlap.add(0, this.isGreaterThanMinimalOverlap());
		if(timesGreaterOverlapThanMinOverlap.size()>1000){
			timesGreaterOverlapThanMinOverlap.remove(1000);
		}
		int totalGt=0;
		for(int i=0;i<timesGreaterOverlapThanMinOverlap.size();i++){
			if(timesGreaterOverlapThanMinOverlap.get(i)){
				totalGt++;
			}
		}
		this.overlapDutyCycle=(double)totalGt/timesGreaterOverlapThanMinOverlap.size();
		
		
		return overlapDutyCycle;
		
	}
	//TODO	updateActiveDutyCycle(c)
	//Computes a moving average of how often column c has been active after 
	//inhibition.
	
	public double updateActiveDutyCycle() {
		activeList.add(0, this.isActive());
		if(activeList.size()>1000){
			activeList.remove(1000);
		}
		int totalActive=0;
		for(int i=0;i<activeList.size();i++){
			if(activeList.get(i)){
				totalActive++;
			}
		}
		this.activeDutyCycle=(double)totalActive/activeList.size();
		
		
		return activeDutyCycle;
	}

	public ArrayList<Boolean> getActiveList() {
		return activeList;
	}

	public void setActive(ArrayList<Boolean> activeList) {
		this.activeList = activeList;
	}

}
