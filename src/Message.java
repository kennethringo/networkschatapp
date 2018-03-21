
import java.io.Serializable;

public class Message implements Serializable{
    private String text = null;
    private String userFrom = null;
    private String userTo=null;
    private byte[] image = null;
    private String MessageType  ; 
    //constructers
    // 
    
    /**
     * This method creates a text-only message that is directed to a specific user.
     * @param text - the text side of the message\ntime - the time when message was sent\nuserFrom - username of the user sending msg\nuserTo - username of user msg is directed to
     * 
     */
    protected Message(String text,  String userFrom,String userTo)
    {
        this.MessageType="textOnly"; this.text=text;this.userFrom=userFrom;this.userTo=userTo;
    }
    protected Message(String text)
    {
        this.MessageType="textOnly"; this.text=text;this.userFrom=null;
    }
    
    /**
     * This method creates a image-only message that is directed to a specific user.
     * @param time - the time when message was sent\nuserFrom - username of the user sending msg\nuserTo - username of user msg is directed to\nimage - byte array of image being sent
     * 
     */
    protected Message(String userFrom,String userTo, String fileName,byte[] image)
    {
        this.MessageType="imageOnly";this.userFrom=userFrom;this.text=fileName;this.userTo=userTo;this.image=image;
    }


    protected Message(String text, String userFrom)
    {
        this.MessageType="textOnly"; this.text=text;this.userFrom=userFrom;this.userTo=null;
    }
    
    
    /**
     * This method creates a image-only message that is directed to a specific user.
     * @param time - the time when message was sent\nuserFrom - username of the user sending msg\nuserTo - username of user msg is directed to\nimage - byte array of image being sent
     * 
     */
    protected Message(String userFrom,String fileName,byte[] image)
    {
        this.MessageType="imageOnly";this.userFrom=userFrom;this.text=fileName;this.userTo=null;this.image=image;
    }


    /**
     * This method makes a deep copy of a message.
     * @param m - the other Message Object
     */
    protected Message(Message m)
    {
        
        this.MessageType=m.MessageType;this.text=m.text;this.userFrom=m.userFrom;this.userTo=m.userTo;this.image=m.image;
        
    }

    protected void setMessageType(String MessageType)
    {
        this.MessageType=MessageType;
    }
    protected void setUserFrom(String user)
    {
        this.userFrom=user;
    }
    protected String getText()
    {
	   return text;
    }

    protected String getMessageType()
    {
    	return MessageType;
    }
    
    protected String getUserTo()
    {
	   return userTo;
    }
    
    protected String getUserFrom()
    {
	   return userFrom;
    }
    
    protected byte[] getImage()
    {
	   return image;
    }
}
