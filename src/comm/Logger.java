package comm;

public class Logger {
	
    private volatile static Logger instance;
    
    private Logger(){}
     
    public static Logger getInstance(){
        if(instance == null){
            synchronized (Logger.class) {
                if(instance == null)
                    instance = new Logger();
            }
        }
        return instance;
    }
    
    public void log( String log ) {
    	System.out.println(log);
    }
    
    public void err(String errLog) {
    	System.err.println(errLog);
    }
    
    public void err(Exception err) {
    	StackTraceElement[] stacks = err.getStackTrace();
    	
		this.err("\n\n\n\n");
    	this.err("####### ERROR [ " + err.getLocalizedMessage() + " ] #######");
    	
    	for(StackTraceElement stack : stacks) {
    		String msg = "## " + stack.toString();
    		this.err(msg);
    	}
    	
    	this.err("####### ERROR [ " + err.getLocalizedMessage() + " ] #######");
    }
    
}
