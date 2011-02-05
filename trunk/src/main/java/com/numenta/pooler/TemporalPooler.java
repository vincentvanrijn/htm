package com.numenta.pooler;
import java.util.ArrayList;

import com.numenta.model.Cell;
import com.numenta.model.Column;
import com.numenta.model.Segment;

public class TemporalPooler {
	
	
	private Column[] activeColumns;
	private String activeState="activeState";//learnState
		public void computeActiveState(){			
			
			for (int i = 0; i < activeColumns.length; i++) {
				Column activeColumn=activeColumns[i];
				boolean buPredicted=false;
				System.out.println("active column " +activeColumn.getOverlap());
				for (int j = 0; j < Column.CELLS_PER_COLUMN-1; j++) {					
										
					if(activeColumn.getPredictiveStatesBefore()[j]){
						Cell cell=activeColumn.getCells()[j];
						
						Segment segment=cell.getActiveSegment(j, activeState);
						if(segment.sequenceSegment()){
							buPredicted=true;
							
							activeColumn.getActiveStatesNow()[j]=true;
						}
					}
				}
				if(!buPredicted){
					for (int j = 0; j < Column.CELLS_PER_COLUMN-1; j++) {
						activeColumn.getPredictiveStatesNow()[j]=true;
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
		
		
		public void calculatePredictedState(){
			for (int i = 0; i < activeColumns.length; i++) {
				
				Column activeColumn=activeColumns[i];
				for (int j = 0; j < Column.CELLS_PER_COLUMN-1; j++) {	
					Cell cell=activeColumn.getCells()[j];
					
					for (int k= 0; k < cell.getSegments().length; j++) {
						
						if(cell.segmentActiveNow(k)){
							activeColumn.getPredictiveStatesNow()[j]=true;
							
						}
						
					}
				}
			}
			
		}
//		private void updateSynapses(){
//			for(int c=0;c<cells.length;c++){
//				
//				if(learnState(s,i,y)==1){
//					adaptSegment(segmentUpdateList(c,i),true);
//					segmentUpdateList(c,i).delete();
//					
//				} else if(prdeictiveState(c,i,t)==0 and predictiveState(c,i,t-1==1)){
//					adaptSegments(segmentUpdateList(c,i),false);
//					segmentUpdateList(c,i).delete();
//				}
//			}
//			
//			
//		}
		public void setActiveColumns(ArrayList<Column> activeColumns) {
			
			Object[] objects=activeColumns.toArray();
			Column[] actives=new Column[objects.length];
			System.arraycopy(objects, 0, actives, 0, objects.length);
			this.activeColumns=actives;
		}
}
