package gui.main;

import java.awt.TrayIcon.MessageType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HINSTANCE;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.HOOKPROC;
import com.sun.jna.platform.win32.WinUser.KBDLLHOOKSTRUCT;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.Native;
import com.sun.jna.Library;

import comm.CryptoUtil;
import comm.Path;
import comm.fileio.FileChooser;
import comm.fileio.TextFileReader;
import main.Main;

public class JnaHook {
	
	public JFrame frame = null;
	public List<JButton> btnList = null;
	
	public static List<Map<String, String>> hotKeyList = null;
	
	public JnaHook(final JFrame frame, final List<JButton> btnList) {
		TextFileReader reader = new TextFileReader(Path.ROOTPATH+"prop", Path.PATH_FILE_HOTKEY, "lsh");
		@SuppressWarnings("static-access")
		Map<String, Map<String, String>> readeResult = reader.getKeyValueTypeDataToMap(reader.reade(true), "\\[HOTKEY\\]", "key");
		hotKeyList = new ArrayList<>(readeResult.values());
		this.frame = frame;
		this.btnList = btnList;
		this.globalHotKey();
	}
    
	// ### 글로벌 단축키 ###
	public void globalHotKey() {
        HOOKPROC hookProc = new hookProc();
        HINSTANCE hInst = Kernel32.INSTANCE.GetModuleHandle(null);
        User32.HHOOK hHook = User32.INSTANCE.SetWindowsHookEx(User32.WH_KEYBOARD_LL, hookProc, hInst, 0);
        User32.MSG msg = new User32.MSG();
        
        if (hHook != null) while (true) User32.INSTANCE.GetMessage(msg, null, 0, 0);
        else TrayIconHandler.displayMessage("에러 발생", "ShortCut Key Error : 다시 실행해주세요.", MessageType.ERROR);
	}
	
	public class KeyCode {
    	Integer CTRL = 162;
    	Integer ALT = 164;
    	Integer ESC = 27;
    	Integer ENTER = 13;
    	Integer SPACE = 32;
    	Integer BACKSPACE = 8;
    	Integer LEFT = 37;
    	Integer UP = 38;
    	Integer RIGHT = 39;
    	Integer DOWN = 40;
    	Integer TILT = 192;
    	Integer MAIN_MENU = 0;
    	Integer SERVER_ON = 1921;
    	Integer SERVER_MSG_SEND = 1922;
    	Integer SERVER_USER_CNT_CHECK = 1923;
    	Integer EXIT = BACKSPACE;
    	Integer KAKAO = TILT;
    	
    	Integer F1 = 112;
    	Integer F2 = 113;
    	Integer F3 = 114;
    	Integer F4 = 115;
    	Integer F5 = 116;
    	Integer F6 = 117;
    	Integer F7 = 118;
    	Integer F8 = 119;
    	Integer F9 = 120;
    	Integer F10 = 121;
    	Integer F11 = 122;
    	Integer F12 = 123;
    	
    	Integer INS = 45;
    	Integer DEV = 46;
    	Integer HOME = 36;
    	Integer END = 35;
    	Integer PAGEUP = 33;
    	Integer PAGEDOWN = 34;
	}
    
	public class hookProc implements HOOKPROC {
		
		KeyCode key = new KeyCode();
    	String inputKey = "";
 		
		public LRESULT callback(int nCode, WPARAM wParam, KBDLLHOOKSTRUCT info) throws Exception{
			
			switch (wParam.intValue()) {
				// 모든키 입력 시
				case WinUser.WM_KEYUP:
					// esc
					if(info.vkCode == key.ESC && frame.isVisible()) {
						// display close 
						frame.dispose();
					// alt + @
					}else if(info.vkCode == key.ALT && !inputKey.equals("")) {
						
						int shortKey = 0;
						
						try {
							shortKey = Integer.parseInt(inputKey);
						}catch(Exception err) {
							// inputKey parse faild ( big size num )
							inputKey = "";
							err.printStackTrace();
							return null;
						}
							
						// short key service event
						if(shortKey == key.MAIN_MENU) {
							frame.setVisible(true); // View Main Menu
						}else if(shortKey <= btnList.size()) {
							// @@ Log 단축 실행시 멈춤현상...
							JButton btn = btnList.get(shortKey-1);
							if(!btn.getText().contains("Log")) btn.doClick(); // Service Menu Click
						}else {
							// btnList 보다 큰 숫자에 대한 명령 커스텀
							altKeyUpCustomCmd(shortKey); 
						}
						inputKey = "";
					}
					break;
				// ALT 키 입력시 
				case WinUser.WM_SYSKEYDOWN:
					if(info.vkCode != key.ALT) {
						Integer num = getKeyCodeToDec(info.vkCode);
						// 등록된 서비스 호출번호 add
						if(num != null) {
							inputKey += num;
						// 등록된 서비스외 기능 호출 커스텀 적용
						}else {
							// 위 호출번호 초기화
							inputKey = "";
							// 0~9 숫자 외 키입력에 대한 명령 커스텀
							altKeyDownCustomCmd(info.vkCode);
						}
					}
					break;
			}
		    return new LRESULT(0);
		}
		
