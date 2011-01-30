package com.numenta.model;

public class Segment {

	private int[] permananceValueBySynapseNumber;
	private Synapse[] synapses;
	public Synapse[] getSynapses() {
		return synapses;
	}
	public void setSynapses(Synapse[] synapses) {
		this.synapses = synapses;
	}
	public boolean sequenceSegment() {
		// TODO Auto-generated method stub
		return false;
	}

}
