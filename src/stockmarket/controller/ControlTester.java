package stockmarket.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ControlTester {

	public static void main(String[] args){
		ExecutorService exec = Executors.newCachedThreadPool();
		
		//((ThreadPoolExecutor) exec).setKeepAliveTime(1000000, TimeUnit.MILLISECONDS);
		System.out.println(((ThreadPoolExecutor) exec).getKeepAliveTime(TimeUnit.MILLISECONDS));
	}
	
	public ControlTester() {
		// TODO Auto-generated constructor stub
	}

}
