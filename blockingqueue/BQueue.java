package com.study.webserver.webserver.blockingqueue;

import java.util.LinkedList;
import java.util.List;

public class BQueue {
	
	private List queue = new LinkedList();
	private int queue_capacity = 10;
	
	public BQueue(int capacity){
		this.queue_capacity = capacity;
	}
	
	//insert if not full
	//otherwise wait
	public synchronized <T> void offer(T t) throws InterruptedException{
		while(this.queue.size() == this.queue_capacity){
			wait();
		}
		if(this.queue.size() == 0){
			notifyAll();
		}
		this.queue.add(t);
	}
	
	//dequeue if not null
	//otherwise wait
	public synchronized <T> T take() throws InterruptedException{
		while(this.queue.size() == 0){
			wait();
		}
		if(this.queue.size() == this.queue_capacity){
			notifyAll();
		}
		return (T) this.queue.remove(0);
	}
}
