package com.numenta.view;

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
import java.util.logging.Logger;

import com.numenta.model.Column;
import com.numenta.model.Synapse;
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
	private Column[] columns;
	private SpatialPooler		spat				= new SpatialPooler();
	private TemporalPooler      tempo=new TemporalPooler();
	
	
	private TextField desiredLocalActivity = new TextField("3");
	private TextField connectedPermanance = new TextField("0.7");
	private TextField minimalOverlap = new TextField("4");
	private TextField permananceDec = new TextField("0.05");
	private TextField permananceInc = new TextField("0.05");
	private TextField amountOfSynapses = new TextField("10");
	private TextField inhibitionRadius = new TextField("5.0");
	private TextField boost = new TextField("1.0");

	private int loggedColomX=-1;

	private int loggedColomY=-1;

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

	public void reset(){

		graphics.clearRect(0, 0, 600, 600);
		for (int i = 0; i < input.length; i++) {
			input[i]=0;
		}
		columns=null;
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
		
		desiredLocalActivity.setName("desiredLocalActivity");
		connectedPermanance.setName("connectedPermanance");
		minimalOverlap.setName("minimalOverlap");
		permananceDec.setName("permananceDec");
		permananceInc.setName("permananceInc");
		amountOfSynapses.setName("amountOfSynapses");
		inhibitionRadius.setName("inhibitionRadius");
		
		Button reset=new Button("reset");
		reset.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (e.getActionCommand().equals("reset"))
				// logger.log(Level.INFO, "sparse");
					//createSparseDistributedRep();
					spat=new SpatialPooler(
							new Integer(desiredLocalActivity.getText()), 
							new Double(connectedPermanance.getText()),
							new Integer( minimalOverlap.getText()), 
							new Double(permananceDec.getText()), 
							new Double(permananceInc.getText()), 
							new Integer(amountOfSynapses.getText()), 
							new Double(inhibitionRadius.getText()));
					reset();
			}
		});
		add(new Label("dla"));
		add(desiredLocalActivity);
		add(new Label("cp"));
		add(connectedPermanance);
		add(new Label("mo"));
		add(minimalOverlap);
		add(new Label("pd"));
		add(permananceDec);
		add(new Label("pi"));
		add(permananceInc);
		add(new Label("as"));
		add(amountOfSynapses);
		add(new Label("ir"));
		add(inhibitionRadius);
		add(reset);
		
		//TODO create input for connectedPermananceMarge
		repaint();
	}

	public void paint(Graphics graphics) {
		graphics.drawImage(image, 0, 0, this);
	}

	private void mouseOver(int x, int y) {
		//TODO mouse over on a column will show it's synapses and display all infos
		int index=-1;
		outer: for (int yy = 0; yy < 12; yy++) {
			
			for (int xx = 0; xx < 12; xx++) {
				index++;
				if ( y < 19 * yy + 116 && y > 19 * yy + 100) {
					if(x > 19 * xx && x < 19 * xx + 16 ){				
						
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
					else{
						
						if (x > 19 * xx+ 260 && x < 19 * xx + 260+ 16) {
							System.out.println("mp "+mousePressed +" md "+mouseDragged);
							
							if(this.columns!=null ){
								logColumn(this.columns[index])	;
							}
							
							
							break outer;
						}
					}
				}
			}
		}
	}

	private void logColumn(Column column) {
		reDraw();
		if(!mousePressed){
			if(this.loggedColomX==column.getxPos() && this.loggedColomY==column.getyPos()){
				this.loggedColomX=-1;
				this.loggedColomY=-1;
				reDraw();
			}
			else{
				this.loggedColomX=column.getxPos();
				this.loggedColomY=column.getyPos();
				for (int i = 0; i < column.getPotentialSynapses().length; i++) {
					Synapse potentialSynapse=column.getPotentialSynapses()[i];
					System.out.println("potsyn "+potentialSynapse.getxPos()+" "+potentialSynapse.getyPos()+" "+potentialSynapse.getSourceInput()+" "+potentialSynapse.getPermanance());
					
					graphics.setColor(Color.RED);
		
					// graphics.setColor(Color.getHSBColor(10, 0.5f,0.5f));
					graphics.fillOval(19 * potentialSynapse.getxPos()+5, 100 + (19 * potentialSynapse.getyPos())+5, 6, 6);
					
				}
			}
			repaint();
		}
		if (mousePressed) {
			mousePressed = false;
		}
	}
	
	private void reDraw(){
		graphics.clearRect(0, 0, 260, 330);
		int j=0;
		for (int y = 0; y < 12; y++) {
			for (int x = 0; x < 12; x++) {
				graphics.setColor(Color.white);
				graphics.drawOval(19 * x, 100 + (19 * y), 16, 16);
				
				graphics.setColor(Color.black);
				if(input[j]==0){
					graphics.drawOval(19 * x, 100 + (19 * y), 16, 16);
				} else{
					graphics.fillOval(19 * x, 100 + (19 * y), 16, 16);
				}
				j++;
			}
			
		}
		repaint();
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

		this.columns = spat.getColumns();
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
					if (columns[j].getConnectedSynapses(spat.getConnectedPermanance()).length > conn) {
						color = color.darker();
					} else {
						if (columns[j].getConnectedSynapses(spat.getConnectedPermanance()).length < conn) {
							color = color.brighter();
						}
					}
					graphics.setColor(color);
					conn = columns[j].getConnectedSynapses(spat.getConnectedPermanance()).length;
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
