package platform.Sentense;

public enum Conjunction {
	AND, AS, BECAUSE, OR, IF;

	public int selectPattern(){
		if(this == IF){
			return 3;
		}else{
			return 2;
		}
	}

}
