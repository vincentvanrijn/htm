package com.numenta.model;

public class Synapse {
	
	public static int CONECTED_PERMANANCE=5;
	//is 0 or 1
	private int sourceInput;
	private double permanance;

	

	public boolean isActive() {
		
		return this.permanance>=CONECTED_PERMANANCE;
	}

	public int getSourceInput() {
		return sourceInput;
	}

	public void setSourceInput(int sourceInput) {
		this.sourceInput = sourceInput;
	}

	

	public double getPermanance() {
		return this.permanance;
	}

	public void setPermanance(double d) {
		this.permanance = d;
	}

}
