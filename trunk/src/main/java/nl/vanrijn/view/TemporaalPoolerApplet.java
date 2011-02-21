/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.view;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Logger;

import nl.vanrijn.model.Cell;
import nl.vanrijn.model.Column;
import nl.vanrijn.pooler.SpatialPooler;
import nl.vanrijn.pooler.TemporalPooler;

public class TemporaalPoolerApplet extends Applet {

	private boolean				mouseDragged		= false;

	private boolean				mousePressed		= false;

	private boolean				black				= true;

	/**
	 * input(t,j) The input to this level at time t. input(t, j) is 1 if the j'th input is on.
	 */
	private int[]				columns				= new int[144];

	private static final long	serialVersionUID	= 1L;

	private Graphics			graphics;

	private Logger				logger				= Logger.getLogger(this.getClass().getName());

	private Image				image;

	// private Column[] columns;

	private TemporalPooler		tempo				= new TemporalPooler();

	private Column				loggedColum			= null;

	DecimalFormat				df2					= new DecimalFormat("#,###,###,##0.00");

	private Cell[][][]			cells;

	public void init() {
		Button submitButton = new Button("invoke Temporal Pooler");
		submitButton.setActionCommand("temporal");
		submitButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("temporal")) {
					// this.invokeTemporalPooler()();
					invokeTemporalPooler();
				}
			}
		});
		add(submitButton);

		Button reset = new Button("reset");
		reset.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("reset"))
				// System.out.println("reset");
					reset();
			}
		});
		add(reset);

		image = createImage(getSize().width, getSize().height);
		graphics = image.getGraphics();

		addMouseListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				mousePressed = true;
				mouseOver(e.getX(), e.getY());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// After the release of mouseDrag, no mouseRelease should occur.
				if (!mouseDragged) {
					mouseOver(e.getX(), e.getY());
				}
				mouseDragged = false;
			}

		});
		addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				mouseDragged = true;
				mouseOver(e.getX(), e.getY());
			}
		});
		tempo.init();

		draw();
	}

	public void reset() {

		graphics.clearRect(0, 0, 600, 600);
		for (int i = 0; i < columns.length; i++) {
			columns[i] = 0;
		}
		this.loggedColum = null;
		tempo.init();

		draw();
	}

	@Override
	public void paint(Graphics graphics) {
		graphics.drawImage(image, 0, 0, this);
	}

	private void mouseOver(int x, int y) {

		int index = -1;
		outer: for (int yy = 0; yy < 12; yy++) {

			for (int xx = 0; xx < 12; xx++) {
				index++;
				if (y < 19 * yy + 116 && y > 19 * yy + 100) {
					if (x > 19 * xx && x < 19 * xx + 16) {

						if (mousePressed) {
							if (columns[index] == 1) {
								black = false;
							} else {
								black = true;
							}
							mousePressed = false;

						} else {
							if (mouseDragged) {

								if (black) {
									drawBlackOval(xx, yy);
									setInputValue(index, 1);
								} else {
									drawWhiteOval(xx, yy);

									setInputValue(index, 0);
								}

							} else {

								if (columns[index] == 1) {
									drawWhiteOval(xx, yy);

									setInputValue(index, 0);
								} else {
									drawBlackOval(xx, yy);

									setInputValue(index, 1);
								}

							}

							repaint();
						}

						break outer;
					}
				}
			}
		}
	}

	/**
	 * Draws the columns and .Without input/output
	 */
	public void draw() {
		graphics.setColor(Color.BLACK);
		for (int x = 0; x < 12; x++) {
			for (int y = 0; y < 12; y++) {
				graphics.drawOval(19 * x, 100 + (19 * y), 16, 16);

				// TODO draw cells
				graphics.drawOval(9 * x + 260, 100 + (9 * y), 7, 7);
				graphics.drawOval(9 * x + 260, 220 + (9 * y), 7, 7);
				graphics.drawOval(9 * x + 380, 100 + (9 * y), 7, 7);
				graphics.drawOval(9 * x + 380, 220 + (9 * y), 7, 7);
			}
		}
		repaint();
	}

	private void drawBlackOval(int x, int y) {
		graphics.setColor(Color.black);
		// graphics.setColor(Color.getHSBColor(10, 0.5f,0.5f));
		graphics.fillOval(19 * x, 100 + (19 * y), 16, 16);

	}

	private void drawWhiteOval(int x, int y) {
		graphics.setColor(Color.white);
		graphics.fillOval(19 * x, 100 + (19 * y), 16, 16);
		graphics.setColor(Color.black);
		graphics.drawOval(19 * x, 100 + (19 * y), 16, 16);

	}

	private void setInputValue(int index, int value) {
		columns[index] = value;
	}

	public void invokeTemporalPooler() {
		graphics.clearRect(0, 0, 600, 600);
		draw();
		//
		ArrayList<Column> activeColumns = new ArrayList<Column>();
		int index = -1;
		for (int yy = 0; yy < 12; yy++) {

			for (int xx = 0; xx < 12; xx++) {
				index++;
				if (columns[index] == 1) {
					// System.out.println(index+" "+xx+","+yy);
					Column column = new Column();
					column.setColumnIndex(index);
					column.setxPos(xx);
					column.setyPos(yy);
					column.setActive(true);
					activeColumns.add(column);
				}
			}
		}

		tempo.setActiveColumns(activeColumns);
		tempo.computeActiveState();
		tempo.calculatePredictedState();
		tempo.updateSynapses();
		this.cells = tempo.getCells();

		for (int c = 0; c < SpatialPooler.AMMOUNT_OF_COLLUMNS; c++) {
			for (int i = 0; i < Column.CELLS_PER_COLUMN; i++) {

				Cell cell = cells[c][i][1];
				if (cell.hasActiveState()) {
					// System.out.println(cell
					// +" "+cell.getXpos()+","+cell.getYpos()
					// );
					graphics.setColor(Color.black);

					switch (cell.getCellIndex()) {
						case 0: {
							// System.out.println("nuuuuu0" + cell.getCellIndex()
							// +" "+cell.getXpos()+" " +cell.getYpos());
							graphics.fillOval(9 * cell.getXpos() + 260, 100 + (9 * cell.getYpos()), 7, 7);
							break;
						}
						case 1: {
							// System.out.println("nuuuuu1" + cell.getCellIndex()
							// +" "+cell.getXpos()+" " +cell.getYpos());
							graphics.fillOval(9 * cell.getXpos() + 260, 220 + (9 * cell.getYpos()), 7, 7);
							break;
						}
						case 2: {
							// System.out.println("nuuuuu2" + cell.getCellIndex()
							// +" "+cell.getXpos()+" " +cell.getYpos());
							graphics.fillOval(9 * cell.getXpos() + 380, 100 + (9 * cell.getYpos()), 7, 7);
							break;
						}
						case 3: {
							// System.out.println("nuuuuu3" + cell.getCellIndex()
							// +" "+cell.getXpos()+" " +cell.getYpos());
							graphics.fillOval(9 * cell.getXpos() + 380, 220 + (9 * cell.getYpos()), 7, 7);
							break;
						}
						default:
							break;
					}
				}
				if (cell.hasPredictiveState()) {
					// System.out.println(cell +" "+cell.getXpos()+","+cell.getYpos()
					// );
					graphics.setColor(Color.blue);

					switch (cell.getCellIndex()) {
						case 0: {
							// System.out.println("nuuuuu0" + cell.getCellIndex() +" "+cell.getXpos()+" "
							// +cell.getYpos());
							graphics.fillOval(9 * cell.getXpos() + 261, 101 + (9 * cell.getYpos()), 5, 5);
							break;
						}
						case 1: {
							// System.out.println("nuuuuu1" + cell.getCellIndex() +" "+cell.getXpos()+" "
							// +cell.getYpos());
							graphics.fillOval(9 * cell.getXpos() + 261, 221 + (9 * cell.getYpos()), 5, 5);
							break;
						}
						case 2: {
							// System.out.println("nuuuuu2" + cell.getCellIndex() +" "+cell.getXpos()+" "
							// +cell.getYpos());
							graphics.fillOval(9 * cell.getXpos() + 381, 101 + (9 * cell.getYpos()), 5, 5);
							break;
						}
						case 3: {
							// System.out.println("nuuuuu3" + cell.getCellIndex() +" "+cell.getXpos()+" "
							// +cell.getYpos());
							graphics.fillOval(9 * cell.getXpos() + 381, 221 + (9 * cell.getYpos()), 5, 5);
							break;
						}
						default:
							break;
					}
				}
				if (cell.hasLearnState()) {
					// System.out.println(cell
					// +" "+cell.getXpos()+","+cell.getYpos()
					// );
					graphics.setColor(Color.red);

					switch (cell.getCellIndex()) {
						case 0: {
							// System.out.println("nuuuuu0" + cell.getCellIndex()
							// +" "+cell.getXpos()+" " +cell.getYpos());
							graphics.fillOval(9 * cell.getXpos() + 262, 102 + (9 * cell.getYpos()), 3, 3);
							break;
						}
						case 1: {
							// System.out.println("nuuuuu1" + cell.getCellIndex()
							// +" "+cell.getXpos()+" " +cell.getYpos());
							graphics.fillOval(9 * cell.getXpos() + 262, 222 + (9 * cell.getYpos()), 3, 3);
							break;
						}
						case 2: {
							// System.out.println("nuuuuu2" + cell.getCellIndex()
							// +" "+cell.getXpos()+" " +cell.getYpos());
							graphics.fillOval(9 * cell.getXpos() + 321, 102 + (9 * cell.getYpos()), 3, 3);
							break;
						}
						case 3: {
							// System.out.println("nuuuuu3" + cell.getCellIndex()
							// +" "+cell.getXpos()+" " +cell.getYpos());
							graphics.fillOval(9 * cell.getXpos() + 321, 222 + (9 * cell.getYpos()), 3, 3);
							break;
						}
						default:
							break;
					}

				}
			}
		}
		repaint();
		tempo.nextTime();

	}
}
