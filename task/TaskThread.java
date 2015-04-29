package com.study.webserver.webserver.task;

import java.util.concurrent.BlockingQueue;

/**
 * process the task
 * 
 * @author zhangning
 * */
public class TaskThread extends Thread {

	private BlockingQueue<Task> taskQueue;

	public TaskThread(BlockingQueue<Task> taskQueue) {
		this.taskQueue = taskQueue;
	}

	public void run() {
		while (true) {
			synchronized (taskQueue) {
				if (!taskQueue.isEmpty()) {
					try {
						Task task = taskQueue.take();
						
						task.execute();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
