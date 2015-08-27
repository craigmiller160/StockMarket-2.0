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
import java.util.ArrayList;
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
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import stockmarket.util.Fonts;
import stockmarket.util.Language;

public class OpenPortfolioDialog extends AbstractDefaultDialog {

	//TODO remove this test main method
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				OpenPortfolioDialog dialog = new OpenPortfolioDialog();
				
				List<String> test = new ArrayList<>();
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 2asdfdsfa-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 3-$52,628.00sdfasdfsdafasdfasdf-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 4-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 5-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 6-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 7-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 8-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");
				test.add("1-New Portfolio 1-$52,628.00-05/22/2015 15:22:33:556");

				dialog.setSavedPortfolioList(test);
				
				dialog.showDialog();
			}
			
		});
	}
	
	private static final Logger LOGGER = Logger.getLogger("stockmarket.gui.dialog.OpenPortfolioDialog");
	
	private static final Language LANGUAGE = Language.getInstance();
	
	private static final String CANCEL_ACTION = "Cancel";
	
	private JList<String> portfolioNameList;
	private JScrollPane listScrollPane;
	
	private JButton openButton;
	private JButton cancelButton;
	
	private JLabel titleLabel;
	private JLabel detailsInstructionsLabel;
	
	public OpenPortfolioDialog() {
		super();
		init();
	}

	public OpenPortfolioDialog(Frame owner) {
		super(owner);
		init();
	}

	public OpenPortfolioDialog(Frame owner, boolean modal) {
		super(owner, modal);
		init();
	}

	@Override
	public Object getValueForAction(String valueToGet) {
		Object result = null;
		if(valueToGet == OPEN_SELECTED_PORTFOLIO_ACTION){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"getValue", "Entering method", 
					new Object[] {"Command: " + valueToGet});
			
			int index = getSelectedIndex();
			result = portfolioNameList.getModel().getElementAt(index);
		}
		
		return result;
	}
	
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
	
	private JLabel createLabel(String text, Font font){
		JLabel label = new JLabel(text);
		label.setFont(font);
		
		return label;
	}
	
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
					//TODO show dialog warning that you must select a portfolio to open one.
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
		
		private JLabel createLabel(String text, Font font){
			JLabel label = new JLabel(text);
			label.setFont(font);
			
			return label;
		}
		
	}

}
