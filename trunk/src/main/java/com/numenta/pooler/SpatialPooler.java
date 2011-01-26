package com.numenta.pooler;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.numenta.model.Column;
import com.numenta.model.Synapse;

public class SpatialPooler {
	
	public SpatialPooler(){
		inputSpace=new int[100];
		DISIRED_LOCAL_ACTIVITY=2;
		columns=new Column[25];
		//lets say every column has 10 synapses
		for (int i = 0; i < columns.length; i++) {
			columns[i]=new Column();
			Synapse[] synapses=new Synapse[10];
			for(int j=0;j<synapses.length;j++){
				synapses[j]=new Synapse();
				//TODO impement better random value;
				synapses[j].setPermanance(Synapse.CONECTED_PERMANANCE-1+new Random().nextInt(3));
				//TODO synapse.setInput
			}
		}
		//TODO colums.setNeighbours();
		//TODO what is the natural center of input
		
	}
	private int[] inputSpace;
	
	private static  int DISIRED_LOCAL_ACTIVITY ;
	
	private Column[] columns;
	private ArrayList<Column> activeColumns;

	private void computOverlap(){
		for (int i = 0; i < columns.length; i++) {
			int overlap=0;
			Column column=this.columns[i];
			Synapse[] connectedSynapses=column.getConnectedSynapses();
			
			for(int j=0;j<connectedSynapses.length;j++){
				Synapse connectedSynapse=connectedSynapses[j];
				int t=1;
				overlap += input(t, connectedSynapse.getSourceInput());
				
			}			
			
			if(overlap<Column.MINIMAL_OVERLAP){
				column.setOverlap(0);				
			} else{
				column.setOverlap(overlap*column.getBoost());
			}			
		}
	}	
	/*input(t,j)  The input to this level at time t. input(t, j) is 1 if the j'th 
		input is on.
		*/
	private int input(int t, int sourceInput) {
		
		
		// TODO Auto-generated method stub
		return 0;
	}

	private void computeWinningColumsAfterInhibition(){
		for (int i = 0; i < columns.length; i++) {
			Column column=columns[i];
			
			int minimalLocalActivity=kthScore(column.getNeigbours(), DISIRED_LOCAL_ACTIVITY);
			
			if((column.getOverlap()>0) && column.getOverlap()>= minimalLocalActivity){
				activeColumns.add(column);
			}
		}		
	}
	
	private void updateSynapses(){
		for (Iterator i = activeColumns.iterator(); i.hasNext();) {
			Column activeColumn = (Column) i.next();
			Synapse[] potentialSynapses=activeColumn.getPotentialSynapses();
			for (int j = 0; j < potentialSynapses.length; j++) {
				Synapse potentialSynapse=potentialSynapses[j];
				int permanance=potentialSynapse.getPermanance();
				if(potentialSynapse.isActive()){
					
					potentialSynapse.setPermanance(permanance++);
					potentialSynapse.setPermanance( min(1.0, potentialSynapse.getPermanance()));
				} else{
					potentialSynapse.setPermanance(permanance--);
					potentialSynapse.setPermanance( max(0.0, potentialSynapse.getPermanance()));
				}
			}
			
		}
		for (int i = 0; i < columns.length; i++) {
			Column column=columns[i];
			
			double minimalDutyCycle=(0.01 * (getMaxDutyCycle(column.getNeigbours())));
			
			int activeDutyCycle=updateActiveDutyCycle(column);
			
			int boost =calculateBoost(activeDutyCycle, minimalDutyCycle);
			column.setBoost(boost);
			
			int overlapDutyCycle=updateOverlapDutyCycle(column);
			
			if(overlapDutyCycle<minimalDutyCycle){
				column.increasePermanance(0.1*column.getConnectedPerm());
			
			}
			

		}		
		int averageReceptiveFieldSize = 0;
		int inhibitionRadius=  averageReceptiveFieldSize;
	}	
	
	private int max(double d, int permanance) {
		// TODO Auto-generated method stub
		return 0;
	}
	private int min(double d, int permanance) {
		// TODO Auto-generated method stub
		return 0;
	}
	private int updateOverlapDutyCycle(Column column) {
		// TODO Auto-generated method stub
		return 0;
	}

	private int calculateBoost(int activeDutyClycle,
			double minimalDesiredDutyCycle) {
		// TODO Auto-generated method stub
		return 0;
	}

	private int updateActiveDutyCycle(Column column) {
		// TODO Auto-generated method stub
		return 0;
	}

	private int getMaxDutyCycle(Column[] neigbours){
		// TODO Auto-generated method stub
		return 0;		
	}
	private int kthScore(Column[] neigbours, int disiredLocalActivity) {
		if(disiredLocalActivity> neigbours.length){
			disiredLocalActivity=neigbours.length;
		}
		// get the overlap of the column that has number <disiredActivity> in boost
		ArrayList<Column> orderedNeigbours =new ArrayList();
		
		
		for (int i = 0; i < neigbours.length; i++) {
			Column neigbour=neigbours[i];
			if(i==0){
				orderedNeigbours.add(neigbour);
			}else{
				for(int j=0;j<orderedNeigbours.size();j++){
					Column orderedNeigbour=orderedNeigbours.get(j);
					if(neigbour.getOverlap()<=orderedNeigbour.getOverlap()){
						orderedNeigbours.add(j,neigbour);
						break;
					}
				}
			}			
		}
		return orderedNeigbours.get(disiredLocalActivity-1).getOverlap();
		
	}
}
