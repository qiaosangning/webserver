package com.study.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
*/
/**
 * web server
 * @author cn40339
 * */
public class ServerBootstrap {

	public static final int port = 33221;// 33201~33250

	private ServerSocket serverSocket;

	// logger
//	private static Logger logger = LoggerFactory.getLogger(ServerBootstrap.class);

	public void bootstrap(int port) {

		try {
			serverSocket = new ServerSocket(port);

//			logger.info("web server bootstrap on port:" + port);
			System.out.println("web server bootstrap on port:" + port);

			while (true) {
				Socket socket = serverSocket.accept();

				// handle the user's request
				this.process(socket);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// start
	public static void main(String[] args) {

		ServerBootstrap server = new ServerBootstrap();

		server.bootstrap(ServerBootstrap.port);
	}

	public static final String WEB_ROOT = "D:/book";

	// process the request
	public void process(Socket socket) {
		PrintStream out;
		InputStream in;

		try {
			in = socket.getInputStream();
			out = new PrintStream(socket.getOutputStream());

			BufferedReader inReader = new BufferedReader(new InputStreamReader(in));
			String inputContent = inReader.readLine();
			if (inputContent == null || inputContent.length() == 0) {
//				logger.info("400");
				System.out.println("400");
			}
			
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
			
//			logger.info("[method:]"+method+"[,filename:]"+filename+"[,version:]"+version);
			
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
}
