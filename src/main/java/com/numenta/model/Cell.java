package com.numenta.model;

import java.util.ArrayList;
import java.util.List;

import com.numenta.model.helper.SegmentUpdate;

public class Cell {

	public static final boolean	ACTIVE_STATE	= false;

	public static final boolean	LEARN_STATE		= true;

	public static final int		NOW				= 1;

	public static final int		BEFORE			= 0;

	/**
	 * segmentUpdateList A list of segmentUpdate structures. segmentUpdateList(c,i) is the list of changes for cell i in
	 * column c.
	 */
	private List<SegmentUpdate>	segmentUpdateList;

	private int					columnIndex;

	private int					cellIndex;

	private int					time;

	/**
	 * predictiveState(c, i, t) A boolean vector with one number per cell. It represents the prediction of the column c
	 * cell i at time t, given the bottom-up activity of other columns and the past temporal context. predictiveState(c,
	 * i, t) is the contribution of column c cell i at time t. If 1, the cell is predicting feed-forward input in the
	 * current temporal context.
	 */

	private boolean				predictiveState;

	/**
	 * learnState(c, i, t) A boolean indicating whether cell i in column c is chosen as the cell to learn on.
	 */

	private boolean				learnState;

	/**
	 * activeState(c, i, t) A boolean vector with one number per cell. It represents the active state of the column c
	 * cell i at time t given the current feed-forward input and the past temporal context. activeState(c, i, t) is the
	 * contribution from column c cell i at time t. If 1, the cell has current feed-forward input as well as an
	 * appropriate temporal context.
	 */
	private boolean				activeState;

	public boolean hasPredictiveState() {
		return predictiveState;
	}

	public void setPredictiveState(boolean predictiveState) {
		this.predictiveState = predictiveState;
	}

	private List<Segment>	segments;

	public boolean hasActiveState() {
		return activeState;
	}

	public Cell(int columnIndex, int cellIndex, int time) {
		this.columnIndex = columnIndex;
		this.cellIndex = cellIndex;
		this.time = time;
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

	public List<Segment> getSegments() {
		return segments;
	}

	public void setLearnState(boolean learnState) {
		this.learnState = learnState;

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
		this.activeState = activeState;

	}

	public void setSegments(List<Segment> segments) {
		this.segments=segments;
		
	}

}
