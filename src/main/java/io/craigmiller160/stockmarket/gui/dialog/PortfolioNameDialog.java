package io.craigmiller160.stockmarket.gui.dialog;

import static io.craigmiller160.stockmarket.controller.StockMarketController.CANCEL_ACTION;
import static io.craigmiller160.stockmarket.controller.StockMarketController.SAVE_PORTFOLIO_NAME_ACTION;
import io.craigmiller160.stockmarket.util.Fonts;
import io.craigmiller160.stockmarket.util.Language;

import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import net.jcip.annotations.NotThreadSafe;
import net.miginfocom.swing.MigLayout;

/**
 * A dialog to set the name of the portfolio. In order to conform with
 * the <tt>ListenerView</tt> interface, this dialog invokes the class's
 * <tt>actionPerformed()</tt> method from within the <tt>Action</tt>
 * classes' own <tt>actionPerformed()</tt> methods. This allows for
 * actions to be shared between components and keyboard shortcuts,
 * while still passing the event and information about it to external 
 * listeners.
 * <p>
 * <b>THREAD SAFETY:</b> Swing is NOT thread safe.
 * 
 * @author craig
 * @version 2.0
 */
@NotThreadSafe
public class PortfolioNameDialog extends AbstractDefaultDialog {
	
	/**
	 * Shared <tt>Language</tt> module for locale-based text support.
	 */
	private static final Language LANGUAGE = Language.getInstance();
	
	/**
	 * Shared logger for the program.
	 */
	private static final Logger LOGGER = Logger.getLogger("stockmarket.gui.dialog.PortfolioNameDialog");
	
	/**
	 * The title label of the dialog.
	 */
	private JLabel titleLabel;
	
	/**
	 * The label for the name field.
	 */
	private JLabel nameFieldLabel;
	
	/**
	 * The text field for the portfolio name.
	 */
	private JTextField portfolioNameField;
	
	/**
	 * The button to save the name.
	 */
	private JButton saveButton;
	
	/**
	 * The button to cancel the dialog and NOT save the name.
	 */
	private JButton cancelButton;
	
	/**
	 * Create a non-modal dialog with no parent frame.
	 */
	public PortfolioNameDialog() {
		super();
		init();
	}

	/**
	 * Create a non-modal dialog with the specified parent frame.
	 * 
	 * @param owner the parent frame.
	 */
	public PortfolioNameDialog(Frame owner) {
		super(owner);
		init();
	}

	/**
	 * Create a dialog with the specified frame and modality.
	 * 
	 * @param owner the parent frame.
	 * @param modal the modality of the dialog.
	 */
	public PortfolioNameDialog(Frame owner, boolean modal) {
		super(owner, modal);
		init();
	}
	
	/**
	 * Initialize the values and components of this dialog.
	 */
	private void init(){
		titleLabel = createLabel(LANGUAGE.getString("name_dialog_title"), 
				Fonts.LABEL_FONT);
		
		nameFieldLabel = createLabel(LANGUAGE.getString("name_field_label") + ": ",
				Fonts.SMALL_LABEL_FONT);
		
		portfolioNameField = createField("", 15);
		
		saveButton = createButton(LANGUAGE.getString("save_button_label"), 
				LANGUAGE.getString("save_name_tooltip"), SAVE_PORTFOLIO_NAME_ACTION);
		
		cancelButton = createButton(LANGUAGE.getString("cancel_button_label"), 
				LANGUAGE.getString("cancel_button_tooltip"), CANCEL_ACTION);
		
		configureInputActionMaps();
		setFocus();
	}
	
	/**
	 * Ensure that the portfolio name field has focus when this
	 * dialog opens.
	 */
	private void setFocus(){
		dialog.addWindowListener(new WindowAdapter(){
			@Override
			public void windowOpened(WindowEvent event){
				nameFieldRequestFocus();
			}
		});
	}
	
	/**
	 * Request focus for the name field. Used by the
	 * Window Listener.
	 */
	private void nameFieldRequestFocus(){
		portfolioNameField.requestFocus();
	}
	
	/**
	 * Configure the input and action maps for keyboard shortcuts.
	 */
	private void configureInputActionMaps(){
		DialogAction saveAction = new DialogAction();
		saveAction.setActionCommand(SAVE_PORTFOLIO_NAME_ACTION);
		
		DialogAction cancelAction = new DialogAction();
		cancelAction.setActionCommand(CANCEL_ACTION);
		
		JRootPane rootPane = dialog.getRootPane();
		rootPane.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
				CANCEL_ACTION);
		rootPane.getActionMap().put(CANCEL_ACTION, cancelAction);
		
