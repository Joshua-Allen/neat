import java.io.*; 
import java.net.*;


public class Network {

	DatagramSocket clientSocket;
	DatagramSocket serverSocket;
	public Network() {
		try {
			clientSocket = new DatagramSocket();
			serverSocket = new DatagramSocket(6511);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendToEmulater(String text)
	{
		try {
			InetAddress IPAddress = InetAddress.getByName("localhost");
			byte[] sendData = new byte[1024];
			String sentence = text;
			sendData = sentence.getBytes();
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 6510);
			clientSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public byte[] getFromEmulater()
	{
		byte[] outData = null;
		try {
			byte[] receiveData = new byte[50000];
			
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

			serverSocket.receive(receivePacket);
			
			outData = new byte[receivePacket.getLength()];
			for(int i=0; i<receivePacket.getLength(); i++)
			{
				outData[i] = receiveData[i];
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return outData;
	}
	
	

}
