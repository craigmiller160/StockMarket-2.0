package stockmarket.gui.dialog;

import static stockmarket.controller.StockMarketController.OPEN_SELECTED_PORTFOLIO_ACTION;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import mvp.listener.ListenerDialog;
import net.miginfocom.swing.MigLayout;
import stockmarket.util.Fonts;
import stockmarket.util.Language;

public class OpenPortfolioDialog extends AbstractDefaultDialog {

	/**
	 * The logger for the program.
	 */
	private static final Logger LOGGER = Logger.getLogger("stockmarket.gui.dialog.OpenPortfolioDialog");
	
	/**
	 * Shares <tt>Language</tt> module for locale-specific text.
	 */
	private static final Language LANGUAGE = Language.getInstance();
	
	/**
	 * Action command for canceling the operation.
	 */
	private static final String CANCEL_ACTION = "Cancel";
	
	/**
	 * The list of portfolio names.
	 */
	private JList<String> portfolioNameList;
	
	/**
	 * The scroll pane for the list of portfolio names.
	 */
	private JScrollPane listScrollPane;
	
	/**
	 * The button to open the portfolio.
	 */
	private JButton openButton;
	
	/**
	 * The button to cancel the operation.
	 */
	private JButton cancelButton;
	
	/**
	 * The dialog's title label.
	 */
	private JLabel titleLabel;
	
	/**
	 * The instructions label for the details panel.
	 */
	private JLabel detailsInstructionsLabel;
	
	/**
	 * Create a new, non-modal dialog with no owner.
	 */
	public OpenPortfolioDialog() {
		super();
		init();
	}

	/**
	 * Create a new, non-modal dialog with the specified
	 * owner.
	 * 
	 * @param owner the owner of the dialog.
	 */
	public OpenPortfolioDialog(Frame owner) {
		super(owner);
		init();
	}

	/**
	 * Create a new dialog with the specified owner and modality.
	 * 
	 * @param owner the owner of the dialog.
	 * @param modal the modality of the dialog.
	 */
	public OpenPortfolioDialog(Frame owner, boolean modal) {
		super(owner, modal);
		init();
	}

	@Override
	public Object getValueForAction(String actionCommand) {
		Object result = null;
		if(actionCommand == OPEN_SELECTED_PORTFOLIO_ACTION){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"getValue", "Entering method", 
					new Object[] {"Command: " + actionCommand});
			
			int index = getSelectedIndex();
			result = portfolioNameList.getModel().getElementAt(index);
		}
		
