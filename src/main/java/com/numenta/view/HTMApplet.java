package com.numenta.view;

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
import java.util.ArrayList;
import java.util.logging.Logger;

import com.numenta.model.Column;
import com.numenta.pooler.SpatialPooler;
import com.numenta.pooler.TemporalPooler;

public class HTMApplet extends Applet {

	private boolean				mouseDragged		= false;

	private boolean				mousePressed		= false;

	private boolean				black				= true;

	private int[]				input				= new int[144];

	private static final long	serialVersionUID	= 1L;

	private Graphics			graphics;

	private Logger				logger				= Logger.getLogger(this.getClass().getName());

	private Image				image;

	private SpatialPooler		spat				= new SpatialPooler();
	private TemporalPooler      tempo=new TemporalPooler();

	public void init() {
		SpatialPooler spat = new SpatialPooler();
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

	public void draw() {

		graphics.setColor(Color.BLACK);
		for (int x = 0; x < 12; x++) {
			for (int y = 0; y < 12; y++) {
				graphics.drawOval(19 * x, 100 + (19 * y), 16, 16);
				graphics.drawOval(19 * x + 260, 100 + (19 * y), 16, 16);
			}
		}
		Button submitButton = new Button("sparseDist");
		submitButton.setActionCommand("sparse");
		submitButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("sparse"))
				// logger.log(Level.INFO, "sparse");
					createSparseDistributedRep();
			}

		});
		add(submitButton);
		repaint();
	}

	public void paint(Graphics graphics) {
		graphics.drawImage(image, 0, 0, this);
	}

	private void mouseOver(int x, int y) {
		int index=-1;
		outer: for (int yy = 0; yy < 12; yy++) {
			
			for (int xx = 0; xx < 12; xx++) {
				index++;
				if (x > 19 * xx && x < 19 * xx + 16 && y < 19 * yy + 116 && y > 19 * yy + 100) {
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
					}
					break outer;
				}

			}
		}
	}

	private void drawBlackOval(int x, int y) {
		graphics.setColor(Color.black);

		// graphics.setColor(Color.getHSBColor(10, 0.5f,0.5f));
		graphics.fillOval(19 * x, 100 + (19 * y), 16, 16);

		
		repaint();
	}
	private void setInputValue(int index, int value){
		input[index]=value;
	}

	private void drawWhiteOval(int x, int y) {
		graphics.setColor(Color.white);
		graphics.fillOval(19 * x, 100 + (19 * y), 16, 16);
		graphics.setColor(Color.black);
		graphics.drawOval(19 * x, 100 + (19 * y), 16, 16);
		repaint();
	}

	public void createSparseDistributedRep() {

		spat.conectSynapsesToInputSpace(input);
		spat.computOverlap();
		spat.computeWinningColumsAfterInhibition();
		spat.updateSynapses();
		// logger.log(Level.INFO, ""+spat.activeColumns.size());
		// logger.log(Level.INFO, "end");

		Column[] columns = spat.getColumns();
		//ArrayList<Column> active= spat.getActiveColumns();
		
		int j = 0;
		graphics.setColor(Color.WHITE);
		graphics.fillRect(250, 0, 270, 340);
		Color color = Color.red;
		graphics.setColor(color);
		double conn = 0;
		for (int x = 0; x < 12; x++) {
			for (int y = 0; y < 12; y++) {
				if (columns[j].isActive()) {
					if (columns[j].getConnectedSynapses().length > conn) {
						color = color.darker();
					} else {
						if (columns[j].getConnectedSynapses().length < conn) {
							color = color.brighter();
						}
					}
					graphics.setColor(color);
					conn = columns[j].getConnectedSynapses().length;
					graphics.fillOval(19 * x + 260, 100 + (19 * y), 16, 16);
				} else {
					graphics.setColor(Color.BLACK);
					graphics.drawOval(19 * x + 260, 100 + (19 * y), 16, 16);
				}
				j++;
			}
		}

		repaint();
		tempo.setActiveColumns(spat.getActiveColumns());
		tempo.computeActiveState();
		tempo.computeActiveState();
	}

}
