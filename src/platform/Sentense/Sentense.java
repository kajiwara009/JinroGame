package platform.Sentense;

import java.util.ArrayList;

import platform.Names;
import platform.Roles;
import platform.Verb;
import platform.Sentense.Simple.SimpleSentense;

public class Sentense {
	private final int MAX_NUMBER_OF_SENTENSES = 3;
	private ArrayList<Sentense> sentenses = null;
	private Conjunction conjunction = null;

	public Sentense(){
//		sentenses.add(new SimpleSentense());
	}
	public Sentense(Conjunction con){
		conjunction = con;
/*		int num = con.selectPattern();
		for(int i = 0; i < num; i++){
			sentenses.add(new Sentense());
		}*/
	}



	public Conjunction getConjunction(){
		return conjunction;
	}
	public Sentense getSentense1(){
		return getSentenseNum(1);
	}
	public Sentense getSentense2(){
		return getSentenseNum(2);
	}
	public Sentense getSentense3(){
		return getSentenseNum(3);
	}

	//これを使った後にsentenseInの元となるSentenseをいじったら反映してれるか？してくれないと困る
	public int setSentenseNum(int num, Sentense sentenseIn){
		if(num <= 0 || num > MAX_NUMBER_OF_SENTENSES){
			return -1;
		}else{
			sentenses.set(num - 1, sentenseIn);
			return 1;
		}

	}

	/**
	 * num番目のSentenseを返す。SimpleSentenseならnum=1のみ。 num = 0とかはエラー
	 * @param num
	 * @return
	 */
	public Sentense getSentenseNum(int num){
		if(num <= 0 || num > this.sentenses.size()){
			return null;
		}
		try {
			return sentenses.get(num - 1).clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Sentense clone() throws CloneNotSupportedException{
		return (Sentense) super.clone();
	}

	public String getString(){
		if(this instanceof SimpleSentense){
			SimpleSentense ss = (SimpleSentense) this;
			return ss.getString();
		}else if(conjunction == Conjunction.IF){
			if(sentenses.get(2) == null){
				return "if (" + sentenses.get(0).getString() + ") then (" + sentenses.get(1).getString() + ")";
			}else{
				return "if (" + sentenses.get(0).getString() + ") then (" + sentenses.get(1).getString() + ") else (" + sentenses.get(2).getString() + ")";
			}
		}else{
			return "(" + sentenses.get(0).getString() + ") " + conjunction.toString() + " (" + sentenses.get(1).getString() + ")";
		}
	}
	/*private Sentense sentense1, sentense2, sentense3;
	public Sentense getSentense1(){
		return sentense1;
	}
	public void setSentense1(Sentense set){
		sentense1 = set;
	}

	public Sentense getSentense2(){
		return sentense2;
	}
	public void setSentense2(Sentense set){
		sentense2 = set;
	}

	public Sentense getSentense3(){
		return sentense3;
	}
	public void setSentense3(Sentense set){
		sentense3 = set;
	}


*/


/*	public void setConjunction(Conjunction con){
		conjunction = con;
	}
*/




}
