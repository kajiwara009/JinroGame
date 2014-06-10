package platform.Reinforcement_Learning.Environment;

import java.io.PrintWriter;

import platform.Field;
import platform.Player;
import platform.Roles;
import platform.SuspectCondition;
import platform.VSmode;
import platform.Reinforcement_Learning.LearningData.LDset;
import platform.Reinforcement_Learning.LearningData.LearningData;
import platform.Reinforcement_Learning.LearningData.LearningData_seer;

public class Environment_seer extends Environment{

	/**
	 * 自分がCOしているか
	 */
	LDset ldset[] = new LDset[2];

	public Environment_seer(){
		role = Roles.seer;
		for(int i = 0; i < ldset.length; i++){
			ldset[i] = new LDset(role);
		}
	}

	public LearningData getLD(Field field, Player me) {
		int a = 0;
		SuspectCondition mySC = field.sub.suspectConditions.get(me.getName());
		if(mySC.condition[mySC.seerCO] != 0){
			a = 1;
		}
		return ldset[a].getLD(field, me);
	}

	public LDset[] getLDset(){
		return ldset;
	}

	public void printLDs(PrintWriter out){
		super.printLDs(out);
		LDset[] ldsets = getLDset();
		out.print("占い");
		out.println("\t" + "日数"+ "\t" + "生き残り人数" + "\t" + "占いCO人数" + "\t" + "霊媒CO人数" + "\t" + "狩人CO人数" + "\t"
		+ "CO前後" + "最適戦略" + "\t" + "最適Q値" + "\t" + "平均Q値" + "\t\t" + LearningData.getStrategysEnum(LearningData_seer.inspectStrategys));
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
								if(ld.getMaxValue(ld.data_InspectQ) == VSmode.defaultQ_human) continue;
								out.println("\t" + i + "\t" + j + "\t" + k + "\t" + l + "\t" + m + "\t" + "CO前" + "\t"
										+ ld.getInspectOptimalStrategy() + "\t" + ld.getMaxValue(ld.data_InspectQ)
												+ "\t" + ld.getAvarageValue(ld.data_InspectQ) + "\t\t" + LearningData.getQvalues(ld.data_InspectQ));
							}
							if(ld2 != null && ld2.selectedSum < VSmode.matchSum / 1000){
								if(ld2.getMaxValue(ld2.data_InspectQ) == VSmode.defaultQ_human) continue;
								out.println("\t" + i + "\t" + j + "\t" + k + "\t" + l + "\t" + m + "\t" + "CO後" + "\t"
									+ ld2.getInspectOptimalStrategy() + "\t" + ld2.getMaxValue(ld2.data_InspectQ)
											+ "\t" + ld2.getAvarageValue(ld2.data_InspectQ) + "\t\t" + LearningData.getQvalues(ld.data_InspectQ));
							}

						}
					}
				}
			}
		}

	}
}
