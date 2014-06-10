package platform.Reinforcement_Learning.Environment;

import platform.Field;
import platform.Player;
import platform.Roles;
import platform.Reinforcement_Learning.LearningData.LDset;
import platform.Reinforcement_Learning.LearningData.LearningData;

public class Environment_freemason extends Environment{
	public Environment_freemason(){
		role = Roles.freemason;
	}

	LDset ldset = new LDset(Roles.freemason);

	public LearningData getLD(Field field, Player me) {
		return ldset.getLD(field, me);
	}
}
