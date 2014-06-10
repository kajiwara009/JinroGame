package platform.Reinforcement_Learning.Environment;

import java.io.PrintWriter;

import platform.Field;
import platform.Infomation;
import platform.Names;
import platform.Player;
import platform.Roles;
import platform.Statement;
import platform.SuspectCondition;
import platform.VSmode;
import platform.Reinforcement_Learning.LearningData.LDset;
import platform.Reinforcement_Learning.LearningData.LearningData;
import platform.Reinforcement_Learning.LearningData.LearningData_teamWolf;
import platform.Sentense.Simple.SimpleSentense;

public class Environment_wolf extends Environment {
	// [][][]がLDを選ぶ基準となる環境。初期に選ぶ戦略はいらない
	/**
	 * 日数15日目に必ず終了 生き残り人数１６ 占い師CO人数（生き残り） 霊媒師CO人数” 狩人CO人数”
	 * 疑われている（Connectで4以上Suspected） 自分が占われている（白　無し　黒）
	 *
	 * 自分がCOしている
	 *
	 * 自分の役職（村、占、霊、狩） 生き残り狼人数３
	 */
	public Roles fakeRole;

	LDset[][] LDsets = new LDset[2][4];
	final int[] con = { 2, 4 };

	public Environment_wolf(Roles r) {
		isHumanSide = false;
		role = Roles.wolf;
		fakeRole = r;
		for (int i = 0; i < con[0]; i++) {
			for (int k = 0; k < con[1]; k++) {
				LDsets[i][k] = new LDset(role);
			}
		}
	}

	public Roles getFakeRole(){
		return fakeRole;
	}


	public Statement makeStatement(Field field) {
		Statement newState = super.makeStatement(field);
		boolean CameOut = false;
		if(field.sub.isCameOut(me.getName(), getFakeRole())){
			CameOut = true;
		}

		if (!CameOut && field.sub.env.fulfill_COcondition(field, me, fakeRole)) {
			newState.addEasySentense(SimpleSentense.comingOut(fakeRole));
			CameOut = true;
		}

		if (CameOut) {
			for (Infomation info : me.info_Easy) {
				if (!info.isSaid) {
					switch (fakeRole) {
					case seer:
						newState.addEasySentense(SimpleSentense.inspectResult(
								info.name, info.bool));
						break;
					case medium:
						newState.addEasySentense(SimpleSentense.mediumResult(
								info.name, info.bool));
						break;
					case hunter:
						newState.addEasySentense(SimpleSentense.guardResult(
								info.name, info.bool));
						break;
					}
					info.isSaid = true;
				}
			}
		}

		return newState;
	}

	@Override
	public LearningData getLD(Field field, Player me) {
		int a = 0, b = 0, c = 0;
		SuspectCondition mySC = field.sub.suspectConditions.get(me.getName());
		if (mySC.condition[mySC.hunterCO] != 0 || mySC.condition[mySC.mediumCO] != 0 || mySC.condition[mySC.seerCO] != 0) {
			a = 1;
		}
		for (Names n : me.friendWolfs) {
			if (field.getPlayer(n).isSurvive()) {
				c++;
			}
		}
		return LDsets[a][c].getLD(field, me);
	}


	public void printLDs(PrintWriter out){
		out.println(role);
		out.print("投票");
		out.println("\t" + "日数"+ "\t" + "生き残り人数" + "\t" + "占いCO人数" + "\t" + "霊媒CO人数" + "\t" + "狩人CO人数" + "\t"
		+ "CO前後" + "\t" + "人狼残り" + "\t" +  "最適戦略" + "\t" + "最適Q値" + "\t" + "平均Q値" + "\t\t" + LearningData.getStrategysEnum(LearningData.voteStrategys));
		LearningData[][][][][] lds_preCO = LDsets[0][0].lds;
		for(int i = 0; i < lds_preCO.length; i++){
			for(int j = 0; j < lds_preCO[0].length; j++){
				for(int k = 0; k < lds_preCO[0][0].length; k++){
					for(int l = 0; l < lds_preCO[0][0][0].length; l++){
						for(int m = 0; m < lds_preCO[0][0][0][0].length; m++){
							for(int n = 0; n < 2; n++){
								for(int o = 0; o < 4; o++){
									LearningData ld = LDsets[n][o].lds[i][j][k][l][m];
									if(ld != null && ld.selectedSum > VSmode.matchSum / 1000){
										String co = n == 0? "NO": "YES";
										out.println("\t" + i + "\t" + j + "\t" + k + "\t" + l + "\t" + m + "\t" + co + "\t" + o + "\t"
											+ ld.getVoteOptimalStrategy() + "\t" + ld.getMaxValue(ld.action_voteQ)
													+ "\t" + ld.getAvarageValue(ld.action_voteQ) + "\t\t" + ld.getQvalues(ld.action_voteQ));
									}
								}
							}
						}
					}
				}
			}
		}

		out.print("\n発言");
		out.println("\t" + "日数"+ "\t" + "生き残り人数" + "\t" + "占いCO人数" + "\t" + "霊媒CO人数" + "\t" + "狩人CO人数" + "\t"
				+ "CO前後" + "\t" + "人狼残り" + "\t" + "最適戦略【怪】" + "\t" + "最適Q値【怪】" + "\t" + "平均Q値【怪】" + "\t"
				+ "最適戦略【占】" + "\t" + "最適Q値【占】" + "\t" + "平均Q値【占】" +
				"\t⇒Suspect\t" + LearningData.getStrategysEnum(LearningData.talkSuspects) +
				"⇒Request\t" + LearningData.getStrategysEnum(LearningData.talkRequests));
		for(int i = 0; i < lds_preCO.length; i++){
			for(int j = 0; j < lds_preCO[0].length; j++){
				for(int k = 0; k < lds_preCO[0][0].length; k++){
					for(int l = 0; l < lds_preCO[0][0][0].length; l++){
						for(int m = 0; m < lds_preCO[0][0][0][0].length; m++){

							for(int n = 0; n < 2; n++){
								for(int o = 0; o < 4; o++){
									LearningData ld = LDsets[n][o].lds[i][j][k][l][m];
									if(ld != null && ld.selectedSum > VSmode.matchSum / 1000){
										String co = n == 0? "NO": "YES";
										out.println("\t" + i + "\t" + j + "\t" + k + "\t" + l + "\t" + m + "\t" + co + "\t" + o + "\t"
												+ ld.getTalk_SuspectOptimalStrategy() + "\t" + ld.getMaxValue(ld.talk_suspectQ) + "\t" + ld.getAvarageValue(ld.talk_suspectQ) + "\t"
												+ ld.getTalk_SuspectOptimalStrategy() + "\t" + ld.getMaxValue(ld.talk_requestQ) + "\t" + ld.getAvarageValue(ld.talk_requestQ) + "\t\t" +
												ld.getQvalues(ld.talk_suspectQ) + "\t"+ ld.getQvalues(ld.talk_requestQ));
									}
								}
							}
						}
					}
				}
			}
		}

	}



}
