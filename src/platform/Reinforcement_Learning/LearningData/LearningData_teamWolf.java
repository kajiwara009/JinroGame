package platform.Reinforcement_Learning.LearningData;

import java.util.ArrayList;
import java.util.HashMap;

import platform.*;
import platform.Sentense.Simple.Verb;

public class LearningData_teamWolf extends LearningData {
	double initNumber = VSmode.defaultQ_wolf;


	int restOfWolf;

	public static final String[] fakeInspectStrategys_opponent = 
		{"怪しい非能力者(asHuman)","白い占い師(asWolf)","白い霊(asWolf)","昨日吊られた", "昨日襲われた","人狼","対抗占"};
	public static final String[] truth_or_lie = {"本当","嘘"};

	/**
	 * 一番白い(asHuman)は「ほら、俺の味方が襲われたから俺人間っぽいでしょ！」アピール
	 * asWolfの方は単に邪魔なやつを消したい。
	 */
	public static final String[] attackStrategys = {"襲わず","白い(asHuman)","白い(asWolf)","占い師","霊媒師","狩人"};
	public String getFakeInspectOptimalStrategy(){
		int column = getMaxColumn(fake_inspectQ);
		return fakeInspectStrategys_opponent[column % fakeInspectStrategys_opponent.length] + "+" + truth_or_lie[column / fakeInspectStrategys_opponent.length];
	}


	public String getAttackOptimalStrategy(){
		int column = getMaxColumn(act_attackQ);
		return attackStrategys[column];
	}


	public String getFakeMediumOptimalStrategy(){
		int column = getMaxColumn(fake_mediumQ);
		return truth_or_lie[column];
	}


	public LearningData_teamWolf(int rest) {
		act_attackQ = new double[attackStrategys.length];
		// 最初の12こは本当を言う、その後は嘘を言う
		fake_inspectQ = new double[fakeInspectStrategys_opponent.length * 2];
		//0が本当1が嘘
		fake_mediumQ = new double[truth_or_lie.length];
		fake_defenceQ = new double[0];
		for (int i = 0; i < act_attackQ.length; i++) {
			act_attackQ[i] = initNumber;
		}
		for (int i = 0; i < fake_inspectQ.length; i++) {
			fake_inspectQ[i] = initNumber;
		}
		for (int i = 0; i < fake_mediumQ.length; i++) {
			fake_mediumQ[i] = initNumber;
		}
		for (int i = 0; i < fake_defenceQ.length; i++) {
			fake_defenceQ[i] = initNumber;
		}
		restOfWolf = rest;
	}

	public int getAbilityStrNum(Verb v, Field field, Player me){
		if (v == Verb.attack) {
			HashMap<Statement, Integer[]> ans = new HashMap<>();
			ArrayList<Integer> removeChoice = new ArrayList<>();

			if(!isAbleAttack(field.sub.seers, field, me, true))
				removeChoice.add(3);
			if(!isAbleAttack(field.sub.mediums, field, me, true))
				removeChoice.add(4);
			if(!isAbleAttack(field.sub.hunters, field, me, true))
				removeChoice.add(5);
			return rouletteSelect(act_attackQ, removeChoice);
		} else {
			return super.getAbilityStrNum(v, field, me);
		}
	}

	public Names getAbilityOpponent(int strNumber, Verb v, Field field, Player me) {
		if (v == Verb.attack) {
			//1 4 5 消す
			//{"襲わず","一番白い(asHuman)","一番白い(asWolf)","占い師","霊媒師","狩人"};
			ArrayList<Names> fulfills = new ArrayList<>();
			switch (strNumber) {
			case 0:
				return null;
			case 1:
			case 2:
				for (Player p : field.getPlayers()) {
					if(!field.sub.seers.contains(p.getName()) && !field.sub.mediums.contains(p.getName()) && !field.sub.hunters.contains(p.getName())){
						fulfills.add(p.getName());
					}
				}
				break;
			case 3:// 怪しい占い師
				fulfills = field.sub.seers;
				break;
			case 4:// 怪しい霊媒師
				fulfills = field.sub.mediums;
				break;
			case 5:// 怪しい狩人
				fulfills = field.sub.hunters;
				break;
			default:
				break;
			}
			ArrayList<Names> fulcopy = (ArrayList<Names>) fulfills.clone();
			for (Names n : me.friendWolfs) {
				fulcopy.remove(n);
			}
			for (Player p : field.getPlayers()) {
				if (!p.isSurvive()) {
					fulcopy.remove(p.getName());
				}
			}
			Names thePerson = null;
			HashMap<Names, Double> trustyValues = new HashMap<>();
			switch (strNumber) {
			case 3:
			case 4:
			case 5:
				trustyValues = me.getTrustyValue(field, true, fulcopy);
				thePerson = getTheMostTrustyPersonName_roler(trustyValues);
				break;
			// 　2一番怪しくない人(fake)　3一番怪しくない人(人狼として)　4一番怪しい人(fake)　5一番怪しい人(人狼として)
			case 1:
				trustyValues = me.getTrustyValue(field, true, fulcopy);
				thePerson = getTheMostTrustyPersonName_roler(trustyValues);
				break;
			case 2:
				trustyValues = me.getTrustyValue(field, false, fulcopy);
				thePerson = getTheMostTrustyPersonName_roler(trustyValues);
				break;

			}
			if (thePerson != null) {
				return thePerson;
			} else {
				return getRandomAttack(field);
			}

		} else {
			return super.getAbilityOpponent(strNumber, v, field, me);
		}
	}

