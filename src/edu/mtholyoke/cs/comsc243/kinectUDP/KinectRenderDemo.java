package edu.mtholyoke.cs.comsc243.kinectUDP;

import java.io.IOException;
import java.util.Random;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

/**
 * @author eitan
 *
 */
public class KinectRenderDemo extends PApplet {

	KinectBodyDataProvider kinectReader;
	public static float PROJECTOR_RATIO = 1080f/1920.0f;
	PImage img;
	PImage bg;
	
	
	Long id1;
	Long id2;
	
	Integer[][] randIdx=new Integer[2][3];
	Integer[][] randLine=new Integer[2][2];
	Integer[] CHL=new Integer[2];
	
	PVector hand00;
	PVector hand01;
	PVector hand10;
	PVector hand11;
	int delay=1000;
	
	PVector connector0;
	PVector connector1;
	
	public void createWindow(boolean useP2D, boolean isFullscreen, float windowsScale) {
		if (useP2D) {
			if(isFullscreen) {
				fullScreen(P2D);  			
			} else {
				size((int)(1920 * windowsScale), (int)(1080 * windowsScale), P2D);
			}
		} else {
			if(isFullscreen) {
				fullScreen();  			
			} else {
				size((int)(1920 * windowsScale), (int)(1080 * windowsScale));
			}
		}		
	}
	
	// use lower numbers to zoom out (show more of the world)
	// zoom of 1 means that the window is 2 meters wide and appox 1 meter tall.
	public void setScale(float zoom) {
		scale(zoom* width/2.0f, zoom * -width/2.0f);
		translate(1f/zoom , -PROJECTOR_RATIO/zoom );		
	}

	public void settings() {
		createWindow(true, true, .5f);
		bg=loadImage("background2.png");
		img=loadImage("star1.png");
	}

	public void setup(){

//		try {
//			kinectReader = new KinectBodyDataProvider("test3.kinect", 5);
//		} catch (IOException e) {
//			System.out.println("Unable to creat e kinect producer");
//		}
		kinectReader = new KinectBodyDataProvider(8008);
		kinectReader.start();
	}
	public void draw(){
		setScale(.5f);
		
		noStroke();



		background(bg);
		
		KinectBodyData bodyData = kinectReader.getMostRecentData();
		
		int count=bodyData.getPersonCount();
		
		
		
		if(count>1){
			Body person1=bodyData.getPerson(0);
			Body person2=bodyData.getPerson(1);
			
			Long temp1=person1.getId();
			Long temp2=person2.getId();
			
			if(id1==null && id2!=temp1){
				id1=temp1;
			}
			if(id2==null && id1!=temp2){
				id2=temp2;
			}
			
			
			if(id1!=null && id2!=null){
				for(int i=0;i<count;i++){
					drawHoomans(i, bodyData);
				}
			}
			
		}


	}

	/**
	 * Draws an ellipse in the x,y position of the vector (it ignores z).
	 * Will do nothing is vec is null.  This is handy because get joint 
	 * will return null if the joint isn't tracked. 
	 * @param vec
	 */
	public void drawIfValid(PVector vec) {
		if(vec != null) {
			ellipse(vec.x, vec.y, .1f,.1f);
		}

	}
	
