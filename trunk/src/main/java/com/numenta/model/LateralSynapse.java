package com.numenta.model;


public class LateralSynapse {
	public static final double PERMANANCE_INC = 0;//TODO choose reasonable value for PERMANANCE_INC

	public static final double PERMANANCE_DEC = 0;//TODO choose reasonable value for PERMANANCE_DEC

	public static final double INITIAL_PERM = 0;

	private static double connectedPermanance;

	private int synapseIndex;

	public LateralSynapse(int c, int i, int s, int y) {
		this.columnIndex=c;
		this.cellIndex=i;
		this.segmentIndex=s;
		this.synapseIndex=y;
	}
	public LateralSynapse() {
	}
	public int getSynapseIndex() {
		return synapseIndex;
	}
	public static void setConnectedPermanance(int connectedPermanance){
		LateralSynapse.connectedPermanance=connectedPermanance;
	}
	private double permanance;
	
	private int columnIndex ;
	private int cellIndex;
	private int segmentIndex;
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
		//logger.log(Level.INFO, "synapse perm ="+this.permanance +" "+(this.permanance>=CONECTED_PERMANANCE)+ "input="+sourceInput);
		return this.permanance>=LateralSynapse.connectedPermanance;
	}
}