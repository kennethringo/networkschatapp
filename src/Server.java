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
import java.util.concurrent.locks.Lock;
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
    Socket newConnection;
    public static ArrayList<Connection> connections = new ArrayList<>();
    public static boolean requestLocked = false;
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
                                

                                System.out.println(messageFromClient.getMessageType());
                                if (messageFromClient.getUserTo()==null)
                                {   

                                    /////////////////////////////only text
                                    if (messageFromClient.getMessageType().equals("textOnly")){
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
                                    }

                                    /////////////////////////////only image
                                    else if (messageFromClient.getMessageType().equals("imageOnly")){
                                        System.out.println(" on server side send image" );
                                        System.out.println(messageFromClient.getText());
                                        System.out.println(messageFromClient.getSize().length + " size " );
                                        String s = new String(messageFromClient.getByteArray());
                                        // System.out.println(s + " byte array vals");
                                        // //old
                                        for(Connection user: connections)
                                        {
                                            if(!userName.equals(user.getUserName()))
                                            {
                                                try
                                                {   
                                                    user.pendingMsgs.push(messageFromClient); //push new image onto user's stack of pending images

                                                    request_message(messageFromClient, null);
                                                    
                                                    
                                                }
                                                catch(Exception e)
                                                {
                                                    System.out.println(e);
                                                }
                                                
                                            }
                                        }
                                        
                                    }
                                    else if (messageFromClient.getMessageType().equals("imageResponse")){
                                        for(Connection user: connections)
                                        {
                                            if (messageFromClient.getUserFrom().equals(user.getUserName())){
                                                // System.out.println("response from server to: " + messageFromClient.getUserFrom());
                                                if (messageFromClient.getInformation().equals("yes")){
                                                    // System.out.println("client says yes");
                                                    
                                                    Message toSend = user.pendingMsgs.pop(); //pop image off and send to user

                                                    // System.out.println("toSend from: " + toSend.getUserFrom() +" to "+ toSend.getUserTo());
                                                    // System.out.println("toSend name: " + toSend.getText());
                                                    // System.out.println("toSend type: " + toSend.getMessageType());
                                                    // System.out.println("toSend byteArray: " + toSend.getByteArray());
                                                    user.getOutputStream().writeObject(toSend);
                                                    System.out.println("wroteObject");
                                                }
                                                else if (messageFromClient.getInformation().equals("no")){
                                                    user.pendingMsgs.peek();
                                                    // break;
                                                }else{
                                                    Message resendRequest = (Message)user.pendingMsgs.peek(); //resend request until user answers -r yes/ -r no
                                                    
                                                    request_message(resendRequest, null);
                                                    
                                                }
                                            }
                                        }
                                    }
                                    
                                }
                                else if (messageFromClient.getUserTo()!=null)
                                {
                                    boolean found = false;
                                    for(Connection user: connections)
                                    {
                                        if(messageFromClient.getUserTo().equals(user.getUserName()))
                                        {
                                            try
                                            {
                                                // user.getOutputStream().writeObject(new Message(messageFromClient));
                                                found = true;
                                                break;
                                            }
                                            catch(Exception e)
                                            {
                                                System.out.println(e);
                                            }
                                            
                                        }
                                    }
                                    if(!found)
                                    {
                                        Message m = new Message(messageFromClient.getUserTo() +" not found in chat", messageFromClient.getUserTo());
                                        m.setMessageType("serverResponse");
                                        outToClient.writeObject(m);
                                    }

                                    /////////////////////////////only text
                                    if (messageFromClient.getMessageType().equals("textOnly")){
                                        for(Connection user: connections)
                                        {
                                            if(messageFromClient.getUserTo().equals(user.getUserName()))
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
                                    }

                                    /////////////////////////////only image
                                    else if (messageFromClient.getMessageType().equals("imageOnly")){
                                        System.out.println(" on server side send image" );
                                        System.out.println(messageFromClient.getText());
                                        System.out.println(messageFromClient.getSize().length + " size " );
                                        String s = new String(messageFromClient.getByteArray());
                                        
                                        for(Connection user: connections)
                                        {
                                            if(messageFromClient.getUserTo().equals(user.getUserName()))
                                            {
                                                try
                                                {   
                                                    user.pendingMsgs.push(messageFromClient); //push new image onto user's stack of pending images

                                                    request_message(messageFromClient, messageFromClient.getUserTo());
                                                    
                                                    
                                                }
                                                catch(Exception e)
                                                {
                                                    System.out.println(e);
                                                }
                                                
                                            }
                                        }
                                        
                                    }
                                    else if (messageFromClient.getMessageType().equals("imageResponse")){
                                        for(Connection user: connections)
                                        {
                                            if (messageFromClient.getUserFrom().equals(user.getUserName())){
                                                // System.out.println("response from server to: " + messageFromClient.getUserFrom());
                                                if (messageFromClient.getInformation().equals("yes")){
                                                    // System.out.println("client says yes");
                                                    
                                                    Message toSend = user.pendingMsgs.pop(); //pop image off and send to user

                                                    // System.out.println("toSend from: " + toSend.getUserFrom() +" to "+ toSend.getUserTo());
                                                    // System.out.println("toSend name: " + toSend.getText());
                                                    // System.out.println("toSend type: " + toSend.getMessageType());
                                                    // System.out.println("toSend byteArray: " + toSend.getByteArray());
                                                    user.getOutputStream().writeObject(toSend);
                                                    System.out.println("wroteObject");
                                                }
                                                else if (messageFromClient.getInformation().equals("no")){
                                                    user.pendingMsgs.peek();
                                                    // break;
                                                }else{
                                                    Message resendRequest = (Message)user.pendingMsgs.peek(); //resend request until user answers -r yes/ -r no
                                                    
                                                    request_message(resendRequest, messageFromClient.getUserFrom());
                                                    
                                                }
                                            }
                                        }
                                    }
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
    void request_message(Message msgSend, String userTo){ //broadcast
        // boolean whoToSendTo [] = new boolean[connections.size()];
        if (userTo == null){
            for (int i = 0 ; i < connections.size();i++){
                if(!msgSend.getUserFrom().equals(connections.get(i).getUserName()))
                {
                    try
                    {   
                        System.out.println("from: " + msgSend.getUserFrom()+ " to: " + connections.get(i).getUserName());
                        Message request = new Message(msgSend.getUserFrom(),null ,msgSend.getUserFrom() + 
                            " would like to send you an image. Would you like to accept? \n '-r yes' / '-r no' to respond.", false);
                        System.out.println("this is request type: "+ request.getMessageType());
                        connections.get(i).getOutputStream().writeObject(request);
                    }
                    catch(Exception e)
                    {
                        System.out.println(e);
                    }
                    
                }
                
                
            }  
        }else{ //if specified user
            for (int i = 0 ; i < connections.size();i++){
                if (msgSend.getUserTo().equals(connections.get(i).getUserName())){
                    try
                    {   
                        System.out.println("from: " + msgSend.getUserFrom()+ " to: " + connections.get(i).getUserName());
                        Message request = new Message(msgSend.getUserFrom(),msgSend.getUserTo(),  msgSend.getUserFrom() + 
                            " would like to send you an image. Would you like to accept? \n '-r yes' / '-r no' to respond.", false);
                        System.out.println("this is request type: "+ request.getMessageType());
                        connections.get(i).getOutputStream().writeObject(request);
                    }
                    catch(Exception e)
                    {
                        System.out.println(e);
                    }
                }
            }
        }
        
        
       
    }

    // void request_message_single(Message msgSend){ //broadcast
    //     // boolean whoToSendTo [] = new boolean[connections.size()];
        
    //     for (int i = 0 ; i < connections.size();i++){
    //         if(msgSend.getUserTo().equals(connections.get(i).getUserName()))
    //         {
    //             try
    //             {   
    //                 System.out.println("from: " + msgSend.getUserFrom()+ " to: " + connections.get(i).getUserName());
    //                 Message request = new Message(msgSend.getUserFrom(), msgSend.getUserFrom() + 
    //                     " would like to send you an image. Would you like to accept? \n '-r yes' / '-r no' to respond.", false);
    //                 System.out.println("this is request type: "+ request.getMessageType());
    //                 connections.get(i).getOutputStream().writeObject(request);
    //             }
    //             catch(Exception e)
    //             {
    //                 System.out.println(e);
    //             }
                
    //         }
            
            
    //     }
        
       
    // }
    
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