		portfolioNameField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
				CANCEL_ACTION);
		portfolioNameField.getActionMap().put(CANCEL_ACTION, cancelAction);
		
		saveButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				SAVE_PORTFOLIO_NAME_ACTION);
		saveButton.getActionMap().put(SAVE_PORTFOLIO_NAME_ACTION, saveAction);
		
		saveButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
				CANCEL_ACTION);
		saveButton.getActionMap().put(CANCEL_ACTION, cancelAction);
		
		cancelButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				CANCEL_ACTION);
		cancelButton.getActionMap().put(CANCEL_ACTION, cancelAction);
		
		cancelButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
				CANCEL_ACTION);
		cancelButton.getActionMap().put(CANCEL_ACTION, cancelAction);
	}
	
	/**
	 * Create a button for this class, based on the specified parameters.
	 * <b>NOTE:</b> The <tt>actionPerformed()</tt> method of this class is
	 * invoked from inside the <tt>actionPerformed()</tt> method of the action
	 * assigned to this button.
	 * This means that this class should NOT be added as an <tt>ActionListener</tt>
	 * on these buttons, as it will result in the event being fired twice.
	 * 
	 * @param text the button's text.
	 * @param toolTipText the button's tool tip text.
	 * @param actionCommand the button's action command.
	 * @return the created button.
	 */
	private JButton createButton(String text, String toolTipText, 
			String actionCommand){
		DialogAction action = new DialogAction();
		action.setText(text);
		action.setToolTipText(toolTipText);
		action.setActionCommand(actionCommand);
		
		JButton button = new JButton(action);
		button.setFont(Fonts.SMALL_LABEL_FONT);
		
		return button;
	}
	
	/**
	 * Create a label based on the specified parameters.
	 * 
	 * @param text the label's text.
	 * @param font the lable's font.
	 * @return the created label.
	 */
	private JLabel createLabel(String text, Font font){
		JLabel label = new JLabel(text);
		label.setFont(font);
		
		return label;
	}
	
	/**
	 * Creates a text field based on the specified parameters.
	 * <b>NOTE:</b> The <tt>actionPerformed()</tt> method of this class is
	 * invoked from inside the <tt>actionPerformed()</tt> method of the action
	 * assigned to this field.
	 * This means that this class should NOT be added as an <tt>ActionListener</tt>
	 * on these buttons, as it will result in the event being fired twice.
	 * 
	 * @param text the field's text.
	 * @param length the field's length.
	 * @return the created field.
	 */
	private JTextField createField(String text, int length){
		JTextField field = new JTextField(text, length);
		field.setFont(Fonts.SMALL_FIELD_FONT);
		
		DialogAction saveAction = new DialogAction();
		saveAction.setActionCommand(SAVE_PORTFOLIO_NAME_ACTION);
		field.setAction(saveAction);
		
		field.requestFocus(true);
		
		return field;
	}
	
	/**
	 * Set the text of the portfolio name field.
	 * 
	 * @param text the text to be set to the field.
	 */
	public void setPortfolioNameFieldText(String text){
		portfolioNameField.setText(text);
	}

	@Override
	public Object getValueForAction(String actionCommand) {
		if(actionCommand == SAVE_PORTFOLIO_NAME_ACTION){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), "getValue", 
					"Entering method", new Object[] {"Command: " + actionCommand});
			
			//Get the text from the name field, then clear the field
			String name = getPortfolioNameFieldText();
			setPortfolioNameFieldText("");
			return name;
		}
		else{
			return null;
		}
	}
	
	/**
	 * Returns the current text in the portfolio name field.
	 * 
	 * @return the text of the portfolio name field.
	 */
	public String getPortfolioNameFieldText(){
		return portfolioNameField.getText();
	}

	@Override
	protected String createTitleBarText() {
		return LANGUAGE.getString("name_dialog_title");
	}

	@Override
	protected ImageIcon createIcon() {
		return new ImageIcon(
				this.getClass().getClassLoader().getResource(
				"96p/stock_market.png"));
	}

	@Override
	protected JPanel createTitlePanel() {
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new MigLayout());
		
		titlePanel.add(titleLabel, "center, pushx");
		
		return titlePanel;
	}

	@Override
	protected JPanel createDetailsPanel() {
		JPanel detailsPanel = new JPanel();
		detailsPanel.setLayout(new MigLayout());
		
		detailsPanel.add(nameFieldLabel, "");
		detailsPanel.add(portfolioNameField, "");
		
		return detailsPanel;
	}
	
	@Override
	protected JButton[] addButtons() {
		return new JButton[] {saveButton, cancelButton};
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
		private static final long serialVersionUID = -1457575774803394397L;

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
		
		@Override
		public void actionPerformed(ActionEvent event) {
			if(event.getActionCommand() == CANCEL_ACTION){
				PortfolioNameDialog.this.closeDialog();
			}
			else if(event.getActionCommand() == SAVE_PORTFOLIO_NAME_ACTION){
				//Pass the event to this class's actionPerformed method
				//so that it is picked up on by external listeners.
				PortfolioNameDialog.this.actionPerformed(event);
				PortfolioNameDialog.this.closeDialog();
			}
		}
		
	}

	

}
