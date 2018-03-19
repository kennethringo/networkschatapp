
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
import java.nio.ByteBuffer;

public class Client 
{
    private Socket clientSocket;
    private Scanner inFromUser = new Scanner(System.in);
    private ObjectOutputStream outToServer;
    private OutputStream out;
    private ObjectInputStream  inFromServer;
    private InputStream in;
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
            clientSocket = new Socket(IP, 7003);
            System.out.println("Connection with server "+IP+" made.");
            
            
            
            out = clientSocket.getOutputStream();
            outToServer = new ObjectOutputStream(out);
            System.out.println("Enter you preferred username: ");
            clientName=inFromUser.next();
            outToServer.writeObject(clientName);
        
            System.out.println("\nConnected Users:");
            in = clientSocket.getInputStream();
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

            System.out.println("Enter text message then press ENTER to send message to everyone in Group.\n"
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
                                System.out.println("saving Image");
                                saveImage(inMessage.getImage(),inMessage.getText());
                            }
                            else if(inMessage.getMessageType().equals("imageRequest"))
                            {
                                //sendingMessage.wait();
                                System.out.println(inMessage.getUserFrom()+" is sending you a media file. \nEnter: '-m yes' to accept, '-m no' to reject");
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
    
    void send_message(Message m)
    {
        //System.out.println(outToServer);
        try
        {
            outToServer.writeObject(m);
            System.out.println("Message Sent");
            outToServer.flush();
        }
        catch(Exception e)
        {
            // System.out.println(e);
            System.out.println("Message not sent. Try again...");
        }
        // catch (NullPointerException n){
        //     // System.out.println(e);
        //     System.out.println("Cannot send. Try again...");
        // }
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
                        // if (image != null)
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
        System.out.println(ImageName);
        File imgPath = new File(ImageName);
        BufferedImage bufferedImage = ImageIO.read(imgPath);
        WritableRaster raster = bufferedImage.getRaster();
        // BufferedImage newImg = new BufferedImage(raster.getWidth(), raster.getHeight(), "jpg");
        // try{
            
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);

            // OutputStream out = clientSocket.getOutputStream();
            byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();
            outToServer.writeObject(size);
            outToServer.writeObject(byteArrayOutputStream.toByteArray());
            outToServer.flush();

            try {
                Thread.sleep(120000);                 //1000 milliseconds is one second.
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            
            // get DataBufferBytes from Raster
           
            DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();

            return ( data.getData() );
        // }catch (NullPointerException e){
        //     System.out.println("Image not found");
           
        // }
        
    }

    void saveImage(byte[] image, String fileName)
    {
        FileOutputStream fos;
        try 
        {
            fos = new FileOutputStream("random.jpg");
            fos.write(image);
            fos.close();
        }
        catch(Exception e)
        {
            System.out.println("Failed to save "+fileName);
            System.out.println(e);
        }
        
            
       
    }
}
