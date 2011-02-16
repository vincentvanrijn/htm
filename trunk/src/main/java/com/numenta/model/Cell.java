package com.numenta.model;

import java.util.ArrayList;
import java.util.List;

import com.numenta.model.helper.SegmentUpdate;

public class Cell {
	

	public static final boolean ACTIVE_STATE = false;
	public static final boolean LEARN_STATE = true;
	public static final int NOW=1;
	public static final int BEFORE=0;
	
	


	
	private List<SegmentUpdate> segmentUpdateList;

	
	
	private int columnIndex;
	private int cellIndex;
	private int time;
	private boolean predictiveState;
	private boolean learnState;
	private boolean activeState;


	


	public boolean hasPredictiveState() {
		return predictiveState;
	}


	public void setPredictiveState(boolean predictiveState) {
		this.predictiveState = predictiveState;
	}

	private ArrayList<Segment> segments;
	
	
	public boolean hasActiveState() {
		return activeState;
	}

	

	public Cell(int columnIndex, int cellIndex, int time) {
		this.columnIndex=columnIndex;
		this.cellIndex=cellIndex;
		this.time=time;
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

	


	public void setLearnState(boolean learnState) {
		this.learnState=learnState;
		
	}

	public boolean hasLearnState() {
		return this.learnState;
	}
	
	

public List<SegmentUpdate> getSegmentUpdateList() {
	return segmentUpdateList;
}

public void setSegmentUpdateList(List<SegmentUpdate> segmentUpdateList) {
	this.segmentUpdateList = segmentUpdateList;
}


public void seActiveState(boolean activeState) {
	this.activeState=activeState;
	
}

}
