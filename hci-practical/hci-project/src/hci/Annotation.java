package hci;

import java.awt.BorderLayout;
import java.io.File;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;



import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
//import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
//import javax.swing.JTextField;

public class Annotation {
    //static String label_msg;
	private static JFrame frame;
	//private static JTextField  field;
	public static void annotate(){
		frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		 JDesktopPane desktop = new JDesktopPane();
	    desktop.setOpaque(true);
	    frame.getContentPane().add(desktop, BorderLayout.CENTER);
		
		//JLabel msg = new JLabel ("Please provide an annotation for the segment");
		
		//JLabel label = new JLabel("Label");
		//annotatepanel.add(label);
		
		
		//field = new JTextField(20);
		
		//field.addActionListener(new ActionListener(){
			//public void actionPerformed(ActionEvent e) {
				//label_msg=(field.getText());
				//frame.dispose();
		//	}
		//});
		//annotatepanel.add(field);
	    JInternalFrame internalFrame = new JInternalFrame("Internal Frame", true,true,true);
	    		internalFrame.setBounds(50, 50, 200, 100);
	    		JPanel panel = new JPanel();
	    		JScrollPane scrollpane = new JScrollPane(panel);
	    		scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
	    		scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	    		internalFrame.getContentPane().add(scrollpane, BorderLayout.CENTER);
	    		internalFrame.getContentPane().add(new JButton("Hello"), BorderLayout.SOUTH);
	    		desktop.add(internalFrame);
	    		internalFrame.setVisible(true);
	    		JFileChooser file = new JFileChooser();
	    		file.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	    		int v= file.showOpenDialog(frame);
	    		System.out.println(file.getSelectedFile().getPath() );
	    		if (v== JFileChooser.APPROVE_OPTION){
	    			File doc = file.getSelectedFile();
	    			System.out.println(doc.getPath());
	    			if (doc.exists()){
	    				System.out.println(doc.getPath());
	    			}
	    		}
	    		frame.setVisible(true);
		
	}
	
	public static void main(String[] args){
		Annotation.annotate();
	}
	
	
}
