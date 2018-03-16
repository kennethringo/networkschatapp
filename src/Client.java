
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Client 
{
    private Socket clientSocket;
    private Scanner inFromUser = new Scanner(System.in);
    private ObjectOutputStream outToServer;
    //private IncomingMessageListener messageListener;
    private String clientName;
    
    public static void main(String[] args)
    {
        
        new Client();
    }
    
    private Client()
    {
        System.out.print("Enter Servers IP (default is localhost): ");
        String IP = inFromUser.nextLine();
        if (IP.equals(""))
        {
            IP = "localhost"; 
        }
        
        try{
            clientSocket = new Socket(IP, 7700);
	    System.out.println("Connection with server "+IP+" made.");
        }
        catch(IOException e){
            System.out.println(e);
            System.exit(0);
        }
        
        
        
        ObjectOutputStream outToServer;
        ObjectInputStream  inFromServer;
        try
        {
	    OutputStream out = clientSocket.getOutputStream();
	    outToServer = new ObjectOutputStream(out);
	    
            System.out.println("Enter you preferred username: ");
	    outToServer.writeObject((new Scanner(System.in)).next());
	    
	    
	    System.out.println("\nConnected Users:");
            InputStream in = clientSocket.getInputStream();
            inFromServer= new ObjectInputStream(in);
            String onlineUsers="";
            try
            {
                onlineUsers=(String)inFromServer.readObject();
            }
            catch(ClassNotFoundException e) 
            {
                System.out.println(e);
            }
            if(onlineUsers.length()==0){
                System.out.println("No online users as yet");
            }
            else
            {
                System.out.println(onlineUsers);
            }
            
            /*DataOutputStream dataOutToServer = new DataOutputStream(clientSocket.getOutputStream());
	    dataOutToServer.writeBytes(inFromUser.nextLine());

            usersOnline = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String incomingMessage = usersOnline.readLine();
            System.out.println("Onlie users");
            if(incomingMessage.length()==0)
            {
                System.out.println("-----------------");
            }
            System.out.println(incomingMessage);
            */
            System.out.println("\nAdd sending intructions here" );
	     while(true){System.out.print("");}//just to keep alive
            // thread for recieving messages
	    /*
            Thread messageOut = new Thread() {
                
                @Override
                public void run(){
                    
                    while (true)
                    {
			Message message=null;
			try
                        {
                            message =(Message)inFromServer.readObject();
                        }
                        catch(ClassNotFoundException e)
                        {
                            System.out.println(e);
                        }
                        catch(IOException e)
                        {
                           System.out.println(e);
                        }
			//if(message!=null){                        
			//message =(Message)inFromServer.readObject();
			/*
                        if(message.getMessageType().equals("textOnly"))
                        {
                            try
                            {
                                message =(Message)inFromServer.readObject();
                            }
                            catch(ClassNotFoundException e)
                            {
                                System.out.println(e);
                            }
                            catch(IOException e)
                            {
                                System.out.println(e);
                            }
                            System.out.println(message.getUserFrom()+": "+message.getText());
                        }
                        else if(message.getMessageType().equals("imageReqeast"))
                        {
                            try
                            {
				System.out.println(message.getUserFrom()+" is trying to send you an image file, send 'YES' to accept, 'NO' to reject");
                                message =(Message)inFromServer.readObject();
                            }
                            catch(ClassNotFoundException e)
                            {
                                System.out.println(e);
                            }
                            catch(IOException e)
                            {
                                System.out.println(e);
                            }
                            System.out.println(message.getUserFrom()+": "+message.getText());
                        }//}/
                    }
                }
            };messageOut.start();*/
        }
        catch(IOException e){
            System.out.println(e);
        }        
        
        
    }
    
    void SendMessage(String text, String timeNow, String UserFrom, String UserTo)throws IOException
    {
        outToServer.writeObject (new Message(text,timeNow,UserFrom,UserTo));
    }

    void SendMessage(String text, String timeNow, String UserFrom,String UserTo,byte[] image)throws IOException
    {
        outToServer.writeObject(new Message(text, timeNow, UserFrom,UserTo, image));
    }
    
    void SendMessage(String timeNow, String UserFrom,String UserTo,byte[] image)throws IOException
    {
        outToServer.writeObject(new Message(timeNow,UserFrom,UserTo,image));
    }
    /**
    public class IncomingMessageListener extends Thread{
    String incomingMessage;
    
    @Override
    public void run()
    {
        //clientSocket = new Socket(ip, 6000);
	while(true)
	{
		BufferedReader inFromServer;
		try {
		    inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		    try 
		    {
		        incomingMessage = inFromServer.readLine();
		        System.out.println(incomingMessage);
		    } 
		    catch (IOException ex) 
		    {
		    Logger.getLogger(IncomingMessageListener.class.getName()).log(Level.SEVERE, null, ex);
		    }
		} 
		catch (IOException ex) 
		{
		    Logger.getLogger(IncomingMessageListener.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
        
    }
}*/
    
    
    
}
