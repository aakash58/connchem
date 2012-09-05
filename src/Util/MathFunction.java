package Util;

import org.jbox2d.common.Vec2;

public class MathFunction {
	
	public MathFunction()
	
	{
		
	}
	
	//Normalize the input force
	public static Vec2 normalizeForce(Vec2 v) {
		float dis = (float)Math.sqrt(v.x * v.x + v.y * v.y);
		return new Vec2(v.x / dis, v.y / dis);
	}

}
