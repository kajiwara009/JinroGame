package platform;

import java.util.ArrayList;
import java.util.HashMap;

import javax.sound.midi.MidiDevice.Info;

import static platform.ConstantValues.*;
import platform.Reinforcement_Learning.Environment.Environment;
import platform.Reinforcement_Learning.Environment.EnvironmentSet;
import player.*;

public class Jinro {
	ArrayList<Player> players = new ArrayList<Player>();
	Field field;
	private boolean humanSideWon, wolfSideWon;

	/***
	 * コンストラクタ
	 */

	public Jinro(EnvironmentSet e) {
		ArrayList<Names> names = new ArrayList<>();
		ArrayList<Roles> roles = new ArrayList<>();
		ArrayList<Strategy> abstractPlayers = new ArrayList<Strategy>();
		ArrayList<Names> wolfs = new ArrayList<>();

		for (int i = 0; i < VSmode.NumberOfPlayer; i++) {
			abstractPlayers.add(new SamplePlayer());
		}

		for (Names n : Names.values()) {
			names.add(n);
		}
		// 人間サイド
		for (int i = 0; i < e.env.size(); i++) {
			players.add(new Player(names.get(i), e.env.get(i).role,
					abstractPlayers.get(i), e.env.get(i)));
		}
		// 人狼
		for (int i = 0; i < e.envSet_wolf.envW.length; i++) {
			players.add(new Player(names.get(e.env.size() + i),
					e.envSet_wolf.envW[i].role, abstractPlayers.get(e.env
							.size() + i), e.envSet_wolf.envW[i]));
			wolfs.add(names.get(e.env.size() + i));
		}
		// 狂人
		players.add(new Player(names.get(e.env.size()
				+ e.envSet_wolf.envW.length), e.envSet_lunatic.envL.role,
				abstractPlayers.get(e.env.size() + e.envSet_wolf.envW.length),
				e.envSet_lunatic.envL));

		for (Player p : players) {
			if (p.getRole() == Roles.wolf) {
				p.friendWolfs = wolfs;
			}
		}

		/*
		 * for (Roles r : Roles.values()) { for (int i = 0; i <
		 * r.NumberOfTheRoler(); i++) { roles.add(r); } }
		 *
		 * for (int i = 0; i < VSmode.NumberOfPlayer; i++) { players.add(new
		 * Player(names.get(i), roles.get(i), abstractPlayers.get(i))); }
		 */
		field = new Field(players);
		field.sub.env = e;

	}

	public Jinro() {
		ArrayList<Names> names = new ArrayList<>();
		ArrayList<Roles> roles = new ArrayList<>();
		for (Names n : Names.values()) {
			names.add(n);
		}

		for (Roles r : Roles.values()) {
			for (int i = 0; i < r.NumberOfTheRoler(); i++) {
				roles.add(r);
			}
		}
		ArrayList<Strategy> abstractPlayers = new ArrayList<Strategy>();
		for (int i = 0; i < VSmode.NumberOfPlayer; i++) {
			abstractPlayers.add(new SamplePlayer());
		}
		for (int i = 0; i < VSmode.NumberOfPlayer; i++) {
			players.add(new Player(names.get(i), roles.get(i), abstractPlayers
					.get(i)));
		}
		field = new Field(players);
	}

	public Jinro(ArrayList<Strategy> abs) {
		ArrayList<Names> names = new ArrayList<>();
		ArrayList<Roles> roles = new ArrayList<>();
		for (Names n : Names.values()) {
			names.add(n);
		}

		for (Roles r : Roles.values()) {
			for (int i = 0; i < r.NumberOfTheRoler(); i++) {
				roles.add(r);
			}
		}
		randomSort(names);
		randomSort(roles);
		for (int i = 0; i < VSmode.NumberOfPlayer; i++) {
			players.add(new Player(names.get(i), roles.get(i), abs.get(i)));
		}
		field = new Field(players);
	}

