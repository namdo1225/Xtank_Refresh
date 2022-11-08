
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.net.InetAddress;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * When a client connects, a new thread is started to handle it.
 */
public class Server 
{
	private static List<ObjectOutputStream> 		sq;
	private static HashMap<Integer, Tank>			tanks;
	private static ArrayList<Bullet>				bullets;

	private static ServerSocket						listener;
	private static ExecutorService					pool;
	private static ExecutorService					closeThread;
	
	private static ExecutorService					bullet_thread;
	private static Lock								bullet_lock;
	private static GameMap							map;
	
	private static boolean							acceptConnection;
	private static boolean							isAcceptingNew;
	
	//private static List<ExecutorService>			poolIOs;
	
    public Server(int port, int mapNum) {
		//System.out.println(InetAddress.getLocalHost());
    	isAcceptingNew = true;
    	acceptConnection = true;
		sq = new ArrayList<ObjectOutputStream>();
		tanks = new HashMap<Integer, Tank>();
		bullets = new ArrayList<Bullet>();
		map = new GameMap(mapNum);
		Bullet.setMap(map);
        try
        {
        	listener = new ServerSocket(port);
            //System.out.println("The XTank server is running...");
        	bullet_thread = Executors.newFixedThreadPool(1);
        	bullet_lock = new ReentrantLock();
            pool = Executors.newFixedThreadPool(4);
            pool.execute(new XTankConnection());
            bullet_thread.execute(new BulletManager());
            
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
    
    public void stopNewConnection() {
    	isAcceptingNew = false;
    	acceptConnection = false;
    	
    	if (acceptConnection) {
    	
    		if (pool != null) {

    			pool.shutdownNow();
    			try {
    				pool.awaitTermination(100, TimeUnit.MICROSECONDS);
    				System.out.println(pool.isShutdown() + " " + pool.isTerminated());
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		}

    		closeThread = Executors.newFixedThreadPool(1);
    		closeThread.execute(new XTankClose());
    	}
    }

    protected static class BulletManager implements Runnable {

		@Override
		public void run() {
			while (true) {
				bullet_lock.lock();
				for (int i = 0; i < bullets.size(); i++) {
					if (!bullets.get(i).step()) {
						bullets.remove(i);
						i--;
						// remove bullet from clients
						for (var client : sq) {
							InputPacket bullet_update = new InputPacket(i + 1, 0, 0, 0, true);
							bullet_update.is_bullet = true;
							try {
								client.writeObject(bullet_update);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					else {
						for (var client : sq) {
							InputPacket bullet_update = new InputPacket(i, (int)bullets.get(i).getX(), (int)bullets.get(i).getY(),
																		0, false);
							bullet_update.is_bullet = true;
							try {
								client.writeObject(bullet_update);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						System.out.println(i + " " + bullets.get(i).getX() + " " + bullets.get(i).getY());
					}
				}
				bullet_lock.unlock();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
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
            	new_id = getNewID();
            	System.out.println("Adding new tank: " + new_id);
            	out.writeObject(map.getID());
                // add new tank
            	out.writeObject(new InputPacket(new_id, initial_x, initial_y, initial_angle, false));
            	System.out.println(1);
            	// send num of tanks
            	out.writeObject(tanks.size());
            	System.out.println(2);
            	// loop through all tanks and send
            	for (var key : tanks.keySet()) {
            		InputPacket packet = new InputPacket(tanks.get(key).getID(),
            				tanks.get(key).getX(), tanks.get(key).getX(), tanks.get(key).getRotate(), false);
            		out.writeObject(packet);
            	}
            	//System.out.println(3);
            	tanks.put(new_id, new Tank(initial_x, initial_y, new_id));
            	ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            	
            	//System.out.println(4);
            	
                sq.add(out);
                //System.out.println(5);
                // send old tanks to new tank
                while (true)
                {
                	InputPacket input = (InputPacket)in.readObject();
                	// update tank position server side
                	final int SPEED = 5;
                	Tank tank = tanks.get(input.id);
                	tank.rotate(-input.x * SPEED);
                	//System.out.println(tanks.get(input.id).getRotate());
                	tanks.get(input.id).moveForward(-input.y * SPEED);
                	if (map.collision(tanks.get(input.id).getX(),
            			tanks.get(input.id).getY(),
            			tanks.get(input.id).getX() + (Tank.width),
            			tanks.get(input.id).getY() + (Tank.height)))
        			{
                		tanks.get(input.id).moveForward(input.y * SPEED);
        			}
                	if (input.shoot) {
                		System.out.println("shoot");
                		float x = (float) (tank.getX() + tank.getDirectionX() * 100);
                		float y = (float) (tank.getY() + tank.getDirectionY() * 100);
                		bullet_lock.lock();
                		bullets.add(new Bullet(x, y, tank.getRotate(), 10));
                		bullet_lock.unlock();
                	}
                	for (ObjectOutputStream o: sq)
                	{
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
            	Server.XTankConnection.setPlCount(Server.XTankConnection.getPlCount() - 1);
                try { socket.close(); } 
                catch (IOException e) {}
                System.out.println("Closed: " + socket);
            }
        }
    }
    
    protected static class XTankConnection implements Runnable {
        private Socket					socket;
        private static int				clients;
        
        public XTankConnection() {
        	clients = 0;
        }

        @Override
        public void run() 
        {
        	while (acceptConnection) {
                try {
                	if (isAcceptingNew)
                		socket = listener.accept();
                	
                	if (!isAcceptingNew)
                		if (socket != null)
                			socket.close();
                	
                	if (isAcceptingNew) {
            		clients++;

            		ExecutorService poolTest = Executors.newFixedThreadPool(5);
            		poolTest.execute(new XTankManager(socket, this));
                	}
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

    protected static class XTankClose implements Runnable {
        private Socket					socket;

        @Override
        public void run() 
        {
        	while (true) {
                try {
                	socket = listener.accept();
                	if (socket != null)
                		socket.close();
                } catch (IOException e) {}
        	}

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
    	
    	if (closeThread != null)
    		closeThread.shutdownNow();
    }
}


