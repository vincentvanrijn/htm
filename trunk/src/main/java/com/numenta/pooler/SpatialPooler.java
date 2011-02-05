package com.numenta.pooler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import com.numenta.model.Column;
import com.numenta.model.Synapse;

public class SpatialPooler {


	private static int DISIRED_LOCAL_ACTIVITY=3;

	private double inhibitionRadius = 5;

	private Column[] columns;

	public ArrayList<Column> activeColumns = new ArrayList<Column>();

	Logger logger=Logger.getLogger(SpatialPooler.class.getName());
	public Column[] getColumns() {
		return columns;
	}

	public void setColumns(Column[] columns) {
		this.columns = columns;
	}

	public ArrayList<Column> getActiveColumns() {
		return activeColumns;
	}

	public void conectSynapsesToInputSpace(int[] inputSpace) {
		for (int i = 0; i < inputSpace.length; i++) {
//			if(inputSpace[i]==0){
//				logger.info("hij is nu 0 "+i );
//			}
		}
//		logger.log(Level.FINE, "conecting synapsesToInputSpace");
//		this.inputSpace = inputSpace;
		for (int i = 0; i < columns.length; i++) {
			Column column = columns[i];
			for (int j = 0; j < column.getPotentialSynapses().length; j++) {
				Synapse synapse = column.getPotentialSynapses()[j];
				synapse.setSourceInput(inputSpace[synapse.getInputSpaceIndex()]);
			}
		}
//		logger.log(Level.INFO, "connected synapses");
	}

	public SpatialPooler() {// 144

//		logger.log(Level.INFO, "SpatialPooler");
		columns = new Column[144];

		// lets say every column has 10 synapses
		Random random = new Random();
		int i = 0;
		for (int y = 0; y < 12; y++) {
			for (int x = 0; x < 12; x++) {

				columns[i] = new Column();
				columns[i].setyPos(y);
				columns[i].setxPos(x);

				Synapse[] synapses = new Synapse[10];
				Set<Integer> synapsesToInput = new HashSet<Integer>();

				for (int j = 0; j < synapses.length; j++) {
					synapses[j] = new Synapse();
					synapses[j].setPermanance(Synapse.CONECTED_PERMANANCE - 0.2
							+ (((double)random.nextInt(4))/10));
					//logger.info(""+synapses[j].getPermanance());
					int inputSpaceIndex = 0;
					do {
						inputSpaceIndex = random.nextInt(144);
					} while (!synapsesToInput.add(inputSpaceIndex));
					synapses[j].setInputSpaceIndex(inputSpaceIndex);
					
					synapses[j].setyPos(inputSpaceIndex/12);
					synapses[j].setxPos(inputSpaceIndex%12);
					//logger.info(""+inputSpaceIndex+ " "+inputSpaceIndex/12 +" "+inputSpaceIndex%12);
				}
				
				columns[i].setPotentialSynapses(synapses);
				i++;//next column
			}
		}
	}

	public void computOverlap() {
	//	logger.log(Level.FINE, "computOverlap");
		for (int i = 0; i < this.columns.length; i++) {
			double overlap = 0.0;
			Column column = this.columns[i];
			Synapse[] connectedSynapses = column.getConnectedSynapses();
//			logger.log(Level.INFO, "connected syn=" + connectedSynapses.length);
			for (int j = 0; j < connectedSynapses.length; j++) {
				Synapse connectedSynapse = connectedSynapses[j];
				int t = 1;
				overlap += input(t, connectedSynapse.getSourceInput());
			}
			
			if (overlap < Column.MINIMAL_OVERLAP) {
				column.setOverlap(0);
				column.addGreaterThanMinimalOverlap(false);
			} else {
				//logger.info( "overlap=" + overlap);
				column.setOverlap(overlap * column.getBoost());

				column.addGreaterThanMinimalOverlap(true);
//				logger.log(Level.INFO, "overlap=" + overlap * column.getBoost());
			}
			column.updateOverlapDutyCycle();			
		}
	}

	

	public void computeWinningColumsAfterInhibition() {

		//logger.log(Level.FINE, "computeWinningColumsAfterInhibition");
		activeColumns=new ArrayList<Column>();
		for (int i = 0; i < columns.length; i++) {
			Column column = columns[i];

			column.setNeigbours(getNeigbors(column));
			
			double minimalLocalActivity = kthScore( column.getNeigbours(),
					DISIRED_LOCAL_ACTIVITY);//if inhibitioradius changes, shouldn't this also change?

			if (column.getOverlap() > 0 && column.getOverlap() >= minimalLocalActivity) {
				//logger.info("column Overlap "+ column.getOverlap()+ " minimal overlap"+ minimalLocalActivity);
				column.setActive(true);
				activeColumns.add(column);
			} else{
				column.setActive(false);
			}
			column.updateActiveDutyCycle();
		}
	}