	public void start() {
		/** 　しゃべって　投票して、襲撃先決めて　死んだり情報与えたりして　次の日？ */

		while (!(humanSideWon) && !(wolfSideWon)) {

			field.setNewDayLogs();
			/**
			 * speak
			 */
			/** 喋るプレイヤー定義 */
			ArrayList<Player> speakerOrder = new ArrayList<Player>();
//			ArrayList<Player> speakerOrder_WOLF = new ArrayList<>();
			for (Player p : players) {
				if (p.isSurvive()) {
					speakerOrder.add(p);
/*					if (p.getRole() == Roles.wolf) {
						speakerOrder_WOLF.add(p);
					}
*/				}
			}
			// 最初に狼が喋る
/*			while (true) {
				if (!talk(speakerOrder_WOLF, "red")) {
					break;
				}
			}
*/			// 卒論用。狼陣営がだます内容を考える
			if(field.getDay() >= 2){
				for (Player p : field.getPlayers()) {
					if (p.getRole() == Roles.wolf && p.getEnvironment().getFakeRole() != Roles.villager) {
						if(!p.isSurvive()) continue;
						Infomation info = field.sub.env.envSet_wolf.setFakeInfo(field, p, p.getEnvironment().getFakeRole());
						if(info != null){
							p.info_Easy.add(info);
						}
					}else if(p.getRole() == Roles.lunatic && p.getEnvironment().getFakeRole() != Roles.villager){
						if(!p.isSurvive()) continue;
						Infomation info = p.getEnvironment().makeFakeInfo(field, p);
						if(info != null){
							p.info_Easy.add(info);
						}
					}

				}
			}

			int talkNum = 0;
			loop1: while (true) {
				boolean talkNormal = talk(speakerOrder, "Normal");
				// System.out.println("ここまで");
/*				while (true) {
					if (!talk(speakerOrder_WOLF, "red")) {
						break;
					}
				}
*/				if (!talkNormal) {
					break;
				}
				if (talkNum > 1) {
					break;
				}
				talkNum++;
			}
			/**
			 * 初日のみSimonを殺して下の作業を省略すると思ったけど、シモンごとゲームから除外した
			 */

			if (field.getDay() == 1) {
				Names inspected = field.getPlayer(Roles.seer).getStrategy()
						.inspect(field);
				if (inspected == null) {
					for (int n : randomSequence(players.size())) {
						if (players.get(n).isSurvive()
								&& players.get(n).getName() != Names.Simon) {
							inspected = players.get(n).getName();
							break;
						}
					}
				}
				field.getPlayer(Roles.seer).info_Easy.add(new Infomation(
						inspected,
						(getPlayer(inspected).getRole() != Roles.wolf) ? true
								: false));
				field.sub.attacked.add(null);
				field.sub.executed.add(null);
				field.forwardDay();
				continue;
			}

			/**
			 * vote
			 */

			// Player inspected = new Player(), told = new Player(), defenced =
			// new Player();
			Names inspected = null, defenced = null;
			ArrayList<Names> eaten = new ArrayList<>();
			HashMap<Names, Names> voted = new HashMap<>();
			for (Player p : players) {
				if (!p.isSurvive()) {
					continue;
				}
				Names votedP = p.getStrategy().vote(field);
				voted.put(p.getName(), votedP == null ? p.getName()
						: (getPlayer(votedP).isSurvive()) ? getPlayer(votedP)
								.getName() : p.getName());
				/** 死んでるプレイヤーを選択or誰も選択しない場合は自分に投票したことになる */
				switch (p.getRole()) {
				case seer:
					inspected = p.getStrategy().inspect(field);

					if (inspected == null) {
						loop1: for (int n : randomSequence(players.size())) {
							if (players.get(n).isSurvive()) {
								for (Infomation info : p.info_Easy) {
									if (info.name == players.get(n).getName()) {
										continue loop1;
									}
								}
								inspected = players.get(n).getName();
								break;
							}
						}
						if (inspected == null) {
							inspected = p.getName();
						}
					}

					break;

				case hunter:
					defenced = p.getStrategy().guard(field);
					if (defenced == null) {
						for (int n : randomSequence(players.size())) {
							if (players.get(n).isSurvive()
									&& players.get(n) != p) {
								defenced = players.get(n).getName();
								break;
							}
						}
					}
					break;

				case wolf:
					/**
					 *
					 * 注意！！！
					 *
					 *
					 */
					//if (eaten.size() == 0) {
						eaten.add(field.sub.env.envSet_wolf.selectAttackPlayer(
								field, p));
					//}else {
						//System.out.println("Ok");
					//}
					// 卒論ではここではやらない。下で狼の総意としてやる
					// eaten.add(p.getStrategy().eat(field));
					break;

				default:
					break;
				}
			}

			/**
			 * change the field ,and get information
			 */
			field.forwardDay(voted, inspected, defenced, eaten);
			int amountOfWolf = 0, amountOfHuman = 0;
			for (Player p : players) {
				if (p.isSurvive()) {
					if (p.getRole() == Roles.wolf) {
						amountOfWolf++;
					} else {
						amountOfHuman++;
					}
				}
			}

//			System.out.println("human:" + amountOfHuman + "  and  wolf:"+ amountOfWolf);
			if (amountOfWolf == 0)
				humanSideWon = true;
			else if (amountOfWolf >= amountOfHuman)
				wolfSideWon = true;
		}

	}

