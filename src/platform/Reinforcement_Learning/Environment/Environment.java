package platform.Reinforcement_Learning.Environment;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import platform.*;
import platform.Reinforcement_Learning.LearningData.LDset;
import platform.Reinforcement_Learning.LearningData.LearningData;
import platform.Sentense.Simple.SimpleSentense;
import platform.Sentense.Simple.Verb;

public abstract class Environment {
	public boolean isHumanSide = true;
	public Roles role;
	Player me;

	HashMap<LearningData, Integer> data_action = new HashMap<>();
	HashMap<LearningData, Integer> data_vote = new HashMap<>();
	HashMap<LearningData, Integer[]> data_talk = new HashMap<>();

	HashMap<LearningData, Integer> data_fake = new HashMap<>();

	//LDはgetLDでおｋ。LDでそれぞれどの[]を選んだか、実際のStatement
	public Statement makeStatement(Field field){
		LearningData ld = getLD(field, me);
		Map.Entry<Statement, Integer[]> state_select = ld.makeStatement(field, me);
		data_talk.put(ld, state_select.getValue());
		if(role == Roles.villager || role == Roles.lunatic || role == Roles.wolf){
			return state_select.getKey();
		}
		Statement newState = state_select.getKey();

/*		if(role == Roles.villager || role == Roles.lunatic || role == Roles.wolf){
			return new Statement();
		}
		Statement newState = new Statement();*/
		boolean CameOut = false;
		if(field.sub.isCameOut(me.getName(), role) || (getFakeRole() != null && field.sub.isCameOut(me.getName(), getFakeRole()))){
			CameOut = true;
		}

		if(!CameOut && field.sub.env.fulfill_COcondition(field, me, role)){
			newState.addEasySentense(SimpleSentense.comingOut(role));
		}
		if(CameOut){
			for(Infomation info: me.info_Easy){
				if(!info.isSaid){
					switch (role) {
					case seer:
						newState.addEasySentense(SimpleSentense.inspectResult(info.name, info.bool));
						break;
					case medium:
						newState.addEasySentense(SimpleSentense.mediumResult(info.name, info.bool));
						break;
					case hunter:
						newState.addEasySentense(SimpleSentense.guardResult(info.name, info.bool));
						break;
					}
					info.isSaid = true;
				}
			}
		}
		return newState;
	}

	public Names setAction(Field field, Verb v){
		LearningData ld = getLD(field, me);
		int strNum = ld.getAbilityStrNum(v, field, me);
		Names opponent = ld.getAbilityOpponent(strNum, v, field, me);
		if(v == Verb.vote){
			data_vote.put(ld, strNum);
		}else{
			data_action.put(ld, strNum);
		}
		return opponent;

	}

	public abstract LearningData getLD(Field field, Player me);


	public void setPlayer(Player p){
		me = p;
	}
	/**
	 * 0日数
	 * 1生き残り人数
	 * 2前日の投票結果：1[投票が割れている] 2[自分に集まっている]
	 * 3明日の投票予想：1[みんな申告している] 2[自分に集まっている]
	 * 4明日の占い予想：1[占い師に挙げられている]
	 * 4占い師のCO人数：0～3「３は3人以上」
	 * 5霊媒師のCO人数：同上
	 * 6狩人のCO人数：同上
	 * 7狂人のCO：0,1
	 * 8共有者のCO人数：0～5「5は5人以上」
	 * 9
	 *
	 *使わないメソッド？
	 * @param field
	 * @return
	 */
	public ArrayList<Integer> getAboutInfo(Field field){
		ArrayList<Integer> subField = new ArrayList<>();
		subField.add(field.getDay());
		subField.add(field.getSurvivorSum());
		return subField;
	}

	public Roles getFakeRole(){
		return null;
	}

	public Infomation makeFakeInfo(Field field, Player me){
		return null;
	}

	public void Learn(boolean isHumanWin){
		for(Map.Entry<LearningData, Integer> votedata: data_vote.entrySet()){
			votedata.getKey().voteLearn(votedata.getValue(), isHumanWin, isHumanSide);
		}
		data_vote.clear();
		for(Map.Entry<LearningData, Integer[]> talkdata: data_talk.entrySet()){
			talkdata.getKey().talkLearn(talkdata.getValue(), isHumanWin, isHumanSide);
		}
		data_talk.clear();
		for(Map.Entry<LearningData, Integer> actiondata: data_action.entrySet()){
			actiondata.getKey().actionLearn(actiondata.getValue(), isHumanWin, isHumanSide);
		}
		data_action.clear();

	}

	public LDset[] getLDset(){
		return null;
	}

