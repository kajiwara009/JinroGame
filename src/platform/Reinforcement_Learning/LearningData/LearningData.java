package platform.Reinforcement_Learning.LearningData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import platform.*;
import platform.Sentense.Simple.SimpleSentense;
import platform.Sentense.Simple.Verb;

public class LearningData {
	//狂人と狼だけ変える
	double initNumber = VSmode.defaultQ_human;
	public int selectedSum = 0;
	/**
	 * 日数15日目に必ず終了
	 * 生き残り人数１６
	 * 占い師CO人数（生き残り）
	 * 霊媒師CO人数”
	 * 狩人CO人数”
	 * 疑われている（Connectで4以上Suspected）
	 * 自分が占われている（白　無し　黒）
	 * のそれぞれの状況が分かるようにint[]を持っておく
	 **/

	//言わない、能力者以外の一番怪しい奴、能力者以外の２番目に怪しい奴、怪しい（占い、霊媒、狩人）、パンダ、周りに合わせる、ランダム
	//人狼側確定の奴、
	//人狼は　人狼以外で一番怪しい、
	//一旦使わない
	double[] talk_voteQ;

	public static final String[] voteStrategys = {"怪しい非能力者","怪しい占","怪しい霊","怪しい狩"};
	public double[] action_voteQ = new double[voteStrategys.length];

	public static final  String[] talkSuspects = {"怪しい非能力","怪しい占","怪しい霊","怪しい狩"};
	public double[] talk_suspectQ = new double[talkSuspects.length];

	public static final String[] talkRequests = {"怪しい非能力","怪しい占","怪しい霊","怪しい狩","怪しくない"};
	public double[] talk_requestQ = new double[talkRequests.length];


	public double[] act_attackQ;
	public double[] data_Guard;
	public double[] fake_inspectQ;
	public double[] fake_mediumQ;
	public double[] fake_defenceQ;
	public double[] data_InspectQ;

	public LearningData(){
		for(int i = 0; i < talk_requestQ.length; i++){
			talk_requestQ[i] = initNumber;
		}
		for(int i = 0; i < talk_suspectQ.length; i++){
			talk_suspectQ[i] = initNumber;
		}
		for(int i = 0; i < action_voteQ.length; i++){
			action_voteQ[i] = initNumber;
		}
	}

	public String getGuardOptimalStrategy(){
		return null;
	}
	public String getInspectOptimalStrategy(){
		return null;
	}
	public String getAttackOptimalStrategy(){
		return null;
	}
	public String getFakeInspectOptimalStrategy(){
		return null;
	}
	public String getFakeMediumOptimalStrategy(){
		return null;
	}
	public String getFakeDefenceOptimalStrategy(){
		return null;
	}


	public String getVoteOptimalStrategy(){
		int column = getMaxColumn(action_voteQ);
		return voteStrategys[column];
	}

	public String getTalk_SuspectOptimalStrategy(){
		int column = getMaxColumn(talk_suspectQ);
		return talkSuspects[column];
	}

	public String getTalk_RequestOptimalStrategy(){
		int column = getMaxColumn(talk_requestQ);
		return talkRequests[column];
	}




	public static int getMaxColumn(double[] data){
		int ans = 0;
		for(int i = 0; i < data.length;i++){
			if(data[i] > data[ans]){
				ans = i;
			}
		}
		return ans;
	}
	public static double getMaxValue(double[] data){
		double ans = 0;
		for(int i = 0; i < data.length;i++){
			if(data[i] > ans){
				ans = data[i];
			}
		}
		return ans;
	}

	public static double getAvarageValue(double[] data){
		double ans = 0;
		int remove = 0;
		for(double v: data){
			if(v == VSmode.defaultQ_human || v == VSmode.defaultQ_wolf){
				remove++;
			}else{
				ans += v;
			}
		}
		return ans / (double)(data.length-remove);
	}

