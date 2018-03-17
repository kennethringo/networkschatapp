
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


public class Client 
{
    private Socket clientSocket;
    private Scanner inFromUser = new Scanner(System.in);
    private ObjectOutputStream outToServer;
    private ObjectInputStream  inFromServer;
    private String clientName;
    String userInput="";
    
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
            clientSocket = new Socket(IP, 7000);
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
                System.out.println("No online users as yet");
            }
            else
            {
                System.out.println(onlineUsers);
            }

            System.out.print("Enter text message then press ENTER to send message to everyone in Group.\n"
                + "-i [path/to/image/file.jpg]              - to send image file to everyone in group\n"
                + "-u [userTo] [Text message]        - to send private text to specific user\n"
                + "-ui [userTo] [path/to/image/file.jpg]            - to send image file to specific user\n");

            
            
            Thread sendingMessage = new Thread() {
            // Output thread
                @Override
                public void run()
                {
                    while(true)
                    {
                        userInput=(new Scanner(System.in)).nextLine();
                        Message outMessage = getMessage(userInput);
                        send_message(outMessage);
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
                            if(inMessage.getMessageType().equals("textOnly"))
                            {
                                System.out.println(inMessage.getUserFrom()+":    "+inMessage.getText());
                            }
                            else if(inMessage.getMessageType().equals("imageOnly"))
                            {
                                saveImage(inMessage.getImage(),inMessage.getText());
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
    void saveImage(byte[] image, String fileName)
    {
        FileOutputStream fos;
        try 
        {
            fos = new FileOutputStream(fileName);
            fos.write(image);
            fos.close();
        }
        catch(Exception e)
        {
            System.out.println("Failed to save "+fileName);
            System.out.println(e);
        }
        
            
       
    }
    void send_message(Message m)
    {
        //System.out.println(outToServer);
        try
        {
            outToServer.writeObject(m);
        }
        catch(Exception e)
        {
            System.out.println(e);
            System.out.println("Message not sent. Try again...");
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
                        return new Message(this.clientName,message[1],readImage(message[1]));
                    }
                    catch(Exception e)
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
        // text message to everyone
        else
        {
            return(new Message(userInput,this.clientName));
        }
    }

    
    byte[] readImage(String ImageName) throws IOException
    {
        File imgPath = new File(ImageName);
        BufferedImage bufferedImage = ImageIO.read(imgPath);

        // get DataBufferBytes from Raster
        WritableRaster raster = bufferedImage .getRaster();
        DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();

        return ( data.getData() );
    }
}
