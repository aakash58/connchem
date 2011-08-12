package view;

import static model.State.molecules;

import java.util.ArrayList;

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
	public static int num_remain =0;
	public static int num_gone =0;
	public static int numWater =0;
	public static int water100mL =25;
	public static int mToMass =10;
	
	
	
	public static void add2Ions(String ion1, String ion2, int count, PBox2D box2d_, P5Canvas parent_) {
		int numRow = (int) (Math.ceil(count/3.8)+1);
		Vec2 size1 = Molecule.getShapeSize(ion1, parent_);
		Vec2 size2 = Molecule.getShapeSize(ion2, parent_);
		for (int i=0;i<count;i++){
			float x1,y1,angle1;
			float x2,y2,angle2;
			int r = i%numRow;
			x1 = x + 50+ (i/numRow)*(size1.x+size2.x);
			x2 = x1 + size1.x;
			if ((r%2==1 )){
				float tmp=x1;
				x1=x2;
				x2=tmp;
			}
			y1 =y + 80-Boundary.difVolume+(i%numRow)*size1.y;
			y2=y1;
			angle1 = 0;
			angle2 = 0;
			molecules.add(new Molecule(x1, y1,ion1,box2d_, parent_,angle1));
			molecules.add(new Molecule(x2, y2,ion2,box2d_, parent_,angle2));
		}
	}
	
	public static void addSiliconDioxide(String compoundName_, int count, PBox2D box2d_, P5Canvas parent_) {
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
	
	
	
	public static void addSodiumBicarbonate(String compoundName_, int count, PBox2D box2d_, P5Canvas parent_) {
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
	
	public static void computeNaClPartner(int index, Molecule mIndex) { // draw background
		Vec2 locIndex = mIndex.getElementLocation(0);
		for (int i = 0; i < molecules.size(); i++) {
			Molecule m = molecules.get(i);
			if (!m.getName().equals("Chlorine-Ion") )
				continue;
			Vec2 loc = m.getElementLocation(0);
			if(loc==null || locIndex==null) continue;
			float x = locIndex.x-loc.x;
			float y = locIndex.y-loc.y;
		    float dis = x*x +y*y;
		    float dif =(float) (PBox2D.scalarWorldToPixels((float) Math.sqrt(dis)) - Molecule.clRadius*2);
		
		    if (dif<5){
		    	mIndex.NaClPartner = i;
		    	m.NaClPartner = index;
		    }

		}
	}	
	
	public static void computeForceSiliconDioxide(int index, Molecule mIndex) { // draw background
		float xMul = 1.4f;	
		for (int i=0; i<mIndex.getNumElement();i++){
			mIndex.faInternalX[i]=0;
			mIndex.faInternalY[i]=0;
			mIndex.frInternalX[i]=0;
			mIndex.frInternalY[i]=0;
			mIndex.faExternalX[i]=0;
			mIndex.faExternalY[i]=0;
			mIndex.frExternalX[i]=0;
			mIndex.frExternalY[i]=0;
		}
		for (int e = 0; e < mIndex.getNumElement(); e++) {
			int indexCharge = mIndex.elementCharges.get(e);
			Vec2 locIndex = mIndex.getElementLocation(e);
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
					Vec2 normV = normalizeForce(new Vec2(x,y));
					float fTemp = mIndex.freezingTem;
					float bTemp = mIndex.boilingTem;
					float	gravityY = (bTemp-temp)/(bTemp-fTemp);
					float	gravityX = gravityY*xMul;
					forceX =  (-normV.x/dis)*m.getMass()*mIndex.getMass()*gravityX*1000;
					forceY =  (-normV.y/dis)*m.getMass()*mIndex.getMass()*gravityY*1000;
					
					
					if (!mIndex.getName().equals("Water") && m.getName().equals("Water")){
						if (m.elementNames.get(e2).equals("Hydrogen")){
							float r = temp/100f;
							forceX*=0.2*r;
							forceY*=0.2*r;
						}
						else if (m.elementNames.get(e2).equals("Oxygen")){
							float r = temp/100f;
							forceX*=0.4*r;
							forceY*=0.4*r;
						}
						if (temp>=100){
							forceX=0;
							forceY=0;
						}
					}
					
					int charge = m.elementCharges.get(e2);
					int mulCharge = charge*indexCharge;
					if (mulCharge<0){
						if (!m.getName().equals(mIndex.getName())){
							mIndex.faExternalX[e] +=-mulCharge*forceX;
							mIndex.faExternalY[e] +=-mulCharge*forceY;
						}
						else if (m.getName().equals(mIndex.getName())){
							mIndex.faInternalX[e] +=-mulCharge*forceX;
							mIndex.faInternalY[e] +=-mulCharge*forceY;
						}
					}
					else if (mulCharge>0){
						if (!m.getName().equals(mIndex.getName())){
							mIndex.frExternalX[e] +=-mulCharge*forceX*mIndex.chargeRate;
							mIndex.frExternalY[e] +=-mulCharge*forceY*mIndex.chargeRate;
						}
						else if (m.getName().equals(mIndex.getName())){
							mIndex.frInternalX[e] +=-mulCharge*forceX*mIndex.chargeRate;
							mIndex.frInternalY[e] +=-mulCharge*forceY*mIndex.chargeRate;
						}
					}
				}
			}
		}
	}
	
	public static void computeForceGlycerol(int index, Molecule mIndex) { // draw background
		float xMul = 1.4f;	
		for (int i=0; i<mIndex.getNumElement();i++){
			mIndex.faInternalX[i]=0;
			mIndex.faInternalY[i]=0;
			mIndex.frInternalX[i]=0;
			mIndex.frInternalY[i]=0;
			mIndex.faExternalX[i]=0;
			mIndex.faExternalY[i]=0;
			mIndex.frExternalX[i]=0;
			mIndex.frExternalY[i]=0;
		}
		
		for (int e = 0; e < mIndex.getNumElement(); e++) {
			int indexCharge = mIndex.elementCharges.get(e);
			Vec2 locIndex = mIndex.getElementLocation(e);
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
					Vec2 normV = normalizeForce(new Vec2(x,y));
					float fTemp = mIndex.freezingTem;
					float bTemp = mIndex.boilingTem;
					float	gravityY = (bTemp-temp)/(bTemp-fTemp);
					float	gravityX = gravityY*xMul;
					forceX =  (-normV.x/dis)*m.getMass()*mIndex.getMass()*gravityX*5;
					forceY =  (-normV.y/dis)*m.getMass()*mIndex.getMass()*gravityY*5;
					if (temp<fTemp){
						forceX *=90;
						forceY *=90;
					}
					
					if (!mIndex.getName().equals("Water") && m.getName().equals("Water")){
						if (m.elementNames.get(e2).equals("Hydrogen")){
							float r = temp/100f;
							forceX*=0.9*r;
							forceY*=0.9*r;
						}
						else if (m.elementNames.get(e2).equals("Oxygen")){
							float r = temp/100f;
							forceX*=0.19*r;
							forceY*=0.19*r;
						}
						if (temp>=100){
							forceX=0;
							forceY=0;
						}
						else if (temp<=0){
							forceX=-forceX;
							forceY=-forceY;
						}
					}
					
					int charge = m.elementCharges.get(e2);
					int mulCharge = charge*indexCharge;
					if (mulCharge<0){
						if (!m.getName().equals(mIndex.getName())){
							mIndex.faExternalX[e] +=-mulCharge*forceX;
							mIndex.faExternalY[e] +=-mulCharge*forceY;
						}
						else if (m.getName().equals(mIndex.getName())){
							mIndex.faInternalX[e] +=-mulCharge*forceX;
							mIndex.faInternalY[e] +=-mulCharge*forceY;
						}
					}
					else if (mulCharge>0){
						if (!m.getName().equals(mIndex.getName())){
							mIndex.frExternalX[e] +=-mulCharge*forceX*mIndex.chargeRate;
							mIndex.frExternalY[e] +=-mulCharge*forceY*mIndex.chargeRate;
						}
						else if (m.getName().equals(mIndex.getName())){
							mIndex.frInternalX[e] +=-mulCharge*forceX*mIndex.chargeRate;
							mIndex.frInternalY[e] +=-mulCharge*forceY*mIndex.chargeRate;
						}
					}
				}
			}
		}
	}
	
	public static void computeForceNaCl(int index, Molecule mIndex) { // draw background
		float xMul = 1.4f;	
		for (int i=0; i<mIndex.getNumElement();i++){
			mIndex.faInternalX[i]=0;
			mIndex.faInternalY[i]=0;
			mIndex.frInternalX[i]=0;
			mIndex.frInternalY[i]=0;
			mIndex.faExternalX[i]=0;
			mIndex.faExternalY[i]=0;
			mIndex.frExternalX[i]=0;
			mIndex.frExternalY[i]=0;
		}
		for (int e = 0; e < mIndex.getNumElement(); e++) {
			int indexCharge = mIndex.elementCharges.get(e);
			Vec2 locIndex = mIndex.getElementLocation(e);
			for (int i = 0; i < molecules.size(); i++) {
				if (i==index)
					continue;
				Molecule m = molecules.get(i);
				
				float rate =1f;
				if (m.isGone) {
					float r = temp/99f;
					r = r*0.1f;
					rate =0.1f-r;
					if (temp>=100) rate=1;
				}	
				else if (mIndex.isGone && !m.getName().equals("Water")) {
					float r = temp/99f;
					r = r*0.4f;
					rate =0.4f-r;
					if (temp>=100) rate=1;
					
				}
				if (m.getName().equals("Water") && m.waterPartner!=index) {
					continue;
				}
				
				float forceX;
				float forceY;
				for (int e2 = 0; e2 < m.getNumElement(); e2++) {
					Vec2 loc = m.getElementLocation(e2);
					if(loc==null || locIndex==null) continue;
					float x = locIndex.x-loc.x;
					float y = locIndex.y-loc.y;
				    float dis = x*x +y*y;
					Vec2 normV = normalizeForce(new Vec2(x,y));
					float fTemp = mIndex.freezingTem;
					float bTemp = mIndex.boilingTem;
					float	gravityY = (bTemp-temp)/(bTemp-fTemp);
					float	gravityX = gravityY*xMul;
					forceX =  (-normV.x/dis)*m.getMass()*mIndex.getMass()*gravityX*30000;
					forceY =  (-normV.y/dis)*m.getMass()*mIndex.getMass()*gravityY*30000;
					
					int charge = m.elementCharges.get(e2);
					if (!mIndex.getName().equals("Water") && m.getName().equals("Water")){
						if (m.elementNames.get(e2).equals("Hydrogen")){
							float r = temp/100f;
							forceX*=0.19*4*r;
							forceY*=0.19*4*r;
						}
						else if (m.elementNames.get(e2).equals("Oxygen")){
							float r = temp/100f;
							forceX*=0.36*4*r;
							forceY*=0.36*4*r;
						}
						if (temp>=100){
							forceX=0;
							forceY=0;
						}
					}
					
					forceX *=rate;
					forceY *=rate;
					if (charge*indexCharge<0){
						if (!m.getName().equals(mIndex.getName())){
							mIndex.faExternalX[e] +=forceX;
							mIndex.faExternalY[e] +=forceY;
						}
						else if (m.getName().equals(mIndex.getName())){
							mIndex.faInternalX[e] +=forceX;
							mIndex.faInternalY[e] +=forceY;
						}
					}
					else if (charge*indexCharge>0){
						if (!m.getName().equals(mIndex.getName())){
							mIndex.frExternalX[e] +=-forceX*mIndex.chargeRate;
							mIndex.frExternalY[e] +=-forceY*mIndex.chargeRate;
						}
						else if (m.getName().equals(mIndex.getName())){
							mIndex.frInternalX[e] +=-forceX*mIndex.chargeRate;
							mIndex.frInternalY[e] +=-forceY*mIndex.chargeRate;
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
		mIndex.ClPartners[0]=-1;
		mIndex.ClPartners[1]=-1;
		
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
		
		    //Computer Ca Cl partner to form a compound
		    if (mIndex.compoundJ<0 && m.compoundJ<0 && dif<10){
				if (mIndex.ClPartners[0]<0)
					mIndex.ClPartners[0] = i;
				else if (mIndex.ClPartners[1]<0)
					mIndex.ClPartners[1] = i;
			}
		
		    if (dif<5){
//		    	isCaClConnected  =true;
				//Joint CaCl with another CaCl
				if (mIndex.compoundJ<0 || m.compoundJ<0 
						|| mIndex.otherJ>=0)
					continue;
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
		int index1 = mIndex.ClPartners[0];
		int index3 = mIndex.ClPartners[1];
		if (index1>=0 && index3>=0){
			Molecule m1 = molecules.get(index1);
			Molecule m3 = molecules.get(index3);
			
			if (mIndex.compoundJ<0 && m1.compoundJ<0 && m3.compoundJ<0){
				jointCaCl(index1, index, index3, m1, mIndex,m3);
			}
		}
	}	
	
	public static boolean isContained(int e, ArrayList<Integer> a) {
		for (int i =0;i<a.size();i++){
			if (a.get(i)==e)
				return true;
		}
		return false;
	}
	public static int isIntersec(int[] a1, int[] a2) {
		for (int i =0;i<a1.length;i++){
			if (a1[i]<0) continue;
			for (int j =0;j<a2.length;j++){
				if (a1[i]==a2[j])
					return a1[i];
			}
		}
		return -1;
		
	}
		
	public static void computeForceCaCl(int index, Molecule mIndex) {
		for (int i=0; i<mIndex.getNumElement();i++){
			mIndex.faInternalX[i]=0;
			mIndex.faInternalY[i]=0;
			mIndex.frInternalX[i]=0;
			mIndex.frInternalY[i]=0;
			mIndex.faExternalX[i]=0;
			mIndex.faExternalY[i]=0;
			mIndex.frExternalX[i]=0;
			mIndex.frExternalY[i]=0;
		}
		if (mIndex.getName().equals("Calcium-Ion") ){
			for (int i = 0; i < molecules.size(); i++) {
				if (i==index)
					continue;
				Molecule m = molecules.get(i);
				Vec2 locIndex = mIndex.getElementLocation(0);
				Vec2 loc = m.getElementLocation(0);
				float x = locIndex.x-loc.x;
				float y = locIndex.y-loc.y;
				float dis2 = x*x +y*y;
				float dis = (float) Math.sqrt(dis2);
				Vec2 normV = normalizeForce(new Vec2(x,y));
				float forceX;
				float forceY;
				
				if (m.getName().equals("Calcium-Ion")){
					if (PBox2D.scalarWorldToPixels(dis)<=Molecule.clRadius*4){
						forceX =  (normV.x/dis2)*100;
						forceY =  (normV.y/dis2)*100;
					}
					else{
						forceX =  (normV.x/dis2)*5;
						forceY =  (normV.y/dis2)*5;
					}
					mIndex.frInternalX[0] +=forceX;
					mIndex.frInternalY[0] +=forceY;
			   }
				else if (m.getName().equals("Chlorine-Ion")){
					forceX =  (normV.x/dis2)*40;
					forceY =  (normV.y/dis2)*40;
					float r = (99-temp)/150f;
					if (0<temp && temp<100){
						forceX *=  r;
						forceY *=  r;
						if (mIndex.isGone){
							forceX *=  r;
							forceY *=  r;
						}
					}	
					else if (temp>=100){
						forceX /=2;
						forceY /=2;
					}
					
					mIndex.faExternalX[0] -=forceX;
					mIndex.faExternalY[0] -=forceY;
					
						
				}
			}	
		}
		else if (mIndex.getName().equals("Chlorine-Ion") ){
			for (int i = 0; i < molecules.size(); i++) {
				if (i==index)
					continue;
				Molecule m = molecules.get(i);
				Vec2 locIndex = mIndex.getElementLocation(0);
				Vec2 loc = m.getElementLocation(0);
				float x = locIndex.x-loc.x;
				float y = locIndex.y-loc.y;
				float dis2 = (x*x +y*y);
				float dis = (float) Math.sqrt(dis2);
				Vec2 normV = normalizeForce(new Vec2(x,y));
				float forceX;
				float forceY;
				
				if (m.getName().equals("Chlorine-Ion")){
					if (PBox2D.scalarWorldToPixels(dis) <= Molecule.clRadius*2.8285){
						forceX =  (normV.x/dis2)*60;
						forceY =  (normV.y/dis2)*60;
					}
					else {
						forceX =  (normV.x/dis2)*5;
						forceY =  (normV.y/dis2)*5;
					}
					mIndex.frInternalX[0] +=forceX;
					mIndex.frInternalY[0] +=forceY;
			    
				}
				else if (m.getName().equals("Calcium-Ion")){
					forceX =  (normV.x/dis2)*40;
					forceY =  (normV.y/dis2)*40;
					float r = (99-temp)/150f;
					if (0<temp && temp<100){
						forceX *=  r;
						forceY *=  r;
						if (mIndex.isGone){
							forceX *=  r;
							forceY *=  r;
						}
					}	
					else if (temp>=100){
						forceX /=2;
						forceY /=2;
					}
					mIndex.faExternalX[0] -=forceX;
					mIndex.faExternalY[0] -=forceY;
				}
			}	
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
					int sign = (int) Math.signum(charge*indexCharge);
					float r = temp/100f;
					if (m.elementNames.get(e2).equals("Hydrogen")){
					}
					else if (m.elementNames.get(e2).equals("Oxygen")){
						r *=2f;
						if (mIndex.getName().equals("Sodium-Ion") && Main.selectedSet==7)
							r *=1.5; 
					}
					if (mIndex.compoundJ<0){
						r =r*4;
					}
					mIndex.sumForceWaterX[e] += sign*(normV.x/dis)*(2f+1*r);
					mIndex.sumForceWaterY[e] += sign*(normV.y/dis)*(2f+1*r);
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
		for (int i=0; i<mIndex.getNumElement();i++){
			mIndex.faInternalX[i]=0;
			mIndex.faInternalY[i]=0;
			mIndex.frInternalX[i]=0;
			mIndex.frInternalY[i]=0;
			mIndex.faExternalX[i]=0;
			mIndex.faExternalY[i]=0;
			mIndex.frExternalX[i]=0;
			mIndex.frExternalY[i]=0;
		}
		
		for (int e = 0; e < mIndex.getNumElement(); e++) {
			int indexCharge = mIndex.elementCharges.get(e);
			Vec2 locIndex = mIndex.getElementLocation(e);
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
					Vec2 normV = normalizeForce(new Vec2(x,y));
					float fTemp = mIndex.freezingTem;
					float bTemp = mIndex.boilingTem;
					float	gravityY = (bTemp-temp)/(bTemp-fTemp);
					float	gravityX = gravityY*xMul;
					forceX =  (-normV.x/dis)*m.getMass()*mIndex.getMass()*gravityX*5;
					forceY =  (-normV.y/dis)*m.getMass()*mIndex.getMass()*gravityY*5;
					if (temp<fTemp){
						forceX *=500;
						forceY *=500;
					}
					
					if (!mIndex.getName().equals("Water") && m.getName().equals("Water")){
						if (temp>=100){
							forceX=0;
							forceY=0;
						}else if (temp<=0){
							
						}
						else {
							if (m.elementNames.get(e2).equals("Hydrogen")){
								float r = temp/100f;
								forceX*=r;
								forceY*=r;
							}
							else if (m.elementNames.get(e2).equals("Oxygen")){
								float r = temp/100f;
								forceX*=2*r;
								forceY*=2*r;
							}
						}
					}
					
					int charge = m.elementCharges.get(e2);
					int sign = (int) Math.signum(charge*indexCharge);
					if (sign<0){
						if (!m.getName().equals(mIndex.getName())){
							mIndex.faExternalX[e] +=forceX;
							mIndex.faExternalY[e] +=forceY;
						}
						else if (m.getName().equals(mIndex.getName())){
							mIndex.faInternalX[e] +=forceX;
							mIndex.faInternalY[e] +=forceY;
						}
					}
					else if (sign>0){
						if (!m.getName().equals(mIndex.getName())){
							mIndex.frExternalX[e] -=forceX*mIndex.chargeRate;
							mIndex.frExternalY[e] -=forceY*mIndex.chargeRate;
						}
						else if (m.getName().equals(mIndex.getName())){
							mIndex.frInternalX[e] -=forceX*mIndex.chargeRate;
							mIndex.frInternalY[e] -=forceY*mIndex.chargeRate;
						}
					}
				}
			}
		}
	}
	
	
	// Unit2 Set7
	public static void computeForceNaHCO3(int index, Molecule mIndex) { // draw background
		for (int i=0; i<mIndex.getNumElement();i++){
			mIndex.faInternalX[i]=0;
			mIndex.faInternalY[i]=0;
			mIndex.frInternalX[i]=0;
			mIndex.frInternalY[i]=0;
			mIndex.faExternalX[i]=0;
			mIndex.faExternalY[i]=0;
			mIndex.frExternalX[i]=0;
			mIndex.frExternalY[i]=0;
		}
		
		for (int e = 0; e < mIndex.getNumElement(); e++) {
			int indexCharge = mIndex.elementCharges.get(e);
			Vec2 locIndex = mIndex.getElementLocation(e);
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
					Vec2 normV = normalizeForce(new Vec2(x,y));
					forceX =  (-normV.x/dis)*20;
					forceY =  (-normV.y/dis)*20;
					
					if (mIndex.compoundJ<0 || m.compoundJ<0){   // Losing mIndex
						forceX *=0.10f;
						forceY *=0.10f;
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
					
					int charge = m.elementCharges.get(e2);
					int sign = (int) Math.signum(charge*indexCharge);
					if (sign<0){
						if (!m.getName().equals(mIndex.getName())){
							mIndex.faExternalX[e] +=forceX;
							mIndex.faExternalY[e] +=forceY;
						}
						else if (m.getName().equals(mIndex.getName())){
							mIndex.faInternalX[e] +=forceX;
							mIndex.faInternalY[e] +=forceY;
						}
					}
					else if (sign>0){
						if (!m.getName().equals(mIndex.getName())){
							mIndex.frExternalX[e] -=forceX*mIndex.chargeRate;
							mIndex.frExternalY[e] -=forceY*mIndex.chargeRate;
						}
						else if (m.getName().equals(mIndex.getName())){
							mIndex.frInternalX[e] -=forceX*mIndex.chargeRate;
							mIndex.frInternalY[e] -=forceY*mIndex.chargeRate;
						}
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
		float sat =-1;
		if (Main.selectedSet==1 && Main.selectedSim<=3)
			sat= (35.7f + r*(39.9f-35.7f)); //Take number of Water to account
		else if (Main.selectedSet==2)
			sat= 0; 
		else if (Main.selectedSet==3)
			sat= 0; 
		else if (Main.selectedSet==4){
			sat= (59.5f + r*(159f-59.5f)); 
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
			mIndex.sumForceX[e] = mIndex.faInternalX[e] +mIndex.faExternalX[e]+mIndex.frInternalX[e]+mIndex.frExternalX[e];
			mIndex.sumForceY[e] = mIndex.faInternalY[e] +mIndex.faExternalY[e]+mIndex.frInternalY[e]+mIndex.frExternalY[e];
			mIndex.addForce(new Vec2(mIndex.sumForceX[e],mIndex.sumForceY[e]), e);
			mIndex.addForce(new Vec2(mIndex.sumForceWaterX[e],mIndex.sumForceWaterY[e]), e);
		
			if (Main.selectedUnit==2 && Main.selectedSet==4){
				float s = mIndex.sumForceWaterX[0]*mIndex.sumForceWaterX[0]
						+ mIndex.sumForceWaterY[0]*mIndex.sumForceWaterY[0];
				float f = (float) Math.sqrt(s);
				if (f>0.5f &&  mIndex.compoundJ>=0){
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
				float s = fX*fX + fY*fY;
				float f = (float) Math.sqrt(s);
				//System.out.println(" satNaHCO3():"+satNaHCO3()+" numNaHC03Gone:"+numNaHC03Gone);
				if (num_gone<numGone_atSaturation() && mIndex.compoundJ>=0 && f>2){
					
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
						
					num_remain--;
					num_gone++;
				}
			}
				
				
		}
	}

	public static void reset() { // draw background
		num_total=0;
		num_gone=0;
		num_remain=0;
		numWater=0;
	}
		
}
