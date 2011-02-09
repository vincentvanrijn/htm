package com.numenta.pooler;
import java.util.ArrayList;

import com.numenta.model.Cell;
import com.numenta.model.Column;
import com.numenta.model.Segment;

public class TemporalPooler {
	
	
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
						//TODO implement this
						Segment segment=cell.getActiveSegment(j, activeState);
						if(segment.isSsequenceSegment()){
							buPredicted=true;							
							activeColumn.getActiveStatesNow()[j]=true;
							if( segmentActiveBefore(segment, cell.isLearnState())){
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
					cell.setLearnState(true);
					Segment sUpdate=getSegmentActiveSynapses(c,i,s,t-1, true);
					sUpdate.setSequenceSegment(true);
					cell.getSegments().add(sUpdate);
				//addNewSegementToCell();//sequenceSegment
				}
			}
		}
		
		private boolean segmentActiveBefore(Segment segment, boolean learnState) {
		// TODO Auto-generated method stub
		return false;
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
							
							//segmentUpdateList.add()
							
							
						}
						
					}
				}
			}
			
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
		
		public void updateSynapses(){
			
			for (int i = 0; i < activeColumns.length; i++) {
				
				Column activeColumn=activeColumns[i];
				for (int j = 0; j < Column.CELLS_PER_COLUMN-1; j++) {	
					Cell cell=activeColumn.getCells()[j];
					if(cell.isLearnState()){
						
						
					} else{
						
						
						
					}
				}
			}
		}
}
