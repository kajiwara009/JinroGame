package platform;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

import platform.Reinforcement_Learning.Environment.EnvironmentSet;

public class SubField {
	public EnvironmentSet env = null;

	public HashMap<Roles, Integer> rolerSum = new HashMap<>();
	public ArrayList<Names> seers = new ArrayList<>();
	public ArrayList<Names> mediums = new ArrayList<>();
	public ArrayList<Names> hunters = new ArrayList<>();

	// column数が日数
	public ArrayList<Names> attacked = new ArrayList<>();
	public ArrayList<Names> executed = new ArrayList<>();

	public ArrayList<HashMap<Names, Names>> wantInspect = new ArrayList<>();

	public HashMap<Names, SuspectCondition> suspectConditions = new HashMap<>();
	public HashMap<Names, ConnectCondition> connectConditions = new HashMap<>();

	public SubField(Field field) {
		rolerSum.put(Roles.seer, 0);
		rolerSum.put(Roles.medium, 0);
		rolerSum.put(Roles.hunter, 0);

		attacked.add(null);
		executed.add(null);
		for (int i = 0; i < VSmode.NumberOfPlayer; i++) {
			wantInspect.add(new HashMap<Names, Names>());
		}
	}

	public void addGIFTsList(Names name, Roles role) {
		switch (role) {
		case seer:
			seers.add(name);
			rolerSum.put(Roles.seer, seers.size());
			break;
		case medium:
			mediums.add(name);
			rolerSum.put(Roles.medium, mediums.size());
			break;
		case hunter:
			hunters.add(name);
			rolerSum.put(Roles.hunter, hunters.size());
			break;
		}
	}

	public ArrayList<Names> getGIFTsList(Roles role) {
		switch (role) {
		case seer:
			return seers;
		case medium:
			return mediums;
		case hunter:
			return hunters;
		default:
			return null;
		}
	}

	public boolean isCameOut(Names name, Roles role) {
		ArrayList<Names> rolersTmp;
		switch (role) {
		case seer:
			rolersTmp = seers;
			break;
		case medium:
			rolersTmp = mediums;
			break;
		case hunter:
			rolersTmp = hunters;
			break;
		default:
			return false;
		}
		if(rolersTmp.contains(name)){
			return true;
		}else{
			return false;
		}
	}

}
