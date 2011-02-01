package com.numenta.pooler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.numenta.model.Column;
import com.numenta.model.Synapse;

public class SpatialPooler {

	public SpatialPooler() {
		inputSpace = new int[100];
		DISIRED_LOCAL_ACTIVITY = 2;
		columns = new Column[25];
		// lets say every column has 10 synapses
		for (int i = 0; i < columns.length; i++) {
			columns[i] = new Column();
			Synapse[] synapses = new Synapse[10];
			for (int j = 0; j < synapses.length; j++) {
				synapses[j] = new Synapse();
				// TODO impement better random value;
				// int start = 12;
				// int end = 15;
				// int rnd = start + new Random().nextInt(end - start);
				synapses[j].setPermanance(Synapse.CONECTED_PERMANANCE - 1 + new Random().nextInt(3));
				// TODO synapse.setInput
			}
		}
		// TODO colums.setNeighbours();
		// TODO what is the natural center of input

	}

	private int[]				inputSpace;

	private static int			DISIRED_LOCAL_ACTIVITY;

	private Column[]			columns;

	private ArrayList<Column>	activeColumns;

	private void computOverlap() {
		for (int i = 0; i < columns.length; i++) {
			int overlap = 0;
			Column column = this.columns[i];
			Synapse[] connectedSynapses = column.getConnectedSynapses();

			for (int j = 0; j < connectedSynapses.length; j++) {
				Synapse connectedSynapse = connectedSynapses[j];
				int t = 1;
				overlap += input(t, connectedSynapse.getSourceInput());

			}

			if (overlap < Column.MINIMAL_OVERLAP) {
				column.setOverlap(0);
			} else {
				column.setOverlap(overlap * column.getBoost());

				column.setGreaterThanMinimalOverlap(true);
			}
		}
	}

	/*
	 * input(t,j) The input to this level at time t. input(t, j) is 1 if the j'th input is on.
	 */

	private void computeWinningColumsAfterInhibition() {
		for (int i = 0; i < columns.length; i++) {
			Column column = columns[i];

			double minimalLocalActivity = kthScore(column.getNeigbours(), DISIRED_LOCAL_ACTIVITY);

			if ((column.getOverlap() > 0) && column.getOverlap() >= minimalLocalActivity) {

				column.setActive(true);
				activeColumns.add(column);
			}
		}
	}

	private void updateSynapses() {
		for (Iterator i = activeColumns.iterator(); i.hasNext();) {
			Column activeColumn = (Column) i.next();
			Synapse[] potentialSynapses = activeColumn.getPotentialSynapses();
			for (int j = 0; j < potentialSynapses.length; j++) {
				Synapse potentialSynapse = potentialSynapses[j];
				double permanance = potentialSynapse.getPermanance();
				if (potentialSynapse.isActive()) {

					potentialSynapse.setPermanance(permanance++);
					double returnValue = 0;
					if (potentialSynapse.getPermanance() >= 1.0) {
						potentialSynapse.setPermanance(1.0);
					} else {
						potentialSynapse.setPermanance(potentialSynapse.getPermanance());
					}
				} else {
					potentialSynapse.setPermanance(permanance--);

					if (potentialSynapse.getPermanance() < 0.0) {
						potentialSynapse.setPermanance(0.0);
					} else {
						potentialSynapse.setPermanance(potentialSynapse.getPermanance());
					}

				}
			}

		}
		for (int i = 0; i < columns.length; i++) {
			Column column = columns[i];

			double minimalDutyCycle = (0.01 * (getMaxDutyCycle(column.getNeigbours())));

			double activeDutyCycle = column.updateActiveDutyCycle();

			double boost = calculateBoost(activeDutyCycle, minimalDutyCycle);
			column.setBoost(boost);

			double overlapDutyCycle = column.updateOverlapDutyCycle();

			if (overlapDutyCycle < minimalDutyCycle) {
				column.increasePermanance(0.1 * column.getConnectedPerm());

			}

		}
		// TODO implement this in a right way
		int averageReceptiveFieldSize = 0;
		int inhibitionRadius = averageReceptiveFieldSize;
	}

	private int input(int t, int sourceInput) {

		// TODO Auto-generated method stub
		return 0;
	}

	// TODO updateOverlapDutyCycle(c)
	// Computes a moving average of how often column c has overlap greater
	// than minOverlap.
	//

	private double calculateBoost(double activeDutyClycle, double minimalDesiredDutyCycle) {
		// TODO Auto-generated method stub
		double boost = 0;
		if (activeDutyClycle > minimalDesiredDutyCycle) {
			boost = 1;
		} else {
			boost = 1 / (activeDutyClycle / minimalDesiredDutyCycle);
		}

		return boost;
	}

	private double getMaxDutyCycle(Column[] neigbours) {

		ArrayList<Column> orderedNeigbours = new ArrayList<Column>();

		for (int i = 0; i < neigbours.length; i++) {
			Column neigbour = neigbours[i];
			if (i == 0) {
				orderedNeigbours.add(neigbour);
			} else {
				for (int j = 0; j < orderedNeigbours.size(); j++) {
					Column orderedNeigbour = orderedNeigbours.get(j);
					if (neigbour.getActiveDutyCycle() <= orderedNeigbour.getActiveDutyCycle()) {
						orderedNeigbours.add(j, neigbour);
						break;
					}
				}
			}
		}
		return orderedNeigbours.get(orderedNeigbours.size() - 1).getActiveDutyCycle();

	}

	private double kthScore(Column[] neigbours, int disiredLocalActivity) {
		if (disiredLocalActivity > neigbours.length) {
			disiredLocalActivity = neigbours.length;
		}
		// get the overlap of the column that has number <disiredActivity> in
		// boost
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
					}
				}
			}
		}
		return orderedNeigbours.get(disiredLocalActivity - 1).getOverlap();

	}
}
