package platform;

import platform.Reinforcement_Learning.LearningData.LD_suspectCondition;
import platform.Reinforcement_Learning.LearningData.LearningData_percentage;

/**
 * プレイヤー個人の怪しさの基となる状態を集めたクラス。Fieldクラスに１６個あり（つまり客観的な指標のみ）、
 * 各プレイヤーがその情報を自分の学習データをもとにどのように用いるか決める。
 * プレイヤーによってはinspectedが以上に怪しいとみるかもしれないし。ってこと。
 * ただ、学習させるタイミングがどうしても１ゲーム終了時になるため、途中の状態を反映できない。
 * 例えば、途中まで単独占い師状態だったけど、最後の日に違う占い師が出てきたとき、それは２人占い師として見られる。
 * Fieldクラスに作ったけど、占い師、霊媒師に関しては自分だけが知っている結果がありうるため、独自に学習していかなければならない
 * 占いのInfoを疑い度を出すときにNullでなければ使うとか、学習するときにまだ残っていたら用いるとか、発表したらInfoを空にするとか。
 * 扱うときは線形的に足すのではなく、秀でたところは秀でてほしい。基本は1/4で
 * ２５％以下のものはn/25として全部かけて、25%以上のものは100-n/75として全部かける。
 * マイナスの数値の逆数：プラスの数値の逆数＝１：４とかなら、25＋75*(1-1/√4)とか？
 * @author kengo
 *
 */
public class SuspectCondition {
	public static final int attacked = 0;//n日目に食べられた
	public static final int executed = 1;//ｎ日目に吊るされた
	public static final int inspected = 2;//-3~3 黒の人数～白の人数 4パンダ
	public static final int telled = 3;
	public static final int seerCO = 4;//123でCOした人数
	public static final int mediumCO = 5;
	public static final int hunterCO = 6;
	public int[] condition = {0,0,0,0,0,0,0};

	public int[] getSusCon(){
		//int[] ans = {attacked, punnished, voteToMe, suspectToMe, inspected, telled, seerCO, mediumCO, hunterCO};
		return condition;
	}

	public double getSuspectDouble(){

		return 0;
	}

	public void setCO_condition(Roles role, int sum){
		int column = 0;
		switch (role) {
		case seer:
			column = seerCO;
			break;
		case medium:
			column = mediumCO;
			break;
		case hunter:
			column = hunterCO;
			break;
			//バグが分かるように
		default:
			column = condition.length;
			break;
		}
		condition[column] = sum;

	}
	public void setAttacked_condition(int day){
		condition[attacked] = day;
	}
	public void setExecuted_condition(int day){
		condition[executed] = day;
	}

	public void setInspected_condition(boolean isHuman){
		//パンダなら変わらず
		if(condition[inspected] == 4){
			return;
		}
		int ishuman = isHuman? 1: -1;
		//逆に占われいてるかどうかを

//		System.out.println(ishuman + "　" + condition[2]);
		if(ishuman * condition[inspected] < 0){
			condition[inspected] = 4;
			return;
		}else{
			condition[inspected] += ishuman;
			if(condition[inspected] < -3){
				condition[inspected] = -3;
			}
			return;
		}
	}

	public void setTelled_condition(boolean isHuman){
		//パンダなら変わらず
				if(condition[telled] == 4){
					return;
				}
				int ishuman = isHuman? 1: -1;
				//逆に占われいてるかどうかを
				if(ishuman * condition[telled] < 0){
					condition[telled] = 4;
					return;
				}else{
					condition[telled] += ishuman;
					if(condition[telled] < -3){
						condition[telled] = -3;
					}
					return;
				}
	}


	private void setSusCon(int column, int value){
		condition[column] = value;
	}

	public double getSupectValue(LD_suspectCondition ld_sus){
//		if(true) return 0;
		double ans = 0;
		for(int i = 0; i < condition.length; i++){
			if(condition[i] != 0){
				double tmp;
				switch (i) {
				case 2:
				case 3:
					tmp = ld_sus.lds[i][condition[i] + 3].percentage;
					break;
				default:
					tmp = ld_sus.lds[i][condition[i]].percentage;
					break;
				}
				ans += tmp > 0.7? (tmp-0.7)*(tmp-0.7)*(tmp-0.7) * 37.04: - (0.7-tmp) * (0.7-tmp) * 2.04;
//				ans += tmp > 0.7? Math.pow(tmp-0.7, 3) * 1000.0/27.0: - Math.pow(0.7-tmp, 2) * 100/49;
			}
		}
		if(ans > 1.0) ans = 1.0;
		else if(ans < -1.0) ans = -1.0;
		return ans;
	}

	public SuspectCondition clone(){
		SuspectCondition clone = new SuspectCondition();
		for(int i = 0; i < condition.length; i++){
			clone.condition[i] = condition[i];
		}
		return clone;
	}
}
