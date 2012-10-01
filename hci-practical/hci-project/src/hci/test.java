package hci;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class test {

	
	public void testin(){
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//JOptionPane.showMessageDialog(frame, "hello");
		//JOptionPane.showMessageDialog(frame, "what the hell are you doing", "warning", JOptionPane.WARNING_MESSAGE);
		
		Object[] options ={"Yes","no", "Fuck off"};
		//int n = JOptionPane.showOptionDialog(frame, "Do you want to go for a coffee", "Ask out",JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		
		//String s  = (String) JOptionPane.showInputDialog(frame, "please type in your preference", "Peference descriptor", JOptionPane.PLAIN_MESSAGE, null, null, null);
		final JOptionPane optionPane = new JOptionPane("specify the directory name",JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION,null,null,null);
		final JDialog dialog = new JDialog(frame,"click a button",true);
		dialog.setContentPane(optionPane);
		
		optionPane.addPropertyChangeListener(
			    new PropertyChangeListener() {
			        public void propertyChange(PropertyChangeEvent e) {
			            String prop = e.getPropertyName();

			            if (dialog.isVisible() 
			             && (e.getSource() == optionPane)
			             && (prop.equals(JOptionPane.VALUE_PROPERTY))) {
			                //If you were going to check something
			                //before closing the window, you'd do
			                //it here.
			                dialog.setVisible(false);
			            }
			        }

					
			    });
			dialog.pack();
			dialog.setVisible(true);
		
		frame.pack();
		//frame.setVisible(true);
	}
	
	public static void main(String[] args){
		test hell = new test();
		hell.testin();
	}
}
