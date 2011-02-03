package com.numenta.pooler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.numenta.model.Column;
import com.numenta.model.Synapse;

public class SpatialPooler {

	private int[] inputSpace;

	private static int DISIRED_LOCAL_ACTIVITY=2;

	private double inhibitionRadius = 1;

	private Column[] columns;

	public ArrayList<Column> activeColumns = new ArrayList();

	Logger logger=Logger.getLogger(SpatialPooler.class.getName());
	public Column[] getColumns() {
		return columns;
	}

	public void setColumns(Column[] columns) {
		this.columns = columns;
	}

	public void conectSynapsesToInputSpace(int[] inputSpace) {
		logger.log(Level.FINE, "conecting synapsesToInputSpace");
		this.inputSpace = inputSpace;
		for (int i = 0; i < columns.length; i++) {
			Column column = columns[i];
			for (int j = 0; j < column.getPotentialSynapses().length; j++) {
				Synapse synapse = column.getPotentialSynapses()[j];
				synapse.setSourceInput(inputSpace[synapse.getInputSpaceIndex()]);
			}
		}
		logger.log(Level.INFO, "connected synapses");
	}

	public SpatialPooler() {// 144

		logger.log(Level.INFO, "SpatialPooler");
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
				HashSet<Integer> synapsesToInput = new HashSet<Integer>();

				for (int j = 0; j < synapses.length; j++) {
					synapses[j] = new Synapse();
					synapses[j].setPermanance(Synapse.CONECTED_PERMANANCE - 1
							+ random.nextInt(2));
					int inputSpaceIndex = 0;
					do {
						inputSpaceIndex = random.nextInt(144);
					} while (!synapsesToInput.add(inputSpaceIndex));
					synapses[j].setInputSpaceIndex(inputSpaceIndex);

				}
				columns[i].setPotentialSynapses(synapses);
				i++;
			}
		}
		//set neighbors
		for (int x = 0; x < this.columns.length; x++) {
			ArrayList<Column> neigbors = new ArrayList<Column>();
			Column column = this.columns[x];
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
					neigbors.add(potentialNeigbor);					
				}
			}

			Object[] objects = neigbors.toArray();
			Column[] neig = new Column[objects.length];
			System.arraycopy(objects, 0, neig, 0, objects.length);
			column.setNeigbours(neig);

		}
		//log the brain
		for (int j = 0; j < columns.length; j++) {
			Column column = columns[j];
			logger.log(Level.INFO, "Column " + j + " " + column.getxPos() + " "
					+ column.getyPos() + " neigbors="
					+ column.getNeigbours().length);
			for (int k = 0; k < column.getPotentialSynapses().length; k++) {
				Synapse synapse = column.getPotentialSynapses()[k];
				logger.log(Level.INFO, "synapse" + k + " input= "
						+ synapse.getSourceInput() + " permanance= "
						+ synapse.getPermanance());
			}

		}

	}

	public void computOverlap() {
		logger.log(Level.FINE, "computOverlap");
		for (int i = 0; i < this.columns.length; i++) {
			double overlap = 0;
			Column column = this.columns[i];
			Synapse[] connectedSynapses = column.getConnectedSynapses();
			logger.log(Level.INFO, "connected syn=" + connectedSynapses.length);
			for (int j = 0; j < connectedSynapses.length; j++) {
				Synapse connectedSynapse = connectedSynapses[j];
				int t = 1;
				overlap += input(t, connectedSynapse.getSourceInput());

			}
			logger.log(Level.INFO, "overlap=" + overlap);
			if (overlap < Column.MINIMAL_OVERLAP) {
				column.setOverlap(0);
				column.addGreaterThanMinimalOverlap(false);
			} else {
				column.setOverlap(overlap * column.getBoost());

				column.addGreaterThanMinimalOverlap(true);
				logger.log(Level.INFO, "overlap=" + overlap * column.getBoost());
			}
			column.updateOverlapDutyCycle();
			
			
		}
	}

	/*
	 * input(t,j) The input to this level at time t. input(t, j) is 1 if the
	 * j'th input is on.
	 */

	public void computeWinningColumsAfterInhibition() {

		logger.log(Level.FINE, "computeWinningColumsAfterInhibition");
		activeColumns=new ArrayList<Column>();
		for (int i = 0; i < columns.length; i++) {
			Column column = columns[i];

			double minimalLocalActivity = kthScore(column.getNeigbours(),
					DISIRED_LOCAL_ACTIVITY);

			if ((column.getOverlap() > 0)
					&& column.getOverlap() >= minimalLocalActivity) {

				column.setActive(true);
				activeColumns.add(column);
			} else{
				column.setActive(false);
			}
			column.updateActiveDutyCycle();
		}
	}

	public void updateSynapses() {
		logger.log(Level.FINE, "updateSynapses");
		for (Iterator i = activeColumns.iterator(); i.hasNext();) {
			Column activeColumn = (Column) i.next();
			Synapse[] potentialSynapses = activeColumn.getPotentialSynapses();
			for (int j = 0; j < potentialSynapses.length; j++) {
				Synapse potentialSynapse = potentialSynapses[j];
				double permanance = potentialSynapse.getPermanance();
				if (potentialSynapse.isActive()) {

					potentialSynapse.setPermanance(permanance+Synapse.PERMANENCE_INC);
					logger.log(Level.INFO, ""+potentialSynapse.getPermanance());
					potentialSynapse.setPermanance(Math.min(potentialSynapse
							.getPermanance(), 1.0));

				} else {
					potentialSynapse.setPermanance(permanance-Synapse.PERMANENCE_DEC);
					potentialSynapse.setPermanance(Math.max(potentialSynapse
							.getPermanance(), 0.0));
				}
			}

		}
		for (int i = 0; i < columns.length; i++) {
			Column column = columns[i];

			double minimalDutyCycle = (0.01 * (getMaxDutyCycle(column
					.getNeigbours())));
			logger.log(Level.INFO, "minimalDutyCycle="+minimalDutyCycle);

			double activeDutyCycle = column.getActiveDutyCycle();
			logger.log(Level.INFO, "activeDutyCycle="+activeDutyCycle);
			column.calculateBoost(activeDutyCycle, minimalDutyCycle);
			
			

			double overlapDutyCycle = column.updateOverlapDutyCycle();

			if (overlapDutyCycle < minimalDutyCycle) {
				column.increasePermanances(0.1 * Synapse.CONECTED_PERMANANCE);

			}

		}
		// TODO implement this in a right way
		int averageReceptiveFieldSize = 1;
		this.inhibitionRadius = averageReceptiveFieldSize;
	}

	private int input(int t, int sourceInput) {

		return sourceInput;
	}

	// TODO updateOverlapDutyCycle(c)
	// Computes a moving average of how often column c has overlap greater
	// than minOverlap.
	//

	

	private double getMaxDutyCycle(Column[] neigbours) {

		Column highestNeighbor = null;
		for (int i = 0; i < neigbours.length; i++) {
			Column neigbour = neigbours[i];
			if (i == 0) {
				highestNeighbor = neigbour;
			} else {

				if (neigbour.getActiveDutyCycle() > highestNeighbor
						.getActiveDutyCycle()) {
					highestNeighbor = neigbour;
				}
			}
		}
		logger.log(Level.INFO, "highestActiveDutyCycly="+highestNeighbor.getActiveDutyCycle());
		return highestNeighbor.getActiveDutyCycle();
	}

	private double kthScore(Column[] neigbours, int disiredLocalActivity) {
		if (disiredLocalActivity > neigbours.length) {
			disiredLocalActivity = neigbours.length;
		}
		// get the overlap of the column that has number <disiredActivity> in
		// boost
		// logger.log(Level.INFO, "amountofNeigbors="+neigbours.length);
		ArrayList<Column> orderedNeigbours = new ArrayList<Column>();

		for (int i = 0; i < neigbours.length; i++) {
			Column neigbour = neigbours[i];
			if (i == 0) {
				orderedNeigbours.add(neigbour);
			} else {
				for (int j = 0; j < orderedNeigbours.size(); j++) {
					Column orderedNeigbour = orderedNeigbours.get(j);
					if (neigbour.getOverlap() <= orderedNeigbour.getOverlap()) {
						orderedNeigbours.add(j, neigbour);
						break;
					} else {
						orderedNeigbours.add(neigbour);
						break;
					}
				}
			}
		}
		// logger.log(Level.INFO, "amountofOrderedNeigbors="+orderedNeigbours.size());
		return orderedNeigbours.get(disiredLocalActivity - 1).getOverlap();

	}

}
