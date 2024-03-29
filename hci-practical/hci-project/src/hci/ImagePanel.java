package hci;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import hci.utils.*;

/**
 * Handles image editing panel
 * 
 * @authors Michal, Hristiana and Adnan
 * 
 */
public class ImagePanel extends JPanel implements MouseListener {
	/**
	 * some java stuff to get rid of warnings
	 */
	private static final long serialVersionUID = 1L;

	boolean adjustPoint = false;
	boolean addpoint = false;
	boolean removePoint = false;
	int indexR = 1000000;

	/**
	 * image to be tagged
	 */
	private BufferedImage image = null;
	private BufferedImage originalImage = null;

	/**
	 * list of current polygon's vertices
	 */
	private ArrayList<Point> currentPolygon = null;
	private ArrayList<Point> selectedPolygon = null;
	public String editedLabel = null; //the current label of the polygon being edited, or null if no polygon is being edited
	private ArrayList<Point> editedPolygon = null;
	ArrayList<Point> pointCache = null;// cache holding a copy of current polygon
	ArrayList<Short> actionCache = null;
	ArrayList<Integer> indexCache = null;
	int cacheIndex = -1; //points to the currently last performed action
	
	public static final Short NEW = 1;
	public static final Short MOVE = 2;
	public static final Short REMOVE = 3;
	
	/**
	 * list of polygons and colours for them
	 */
	private Hashtable<String, ArrayList<Point>> polygontable = null;

	/**
	 * default constructor, sets up the window properties
	 */
	public ImagePanel() {
		polygontable = new Hashtable<String, ArrayList<Point>>();
		selectedPolygon = new ArrayList<Point>();
		
		this.setVisible(true);

		Dimension panelSize = new Dimension(800, 600);
		this.setSize(panelSize);
		this.setMinimumSize(panelSize);
		this.setPreferredSize(panelSize);
		this.setMaximumSize(panelSize);

		addMouseListener(this);
	}

	/**
	 * extended constructor - loads image to be labelled
	 * 
	 * @param imageName
	 *            - path to image
	 * @throws Exception
	 *             if error loading the image
	 */
	public ImagePanel(String imageName) throws Exception {
		this();
		image = ImageIO.read(new File(imageName));
		if (image.getWidth() > 800 || image.getHeight() > 600) {
			int newWidth = image.getWidth() > 800 ? 800
					: (image.getWidth() * 600) / image.getHeight();
			int newHeight = image.getHeight() > 600 ? 600
					: (image.getHeight() * 800) / image.getWidth();
			System.out.println("SCALING TO " + newWidth + "x" + newHeight);
			Image scaledImage = image.getScaledInstance(newWidth, newHeight,
					Image.SCALE_FAST);
			image = new BufferedImage(newWidth, newHeight,
					BufferedImage.TYPE_INT_RGB);
			image.getGraphics().drawImage(scaledImage, 0, 0, this);
		}
		originalImage = image;
	}

	public void changePicture(String imageName) throws Exception {

		image = ImageIO.read(new File(imageName));
		if (image.getWidth() > 800 || image.getHeight() > 600) {
			int newWidth = image.getWidth() > 800 ? 800
					: (image.getWidth() * 600) / image.getHeight();
			int newHeight = image.getHeight() > 600 ? 600
					: (image.getHeight() * 800) / image.getWidth();
			System.out.println("SCALING TO " + newWidth + "x" + newHeight);
			Image scaledImage = image.getScaledInstance(newWidth, newHeight,
					Image.SCALE_FAST);
			image = new BufferedImage(newWidth, newHeight,
					BufferedImage.TYPE_INT_RGB);
			image.getGraphics().drawImage(scaledImage, 0, 0, this);
		}
		currentPolygon = new ArrayList<Point>();
		polygontable = new Hashtable<String, ArrayList<Point>>();
		revalidate();
		repaint();
		originalImage = image;
	}

