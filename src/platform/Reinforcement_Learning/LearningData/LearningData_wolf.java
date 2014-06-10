package platform.Reinforcement_Learning.LearningData;

import java.util.ArrayList;
import java.util.HashMap;

import platform.*;
import platform.Sentense.Simple.Verb;

public class LearningData_wolf extends LearningData {
	double initNumber = VSmode.defaultQ_wolf;


	public LearningData_wolf() {
		for(int i = 0; i < talk_requestQ.length; i++){
			talk_requestQ[i] = initNumber;
		}
		for(int i = 0; i < talk_suspectQ.length; i++){
			talk_suspectQ[i] = initNumber;
		}
		for(int i = 0; i < action_voteQ.length; i++){
			action_voteQ[i] = initNumber;
		}
	}

/*	public int getAbilityStrNum(platform.Sentense.Simple.Verb v) {
		if(v == Verb.attack){
			System.out.println("今は使われないはず");

			return rouletteSelect(act_attackQ);
		}else{
			return super.getAbilityStrNum(v);
		}
	}

	public Names getAbilityOpponent(int strNumber, Verb v, Field field, Player me) {
		if(v == Verb.attack){
			//0誰も襲わない　1占って欲しいと言われてた人　2一番怪しくない人(fake)　3一番怪しくない人(人狼として)　4一番怪しい人(fake)　5一番怪しい人(人狼として)
			//6占い師CO者　7霊媒CO者　8狩人CO者　
			ArrayList<Names> fulfills = new ArrayList<>();
			switch (strNumber) {
			case 0:
				return null;
			case 1://占って欲しいベスト１
				HashMap<Names, Integer> voted = new HashMap<>();
				for(Player p: field.getPlayers()){
					Names n = p.wantInspect_said;
					if(voted.containsKey(n)){
						voted.put(n, voted.get(n) + 1);
					}else{
						voted.put(n, 1);
					}
				}
				Names ans = Field.getMaxVoted(voted);
				return me.friendWolfs.contains(ans)? null: ans;
			case 2:
			case 3:
			case 4:
			case 5:
				for(Player p : field.getPlayers()){
					fulfills.add(p.getName());
				}
				break;
			case 6://怪しい占い師
				fulfills = field.sub.seers;
				break;
			case 7://怪しい霊媒師
				fulfills = field.sub.mediums;
				break;
			case 8://怪しい狩人
				fulfills = field.sub.hunters;
				break;
			default:
				break;
			}
			for(Names n: me.friendWolfs){
				fulfills.remove(n);
			}
			Names thePerson = null;
			HashMap<Names, Double> trustyValues;
			switch (strNumber) {
			case 6:
			case 7:
			case 8:
				trustyValues = me.getTrustyValue(field, true, fulfills);
				thePerson = getTheMostTrustyPersonName_roler(trustyValues);
				break;
				//　2一番怪しくない人(fake)　3一番怪しくない人(人狼として)　4一番怪しい人(fake)　5一番怪しい人(人狼として)
			case 2:
				trustyValues = me.getTrustyValue(field, true, fulfills);
				thePerson = getTheMostTrustyPersonName_roler(trustyValues);
				break;
			case 3:
				trustyValues = me.getTrustyValue(field, false, fulfills);
				thePerson = getTheMostTrustyPersonName_roler(trustyValues);
				break;
			case 4:
				trustyValues = me.getTrustyValue(field, true, fulfills);
				thePerson = getTheMostSuspectPersonName_roler(trustyValues);
				break;
			case 5:
				trustyValues = me.getTrustyValue(field, false, fulfills);
				thePerson = getTheMostSuspectPersonName_roler(trustyValues);
				break;
			}
			return thePerson;

		}else{
			return super.getAbilityOpponent(strNumber, v, field, me);
		}
	}
*/
}
