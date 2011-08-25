package view;

import static model.State.molecules;

import java.util.ArrayList;
import java.util.Random;

import main.Main;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.JointType;
import org.jbox2d.dynamics.joints.PrismaticJoint;
import org.jbox2d.dynamics.joints.PrismaticJointDef;

import static view.P5Canvas.*;

public class Unit2 {
	public static int num_total =0;
	public static int num_gone =0;
	public static int numWater =0;
	public static float massDissolved=0;
	public static int water100mL =25;
	public static int mToMass =10;
	
	
	/******************************************************************
	* FUNCTION :     add2Ions
	* DESCRIPTION :  Specific function used to add particular Ions, Called by addMolecule()
	*
	* INPUTS :       ion1(String), ion2(String), count(int), box2d_(PBox2D),parent_(P5Canvas)
	* OUTPUTS:       None
	*******************************************************************/
	public static void add2Ions(String ion1, String ion2, int count, PBox2D box2d_, P5Canvas parent_) {
		int numRow = (int) (Math.ceil(count/3.8)+1);
		Vec2 size1 = Molecule.getShapeSize(ion1, parent_);
		Vec2 size2 = Molecule.getShapeSize(ion2, parent_);
		Random rand = new Random();                            //Random number used to generate ions in random location
		float centerX = x + 50 + rand.nextFloat()*(w/3*2);     //X coordinate around which we are going to add Ions, 50 is border width
		float centerY = y + 80-Boundary.difVolume;             //Y coordinate around which we are going to add Ions
		
		for (int i=0;i<count;i++){
			float x1,y1,angle1;
			float x2,y2,angle2;
			int r = i%numRow;
			x1 = centerX + (i/numRow)*(size1.x+size2.x);
			x2 = x1 + size1.x;
			if ((r%2==1 )){
				float tmp=x1;
				x1=x2;
				x2=tmp;
			}
			y1 =centerY + (i%numRow)*size1.y;
			y2=y1;
			angle1 = 0;
			angle2 = 0;
			molecules.add(new Molecule(x1, y1,ion1,box2d_, parent_,angle1));
			molecules.add(new Molecule(x2, y2,ion2,box2d_, parent_,angle2));
			int index1 = molecules.size()-2;
			int index2 = molecules.size()-1;
			Molecule m1 = molecules.get(index1);
			Molecule m2 = molecules.get(index2);
			joint2Ions(index1, index2,m1,m2);
		}
	}
	
	public static void joint2Ions(int index1, int index2, Molecule m1, Molecule m2) { // draw background
		DistanceJointDef djd = new DistanceJointDef();
		djd.bodyA = m1.body;
		djd.bodyB = m2.body;
		djd.length =PBox2D.scalarPixelsToWorld(2*Molecule.clRadius);
		djd.frequencyHz = 10.0f;
		DistanceJoint dj = (DistanceJoint) PBox2D.world.createJoint(djd);
		m1.compoundJ = index2;
		m2.compoundJ = index1;
		m1.compoundJoints = dj;
		m2.compoundJoints = dj;
	}
	
	public static void addSiO2(String compoundName_, int count, PBox2D box2d_, P5Canvas parent_) {
		int numRow = (int) (Math.ceil(count/4)+1);
		int numCol = count/numRow;
			Vec2 size1 = Molecule.getShapeSize(compoundName_, parent_);
		for (int i=0;i<count;i++){
			float x1,y1,angle1;
			int c = i%numCol;
			int r = i/numCol;
			if (r%2==0)
				x1 = x + 260+ c*(size1.x);
			else
				x1 = x + 260+ (c+0.5f)*(size1.x);
				
			y1 =y + 80-Boundary.difVolume+r*size1.y;
			
			if (r==0 && c==0){
				x1 = x + 260+ 3*(size1.x);
				y1 = y + 80-Boundary.difVolume+2*size1.y;
			}
			angle1 = 0;
			molecules.add(new Molecule(x1, y1,compoundName_,box2d_, parent_,angle1));
		}
	}
	
	public static void addCalciumChloride(String compoundName_, int count, PBox2D box2d_, P5Canvas parent_) {
		String ion1 = "Chlorine-Ion";
		String ion2 = "Calcium-Ion";
		String ion3 = "Chlorine-Ion";
		Vec2 size1 = Molecule.getShapeSize(ion1, parent_);
		Vec2 size3 = Molecule.getShapeSize(ion3, parent_);
		for (int i=0;i<count;i++){
			float x1,y1,angle1;
			float x2,y2,angle2;
			float x3,y3,angle3;
			x1 = x + 200 + (i%2)*(size1.x*3);
			x2 = x1 + size1.x;
			x3 = x1 + size1.x + size3.x;
			y1 =y + 100-Boundary.difVolume+(i/2)*2*size1.y;
			y2=y1;
			y3=y1;
			angle1 = 0;
			angle2 = 0;
			angle3 = 0;
			if (i%4==1){
				x2=x1;
				x3=x1;
				y1=y1-size1.x;
				y2=y1+size1.x;
				y3=y2+size1.x;
			}
			else if (i%4==2){
				x1=x2;
				x3=x2;
				y1=y1-size1.x;
				y2=y1+size1.x;
				y3=y2+size1.x;
			}
			else if (i%4==3){
				x1=x1-size1.x;
				x2=x2-size1.x;
				x3=x3-size1.x;
			}
				
			molecules.add(new Molecule(x1, y1,ion1,box2d_, parent_,angle1));
			molecules.add(new Molecule(x2, y2,ion2,box2d_, parent_,angle2));
			molecules.add(new Molecule(x3, y3,ion3,box2d_, parent_,angle3));
			
			
			int num = molecules.size();
			int index1 = num-3;
			int index2 = num-2;
			int index3 = num-1;
			Molecule m1 = molecules.get(index1);
			Molecule m2 = molecules.get(index2);
			Molecule m3 = molecules.get(index3);
			jointCaCl(index1, index2,index3,m1,m2,m3);
		}
	}
	
	
	
