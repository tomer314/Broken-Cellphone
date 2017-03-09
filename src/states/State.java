package states;
import java.util.logging.Level;
import java.util.logging.Logger;

import application.Application;


public abstract class State {
	static final Logger LOGGER = Logger.getLogger( State.class.getName() );
	Application app;
	String Rx = "off";
	String Tx = "off";
	String name = "";
	Boolean keepGoing = new Boolean(true);
	
	public void beginState(){
		LOGGER.log(Level.INFO, "STATE:= " + this.getClass().getName());
		run();
	}
	
	public abstract void run();
	public abstract State preState();
	
	public State setRx(String rx){
		if(rx.length() > 0)
			this.Rx = rx;
		return this;
	}
	
	public State setTx(String tx){
		if(tx.length() > 0)
			this.Tx = tx;
		return this;
	}
	
	public String toString(){
		return "App: " + name + "\n" + "Rx - " + Rx + " - Tx - " + Tx;
	}
	
	public State setName(String name){
		this.name = name;
		return this;
	}
}
