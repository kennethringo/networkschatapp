
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection
{
    protected Socket connection;
    private String userName;
    public ObjectInputStream input;
    public ObjectOutputStream output;
    public boolean hasNotQuit;
    
    public Connection(Socket connection)
    {
        hasNotQuit = true;
        this.connection=connection;
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
    
    
    
    
}