	public static void addNaHCO3(String compoundName_, int count, PBox2D box2d_, P5Canvas parent_) {
		String ion1 = "Bicarbonate";
		String ion2 = "Sodium-Ion";
		Vec2 size1 = Molecule.getShapeSize(ion1, parent_);
		Vec2 size2 = Molecule.getShapeSize(ion2, parent_);
		for (int i=0;i<count;i++){
			float x1,y1,angle1;
			float x2,y2,angle2;
			x1 = x + 200 + (i%3)*(size1.x+size2.x);
			x2 = x1 + size1.x-20;
			y1 =y + 100-Boundary.difVolume+(i/3)*size1.y;
			y2=y1;
			angle1 = 0;
			angle2 = 0;
			molecules.add(new Molecule(x1, y1,ion1,box2d_, parent_,angle1));
			molecules.add(new Molecule(x2, y2,ion2,box2d_, parent_,angle2));
			
			int index1 = molecules.size()-2;
			int index2 = molecules.size()-1;
			Molecule m1 = molecules.get(index1);
			Molecule m2 = molecules.get(index2);
			jointNaHCO3(index1, index2,m1,m2);
		}
	}
	
	public static void jointNaHCO3(int index1, int index2, Molecule m1, Molecule m2) { // draw background
		DistanceJointDef djd = new DistanceJointDef();
		djd.bodyA = m1.body;
		djd.bodyB = m2.body;
		//djd.initialize(m1.body, m2.body, new Vec2(0,0), new Vec2(0,0));
		djd.length =PBox2D.scalarPixelsToWorld(Molecule.oRadius +34);
		
		//djd.dampingRatio = 0.5f;
		
		djd.frequencyHz = 10.0f;
		DistanceJoint dj = (DistanceJoint) PBox2D.world.createJoint(djd);
		m1.compoundJ = index2;
		m2.compoundJ = index1;
		m2.compoundJoints = dj;
		
		PrismaticJointDef pjd = new PrismaticJointDef();
		pjd.initialize(m1.body, m2.body, m1.body.getWorldCenter(),new Vec2(1,0));
		PrismaticJoint pj = (PrismaticJoint) PBox2D.world.createJoint(pjd);
		m2.compoundJoints2 = pj;
	}
	
	public static void computeForceSiO2(int index, Molecule mIndex) { // draw background
		for (int e = 0; e < mIndex.getNumElement(); e++) {
			int indexCharge = mIndex.elementCharges.get(e);
			Vec2 locIndex = mIndex.getElementLocation(e);
			mIndex.sumForceX[e]=0;
			mIndex.sumForceY[e]=0;
			for (int i = 0; i < molecules.size(); i++) {
				if (i==index)
					continue;
				Molecule m = molecules.get(i);
				if (m.getName().equals("Water")) continue;
				
				float forceX;
				float forceY;
				for (int e2 = 0; e2 < m.getNumElement(); e2++) {
					Vec2 loc = m.getElementLocation(e2);
					if(loc==null || locIndex==null) continue;
					float x = locIndex.x-loc.x;
					float y = locIndex.y-loc.y;
				    float dis = x*x +y*y;
					forceX =  (float) ((x/Math.pow(dis,1.5))*10);
					forceY =  (float) ((y/Math.pow(dis,1.5))*10);
					
					int charge = m.elementCharges.get(e2);
					int mul = charge*indexCharge;
					if (mul<0){
						mIndex.sumForceX[e] += mul*forceX;
						mIndex.sumForceY[e] += mul*forceY;
					}
					else if (mul>0){
						mIndex.sumForceX[e] += mul*forceX*mIndex.chargeRate;
						mIndex.sumForceY[e] += mul*forceY*mIndex.chargeRate;
					}
				}
			}
		}
	}
	