	private boolean talk(ArrayList<Player> order, String logType) {
		boolean talkExist = false;
		randomSort(order);

		if (logType == "red") {
			for (Player p : order) {
				if (p.getRestOfRedVoice() > 0) {
					Statement statement = p.getStrategy().talkRedLog(field);
					if (statement != null) {
						talkExist = true;
						if (statement.getString() != "continue") {
							field.addRedLog(p, statement);
						}
					}
				}
			}
		} else {
			for (Player p : order) {
				if (p.getRestOfVoice() > 0) {
					Statement statement = p.getStrategy().talk(field);

					// 卒論用
					if (statement.getEasySentenses().size() != 0) {
						// if (statement != null) {
						talkExist = true;
						if (statement.getString() != "continue") {
							field.addLog(p, statement);
						}
					}
				}
			}
		}

		return talkExist;
	}

	public boolean isHumanWin() {
		return humanSideWon;
	}

	public Player createPlayer() {

		return new Player();
	}

	/**
	 * 注意：あるArrayListをfor文でまわしている中で使うとfor文がぐちゃぐちゃになる
	 *
	 * @param array
	 * @return
	 */
	public static <T> ArrayList randomSort(ArrayList array) {
		for (int i = 0; i < array.size(); i++) {
			int j = (int) Math.floor(Math.random() * (i + 1));
			Object tmp = array.get(i);
			array.set(i, array.get(j));
			array.set(j, tmp);
		}
		return array;
	}

	public Player getPlayer(Names name) {
		Player ans = new Player();
		for (Player p : players) {
			if (p.getName() == name) {
				ans = p;
				break;
			}
		}
		return ans;
	}

	public Field getField() {
		return field;
	}

	public static ArrayList<Integer> randomSequence(int length) {
		ArrayList<Integer> num = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			num.add(i);
		}
		randomSort(num);
		return num;
	}

	/**
	 * 本当は条件式にメソッドを入れたいが無理
	 *
	 * @param playersSel
	 * @param condition
	 * @return
	 */
	/*
	 * public Player randomSelect(ArrayList<Player> playersSel, boolean
	 * condition){ ArrayList<Integer> num = new ArrayList<>(); for(int i = 0; i
	 * < playersSel.size(); i++){ num.add(i); } randomSort(num); for(int i:
	 * num){ if(condition){ return } } return null; }
	 */

}
