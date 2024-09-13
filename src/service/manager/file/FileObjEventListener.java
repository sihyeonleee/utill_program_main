package service.manager.file;

public interface FileObjEventListener {
	public void onMenuEvt(FileObject label, String menuType);
	public void onDouClickEvt(FileObject label);
	public void onReleaseEvt(FileObject label);
	public void onFocusEvt(FileObject label);
}
