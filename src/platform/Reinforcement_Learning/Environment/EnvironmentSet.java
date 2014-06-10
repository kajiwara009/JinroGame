package platform.Reinforcement_Learning.Environment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import platform.ConnectCondition;
import platform.Field;
import platform.Names;
import platform.Player;
import platform.Roles;
import platform.Reinforcement_Learning.LearningData.LD_connectCondition;
import platform.Reinforcement_Learning.LearningData.LD_suspectCondition;
import platform.Reinforcement_Learning.LearningData.LearningData;
import platform.Reinforcement_Learning.LearningData.LearningData_percentage;

public class EnvironmentSet {
	double LD = 0.5;

	public HashMap<Roles, LD_suspectCondition> LD_suspect = new HashMap<>();
	public HashMap<Roles, LD_connectCondition> LD_connect = new HashMap<>();
	/*
	 * 占い:　狼見つける　何日目　投票候補　他の占い師
	 * 霊媒:　狼見つける　何日目　投票候補　他の霊媒師　占い候補
	 * 狩人:　襲撃守る　投票候補　他の狩人　占い候補
	 */
	// s = {2,4,2,2}, m = {2,4,2,2,2}, h = {2,2,2,2};

	int seerCO = 0;
	int[] sarray;
	int mediumCO = 0;
	int[] marray;
	int hunterCO = 0;
	int[] harray;
	public ArrayList<Environment> env = new ArrayList<>();
	public EnvironmentSet_wolf envSet_wolf;
	public EnvironmentSet_wolf[] envSet_wolfs = new EnvironmentSet_wolf[8];

	//村、占、霊、狩
	public EnvironmentSet_lunatic envSet_lunatic;
	public EnvironmentSet_lunatic[] envSet_lunatics = new EnvironmentSet_lunatic[4];

	public EnvironmentSet(int s, int m, int h){
		this();
		seerCO = s;
		mediumCO = m;
		hunterCO = h;
		sarray = arrayFitting(Environment_humanside.s, seerCO);
		marray = arrayFitting(Environment_humanside.m, mediumCO);
		harray = arrayFitting(Environment_humanside.h, hunterCO);

	}

	public EnvironmentSet(){
		for (Roles r : Roles.values()) {
			LD_suspect.put(r, new LD_suspectCondition());
			LD_connect.put(r, new LD_connectCondition());
			for (int i = 0; i < r.NumberOfTheRoler(); i++) {
				switch (r) {
				case freemason:
					env.add(new Environment_freemason());
					break;
				case hunter:
					env.add(new Environment_hunter());
					break;
				case medium:
					env.add(new Environment_medium());
					break;
				case seer:
					env.add(new Environment_seer());
					break;
				case Simon:
					env.add(new Environment_Simon());
					break;
				case villager:
					env.add(new Environment_villager());
					break;
				}
			}
		}

		for(int i = 0; i < envSet_lunatics.length; i++){
			envSet_lunatics[i] = new EnvironmentSet_lunatic(i);
		}

		for(int i = 0; i < envSet_wolfs.length; i++){
			envSet_wolfs[i] = new EnvironmentSet_wolf(i);
		}


	}

	public int[] CO_condition(Roles role){
		int[] ans = null;
		switch (role) {
		case seer:
			ans = sarray;
			break;
		case medium:
			ans = marray;
			break;
		case hunter:
			ans = harray;
			break;
		}
		return ans;
	}

