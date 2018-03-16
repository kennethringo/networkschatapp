
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
    public InputStream inFromClient;
public ObjectInputStream in;
    private ServerSocket welcomeSocket=null;
    public static ArrayList<Connection> connections = new ArrayList<>();
    
    public static void main(String args[]) throws Exception {
        new Server();       
    }
    
    private Server() throws ClassNotFoundException 
    {
        
        
        try
        {
            welcomeSocket = new ServerSocket(7700);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
	
	System.out.println("Waiting for connection...");
	
	// thread for making connections
	Thread makingConnections = new Thread(){
		public void run(){
		while(true)
		{
			try
			{
				
				Connection connection=null;
				Socket newConnection=null;
				newConnection = welcomeSocket.accept();
				System.out.println("Connection made");
				inFromClient = newConnection.getInputStream();
				in = new ObjectInputStream(inFromClient);
				String userName=(String)in.readObject();
				//
				//connection.setUserName(inFromClient.readLine());
		                System.out.println("Here");
				String onlineUsers="";
		                int count=1;
		                for(Connection user:connections)
		                {
				    user.send_message(new Message(userName+ " joined chat."));		                
				    onlineUsers+=count+" "+user.getUserName()+"\n";
		                }
		                
		                OutputStream out = newConnection.getOutputStream();
		                ObjectOutputStream outToClient = new ObjectOutputStream(out);
				//System
		                outToClient.writeObject(onlineUsers);
		                
		                System.out.println("Connection made with "+userName);
		                connection = new Connection(newConnection);
		                System.out.println("g");
		                connection.setUserName(userName);
				//connection.start();
		                connections.add(connection);
				// send out connected users notification here

		                
				
			}
			catch (IOException e)
			{
				System.out.println(e);		
			}
			catch (ClassNotFoundException e)
			{
				System.out.println(e);		
			}
		}
	    }
	};makingConnections.start();
	/**
        Object[] connection = new Object[2];S
	connection[0] = welcomeSocket.accept();
	System.out.println("ConnectionMade");
	BufferedReader inFromClient = new BufferedReader(new InputStreamReader(((Socket)connection[0]).getInputStream()));
	connection[1] = inFromClient.readLine();
        System.out.println("Connection made with "+(String)connection[1]);
        DataOutputStream outToClient = new DataOutputStream(((Socket)connection[0]).getOutputStream());
	
	outToClient.writeBytes((String)connection[1]);
	**/		
        	
		
	

        
    }
}
