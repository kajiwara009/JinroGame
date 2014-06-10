package platform.Reinforcement_Learning.LearningData;

import java.util.HashMap;
import java.util.Map;

import platform.*;

public class LDset {
	/**
	 * 日数15日目に必ず終了(11日目以降は同じ)
	 * 襲撃ミス回数(生き残り人数にするとメモリが足りなくなるため)(0,1,2,3,4) やっぱり生き残り人数
	 * 占い師CO人数（生き残り）
	 * 霊媒師CO人数”
	 * 狩人CO人数”
	 * 疑われている（Connectで4以上Suspected）
	 * 自分が占われている（無し　白　黒　パンダ）
	 *
	 * 自分がCOしている
	 *
	 * 自分の役職（村、占、霊、狩）
	 * 生き残り狼人数３
	 */
	//16,17,4,4,4,2,3     11*16*4*4*4*(8~32)のどこかに限界あり（ノーパソ）
	public LearningData[][][][][] lds = new LearningData[11][16][4][4][4];
	//LearningData[][][][][][][] lds = new LearningData[11][16][4][4][4][2][4];
	//static int[] con = {11,16,4,4,4,2,4};
	static int[] con = {11,16,4,4,4, 1, 1};
	public LearningData getLD(Field field, Player me){
		int[] c = new int[7];
		c[0] = Math.min(con[0] - 1, field.getDay());
		c[1] = field.getSurvivorSum();
/*		if(field.getDay() == 1 || field.getDay() == 2) c[1] = 0;
		else{
			c[1] = field.getSurvivorSum() - (VSmode.NumberOfPlayer - (field.getDay()-2) * 2);
			if(c[1] > 4){
				c[1] = 4;
			}
		}
*/		for(Names n: field.sub.seers){
			if(field.getPlayer(n).isSurvive()) c[2]++;
		}
		for(Names n: field.sub.mediums){
			if(field.getPlayer(n).isSurvive()) c[3]++;
		}
		for(Names n: field.sub.hunters){
			if(field.getPlayer(n).isSurvive()) c[4]++;
		}

		int tmpSuspected = 0;
		ConnectCondition s = field.sub.connectConditions.get(me.getName());
		for(Map.Entry<Names, Integer[]> set: s.condition.entrySet()){
			if(set.getValue()[ConnectCondition.suspected] == 1){
				tmpSuspected++;
			}
		}
		if(tmpSuspected >= 4){
			c[5] = 1;
		}

		int inspected = field.sub.suspectConditions.get(me.getName()).condition[SuspectCondition.inspected];
		if(inspected == 0){
			c[6] = 0;
		}else if(inspected < 0){
			c[6] = 2;
		}else{
			if(inspected == 4){
				c[6] = 3;
			}else{
				c[6] = 1;
			}
		}
		lds[c[0]][c[1]][c[2]][c[3]][c[4]].selectedSum++;
		return lds[c[0]][c[1]][c[2]][c[3]][c[4]];
//		return lds[c[0]][c[1]][c[2]][c[3]][c[4]][c[5]][c[6]];
	}


	public LDset(Roles role){
		for(int i0 = 1; i0 < con[0]; i0++){
			int min = i0 == 1? 15: i0 == 2? 15: Math.max(3, 15 - (i0 -2) * 2);
			int max = i0 == 1? 15: i0 == 2? 15: 15 - (i0 - 2);
			for(int i1 = min; i1 <= max; i1++){
				for(int i2 = 0; i2 < con[2]; i2++){
					for(int i3 = 0; i3 < con[3]; i3++){
						for(int i4 = 0; i4 < con[4]; i4++){
							for(int i5 = 0; i5 < con[5]; i5++){
								for(int i6 = 0; i6 < con[6]; i6++){
									/*switch (role) {
									case villager:
										lds[i0][i1][i2][i3][i4][i5][i6] = new LearningData_villager();
										break;
									case freemason:
										lds[i0][i1][i2][i3][i4][i5][i6] = new LearningData_freemason();
										break;
									case hunter:
										lds[i0][i1][i2][i3][i4][i5][i6] = new LearningData_hunter();
										break;
									case lunatic:
										lds[i0][i1][i2][i3][i4][i5][i6] = new LearningData_lunatic();
										break;
									case medium:
										lds[i0][i1][i2][i3][i4][i5][i6] = new LearningData_medium();
										break;
									case seer:
										lds[i0][i1][i2][i3][i4][i5][i6] = new LearningData_seer();
										break;
									case Simon:
										lds[i0][i1][i2][i3][i4][i5][i6] = new LearningData_Simon();
										break;
									case wolf:
										lds[i0][i1][i2][i3][i4][i5][i6] = new LearningData_wolf();
										break;
									}*/
									switch (role) {
									case villager:
										lds[i0][i1][i2][i3][i4] = new LearningData_villager();
										break;
									case freemason:
										lds[i0][i1][i2][i3][i4] = new LearningData_freemason();
										break;
									case hunter:
										lds[i0][i1][i2][i3][i4] = new LearningData_hunter();
										break;
									case lunatic:
										lds[i0][i1][i2][i3][i4] = new LearningData_lunatic();
										break;
									case medium:
										lds[i0][i1][i2][i3][i4] = new LearningData_medium();
										break;
									case seer:
										lds[i0][i1][i2][i3][i4] = new LearningData_seer();
										break;
									case Simon:
										lds[i0][i1][i2][i3][i4] = new LearningData_Simon();
										break;
									case wolf:
										lds[i0][i1][i2][i3][i4] = new LearningData_wolf();
										break;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public LDset(int rest){
		for(int i0 = 1; i0 < con[0]; i0++){
			int min = i0 == 1? 15: i0 == 2? 15: Math.max(3, 15 - (i0 -2) * 2);
			int max = i0 == 1? 15: i0 == 2? 15: 15 - (i0 - 2);
			for(int i1 = min; i1 <= max; i1++){
				for(int i2 = 0; i2 < con[2]; i2++){
					for(int i3 = 0; i3 < con[3]; i3++){
						for(int i4 = 0; i4 < con[4]; i4++){
							lds[i0][i1][i2][i3][i4] = new LearningData_teamWolf(rest);
						}
					}
				}
			}
		}
	}



}
