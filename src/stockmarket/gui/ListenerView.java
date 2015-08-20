package stockmarket.gui;

import java.awt.event.ActionListener;

/**
 * This interface provides a basic API to allow for a GUI class
 * to pass events to <tt>ActionListener</tt>s outside of the class.
 * This design is meant to facilitate loose coupling between view
 * and controller components of a program, allowing them to interact
 * through the <tt>ActionListener</tt> interface without needing
 * direct knowledge of each other.
 * <p>
 * In addition to the methods to add and remove an <tt>ActionListener</tt>,
 * another method, <tt>getValue()</tt>, is provided. Many times a listener
 * will need values from the UI to perform its function (such as user input).
 * This method provides an abstract way for a separate listener class to retrieve
 * any needed values. Any values needed to perform any action from this class should be
 * set up to be returned by this method.
 * <p>
 * Listeners should invoke <tt>getSource()</tt> on any <tt>ActionEvent</tt> 
 * received, and if the source is an instance of this interface, call the
 * <tt>getValue()</tt> method. A constant value should be passed to 
 * <tt>getValue()</tt> as its parameter, to ensure consistency across classes.
 * 
 * @author craig
 * @version 2.0
 */
public interface ListenerView{

	/**
	 * Returns a specified value from the view. The value returned 
	 * will correspond to the property name parameter. If the parameter
	 * doesn't match any value that this class is meant to return, this method
	 * should return null.
	 * <p>
	 * <b>THREAD SAFETY:</b> Again, this is a Swing class. Controllers that
	 * off-load their tasks to other threads need to make sure that they only
	 * call on this method on the <tt>EventDispatchThread</tt>.
	 * 
	 * @param valueToGet the name of the value to be retrieved. This should
	 * be a constant value and determined by the implementation.
	 * @return the value of the requested property.
	 */
	Object getValue(String valueToGet);
	
	/**
	 * Adds an <tt>ActionListener</tt> to the list of listeners in this view. 
	 * The listener being passed to the view should function as a controller 
	 * for implementing business logic in response to user-triggered <tt>ActionEvents</tt> 
	 * in the GUI.
	 * 
	 * @param listener the listener being added to this view.
	 */
	void addActionListener(ActionListener listener);
	
	/**
	 * Removes an <tt>ActionListener</tt> to the list of listeners in this view. 
	 * 
	 * @param listener the listener being removed from this view.
	 */
	void removeActionListener(ActionListener listener);
	
}
