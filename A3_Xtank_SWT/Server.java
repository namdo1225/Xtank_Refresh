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
	private static List<ObjectOutputStream> 		outs;
	private static Map<Integer, Tank>				tanks;
	private static List<Bullet>						bullets;

	private static ServerSocket						listener;
	private static List<Socket>						sockets;
	
	private static ExecutorService					pool;
	
	private static ExecutorService					bulletThread;
	private static Lock								bulletLock;
	private static Lock								tankLock;
	private static GameMap							map;
	private static int								maxLives;
	private static final int						initialX = 50;
	private static final int 						initialY = 50;
	private static final int						initialAng = 0;
	
    private static int								clients;
	private static int								winner;
    
	/**
	 * Constructor for Server.
	 * 
	 * @param port		an int for the port number.
	 * @param mapNum	an int for the maze map's id.
	 */
    public Server(int port, int mapNum, int maxLives) {
    	clients = 0;
    	
    	outs = new ArrayList<ObjectOutputStream>();
		tanks = new HashMap<Integer, Tank>();
		sockets = new ArrayList<Socket>();
		bullets = new ArrayList<Bullet>();
		map = new GameMap(mapNum);
		
		Server.maxLives = maxLives;
		Bullet.setMap(map);
		
        try
        {
        	listener = new ServerSocket(port);
        	bulletThread = Executors.newFixedThreadPool(1);
        	bulletLock = new ReentrantLock();
        	tankLock = new ReentrantLock();
            pool = Executors.newFixedThreadPool(4);
            pool.execute(new XTankConnection());
            bulletThread.execute(new BulletManager());
            
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
    	if (bulletThread != null)
    		bulletThread.shutdown();
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
			} catch (IOException e) {}
    	
    	if (bulletThread != null)
    		bulletThread.shutdown();
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
				bulletLock.lock();
				for (int i = 0; i < bullets.size(); i++) {
					if (!bullets.get(i).step()) {
						removeBulletWall(i);
						i--;
					}
					else {
						boolean deleted = false;
						for (Integer key : tanks.keySet()) {
							if (tanks.get(key).rectCollides(bullets.get(i).getX(), bullets.get(i).getY(),
									bullets.get(i).getX() + Bullet.size, bullets.get(i).getY() + Bullet.size))
							{
								removeBulletWall(i);
								i--;
								
								int lives = tanks.get(key).getLives();
								if (!tanks.get(key).hit()) {
									playerIsHit(key);
									if (tanks.size() < 2) {
										closeServeMisc();
										return;
									}
								}
								else {
									if (lives != tanks.get(key).getLives())
										updatePlayerLives(key);
									else
										updateArmor(key);
								}
								deleted = true;
								break;
							}
						}
						if (deleted)
							continue;
						for (ObjectOutputStream client : outs) {
							InputPacket bulletUpdate = new InputPacket(i, (int)bullets.get(i).getX(),
																		(int)bullets.get(i).getY(),
																		0, false);
							bulletUpdate.isBullet = true;
							try {
								client.writeObject(bulletUpdate);
							} catch (IOException e) {}
						}
					}
				}
				bulletLock.unlock();
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			}
		}
		
		/**
		 * Remove a bullet from server and client.
		 * 
		 * @param i		an int representing the exact bullet to remove.
		 */
		private void removeBulletWall(int i) {
			// remove bullet from clients
			for (ObjectOutputStream client : outs) {
				InputPacket bullet_update = new InputPacket(i, 0, 0, 0, true);
				bullet_update.isBullet = true;
				bullet_update.delete = true;
				try {
					client.writeObject(bullet_update);
				} catch (IOException e) {}
			}
			
			bullets.remove(i);
		}
    
		/**
		 * Handle an event where a player is hit by a bullet.
		 * 
		 * @param key	an Integer referring to the key in a map of Tank
		 * 				objects.
		 */
		private void playerIsHit(Integer key) {
			tankLock.lock();
			
			// kill tank
			tanks.remove(key);
			InputPacket tank_delete = new InputPacket(key, 0, 0, 0, false);
			tank_delete.delete = true;
			for (ObjectOutputStream client : outs) {
				try {
					client.writeObject(tank_delete);
				} catch (IOException e) {}
			}
			
			// check if there is winner
			if (tanks.size() == 1) { 
				for (Integer winnerID : tanks.keySet()) {
					// -69 is out of range, reserved for winner
					InputPacket winnerPack = new InputPacket(winnerID, -69, -69, -69, false);
					for (ObjectOutputStream client : outs) {
						try {
							client.writeObject(winnerPack);
						} catch (IOException e) {}
					}
					
					break;
				}
			}
			
			tankLock.unlock();
		}
		
		/**
		 * Update players' lives and send the updated data to clients.
		 * 
		 * @param key	an Integer referring to the key in a map of Tank
		 * 				objects.
		 */
		private void updatePlayerLives(Integer key) {
			tanks.get(key).set(initialX, initialY, initialAng, tanks.get(key).getArmor());
			for (ObjectOutputStream client : outs) {
				InputPacket tank_reset = new InputPacket(key, initialX, initialY, initialAng, false);
				try {
					client.writeObject(tank_reset);
				} catch (IOException e) {}
			}
		}
		
		/**
		 * Update the armor visual on the client-side.
		 * 
		 * @param key	an Integer that acts as the key to a map of
		 * 				Tank objects.
		 */
		private void updateArmor(Integer key) {
			// update visual armor client-side
			for (ObjectOutputStream o: outs) {
            	InputPacket to_client = new InputPacket(key, 
            			tanks.get(key).getX(), tanks.get(key).getY(),
            			tanks.get(key).getRotate(), false);
            	to_client.armor = tanks.get(key).getArmor();
				try {
					o.writeObject(to_client);
				} catch (IOException e) {}
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
            ObjectOutputStream out = null;
            int newID = -1;
            try {
            	out = new ObjectOutputStream(socket.getOutputStream());
            	ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            	out.flush();
            	
            	newID = getNewID();
            	out.writeObject(map.getID());
            	
            	int armType = (Integer)in.readObject();
            	int tankArm = 0;
            	int shootSpd = 0;
            	switch (armType) {
            	case 1:
            		tankArm = 3; shootSpd = 10;
            		break;
            	case 2:
            		tankArm = 2; shootSpd = 20;
            		break;
            	}

            	addNewTank(newID, tankArm, shootSpd, out);
            	
                outs.add(out);
                
                // send old tanks to new tank
                long time = 0;
                long start = System.nanoTime() / 1000000;
                while (!socket.isClosed()) {
                	InputPacket input = (InputPacket)in.readObject();
                	
                	// update tank position server side
                	final int SPEED = 5;
                	tankLock.lock();
                	if (tanks.containsKey(input.id)) {
	                	Tank tank = tanks.get(input.id);
	                	tank.rotate(-input.x * SPEED);
	                	tanks.get(input.id).moveForward(-input.y * SPEED);
	                	
	                	playerWallCollision(input, SPEED);
	                	
	                	time = (System.nanoTime() / 1000000) - start;
	                	if (input.shoot) {
	                		// 2000 = 2 seconds.
	                		if (time > 2000) {
	                			start = System.nanoTime() / 1000000;
	                			time = 0;
	                    		float x = (float) (tank.getX() + (Tank.width / 2) + tank.getDirectionX() * 50);
	                    		float y = (float) (tank.getY() + (Tank.height / 2) - tank.getDirectionY() * 50);
	                    		bulletLock.lock();
	                    		bullets.add(new Bullet(x, y, tank.getRotate(), tanks.get(tank.getID()).getShootSpeed()));
	                    		bulletLock.unlock();
	                		}
	                	}

	                	sendPacketToPlayers(input);
                	}
                	tankLock.unlock();
                }
            }
            catch (Exception e) {} 
            finally {
            	cleanTank(newID, out);
            }
        }
        
        /**
         * Add tanks to the new client.
         * 
         * @param newID		an int for the new client's tank ID.
         * @param armor		an int for the new tank's armor type.
         * @param shootSpd	an int for the new tank's shooting speed.
         * @param out		an ObjectOutputStream to send data out to the client.
         */
        private void addNewTank(int newID, int armor, int shootSpd, ObjectOutputStream out) {
        	 // add new tank
        	InputPacket initial_tank = new InputPacket(newID, initialX, initialY, initialAng, false);
        	initial_tank.armor = armor;
        	try {
        		out.writeObject(initial_tank);

        		for (ObjectOutputStream client : outs) {
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
        	} catch (IOException e) {}
        	
        	tanks.put(newID, new Tank(initialX, initialY, newID, maxLives, armor));
        	tanks.get(newID).setShootSpeed(shootSpd);
        }
        
        private void playerWallCollision(InputPacket input, final int SPEED) {
        	if (map.collision(tanks.get(input.id).getX(),
    			tanks.get(input.id).getY(),
    			tanks.get(input.id).getX() + (Tank.width),
    			tanks.get(input.id).getY() + (Tank.height)))
			{
        		tanks.get(input.id).moveForward(input.y * SPEED);
			}
        }
        
        /**
         * Send current player's tank information to other players.
         * 
         * @param input		an InputPacket to compile all data needed to send to
         * 					other players.
         */
        private void sendPacketToPlayers(InputPacket input) {
        	for (ObjectOutputStream o: outs) {
            	InputPacket to_client = new InputPacket(input.id, 
            			tanks.get(input.id).getX(), tanks.get(input.id).getY(),
            			tanks.get(input.id).getRotate(), false);
            	to_client.armor = tanks.get(input.id).getArmor();
				try {
					o.writeObject(to_client);
					o.flush();
				} catch (IOException e) {}
        	}
        }
        
        /**
         * Clean the current socket's tank from the server and other clients.
         * 
         * @param newID		an int representing the tank to be cleaned.
         * @param out		an ObjectOutputStream to send data to the clients.
         */
        private void cleanTank(int newID, ObjectOutputStream out) {
        	sockets.remove(socket);
        	outs.remove(out);
        	if (newID > -1)
        		tanks.remove(newID);
        	clients--;
        	
			// kill tank
			InputPacket tank_delete = new InputPacket(newID, 0, 0, 0, false);
			tank_delete.delete = true;
			for (ObjectOutputStream client : outs) {
				try {
					client.writeObject(tank_delete);
				} catch (IOException e) {}
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