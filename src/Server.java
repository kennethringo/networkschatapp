import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * @author sbnnko004
 * @date 2018/03/12
 */

public class Server {
    private ServerSocket welcomeSocket=null;
    public static ArrayList<Connection> connections = new ArrayList<>();
    
    public static void main(String args[]) throws Exception {
        new Server();       
    }

    private void checkConnections()
    {
    	for(Connection each:connections)
    	{
    		if(!each.connection.isConnected())
    		{
    			return;
    		}
    	}
    }
    
    private Server() 
    {
        Connection connection;
        System.out.println("Waiting for connection...");
		try
        {
            welcomeSocket = new ServerSocket(7000);
            while(true)
            {
                Socket newConnection=null;
		newConnection = welcomeSocket.accept();
		System.out.println("Connection made");
		InputStream inFromClient = newConnection.getInputStream();
		ObjectInputStream in = new ObjectInputStream(inFromClient);
		String userName=(String)in.readObject();
			               
                String onlineUsers="";
	        int count=1;
	            
	        for(Connection user:connections)
	        {
	            onlineUsers+=count+" "+user.getUserName()+"\n";
	        }
	                        
	        OutputStream out = newConnection.getOutputStream();
	        ObjectOutputStream outToClient = new ObjectOutputStream(out);
	        outToClient.writeObject(onlineUsers);
	                        
	        System.out.println("Connection made with "+userName);
	        connection = new Connection(newConnection);
	                        
	        connection.setUserName(userName);
	        connections.add(connection);

	        System.out.println("Waiting for another connection...");

	        checkConnections();

	            
	    }
        }

        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
   
    }
}
