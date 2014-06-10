package platform;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.ObjectOutputStream.PutField;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import platform.Reinforcement_Learning.*;
import platform.Reinforcement_Learning.Environment.Environment;
import platform.Reinforcement_Learning.Environment.EnvironmentSet;
import platform.Reinforcement_Learning.Environment.EnvironmentSet_lunatic;
import platform.Reinforcement_Learning.Environment.EnvironmentSet_wolf;
import platform.Reinforcement_Learning.Environment.Environment_Simon;
import platform.Reinforcement_Learning.Environment.Environment_freemason;
import platform.Reinforcement_Learning.Environment.Environment_humanside;
import platform.Reinforcement_Learning.Environment.Environment_hunter;
import platform.Reinforcement_Learning.Environment.Environment_lunatic;
import platform.Reinforcement_Learning.Environment.Environment_medium;
import platform.Reinforcement_Learning.Environment.Environment_seer;
import platform.Reinforcement_Learning.Environment.Environment_villager;
import platform.Reinforcement_Learning.Environment.Environment_wolf;
import platform.Reinforcement_Learning.LearningData.LD_connectCondition;
import platform.Reinforcement_Learning.LearningData.LD_suspectCondition;
import platform.Reinforcement_Learning.LearningData.LearningData_percentage;
import player.Strategy;
import player.SamplePlayer;
import static platform.ConstantValues.*;

public class VSmode {
	public static double alpha = 0.001;
	public static double defaultQ_human = 20;
	public static double defaultQ_wolf = 20;

	public final static int NumberOfPlayer = 15;
	public final static int NumberOfWolf = 3;
	public final static int NumberOfHunter = 1;
	public final static int NumberOfSeer = 1;
	public final static int NumberOfMedium = 1;
	public final static int NumberOfFreemason = 0;
	public final static int NumberOfLunatic = 1;
	public final static int NumberOfSimon = 0;
	public final static int NumberOfVillager = NumberOfPlayer - NumberOfFreemason - NumberOfHunter - NumberOfLunatic - NumberOfMedium - NumberOfSeer - NumberOfSimon - NumberOfWolf;
	public static ArrayList<Environment> env = new ArrayList<>();

	public static ArrayList<Boolean> humanWin = new ArrayList<>();
	public static int matchSum = 0;
	public static double humanWinPercentage = 0;
	public static double latest = 1.0000;
	
	public static double elite = 0.0;

	static String fileNameValue = "C:/Users/kengo/Dropbox/東大関連/卒論/卒論データ/Qvalue2.txt";

	static ArrayList<Double> WhiteasHumanQvalue = new ArrayList<>();
	static ArrayList<Double> noAttackQvalue = new ArrayList<>();
	static ArrayList<Double> WhiteasHumanQvalue2 = new ArrayList<>();
	static ArrayList<Double> noAttackQvalue2 = new ArrayList<>();

	public static int[] in = {2,5,6};
//	public static Environment_humanside env_Human = new Environment_humanside();

