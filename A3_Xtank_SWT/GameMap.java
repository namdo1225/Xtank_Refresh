/**
 * A class to represent maze map of the game, including its walls.
 * 
 * @author	Nam Do
 * @version	1.0
 * @since	2022-11-12
 */

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class GameMap {
	private List<Canvas>	obstacles;
	private Canvas			canvas;
	
	private int[]			x;
	private int[]			y;
	private int[]			w;
	private int[]			h;
	
	private int				id;
	
	/**
	 * A constructor for GameMap. Meant to be used by the client.
	 * 
	 * @param display		a Display from SWT to help use the graphics library.
	 * @param composite		a Composite object where the GameMap's UI components will be put in.
	 * @param mapNum		an int to load the correct map for the GameMap screen.
	 */
	public GameMap(Display display, Composite composite, int mapNum) {
		obstacles = new ArrayList<Canvas>();
		
		canvas = new Canvas(composite, SWT.NONE);
		canvas.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		canvas.setBounds(0, 0, 800, 500);
		
		canvas.setLayout(null);
		
		if (mapNum == 2)
			setMap2();
		else
			setMap1();
		makeObstacle();
	}
	
	/**
	 * A constructor for GameMap. Meant to be used by the server.
	 * 
	 * @param mapNum		an int to load the correct map for the GameMap screen.
	 */
	public GameMap(int mapNum) {
		id = mapNum;
		if (mapNum == 2)
			setMap2();
		else
			setMap1();
	}
	
	/**
	 * A method to return the id (map number) of the map.
	 * 
	 * @return	an int for the map's id.
	 */
	public int getID() {
		return id;
	}
	
	/**
	 * A method to set the member variables needed to create the correct
	 * map maze.
	 */
	private void setMap1() {
		x = new int[] { 0,   0, 790,   0, 200, 400, 500, 600};
		y = new int[] { 0,   0,   0, 490, 200, 400, 200, 100};
		w = new int[] {10, 800,  10, 800,  10,  10,  10, 100};
		h = new int[] {500, 10, 800,  10, 100, 100, 100,  10};
	}
	
	/**
	 * A method to set the member variables needed to create the correct
	 * map maze.
	 */
	private void setMap2() {
		x = new int[] { 0,   0, 790,   0, 200, 400,   0, 600, 500};
		y = new int[] { 0,   0,   0, 490,   0, 300, 400, 400,   0};
		w = new int[] {10, 800,  10, 800,  10,  10, 200, 200,  10};
		h = new int[] {500, 10, 800,  10, 200, 200,  10,  10, 150};
	}
	
	/**
	 * A method to make the obstacles for the map maze that will be displayed
	 * on the screen.
	 */
	private void makeObstacle() {
		for (int i = 0; i < x.length; i++) {
			obstacles.add(new Canvas(canvas, SWT.NONE));
			obstacles.get(i).setBackground(canvas.getDisplay().getSystemColor(SWT.NONE));
			
			obstacles.get(i).setBounds(x[i], y[i], w[i], h[i]);
			obstacles.get(i).addPaintListener(event -> {
				//event.gc.fillRectangle(canvas.getBounds());
				event.gc.setBackground(canvas.getDisplay().getSystemColor(SWT.COLOR_BLACK));
				event.gc.fillRectangle(obstacles.get(obstacles.size() - 1).getBounds());
			});
		}
	}
	
	/**
	 * A method to check if a given rectangle intersects with any obstacles of the map.
	 * 
	 * @param x1	an int of the top-left corner of the rectangle.
	 * @param y1	an int of the top-right corner of the rectangle.
	 * @param x2	an int of the bottom-left corner of the rectangle.
	 * @param y2	an int of the bottom-right corner of the rectangle.
	 * 
	 * @return	a boolean. true if collision occurs. false if not.
	 */
	public boolean collision(int x1, int y1, int x2, int y2) {
		for (int i = 0; i < x.length; i++)
			if (x1 < x[i] + w[i] &&
				x2 > x[i] &&
				y1 < y[i] + h[i] &&
				y2 > y[i])
				return true;
		return false;
	}
}
