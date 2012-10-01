package hci;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


/**
 * Main class of the program - handles display of the main window
 * @author Michal
 *
 */
public class ImageLabeller extends JFrame {
	/**
	 * some java stuff to get rid of warnings
	 */
	 private static final long serialVersionUID = 1L;
	
	/**
	 * main window panel
	 */
	JPanel appPanel = null;
	
	/**
	 * toolbox - put all buttons and stuff here!
	 */
	JPanel toolboxPanel = null;
	
	/*contains the list of labels alongs with labls caption*/
	JPanel labelPanel=null;
	
	/* contains  list of labels*/
	 JPanel listOfLabels=null;
	/**
	 * image panel - displays image and editing area
	 */
	ImagePanel imagePanel = null;
	
	/**
	 * handles New Object button action
	 */
	public void addNewPolygon() {
		imagePanel.addNewPolygon();
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		imagePanel.paint(g); //update image panel
		
	}
	
	/**
	 * sets up application window and the interactive interface
	 * @param imageFilename image to be loaded for editing
	 * @throws Exception
	 */
	public void setupGUI(String imageFilename) throws Exception {
		this.addWindowListener(new WindowAdapter() {
		  	public void windowClosing(WindowEvent event) {
		  		//here we exit the program (maybe we should ask if the user really wants to do it?)
		  		//maybe we also want to store the polygons somewhere? and read them next time
		  		
		  		CloseApp_query.closeapp_query(); // asks whether the user really want to exit the app;
		  	}
		});

		//setup main window panel
		appPanel = new JPanel();
		this.setLayout(new BoxLayout(appPanel, BoxLayout.X_AXIS));
		this.setContentPane(appPanel);// this hold the images, image labels and the buttons
		
        //Create and set up the image panel.
		imagePanel = new ImagePanel(imageFilename);
		imagePanel.setOpaque(true); //content panes must be opaque
		
        appPanel.add(imagePanel);

        //create toolbox panel
        toolboxPanel = new JPanel();
        toolboxPanel.setLayout(new BoxLayout(toolboxPanel, BoxLayout.Y_AXIS));
        
        
        //create labelPanel
        labelPanel= new JPanel();
        labelPanel.setLayout(new BorderLayout());
        labelPanel.add(new JLabel("List of Labels"),BorderLayout.WEST);//add the 'label' caption
        
        listOfLabels = new JPanel();//holds all the annotations
        listOfLabels.setLayout(new BoxLayout(listOfLabels,BoxLayout.Y_AXIS));
        
        labelPanel.add(listOfLabels,BorderLayout.EAST);
        toolboxPanel.add(labelPanel);
        
        //Add button to the toolbox
		JButton newPolyButton = new JButton("Create new object");
		newPolyButton.setMnemonic(KeyEvent.VK_N);
		newPolyButton.setSize(50, 20);
		newPolyButton.setEnabled(true);
		newPolyButton.addActionListener(new ActionListener() {
			//@Override
			public void actionPerformed(ActionEvent e) {
				/*if the create button was pressed before the finish button , the existing polygon is removed */
				    if (imagePanel.get_currentPolygon()!=null){
				    	
				    	imagePanel.repaint();
				    	
				    }
				    
				if (getMouseListeners().length ==0){ 
				    addMouseListener(imagePanel);
				}
			    	imagePanel.createPolygon();
			}
		});
		newPolyButton.setToolTipText("Click to add new object");
		toolboxPanel.add(newPolyButton);
		
		
		JButton closeButton = new JButton("Finish");
		closeButton.setMnemonic(KeyEvent.VK_N);
		closeButton.setSize(50,20);
		closeButton.setEnabled(true);
		closeButton.addActionListener(new ActionListener() {
			//@Override
			public void actionPerformed(ActionEvent e) {
			    	addNewPolygon();
			    	
			    	// create a dialogue to ask the user for an annotation
			    	JFrame dialogue_frame = new JFrame();
			    	String label_msg = (String) JOptionPane.showInputDialog(dialogue_frame, "Please type in your prefered Label", "Annotator", JOptionPane.OK_OPTION, null, null, null);
			    	JLabel currentLabel =new JLabel(label_msg);
			    	currentLabel.setForeground(Color.red);
			    	listOfLabels.add(currentLabel);
			    	listOfLabels.revalidate();
			    	listOfLabels.repaint();
			    	
			    	
			}
		});
		closeButton.setToolTipText("click if you have finished segmenting");
		toolboxPanel.add(closeButton);
		
		
	
	
		//add toolbox to window
		appPanel.add(toolboxPanel);
		
		//display all the stuff
		//this.setSize(1100,700);
        this.pack();
		this.setVisible(true);
        System.out.println(this.getSize().getHeight() + " " +this.getSize().getWidth());
	}
	
	/**
	 * Runs the program
	 * @param argv path to an image
	 */
	public static void main(String argv[]) {
		try {
			//create a window and display the image
			ImageLabeller window = new ImageLabeller();
			window.setupGUI(argv[0]);
			window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		} catch (Exception e) {
			System.err.println("Image: " + argv[0]);
			e.printStackTrace();
		}
	}
}