	public static void main(String[] args) throws IOException{
		for(int i = 0; i < 10000; i++){
			humanWin.add(i%1 == 0? true: false);
		}
		humanWinPercentage = 1;

/*		ArrayList<Strategy> abstractPlayers = new ArrayList<Strategy>();
		for (int i = 0; i < NumberOfPlayer; i++) {
			abstractPlayers.add(new SamplePlayer());
		}
		ArrayList<Names> names = new ArrayList<>();
		ArrayList<Roles> roles = new ArrayList<>();
		for (Names n : Names.values()) {
			names.add(n);
		}
		for (Roles r : Roles.values()) {
			for (int i = 0; i < r.NumberOfTheRoler(); i++) {
				roles.add(r);
				switch (r) {
				case freemason:
					env.add(new Environment_freemason());
					break;
				case hunter:
					env.add(new Environment_hunter());
					break;
					//てきとうに
				case lunatic:
					env.add(new Environment_lunatic(Roles.seer));
					break;
				case medium:
					env.add(new Environment_medium());
					break;
				case seer:
					env.add(new Environment_seer());
					break;
				case Simon:
					env.add(new Environment_Simon());
					break;
				case villager:
					env.add(new Environment_villager());
					break;
				case wolf:
					//適当に
					env.add(new Environment_wolf(Roles.seer));
					break;
				}
			}
		}
*/

		/***
		 * プレイするエージェントを player[16]に入れる
		 * 15人以下の場合はこっちのクラスでNumberOfPlayerの数だけAbstractPlayerを用意すること
		 */


		/*
		 * 占い:　狼見つける　何日目　投票候補　他の占い師
		 * 霊媒:　狼見つける　何日目　投票候補　他の霊媒師　占い候補
		 * 狩人:　襲撃守る　投票候補　他の狩人　占い候補
		 */
		// s = {16,4,2,1}, m = {32,8,4,2,1}, h = {8,4,2,1};
		// s = {2,4,2,2}, m = {2,4,2,2,2}, h = {2,2,2,2};
		//〇　0　〇　〇　　　〇　０　〇　〇　×　　　×　×　×　× 19,38,0
		//〇　3　〇　〇　　　〇　０　×　〇　〇　　　〇　×　〇　× 31,35,10
		
		
		
		
		int[] str0 = {16,0,15}, str1 = {31,34,4}, str2 = {19,38,0};
		int[][] strategys = {str0, str1, str2};

		
		
		for(int i = 2; i < 3; i++){
			EnvironmentSet envSet = new EnvironmentSet(strategys[i][0], strategys[i][1], strategys[i][2]);
			for (; matchSum < 10001 * 10000; matchSum++) {
				if(matchSum % 1000 == 0){
					System.out.println(matchSum + "回目\t" + System.currentTimeMillis());
					//System.out.println(matchSum + "回目");
				}
				if(matchSum == 10000){
					//String filName = "C:/Users/kengo/Desktop/Dropbox/東大関連/卒論/卒論データ/10000回_" + strategys[i][0] + "_" +  strategys[i][1] + "_" + strategys[i][2];
					String fileName = "C:/Users/kengo/Dropbox/東大関連/卒論/卒論データ/エリート210000回_" + strategys[i][0] + "_" +  strategys[i][1] + "_" + strategys[i][2];
					writeDownData(envSet, fileName);
				}else  if(matchSum % 2000000 == 0 && matchSum != 0){
					//String filName = "C:/Users/kengo/DeskTop/Dropbox/東大関連/卒論/卒論データ/" + matchSum + "回_" + strategys[i][0] + "_" +  strategys[i][1] + "_" + strategys[i][2];
					String fileName = "C:/Users/kengo/Dropbox/東大関連/卒論/卒論データ/エリート2" + matchSum + "回_" + strategys[i][0] + "_" +  strategys[i][1] + "_" + strategys[i][2];
					writeDownData(envSet, fileName);
				}
				envSet.selectEnvSetWolfSide();

				Jinro jinro = new Jinro(envSet);
				jinro.start();
				learn(jinro);
				if(matchSum % 1000 == 0){
					WhiteasHumanQvalue.add(envSet.envSet_wolfs[0].attack_fake_LD[1].lds[9][4][0][0][0].act_attackQ[1]);
					noAttackQvalue.add(envSet.envSet_wolfs[0].attack_fake_LD[1].lds[9][4][0][0][0].act_attackQ[0]);
					WhiteasHumanQvalue2.add(envSet.envSet_wolfs[0].attack_fake_LD[1].lds[5][9][0][0][0].act_attackQ[1]);
					noAttackQvalue2.add(envSet.envSet_wolfs[0].attack_fake_LD[1].lds[5][9][0][0][0].act_attackQ[0]);
				}
			}
			matchSum = 0;
		}


/*		EnvironmentSet envSet = new EnvironmentSet(19, 38, 0);
		for (; matchSum < 10000 * 10000; matchSum++) {
			if(matchSum % 500 == 0){
				System.out.println(matchSum + "回目");

			}
			if(matchSum == 10000){
				String filName = "C:/Users/kengo/Desktop/Dropbox/東大関連/卒論/卒論データ/10000_19_38_0" ;
				writeDownData(envSet, filName);
			}else  if(matchSum % 500000 == 0 && matchSum != 0){
				String filName = "C:/Users/kengo/Desktop/Dropbox/東大関連/卒論/卒論データ/" + matchSum + "_19_38_0";
				writeDownData(envSet, filName);
			}
			//EnvironmentSet envSet = env_Human.selectEnvSet();
			envSet.selectEnvSetWolfSide();
			Jinro jinro = new Jinro(envSet);
			//envset変えてる狼と狂人のやつ
//			System.out.println(envSet.LD_suspect.get(Roles.villager).trustseerCO[1].percentage);
			jinro.start();
			learn(jinro);
		}
*/
	}

