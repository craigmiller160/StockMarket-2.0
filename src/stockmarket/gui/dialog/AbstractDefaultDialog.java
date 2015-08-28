package stockmarket.gui.dialog;

import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mvp.listener.AbstractListenerDialog;
import net.miginfocom.swing.MigLayout;

/**
 * An default design of a dialog for this program. This class
 * extends <tt>AbstractListenerDialog</tt> and its ability to
 * pass events to external listening controllers. Subclasses
 * will also have to implement the abstract superclass method,
 * <tt>getValueForAction(String)</tt>. The documentation from
 * the superclass should be consulted for how best to handle
 * its methods.
 * <p>
 * In addition, this class implements all abstract dialog-specific methods from
 * its parent class and wraps around an inner <tt>JDialog</tt>
 * object that this class constructs. As the <tt>ListenerDialog</tt> interface specifies, the
 * dialog created by this class will not become visible until
 * the <tt>showDialog()</tt> method is invoked. This allows for
 * the dialog to be configured in additional ways prior to it
 * being displayed. One example of this would be adding listeners,
 * but there is no limit to what subclasses could offer.
 * <p>
 * The primary purpose of this class, however, is to define
 * an API for a uniform style of dialog to be used by this
 * program. Any class that properly extends this one will
 * create a dialog with the same basic appearance and structure.
 * Even with that framework, this class offers an incredibly
 * flexible design that still allows for the creation of a
 * wide range of unique dialogs.
 * <p>
 * The dialog that is ultimately created will have the following structure:
 * <pre>
 * ________________________
 * |Title_Bar______________|
 * |_____   ______________ |
 * ||    |  |Title Panel  ||
 * ||Icon|  |_____________||
 * ||____|  ______________ |
 * |        |Details Panel||
 * |        |             ||
 * |        |_____________||
 * |        ______________ |
 * |________|______Buttons||
 * </pre>
 * 
 * To accomplish this, five abstract methods are provided for 
 * each of the five regions of this dialog. Each one allows
 * returns another element of the dialog, all of which are 
 * ultimately assembled when the <tt>showDialog()</tt> method
 * is invoked. The methods are:
 * <p>
 * <tt>createTitleBarText()</tt> should be implemented to return a 
 * <tt>String</tt> that will appear as the text in the dialog's 
 * title bar.
 * <p>
 * <tt>createIcon()</tt> should be implemented to return an 
 * <tt>ImageIcon</tt> to be displayed in the upper left of the
 * dialog. In addition, a shrunk-down version of the icon will
 * be displayed in the title bar, to the left of the title bar
 * text. For added consistency, all subclasses should use icons
 * that are the same size.
 * <p>
 * <tt>createTitlePanel()</tt> should be implemented to create
 * and return a <tt>JPanel</tt> containing a large title for
 * the dialog. It will be placed at the top of the main part 
 * of the dialog (everything to the right of the big icon).
 * <p>
 * <tt>createDetailsPanel()</tt> should be implemented to
 * create and return a <tt>JPanel</tt> this is the dialog's
 * details panel, the largest and most flexible part of the 
 * dialog. Any number of components can be placed in this panel
 * to perform whatever functions the dialog needs. It will
 * be located in the center of the main part of the dialog
 * (everything to the right of the big icon).
 * <p>
 * <tt>addButtons()</tt> should be implemented to return an
 * array of <tt>JButton</tt>s to be added to this dialog. 
 * These should be the main action buttons that complete
 * the task of the dialog (eg, "Ok", "Cancel", etc).
 * They will be added to the bottom of the dialog, right-aligned.
 * They will be ordered from left-to-right in order of
 * their index position in the array.
 * <p>
 * <b>THREAD SAFETY:</b> Swing is NOT thread safe. All
 * methods in this class MUST be invoked on the <tt>EventDispatchThread</tt>.
 * 
 * @author craig
 * @version 2.0
 */
