package application;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import com.google.common.primitives.Bytes;

public class EncoderDecoder {
	private String name = "Networking17TOGA";//default data.
	private String randNum;
	private String port;
	private String ip;

	/*
	 * used to encode the offer message in the server module.
	 * receives the TCP port used by our server to accept connection to client.
	 */
	public byte[] Encode(short tcpPort){
		byte[] offer = new byte[26];
		try {
			//combines all three byte arrays into one (= offer).
			offer = Bytes.concat(
						(name + randNum).getBytes(StandardCharsets.UTF_8),
						ByteBuffer.allocate(2).putShort(tcpPort).array(),
						Inet4Address.getLocalHost().getAddress()
					);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		return offer;
	}
	
	/*
	 * decodes byteData. 
	 * saves results in this class variables.
	 * both client and server module use this method.
	*/
	public void Decode(byte [] byteData){
		name = new String(byteData,0,16,StandardCharsets.UTF_8);
		randNum = new String(byteData,16,4,StandardCharsets.UTF_8);
		if (byteData.length > 20){ //decoding a offer message.
			port = "" + ByteBuffer.wrap(Arrays.copyOfRange(byteData, 20, 22)).getShort();
			try {
				ip = "" + Inet4Address.getByAddress(Arrays.copyOfRange(byteData, 22, 26)).getHostAddress();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * returns true if the offer message received is a valid one. 
	*/
	public boolean isOfferLegal(String random){
		try {
			return name.equals("Networking17TOGA") &&
					randNum.equals("" + random) &&
					!ip.equals(Inet4Address.getLocalHost().getHostAddress());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	//getters
	public String getName() {
		return name;
	}

	public String getRandNum() {
		return randNum;
	}

	public String getPort() {
		return port;
	}

	public String getIp() {
		return ip;
	}
}
