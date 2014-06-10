package platform;

import java.util.ArrayList;

import platform.Sentense.*;
import platform.Sentense.Simple.SimpleSentense;

public class Statement {
	private boolean official;
	private Names playerName;
	private String content;
	private ArrayList<Sentense> sentenses;
	private int order;//赤,青ログ専用フィールド。白ログのどの発言の後になされた発言かを記録するため。logのindex番号2の発言の後なら２が入る。

	private ArrayList<SimpleSentense> sentenses_easy = new ArrayList<>();

	/**
	 * @param isOfficial 白ログの場合はtrue、赤ログの場合はfalse
	 */
	public Statement(boolean isOfficial){
		official = isOfficial;
	}

	/**
	 * 白ログを生成
	 */
	public Statement(){
		this(true);
	}

	public void setPlayerName(Names name){
		playerName = name;
	}

	public Names getPlayerName(){
		return playerName;
	}

	public String getContent(){
		return content;
	}
	public void setContent(String set){
		content = set;
	}

	public ArrayList<Sentense> getSentense(){
		return sentenses;
	}

	void setorder(int set){
		order = set;
	}

	public void addSentense(Conjunction con){
		sentenses.add(new Sentense(con));
	}

	public String getString(){

		return null;
	}

	//卒論用の簡単な言語
	public void addEasySentense(SimpleSentense sentense){
		sentenses_easy.add(sentense);
	}
	public ArrayList<SimpleSentense> getEasySentenses(){
		return sentenses_easy;
	}

}