	public void loadProject(SerializableImage image,
			Hashtable<String, ArrayList<Point>> polygons,
			ArrayList<Point> polygon) throws Exception {

		this.image = new BufferedImage(image.getWidth(), image.getHeight(),
				image.getImageType());
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				this.image.setRGB(i, j, image.getPixels()[i][j]);
				;
			}
		}
		currentPolygon = polygon;
		polygontable = polygons;
		revalidate();
		repaint();
		originalImage = this.image;
	}
	
	public void scale(double factor, double current_scale) {
		int newWidth = (int) Math.round(originalImage.getWidth() * factor);
		int newHeight = (int) Math.round(originalImage.getHeight() * factor);
		
		System.out.println(originalImage.getWidth()* factor + ", " + originalImage.getHeight()* factor);
		//TODO: grey out the zoom buttons when not needed. Also: a sroller can be made for the zoom level not just buttons
		if (newWidth > 3200 || newHeight > 2400) return;
		if (newWidth < 740 && newHeight < 560) return;
		
		System.out.println("SCALING TO " + newWidth + "x" + newHeight);
		Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_FAST);
		image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		image.getGraphics().drawImage(scaledImage, 0, 0, this);
		
		Dimension panelSize = new Dimension(image.getWidth(), image.getHeight());
		this.setSize(panelSize);
		this.setMinimumSize(panelSize);
		this.setPreferredSize(panelSize);
		this.setMaximumSize(panelSize);
		
		Enumeration en = polygontable.keys();
		while (en.hasMoreElements()) {
			ArrayList<Point> polygon = polygontable.get(en.nextElement());
			for (int i = 0; i < polygon.size(); i++) {
				Point p = polygon.get(i);
				polygon.set(i, new Point((int) Math.round(p.getX()*factor/current_scale), (int)Math.round(p.getY()*factor/current_scale)));
			}
		}
		
		if (currentPolygon != null) {
			for (int i = 0; i < currentPolygon.size(); i++) {
				Point p = currentPolygon.get(i);
				currentPolygon.set(i, new Point((int)Math.round(p.getX()*factor/current_scale), (int)Math.round(p.getY()*factor/current_scale)));
			}
		}
	}
	
	public Hashtable<String, ArrayList<Point>> getPolygonTable() {
		return polygontable;
	}

	//returns true if it removed the currentPolygon
	public boolean removePolygon(String key) {
		if (key==null) return false;
		System.out.println(key);
		if ((!polygontable.containsKey(key)) && ((editedLabel==null) || (!editedLabel.equals(key)))) return false;
		if (editedLabel != null && editedLabel.equals(key)) {
			currentPolygon = null;
			return true;
		}
		selectedPolygon = null;
		polygontable.remove(key);
		return false;
	}

	public ArrayList<Point> getCurrentPolygon() {
		return currentPolygon;
	}

	public void setSelectedPolygon(String key) {
		if (key == null || !polygontable.containsKey(key))
			selectedPolygon = null;
		else
			selectedPolygon = polygontable.get(key);
	}

	public void setCurrentPolygon(ArrayList<Point> polygon) {
		currentPolygon = polygon;
	}

	public void stopPointEditing() {
		addpoint = false;// /no more points can be added just by clicking
		removePoint = false;
		adjustPoint = false;
		indexR = -1;
	}

	public SerializableImage getSerializableImage() {
		int[][] pixels = new int[image.getWidth()][image.getHeight()];
		for (int i = 0; i < pixels.length; i++) {
			for (int j = 0; j < pixels[i].length; j++) {
				pixels[i][j] = image.getRGB(i, j);
			}
		}
		return new SerializableImage(image.getWidth(), image.getHeight(), image
				.getType(), pixels);
	}
	public void startNewPolygon() {
		currentPolygon = new ArrayList<Point>();
		pointCache = new ArrayList<Point>();
		actionCache = new ArrayList<Short>();
		indexCache = new ArrayList<Integer>();
	}
	
	public void startEditing(ArrayList<Point> polygon, String label) {
		pointCache = new ArrayList<Point>();
		actionCache = new ArrayList<Short>();
		indexCache = new ArrayList<Integer>();
		currentPolygon = polygon;
		editedPolygon = new ArrayList<Point>();
		for (Point p : polygon) {
			editedPolygon.add(new Point(p.getX(), p.getY()));
		}
		editedLabel = label;
	}

	public void cancelEdit() {
		pointCache = null;
		actionCache = null;
		indexCache = null;
		cacheIndex = -1;
		if (editedLabel !=null)	polygontable.put(editedLabel, editedPolygon);
		editedPolygon = null;
		editedLabel = null;
		currentPolygon = null;
	}
	
	public void endEdit() {
		pointCache = null;
		actionCache = null;
		indexCache = null;
		cacheIndex = -1;
		editedPolygon = null;
		editedLabel = null;
		currentPolygon = null;
	}
	
	public void undo() {
		if (cacheIndex >= 0) {
			System.out.println(indexCache.get(cacheIndex) + ", " +  actionCache.get(cacheIndex)) ;
			int action = actionCache.get(cacheIndex);
			if (action == NEW) {
				currentPolygon.remove((int)indexCache.get(cacheIndex));
			} else if (action == MOVE) {
				//swap the points
				Point p = pointCache.remove(cacheIndex);
				pointCache.add(cacheIndex,currentPolygon.remove((int) indexCache.get(cacheIndex)));
				currentPolygon.add(indexCache.get(cacheIndex),p);
			} else if (action==REMOVE) {
				currentPolygon.add(indexCache.get(cacheIndex), pointCache.get(cacheIndex));
			}
			cacheIndex--;
		}
		repaint();
	}
	
	public void redo() {
		if (actionCache.size()-1 > cacheIndex) {
			cacheIndex++;
			int action = actionCache.get(cacheIndex);
			if (action == NEW) {
				currentPolygon.add(indexCache.get(cacheIndex), pointCache.get(cacheIndex));
			} else if (action == MOVE) {
				Point p = pointCache.remove(cacheIndex);
				pointCache.add(cacheIndex,currentPolygon.remove((int) indexCache.get(cacheIndex)));
				currentPolygon.add(indexCache.get(cacheIndex),p);
			} else if (action==REMOVE) {
				currentPolygon.remove((int)indexCache.get(cacheIndex));
			}
			
		}
		repaint();
	}
	
	/**
	 * moves current polygon to the list of polygons and makes pace for a new
	 * one
	 */
	public void finalizePolygon(String key) {
		// finish the current polygon if any
		editedPolygon = null;
		editedLabel = null;
		pointCache = null;
		actionCache = null;
		indexCache = null;
		cacheIndex = -1;
		if (currentPolygon != null) {
			polygontable.put(key, currentPolygon);
			currentPolygon = null;
			pointCache = null;
			repaint();
		}
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);

		// display image
		if (image != null) {
			g.drawImage(image, 0, 0, null);
		}

		// display all the polygons using information from the hashtable
		Enumeration e = polygontable.keys();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			displayPolygon(key, Color.GREEN, g);
			if (polygontable.get(key) != selectedPolygon)
				fillPolygon(polygontable.get(key), g,
						new Color(50, 255, 50, 25));

		}
		if (selectedPolygon != null) {
			// fill the polygon with semi-transparent
			fillPolygon(selectedPolygon, g, new Color(255, 255, 3, 40));
			drawPolygon(selectedPolygon, Color.YELLOW, g);
			finishPolygon(selectedPolygon, Color.YELLOW, g);
		}

		if (currentPolygon != null) {
			drawPolygon(currentPolygon, new Color(255, 170, 0), g);
			fillPolygon(currentPolygon, g, new Color(255, 200, 3, 40));
			if (editedLabel != null) {
				if (!addpoint)
					finishPolygon(currentPolygon, new Color(255, 170, 0), g);
			}
		}
	}
		
	public void fillPolygon(ArrayList<Point> polygon, Graphics g, Color color) {
		g.setColor(color);
		Polygon pol = new Polygon();
		for (Point p : polygon) {
			pol.addPoint(p.getX(), p.getY());
		}
		Graphics2D g2 = (Graphics2D) g.create();
		g2.fill(pol);
		g2.dispose();

	}
	
	public Point getCentreOfPolygon(ArrayList<Point> polygon) {
		int averageX = 0;
		int averageY = 0;
		for (Point point : polygon) {
			averageX += point.getX() / (float) polygon.size();
			averageY += point.getY() / (float) polygon.size();
		}
		return new Point(averageX, averageY);
	}

	// displays the polygon indexed by the key in the hash table
	public void displayPolygon(String key, Color color, Graphics g) {
		if (key == null ) return;
		if (!polygontable.containsKey(key)) return;
		
		ArrayList<Point> polygon = (ArrayList<Point>) polygontable.get(key);
		drawName(polygon, key, color, g);
		drawPolygon(polygon, color, g);
		finishPolygon(polygon, color, g);
	}

	public void drawName(ArrayList<Point> polygon, String label, Color color,
			Graphics g) {
		Point centre = getCentreOfPolygon(polygon);
		g.setColor(new Color(3, 3, 3, 128));
		g.fillRect(centre.getX() - 5, centre.getY() - 15, label.length() * 10,
				20);
		g.setColor(color);
		g.setFont(new Font("Verenda", Font.LAYOUT_LEFT_TO_RIGHT, 16));
		g.drawString(label, centre.getX(), centre.getY());
	}

	/**
	 * displays a polygon without last stroke
	 * 
	 * @param polygon
	 *            to be displayed
	 */
	public void drawPolygon(ArrayList<Point> polygon, Color color, Graphics g) {

		g.setColor(color);
		for (int i = 0; i < polygon.size(); i++) {
			Point currentVertex = polygon.get(i);
			g.setFont(new Font("Arial", Font.LAYOUT_LEFT_TO_RIGHT, 12));
			g.setColor(new Color(3, 3, 3, 128));
			if (i >= 10)
				g.fillRect(currentVertex.getX() + 8, currentVertex.getY() - 3,
						20, 15);
			else
				g.fillRect(currentVertex.getX() + 8, currentVertex.getY() - 3,
						12, 15);
			g.setColor(color);
			g.drawString(Integer.toString(i+1), currentVertex.getX() + 10,
					currentVertex.getY() + 10);
			if (i != 0) {
				Point prevVertex = polygon.get(i - 1);
				g.drawLine(prevVertex.getX(), prevVertex.getY(), currentVertex
						.getX(), currentVertex.getY());
			}
			if (polygon == currentPolygon && i == indexR)
				g.setColor(Color.RED);
			g.fillOval(currentVertex.getX() - 5, currentVertex.getY() - 5, 10,
					10);
			g.setColor(color);
		}
	}

	/**
	 * displays last stroke of the polygon (arch between the last and first
	 * vertices)
	 * 
	 * @param polygon
	 *            to be finished
	 */
	public void finishPolygon(ArrayList<Point> polygon, Color color, Graphics g) {
		// if there are less than 3 vertices than nothing to be completed
		if (polygon.size() >= 3) {
			Point firstVertex = polygon.get(0);
			Point lastVertex = polygon.get(polygon.size() - 1);

			g.setColor(color);
			// g.setColor(Color.GREEN);

			g.drawLine(firstVertex.getX(), firstVertex.getY(), lastVertex
					.getX(), lastVertex.getY());
		}
	}

	public int findIndex(ArrayList<Point> polygon, Point pt) {
		for (Point p : currentPolygon) {
			if ((Math.abs(p.getX() - pt.getX()) < 10)
					&& (Math.abs(p.getY() - pt.getY()) < 10)) {
				return currentPolygon.indexOf(p);

			}
		}
		return -1;
	}

	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();

		// the way the mouse events are handled depends on whether the remove,
		// add or adjust button have been clicked
		if (removePoint && (currentPolygon != null)) {
			Point pt = new Point(x, y);
			int index = findIndex(currentPolygon, pt);
			if (index < currentPolygon.size() && index >= 0) {
				if (cacheIndex < pointCache.size() - 1) {
					for (int i = 0; i< pointCache.size() - 1 - cacheIndex; i++) {
						pointCache.remove(pointCache.size()-1);
						actionCache.remove(actionCache.size()-1);
						indexCache.remove(indexCache.size() -1);
					}
				}
				pointCache.add(currentPolygon.remove(index));
				actionCache.add(REMOVE);
				cacheIndex++;
				indexCache.add(index);
				repaint();
			}
		} else if (adjustPoint && indexR < currentPolygon.size() && indexR >= 0
				&& (currentPolygon != null)) {
			Point pt = new Point(x, y);
			//if we're not at the last time step in the undo/redo action cache, remove everything after the current time step
			if (cacheIndex < pointCache.size() - 1) {
				for (int i = 0; i< pointCache.size() - 1 - cacheIndex; i++) {
					pointCache.remove(pointCache.size()-1);
					actionCache.remove(actionCache.size()-1);
					indexCache.remove(indexCache.size() -1);
				}
			}
			pointCache.add(currentPolygon.remove(indexR));
			actionCache.add(MOVE);
			cacheIndex++;
			indexCache.add(indexR);
			currentPolygon.add(indexR, pt);
			repaint();
			indexR = 100000;

		} else if (adjustPoint && (currentPolygon != null)) {
			System.out.println("touches here");
			Point pt = new Point(x, y);
			indexR = findIndex(currentPolygon, pt);
			repaint();
		}

		else if (currentPolygon != null && addpoint) {
			// check if the cursors with in image area
			if (x > image.getWidth() || y > image.getHeight()) return;
			// if the left button than we will add a vertex to polygon
			if (e.getButton() == MouseEvent.BUTTON1) {

				currentPolygon.add(new Point(x, y));
				//cache stuff
				if (cacheIndex < pointCache.size() - 1) {
					for (int i = 0; i< pointCache.size() - 1 - cacheIndex; i++) {
						pointCache.remove(pointCache.size()-1);
						actionCache.remove(actionCache.size()-1);
						indexCache.remove(indexCache.size() -1);
					}
				}
				pointCache.add(new Point(x, y));
				actionCache.add(NEW);
				indexCache.add(currentPolygon.size() - 1);
				cacheIndex++;
				System.out.println(x + " " + y);
				repaint();
			}
		}
	}

	public void mouseEntered(MouseEvent arg0) {
	}

	public void mouseExited(MouseEvent arg0) {
	}

	public void mousePressed(MouseEvent arg0) {
	}

	public void mouseReleased(MouseEvent arg0) {
	}

}
