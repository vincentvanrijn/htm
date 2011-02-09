package com.numenta.model;

public class Segment {

	private int[] permananceValueBySynapseNumber;
	private Synapse[] synapses;
	private boolean sequenceSegment;
	public Synapse[] getSynapses() {
		return synapses;
	}
	public void setSynapses(Synapse[] synapses) {
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