		return result;
	}
	
	/**
	 * Get the index of the selected portfolio.
	 * 
	 * @return the index of the selected portfolio.
	 */
	public int getSelectedIndex(){
		return portfolioNameList.getSelectedIndex();
	}
	
	
	@SuppressWarnings("serial")
	public void setSavedPortfolioList(final List<String> savedPortfolioList){
		portfolioNameList.setModel(new AbstractListModel<String>(){
			@Override
			public String getElementAt(int index) {
				return savedPortfolioList.get(index);
			}

			@Override
			public int getSize() {
				return savedPortfolioList.size();
			}
		});
	}

	/**
	 * Initialize components and values for this dialog.
	 */
	private void init() {
		portfolioNameList = new JList<>();
		portfolioNameList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		portfolioNameList.setCellRenderer(new SavedPortfolioListCellRenderer());
		portfolioNameList.setVisibleRowCount(4);
		portfolioNameList.addMouseListener(new DialogMouseListener());
		
		listScrollPane = new JScrollPane(portfolioNameList);
		listScrollPane.getVerticalScrollBar().setUnitIncrement(15);
		listScrollPane.getHorizontalScrollBar().setUnitIncrement(15);
		
		openButton = createButton(LANGUAGE.getString("open_button_label"), 
				LANGUAGE.getString("open_button_tooltip"), 
				OPEN_SELECTED_PORTFOLIO_ACTION);
		
		cancelButton = createButton(LANGUAGE.getString("cancel_button_label"), 
				LANGUAGE.getString("cancel_button_tooltip"), CANCEL_ACTION);
		
		titleLabel = createLabel(LANGUAGE.getString("open_portfolio_label"), 
				Fonts.LABEL_FONT);
		
		detailsInstructionsLabel = createLabel("<html><body style='width: 300px'>" +
				LANGUAGE.getString("open_portfolio_instructions") + "</body></html>", 
				Fonts.SMALL_FIELD_FONT);
		
		configureInputActionMaps();
	}
	
	/**
	 * Configure the input and action maps for this dialog's components.
	 */
	private void configureInputActionMaps(){
		DialogAction openAction = new DialogAction();
		openAction.setActionCommand(OPEN_SELECTED_PORTFOLIO_ACTION);
		
		DialogAction cancelAction = new DialogAction();
		cancelAction.setActionCommand(CANCEL_ACTION);
		
		JRootPane root = dialog.getRootPane();
		
		root.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
				CANCEL_ACTION);
		root.getActionMap().put(CANCEL_ACTION, cancelAction);
		
		openButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				OPEN_SELECTED_PORTFOLIO_ACTION);
		openButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
				CANCEL_ACTION);
		openButton.getActionMap().put(OPEN_SELECTED_PORTFOLIO_ACTION, openAction);
		openButton.getActionMap().put(CANCEL_ACTION, cancelAction);
		
		cancelButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				CANCEL_ACTION);
		cancelButton.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
				CANCEL_ACTION);
		cancelButton.getActionMap().put(CANCEL_ACTION, cancelAction);
		
		portfolioNameList.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), 
				OPEN_SELECTED_PORTFOLIO_ACTION);
		portfolioNameList.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), 
				CANCEL_ACTION);
		portfolioNameList.getActionMap().put(OPEN_SELECTED_PORTFOLIO_ACTION, openAction);
		portfolioNameList.getActionMap().put(CANCEL_ACTION, cancelAction);
	}
	
	/**
	 * Utility class for creating labels.
	 * 
	 * @param text the label's text.
	 * @param font the label's font.
	 * @return the created label.
	 */
	private JLabel createLabel(String text, Font font){
		JLabel label = new JLabel(text);
		label.setFont(font);
		
		return label;
	}
	
	/**
	 * Utility class for creating buttons.
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

	@Override
	protected String createTitleBarText() {
		return LANGUAGE.getString("open_portfolio_label");
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
		
		detailsPanel.add(detailsInstructionsLabel, "gap 0 0 10 10, center, pushx, dock north");
		detailsPanel.add(listScrollPane, "center, growx, gap 0 10");
		
		return detailsPanel;
	}

	@Override
	protected JButton[] addButtons() {
		return new JButton[] {openButton, cancelButton};
	}
	
	/**
	 * Mouse listener for the saved portfolio list. Allows for 
	 * detecting double-click operations for indicating a selection.
	 * 
	 * @author craig
	 * @version 2.0
	 */
	private class DialogMouseListener extends MouseAdapter{
		
		@Override
		public void mouseClicked(MouseEvent event){
			if(event.getClickCount() == 2){
				ActionEvent newEvent = new ActionEvent(
						OpenPortfolioDialog.this,
						ActionEvent.ACTION_PERFORMED, 
						OPEN_SELECTED_PORTFOLIO_ACTION);
				
				//Pass the event to this class's actionPerformed method
				//so that it is picked up on by external listeners.
				OpenPortfolioDialog.this.actionPerformed(newEvent);
				OpenPortfolioDialog.this.closeDialog();
			}
		}
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
				OpenPortfolioDialog.this.closeDialog();
			}
			else if(event.getActionCommand() == OPEN_SELECTED_PORTFOLIO_ACTION){
				if(getSelectedIndex() < 0){
					ListenerDialog dialog = DialogFactory.createExceptionDialog(
							null, "No Portfolio Selected", 
							"You must select a portfolio to open");
					dialog.showDialog();
				}
				else{
					//Pass the event to this class's actionPerformed method
					//so that it is picked up on by external listeners.
					OpenPortfolioDialog.this.actionPerformed(event);
					OpenPortfolioDialog.this.closeDialog();
				}
			}
		}
		
	}
	
	/**
	 * Cell renderer for the saved portfolios list.
	 * 
	 * @author craig
	 * @version 2.0
	 */
	private class SavedPortfolioListCellRenderer implements ListCellRenderer<String>{

		@Override
		public Component getListCellRendererComponent(
				JList<? extends String> list, String value, int index,
				boolean isSelected, boolean cellHasFocus) {
			
			JPanel cellPanel = new JPanel();
			cellPanel.setLayout(new MigLayout());
			cellPanel.setBorder(BorderFactory.createEtchedBorder());
			
			String[] values = value.split("-");
			
			JLabel nameLabel = createLabel(LANGUAGE.getString("portfolio_name_label") + ": ", 
					Fonts.SMALL_LABEL_FONT);
			JLabel nameValue = createLabel(values[1], Fonts.SMALL_FIELD_FONT);
			
			JLabel netWorthLabel = createLabel(LANGUAGE.getString("net_worth_label") + ": ", 
					Fonts.SMALL_LABEL_FONT);
			JLabel netWorthValue = createLabel(values[2], Fonts.SMALL_FIELD_FONT);
			
			JLabel timestampLabel = createLabel(LANGUAGE.getString("timestamp_label") + ": ", 
					Fonts.SMALL_LABEL_FONT);
			JLabel timestampValue = createLabel(values[3], Fonts.SMALL_FIELD_FONT);
			
			cellPanel.add(nameLabel, "");
			cellPanel.add(nameValue, "");
			cellPanel.add(timestampLabel, "align right, pushx");
			cellPanel.add(timestampValue, "align right, wrap");
			cellPanel.add(netWorthLabel, "");
			cellPanel.add(netWorthValue, "span 3");
			
			if(isSelected){
				cellPanel.setBackground(Color.CYAN);
			}
			else{
				cellPanel.setBackground(Color.WHITE);
			}
			
			
			return cellPanel;
		}
		
		/**
		 * Utility class to create a label.
		 * 
		 * @param text the label's text.
		 * @param font the label's font.
		 * @return the created label.
		 */
		private JLabel createLabel(String text, Font font){
			JLabel label = new JLabel(text);
			label.setFont(font);
			
			return label;
		}
		
	}

}
