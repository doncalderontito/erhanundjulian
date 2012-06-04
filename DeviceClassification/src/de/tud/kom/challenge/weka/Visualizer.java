package de.tud.kom.challenge.weka;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import org.apache.log4j.Logger;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.gui.treevisualizer.PlaceNode2;
import weka.gui.treevisualizer.TreeVisualizer;

public class Visualizer {
	
	private final static Logger log = Logger.getLogger(Visualizer.class.getSimpleName());
	private static boolean show = false;
	
	public static void visualize(final Classifier input) {
		if(show) {
			return;
		}
		
		if(input instanceof J48) {
			try {
				final J48 tree = (J48) input;
				final JFrame jf = new JFrame("Weka Classifier Tree Visualizer: J48");
				jf.setSize(1100, 700);
				jf.getContentPane().setLayout(new BorderLayout());
				final TreeVisualizer tv = new TreeVisualizer(null, tree.graph(), new PlaceNode2());
				jf.getContentPane().add(tv, BorderLayout.CENTER);
				jf.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(final WindowEvent e) {
						jf.dispose();
					}
				});
				jf.setVisible(true);
				tv.fitToScreen();
			} catch(final Exception e) {
				Visualizer.log.warn("Could not visualize tree");
				Visualizer.log.warn(e);
				e.printStackTrace();
			}
		}
	}
	
}
