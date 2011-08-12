package view;

import static model.State.molecules;
import static view.P5Canvas.*;
import main.Main;
import org.jbox2d.common.Vec2;

public class Water {

	public static void computeWaterPartner(int index, Molecule mIndex) { // draw background
		mIndex.isGone =false;
		if (temp<=0) return;
		for (int e = 0; e < mIndex.getNumElement(); e++) {
			Vec2 locIndex = mIndex.getElementLocation(e);
			for (int i = 0; i < molecules.size(); i++) {
				if (i==index)
					continue;
				Molecule m = molecules.get(i);
				for (int e2 = 0; e2 < m.getNumElement(); e2++) {
					Vec2 loc = m.getElementLocation(e2);
					if(loc==null || locIndex==null) continue;
					float x = locIndex.x-loc.x;
					float y = locIndex.y-loc.y;
				    float dis = x*x +y*y;
					if (!mIndex.getName().equals("Water")
							&& m.getName().equals("Water")){
						float sum = (mIndex.circles[e][0]+m.circles[e2][0]);
						float dif =(float) (PBox2D.scalarWorldToPixels((float) Math.sqrt(dis))
								-sum);
						if (dif<12 && m.waterPartner<0){
							mIndex.isGone =true;
							mIndex.waterPartner = i;
							m.waterPartner = index;
						}
					}
				}	
			}
		}
	}	
	public static void setForceWater(int indexWater, Molecule mWater) { // draw background
		if (temp<=0){
			for (int i=0; i<mWater.getNumElement();i++){
				mWater.faInternalX[i]=0;
				mWater.faInternalY[i]=0;
				mWater.frInternalX[i]=0;
				mWater.frInternalY[i]=0;
				mWater.faExternalX[i]=0;
				mWater.faExternalY[i]=0;
				mWater.frExternalX[i]=0;
				mWater.frExternalY[i]=0;
			}
			for (int e = 0; e < mWater.getNumElement(); e++) {
				float sumForceX=0;
				float sumForceY=0;
				int indexCharge = mWater.elementCharges.get(e);
				Vec2 locIndex = mWater.getElementLocation(e);
				for (int i = 0; i < molecules.size(); i++) {
					if (i==indexWater)
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
						forceX =  (-normV.x/dis)*m.getMass()*mWater.getMass()*9000;
						forceY =  (-normV.y/dis)*m.getMass()*mWater.getMass()*9000;
						if (m.getName().equals("Silicon-Dioxide")){
							forceX *=0.1;
							forceY *=0.1;
						}
						else if (m.getName().equals("Glycerol")){
							forceX *=0.08;
							forceY *=0.08;
						}
						else if (m.getName().equals("Acetic-Acid")){
							forceX *=0.2;
							forceY *=0.2;
						}
						else if (m.getName().equals("Pentane")){
							forceX *=0.08;
							forceY *=0.08;
						}
						
						int charge = m.elementCharges.get(e2);
						if (charge*indexCharge<0){
							if (!m.getName().equals(mWater.getName())){
								mWater.faExternalX[e] +=forceX;
								mWater.faExternalY[e] +=forceY;
							}
							else if (m.getName().equals(mWater.getName())){
								mWater.faInternalX[e] +=forceX;
								mWater.faInternalY[e] +=forceY;
							}
							sumForceX += forceX;
							sumForceY += forceY;
						}
						else if (charge*indexCharge>0){
							if (!m.getName().equals(mWater.getName())){
								mWater.frExternalX[e] +=-forceX*mWater.chargeRate;
								mWater.frExternalY[e] +=-forceY*mWater.chargeRate;
							}
							else if (m.getName().equals(mWater.getName())){
								mWater.frInternalX[e] +=-forceX*mWater.chargeRate;
								mWater.frInternalY[e] +=-forceY*mWater.chargeRate;
							}
							sumForceX += -forceX*mWater.chargeRate;
							sumForceY += -forceY*mWater.chargeRate;
						}
					}
				}
				mWater.addForce(new Vec2(sumForceX,sumForceY), e);
			}
		
		}
		else if (Main.selectedUnit==1 && Main.selectedSim!=3){
			for (int i = 0; i < molecules.size(); i++) {
				if (i==indexWater)
					continue;
				Molecule m = molecules.get(i);
				Vec2 loc = m.getPosition();
				Vec2 locIndex = mWater.getPosition();
				if(loc==null || locIndex==null) continue;
				float x = locIndex.x-loc.x;
				float y = locIndex.y-loc.y;
			   float dis = x*x +y*y;
				Vec2 normV = normalizeForce(new Vec2(x,y));
				float forceX;
				float forceY;
				if (mWater.polarity==m.polarity){
					float fTemp = mWater.freezingTem;
					float bTemp = mWater.boilingTem;
					float gravityX,gravityY;
					if (temp>=bTemp){
						gravityX = 0;
						gravityY = 0;
					}
					else if (temp<=fTemp){
						gravityY = (bTemp-temp)/(bTemp-fTemp);
						gravityX = gravityY*2f;
					}	
					else{
						gravityY = (bTemp-temp)/(bTemp-fTemp);
						gravityX = gravityY*0.6f;
					}	
					forceX =  (-normV.x/dis)*m.getMass()*mWater.getMass()*gravityX*3000;
					forceY =  (-normV.y/dis)*m.getMass()*mWater.getMass()*gravityY*3000;
				}	
				else{
					float num = m.getNumElement();
					forceX =  (normV.x/dis)*m.getMass()*mWater.getMass()*300*num;
					forceY =  (normV.y/dis)*m.getMass()*mWater.getMass()*300*num;
				}
				mWater.addForce(new Vec2(forceX,forceY));
			}
		}
		else {
			for (int e = 0; e < mWater.getNumElement(); e++) {
				float sumForceX=0;
				float sumForceY=0;
				int indexCharge = mWater.elementCharges.get(e);
				Vec2 locIndex = mWater.getElementLocation(e);
				for (int i = 0; i < molecules.size(); i++) {
					Molecule m = molecules.get(i);
					if (m.getName().equals("Water"))
						continue;
					float forceX;
					float forceY;
					for (int e2 = 0; e2 < m.getNumElement(); e2++) {
						Vec2 loc = m.getElementLocation(e2);
						float x = locIndex.x-loc.x;
						float y = locIndex.y-loc.y;
					    float dis = x*x +y*y;
						forceX =  (float) (x/Math.pow(dis,1.5));
						forceY =  (float) (y/Math.pow(dis,1.5));
						int charge = m.elementCharges.get(e2);
						/*if (m.getName().equals("Chlorine-Ion") || m.getName().equals("Sodium-Ion")){
							forceX *=2;
							forceY *=2;
						}
						else if (m.getName().equals("Silicon-Dioxide")){
							forceX *=1f;
							forceY *=1f;
						}
						else if (m.getName().equals("Bicarbonate")){
							forceX *=2;
							forceY *=2;
						}*/
							
						if (charge*indexCharge<0){
							sumForceX -= forceX;
							sumForceY -= forceY;
						}
						else if (charge*indexCharge>0){
							sumForceX += forceX*mWater.chargeRate;
							sumForceY += forceY*mWater.chargeRate;
						}
					}
				}
				mWater.addForce(new Vec2(sumForceX,sumForceY), e);
			}
		}
	}
}
