package com.numenta.model;

import java.util.List;

public class Segment implements Comparable<Segment> {

	public static final boolean GETS_NO_NEW_SYNAPSE = false;
	public static final boolean GETS_NEW_SYNAPSE = true;
	private List<LateralSynapse> synapses;
	private boolean sequenceSegment;
	public List<LateralSynapse> getSynapses() {
		return synapses;
	}
	public void setSynapses(List <LateralSynapse> synapses) {
		this.synapses = synapses;
	}
	public boolean isSsequenceSegment() {
	return this.sequenceSegment;
	}
	public void setSequenceSegment(boolean sequenceSegment) {
		this.sequenceSegment=sequenceSegment;
	}
	
	public int compareTo(Segment segment) {
		int returnValue = 0;
		if (this.getSynapses().size() > segment.getSynapses().size() ||
			(this.getSynapses().size() == segment.getSynapses().size()&&this.isSsequenceSegment())	) {
			returnValue = -1;
		} else {
			if (this.getSynapses().size() < segment.getSynapses().size()||
					(this.getSynapses().size() == segment.getSynapses().size()&&segment.isSsequenceSegment())	) {
				returnValue = 0;
			} else {
				if (this.getSynapses().size() == segment.getSynapses().size()) {
					returnValue = 1;
				}
			}
		}
		return returnValue;
	}


}
