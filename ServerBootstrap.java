package com.study.webserver.webserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import com.study.webserver.webserver.task.Task;
import com.study.webserver.webserver.task.TaskImpl;

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
			
			ThreadManager manager = new ThreadManager(10);

			while (true) {
				Socket socket = serverSocket.accept();
				
				Task task = new TaskImpl(socket);
				
				manager.addTask(task);
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
}
