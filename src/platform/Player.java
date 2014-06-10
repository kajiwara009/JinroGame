package platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import platform.*;
import platform.Reinforcement_Learning.Environment.Environment;
import platform.Reinforcement_Learning.LearningData.LD_connectCondition;
import platform.Reinforcement_Learning.LearningData.LD_suspectCondition;
import platform.Sentense.Simple.SimpleSentense;
import player.Strategy;

public class Player {
	public final static int MaxCountOfvoice = 100;

	private Names name;
	private Roles role;
	private Strategy strategy;
	private boolean survive;
	/** 外からいじれないように。Speakしたら自動的に１減る。リセットできる　外からは残り回数だけ見れる */
	private ArrayList<ArrayList<Statement>> log = new ArrayList<ArrayList<Statement>>();
	private ArrayList<ArrayList<Statement>> redLog = new ArrayList<ArrayList<Statement>>();
	private ArrayList<Statement> information = new ArrayList<Statement>();
	private int restOfVoice, restOfRedVoice;
	private Environment env;
	private HashMap<Names, SuspectCondition> mySuspection = new HashMap<>();
	private HashMap<Names, ConnectCondition> myConnect = new HashMap<>();

	// getSuspectionみたいなメソッドを作って、呼び出すときに毎回言われてないやつを足して返してくれる

	public Names suspect_said = null;
	public Names suspect_said_yesterday = null;
	public Names wantInspect_said = null;
	public Names wantInspect_said_yesterday = null;
	// 占、霊能ならばBoolean = isHuman、狩人ならば Boolean = successGuard
	public ArrayList<Infomation> info_Easy = new ArrayList<>();
	// 自分も含める
	public ArrayList<Names> friendWolfs = new ArrayList<>();

	public Player() {
	}

	public Player(Names nameIn, Roles roleIn, Strategy abstractPlayerIn) {
		setName(nameIn);
		setRole(roleIn);
		strategy = abstractPlayerIn;
		strategy.setPlayer(this);
		survive = true;
		restOfVoice = MaxCountOfvoice;
		if (role == Roles.wolf)
			restOfRedVoice = MaxCountOfvoice;
	}

	public Player(Names nameIn, Roles roleIn, Strategy abstractPlayerIn,
			Environment e) {
		this(nameIn, roleIn, abstractPlayerIn);
		env = e;
		env.setPlayer(this);
	}

	public Strategy getStrategy() {

		return strategy;
	}

	public Statement talking(int i) {
		Statement statement = new Statement();
		/**
		 * ここをみんなに作ってもらう。 どういう時にしゃべるのか。 何も話すことがない場合はNULLを返す。
		 */
		return null;
	}

	public Statement talkOnRedLog() {
		return null;
	}

	public Statement speak(String content) {
		Statement statement = new Statement();
		statement.setPlayerName(this.getName());
		statement.setContent(content);

		return statement;
	}

	protected Player clone() throws CloneNotSupportedException {
		return (Player) super.clone();
	}

	public Names getName() {
		return name;
	}

	private void setName(Names nameIn) {
		name = nameIn;
	}

	public Roles getRole() {
		return role;
	}

	private void setRole(Roles set) {
		role = set;
	}

	public boolean isSurvive() {
		return survive;
	}

	void toBeDead() {
		survive = false;
	}

	public ArrayList<Statement> getLogOfToday(Field field) {
		ArrayList<Statement> re = new ArrayList<Statement>(log.get(field
				.getDay()));
		return re;
	}

	public ArrayList<ArrayList<Statement>> getLog() {
		ArrayList<ArrayList<Statement>> re = new ArrayList<ArrayList<Statement>>(
				log);
		return re;
	}

	ArrayList<Statement> getRedLogOfToday(Field field) {
		return redLog.get(field.getDay());
	}

	ArrayList<ArrayList<Statement>> getRedLog() {
		return redLog;
	}

	void setLogs(Field field) {
		log = field.getLog();
		redLog = this.getRole() == Roles.wolf ? field.getRedLog() : null;
	}

	void addInformation(Statement info) {
		information.add(info);
	}

	/**
	 * 公開性が謎
	 *
	 * @return
	 */
	ArrayList<Statement> getInformation() {
		return (ArrayList<Statement>) information.clone();
	}

	public int getRestOfVoice() {
		return restOfVoice;
	}

	void useVoice() {
		restOfVoice--;
	}

	public int getRestOfRedVoice() {
		return restOfRedVoice;
	}

	void useRedVoice() {
		restOfRedVoice--;
	}

	void chargeVoices() {
		restOfVoice = MaxCountOfvoice;
		if (role == Roles.wolf)
			restOfRedVoice = MaxCountOfvoice;
	}

