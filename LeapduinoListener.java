package seniordesign; 
import com.leapmotion.leap.*; 

public class LeapduinoListener extends Listener
{  
	int posTable = 0;
	double len1 = 165;
	double len2 = 165;
	
	

//Serial port that we'll be using to communicate with the Arduino.
private RS232Protocol serial; 
//Constructor
public LeapduinoListener(RS232Protocol serial)
{
   this.serial = serial;
} 
//Member Function: onInit
public void onInit(Controller controller)
{
   System.out.println("Initialized");
} 
//Member Function: onConnect
public void onConnect(Controller controller)
{
   System.out.println("Connected");
} 
//Member Function: onDisconnect
public void onDisconnect(Controller controller)
{
   System.out.println("Disconnected");
} 
//Member Function: onExit
public void onExit(Controller controller)
{
   System.out.println("Exited");
} 
//calculate the law of cosines
public double lawOfCosines(double a,double b, double c){
	
	return (float) Math.acos((((a*a) + (b*b)) - (c*c))/(2*a*b));
}
//calculate the distance between the two points
public double distance(double z, double y){
	
	return Math.sqrt((z*z) + (y*y));
}
//calculate theta 1 
public double angle1(double z, double y){
	
	double dist = distance(z,y);
	double D1 = Math.atan2(y, z);
	double D2 = lawOfCosines(dist, len1, len2); 
	double A1 = D1+ D2;
	return A1;
	
}
//calculate theta 2
public double angle2(double z, double y){
	
	double dist = distance(y,z);
	double A2 = lawOfCosines(len1, len2, dist);
	return A2;
}

public double deg(double rad){
	
	return rad * (180/Math.PI);
}
Vector leapToWorld(Vector leapPoint, InteractionBox iBox)
{
	Vector origin = new Vector(0, 0.5f, 0); //TODO: origin is now taking into account set up. (0.0) -> edge of the game board
	//we have a max reach of 330 along the Z axis
	
    //leapPoint.setZ((float) (leapPoint.getZ() * -1.0)); //right-hand to left-hand rule
    Vector normalized = iBox.normalizePoint(leapPoint, true);
    normalized = normalized.plus(origin); //re-center origin
    return normalized.times((float) 100.0); //the interaction box is exactly the same size as the game board.  reduce this number to increase sensitivity.
}


//Member Function: onFrame
public void onFrame(Controller controller)
{
	
   //Get the most recent frame.
   Frame frame = controller.frame(); //set an object to the current frame
   Frame previous = controller.frame(1); //set an object to the previous frame
   Hand hand = frame.hands().frontmost(); //read the hand in the current frame
   Hand hand1 = previous.hands().frontmost(); //read the hand in the previous frame
   Boolean handIsEqual = hand.equals(hand1); //compare the hand in the previous frame 
   
   InteractionBox box = frame.interactionBox();
   Vector leapPos = new Vector (hand.palmPosition());
   Vector realPos = new Vector (leapToWorld(leapPos,box));
   
   float x = realPos.getX();
   float y = realPos.getY();
   float z = realPos.getZ();
    
   
   //Verify a hand is in view.
   
   if (frame.hands().count() > 0)
   {

     float graspAngle = hand.grabAngle();  

     double ang1 = angle1(z, y);
     double theta1 = deg(ang1);
     double ang2 = angle2(z, y);
     double theta2 = deg(ang2);
     
    
     //double xx = 330;
     //double yy = 0;
     
     //double ang1 = angle1(xx,yy);
     //double theta1 = deg(ang1);

     
     //double ang2 = angle2(xx,yy);
     //double theta2 = deg(ang2);
  
     
     int count = 0;
     
     
    // float rotationIntentFactor = hand.rotationProbability(previous);
     
     
     if (handIsEqual){
    	 if (posTable == 0 && count == 0){
    		 posTable = 180;
    		 count = 1;
    	 }else if (posTable == 180 && count == 0){
    		 posTable =0;
    		 count = 1;
    	 }
     }
   
     double normalize = 3.0;
     double n = 100*normalize;
     double X = 1.5 + 2 * x / n;
     double baseAngle = 90.0+Math.cos(X)*90.0;
     
     Math.round(theta1);
     Math.round(theta2);
     Math.round(baseAngle);
     Math.round(graspAngle); //if this is inaccurate, check the math here then send a boolean to Arduino
     
     String Theta1 = (String.valueOf(theta1));
     String Theta2 = (String.valueOf(theta2));
     String BaseAngle = (String.valueOf(baseAngle));
     String GraspAngle = (String.valueOf(graspAngle));
     String PosTable = (String.valueOf(posTable));
     //------------------------------------------------------------
     serial.write(Theta1);
     serial.write(",");
     serial.write(Theta2);
     serial.write(",");
     serial.write(BaseAngle);
     serial.write(",");
     serial.write(GraspAngle);
     serial.write(",");
     serial.write(PosTable);
     
     //System.out.println("X: " + x + "    Y: " + y + "     Z: " + z);
     System.out.println("theta1: " + Theta1 + "     theta2: " + Theta2);

     //Give the Arduino some time to process our data.
 
     try{ Thread.sleep(140); }
     catch (InterruptedException e) { e.printStackTrace(); }
   }
}
}