package com.numenta.model;


public class LateralSynapse {
	private double permanance;
	
	private int columnIndex ;
	private int cellIndex;
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
	public boolean isActive(double connectedPermanance) {		
		//logger.log(Level.INFO, "synapse perm ="+this.permanance +" "+(this.permanance>=CONECTED_PERMANANCE)+ "input="+sourceInput);
		return this.permanance>=connectedPermanance;
	}
}