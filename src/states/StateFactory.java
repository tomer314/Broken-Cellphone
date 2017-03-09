package states;
import application.Application;

public class StateFactory {
	private Application app;
	
	public StateFactory(Application application){
		this.app = application;
	}
	//returns a new <? extends State> object representing a given state of this application.
	public State getState(int state){
		switch(state){
			case 0: return new RxoffTxoff(app);
			case 1: return new RxoffTxon(app);
			case 2: return new RxonTxoff(app);
			case 3: return new RxonTxon(app);
			default: return null;
		}
	}
}