	/**
	 * Integer[2] で suspectのStr, requestのStrの順に入ってる
	 * @param field
	 * @param me
	 * @return
	 */
	public Map.Entry<Statement, Integer[]> makeStatement(Field field, Player me){
		HashMap<Statement, Integer[]> ans = new HashMap<>();
		ArrayList<Integer> removeChoice = new ArrayList<>();

		if(!isAbleTalk(field.sub.seers, field, me, true))
			removeChoice.add(1);
		if(!isAbleTalk(field.sub.mediums, field, me, true))
			removeChoice.add(2);
		if(!isAbleTalk(field.sub.hunters, field, me, true))
			removeChoice.add(3);

//		Integer[] inte = {rouletteSelect(talk_suspectQ, removeChoice), rouletteSelect(talk_requestQ, removeChoice)};
		Integer[] inte = {rouletteSelect(talk_suspectQ, removeChoice), 0};
		Statement s = new Statement();
		SimpleSentense ss1 = makeSuspectSentense(inte[0], field, me);
//		SimpleSentense ss2 = makeRequestSentense(inte[1], field, me);
		if(ss1 != null){
			s.addEasySentense(ss1);
		}
/*		if(ss2 != null){
			s.addEasySentense(ss2);
		}
*/		ans.put(s, inte);
		for(Map.Entry<Statement, Integer[]> an: ans.entrySet()){
			return an;
		}
		return null;
	}


