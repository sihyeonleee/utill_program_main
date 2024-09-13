package comm.remote;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;


public class SSH {
	
	
	protected String host;
	protected String user;
	protected String pwd;
	protected int port;
	
	protected Session session = null;
	protected Channel execChannel = null;
	protected ChannelExec channelExec = null;
	
	public SSH(String host, String user, String pwd){
		this(host, user, pwd, 22);
	}

	public SSH(String host, String user, String pwd, int port){
		this.host = host;
		this.user = user;
		this.pwd = pwd;
		this.port = port;
	}
	
	
	public void setTunnels(List<Map<String, Object>> params) throws Exception{
		
		connect();
		
		for(Map<String, Object>param : params){
			int localPort = (int) param.get("lPort");
			int remotePort = (int) param.get("rPort");
			String remoteHost = (String) param.get("rHost");
			session.setPortForwardingL(localPort, remoteHost, remotePort);
		}
		
	}
	
	public String exec(String cmd){
		
		String result = "EXEC_FAIL";

		try {
			result = run(cmd);
		} catch (Exception err) {
			err.printStackTrace();
		}finally {
			disconnect();
		}
		
		System.out.println(result);
		return result;
		
	}
	
	public String execContinue(String cmd){
		
		String result = "EXEC_FAIL";

		try {
			result = run(cmd);
		} catch (Exception err) {
			err.printStackTrace();
		}
		
		System.out.println(result);
		return result;
		
	}
	
	public String run(String cmd){
		
		String result = "";
		
		StringBuilder outputBuffer = null;
		InputStream in = null;
		
		try{
			
			connect();
			
			channelConnect();
			
	        channelExec.setCommand(cmd);
	        
	        outputBuffer = new StringBuilder();
	        in = execChannel.getInputStream();
	        ((ChannelExec) execChannel).setErrStream(System.err);        
	        execChannel.connect();
	        
	        byte[] tmp = new byte[1024];
	        while (true) {
	            
	        	while (in.available() > 0) {
	                int i = in.read(tmp, 0, 1024);
	                outputBuffer.append(new String(tmp, 0, i));
	                if (i < 0) break;
	            }
	            
	            if (execChannel.isClosed()) {
	            	result = outputBuffer.toString();
	                break;
	            }
	            
	        }
		} catch (Exception err) {
			err.printStackTrace();
		}finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		return result;
		
	}
	
	public boolean isConnect(){
		return session == null ? false : session.isConnected();
	}
	
	public boolean isChannelConnect(){
		return execChannel == null ? false : execChannel.isConnected();
	}
	
	public void connect(){
		if(!isConnect()){
			JSch jsch = new JSch();
			localUserInfo lui = new localUserInfo();
			
			try {
				this.session = jsch.getSession( user, host, port );
				this.session.setPassword( pwd );
				this.session.setUserInfo(lui);
				this.session.setConfig("StrictHostKeyChecking", "no");
				session.connect();
			} catch (JSchException err) {
				err.printStackTrace();
			}
		}
	}
	
	public void channelConnect(){
		if(!isChannelConnect()){
			try {
				execChannel = session.openChannel("exec");
				channelExec = (ChannelExec) execChannel;
				channelExec.setPty(true);
			} catch (JSchException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void disconnect(){
		
		if(session != null && session.isConnected()){
			session.disconnect();
		}
		
		if(channelExec != null && channelExec.isConnected()){
			channelExec.disconnect();
		}
		
		if(execChannel != null && execChannel.isConnected()){
			execChannel.disconnect();
		}
		
	}
	
    class localUserInfo implements UserInfo{
    	
    	String passwd;
    	
    	public String getPassword(){ return passwd; }
    	public boolean promptYesNo(String str){return true;}
    	public String getPassphrase(){ return null; }
    	public boolean promptPassphrase(String message){return true; }
    	public boolean promptPassword(String message){return true;}
    	public void showMessage(String message){}
    	
    }
}