	public static void computeForceGlycerol(int index, Molecule mIndex) { // draw background
		float xMul = 1.4f;	
		for (int e = 0; e < mIndex.getNumElement(); e++) {
			int indexCharge = mIndex.elementCharges.get(e);
			Vec2 locIndex = mIndex.getElementLocation(e);
			mIndex.sumForceX[e]=0;
			mIndex.sumForceY[e]=0;
			for (int i = 0; i < molecules.size(); i++) {
				if (i==index)
					continue;
				Molecule m = molecules.get(i);
				if (m.getName().equals("Water"))
					continue;
				float forceX;
				float forceY;
				for (int e2 = 0; e2 < m.getNumElement(); e2++) {
					Vec2 loc = m.getElementLocation(e2);
					if(loc==null || locIndex==null) continue;
					float x = locIndex.x-loc.x;
					float y = locIndex.y-loc.y;
				    float dis = x*x +y*y;
					forceX =  (float) ((x/Math.pow(dis, 1.5))*0.3);
					forceY =  (float) ((y/Math.pow(dis, 1.5))*0.3);
					if (temp<mIndex.freezingTem){
						forceX *=90;
						forceY *=90;
					}
					int charge = m.elementCharges.get(e2);
					int mul = charge*indexCharge;
					if (mul<0){
						mIndex.sumForceX[e] += mul*forceX;
						mIndex.sumForceY[e] += mul*forceY;
					}
					else if (mul>0){
						mIndex.sumForceX[e] += mul*forceX*mIndex.chargeRate;
						mIndex.sumForceY[e] += mul*forceY*mIndex.chargeRate;
					}
				}
			}
		}
	}
	
	public static void computeForceNaCl(int index, Molecule mIndex) { // draw background
		for (int e = 0; e < mIndex.getNumElement(); e++) {
			int indexCharge = mIndex.elementCharges.get(e);
			Vec2 locIndex = mIndex.getElementLocation(e);
			mIndex.sumForceWaterX[e]=0;
			mIndex.sumForceWaterY[e]=0;
			mIndex.sumForceX[e]=0;
			mIndex.sumForceY[e]=0;
			for (int i = 0; i < molecules.size(); i++) {
				if (i==index)
					continue;
				Molecule m = molecules.get(i);
				float forceX;
				float forceY;
				for (int e2 = 0; e2 < m.getNumElement(); e2++) {
					Vec2 loc = m.getElementLocation(e2);
					float x = locIndex.x-loc.x;
					float y = locIndex.y-loc.y;
					float dis = x*x +y*y;
					forceX =  (float) ((x/Math.pow(dis,1.5))*40);
					forceY =  (float) ((y/Math.pow(dis,1.5))*40);
					
					int charge = m.elementCharges.get(e2);
					int mul = charge*indexCharge;
					if (m.getName().equals("Water")){
						float r = 0.002f+temp/10000;
						if (mIndex.compoundJ>=0){
							forceX *=r;
							forceY *=r;
						}
						else{
							forceX *=0.10;
							forceY *=0.10;
						}
						if (temp>=100){
							forceX=0;
							forceY=0;
						}
						if (mul<0){
							mIndex.sumForceWaterX[e] += mul*forceX;
							mIndex.sumForceWaterY[e] += mul*forceY;
						}
						else if (mul>0){
							mIndex.sumForceWaterX[e] += mul*forceX*mIndex.chargeRate;
							mIndex.sumForceWaterY[e] += mul*forceY*mIndex.chargeRate;
						}
					}
					else{
						if (mIndex.compoundJ<0){             //Compute IonDis
							float dis2 =(float) (PBox2D.scalarWorldToPixels((float) Math.sqrt(dis)));
							if (mIndex.ionDis==0)
								mIndex.ionDis = dis2;
							else{
								if (dis2<mIndex.ionDis){
									mIndex.ionDis =dis2;
								}
							}
						}
						
						if ((m.compoundJ<0 || mIndex.compoundJ<0)&& 0<temp && temp<100){
							forceX *=0.05f;
							forceY *=0.05f;
						}
						if (num_gone>numGone_atSaturation()
					    		&&m.compoundJ<0 && mIndex.compoundJ<0
					    		&&mIndex.getName().equals("Sodium-Ion")
					    		&&m.getName().equals("Chlorine-Ion")){
							float dif =(float) (PBox2D.scalarWorldToPixels((float) Math.sqrt(dis)) - Molecule.clRadius*2);
						    if (dif<2){
					    		joint2Ions(index,i,mIndex,m);
					    		num_gone--;
					    		System.out.println("num_gone is "+num_gone);
					    	}
					    }
					    
						if (mul<0){
							mIndex.sumForceX[e] += mul*forceX;
							mIndex.sumForceY[e] += mul*forceY;
						}
						else if (mul>0){
							mIndex.sumForceX[e] += mul*forceX*mIndex.chargeRate;
							mIndex.sumForceY[e] += mul*forceY*mIndex.chargeRate;
						}
					}
				}
			}
		}
	}
	
