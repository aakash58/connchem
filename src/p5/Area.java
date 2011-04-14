package p5;

import processing.core.PApplet;
import processing.core.PImage;

public class Area extends PApplet implements Region {
	private float x;
	private float y;
	private float w;
	private float h;

	public void setDimensions() {
		setX(0);
		setY(0);
		setW(0);
		setH(0);
	}
	public void setDimensions(float w_, float h_) {
		setW(w_);
		setH(h_);
	}
	public void setDimensions(float x_, float y_, float w_, float h_) {
		setX(x_);
		setY(y_);
		setW(w_);
		setH(h_);
	}
	public void setX(float input) {
		x = input;
	}
	public void setY(float input) {
		y = input;
	}
	public void setW(float input) {
		w = input;
	}
	public void setH(float input) {
		h = input;
	}
	public void setR(float input) {
		float prevR = r();
		setR(input);
		float diff = prevR - r();
		setW(w() - diff);
	}
	public void setB(float input) {
		float prevB = b();
		setB(input);
		float diff = prevB - b();
		setH(h() - diff);
	}
	public void setMW(float input) {
		float diff = mw() - input;
		x = x - diff;
	}
	public void setMH(float input) {
		float diff = mh() - input;
		y = y - diff;
	}
	public float x() { return x; }
	public float y() { return y; }
	public float w() { return w; }
	public float h() { return h; }
	public float r() {
		float r = x() + w();
		return r;
	}
	public float b() {
		float b = y() + h();
		return b;
	}
	public float mh() {
		float mh = y() + h()/2;
		return mh;
	}
	public float mw() {
		float mw = x() + w()/2;
		return mw;
	}
	public boolean mouseOver() {
		if (mouseX > x() && mouseY > y() && mouseX < r() && mouseY < b()) {
			return true;
		}
		return false;
	}
	public float mouseX() {
		return mouseX - x();
	}
	public float mouseY() {
		return mouseY - y();
	}
	public float mouseXLocalNorm() {
		return norm(mouseX - x(), 0, w());
	}
	public float mouseYLocalNorm() {
		return norm(mouseY - y(), h(), 0);
	}
	public void setFill(int input) {
		// TODO implement
	}
	public void setStrk(int input) {
		// TODO implement
	}
	public void removeFill() {
		// TODO implement
	}
	public void removeStrk() {
		// TODO implement
	}

	public String getState() {
		String state;
		if (mouseOver() && mousePressed == true) {
			state = "active";
		} else if (mouseOver()) {
			state = "over";
		} else {
			state = "up";
		}
		return state;
	}


	////////////////
	/* BACKGROUND */
	////////////////

	// TODO implement these variables with methods
	String corners;
	PImage wallpaper;
	float wallpaperDarkness;
	float cornerRadius;

	public void displayBg() {
		pushMatrix();
		translate(x(), y());
		pushStyle();

		if (corners != null && corners.equals("rounded")) {
			roundRect(0,0,w(), h(), cornerRadius);
		}
		else {
			rect(0,0,w(), h());
		}
		popStyle();
		popMatrix();
		if (wallpaper != null) {
			displayWallpaper();
		}
	}
	public void wallpaper(String imagePath, int darkness) {
		PImage img = loadImage(imagePath);
		wallpaper = img;
		wallpaperDarkness = darkness;
	}

	public void displayWallpaper() {
		pushStyle();
		noStroke();
		float imageW = wallpaper.width; 
		float imageH = wallpaper.height;

		int imageQntyHoriz = ceil(w() / imageW);
		int imageQntyVert = ceil(h() / imageH);

		for (int i = 0; i < imageQntyHoriz; i++) {
			for (int j = 0; j < imageQntyVert; j++) {
				float imageX = x() + (i * imageW);
				float imageY = y() + (j * imageH);
				image(wallpaper, imageX, imageY);
			}
		}
		fill(color(0, wallpaperDarkness));
		rect(x(), y(), w(), h());
		popStyle();
	}
	/////////////////////
	/* ROUNDED CORNERS */
	/////////////////////

	// Adds support for a quadratic Bézier curve by converting it 
	// to a cubic Bézier curve that is supported by Processing.
	// prevX and prevY are used to get the previous x,y of the current path
	private void quadraticBezierVertex(float prevX, float prevY, float cpx, float cpy, float x_, float y_) {
		float cp1x = prevX + 2.0f/3.0f*(cpx - prevX);
		float cp1y = prevY + 2.0f/3.0f*(cpy - prevY);
		float cp2x = cp1x + (x_ - prevX)/3.0f;
		float cp2y = cp1y + (y_ - prevY)/3.0f;

		// finally call cubic Bezier curve function
		bezierVertex(cp1x, cp1y, cp2x, cp2y, x_, y_);
	};

	public void roundRect(float x_, float y_, float w_, float h_, float r_) {
		float p1x = x_ + r_;
		float p1y = y_;

		float p2x = x_ + w_ - r_;
		float p2y = y_;

		float p3x = x_ + w_;
		float p3y = y_ + r_;

		float p4x = x_ + w_;
		float p4y = y_ + h_ - r_;

		float p5x = x_ + w_ - r_;
		float p5y = y_ + h_;

		float p6x = x_ + r_;
		float p6y = y_ + h_;

		float p7x = x_;
		float p7y = y_ + h_ - r_;

		float p8x = x_;
		float p8y = y_ + r_;


		beginShape();
		vertex(p1x, p1y);
		vertex(p2x, p2y);
		quadraticBezierVertex(p2x, p2y, p3x, p2y, p3x, p3y);
		vertex(p4x, p4y);
		quadraticBezierVertex(p4x, p4y, p4x, p5y, p5x, p5y);
		vertex(p6x, p6y);
		quadraticBezierVertex(p6x, p6y, p7x, p6y, p7x, p7y);
		vertex(p8x, p8y);
		quadraticBezierVertex(p8x, p8y, p8x, p1y, p1x, p1y);
		endShape(CLOSE);
	}
}
