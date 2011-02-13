package com.numenta.pooler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.numenta.model.Cell;
import com.numenta.model.Column;
import com.numenta.model.LateralSynapse;
import com.numenta.model.Segment;
import com.numenta.model.Synapse;
import com.numenta.model.helper.SegmentUpdate;

public class TemporalPooler {

	private static final int CONNECTED_PERM = 0;
	private static final int MIN_TRESHOLD = 0;
	private static int ACTIVATION_TRESHOLD=0;
	private static int AMMOUNT_TIME=2;
	private Column[] activeColumns;
	private Cell[][][] cells = new Cell[SpatialPooler.AMMOUNT_OF_COLLUMNS][Column.CELLS_PER_COLUMN][TemporalPooler.AMMOUNT_TIME];
	

	public void init() {
		for (int i = 0; i < SpatialPooler.AMMOUNT_OF_COLLUMNS; i++){
			for (int j = 0; j < Column.CELLS_PER_COLUMN; j++) {
				for(int t=0;t<TemporalPooler.AMMOUNT_TIME;t++){

					cells[i][j][t] = new Cell(i, j, 1);
				}
			}
		}
	}

	public void computeActiveState() {
		for (int c = 0; c < activeColumns.length; c++) {
			boolean buPredicted = false;
			boolean lcChosen = false;
			for (int i = 0; i < Column.CELLS_PER_COLUMN ; i++) {

				if (cells[c][i][Cell.BEFORE].hasPredictiveState()) {
					System.out.println("predicted before ");
					Segment segment = getActiveSegment(c, i, Cell.BEFORE,
							Cell.ACTIVE_STATE);
					if (segment.isSsequenceSegment()) {
						buPredicted = true;
						cells[c][i][Cell.NOW].seActiveState(true);
						if (segmentActive(segment, Cell.BEFORE, Cell.LEARN_STATE)) {
							lcChosen = true;
							cells[c][i][Cell.NOW].setLearnState(true);
						}
					}
				}
			}
			if (!buPredicted) {
				for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {
					cells[c][i][Cell.NOW].seActiveState(true);
				}

				System.out.println("all cells active");
			}
			if (!lcChosen) {
				// TODO implement getBestMatchingCell
				Cell cell = getBestMatchingCell(c, Cell.BEFORE);
				Segment segment = getBestMatchingSegment(c,cell.getCellIndex(), Cell.BEFORE);
				
				cells[c][cell.getCellIndex()][Cell.NOW].setLearnState(true);
				// TODO implementgetSegmentActiveSynapses
				SegmentUpdate sUpdate = getSegmentActiveSynapses(c, cell
						.getCellIndex(), segment, 0, true);
				sUpdate.setSequenceSegment(true);
				cell.getSegmentUpdateList().add(sUpdate);
			}
		}
	}

	

	public void calculatePredictedState() {
		for (int c = 0; c < SpatialPooler.AMMOUNT_OF_COLLUMNS; c++)
			for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {

				Cell cell = cells[c][i][Cell.NOW];

				for (int s = 0; s < cell.getSegments().size(); s++) {
					Segment segment = cell.getSegments().get(s);
					// TODO implement segmentActive
					if (segmentActive(segment, Cell.NOW, Cell.ACTIVE_STATE)) {
						cells[c][i][Cell.NOW].setPredictiveState(true);
						// TODO implement getSegmentActiveSynapses
						SegmentUpdate activeUpdate = getSegmentActiveSynapses(
								c, i, segment, Cell.NOW, Segment.GETS_NO_NEW_SYNAPSE);
						cell.getSegmentUpdateList().add(activeUpdate);
						// TODO implement getBestMatchingSegment

						Segment predSegment = getBestMatchingSegment(c, i , Cell.BEFORE);
						// TODO implement getSegmentActiveSynapses
						SegmentUpdate predUpdate = getSegmentActiveSynapses(c,
								i, predSegment, Cell.BEFORE, Segment.GETS_NEW_SYNAPSE);

						cell.getSegmentUpdateList().add(predUpdate);
					}

				}
			}
	}

	public void updateSynapses() {

		for (int c = 0; c < SpatialPooler.AMMOUNT_OF_COLLUMNS; c++)
			for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {

				Cell cell = cells[c][i][Cell.NOW];

				if (cells[c][i][1].hasLearnState()) {
					// TODO implement adaptSegments
					adaptSegments(cell.getSegmentUpdateList(), SegmentUpdate.POSITIVE_REINFORCEMENT);
					cell.setSegmentUpdateList(null);

				} else if (!cells[c][i][Cell.NOW].hasPredictiveState()
						&& cells[c][i][Cell.BEFORE].hasPredictiveState()) {

					// TODO implement adaptSegments
					adaptSegments(cell.getSegmentUpdateList(), SegmentUpdate.NO_POSITIVE_REINFORCEMENT);
					cell.setSegmentUpdateList(null);

				}
			}

	}
	
