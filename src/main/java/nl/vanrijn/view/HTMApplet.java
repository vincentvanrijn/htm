/*
 * Copyright Numenta. All Rights Reserved.
 */
package nl.vanrijn.view;

import java.applet.Applet;
import java.awt.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.DecimalFormat;
import java.util.logging.Logger;

import nl.vanrijn.model.Column;
import nl.vanrijn.model.Synapse;
import nl.vanrijn.pooler.SpatialPooler;
import nl.vanrijn.pooler.TemporalPooler;


public class HTMApplet extends Applet {

	private boolean				mouseDragged			= false;

	private boolean				mousePressed			= false;

	private boolean				black					= true;

	/**
	 * input(t,j) The input to this level at time t. input(t, j) is 1 if the j'th input is on.
	 */
	private int[]				input					= new int[144];

	private static final long	serialVersionUID		= 1L;

	private Graphics			graphics;

	private Logger				logger					= Logger.getLogger(this.getClass().getName());

	private Image				image;

	// private Column[] columns;
	private SpatialPooler		spat					= null;

	private TemporalPooler		tempo					= new TemporalPooler(12,12);

	private TextField			desiredLocalActivity	= new TextField("3");

	private TextField			connectedPermanance		= new TextField("0.7");

	private TextField			minimalOverlap			= new TextField("7");

	private TextField			permananceDec			= new TextField("0.05");

	private TextField			permananceInc			= new TextField("0.05");

	private TextField			amountOfSynapses		= new TextField("30");

	private TextField			inhibitionRadius		= new TextField("5.0");

	private TextField			boost					= new TextField("1.0");

	private Column				loggedColum				= null;

	DecimalFormat				df2						= new DecimalFormat("#,###,###,##0.00");