	public boolean isGuardAttack() {
		for (Infomation info : info_Easy) {
			if (info.bool)
				return true;
		}
		return false;

	}

	public boolean isfindWolf() {
		for (Infomation info : info_Easy) {
			if (!info.bool)
				return true;
		}
		return false;
	}

	public Environment getEnvironment() {
		return env;
	}

	/**
	 * roleには自分の立場を入れる。もし、自分が占い師だったらどのようになるかはrole = seerとすれば分かる。
	 * @param field
	 * @param role
	 * @return
	 */
	public HashMap<Names, SuspectCondition> getMySuspection(Field field) {
//		if(true) return field.sub.suspectConditions;
		if(info_Easy.size() == 0) return field.sub.suspectConditions;
		else{
			boolean existIsntSaid = false;
			for(Infomation info: info_Easy){
				if(!info.isSaid) existIsntSaid = true;
				break;
			}
			if(!existIsntSaid) return field.sub.suspectConditions;
		}
		HashMap<Names, SuspectCondition> tmp = new HashMap<>();
		for(Names n: Names.values()){
			if(field.sub.suspectConditions.containsKey(n)){
				tmp.put(n, new SuspectCondition());
			}
		}
		for(Map.Entry<Names, SuspectCondition> set: field.sub.suspectConditions.entrySet()){
			SuspectCondition s = set.getValue().clone();
			Names n = set.getKey();
			tmp.put(n, s);
		}
		
		
		Roles r = env.getFakeRole();
		if(r == null) r = role;

		for(Infomation info: info_Easy){
			if(!info.isSaid){
				SuspectCondition s2 = tmp.get(info.name).clone();
				switch (r) {
				case seer:
					s2.setInspected_condition(info.bool);
					break;
				case medium:
					s2.setTelled_condition(info.bool);
					break;
				}
				tmp.put(info.name, s2);
			}
		}
		return tmp;
	}

	public HashMap<Names, ConnectCondition> getMyConnection(Field field) {
		if(info_Easy.size() == 0) return field.sub.connectConditions;
		
		else{
			boolean existIsntSaid = false;
			for(Infomation info: info_Easy){
				if(!info.isSaid) existIsntSaid = true;
				break;
			}
			if(!existIsntSaid) return field.sub.connectConditions;
		}
		HashMap<Names, ConnectCondition> tmp = new HashMap<>();
		for(Map.Entry<Names, ConnectCondition> set: field.sub.connectConditions.entrySet()){
			tmp.put(set.getKey(), set.getValue().clone());
		}
		Roles r = env.getFakeRole();
		if(r == null) r = role;

		for(Infomation info: info_Easy){
			if(!info.isSaid){
				ConnectCondition c = tmp.get(info.name);
				switch (r) {
				case seer:
					c.condition.get(name)[ConnectCondition.inspected] = info.bool? 1: -1;
					break;
				case medium:
					c.condition.get(name)[ConnectCondition.telled] = info.bool? 1: -1;
					break;
				case hunter:
					c.condition.get(name)[ConnectCondition.guarded] = info.bool? 1: -1;
					break;
				}
			}
		}
		return tmp;
	}

	/**
	 * 人狼や狂人はなりきった上での怪しい度合いを出したければ、boolean に falseを入れる
	 * 全員のSuspectDoubleを出す。（人狼側ならちょっと変える）
	 * 全員のコネクトバリュー出すHash in　Hash
	 * field.playersのfor文で全員の出していく。単純に掛け算
	 *
	 * @param field
	 * @param isTrueRole
	 * @return
	 */
	public HashMap<Names, Double> getTrustyValue(Field field, boolean asHumanSide){
		HashMap<Names, Double> answers = new HashMap<>();
		answers.put(name, asHumanSide? 1.0: 0.0);

/*		for(Player p : field.getPlayers()){
			answers.put(p.name, 0.0);
		}
		if(true) return answers;
*/
		HashMap<Names, Double> suspectValues = new HashMap<>();
		HashMap<Names, HashMap<Names, Double>> connectValueses = new HashMap<>();

		HashMap<Names, ConnectCondition> myConnections = getMyConnection(field);
		HashMap<Names, SuspectCondition> mySuspections = getMySuspection(field);

		Roles asRole = !asHumanSide? env.getFakeRole(): role;

		LD_suspectCondition ld_sus = field.sub.env.LD_suspect.get(asRole);

		//まずはSuspect,ConnectVallueを全部求める
		for(Player p: field.getPlayers()){
			if(p.name == name){
				suspectValues.put(name, asHumanSide? 1.0: 0.0);
				//connectValuesesの自分部分は作らない
			}else if(!asHumanSide && friendWolfs.contains(p.name)){
				suspectValues.put(p.name, 0.0);
			}else{
				double suspectValue_p = mySuspections.get(p.name).getSupectValue(ld_sus);
				suspectValues.put(p.name, suspectValue_p);
			}
			HashMap<Names, Double> connectValue_p = myConnections.get(p.name).getConnectValues(asRole, this, p, field);
			connectValueses.put(p.name, connectValue_p);

//			connectValueses.put(p.name, myConnections.get(p.name).getConnectValues(asRole, this, p, field));

		}
		if (!asHumanSide) {
			for (Names n1 : friendWolfs) {
				for (Names n2 : friendWolfs) {
					connectValueses.get(n1).put(n2, 1.0);
				}
			}
		}

		//一気に各プレイやーの値を計算する
		for(Player p : field.getPlayers()){
			if(p.name == name) continue;
			double tmp = 0.0;
			for(Map.Entry<Names, Double> sus: suspectValues.entrySet()){
				tmp += sus.getValue() * connectValueses.get(p.name).get(sus.getKey());
			}
			if(tmp > 1.0) tmp = 1.0;
			else if( tmp < -1.0) tmp = -1.0;
			answers.put(p.name, tmp);
		}
		return answers;
	}

