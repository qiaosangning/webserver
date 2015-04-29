package com.study.webserver.webserver.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

import com.study.webserver.ServerBootstrap;

/**
 * Task Do
 * @author zhangning
 * */
public class TaskImpl implements Task {
	
	private Socket socket;
	
	public void execute(){
		
		PrintStream out;
		InputStream in;

		try {
			in = socket.getInputStream();
			out = new PrintStream(socket.getOutputStream());

			String filename = this.getSourceName(in);
			
			//get file
			File file = new File(ServerBootstrap.WEB_ROOT+filename);
			if(!file.exists()){
//				logger.info("404");
				System.out.println("404");
			}
			
			//write the file to stream
			@SuppressWarnings("resource")
			InputStream inFile = new FileInputStream(file);
			byte content[] = new byte[(int) file.length()];
			
			inFile.read(content);
			out.println("HTTP/1.0 200 sendFile");
			out.println("Content-length: "+content.length);
			out.println();
			
			out.write(content);
			out.flush();
			out.close();
			in.close();
			

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	public String getSourceName(InputStream in) throws IOException{
		
		BufferedReader inReader = new BufferedReader(new InputStreamReader(in));
		String inputContent = inReader.readLine();
		if (inputContent == null || inputContent.length() == 0) {
//			logger.info("400");
			System.out.println("400");
			return null;
		}else{
			//decode the http info
			String request[] = inputContent.split(" ");
			if(request.length != 3){
//				logger.info("400");
				System.out.println("400");
			}
			
			//request method
			String method = request[0];
			//request filename
			String filename = request[1];
			//http version
			String version = request[2];
			
			return filename;
		}
	}
	
	public TaskImpl(){
		
	}
	
	public TaskImpl(Socket socket){
		this.socket = socket;
	}
}
