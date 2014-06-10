package platform.Reinforcement_Learning.LearningData;

import java.util.HashMap;
import java.util.Map;

import platform.*;

public class LD_suspectCondition {

	public LearningData_percentage[] trustAttacked = new LearningData_percentage[15];
	public LearningData_percentage[] trustExecuted = new LearningData_percentage[15];
	public LearningData_percentage[] trustInspected = new LearningData_percentage[8];// 元数値を３足したもの。３のときは考慮しない
	public LearningData_percentage[] trustTelled = new LearningData_percentage[8];
	public LearningData_percentage[] trustseerCO = new LearningData_percentage[4];
	public LearningData_percentage[] trustmediumCO = new LearningData_percentage[4];
	public LearningData_percentage[] trusthunterCO = new LearningData_percentage[4];

	public LearningData_percentage[][] lds = { trustAttacked, trustExecuted,
			trustInspected, trustTelled, trustseerCO, trustmediumCO,
			trusthunterCO };

	public LD_suspectCondition() {
		for(int i = 0; i < lds.length; i++){
			for(int j = 0; j < lds[i].length; j++){
				lds[i][j] = new LearningData_percentage();
			}
		}
	}

	public void Learn(Field field, Player me) {
		HashMap<Names, Boolean> isHumanSide = new HashMap<>();
		for (Player p : field.getPlayers()) {
			isHumanSide.put(p.getName(), p.getRole().isHumanSide());
		}

		HashMap<Names, SuspectCondition> mySuspects = me.getMySuspection(field);
		for (Map.Entry<Names, SuspectCondition> set : mySuspects.entrySet()) {
			if (set.getKey() == me.getName() && !(me.getRole() == Roles.villager)){
				continue;
			}else if(me.getRole() == Roles.wolf && me.friendWolfs.contains(set.getKey())){
				continue;
			}
			int[] condition = set.getValue().condition;
			for (int i = 0; i < 7; i++) {
				if (condition[i] != 0) {
					switch (i) {
					case 2:
					case 3:
						lds[i][condition[i] + 3].UpdatePercentage(isHumanSide.get(set.getKey()));
						break;
					default:
						lds[i][condition[i]].UpdatePercentage(isHumanSide.get(set.getKey()));
						break;
					}
				}
			}
		}
	}

}