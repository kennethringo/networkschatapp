
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.io.ByteArrayOutputStream;
public class Message implements Serializable{
    private String text = null;
    private String extension;
    private String information = null;
    private boolean accept = false;
    private String userFrom = null;
    private String userTo=null;
    private byte[] image = null;
    private boolean response = false;
    private byte[] size;
    private String MessageType;
    private byte[] ba; 
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
    protected Message(String userFrom,String userTo, String fileName, String extension, byte [] size, byte [] byteArray)
    {   
        // if (image != null)
        this.MessageType="imageOnly";this.userFrom=userFrom;this.text=fileName;this.userTo=userTo;
        this.extension = extension; this.size = size.clone(); this.ba = byteArray.clone();
    }

    /**
     * This method creates a image-only message that is directed to all users.
     * @param time - the time when message was sent\nuserFrom - username of the user sending msg\nuserTo - username of user msg is directed to\nimage - byte array of image being sent
     * 
     */
    /////////////////////////////////////////////////////////////////////////////////////
    protected Message(String userFrom, String fileName, String extension, byte [] size, byte [] byteArray)
    {   
        
        this.MessageType="imageOnly";this.userFrom=userFrom;this.text=fileName;this.userTo=null;this.image=image; 
        this.extension = extension;
        this.size = size.clone(); this.ba = byteArray.clone();
    }
    //////////////////////////////////////////////imageRequest
    protected Message(String userFrom ,String userTo,  String information, boolean accept)
    {   
        
        this.MessageType="imageResponse";this.text = text; this.userFrom=userFrom; this.userTo=userTo; this.information = information; this.accept = accept;
    }
    //image Response
    // protected Message(String userFrom , boolean response)
    // {   
        
    //     this.MessageType="imageResponse";this.userFrom=userFrom;this.response = response;
    // }


    protected Message(String text, String userFrom)
    {
        this.MessageType="textOnly"; this.text=text;this.userFrom=userFrom;this.userTo=null;
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
    protected String getText()
    {
	   return text;
    }

    protected String getExtension()
    {
       return extension;
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

    protected void setUserFrom(String userFrom)
    {
       this.userFrom = userFrom;
    }
    
    protected String getInformation()
    {
       return information;
    }

    protected void setInformation(String info)
    {
       this.information = info;
    }

    protected boolean getAccept(){
        return accept;
    }
    
    protected void setAccept(boolean accept){
        this.accept = accept;
    }
    
    /*protected void setResponse(String yesOrNo){
        if (yesOrNo.equals("yes"){
            this.response = "yes";
        }else if (yesOrNo.equals("no"){
            this.response = "no";
        }*/
        
    // }
    protected byte[] getImage()
    {
	   return image;
    }
    // protected ByteArrayOutputStream getByteArrayOutputStream()
    // {
    //    return bs;
    // }

    protected byte[] getSize()
    {
       return size;
    }

    protected byte[] getByteArray()
    {
       return ba;
    }
}
