package or.tango.android.pojo;

import java.util.ArrayList;

public class Citiao {
	
	String text;
	int type;
	double zhixinDu;
	ArrayList<Suggestion> otherSuggestions=new ArrayList<Suggestion>();
	public Citiao(String text) {
		// TODO Auto-generated constructor stub
		this.text=text;
		this.type=-1;
		this.zhixinDu=0;
	}
	public void addSuggestion(Suggestion suggestion)
	{
		this.otherSuggestions.add(suggestion);
	}
	public void removeSuggestion(Suggestion suggestion)
	{
		this.otherSuggestions.remove(suggestion);
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
		if(this.type==0||zhixinDu==2)
			zhixinDu=1.0f;
	}
	public double getZhixinDu() {
		return zhixinDu;
	}
	public void setZhixinDu(double zhixinDu) {
		this.zhixinDu = zhixinDu;
	}
	public ArrayList<Suggestion> getOtherSuggestions() {
		return otherSuggestions;
	}
	public void setOtherSuggestions(ArrayList<Suggestion> otherSuggestions) {
		this.otherSuggestions = otherSuggestions;
	}
}
