package player;

import platform.*;

public abstract class Strategy {
	Player me;

	public void setPlayer(Player p){
		me = p;
	}

	/**
	 * 話す人がいるターンに話すこと
	 * @return
	 */
	public abstract Statement talk(Field field);

	/**
	 * 他に話す人がいないときに話すこと
	 * @return
	 */
	public abstract Statement talk2(Field field);

	/**
	 * 赤ログで話すこと。狼専用
	 * @return
	 */
	public abstract Statement talkRedLog(Field field);


	/**
	 * 追放するプレイヤーを投票
	 * @return
	 */
	public abstract Names vote(Field field);

	/**
	 * 襲撃するプレイヤーを投票。狼専用
	 * @return
	 */
	public abstract Names eat(Field field);

	/**
	 * 占い師が投票ターンに占う
	 * @return
	 */
	public abstract Names inspect(Field field);

	/**
	 * 狩人が投票ターンに守る
	 * @return
	 */
	public abstract Names guard(Field field);


}
