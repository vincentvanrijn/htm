package com.numenta.pooler;
import java.util.HashMap;
import java.util.Vector;

public class TemporalPooler {
	
	
	private Column[] activeColumns;
	private Cell[] cells;

		private void computeActiveState(){
			
			for (int i = 0; i < activeColumns.length; i++) {
				Column activeColumn=activeColumns[i];
				boolean buPredicted=false;
				
				for (int j = 0; j < Column.CELLS_PER_COLUMN-1; j++) {
					Vector vector=activeColumn.getPredictiveState();
					if(true){
						buPredicted=true;
					}
				}
				if(buPredicted){
					for (int j = 0; j < Column.CELLS_PER_COLUMN-1; j++) {
						
					}
				}
			}
			
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
		private void computePredictedState(){
			for (int i = 0; i < cells.length; i++) {
				Cell cell=cells[i];
				for (int j = 0; j < cell.getSegments().length; j++) {
//					if(){
//						
//					}
					
				}
				
			}
			
		}
		private void updateSynapses(){
			
			
		}
}
