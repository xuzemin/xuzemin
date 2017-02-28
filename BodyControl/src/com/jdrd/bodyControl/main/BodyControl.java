package com.jdrd.bodyControl.main;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class BodyControl {
	private static String IP_ADDR = "192.168.1.106";
	private static int PORT = 12345;
	public static void main(String[] args) {
		while(true){
			 Socket socket = null;  
			try {
				socket = new Socket(IP_ADDR,PORT);
				PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "GBK")), true);
//				socket.setKeepAlive(true);
				InputStream inputStream = socket.getInputStream();
				byte buffer[] = new byte[1024 * 4];
				int temp = 0;
				while ((temp = inputStream.read(buffer)) != -1) {
					String msg = new String(buffer, 0, temp, "GBK");
					System.out.println(msg);
					out.println("i recieved");
					if(msg.equals("AR")){
						openAR();
					}else{
						openNotepad();
					}
				}
				out.close(); 
				inputStream.close();
				socket.close();
			}catch(IOException e) { 
				e.printStackTrace();
			}
		}
	}
	private static void openNotepad(){
		Runtime rt = Runtime.getRuntime();
		Process p = null;
		String fileLac = "";
		try {
			fileLac = "E:\\Program Files (x86)\\Tencent\\WeChat\\WeChat.exe";
			p = rt.exec(fileLac);
		} catch (Exception e) {
			System.out.println("open failure");
		}
	}
	private static void openAR(){
		Runtime rt = Runtime.getRuntime();
		Process p = null;
		String fileLac = "";
		try {
			fileLac = "E:\\Program Files (x86)\\Tencent\\QQ\\Bin\\QQScLauncher.exe";
			p = rt.exec(fileLac);
		} catch (Exception e) {
			System.out.println("open failure");
		}
	}
}
