package com.study.webserver.webserver.task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.study.webserver.webserver.ServerBootstrap;

/**
 * Task Do
 * 
 * @author zhangning
 * */
public class TaskImpl implements Task {

	private Socket socket;

	private String web_root = ServerBootstrap.class.getResource("").getPath();

	public void execute() {

		PrintStream out;
		InputStream in;

		try {
			in = socket.getInputStream();
			out = new PrintStream(socket.getOutputStream());

			String filename = this.getSourceName(in);

			boolean isStatic = !(filename.contains("jsp"));
			
			byte[] content = null;

			// static page controller
			if (isStatic) {
				// get file
				File file = new File(web_root + "webroot" + filename);

				if (!file.exists()) {
					System.out.println("404");
				}

				// write the file to stream
				@SuppressWarnings("resource")
				InputStream inFile = new FileInputStream(file);
				content= new byte[(int) file.length()];
				inFile.read(content);

			} else {// dynamic page controllers
				// jsp container
				// get hello.jsp
				// get params
				if(filename.contains("?")){
					System.out.println("yes contains");
				}else{
					System.out.println("no contains");
				}
				
				int index = filename.indexOf("?");
				System.out.println("index of ?:"+index);
				
				String requestParams_str = filename.substring(index+1);
				requestParams_str.replaceAll(" ", "");
				System.out.println("requestParams:"+requestParams_str);
				
				String[] requestParams = requestParams_str.split(",");
				Map<String, String> values = new HashMap<String, String>();
				for(int i = 0;i<requestParams.length;i++){
					int indexOfE = requestParams[i].indexOf("=");
					values.put(requestParams[i].substring(0,indexOfE),requestParams[i].substring(indexOfE+1));
				}
				
				String requestFile = filename.substring(0, index);
				System.out.println("requestFile:"+requestFile);
				
				// get file
				File file = new File(web_root + "webroot" + requestFile);

				if (!file.exists()) {
					System.out.println("404");
					return;
				}

				// write the file to stream
				@SuppressWarnings("resource")
				InputStream inFile = new FileInputStream(file);
				content = new byte[(int) file.length()];
				inFile.read(content);

				String content_str = new String(content);

				Pattern p = Pattern.compile("\\{.*?\\}");// 查找规则公式中大括号以内的字符
				Matcher m = p.matcher(content_str);
				while (m.find()) {
					//int start = m.start()+1;
					//int end = m.end()-1;
					
					String param = m.group();
					param = param.replaceAll("\\{", "");// 去掉括号
					param = param.replaceAll("\\}", "");// 去掉括号
					System.out.println(param);
					
					boolean change = false;
					
					for(Map.Entry<String, String> entry: values.entrySet()){
						if(param.equalsIgnoreCase(entry.getKey())){
							change = true;
							
							content_str = content_str.replaceAll("\\{"+param+"\\}", entry.getValue());
						}
					}
					if(!change){
						
						content_str = content_str.replaceAll("\\{"+param+"\\}", "");
						change = false;
						
					}
				}
				
				content_str = content_str.replaceAll("\\{", " ");
				content_str = content_str.replaceAll("\\}", " ");
				
				System.out.println("response:"+content_str);
				
				content = content_str.getBytes();

			}

			out.println("HTTP/1.0 200 sendFile");
			out.println("Content-length: " + content.length);
			out.println();

			out.write(content);
			out.flush();
			out.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getSourceName(InputStream in) throws IOException {

		BufferedReader inReader = new BufferedReader(new InputStreamReader(in));
		String inputContent = inReader.readLine();
		if (inputContent == null || inputContent.length() == 0) {
			// logger.info("400");
			System.out.println("400");
			return null;
		} else {
			// decode the http info
			String request[] = inputContent.split(" ");
			if (request.length != 3) {
				// logger.info("400");
				System.out.println("400");
			}

			// request method
			String method = request[0];

			// request filename
			String filename = request[1];

			// http version
			String version = request[2];

			return filename;
		}
	}

	public TaskImpl() {

	}

	public TaskImpl(Socket socket) {
		this.socket = socket;
	}
}
