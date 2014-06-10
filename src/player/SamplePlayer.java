package player;

import platform.Field;
import platform.Names;
import platform.Player;
import platform.Roles;
import platform.Statement;
import platform.Sentense.Simple.Verb;

public class SamplePlayer extends Strategy{
	//自分のPlayerはmeで入ってる

	@Override
	public Statement talk(Field field) {
		if(me.getEnvironment() == null){
		}
		return me.getEnvironment().makeStatement(field);

	}

	@Override
	public Statement talk2(Field field) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Statement talkRedLog(Field field) {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public Names vote(Field field) {
		// TODO 自動生成されたメソッド・スタブ
		return me.getEnvironment().setAction(field, Verb.vote);
	}

	@Override
	public Names eat(Field field) {
		// TODO 自動生成されたメソッド・スタブ
		return me.getEnvironment().setAction(field, Verb.attack);
	}

	@Override
	public Names inspect(Field field) {
		// TODO 自動生成されたメソッド・スタブ
		return me.getEnvironment().setAction(field, Verb.inspect);
	}

	@Override
	public Names guard(Field field) {
		// TODO 自動生成されたメソッド・スタブ
		return me.getEnvironment().setAction(field, Verb.guard);
	}
}