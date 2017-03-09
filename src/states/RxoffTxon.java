package states;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import application.Application;

public class RxoffTxon extends State{
	
	public RxoffTxon(Application application) {
		this.app = application;
	}
	
	@Override 
	public State preState(){
		//client sends app name to server.
		app.getClient().receiveMsg();
		app.getClient().sendToServer(app.getClient().getNameForServer());
		
		return this;
	}
	
	//running the rx-off-tx-on state
	@Override
	public void run() {
		//gets input from user and sends it to the clients server.
		new Thread(() -> {
			while(keepGoing){
				String msg = JOptionPane.showInputDialog("Please Enter Input.");
				if (msg != null && msg.length() > 0){
					app.getClient().sendToServer(msg);
					LOGGER.log(Level.INFO,toString() + "\n" + "client sent " + msg + " to server");
				}
			}
		}).start();
		
		//server attempts to accept a connection.
		while(keepGoing){
			if (app.getServer().tcpListener()){
				LOGGER.log(Level.INFO, toString() + "\n" + "Servers tcp connection is ON!!!");
				keepGoing = false;
				app.incState(2);
				return;
			}
				app.getServer().udpListener();//server listens for request messages.
		}
	}
		
}


