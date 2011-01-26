package com.numenta.pooler;
import com.numenta.model.Cell;
import com.numenta.model.Column;
import com.numenta.model.Segment;
import com.numenta.model.helper.CellHelper;

public class TemporalPooler {
	
	
	private Column[] activeColumns;
	private Cell[] cells;

		private void computeActiveState(){
			
			for (int i = 0; i < activeColumns.length; i++) {
				Column activeColumn=activeColumns[i];
				boolean buPredicted=false;
				
				for (int j = 0; j < Column.CELLS_PER_COLUMN-1; j++) {
					
					int t =0;
					CellHelper cellHelper=new CellHelper(j,t-1);
					
					if((Boolean)activeColumn.getPredictiveState().get(cellHelper)){
						Segment segment=getActiveSegment(activeColumn,j,t-1, activeState);
						if(segment.sequenceSegment()){
							buPredicted=true;
							activeState(activeColumn,j,t)=1;
						}
					}
				}
				if(buPredicted){
					for (int j = 0; j < Column.CELLS_PER_COLUMN-1; j++) {
						activeState(activeColumn,j,t)=1;
					}
				}
			}
		}
		//tell me if on this column, the jth cell was predicting one timestep before
		private boolean predictiveState(Column activeColumn, int j, int i) {
			// TODO Auto-generated method stub
			return false;
		}
		public Column[] getActiveColumns() {
			return activeColumns;
		}
		public void setActiveColumns(Column[] activeColumns) {
			this.activeColumns = activeColumns;
		}
		public Cell[] getCells() {
			return cells;
		}
		public void setCells(Cell[] cells) {
			this.cells = cells;
		}
		private void calculatePredictedState(){
			for (int i = 0; i < cells.length; i++) {
				Cell cell=cells[i];
				for (int j = 0; j < cell.getSegments().length; j++) {
					if(segmentActive(cell, i,s,t)){
						predictiveState(c,i,t)=1;
						
					}
					
				}
				
			}
			
		}
		private void updateSynapses(){
			for(int c=0;c<cells.length;c++){
				
				if(learnState(s,i,y)==1){
					adaptSegment(segmentUpdateList(c,i),true);
					segmentUpdateList(c,i).delete();
					
				} else if(prdeictiveState(c,i,t)==0 and predictiveState(c,i,t-1==1)){
					adaptSegments(segmentUpdateList(c,i),false);
					segmentUpdateList(c,i).delete();
				}
			}
			
			
		}
}