	public static void computeForceKCl(int index, Molecule mIndex) { 
		for (int e = 0; e < mIndex.getNumElement(); e++) {
			int indexCharge = mIndex.elementCharges.get(e);
			Vec2 locIndex = mIndex.getElementLocation(e);
			mIndex.sumForceWaterX[e]=0;
			mIndex.sumForceWaterY[e]=0;
			mIndex.sumForceX[e]=0;
			mIndex.sumForceY[e]=0;
			for (int i = 0; i < molecules.size(); i++) {
				if (i==index)
					continue;
				Molecule m = molecules.get(i);
				float forceX;
				float forceY;
				for (int e2 = 0; e2 < m.getNumElement(); e2++) {
					Vec2 loc = m.getElementLocation(e2);
					float x = locIndex.x-loc.x;
					float y = locIndex.y-loc.y;
					float dis = x*x +y*y;
					forceX =  (float) ((x/Math.pow(dis,1.5))*40);
					forceY =  (float) ((y/Math.pow(dis,1.5))*40);
					
					int charge = m.elementCharges.get(e2);
					int mul = charge*indexCharge;
					if (m.getName().equals("Water")){
						float r = 0.002f+temp/10000;
						if (mIndex.compoundJ>=0){
							forceX *=r;
							forceY *=r;
						}
						else{
							forceX *=0.10;
							forceY *=0.10;
						}
						if (temp>=100){
							forceX=0;
							forceY=0;
						}
						if (mul<0){
							mIndex.sumForceWaterX[e] += mul*forceX;
							mIndex.sumForceWaterY[e] += mul*forceY;
						}
						else if (mul>0){
							mIndex.sumForceWaterX[e] += mul*forceX*mIndex.chargeRate;
							mIndex.sumForceWaterY[e] += mul*forceY*mIndex.chargeRate;
						}
					}
					else{
						if (mIndex.compoundJ<0){             //Compute IonDis
							float dis2 =(float) (PBox2D.scalarWorldToPixels((float) Math.sqrt(dis)));
							if (mIndex.ionDis==0)
								mIndex.ionDis = dis2;
							else{
								if (dis2<mIndex.ionDis){
									mIndex.ionDis =dis2;
								}
							}
						}
						
						if ((m.compoundJ<0 || mIndex.compoundJ<0)&& 0<temp && temp<100){
							forceX *=0.05f;
							forceY *=0.05f;
						}
						if (num_gone>numGone_atSaturation()
					    		&&m.compoundJ<0 && mIndex.compoundJ<0
					    		&&mIndex.getName().equals("Potassium-Ion")
					    		&&m.getName().equals("Chlorine-Ion")){
							float dif =(float) (PBox2D.scalarWorldToPixels((float) Math.sqrt(dis)) - Molecule.clRadius*2);
						    if (dif<2){
					    		joint2Ions(index,i,mIndex,m);
					    		num_gone--;
					    		System.out.println("num_gone is "+num_gone);
					    	}
					    }
					    
						if (mul<0){
							mIndex.sumForceX[e] += mul*forceX;
							mIndex.sumForceY[e] += mul*forceY;
						}
						else if (mul>0){
							mIndex.sumForceX[e] += mul*forceX*mIndex.chargeRate;
							mIndex.sumForceY[e] += mul*forceY*mIndex.chargeRate;
						}
					}
				}
			}
		}
	}


