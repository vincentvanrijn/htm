/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.model;

public class LateralSynapse {

	@Override
	public String toString() {

		return "LateralSynapse " + this.fromColumnIndex + "," + this.fromCellIndex + "," + this.columnIndex + ","
				+ this.getCellIndex() + "," + this.segmentIndex + "," + this.permanance;
	}

	/**
	 * permanenceInc Amount permanence values of synapses are incremented when activity-based learning occurs.
	 */
	public static final double	PERMANANCE_INC		= 0.1;	// TODO choose reasonable value for PERMANANCE_INC

	/**
	 * permanenceDec Amount permanence values of synapses are decremented when activity-based learning occurs.
	 */
	public static final double	PERMANANCE_DEC		= 0.2;	// TODO choose reasonable value for PERMANANCE_DEC

	/**
	 * initialPerm Initial permanence value for a synapse.
	 */
	public static final double	INITIAL_PERM		= 0.5;

	/**
	 * connectedPerm If the permanence value for a synapse is greater than this value, it is said to be connected.
	 */
	public static double		connectedPermanance	= 0.5;

	private int					synapseIndex;

	private int					fromColumnIndex;

	private int					fromCellIndex;

	public LateralSynapse(int c, int i, int s, int y, int fromColumnIndex, int fromCellIndex, double initialPerm) {
		this.columnIndex = c;
		this.cellIndex = i;
		this.segmentIndex = s;
		this.synapseIndex = y;
		this.fromColumnIndex = fromColumnIndex;
		this.fromCellIndex = fromCellIndex;
		this.permanance = initialPerm;
	}

	public int getFromColumnIndex() {
		return fromColumnIndex;
	}

	public int getFromCellIndex() {
		return fromCellIndex;
	}

	public LateralSynapse() {}

	

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

	public boolean isConnected() {
		// logger.log(Level.INFO, "synapse perm ="+this.permanance +" "+(this.permanance>=CONECTED_PERMANANCE)+
		// "input="+sourceInput);
		return this.permanance >= LateralSynapse.connectedPermanance;
	}

	public static double getConnectedPermanance() {
		return connectedPermanance;
	}

}
