package platform.Reinforcement_Learning.Environment;

import java.awt.AlphaComposite;
import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;

import platform.*;
import platform.Reinforcement_Learning.LearningData.LDset;
import platform.Reinforcement_Learning.LearningData.LearningData;
import platform.Reinforcement_Learning.LearningData.LearningData_villager;

public class Environment_villager extends Environment{

	LDset ldset = new LDset(Roles.villager);

	public Environment_villager(){
		role = Roles.villager;
	}

	public LearningData getLD(Field field, Player me) {
		return ldset.getLD(field, me);
	}


	public void printLDs(PrintWriter out){
		out.println(role);
		out.println("投票");
		out.println("\t" + "日数"+ "\t" + "生き残り人数" + "\t" + "占いCO人数" + "\t" + "霊媒CO人数" + "\t" + "狩人CO人数" + "\t"
		+ "最適戦略" + "\t" + "最適Q値" + "\t" + "平均Q値" + "\t\t" + LearningData.getStrategysEnum(LearningData.voteStrategys));
		LearningData[][][][][] lds = ldset.lds;
		for(int i = 0; i < lds.length; i++){
			for(int j = 0; j < lds[0].length; j++){
				for(int k = 0; k < lds[0][0].length; k++){
					for(int l = 0; l < lds[0][0][0].length; l++){
						for(int m = 0; m < lds[0][0][0][0].length; m++){
							LearningData ld = lds[i][j][k][l][m];
							if(ld == null) continue;
							if(ld.selectedSum > VSmode.matchSum / 1000){
								out.println("\t" + i + "\t" + j + "\t" + k + "\t" + l + "\t" + m + "\t"
									+ ld.getVoteOptimalStrategy() + "\t" + ld.getMaxValue(ld.action_voteQ)
											+ "\t" + ld.getAvarageValue(ld.action_voteQ) + "\t\t" + ld.getQvalues(ld.action_voteQ));
							}
						}
					}
				}
			}
		}
		out.print("\n発言");
		out.println("\t" + "日数"+ "\t" + "生き残り人数" + "\t" + "占いCO人数" + "\t" + "霊媒CO人数" + "\t" + "狩人CO人数" + "\t"
				+ "最適戦略【怪】" + "\t" + "最適Q値【怪】" + "\t" + "平均Q値【怪】" + "\t"
				+ "最適戦略【占】" + "\t" + "最適Q値【占】" + "\t" + "平均Q値【占】" +
				"\t⇒Suspect\t" + LearningData.getStrategysEnum(LearningData.talkSuspects) +
				"⇒Request\t" + LearningData.getStrategysEnum(LearningData.talkRequests));
		for(int i = 0; i < lds.length; i++){
			for(int j = 0; j < lds[0].length; j++){
				for(int k = 0; k < lds[0][0].length; k++){
					for(int l = 0; l < lds[0][0][0].length; l++){
						for(int m = 0; m < lds[0][0][0][0].length; m++){
							LearningData ld = lds[i][j][k][l][m];
							if(ld != null && ld.selectedSum > VSmode.matchSum / 1000){
								out.println("\t" + i + "\t" + j + "\t" + k + "\t" + l + "\t" + m + "\t"
									+ ld.getTalk_SuspectOptimalStrategy() + "\t" + ld.getMaxValue(ld.talk_suspectQ)	+ "\t" + ld.getAvarageValue(ld.talk_suspectQ) + "\t"
									+ ld.getTalk_RequestOptimalStrategy() + "\t" + ld.getMaxValue(ld.talk_requestQ) + "\t" + ld.getAvarageValue(ld.talk_requestQ) + "\t\t" +
									ld.getQvalues(ld.talk_suspectQ) + "\t"+ ld.getQvalues(ld.talk_requestQ));
							}

						}
					}
				}
			}
		}

	}

}
