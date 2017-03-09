package states;
import java.util.logging.Level;

import application.Application;

public class RxoffTxoff extends State{
	
	public RxoffTxoff(Application application){
		this.app = application;
	}
	
	@Override
	public State preState(){
		return this;
	}
	
	/*
	 * runs the rx-off-tx-off state.
	 */
	@Override
	public void run(){
		while(keepGoing){
			//Server: listen to tcp port.
			if(app.getServer().tcpListener()){
				LOGGER.log(Level.INFO, toString() + "\n" + "Servers tcp connection is ON!!!");
				keepGoing = false;
				app.incState(2);
				return;
			}
			clientBroadcast();	//Client: broadcast request message.
			app.getServer().udpListener(); //Server: listen to udp socket for request messages.
			clientBroadcast(); //Client: broadcast request message.
			//Client: listen for 'offer' messages in client.
			if(app.getClient().udpReceive()){
				LOGGER.log(Level.INFO, toString() + "\n" + "Client connected to server");
				keepGoing = false;
				app.incState(1);	
				return;
			}
			clientBroadcast(); //Client: broadcast request message.
		}	
	}
	
	//Client: broadcast request message. and log it.
	private void clientBroadcast(){
		app.getClient().udpBroadcast();
		LOGGER.log(Level.INFO,toString() + 
				"\n" + 
				"Client -> broadcasted request message.");
	}
}