	public void updateSynapses() {
//		logger.log(Level.FINE, "updateSynapses");
		for (Iterator<Column> i = activeColumns.iterator(); i.hasNext();) {
			Column activeColumn = (Column) i.next();
			Synapse[] potentialSynapses = activeColumn.getPotentialSynapses();
			for (int j = 0; j < potentialSynapses.length; j++) {
				Synapse potentialSynapse = potentialSynapses[j];
				double permanance = potentialSynapse.getPermanance();
				if (potentialSynapse.isActive()) {

					potentialSynapse.setPermanance(permanance+Synapse.PERMANENCE_INC);
//					logger.log(Level.INFO, ""+potentialSynapse.getPermanance());
					potentialSynapse.setPermanance(Math.min(potentialSynapse
							.getPermanance(), 1.0));

					//logger.info("plus "+potentialSynapse.getPermanance());

				} else {
					potentialSynapse.setPermanance(permanance-Synapse.PERMANENCE_DEC);
					potentialSynapse.setPermanance(Math.max(potentialSynapse
							.getPermanance(), 0.0));
					//logger.info("min "+potentialSynapse.getPermanance());
				}
			}

		}
		ArrayList<Integer> inhibitionRadiuses=new ArrayList<Integer>();
		for (int i = 0; i < columns.length; i++) {
			Column column = columns[i];
			double minimalDutyCycle = (0.01 * (getMaxDutyCycle(column
					.getNeigbours())));
			column.calculateBoost( minimalDutyCycle);

			double overlapDutyCycle = column.updateOverlapDutyCycle();

			if (overlapDutyCycle < minimalDutyCycle) {
				column.increasePermanances(0.1 * Synapse.CONECTED_PERMANANCE);
				//logger.info("increasePermanances of all synapses of the column");
			}
			Synapse[] connectedSynapses=column.getConnectedSynapses();
			for (int j = 0; j < connectedSynapses.length; j++) {
				Synapse synapse=connectedSynapses[j];
				
				inhibitionRadiuses.add(Math.max(Math.abs(column.getxPos()-synapse.getxPos()), Math.abs(column.getyPos()-synapse.getyPos())));
			}
			
		}
		
		double averageReceptiveFieldSize=0;
		
		for (Iterator<Integer> iterator = inhibitionRadiuses.iterator(); iterator
				.hasNext();) {
			Integer integer = (Integer) iterator.next();
			averageReceptiveFieldSize+=integer;
		}
		averageReceptiveFieldSize=averageReceptiveFieldSize/inhibitionRadiuses.size();
		inhibitionRadiuses=null;
		
		this.inhibitionRadius = averageReceptiveFieldSize;
		logger.info("new inhib fac="+this.inhibitionRadius);
	}

	private int input(int t, int sourceInput) {

		return sourceInput;
	}

		

	private double getMaxDutyCycle(List<Column> neighbors) {

		Column highestNeighbor = null;
		if(neighbors.size()>0){
			highestNeighbor=neighbors.get(0);
		}
		for (Iterator<Column> iterator = neighbors.iterator(); iterator.hasNext();) {
			Column neighbor = (Column) iterator.next();			

			if (neighbor.getActiveDutyCycle() > highestNeighbor
					.getActiveDutyCycle()) {
				highestNeighbor = neighbor;
			}
		}
//		logger.log(Level.INFO, "highestActiveDutyCycly="+highestNeighbor.getActiveDutyCycle());
		return highestNeighbor.getActiveDutyCycle();
	}
	
	private double kthScore( List<Column> neighbors, int disiredLocalActivity) {
		
		if (disiredLocalActivity > neighbors.size()) {
			disiredLocalActivity = neighbors.size();
		}
		// get the overlap of the column that has number <disiredActivity> in
		// overlap
		// logger.log(Level.INFO, "amountofNeigbors="+neigbours.length);
		
		Collections.sort(neighbors);
//		if(parent.getxPos()==5 && parent.getyPos()==5){
//			for (Iterator<Column> iterator = neighbors.iterator(); iterator.hasNext();) {
//				Column column = (Column) iterator.next();
//				//
//				logger.info("amount of neighbors "+ neighbors.size()+" "+parent.getxPos()+" "+parent.getyPos()+" overlap="+column.getOverlap());
//				if(column.getOverlap()>0.0){
//					for (int i = 0; i < column.getPotentialSynapses().length; i++) {
//						Synapse syn=column.getPotentialSynapses()[i];
//						logger.info("synapse+ "+syn.getxPos()+" "+syn.getyPos()+" "+syn.getPermanance()+" "+syn.getSourceInput());
//					}
//					
//				}
//			}
//			
//			logger.info(parent.getxPos()+" "+parent.getyPos()+" chosen overlap="+neighbors.get(disiredLocalActivity-1).getOverlap());
//			
//		}
		double ktScore=neighbors.get(disiredLocalActivity-1).getOverlap();
		
			return ktScore;

	}
	
	private List<Column> getNeigbors(Column column){		
		
		List<Column> neighbors = new ArrayList<Column>();
		for (int g = 0; g < this.columns.length; g++) {
			Column potentialNeigbor = this.columns[g];
			int xposColPlusIn = (int) (column.getxPos() + Math
					.round(inhibitionRadius));
			int yposColPlusIn = (int) (column.getyPos() + Math
					.round(inhibitionRadius));
			int xposColMinIn = (int) (column.getxPos() - Math
					.round(inhibitionRadius));
			int yposColMinIn = (int) (column.getyPos() - Math
					.round(inhibitionRadius));
			if ((xposColPlusIn >= potentialNeigbor.getxPos())
					&& (yposColPlusIn >= potentialNeigbor.getyPos())
					&& (xposColMinIn <= potentialNeigbor.getxPos())
					&& (yposColMinIn <= potentialNeigbor.getyPos() && column != potentialNeigbor)) {
				neighbors.add(potentialNeigbor);					
			}
		}

		
		column.setNeigbours(neighbors);
		//logger.info(column.getxPos()+" "+column.getyPos()+"amount of neigbors="+neighbors.size());
		//System.out.println(column.getxPos()+" "+column.getyPos()+"amount of neigbors="+neighbors.size());
		return neighbors;
	}
}
