package nl.vanrijn.pooler;

import static org.junit.Assert.assertEquals;
import nl.vanrijn.model.Cell;
import nl.vanrijn.model.LateralSynapse;
import nl.vanrijn.model.Segment;

import org.junit.Before;
import org.junit.Test;

public class TemporalPoolerTest {

	@Before
	public void setup() {
		TemporalPooler tempo = new TemporalPooler(12,12);
		tempo.init();
		Cell cell = tempo.getCells()[0][0][1];
		tempo.nextTime();

		Segment segment = cell.getSegments().get(0);
		
		for (LateralSynapse synaps : segment.getSynapses()) {
			//System.out.println(synaps);
			Cell cell1 =tempo.getCells()[synaps.getFromColumnIndex()][synaps.getFromCellIndex()][Cell.NOW];
			cell1.setActiveState(true);
			cell1.setLearnState(true);
		}

		System.out.println("1"+segment );
		System.out.println(tempo.segmentActive(segment, Cell.NOW, Cell.ACTIVE_STATE));
		Segment segment2=cell.getSegments().get(1);
		segment2.setSequenceSegment(true);
		for (LateralSynapse synaps : segment2.getSynapses()) {
			//System.out.println(synaps);
			Cell cell1 =tempo.getCells()[synaps.getFromColumnIndex()][synaps.getFromCellIndex()][Cell.NOW];
			cell1.setActiveState(true);
			cell1.setLearnState(true);
		}
		System.out.println("2"+segment2);

		System.out.println(tempo.segmentActive(segment2, Cell.NOW, Cell.ACTIVE_STATE));
		//tempo.nextTime();
//		boolean segmentActive=tempo.segmentActive(segment, Cell.BEFORE, Cell.ACTIVE_STATE);
//		System.out.println(segmentActive);
		
		
		Segment activeSegment=tempo.getActiveSegment(0, 0, Cell.NOW, Cell.ACTIVE_STATE);
		System.out.println("this should be the best "+activeSegment);
		
		for(Segment segment3 : tempo.getCells()[0][0][Cell.NOW].getSegments()){
			System.out.println(segment3);
		}
	}

	@Test
	public void setSegmentIndex() {

		assertEquals(50, 5 * 10);

	}
}
