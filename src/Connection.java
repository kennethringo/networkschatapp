
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection
{
    private Socket connection;
    private String userName;
    public ObjectInputStream input;
    public ObjectOutputStream output;
    public boolean hasNotQuit;
    
    public Connection(Socket connection)
    {
        hasNotQuit = true;
        this.connection=connection;
	//InputStream inFromClient = newConnection.getInputStream();
        try{
		//input = new ObjectInputStream(connection.getInputStream());
		output = new ObjectOutputStream(connection.getOutputStream());
	}
	catch(IOException e){System.out.println(e);}
    }
    public void setUserName(String userName)
    {
        this.userName=userName;
    }
    public String getUserName()
    {
        return userName;
    }
    public void send_message(Message message)
    {
        try{
            output.writeObject(message);
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
    }
    /*
    public void run()
    {
	System.out.print("");    
    }

    /*
    @Override
    public void run()
    {
   
        while(hasNotQuit)
        {
            Message message=null;
            try
	    {
            	message=(Message)input.readObject();
            }
	    catch(IOException e)
	    {
		System.out.println(e);
	    }
	    catch(ClassNotFoundException e){
		System.out.println(e);
	    }

            if((message.getMessageType()).equals("textOnly"))
            {
                if((message.getUserTo())==null)
                {
                    for(Connection user:Server.connections)
                    {
                        
                         user.send_message(message);
                         
                    }
                }
                else
                {
                    for(Connection user:Server.connections)
                    {
                        boolean found = false;
                        if((user.userName).equals(message.getUserTo()))
                        {
                            user.send_message(message);
                            found=true;
                        }
                    }
                    System.out.println("User not found");
                    
                    
                }
            }
            else if((message.getMessageType()).equals("imageOnly"))
            {
                
            }
            else if((message.getMessageType()).equals("textImage"))
            {
                
            }
            else if((message.getMessageType()).equals("QuitMessage"))
            {
                this.hasNotQuit=false;
		try{this.connection.close();}catch(IOException w){System.out.println(w);}
                Server.connections.remove(this);
                System.out.println("User "+this.getUserName()+" has disconnected");
            }
        }
    }*/
    
}
