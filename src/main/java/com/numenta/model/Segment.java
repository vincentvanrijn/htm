package com.numenta.model;

import java.util.ArrayList;
import java.util.List;

public class Segment implements Comparable<Segment> {

	public static final boolean		GETS_NO_NEW_SYNAPSE	= false;

	public static final boolean		GETS_NEW_SYNAPSE	= true;

	private List<LateralSynapse>	synapses;

	private boolean					sequenceSegment;
	
	private int cellIndex;

	public int getCellIndex() {
		return cellIndex;
	}

	public void setCellIndex(int cellIndex) {
		this.cellIndex = cellIndex;
	}

	private int						SegmentIndex;

	public int getSegmentIndex() {
		return SegmentIndex;
	}

	public void setSegmentIndex(int segmentIndex) {
		SegmentIndex = segmentIndex;
	}

	public List<LateralSynapse> getSynapses() {
		return synapses;
	}
	public List<LateralSynapse> getConnectedSynapses(){
		List<LateralSynapse> connectedSynapses=new ArrayList<LateralSynapse>();
		for (LateralSynapse synapse : synapses){
			if( synapse.isActive()){
				connectedSynapses.add(synapse);
			}
		}
		return connectedSynapses;
	}

	public void setSynapses(List<LateralSynapse> synapses) {
		this.synapses = synapses;
	}

	public boolean isSsequenceSegment() {
		return this.sequenceSegment;
	}

	public void setSequenceSegment(boolean sequenceSegment) {
		this.sequenceSegment = sequenceSegment;
	}

	public int compareTo(Segment segment) {
		//1 sequence most activity
		//2 sequence and active
		//3 most activity
		//4 least activity
		int returnValue = 0;
		//
		if(this.isSsequenceSegment()==segment.isSsequenceSegment()
				&& this.getConnectedSynapses().size()==segment.getConnectedSynapses().size()){
			returnValue=0;
		} else if((this.isSsequenceSegment() && !segment.isSsequenceSegment()) ||
				(this.isSsequenceSegment()==segment.isSsequenceSegment() &&
						this.getConnectedSynapses().size()>segment.getConnectedSynapses().size())
				){
			returnValue=1;
		} else {
			returnValue=-1;
		}
		return returnValue;
	}

}