	public static void learn(Jinro jin){
		//一つ一つLearnメソッドを作って、最終的な勝ち負けを入れると学習してくれる。jin.ishumanwin 的な
		/*
			各Environment 16個
			EnvironmentSet. LD_connect のセット　LD_suspectのセット 役職分
			EnvironmentSet_wolfのLD 1個
			人狼側の戦略の決め方 2つ
			LearningDataのLearnを書いてください
		}*/
		humanWinPercentage -= humanWin.remove(0) == true? 0.0001: 0;

		humanWin.add(jin.isHumanWin());
		humanWinPercentage += jin.isHumanWin() ? 0.0001 : 0;
		if (Math.abs(latest - humanWinPercentage) > 0.02) {
			DecimalFormat df = new DecimalFormat("0.000");
			System.out.println(df.format(humanWinPercentage));
			latest = humanWinPercentage;
		}


		EnvironmentSet envSet = jin.field.sub.env;
		for(Environment env: envSet.env){
			env.Learn(jin.isHumanWin());
		}
		envSet.envSet_lunatic.Learn(jin.isHumanWin());
		for(Environment env: envSet.envSet_wolf.envW){
			env.Learn(jin.isHumanWin());
		}


		Field field = jin.getField();
		ArrayList<Names> villagers = new ArrayList<>();
		boolean villagerLearned = false;
		for(Player p: field.getPlayers()){
			if(p.getRole() == Roles.villager && villagerLearned){
				villagers.add(p.getName());
				continue;
			}
			LD_suspectCondition ld_sus = envSet.LD_suspect.get(p.getRole());
			ld_sus.Learn(field, p);
			if (p.getRole() != Roles.villager) {
				LD_connectCondition ld_con = envSet.LD_connect.get(p.getRole());
				ld_con.Learn(field, p);
			}else{
				villagerLearned = true;
				villagers.add(p.getName());
			}
		}
		LD_connectCondition ld_con = envSet.LD_connect.get(Roles.villager);
		ld_con.villagerLearn(field, villagers);



		envSet.envSet_wolf.Learn(jin.isHumanWin());
		envSet.envSet_lunatic.Learn(jin.isHumanWin());
		
		

	}


	public static double Q_Learning(double preQ, boolean isHumanWin, boolean isHumanSide){
		boolean win = isHumanWin == isHumanSide;
		double r = win? 100.0: 0.0;
		double winPer = isHumanSide? humanWinPercentage: 1 - humanWinPercentage;
//		double alpha2 = win?
		double q = (1 - alpha) * preQ + alpha * r;
		return q;
	}

	public static void writeDownData(EnvironmentSet envSet, String fileName) throws IOException{
		/*
		各Environment 16個　毎回の発言、そして投票等全部のつかさどり
		各環境における最適戦略＋Q値＋平均Q値
		EnvironmentSet. LD_connect のセット　LD_suspectのセット 役職分
		村人　コネクト　自分
		ステータスの種類　状態　値
		・・・
		・・・

		EnvironmentSet_wolfのLD 1個　Fake


		人狼側の戦略の決め方 2つ
		*/
		PrintWriter outValue = new PrintWriter(new BufferedWriter(new FileWriter(fileNameValue, true)));
		for(int i = 0; i < WhiteasHumanQvalue.size(); i++){
			outValue.println(WhiteasHumanQvalue.get(i) + "\t" + noAttackQvalue.get(i) + "\t" + WhiteasHumanQvalue2.get(i) + "\t" + noAttackQvalue2.get(i));
		}
		outValue.close();
		WhiteasHumanQvalue.clear();
		WhiteasHumanQvalue2.clear();
		noAttackQvalue.clear();
		noAttackQvalue2.clear();
		
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fileName + ".txt")));
		PrintWriter out_wolf = new PrintWriter(new BufferedWriter(new FileWriter(fileName + "_for_wolf" + ".txt")));
		PrintWriter out_wolfAbility = new PrintWriter(new BufferedWriter(new FileWriter(fileName + "_for_wolf_ability" + ".txt")));
		PrintWriter out_sus_connect = new PrintWriter(new BufferedWriter(new FileWriter(fileName + "_connect_suspect" + ".txt")));

		out.println("人間側勝率 = " + humanWinPercentage + "\n\n");
		for(Environment env: envSet.env){
			if(env.role == Roles.freemason || env.role == Roles.Simon){
				continue;
			}
			env.printLDs(out);
			out.println();
		}
		EnvironmentSet_lunatic[] envSetLuna = envSet.envSet_lunatics;
		for(int i = 0; i < envSetLuna.length; i++){
			if(i == 3){
				continue;
			}
			String fake = i== 0? "村人": i == 1? "占い師": i == 2? "霊媒師": "狩人";
			out.println(fake);
			envSetLuna[i].envL.printLDs(out);
			out.println();
		}

		EnvironmentSet_wolf[] envSetWolf = envSet.envSet_wolfs;
		for(int i = 0; i < envSetWolf.length; i++){
			if(envSetWolf[i].smhs[2] == 1) continue;
			String a = envSetWolf[i].smhs[0] == 1? "占い師":"村人";
			String b = envSetWolf[i].smhs[1] == 1? "霊媒師":"村人";
			String c = envSetWolf[i].smhs[2] == 1? "狩人":"村人";
			out_wolf.println(a + "と" + b  + "と"+ c);
			for(int j = 0; j < envSetWolf[i].envW.length; j++){
				out_wolf.println(j + 1 + "匹目");
				envSetWolf[i].envW[j].printLDs(out_wolf);
				out_wolf.println();
			}
			out_wolfAbility.println(a + "と" + b  + "と"+ c);
			envSetWolf[i].printLD(out_wolfAbility);
		}




		String[] suspectStatus = {"襲撃日","処刑日","占結果","霊結果","占CO","霊媒CO","狩人CO"};
		for(Map.Entry<Roles, LD_suspectCondition> set: envSet.LD_suspect.entrySet()){
			if(set.getKey() == Roles.freemason || set.getKey() == Roles.Simon) continue;
			out_sus_connect.println(set.getKey());
			for(int i = 0; i < set.getValue().lds.length; i++){
				out_sus_connect.print("\t" + suspectStatus[i]);
				for(int j = 0; j < set.getValue().lds[i].length; j++){
					out_sus_connect.print("\t" + set.getValue().lds[i][j].percentage);
				}
				out_sus_connect.println();
			}
			out_sus_connect.println();
		}

		out_sus_connect.println();
