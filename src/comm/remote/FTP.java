package comm.remote;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;

public class FTP extends SSH{
	
	Channel sftpChannel = null;
	ChannelSftp channelSftp = null;
	
	public FTP(String host, String user, String pwd) {
		super(host, user, pwd);
	}
	
	public FTP(String host, String user, String pwd, int port) {
		super(host, user, pwd, port);
	}

    public void upload(String fileName, String remoteDir) throws Exception {
    	
    	connect();
    	ftpChannelConnect();
    	
        FileInputStream fis = null;
        
        try {
            channelSftp.cd(remoteDir);
            File file = new File(fileName);
            fis = new FileInputStream(file);
            channelSftp.put(fis, file.getName());
            fis.close();

        } catch (Exception e) {
        	e.printStackTrace();
        }finally {
        	fis.close();
        	disconnect();
		}
        
    }
    
    public void download(String fileName, String localDir) throws Exception{
        
    	connect();
    	ftpChannelConnect();
		
    	byte[] buffer = new byte[1024];
        BufferedInputStream bis;
        
        try {
            String cdDir = fileName.substring(0, fileName.lastIndexOf("/") + 1);
            channelSftp.cd(cdDir);

            File file = new File(fileName);
            bis = new BufferedInputStream(channelSftp.get(file.getName()));

            File newFile = new File(localDir + "/" + file.getName());

            OutputStream os = new FileOutputStream(newFile);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            
            int readCount;
            
            while ((readCount = bis.read(buffer)) > 0) {
                bos.write(buffer, 0, readCount);
            }
            
            bis.close();
            bos.close();

        } catch (Exception e) {
        	e.printStackTrace();
        }finally {
        	disconnect();
		}
        
    }
    
    public void ftpChannelConnect(){
    	if(!isFtpChannelConnect()){
    		try {
				sftpChannel = session.openChannel("sftp");
				sftpChannel.connect();
				channelSftp = (ChannelSftp) sftpChannel;
			} catch (JSchException err) {
				err.printStackTrace();
			}
    	}
    }
    
    public boolean isFtpChannelConnect(){
		return sftpChannel == null ? false : sftpChannel.isConnected();
	}
    
	public void disconnect(){
		
		if(this.sftpChannel != null && this.sftpChannel.isConnected()){
			sftpChannel.disconnect();
		}
		
		if(this.channelSftp != null && this.channelSftp.isConnected()){
			channelSftp.disconnect();
		}
		
		super.disconnect();
		
	}
}
