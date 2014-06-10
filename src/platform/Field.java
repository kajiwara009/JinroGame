 package platform;

import java.awt.font.NumericShaper.Range;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import platform.Sentense.Conjunction;
import platform.Sentense.Simple.SimpleSentense;
import platform.Sentense.Simple.Verb;
import player.*;
import static platform.ConstantValues.*;
import static platform.Term.*;

public class Field {
	private int day;
	private ArrayList<Player> players;
	private ArrayList<ArrayList<Statement>> log = new ArrayList<ArrayList<Statement>>();
	private ArrayList<ArrayList<Statement>> redLog = new ArrayList<ArrayList<Statement>>();
	private ArrayList<HashMap<Names, Names>> voted = new ArrayList<HashMap<Names, Names>>();

	public SubField sub = new SubField(this);

	public Field(ArrayList<Player> playerIn) {
		players = playerIn;
		day = 1;

		log.add(null);// 0日目のログを埋めるため
		redLog.add(null);
		voted.add(null);
		voted.add(null);

		for(Player p: players){
			sub.connectConditions.put(p.getName(), new ConnectCondition(players));
			sub.suspectConditions.put(p.getName(), new SuspectCondition());

		}
	}

	public int getDay() {
		return day;
	}

	public int getSurvivorSum(){
		int survivorSum = 0;
		for (Player p : players) {
			if (p.isSurvive())
				survivorSum++;
		}
		return survivorSum;
	}

	void forwardDay(){
		for(Player p: players){
			p.suspect_said_yesterday = p.suspect_said;
			p.suspect_said = null;
			p.wantInspect_said_yesterday = p.wantInspect_said;
			p.wantInspect_said = null;
		}
		day++;
	}
	void forwardDay(HashMap<Names, Names> votedIn, Names inspected, Names defenced, ArrayList<Names> eatenVoted) {
		Names tmpEaten = null, tmpVoted = null;
		HashMap<Names, Integer> votedSum = new HashMap<>();

		/** 投票 */
		for (Map.Entry<Names, Names> e : votedIn.entrySet()) {
			sub.connectConditions.get(e.getKey()).condition.get(e.getValue())[ConnectCondition.vote] = 1;
			sub.connectConditions.get(e.getValue()).condition.get(e.getKey())[ConnectCondition.voted] = 1;
			if (votedSum == null) {
				votedSum.put(e.getValue(), 1);
			} else if (!(votedSum.containsKey(e.getValue()))) {
				votedSum.put(e.getValue(), 1);
			} else {
				votedSum.put(e.getValue(), votedSum.get(e.getValue()) + 1);
			}
		}

		tmpVoted = getMaxVoted(votedSum);
		voted.add(day, votedIn);
		getPlayer(tmpVoted).toBeDead();
		sub.executed.add(day, tmpVoted);
		sub.suspectConditions.get(tmpVoted).setExecuted_condition(day);
		/**
		 * 追放を知らせる情報を書き込む
		 */
		inform(new Statement());

		/** 襲撃 */
		HashMap<Names, Integer> eatenSum = new HashMap<>();
		for (Names name: eatenVoted) {
			if (name == null)
				continue;
			if (eatenSum == null || !(eatenSum.containsKey(name))) {
				eatenSum.put(name, 1);
			} else {
				eatenSum.put(name, eatenSum.get(name) + 1);
			}
		}
		if (eatenSum.size() != 0) {
			tmpEaten = getMaxVoted(eatenSum);
			if (defenced != tmpEaten) {
				if(getPlayer(tmpEaten).isSurvive()){
					getPlayer(tmpEaten).toBeDead();
					if(defenced != null){
						getPlayer(Roles.hunter).info_Easy.add(new Infomation(defenced, false));
					}
					sub.attacked.add(day, tmpEaten);
					sub.suspectConditions.get(tmpEaten).setAttacked_condition(day);
					/**
					 * 襲撃されたことを知らせる情報を書き込む
					 */
					inform(new Statement());
				}else{
					if(defenced != null){
						getPlayer(Roles.hunter).info_Easy.add(new Infomation(defenced, false));
					}
					sub.attacked.add(day, null);
				}
				
			}else{
				getPlayer(Roles.hunter).info_Easy.add(new Infomation(defenced, true));
				sub.attacked.add(day, null);
				/**
				 * GJを知らせる情報を書き込む
				 * ちゃんと守れたことも狩人に伝える
				 */
				inform(new Statement());
			}
		}else{
			if(defenced != null){
				getPlayer(Roles.hunter).info_Easy.add(new Infomation(defenced, false));
			}
			sub.attacked.add(day, null);
			/**
			 * GJを知らせる情報を書き込む
			 * 守ってはいないことを伝える
			 */
			inform(new Statement());
		}

		/**
		 * 各役職への行動 情報を与える際のStatementを作る必要あり。（プレイヤーAがWolfであることをStatementで伝えるため）
		 */
		//卒論用
		Player s = getPlayer(Roles.seer);
		if(s.isSurvive()){
			s.info_Easy.add(new Infomation(inspected, (getPlayer(inspected).getRole() != Roles.wolf)? true: false));
		}
		Player m = getPlayer(Roles.medium);
		if(m.isSurvive()){
			m.info_Easy.add(new Infomation(tmpVoted, (getPlayer(tmpVoted).getRole() != Roles.wolf)? true: false));
		}


		Statement toSeer, toMedium;
		/** ここで情報の中身を作成 */
		toSeer = new Statement();
		toMedium = new Statement();
		for (Player p : players) {
			p.chargeVoices();
			switch (p.getRole()) {
			case seer:
				p.addInformation(toSeer);
				;
				break;
			case medium:
				p.addInformation(toMedium);
				;
			}
		}
		day++;
	}

