package com.numenta.model;

import java.util.ArrayList;

public class Cell {
	

	private ArrayList<Boolean> predictiveStatesBefore=new ArrayList<Boolean>();
	private ArrayList<Boolean> activeStateNow=new ArrayList<Boolean>();
	
	
	private int columnIndex;
	private int cellIndex;
	
	
	private Segment[] segments;
	
	private int[] segmentUpdateList;

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

	

	public Segment[] getSegments() {
		return segments;
	}

	public void setSegments(Segment[] segments) {
		this.segments = segments;
	}

	public Segment getActiveSegment(int j,  String activeState) {
		Segment returnValue=null;
		if(predictiveStatesBefore.get(j)){
			returnValue= this.segments[j];
		}
		return returnValue;
	}

	public boolean segmentActiveNow(int k) {
		return activeStateNow.get(k);
			
	}
}
