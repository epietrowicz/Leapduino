package seniordesign;
import com.leapmotion.leap.Controller;
public class Leapduino
{
//Main
public static final void main(String args[])
{  
	
   //Initialize serial communications.
   RS232Protocol Serial = new RS232Protocol();
   
   Serial.connect("COM3");
   //Initialize the Leapduino listener.
   LeapduinoListener leap = new LeapduinoListener(Serial);
   Controller controller = new Controller();
   controller.addListener(leap);
   while (true) {
	    try {
	        Thread.sleep(1000);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
   }
}
}