	public static void jointCaCl(int index1, int index2, int index3, Molecule m1, Molecule m2, Molecule m3) { // draw background
		DistanceJointDef djd = new DistanceJointDef();
		djd.bodyA = m3.body;
		djd.bodyB = m1.body;
		djd.length =PBox2D.scalarPixelsToWorld(Molecule.clRadius*4);
		djd.dampingRatio = 0.f;
		djd.frequencyHz = 100.0f;
		DistanceJoint dj = (DistanceJoint) PBox2D.world.createJoint(djd);
		m3.compoundJ = index1;
		m3.compoundJoints = dj;
		
		djd.bodyA = m1.body;
		djd.bodyB = m2.body;
		djd.length =PBox2D.scalarPixelsToWorld(Molecule.clRadius*2);
		djd.dampingRatio = 0.f;
		djd.frequencyHz = 60.0f;
		dj = (DistanceJoint) PBox2D.world.createJoint(djd);
		m1.compoundJ = index2;
		m1.compoundJoints = dj;
		
		djd.bodyA = m2.body;
		djd.bodyB = m3.body;
		djd.length =PBox2D.scalarPixelsToWorld(Molecule.clRadius*2);
		djd.dampingRatio = 0.f;
		djd.frequencyHz = 60.0f;
		dj = (DistanceJoint) PBox2D.world.createJoint(djd);
		m2.compoundJ = index3;
		m2.compoundJoints = dj;
	}
		
	
	public static void computeCaClPartner(int index, Molecule mIndex) { // draw background
		int[] ClPartners = new int[2]; 
		ClPartners[0]=-1;
		ClPartners[1]=-1;
		if (temp<=0) return;
		Vec2 locIndex = mIndex.getElementLocation(0);
		for (int i = 0; i < molecules.size(); i++) {
			Molecule m = molecules.get(i);
			if (!m.getName().equals("Chlorine-Ion") )
				continue;
			if (i==mIndex.compoundJ || 
					(mIndex.compoundJ>0 && i==molecules.get(mIndex.compoundJ).compoundJ))
				continue;
			Vec2 loc = m.getElementLocation(0);
			if(loc==null || locIndex==null) continue;
			float x = locIndex.x-loc.x;
			float y = locIndex.y-loc.y;
		    float dis = x*x +y*y;
		    float dif =(float) (PBox2D.scalarWorldToPixels((float) Math.sqrt(dis)) - Molecule.clRadius*2);
		
		    if (mIndex.compoundJ<0){             //Compute IonDis
				float dis1 =(float) (PBox2D.scalarWorldToPixels((float) Math.sqrt(dis)));
				if (mIndex.ionDis==0)
					mIndex.ionDis = dis1;
				else{
					if (dis1<mIndex.ionDis){
						mIndex.ionDis =dis1;
					}
				}
			}
		    //Computer Ca Cl partner to form a compound
		    if (mIndex.compoundJ<0 && m.compoundJ<0 && dif<10){
				if (ClPartners[0]<0)
					ClPartners[0] = i;
				else if (ClPartners[1]<0)
					ClPartners[1] = i;
			}
		    if (dif<3){
				if (mIndex.compoundJ<0 || m.compoundJ<0 || mIndex.otherJ>=0) continue;
				
		    	//Joint CaCl with another CaCl
				DistanceJointDef djd = new DistanceJointDef();
				
				// Connect Na to Cl of another NaCl
				djd.bodyA = mIndex.body;
				djd.bodyB = m.body;
				djd.length =PBox2D.scalarPixelsToWorld(Molecule.clRadius*2);
				djd.dampingRatio = 0.f;
				djd.frequencyHz = 1000.0f;
				DistanceJoint dj = (DistanceJoint) PBox2D.world.createJoint(djd);
				mIndex.otherJ=i;
				mIndex.otherJoints=dj;
				
				// Connect Cl to Cl of another NaCl
				int anotherClIndex = m.compoundJ;
				Molecule anotherCl = molecules.get(anotherClIndex); 
				if (!anotherCl.getName().equals("Chlorine-Ion")){
					anotherClIndex = anotherCl.compoundJ;
					anotherCl = molecules.get(anotherClIndex);
				}	
				anotherCl.CaOtherJ = index;
				int clIndex1 =  mIndex.compoundJ;
				Molecule mCl1 = molecules.get(clIndex1);
				djd.bodyA = mCl1.body;
				djd.bodyB = anotherCl.body;
				djd.length =PBox2D.scalarPixelsToWorld((float) (Molecule.clRadius*Math.sqrt(40)));
				djd.dampingRatio = 0.f;
				djd.frequencyHz = 1000.0f;
				dj = (DistanceJoint) PBox2D.world.createJoint(djd);
				mCl1.otherJ=anotherClIndex;
				mCl1.otherJoints= dj;
				
				int clIndex2 =  molecules.get(mIndex.compoundJ).compoundJ;
				Molecule mCl2 = molecules.get(clIndex2);
				djd.bodyA = mCl2.body;
				djd.bodyB = anotherCl.body;
				djd.length =PBox2D.scalarPixelsToWorld((float) (Molecule.clRadius*Math.sqrt(40)));
				djd.dampingRatio = 0.f;
				djd.frequencyHz = 1000.0f;
				dj = (DistanceJoint) PBox2D.world.createJoint(djd);
				mCl2.otherJ=anotherClIndex;
				mCl2.otherJoints=dj;
			}
		}
		int index1 = ClPartners[0];
		int index3 = ClPartners[1];
		if (index1>=0 && index3>=0){
			Molecule m1 = molecules.get(index1);
			Molecule m3 = molecules.get(index3);
			if (num_gone>numGone_atSaturation() && mIndex.compoundJ<0 && m1.compoundJ<0 && m3.compoundJ<0){
				jointCaCl(index1, index, index3, m1, mIndex,m3);
				num_gone--;
				System.out.println("num_gone is "+num_gone);
			}
		}
	}	
		
	public static void computeForceCaCl(int index, Molecule mIndex) {
		mIndex.sumForceX[0] =0;
		mIndex.sumForceY[0] =0;
		for (int i = 0; i < molecules.size(); i++) {
			if (i==index)
				continue;
			Molecule m = molecules.get(i);
			Vec2 locIndex = mIndex.getElementLocation(0);
			Vec2 loc = m.getElementLocation(0);
			float x = locIndex.x-loc.x;
			float y = locIndex.y-loc.y;
			float dis3 = x*x +y*y;
			float dis = (float) Math.sqrt(dis3);
			dis3 = (float) Math.pow(dis3,1.5);
			float forceX=0;
			float forceY=0;
			if (mIndex.getName().equals("Calcium-Ion") ){
				if (m.getName().equals("Calcium-Ion")){
					if (PBox2D.scalarWorldToPixels(dis)<=Molecule.clRadius*4){
						forceX =  (x/dis3)*100;
						forceY =  (y/dis3)*100;
					}
					else{
						forceX =  (x/dis3)*3;
						forceY =  (y/dis3)*3;
					}
				}
				else if (m.getName().equals("Chlorine-Ion")){
					forceX =  -(x/dis3)*16;
					forceY =  -(y/dis3)*16;	
				}
			}	
			else if (mIndex.getName().equals("Chlorine-Ion") ){
				if (m.getName().equals("Chlorine-Ion")){
					if (PBox2D.scalarWorldToPixels(dis) <= Molecule.clRadius*2.8282){
						forceX =  (x/dis3)*100;
						forceY =  (y/dis3)*100;
					}
					else {
						forceX =  (x/dis3)*3;
						forceY =  (y/dis3)*3;
					}
				}
				else if (m.getName().equals("Calcium-Ion")){
					forceX = -(x/dis3)*24;
					forceY = -(y/dis3)*24;
				}
			}	
			if (mIndex.compoundJ<0 || m.compoundJ<0) {
				forceX *=0.11;
				forceY *=0.11;
			}
			mIndex.sumForceX[0] += forceX;
			mIndex.sumForceY[0] += forceY;
		}
		
			
	}
	
