package platform.Reinforcement_Learning.Environment;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import platform.*;
import platform.Reinforcement_Learning.LearningData.LDset;
import platform.Reinforcement_Learning.LearningData.LearningData;
import platform.Reinforcement_Learning.LearningData.LearningData_teamWolf;
import platform.Sentense.Simple.Verb;

public class EnvironmentSet_wolf {

	boolean isHumanSide = false;
	public LDset[] attack_fake_LD = new LDset[4];
	public Environment_wolf[] envW = new Environment_wolf[VSmode.NumberOfWolf];
	public double LD = VSmode.defaultQ_wolf;
	int selectedCount = 0;
	int winCount = 0;
	// 0~7 s=4 m=2 h=1
	static int[] smhCuts = { 2, 2, 2 };
	int smh = 0;
	public int[] smhs = new int[3];



	HashMap<LearningData, Integer> data_attack = new HashMap<>();
	HashMap<LearningData, Integer> data_inspect = new HashMap<>();
	HashMap<LearningData, Integer> data_tell = new HashMap<>();
	HashMap<LearningData, Integer> data_guard = new HashMap<>();

	public Infomation setFakeInfo(Field field, Player me, Roles fakeRole){
		int rest = 0;
		for(Names n: me.friendWolfs){
			if(field.getPlayer(n).isSurvive()) rest++;
		}
		LearningData ld = attack_fake_LD[rest].getLD(field, me);
		int strNumber = ld.getFakeStrNum(fakeRole, field, me);
		switch (fakeRole) {
		case seer:
			data_inspect.put(ld, strNumber);
			break;
		case medium:
			data_tell.put(ld, strNumber);
			break;
		case hunter:
			data_guard.put(ld, strNumber);
			break;
		}
		return ld.getFakeInfo(strNumber, fakeRole, me, field);
	}

	public Names selectAttackPlayer(Field field, Player me){
		int rest = 0;
		for(Names n: me.friendWolfs){
			if(field.getPlayer(n).isSurvive()) rest++;
		}
		LearningData ld = attack_fake_LD[rest].getLD(field, me);
		
		//ここから手を加える
		for(Map.Entry<LearningData, Integer> set: data_attack.entrySet()){
			double re = ld.getMaxValue(ld.act_attackQ);
			set.getKey().act_attackQ[set.getValue()] = (1 - VSmode.alpha) *  set.getKey().act_attackQ[set.getValue()] + VSmode.alpha * (re * 0.95);
		}
		data_attack.clear();
		
		
		int strNumber = ld.getAbilityStrNum(Verb.attack, field, me);
		data_attack.put(ld, strNumber);
		return ld.getAbilityOpponent(strNumber, Verb.attack, field, me);
	}

	public EnvironmentSet_wolf(int smh_Strategy) {
		smh = smh_Strategy;
		smhs = EnvironmentSet.arrayFitting(smhCuts, smh_Strategy);
		Roles[] r = { smhs[0] == 1 ? Roles.seer : Roles.villager,
				smhs[1] == 1 ? Roles.medium : Roles.villager,
				smhs[2] == 1 ? Roles.hunter : Roles.villager };
		for(int i = 0; i < envW.length; i++){
			envW[i] = new Environment_wolf(r[i]);
		}
		for(int i = 0; i < attack_fake_LD.length; i++){
			attack_fake_LD[i] = new LDset(i);
		}
	}

	public void Learn(boolean isHumanWin) {
		LD = VSmode.Q_Learning(LD, isHumanWin, false);
		selectedCount++;
		if (!isHumanWin){
			winCount++;
		}
		for(Map.Entry<LearningData, Integer> attackdata: data_attack.entrySet()){
			attackdata.getKey().actionLearn(attackdata.getValue(), isHumanWin, isHumanSide);
		}
/*		for(Map.Entry<LearningData, Integer> guarddata: data_guard.entrySet()){
			guarddata.getKey().fakeLearn(guarddata.getValue(), isHumanWin, isHumanSide, Roles.hunter);
		}
*/		for(Map.Entry<LearningData, Integer> inspectdata: data_inspect.entrySet()){
			inspectdata.getKey().fakeLearn(inspectdata.getValue(), isHumanWin, isHumanSide, Roles.seer);
		}
		for(Map.Entry<LearningData, Integer> telldata: data_tell.entrySet()){
			telldata.getKey().fakeLearn(telldata.getValue(), isHumanWin, isHumanSide, Roles.medium);
		}
		data_attack.clear();
		data_inspect.clear();
		data_tell.clear();
	}