	public ArrayList<Player> getPlayers() {
		ArrayList<Player> re = new ArrayList<Player>(players);
		return re;
	}

	public ArrayList<Statement> getLogOfToday() {
		ArrayList<Statement> re = new ArrayList<Statement>(log.get(day));
		return re;
	}

	public ArrayList<ArrayList<Statement>> getLog() {
		ArrayList<ArrayList<Statement>> re = new ArrayList<ArrayList<Statement>>(log);
		return re;
	}

	void setNewDayLogs() {
		log.add(new ArrayList<Statement>());
		redLog.add(new ArrayList<Statement>());
	}

	Statement addLog(Player p, Statement statement) {
		statement.setPlayerName(p.getName());
		log.get(day).add(statement);
		p.useVoice();

		//fieldへ作用するmethod
		reflectStatement(statement);
		return statement;
	}

	ArrayList<Statement> getRedLogOfToday() {
		return redLog.get(day);
	}

	ArrayList<ArrayList<Statement>> getRedLog() {
		return redLog;
	}

	Statement addRedLog(Player p, Statement statement) {
		statement.setPlayerName(p.getName());
		statement.setorder(log.size() - 1);
		redLog.get(day).add(statement);
		p.useRedVoice();
		return statement;
	}

	private void inform(Statement info){
		for(Player p: players){
			p.addInformation(info);
		}
	}


	public Player getPlayer(Names name){
		Player ans = new Player();
		for(Player p: players){
			if(p.getName() == name){
				ans = p;
				break;
			}
		}
		return ans;
	}

	public Player getPlayer(Roles role){
		Player ans = new Player();
		for(Player p: players){
			if(p.getRole() == role){
				ans = p;
				break;
			}
		}
		return ans;
	}

	/**
	 * 卒論用
	 * CO, suspect, request, inspectResult, tellResult, guard以外には対応してない
	 * @param s
	 */
	public void reflectStatement(Statement s){
		Names playerName = s.getPlayerName();
		ArrayList<SimpleSentense> sentenses = s.getEasySentenses();
		for(SimpleSentense sentense: sentenses){
			Names opponent = sentense.name;
			switch (sentense.v) {
			case CO:
				sub.addGIFTsList(playerName, sentense.role);
				for(Names n: sub.getGIFTsList(sentense.role)){
					sub.suspectConditions.get(n).setCO_condition(sentense.role, sub.rolerSum.get(sentense.role));
					sub.connectConditions.get(playerName).condition.get(n)[ConnectCondition.comingOutSameRole] = 1;
					sub.connectConditions.get(n).condition.get(playerName)[ConnectCondition.comingOutSameRole] = 1;
				}
				break;
			case suspect:
				sub.connectConditions.get(playerName).condition.get(opponent)[ConnectCondition.suspect] = 1;
				sub.connectConditions.get(opponent).condition.get(playerName)[ConnectCondition.suspected] = 1;
				getPlayer(playerName).suspect_said = opponent;
				break;
			case request:
				sub.connectConditions.get(playerName).condition.get(opponent)[ConnectCondition.request_Inspect] = 1;
				sub.connectConditions.get(opponent).condition.get(playerName)[ConnectCondition.requested_Inspect] = 1;
				sub.wantInspect.get(day).put(playerName, opponent);
				getPlayer(playerName).wantInspect_said = opponent;
				break;
			case inspectResult:
				sub.connectConditions.get(playerName).condition.get(opponent)[ConnectCondition.inspect] = sentense.isHuman? 1: -1;
				sub.connectConditions.get(opponent).condition.get(playerName)[ConnectCondition.inspected] = sentense.isHuman? 1: -1;
				sub.suspectConditions.get(opponent).setInspected_condition(sentense.isHuman);
				break;
			case tellResult:
				sub.connectConditions.get(playerName).condition.get(opponent)[ConnectCondition.tell] = sentense.isHuman? 1: -1;
				sub.connectConditions.get(opponent).condition.get(playerName)[ConnectCondition.telled] = sentense.isHuman? 1: -1;
				sub.suspectConditions.get(opponent).setTelled_condition(sentense.isHuman);
				break;
			case guard:
				sub.connectConditions.get(playerName).condition.get(opponent)[ConnectCondition.guard] = sentense.successGuard? 1: -1;
				sub.connectConditions.get(opponent).condition.get(playerName)[ConnectCondition.guarded] = sentense.successGuard? 1: -1;
				break;
			default:
				System.out.println("何かおかしい");
				break;
			}
		}
	}

	public static Names getMaxVoted(HashMap<Names, Integer> map){
		int maxVoted = 0;
		double percent = 0.5;
		Names tmpVoted = null;
		for (Map.Entry<Names, Integer> e : map.entrySet()) {
			if (e.getValue() > maxVoted) {
				tmpVoted = e.getKey();
				maxVoted = e.getValue();
				percent = 0.5;
			} else if (e.getValue() == maxVoted) {
				if (Math.random() < percent) {
					tmpVoted = e.getKey();
				}
				percent = 1.0 / (1.0 / percent + 1.0);
			}
		}
		return tmpVoted;
	}

	/**
	 * ゲッターとセッター作ったけど、 getLogToday().add(anything)
	 * って風に書けばセッターを使わずに(publicに)ログを書きかえられちゃう。ArrayListが参照渡し状態になっちゃう。
	 * roleが人狼のPlayerだけが赤ログを見れるように、取得できるようにしたい。
	 */

}
