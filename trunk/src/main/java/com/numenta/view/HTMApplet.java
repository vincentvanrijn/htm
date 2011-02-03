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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.numenta.model.Column;
import com.numenta.pooler.SpatialPooler;

public class HTMApplet extends Applet {

	private boolean				mouseDragged		= false;

	private boolean				mousePressed		= false;

	private boolean				black				= true;

	private int[]			input				= new int[144];

	private static final long	serialVersionUID	= 1L;

	private Graphics			graphics;
	private Logger logger=Logger.getLogger(this.getClass().getName());
	private Image				image;
	private SpatialPooler spat=new SpatialPooler();

	public void init() {
		SpatialPooler spat=new SpatialPooler();
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
			}
		}
		Button submitButton=new Button("sparseDist");
		submitButton.setActionCommand("sparse");
		submitButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("sparse"))
						//logger.log(Level.INFO, "sparse");
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

		outer: for (int xx = 0; xx < 12; xx++) {
			for (int yy = 0; yy < 12; yy++) {
				if (x > 19 * xx && x < 19 * xx + 16 && y < 19 * yy + 116 && y > 19 * yy + 100) {
					if (mousePressed) {
						if (input[xx * yy]==1) {
							black = false;
						} else {
							black = true;
						}
						mousePressed = false;
					} else {
						if (mouseDragged) {

							if (black) {
								drawBlackOval(xx, yy);
							} else {
								drawWhiteOval(xx, yy);
							}
						} else {

							if (input[xx * yy]==1) {
								drawWhiteOval(xx, yy);
							} else {
								drawBlackOval(xx, yy);
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
		graphics.fillOval(19 * x, 100 + (19 * y), 16, 16);

		input[x * y] = 1;
		repaint();
	}

	private void drawWhiteOval(int x, int y) {
		graphics.setColor(Color.white);
		graphics.fillOval(19 * x, 100 + (19 * y), 16, 16);
		graphics.setColor(Color.black);
		graphics.drawOval(19 * x, 100 + (19 * y), 16, 16);
		input[x * y] = 0;
		repaint();
	}
	public void createSparseDistributedRep() {
		
		spat.conectSynapsesToInputSpace(input);
		spat.computOverlap();
		spat.computeWinningColumsAfterInhibition();
		spat.updateSynapses();
		logger.log(Level.INFO, ""+spat.activeColumns.size());
		logger.log(Level.INFO, "end");
		
		Column[] columns=spat.getColumns();
		int j=0;
		graphics.setColor(Color.WHITE);
		graphics.fillRect(250, 0,270, 340);
		graphics.setColor(Color.BLACK);
		for (int x = 0; x < 12; x++) {
			for (int y = 0; y < 12; y++) {
				if(columns[j].isActive()){
					graphics.fillOval(19 * x+260, 100 + (19 * y), 16, 16);
				} else{
					graphics.drawOval(19 * x+260, 100 + (19 * y), 16, 16);
				}
				j++;
			}
			
		}
		repaint();		
	}
}
