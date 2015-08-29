package stockmarket.gui;

import static stockmarket.controller.StockMarketController.DIALOG_DISPLAYED_PROPERTY;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;

import mvp.listener.AbstractListenerView;
import mvp.listener.ListenerDialog;
import net.jcip.annotations.NotThreadSafe;
import net.miginfocom.swing.MigLayout;
import stockmarket.gui.dialog.Dialog;
import stockmarket.gui.dialog.DialogFactory;
import stockmarket.util.Language;

/**
 * A GUI frame that is the container for this program. It uses blank
 * placeholder panels which are replaced by additional components from
 * separate classes that are added at runtime. Setter methods are provided
 * to add the external components and substitute them for the blank ones.
 * <p>
 * Like all GUI classes in this program, this class separates its 
 * instantiation code from its assembly code for maximum flexibility.
 * This flexibility is fully exploited by the insertion of additional
 * components at runtime, replacing blank placeholder components in
 * the code.
 * <p>
 * <b>THREAD SAFETY:</b> Swing is NOT thread safe.
 * 
 * @author craig
 * @version 2.0
 */
@NotThreadSafe
public class Frame extends AbstractListenerView {

	//TODO work on frame split pane, need to make sure the divider resizes properly when portfolio
	//panel contents change
	
	/**
	 * Shared logger for the program.
	 */
	private static final Logger LOGGER = Logger.getLogger("stockmarket.gui.Frame");
	
	/**
	 * The Frame created by this class.
	 */
	private final JFrame frame;
	
	/**
	 * The menubar for this frame. Constructed as a blank placeholder
	 * component until an assembled one is added via the appropriate 
	 * setter method.
	 */
	private JMenuBar menuBar;
	
	/**
	 * The toolbar for this frame. Constructed as a blank placeholder
	 * component until an assembled one is added via the appropriate 
	 * setter method.
	 */
	private JComponent toolBar;
	
	/**
	 * The portfolio panel. Constructed as a blank placeholder
	 * component until an assembled one is added via the appropriate 
	 * setter method.
	 */
	private JComponent portfolioPanel;
	
	/**
	 * The stock display panel. Constructed as a blank placeholder
	 * component until an assembled one is added via the appropriate 
	 * setter method.
	 */
	private JComponent stockDisplayPanel;
	
	/**
	 * The split pane for this frame, dividing the portfolio
	 * panel and the stock display panel with an adjustable
	 * divider.
	 */
	private JSplitPane splitPane;
	
	/**
	 * Shared <tt>Language</tt> module for locale-specific text.
	 */
	private static final Language LANGUAGE = Language.getInstance();
	
	/**
	 * Create the frame.
	 */
	public Frame() {
		super();
		frame = createFrame();
		initComponents();
		assembleAndDisplayFrame();
	}
	
	/**
	 * Create the <tt>JFrame</tt> that this class wraps around.
	 * 
	 * @return the created <tt>JFrame</tt>.
	 */
	private JFrame createFrame(){
		JFrame frame = new JFrame(LANGUAGE.getString("program_title"));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		
		ImageIcon appIcon = new ImageIcon(
				this.getClass().getClassLoader().getResource("32p/stock_market.png"));
		
		frame.setIconImage(appIcon.getImage());
		frame.setContentPane(new JPanel(new MigLayout()));
		
		return frame;
	}
	
	/**
	 * Initialize the components for this GUI class.
	 */
	private void initComponents(){
		menuBar = new JMenuBar();
		toolBar = new JToolBar();
		portfolioPanel = new JPanel();
		stockDisplayPanel = new JPanel();
		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
	}
	
