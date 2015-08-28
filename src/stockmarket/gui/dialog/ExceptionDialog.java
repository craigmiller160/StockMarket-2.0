package stockmarket.gui.dialog;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import net.jcip.annotations.NotThreadSafe;
import net.miginfocom.swing.MigLayout;
import stockmarket.util.Fonts;
import stockmarket.util.Language;

/**
 * A standard dialog generated when an exception occurs during the program.
 * The dialog contains a simple message about the nature of the exception, 
 * and the option to expand and show a detailed stack trace.
 * <p>
 * If this dialog is displayed before the <tt>setThrowable()</tt> method
 * has been called and a <tt>Throwable</tt> object passed to it, most of the
 * dialog will be blank. The text is set based on the <tt>Throwable</tt> it is
 * displaying.
 * <p>
 * <b>THREAD SAFETY:</b> Swing is NOT thread safe.
 * 
 * @author craig
 * @version 2.0
 */
@NotThreadSafe
public class ExceptionDialog extends AbstractDefaultDialog {

	//TODO remove this main method after testing is done
	public static void main(String[] args){
		ExceptionDialog dialog = new ExceptionDialog();
		
		try{
			throw new Exception();
		}
		catch(Exception ex){
			dialog.setThrowable(ex);
		}
		
		dialog.showDialog();
	}
	
	//TODO redo the non-throwable text setting methods. The standard exception
	//format doesn't work with the custom text dialogs.
	
	/**
	 * The title label, either <tt>Exception</tt>, <tt>RuntimeException</tt>, 
	 * or <tt>Error</tt>.
	 */
	private JLabel exceptionTitleLabel;
	
	/**
	 * The detailed exception label, containing a simple message from the
	 * exception and generic information for the user.
	 */
	private JLabel exceptionLabel;
	
	/**
	 * A label containing the full stack trace of the program.
	 */
	private JLabel stackTraceLabel;
	
	/**
	 * Button to acknowledge and close the exception dialog.
	 */
	private JButton okButton;
	
	/**
	 * Button to expand the dialog and show the full stack trace. 
	 */
	private JButton expandButton;
	
	/**
	 * The details panel of this dialog.
	 */
	private JPanel detailsPanel;
	
	/**
	 * Action Command to dismiss the dialog.
	 */
	public static final String DISMISS_ACTION = "Dismiss";
	
	/**
	 * Action Command to expand the dialog and show the stack trace.
	 */
	public static final String EXPAND_ACTION = "Expand";
	
	/**
	 * Action Command to un-expand the dialog and hide the stack trace.
	 */
	public static final String HIDE_ACTION = "Hide";
	
	/**
	 * Private <tt>Language</tt> module to provide locale-specific text.
	 */
	private static final Language LANGUAGE = Language.getInstance();
	
	/**
	 * Shared action to expand the stack trace for both the expand
	 * button and keyboard shortcuts.
	 */
	private DialogAction expandAction;
	
	/**
	 * Create a non-modal dialog with no parent frame.
	 */
	public ExceptionDialog() {
		super();
		init();
	}

	/**
	 * Create a non-modal dialog with the specified parent frame.
	 * 
	 * @param owner the parent frame.
	 */
	public ExceptionDialog(Frame owner) {
		super(owner);
		init();
	}

	/**
	 * Create a dialog with the specified parent frame and modality.
	 * 
	 * @param owner the parent frame.
	 * @param modal the modality of the dialog.
	 */
	public ExceptionDialog(Frame owner, boolean modal) {
		super(owner, modal);
		init();
	}

	@Override
	public Object getValueForAction(String valueToGet) {
		//LOGGER.logp(Level.FINEST, this.getClass().getName(), "getValue", 
				//"Entering method", new Object[] {"Command: " + valueToGet});
		
		
		// TODO Not sure this method actually needs to return
		//anything from this dialog.
		return null;
	}
	
	/**
	 * Set the <tt>Throwable</tt> object that this dialog is to
	 * display information about.
	 * 
	 * @param t the <tt>Throwable</tt> that this dialog displays 
	 * information about.
	 */
	public void setThrowable(Throwable t){
		expandButton.setVisible(true);
		expandButton.setEnabled(true);
		
		setExceptionTitleAndText(t);
		setStackTraceLabel(t);
	}
	