	public HashMap<Names, Double> getTrustyValue(Field field, boolean asHumanSide, ArrayList<Names> fulfills){
		
		HashMap<Names, Double> answers = new HashMap<>();
		
/*		for(Player p : field.getPlayers()){
			answers.put(p.name, 0.0);
		}
		if(true) return answers;
*/		
		if(fulfills.contains(name)) answers.put(name, asHumanSide? 1.0: 0.0);

		HashMap<Names, Double> suspectValues = new HashMap<>();
		HashMap<Names, HashMap<Names, Double>> connectValueses = new HashMap<>();
		HashMap<Names, ConnectCondition> myConnections = getMyConnection(field);
		HashMap<Names, SuspectCondition> mySuspections = getMySuspection(field);
		
		Roles asRole = !asHumanSide? env.getFakeRole(): role;

		LD_suspectCondition ld_sus = field.sub.env.LD_suspect.get(asRole);

		//まずはSuspect,ConnectVallueを全部求める
		for(Player p: field.getPlayers()){
			if(p.name == name){
				suspectValues.put(name, asHumanSide? 1.0: 0.0);
				//connectValuesesの自分部分は作らない
			}else if(!asHumanSide && friendWolfs.contains(p.name)){
				suspectValues.put(p.name, 0.0);
			}else{
				double suspectValue_p = mySuspections.get(p.name).getSupectValue(ld_sus);
				suspectValues.put(p.name, suspectValue_p);
			}
			if (fulfills.contains(p.getName())) {
				HashMap<Names, Double> connectValue_p = myConnections.get(p.name).getConnectValues(asRole, this, p, field);
				connectValueses.put(p.name, connectValue_p);
			}
		}
		if (!asHumanSide) {
			for (Names n1 : friendWolfs) {
				if (connectValueses.containsKey(n1)) {
					for (Names n2 : friendWolfs) {
						connectValueses.get(n1).put(n2, 1.0);
					}
				}
			}
		}
		
		

		//一気に各プレイやーの値を計算する
		for(Names n : fulfills){
			if(n == name) continue;
			double tmp = 0.0;
			for(Map.Entry<Names, Double> sus: suspectValues.entrySet()){
				tmp += sus.getValue() * connectValueses.get(n).get(sus.getKey());
			}
			if(tmp > 1.0) tmp = 1.0;
			else if( tmp < -1.0) tmp = -1.0;
			answers.put(n, tmp);
		}
		return answers;
	}

	public Names getTheMostSuspectPersonsName(Field field, boolean asHumanSide, ArrayList<Names> names){
		HashMap<Names, Double> answers = new HashMap<>();
		answers.put(name, asHumanSide? 1.0: 0.0);

		HashMap<Names, Double> suspectValues = new HashMap<>();
		HashMap<Names, HashMap<Names, Double>> connectValueses = new HashMap<>();

		HashMap<Names, ConnectCondition> myConnections = getMyConnection(field);
		HashMap<Names, SuspectCondition> mySuspections = getMySuspection(field);

		Roles asRole = asHumanSide? env.getFakeRole(): role;

		LD_suspectCondition ld_sus = field.sub.env.LD_suspect.get(asRole);



		return null;
	}
	
	public boolean isExistInfoYet(Names n){
		for(Infomation info: info_Easy){
			if(info.name == n){
				return true;
			}
		}
		return false;
	}


}
