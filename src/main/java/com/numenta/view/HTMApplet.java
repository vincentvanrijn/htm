package com.numenta.view;

import java.applet.Applet;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class HTMApplet extends Applet {

	private boolean mouseDragged = false;
	private boolean mousePressed = false;
	private boolean black = true;
	private boolean[] input = new boolean[144];
	private static final long serialVersionUID = 1L;

	private Graphics graphics;

	private Image image;

	public void init() {

		for (int i = 0; i < input.length; i++) {
			input[i] = false;
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
		repaint();
	}

	public void paint(Graphics graphics) {
		graphics.drawImage(image, 0, 0, this);
	}
	
	private void mouseOver(int x, int y) {

		outer: for (int xx = 0; xx < 12; xx++) {
			for (int yy = 0; yy < 12; yy++) {
				if (x > 19 * xx && x < 19 * xx + 16 && y < 19 * yy + 116
						&& y > 19 * yy + 100) {
					if (mousePressed) {
						if (input[xx * yy]) {
							black = false;
						} else {
							black = true;
						}
						mousePressed=false;
					} else {
						if (mouseDragged) {

							if (black) {
								drawBlackOval(xx, yy);
							} else {
								drawWhiteOval(xx, yy);
							}
						} else {

							if (input[xx * yy]) {
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

		input[x * y] = true;
		repaint();
	}

	private void drawWhiteOval(int x, int y) {
		graphics.setColor(Color.white);
		graphics.fillOval(19 * x, 100 + (19 * y), 16, 16);
		graphics.setColor(Color.black);
		graphics.drawOval(19 * x, 100 + (19 * y), 16, 16);
		input[x * y] = false;
		repaint();
	}
}
