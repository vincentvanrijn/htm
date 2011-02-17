package com.numenta.pooler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import com.numenta.model.Cell;
import com.numenta.model.Column;
import com.numenta.model.LateralSynapse;
import com.numenta.model.Segment;
import com.numenta.model.helper.SegmentUpdate;

public class TemporalPooler {

	/**
	 * minThreshold Minimum segment activity for learning.
	 */
	private static final int	MIN_TRESHOLD		= 0;																									// TODO

	// TODO choose value
	/**
	 * activationThreshold Activation threshold for a segment. If the number of active connected synapses in a segment
	 * is greater than activationThreshold, the segment is said to be active.
	 */
	private static int			ACTIVATION_TRESHOLD	= 0;																									// TODO

	// TODO choose value

	private static int			AMMOUNT_TIME		= 2;

	private static int			AMMOUNT_OF_SEGMENTS	= 10;																									// TODO

	// TODO choose value
	private static int			AMMOUNT_OF_SYNAPSES	= 10;																									// TODO

	// TODO choose value
	/**
	 * newSynapseCount The maximum number of synapses added to a segment during learning.
	 */
	private static int			NEW_SYNAPSE_COUNT;																											// TODO

	// TODO choose value

	/**
	 * learningRadius The area around a temporal pooler cell from which it can get lateral connections.
	 */
	private static int			LEARNING_RADIUS;																											// TODO

	// TODO choose value

	/**
	 * activeColumns(t) List of column indices that are winners due to bottom-up input (this is the output of the
	 * spatial pooler).
	 */
	private Column[]			activeColumns;

	/**
	 * cell(c,i) A list of all cells, indexed by i and c.
	 */

	private Cell[][][]			cells				= new Cell[SpatialPooler.AMMOUNT_OF_COLLUMNS][Column.CELLS_PER_COLUMN][TemporalPooler.AMMOUNT_TIME];

	public void init() {
		// TODO choose a reasonable connectedpermanance

		ArrayList<Integer> collumnIndexes = new ArrayList<Integer>();
		for (int c = 0; c < SpatialPooler.AMMOUNT_OF_COLLUMNS; c++) {
			collumnIndexes.add(c);

		}
		Random random = new Random();
		LateralSynapse.setConnectedPermanance(1);
		for (int c = 0; c < SpatialPooler.AMMOUNT_OF_COLLUMNS; c++) {
			for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {
				for (int t = 0; t < TemporalPooler.AMMOUNT_TIME; t++) {

					cells[c][i][t] = new Cell(c, i, t);
					List<Segment> segments = new ArrayList<Segment>();
					for (int s = 0; s < AMMOUNT_OF_SEGMENTS; s++) {
						List<LateralSynapse> synapses = new ArrayList<LateralSynapse>();
						Collections.shuffle(collumnIndexes);
						for (int y = 0; y < AMMOUNT_OF_SYNAPSES; y++) {

							// TODO permanance
							// TODO connect to cells

							// Get all cells in the area of this cells'Learning
							// radius
							LateralSynapse synapse = new LateralSynapse(c, i,
									s, y, collumnIndexes.get(y), random
											.nextInt(3));
							synapses.add(synapse);
							// System.out.println(c+","+i+","+s+","+y+","+synapse.getFromColumnIndex()+","+synapse.getFromCellIndex());
						}

						segments.add(new Segment(c, i, s, synapses));
					}
					cells[c][i][t].setSegments(segments);
				}
			}
		}
	}

	// private List<Cell> getNeighbors(){
	// List<Column> neighbors = new ArrayList<Column>();
	// for (Column potentialNeigbor : this.columns) {
	// int xposColPlusIn = (int) (column.getxPos() + Math.round(inhibitionRadius));
	// int yposColPlusIn = (int) (column.getyPos() + Math.round(inhibitionRadius));
	// int xposColMinIn = (int) (column.getxPos() - Math.round(inhibitionRadius));
	// int yposColMinIn = (int) (column.getyPos() - Math.round(inhibitionRadius));
	// if ((xposColPlusIn >= potentialNeigbor.getxPos()) && (yposColPlusIn >= potentialNeigbor.getyPos())
	// && (xposColMinIn <= potentialNeigbor.getxPos())
	// && (yposColMinIn <= potentialNeigbor.getyPos() && column != potentialNeigbor)) {
	// neighbors.add(potentialNeigbor);
	// }
	// }
	//		
	// }