	public void printLD(PrintWriter out){
		LDset[] ldsets =attack_fake_LD;

		out.print("襲撃");
		out.println("\t" + "日数"+ "\t" + "生き残り人数" + "\t" + "占いCO人数" + "\t" + "霊媒CO人数" + "\t" + "狩人CO人数" + "\t"
		+ "人狼残り" + "\t" + "最適戦略" + "\t" + "最適Q値" + "\t" + "平均Q値" + "\t\t" + LearningData.getStrategysEnum(LearningData_teamWolf.attackStrategys));
		LearningData[][][][][] lds_preCO = ldsets[0].lds;
		for(int i = 0; i < lds_preCO.length; i++){
			for(int j = 0; j < lds_preCO[0].length; j++){
				for(int k = 0; k < lds_preCO[0][0].length; k++){
					for(int l = 0; l < lds_preCO[0][0][0].length; l++){
						for(int m = 0; m < lds_preCO[0][0][0][0].length; m++){
							for(int o = 0; o < 4; o++){
								LearningData ld = ldsets[o].lds[i][j][k][l][m];
								if(ld != null && ld.selectedSum > VSmode.matchSum / 1000){
									if(ld.getMaxValue(ld.act_attackQ) == VSmode.defaultQ_wolf) continue;
									out.println("\t" + i + "\t" + j + "\t" + k + "\t" + l + "\t" + m + "\t" + o + "\t"
										+ ld.getAttackOptimalStrategy() + "\t" + ld.getMaxValue(ld.act_attackQ)
												+ "\t" + ld.getAvarageValue(ld.act_attackQ) + "\t\t" + ld.getQvalues(ld.act_attackQ));
								}
							}
						}
					}
				}
			}
		}

		if(smhs[0] == 1){
			out.print("偽占い");
			out.println("\t" + "日数"+ "\t" + "生き残り人数" + "\t" + "占いCO人数" + "\t" + "霊媒CO人数" + "\t" + "狩人CO人数" + "\t"
			+ "人狼残り" + "\t" + "最適戦略" + "\t" + "最適Q値" + "\t" + "平均Q値" + "\t\t" +
					LearningData.getStrategysEnum_comlex(LearningData_teamWolf.fakeInspectStrategys_opponent, LearningData_teamWolf.truth_or_lie));
			for(int i = 0; i < lds_preCO.length; i++){
				for(int j = 0; j < lds_preCO[0].length; j++){
					for(int k = 0; k < lds_preCO[0][0].length; k++){
						for(int l = 0; l < lds_preCO[0][0][0].length; l++){
							for(int m = 0; m < lds_preCO[0][0][0][0].length; m++){
								for(int o = 0; o < 4; o++){
									LearningData ld = ldsets[o].lds[i][j][k][l][m];
									if(ld != null && ld.selectedSum > VSmode.matchSum / 1000){
										if(ld.getMaxValue(ld.fake_inspectQ) == VSmode.defaultQ_wolf) continue;
										out.println("\t" + i + "\t" + j + "\t" + k + "\t" + l + "\t" + m + "\t" + o + "\t"
											+ ld.getFakeInspectOptimalStrategy() + "\t" + ld.getMaxValue(ld.fake_inspectQ)
													+ "\t" + ld.getAvarageValue(ld.fake_inspectQ) + "\t\t" + ld.getQvalues(ld.fake_inspectQ));
									}
								}
							}
						}
					}
				}
			}
		}

		if(smhs[1] == 1){
			out.print("偽霊媒");
			out.println("\t" + "日数"+ "\t" + "生き残り人数" + "\t" + "占いCO人数" + "\t" + "霊媒CO人数" + "\t" + "狩人CO人数" + "\t"
			+ "人狼残り" + "\t" + "最適戦略" + "\t" + "最適Q値" + "\t" + "平均Q値" + "\t\t" + LearningData.getStrategysEnum(LearningData_teamWolf.truth_or_lie));
			for(int i = 0; i < lds_preCO.length; i++){
				for(int j = 0; j < lds_preCO[0].length; j++){
					for(int k = 0; k < lds_preCO[0][0].length; k++){
						for(int l = 0; l < lds_preCO[0][0][0].length; l++){
							for(int m = 0; m < lds_preCO[0][0][0][0].length; m++){
								for(int o = 0; o < 4; o++){
									LearningData ld = ldsets[o].lds[i][j][k][l][m];
									if(ld != null && ld.selectedSum > VSmode.matchSum / 1000){
										if(ld.getMaxValue(ld.fake_mediumQ) == VSmode.defaultQ_wolf) continue;
										out.println("\t" + i + "\t" + j + "\t" + k + "\t" + l + "\t" + m + "\t" + o + "\t"
											+ ld.getFakeMediumOptimalStrategy() + "\t" + ld.getMaxValue(ld.fake_mediumQ)
													+ "\t" + ld.getAvarageValue(ld.fake_mediumQ) + "\t\t" + ld.getQvalues(ld.fake_mediumQ));
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
