package com.numenta.pooler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.numenta.model.Cell;
import com.numenta.model.Column;
import com.numenta.model.Segment;
import com.numenta.model.Synapse;
import com.numenta.model.helper.SegmentUpdate;

public class TemporalPooler {
	
	
	private static double activeTresHold;
	private Column[] activeColumns;
	private String activeState="activeState";//learnState
	private List<SegmentUpdate> segmentUpdateList;
	
	private void computeNextTimeStep(){
		for (int i = 0; i < activeColumns.length; i++) {
			Column activeColumn=activeColumns[i];
			activeColumn.setActiveStatesNow(new boolean[Column.CELLS_PER_COLUMN]);
			//activeColumn.getPredictiveStatesNow();
			for (int j = 0; j < activeColumn.getPredictiveStatesNow().length; j++) {
				activeColumn.getPredictiveStatesBefore()[j]=activeColumn.getPredictiveStatesNow()[j];
			}
			activeColumn.setPredictiveStatesBefore(activeColumn.getPredictiveStatesNow());
			activeColumn.setPredictiveStatesBefore(new boolean[Column.CELLS_PER_COLUMN]);
			
		}
	}
	public void computeActiveState(){			
		computeNextTimeStep();
			for (int i = 0; i < activeColumns.length; i++) {
				Column activeColumn=activeColumns[i];
				boolean buPredicted=false;
				boolean lcChosen=false;
				System.out.println("active column " +activeColumn.getOverlap());
				for (int j = 0; j < Column.CELLS_PER_COLUMN-1; j++) {					
										
					if(activeColumn.getPredictiveStatesBefore()[j]){
						Cell cell=activeColumn.getCells()[j];
						System.out.println("predicted before ");
						//TODO implement this
						Segment segment=cell.getActiveSegment(j, activeState);
						if(segment.isSsequenceSegment()){
							buPredicted=true;							
							activeColumn.getActiveStatesNow()[j]=true;
							if( segmentActive(segment, cell.isLearnState())){
								lcChosen=true;
								cell.setLearnState(true);
							}
						}
					}
				}
				if(!buPredicted){
					for (int j = 0; j < Column.CELLS_PER_COLUMN; j++) {
						activeColumn.getActiveStatesNow()[j]=true;
					}

					System.out.println("all cells active");
				}
				if(!lcChosen){
					Cell cell=getBestMatchingCell(activeColumn);
					Segment segment=getBestMatchingSegment(cell);
					cell.setLearnState(true);
					SegmentUpdate sUpdate=getSegmentActiveSynapses(activeColumn,cell,-1,segment, true);
					sUpdate.setSequenceSegment(true);
					segmentUpdateList.add(sUpdate);
				}
			}
		}
		
		private SegmentUpdate getSegmentActiveSynapses(Column activeColumn, Cell cell,
			int i, Segment segment, boolean b) {
		// TODO Auto-generated method stub
		return null;
	}
		private Segment getBestMatchingSegment(Cell cell) {
		// TODO Auto-generated method stub
		return null;
	}
		private boolean segmentActive(Segment segment, boolean learnState) {
			
			List synapses=segment.getSynapses();
			int ammountConnected=0;
			for (Iterator iterator = synapses.iterator(); iterator.hasNext();) {
				Synapse synapse = (Synapse) iterator.next();
				if(synapse.isActive(12)){
					ammountConnected++;
				}							
			}
			return ammountConnected>TemporalPooler.activeTresHold;
		
		}
		public Column[] getActiveColumns() {
			return activeColumns;
		}
		public void setActiveColumns(Column[] activeColumns) {
			this.activeColumns = activeColumns;
		}
		
		
		public void calculatePredictedState(){
			for (int i = 0; i < activeColumns.length; i++) {
				
				Column activeColumn=activeColumns[i];
				for (int j = 0; j < Column.CELLS_PER_COLUMN-1; j++) {	
					Cell cell=activeColumn.getCells()[j];
					
					for (int k= 0; k < cell.getSegments().size(); j++) {
						
						if(cell.segmentActiveNow(k)){
							activeColumn.getPredictiveStatesNow()[j]=true;
							
							SegmentUpdate activeUpdate=getSegmentActiveSynapses(activeColumn, cell, 1, segment, false);
							segmentUpdateList.add(activeUpdate);
							
							Segment predSegment=getBestMatchingSegment(cell);
							SegmentUpdate predUpdate=getSegmentActiveSynapses(activeColumn, cell, -1, segment, true);
							
							segmentUpdateList.add(predUpdate);
						}
						
					}
				}
			}
			
		}
		
		public void updateSynapses(){
			
			for (int i = 0; i < activeColumns.length; i++) {
				
				Column activeColumn=activeColumns[i];
				for (int j = 0; j < Column.CELLS_PER_COLUMN-1; j++) {

					Cell cell=activeColumn.getCells()[j];
					for (Iterator iterator = cell.getSegments().iterator(); iterator
							.hasNext();) {
						Segment segment = (Segment) iterator.next();
						
					
						if(cell.isLearnState()){
							adaptSegments(segmentUpdateList ,true);
							segmentUpdateList.remove(segment);
							
						} else if(cell.getPredictiveStatesNow().get(0).equals(null)
								&&cell.getPredictiveStatesBefore().get(0).equals(null)) {
							adaptSegments(segmentUpdateList ,false);
							segmentUpdateList.remove(segment);
							
						}
						
						
					}
				}
			}
		}
		
		private void adaptSegments(List<SegmentUpdate> segmentUpdateList2,
				boolean b) {
			// TODO Auto-generated method stub
			
		}
		private Cell getBestMatchingCell(Column column){
			Cell cell=null;
			for (int j = 0; j < Column.CELLS_PER_COLUMN-1; j++) {	
				cell=column.getCells()[j];
				System.out.println(cell.getSegments().size());
			}	
			return cell;
		}
		

		public void setActiveColumns(ArrayList<Column> activeColumns) {
			
			Object[] objects=activeColumns.toArray();
			Column[] actives=new Column[objects.length];
			System.arraycopy(objects, 0, actives, 0, objects.length);
			this.activeColumns=actives;
		}
		
		
}