	public void drawHoomans(int i, KinectBodyData bodyData){

		Body person = bodyData.getPerson(i);
		
		if(person.getId()==id1 || person.getId()==id2){
			
			
			PVector[] Top=new PVector[5];
			
			Top[0]=person.getJoint(Body.HEAD);
			Top[1]=person.getJoint(Body.NECK);
			Top[2]=person.getJoint(Body.SHOULDER_LEFT);
			Top[3]=person.getJoint(Body.SHOULDER_RIGHT);
			Top[4]=person.getJoint(Body.SPINE_SHOULDER);
			
			PVector[] Mid=new PVector[14];
			
			Mid[0]=person.getJoint(Body.SPINE_BASE);
			Mid[1]=person.getJoint(Body.SPINE_MID);
			Mid[2]=person.getJoint(Body.THUMB_LEFT);
			Mid[3]=person.getJoint(Body.THUMB_RIGHT);
			Mid[4]=person.getJoint(Body.WRIST_LEFT);
			Mid[5]=person.getJoint(Body.WRIST_RIGHT);
			Mid[6]=person.getJoint(Body.ELBOW_LEFT);
			Mid[7]=person.getJoint(Body.ELBOW_RIGHT);
			Mid[8]=person.getJoint(Body.HAND_LEFT);
			Mid[9]=person.getJoint(Body.HAND_RIGHT);
			Mid[10]=person.getJoint(Body.HAND_TIP_LEFT);
			Mid[11]=person.getJoint(Body.HAND_TIP_RIGHT);
			Mid[12]=person.getJoint(Body.HIP_LEFT);
			Mid[13]=person.getJoint(Body.HIP_RIGHT);
			
			PVector[] Bottom=new PVector[6];
			
			Bottom[0]=person.getJoint(Body.ANKLE_LEFT);
			Bottom[1]=person.getJoint(Body.ANKLE_RIGHT);
			Bottom[2]=person.getJoint(Body.FOOT_LEFT);
			Bottom[3]=person.getJoint(Body.FOOT_RIGHT);
			Bottom[4]=person.getJoint(Body.KNEE_LEFT);
			Bottom[5]=person.getJoint(Body.KNEE_RIGHT);
			
			randomTester(Top, Mid, Bottom, person);
			
			if((gradient(Mid[6],Mid[8])*gradient(Mid[7], Mid[9])<0) && dist(Mid[8].x, Mid[8].y, Mid[9].x, Mid[9].y)<=0.06){
				plotLine(Top, Mid, Bottom, person);
			}
			if(person.getId()==id1){
				hand00=Mid[8];
				hand01=Mid[9];
				int randPart=new Random().nextInt(3);
				
				if(randPart==0){
					connector0=Top[randIdx[0][0]];
				}
				else if(randPart==1){
					connector0=Mid[randIdx[0][1]];
				}
				else if(randPart==2){
					connector0=Bottom[randIdx[0][2]];
				}
			}
			else if(person.getId()==id2){
				hand10=Mid[8];
				hand11=Mid[9];
				
				int randPart=new Random().nextInt(3);
				
				if(randPart==0){
					connector0=Top[randIdx[1][0]];
				}
				else if(randPart==1){
					connector0=Mid[randIdx[1][1]];
				}
				else if(randPart==2){
					connector0=Bottom[randIdx[1][2]];
				}
			}
			
			if(hand00!=null && hand11!=null){
				if(dist(hand00.x, hand00.y, hand11.x, hand11.y)<=0.06){
					simpleLinePlot(connector0,connector1);
				}
			}
			if(hand01!=null && hand10!=null){
				if(dist(hand01.x, hand01.y, hand10.x, hand10.y)<=0.06){
					simpleLinePlot(connector0, connector1);
				}
			}
			

			
		}

	}
	public void randomTester(PVector[] Top, PVector[] Mid, PVector[] Bot, Body person){
		int idIdx=0;
		if(person.getId()==id1){
			idIdx=0;
		}
		else if(person.getId()==id2){
			idIdx=1;
		}
		
		
		if(randIdx[idIdx][0]!=null && Top[randIdx[idIdx][0]]!=null){
			plotImage(Top[randIdx[idIdx][0]]);
		}
		else{
			int randVal=new Random().nextInt(Top.length);
			long start=System.currentTimeMillis();
			while(Top[randVal]==null){
				randVal=new Random().nextInt(Top.length);
				
				if(System.currentTimeMillis()-start>delay){
					break;
				}
			}
			randIdx[idIdx][0]=randVal;
		}
		
		if(randIdx[idIdx][1]!=null && Mid[randIdx[idIdx][1]]!=null){
			plotImage(Mid[randIdx[idIdx][1]]);
		}
		else{
			int randVal=new Random().nextInt(Mid.length);
			long start=System.currentTimeMillis();
			while(Mid[randVal]==null){
				randVal=new Random().nextInt(Mid.length);
				
				if(System.currentTimeMillis()-start>delay){
					break;
				}
			}
			randIdx[idIdx][1]=randVal;
		}
		
		if(randIdx[idIdx][2]!=null && Bot[randIdx[idIdx][2]]!=null){
			plotImage(Bot[randIdx[idIdx][2]]);
		}
		else{
			int randVal=new Random().nextInt(Bot.length);
			long start=System.currentTimeMillis();
			while(Bot[randVal]==null){
				randVal=new Random().nextInt(Bot.length);
				
				if(System.currentTimeMillis()-start>delay){
					break;
				}
			}
			randIdx[idIdx][2]=randVal;
		}
	}
		

	
	public void plotImage(PVector v){
		
		if(v!=null){
			imageMode(CENTER);
			image(img,v.x,v.y, .1f, .1f);
		}
		
	}
	public void plotLine(PVector[] Top, PVector[] Mid, PVector[] Bot, Body person){
		int idIdx=0;
		if(person.getId()==id1){
			idIdx=0;
		}
		else if(person.getId()==id2){
			idIdx=1;
		}
		PVector[][] ptArr=new PVector[3][2];
		ptArr[0][0]=Top[randIdx[idIdx][0]];
		ptArr[0][1]=Mid[randIdx[idIdx][1]];
		
		ptArr[1][0]=Mid[randIdx[idIdx][1]];
		ptArr[1][1]=Bot[randIdx[idIdx][2]];
		
		ptArr[2][0]=Top[randIdx[idIdx][0]];
		ptArr[2][1]=Bot[randIdx[idIdx][2]];
		
		if(randLine[idIdx][0]==null){
			randLine[idIdx][0]=new Random().nextInt(3);
		}
		if(randLine[idIdx][1]==null){
			int rand2=new Random().nextInt(3);
			while(rand2==randLine[idIdx][0]){
				rand2=new Random().nextInt(3);
			}
			randLine[idIdx][1]=rand2;
		}
		
		stroke(255);
		strokeWeight(.01f);
		if(ptArr[randLine[idIdx][0]][0]!=null && ptArr[randLine[idIdx][0]][1]!=null){
			line(ptArr[randLine[idIdx][0]][0].x, ptArr[randLine[idIdx][0]][0].y, ptArr[randLine[idIdx][0]][1].x, ptArr[randLine[idIdx][0]][1].y);
		}
		if(ptArr[randLine[idIdx][1]][0]!=null && ptArr[randLine[idIdx][1]][1]!=null){
			line(ptArr[randLine[idIdx][1]][0].x, ptArr[randLine[idIdx][1]][0].y, ptArr[randLine[idIdx][1]][1].x, ptArr[randLine[idIdx][1]][1].y);
		}
		
	}
	
	public float gradient(PVector vec1, PVector vec2){
		float grad=0;
		if(vec1!=null&&vec2!=null){
			grad=((float) vec1.y-vec2.y)/((float) vec1.x-vec2.x);
			
		}
		return grad;

	}
	
	public void simpleLinePlot(PVector v1, PVector v2){
		if(v1!=null && v2!=null){
			stroke(255);
			strokeWeight(.01f);
			line(v1.x, v1.y, v2.x, v2.y);
		}
	}
	
	public static void main(String[] args) {
		PApplet.main(KinectRenderDemo.class.getName());
	}

}
