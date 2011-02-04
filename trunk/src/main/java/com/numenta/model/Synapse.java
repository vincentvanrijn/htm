package com.numenta.model;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Synapse {
	
	private int xPos;
	private int yPos;
	public int getxPos() {
		return xPos;
	}

	public void setxPos(int xPos) {
		this.xPos = xPos;
	}

	public int getyPos() {
		return yPos;
	}

	public void setyPos(int yPos) {
		this.yPos = yPos;
	}

	public static double CONECTED_PERMANANCE=0.7;
	public static double PERMANENCE_DEC=0.05;
	public static double PERMANENCE_INC=0.05;
	//is 0 or 1
	private int sourceInput;
	private double permanance;
	private int inputSpaceIndex;
	private Logger logger=Logger.getLogger(this.getClass().getName());
	

	public boolean isActive() {		
		//logger.log(Level.INFO, "synapse perm ="+this.permanance +" "+(this.permanance>=CONECTED_PERMANANCE)+ "input="+sourceInput);
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
