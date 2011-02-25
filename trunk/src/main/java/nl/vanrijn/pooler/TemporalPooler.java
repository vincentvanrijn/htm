/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.pooler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import nl.vanrijn.model.Cell;
import nl.vanrijn.model.Column;
import nl.vanrijn.model.LateralSynapse;
import nl.vanrijn.model.Segment;
import nl.vanrijn.model.helper.SegmentUpdate;

public class TemporalPooler {

	/**
	 * minThreshold Minimum segment activity for learning.
	 */
	private static final int	MIN_TRESHOLD		= 0;

	// TODO value for min treshold

	// TODO choose value for min treshold
	/**
	 * activationThreshold Activation threshold for a segment. If the number of active connected synapses in a segment
	 * is greater than activationThreshold, the segment is said to be active.
	 */
	private static int			ACTIVATION_TRESHOLD	= 2;

	// TODO value for activasion treshold

	private static int			AMMOUNT_TIME		= 2;

	private static int			AMMOUNT_OF_SEGMENTS	= 10;

	// TODO value for ammount segments

	// TODO choose value maybe first same ammount as cells
	private static int			AMMOUNT_OF_SYNAPSES	= 20;

	// TODO choose value for ammount of synapse

	/**
	 * newSynapseCount The maximum number of synapses added to a segment during learning.
	 */
	private static int			NEW_SYNAPSE_COUNT;

	// TODO for new synapse count

	// TODO choose value for learning radius

	/**
	 * learningRadius The area around a temporal pooler cell from which it can get lateral connections.
	 */
	// TODO implement learning radius implementation
	private static int			LEARNING_RADIUS;

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

