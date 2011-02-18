/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.pooler;

import java.util.ArrayList;

import nl.vanrijn.model.Column;

public class TemporalPoolerTest {

	public static void main(String[] args) {
		TemporalPooler tempo = new TemporalPooler();
		tempo.init();
		ArrayList<Column> activeColumns = new ArrayList<Column>();
		Column column1 = new Column();
		column1.setColumnIndex(12);
		Column column2 = new Column();
		column1.setColumnIndex(14);
		activeColumns.add(column1);
		activeColumns.add(column2);
		tempo.setActiveColumns(activeColumns);

		tempo.computeActiveState();

		tempo.calculatePredictedState();
		tempo.updateSynapses();

		activeColumns = new ArrayList<Column>();
		column1 = new Column();
		column1.setColumnIndex(19);
		column2 = new Column();
		column1.setColumnIndex(33);
		activeColumns.add(column1);
		activeColumns.add(column2);
		tempo.setActiveColumns(activeColumns);

		tempo.computeActiveState();

		tempo.calculatePredictedState();
		tempo.updateSynapses();

	}
}
