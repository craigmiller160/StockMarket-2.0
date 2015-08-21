package stockmarket.controller;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import net.jcip.annotations.ThreadSafe;
import stockmarket.gui.ListenerView;
import stockmarket.gui.ProperyChangeView;

/**
 * 
 * 
 * TODO documentation here needs to specify that this controller is meant for AbstractView.
 * doesn't absolutely require it, but best that way.
 * 
 * EXECUTOR ISSUES:
 * 1) Task cancellation - need some way to do that
 * 2) Shutdown - all threads should finish running before a graceful termination
 * 3) Exception handling, thread names, and other things that probably would be better with ThreadFactory created threads.
 * 
 * TODO the executor changing methods are not thread safe. Except now they're protected, they might work
 * 
 * 
 * @author Craig
 *
 */
@ThreadSafe
public abstract class AbstractConcurrentListenerController 
extends AbstractListenerController{

	//TODO hahahaha
	
	//protected so it can be manually accessed by subclasses
	protected final ExecutorService eventExecutor;
	
	//No limit on thread pool size
	public AbstractConcurrentListenerController(){
		super();
		eventExecutor = Executors.newCachedThreadPool();
	}
	
	//Limit on thread pool size
	public AbstractConcurrentListenerController(int threadPoolSize){
		super();
		eventExecutor = Executors.newFixedThreadPool(threadPoolSize);
	}
	
	public AbstractConcurrentListenerController(int corePoolSize, int maximumPoolSize, 
			long keepAliveTime, TimeUnit unit){
		super();
		eventExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 
				keepAliveTime, unit, new LinkedBlockingQueue<Runnable>());
	}
	
	//Shutdown executor, with the option of terminating 
	//running threads
	public void shutdown(boolean interruptIfRunning){
		if(interruptIfRunning){
			eventExecutor.shutdownNow();
		}
		else{
			eventExecutor.shutdown();
		}
	}
	
	//Following methods allow for modifying the Executor at the heart of this class
	public void setThreadFactory(ThreadFactory factory){
		((ThreadPoolExecutor) eventExecutor).setThreadFactory(factory);
	}
	
	public void setRejectedExecutionHandler(RejectedExecutionHandler handler){
		((ThreadPoolExecutor) eventExecutor).setRejectedExecutionHandler(handler);
	}
	
	public void setThreadPoolProperties(int corePoolSize, int maximumPoolSize, 
			long keepAliveTime, TimeUnit unit){
		((ThreadPoolExecutor) eventExecutor).setCorePoolSize(corePoolSize);
		((ThreadPoolExecutor) eventExecutor).setMaximumPoolSize(maximumPoolSize);
		((ThreadPoolExecutor) eventExecutor).setKeepAliveTime(keepAliveTime, unit);
	}

	//Document in this method that it is invoked on the EDT
	@Override
	public final void actionPerformed(final ActionEvent event) {
		//TODO decide if Runnable or Callable is better here
		Runnable eventTask = null;
		if(event.getSource() instanceof ListenerView){
			final Object valueFromView = ((ListenerView) event.getSource()).getValue(
					event.getActionCommand());
			eventTask = new Runnable(){
				@Override
				public void run() {
					processEvent(event, valueFromView);
				}
			};
		}
		else{
			eventTask = new Runnable(){
				@Override
				public void run(){
					processEvent(event, null);
				}
			};
		}
		
		eventExecutor.execute(eventTask);
	}
	
	//Always accessed from multiple threads concurrently, 
	//synchronization totally needed. valueFromView could be null
	//This method is never invoked from the EDT
	protected abstract void processEvent(final ActionEvent event, 
			final Object valueFromView);
	
	//Uncaught exception, handle this in subclass
	//TODO maybe eliminate this one
	//protected abstract void uncaughtException(Throwable t);
	
	//Overridden to make passing event back to view thread-safe
	//by putting it on EDT
	public void propertyChange(final PropertyChangeEvent event){
		synchronized(viewList){
			for(final ProperyChangeView view : viewList){
				SwingUtilities.invokeLater(new Runnable(){
					@Override
					public void run() {
						view.changeProperty(event);
					}
				});
			}
		}
	}
	
		
	
}