		ArrayList<Integer> collumnIndexes = new ArrayList<Integer>();
		for (int c = 0; c < SpatialPooler.AMMOUNT_OF_COLLUMNS; c++) {
			collumnIndexes.add(c);

		}
		Random random = new Random();
		// for (int c = 0; c < SpatialPooler.AMMOUNT_OF_COLLUMNS; c++) {
		int c = 0;
		for (int yy = 0; yy < 12; yy++) {
			for (int xx = 0; xx < 12; xx++) {

				for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {
					for (int t = 0; t < TemporalPooler.AMMOUNT_TIME; t++) {

						cells[c][i][t] = new Cell(c, i, t);
						cells[c][i][t].setXpos(xx);
						cells[c][i][t].setYpos(yy);

						List<Segment> segments = new ArrayList<Segment>();
						for (int s = 0; s < AMMOUNT_OF_SEGMENTS; s++) {
							List<LateralSynapse> synapses = new ArrayList<LateralSynapse>();
							Collections.shuffle(collumnIndexes);
							for (int y = 0; y < AMMOUNT_OF_SYNAPSES; y++) {

								// TODO permanance
								// TODO connect to cells

								// Get all cells in the area of this
								// cells'Learning
								// radius
								LateralSynapse synapse = new LateralSynapse(c, i, s, y, collumnIndexes.get(y), random
										.nextInt(3), LateralSynapse.INITIAL_PERM);
								synapses.add(synapse);
								// System.out.println(c+","+i+","+s+","+y+","+synapse.getFromColumnIndex()+","+synapse.getFromCellIndex());
							}

							segments.add(new Segment(c, i, s, synapses));
							// System.out.println(c);
						}
						cells[c][i][t].setSegments(segments);
					}
				}
				c++;
			}

		}
	}

	// private List<Cell> getNeighbors(){
	// List<Column> neighbors = new ArrayList<Column>();
	// for (Column potentialNeigbor : this.columns) {
	// int xposColPlusIn = (int) (column.getxPos() +
	// Math.round(inhibitionRadius));
	// int yposColPlusIn = (int) (column.getyPos() +
	// Math.round(inhibitionRadius));
	// int xposColMinIn = (int) (column.getxPos() -
	// Math.round(inhibitionRadius));
	// int yposColMinIn = (int) (column.getyPos() -
	// Math.round(inhibitionRadius));
	// if ((xposColPlusIn >= potentialNeigbor.getxPos()) && (yposColPlusIn >=
	// potentialNeigbor.getyPos())
	// && (xposColMinIn <= potentialNeigbor.getxPos())
	// && (yposColMinIn <= potentialNeigbor.getyPos() && column !=
	// potentialNeigbor)) {
	// neighbors.add(potentialNeigbor);
	// }
	// }
	//		
	// }

	public Cell[][][] getCells() {
		return cells;
	}

	public void setCells(Cell[][][] cells) {
		this.cells = cells;
	}

	/**
	 * Phase 1 The first phase calculates the activeState for each cell that is in a winning column. For those columns,
	 * the code further selects one cell per column as the learning cell (learnState). The logic is as follows: if the
	 * bottom-up input was predicted by any cell (i.e. its predictiveState output was 1 due to a sequence segment), then
	 * those cells become active (lines 23-27). If that segment became active from cells chosen with learnState on, this
	 * cell is selected as the learning cell (lines 28-30). If the bottom-up input was not predicted, then all cells in
	 * the column become active (lines 32-34). In addition, the best matching cell is chosen as the learning cell (lines
	 * 36-41) and a new segment is added to that cell.
	 */
	public void computeActiveState() {
		for (int c = 0; c < activeColumns.length; c++) {
			// System.out.println(c);
			Column column = activeColumns[c];
			// System.out.println(column);
			boolean buPredicted = false;
			boolean lcChosen = false;
			for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {

				if (cells[column.getColumnIndex()][i][Cell.BEFORE].hasPredictiveState()) {
					// System.out.println("predicted before ");
					Segment segment = getActiveSegment(column.getColumnIndex(), i, Cell.BEFORE, Cell.ACTIVE_STATE);
					if (segment!=null && segment.isSsequenceSegment()) {
						System.out.println("predicted and sequence");
						buPredicted = true;
						cells[column.getColumnIndex()][i][Cell.NOW].setActiveState(true);
						if (segmentActive(segment, Cell.BEFORE, Cell.LEARN_STATE)) {
							lcChosen = true;
							cells[column.getColumnIndex()][i][Cell.NOW].setLearnState(true);
							// TODO this never happsens Create a unit test that will make this happen
							System.out.println("setLearnstate because of sequence sg");
						}
					}
				}
			}
			if (!buPredicted) {
				for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {
					cells[column.getColumnIndex()][i][Cell.NOW].setActiveState(true);
					// System.out.println("all cells active "
					// + cells[c][i][Cell.NOW]);
				}

				// /System.out.println("all cells active "+);
			}
			if (!lcChosen) {

				Cell cell = getBestMatchingCell(column.getColumnIndex(), Cell.BEFORE);
				// System.out.println("best matching cell=" + cell);
				Segment segment = getBestMatchingSegment(column.getColumnIndex(), cell.getCellIndex(), Cell.BEFORE);

				// System.out.println("best matching segment=" + segment);
				// cells only get active in first layer...why???
				cells[column.getColumnIndex()][cell.getCellIndex()][Cell.NOW].setLearnState(true);
				// System.out.println("cell active="
				// + cells[c][cell.getCellIndex()][Cell.NOW]);
				if (segment != null) {
					SegmentUpdate sUpdate = getSegmentActiveSynapses(column.getColumnIndex(), cell.getCellIndex(),
							segment, Cell.BEFORE, Segment.GETS_NEW_SYNAPSE);
					// System.out.println("activeState "+sUpdate);
					// System.out.println("creating a segmentUpdate");
					sUpdate.setSequenceSegment(true);
					// TODO the segment update is for now!
					Cell cellToUpdate = cells[cell.getColumnIndex()][cell.getCellIndex()][Cell.NOW];
					cellToUpdate.getSegmentUpdateList().add(sUpdate);
					// System.out.println(sUpdate + " okok");
					// System.out.println(cellToUpdate);
				}

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
		for (int c = 0; c < SpatialPooler.AMMOUNT_OF_COLLUMNS; c++) {
			for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {

				Cell cell = cells[c][i][Cell.NOW];

				for (int s = 0; s < cell.getSegments().size(); s++) {
					Segment segment = cell.getSegments().get(s);

					if (segmentActive(segment, Cell.NOW, Cell.ACTIVE_STATE)) {// TODO which synapses and cells?
						// for(LateralSynapse synapse :segment.getConnectedSynapses()){
						// Cell
						// potentiallyActiveCell=cells[synapse.getFromColumnIndex()][synapse.getFromCellIndex()][Cell.NOW];
						// if(potentiallyActiveCell.hasActiveState()){
						// System.out.println("active Celll "+potentiallyActiveCell);
						// }
						// }
						// System.out.println("segmentActive pred " + segment);
						cells[c][i][Cell.NOW].setPredictiveState(true);
						SegmentUpdate activeUpdate = getSegmentActiveSynapses(c, i, segment, Cell.NOW,
								Segment.GETS_NO_NEW_SYNAPSE);
						// System.out.println("activeUpdate"+ activeUpdate);
						cell.getSegmentUpdateList().add(activeUpdate);
						// TODO This should not happen so often. Only once for an active cell.
						Segment predSegment = getBestMatchingSegment(c, i, Cell.BEFORE);
						if (predSegment != null) {
							SegmentUpdate predUpdate = getSegmentActiveSynapses(c, i, predSegment, Cell.BEFORE,
									Segment.GETS_NEW_SYNAPSE);

							cell.getSegmentUpdateList().add(predUpdate);

							// System.out.println("predUpdate "+predUpdate);
						}
						// System.out.println(cell.getSegmentUpdateList().size()
						// + " hllo");
					}

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
					// System.out.println("learnstate");
					adaptSegments(cell.getSegmentUpdateList(), SegmentUpdate.POSITIVE_REINFORCEMENT);
					//System.out.println("updating learnstate "+cell);
					cell.getSegmentUpdateList().clear();

				} else
					// TODO I have the feeling that this is wrong. It should be:if the cell was predicted but is not
					// active now.
					if (!cells[c][i][Cell.NOW].hasPredictiveState() && cells[c][i][Cell.BEFORE].hasPredictiveState()) {
						// System.out.println("was predicted but not active "+cell);
						adaptSegments(cell.getSegmentUpdateList(), SegmentUpdate.NO_POSITIVE_REINFORCEMENT);
						cell.getSegmentUpdateList().clear();

					}
			}

	}

	/**
	 * getActiveSegment(c, i, t, state) For the given column c cell i, return a segment index such that
	 * segmentActive(s,t, state) is true. If multiple segments are active, sequence segments are given preference.
	 * Otherwise, segments with most activity are given preference.
	 * 
	 * @param c
	 * @param i
	 * @param time
	 * @param state
	 * @return
	 */
	private Segment getActiveSegment(int c, int i, final int time, int state) {

		Segment returnValue = null;
		Cell cell = cells[c][i][time];
		List<Segment> activeSegments = new ArrayList<Segment>();
		List<Segment> segments = cell.getSegments();

		for (Segment segment : segments) {
			// System.out.println("segments "+segment);
			if (segmentActive(segment, time, state)) {
				activeSegments.add(segment);
			}
		}
		if (activeSegments.size() == 1) {
			// System.out.println("only 1");
			returnValue = activeSegments.get(0);
		} else {
			// TODO check if sequence segments are prefered
			Collections.sort(activeSegments, new Comparator<Segment>() {

				public int compare(Segment segment, Segment segmentToCompare) {
					// 1 sequence most activity
					// 2 sequence and active
					// 3 most activity
					// 4 least activity
					int returnValue = 0;
					//
					int ammountActiveCells = 0;
					int ammountActiveCellsToCompare = 0;
					for (LateralSynapse synapse : segment.getSynapses()) {
						if (cells[synapse.getFromColumnIndex()][synapse.getFromCellIndex()][time].hasActiveState()) {
							ammountActiveCells++;
						}
					}
					segment.setAmmountActiveCells(ammountActiveCells);
					for (LateralSynapse synapse : segmentToCompare.getSynapses()) {
						if (cells[synapse.getFromColumnIndex()][synapse.getFromCellIndex()][time].hasActiveState()) {
							ammountActiveCellsToCompare++;
						}
					}
					segmentToCompare.setAmmountActiveCells(ammountActiveCellsToCompare);

					if (segment.isSsequenceSegment() == segmentToCompare.isSsequenceSegment()
							&& segment.getAmmountActiveCells() == segmentToCompare.getAmmountActiveCells()) {
						returnValue = 0;
					} else
						if ((segment.isSsequenceSegment() && !segmentToCompare.isSsequenceSegment())
								|| (segment.isSsequenceSegment() == segmentToCompare.isSsequenceSegment() && segment
										.getAmmountActiveCells() > segmentToCompare.getAmmountActiveCells())) {
							returnValue = 1;
						} else {
							returnValue = -1;
						}
					return returnValue;
				}
			});
			if(activeSegments.size()>0){
				returnValue = activeSegments.get(0);
			}
			// System.out.println("most active "+returnValue);
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
		if (segmentUpdateList != null) {
			for (SegmentUpdate segmentUpdate : segmentUpdateList) {
				Cell cell = cells[segmentUpdate.getColumnIndex()][segmentUpdate.getCellIndex()][Cell.NOW];
				Segment segment = cell.getSegments().get(segmentUpdate.getSegmentUpdateIndex());
				if (segmentUpdate.isSequenceSegment()) {
					segment.setSequenceSegment(true);
					// System.out.println(segment);
				}
				for (LateralSynapse synapse : segmentUpdate.getActiveSynapses()) {
					if (positiveReinforcement) {
						System.out.println("inc " + synapse);

						synapse.setPermanance(synapse.getPermanance() + LateralSynapse.PERMANANCE_INC);
						// System.out.println("inc after " + synapse);
					} else {
						System.out.println("dec " + synapse);
						synapse.setPermanance(synapse.getPermanance() - LateralSynapse.PERMANANCE_DEC);
					}

					if (synapse.getSegmentIndex() >= segment.getSynapses().size()) {// this is a new segment
						// System.out.println("adding new Synapse");
						// TODO should point to a cell
						synapse.setPermanance(LateralSynapse.INITIAL_PERM);
						segment.getSynapses().add(synapse);
					}
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
		// TODO all cells have the same amount of segments. Do they mean connected synapses?
		int cellIndexWithFewestNumberOfSegments = -1;
		int lowestAmmountOfSegments = 0;
		if (Column.CELLS_PER_COLUMN > 0) {
			cellIndexWithFewestNumberOfSegments = 0;
			lowestAmmountOfSegments = cells[c][0][time].getSegments().size();
		}
		// find the cell with the fewest amount of segments and on the same time find the bestMatching segment from the
		// cell
		for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {
			// cellsToCompare[j] = this.cells[c][j][time];
			if (cells[c][i][time].getSegments().size() < lowestAmmountOfSegments) {
				cellIndexWithFewestNumberOfSegments = i;
				lowestAmmountOfSegments = cells[c][i][time].getSegments().size();
			}
			Segment bestMatchingSegmentPerCell = getBestMatchingSegment(c, i, time);
			if (bestMatchingSegmentPerCell != null) {
				bestMatchingSegments.add(bestMatchingSegmentPerCell);
			}
		}
		if (bestMatchingSegments.size() != 0) {
			Segment bestMatchingSegment = getBestMatchingSegment(bestMatchingSegments, time);
			returnValue = cells[c][bestMatchingSegment.getCellIndex()][time];
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
	private SegmentUpdate getSegmentActiveSynapses(int c, int i, Segment segment, int time, boolean newSynapses) {
		SegmentUpdate returnValue = null;

		if (segment != null) {
			List<LateralSynapse> activeSynapses = new ArrayList<LateralSynapse>();

			for (LateralSynapse synapse : segment.getConnectedSynapses()) {
				Cell cell = cells[synapse.getColumnIndex()][synapse.getCellIndex()][time];
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

					// TODO first build a list with cells with learnstate on then reorder that list random and add
					// synapses to the segment
					// TODO the first time no cells will have learnstate so this
					// will continue eternally
					do {
						cell = cells[random.nextInt(SpatialPooler.AMMOUNT_OF_COLLUMNS) - 1][random
								.nextInt(Column.CELLS_PER_COLUMN) - 1][time];
					} while (!cell.hasLearnState());

					newSynapse.setFromColumnIndex(cell.getColumnIndex());
					newSynapse.setFromCellIndex(cell.getCellIndex());
					newSynapse.setColumnIndex(c);
					newSynapse.setCellIndex(i);
					newSynapse.setSegmentIndex(segment.getSegmentIndex());

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

	// TODO order the synapses 1 active state from connected cell 2 permanance of the synapse.
	// only return a segment if the ammount of active synapses is obove minTreshold

	private Segment getBestMatchingSegment(int c, int i, final int time) {

		Cell cell = cells[c][i][time];
		List<Segment> segments = cell.getSegments();
		return (getBestMatchingSegment(segments, time));
	}

	private Segment getBestMatchingSegment(List<Segment> segments, final int time) {
		Segment returnValue = null;
		Collections.sort(segments, new Comparator<Segment>() {

			public int compare(Segment segment, Segment toCompare) {
				int ammountActiveCells = 0;
				int ammountActiveCellsToCompare = 0;
				for (LateralSynapse synapse : segment.getSynapses()) {
					if (cells[synapse.getFromColumnIndex()][synapse.getFromCellIndex()][time].hasActiveState()) {

						ammountActiveCells++;
					}
				}
				segment.setAmmountActiveCells(ammountActiveCells);
				for (LateralSynapse synapse : toCompare.getSynapses()) {
					if (cells[synapse.getFromColumnIndex()][synapse.getFromCellIndex()][time].hasActiveState()) {
						ammountActiveCellsToCompare++;
					}
				}
				toCompare.setAmmountActiveCells(ammountActiveCellsToCompare);
				int returnValue = 0;
				if (ammountActiveCells == ammountActiveCellsToCompare) {
					returnValue = 0;
				} else
					if (ammountActiveCells > ammountActiveCellsToCompare) {
						returnValue = 1;
					} else {
						returnValue = -1;
					}
				return returnValue;

			}
		});
		if (segments.get(0) != null && segments.get(0).getAmmountActiveCells() > TemporalPooler.MIN_TRESHOLD) {
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
	private boolean segmentActive(Segment segment, int time, int state) {
		List<LateralSynapse> synapses = segment.getSynapses();
		int ammountConnected = 0;
		Cell fromCell = null;

		for (LateralSynapse synapse : synapses) {

			if (state == Cell.LEARN_STATE) {
				// System.out.println("learnstatr "+synapse.isConnected());
				// TODO are all cells that have learnstate also Active?
				if (synapse.isConnected()
						&& cells[synapse.getFromColumnIndex()][synapse.getFromCellIndex()][time].hasLearnState()) {
					// fromCell = cells[synapse.getFromColumnIndex()][synapse.getFromCellIndex()][time];

					ammountConnected++;
					// System.out.println("ammountCon learnstate");
				}
			} else {
				if (state == Cell.ACTIVE_STATE) {

					// System.out.println("activetatr"+synapse.isConnected());
					if (synapse.isConnected()
							&& cells[synapse.getFromColumnIndex()][synapse.getFromCellIndex()][time].hasActiveState()) {
						ammountConnected++;

						// System.out.println("ammountCon"+synapse.isConnected());
					}
				}
			}

		}
		if (ammountConnected > TemporalPooler.ACTIVATION_TRESHOLD) {
			// System.out.println("ammo "
			// + (ammountConnected > TemporalPooler.ACTIVATION_TRESHOLD));
		}
		return ammountConnected > TemporalPooler.ACTIVATION_TRESHOLD;

	}

	public void setActiveColumns(ArrayList<Column> activeColumns) {

		Object[] objects = activeColumns.toArray();
		Column[] actives = new Column[objects.length];
		System.arraycopy(objects, 0, actives, 0, objects.length);
		this.activeColumns = actives;
	}

	public void nextTime() {
		for (int c = 0; c < SpatialPooler.AMMOUNT_OF_COLLUMNS; c++) {
			for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {
				// for(Segment segment :cells[c][i][1].getSegments()){
				// for (LateralSynapse lat:segment.getSynapses()) {
				// if(lat.getPermanance()>0.5){
				// System.out.println("hier ergens "+lat);
				// }
				// }
				// }
				cells[c][i][0] = cells[c][i][1];// old cell is new cell
				Cell cell = new Cell(c, i, 1);

				cell.setActiveState(false);
				cell.setLearnState(false);
				cell.setPredictiveState(false);
				cell.setSegments(cells[c][i][0].getSegments());
				cell.setSegmentUpdateList(new ArrayList<SegmentUpdate>());
				cell.setXpos(cells[c][i][0].getXpos());
				cell.setYpos(cells[c][i][0].getYpos());
				cells[c][i][1] = cell;
				// for(Segment segment :cell.getSegments()){
				// for (LateralSynapse lat:segment.getSynapses()) {
				// if(lat.getPermanance()>0.5){
				// System.out.println("hier ergens "+lat);
				// }
				// }
				// }
				// System.out.println(cell);

			}

		}

	}
}