	public Names getRandomAttack(Field field) {
		ArrayList<Integer> array = Jinro.randomSequence(field.getPlayers()
				.size());
		for (Integer i : array) {
			Player p = field.getPlayers().get(i);
			if (p.isSurvive() && p.getRole() != Roles.wolf) {
				return p.getName();
			}
		}
		return null;
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
			if(!isAbleInspect(field.sub.mediums, field, me, true)){
				removeChoice.add(4);
				removeChoice.add(5);
			}
			if(field.getDay() == 2){
				removeChoice.add(6);
				removeChoice.add(7);
			}
			if(field.sub.attacked.get(field.getDay()-1) == null){
				removeChoice.add(8);
				removeChoice.add(9);
			}
			if(!isAbleInspect(me.friendWolfs, field, me, false)){
				removeChoice.add(10);
				removeChoice.add(11);
			}
			boolean remove3 = true;
			for(Player p: field.getPlayers()){
				if(!(field.sub.suspectConditions.get(p.getName()).condition[SuspectCondition.inspected] == 0) &&
						p.getName() != me.getName() && p.isSurvive() && !me.isExistInfoYet(p.getName())){
					remove3 = false;
					break;
				}
			}
			if(remove3)
				removeChoice.add(6);
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

	/**
	 * strNumber[1]は占い結果が本当かどうか。０なら本当、１なら嘘つく
	 * @param strNumber
	 * @param field
	 * @param me
	 * @return
	 */
	public Infomation getFakeInspect(int strNumber, Field field, Player me) {

		ArrayList<Names> fulfills = new ArrayList<>();
		boolean asHuman = true;
		boolean suspect = true;
		Names opponent = null;
		int strChange = strNumber%12;

		ArrayList<Names> inspect_pre = new ArrayList<>();
		for(Infomation info: me.info_Easy){
			inspect_pre.add(info.name);
		}
		boolean isTrueResult = strNumber < 12? true: false;
		switch (strChange) {
		case 0://能力者以外
			for (Player p : field.getPlayers()) {
				if (!field.sub.seers.contains(p.getName())
						&& !field.sub.mediums.contains(p.getName())
						&& !field.sub.hunters.contains(p.getName())) {
					fulfills.add(p.getName());
				}
			}
			break;
		case 1:// 白い占い師
			fulfills = field.sub.seers;
			asHuman = false;
			suspect = false;
			break;
		case 2:// 白い霊媒師
			fulfills = field.sub.mediums;
			asHuman = false;
			suspect = false;
			break;
		case 3:// 昨日吊られた
			opponent = field.sub.executed.get(field.getDay()-1);
			break;
		case 4://昨日襲撃された
			opponent = field.sub.attacked.get(field.getDay()-1);
			break;
		case 5://人狼
			for(Names n: me.friendWolfs){
				if(n != me.getName() && field.getPlayer(n).isSurvive() && !inspect_pre.contains(n)){
					fulfills.add(n);
				}
			}
			if(fulfills.size() != 0){
				asHuman = true;
				suspect = true;
				HashMap<Names, Double> trustyValues = me.getTrustyValue(field, asHuman,
						fulfills);
				opponent = getTheMostSuspectPersonName_roler(trustyValues);
				return new Infomation(opponent,  field.getPlayer(opponent).getRole().isHuman() == isTrueResult);
			}else{
				break;
			}
		case 6://他の占い師が占った人
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

		return new Infomation(opponent,  field.getPlayer(opponent).getRole().isHuman() == isTrueResult);
	}

	public Names randomInspect(Field field, Player me){
		ArrayList<Integer> array = Jinro.randomSequence(field.getPlayers()
				.size());
		ArrayList<Names> inspect_pre = new ArrayList<>();
		for(Infomation info: me.info_Easy){
			inspect_pre.add(info.name);
		}
		for(Integer i : array) {
			Player p = field.getPlayers().get(i);
			if (p.getName() != me.getName() && !inspect_pre.contains(p.getName())){
				return p.getName();
			}
		}
		return me.getName();
	}

	public Infomation getFakeTell(int strNum, Field field, Player me){
		if(field.getDay() <= 2) return null;
		boolean isTrueResult = strNum == 0? true: false;
		Names opponent = field.sub.executed.get(field.getDay() - 1);
		boolean isHuman = field.getPlayer(opponent).getRole().isHuman();
		Infomation info = new Infomation(opponent, isTrueResult == isHuman);
		return info;
	}

	public Infomation getFakeGuard(int strNum, Field field, Player me){
		return null;
	}

	public void actionLearn(int selected, boolean isHumanWin, boolean isHumanSide){
		act_attackQ[selected] = VSmode.Q_Learning(act_attackQ[selected], isHumanWin, isHumanSide);
		//学習すべき役職の人はそっちのLDで書いてください
	}

	public void fakeLearn(Integer selected, boolean isHumanWin, boolean isHumanSide, Roles fakeRole){
		switch (fakeRole) {
		case seer:
			fake_inspectQ[selected] = VSmode.Q_Learning(fake_inspectQ[selected], isHumanWin, isHumanSide);
			break;
		case medium:
			fake_mediumQ[selected] = VSmode.Q_Learning(fake_mediumQ[selected], isHumanWin, isHumanSide);
			break;
		case hunter:
			fake_defenceQ[selected] = VSmode.Q_Learning(fake_defenceQ[selected], isHumanWin, isHumanSide);
			break;

		default:
			break;
		}
		//学習すべき役職の人はそっちのLDで書いてください
	}

}
