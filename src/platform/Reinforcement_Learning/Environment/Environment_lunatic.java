package platform.Reinforcement_Learning.Environment;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

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
import platform.Reinforcement_Learning.LearningData.LearningData_lunatic;
import platform.Sentense.Simple.SimpleSentense;
import platform.Sentense.Simple.Verb;

public class Environment_lunatic extends Environment {

	/**
	 * 自分がCOしている ←だけ   自分の役職（村、占、霊、狩）
	 */

	Roles fakeRole;
	LDset ldset[] = new LDset[2];
//	HashMap<LearningData, Integer> fakeInspect_data = new HashMap<>();

	public Environment_lunatic(Roles r) {
		isHumanSide = false;
		role = Roles.lunatic;
		fakeRole = r;
		for (int i = 0; i < ldset.length; i++) {
			ldset[i] = new LDset(role);
		}
	}

	public Roles getFakeRole(){
		return fakeRole;
	}


	public Infomation makeFakeInfo(Field field, Player me){
		LearningData ld = getLD(field, me);
		Verb v = fakeRole == Roles.seer? Verb.inspect: fakeRole == Roles.medium? Verb.tell: fakeRole == Roles.hunter? Verb.guard: null;
		int strNum = ld.getFakeStrNum(fakeRole, field, me);
		data_fake.put(ld, strNum);
		if(v == Verb.inspect || v == Verb.tell || v == Verb.guard){
			Infomation ans = ld.getFakeInfo(strNum, fakeRole, me, field);
			return ans;
		}

		return null;
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

	public LearningData getLD(Field field, Player me) {
		int a = 0;
		SuspectCondition mySC = field.sub.suspectConditions.get(me.getName());
		if (mySC.condition[mySC.hunterCO] != 0 || mySC.condition[mySC.mediumCO] != 0 || mySC.condition[mySC.seerCO] != 0) {
			a = 1;
		}
		return ldset[a].getLD(field, me);
	}

	public void Learn(boolean isHumanWin){
		super.Learn(isHumanWin);
		for(Map.Entry<LearningData, Integer> fakedata: data_fake.entrySet()){
			fakedata.getKey().fakeLearn(fakedata.getValue(), isHumanWin, isHumanSide, fakeRole);
		}
		data_fake.clear();

	}

	public LDset[] getLDset(){
		return ldset;
	}

	public void printLDs(PrintWriter out){
		super.printLDs(out);
		System.out.println("狩人騙りは抜いてる");
		if(fakeRole == Roles.villager || fakeRole == Roles.hunter) return;

		LDset[] ldsets = getLDset();
		String action = fakeRole == Roles.seer? "占い": fakeRole == Roles.medium? "霊媒": fakeRole==Roles.hunter?"守り" : "おかしい";
		out.print(action);
		String strategys = "";
		if(fakeRole == Roles.seer){
			strategys = LearningData.getStrategysEnum_comlex(LearningData_lunatic.fakeInspectStrategys_opponent, LearningData_lunatic.white_or_black);
		}else if(fakeRole == Roles.medium){
			strategys = LearningData.getStrategysEnum(LearningData_lunatic.white_or_black);
		}
		out.println("\t" + "日数"+ "\t" + "生き残り人数" + "\t" + "占いCO人数" + "\t" + "霊媒CO人数" + "\t" + "狩人CO人数" + "\t"
		+ "CO前後" + "最適戦略" + "\t" + "最適Q値" + "\t" + "平均Q値" + "\t\t" + strategys);
		LearningData[][][][][] lds_preCO = ldsets[0].lds;
		LearningData[][][][][] lds_postCO = ldsets[1].lds;
		for(int i = 0; i < lds_preCO.length; i++){
			for(int j = 0; j < lds_preCO[0].length; j++){
				for(int k = 0; k < lds_preCO[0][0].length; k++){
					for(int l = 0; l < lds_preCO[0][0][0].length; l++){
						for(int m = 0; m < lds_preCO[0][0][0][0].length; m++){
							LearningData ld = lds_preCO[i][j][k][l][m];
							LearningData ld2 = lds_postCO[i][j][k][l][m];

							switch (fakeRole) {
							case seer:
								if(ld != null && ld.selectedSum > VSmode.matchSum / 1000){
									if(ld.getMaxValue(ld.fake_inspectQ) == VSmode.defaultQ_wolf) continue;
									out.println("\t" + i + "\t" + j + "\t" + k + "\t" + l + "\t" + m + "\t" + "CO前" + "\t"
											+ ld.getFakeInspectOptimalStrategy() + "\t" + ld.getMaxValue(ld.fake_inspectQ)
													+ "\t" + ld.getAvarageValue(ld.fake_inspectQ) + "\t\t" + LearningData.getQvalues(ld.fake_inspectQ));
								}
								if(ld2 != null && ld2.selectedSum > VSmode.matchSum / 1000){
									if(ld2.getMaxValue(ld2.fake_inspectQ) == VSmode.defaultQ_wolf) continue;
									out.println("\t" + i + "\t" + j + "\t" + k + "\t" + l + "\t" + m + "\t" + "CO後" + "\t"
											+ ld2.getFakeInspectOptimalStrategy() + "\t" + ld2.getMaxValue(ld2.fake_inspectQ)
													+ "\t" + ld2.getAvarageValue(ld2.fake_inspectQ) + "\t\t" + LearningData.getQvalues(ld2.fake_inspectQ));
								}
								break;
							case medium:
								if(ld != null && ld.selectedSum > VSmode.matchSum / 1000){
									if(ld.getMaxValue(ld.fake_mediumQ) == VSmode.defaultQ_wolf) continue;
									out.println("\t" + i + "\t" + j + "\t" + k + "\t" + l + "\t" + m + "\t" + "CO前" + "\t"
											+ ld.getFakeMediumOptimalStrategy() + "\t" + ld.getMaxValue(ld.fake_mediumQ)
													+ "\t" + ld.getAvarageValue(ld.fake_mediumQ) + "\t\t" + LearningData.getQvalues(ld.fake_mediumQ));
								}
								if(ld != null && ld2.selectedSum > VSmode.matchSum / 1000){
									if(ld2.getMaxValue(ld2.fake_mediumQ) == VSmode.defaultQ_wolf) continue;
									out.println("\t" + i + "\t" + j + "\t" + k + "\t" + l + "\t" + m + "\t" + "CO後" + "\t"
											+ ld2.getFakeMediumOptimalStrategy() + "\t" + ld2.getMaxValue(ld2.fake_mediumQ)
													+ "\t" + ld2.getAvarageValue(ld2.fake_mediumQ) + "\t\t" + LearningData.getQvalues(ld2.fake_mediumQ));
								}
								break;
							case hunter:

							default:
								break;
							}




						}
					}
				}
			}
		}

	}

}