	public static void computeForceFromWater(int index, Molecule mIndex) {
		int numElements = mIndex.getNumElement();
		mIndex.sumForceWaterX = new float[numElements];
		mIndex.sumForceWaterY = new float[numElements];

		for (int e = 0; e < numElements; e++) {
			int indexCharge = mIndex.elementCharges.get(e);
			Vec2 locIndex = mIndex.getElementLocation(e);
			for (int i = 0; i < molecules.size(); i++) {
				if (i==index) continue;
				Molecule m = molecules.get(i);
				if (!m.getName().equals("Water"))
					continue;
				for (int e2 = 0; e2 < m.getNumElement(); e2++) {
					Vec2 loc = m.getElementLocation(e2);
					if(loc==null || locIndex==null) continue;
					float x = locIndex.x-loc.x;
					float y = locIndex.y-loc.y;
				    float dis = x*x +y*y;
					Vec2 normV = normalizeForce(new Vec2(x,y));

					int charge = m.elementCharges.get(e2);
					int mul = charge*indexCharge;
					float r = temp/100f;
					
					//if (m.elementNames.get(e2).equals("Oxygen")){
					//	if (mIndex.getName().equals("Sodium-Ion") && Main.selectedSet==7)
					//		r *=1.5; 
					//}
					
					if (mIndex.compoundJ>=0){
						mIndex.sumForceWaterX[e] += mul*(normV.x/dis)*0.1;
						mIndex.sumForceWaterY[e] += mul*(normV.y/dis)*0.1;
					}
					else{
						mIndex.sumForceWaterX[e] += mul*(normV.x/dis)*(3+r);
						mIndex.sumForceWaterY[e] += mul*(normV.y/dis)*(3+r);
					}
					
					if (temp>=100){
						mIndex.sumForceWaterX[e]=0;
						mIndex.sumForceWaterY[e]=0;
					}
				}
			}
		}
	}

	public static void removeCaOtherJ(Molecule mCl){
		if (mCl.CaOtherJ<0) return;
		Molecule mmm = molecules.get(mCl.CaOtherJ);
		
		if (mmm.otherJ>=0){
			DistanceJoint dj2 = mmm.otherJoints;
			PBox2D.world.destroyJoint(dj2);
			mmm.otherJoints = null;
			mmm.otherJ =-1;
		}
		if (mmm.compoundJ>=0) {
			Molecule m = molecules.get(mmm.compoundJ);
			if (m.otherJ>=0){
				DistanceJoint dj2 = m.otherJoints;
				PBox2D.world.destroyJoint(dj2);
				m.otherJoints = null;
				m.otherJ =-1;
			}
			
			if (m.compoundJ>=0) {
				m = molecules.get(m.compoundJ);
				if (m.otherJ>=0){
					DistanceJoint dj2 = m.otherJoints;
					PBox2D.world.destroyJoint(dj2);
					m.otherJoints = null;
					m.otherJ =-1;
				}
			}	
		}	
		
	}
	
