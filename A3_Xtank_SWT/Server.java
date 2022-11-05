
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.net.InetAddress;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;

/**
 * When a client connects, a new thread is started to handle it.
 */
public class Server 
{
	static ArrayList<ObjectOutputStream> sq;
	static ArrayList<Tank> tanks; // TODO: change to map
	
    public static void main(String[] args) throws Exception 
    {
		//System.out.println(InetAddress.getLocalHost());
		sq = new ArrayList<>();
		tanks = new ArrayList<>();
        try (var listener = new ServerSocket(12346)) 
        {
            //System.out.println("The XTank server is running...");
            var pool = Executors.newFixedThreadPool(19);
            while (true) 
            {
                pool.execute(new XTankManager(listener.accept()));
            }
        }
    }

    private static class XTankManager implements Runnable 
    {
        private Socket socket;

        XTankManager(Socket socket) { this.socket = socket; }

        @Override
        public void run() 
        {
            System.out.println("Connected: " + socket);
            ObjectOutputStream out = null;
            try 
            {
            	out = new ObjectOutputStream(socket.getOutputStream());
            	out.flush();
            	int initial_x = 0;
            	int initial_y = 0;
            	out.writeObject(new InputPacket(tanks.size(), initial_x, initial_y, 0));
            	ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            	System.out.println("cat");
            	
                sq.add(out);
                tanks.add(new Tank(initial_x, initial_y, tanks.size()));
                
                while (true)
                {
                	InputPacket input = (InputPacket)in.readObject();
                	// update tank position server side
                	final int SPEED = 5;
                	tanks.get(input.id).move(input.x * SPEED, input.y * SPEED);
                	System.out.println(input.y == 1);
                	for (ObjectOutputStream o: sq)
                	{
                    	System.out.println("o = " + o);
                    	InputPacket to_client = new InputPacket(input.id, 
                    			tanks.get(input.id).getX(), tanks.get(input.id).getY(), 0);
                    	
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
            	tanks.remove(tanks.size() - 1);
                try { socket.close(); } 
                catch (IOException e) {}
                System.out.println("Closed: " + socket);
            }
        }
    }
    
}


