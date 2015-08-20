package stockmarket.gui.dialog;

import stockmarket.gui.ListenerView;

/**
 * Interface defining the API for a <tt>ListenerDialog</tt>,
 * an object very similar to the <ttListenerView</tt> API that
 * it inherits. The primary difference is that a <tt>ListenerDialog</tt>
 * is a temporary dialog created by in a GUI, whereas <tt>ListenerView</tt>
 * is intended for the larger GUI classes.
 * <p>
 * In addition to inheriting the methods from <tt>ListenerView</tt>,
 * <tt>ListenerDialog</tt> has two additional utility methods to
 * manage the dialogs created. They are <tt>showDialog()</tt> and
 * <tt>closeDialog()</tt>, which display and dispose of the dialog
 * respectively.
 * 
 * @author craig
 * @version 2.0
 * @see stockmarket.gui.dialog.DialogFactory DialogFactory
 */
public interface ListenerDialog extends ListenerView {

	/**
	 * Display the dialog in the GUI.
	 */
	void showDialog();
	
	/**
	 * Close the dialog when it is no longer needed.
	 */
	void closeDialog();
	
}
