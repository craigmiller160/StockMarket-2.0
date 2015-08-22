package stockmarket.gui.dialog;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

/**
 * An abstract implementation of the <tt>ListenerDialog</tt>
 * interface, which creates a dialog that can pass events to and
 * be abstractly interacted with by one or more listening controllers
 * added to this class. This class also defines an API for a uniform design
 * for all dialogs in this program.
 * <p>
 * This class also implements the <tt>ActionListener</tt> interface
 * to facilitate the passing of <tt>ActionEvent</tt>s from actionable
 * components to external controllers. Any actionable components should
 * add <tt>this</tt> as their <tt>ActionListener</tt>, and this class's
 * <tt>actionPerformed()</tt> method will pass the event to any listening
 * controllers.
 * <p>
 * This design does not forbid the use of other listeners of any
 * type in subclasses. However, those listeners should only be used
 * for minor events. Anything that impacts the state of the program
 * should utilize the external listening controller to perform the action.
 * <p>
 * Implementing this abstract class requires implementing six abstract methods.
 * <p>
 * The <tt>getValue()</tt> method, inherited from the <tt>ListenerView</tt>,
 * interface, allows for the external controller to acquire values from this
 * class without needing any direct knowledge of it. Any values that a 
 * controller could need to respond to an action from this view should be made
 * available from that method.
 * <p>
 * The six new abstract methods provided are for the design of the dialog itself.
 * Implementing them defines the nature of specific elemenets of the dialog's GUI,
 * and those elements are then placed in pre-defined locations in the dialog.
 * The format of the dialog is as follows:
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
 * The methods to create this are:
 * <p>
 * <tt>createTitleBarText()</tt> returns a <tt>String and assigns the 
 * text for the title bar.
 * <p>
 * <tt>createIcon()</tt> returns an <tt>ImageIcon</tt> that is placed
 * in the upper left of the dialog. A smaller version of the icon is 
 * also assigned as the icon in the title bar.
 * <p>
 * <tt>createTitlePanel()</tt> returns a <tt>JPanel</tt> which contains
 * the big title for the dialog.
 * <p>
 * <tt>createDetailsPanel()</tt> returns a <tt>JPanel</tt> which
 * contains the details for the dialog. This could be text information,
 * text fields for input, buttons, or any number of other UI components.
 * <p>
 * <tt>addButtons()</tt> returns a <tt>JButton</tt> array and adds
 * buttons to the bottom right of the dialog. These buttons should
 * be the final actions buttons of the dialog, the ones that close 
 * the dialog and either commit or discard any data changes the dialog
 * may or may not have made.
 * <p>
 * One final method, <tt>init()</tt>, is provided as an optional method 
 * to initialize any components or elements that need to be initialized
 * for this dialog. If this method is not needed by a subclass, its 
 * implementation can be left blank.
 * <p>
 * <b>THREAD SAFETY:</b> Swing is NOT thread safe. All
 * methods in this class MUST be invoked on the <tt>EventDispatchThread</tt>.
 * 
 * @author craig
 * @version 2.0
 */
public abstract class AbstractDefaultDialog 
implements ActionListener, ListenerDialog {

	/**
	 * List of listeners/controllers assigned to this class.
	 */
	private final List<ActionListener> listeners;
	
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
		listeners = new ArrayList<>();
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
	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}
	
	@Override
	public void removeActionListener(ActionListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Passes an event from an actionable component in the GUI to the
	 * listeners assigned to this dialog.
	 * 
	 * @param event the event that needs to be executed.
	 */
	@Override
	public final void actionPerformed(ActionEvent event){
		ActionEvent newEvent = new ActionEvent(
				this, ActionEvent.ACTION_PERFORMED, event.getActionCommand());
		
		for(ActionListener l : listeners){
			l.actionPerformed(newEvent);
		}
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
	 * in the dialog and the small icon in the title bar.
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
