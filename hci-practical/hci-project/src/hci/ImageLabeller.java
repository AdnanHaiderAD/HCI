package hci;

import static java.awt.event.InputEvent.CTRL_DOWN_MASK;
import hci.utils.Point;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;


/**
 * Main class of the program - handles display of the main window
 * 
 * @author Michal
 * 
 */
public class ImageLabeller extends JFrame {

	private static final long serialVersionUID = 1L;

	JPanel appPanel = null; 	//main window panel
	JPanel toolboxPanel = null;	//toolbox for editing
	
	JSlider zoom; 
	
	/* containers to hold and display labels created by the user */
	JInternalFrame  labelFrame=null;
	JList LabelPanel = null;
	JPanel optionsPanel =null;
	
	//editor panel - tools for editing
	JInternalFrame editFrame= null;
	JPanel editPanel = null;
	 
	//image panel - displays the image
	 JPanel leftPanel =  null;
	 ImagePanel imagePanel = null;
	 
	 /* handles the Edit button  and its event corresponding to the  internal frame  */
	 JButton Edit = new JButton("Edit");
	 JButton Remove = new JButton("Remove");
	 JButton New = new JButton("New");
 
	 JButton addP;
	 JButton remP;
	 JButton adjP;
	 JButton save = new JButton ("Save");
	 JButton cancel = new JButton("Cancel");
	 JTextField name = new JTextField();
	 
	 JButton status;
	 
	 /*create the undo and redo action objects*/
	 UndoAction undo = new UndoAction("Undo","Undo previous step",new Integer(KeyEvent.VK_3));
	 RedoAction redo = new RedoAction("Redo","Redo next step",new Integer(KeyEvent.VK_4));
	
	 double prev_zoom = 100;
	 
	public void paint(Graphics g) {
		super.paint(g);
		imagePanel.repaint(); // update image pane
	}