	// Unit2 Set5
	public static void computeForceAceticAcid(int index, Molecule mIndex) { // draw background
		float xMul = 2f;
		for (int e = 0; e < mIndex.getNumElement(); e++) {
			int indexCharge = mIndex.elementCharges.get(e);
			Vec2 locIndex = mIndex.getElementLocation(e);
			mIndex.sumForceX[e] =0;
			mIndex.sumForceY[e] =0;
			for (int i = 0; i < molecules.size(); i++) {
				if (i==index)
					continue;
				Molecule m = molecules.get(i);
				float forceX;
				float forceY;
				for (int e2 = 0; e2 < m.getNumElement(); e2++) {
					Vec2 loc = m.getElementLocation(e2);
					if(loc==null || locIndex==null) continue;
					float x = locIndex.x-loc.x;
					float y = locIndex.y-loc.y;
				    float dis = x*x +y*y;
					forceX =  (float) ((x/Math.pow(dis,1.5))*0.1);
					forceY =  (float) ((y/Math.pow(dis,1.5))*0.1);
					if ((temp<=0) || (temp<mIndex.freezingTem && !m.getName().equals("Water"))){
						forceX *=40;
						forceY *=40;
					}
					int charge = m.elementCharges.get(e2);
					int mul = charge*indexCharge;
					if (mul<0){
						mIndex.sumForceX[e] +=mul*forceX;
						mIndex.sumForceY[e] +=mul*forceY;
						
					}
					else if (mul>0){
						mIndex.sumForceX[e] +=mul*forceX*mIndex.chargeRate;
						mIndex.sumForceY[e] +=mul*forceY*mIndex.chargeRate;
					}
				}
			}
		}
	}
	
	
	// Unit2 Set7
	public static void computeForceNaHCO3(int index, Molecule mIndex) { // draw background
		for (int e = 0; e < mIndex.getNumElement(); e++) {
			int indexCharge = mIndex.elementCharges.get(e);
			Vec2 locIndex = mIndex.getElementLocation(e);
			mIndex.sumForceX[e] =0;
			mIndex.sumForceY[e] =0;
			for (int i = 0; i < molecules.size(); i++) {
				Molecule m = molecules.get(i);
				if (i==index || i == mIndex.compoundJ || index==m.compoundJ     // No interMolecule force
						|| m.getName().equals("Water")) // No water attraction here   
					continue;
				
				float forceX;
				float forceY;
				for (int e2 = 0; e2 < m.getNumElement(); e2++) {
					Vec2 loc = m.getElementLocation(e2);
					if(loc==null || locIndex==null) continue;
					float x = locIndex.x-loc.x;
					float y = locIndex.y-loc.y;
				    float dis = x*x +y*y;
				    float dis3 = (float) Math.pow(dis, 1.5);
					forceX = (x/dis3)*20;
					forceY = (y/dis3)*20;
					
					if ((mIndex.compoundJ<0 || m.compoundJ<0) && 0<temp&& temp<100){   // Losing mIndex
						forceX *=0.05f;
						forceY *=0.05f;
					}
					if (mIndex.compoundJ<0){             //Compute IonDis
						float dis2 =(float) (PBox2D.scalarWorldToPixels((float) Math.sqrt(dis)));
						if (mIndex.ionDis==0)
							mIndex.ionDis = dis2;
						else{
							if (dis2<mIndex.ionDis){
								mIndex.ionDis =dis2;
							}
						}
					} 
					if (num_gone>numGone_atSaturation() // Recombine compound
				    		&&m.compoundJ<0 && mIndex.compoundJ<0
				    		&&mIndex.getName().equals("Bicarbonate")
				    		&&m.getName().equals("Sodium-Ion")){
						float dif =(float) (PBox2D.scalarWorldToPixels((float) Math.sqrt(dis)) - (Molecule.oRadius +34));
					    if (dif<2){
					    	Vec2 p1 = mIndex.body.getPosition();
					    	float a= mIndex.body.getAngle();
					    	float d = PBox2D.scalarPixelsToWorld(Molecule.oRadius +30);
							float x2 =  (float) (p1.x+d*Math.cos(a));
							float y2 =  (float) (p1.y+d*Math.sin(a));
					    	m.body.setTransform(new Vec2(x2,y2), 0);
				    		jointNaHCO3(index,i,mIndex,m);
				    		num_gone--;
				    		System.out.println("num_gone is "+num_gone);
				    	}
				    }
					
					int charge = m.elementCharges.get(e2);
					int mul = (int) Math.signum(charge*indexCharge);
					if (mul<0){
						mIndex.sumForceX[e] += mul*forceX;
						mIndex.sumForceY[e] += mul*forceY;
					}
					else if (mul>0){
						mIndex.sumForceX[e] += mul*forceX*mIndex.chargeRate;
						mIndex.sumForceY[e] += mul*forceY*mIndex.chargeRate;
					}
				}
				
			}
		}
	}
	
