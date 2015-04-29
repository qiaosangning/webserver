package com.study.webserver.webserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.study.webserver.webserver.task.Task;
import com.study.webserver.webserver.task.TaskThread;

/**
 * manage the thread
 * 
 * @author zhangning
 * */
public class ThreadManager {

	// thread size
	private int size;

	// task list
	private BlockingQueue<Task> taskQueue;

	// thread list
	private List<TaskThread> threadList;

	// default size 10
	public ThreadManager() {
		this(10);
	}

	public ThreadManager(int size) {
		if (size > 0) {
			this.size = size;
		}
		this.taskQueue = new LinkedBlockingQueue<Task>();
		this.threadList = new ArrayList<TaskThread>();

		this.start();
	}

	// create fixed number thread, and start them
	public void start() {
		synchronized (threadList) {

			for (int i = 0; i < size; i++) {
				TaskThread taskThread = new TaskThread(taskQueue);
				threadList.add(taskThread);
				taskThread.start();
			}
		}
	}

	// thread number
	public int getThreadNumber() {
		return threadList.size();
	}

	// add task
	public void addTask(Task task) {
		taskQueue.offer(task);
	}

	// stop the thread
	public void stopThread() {

	}

}