	/**
	 * sets up application window and the interactive interface
	 * @param imageFilename -  image to be loaded for editing
	 * @throws Exception
	 */
	public void setupGUI() throws Exception {
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				// asks whether the user really want to exit the app;
				String[] options = {"Yes","No"};
				int rep = JOptionPane.showOptionDialog(appPanel, "Do you really wish to quit?", "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
				if (rep == JOptionPane.YES_OPTION) System.exit(0);
			}
		});

		//create the menu bar
		JMenuBar menubar = new JMenuBar();
		
		//file menu
		JMenu filemenu = new JMenu("File");
		filemenu.setMnemonic(KeyEvent.VK_1);
		menubar.add(filemenu);
		
		JMenuItem fileItem1 = new JMenuItem("Load New Image");
		JMenuItem fileItem2 = new JMenuItem("Open Existing Project");
		JMenuItem fileItem3 = new JMenuItem("Save Project");
		JMenuItem fileItem4 = new JMenuItem("Close");
		filemenu.add(fileItem1);
		filemenu.add(new JSeparator());
		filemenu.add(fileItem2);
		filemenu.add(fileItem3);
		filemenu.add(new JSeparator());
		filemenu.add(fileItem4);
		fileItem1.setAccelerator(KeyStroke.getKeyStroke('N', CTRL_DOWN_MASK));
		fileItem2.setAccelerator(KeyStroke.getKeyStroke('O', CTRL_DOWN_MASK));
		fileItem3.setAccelerator(KeyStroke.getKeyStroke('S', CTRL_DOWN_MASK));
		fileItem4.setAccelerator(KeyStroke.getKeyStroke('Q', CTRL_DOWN_MASK));
		
		//edit menu
		JMenu editmenu = new JMenu("Edit");
		editmenu.setMnemonic(KeyEvent.VK_2);
		menubar.add(editmenu);
		
		JMenuItem editItem1 = new JMenuItem();
		editItem1.setAction(undo);
		editItem1.setAccelerator(KeyStroke.getKeyStroke('Z', CTRL_DOWN_MASK));
		JMenuItem editItem2 = new JMenuItem();
		editItem2.setAction(redo);
		editItem2.setAccelerator(KeyStroke.getKeyStroke('Y', CTRL_DOWN_MASK));
		JMenuItem editItem3 = new JMenuItem("Zoom in");
		editItem3.setAccelerator(KeyStroke.getKeyStroke('=', CTRL_DOWN_MASK));
		JMenuItem editItem4 = new JMenuItem("Zoom out");
		editItem4.setAccelerator(KeyStroke.getKeyStroke('-', CTRL_DOWN_MASK));
		
		editmenu.add(editItem1);
		editmenu.add(editItem2);
		editmenu.add(new JSeparator());
		editmenu.add(editItem3);
		editmenu.add(editItem4);
		
		//add menubar to frame
		setJMenuBar(menubar);
		
		
		//create the action listeners for the menu items
		
		//open file
		fileItem1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				CustomFileFilter filter = new CustomFileFilter("Images(.jpg, .png)");
				filter.addExtension("jpg");
				filter.addExtension("png");
				
			    chooser.setFileFilter(filter);
				int rVal = chooser.showOpenDialog(imagePanel);
				if(rVal == JFileChooser.APPROVE_OPTION) {
					try {
						imagePanel.changePicture(chooser.getSelectedFile().getAbsolutePath());
			            labelFrame.revalidate();
			            labelFrame.repaint();
					} catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(null,
								"The file you selected cannot be opened",
								"Error opening file",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		
		//open project
		fileItem2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				CustomFileFilter filter = new CustomFileFilter("HCI projects (.ser)");
				filter.addExtension("ser");
			    chooser.setFileFilter(filter);
				int rVal = chooser.showOpenDialog(imagePanel);
				if(rVal == JFileChooser.APPROVE_OPTION) {
					try {
			            FileInputStream fileIn = new FileInputStream(chooser.getSelectedFile());
			            ObjectInputStream in = new ObjectInputStream(fileIn);
			            imagePanel.loadProject((SerializableImage) in.readObject(), (Hashtable<String, ArrayList<Point>>) in.readObject(), (ArrayList<Point>) in.readObject());
			            in.close();
			            fileIn.close();
			            
			            //remove everything in the label panel
			            LabelPanel.removeAll();
			            
			            //put the new JLabels
			            ArrayList<String> list = Collections.list(imagePanel.getPolygonTable().keys());
				    	LabelPanel.setListData(list.toArray(new String[list.size()]));
			            
			        } catch (Exception e2) {
						e2.printStackTrace();
						JOptionPane.showMessageDialog(null,	"The project you selected cannot be opened", "Error opening project", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		
		//save
		fileItem3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				CustomFileFilter filter = new CustomFileFilter("HCI projects (.ser)");
				filter.addExtension("ser");
			    chooser.setFileFilter(filter);
				int rVal = chooser.showSaveDialog(imagePanel);
				if(rVal == JFileChooser.APPROVE_OPTION) {
					try
				      {
						 File selectedFile = chooser.getSelectedFile();
						 if (!getExtension(selectedFile.getName()).equals("ser")) {
							 String filename = selectedFile.getAbsolutePath();;
							 selectedFile = new File(filename.concat(".ser"));
						 }
				         FileOutputStream fileOut = new FileOutputStream(selectedFile);
				         ObjectOutputStream out = new ObjectOutputStream(fileOut);
				         out.writeObject(imagePanel.getSerializableImage());
				         out.writeObject(imagePanel.getPolygonTable());
				         out.writeObject(imagePanel.getCurrentPolygon());
				         out.close();
				         fileOut.close();
				      }catch(IOException e3) {
				          e3.printStackTrace();
				      }
				}
			}
		});

		//close
		fileItem4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] options = {"Yes","No"};
				int rep = JOptionPane.showOptionDialog(appPanel, "Do you really wish to quit?", "Confirm Exit", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
				if (rep == JOptionPane.YES_OPTION) System.exit(0);
			}
		});
		
		editItem3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				imagePanel.scale((prev_zoom/(double)100)*1.1, prev_zoom/ (double) 100);
				prev_zoom *= 1.1;
			}
		});
		
		editItem4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				imagePanel.scale((prev_zoom/(double)100)*1/1.1, prev_zoom / (double) 100);
				prev_zoom *= 1/1.1;
			}
		});
		
		// setup main window panel
		appPanel = new JPanel();
		this.setLayout(new BoxLayout(appPanel, BoxLayout.X_AXIS));
		this.setContentPane(appPanel);// this hold the images, image labels and the buttons
	
		leftPanel = new JPanel();
		GridBagLayout g = new GridBagLayout();
		leftPanel.setLayout(g);
		GridBagConstraints c = new GridBagConstraints();
		
		// Create and set up the image panel.
		imagePanel = new ImagePanel();
		imagePanel.setOpaque(true); //content panes must be opaque
        JScrollPane imageScroller = new JScrollPane(imagePanel);
        imageScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        imageScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        status = new JButton(" Click  New to create a new annotated object");
        //status.setEnabled(false);
        status.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        status.setHorizontalAlignment(SwingConstants.LEFT);
        status.setContentAreaFilled(false);
        status.setSize(new Dimension(800, 20));
        status.setPreferredSize(new Dimension(800, 20));
        status.setMinimumSize(new Dimension(800, 20));
        status.setMaximumSize(new Dimension(800, 20));
        status.setVisible(true);
        
        c.weightx = 1;
        c.gridwidth = GridBagConstraints.REMAINDER;
        g.setConstraints(imageScroller, c);
        leftPanel.add(imageScroller);
        g.setConstraints(status, c);
		leftPanel.add(status);
        
        //create toolbox panel
        toolboxPanel = new JPanel();
        toolboxPanel.setLayout(new BoxLayout(toolboxPanel, BoxLayout.Y_AXIS));
        
        //create internal frame to hold the list of labels
        labelFrame = new JInternalFrame("List of Labels", false,false,false);
        labelFrame.setOpaque(true);
		labelFrame.setPreferredSize(new Dimension(toolboxPanel.getWidth(), 200));
		labelFrame.setVisible(true);
       
        LabelPanel = new JList();// this ensures the new labels are stacked vertically.
        LabelPanel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
		JScrollPane scroller = new JScrollPane(LabelPanel);
        scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        labelFrame.getContentPane().add(scroller);
        
        LabelPanel.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				if( event.getSource() == LabelPanel	&& !event.getValueIsAdjusting() ) {
					if (LabelPanel.getSelectedValue()==null) {
						Edit.setEnabled(false);
						Remove.setEnabled(false);
						imagePanel.setSelectedPolygon(null);
						return;
					}
					Edit.setEnabled(true);
					Remove.setEnabled(true);
					imagePanel.setSelectedPolygon((String)LabelPanel.getSelectedValue());
					imagePanel.repaint();
				}
			}
        });
        
        optionsPanel = new JPanel();//this contains the edit and remove buttons
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.X_AXIS));
        labelFrame.getContentPane().add(optionsPanel,BorderLayout.SOUTH);
        Edit.setEnabled(false);
        Edit.setMnemonic(KeyEvent.VK_E);
        Edit.setToolTipText("Click to edit the selected annotation object");
        Remove.setEnabled(false);
        Remove.setMnemonic(KeyEvent.VK_DELETE);
        Remove.setToolTipText("Click to remove the selected annotation object");
        New.setEnabled(true);
        New.setMnemonic(KeyEvent.VK_N);
		New.setToolTipText("Click to add new annotation object");
		optionsPanel.add(New);
		optionsPanel.add(Edit);
		optionsPanel.add(Remove);
        
		New.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
						if (imagePanel.getCurrentPolygon()!= null && imagePanel.getCurrentPolygon().size()>0) {
							JOptionPane.showMessageDialog(appPanel, "You have not saved your last object. Please Save or Cancel your current object before working on another one.");
							return;
						}
					imagePanel.stopPointEditing();
					imagePanel.addpoint = true;
					imagePanel.startNewPolygon();
					imagePanel.revalidate();
					imagePanel.repaint();
					
					remP.setSelected(false);
					addP.setSelected(true);
					adjP.setSelected(false);
					
					editFrame.setVisible(true);
					status.setText( " Click on the image to add a point to the current object.");
				}
	        });
		
        //add listener to the edit button
        Edit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (LabelPanel.getSelectedValue() == null) return;
				if (imagePanel.getCurrentPolygon()!= null && imagePanel.getCurrentPolygon().size()>0) {
						JOptionPane.showMessageDialog(appPanel, "You have not saved your last object. Please Save or Cancel your current object before working on another one.");
						return;
				}
				
				imagePanel.stopPointEditing();
				imagePanel.addpoint = true;
				
				String selected = (String)LabelPanel.getSelectedValue();
				name.setText(selected);
				ArrayList<Point> to_edit = imagePanel.getPolygonTable().get(selected);
				imagePanel.removePolygon(selected);
				imagePanel.repaint();
				imagePanel.startEditing(to_edit, (String) LabelPanel.getSelectedValue());
					
				toolboxPanel.revalidate();
				toolboxPanel.repaint();
				
				editFrame.setVisible(true);
				  
				remP.setSelected(false);
				addP.setSelected(true);
				adjP.setSelected(false);
				status.setText( " Click on the image to add a point to the current object. ");
			}
        });
        
        Remove.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				if (LabelPanel.getSelectedValue() ==null) return;
				String[] options = {"Yes","No"};
				int reval =  JOptionPane.showOptionDialog(appPanel, "Do you really wish to remove this object?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
				if (reval== JOptionPane.YES_OPTION){
					//remove the selected polygon and check if it was the current polygon
					if (imagePanel.removePolygon((String)LabelPanel.getSelectedValue())) {
						//if yes, hide the edit panel and reset stuff
						imagePanel.stopPointEditing();
						imagePanel.endEdit();
						editFrame.setVisible(false);
						name.setText("");
					}
					//update the LabelPanel
					ArrayList<String> list = Collections.list(imagePanel.getPolygonTable().keys());
				    LabelPanel.setListData(list.toArray(new String[list.size()]));
				    
				    if (LabelPanel.getSelectedValue()!= null) 
				    	imagePanel.setSelectedPolygon((String)LabelPanel.getSelectedValue());
				    else imagePanel.setSelectedPolygon(null);
				    
					imagePanel.revalidate();
					imagePanel.repaint();
					LabelPanel.revalidate();
					LabelPanel.repaint();
				}
			}
        });
        
        ///creating the editor Panel
        editFrame = new JInternalFrame("Editing Toolbox", false, false, false);
        editFrame.setVisible(false);
        editPanel = new JPanel();
        editPanel.setSize(new Dimension(200, 200));
        editPanel.setMinimumSize(new Dimension(200, 200));
        editPanel.setMaximumSize(new Dimension(200, 200));
        editPanel.setPreferredSize(new Dimension(200, 200));
        //GridBagLayout g = new GridBagLayout();
        //GridBagConstraints c = new GridBagConstraints();
        //editPanel.setLayout(g);
        editFrame.add(editPanel);
        JLabel label = new JLabel("Label:   ");
        JLabel tools = new JLabel("Tools");
        tools.setFont(new Font("Verenda", Font.BOLD, 16));
        tools.setSize(70, 30);
        tools.setVerticalAlignment(SwingConstants.TOP);
        tools.setPreferredSize(new Dimension(70, 30));
        tools.setMaximumSize(new Dimension(70, 30));
        tools.setMinimumSize(new Dimension(70, 30));
        
        //TODO: Attention!! Need to change these if we change the structure of the folders!!
        BufferedImage buttonIcon = ImageIO.read(new File("../images/addButton.png"));
        addP = new JButton(new ImageIcon(buttonIcon));
        addP.setBorder(BorderFactory.createEmptyBorder());
        addP.setContentAreaFilled(false);
        //hovered
        buttonIcon = ImageIO.read(new File("../images/addHovered.png"));
        addP.setRolloverIcon(new ImageIcon(buttonIcon));
        buttonIcon = ImageIO.read(new File("../images/addSelected.png"));
        addP.setSelectedIcon(new ImageIcon(buttonIcon));
        addP.setMnemonic(KeyEvent.VK_A);
        
        buttonIcon = ImageIO.read(new File("../images/editButton.png"));
        adjP = new JButton(new ImageIcon(buttonIcon));
        adjP.setBorder(BorderFactory.createEmptyBorder());
        adjP.setContentAreaFilled(false);
        buttonIcon = ImageIO.read(new File("../images/editHovered.png"));
        adjP.setRolloverIcon(new ImageIcon(buttonIcon));
        buttonIcon = ImageIO.read(new File("../images/editSelected.png"));
        adjP.setSelectedIcon(new ImageIcon(buttonIcon));
        adjP.setMnemonic(KeyEvent.VK_M);
        
        buttonIcon = ImageIO.read(new File("../images/removeButton.png"));
        remP = new JButton(new ImageIcon(buttonIcon));
        remP.setBorder(BorderFactory.createEmptyBorder());
        remP.setContentAreaFilled(false);
        buttonIcon = ImageIO.read(new File("../images/removeHovered.png"));
        remP.setRolloverIcon(new ImageIcon(buttonIcon));
        buttonIcon = ImageIO.read(new File("../images/removeSelected.png"));
        remP.setSelectedIcon(new ImageIcon(buttonIcon));
        remP.setMnemonic(KeyEvent.VK_R);
        
        buttonIcon = ImageIO.read(new File("../images/undoButton.png"));
        JButton undoButton = new JButton();
        undoButton.setSize(new Dimension(45, 30));
        undoButton.setPreferredSize(new Dimension(45, 30));
        undoButton.setMinimumSize(new Dimension(45, 30));
        undoButton.setMaximumSize(new Dimension(45, 30));
        undoButton.setBorder(BorderFactory.createEmptyBorder());
        undoButton.setContentAreaFilled(false);
        undoButton.setAction(undo);
        undoButton.setIcon(new ImageIcon(buttonIcon));
        buttonIcon = ImageIO.read(new File("../images/undoHovered.png"));
        undoButton.setRolloverIcon(new ImageIcon(buttonIcon));
        undoButton.setText("");
        
        buttonIcon = ImageIO.read(new File("../images/redoButton.png"));
        JButton redoButton = new JButton();
        redoButton.setSize(new Dimension(45, 30));
        redoButton.setPreferredSize(new Dimension(45, 30));
        redoButton.setMinimumSize(new Dimension(45, 30));
        redoButton.setMaximumSize(new Dimension(45, 30));
        redoButton.setBorder(BorderFactory.createEmptyBorder());
        redoButton.setContentAreaFilled(false);
        redoButton.setAction(redo);
        redoButton.setText("");
        redoButton.setIcon(new ImageIcon(buttonIcon));
        buttonIcon = ImageIO.read(new File("../images/redoHovered.png"));
        redoButton.setRolloverIcon(new ImageIcon(buttonIcon));
        
        name.setSize(120, 20);
        name.setPreferredSize(new Dimension(120, 20));
        name.setMinimumSize(new Dimension(120, 20));
        name.setMaximumSize(new Dimension(120, 20));
        name.setToolTipText("Name for your object");
        
        editPanel.add(Box.createRigidArea(new Dimension(170,10)));
        editPanel.add(label);
        editPanel.add(name);
        editPanel.add(Box.createRigidArea(new Dimension(170,25)));
        editPanel.add(tools);
        editPanel.add(undoButton);
        editPanel.add(redoButton);
        editPanel.add(Box.createRigidArea(new Dimension(170,5)));
        editPanel.add(addP);
        addP.setToolTipText("Add points to the current object");
        editPanel.add(adjP);
        adjP.setToolTipText("Repoistion a point from the current object");
        editPanel.add(remP);
        remP.setToolTipText("Remove a point from the current object");
        editPanel.add(Box.createRigidArea(new Dimension(170,30)));
        editPanel.add(save);
        save.setMnemonic(KeyEvent.VK_ENTER);
        save.setToolTipText("Click to save the current annotated object");
        cancel.setMnemonic(KeyEvent.VK_ESCAPE);
        cancel.setToolTipText("Click to cancel the current object");
        editPanel.add(cancel);
        
        adjP.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				imagePanel.stopPointEditing();
				imagePanel.adjustPoint=true;
				imagePanel.repaint();
				adjP.setSelected(true);
				addP.setSelected(false);
				remP.setSelected(false);
				status.setText( " Click on the old point - it will become red - and then click on the new place you want to move it to.");
			}
        });
        
        addP.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				imagePanel.stopPointEditing();
				imagePanel.addpoint=true;
				imagePanel.repaint();
				addP.setSelected(true);
				adjP.setSelected(false);
				remP.setSelected(false);
				status.setText( " Click anywhere on the image to add a point to the current object.");
    			
			}
        });

        remP.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				//stopp al preious editing
				imagePanel.stopPointEditing();
				//and allow the removing of points
				imagePanel.removePoint=true;
				imagePanel.repaint();
				remP.setSelected(true);
				addP.setSelected(false);
				adjP.setSelected(false);
				status.setText( " Click on any point from the selected object to remove it.");	
			}
        });
        
        //save and cancel buttons
        save.setMnemonic(KeyEvent.VK_S);
		save.setSize(50, 20);
		save.setToolTipText("Click to complete this annotation object");
        save.addActionListener(new ActionListener(){
        //when the save button is clicked , the edited polygon is added referenced by the current label
			public void actionPerformed(ActionEvent arg0) {
				
			  if (name.getText().equals("")) {
				  JOptionPane.showMessageDialog(appPanel, "You must add a name for your object.");
				  return;
			  }
			  if (Collections.list(imagePanel.getPolygonTable().keys()).contains(name.getText())) {
				  JOptionPane.showMessageDialog(appPanel, "There is already an object with the same label. Please change your label.");
				  return;
			  }
			  
			  if (imagePanel.getCurrentPolygon().size()<3) {
				  JOptionPane.showMessageDialog(appPanel, "Your object must countain at least three points.");
				  return;
			  }
			  
			  imagePanel.stopPointEditing();
			  
			  imagePanel.finalizePolygon(name.getText());
			  String label = (String) LabelPanel.getSelectedValue();
			  if (label != null) imagePanel.setSelectedPolygon(label);
			  else imagePanel.setSelectedPolygon(null);
			 
			  name.setText("");

			  //refresh views
		      ArrayList<String> list = Collections.list(imagePanel.getPolygonTable().keys());
		      LabelPanel.setListData((list.toArray(new String[list.size()])));
			  editFrame.setVisible(false);
			  imagePanel.revalidate();
			  imagePanel.repaint();;
			  status.setText( " Use the New, Edit and Remove buttons to manage the existing objects or to add new ones.");
  			
			}
        });
        
        //cancel.setMnemonic(ESC);
		cancel.setSize(50, 20);
		cancel.setEnabled(true);
        cancel.addActionListener(new ActionListener(){
            //when the save button is clicked , the edited polygon is added referenced by the current label
    			public void actionPerformed(ActionEvent arg0) {
    				name.setText("");
    				imagePanel.stopPointEditing();
    				imagePanel.cancelEdit();
					editFrame.setVisible(false);
					imagePanel.revalidate();
					imagePanel.repaint();
					String label = (String) LabelPanel.getSelectedValue();
					if (label != null) imagePanel.setSelectedPolygon(label);
					else imagePanel.setSelectedPolygon(null);
					status.setText( " Use the New, Edit and Remove buttons to manage the existing objects or to add new ones.");
			  }
            });
        
        JPanel zoomPanel = new JPanel();
        zoomPanel.setLayout(new BoxLayout(zoomPanel, BoxLayout.X_AXIS));

        JLabel zoomLabel = new JLabel(" Scale: ");
        
        zoom = new JSlider();
        zoom.setMinimum(100);
        zoom.setMaximum(300);
        zoom.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				System.out.println(zoom.getValue() + ", " + prev_zoom);
				imagePanel.scale((zoom.getValue()/(double) 100), prev_zoom/(double)100);
				prev_zoom = zoom.getValue();
			}	
        });
        zoom.setToolTipText("Move the slider to zoom in an out of the picture");
        zoomPanel.setPreferredSize(new Dimension(220,20));
        
        zoomPanel.add(zoomLabel);
        zoomPanel.add(Box.createRigidArea(new Dimension(10,20)));
        zoomPanel.add(zoom); 
        
        //adding the internal frames to the toolbox
        
        toolboxPanel.add(labelFrame);
        toolboxPanel.add(editFrame);
        toolboxPanel.add(zoomPanel);
        
		//add toolbox to window
		toolboxPanel.setPreferredSize(new Dimension(220,620));
      
		appPanel.add(leftPanel);
		appPanel.add(toolboxPanel);

		KeyListener k = new KeyListener() {

			public void keyPressed(KeyEvent press) {
				if (press.getKeyCode()==(KeyEvent.VK_ENTER) && editFrame.isVisible()) {
					save.doClick();
				} else if (press.getKeyCode() == KeyEvent.VK_ESCAPE && editFrame.isVisible()) {
					cancel.doClick();
				} else if (press.getKeyCode() == (KeyEvent.VK_E) && press.isAltDown() && Edit.isEnabled()) {
					Edit.doClick();
				} else if (press.getKeyCode() == (KeyEvent.VK_N) && press.isAltDown() ) {
					New.doClick();
				} else if (press.getKeyCode() == (KeyEvent.VK_DELETE) ) {
					Remove.doClick();
				}
			}
			public void keyReleased(KeyEvent arg0) {
			}

			public void keyTyped(KeyEvent press) {
				
			}	
		};
		
		this.addKeyListener(k);
		appPanel.addKeyListener(k);
		imagePanel.addKeyListener(k);
		leftPanel.addKeyListener(k);
		scroller.addKeyListener(k);
		zoomPanel.addKeyListener(k);
		LabelPanel.addKeyListener(k);
		editPanel.addKeyListener(k);
		zoom.addKeyListener(k);
		editFrame.addKeyListener(k);
		optionsPanel.addKeyListener(k);
		toolboxPanel.addKeyListener(k);
		name.addKeyListener(k);
		addP.addKeyListener(k);
		adjP.addKeyListener(k);
		remP.addKeyListener(k);
		save.addKeyListener(k);
		cancel.addKeyListener(k);
		Edit.addKeyListener(k);
		Remove.addKeyListener(k);
		New.addKeyListener(k);
		status.addKeyListener(k);
		
		// display all the stuff
		this.setSize(1200,700);
		pack();
		setVisible(true);
		
		validate();
		repaint();

		//ask the user whether he want to load a new image or to open an existing project
		String[] options = {"Load a New Image","Open and Existing Project"};
		int reval =  JOptionPane.showOptionDialog(appPanel, "Do you want to load a new image for annotation or to open an existing project?", "What do you want to do?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (reval==JOptionPane.YES_OPTION) {
			fileItem1.doClick();
		} else {
			fileItem2.doClick();
		}
		
	}

	/**
	 * Runs the program
	 * 
	 * @param argv
	 *            path to an image
	 */
	public static void main(String argv[]) {
		try {
			// create a window and display the image
			ImageLabeller window = new ImageLabeller();
			window.setupGUI();
			window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		} catch (Exception e) {
			System.err.println("Image: " + argv[0]);
			e.printStackTrace();
		}
	}
	
	public String getExtension(String filename) {
		String[] nameParts = filename.split("\\.");
		//file has no extension and is not a folder so refuse it
		if (nameParts.length == 0) return "";
		return nameParts[nameParts.length - 1];
	}
	
	class CustomFileFilter extends FileFilter {
		
		private ArrayList<String> accepted_extensions = new ArrayList<String>();
		private String name;
		
		public boolean accept(File f) {
			if (f.isDirectory()) return true;
			String filename = f.getName();
			String extension = getExtension(filename);
			for (int i=0; i<accepted_extensions.size(); i++) {
				if (extension.equals(accepted_extensions.get(i))) return true;
			}
			return false;
		}
		
		public CustomFileFilter(String name) {
			super();
			this.name = name;
		}

		public String getDescription() {
			return name;
		}
		
		public void addExtension(String ext) {
			accepted_extensions.add(ext);
		}
	}
	
	class UndoAction extends AbstractAction{
		
		private static final long serialVersionUID = 1L;
		
		public UndoAction(String text, String desc , Integer mnemonic){
			super(text);
			putValue(SHORT_DESCRIPTION,desc);
			putValue(MNEMONIC_KEY, mnemonic);	
			
		}

		public void actionPerformed(ActionEvent arg0) {
				imagePanel.undo();
		}
	}	
		
		class RedoAction extends AbstractAction{
			
			private static final long serialVersionUID = 2L;

			public RedoAction(String text, String desc , Integer mnemonic){
				super(text);
				putValue(SHORT_DESCRIPTION,desc);
				putValue(MNEMONIC_KEY, mnemonic);
		
			}

			public void actionPerformed(ActionEvent e) {
				imagePanel.redo();
			}
		}
	}

