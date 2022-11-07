
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.net.InetAddress;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * When a client connects, a new thread is started to handle it.
 */
public class Server 
{
	private static ArrayList<ObjectOutputStream> 	sq;
	private static HashMap<Integer, Tank>			tanks;

	private static ServerSocket						listener;
	private static ExecutorService					pool;
	private static GameMap							map;
	
	private static boolean							acceptConnection;
	
    public Server(int port, int mapNum) {
		//System.out.println(InetAddress.getLocalHost());
    	
    	acceptConnection = true;
		sq = new ArrayList<>();
		tanks = new HashMap<Integer, Tank>();
		map = new GameMap(mapNum);
        try
        {
        	listener = new ServerSocket(port);
            //System.out.println("The XTank server is running...");
            pool = Executors.newFixedThreadPool(5);
            pool.execute(new XTankConnection(listener));
        } catch (Exception e) {}
    }
    
    public int getPlCount() {
    	return XTankConnection.getPlCount();
    }
    
    public static int getNewID() {
    	int i = 0;
    	while (tanks.containsKey(i)) {
    		i++;
    	}
    	return i;
    }
    
    protected static class XTankManager implements Runnable {
        private Socket					socket;
        private static XTankConnection	connector;
        
        public XTankManager(Socket soc, XTankConnection connection) {
        	socket = soc;
        	connector = connection;
        }

        @Override
        public void run() 
        {
            System.out.println("Connected: " + socket);
            ObjectOutputStream out = null;
            int new_id = -1;
            try 
            {
            	out = new ObjectOutputStream(socket.getOutputStream());
            	out.flush();
            	int initial_x = 50;
            	int initial_y = 50;
            	int initial_angle = 0;
            	out.writeObject(new InputPacket(tanks.size(), initial_x, initial_y, initial_angle, false));
            	ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            	System.out.println("cat");
            	
                sq.add(out);
                new_id = getNewID();
                // add new tank
                tanks.put(new_id, new Tank(initial_x, initial_y, new_id));
                // send old tanks to new tank
                
                
                while (true)
                {
                	InputPacket input = (InputPacket)in.readObject();
                	// update tank position server side
                	final int SPEED = 5;
                	tanks.get(input.id).rotate(-input.x * SPEED);
                	System.out.println(tanks.get(input.id).getRotate());
                	tanks.get(input.id).moveForward(-input.y * SPEED);
                	if (map.collision(tanks.get(input.id).getX(),
            			tanks.get(input.id).getY(),
            			tanks.get(input.id).getX() + (Tank.width),
            			tanks.get(input.id).getY() + (Tank.height)))
        			{
                		tanks.get(input.id).moveForward(input.y * SPEED);
        			}
                	for (ObjectOutputStream o: sq)
                	{
                    	System.out.println("o = " + o);
                    	InputPacket to_client = new InputPacket(input.id, 
                    			tanks.get(input.id).getX(), tanks.get(input.id).getY(),
                    			tanks.get(input.id).getRotate(), false);
                    	
    					o.writeObject(to_client);
    					o.flush();
                	}
                }
            }
            catch (Exception e) 
            {
                System.out.println("Error:" + socket);
            } 
            finally 
            {
            	sq.remove(out);
            	if (new_id > -1)
            		tanks.remove(new_id);
            	connector.setPlCount(connector.getPlCount() - 1);
                try { socket.close(); } 
                catch (IOException e) {}
                System.out.println("Closed: " + socket);
            }
        }
    }
    
    protected static class XTankConnection implements Runnable {
        private Socket					socket;
        private static ServerSocket		listener;
        private static HostModel		hostModel;
        private static int				clients;
        
        public XTankConnection(ServerSocket listener) {
        	clients = 0;
        	XTankConnection.listener = listener;
        }

        @Override
        public void run() 
        {
        	while (acceptConnection) {
                try {
                	
                    socket = listener.accept();
            		clients++;

            		ExecutorService poolTest = Executors.newFixedThreadPool(5);
            		poolTest.execute(new XTankManager(socket, this));
                } catch (IOException e) {
                	acceptConnection = false;
                }
        	}
        	
        	if (!acceptConnection)
        		if (socket != null)
					try {
						socket.close();
					} catch (IOException e) {
					}
        }

        public static int getPlCount() {
        	return clients;
        }
        
        public static void setPlCount(int players) {
        	clients = players;
        }
    }

    public void closeServer() {
    	acceptConnection = false;
    	
    	try {
        	if (listener != null)
        		listener.close();
		} catch (IOException e) {}
    	
    	if (pool != null)
    		pool.shutdownNow();
    }
}


