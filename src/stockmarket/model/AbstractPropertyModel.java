package stockmarket.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import net.jcip.annotations.ThreadSafe;

/**
 * Abstract model which handles the basic functions of adding, removing, and firing events to
 * property change listeners. This class exists to provide an API for interacting with all models
 * that need to pass property changes to controllers in this program, following the Model-View-Controller
 * design pattern.
 * <p>
 * This class should only be inherited by <tt>JavaBean</tt> classes that hold values tracking the state of 
 * various elements in the program.
 * <p>
 * <b>IMPLEMENTATION NOTES:</b>
 * <p>
 * <b>1)</b> Wrapper methods around a <tt>PropertyChangeSupport</tt> object are provided to allow
 * implementations of this class to easily configure fields as bound <tt>JavaBean</tt> properties.
 * <p>
 * <b>2)</b> This class should follow <tt>JavaBean</tt> design guidelines to ensure compatibility
 * with method invocation via reflection. Property names should be <tt>String</tt> values that match
 * the signature of the property's corresponding getter/setter methods (without the get/set).
 * <p>
 * <b>THREAD SAFETY:</b> Thread safety is delegated to the <tt>PropertyChangeSupport</tt> object
 * that this abstract class wraps around. No additional synchronization should be needed when 
 * adding/removing listeners or firing change events.
 * 
 * This abstract class is designed to be mostly thread safe. Any access to the
 * underlying <tt>PropertyChangeSupport</tt> object is locked on a private lock object in this
 * class. While this is good enough synchronization for nearly all cases, race conditions
 * (check-then-act) involving this field should be avoided in implementations of this class.
 * <p>
 * Of course, this class cannot make any guarantees for the thread safety of its subclass
 * implementations.
 * <p>
 * <b>NOTE:</b> As of Version 2.0, the <tt>AbstractController</tt> class can only reflectively
 * invoke setter methods with a single parameter. Multiple parameter methods are not supported.
 * If a property model needs to have multiple parameters, they should be passed in the form
 * of an array.
 * 
 * @author Craig
 * @version 2.0
 * @see stockmarket.gui.PropertyChangeView ControllableView
 * @see stockmarket.controller.AbstractController AbstractController
 */
@ThreadSafe
public abstract class AbstractPropertyModel 
implements Serializable{

	//TODO document how firePropertyEvent should never be invoked in a synchronized
	//block or method
	
	/**
	 * SerialVersionUID for implementing Serialization.
	 */
	private static final long serialVersionUID = 873132566748734660L;

	/**
	 * <tt>PropertyChangeSupport</tt> object used to monitor property changes and handle events.
	 * <p>
	 * <tt>PropertyChangeSupport</tt> is thread-safe.
	 */
	protected final PropertyChangeSupport propertyChangeSupport;
	
	/**
	 * Creates a new model with a property change support object linked to it.
	 */
	public AbstractPropertyModel(){
		propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	/**
	 * Adds a property change listener to this model.
	 * 
	 * @param listener the property change listener to add to this model.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener){
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	/**
	 * Removes a property change listener from this model.
	 * 
	 * @param listener the property change listener to remove from this model.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener){
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	/**
	 * Fires a property change event for a bound property of this model.
	 * 
	 * @param propertyName the name of the property.
	 * @param oldValue the old value of the property.
	 * @param newValue the new vale being assigned to this property.
	 */
	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue){
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}
	
}
