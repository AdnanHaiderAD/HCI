package orig;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Main class of the program - handles display of the main window
 * @author Michal
 *
 */
public class ImageLabeller extends JFrame {

	private static final long serialVersionUID = 1L;
	
	JPanel appPanel = null;
	
	JPanel toolboxPanel = null;
	
	ImagePanel imagePanel = null;
	
	public void addNewPolygon() {
		imagePanel.addNewPolygon();
	}
	
	//@Override
	public void paint(Graphics g) {
		super.paint(g);
		imagePanel.paint(imagePanel.getGraphics()); //update image panel
	}
	
	/**
	 * sets up application window
	 * @param imageFilename image to be loaded for editing
	 * @throws Exception
	 */
	public void setupGUI(String imageFilename) throws Exception {

		//create the menu bar
		JMenuBar menubar = new JMenuBar();
		menubar.setOpaque(true);
		//file menu
		JMenu filemenu = new JMenu("File");
		menubar.add(filemenu);
		
		//edit menu
		JMenu editmenu = new JMenu("Edit");
		menubar.add(editmenu);
		
		//add menubar to frame
		this.setJMenuBar(menubar);
		JMenuItem item = new JMenuItem("bojhuo");
		filemenu.add(item);
		
		//create the action listeners for the menu items
		
				//setup main window panel
		appPanel = new JPanel();
		this.setLayout(new BoxLayout(appPanel, BoxLayout.X_AXIS));
		this.setContentPane(appPanel);
		
        //Create and set up the image panel.
		imagePanel = new ImagePanel(imageFilename);
		imagePanel.setOpaque(true); //content panes must be opaque
		
        appPanel.add(imagePanel);

        //create toolbox panel
        toolboxPanel = new JPanel();
        
        //Add button
		JButton newPolyButton = new JButton("New object");
		newPolyButton.setMnemonic(KeyEvent.VK_N);
		newPolyButton.setSize(50, 20);
		newPolyButton.setEnabled(true);
		newPolyButton.addActionListener(new ActionListener() {
			//@Override
			public void actionPerformed(ActionEvent e) {
			    	addNewPolygon();
			}
		});
		newPolyButton.setToolTipText("Click to add new object");
		
		toolboxPanel.add(newPolyButton);
		
		//add toolbox to window
		appPanel.add(toolboxPanel);
		
		//display all the stuff
		this.pack();
        this.setVisible(true);
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
		} catch (Exception e) {
			System.err.println("Image: " + argv[0]);
			e.printStackTrace();
		}
	}
}