	/**
	 * Set the stack trace text to the appropriate label based
	 * on the <tt>Throwable</tt>.
	 * 
	 * @param t the <tt>Throwable</tt> to get the stack trace from.
	 */
	private void setStackTraceLabel(Throwable t){
		StringBuilder builder = new StringBuilder("<html>");
		
		builder.append(t + "<br>");
		StackTraceElement[] steArray = t.getStackTrace();
		for(StackTraceElement ste : steArray){
			builder.append("at " + ste + "<br>");
		}
		builder.append("</html>");
		
		stackTraceLabel.setText(builder.toString());
	}
	
	/**
	 * Set the title and text labels of the exception based
	 * on the <tt>Throwable</tt>.
	 * 
	 * @param t the <tt>Throwable</tt> to use for the title and text of
	 * the dialog.
	 */
	private void setExceptionTitleAndText(Throwable t){
		String title = "";
		if(t instanceof RuntimeException){
			title = LANGUAGE.getString("runtime_exception_title");
			
		}
		else if(t instanceof Error){
			title = LANGUAGE.getString("error_title");
		}
		else{
			title = LANGUAGE.getString("exception_title");
		}

		setExceptionTitle(title);
		
		setExceptionMessage(t.toString());
	}
	
	/**
	 * Set the title of the Exception Dialog.
	 * 
	 * @param title the title.
	 */
	public void setExceptionTitle(String title){
		exceptionTitleLabel.setText(title);
	}
	
	/**
	 * Set the details text of the Exception Dialog
	 * 
	 * @param message the detailed message to add.
	 */
	public void setExceptionMessage(String message){
		StringBuilder builder = new StringBuilder(
				"<html><body style='width: 330px'>");
		
		builder.append(LANGUAGE.getString("exception_starting_text") + "<br><br>");
		builder.append("<b>" + LANGUAGE.getString("message") + "</b>: ");
		builder.append(message + "<br><br>");
		builder.append(LANGUAGE.getString("exception_ending_text"));
		builder.append(" <u>craigmiller160@gmail.com</u>.</body></html>");
		
		exceptionLabel.setText(builder.toString());
	}

	private void init() {
		exceptionLabel = createLabel("", Fonts.SMALL_FIELD_FONT);
		exceptionTitleLabel = createLabel("", Fonts.LABEL_FONT);
		stackTraceLabel = createLabel("", null);
		
		okButton = createButton(LANGUAGE.getString("ok_button_label"), null,
				LANGUAGE.getString("ok_button_label"),
				DISMISS_ACTION);
		
		expandAction = createAction(LANGUAGE.getString("expand_button"), null, 
				LANGUAGE.getString("expand_tooltip"), EXPAND_ACTION);
		
		expandButton = new JButton(expandAction);
		expandButton.setFont(Fonts.SMALL_LABEL_FONT);
		expandButton.setVisible(false);
		expandButton.setEnabled(false);
		
		configureInputActionMaps();
	}
	
	/**
	 * Configure the input and action maps of components in 
	 * this dialog to enable keyboard actions.
	 */
	private void configureInputActionMaps(){
		
		DialogAction dismissAction = new DialogAction();
		dismissAction.setActionCommand(DISMISS_ACTION);
		
		JRootPane rootPane = dialog.getRootPane();
		
		//RootPane, press escape to dismiss dialog
		rootPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
				DISMISS_ACTION);
		rootPane.getActionMap().put(DISMISS_ACTION, dismissAction);
		
