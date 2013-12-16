package or.tango.android.pojo;

public class Suggestion implements Comparable<Suggestion>{
	String text;
	double zhixinDu;
	public Suggestion(String text,double zhixinDu) {
		// TODO Auto-generated constructor stub
		this.text=text;
		this.zhixinDu=zhixinDu;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public double getZhixinDu() {
		return zhixinDu;
	}
	public void setZhixinDu(double zhixinDu) {
		this.zhixinDu = zhixinDu;
	}
	@Override
	public int compareTo(Suggestion o) {
		// TODO Auto-generated method stub
		if(this.zhixinDu<o.zhixinDu)
			return 1;
		else if (this.zhixinDu==o.zhixinDu) {
			return 0;
		}
		else {
			return -1;
		}
	}
	
}
