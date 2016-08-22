package org.abia.AgentContainer;

import org.abia.Blackboard.*;

public abstract class Agent extends Thread {

	   public final void Store(AgentData DataToStore) throws BlackboardException{	
		      Blackboard.getBlackboard().Store(this.getClass(),DataToStore);
	   }

       public abstract void   Initialize() throws AgentException;
	   public abstract String getAgentNameInBlackboard();	  	  
}