	//Compute saturation 
	public static float computeSat() {
		if (temp>99 || temp<0){
			return 0;
		}
		float r = (float) (temp/99.);
		float sat =0;
		if (Main.selectedSet==1 && Main.selectedSim<=3)
			sat= (35.7f + r*(39.9f-35.7f)); //Take number of Water to account
		else if (Main.selectedSet==2)
			sat= 0; 
		else if (Main.selectedSet==3)
			sat= 0; 
		else if (Main.selectedSet==4){
			if (0<temp && temp<=20){
				r = (float) (temp/20.);
				sat= (59.5f + r*(74.5f-59.5f)); 
			}
			if (20<temp && temp<=40){
				r = (float) ((temp-20)/20.);
				sat= (74.5f + r*(128f-74.5f)); 
			}
			if (40<temp && temp<=60){
				r = (float) ((temp-40)/20.);
				sat= (128f + r*(137f-128f)); 
			}
			if (60<temp && temp<=80){
				r = (float) ((temp-60)/20.);
				sat= (137f + r*(147f-137)); 
			}
			if (80<temp && temp<=100){
				r = (float) ((temp-80)/20.);
				sat= (147f + r*(159f-147f)); 
			}
		}	
		else if (Main.selectedSet==5){
			sat= 0; 
		}
		else if (Main.selectedSet==6){
			sat= 0; 
		}
		else if (Main.selectedSet==7)
			sat= (6.9f + r*(19.2f-6.9f));
		else if (Main.selectedSet==1 && Main.selectedSim==4)
			sat= (28f + r*(56.3f-28f));
		return sat*((float) numWater/water100mL);
	}
	
	
		
	
	public static void applyForceUnit2(int index, Molecule mIndex) { // draw background
		for (int e = 0; e < mIndex.getNumElement(); e++) {
			mIndex.addForce(new Vec2(mIndex.sumForceX[e],mIndex.sumForceY[e]), e);
			mIndex.addForce(new Vec2(mIndex.sumForceWaterX[e],mIndex.sumForceWaterY[e]), e);
		
			if (Main.selectedUnit==2 && Main.selectedSet==1){
				int num = mIndex.getNumElement();
				float fX =0;
				float fY =0;
				for (int i=0;i<num;i++){
					fX += mIndex.sumForceWaterX[i];
					fY += mIndex.sumForceWaterY[i];
				}
				float s = fX*fX + fY*fY;
				float f = (float) Math.sqrt(s);
				//System.out.println(" numGone_atSaturation():"+numGone_atSaturation()
				//		+" num_gone:"+num_gone+" "+f);
				if (num_gone<numGone_atSaturation() && mIndex.compoundJ>=0 && f>0.02){
					DistanceJoint dj1 = mIndex.compoundJoints;
					PBox2D.world.destroyJoint(dj1);
					mIndex.compoundJoints = null;
					Molecule m2  = molecules.get(mIndex.compoundJ);
					mIndex.compoundJ =-1;
					m2.compoundJ =-1;
					m2.compoundJoints = null;
					num_gone++;
					System.out.println("num_gone is "+num_gone);
				}
			}
			else if (Main.selectedUnit==2 && Main.selectedSet==4){
				float s = mIndex.sumForceWaterX[0]*mIndex.sumForceWaterX[0]
						+ mIndex.sumForceWaterY[0]*mIndex.sumForceWaterY[0];
				float f = (float) Math.sqrt(s);
				if (num_gone<numGone_atSaturation() &&  mIndex.compoundJ>=0 && f>0.02f){
					DistanceJoint dj1 = mIndex.compoundJoints;
					PBox2D.world.destroyJoint(dj1);
					mIndex.compoundJoints = null;
					int jIndex = mIndex.compoundJ;
					mIndex.compoundJ =-1;
					
					if (mIndex.otherJ>=0){
						DistanceJoint dj2 = mIndex.otherJoints;
						PBox2D.world.destroyJoint(dj2);
						mIndex.otherJoints = null;
						mIndex.otherJ =-1;
					}
					removeCaOtherJ(mIndex);
					
					
					Molecule m = molecules.get(jIndex);
					dj1 = m.compoundJoints;
					PBox2D.world.destroyJoint(dj1);
					m.compoundJoints = null;
					jIndex = m.compoundJ;
					m.compoundJ =-1;
					
					if (m.otherJ>=0){
						DistanceJoint dj2 = m.otherJoints;
						PBox2D.world.destroyJoint(dj2);
						m.otherJoints = null;
						m.otherJ =-1;
					}
					removeCaOtherJ(m);
					
					
					m = molecules.get(jIndex);
					dj1 = m.compoundJoints;
					PBox2D.world.destroyJoint(dj1);
					m.compoundJoints = null;
					m.compoundJ =-1;
					if (m.otherJ>=0){
						DistanceJoint dj2 = m.otherJoints;
						PBox2D.world.destroyJoint(dj2);
						m.otherJoints = null;
						m.otherJ =-1;
					}
					removeCaOtherJ(m);
					num_gone++;
					System.out.println("num_gone is "+num_gone);
				}	
			}
			else if (Main.selectedUnit==2 && Main.selectedSet==7){
				int num = mIndex.getNumElement();
				float fX =0;
				float fY =0;
				for (int i=0;i<num;i++){
					fX += mIndex.sumForceWaterX[i];
					fY += mIndex.sumForceWaterY[i];
					
				}
				float f = fX*fX + fY*fY;
				//System.out.println(" satNaHCO3():"+satNaHCO3()+" numNaHC03Gone:"+numNaHC03Gone);
				if (num_gone<numGone_atSaturation() && mIndex.compoundJ>=0 && f>0.01){
					
					Molecule mCa = mIndex;
					if (mIndex.getName().equals("Bicarbonate")){
						mCa = molecules.get(mIndex.compoundJ);
					}
					
					DistanceJoint dj1 = mCa.compoundJoints;
					PBox2D.world.destroyJoint(dj1);
					mCa.compoundJoints = null;
					Molecule mHCO3  = molecules.get(mCa.compoundJ);
					mCa.compoundJ =-1;
					mHCO3.compoundJ =-1;
					
					PrismaticJoint dj2 = mCa.compoundJoints2;
					PBox2D.world.destroyJoint(dj2);
					mCa.compoundJoints2 = null;
						
					num_gone++;
					System.out.println("num_gone is "+num_gone);
				}
			}
				
				
		}
	}

	public static void reset() { // draw background
		num_total=0;
		num_gone=0;
		numWater=0;
		Unit2.mToMass =10;
		if (Main.selectedSet==4)
			Unit2.mToMass =20;
	}
		
}
