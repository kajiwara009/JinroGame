package platform;

import platform.Sentense.Sentense;

public abstract class Verb {
	Object subject;
	int percent;
	Sentense sentense;

	public int setPercent(int per){
		if(per >= 0 && per <= 100){
			percent = per;
			return 1;
		}else{
			return -1;
		}
	}
	public int getPercent(){
		return percent;
	}

	public void setSentense(Sentense sentenseIn){
		sentense = sentenseIn;
	}
	public Sentense getSentense() throws CloneNotSupportedException{
		return sentense.clone();
	}


}
