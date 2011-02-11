package com.numenta.model;

import java.util.ArrayList;

public class Cell {
	

	private ArrayList<Boolean> predictiveStatesBefore=new ArrayList<Boolean>();
	public ArrayList<Boolean> getPredictiveStatesBefore() {
		return predictiveStatesBefore;
	}

	private ArrayList<Boolean> predictiveStatesNow=new ArrayList<Boolean>();
	public ArrayList<Boolean> getPredictiveStatesNow() {
		return predictiveStatesNow;
	}

	private ArrayList<Boolean> activeStateNow=new ArrayList<Boolean>();
	
	
	private int columnIndex;
	private int cellIndex;
	
	
	private ArrayList<Segment> segments;
	
	private int[] segmentUpdateList;
	private boolean learnState;

	public int[] getSegmentUpdateList() {
		return segmentUpdateList;
	}

	public void setSegmentUpdateList(int[] segmentUpdateList) {
		this.segmentUpdateList = segmentUpdateList;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public int getCellIndex() {
		return cellIndex;
	}

	public void setCellIndex(int cellIndex) {
		this.cellIndex = cellIndex;
	}

	

	public ArrayList<Segment> getSegments() {
		return segments;
	}

	

	public Segment getActiveSegment(int j,  String activeState) {
		Segment returnValue=null;
		if(predictiveStatesBefore.get(j)){
			returnValue= this.segments.get(j);
		}
		return returnValue;
	}

	public boolean segmentActiveNow(int k) {
		return activeStateNow.get(k);
			
	}

	public void setLearnState(boolean learnState) {
		this.learnState=learnState;
		
	}

	public boolean isLearnState() {
		return this.learnState;
	}
}