	public void printLDs(PrintWriter out){
		LDset[] ldsets = getLDset();
		out.println(role);
		out.print("投票");
		out.println("\t" + "日数"+ "\t" + "生き残り人数" + "\t" + "占いCO人数" + "\t" + "霊媒CO人数" + "\t" + "狩人CO人数" + "\t"
		+ "CO前後" + "\t" + "最適戦略" + "\t" + "最適Q値" + "\t" + "平均Q値" + "\t\t" + LearningData.getStrategysEnum(LearningData.voteStrategys));
		LearningData[][][][][] lds_preCO = ldsets[0].lds;
		LearningData[][][][][] lds_postCO = ldsets[1].lds;
		for(int i = 0; i < lds_preCO.length; i++){
			for(int j = 0; j < lds_preCO[0].length; j++){
				for(int k = 0; k < lds_preCO[0][0].length; k++){
					for(int l = 0; l < lds_preCO[0][0][0].length; l++){
						for(int m = 0; m < lds_preCO[0][0][0][0].length; m++){
							LearningData ld = lds_preCO[i][j][k][l][m];
							LearningData ld2 = lds_postCO[i][j][k][l][m];

							if(ld != null && ld.selectedSum > VSmode.matchSum / 1000){
								if(ld.getMaxValue(ld.action_voteQ) == VSmode.defaultQ_human) continue;
								out.println("\t" + i + "\t" + j + "\t" + k + "\t" + l + "\t" + m + "\t" + "CO前" + "\t"
									+ ld.getVoteOptimalStrategy() + "\t" + ld.getMaxValue(ld.action_voteQ)
											+ "\t" + ld.getAvarageValue(ld.action_voteQ) + "\t\t" + ld.getQvalues(ld.action_voteQ));
							}
							if(ld2 != null && ld2.selectedSum > VSmode.matchSum / 1000){
								if(ld2.getMaxValue(ld2.action_voteQ) == VSmode.defaultQ_human) continue;
								out.println("\t" + i + "\t" + j + "\t" + k + "\t" + l + "\t" + m + "\t" + "CO後" + "\t"
									+ ld2.getVoteOptimalStrategy() + "\t" + ld2.getMaxValue(ld2.action_voteQ)
											+ "\t" + ld2.getAvarageValue(ld2.action_voteQ) + "\t\t" + ld.getQvalues(ld.action_voteQ));
							}

						}
					}
				}
			}
		}

		out.print("\n発言");
		out.println("\t" + "日数"+ "\t" + "生き残り人数" + "\t" + "占いCO人数" + "\t" + "霊媒CO人数" + "\t" + "狩人CO人数" + "\t"
				+ "CO前後" + "\t" + "最適戦略【怪】" + "\t" + "最適Q値【怪】" + "\t" + "平均Q値【怪】" + "\t"
				+ "最適戦略【占】" + "\t" + "最適Q値【占】" + "\t" + "平均Q値【占】" +
				"\t⇒Suspect\t" + LearningData.getStrategysEnum(LearningData.talkSuspects) +
				"⇒Request\t" + LearningData.getStrategysEnum(LearningData.talkRequests));
		for(int i = 0; i < lds_preCO.length; i++){
			for(int j = 0; j < lds_preCO[0].length; j++){
				for(int k = 0; k < lds_preCO[0][0].length; k++){
					for(int l = 0; l < lds_preCO[0][0][0].length; l++){
						for(int m = 0; m < lds_preCO[0][0][0][0].length; m++){
							LearningData ld = lds_preCO[i][j][k][l][m];
							LearningData ld2 = lds_postCO[i][j][k][l][m];
							if(ld != null && ld.selectedSum > VSmode.matchSum / 1000){
								if(ld.getMaxValue(ld.talk_suspectQ) == VSmode.defaultQ_human) continue;
								out.println("\t" + i + "\t" + j + "\t" + k + "\t" + l + "\t" + m + "\t" + "CO前" + "\t"
										+ ld.getTalk_SuspectOptimalStrategy() + "\t" + ld.getMaxValue(ld.talk_suspectQ) + "\t" + ld.getAvarageValue(ld.talk_suspectQ) +"\t"
										+ ld.getTalk_RequestOptimalStrategy() + "\t" + ld.getMaxValue(ld.talk_requestQ) + "\t" + ld.getAvarageValue(ld.talk_requestQ) + "\t\t" +
										ld.getQvalues(ld.talk_suspectQ) + "\t"+ ld.getQvalues(ld.talk_requestQ));
							}
							if(ld2 != null && ld2.selectedSum > VSmode.matchSum / 1000){
								if(ld.getMaxValue(ld2.talk_suspectQ) == VSmode.defaultQ_human) continue;
								out.println("\t" + i + "\t" + j + "\t" + k + "\t" + l + "\t" + m + "\t" + "CO後" + "\t"
										+ ld2.getTalk_SuspectOptimalStrategy() + "\t" + ld2.getMaxValue(ld2.talk_suspectQ) + "\t" + ld2.getAvarageValue(ld2.talk_suspectQ) + "\t"
										+ ld2.getTalk_RequestOptimalStrategy() + "\t" + ld2.getMaxValue(ld2.talk_requestQ) + "\t" + ld2.getAvarageValue(ld2.talk_requestQ) + "\t\t" +
										ld2.getQvalues(ld2.talk_suspectQ) + "\t"+ ld2.getQvalues(ld2.talk_requestQ));
							}
						}
					}
				}
			}
		}

	}


}
