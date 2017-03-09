package server;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;

import application.EncoderDecoder;

public class Server {
	private static final Logger LOGGER = Logger.getLogger( Server.class.getName() );
	private ServerSocket tcpSocket = null;
	private DatagramSocket udpSocket = null;
	private Socket connection = null;
	private short tcpPort = 0;
	private EncoderDecoder encdec = new EncoderDecoder();
	private String Rx = "";
	private boolean first = true;

	//constructor
	public Server() {
		try {
			createTcpSocket(IntStream.rangeClosed(6000, 7000));
			udpSocket = new DatagramSocket(6000);
		} catch (SocketException e) {
			LOGGER.log(Level.SEVERE, "[Server] : ** Error occured **",e);
		}
	}
	
	/*
	 * method binds tcpSocket to a port in ports range.
	 * will repeat itself until succeeding the operation.
	 */
	private void createTcpSocket(IntStream ports) {
		ports.filter(port -> {
			try{
				tcpSocket = new ServerSocket(port);
				tcpPort = Short.parseShort(""+port);
				LOGGER.log(Level.INFO, "Server created TCP socket on port " + tcpPort + ".");
			} catch(IOException e) { return false; }
			return true;
		}).findFirst();
		
		if(tcpPort == 0){
			LOGGER.log(Level.WARNING, "Server could not find an available port between 6000 - 7000. Trying again.");
			createTcpSocket(ports);
		}
	}
	
	/*
	 * method attempts to accept a tcp connection for 1 second.
	 */
	public boolean tcpListener() {
		try {
			tcpSocket.setSoTimeout(1000);
			connection = tcpSocket.accept();
		} catch (IOException e) { return false; }

		return true;
	}
	
	/*
	 * method attempts to receive a udp transmission for one second.
	 * failing -> finish its run.
	 * succeeding -> it will read the request and return a offer.
	 */
	public void udpListener() {
		DatagramPacket dp = new DatagramPacket( new byte[20], 20);
		//attempting to receive a 'request' message from a client module.
		try {
			udpSocket.setSoTimeout(1000);
			udpSocket.receive(dp);
		} catch (IOException e) { return; }
		
		//message received -> decode it.
		encdec.Decode(dp.getData());
		
		try{
			if(encdec.getName().equals("Networking17TOGA") && !dp.getAddress().getHostAddress().equals(Inet4Address.getLocalHost().getHostAddress()))//if request is legal
			{
				byte[] offer = encdec.Encode(tcpPort);//encode and get offer request.
				udpSocket.send(new DatagramPacket(offer, 26, dp.getAddress(), dp.getPort()));
				LOGGER.log(Level.INFO,"Server udp listener returns a offer message to the sending client.");
			}
		}catch(IOException e){
			LOGGER.log(Level.SEVERE, "[Server] : ** Error occured **", e);
		}
	}

	/*
	 * receives a message from TCP connection.
	 * if(edit) -> message = edited message.
	 * return message
	 */
	public String receiveMsg(boolean edit) {
		try{
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			if((line = in.readLine()) != null && line.length() > 0)
			{
				//////////////////
				if(first){
					first = false;
					this.Rx = line;
					return null;
				}
				////////////////////
				
				LOGGER.log(Level.INFO,"Server recieved a message " + line +" from " + ((edit)? "Client" : "User"));
				if(!edit)
					return line;
				return edit(line);
			}
		}catch(IOException e){
			LOGGER.log(Level.SEVERE, "Server Lost Connection With Its Client.");
			System.exit(0);
		}
		return null;
	}
	
	/*
	 * changes a random character inside the given String 'msg'.
	 */
	private String edit(String msg){
		Random r = new Random();
		int index = r.nextInt(msg.length());
		char c = msg.charAt(index);
		c = (char)((c == '~') ? ((int)c+1)%127 + 33 : ((int)c+1)%127);
		return msg.substring(0, index) + c + msg.substring(index+1);
	}

	public String getRx() {
		return this.Rx;
	}
	
	//sends server's name to client
	public void sendToClient(String s)  {
		try{
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
			out.write(s);
			out.newLine();
			out.flush();
		}catch(IOException e){
			LOGGER.log(Level.SEVERE, "Client Lost Connection to our Server.");
			System.exit(0);
		}
		
	}
}
