package platform;

public class Infomation {
	public Names name;
	//占、霊能ならばBoolean = isHuman、狩人ならば Boolean = successGuard
	public boolean bool;
	public boolean isSaid = false;

	public Infomation(Names nameIn, boolean isHuman_Guarded){
		name = nameIn;
		bool = isHuman_Guarded;
	}

	public void isSaid(){
		isSaid = true;
	}

}
