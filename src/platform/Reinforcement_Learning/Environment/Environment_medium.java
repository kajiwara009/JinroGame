package platform.Reinforcement_Learning.Environment;

import platform.Field;
import platform.Player;
import platform.Roles;
import platform.SuspectCondition;
import platform.Reinforcement_Learning.LearningData.LDset;
import platform.Reinforcement_Learning.LearningData.LearningData;

public class Environment_medium extends Environment{

	/**
	 * 自分がCOしているか
	 */
	LDset ldset[] = new LDset[2];

	public Environment_medium(){
		role = Roles.medium;
		for(int i = 0; i < ldset.length; i++){
			ldset[i] = new LDset(role);
		}
	}

	public LearningData getLD(Field field, Player me) {
		int a = 0;
		SuspectCondition mySC = field.sub.suspectConditions.get(me.getName());
		if(mySC.condition[mySC.mediumCO] != 0){
			a = 1;
		}
		return ldset[a].getLD(field, me);
	}

	public LDset[] getLDset(){
		return ldset;
	}
}
