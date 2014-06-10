
package platform.Reinforcement_Learning.LearningData;

import java.util.LinkedList;

public class LearningData_percentage {
	public double percentage;
	LinkedList<Boolean> per1000 = new LinkedList<>();
	public int selectedSum = 0;

	public LearningData_percentage(){
		percentage = 0.666;
		for(int i = 0; i < 1000; i++){
			per1000.add(i%3 != 0? true: false);
		}
	}

	public void UpdatePercentage(Boolean isFriend){
		boolean out = per1000.removeFirst();
		per1000.add(isFriend);
		if(isFriend != out){
			percentage += isFriend? 0.001: -0.001;
		}
		selectedSum++;
	}

}
