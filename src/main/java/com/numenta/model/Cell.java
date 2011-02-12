package com.numenta.model;

import java.util.ArrayList;
import java.util.List;

import com.numenta.model.helper.SegmentUpdate;

public class Cell {
	

	private List<SegmentUpdate> segmentUpdateList;

	
	
	private int columnIndex;
	private int cellIndex;
	
	
	private ArrayList<Segment> segments;
	
	
	private boolean learnState;

	

	public List<SegmentUpdate> getSegmentUpdateList() {
		return segmentUpdateList;
	}

	public void setSegmentUpdateList(List<SegmentUpdate> segmentUpdateList) {
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
		return returnValue;
	}


	public void setLearnState(boolean learnState) {
		this.learnState=learnState;
		
	}

	public boolean isLearnState() {
		return this.learnState;
	}
}
