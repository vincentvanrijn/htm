/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.model;

import nl.vanrijn.pooler.TemporalPooler;

public class LateralSynapse {

	public void setFromColumnIndex(int fromColumnIndex) {
		this.fromColumnIndex = fromColumnIndex;
	}

	public void setFromCellIndex(int fromCellIndex) {
		this.fromCellIndex = fromCellIndex;
	}

	@Override
	public String toString() {

		return "LateralSynapse from " + this.fromColumnIndex + "," + this.fromCellIndex + ", on " + this.columnIndex
				+ "," + this.getCellIndex() + "," + this.segmentIndex + ",perm " + this.permanance;
	}

	private int	fromColumnIndex;

	private int	fromCellIndex;

	public LateralSynapse(int c, int i, int s, int fromColumnIndex, int fromCellIndex, double initialPerm) {
		this.columnIndex = c;
		this.cellIndex = i;
		this.segmentIndex = s;
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
		return this.permanance >= TemporalPooler.CONNECTED_PERMANANCE;
	}

}