/*

		//他人同士のコネクトの学習データ [6][2] の[2]は [0]に-1の学習データ [1]に１の学習データが入る
		public LearningData_percentage[] boolTrust = new LearningData_percentage[6];
		public LearningData_percentage[][] switchTrust = new LearningData_percentage[6][2];

		//<相手, 自分>のコネクトのときに使う学習データ
		public LearningData_percentage[] boolTrust_toMe = new LearningData_percentage[6];
		public LearningData_percentage[][] switchTrust_toMe = new LearningData_percentage[6][2];

*/		String[] connectStatus = {"投票","被投票","疑い","被疑い","占希望","被占希望","CO被り","占黒","占白","被占黒","被占白","霊黒","霊白","被霊黒","被霊白","守失敗","守成功","被守","被守成功"};

		for(Map.Entry<Roles, LD_connectCondition> set: envSet.LD_connect.entrySet()){
			if(set.getKey() == Roles.freemason || set.getKey() == Roles.Simon) continue;
			out_sus_connect.print(set.getKey());
			double[] connectValues = new double[ConnectCondition.resourseBoolNum + ConnectCondition.resourseSwitchNum * 2];
			double[] connectValues_toMe = new double[ConnectCondition.resourseBoolNum + ConnectCondition.resourseSwitchNum * 2];
			for(int i = 0; i < set.getValue().boolTrust.length; i++){
				connectValues[i] = set.getValue().boolTrust[i].percentage;
				connectValues_toMe[i] = set.getValue().boolTrust_toMe[i].percentage;
			}
			for(int i = 0; i < set.getValue().switchTrust.length; i++){
				connectValues[ConnectCondition.resourseBoolNum+i * 2] = set.getValue().switchTrust[i][0].percentage;
				connectValues[ConnectCondition.resourseBoolNum+i * 2 + 1] = set.getValue().switchTrust[i][1].percentage;
				connectValues_toMe[ConnectCondition.resourseBoolNum+i * 2] = set.getValue().switchTrust_toMe[i][0].percentage;
				connectValues_toMe[ConnectCondition.resourseBoolNum+i * 2 + 1] = set.getValue().switchTrust_toMe[i][1].percentage;
			}
			for(int i = 0; i < connectStatus.length; i++){
				out_sus_connect.print("\t" + connectStatus[i]);
			}
			out_sus_connect.println();
			out_sus_connect.print("他人同士");
			for(int i = 0; i < connectValues.length; i++){
				out_sus_connect.print("\t" + connectValues[i]);
			}
			out_sus_connect.println();
			out_sus_connect.print("自分へ");
			for(int i = 0; i < connectValues.length; i++){
				out_sus_connect.print("\t" + connectValues_toMe[i]);
			}
			out_sus_connect.println();

		}

		out_sus_connect.print("\n狼の騙り役職");
		for(int i = 0; i < envSetWolf.length; i++){
			out_sus_connect.print("\t" + envSetWolf[i].LD);
		}
		out_sus_connect.println();
		out_sus_connect.print("\n狂人の騙り役職");
		for(int i = 0; i < envSetLuna.length; i++){
			out_sus_connect.print("\t" + envSetLuna[i].LD);
		}
		out.close();
		out_sus_connect.close();
		out_wolf.close();
	}

}