	/**
	 * getActiveSegment(c, i, t, state) For the given column c cell i, return a
	 * segment index such that segmentActive(s,t, state) is true. If multiple
	 * segments are active, sequence segments are given preference. Otherwise,
	 * segments with most activity are given preference.
	 * 
	 * @param c
	 * @param i
	 * @param time
	 * @param state
	 * @return
	 */
	private Segment getActiveSegment(int c, int i, int time, boolean state) {

		Cell cell = cells[c][i][time];
		List<Segment> activeSegments=new ArrayList<Segment>();
		List<Segment> segments = cell.getSegments();
		for (Segment segment : segments) {
			if(segmentActive(segment, time, state)){
				activeSegments.add(segment);
			}
		}
		Collections.sort(segments);		
		return segments.get(0);
	}
/**
 * adaptSegments(segmentList, positiveReinforcement)
This function iterates through a list of segmentUpdate's and reinforces each segment. 
For each segmentUpdate element, the following changes are performed. 
If positiveReinforcement is true then synapses on the active list get their permanence counts incremented 
by permanenceInc. All other synapses get their permanence counts decremented by permanenceDec. 
If positiveReinforcement is false, 
then synapses on the active list get their permanence counts decremented by permanenceDec. 
After this step, 
any synapses in segmentUpdate that do yet exist get added with a permanence count of initialPerm.
 * @param segmentUpdateList2
 * @param b
 */
	private void adaptSegments(List<SegmentUpdate> segmentUpdateList2, boolean b) {
		// TODO Auto-generated method stub

	}
/**
 * getBestMatchingCell(c)
For the given column, return the cell with the best matching segment (as defined above). 
If no cell has a matching segment, then return the cell with the fewest number of segments.
 * @param c
 * @param time
 * @return
 */
	private Cell getBestMatchingCell(int c, int time) {
		
		Cell[] cellsToCompare=new Cell[Column.CELLS_PER_COLUMN];
		for (int j = 0; j < Column.CELLS_PER_COLUMN - 1; j++) {
			cellsToCompare[j]=this.cells[c][j][time];
		}
		Arrays.sort(cellsToCompare, new Comparator<Cell>() {

			public int compare(Cell cell, Cell toCampare) {
				return 0;
			}
		});
			
		return cellsToCompare[0];
	}

	

	/**
	 * getSegmentActiveSynapses(c, i, t, s, newSynapses= false)
Return a segmentUpdate data structure containing a list of proposed changes to segment s. 
Let activeSynapses be the list of active synapses where the originating cells 
have their activeState output = 1 at time step t. 
(This list is empty if s = -1 since the segment doesn't exist.)
 newSynapses is an optional argument that defaults to false. 
 If newSynapses is true, then newSynapseCount - count(activeSynapses) synapses are added to activeSynapses. 
These synapses are randomly chosen from the set of cells that have learnState output = 1 at time step t.
	 * @param c
	 * @param i
	 * @param segment
	 * @param t
	 * @param b
	 * @return
	 */
	private SegmentUpdate getSegmentActiveSynapses(int c, int i,
			Segment segment, int t, boolean b) {
		// TODO Auto-generated method stub
		return null;
	}
/**
 * getBestMatchingSegment(c, i, t)
For the given column c cell i at time t, find the segment with the largest number of active synapses. 
This routine is aggressive in finding the best match. 
The permanence value of synapses is allowed to be below connectedPerm. 
The number of active synapses is allowed to be below activationThreshold, but must be above minThreshold. 
The routine returns the segment index. If no segments are found, then an index of -1 is returned.
 * @param cell
 * @return
 */
	private Segment getBestMatchingSegment(int c, int i,int time) {
		Cell cell=cells[c][i][time];
		List<Segment> segments =cell.getSegments();
		Collections.sort(segments);
		Segment returnValue=null;
		if(segments!=null){
			if(segments.get(0).getSynapses().size()>TemporalPooler.MIN_TRESHOLD){
				returnValue=	segments.get(0);
			}
		}
		return returnValue;
	}

	/**
	 * segmentActive(s, t, state) This routine returns true if the number of
	 * connected synapses on segment s that are active due to the given state at
	 * time t is greater than activationThreshold. The parameter state can be
	 * activeState, or learnState.
	 * 
	 * @param segment
	 * @param time
	 *            can be 1 meaning now or 0 meaning t-1
	 * @param learnState
	 * @return
	 */
	private boolean segmentActive(Segment segment, int time, boolean learnState) {
			List<LateralSynapse> synapses = segment.getSynapses();
		int ammountConnected = 0;
		for (Iterator<LateralSynapse> iterator = synapses.iterator(); iterator
				.hasNext();) {
			LateralSynapse synapse = (LateralSynapse) iterator.next();
			if (synapse.isActive(TemporalPooler.CONNECTED_PERM) && cells[synapse.getColumnIndex()][synapse.getCellIndex()][time].hasLearnState()==learnState) {
				ammountConnected++;
			}
		}
		return ammountConnected > TemporalPooler.ACTIVATION_TRESHOLD;

	}
	
	public void setActiveColumns(ArrayList<Column> activeColumns) {

		Object[] objects = activeColumns.toArray();
		Column[] actives = new Column[objects.length];
		System.arraycopy(objects, 0, actives, 0, objects.length);
		this.activeColumns = actives;
	}
}
