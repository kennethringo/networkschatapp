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
    Socket newConnection;
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
        
        System.out.println("Waiting for connection...");
		try
        {
            welcomeSocket = new ServerSocket(7003);
            while(true)
            {
                newConnection = welcomeSocket.accept();
                Thread socket = new Thread()
                {
                    @Override
                    public void run()
                    {
                        
                        try
                        {
                            
                            InputStream in = newConnection.getInputStream();
                            OutputStream out = newConnection.getOutputStream();
                            ObjectInputStream inFromClient = new ObjectInputStream(in);
                            ObjectOutputStream outToClient = new ObjectOutputStream(out);

                            String userName=(String)inFromClient.readObject();

                            String onlineUsers="";
                            int count=1;

                            for(Connection user:connections)
                            {
                                onlineUsers+=count+" "+user.getUserName()+"\n";
                            }

                            
                            //outToClient ;
                            outToClient.writeObject(onlineUsers);

                            System.out.println("Connection made with "+userName+"\n"+"Waiting for another connection...");
                            Connection connection = new Connection(newConnection,inFromClient,outToClient);
                            connection.setUserName(userName);
                            broadcast_message(userName+" joined group.",userName);
                            connections.add(connection);

                            while(true)
                            {
                                Message messageFromClient=(Message)inFromClient.readObject();
                                //if(messageFromClient)
                                //System.out.println("Here");
                                if(messageFromClient.getUserFrom().equals(userName))
                                {

                                }
                            }
                            
                            
                        }
                        catch(Exception e)
                        {
                            System.out.println("I'm not entirely sure how this is supposed to work");
                            System.out.println(e);
                        }
                    }
                };socket.start();
	        

	        

	        checkConnections();
                
                

	            
	    }
        }

        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        
   
    }
    
    void broadcast_message(String m, String userName)
    {
        for(Connection user: connections)
        {
            if(!userName.equals(user.getUserName()))
            {
                try
                {
                    user.getOutputStream().writeObject(new Message(m));
                }
                catch(Exception e)
                {
                    System.out.println(e);
                }
                
            }
        }   
    }
}