	public void init() {
		desiredLocalActivity.setName("desiredLocalActivity");
		connectedPermanance.setName("connectedPermanance");
		minimalOverlap.setName("minimalOverlap");
		permananceDec.setName("permananceDec");
		permananceInc.setName("permananceInc");
		amountOfSynapses.setName("amountOfSynapses");
		inhibitionRadius.setName("inhibitionRadius");
		Button submitButton = new Button("sparseDist");
		submitButton.setActionCommand("sparse");
		submitButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("sparse")) createSparseDistributedRep();
			}
		});
		add(submitButton);

		Button reset = new Button("reset");
		reset.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("reset"))
				// logger.log(Level.INFO, "sparse");
					// createSparseDistributedRep();
					spat = new SpatialPooler(new Integer(desiredLocalActivity.getText()), new Double(
							connectedPermanance.getText()), new Integer(minimalOverlap.getText()), new Double(
							permananceDec.getText()), new Double(permananceInc.getText()), new Integer(amountOfSynapses
							.getText()), new Double(inhibitionRadius.getText()));
				reset();
			}
		});
		add(new Label("desi.loc.act"));
		add(desiredLocalActivity);
		add(new Label("con.perm"));
		add(connectedPermanance);
		add(new Label("min.ov"));
		add(minimalOverlap);
		add(new Label("perm.dec"));
		add(permananceDec);
		add(new Label("perm.inc"));
		add(permananceInc);
		add(new Label("amount.syn"));
		add(amountOfSynapses);
		add(new Label("inhib.rad"));
		add(inhibitionRadius);
		add(reset);
		spat = new SpatialPooler(new Integer(desiredLocalActivity.getText()), new Double(
				connectedPermanance.getText()), new Integer(minimalOverlap.getText()), new Double(
				permananceDec.getText()), new Double(permananceInc.getText()), new Integer(amountOfSynapses
				.getText()), new Double(inhibitionRadius.getText()));

		for (int i = 0; i < input.length; i++) {
			input[i] = 0;
		}
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
		draw();
	}

	public void reset() {

		graphics.clearRect(0, 0, 600, 600);
		for (int i = 0; i < input.length; i++) {
			input[i] = 0;
		}
		this.loggedColum = null;
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
							if (input[index] == 1) {
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

								if (input[index] == 1) {
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
					} else {

						if (x > 19 * xx + 260 && x < 19 * xx + 260 + 16) {
							if (!mousePressed) {
								logColumn(spat.getColumns()[index], xx, yy);
							}
							if (mousePressed) {
								mousePressed = false;
							}
							break outer;
						}
					}
				}
			}
		}
	}

	private void logColumn(Column column, int xx, int yy) {

		// delete the blue dot if there is one
		if (!(mouseDragged && loggedColum != null && this.loggedColum.getxPos() == column.getxPos() && this.loggedColum
				.getyPos() == column.getyPos())) {

			if (loggedColum != null) {
				if (loggedColum.isActive()) {
					graphics.setColor(Color.RED);
				} else {
					graphics.setColor(Color.WHITE);
				}
				graphics.fillOval(19 * this.loggedColum.getxPos() + 5 + 260, 99 + 19 * this.loggedColum.getyPos() + 6,
						6, 6);
			}

			if (loggedColum != null && this.loggedColum.getxPos() == column.getxPos()
					&& this.loggedColum.getyPos() == column.getyPos()) {
				this.loggedColum = null;
				reDraw();
				repaint();
				// we click on a new column
			} else {
				this.loggedColum = column;
				graphics.setColor(Color.blue);
				graphics.fillOval(19 * xx + 5 + 260, 99 + 19 * yy + 6, 6, 6);
				reDraw();
				logSynapses(column);

			}
		}

	}

	private void logSynapses(Column column) {

		graphics.setColor(Color.black);
		if (column.getNeigbours() != null) {
			String columnBoost = df2.format(column.getBoost());

			String minimalLocalActivity = df2.format(column.getMinimalLocalActivity());
			String overlap = df2.format(column.getOverlap());
			String overlapDutyCycle = df2.format(column.getOverlapDutyCycle());
			String activeDutyCycle = df2.format(column.getActiveDutyCycle());
			String minimalDutyCycle = df2.format(column.getMinimalDutyCycle());

			graphics.drawString("C " + column.getxPos() + "," + column.getyPos() + " bst=" + columnBoost + " nbrs="
					+ column.getNeigbours().size() + " ovl=" + overlap + " m.loc.act=" + minimalLocalActivity
					+ " o.d.cy=" + overlapDutyCycle + " a.d.cy=" + activeDutyCycle + " m.d.cy=" + minimalDutyCycle, 0,
					340);
		}

		for (int i = 0; i < column.getPotentialSynapses().length; i++) {
			Synapse potentialSynapse = column.getPotentialSynapses()[i];
			if (potentialSynapse.isConnected(spat.getConnectedPermanance())) {
				graphics.setColor(Color.GREEN);
			} else {
				graphics.setColor(Color.RED);
			}
			String permanance = df2.format(potentialSynapse.getPermanance());

			graphics.drawString("Synapse " + potentialSynapse.getxPos() + " " + potentialSynapse.getyPos() + " perm="
					+ permanance + " input=" + potentialSynapse.getSourceInput() + " connected="
					+ potentialSynapse.isConnected(spat.getConnectedPermanance()), 0, 354 + 16 * i);
			// graphics.setColor(Color.getHSBColor(10, 0.5f,0.5f));
			graphics.fillOval(19 * potentialSynapse.getxPos() + 5, 100 + (19 * potentialSynapse.getyPos()) + 5, 6, 6);
		}
		repaint();
	}

	/**
	 * Draws the inputspace and columns.Without input/output
	 */
	public void draw() {
		graphics.setColor(Color.BLACK);
		for (int x = 0; x < 12; x++) {
			for (int y = 0; y < 12; y++) {
				graphics.drawOval(19 * x, 100 + (19 * y), 16, 16);
				graphics.drawOval(19 * x + 260, 100 + (19 * y), 16, 16);
			}
		}
		repaint();
	}

	private void reDraw() {
		graphics.clearRect(0, 100, 260, 230);
		graphics.clearRect(0, 330, 500, 750);
		int j = 0;
		for (int y = 0; y < 12; y++) {
			for (int x = 0; x < 12; x++) {

				if (input[j] == 0) {
					graphics.setColor(Color.black);
					graphics.drawOval(19 * x, 100 + (19 * y), 16, 16);
				} else {

					graphics.drawOval(19 * x, 100 + (19 * y), 16, 16);
					graphics.setColor(Color.black);
					graphics.fillOval(19 * x, 100 + (19 * y), 16, 16);
				}
				j++;
			}
		}
	}

	private void drawBlackOval(int x, int y) {
		graphics.setColor(Color.black);
		// graphics.setColor(Color.getHSBColor(10, 0.5f,0.5f));
		graphics.fillOval(19 * x, 100 + (19 * y), 16, 16);
		if (loggedColum != null) {
			logSynapses(loggedColum);
		}
	}

	private void drawWhiteOval(int x, int y) {
		graphics.setColor(Color.white);
		graphics.fillOval(19 * x, 100 + (19 * y), 16, 16);
		graphics.setColor(Color.black);
		graphics.drawOval(19 * x, 100 + (19 * y), 16, 16);

		if (loggedColum != null) {
			logSynapses(loggedColum);
		}
	}

	private void setInputValue(int index, int value) {
		input[index] = value;
	}

	public void createSparseDistributedRep() {

		spat.conectSynapsesToInputSpace(input);
		spat.computOverlap();
		spat.computeWinningColumsAfterInhibition();
		spat.updateSynapses();

		int j = 0;
		Color color = Color.red;
		graphics.setColor(color);

		Column[] columns = spat.getColumns();
		graphics.clearRect(0, 60, 260, 40);
		graphics.drawString("new inhibitian radius " + Math.round(spat.getInhibitionRadius()), 0, 80);
		for (int y = 0; y < 12; y++) {
			for (int x = 0; x < 12; x++) {
				if (columns[j].isActive()) {
					graphics.setColor(color);
					graphics.fillOval(19 * x + 260, 100 + (19 * y), 16, 16);
				} else {
					graphics.setColor(Color.WHITE);
					graphics.fillOval(19 * x + 260, 100 + (19 * y), 16, 16);
					graphics.setColor(Color.BLACK);
					graphics.drawOval(19 * x + 260, 100 + (19 * y), 16, 16);
				}
				j++;
			}
		}
		// if we are currently logging a column
		if (this.loggedColum != null) {
			reDraw();
			logSynapses(loggedColum);
			graphics.setColor(Color.BLUE);
			graphics.fillOval(19 * loggedColum.getxPos() + 5 + 260, 99 + 19 * loggedColum.getyPos() + 6, 6, 6);
		}

		repaint();
		
		System.out.println(spat.reconstructionQuality());
		// tempo.setActiveColumns(spat.getActiveColumns());
		// tempo.computeActiveState();
		// tempo.computeActiveState();

	}
}
