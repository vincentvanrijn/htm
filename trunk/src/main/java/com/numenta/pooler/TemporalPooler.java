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
						//TODO implement getActiveSegment
						Segment segment=cell.getActiveSegment(j, activeState);
						if(segment.isSsequenceSegment()){
							buPredicted=true;							
							activeColumn.getActiveStatesNow()[j]=true;
							//TODO implement segmentActive this is also in cell which one to choose?
							if( segmentActive(segment,-1, cell.isLearnState())){
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
					//TODO implement getBestMatchingCell
					Cell cell=getBestMatchingCell(activeColumn);
					Segment segment=getBestMatchingSegment(cell);
					cell.setLearnState(true);
					//TODO implementgetSegmentActiveSynapses
					SegmentUpdate sUpdate=getSegmentActiveSynapses(activeColumn,cell,-1,segment, true);
					sUpdate.setSequenceSegment(true);
					cell.getSegmentUpdateList().add(sUpdate);
				}
			}
		}
		

		
		
		public void calculatePredictedState(){
			for (int i = 0; i < activeColumns.length; i++) {//or do I need all columns?
				
				Column activeColumn=activeColumns[i];
				for (int j = 0; j < Column.CELLS_PER_COLUMN-1; j++) {	
					Cell cell=activeColumn.getCells()[j];
					
					for (int k= 0; k < cell.getSegments().size(); j++) {
						Segment segment=cell.getSegments().get(k);
						//TODO implement segmentActive 
						if(segmentActive(segment,-1, cell.isLearnState())){
							activeColumn.getPredictiveStatesNow()[j]=true;
							//TODO implement getSegmentActiveSynapses
							SegmentUpdate activeUpdate=getSegmentActiveSynapses(activeColumn, cell, 1, segment, false);
							cell.getSegmentUpdateList().add(activeUpdate);
							//TODO implement getBestMatchingSegment
							Segment predSegment=getBestMatchingSegment(cell);

							//TODO implement getSegmentActiveSynapses
							SegmentUpdate predUpdate=getSegmentActiveSynapses(activeColumn, cell, -1, segment, true);
							
							cell.getSegmentUpdateList().add(predUpdate);
						}
						
					}
				}
			}
			
		}
		
		public void updateSynapses(){
			
			for (int i = 0; i < activeColumns.length; i++) {//or do I need all columns?
				
				Column activeColumn=activeColumns[i];
				for (int j = 0; j < Column.CELLS_PER_COLUMN-1; j++) {

					Cell cell=activeColumn.getCells()[j];
					
						if(cell.isLearnState()){
							//TODO implement adaptSegments
							adaptSegments(cell.getSegmentUpdateList() ,true);
							cell.setSegmentUpdateList(null);
							
						} else if(!activeColumn.getPredictiveStatesNow()[j]
								&&activeColumn.getPredictiveStatesBefore()[j]) {

							//TODO implement adaptSegments
							adaptSegments(cell.getSegmentUpdateList() ,false);
							cell.setSegmentUpdateList(null);
							
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
		private SegmentUpdate getSegmentActiveSynapses(Column activeColumn, Cell cell,
				int i, Segment segment, boolean b) {
			// TODO Auto-generated method stub
			return null;
		}
			private Segment getBestMatchingSegment(Cell cell) {
			// TODO Auto-generated method stub
			return null;
		}
			private boolean segmentActive(Segment segment,int  time, boolean learnState) {
				
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
		
}
