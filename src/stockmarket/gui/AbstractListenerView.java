package stockmarket.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import stockmarket.gui.dialog.ListenerDialog;
import net.jcip.annotations.NotThreadSafe;

/**
 * An abstract implementation of the <tt>ListenerView</tt> interface.
 * This implementation sets up a <tt>List</tt> to store any external
 * listeners added to this class. In addition, it implements the
 * <tt>ActionListener</tt> interface, with an implemented and final
 * <tt>actionPerformed()</tt> method that merely iterates through
 * the list of listeners and passes the <tt>ActionEvent</tt> to them.
 * <p>
 * To extend this class, two abstract methods must be implemented.
 * <p>
 * <tt>getValue()</tt>, from <tt>ListenerView</tt>, provides an
 * abstract tool for an external listener that needs to retrieve
 * a value from this class. The <tt>String</tt> parameter passed to it
 * should be a constant across the entire program, to ensure consistency.
 * Any values needed to perform any action from this class should be
 * set up to be returned by this method.
 * <p>
 * This class also implements the <tt>PropertyChangeView</tt> interface.
 * The abstract method <tt>propertyChange()</tt> needs to be implemented,
 * to provide an abstract way for the external listening controller to
 * pass updates back to this view.
 * <p>
 * This design is meant to facilitate the passing of events from
 * actionable components in this GUI class to external listeners.
 * By functioning as an <tt>ActionListener</tt> itself, this class
 * can be added as a listener to any components that need one.
 * Thus subclasses will need no knowledge of what, if any, external
 * listeners have been added, they merely need to add <tt>this</tt>
 * as their <tt>ActionListener</tt>s, and if external listeners
 * exist they will be called upon.
 * <p>
 * This design does not forbid the use of other listeners of any
 * type in subclasses. However, those listeners should only be used
 * for minor events. Anything that impacts the state of the program
 * should utilize the external listening controller to perform the action.
 * <p>
 * <b>NOTE:</b> A view extending this class cannot also extend a GUI component class,
 * so views using this API will have to rely on composition (wrapping around
 * an instance of the GUI component their building) rather than inheritence
 * to create components.
 * <p>
 * <b>THREAD SAFETY:</b> Swing is NOT thread safe. All
 * methods in this class MUST be invoked on the <tt>EventDispatchThread</tt>.
 * 
 * @author Craig
 * @version 2.0
 * @see stockmarket.controller.AbstractListenerController AbstractListenerController
 * @see stockmarket.model.AbstractPropertyModel AbstractPropertyModel
 */
@NotThreadSafe
public abstract class AbstractListenerView 
implements ListenerView{
	
	//TODO change documentation, because this now just implements ListenerView,
	//and ListenerView implements ActionListener and PropertyChangeListener
	
	/**
	 * List of listeners/controllers assigned to this class.
	 */
	private final List<ActionListener> listeners;
	
	/**
	 * Creates a view class with default configuration.
	 */
	public AbstractListenerView(){
		this.listeners = new ArrayList<>(10);
	}
	
	@Override
	public void addActionListener(ActionListener listener){
		listeners.add(listener);
	}
	
	@Override
	public void removeActionListener(ActionListener listener){
		listeners.remove(listener);
	}
	
	/**
	 * Passes an event from an actionable component in the GUI to the
	 * listeners assigned to this view.
	 * 
	 * @param event the event that needs to be executed.
	 */
	@Override
	public final void actionPerformed(ActionEvent event){
		ActionEvent newEvent = null;
		if(!(event.getSource() instanceof ListenerDialog)){
			newEvent = new ActionEvent(
					this, ActionEvent.ACTION_PERFORMED, event.getActionCommand());
		}
		else{
			newEvent = event;
		}
		
		for(ActionListener listener : listeners){
			listener.actionPerformed(newEvent);
		}
	}
	
	
}
