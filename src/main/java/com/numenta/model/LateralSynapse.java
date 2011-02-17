package com.numenta.model;

public class LateralSynapse {

	/**
	 * permanenceInc Amount permanence values of synapses are incremented when activity-based learning occurs.
	 */
	public static final double	PERMANANCE_INC	= 0;	// TODO choose reasonable value for PERMANANCE_INC

	/**
	 * permanenceDec Amount permanence values of synapses are decremented when activity-based learning occurs.
	 */
	public static final double	PERMANANCE_DEC	= 0;	// TODO choose reasonable value for PERMANANCE_DEC

	/**
	 * initialPerm Initial permanence value for a synapse.
	 */
	public static final double	INITIAL_PERM	= 0;

	/**
	 * connectedPerm If the permanence value for a synapse is greater than this value, it is said to be connected.
	 */
	private static double		connectedPermanance;

	private int					synapseIndex;

	public LateralSynapse(int c, int i, int s, int y) {
		this.columnIndex = c;
		this.cellIndex = i;
		this.segmentIndex = s;
		this.synapseIndex = y;
	}

	public LateralSynapse() {}

	public int getSynapseIndex() {
		return synapseIndex;
	}

	public static void setConnectedPermanance(int connectedPermanance) {
		LateralSynapse.connectedPermanance = connectedPermanance;
	}

	private double	permanance;

	private int		columnIndex;

	private int		cellIndex;

	private int		segmentIndex;

	public int getSegmentIndex() {
		return segmentIndex;
	}

	public void setSegmentIndex(int segmentIndex) {
		this.segmentIndex = segmentIndex;
	}

	public double getPermanance() {
		return permanance;
	}

	public void setPermanance(double permanance) {
		this.permanance = permanance;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public int getCellIndex() {
		return cellIndex;
	}

	public void setCellIndex(int cellIndex) {
		this.cellIndex = cellIndex;
	}

	public boolean isActive() {
		// logger.log(Level.INFO, "synapse perm ="+this.permanance +" "+(this.permanance>=CONECTED_PERMANANCE)+
		// "input="+sourceInput);
		return this.permanance >= LateralSynapse.connectedPermanance;
	}
}
