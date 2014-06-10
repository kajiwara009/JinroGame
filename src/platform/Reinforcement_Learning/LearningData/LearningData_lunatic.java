package platform.Reinforcement_Learning.LearningData;

import java.util.ArrayList;
import java.util.HashMap;

import platform.Field;
import platform.Infomation;
import platform.Jinro;
import platform.Names;
import platform.Player;
import platform.Roles;
import platform.Statement;
import platform.SuspectCondition;
import platform.VSmode;
import platform.Sentense.Simple.Verb;

public class LearningData_lunatic extends LearningData {
	double initNumber = VSmode.defaultQ_wolf;
	//oなら白１なら黒

	public static final String[] fakeInspectStrategys_opponent = {"怪しい非能力者(asWolf)","白い非能力者(asWolf)",
		"白い占い師(asWolf)","黒い占い師(asWolf)","白い霊(asWolf)","黒い霊(asWolf)","昨日吊られた", "昨日襲われた","対抗占"};
	public static final String[] white_or_black = {"白","黒"};

	public LearningData_lunatic() {
		fake_inspectQ = new double[fakeInspectStrategys_opponent.length * 2];
		fake_mediumQ = new double[white_or_black.length];
		fake_defenceQ = new double[0];
		for (int i = 0; i < fake_inspectQ.length; i++) {
			fake_inspectQ[i] = initNumber;
		}
		for (int i = 0; i < fake_mediumQ.length; i++) {
			fake_mediumQ[i] = initNumber;
		}
		for (int i = 0; i < fake_defenceQ.length; i++) {
			fake_defenceQ[i] = initNumber;
		}
		for (int i = 0; i < talk_requestQ.length; i++) {
			talk_requestQ[i] = initNumber;
		}
		for (int i = 0; i < talk_suspectQ.length; i++) {
			talk_suspectQ[i] = initNumber;
		}
		for(int i = 0; i < action_voteQ.length; i++){
			action_voteQ[i] = initNumber;
		}
	}

	public String getFakeInspectOptimalStrategy(){
		int column = getMaxColumn(fake_inspectQ);

		return fakeInspectStrategys_opponent[column%fakeInspectStrategys_opponent.length] + "+" + white_or_black[column/fakeInspectStrategys_opponent.length];
	}

	public String getFakeMediumOptimalStrategy(){
		int column = getMaxColumn(fake_mediumQ);
		return white_or_black[column];
	}


	public int getFakeStrNum(Roles role, Field field, Player me) {
		switch (role) {
		case seer:
			HashMap<Statement, Integer[]> ans = new HashMap<>();
			ArrayList<Integer> removeChoice = new ArrayList<>();

			if(!isAbleInspect(field.sub.seers, field, me, true)){
				removeChoice.add(2);
				removeChoice.add(3);
			}
			if(isAbleInspect(field.sub.mediums, field, me, true)){
				removeChoice.add(4);
				removeChoice.add(5);
			}
			if(field.sub.attacked.get(field.getDay()-1) == null)
				removeChoice.add(7);
			boolean remove3 = true;
			for(Player p: field.getPlayers()){
				if(!(field.sub.suspectConditions.get(p.getName()).condition[SuspectCondition.inspected] == 0) &&
						p.getName() != me.getName() && p.isSurvive() && !me.isExistInfoYet(p.getName())){
					remove3 = false;
					break;
				}
			}
			if(remove3)
				removeChoice.add(8);
			return rouletteSelect(fake_inspectQ, removeChoice);
		case medium:
			return rouletteSelect(fake_mediumQ);
		case hunter:
			return rouletteSelect(fake_defenceQ);
		default:
			return 1000;
		}
	}

	public Infomation getFakeInfo(int strNum, Roles role, Player me, Field field) {
		switch (role) {
		case seer:
			return getFakeInspect(strNum, field, me);
		case medium:
			return getFakeTell(strNum, field, me);
		case hunter:
			return getFakeGuard(strNum, field, me);
		default:
			break;
		}
		return null;
	}

