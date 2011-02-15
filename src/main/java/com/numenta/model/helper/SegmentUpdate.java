package com.numenta.model.helper;

import java.util.List;

import com.numenta.model.Synapse;

public class SegmentUpdate {

	private boolean	sequenceSegment;

	private int		segmentUpdateIndex;

	private int		cellIndex;

	public int getCellIndex() {
		return cellIndex;
	}

	public void setCellIndex(int cellIndex) {
		this.cellIndex = cellIndex;
	}

	private List<Synapse>	activeSynapses;

	public static boolean	POSITIVE_REINFORCEMENT		= true;

	public static boolean	NO_POSITIVE_REINFORCEMENT	= false;

	public int getSegmentUpdateIndex() {
		return segmentUpdateIndex;
	}

	public void setSegmentUpdateIndex(int segmentUpdateIndex) {
		this.segmentUpdateIndex = segmentUpdateIndex;
	}

	public List<Synapse> getActiveSynapses() {
		return activeSynapses;
	}

	public void setActiveSynapses(List<Synapse> activeSynapses) {
		this.activeSynapses = activeSynapses;
	}

	public boolean isSequenceSegment() {
		return sequenceSegment;
	}

	public void setSequenceSegment(boolean sequenceSegment) {
		this.sequenceSegment = sequenceSegment;

	}

}
