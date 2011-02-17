package com.numenta.pooler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.logging.Logger;

import com.numenta.model.Column;
import com.numenta.model.Synapse;

public class SpatialPooler {

	public static final int		AMMOUNT_OF_COLLUMNS			= 144;

	/**
	 * desiredLocalActivity A parameter controlling the number of columns that will be winners after the inhibition
	 * step.
	 */

	private int					desiredLocalActivity		= 3;

	/**
	 * connectedPerm If the permanence value for a synapse is greater than this value, it is said to be connected.
	 */

	private double				connectedPermanance			= 0.7;

	/**
	 * minOverlap A minimum number of inputs that must be active for a column to be considered during the inhibition
	 * step.
	 */

	private int					minimalOverlap				= 4;

	/**
	 * permanenceDec Amount permanence values of synapses are decremented during learning.
	 */
	private double				permananceDec				= 0.05;

	/**
	 * permanenceInc Amount permanence values of synapses are incremented during learning.
	 */
	private double				permananceInc				= 0.05;

	private int					amountOfSynapses			= 10;

	/**
	 * inhibitionRadius Average connected receptive field size of the columns.
	 */

	private double				inhibitionRadius			= 5.0;

	/**
	 * columns List of all columns.
	 */
	private Column[]			columns;

	private double				connectedPermananceMarge	= 0.2;

	/**
	 * activeColumns(t) List of column indices that are winners due to bottom-up input.
	 */

	public ArrayList<Column>	activeColumns				= new ArrayList<Column>();

	Logger						logger						= Logger.getLogger(SpatialPooler.class.getName());

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

