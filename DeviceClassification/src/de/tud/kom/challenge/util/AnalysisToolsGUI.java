package de.tud.kom.challenge.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.Border;
import org.apache.log4j.Logger;



/**
 * 
 * @author Daniel Burgstahler
 *
 */
public class AnalysisToolsGUI {
	
	private final static Logger log = Logger.getLogger(AnalysisToolsGUI.class.getSimpleName());
	
	private JFrame mainFrame = new JFrame("Device Classification Challenge - Tools");
	private JPanel myBody1;
	private JPanel myBody2;
	private JPanel myBody3;
	private JPanel myBody4;
	private JPanel myBody5;
	
	

	/**
	 * Constructor of the GUI
	 */
	public AnalysisToolsGUI(){

		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainFrame.setResizable(true);
		Dimension mSize = new Dimension(650, 500);
		mainFrame.setMinimumSize(mSize);
		mainFrame.setVisible(true);
		ImageIcon icon = new ImageIcon("resources/kom_logo_small2.png");
		mainFrame.setBackground(Color.white);
		Image titleIcon = icon.getImage();
		mainFrame.setIconImage(titleIcon);
		
		
		
		mainFrame.addWindowListener(new WindowListener() {			
			@Override
			public void windowOpened(WindowEvent e) {
			}			
			@Override
			public void windowIconified(WindowEvent e) {	
			}			
			@Override
			public void windowDeiconified(WindowEvent e) {
			}			
			@Override
			public void windowDeactivated(WindowEvent e) {	
			}			
			@Override
			public void windowClosing(WindowEvent e) {	
			}			
			@Override
			public void windowClosed(WindowEvent e) {
				System.exit(0);
			}			
			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
		
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBackground(Color.white);
		
		

		JComponent panel1 = makePanel1();
		DropTarget dt = new DropTarget(panel1, new DropTargetAdapter() {	
			@SuppressWarnings("unchecked")
			@Override
			public void drop(DropTargetDropEvent dtde) {
				if(dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
					dtde.acceptDrop(dtde.getDropAction());
					try{						
						List<File> list = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
						File f=null;
						if (list.size() > 0){
							f = list.get(0);
						}		
						if (f!=null && f.isFile()){
							paintSingleGraph(f);
						}
						
					} catch (UnsupportedFlavorException e) {
						log.error(e);
						e.printStackTrace();
					} catch (IOException e) {
						log.error(e);
						e.printStackTrace();
					}	
					catch (Exception e) {
						log.error(e);
						e.printStackTrace();
					}
				}				
			}
		});
		

		panel1.setDropTarget(dt);		
		tabbedPane.addTab("visualize file", icon, panel1,
		                  "visualizes a sampled file by using drag&drop");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

		
		JComponent panel2 = makePanel2();
		DropTarget dt2 = new DropTarget(panel2, new DropTargetAdapter() {			
			@SuppressWarnings("unchecked")
			@Override
			public void drop(DropTargetDropEvent dtde) {
				if(dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
					dtde.acceptDrop(dtde.getDropAction());
					try{
						List<File> f = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
						if (f!=null){
							File folder=f.get(0);
							if (folder.isDirectory()){
								paintGroupOfGraphs(Arrays.asList(folder.listFiles()));
							}else{
								paintGroupOfGraphs(f);	
							}
						}						
					} catch (UnsupportedFlavorException e) {
						log.error(e);
						e.printStackTrace();
					} catch (IOException e) {
						log.error(e);
						e.printStackTrace();
					}											
				}				
			}
		});
		panel2.setDropTarget(dt2);
		
		tabbedPane.addTab("group visualizer", icon, panel2,
		                  "visualizes a total folder of sampled files");
		tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
		
		JComponent panel3 = makePanel3();
		DropTarget dt3 = new DropTarget(panel3, new DropTargetAdapter() {			
			@SuppressWarnings("unchecked")
			@Override
			public void drop(DropTargetDropEvent dtde) {
				if(dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
					dtde.acceptDrop(dtde.getDropAction());
					try{
						
						List<File> f = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
						if (f!=null){
							File folder=f.get(0);
							if (folder.isDirectory()){
								ToolWorker.convertFiles(Arrays.asList(folder.listFiles()));
							}else{
								ToolWorker.convertFiles(f);	
							}
						}						
					} catch (UnsupportedFlavorException e) {
						log.error(e);
						e.printStackTrace();
					} catch (IOException e) {
						log.error(e);
						e.printStackTrace();
					}											
				}				
			}
		});
		panel3.setDropTarget(dt3);
		
		tabbedPane.addTab("split Files", icon, panel3,
		                  "split files by days if necessary");
		tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
		

		JComponent panel4 = makePanel4();
		DropTarget dt4 = new DropTarget(panel4, new DropTargetAdapter() {			
			@SuppressWarnings("unchecked")
			@Override
			public void drop(DropTargetDropEvent dtde) {
				if(dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
					dtde.acceptDrop(dtde.getDropAction());
					try{
						
						List<File> f = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
						if (f!=null){
							File folder=f.get(0);
							if (folder.isDirectory()){
								ToolWorker.copyPlainFiles(Arrays.asList(folder.listFiles()));
							}else{
								ToolWorker.copyPlainFiles(f);	
							}
						}						
					} catch (UnsupportedFlavorException e) {
						log.error(e);
						e.printStackTrace();
					} catch (IOException e) {
						log.error(e);
						e.printStackTrace();
					}											
				}				
			}
		});
		panel4.setDropTarget(dt4);		
		tabbedPane.addTab("plain Files", icon, panel4,
		                  "create plain files without time stamps");
		tabbedPane.setMnemonicAt(3, KeyEvent.VK_4);
		
		
		//JComponent 5
		JComponent panel5 = makePanel5();
		DropTarget dt5 = new DropTarget(panel5, new DropTargetAdapter() {	
			@SuppressWarnings("unchecked")
			@Override
			public void drop(DropTargetDropEvent dtde) {
				if(dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)){
					dtde.acceptDrop(dtde.getDropAction());
					try{
						
						List<File> f = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
						if (f!=null){
							File folder=f.get(0);
							System.out.println("bin drinnen "+folder.isDirectory());
							if (folder.isDirectory()){
								System.out.println("filefolder: "+folder.isDirectory());
								ToolWorker.fillUpMissingSamples(Arrays.asList(folder.listFiles()));
							}else{
								ToolWorker.fillUpMissingSamples(f);	
							}
						}					
					} catch (UnsupportedFlavorException e) {
						log.error(e);
						e.printStackTrace();
					} catch (IOException e) {
						log.error(e);
						e.printStackTrace();
					}											
				}				
			}
		});
		panel5.setDropTarget(dt5);		
		tabbedPane.addTab("fillUp Files", icon, panel5,
		                  "fill up missing samples at begin and end");
		tabbedPane.setMnemonicAt(4, KeyEvent.VK_5);
		
		mainFrame.getContentPane().add(tabbedPane);
		mainFrame.validate();
		mainFrame.pack();
		mainFrame.repaint();
		
	}

	

	
	  protected JComponent makePanel1() {
		    JPanel mainPanel = new JPanel(false);
		    myBody1 = new JPanel();		    
		    JLabel infoTextLabel = new JLabel("<html><body>drop your file here<br><br>get a detailed view of your files data</body></html>");	
		    return makePanel(myBody1, mainPanel, infoTextLabel);
		  }
	  	  
	  protected JComponent makePanel2() {
		    JPanel mainPanel = new JPanel(false);
		    myBody2 = new JPanel();		    
		    JLabel infoTextLabel = new JLabel("<html><body>drop your files here<br><br>get an overview visualization of all your files</body></html>");	
		    return makePanel(myBody2, mainPanel, infoTextLabel);
		  }
	  	  
	  protected JComponent makePanel3() {
		    JPanel mainPanel = new JPanel(false);
		    myBody3 = new JPanel();		    
		    JLabel infoTextLabel = new JLabel("<html><body>drop your files here<br><br>you will receive a cleaned copy in a new subfolder</body></html>");	
		    return makePanel(myBody3, mainPanel, infoTextLabel);
		  }
	  
	  protected JComponent makePanel4() {
		    JPanel mainPanel = new JPanel(false);
		    myBody4 = new JPanel();		    
		    JLabel infoTextLabel = new JLabel("<html><body>drop your files here<br><br>you will receive a plain copy, including only the values, no time stamps</body></html>");	
		    return makePanel(myBody4, mainPanel, infoTextLabel);
		  }
	  
	  protected JComponent makePanel5() {
		    JPanel mainPanel = new JPanel(false);
		    myBody5 = new JPanel();		    
		    JLabel infoTextLabel = new JLabel("<html><body>drop your files here<br><br>you will receive files with filled up zero samples at begin and end</body></html>");	
		    return makePanel(myBody5, mainPanel, infoTextLabel);
		  }
	  
	  
	  
	  protected JComponent makePanel(JPanel body,JPanel mainPanel,JLabel infoTextLabel) {	
		    infoTextLabel.validate();
		    infoTextLabel.setHorizontalAlignment(JLabel.CENTER);
		    
		    body.add(infoTextLabel);		 
		    body.setBackground(Color.white);
		    body.setAutoscrolls(true);		    
		    body.setBackground(new Color(232,232,255));
		    body.setLayout(new GridLayout(0, 1, 30, 30));
			
		    mainPanel.setBackground(Color.white);
		    mainPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();

			c.anchor = GridBagConstraints.PAGE_START;
			
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1;
			c.gridx = 0;
			c.gridy = 0;
			mainPanel.add(addStaticComponentsTop(mainPanel),c);	
		    //addStaticComponentsTop(mainPanel,c);
			c.fill = GridBagConstraints.BOTH;
			c.gridx = 0;
			c.gridy = 1;
			c.weighty = 1.0;   //request any extra vertical space
			
			JScrollPane sp = new JScrollPane(body);
			sp.setAutoscrolls(true);
			mainPanel.add(sp,c);
			
		    return mainPanel;
		  }
	  	
	  
	  
	  //Static window contents
	  private JPanel addStaticComponentsTop(JPanel mainPanel){
		  JPanel topPanel = new JPanel(new BorderLayout());
		  topPanel.setOpaque(true);
		  topPanel.setBackground(new Color(45,45,45));			// a dark grey panel element
		  topPanel.setPreferredSize(new Dimension(500,75));
		  LayoutManager myGrid = new GridLayout(1, 2);
		  topPanel.setLayout(myGrid);
		  
		  
		  JLabel labelTitle = new JLabel("KOM - Multimedia Communications Lab");									// the title text in a label element
		  labelTitle.setFont(new Font("Arial", Font.BOLD + Font.ITALIC, 12)); 	// with light grey bold font
		  labelTitle.setForeground(new Color(195,195,195));		
		  labelTitle.setOpaque(true);
		  labelTitle.setBackground( new Color(45,45,45));
		  Border paddingBorder = BorderFactory.createEmptyBorder(10,10,10,10);	// padding around title text
		  labelTitle.setBorder(BorderFactory.createCompoundBorder(null,paddingBorder));
		  topPanel.add(labelTitle);
		  

		  JLabel topLabelRight =  new JLabel();
		  topLabelRight.setBorder(BorderFactory.createCompoundBorder(null,paddingBorder));
		  topLabelRight.setHorizontalAlignment(SwingConstants.RIGHT);
		  topLabelRight.setIcon(new ImageIcon("resources/athene_kom_50w.png"));
		  
		  topPanel.add(topLabelRight);

		  return topPanel;
  	  }
	  

	  
	  private void paintSingleGraph(File f){

		  	Container c = myBody1.getParent();
		  	c.remove(myBody1);
		  	myBody1.removeAll();
		  	myBody1 = null;
		  	Runtime.getRuntime().gc();
		  	
		  	myBody1 = new JPanel();
		    myBody1.setBackground(Color.white);
		    myBody1.setAutoscrolls(true);		    
		    myBody1.setBackground(new Color(232,232,255));
		    c.add(myBody1);


		  Image img = ImageGenerator.drawDetailedGraph(f);
		  JLabel picLabel = new JLabel(new ImageIcon( img ));  
		  myBody1.add(picLabel);
		  myBody1.validate();
		  myBody1.repaint();
		  
		  mainFrame.validate();
		  mainFrame.pack();
		  mainFrame.repaint();
	  }
	  
	  private void paintGroupOfGraphs(List<File> f){

		  	Container c = myBody2.getParent();
		  	c.remove(myBody2);
		  	myBody2.removeAll();
		  	myBody2 = null;
		  	Runtime.getRuntime().gc();
		  	
		  	myBody2 = new JPanel();
		    myBody2.setBackground(Color.white);
		    myBody2.setAutoscrolls(true);		    
		    myBody2.setBackground(new Color(232,232,255));
		    myBody2.setLayout(new GridLayout(0, 1, 30, 30));
		    c.add(myBody2);


		    ListIterator<File> it = f.listIterator();
		    while( it.hasNext() ){
		    	Image img = ImageGenerator.drawSmallGraph(it.next());
				JLabel picLabel = new JLabel(new ImageIcon( img ));  
				myBody2.add(picLabel);
		    }

		  myBody2.validate();
		  myBody2.repaint();
		  
		  mainFrame.validate();
		  mainFrame.pack();
		  mainFrame.repaint();
	  }
	  
}
