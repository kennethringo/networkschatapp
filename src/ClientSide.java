import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ClientSide {
    public static void main(String argv[]) throws Exception {
        String sentence="";

        String modifiedSentence;

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        
	System.out.print("Enter Servers IP: ");
	String ip = (new Scanner(System.in)).next();
        Socket clientSocket;
	try 
	{
		clientSocket = new Socket(ip, 6000);
	}
	catch( IOException e)
	{
		System.out.println(e);	
	}

	
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
	
      	sentence = inFromUser.readLine();
        outToServer.writeBytes(sentence + '\n');
    		
        
	
	System.out.print("New message: ");
        modifiedSentence = inFromServer.readLine();
        System.out.println(modifiedSentence);
        clientSocket.close();
    }
}
