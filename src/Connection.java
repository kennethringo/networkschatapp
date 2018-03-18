
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Connection
{
    protected Socket connection;
    private String userName;
    public ObjectInputStream input;
    public ObjectOutputStream output;
    public boolean hasNotQuit;
    private Message waiting;
    
    public Connection(Socket connection, ObjectInputStream input,ObjectOutputStream output)
    {
        hasNotQuit = true;
        this.connection=connection;
        this.input=input;
        this.output=output;
        
    }
    public void setUserName(String userName)
    {
        this.userName=userName;
    }
    public String getUserName()
    {
        return userName;
    }
    public ObjectOutputStream getOutputStream()
    {
        return this.output;
    }
    
    public ObjectInputStream getInputStream()
    {
        return this.input;
    }
   
    
    void send_message(Message m)
    {
        //System.out.println(outToServer);
        try
        {
            output.writeObject(m);
        }
        catch(Exception e)
        {
            System.out.println(e);
            System.out.println("Message not sent. Try again...");
        }
    }
    
   
}
