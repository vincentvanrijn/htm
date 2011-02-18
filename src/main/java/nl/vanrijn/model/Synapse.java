/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.model;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * synapse A data structure representing a synapse - contains a permanence value and the source input index.
 * 
 * @author vanrijn
 */
public class Synapse {

	private int		sourceInput;

	private double	permanance;

	private int		inputSpaceIndex;

	private Logger	logger	= Logger.getLogger(this.getClass().getName());

	private int		xPos;

	private int		yPos;

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

	public boolean isActive(double connectedPermanance) {
		// logger.log(Level.INFO, "synapse perm ="+this.permanance +" "+(this.permanance>=CONECTED_PERMANANCE)+
		// "input="+sourceInput);
		return this.permanance >= connectedPermanance;
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