	public static int[] arrayFitting(int[] cuts, int value){
		int[] ans = new int[cuts.length];
		int[] tmpSum = new int[cuts.length];

		tmpSum[cuts.length-1] = 1;
		for(int i = cuts.length-2; i >= 0 ; i--){
			tmpSum[i] = tmpSum[i+1] * cuts[i+1];
		}
		for(int i = 0; i < cuts.length; i++){
			ans[i] = value / tmpSum[i];
			value = value % tmpSum[i];
		}
/*
		tmpSum[0] = 1;
		for(int i = 1; i < cuts.length; i++){
			tmpSum[i] = tmpSum[i-1] * cuts[i-1];
		}
		for(int i = cuts.length - 1; i >= 0; i--){
			ans[i] = value / tmpSum[i];
			value = value % tmpSum[i];
		}
*/		if(value != 0){
			System.out.println("EnvironmentSetnのValueがおかしい" + value);
		}
		return ans;
	}
	/*
	 * 占い:　狼見つける　何日目　投票候補　他の占い師
	 * 霊媒:　狼見つける　何日目　投票候補　他の霊媒師　占い候補
	 * 狩人:　襲撃守る　投票候補　他の狩人　占い候補
	 */
	// s = {2,2,4,2}, m = {2,2,4,2,2}, h = {2,2,2,2};

	public boolean fulfill_COcondition(Field field, Player me, Roles role){
		if(role != Roles.seer && role != Roles.medium && role != Roles.hunter){
			return false;
		}
		boolean isOtherRoler = (field.sub.rolerSum.get(role) == 0)? false: true;
		int day = field.getDay();
		int suspectedNum = field.sub.connectConditions.get(me.getName()).getSuspected_Num();
		int requested_inspect_Num = field.sub.connectConditions.get(me.getName()).getRequested_Inspect_Num();

		switch (role) {
		case seer:
			if(sarray[0] == 0 && sarray[1] == 0 && sarray[2] == 0 && sarray[3] == 0) return false;
			if(sarray[0] == 1 && me.isfindWolf()) return true;
			else if(sarray[1] != 0 && day >= sarray[1]) return true;
			else if(sarray[2] == 1 && suspectedNum >= 4) return true;
			else if(sarray[3] == 1 && isOtherRoler) return true;
			break;
		case medium:
			if(marray[0] == 0 && marray[1] == 0 && marray[2] == 0 && marray[3] == 0 && marray[4] == 0) return false;
			if(marray[0] == 1 && me.isfindWolf()) return true;
			else if(marray[1] != 0 && day >= marray[1]) return true;
			else if(marray[2] == 1 && suspectedNum > 4) return true;
			else if(marray[3] == 1 && isOtherRoler) return true;
			else if(marray[4] == 1 && requested_inspect_Num >= 4) return true;
			break;
		case hunter:
			if(harray[0] == 0 && harray[1] == 0 && harray[2] == 0 && harray[3] == 0) return false;
			if(harray[0] == 1 && me.isGuardAttack()) return true;
			else if(harray[1] == 1 && suspectedNum >= 4) return true;
			else if(harray[2] == 1 && isOtherRoler) return true;
			else if(harray[3] == 1 && requested_inspect_Num >= 4) return true;
			break;
		default:
			break;
		}

		return false;
	}

	public void selectEnvSetWolfSide(){
		//Q学習で選択する
		double sumPercentageLuna = 0.0;
		for(EnvironmentSet_lunatic e: envSet_lunatics){
			if(e.smh_Str == 3) continue;
			sumPercentageLuna += e.LD;
		}
		double rand = Math.random() * sumPercentageLuna;
		for(EnvironmentSet_lunatic e: envSet_lunatics){
			if(e.smh_Str == 3) continue;
			rand -= e.LD;
			if(rand <= 0.0){
				envSet_lunatic = e;
				break;
			}
		}
		double sumPercentageWolf = 0.0;
		for(EnvironmentSet_wolf e: envSet_wolfs){
			if(e.smhs[2] == 1) continue;
			sumPercentageWolf += e.LD;
		}
		double rand2 = Math.random() * sumPercentageWolf;
		for(EnvironmentSet_wolf e: envSet_wolfs){
			if(e.smhs[2] == 1) continue;
			rand2 -= e.LD;
			if(rand2 <= 0.0){
				envSet_wolf = e;
				break;
			}
		}
	}

}
