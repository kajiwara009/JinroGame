package platform.Sentense.Simple;

import java.util.ArrayList;
import java.util.jar.Attributes.Name;

import platform.*;
import platform.Sentense.Sentense;
//作成途中
public class Noun {
	String nounString;
	ArrayList<Names> name;
	ArrayList<Roles> role;
	ArrayList<Sentense> pronoun;
	Operator operator = null;


	public Noun(Object nounIn){
		if(nounIn instanceof Names){
			name.add((Names)nounIn);
		}else if(nounIn instanceof Roles){
			role.add((Roles)nounIn);
		}else if(nounIn instanceof Sentense){
			pronoun.add((Sentense)nounIn);
		}
	}
	public Noun(ArrayList nounIn, Operator ope){
		for(Object n: nounIn){
			if(n instanceof Names){
				name.add((Names)n);
			}else if(n instanceof Roles){
				role.add((Roles)n);
			}else if(n instanceof Sentense){
				pronoun.add((Sentense)n);
			}
		}
		operator = ope;
	}

	public String getString(){

		return null;
	}

}