	public static boolean isAbleInspect(ArrayList<Names> opponents, Field field, Player me, boolean isRemoveFriendWolf){
		ArrayList<Names> preInspect = new ArrayList<>();
		for(Infomation info: me.info_Easy){
			preInspect.add(info.name);
		}
		for(Names n: opponents){
			if(!preInspect.contains(n) && field.getPlayer(n).isSurvive() && me.getName() != n){
				if(me.getRole() == Roles.wolf && isRemoveFriendWolf && me.friendWolfs.contains(n)){
					continue;
				}else{
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isAbleAttack(ArrayList<Names> opponents, Field field, Player me, boolean isRemoveFriendWolf){
		for(Names n: opponents){
			if(field.getPlayer(n).isSurvive() && me.getName() != n){
				if(me.getRole() == Roles.wolf && isRemoveFriendWolf && me.friendWolfs.contains(n)){
					continue;
				}else{
					return true;
				}
			}
		}
		return false;
	}
	public static boolean isAbleVote(ArrayList<Names> opponents, Field field, Player me, boolean isRemoveFriendWolf){
		return isAbleAttack(opponents, field, me, isRemoveFriendWolf);
	}
	public static boolean isAbleGuard(ArrayList<Names> opponents, Field field, Player me, boolean isRemoveFriendWolf){
		return isAbleAttack(opponents, field, me, isRemoveFriendWolf);
	}
	public static boolean isAbleTalk(ArrayList<Names> opponents, Field field, Player me, boolean isRemoveFriendWolf){
		return isAbleAttack(opponents, field, me, isRemoveFriendWolf);
	}


	/**
	 * 今はエリート選択入れてる
	 * @param data
	 * @return
	 */
	public static int rouletteSelect(double[] data){
		if(data.length == 0){
			System.out.println("LearningDataのrouletteSelect0");
			return 1000;
		}

		if(Math.random() < 0.3){
			return getMaxColumn(data);
		}

		double sum_of_percent = 0;
		for(double p: data){
			sum_of_percent += p;
		}
		if(sum_of_percent == 0){
			System.out.println("LearningDataのrouletteSelect1");
		}
		double t = Math.random() * sum_of_percent;
		for(int i = 0; i < data.length; i++){
			t -= data[i];
			if(t <= 0){
				return i;
			}
		}
		System.out.println("LearningDataのrouletteSelect2");
		return 1000;
	}

	public static int rouletteSelect(double[] data, ArrayList<Integer> remove){
		double[] dataClone = data.clone();
		for(Integer i: remove){
			dataClone[i] = 0.0;
		}
		return rouletteSelect(dataClone);
	}

	public SimpleSentense makeSuspectSentense(int strNumber, Field field, Player me){
		if(strNumber == 0) return null;

		ArrayList<Names> fulfills = new ArrayList<>();
		switch (strNumber) {

		case 0://能力者以外で一番怪しい
			for(Player p: field.getPlayers()){
				if(!field.sub.seers.contains(p.getName()) && !field.sub.mediums.contains(p.getName()) && !field.sub.hunters.contains(p.getName())){
					fulfills.add(p.getName());
				}
			}
			break;
		case 1://怪しい占い師
			fulfills = field.sub.seers;
			break;
		case 2://怪しい霊媒師
			fulfills = field.sub.mediums;
			break;
		case 3://怪しい狩人
			fulfills = field.sub.hunters;
			break;
		default:
			break;
		}

		ArrayList<Names> fulcopy = (ArrayList<Names>) fulfills.clone();
		fulcopy.remove(me.getName());
		for(int i = 0; i < fulcopy.size(); i++){
			if(!field.getPlayer(fulcopy.get(i)).isSurvive()) fulcopy.remove(i);
		}
		HashMap<Names, Double> trustyValues = me.getTrustyValue(field, true, fulcopy);

		Names thePerson = getTheMostSuspectPersonName_roler(trustyValues);
		if(thePerson == null){
			return null;
		}
		return SimpleSentense.suspect(thePerson);
	}

	//占って欲しい人を言う 0言わない　1一番怪しい　2二番目に怪しい非能力　3怪しい占い師　4霊媒師　5狩人
	public SimpleSentense makeRequestSentense(int strNumber, Field field, Player me){
		if(strNumber == 0) return null;

		ArrayList<Names> fulfills = new ArrayList<>();
		switch (strNumber) {
		case 0://能力者以外で一番怪しい
			for(Player p: field.getPlayers()){
				if(!field.sub.seers.contains(p.getName()) && !field.sub.mediums.contains(p.getName()) && !field.sub.hunters.contains(p.getName())){
					fulfills.add(p.getName());
				}
			}
			break;
		case 1://怪しい占い師
			fulfills = field.sub.seers;
			break;
		case 2://怪しい霊媒師
			fulfills = field.sub.mediums;
			break;
		case 3://怪しい狩人
			fulfills = field.sub.hunters;
			break;
		case 4://怪しくないやつ
			for(Player p: field.getPlayers()){
				if(p.getName() != me.getName()){
					fulfills.add(p.getName());
				}
			}

			ArrayList<Names> fulcopy = (ArrayList<Names>) fulfills.clone();
			for(int i = 0; i < fulcopy.size(); i++){
				if(!field.getPlayer(fulcopy.get(i)).isSurvive()) fulcopy.remove(i);
			}
			HashMap<Names, Double> trustyValues = me.getTrustyValue(field, true, fulcopy);
			Names thePerson = getTheMostTrustyPersonName_roler(trustyValues);
			if(thePerson == null || thePerson == me.getName()){
				return null;
			}
			return SimpleSentense.requestInspect(thePerson);
		default:
			break;
		}

		HashMap<Names, Double> trustyValues = me.getTrustyValue(field, true, fulfills);
		Names thePerson = getTheMostSuspectPersonName_roler(trustyValues);
		if(thePerson == null || thePerson == me.getName()){
			return null;
		}
		return SimpleSentense.requestInspect(thePerson);

	}

	//atac, vote, ins ,guard
	public int getAbilityStrNum(Verb v, Field field, Player me){
		if(v == Verb.vote){
			HashMap<Statement, Integer[]> ans = new HashMap<>();
			ArrayList<Integer> removeChoice = new ArrayList<>();

			if(!isAbleVote(field.sub.seers, field, me, true))
				removeChoice.add(1);
			if(!isAbleVote(field.sub.mediums, field, me, true))
				removeChoice.add(2);
			if(!isAbleVote(field.sub.hunters, field, me, true))
				removeChoice.add(3);
			return rouletteSelect(action_voteQ, removeChoice);
		}
		return 0;
	}
	public Names getAbilityOpponent(int strNumber, Verb v, Field field, Player me){
		if(v == Verb.vote){
			ArrayList<Names> fulfills = new ArrayList<>();
			switch (strNumber) {
			case 0://能力者以外で一番怪しい
				for(Player p: field.getPlayers()){
					if(!field.sub.seers.contains(p.getName()) && !field.sub.mediums.contains(p.getName()) && !field.sub.hunters.contains(p.getName())){
						fulfills.add(p.getName());
					}
				}
				break;
			case 100://怪しいと一番言われてた人
				HashMap<Names, Integer> voted = new HashMap<>();
				for(Player p: field.getPlayers()){
					Names n = p.suspect_said;
					if(n == null || !field.getPlayer(n).isSurvive()) continue;
					if(voted.containsKey(n)){
						voted.put(n, voted.get(n) + 1);
					}else{
						voted.put(n, 1);
					}
				}
				return Field.getMaxVoted(voted);
			case 1://怪しい占い師
				fulfills = field.sub.seers;
				break;
			case 2://怪しい霊媒師
				fulfills = field.sub.mediums;
				break;
			case 3://怪しい狩人
				fulfills = field.sub.hunters;
				break;
			default:
				break;
			}
			ArrayList<Names> fulcopy = (ArrayList<Names>) fulfills.clone();
			fulcopy.remove(me.getName());
			for(int i = 0; i < fulcopy.size(); i++){
				if(!field.getPlayer(fulcopy.get(i)).isSurvive()) fulcopy.remove(i);
			}
			HashMap<Names, Double> trustyValues = me.getTrustyValue(field, true, fulcopy);

			Names thePerson = getTheMostSuspectPersonName_roler(trustyValues);
			return thePerson;
		}
		return null;
	}

	public int getFakeStrNum(Roles role, Field field, Player me) {
		return 0;
	}
	public Infomation getFakeInfo(int strNum, Roles role, Player me, Field field) {
		return null;
	}


	public Names getTheMostSuspectPersonName_roler(HashMap<Names, Double> trustys){
		HashMap<Names, Double> newMap = new HashMap<>();
		for(Map.Entry<Names, Double> set: trustys.entrySet()){
			Names n = set.getKey();
			newMap.put(n, set.getValue());
		}
		return getMin(newMap);
	}

	public Names getTheMostTrustyPersonName_roler(HashMap<Names, Double> trustys){
		HashMap<Names, Double> newMap = new HashMap<>();
		for(Map.Entry<Names, Double> set: trustys.entrySet()){
			Names n = set.getKey();
			newMap.put(n, set.getValue());
		}
		return getMax(newMap);
	}

	public static Names getMax(HashMap<Names, Double> map){
		double maxVoted = -100.0;
		double percent = 0.5;
		Names tmpVoted = null;
		for (Map.Entry<Names, Double> e : map.entrySet()) {
			if (e.getValue() > maxVoted) {
				tmpVoted = e.getKey();
				maxVoted = e.getValue();
				percent = 0.5;
			} else if (e.getValue() == maxVoted) {
				if (Math.random() < percent) {
					tmpVoted = e.getKey();
				}
				percent = 1.0 / (1.0 / percent + 1.0);
			}
		}
		return tmpVoted;
	}

	/**
	 * 100を越えないやつ
	 * @param map
	 * @return
	 */
	public static Names getMin(HashMap<Names, Double> map){
		double minVoted = 100.0;
		double percent = 0.5;
		Names tmpVoted = null;
		for (Map.Entry<Names, Double> e : map.entrySet()) {
			if (e.getValue() < minVoted) {
				tmpVoted = e.getKey();
				minVoted = e.getValue();
				percent = 0.5;
			} else if (e.getValue() == minVoted) {
				if (Math.random() < percent) {
					tmpVoted = e.getKey();
				}
				percent = 1.0 / (1.0 / percent + 1.0);
			}
		}
		return tmpVoted;
	}


	/**
	 * 真の勝敗を入れる。村人の勝敗じゃないよ
	 * @param selected
	 * @param isWin
	 */
	public void talkLearn(Integer[] selected, boolean isHumanWin, boolean isHumanSide){
		int susStr = selected[0];
		int reqStr = selected[1];
		talk_suspectQ[susStr] = VSmode.Q_Learning(talk_suspectQ[susStr], isHumanWin, isHumanSide);
		talk_requestQ[reqStr] = VSmode.Q_Learning(talk_requestQ[reqStr], isHumanWin, isHumanSide);
	}

	public void voteLearn(int selected, boolean isHumanWin, boolean isHumanSide){
		action_voteQ[selected] = VSmode.Q_Learning(action_voteQ[selected], isHumanWin, isHumanSide);

	}
	public void actionLearn(int selected, boolean isHumanWin, boolean isHumanSide){
		//学習すべき役職の人はそっちのLDで書いてください
	}

	public void fakeLearn(Integer selected, boolean isHumanWin, boolean isHumanSide, Roles fakeRole){
		//学習すべき役職の人はそっちのLDで書いてください
	}

	public static  String getQvalues(double[] data){
		String ans = "";
		for(int i = 0; i < data.length; i++){
			ans += data[i] + "\t";
		}

		return ans;
	}

	public static String getStrategysEnum(String[] strings){
		String ans = "";
		for(int i = 0; i < strings.length; i++){
			ans += strings[i] + "\t";
		}
		return ans;
	}

	public static String getStrategysEnum_comlex(String[] ss1, String[] ss2){
		String ans = "";
		for(int i = 0; i < ss1.length; i++){
			for(int j = 0; j < ss2.length; j++){
				ans += ss1[i] + "+" + ss2[j] + "\t";
			}
		}
		return ans;
	}
}
