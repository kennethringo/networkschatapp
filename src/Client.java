
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.*;
import java.nio.file.Paths;
import java.io.IOException;

public class Client 
{
    private Socket clientSocket;
    private Server s1;
    private Scanner inFromUser = new Scanner(System.in);
    private ObjectOutputStream outToServer;
    private ObjectInputStream  inFromServer;
    private String clientName;

    String userInput="";
    private Message toBeAccepted;
    
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
        
        try
        {
            clientSocket = new Socket(IP, 7003);
            System.out.println("Connection with server "+IP+" made.");
            
            
            
            OutputStream out = clientSocket.getOutputStream();
            outToServer = new ObjectOutputStream(out);
            System.out.println("Enter you preferred username: ");
            clientName=inFromUser.next();
            outToServer.writeObject(clientName);
        
            System.out.println("\nConnected Users:");
            InputStream in = clientSocket.getInputStream();
            inFromServer= new ObjectInputStream(in);
            String onlineUsers="";
            onlineUsers=(String)inFromServer.readObject();
            if(onlineUsers.length()==0){
                System.out.println("No online users as yet\n");
            }
            else
            {
                System.out.println(onlineUsers);
            }

            System.out.println("[Text message]                         - to send text message to everyone in group\n"
                             + "-i [path/to/image/file.jpg]            - to send image file to everyone in group\n"
                             + "-u [userTo] [Text message]             - to send text message to only to one specific user\n"
                             + "-ui [userTo] [path/to/image/file.jpg]  - to send image file to specific user\n");

            
            
            Thread sendingMessage = new Thread() {
            // Output thread
                @Override
                public void run()
                {
                    while(true)
                    {
                        userInput=(new Scanner(System.in)).nextLine();
                        Message outMessage = getMessage(userInput);
                        System.out.println("after get message");

                        if (outMessage == null){
                           outMessage = getMessage(userInput);
                        }

                        

                        else if (outMessage.getMessageType().equals("textOnly")){
                            send_message(outMessage);
                        }
                        else if (outMessage.getMessageType().equals("imageOnly") && (outMessage.getUserTo()==null)){
                            send_image(outMessage);

                        }

                        else if (outMessage.getMessageType().equals("imageResponse")){
                            System.out.println("user response: " + userInput);
                            send_message(outMessage);
                            


                        }
                        //////////////////for response to imageRequest from server
                        // else if (userInput()){}
                        
                    }
                }
            };sendingMessage.start();
            
            Thread recievingMessage = new Thread()
            {
                @Override
                public void run(){
                    while(true)
                    {
                        try
                        {
                            Message inMessage=(Message)inFromServer.readObject();
                            if(inMessage.getMessageType().equals("textOnly")&&inMessage.getUserFrom()!=null)
                            {
                                System.out.println(inMessage.getUserFrom()+":    "+inMessage.getText());
                            }
                            else if(inMessage.getMessageType().equals("textOnly"))
                            {
                                System.out.println(inMessage.getText());
                            }
                            else if(inMessage.getMessageType().equals("imageOnly"))
                            {   
                                System.out.println("New image file from "+inMessage.getUserFrom());
                                System.out.println(inMessage.getText());
                                System.out.println(inMessage.getSize().length);
                                
                                saveImage(inMessage.getText(), inMessage.getExtension(),  inMessage.getSize(), inMessage.getByteArray());
                            }

                            else if(inMessage.getMessageType().equals("imageResponse"))
                            {
                                
                                //will print what server tells user about request
                                System.out.println(inMessage.getInformation());
                                
                                

                                
                            }

                            else if(inMessage.getMessageType().equals("serverResponse"))
                            {
                                //sendingMessage.wait();
                                System.out.println(inMessage.getText());
                            }
                        }
                        catch(Exception e)
                        {   

                            System.out.println("recievingMessage thread not working");
                            System.out.println(e);
                        }
                    }
                }
            };recievingMessage.start();
            
            // close socket if user input exit
            if(!true){
                clientSocket.close();
            }
        }
        
        
            
        
        catch(IOException e){
            System.out.println(e);
        }   
        catch(ClassNotFoundException e) 
        {
            System.out.println(e);
        }     
        
        
    }
    
    

   

    Message getMessage(String userInput)
    {
        String[] message = userInput.split(" ");
        // image to everyone
        if(message[0].equals("-i"))
        {
            if (message[1].length()>4)
            {
                if((message[1].substring(message[1].indexOf(".")+1,message[1].length())).equals("jpg")||(message[1].substring(message[1].indexOf(".")+1,message[1].length())).equals("jpeg")||(message[1].substring(message[1].indexOf(".")+1,message[1].length())).equals("png"))
                {
                    try
                    {
                        System.out.println("Picture");
                        //old
                        // return new Message(this.clientName,message[1],readImage(message[1]));
                        //new
                         
                        return readImage(message[1]);
                        

                    }
                    catch(IOException e)
                    {
                        System.out.println("Failed to send image.");
                        System.out.println(e);
                        return null;
                    }
                    
                }
                else
                {
                    System.out.println("incorrect file format "+message[1]);
                    return null;
                }
            }
            else
            {
                System.out.println("incorrect file format "+message[1]);
                return null;
            }
        }
        else if(message[0].equals("-u"))
        {
            if(message.length>2)
            {
                String m="";
                for(int i=2;i<message.length;i++)
                {
                    m+=message[i]+" ";
                }
                return (new Message(m, this.clientName,message[1]));
            }
            else
            {
                System.out.println("Wrong format '-u [usernameTo] [text]'");
                return null;
            }
        }
        //this is how we answer requests
        else if(message[0].equals("-r"))
        {
            if (message[1].equals("yes")){
                return (new Message(clientName, "yes",  true));
            }else if (message[1].equals("no")){
                return (new Message(clientName, "no", false));
            }else{

                System.out.println("not a valid response. Answer '-r yes' or '-r no' to respond");
                return (new Message(clientName, "wrong input", false));
                
            }
        }

        // text message to everyone
        else
        {
            return(new Message(userInput,this.clientName));
        }
    }

    
    Message readImage(String ImageName) throws IOException
    {   
        
        System.out.println(ImageName);
        File imgPath = new File(ImageName);
        System.out.println(imgPath.getName() + "%%%%%");

        if(imgPath.exists() && !imgPath.isDirectory()) { 
            BufferedImage image = ImageIO.read(imgPath);

            System.out.println("file name :'" + imgPath.getName() + "'");
        
            System.out.println("attempting to split file name");
            String [] splitInfo = imgPath.getName().split("\\.");
            System.out.println ("splitInfo[0]: " + splitInfo[0]);
            System.out.println ("splitInfo[1]: " + splitInfo[1]);
            
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(image, splitInfo[1], byteArrayOutputStream);

            byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
            System.out.println (size.length + " $$$$$$$$$$");
            Message im = new Message(this.clientName,splitInfo[0] , splitInfo[1], size, byteArrayOutputStream.toByteArray());

            return im;
        }
        else{
            System.out.println("incorrect file name specified: " + imgPath.getName());
            return null;
        }
        

        
        

        //converts byteArrayOutputStream.size into a byte array of size
        
        
        
      
    }

    void send_message(Message m)
    {
        //System.out.println(outToServer);
        try
        {   
            // outToServer.writeObject("message");
            outToServer.writeObject(m);
            //System.out.println("Message Sent");
            outToServer.flush();
        }
        catch(Exception e)
        {
            System.out.println(e);
            System.out.println("Message not sent. Try again...");
        }
    }

     void send_image(Message image)
    {   
        
        try
        {   
            
            outToServer.writeObject(image);
            outToServer.flush();
            System.out.println("Flushed: " + System.currentTimeMillis());

            outToServer.flush();
        }
        catch(Exception e)
        {
            System.out.println(e);
            System.out.println("Image not sent. Try again...");
        }
    }

    void saveImage(String fileName , String extension, byte[] sizeArray, byte [] ba)
    {   
        
        System.out.println("Reading: " + System.currentTimeMillis());
        
        
        byte[] sizeAr = new byte[4];
        sizeAr = sizeArray;
        int size = ByteBuffer.wrap(sizeAr).asIntBuffer().get();

        byte[] imageAr = new byte[size];
        imageAr = ba;

        
        try{
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageAr));
            
            Path currentRelativePath = Paths.get("photos");
            String s = currentRelativePath.toAbsolutePath().toString();
            
            File imageFile = new File(s, fileName + "."+ extension ); //s is parent directory, second part for name of file
            ImageIO.write(image, extension, imageFile);
            
        }catch (IOException io){
            System.out.println("failed to write to " + fileName);
        }
    }
}