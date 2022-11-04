
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
	
    public static void main(String[] args) throws Exception 
    {
		//System.out.println(InetAddress.getLocalHost());
		sq = new ArrayList<>();
		
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
            	out.writeObject(new InputPacket(69, 0, 1, 0));
            	ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            	System.out.println("cat");
            	
                sq.add(out);
                while (true)
                {
                	InputPacket input = (InputPacket)in.readObject();
                	System.out.println(input.y == 1);
                	for (ObjectOutputStream o: sq)
                	{
                    	System.out.println("o = " + o);
    					o.writeObject(input);
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
                try { socket.close(); } 
                catch (IOException e) {}
                System.out.println("Closed: " + socket);
            }
        }
    }
    
}


