package seniordesign;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException; 
public class RS232Protocol
{
//Serial port we're manipulating.
private SerialPort port; 
//Class: RS232Listener
public class RS232Listener implements SerialPortEventListener  
{
   public void serialEvent(SerialPortEvent event)
   {
     //Check if data is available.
     if (event.isRXCHAR() && event.getEventValue() > 0)
     {
    	 
       try
       {
         int bytesCount = event.getEventValue();
         System.out.print(port.readString(bytesCount));
       }          
       catch (SerialPortException e) { e.printStackTrace(); }
     }
   }
}
//Member Function: connect
public void connect(String newAddress)
{
   try
   {
     //Set up a connection.
     port = new SerialPort(newAddress);    
     //Open the new port and set its parameters.
     port.openPort();
     port.setParams(38400, 8, 1, 0);          
     //Attach our event listener.
     port.addEventListener(new RS232Listener());
   }  
   catch (SerialPortException e) { e.printStackTrace(); }
}
//Member Function: disconnect
public void disconnect()
{
   try { port.closePort(); }  
   catch (SerialPortException e) { e.printStackTrace(); }
}
//Member Function: write
public void write(String text)
{
   try { port.writeBytes(text.getBytes()); }  
   catch (SerialPortException e) { e.printStackTrace(); }
}
}