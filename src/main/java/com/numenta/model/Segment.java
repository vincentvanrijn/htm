package com.numenta.model;

import java.util.List;

public class Segment {

	private int[] permananceValueBySynapseNumber;
	private List<Synapse> synapses;
	private boolean sequenceSegment;
	public List<Synapse> getSynapses() {
		return synapses;
	}
	public void setSynapses(List <Synapse> synapses) {
		this.synapses = synapses;
	}
	public boolean isSsequenceSegment() {
		// TODO Auto-generated method stub
		return this.sequenceSegment;
	}
	public void setSequenceSegment(boolean sequenceSegment) {
		this.sequenceSegment=sequenceSegment;
	}

}