		//Expand button, press escape to dismiss dialog
		expandButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
				DISMISS_ACTION);
		expandButton.getActionMap().put(DISMISS_ACTION, dismissAction);
		
		//Expand button, press enter to expand/hide
		expandButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				EXPAND_ACTION);
		expandButton.getActionMap().put(EXPAND_ACTION, expandAction);
		
		//Ok button, press escape or enter to dismiss dialog
		okButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
				DISMISS_ACTION);
		okButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				DISMISS_ACTION);
		okButton.getActionMap().put(DISMISS_ACTION, dismissAction);
		
	}
	
	private JLabel createLabel(String text, Font font){
		JLabel label = new JLabel(text);
		label.setFont(font);
		
		return label;
	}

	@Override
	protected String createTitleBarText() {
		return LANGUAGE.getString("exception_dialog_title");
	}

	@Override
	protected ImageIcon createIcon() {
		return new ImageIcon(
				this.getClass().getClassLoader().getResource(
				"96p/error.png"));
	}
	
	@Override
	protected JPanel createTitlePanel() {
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new MigLayout());
		titlePanel.add(exceptionTitleLabel, "center, pushx");
		
		return titlePanel;
	}
	
	@Override
	protected JPanel createDetailsPanel() {
		detailsPanel = new JPanel();
		detailsPanel.setLayout(new MigLayout());
		
		detailsPanel.add(exceptionLabel, "wrap");
		
		//detailsPanel.add(expandButton, "align right, pushx, wrap");
		
		return detailsPanel;
	}
	
	@Override
	protected JButton[] addButtons() {
		return new JButton[] {expandButton, okButton};
	}
	
	/**
	 * Create a button based on the parameters inputed.
	 * 
	 * @param text the button's text.
	 * @param icon the button's icon
	 * @param toolTipText the button's tool tip text.
	 * @param actionCommand the button's action command.
	 * @return the button created.
	 */
	private JButton createButton(String text, ImageIcon icon, String toolTipText, 
			String actionCommand){
		DialogAction action = new DialogAction();
		action.setText(text);
		action.setToolTipText(toolTipText);
		action.setActionCommand(actionCommand);
		action.setIcon(icon);
		
		JButton button = new JButton(action);
		button.setFont(Fonts.SMALL_LABEL_FONT);
		
		return button;
	}
	
	/**
	 * Create an action based on the parameters inputted.
	 * 
	 * @param text the action's text to display.
	 * @param icon the action's icon to display.
	 * @param toolTipText the action's tool tip text to display.
	 * @param actionCommand the action's action command.
	 * @return the action created.
	 */
	private DialogAction createAction(String text, ImageIcon icon, String toolTipText, 
			String actionCommand){
		DialogAction action = new DialogAction();
		action.setText(text);
		action.setIcon(icon);
		action.setToolTipText(toolTipText);
		action.setActionCommand(actionCommand);
		
		return action;
	}
	
	/**
	 * Private implementation of the <tt>Action</tt> interface for this dialog.
	 * Allows for actions to be shared between components and keyboard shortcuts.
	 * 
	 * @author craig
	 * @version 2.0
	 */
	private class DialogAction extends AbstractAction{

		/**
		 * SerialVersionUID for serialization support.
		 */
		private static final long serialVersionUID = -3151599049989465129L;
		
		/**
		 * Create a new action.
		 */
		public DialogAction(){
			super();
		}
		
		/**
		 * Set the text for the action's visual component.
		 * 
		 * @param text the text for the action.
		 */
		public void setText(String text){
			putValue(AbstractAction.NAME, text);
		}
		
		/**
		 * Set the tooltip text for the action's visual component.
		 * 
		 * @param text the tool tip text for the action.
		 */
		public void setToolTipText(String text){
			putValue(AbstractAction.SHORT_DESCRIPTION, text);
		}
		
		/**
		 * Set the action command for the action.
		 * 
		 * @param command the action command for the action.
		 */
		public void setActionCommand(String command){
			putValue(AbstractAction.ACTION_COMMAND_KEY, command);
		}
		
		/**
		 * Set the icon for the action's visual component.
		 * 
		 * @param icon the icon for the action.
		 */
		public void setIcon(ImageIcon icon){
			putValue(AbstractAction.SMALL_ICON, icon);
		}
		
		@Override
		public void actionPerformed(ActionEvent event) {
			if(event.getActionCommand() == DISMISS_ACTION){
				ExceptionDialog.this.closeDialog();
			}
			else if(event.getActionCommand() == EXPAND_ACTION){
				//Change action command, text, and tooltip
				setActionCommand(HIDE_ACTION);
				setText(LANGUAGE.getString("hide_button"));
				setToolTipText(LANGUAGE.getString("hide_tooltip"));
				
				//Add stack trace label, repaint and re-pack.
				detailsPanel.add(stackTraceLabel);
				detailsPanel.revalidate();
				detailsPanel.repaint();
				dialog.pack();
			}
			else if(event.getActionCommand() == HIDE_ACTION){
				//Change action command, text, and tooltip
				setActionCommand(EXPAND_ACTION);
				setText(LANGUAGE.getString("expand_button"));
				setToolTipText(LANGUAGE.getString("expand_tooltip"));
				
				//Remove stack trace label, repaint and re-pack.
				detailsPanel.remove(stackTraceLabel);
				detailsPanel.revalidate();
				detailsPanel.repaint();
				dialog.pack();
			}
		}
		
	}

}
