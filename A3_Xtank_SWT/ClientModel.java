import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientModel {	
	private XTankUI				clientView;
	private Mode				mode;
	private	Socket				socket;
	private ObjectInputStream 	in;
	private ObjectOutputStream 	out;
	private InputPacket			new_tank;
	private Tank				tank;
	private ArrayList<Tank>		tanks; // TODO: change to map
	private Runner				runnable;
	private ExecutorService		pool;
	
	public ClientModel() {
		mode = Mode.MAIN;
		tanks = new ArrayList<>();
		tank = new Tank(0, 0, 0);
	}
	
	public void setMVC(XTankUI view) {
		clientView = view;
	}
	
	public void setMode(Mode m) {
		mode = m;
		clientView.updateScreen(mode);
	}
	
	public void setSocket(String ip, int port) {
        try {
        	socket = new Socket(ip, port);
        	        	
        	in = new ObjectInputStream(socket.getInputStream());
        	System.out.println(in);
        	out = new ObjectOutputStream(socket.getOutputStream());
        	out.flush();
        	try {
        		new_tank = (InputPacket)in.readObject();
        		System.out.println(new_tank.id);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	tank = new Tank(new_tank.x, new_tank.y, new_tank.id);
        	
        }
        catch (Exception e) {
        	System.out.println("error");
        }
        
        if (socket.isConnected()) {        	
        	runnable = new Runner();
        	pool = Executors.newFixedThreadPool(1);
			pool.execute(runnable);
        }
	}
		
	public Tank getTank() {
		return tank;
	}
	
	public ObjectInputStream getInput() {
		return in;
	}
	
	public ObjectOutputStream getOutput() {
		return out;
	}
	
	public Runner getRun() {
		return runnable;
	}
	
	public Socket getSocket() {
		return socket;
	}
	
	public class Runner implements Runnable
	{
		private boolean terminate;
		
		public void stop() {
			terminate = true;
		}
		
		@Override
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
						// used if new tank added after this local tank was added
						if (packet.id >= tanks.size()) {
							tanks.add(new Tank(packet.x, packet.y, packet.id));
						}
						tank.set(packet.x, packet.y, packet.angle);
						//canvas.redraw();
					}
				}
				catch(IOException ex) {
					System.out.println("The server did not respond (async).");
					stop();
				}
		        //display.timerExec(150, this);
			}
		}
	};
}