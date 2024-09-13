package comm;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoUtil {
	
	public static String sha256(String msg) {

	    MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
		    md.update(msg.getBytes());
		    return CryptoUtil.byteToHexString(md.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return null;

	}
	
	public static String md5(String msg) {

	    MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(msg.getBytes());
			return CryptoUtil.byteToHexString(md.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
		
	}

	public static String byteToHexString(byte[] data) {
	    StringBuilder sb = new StringBuilder();
	    for(byte b : data) {
	        sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
	    }
	    return sb.toString();
	}
	
	@SuppressWarnings("static-access")
	public static boolean isAdmin(String crptPwd){
		
		boolean isAdmin = false;
		
		String adminPwd = sha256("1008");
		
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			if(md.isEqual(crptPwd.getBytes(), adminPwd.getBytes())) isAdmin = true;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return isAdmin;
		
	}

}
