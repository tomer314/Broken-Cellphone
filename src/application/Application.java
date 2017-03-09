package application;

import client.Client;
import server.Server;
import states.StateFactory;

public class Application {
	private Server server;
	private Client client;
	private StateFactory stateFactory;
	private int state;
	
	public Application() {
		stateFactory = new StateFactory(this);
		server = new Server();
		client = new Client();
		state = 0;
	}
	
	/*
	 * begin -> initial state of application is 0.
	 */
	public void begin() {
		incState(0);
	}
	
	/*
	 * controls the State shifting along this application.
	 * each state changing will result in a new .beginState() implementation running.
	*/
	public void incState(int inc) {
		state += inc;
		stateFactory.getState(state).
			preState().
			setName(client.getNameForServer()).
			setRx(server.getRx()).
			setTx(client.getTx()).
			beginState();
	}

	/*
	 * returns reference to client.
	 */
	public Client getClient(){
		return this.client;
	}
	
	/*
	 * returns reference to server.
	 */
	public Server getServer(){
		return this.server;
	}
}
