package service.config; 

import service.Service;  

public class ExitService extends Service{ 

	public ExitService(){ 
		
	}

	@Override
	public void doShow(String name) {
		System.exit(0);
	}

}
