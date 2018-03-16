public class Message {
    private String text = null;
    private String time=null;
    private String userFrom=null;
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
    protected Message(String text){
		this.text=text;this.MessageType="newConnection";
	}
    protected Message(String text, String time, String userFrom,String userTo)
    {
        this.MessageType="textOnly"; this.text=text;this.time=time;this.userFrom=userFrom;this.userTo=userTo;
    }
    /**
     * This method creates a text-image message that is directed to a specific user.
     * @param text - the text side of the message\ntime - the time when message was sent\nuserFrom - username of the user sending msg\nuserTo - username of user msg is directed to\nimage - byte array of image being sent
     * 
     */
    protected Message(String text, String time, String userFrom,String userTo,byte[] image)
    {
        this.MessageType="textImage";this.text=text;this.time=time;this.userFrom=userFrom;this.userTo=userTo;this.image=image;
    }
    
    /**
     * This method creates a image-only message that is directed to a specific user.
     * @param time - the time when message was sent\nuserFrom - username of the user sending msg\nuserTo - username of user msg is directed to\nimage - byte array of image being sent
     * 
     */
    protected Message(String time, String userFrom,String userTo,byte[] image)
    {
        this.MessageType="imageOnly";this.time=time;this.userFrom=userFrom;this.userTo=userTo;this.image=image;
    }
    /**
     * This method makes a deep copy of a message.
     * @param m - the other Message Object
     */
    protected Message(Message m)
    {
        
        this.MessageType=m.MessageType;this.text=m.text;this.time=m.time;this.userFrom=m.userFrom;this.userTo=m.userTo;this.image=m.image;
        
    }

    protected String getText()
    {
	return text;
    }

    protected String getMessageType()
    {
    	return MessageType;
    }
    
    protected String getTime()
    {
	return time;
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
