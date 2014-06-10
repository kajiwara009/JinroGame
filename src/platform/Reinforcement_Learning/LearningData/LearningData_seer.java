package platform.Reinforcement_Learning.LearningData;

import java.util.ArrayList;
import java.util.HashMap;

import platform.Field;
import platform.Infomation;
import platform.Names;
import platform.Player;
import platform.Roles;
import platform.Statement;
import platform.SuspectCondition;
import platform.VSmode;
import platform.Sentense.Simple.Verb;

public class LearningData_seer extends LearningData {

	public static final String[] inspectStrategys = {"怪しい非能力者","怪しい占","怪しい霊","対抗占"};


	public LearningData_seer(){
		data_InspectQ = new double[inspectStrategys.length];
		for(int i = 0; i < data_InspectQ.length; i++){
			data_InspectQ[i] = VSmode.defaultQ_human;
		}
	}

	public String getInspectOptimalStrategy(){
		int column = getMaxColumn(data_InspectQ);
		return inspectStrategys[column];
	}

	public int getAbilityStrNum(Verb v, Field field, Player me){
		if(v == Verb.inspect){
			HashMap<Statement, Integer[]> ans = new HashMap<>();
			ArrayList<Integer> removeChoice = new ArrayList<>();

			if(!isAbleInspect(field.sub.seers, field, me, true))
				removeChoice.add(1);
			if(!isAbleInspect(field.sub.mediums, field, me, true)){
				removeChoice.add(2);
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
				removeChoice.add(3);
			return rouletteSelect(data_InspectQ, removeChoice);
		}else{
			return super.getAbilityStrNum(v, field, me);
		}
	}
	public Names getAbilityOpponent(int strNumber, Verb v, Field field, Player me){
		if(v == Verb.vote){
			super.getAbilityOpponent(strNumber, v, field, me);
		}else if(v == Verb.inspect){
			ArrayList<Names> fulfills = new ArrayList<>();

			ArrayList<Names> inspect_pre = new ArrayList<>();
			for(Infomation info: me.info_Easy){
				inspect_pre.add(info.name);
			}

			switch (strNumber) {
			case 400://占って欲しいベスト１
				HashMap<Names, Integer> voted = new HashMap<>();
				for(Player p: field.getPlayers()){
					Names n = p.wantInspect_said;
					if(!field.getPlayer(n).isSurvive() || inspect_pre.contains(n)) continue;
					if(voted.containsKey(n)){
						voted.put(n, voted.get(n) + 1);
					}else{
						voted.put(n, 1);
					}
				}
				Names ans = Field.getMaxVoted(voted);
				if(ans == me.getName()){
					return null;
				}else{
					return ans;
				}
			case 0://能力者以外で一番怪しい
				for(Player p: field.getPlayers()){
					if(!field.sub.seers.contains(p.getName()) && !field.sub.mediums.contains(p.getName()) && !field.sub.hunters.contains(p.getName())){
						fulfills.add(p.getName());
					}
				}
				break;
			case 100://怪しいと一番言われてた人
				HashMap<Names, Integer> voted2 = new HashMap<>();
				for(Player p: field.getPlayers()){
					Names n = p.suspect_said;
					if(n == null || !field.getPlayer(n).isSurvive()) continue;
					if(voted2.containsKey(n)){
						voted2.put(n, voted2.get(n) + 1);
					}else{
						voted2.put(n, 1);
					}
				}
				return Field.getMaxVoted(voted2);
			case 1://怪しい占い師
				fulfills = field.sub.seers;
				break;
			case 2://怪しい霊媒師
				fulfills = field.sub.mediums;
				break;
			case 3://他の占い師が占った人
				for(Player p: field.getPlayers()){
					if(!(field.sub.suspectConditions.get(p.getName()).condition[SuspectCondition.inspected] == 0) &&
							p.getName() != me.getName() && p.isSurvive() && !me.isExistInfoYet(p.getName())){
						fulfills.add(p.getName());
					}
				}

				break;
			}
			ArrayList<Names> fulcopy = (ArrayList<Names>) fulfills.clone();
			fulcopy.remove(me.getName());
			ArrayList<Names> remove = new ArrayList<>();
			for(Names n: fulcopy){
				if (!field.getPlayer(n).isSurvive() || inspect_pre.contains(n)) {
					remove.add(n);
				}
			}
			for(Names n: remove){
				fulcopy.remove(n);
			}
			HashMap<Names, Double> trustyValues = me.getTrustyValue(field, true, fulcopy);

			Names thePerson = getTheMostSuspectPersonName_roler(trustyValues);
			return thePerson;
		}
		return null;
	}

	public void actionLearn(int selected, boolean isHumanWin, boolean isHumanSide){
		data_InspectQ[selected] = VSmode.Q_Learning(data_InspectQ[selected], isHumanWin, isHumanSide);
	}
}
