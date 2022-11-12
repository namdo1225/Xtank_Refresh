/**
 * A class to represent the UI portion of the actual gameplay screen.
 * 
 * For the parent class, @see Screen.java
 * 
 * @author	Nam Do
 * @version	1.0
 * @since	2022-11-12
 */

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class GameScreen extends Screen {
	private Composite 		compositePlayer;
	private Label			plHeader;
	private Button			quit;
	private Label			serverStatus;
	
	private Composite 		compositeGame;
	private Canvas			canvas;
	
	private GridLayout		layout;
	
	private GameMap			map;
	
	private	int				color1;
	private int				color2;
	private int				color3;
	
    private long 			time;
    private long 			start;
	
	private	ExecutorService	redrawThread;

	/**
	 * A constructor for GameScreen. Specifically for the server/host.
	 * 
	 * @param shell		a Shell from SWT to help use the graphic library.
	 * @param display	a Display from SWT to help use the graphic library.
	 * @param cCon		a ClientController for the controller of the client.
	 * @param hCon		a HostController for the controller of the host.
	 * @param cMod		a ClientModel for the model of the client.
	 * @param hMod		a HostModel for the model of the host.
	 */
	public GameScreen(Shell shell, Display display, ClientController cCon, HostController hCon,
			ClientModel cMod, HostModel hMod) {
		super(shell, display, cCon, hCon, cMod, hMod);
	}
	
	/**
	 * A constructor for GameScreen. Specifically for the client.
	 * 
	 * @param shell		a Shell from SWT to help use the graphic library.
	 * @param display	a Display from SWT to help use the graphic library.
	 * @param cCon		a ClientController for the controller of the client.
	 * @param hCon		a HostController for the controller of the host.
	 * @param cMod		a ClientModel for the model of the client.
	 * @param hMod		a HostModel for the model of the host.
	 * @param mapID		an int for the map's id.
	 * @param tankModel	an int for the model of the tank.
	 * @param tankID	an int for the tank's id.
	 */
	public GameScreen(Shell shell, Display display, ClientController cCon, HostController hCon,
			ClientModel cMod, HostModel hMod, int mapID, int tankModel, int tankID) {
		super(shell, display, cCon, hCon, cMod, hMod, mapID, tankModel, tankID);
	}
	
	/**
	 * A method that construct parts of the UI for this screen. No. 1
	 * 
	 * @param shell		a Shell from SWT to help use the graphic library.
	 * @param display	a Display from SWT to help use the graphic library.
	 */
	private void makeCompPart1(Shell shell, Display display) {
		composite = new Composite(shell, SWT.COLOR_WHITE);
		
		compositePlayer = new Composite(composite, SWT.COLOR_WHITE);
		compositePlayer.setLayout(new FillLayout(SWT.VERTICAL));

		plHeader = new Label(compositePlayer, SWT.BALLOON);
		plHeader.setText("You are\nplayer:\n");
		plHeader.setFont(new Font(display,"Times New Roman", 14, SWT.BOLD ));
		plHeader.setAlignment(SWT.LEFT);
		
		serverStatus = new Label(compositePlayer, SWT.BALLOON);
		serverStatus.setText("Server\nACTIVE");
		serverStatus.setFont(new Font(display,"Times New Roman", 12, SWT.BOLD ));
		serverStatus.setAlignment(SWT.LEFT);
		serverStatus.setForeground(display.getSystemColor(SWT.COLOR_GREEN));
		
		quit = new Button(compositePlayer, SWT.PUSH);
		quit.setText("Quit");
		quit.addSelectionListener(SelectionListener.widgetSelectedAdapter(e-> endGame()));
		
		compositeGame = new Composite(composite, SWT.COLOR_BLACK);
		compositeGame.setBackgroundMode(SWT.INHERIT_FORCE);
		
		composite.setBackgroundMode(SWT.INHERIT_FORCE);

		canvas = new Canvas(compositeGame, SWT.TRANSPARENT);
		canvas.setBounds(0, 0, 800, 500);
		shell.forceFocus();
	}
	
	/**
	 * A method that construct parts of the UI for this screen. No. 2
	 */
	private void makeCompPart2() {
		canvas.addMouseListener(new MouseListener() {
			public void mouseDown(MouseEvent e) {
				isConnected();
			} 
			public void mouseUp(MouseEvent e) {} 
			public void mouseDoubleClick(MouseEvent e) {} 
		});

		
		time = 0;
		start = System.nanoTime() / 1000000;
		canvas.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
            	time = (System.nanoTime() / 1000000) - start;
				
				if (isConnected()) {
					int tankID = cModel.getTank().getID();
					InputPacket packet = new InputPacket(tankID);
					
					// set up an object/packet to send to server based
					// on player's input.
					switch (e.keyCode) {
					case SWT.ARROW_UP:
						packet.y = -1;
						break;
					case SWT.ARROW_DOWN:
						packet.y = 1;
						break;
					case SWT.ARROW_LEFT:
						packet.x = -1;
						break;
					case SWT.ARROW_RIGHT:
						packet.x = 1;
						break;
					case SWT.SPACE:
						packet.shoot = true;
						break;
					}

					if (!packet.shoot || (time > 2000 && packet.shoot)) {
						if (packet.shoot) {
							start = System.nanoTime() / 1000000;
							time = 0;
						}
						cControl.writeOut(packet);
					}
					canvas.redraw();
				}
			}
			public void keyReleased(KeyEvent e) {}
		});

		layout = new GridLayout();
		layout.numColumns = 3;
	
		GridData data = new GridData();
		data.horizontalSpan = 1;
		data.grabExcessVerticalSpace = true;
		data.heightHint = 400;
		compositePlayer.setLayoutData(data);
		
		GridData data2 = new GridData();
		data2.horizontalSpan = 2;
		compositeGame.setLayoutData(data2);
		
		GridData data3 = new GridData();
		data2.widthHint = 800;
		data2.heightHint = 500;
		data3.horizontalSpan = 2;
		canvas.setLayoutData(data2);
		
		composite.setLayout(layout);
	}
	
	/**
	 * A method to construct parts of the UI that depends on the client/server.
	 * 
	 * @param display	a Display from SWT to help use the graphic library.
	 * @param mapID		an int for the map's id.
	 * @param tankModel	an int for the model of the tank.
	 * @param tankID	an int for the tank's id.
	 */
	private void makeCompChange(Display display, int mapID, int tankModel, int tankID) {
		map = new GameMap(display, compositeGame, mapID);
		canvas.addPaintListener(event -> paint(event));
		plHeader.setText("You are\nplayer:\n" + tankID);
	}

	/**
	 * A method to paint to the SWT canvas.
	 * 
	 * @param event		a PaintEvent object to get more information about the
	 * 					event that the player created.
	 */
	private void paint(PaintEvent event) {
		// render bullets before transformation for tanks
		List<Bullet> bullets = cModel.getBullets();
		for (var bullet : bullets) {
			Rectangle bullet_body = new Rectangle((int)bullet.getX(), (int)bullet.getY(), Bullet.size, Bullet.size);
			event.gc.setBackground(compositeGame.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
			event.gc.fillRectangle(bullet_body);
		}
		
		Map<Integer, Tank> tanks = cModel.getTanks();
		for (var key : tanks.keySet()) {
			
			// get tank info
			Tank tank = tanks.get(key);
			int x = tank.getX();
			int y = tank.getY();
			int w = Tank.width;
			int h = Tank.height;
			Rectangle tank_body = new Rectangle(x, y, w, h);
			Transform transform = new Transform(composite.getDisplay());
			
			color1 = tank.getBodyColor();
			color2 = tank.getCannonColor();
			
			// move tank
			transform.translate(x + (w / 2), y + (h / 2));
			transform.rotate(-(float)tank.getRotate() + 90.0f);
			transform.translate(-x - (w / 2), -y - (h / 2));
			event.gc.setTransform(transform);
			
			// draw shield
			if (tank.getArmor() > 0) {
				color3 = tank.getShield();
				
				event.gc.setBackground(compositeGame.getDisplay().getSystemColor(color3));
				event.gc.fillOval(x - 20, y+(w/2) - 20, w + 40, w + 40);
			}

			// draw main rectangle
			event.gc.setBackground(compositeGame.getDisplay().getSystemColor(color1));
			event.gc.fillRectangle(tank_body);
			
			// draw circle on tank
			event.gc.setBackground(compositeGame.getDisplay().getSystemColor(color2));
			event.gc.fillOval(x, y+(w/2), w, w);
			
			// draw line
			event.gc.setLineWidth(4);
			event.gc.setForeground(compositeGame.getDisplay().getSystemColor(SWT.COLOR_BLACK));
			event.gc.drawLine(x+(w/2), y+(w/2), x+(w/2), y-15);

			// draw id
			event.gc.setForeground(compositeGame.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			event.gc.drawText(String.valueOf(tank.getID()), x + 10, y+(w/2));
			map.collision(x, y, x + h, y + h);
		}
	}
	
	/**
	 * A method to make the entire UI for this screen.
	 * 
	 * @see Screen.java
	 * 
	 * @param shell		a Shell from SWT to help use the graphic library.
	 * @param display	a Display from SWT to help use the graphic library.
	 */
	@Override
	protected Composite makeComposite(Shell shell, Display display) {
		makeCompPart1(shell, display);
		makeCompChange(display, 1, 1, 1);
		makeCompPart2();
		
		return composite;
	}
	
	/**
	 * A method to make the entire UI for this screen with data based on client
	 * and server.
	 * 
	 * @see Screen.java
	 * 
	 * @param shell		a Shell from SWT to help use the graphic library.
	 * @param display	a Display from SWT to help use the graphic library.
	 * @param mapID		an int for the map's id.
	 * @param tankModel	an int for the model of the tank.
	 * @param tankID	an int for the tank's id.
	 */
	@Override
	protected Composite makeCompositeAndMap(Shell shell, Display display, 
			int mapID, int tankModel, int tankID) {
		makeCompPart1(shell, display);
		
       	redrawThread = Executors.newFixedThreadPool(1);
       	redrawThread.execute(new CanvasUpdater());
		
		makeCompChange(display, mapID, tankModel, tankID);
		makeCompPart2();
		
		return composite;
	}

	/**
	 * A method to check if the model is still connected to the server.
	 * 
	 * @return	a boolean. true if model is still connected to server. false if not.
	 */
	public boolean isConnected() {
		if (!cControl.isConnected()) {
			serverStatus.setText("Server\nCLOSED\nOr\nWinner\nDecided.\nPlease\nQuit.");
			serverStatus.setForeground(compositePlayer.getDisplay().getSystemColor(SWT.COLOR_RED));
			
			cControl.endGame();
			return false;
		}
		return true;
	}
	
	/**
	 * A method to end the game, cleaning up the UI and returning to the title screen.
	 */
	public void endGame() {
		serverStatus.setForeground(compositePlayer.getDisplay().getSystemColor(SWT.COLOR_GREEN));
		serverStatus.setText("Server\nACTIVE");
		cControl.endGame();
		cControl.updateScreen(Mode.MAIN);
	}
	
	/**
	 * A Runnable derived class that represents the task of
	 * updating the canvas for the UI.
	 * 
	 * @author	Nam Do
	 * @version	1.0
	 * @since	2022-11-12
	 */
	private class CanvasUpdater implements Runnable {
		
		/**
		 * A method to run the task, which updates the canvas.
		 */
		@Override
		public void run() {
			while (!canvas.isDisposed()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
			
            	if (!canvas.isDisposed())
				canvas.getDisplay().asyncExec(new Runnable() {
		            public void run() {
		            	if (!canvas.isDisposed())
		            		canvas.redraw();
		            }
		        });
			}
		}
	}
}
