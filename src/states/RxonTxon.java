package states;
import java.util.logging.Level;

import application.Application;

public class RxonTxon extends State{
	
	public RxonTxon(Application application) {
		this.app = application;
	}
	
	@Override
	public State preState(){
		//client sends app name to server.
		app.getServer().sendToClient(app.getClient().getNameForServer());
		app.getServer().receiveMsg(false);
		return this;
	}
	
	//runs the rx-on-tx-on state.
	@Override
	public void run(){
		while(true){
			LOGGER.log(Level.INFO, toString());
			String newMsg = app.getServer().receiveMsg(true);
			app.getClient().sendToServer(newMsg);
			LOGGER.log(Level.INFO,toString() + "\n" + "client sent " + newMsg + " to server");
		}
	}

}
