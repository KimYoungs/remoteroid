package org.secmem.remoteroid.network;

import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

import org.secmem.remoteroid.network.PacketHeader.OpCode;

import android.util.*;


public class Transmitter{
	private static final int PORT = 50000;
	
	private static Transmitter mInstance;
	
	private Socket socket;
	private OutputStream sendStream;
	private InputStream recvStream;
	
	private PacketSendListener packetListener;
	private FilePacketListener fileListener;	
	
	private PacketReceiver packetReceiver;	
	private FileTransReceiver fileTransReceiver;
	
	private Transmitter(){		
	}
	
	public static Transmitter getInstance(){
		if(mInstance==null)
			mInstance = new Transmitter();
		return mInstance;
	}
	
	/**
	 * Connect to specified host.
	 * @param ipAddr ip address
	 * @throws IOException
	 */
	public void connect(String ipAddr) throws IOException{
		socket = new Socket();
		socket.connect(new InetSocketAddress(ipAddr, PORT));
		
		// Open outputStream
		sendStream = socket.getOutputStream();
		
		// Open inputStream
		recvStream = socket.getInputStream();		
		
		fileTransReceiver = new FileTransReceiver(sendStream);		
		
		// Create and start packet receiver
		packetReceiver = new PacketReceiver(recvStream, fileListener);
		packetReceiver.start();
	}
	
	/**
	 * Disconnect from host.
	 * @throws IOException
	 */
	public void disconnect(){
		if(socket!=null){
			try{				
				recvStream.close();
				sendStream.close();
				packetReceiver = null;				
				socket.close();
			}catch(IOException e){}
		}
	}
	

	
	public void sendFile(ArrayList<File> fileList){
		// TODO implement send only one file
		try{
			fileTransReceiver.sendFileList(fileList);
		}catch(IOException e){
			e.printStackTrace();
		}
	}


	/**
	 * Get packet from host and notify to other component via listener to response to each packet properly.
	 * @author Taeho Kim
	 *
	 */
	class PacketReceiver extends Thread{		
		
		private InputStream recvStream;
		private FilePacketListener fileListener;
		// TODO private EventPacketListener eventListener;
		
		
		public PacketReceiver(InputStream recvStream, FilePacketListener fileListener){
			this.recvStream = recvStream;
			this.fileListener = fileListener;
		}		
		
		
		/**
		 * Get packet from stream.
		 * @return a Packet object
		 * @throws IOException a network problem exists
		 * @throws ParseException a malformed packet received
		 */
		
		private byte[] buffer = new byte[Packet.MAX_LENGTH*2];
		private int bufferOffset = 0;
		
		public Packet getPacket() throws IOException, ParseException{
			
			int nRead;			
			while(true){
				nRead = recvStream.read(buffer, bufferOffset, Packet.MAX_LENGTH);				
				
				if(nRead<0)
					throw new IOException();
				
				if(nRead>0)
					bufferOffset+=nRead;
				
				// If packet size is smaller than header's length
				if(bufferOffset < PacketHeader.LENGTH)
					continue; // try fetching more data from stream
				
				// Try getting header data
				PacketHeader header = PacketHeader.parse(buffer);
				
				// If read data's length is smaller than whole packet's length
				
				if(bufferOffset < header.getPacketLength())
					continue; //  try fetching more data from stream
				
				// If you reached here, all required packed data has received.
				// Now we can parse received data as Packet object.
				Packet packet = Packet.parse(buffer);
				
				// Decrease current offset by last packet's length
				bufferOffset-=header.getPacketLength();
				
				//The remaining packets moves forward
				System.arraycopy(buffer, header.getPacketLength(), buffer, 0, bufferOffset);
				
				// Return packet object
				return packet;
			}
		}

		@Override
		public void run() {			
			
			/**
			 * Run infinitely and get packet from stream before user requested to stop
			 */
			while(true){
				try{
					Packet packet = getPacket();					
					
					switch(packet.getOpcode()){
					case OpCode.FILEINFO_RECEIVED:
						// TODO prototype						
						//fileListener.onReceiveFileInfo();
						fileTransReceiver.receiveFileInfo(packet);
						break;
						
					case OpCode.FILEDATA_RECEIVED:
						// TODO prototype
						//fileListener.onReceiveFileData();
						fileTransReceiver.receiveFileData(packet);
						break;
						
					case OpCode.FILEDATA_REQUESTED:
						//fileListener.onFileDataRequested();
						fileTransReceiver.sendFileData();
						break;
						
					case OpCode.FILEINFO_REQUESTED:
						//fileListener.onFileInfoRequested();
						fileTransReceiver.sendFileInfo();
						break;
						
					}
					
				} catch(IOException e){
					e.printStackTrace();					
					//If server was closed, throw an IOException	
					//If file is open, Shoud be closed
					fileTransReceiver.closeFile();
					disconnect();
					break;
				} catch (ParseException e) {
					e.printStackTrace();					
				}
			}
		}		
	}
}