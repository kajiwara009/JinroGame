package platform.Reinforcement_Learning.Environment;

import java.util.ArrayList;

import platform.Field;
import platform.Reinforcement_Learning.LearningData.LearningData;

public class Environment_humanside{

	/*
	 * 占い:　狼見つける　何日目　投票候補　他の占い師
	 * 霊媒:　狼見つける　何日目　投票候補　他の霊媒師　占い候補
	 * 狩人:　襲撃守る　投票候補　他の狩人　占い候補
	 */
	ArrayList<Integer> seerCondition = new ArrayList<>();
	ArrayList<Integer> mediumCondition = new ArrayList<>();
	ArrayList<Integer> hunterCondition = new ArrayList<>();

	//EnvironmentSet[][][] sets = new EnvironmentSet[2*2*4*2+1][2*2*4*2*2+1][2*2*2*2+1];
	EnvironmentSet[][][] sets = new EnvironmentSet[2*2*4*2][2*2*4*2*2][2*2*2*2];
	static int[] s = {2,4,2,2}, m = {2,4,2,2,2}, h = {2,2,2,2};


	public Environment_humanside(int sin, int min, int hin){
		sets[sin][min][hin] = new EnvironmentSet(sin, min, hin);
	}

	public Environment_humanside(){
		for(int i = 0; i < sets.length; i++){
			for(int j = 0; j < sets[0].length; j++){
				for(int k = 0; k < sets[0][0].length; k++){
					System.out.println("ここまで");
					sets[i][j][k] = new EnvironmentSet(i, j, k);
				}
			}
		}
/*
		for(EnvironmentSet[][] set1: sets){
			for(EnvironmentSet[] set2: set1){
				for(EnvironmentSet set3: set2){
					set3 = new EnvironmentSet();
				}
			}
		}
*/

	}

	public EnvironmentSet selectEnvSet(){
		//setsのLDの値でルーレット選択をする
		//そのセット内でさらに人狼のEnvsetを選ぶ
		EnvironmentSet e = new EnvironmentSet();
		e.selectEnvSetWolfSide();

		return e;
	}



}
