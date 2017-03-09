package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.EncoderDecoder;

public class Client {
	private static final Logger LOGGER = Logger.getLogger( Client.class.getName() );
	private Socket tcpConnection = null;
	private DatagramSocket udpConnection = null;
	private String randomNum = "";
	private String Tx = "";
	private EncoderDecoder encdec = new EncoderDecoder(); 

	
	public Client() {
		try {
			udpConnection = new DatagramSocket();
			randomNum = getRandomNumber();
		} catch (SocketException e) {
			//error occured in creating dataSocket (=could not open the socket).
			e.printStackTrace();
		}
	}
	
	/*
	 * broadcasts a request message to port 6000. 
	*/
	public void udpBroadcast() {
		try {
			DatagramPacket dp = new DatagramPacket(getRequestData(), 20, InetAddress.getByName("255.255.255.255"), 6000);
			this.udpConnection.send(dp);
		} catch (IOException e) { 
			LOGGER.log(Level.SEVERE, "[Client]: ** Error occured **",e); 
		}		
	}
	
	/*
	 * attempts to receive data on port 6000 of udp socket.
	 * failing -> return false;
	 * succeeding -> attempts to connect to the tcp socket on the server.
	*/
	public boolean udpReceive() {
		DatagramPacket dp = new DatagramPacket(new byte[26],26);
		//attempt to receive 'offer' message from a server.
		try {
			this.udpConnection.setSoTimeout(1000);
			this.udpConnection.receive(dp);
		} catch (IOException e) {
			return false;
		}
		//a message was received, decode it.
		encdec.Decode(dp.getData());
		
		if(encdec.isOfferLegal(randomNum))//if the message is indeed a offer message.
		{
			try {
				this.tcpConnection = new Socket(encdec.getIp(), Integer.parseInt(encdec.getPort()));
				return true;
			}
			 catch (IOException e) {
				LOGGER.log(Level.SEVERE, "[Client] : ** Error occured **", e);
			}
		}
		return false;
	}
	
	/*
	 * returns the 'request' message data encrypted into a byte array.
	 */
	private byte[] getRequestData(){
		return ("Networking17TOGA" + this.randomNum).getBytes(StandardCharsets.UTF_8);
	}
	public String getNameForServer(){
		return "Networking17Toga" + this.randomNum;
	}
	
	/*
	 * returns a random String with 4 digits
	 */
	private String getRandomNumber() {
		Random r = new Random();
		String num = Integer.toString(r.nextInt(10000));
		return new String(new char[4]).replace('\0', '0').substring(0,4-num.length()) + num;
	}

	/*
	 * sends the given string 's' to the server connected to this client.
	 */
	public void sendToServer(String s)  {
		try{
			if(s == null)
				return;
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(tcpConnection.getOutputStream()));
			out.write(s);
			out.newLine();
			out.flush();
			
		}catch(IOException e){
			LOGGER.log(Level.SEVERE, "Client Lost Connection to our Server.");
			System.exit(0);
		}
		
	}

	public String getTx() {
		return this.Tx;
	}
	
	//recieve server's name
	public String receiveMsg() {
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(tcpConnection.getInputStream()));
			String line;
			if((line = in.readLine()) != null && line.length() > 0)
			{
				this.Tx = line;
			}
		}catch(IOException e){
			LOGGER.log(Level.SEVERE, "Server Lost Connection With Its Client.");
			System.exit(0);
		}
		return null;
	}
}
