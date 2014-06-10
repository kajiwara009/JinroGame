package platform.Reinforcement_Learning.LearningData;

import java.util.ArrayList;
import java.util.HashMap;

import platform.Field;
import platform.Names;
import platform.Player;
import platform.Statement;
import platform.VSmode;
import platform.Sentense.Simple.Verb;

public class LearningData_hunter extends LearningData {

	public static final String[] guardStrategys = {"信頼非能力者","信頼占","信頼霊","占って欲しい人"};

	public String getGuardOptimalStrategy(){
		int column = getMaxColumn(data_Guard);
		return guardStrategys[column];
	}

	public LearningData_hunter(){
		data_Guard = new double[guardStrategys.length];
		for(int i = 0; i < data_Guard.length; i++){
			data_Guard[i] = VSmode.defaultQ_human;
		}
	}

	public int getAbilityStrNum(Verb v, Field field, Player me){
		if(v == Verb.guard){
			HashMap<Statement, Integer[]> ans = new HashMap<>();
			ArrayList<Integer> removeChoice = new ArrayList<>();

			if(!isAbleGuard(field.sub.seers, field, me, true))
				removeChoice.add(1);
			if(!isAbleGuard(field.sub.mediums, field, me, true))
				removeChoice.add(2);
			return rouletteSelect(data_Guard, removeChoice);
		}else{
			return super.getAbilityStrNum(v, field, me);
		}
	}

	public Names getAbilityOpponent(int strNumber, Verb v, Field field, Player me){

		if(v == Verb.vote){
			super.getAbilityOpponent(strNumber, v, field, me);
		}else if(v == Verb.guard){
			ArrayList<Names> fulfills = new ArrayList<>();
			switch (strNumber) {
			case 3://占って欲しいベスト１
				HashMap<Names, Integer> voted = new HashMap<>();
				for(Player p: field.getPlayers()){
					Names n = p.wantInspect_said;
					if(!field.getPlayer(n).isSurvive()) continue;
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
			case 0://能力者以外で一番白い
				for(Player p: field.getPlayers()){
					if(!field.sub.seers.contains(p.getName()) && !field.sub.mediums.contains(p.getName()) && !field.sub.hunters.contains(p.getName())){
						fulfills.add(p.getName());
					}
				}
				break;

			case 1://怪しくない占い師
				fulfills = field.sub.seers;
				break;
			case 2://怪しくない霊媒師
				fulfills = field.sub.mediums;
				break;
			default:
				break;
			}
			ArrayList<Names> fulcopy = (ArrayList<Names>) fulfills.clone();

			fulcopy.remove(me.getName());
			for(int i = 0; i < fulcopy.size(); i++){
				if(!field.getPlayer(fulcopy.get(i)).isSurvive()){
					fulcopy.remove(i);
				}
			}
			HashMap<Names, Double> trustyValues = me.getTrustyValue(field, true, fulcopy);

			Names thePerson = getTheMostTrustyPersonName_roler(trustyValues);
			return thePerson;
		}
		return null;
	}

	public void actionLearn(int selected, boolean isHumanWin, boolean isHumanSide){
		data_Guard[selected] = VSmode.Q_Learning(data_Guard[selected], isHumanWin, isHumanSide);
	}
}
