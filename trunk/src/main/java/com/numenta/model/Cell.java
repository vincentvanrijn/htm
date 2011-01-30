package com.numenta.model;

public class Cell {
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

}
