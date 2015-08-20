package stockmarket.controller;

import java.awt.event.ActionListener;

import net.jcip.annotations.ThreadSafe;
import stockmarket.gui.AbstractListenerView;
import stockmarket.gui.ProperyChangeView;

/**
 * Expanded version of <tt>AbstractController</tt> with the controller
 * functioning as an <tt>ActionListener</tt>. This design allows for
 * a more complete off-loading of all business logic from the view to
 * the controller, increasing the abstractness of their interactions
 * and reducing class coupling.
 * <p>
 * Any views added to this class that support the function will add
 * this class as an <tt>ActionListener</tt>. Events fired by the view
 * will be handled in the <tt>actionPerformed()</tt> method, giving the
 * controller nearly complete control over the implementation of view
 * actions.
 * <p>
 * <b>THREAD SAFETY:</b> This class is thread safe because its super class
 * is thread safe. While this class adds methods, it doesn't add any state
 * variables, therefore the thread-safe design of its superclass applies
 * to this class as well.
 * 
 * @author Craig
 * @version 2.0
 * @see stockmarket.gui.AbstractListenerView AbstractView
 * @see stockmarket.model.AbstractPropertyModel AbstractPropertyModel
 */
@ThreadSafe
public abstract class AbstractListenerController 
extends AbstractController
implements ActionListener{

	/**
	 * Creates the controller.
	 */
	public AbstractListenerController() {
		super();
	}
	
	/**
	 * Add a view. If the type of view supports it, this controller
	 * is added to it as an <tt>ActionListener</tt>.
	 * 
	 * @param view the view to be managed by this controller.
	 */
	@Override
	public void addView(ProperyChangeView view){
		if(view instanceof AbstractListenerView){
			((AbstractListenerView) view).addActionListener(this);
		}
		super.addView(view);
	}
	
	/**
	 * Remove a view. If the type of view supports it, this controller
	 * is removed from it as an <tt>ActionListener</tt>.
	 * 
	 * @param view the view to be removed from this controller.
	 */
	@Override
	public void removeView(ProperyChangeView view){
		if(view instanceof AbstractListenerView){
			((AbstractListenerView) view).removeActionListener(this);
		}
		super.removeView(view);
	}

}
