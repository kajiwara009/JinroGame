package platform.Sentense.Simple;

import platform.Names;
import platform.Roles;
import platform.Verb;
import platform.Sentense.Sentense;

public class SimpleSentense extends Sentense {
	Object S;
	Verb V;
	Object C;
	Object O;
	SentensePattern sp;
	boolean affirmative = true;


	//卒論用
	public platform.Sentense.Simple.Verb v = null;
	public Names name = null;
	public Roles role = null;
	public boolean isHuman = false;
	public boolean successGuard = false;

	public static SimpleSentense comingOut(Roles r){
		SimpleSentense s = new SimpleSentense();
		s.v = platform.Sentense.Simple.Verb.CO;
		s.role = r;
		return s;
	}
	/**
	 * 人間ならtrueが入ってる
	 * @param n
	 * @param result
	 * @return
	 */
	public static SimpleSentense inspectResult(Names n, boolean result){
		SimpleSentense s = new SimpleSentense();
		s.v = platform.Sentense.Simple.Verb.inspectResult;
		s.name = n;
		s.isHuman = result;
		return s;
	}
	public static SimpleSentense mediumResult(Names n, boolean result){
		SimpleSentense s = new SimpleSentense();
		s.v = platform.Sentense.Simple.Verb.tellResult;
		s.name = n;
		s.isHuman = result;
		return s;
	}
	public static SimpleSentense guardResult(Names n, boolean result){
		SimpleSentense s = new SimpleSentense();
		s.v = platform.Sentense.Simple.Verb.guard;
		s.name = n;
		s.successGuard = result;
		return s;
	}
	public static SimpleSentense vote(Names n){
		SimpleSentense s = new SimpleSentense();
		s.v = platform.Sentense.Simple.Verb.vote;
		s.name = n;
		return s;
	}
	public static SimpleSentense requestInspect(Names n){
		SimpleSentense s = new SimpleSentense();
		s.v = platform.Sentense.Simple.Verb.request;
		s.name = n;
		return s;
	}
	public static SimpleSentense suspect(Names n){
		SimpleSentense s = new SimpleSentense();
		s.v = platform.Sentense.Simple.Verb.suspect;
		s.name = n;
		return s;
	}



	//ここから本当のSimpleSentense

	//これから意味をとるためのメソッドを作るが、意味を作るって何だろう・・・よくわかんないって感じ
	//目的語をObjectiveというクラス作って、というか名詞っていうのを作るべき？主語も（A and B）とかなりうるし

	//これらも目的語を代入した後に、代入元を変えたら反映されてくれるかチェックしなきゃ
	public void setSO(Object s, Object o) {
		sp = SentensePattern.SO;
		S = s;
		O = o;
	}

	public void setSV(Object s, Verb v) {
		sp = SentensePattern.SV;
		S = s;
		V = v;
	}

	public void setSVO(Object s, Verb v, Object o) {
		sp = SentensePattern.SVO;
		S = s;
		V = v;
		O = o;
	}

	public void setVO(Verb v, Object o) {
		sp = SentensePattern.VO;
		V = v;
		O = o;
	}

	public void setAffirmativeSentense(){
		affirmative = true;
	}
	public void setNegativeSentense(){
		affirmative = false;
	}

	public String getString(){
		String answer = null;
		if(affirmative == false){
			answer = "not (";
		}
		switch (sp) {
		case SO:
			answer = answer + S.toString() + "\t" + O.toString();
			break;
		case SV:
			answer = answer + S.toString() + "\t" + V.toString();
			break;

		case SVO:
			answer = answer + S.toString() + "\t" + V.toString() + "\t" + O.toString();
			break;

		case VO:
			answer = answer + V.toString() + "\t" + O.toString();
			break;
		}
		if(affirmative == false){
			answer = answer + ")";
		}
		return null;
	}

}
