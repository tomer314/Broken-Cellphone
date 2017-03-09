package states;
import java.util.logging.Level;

import application.Application;

public class RxonTxoff extends State{
	
	public RxonTxoff(Application application) {
		this.app = application;
	}
	
	@Override
	public State preState(){
		app.getServer().sendToClient(app.getClient().getNameForServer());
		app.getServer().receiveMsg(false);
		return this;
	}
	
	//runs the rx-on-tx-off state.
	@Override
	public void run(){
		while(true){
			LOGGER.log(Level.INFO, toString());
			String msg = app.getServer().receiveMsg(false);
			System.out.println(msg);
		}
	}

}
