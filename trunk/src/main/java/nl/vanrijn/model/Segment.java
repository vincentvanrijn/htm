/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.model;

import java.util.ArrayList;
import java.util.List;

public class Segment implements Comparable<Segment> {

	public static final boolean		GETS_NO_NEW_SYNAPSE	= false;

	public static final boolean		GETS_NEW_SYNAPSE	= true;

	private List<LateralSynapse>	synapses;

	private boolean					sequenceSegment;
	
	private int cellIndex;
	private int						segmentIndex;

	private int columnIndex;

	public Segment(int c, int i, int s, List<LateralSynapse> synapses) {
		this.columnIndex=c;
		this.cellIndex=i;
		this.segmentIndex=s;
		this.synapses=synapses;
	}

	public int getCellIndex() {
		return cellIndex;
	}

	



	public int getSegmentIndex() {
		return segmentIndex;
	}

	

	public int getColumnIndex() {
		return columnIndex;
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