public abstract class AbstractDefaultDialog 
extends AbstractListenerDialog {

	/**
	 * <tt>JDialog</tt> object created by this class.
	 */
	protected final JDialog dialog;
	
	/**
	 * The <tt>java.awt.Frame</tt> class that is the
	 * "owner" of this dialog.
	 */
	private final Frame owner;
	
	/**
	 * Create a non-modal dialog with no parent frame.
	 */
	protected AbstractDefaultDialog() {
		this(null, false);
	}
	
	/**
	 * Create a non-model dialog with a parent frame.
	 * 
	 * @param owner the parent frame.
	 */
	protected AbstractDefaultDialog(Frame owner){
		this(owner, false);
	}
	
	/**
	 * Create a dialog with a parent frame and its modality set
	 * by the user.
	 * 
	 * @param owner the parent frame.
	 * @param modal the modality of the dialog.
	 */
	protected AbstractDefaultDialog(Frame owner, boolean modal){
		super();
		this.owner = owner;
		dialog = createDialog(owner, modal);
	}
	
	/**
	 * Create and configure the dialog.
	 * 
	 * @param owner the parent frame.
	 * @param modal the modality of the dialog.
	 * @return the created dialog.
	 */
	private JDialog createDialog(Frame owner, boolean modal){
		JDialog dialog = new JDialog(owner);
		dialog.setModal(modal);
		dialog.setTitle(createTitleBarText());
		dialog.setIconImage(createIcon().getImage());
		dialog.setContentPane(new JPanel(new MigLayout()));
		
		return dialog;
	}
	
	/**
	 * Assemble the dialog's components.
	 */
	protected void assembleDialog(){
		dialog.getContentPane().removeAll();
		
		JPanel iconPanel = new JPanel();
		iconPanel.setLayout(new MigLayout());
		
		JLabel iconLabel = new JLabel(createIcon());
		iconPanel.add(iconLabel, "dock north, gap 10 20");
		dialog.getContentPane().add(iconPanel, "dock west");
		
		dialog.getContentPane().add(createTitlePanel(), "dock north, center, pushx");
		dialog.getContentPane().add(createDetailsPanel(), "dock center, center, pushx");
		dialog.getContentPane().add(createButtonPanel(), "dock south, center, pushx");
		
		dialog.getContentPane().revalidate();
		dialog.getContentPane().repaint();
	}

	@Override
	public void showDialog() {
		assembleDialog();
		dialog.pack();
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
	}
	
	@Override
	public void closeDialog(){
		
		dialog.dispose();
	}
	
	@Override
	public void setModal(boolean modal){
		dialog.setModal(modal);
	}
	
	@Override
	public boolean getModal(){
		return dialog.isModal();
	}
	
	/**
	 * Create the bottom panel with the dialog's action buttons.
	 * 
	 * @return the panel with the dialog's action buttons.
	 */
	private JPanel createButtonPanel(){
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new MigLayout());
		
		JButton[] buttons = addButtons();
		for(int i = 0; i < buttons.length; i++){
			JButton button = buttons[i];
			if(i == 0){
				buttonPanel.add(button, "align right, pushx");
			}
			else{
				buttonPanel.add(button, "align right");
			}
		}
		
		return buttonPanel;
	}
	
	/**
	 * Create and return the <tt>String</tt> that should be used as the text
	 * for the title bar of the dialog.
	 * 
	 * @return the text for the title bar of the dialog.
	 */
	protected abstract String createTitleBarText();
	
	/**
	 * Create and return the icon to be used as the large icon image
	 * in the dialog and the small icon in the title bar. The icons
	 * returned by all subclasses in this program should be the same
	 * size to ensure consistency.
	 * 
	 * @return the icon to be used in the dialog.
	 */
	protected abstract ImageIcon createIcon();
	
	/**
	 * Create and return the top panel, which contains title/header
	 * information for the dialog.
	 * 
	 * @return the top title panel of the dialog.
	 */
	protected abstract JPanel createTitlePanel();
	
	/**
	 * Create and return the details panel, which can contain any
	 * amount of text, buttons, or other components the dialog may
	 * require.
	 * 
	 * @return the details panel of the dialog.
	 */
	protected abstract JPanel createDetailsPanel();
	
	/**
	 * Adds buttons to the bottom button panel of the dialog. These
	 * buttons are returned in an array, and will be added from 
	 * left-to-right in the order they appear in the array. All
	 * buttons will be right-aligned in the panel.
	 * 
	 * @return an array of the buttons to add to the button panel.
	 */
	protected abstract JButton[] addButtons();

}
