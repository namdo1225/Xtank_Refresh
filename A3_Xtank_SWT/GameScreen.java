import java.awt.Color;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
	
	private Composite 		compositeGame;
	private Canvas			canvas;
	
	private GridLayout		layout;
	
	
	public GameScreen(Shell shell, Display display, ClientController cCon, HostController hCon,
			ClientModel cMod, HostModel hMod) {
		super(shell, display, cCon, hCon, cMod, hMod);
	}
	
	@Override
	protected Composite makeComposite(Shell shell, Display display) {
		composite = new Composite(shell, SWT.COLOR_WHITE);
		
		compositePlayer = new Composite(composite, SWT.COLOR_WHITE);
		compositePlayer.setLayout(new FillLayout(SWT.VERTICAL));

		plHeader = new Label(compositePlayer, SWT.BALLOON);
		plHeader.setText("Players:");
		plHeader.setFont(new Font(display,"Times New Roman", 14, SWT.BOLD ));
		plHeader.setAlignment(SWT.LEFT);
		
		quit = new Button(compositePlayer, SWT.PUSH);
		quit.setText("Quit");
		quit.addSelectionListener(SelectionListener.widgetSelectedAdapter(e-> System.out.println("Pressed")));
		
		compositeGame = new Composite(composite, SWT.COLOR_BLACK);
		compositeGame.setLayout(new FillLayout(SWT.VERTICAL));
		compositeGame.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
		composite.setBackgroundMode(SWT.INHERIT_FORCE);

		
		canvas = new Canvas(compositeGame, SWT.COLOR_WHITE);
		
		canvas.addPaintListener(event -> {
			Tank tank = cModel.getTank();
			Rectangle tank_body = new Rectangle(tank.getX(), tank.getY(), 50, 100);
			Transform transform = new Transform(display);
			transform.translate(tank.getX() + 25, tank.getY() + 50);
			transform.rotate(-(float)tank.getRotate() + 90.0f);
			transform.translate(-tank.getX() - 25, -tank.getY() - 50);
			event.gc.setTransform(transform);
			event.gc.fillRectangle(canvas.getBounds());
			event.gc.setBackground(compositeGame.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
			event.gc.fillRectangle(tank_body);
			event.gc.setBackground(compositeGame.getDisplay().getSystemColor(SWT.COLOR_BLACK));
			event.gc.fillOval(tank.getX(), tank.getY()+25, 50, 50);
			event.gc.setLineWidth(4);
			event.gc.drawLine(tank.getX()+25, tank.getY()+25, tank.getX()+25, tank.getY()-15);
		});	

		canvas.addMouseListener(new MouseListener() {
			public void mouseDown(MouseEvent e) {
				System.out.println("mouseDown in canvas");
			} 
			public void mouseUp(MouseEvent e) {} 
			public void mouseDoubleClick(MouseEvent e) {} 
		});

		canvas.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
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
		
		return composite;
	}
	
}
