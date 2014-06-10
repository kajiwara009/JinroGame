package platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import platform.Reinforcement_Learning.LearningData.LD_connectCondition;
import platform.Reinforcement_Learning.LearningData.LD_suspectCondition;
import platform.Reinforcement_Learning.LearningData.LearningData_percentage;
import platform.Sentense.Simple.Operator;

public class ConnectCondition {
	//120組存在するうち、3+78=81組つまり３分の２が仲間同士である。
	//占い師など自分のみ知ってるコンディションがある場合は、Playerクラスにその知らない情報だけ書いたリストを作って、
	//わあああーい

	public static final int vote = 0;
	public static final int voted = 1;
	public static final int suspect = 2;
	public static final int suspected = 3;
/*	public static final int vote_will = 2;
	public static final int voted_will = 3;
*/	public static final int request_Inspect = 4;
	public static final int requested_Inspect = 5;
	public static final int comingOutSameRole = 6;

	public static final int inspect = 7;//-1黒 1白
	public static final int inspected = 8;//-1黒 1白
	public static final int tell = 9;
	public static final int telled = 10;
	public static final int guard = 11;//-1守れず 1守れた
	public static final int guarded = 12;

	public static final int resourseNum = 13;
	public static final int resourseBoolNum = 7;
	public static final int resourseSwitchNum = 6;
	int[] cond = new int[resourseNum];

	public HashMap<Names, Integer[]> condition = new HashMap<>();


	public ConnectCondition clone(){
		ConnectCondition clone = new ConnectCondition(this);
		return clone;
	}


	public ConnectCondition(ConnectCondition c){
		for(Map.Entry<Names, Integer[]> set: c.condition.entrySet()){
			Integer[] param = new Integer[set.getValue().length];
			for(int i = 0; i < set.getValue().length; i++){
				param[i] = set.getValue()[i];
			}
			this.condition.put(set.getKey(), param);
		}
	}

	public ConnectCondition(ArrayList<Player> players){
		for(Player p: players){
			Integer[] tmp = new Integer[resourseNum];
			for(int i = 0; i < resourseNum; i++){
				tmp[i] = 0;
			}
			condition.put(p.getName(), tmp);
		}
	}

	public void setConnectCondition(Statement s){

	}

	public void setConnectCondition(Field field){

	}


	public int getSuspected_Num(){
		int sum = 0;
		for(Map.Entry<Names, Integer[]> set: condition.entrySet()){
			if(set.getValue()[suspected] == 1) sum++;
		}
		return sum;
	}

	public int getRequested_Inspect_Num(){
		int sum = 0;
		for(Map.Entry<Names, Integer[]> set: condition.entrySet()){
			if(set.getValue()[requested_Inspect] == 1) sum++;
		}
		return sum;
	}

	private void setConnectCondition(Names name, int column, int value){
		condition.get(name)[column] = value;
	}

	/**
	 * opponentが他の人とのCondition状態。数値化。
	 * 人狼側が騙す時は、LD_Connectの部分をFakeRoleのものに変える。
	 * 狼の真の疑い値を見たければもう一つの方
	 * 人狼の時はFriendとのつながりを１にするっていう手も。狂人見つけるためならあり
	 * このコンディションの対象は自分もありえるけど、使うことは有り得ない
	 * @param ld_con
	 * @param me
	 * @return
	 */
	public HashMap<Names, Double> getConnectValues(Roles role, Player me, Player opponent, Field field){
		LD_connectCondition ld_con = field.sub.env.LD_connect.get(role);
		HashMap<Names, Double> answers = new HashMap<>();
		answers.put(opponent.getName(), 1.0);
		for(Map.Entry<Names, Integer[]> set: condition.entrySet()){

			if(set.getKey() == opponent.getName()) continue;
			LearningData_percentage[] boolPer = null;
			LearningData_percentage[][] switchPer = null;
			if(set.getKey() == me.getName()){
				boolPer = ld_con.boolTrust_toMe;
				switchPer = ld_con.switchTrust_toMe;
			}else{
				boolPer = ld_con.boolTrust;
				switchPer = ld_con.switchTrust;
			}
			double tmp = 0.0;
			for(int i = 0; i < set.getValue().length; i++){
				if(set.getValue()[i] == 0) continue;
				double v = 0.0;
				if(i < resourseBoolNum){
					v = boolPer[i].percentage;
				}else{
					v = switchPer[i-resourseBoolNum][set.getValue()[i] == 1? 1: 0].percentage;
				}
				tmp += v > 0.7? (v-0.7)*(v-0.7)*(v-0.7) * 37.04: - (0.7-v) * (0.7-v) * 2.04;
//				tmp += v > 0.7? Math.pow(v-0.7, 3) * 1000.0/27.0: - Math.pow(0.7-v, 2) * 100/49;
			}
			if(tmp > 1.0) tmp = 1.0;
			else if(tmp < -1.0) tmp = -1.0;
			answers.put(set.getKey(), tmp);
		}
		if(role == Roles.wolf){
			for(Names n: me.friendWolfs){
				answers.put(n, 1.0);
			}
		}
		return answers;
	}

	public double getConnectSuspecValue(LearningData_percentage ld_per, SuspectCondition sus){

		return 0;
	}

}
