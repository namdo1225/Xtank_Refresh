import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;


public class GameMap {
	private List<Canvas>	obstacles;
	private Canvas			canvas;
	
	private int[] x;
	private int[] y;
	private int[] w;
	private int[] h;
	
	private int id;
	
	// Client
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
	
	// Server
	public GameMap(int mapNum) {
		id = mapNum;
		if (mapNum == 2)
			setMap2();
		else
			setMap1();
	}
	
	public int getID() {
		return id;
	}
	
	private void setMap1() {
		x = new int[] { 0,   0, 790,   0, 200, 400, 500, 600};
		y = new int[] { 0,   0,   0, 490, 200, 400, 200, 100};
		w = new int[] {10, 800,  10, 800,  10,  10,  10, 100};
		h = new int[] {500, 10, 800,  10, 100, 100, 100,  10};
	}
	
	private void setMap2() {
		x = new int[] { 0,   0, 790,   0, 200, 400,   0, 600, 500};
		y = new int[] { 0,   0,   0, 490,   0, 300, 400, 400,   0};
		w = new int[] {10, 800,  10, 800,  10,  10, 200, 200,  10};
		h = new int[] {500, 10, 800,  10, 200, 200,  10,  10, 150};
	}
	
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
	
	public boolean collision(int x1, int y1, int x2, int y2) {
		for (int i = 0; i < x.length; i++) {
			if (x1 < x[i] + w[i] &&
				x2 > x[i] &&
				y1 < y[i] + h[i] &&
				y2 > y[i])
			{
				//System.out.println("collision " + x1 + " " + y1);
				return true;
			}
		}
		
		return false;
	}
}
