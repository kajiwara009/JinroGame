package platform.Sentense.Simple;

import java.util.ArrayList;

import platform.*;
import platform.Sentense.Sentense;
/**
 * テンプレ発言としてあるもの。
 * 役職CO
 * 占い結果、霊能結果
 * 投票した人
 * 明日投票する人（予定）
 * 明日占う人を宣言
 * ｎ日目に誰を守った
 * 明日守る人を宣言
 *
 *
 * @author kengo
 *
 */
public class TemplateStatement {
	Player player;

	public TemplateStatement(Player p) {
		player = p;
	}

	/**
	 * 役職をカミングアウトする
	 * @return
	 */
	public Sentense comingOut(Roles role) {

		return null;
	}

	// 日にちもつける？
	/**
	 * 占い結果を発表する。引数に日にちを入れるか迷い中。両方作って日にちを省略で今日にするか。
	 * @param inspected
	 * @param isHumanside
	 * @return
	 */
	public Sentense informInspection(Player inspected, boolean isHumanside) {

		return null;
	}

	/**
	 * 霊能結果を発表する。引数に日にちを入れるか迷い中
	 * @param told
	 * @param isHumanside
	 * @return
	 */
	public Sentense informMediumTelling(Player told, boolean isHumanside) {

		return null;
	}

	/**
	 * 今日占って欲しいプレイヤーを挙げる
	 * @param players
	 * @return
	 */
	public Sentense wantInspect(Player player){

		return null;
	}

	/**
	 * 今日誰に投票するつもりか
	 * @param players
	 * @return
	 */
	public Sentense voteWill(Player player){

		return null;
	}


	//今回のシミュレーションでは使わないものたち↓
	/**
	 * ｎ日目に誰に投票したか
	 * @param voted
	 * @param day
	 * @return
	 */
	public Sentense informVote(Player voted, int day){

		return null;
	}

	public Sentense predictVote(Player voted){

		return null;
	}

}