	public Infomation getFakeTell(int strNum, Field field, Player me){
		if(field.getDay() <= 2) return null;
		boolean isTrueResult = strNum == 0? true: false;
		Names opponent = field.sub.executed.get(field.getDay() - 1);
		Infomation info = new Infomation(opponent, isTrueResult);
		return info;
	}

	public Infomation getFakeGuard(int strNum, Field field, Player me){
		return null;
	}

	public Infomation getFakeInspect(int strNumber, Field field, Player me) {
		ArrayList<Names> fulfills = new ArrayList<>();
		boolean asHuman = true;
		boolean suspect = true;
		Names opponent = null;
		int strChange = strNumber%13;
		boolean result_isHuman = strNumber < 13? true: false;

		ArrayList<Names> inspect_pre = new ArrayList<>();
		for(Infomation info: me.info_Easy){
			inspect_pre.add(info.name);
		}

		switch (strChange) {
		case 0:
			suspect = false;
		case 1://能力者以外
			asHuman = false;
			for (Player p : field.getPlayers()) {
				if (!field.sub.seers.contains(p.getName())
						&& !field.sub.mediums.contains(p.getName())
						&& !field.sub.hunters.contains(p.getName())) {
					fulfills.add(p.getName());
				}
			}
			break;
		case 2://白い
			suspect = false;
		case 3:// 黒い占い師
			fulfills = field.sub.seers;
			asHuman = false;
			break;
		case 4://白い
			suspect = false;
		case 5:// 黒い霊媒師
			fulfills = field.sub.mediums;
			asHuman = false;
			break;
		case 6:// 昨日吊られた
			opponent = field.sub.executed.get(field.getDay()-1);
			break;
		case 7://昨日襲撃された
			opponent = field.sub.attacked.get(field.getDay()-1);
			break;
		case 8://他の占い師が占った人
			for(Player p: field.getPlayers()){
				if(!(field.sub.suspectConditions.get(p.getName()).condition[SuspectCondition.inspected] == 0) &&
						p.getName() != me.getName() && p.isSurvive() && !me.isExistInfoYet(p.getName())){
					fulfills.add(p.getName());
				}
			}
			break;
		default:
			break;
		}
		ArrayList<Names> fulcopy = (ArrayList<Names>) fulfills.clone();

		ArrayList<Names> remove = new ArrayList<>();
		for(Names n: fulcopy){
			if (!field.getPlayer(n).isSurvive() || inspect_pre.contains(n) || me.friendWolfs.contains(n)) {
				remove.add(n);
			}
		}
		for(Names n: remove){
			fulcopy.remove(n);
		}
		HashMap<Names, Double> trustyValues = me.getTrustyValue(field, asHuman,
				fulcopy);
		if(suspect){
			opponent = getTheMostSuspectPersonName_roler(trustyValues);
		}else{
			opponent = getTheMostTrustyPersonName_roler(trustyValues);
		}
		if(opponent == null){
			opponent = randomInspect(field, me);
		}

		return new Infomation(opponent, result_isHuman);
	}

	public Names randomInspect(Field field, Player me){
		ArrayList<Integer> array = Jinro.randomSequence(field.getPlayers()
				.size());
		ArrayList<Names> inspect_pre = new ArrayList<>();
		for(Infomation info: me.info_Easy){
			inspect_pre.add(info.name);
		}

		for (Integer i : array) {
			Player p = field.getPlayers().get(i);
			if (p.getName() != me.getName() && !inspect_pre.contains(p.getName())){
				return p.getName();
			}
		}
		return me.getName();
	}

	public void fakeLearn(Integer selected, boolean isHumanWin, boolean isHumanSide, Roles fakeRole){
		double[] learn = null;
		switch (fakeRole) {
		case seer:
			learn = fake_inspectQ;
			break;
		case medium:
			learn = fake_mediumQ;
			break;
		case hunter:
			learn = fake_defenceQ;
		default:
			break;
		}
		learn[selected] = VSmode.Q_Learning(learn[selected], isHumanWin, isHumanSide);
	}



}
