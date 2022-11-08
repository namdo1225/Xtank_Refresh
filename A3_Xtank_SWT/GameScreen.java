import java.awt.Color;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
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
import org.eclipse.swt.widgets.Text;

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
	
	public GameScreen(Shell shell, Display display, ClientController cCon, HostController hCon,
			ClientModel cMod, HostModel hMod) {
		super(shell, display, cCon, hCon, cMod, hMod);
	}
	
	public GameScreen(Shell shell, Display display, ClientController cCon, HostController hCon,
			ClientModel cMod, HostModel hMod, int mapID, int tankID) {
		super(shell, display, cCon, hCon, cMod, hMod, mapID, tankID);
	}
	
	private void makeCompPart1(Shell shell, Display display) {
		composite = new Composite(shell, SWT.COLOR_WHITE);
		
		compositePlayer = new Composite(composite, SWT.COLOR_WHITE);
		compositePlayer.setLayout(new FillLayout(SWT.VERTICAL));

		plHeader = new Label(compositePlayer, SWT.BALLOON);
		plHeader.setText("Players:");
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
		//compositeGame.setLayout(new FillLayout(SWT.VERTICAL));
		//compositeGame.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		compositeGame.setBackgroundMode(SWT.INHERIT_FORCE);
		composite.setBackgroundMode(SWT.INHERIT_FORCE);

		canvas = new Canvas(compositeGame, SWT.TRANSPARENT);
		canvas.setBounds(0, 0, 800, 500);
	}
	
	private void makeCompPart2() {
		canvas.addMouseListener(new MouseListener() {
			public void mouseDown(MouseEvent e) {
				System.out.println("mouseDown in canvas");
			} 
			public void mouseUp(MouseEvent e) {} 
			public void mouseDoubleClick(MouseEvent e) {} 
		});

		canvas.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				if (isConnected()) {

					InputPacket packet = new InputPacket(0);
					switch (e.character) {
					case 'w':
						packet.y = -1;
						break;
					case 's':
						packet.y = 1;
						break;
					case 'a':
						packet.x = -1;
						break;
					case 'd':
						packet.x = 1;
						break;
					}
					//System.out.println("key " + e.character);
					// update tank location
					//x += directionX;
					//y += directionY;

					cControl.writeOut(packet);
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
		//data.verticalAlignment = GridData.FILL;
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
	
	private void makeCompChange(Display display, int mapID, int tankID) {
		map = new GameMap(display, compositeGame, mapID);
		
		color1 = SWT.COLOR_DARK_GREEN;
		color2 = SWT.COLOR_BLACK;
		if (tankID == 2) {
			color1 = SWT.COLOR_DARK_RED;
			color2 = SWT.COLOR_GRAY;
		}
		
		canvas.addPaintListener(event -> {
			//Tank tank = cModel.getTank();
			HashMap<Integer, Tank> tanks = cModel.getTanks();
			for (var key : tanks.keySet()) {
				Tank tank = tanks.get(key);
				Rectangle tank_body = new Rectangle(tank.getX(), tank.getY(), tank.width, tank.height);
				Transform transform = new Transform(display);
				transform.translate(tank.getX() + (tank.width / 2), tank.getY() + (tank.height / 2));
				transform.rotate(-(float)tank.getRotate() + 90.0f);
				transform.translate(-tank.getX() - (tank.width / 2), -tank.getY() - (tank.height / 2));
				event.gc.setTransform(transform);
				//event.gc.fillRectangle(canvas.getBounds());
				event.gc.setBackground(compositeGame.getDisplay().getSystemColor(color1));
				event.gc.fillRectangle(tank_body);
				event.gc.setBackground(compositeGame.getDisplay().getSystemColor(color2));
				event.gc.fillOval(tank.getX(), tank.getY()+(tank.width/2), tank.width, tank.width);
				event.gc.setLineWidth(4);
				event.gc.drawLine(tank.getX()+(tank.width/2), tank.getY()+(tank.width/2), tank.getX()+(tank.width/2), tank.getY()-15);
				//canvas.setBounds(tank.getX(), tank.getY(), 50, 100);
				map.collision(tank.getX(), tank.getY(), tank.getX() + tank.height, tank.getY() + tank.height);
			}
			
			ArrayList<Bullet> bullets = cModel.getBullets();
			for (var bullet : bullets) {
				Rectangle bullet_body = new Rectangle((int)bullet.getX(), (int)bullet.getY(), Bullet.size, Bullet.size);
				event.gc.setBackground(compositeGame.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
				event.gc.fillRectangle(bullet_body);
			}
		});
	}
	
	@Override
	protected Composite makeComposite(Shell shell, Display display) {
		makeCompPart1(shell, display);
		makeCompChange(display, 1, 1);
		makeCompPart2();
		
		return composite;
	}
	
	@Override
	protected Composite makeCompositeAndMap(Shell shell, Display display, int mapID, int tankID) {
		makeCompPart1(shell, display);
		makeCompChange(display, mapID, tankID);
		makeCompPart2();
		
		return composite;
	}
		
	public boolean isConnected() {
		if (!cControl.isConnected()) {
			serverStatus.setText("Server\nCLOSED\nPlease\nQuit.");
			serverStatus.setForeground(compositePlayer.getDisplay().getSystemColor(SWT.COLOR_RED));
			
			cControl.endGame();
			return false;
		}
		return true;
	}
	
	public void endGame() {
		serverStatus.setText("Server\nACTIVE");
		serverStatus.setForeground(compositePlayer.getDisplay().getSystemColor(SWT.COLOR_GREEN));
		cControl.endGame();
		cControl.updateScreen(Mode.MAIN);
	}
}
