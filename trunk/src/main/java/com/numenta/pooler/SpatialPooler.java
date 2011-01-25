package com.numenta.pooler;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

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
				//TODO overlap(c)=overlap(c) + input(t, s.ourceinput)
				overlap+=connectedSynapse.getSourceInput();
			}
			
			
			if(overlap<Column.MINIMAL_OVERLAP){
				column.setOverlap(0);
				
			} else{
				column.setOverlap(overlap*column.getBoost());
			}			
		}
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

	private void updateSynapses(){
		for (Iterator i = activeColumns.iterator(); i.hasNext();) {
			Column activeColumn = (Column) i.next();
			Synapse[] potentialSynapses=activeColumn.getPotentialSynapses();
			for (int j = 0; j < potentialSynapses.length; j++) {
				Synapse potentialSynapse=potentialSynapses[j];
				int permanance=potentialSynapse.getPermanance();
				if(potentialSynapse.isActive()){
					
					potentialSynapse.setPermanance(permanance++);
					//TODO s.permanance= min(1.0, s.permanance)
				} else{
					potentialSynapse.setPermanance(permanance--);
					//TODO s.permanance= max(0.0, s.permanance)
				}
			}
			
		}
		for (int i = 0; i < columns.length; i++) {
			Column column=columns[i];
			column.setMinimalDesiredDutyCycle(0.01 * (getMaxDutyClycle(column.getNeigbours())));
			
		}		
	}	
	private int getMaxDutyClycle(Column[] neigbours){
		return 1;
		
	}
}
