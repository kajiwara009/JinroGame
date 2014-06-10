package platform.Reinforcement_Learning.LearningData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import platform.*;

public class LD_connectCondition {
	/*
	public static final int vote = 0;
	public static final int voted = 1;
	public static final int suspect = 2;
	public static final int suspected = 3;
	public static final int request_Inspect = 4;
	public static final int requested_Inspect = 5;
	public static final int inspect = 6;//-1黒 1白
	public static final int inspected = 7;//-1黒 1白
	public static final int tell = 8;
	public static final int telled = 9;
	public static final int guard = 10;//-1守れず 1守れた
	public static final int guarded = 11;
*/
	//他人同士のコネクトの学習データ [6][2] の[2]は [0]に-1の学習データ [1]に１の学習データが入る
	public LearningData_percentage[] boolTrust = new LearningData_percentage[ConnectCondition.resourseBoolNum];
	public LearningData_percentage[][] switchTrust = new LearningData_percentage[ConnectCondition.resourseSwitchNum][2];

	//<相手, 自分>のコネクトのときに使う学習データ
	public LearningData_percentage[] boolTrust_toMe = new LearningData_percentage[ConnectCondition.resourseBoolNum];
	public LearningData_percentage[][] switchTrust_toMe = new LearningData_percentage[ConnectCondition.resourseSwitchNum][2];

	public LD_connectCondition(){
		for(int i = 0; i < boolTrust.length; i++){
			boolTrust[i] = new LearningData_percentage();
			boolTrust_toMe[i] = new LearningData_percentage();
		}
		for(int i = 0; i < switchTrust.length; i++){
			for(int j = 0; j < 2; j++){
				switchTrust[i][j] = new LearningData_percentage();
				switchTrust_toMe[i][j] = new LearningData_percentage();
			}
		}
	}


	/**
	 * boolean にはrole.isHumanSide
	 * Player独自のHash <Names, ConnectCondition>を渡して、
	 * 全部まわすんだけど、自分を含むものは飛ばす
	 * それ以外の　condition[12]を渡したら学習してくれるプログラムを書く
	 * 終わったら、＜自分、他のやつ＞を16回学習するが、実際のプレイでは＜他のやつ、自分＞しか使わないので、
	 * すべてを反転したものを学習する。
	 *
	 * @param connect
	 * @param a
	 */
	public void Learn(Field field, Player me){
		HashMap<Names, Boolean> isHumanSide = new HashMap<>();
		for(Player p: field.getPlayers()){
			isHumanSide.put(p.getName(), p.getRole().isHumanSide());
		}

		HashMap<Names, ConnectCondition> myConnects = me.getMyConnection(field);
		for(Map.Entry<Names, ConnectCondition> set_active: myConnects.entrySet()){
			if(set_active.getKey() == me.getName()) continue;
			ConnectCondition c = set_active.getValue();
			for(Map.Entry<Names, Integer[]> set_passive: c.condition.entrySet()){
				if(set_passive.getKey() == set_active.getKey()) continue;
				LearningData_percentage[] learnBool = set_passive.getKey() == me.getName()? boolTrust_toMe: boolTrust;
				LearningData_percentage[][] learnSwitch = set_passive.getKey() == me.getName()? switchTrust_toMe: switchTrust;
				for(int i = 0; i < boolTrust.length; i++){
					if(set_passive.getValue()[i] == 1) learnBool[i].UpdatePercentage(isHumanSide.get(set_active.getKey()) == isHumanSide.get(set_passive.getKey()));
				}
				for(int i = boolTrust.length; i < boolTrust.length + switchTrust.length; i++){
					if(set_passive.getValue()[i] == 1) learnSwitch[i-boolTrust.length][1].UpdatePercentage(isHumanSide.get(set_active.getKey()) == isHumanSide.get(set_passive.getKey()));
					if(set_passive.getValue()[i] == -1) learnSwitch[i-boolTrust.length][0].UpdatePercentage(isHumanSide.get(set_active.getKey()) == isHumanSide.get(set_passive.getKey()));
				}
			}
		}
	}

	public void villagerLearn(Field field, ArrayList<Names> villagers){
		HashMap<Names, Boolean> isHumanSide = new HashMap<>();
		for(Player p: field.getPlayers()){
			isHumanSide.put(p.getName(), p.getRole().isHumanSide());
		}
		HashMap<Names, ConnectCondition> myConnects = field.sub.connectConditions;

		for(Map.Entry<Names, ConnectCondition> set_active: myConnects.entrySet()){
			ConnectCondition c = set_active.getValue();
			for(Map.Entry<Names, Integer[]> set_passive: c.condition.entrySet()){
				if(set_passive.getKey() == set_active.getKey()) continue;
				LearningData_percentage[] learnBool =  boolTrust;
				LearningData_percentage[][] learnSwitch = switchTrust;
				for(int i = 0; i < boolTrust.length; i++){
					if(set_passive.getValue()[i] == 1) learnBool[i].UpdatePercentage(isHumanSide.get(set_active.getKey()) == isHumanSide.get(set_passive.getKey()));
				}
				for(int i = boolTrust.length; i < boolTrust.length + switchTrust.length; i++){

					if(set_passive.getValue()[i] == 1) learnSwitch[i-boolTrust.length][1].UpdatePercentage(isHumanSide.get(set_active.getKey()) == isHumanSide.get(set_passive.getKey()));
					if(set_passive.getValue()[i] == -1) learnSwitch[i-boolTrust.length][0].UpdatePercentage(isHumanSide.get(set_active.getKey()) == isHumanSide.get(set_passive.getKey()));
				}
			}
			if(villagers.contains(set_active.getKey())){
				for(Map.Entry<Names, Integer[]> set: c.condition.entrySet()){
					if(set.getKey() == set_active.getKey()) continue;
					LearningData_percentage[] learnBool =  boolTrust_toMe;
					LearningData_percentage[][] learnSwitch = switchTrust_toMe;
					for(int i = 0; i < boolTrust.length; i++){
						if(set.getValue()[i] == 1) learnBool[i].UpdatePercentage(isHumanSide.get(set_active.getKey()) == isHumanSide.get(set.getKey()));
					}
					for(int i = boolTrust.length; i < boolTrust.length + switchTrust.length; i++){
						if(set.getValue()[i] == 1) learnSwitch[i-boolTrust.length][1].UpdatePercentage(isHumanSide.get(set_active.getKey()) == isHumanSide.get(set.getKey()));
						if(set.getValue()[i] == -1) learnSwitch[i-boolTrust.length][0].UpdatePercentage(isHumanSide.get(set_active.getKey()) == isHumanSide.get(set.getKey()));
					}
				}
			}
		}

	}

}
