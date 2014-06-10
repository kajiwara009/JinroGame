package platform.Reinforcement_Learning.Environment;

import platform.Roles;
import platform.VSmode;

public class EnvironmentSet_lunatic {
	public Environment_lunatic envL;
	public double LD = VSmode.defaultQ_wolf;
	int selectedCount = 0;
	int winCount = 0;

	int smh_Str = 0;//0～3

	public EnvironmentSet_lunatic(int str){
		smh_Str = str;
		Roles r = null;
		switch (str) {
		case 0:
			r = Roles.villager;
			break;
		case 1:
			r = Roles.seer;
			break;
		case 2:
			r = Roles.medium;
			break;
		case 3:
			r = Roles.hunter;
			break;
		}
		envL = new Environment_lunatic(r);
	}

	public void Learn(boolean isHumanwin){
		LD = VSmode.Q_Learning(LD, isHumanwin, false);
		selectedCount++;
		if(!isHumanwin) winCount++;
		envL.Learn(isHumanwin);
	}

}
