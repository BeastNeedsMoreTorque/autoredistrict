package new_metrics;

import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

import org.apache.commons.math3.distribution.BetaDistribution;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

public class FrameDraw extends JFrame {

	PiePanel panel = null;
	
	public FrameDraw() {
		super();
		initComponents();
	}

	private void initComponents() {
		getContentPane().setLayout(null);
		setSize(500,500);
		setTitle("Draw");
		
		panel = new PiePanel();
		panel.setBounds(0, 0, 1000, 1000);
		getContentPane().add(panel);
	}
	
	BetaDistribution dist = null;
	Vector<BetaDistribution> dists = null;
	double[] seats = null;
	
	class PiePanel extends JPanel {
		
	    public void paintComponent(Graphics graphics0) {
	    	try {

	            Graphics2D graphics = (Graphics2D)graphics0;
		        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		        graphics.setRenderingHint(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY);

			    super.paintComponent(graphics);
			    
			    graphics.setColor(Color.white);
			    graphics.fillRect(0, 0, 1000, 1000);

			    int xs = 10;
			    int ys = 40;
			    int w = 100;
			    int h = 100;
			    

			    graphics.setColor(Color.gray);
			    
			    if( seats != null) {
			    	int length = (int)(400/seats.length);
			    	int x = 50;
			    	for( int i = 0; i < seats.length; i++) {
			    		int height = (int)(seats[i]*500);
			    		if( x < 250) {
						    graphics.setColor(Color.blue);
						    if( x+length > 250) {
						    	graphics.setColor(Color.gray);
						    }
			    			
			    		} else {
						    graphics.setColor(Color.red);

			    		}
			    		graphics.fillRect(x, 400-height, length, height);
			    		String s = ""+i+" rep";
			    		int textx = x + length/2 - graphics.getFontMetrics().stringWidth(s)/2;
			    		graphics.drawString(s, textx, 420);
			    		x += length;
			    	}
			    }
 			    
			    if( dists != null) {
				    double inc = 0.001;
			    	for( BetaDistribution dist : dists) {
				    	double x = 0;
				    	double last_x = 0;
				    	double last_y = 0;
				    	while(x <= 1) {
				    		double y = dist.density(x);
				    		if( x > 0.5) {
							    graphics.setColor(Color.blue);
				    			
				    		} else {
							    graphics.setColor(Color.red);

				    		}
				    		
				    		graphics.drawLine(transformx(last_x),transformy(last_y),transformx(x),transformy(y));
				    		
				    		last_y = y;
				    		last_x = x;
				    		x += inc;
				    	}
			    	}
				    graphics.drawString("Popular vote", 220, 420);
					
				    graphics.setColor(Color.blue);
					graphics.drawString("Democratic seats", 10, 420);
					
				    graphics.setColor(Color.red);
					graphics.drawString("Republican seats", 380, 420);
			    }
			    graphics.setColor(Color.black);
			    
			    if( dist != null) {
				    double inc = 0.001;
			    	double x = 0;
			    	double last_x = 0;
			    	double last_y = 0;
			    	while(x <= 1) {
			    		double y = dist.density(x);
			    		
			    		graphics.drawLine(transformx(last_x),transformy(last_y),transformx(x),transformy(y));
			    		
			    		last_y = y;
			    		last_x = x;
			    		x += inc;
			    	}
				    graphics.drawString("Popular vote", 220, 420);
					
				    graphics.setColor(Color.blue);
					graphics.drawString("Democratic seats", 10, 420);
					
				    graphics.setColor(Color.red);
					graphics.drawString("Republican seats", 380, 420);
			    }
				
						    

			    /*
			    graphics.drawImage(MainFrame.mainframe.panelStats.pie_eth_descr,0,0,100,100,null);
			    graphics.drawImage(MainFrame.mainframe.panelStats.pie_eth_target,0,0,100,100,null);
			    graphics.drawImage(MainFrame.mainframe.panelStats.pie_eth_descr,0,0,100,100,null);
			    graphics.drawImage(MainFrame.mainframe.panelStats.pie_eth_descr,0,0,100,100,null);
			    graphics.drawImage(MainFrame.mainframe.panelStats.pie_eth_packingm_byvoter,0,0,100,100,null);
			    graphics.drawImage(MainFrame.mainframe.panelStats.pie_party_packingm_byvoter,0,0,100,100,null);
			    */
			    //graphics.drawImage(MainFrame.mainframe.panelStats.pie_eth_power,100,0,100,100,null);

	
	    	} catch (Exception ex) {
	    		System.out.println("ex csafs "+ex);
	    		ex.printStackTrace();
	    		
	    	}
	    	
	    }
    	int transformx(double x) {
    		return (int)Math.round(500-x*500);
    	}
    	int transformy(double y) {
    		return (int)Math.round(-y*5+400);
    	}
}

}
