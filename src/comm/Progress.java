package comm;

public class Progress {
	
	private String text;
	private int totProc;
	private int nowProc;
	
	public void nowProc(int total, int now){
		
	}
	
	public int getTotProc() {
		return totProc;
	}

	public void setTotProc(int totProc) {
		this.totProc = totProc;
	}

	public int getNowProc() {
		return nowProc;
	}

	public void setNowProc(int nowProc) {
		this.nowProc = nowProc;
		nowProc(this.totProc, this.nowProc);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
