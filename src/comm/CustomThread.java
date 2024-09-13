package comm;

import javax.swing.JLabel;

public class CustomThread extends Thread{

	private double totalProc;
	private double nowProc;
	private JLabel label;
	
	public CustomThread() {
		super();
	}
	
	public void update(){
		if(label != null && label.isVisible() && this.getTotalProc() != 0){
			
			if(this.getTotalProc() != this.getNowProc()){
				String progressStr = "진행 률 : ";
				progressStr += this.getNowProc() + "/" + this.getTotalProc() + "( " + this.getPercent() + "% )";
				label.setText(progressStr);
			}else {
				label.setText("완료");
			}
			
		}
	}
	
	public CustomThread(Runnable runnable) {
		super(runnable);
	}
	public int getPercent(){
		return (int) ( nowProc * 100 / totalProc );
	}
	public double getTotalProc() {
		return totalProc;
	}
	public void setTotalProc(double totalProc) {
		this.totalProc = totalProc;
	}
	public double getNowProc() {
		return nowProc;
	}
	public void setNowProc(double nowProc) {
		this.nowProc = nowProc;
		update();
	}
	public JLabel getLabel() {
		return label;
	}
	public void setLabel(JLabel label) {
		this.label = label;
	}
	
}
