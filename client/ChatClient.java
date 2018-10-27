// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  //The login ID of the user.
  String LoginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
    this.loginID = "ANONYMOUS";
    sendToServer("#login ANONYMOUS");
  }
  
  
  /*
   * Constructor if there is a given login ID
   * 
   * @param the given loginID
   * @param port the port number to connect on.
   * @param clientUI the interface type variable.
   */
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
		    throws IOException 
		  {
		    super(host, port); //Call the superclass constructor
		    this.clientUI = clientUI;
		    openConnection();
		    this.loginID = loginID;
		    sendToServer("#login " + this.loginID);
		  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
	  
	if(message.charAt(0) == '#') {
		runCommand(message)
	}
	else {
		try
	    {
	      sendToServer(message);
	    }
	    catch(IOException e)
	    {
	      clientUI.display
	        ("Could not send message to server.  Terminating client.");
	      quit();
	    }
	}
  }
  
  public void runCommand(String message) {
	  if(message.equalsIgnoreCase("#quit")) 
		  quit();
	  else if(message.equalsIgnoreCase("#logoff")) {
		  try {
			  closeConnection();
		  }
		  catch(IOException e) {}
		  
		  clientUI.display("Sucessfully logged off");
	  }
	  else if(message.toLowerCase().startsWith("#setport")) {
		  try {
			  int newPort = Integer.parseInt(message.substring(9));
			  
			  setPort(newPort);
			  
			  clientUI.display("Port changed to " + getPort());
		  }
		  catch(Exception e) {
			  System.out.println("Unexpected error when attempting to change the port");
		  }
	  }
	  else if(message.toLowerCase().startsWith("#sethost")) {
		  setHost(message.substring(9))
		  
		  clientUI.display("Host changed to " + getHost());
	  }
	  else if(message.toLowerCase().startsWith("#login")) {
		  if(isConnected()) {
			  clientUI.display("You must first logout before attempting a new login");
			  return;
		  }
		  
		  loginID = message.substring(7);
		  try {
			  openConnection();
			  sendToServer("#login " + loginID);
		  }
		  catch(Exception e) {
		      clientUI.display("Connection could not be established.");
		  }
	  }
	  else if (message.equalsIgnoreCase("#gethost")){
	      clientUI.display("Current host: " + getHost());
	  }
	  else if (message.equalsIgnoreCase("#getport")){
	      clientUI.display("Current port: " + Integer.toString(getPort()));
	  }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  //For when there is a closed connection while waiting for messages from the server. Overrides method in <code>AbstractClient<code>
  protected void connectionException(Exception exception)
  {
    clientUI.display
      ("The connection to the Server (" + getHost() + ", " + getPort() + 
      ") has been disconnected");
  }
}
//End of ChatClient class
