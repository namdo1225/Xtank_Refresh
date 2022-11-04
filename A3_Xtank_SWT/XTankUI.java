
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class XTankUI
{
	// The location and direction of the "tank"
	private int x = 300;
	private int y = 500;
	private int directionX = 0;
	private int directionY = -10;

	private Canvas canvas;
	private Display display;
	
	private ObjectInputStream in; 
	private ObjectOutputStream out;
	
	private Lock position_lock = new ReentrantLock();
	private ExecutorService pool;
	
	public XTankUI(ObjectInputStream in, ObjectOutputStream out)
	{
		this.in = in;
		this.out = out;
	}
	
	public void start()
	{
		System.out.println("1");
		
		display = new Display();
		Shell shell = new Shell(display);
		shell.setText("xtank");
		shell.setLayout(new FillLayout());

		canvas = new Canvas(shell, SWT.NO_BACKGROUND);
		Runner runnable = new Runner();
		
		System.out.println("2");
		
		shell.addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event) {
				try {
					in.close();
					out.close();
					runnable.stop();
						
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				shell.dispose();
				display.dispose();
			}
		});

		canvas.addPaintListener(event -> {
			event.gc.fillRectangle(canvas.getBounds());
			event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_DARK_GREEN));
			event.gc.fillRectangle(x, y, 50, 100);
			event.gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
			event.gc.fillOval(x, y+25, 50, 50);
			event.gc.setLineWidth(4);
			event.gc.drawLine(x+25, y+25, x+25, y-15);
		});	

		canvas.addMouseListener(new MouseListener() {
			public void mouseDown(MouseEvent e) {
				//System.out.println("mouseDown in canvas");
				try {
					System.out.println(in.available());
					InputPacket packet = (InputPacket)in.readObject();
					System.out.println(packet.y == 1);
				} catch (ClassNotFoundException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} 
			public void mouseUp(MouseEvent e) {} 
			public void mouseDoubleClick(MouseEvent e) {} 
		});

		canvas.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				InputPacket packet = new InputPacket(0);
				packet.y = 1;
				//System.out.println("key " + e.character);
				// update tank location
				//x += directionX;
				//y += directionY;
				try {
					out.writeObject(packet);
				}
				catch(IOException ex) {
					System.out.println("The server did not respond (write KL).");
				}

				canvas.redraw();
			}
			public void keyReleased(KeyEvent e) {}
		});

		System.out.println("3");
		
		try {
			InputPacket test = new InputPacket(0);
			out.writeObject(test);
		}
		catch(IOException ex) {
			System.out.println("The server did not respond (initial write).");
		}	
		
		System.out.println("4");
		
		
		pool = Executors.newFixedThreadPool(1);
		pool.execute(runnable);
		//display.asyncExec(runnable);
		shell.open();
		while (!shell.isDisposed()) 
			if (!display.readAndDispatch())
				display.sleep();

		display.dispose();
		
		System.out.println("5");
	}
	
	class Runner implements Runnable
	{
		private boolean terminate;
		
		public void stop() {
			terminate = true;
		}
		
		public void run() 
		{
			terminate = false;
			while (!terminate) {
				try {
					if (in.available() > 0 || true)
					{
						// read in only other tanks and shots
						InputPacket packet = null;
						try {
							packet = (InputPacket)in.readObject();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if (packet.y == 1) {
							y += 2;
						}
						//canvas.redraw();
					}
				}
				catch(IOException ex) {
					System.out.println("The server did not respond (async).");
				}				
		        //display.timerExec(150, this);
			}
		}
	};	
}