	/**
	 * Assemble the components in this panel.
	 */
	private void assembleAndDisplayFrame(){
		frame.getContentPane().removeAll();
		
		frame.setJMenuBar(menuBar);
		frame.getContentPane().add(toolBar, "dock north");
		
		splitPane.setLeftComponent(portfolioPanel);
		splitPane.setRightComponent(stockDisplayPanel);
		//TODO make sure this is setting the divider to the propper location
		splitPane.setDividerLocation(-1);
		frame.getContentPane().add(splitPane, "grow, push, dock center");
		
		frame.getContentPane().revalidate();
		frame.getContentPane().repaint();
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	/**
	 * Replace the blank menubar placeholder component with
	 * a fully constructed menubar from a separate class.
	 * 
	 * @param menuBar the fully constructed menubar.
	 */
	public void setMenuBar(JMenuBar menuBar){
		this.menuBar = menuBar;
		assembleAndDisplayFrame();
	}
	
	/**
	 * Replace the blank toolbar placeholder component with
	 * a fully constructed toolbar from a separate class.
	 * 
	 * @param toolBar the fully constructed toolbar.
	 */
	public void setToolBar(JComponent toolBar){
		this.toolBar = toolBar;
		assembleAndDisplayFrame();
	}
	
	/**
	 * Replace the blank portfolio panel placeholder component with
	 * a fully constructed portoflio panel from a separate class.
	 * 
	 * @param portfolioPanel the fully constructed portfolio panel.
	 */
	public void setPortfolioPanel(JComponent portfolioPanel){
		this.portfolioPanel = portfolioPanel;
		assembleAndDisplayFrame();
	}
	
	/**
	 * Replace the blank stock display panel placeholder component with
	 * a fully constructed stock display panel from a separate class.
	 * 
	 * @param stockDisplayPanel the fully constructed stock display panel.
	 */
	public void setStockDisplayPanel(JComponent stockDisplayPanel){
		this.stockDisplayPanel = stockDisplayPanel;
		assembleAndDisplayFrame();
	}
	
	/**
	 * Replace all blank placeholder components with their fully
	 * constructed counterparts from separate classes.
	 * 
	 * @param menuBar the fully constructed menubar.
	 * @param toolBar the fully constructed toolbar.
	 * @param portfolioPanel the fully constructed portfolio panel.
	 * @param stockDisplayPanel the fully constructed stock display panel.
	 */
	public void setAllComponents(JMenuBar menuBar, JComponent toolBar, 
			JComponent portfolioPanel, JComponent stockDisplayPanel){
		this.menuBar = menuBar;
		this.toolBar = toolBar;
		this.portfolioPanel = portfolioPanel;
		this.stockDisplayPanel = stockDisplayPanel;
		assembleAndDisplayFrame();
	}
	
	/**
	 * Return the frame created by this class.
	 * 
	 * @return the frame created by this class.
	 */
	public JFrame getFrame(){
		return frame;
	}

	/**
	 * {@inheritDoc}
	 * @throws IllegalArgumentException if the new value from the event is not
	 * the expected type to perform the operation.
	 */
	@Override
	public void changeProperty(PropertyChangeEvent event) {
		if(event.getPropertyName() == DIALOG_DISPLAYED_PROPERTY){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"changeProperty", "Changing Property", 
					new Object[]{"Property: " + event.getPropertyName()});
			
			if(event.getNewValue() instanceof Object[]){
				displayDialog((Object[]) event.getNewValue());
			}
			else{
				throw new IllegalArgumentException(
						"Not instance of Object[] " + event.getNewValue());
			}
		}
	}
	
	/**
	 * Displays a dialog according to the specified parameters.
	 * 
	 * @param dialogConfig the dialog configuration parameters.
	 * @throws IllegalArgumentException if any of the dialog config parameters
	 * are not the correct types for the specified dialog.
	 */
	@SuppressWarnings("unchecked") //Because it can't verify the type parameter on the list due to type erasure
	public void displayDialog(Object[] dialogConfig){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"displayDialog", "Entering method", 
				new Object[]{"Dialog: " + dialogConfig.toString()});
		
		ListenerDialog dialog = null;
		if(dialogConfig[0] instanceof Dialog){
			if((Dialog) dialogConfig[0] == Dialog.PORTFOLIO_NAME_DIALOG){
				String portfolioName = null;
				if(dialogConfig[1] != null && dialogConfig[1] instanceof String){
					portfolioName = (String) dialogConfig[1];
				}
				else{
					throw new IllegalArgumentException
					("Not a valid String: " + dialogConfig[1]);
				}
				
				dialog = DialogFactory.createPortfolioNameDialog(frame, portfolioName);
			}
			else if((Dialog) dialogConfig[0] == Dialog.OPEN_PORTFOLIO_DIALOG){
				List<String> portfolioNameList = null;
				if(dialogConfig[1] != null && dialogConfig[1] instanceof List<?>){
					portfolioNameList = (List<String>) dialogConfig[1];
				}
				else{
					throw new IllegalArgumentException
					("Not a valid name list: " + dialogConfig[1]);
				}
				
				dialog = DialogFactory.createOpenPortfolioDialog(frame, portfolioNameList);
			}
		}
		else{
			throw new IllegalArgumentException(
					"Not a valid dialog value: " + dialogConfig[0]);
		}
		
		if(dialog != null){
			dialog.addActionListener(this);
			dialog.showDialog();
			
			LOGGER.logp(Level.INFO, this.getClass().getName(), 
					"displayDialog", "Displaying Dialog: Code: " + dialogConfig[0]);
		}
		else{
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), 
					"displayDialog", "Failed to display dialog");
		}
	}
	
	

	@Override
	public Object getValueForAction(String actionCommand) {
		//LOGGER.logp(Level.FINEST, this.getClass().getName(), "getValue", 
				//"Entering method", new Object[] {"Command: " + valueToGet});
		
		// TODO Will be filled out if any values are ultimately needed
		//from this class.
		return null;
	}

}
