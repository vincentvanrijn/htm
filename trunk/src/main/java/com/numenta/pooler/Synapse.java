package com.numenta.pooler;

public class Synapse {
	
	public static int CONECTED_PERMANANCE=5;
	//is 0 or 1
	private int sourceInput;
	private int permanance;

	

	public boolean isActive() {
		
		return this.permanance>=CONECTED_PERMANANCE;
	}

	public int getSourceInput() {
		return sourceInput;
	}

	public void setSourceInput(int sourceInput) {
		this.sourceInput = sourceInput;
	}

	

	public int getPermanance() {
		return this.permanance;
	}

	public void setPermanance(int permanance) {
		this.permanance = permanance;
	}

}