	/**
	 * Phase 1 The first phase calculates the activeState for each cell that is in a winning column. For those columns,
	 * the code further selects one cell per column as the learning cell (learnState). The logic is as follows: if the
	 * bottom-up input was predicted by any cell (i.e. its predictiveState output was 1 due to a sequence segment), then
	 * those cells become active (lines 23-27). If that segment became active from cells chosen with learnState on, this
	 * cell is selected as the learning cell (lines 28-30). If the bottom-up input was not predicted, then all cells in
	 * the become active (lines 32-34). In addition, the best matching cell is chosen as the learning cell (lines 36-41)
	 * and a new segment is added to that cell.
	 */
	public void computeActiveState() {
		for (int c = 0; c < activeColumns.length; c++) {
			boolean buPredicted = false;
			boolean lcChosen = false;
			for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {

				if (cells[c][i][Cell.BEFORE].hasPredictiveState()) {
					// System.out.println("predicted before ");
					Segment segment = getActiveSegment(c, i, Cell.BEFORE, Cell.ACTIVE_STATE);
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

				Cell cell = getBestMatchingCell(c, Cell.BEFORE);
				Segment segment = getBestMatchingSegment(c, cell.getCellIndex(), Cell.BEFORE);

				cells[c][cell.getCellIndex()][Cell.NOW].setLearnState(true);
				SegmentUpdate sUpdate = getSegmentActiveSynapses(c, cell.getCellIndex(), segment, Cell.BEFORE,
						Segment.GETS_NEW_SYNAPSE);
				sUpdate.setSequenceSegment(true);
				cell.getSegmentUpdateList().add(sUpdate);
			}
		}
	}

	/**
	 * Phase 2 The second phase calculates the predictive state for each cell. A cell will turn on its predictive state
	 * output if one of its segments becomes active, i.e. if enough of its lateral inputs are currently active due to
	 * feed-forward input. In this case, the cell queues up the following changes: a) reinforcement of the currently
	 * active segment (lines 47-48), and b) reinforcement of a segment that could have predicted this activation, i.e. a
	 * segment that has a (potentially weak) match to activity during the previous time step (lines 50-53).
	 */
	public void calculatePredictedState() {
		for (int c = 0; c < SpatialPooler.AMMOUNT_OF_COLLUMNS; c++)
			for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {

				Cell cell = cells[c][i][Cell.NOW];

				for (int s = 0; s < cell.getSegments().size(); s++) {
					Segment segment = cell.getSegments().get(s);

					if (segmentActive(segment, Cell.NOW, Cell.ACTIVE_STATE)) {
						cells[c][i][Cell.NOW].setPredictiveState(true);
						SegmentUpdate activeUpdate = getSegmentActiveSynapses(c, i, segment, Cell.NOW,
								Segment.GETS_NO_NEW_SYNAPSE);
						cell.getSegmentUpdateList().add(activeUpdate);

						Segment predSegment = getBestMatchingSegment(c, i, Cell.BEFORE);
						SegmentUpdate predUpdate = getSegmentActiveSynapses(c, i, predSegment, Cell.BEFORE,
								Segment.GETS_NEW_SYNAPSE);

						cell.getSegmentUpdateList().add(predUpdate);
					}

				}
			}
	}

	/**
	 * Phase 3 The third and last phase actually carries out learning. In this phase segment updates that have been
	 * queued up are actually implemented once we get feedforward input and the cell is chosen as a learning cell (lines
	 * 56-57). Otherwise, if the cell ever stops predicting for any reason, we negatively reinforce the segments (lines
	 * 58-60).
	 */
	public void updateSynapses() {

		for (int c = 0; c < SpatialPooler.AMMOUNT_OF_COLLUMNS; c++)
			for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {

				Cell cell = cells[c][i][Cell.NOW];

				if (cells[c][i][Cell.NOW].hasLearnState()) {
					adaptSegments(cell.getSegmentUpdateList(), SegmentUpdate.POSITIVE_REINFORCEMENT);
					cell.getSegmentUpdateList().clear();

				} else
					if (!cells[c][i][Cell.NOW].hasPredictiveState() && cells[c][i][Cell.BEFORE].hasPredictiveState()) {

						adaptSegments(cell.getSegmentUpdateList(), SegmentUpdate.NO_POSITIVE_REINFORCEMENT);
						cell.getSegmentUpdateList().clear();

					}
			}

	}

	/**
	 * getActiveSegment(c, i, t, state) For the given column c cell i, return a segment index such that
	 * segmentActive(s,t, state) is true. If multiple segments are active, sequence segments are given preference.
	 * Otherwise, segments with most activity are given preference. I have used most activity od a segment as most
	 * connected synapses
	 * 
	 * @param c
	 * @param i
	 * @param time
	 * @param state
	 * @return
	 */
	private Segment getActiveSegment(int c, int i, int time, boolean state) {

		Segment returnValue = null;
		Cell cell = cells[c][i][time];
		List<Segment> activeSegments = new ArrayList<Segment>();
		List<Segment> segments = cell.getSegments();

		for (Segment segment : segments) {
			if (segmentActive(segment, time, state)) {
				activeSegments.add(segment);
			}
		}
		if (activeSegments.size() == 0) {
			returnValue = segments.get(0);
		} else {
			Collections.sort(segments);
			returnValue = segments.get(0);
		}
		return returnValue;
	}

	/**
	 * adaptSegments(segmentList, positiveReinforcement) This function iterates through a list of segmentUpdate's and
	 * reinforces each segment. For each segmentUpdate element, the following changes are performed. If
	 * positiveReinforcement is true then synapses on the active list get their permanence counts incremented by
	 * permanenceInc. All other synapses get their permanence counts decremented by permanenceDec. If
	 * positiveReinforcement is false, then synapses on the active list get their permanence counts decremented by
	 * permanenceDec. After this step, any synapses in segmentUpdate that do yet exist get added with a permanence count
	 * of initialPerm.
	 * 
	 * @param segmentUpdateList2
	 * @param b
	 */
	private void adaptSegments(List<SegmentUpdate> segmentUpdateList, boolean positiveReinforcement) {

		for (SegmentUpdate segmentUpdate : segmentUpdateList) {
			for (LateralSynapse synapse : segmentUpdate.getActiveSynapses()) {
				if (positiveReinforcement) {
					synapse.setPermanance(synapse.getPermanance() + LateralSynapse.PERMANANCE_INC);
				} else {
					synapse.setPermanance(synapse.getPermanance() - LateralSynapse.PERMANANCE_DEC);
				}
				Cell cell = cells[segmentUpdate.getColumnIndex()][segmentUpdate.getCellIndex()][Cell.NOW];
				Segment segment = cell.getSegments().get(segmentUpdate.getSegmentUpdateIndex());
				if (segment.getSynapses().size() > synapse.getSegmentIndex()) {// this is a new segment
					synapse.setPermanance(LateralSynapse.INITIAL_PERM);
				} else {
					segment.getSynapses().add(synapse);
				}

			}
		}

	}

	/**
	 * getBestMatchingCell(c) For the given column, return the cell with the best matching segment (as defined above).
	 * If no cell has a matching segment, then return the cell with the fewest number of segments.
	 * 
	 * @param c
	 * @param time
	 * @return
	 */
	private Cell getBestMatchingCell(int c, int time) {

		Cell returnValue = null;
		List<Segment> bestMatchingSegments = new ArrayList<Segment>();
		int cellIndexWithFewestNumberOfSegments = 0;
		if (Column.CELLS_PER_COLUMN > 0) {
			cellIndexWithFewestNumberOfSegments = cells[c][0][time].getSegments().size();
		}

		for (int i = 0; i < Column.CELLS_PER_COLUMN - 1; i++) {
			// cellsToCompare[j] = this.cells[c][j][time];
			if (cells[c][0][time].getSegments().size() < cellIndexWithFewestNumberOfSegments) {
				cellIndexWithFewestNumberOfSegments = cells[c][0][time].getSegments().size();
			}
			Segment bestMatchingSegment = getBestMatchingSegment(c, i, time);
			if (bestMatchingSegment != null) {
				bestMatchingSegments.add(bestMatchingSegment);
			}
		}
		if (bestMatchingSegments.size() != 0) {
			returnValue = cells[c][bestMatchingSegments.get(0).getCellIndex()][time];
		} else {
			returnValue = cells[c][cellIndexWithFewestNumberOfSegments][time];
			// return the cell with the fewest number of segments.
		}

		return returnValue;
	}

	/**
	 * getSegmentActiveSynapses(c, i, t, s, newSynapses= false) Return a segmentUpdate data structure containing a list
	 * of proposed changes to segment s. Let activeSynapses be the list of active synapses where the originating cells
	 * have their activeState output = 1 at time step t. (This list is empty if s = -1 since the segment doesn't exist.)
	 * newSynapses is an optional argument that defaults to false. If newSynapses is true, then newSynapseCount -
	 * count(activeSynapses) synapses are added to activeSynapses. These synapses are randomly chosen from the set of
	 * cells that have learnState output = 1 at time step t.
	 * 
	 * @param c
	 * @param i
	 * @param segment
	 * @param t
	 * @param b
	 * @return
	 */
	private SegmentUpdate getSegmentActiveSynapses(int c, int i, Segment segment, int t, boolean newSynapses) {
		SegmentUpdate returnValue = null;

		if (segment != null) {
			List<LateralSynapse> activeSynapses = new ArrayList<LateralSynapse>();

			for (LateralSynapse synapse : segment.getConnectedSynapses()) {
				Cell cell = cells[synapse.getColumnIndex()][synapse.getCellIndex()][t];
				if (cell.hasActiveState()) {
					activeSynapses.add(synapse);
				}
			}
			if (newSynapses) {
				Random random = new Random();
				for (int k = 0; k < TemporalPooler.NEW_SYNAPSE_COUNT - activeSynapses.size(); k++) {
					LateralSynapse newSynapse = new LateralSynapse();
					activeSynapses.add(newSynapse);
					Cell cell = null;
					// TODO the first time no cells will have learnstate so this will continue eternally
					do {
						cell = cells[random.nextInt(SpatialPooler.AMMOUNT_OF_COLLUMNS) - 1][random
								.nextInt(Column.CELLS_PER_COLUMN) - 1][Cell.NOW];
					} while (!cell.hasLearnState());
					newSynapse.setColumnIndex(random.nextInt(SpatialPooler.AMMOUNT_OF_COLLUMNS));
					newSynapse.setCellIndex(random.nextInt(Column.CELLS_PER_COLUMN));

				}
			}
			returnValue = new SegmentUpdate(c, i, segment.getSegmentIndex(), activeSynapses);
		}

		return returnValue;
	}

	/**
	 * getBestMatchingSegment(c, i, t) For the given column c cell i at time t, find the segment with the largest number
	 * of active synapses. This routine is aggressive in finding the best match. The permanence value of synapses is
	 * allowed to be below connectedPerm. The number of active synapses is allowed to be below activationThreshold, but
	 * must be above minThreshold. The routine returns the segment index. If no segments are found, then an index of -1
	 * is returned.
	 * 
	 * @param cell
	 * @return
	 */

	// TODO How can a synapse be active if its permanance is below connectedPerm
	// als het aantal connected synapses gelijk is, kijk dan naar het totaal aantal synapses.
	private Segment getBestMatchingSegment(int c, int i, int time) {

		Cell cell = cells[c][i][time];
		List<Segment> segments = cell.getSegments();
		return (getBestMatchingSegment(segments));
	}

	private Segment getBestMatchingSegment(List<Segment> segments) {
		Segment returnValue = null;
		Collections.sort(segments, new Comparator<Segment>() {

			public int compare(Segment segment, Segment toCompare) {
				int returnValue = 0;
				if (segment.getConnectedSynapses().size() == toCompare.getConnectedSynapses().size()
						&& segment.getSynapses().size() == toCompare.getSynapses().size()) {
					returnValue = 0;
				} else
					if ((segment.getConnectedSynapses().size() > toCompare.getConnectedSynapses().size())
							|| ((segment.getConnectedSynapses().size() == toCompare.getConnectedSynapses().size() && segment
									.getSynapses().size() > toCompare.getSynapses().size()))) {
						returnValue = 1;
					} else {
						returnValue = -1;
					}
				return returnValue;

			}
		});
		if (segments.get(0) != null && segments.get(0).getConnectedSynapses().size() > TemporalPooler.MIN_TRESHOLD) {
			returnValue = segments.get(0);
		}
		return returnValue;
	}

	/**
	 * segmentActive(s, t, state) This routine returns true if the number of connected synapses on segment s that are
	 * active due to the given state at time t is greater than activationThreshold. The parameter state can be
	 * activeState, or learnState.
	 * 
	 * @param segment
	 * @param time
	 *            can be 1 meaning now or 0 meaning t-1
	 * @param learnState
	 * @return
	 */
	private boolean segmentActive(Segment segment, int time, boolean state) {
		List<LateralSynapse> synapses = segment.getSynapses();
		int ammountConnected = 0;
		for (LateralSynapse synapse : synapses) {

			if (synapse.isActive()
					&& cells[synapse.getColumnIndex()][synapse.getCellIndex()][time].hasLearnState() == state) {
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