		// run service number
		public Integer getKeyCodeToDec(int code) {
			Integer dec = null;
			if(code >= 96 && code <= 105) dec = code - 96;
			else if (code >= 48 && code <= 57) dec = code - 48;
			return dec;
		}
		
		
		/**
		 * ============================= 커스텀 이벤트 ==================================
		 */
		@SuppressWarnings("static-access")
		public void altKeyDownCustomCmd(int shortKey) {
			// 커스텀 호출 적용
			if(shortKey == key.EXIT){
				System.exit(0); // EXIT
			}
//			else if(shortKey == key.KAKAO) {
//				// KAKAO doShow
//		        HWND hWnd = User32.INSTANCE.FindWindow(null, "카카오톡");
//		        if (hWnd != null) {
//		        	// show & hide
//		        	boolean isKakaoShow = User32.INSTANCE.IsWindowVisible(hWnd);
//		        	if(isKakaoShow) User32.INSTANCE.ShowWindow(hWnd, WinUser.SW_HIDE);
//		        	else User32.INSTANCE.ShowWindow(hWnd, WinUser.SW_RESTORE);
//		        } else {
//		        	// exec
//					FileChooser ch = new FileChooser(0);
//					ch.exec("C:\\Program Files (x86)\\Kakao\\KakaoTalk\\KakaoTalk.exe", false);
//		        }
//			}
			else {
				for(Map<String, String> k : hotKeyList) {
					String value = k.get("value");
					String trayIcon = k.get("trayIcon");
					String installPath = k.get("installPath");
					if(value != null && !"".equals(value) && Integer.parseInt(value) == shortKey) {
						HWND hWnd = User32.INSTANCE.FindWindow(null, trayIcon);
				        if (hWnd != null && !"".equals(trayIcon.trim())) {
				        	// show & hide
				        	boolean isShow = User32.INSTANCE.IsWindowVisible(hWnd);
				        	if(isShow) User32.INSTANCE.ShowWindow(hWnd, WinUser.SW_HIDE);
				        	else User32.INSTANCE.ShowWindow(hWnd, WinUser.SW_RESTORE);
				        } else if(!"".equals(installPath.trim())){
				        	// exec
							FileChooser ch = new FileChooser(0);
							ch.exec(installPath.trim(), false);
				        }
					}
				}
			}
		}
		
		public void altKeyUpCustomCmd(int shortKey) {
			if(shortKey == key.SERVER_ON || shortKey == key.SERVER_MSG_SEND || shortKey == key.SERVER_USER_CNT_CHECK) {
				// 관리자일경우 사용되는 명령
				serverCommand(shortKey);
			}
		}
		
		public void serverCommand(int shortKey) {
			
			if(!Main.isAdmin) return;
				
			if(shortKey == key.SERVER_ON){
				String pwd = JOptionPane.showInputDialog("");
				if(pwd == null || pwd.equals("")) {
					
				}else {
					Main.isAdmin = CryptoUtil.isAdmin(CryptoUtil.sha256(pwd));
					Main.net.serverOn();
				}
			}else if(shortKey == key.SERVER_MSG_SEND){
				Object msg = JOptionPane.showInputDialog(null, "", "", JOptionPane.PLAIN_MESSAGE, null, Main.adminCmds, Main.adminCmds[0]);
				if(msg != null) Main.net.send((String) msg);
			}else if(shortKey == key.SERVER_USER_CNT_CHECK){
				System.out.println("현재 접속인원 : " + Main.net.connectedUserCnt() + "명");
			}
		}
		
	}
	
}
