package platform.Reinforcement_Learning.Environment;

import platform.Field;
import platform.Player;
import platform.Roles;
import platform.Reinforcement_Learning.LearningData.LDset;
import platform.Reinforcement_Learning.LearningData.LearningData;

public class Environment_Simon extends Environment {

	public Environment_Simon(){
		role = Roles.Simon;
	}
	LDset ldset = new LDset(Roles.Simon);

	public LearningData getLD(Field field, Player me) {
		return ldset.getLD(field, me);
	}

}
