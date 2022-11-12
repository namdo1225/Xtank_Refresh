/**
 * A class to represent the server itself with hosting capability
 * and threading to accompany it.
 * 
 * @author	Patrick Comden
 * @version	1.0
 * @since	2022-11-12
 */

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
	private static List<ObjectOutputStream> 		sq;
	private static Map<Integer, Tank>				tanks;
	private static List<Bullet>						bullets;

	private static ServerSocket						listener;
	private static List<Socket>						sockets;
	
	private static ExecutorService					pool;
	
	private static ExecutorService					bullet_thread;
	private static Lock								bullet_lock;
	private static Lock								tank_lock;
	private static GameMap							map;
	private static int								max_lives;
	private static final int						initial_x = 50;
	private static final int 						initial_y = 50;
	private static final int						initial_angle = 0;
	
    private static int								clients;
	
	/**
	 * Constructor for Server.
	 * 
	 * @param port		an int for the port number.
	 * @param mapNum	an int for the maze map's id.
	 */
    public Server(int port, int mapNum, int max_lives) {
		//System.out.println(InetAddress.getLocalHost());
    	clients = 0;
		sq = new ArrayList<ObjectOutputStream>();
		tanks = new HashMap<Integer, Tank>();
		
		sockets = new ArrayList<Socket>();
		
		bullets = new ArrayList<Bullet>();
		map = new GameMap(mapNum);
		Server.max_lives = max_lives;
		Bullet.setMap(map);
        try
        {
        	listener = new ServerSocket(port);
            //System.out.println("The XTank server is running...");
        	bullet_thread = Executors.newFixedThreadPool(1);
        	bullet_lock = new ReentrantLock();
        	tank_lock = new ReentrantLock();
            pool = Executors.newFixedThreadPool(4);
            pool.execute(new XTankConnection());
            bullet_thread.execute(new BulletManager());
            
        } catch (Exception e) {}
    }
    
    /**
     * A getter to get the players count.
     * 
     * @return	an int for the players count.
     */
    public int getPlCount() {
    	return clients;
    }
    
    /**
     * A method to return a new id to assign to a new tank.
     * 
     * @return	an int for the new id of the new tank.
     */
    public static int getNewID() {
    	int i = 0;
    	while (tanks.containsKey(i)) {
    		i++;
    	}
    	return i;
    }

    /**
     * A method to close the server socket and the threading related
     * to the server's task.
     */
    public void closeServer() {
    	try {
        	if (listener != null)
        		listener.close();
		} catch (IOException e) {}
    	
    	if (pool != null)
    		pool.shutdown();
    	if (bullet_thread != null)
    		bullet_thread.shutdown();
    	clients = 0;
    }
    
    private static void closeServeMisc() {
    	try {
        	if (listener != null)
        		listener.close();
		} catch (IOException e) {}
    	
    	for (Socket socket : sockets)
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	
    	if (bullet_thread != null)
    		bullet_thread.shutdown();
    	clients = 0;
    }
    
    /**
     * A Runnable-derived class to represent the task of managing bullets
     * for the server.
     * 
     * @author	Patrick Comden
     * @version	1.0
     * @since	2022-11-12
     */
    private static class BulletManager implements Runnable {
    	/**
    	 * A method to run the tasks, which manages bullets for the server.
    	 */
		@Override
		public void run() {
			while (true) {
				bullet_lock.lock();
				for (int i = 0; i < bullets.size(); i++) {
					if (!bullets.get(i).step()) {
						// remove bullet from clients
						for (var client : sq) {
							InputPacket bullet_update = new InputPacket(i, 0, 0, 0, true);
							bullet_update.is_bullet = true;
							bullet_update.delete = true;
							try {
								client.writeObject(bullet_update);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						
						bullets.remove(i);
						i--;

					}
					else {
						boolean deleted = false;
						for (var key : tanks.keySet()) {
							if (tanks.get(key).rectCollides(bullets.get(i).getX(), bullets.get(i).getY(),
									bullets.get(i).getX() + Bullet.size, bullets.get(i).getY() + Bullet.size))
							{
								bullets.remove(i);
								i--;
								// remove bullet from clients
								for (var client : sq) {
									InputPacket bullet_update = new InputPacket(i + 1, 0, 0, 0, false);
									bullet_update.is_bullet = true;
									bullet_update.delete = true;
									try {
										client.writeObject(bullet_update);
									} catch (IOException e) {
										e.printStackTrace();
									}
								}
								int lives = tanks.get(key).getLives();
								if (!tanks.get(key).hit()) {
									tank_lock.lock();
									// kill tank
									tanks.remove(key);
									InputPacket tank_delete = new InputPacket(key, 0, 0, 0, false);
									tank_delete.delete = true;
									for (var client : sq) {
										try {
											client.writeObject(tank_delete);
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}
									// check if there is winner
									if (tanks.size() == 1) { 
										for (var winner_id : tanks.keySet()) {
											System.out.println("Winner!!! "  + winner_id);
											// -69 is out of range, reserved for winner
											InputPacket winner_packet = new InputPacket(winner_id, -69, -69, -69, false);
											for (var client : sq) {
												try {
													client.writeObject(winner_packet);
													
													
												} catch (IOException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
											}
											
											break;
										}
										
										closeServeMisc();
										return;
									}
									
									tank_lock.unlock();
								}
								else {
									if (lives != tanks.get(key).getLives()) {
										tanks.get(key).set(initial_x, initial_y, initial_angle, tanks.get(key).getArmor());
										for (var client : sq) {
											InputPacket tank_reset = new InputPacket(key, initial_x, initial_y, initial_angle, false);
											try {
												client.writeObject(tank_reset);
											} catch (IOException e) {
												e.printStackTrace();
											}
										}
									}
									else {
										// update visual armor client-side
										for (ObjectOutputStream o: sq)
					                	{
					                    	InputPacket to_client = new InputPacket(key, 
					                    			tanks.get(key).getX(), tanks.get(key).getY(),
					                    			tanks.get(key).getRotate(), false);
					                    	to_client.armor = tanks.get(key).getArmor();
					    					try {
												o.writeObject(to_client);
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
					                	}
									}
								}
								deleted = true;
								break;
							}
						}
						if (deleted)
							continue;
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
    
    /**
     * A Runnable-derived class to represent the task of managing tanks,
     * their input from the clients, and their output to the clients to maintain
     * game flow.
     * 
     * @author	Patrick Comden
     * @version	1.0
     * @since	2022-11-12
     */
    private static class XTankManager implements Runnable {
        private Socket					socket;
        
        /**
         * Constructor for XTankManager.
         * 
         * @param soc		a Socket representing the player's connection.
         */
        public XTankManager(Socket soc) {
        	socket = soc;
        }

        /**
         * A method to run the task, which is managing the tanks, clients' inputs,
         * and server's outputs to maintain game flow.
         */
        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            ObjectOutputStream out = null;
            int new_id = -1;
            try {
            	out = new ObjectOutputStream(socket.getOutputStream());
            	ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            	out.flush();
            	
            	new_id = getNewID();
            	out.writeObject(map.getID());
            	int armor_type = (Integer)in.readObject();
            	System.out.println("Model: " + armor_type);
            	int tank_armor = 0;
            	int shoot_speed = 0;
            	switch (armor_type) {
            	case 1:
            		tank_armor = 3;
            		shoot_speed = 10;
            		break;
            	case 2:
            		tank_armor = 2;
            		shoot_speed = 20;
            		break;
            	}
                // add new tank
            	InputPacket initial_tank = new InputPacket(new_id, initial_x, initial_y, initial_angle, false);
            	initial_tank.armor = tank_armor;
            	out.writeObject(initial_tank);
            	for (var client : sq) {
            		if (client != out)
            			client.writeObject(initial_tank);
            	}
            	// send num of tanks
            	out.writeObject(tanks.size());
            	// loop through all tanks and send
            	for (var key : tanks.keySet()) {
            		InputPacket packet = new InputPacket(tanks.get(key).getID(),
            				tanks.get(key).getX(), tanks.get(key).getY(), tanks.get(key).getRotate(), false);
            		packet.armor = tanks.get(key).getArmor();
            		out.writeObject(packet);
            	}
            	//System.out.println(3);
            	tanks.put(new_id, new Tank(initial_x, initial_y, new_id, max_lives, tank_armor));
            	tanks.get(new_id).setShootSpeed(shoot_speed);
            	System.out.println(tanks.get(new_id).getArmor() + " asfsdgf sdgregyregt");
            	//System.out.println(4);
            	
                sq.add(out);
                //System.out.println(5);
                // send old tanks to new tank
                long time = 0;
                long start = System.nanoTime() / 1000000;
                while (!socket.isClosed()) {
                	InputPacket input = (InputPacket)in.readObject();
                	// update tank position server side
                	final int SPEED = 5;
                	tank_lock.lock();
                	if (tanks.containsKey(input.id)) {
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
	                	time = (System.nanoTime() / 1000000) - start;
	                	if (input.shoot) {
	                		System.out.println(time);
	                		// one second
	                		if (time > 1000) {
	                			start = System.nanoTime() / 1000000;
	                			time = 0;
			            		System.out.println("shoot");
			            		float x = (float) (tank.getX() + (Tank.width / 2) + tank.getDirectionX() * 50);
			            		float y = (float) (tank.getY() + (Tank.height / 2) - tank.getDirectionY() * 50);
			            		bullet_lock.lock();
			            		bullets.add(new Bullet(x, y, tank.getRotate(), tanks.get(tank.getID()).getShootSpeed()));
			            		bullet_lock.unlock();
	                		}
	                	}
	                	for (ObjectOutputStream o: sq)
	                	{
	                    	InputPacket to_client = new InputPacket(input.id, 
	                    			tanks.get(input.id).getX(), tanks.get(input.id).getY(),
	                    			tanks.get(input.id).getRotate(), false);
	                    	to_client.armor = tanks.get(input.id).getArmor();
	    					o.writeObject(to_client);
	    					o.flush();
	                	}
                	}
                	tank_lock.unlock();
                }
            }
            catch (Exception e) {
                System.out.println("Error:" + socket);
            } 
            finally {
            	sockets.remove(socket);
            	sq.remove(out);
            	if (new_id > -1)
            		tanks.remove(new_id);
            	clients--;
            	
				// kill tank
				InputPacket tank_delete = new InputPacket(new_id, 0, 0, 0, false);
				tank_delete.delete = true;
				for (var client : sq) {
					try {
						client.writeObject(tank_delete);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
            }
        }
    }
    
    /**
     * A Runnable-derived class to represent the task of accepting new connections
     * for the server.
     * 
     * @author	Patrick Comden
     * @version	1.0
     * @since	2022-11-12
     */
    private static class XTankConnection implements Runnable {
        private Socket					socket;
        
        /**
         * Constructor for XTankConnection.
         */
        public XTankConnection() {
        	clients = 0;
        }

        /**
         * A method to run the task, which keeps accepting new player connections.
         */
        @Override
        public void run() {
        	while (true) {
                try {
                	socket = listener.accept();
                	sockets.add(socket);
            		clients++;

            		ExecutorService poolTest = Executors.newFixedThreadPool(5);
            		poolTest.execute(new XTankManager(socket));
                } catch (IOException e) {}
        	}
        }
    }
}