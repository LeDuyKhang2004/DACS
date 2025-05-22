package FLB;

import java.awt.CardLayout;

import javax.swing.JButton;
import javax.swing.JFrame;

public class App {
	    public static void main(String[] args) {
	    JFrame frame = new JFrame("Flappy Bird");
	    frame.setSize(360, 640);
	    frame.setLocationRelativeTo(null);
	    frame.setResizable(false);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    

	    JButton bt = new JButton();
	    bt.setBounds(10, 20, 30, 50);
	    FlappyBird flb = new FlappyBird();
	    
	    frame.add(bt);
	    frame.add(flb);
	    frame.pack();
	    frame.setVisible(true);
	    
	    flb.requestFocus();
	    }
	}