		for (Column column : this.columns) {
			for (Synapse synapse : column.getPotentialSynapses()) {
				synapse.setSourceInput(inputSpace[synapse.getInputSpaceIndex()]);
			}
		}
	}

	public SpatialPooler(int desiredLocalActivity, double connectedPermanance, int minimalOverlap,
			double permananceDec, double permananceInc, int amountOfSynapses, double inhibitionRadius) {
		this.inhibitionRadius = inhibitionRadius;
		this.connectedPermanance = connectedPermanance;
		this.minimalOverlap = minimalOverlap;
		this.permananceDec = permananceDec;
		this.permananceInc = permananceInc;
		this.amountOfSynapses = amountOfSynapses;
		this.inhibitionRadius = inhibitionRadius;

		init();
	}

	public SpatialPooler() {// 144
		init();
	}

	/**
	 * Initialization Prior to receiving any inputs, the region is initialized by computing a list of initial potential
	 * synapses for each column. This consists of a random set of inputs selected from the input space. Each input is
	 * represented by a synapse and assigned a random permanence value. The random permanence values are chosen with two
	 * criteria. First, the values are chosen to be in a small range around connectedPerm (the minimum permanence value
	 * at which a synapse is considered "connected"). This enables potential synapses to become connected (or
	 * disconnected) after a small number of training iterations. Second, each column has a natural center over the
	 * input region, and the permanence values have a bias towards this center (they have higher values near the
	 * center).
	 */
	public void init() {
		// logger.log(Level.INFO, "SpatialPooler");
		columns = new Column[AMMOUNT_OF_COLLUMNS];

		// lets say every column has 10 synapses
		Random random = new Random();
		int i = 0;
		for (int y = 0; y < 12; y++) {
			for (int x = 0; x < 12; x++) {

				columns[i] = new Column();
				columns[i].setyPos(y);
				columns[i].setxPos(x);

				Synapse[] synapses = new Synapse[amountOfSynapses];
				Set<Integer> synapsesToInput = new HashSet<Integer>();

				for (int j = 0; j < synapses.length; j++) {
					synapses[j] = new Synapse();
					// TODO 4 is not correct permananceMarge is responsible for this value
					synapses[j].setPermanance(connectedPermanance - connectedPermananceMarge
							+ (((double) random.nextInt(4)) / 10));
					// logger.info(""+synapses[j].getPermanance());
					int inputSpaceIndex = 0;

					// Collections.shuffle(list);
					do {
						inputSpaceIndex = random.nextInt(144);
					} while (!synapsesToInput.add(inputSpaceIndex));
					synapses[j].setInputSpaceIndex(inputSpaceIndex);

					synapses[j].setyPos(inputSpaceIndex / 12);
					synapses[j].setxPos(inputSpaceIndex % 12);
					// logger.info(""+inputSpaceIndex+ " "+inputSpaceIndex/12 +" "+inputSpaceIndex%12);
				}

				columns[i].setPotentialSynapses(synapses);
				i++;// next column
			}
		}
	}

	public double getInhibitionRadius() {
		return inhibitionRadius;
	}

	/**
	 * Phase 1: Overlap Given an input vector, the first phase calculates the overlap of each column with that vector.
	 * The overlap for each column is simply the number of connected synapses with active inputs, multiplied by its
	 * boost. If this value is below minOverlap, we set the overlap score to zero.
	 */
	public void computOverlap() {
		// logger.log(Level.FINE, "computOverlap");
		for (Column column : this.columns) {
			double overlap = 0.0;
			// logger.log(Level.INFO, "connected syn=" + connectedSynapses.length);
			for (Synapse connectedSynapse : column.getConnectedSynapses(connectedPermanance)) {
				int t = 1;
				overlap += input(t, connectedSynapse.getSourceInput());
			}

			if (overlap < minimalOverlap) {
				column.setOverlap(0);
				column.addGreaterThanMinimalOverlap(false);
			} else {
				// logger.info( "overlap=" + overlap);
				column.setOverlap(overlap * column.getBoost());

				column.addGreaterThanMinimalOverlap(true);
				// logger.log(Level.INFO, "overlap=" + overlap * column.getBoost());
			}
			column.updateOverlapDutyCycle();
		}
	}

	/**
	 * Phase 2: Inhibition The second phase calculates which columns remain as winners after the inhibition step.
	 * desiredLocalActivity is a parameter that controls the number of columns that end up winning. For example, if
	 * desiredLocalActivity is 10, a column will be a winner if its overlap score is greater than the score of the 10'th
	 * highest column within its inhibition radius.
	 */
	public void computeWinningColumsAfterInhibition() {

		// logger.log(Level.FINE, "computeWinningColumsAfterInhibition");
		activeColumns = new ArrayList<Column>();
		for (Column column : this.columns) {
			column.setNeigbours(getNeigbors(column));

			double minimalLocalActivity = kthScore(column.getNeigbours(), desiredLocalActivity);// if inhibitioradius
			// changes, shouldn't
			// this also change?
			column.setMinimalLocalActivity(minimalLocalActivity);
			if (column.getOverlap() > 0 && column.getOverlap() >= minimalLocalActivity) {
				// logger.info("column Overlap "+ column.getOverlap()+ " minimal overlap"+ minimalLocalActivity);
				column.setActive(true);
				activeColumns.add(column);
			} else {
				column.setActive(false);
			}
			column.updateActiveDutyCycle();
		}
	}

	/**
	 * Phase 3: Learning The third phase performs learning; it updates the permanence values of all synapses as
	 * necessary, as well as the boost and inhibition radius. The main learning rule is implemented in lines 20-26. For
	 * winning columns, if a synapse is active, its permanence value is incremented, otherwise it is decremented.
	 * Permanence values are constrained to be between 0 and 1. Lines 28-36 implement boosting. There are two separate
	 * boosting mechanisms in place to help a column learn connections. If a column does not win often enough (as
	 * measured by activeDutyCycle), its overall boost value is increased (line 30-32). Alternatively, if a column's
	 * connected synapses do not overlap well with any inputs often enough (as measured by overlapDutyCycle), its
	 * permanence values are boosted (line 34-36). Note: once learning is turned off, boost(c) is frozen. Finally, at
	 * the end of Phase 3 the inhibition radius is recomputed (line 38).
	 */
	public void updateSynapses() {
		// logger.log(Level.FINE, "updateSynapses");
		for (Column activeColumn : activeColumns) {
			for (Synapse potentialSynapse : activeColumn.getPotentialSynapses()) {

				double permanance = potentialSynapse.getPermanance();
				if (potentialSynapse.isActive(connectedPermanance)) {

					potentialSynapse.setPermanance(permanance + permananceInc);
					// logger.log(Level.INFO, ""+potentialSynapse.getPermanance());
					potentialSynapse.setPermanance(Math.min(potentialSynapse.getPermanance(), 1.0));

					// logger.info("plus "+potentialSynapse.getPermanance());

				} else {
					potentialSynapse.setPermanance(permanance - permananceDec);
					potentialSynapse.setPermanance(Math.max(potentialSynapse.getPermanance(), 0.0));
					// logger.info("min "+potentialSynapse.getPermanance());
				}
			}

		}
		ArrayList<Integer> inhibitionRadiuses = new ArrayList<Integer>();
		for (Column column : this.columns) {
			double minimalDutyCycle = (0.01 * (getMaxDutyCycle(column.getNeigbours())));
			column.setMinimalDutyCycle(minimalDutyCycle);
			column.calculateBoost(minimalDutyCycle);

			double overlapDutyCycle = column.updateOverlapDutyCycle();

			if (overlapDutyCycle < minimalDutyCycle) {
				column.increasePermanances(0.1 * connectedPermanance);
				// logger.info("increasePermanances of all synapses of the column");
			}
			for (Synapse synapse : column.getConnectedSynapses(connectedPermanance)) {

				inhibitionRadiuses.add(Math.max(Math.abs(column.getxPos() - synapse.getxPos()), Math.abs(column
						.getyPos()
						- synapse.getyPos())));
			}
		}

		double averageReceptiveFieldSize = 0;

		for (Integer integer : inhibitionRadiuses) {
			averageReceptiveFieldSize += integer;
		}
		averageReceptiveFieldSize = averageReceptiveFieldSize / inhibitionRadiuses.size();
		inhibitionRadiuses = null;

		this.inhibitionRadius = averageReceptiveFieldSize;
		logger.info("new inhib fac=" + this.inhibitionRadius);
	}

	/**
	 * averageReceptiveFieldSize() The radius of the average connected receptive field size of all the columns. The
	 * connected receptive field size of a column includes only the connected synapses (those with permanence values >=
	 * connectedPerm). This is used to determine the extent of lateral inhibition between columns.
	 * 
	 * @return
	 */

	public double getConnectedPermanance() {
		return connectedPermanance;
	}

	private int input(int t, int sourceInput) {

		return sourceInput;
	}

	/**
	 * maxDutyCycle(cols) Returns the maximum active duty cycle of the columns in the given list of columns.
	 * 
	 * @param neighbors
	 * @return
	 */
	private double getMaxDutyCycle(List<Column> neighbors) {

		Column highestNeighbor = null;
		if (neighbors.size() > 0) {
			highestNeighbor = neighbors.get(0);
		}
		for (Column neighbor : neighbors) {
			if (neighbor.getActiveDutyCycle() > highestNeighbor.getActiveDutyCycle()) {
				highestNeighbor = neighbor;
			}
		}
		// logger.log(Level.INFO, "highestActiveDutyCycly="+highestNeighbor.getActiveDutyCycle());
		return highestNeighbor.getActiveDutyCycle();
	}

	/**
	 * kthScore(cols, k) Given the list of columns, return the k'th highest overlap value.
	 * 
	 * @param neighbors
	 * @param disiredLocalActivity
	 * @return
	 */
	private double kthScore(List<Column> neighbors, int disiredLocalActivity) {

		if (disiredLocalActivity > neighbors.size()) {
			disiredLocalActivity = neighbors.size();
		}

		Collections.sort(neighbors);
		double ktScore = neighbors.get(disiredLocalActivity - 1).getOverlap();

		return ktScore;
	}

	private List<Column> getNeigbors(Column column) {

		List<Column> neighbors = new ArrayList<Column>();
		for (Column potentialNeigbor : this.columns) {
			int xposColPlusIn = (int) (column.getxPos() + Math.round(inhibitionRadius));
			int yposColPlusIn = (int) (column.getyPos() + Math.round(inhibitionRadius));
			int xposColMinIn = (int) (column.getxPos() - Math.round(inhibitionRadius));
			int yposColMinIn = (int) (column.getyPos() - Math.round(inhibitionRadius));
			if ((xposColPlusIn >= potentialNeigbor.getxPos()) && (yposColPlusIn >= potentialNeigbor.getyPos())
					&& (xposColMinIn <= potentialNeigbor.getxPos())
					&& (yposColMinIn <= potentialNeigbor.getyPos() && column != potentialNeigbor)) {
				neighbors.add(potentialNeigbor);
			}
		}

		column.setNeigbours(neighbors);
		// logger.info(column.getxPos()+" "+column.getyPos()+"amount of neigbors="+neighbors.size());
		// System.out.println(column.getxPos()+" "+column.getyPos()+"amount of neigbors="+neighbors.size());
		return neighbors;
	}
}
