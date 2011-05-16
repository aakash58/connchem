package control;

import view.P5Canvas;
import processing.core.*;

public class MainController extends PApplet{
	public MainController(P5Canvas bouncyBalls_) {
		p5Canvas = bouncyBalls_;
	}
	P5Canvas p5Canvas;
	
	public void addMolecule(String molName) {
		p5Canvas.addMolecule(molName);
	}
}
