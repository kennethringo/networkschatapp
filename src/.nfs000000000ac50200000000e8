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
import java.nio.ByteBuffer;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
/**
 * 
 * @author sbnnko004
 * @date 2018/03/12
 */

public class Server {
    private ServerSocket welcomeSocket=null;
    private InputStream in;
    private OutputStream out;
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
                            
                            in = newConnection.getInputStream();
                            out = newConnection.getOutputStream();
                            ObjectInputStream inFromClient = new ObjectInputStream(in);
                            ObjectOutputStream outToClient = new ObjectOutputStream(out);

                            String userName=(String)inFromClient.readObject();

                            String onlineUsers="";
                            int count=1;

                            for(Connection user:connections)
                            {
                                onlineUsers+=count+". "+user.getUserName()+"\n";
                                count++;
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
                                /*if(messageFromClient.getUserFrom().equals(userName))
                                {
                                    try
                                    {
                                        outToClient.writeObject(new Message(m));
                                    }
                                    catch(Exception e)
                                    {
                                        System.out.println(e);
                                    }
                                }*/
                                // if (messageFromClient != null){
                                    System.out.println("before message type");
                                    System.out.println("this is the type:" + messageFromClient.getMessageType());
                                    
                                    if (messageFromClient.getMessageType().equals("imageOnly")){
                                        System.out.println("in receive message");
                                        in = newConnection.getInputStream();

                                        byte[] sizeAr = new byte[4];
                                        inFromClient.read(sizeAr);
                                        int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

                                        byte[] imageAr = new byte[size];
                                        inFromClient.read(imageAr);

                                        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));

                                        System.out.println("Received " + image.getHeight() + "x" + image.getWidth() + ": " + System.currentTimeMillis());
                                        for(Connection user: connections)
                                        {
                                            if(!userName.equals(user.getUserName()))
                                            {
                                                try
                                                {
                                                    user.getOutputStream().writeObject(new Message(messageFromClient));
                                                }
                                                catch(Exception e)
                                                {
                                                    System.out.println(e);
                                                }
                                                
                                            }
                                        }
                                        // ImageIO.write(image, "jpg", new File("C:\\Users\\Jakub\\Pictures\\test2.jpg"));
                                    }
                                    for(Connection user: connections)
                                    {
                                        if(!userName.equals(user.getUserName()))
                                        {
                                            try
                                            {
                                                user.getOutputStream().writeObject(new Message(messageFromClient));
                                            }
                                            catch(Exception e)
                                            {
                                                System.out.println(e);
                                            }
                                            
                                        }
                                    }
                                // }
                                // else {
                                //     messageFromClient=(Message)inFromClient.readObject();
                                // }
                                
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
    
   
    void send_message(Message m, String userTo)
    {
    	for(Connection user: connections)
        {
            if(userTo.equals(user.getUserName()))
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
