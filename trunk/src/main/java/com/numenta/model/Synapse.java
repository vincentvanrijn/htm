package com.numenta.model;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Synapse {
	
	public static double CONECTED_PERMANANCE=5;
	public static double PERMANENCE_DEC=0.1;
	public static double PERMANENCE_INC=0.1;
	//is 0 or 1
	private int sourceInput;
	private double permanance;
	private int inputSpaceIndex;
	private Logger logger=Logger.getLogger(this.getClass().getName());
	

	public boolean isActive() {		
		logger.log(Level.INFO, "synapse perm ="+this.permanance +" "+(this.permanance>=CONECTED_PERMANANCE)+ "input="+sourceInput);
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

	

	public int getInputSpaceIndex() {
		return inputSpaceIndex;
	}

	public void setInputSpaceIndex(int inputSpaceIndex) {
		this.inputSpaceIndex = inputSpaceIndex;
	}

}
