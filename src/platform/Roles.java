package platform;

public enum Roles {
	/**
	 * GIFTEDなどの扱いは実際にセンテンスに入れるときに、S = GIFTED を S = (seer or medium or ~~)って形に直せばいいかなとか思ってみたり
	 */

	/** 村人   占     霊     狩人     共有者    狼     狂人    シモン */
	villager, seer, medium, hunter, freemason, wolf, lunatic, Simon ,


	;



	public Boolean isHuman(){
		if(this == wolf){
			return false;
		}else{
			return true;
		}
	}

	public Boolean isHumanSide(){
		if(this == wolf || this == lunatic){
			return false;
		}else{
			return true;
		}
	}

	public Boolean isWolfSide(){
		return !this.isHumanSide();
	}

	public Boolean isGifted(){
		if(this == seer || this == medium || this == freemason || this == hunter){
			return true;
		}else{
			return false;
		}
	}



	public int NumberOfTheRoler(){
		switch (this) {
		case freemason:
			return VSmode.NumberOfFreemason;
		case hunter:
			return VSmode.NumberOfHunter;
		case lunatic:
			return VSmode.NumberOfLunatic;
		case medium:
			return VSmode.NumberOfMedium;
		case seer:
			return VSmode.NumberOfSeer;
		case Simon:
			return VSmode.NumberOfSimon;
		case wolf:
			return VSmode.NumberOfWolf;
		case villager:
			return VSmode.NumberOfPlayer - VSmode.NumberOfFreemason - VSmode.NumberOfHunter - VSmode.NumberOfLunatic - VSmode.NumberOfMedium - VSmode.NumberOfSeer - VSmode.NumberOfSimon - VSmode.NumberOfWolf;
		default:
			return 0;
		}
	}


}
