package stockmarket.gui;

import static stockmarket.controller.StockMarketController.ABOUT_PROGRAM_ACTION;
import static stockmarket.controller.StockMarketController.CLOSE_PORTFOLIO_ACTION;
import static stockmarket.controller.StockMarketController.COMPONENTS_ENABLED_PROPERTY;
import static stockmarket.controller.StockMarketController.DEBUG_MENU_ACTION;
import static stockmarket.controller.StockMarketController.ENABLE_LOOKUP_STOCK_LOADED;
import static stockmarket.controller.StockMarketController.ENABLE_NO_PORTFOLIO_OPEN;
import static stockmarket.controller.StockMarketController.ENABLE_NO_STOCK_LOADED;
import static stockmarket.controller.StockMarketController.ENABLE_OWNED_STOCK_LOADED;
import static stockmarket.controller.StockMarketController.EXIT_PROGRAM_ACTION;
import static stockmarket.controller.StockMarketController.LANGUAGE_MENU_ACTION;
import static stockmarket.controller.StockMarketController.NEW_PORTFOLIO_ACTION;
import static stockmarket.controller.StockMarketController.OPEN_PORTFOLIO_ACTION;
import static stockmarket.controller.StockMarketController.SAVE_PORTFOLIO_ACTION;

import java.awt.Event;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import net.jcip.annotations.NotThreadSafe;
import stockmarket.util.Language;

/**
 * A GUI menubar for this program.
 * <p>
 * Like all GUI classes in this program, this class separates its 
 * instantiation code from its assembly code for maximum flexibility.
 * <p>
 * <b>THREAD SAFETY:</b> Swing is NOT thread safe.
 * 
 * @author craig
 * @version 2.0
 */
@NotThreadSafe
public class MenuBar extends AbstractListenerView {

	/**
	 * Shared logger for the program.
	 */
	private static final Logger LOGGER = Logger.getLogger("stockmarket.gui.MenuBar");
	
	/**
	 * The menubar created by this class.
	 */
	private final JMenuBar menuBar;
	
	/**
	 * Menu item for creating a new portfolio.
	 */
	private JMenuItem newPortfolioItem;
	
	/**
	 * Menu item for opening a new portfolio.
	 */
	private JMenuItem openPortfolioItem;
	
	/**
	 * Menu item for saving a new portfolio.
	 */
	private JMenuItem savePortfolioItem;
	
	/**
	 * Menu item for closing a new portfolio.
	 */
	private JMenuItem closePortfolioItem;
	
	/**
	 * Menu item for exiting the program.
	 */
	private JMenuItem exitProgramItem;
	
	/**
	 * Menu item for setting the program language.
	 */
	private JMenuItem languageMenuItem;
	
	/**
	 * Menu item for setting debug mode.
	 */
	private JMenuItem debugMenuItem;
	
	/**
	 * Menu item for the about program dialog.
	 */
	private JMenuItem aboutProgramItem;
	
	/**
	 * The File menu.
	 */
	private JMenu fileMenu;

	/**
	 * The Options menu.
	 */
	private JMenu optionsMenu;
	
	/**
	 * The Help menu.
	 */
	private JMenu helpMenu;
	
	/**
	 * Shared <tt>Language</tt> module for locale-specific text.
	 */
	private static final Language LANGUAGE = Language.getInstance();
	
	/**
	 * Create the menubar.
	 */
	public MenuBar() {
		super();
		menuBar = createMenuBar();
		initMenus();
		initMenuItems();
		assembleMenuBar();
	}
	
	/**
	 * Create the <tt>JMenuBar</tt> that this class wraps around.
	 * 
	 * @return the created <tt>JMenuBar</tt>.
	 */
	private JMenuBar createMenuBar(){
		JMenuBar menuBar = new JMenuBar();
		
		return menuBar;
	}
	
	/**
	 * Create the different menus for the menubar.
	 */
	private void initMenus(){
		fileMenu = new JMenu(LANGUAGE.getString("file_menu_label"));
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		optionsMenu = new JMenu(LANGUAGE.getString("options_menu_label"));
		optionsMenu.setMnemonic(KeyEvent.VK_O);
		
		helpMenu = new JMenu(LANGUAGE.getString("help_menu_label"));
		helpMenu.setMnemonic(KeyEvent.VK_H);
	}
	
	/**
	 * Create the menu items for the menus in the menubar.
	 */
	private void initMenuItems(){
		newPortfolioItem = createMenuItem(LANGUAGE.getString("new_portfolio_label"), "new", 
				LANGUAGE.getString("new_portfolio_tooltip"), KeyEvent.VK_N, 
				NEW_PORTFOLIO_ACTION, KeyEvent.VK_N, Event.CTRL_MASK);
		
		openPortfolioItem = createMenuItem(LANGUAGE.getString("open_portfolio_label"), "open", 
				LANGUAGE.getString("open_portfolio_tooltip"), KeyEvent.VK_O,
				OPEN_PORTFOLIO_ACTION, KeyEvent.VK_O, Event.CTRL_MASK);
		
		savePortfolioItem = createMenuItem(LANGUAGE.getString("save_portfolio_label"), "save", 
				LANGUAGE.getString("save_portfolio_tooltip"), KeyEvent.VK_S,
				SAVE_PORTFOLIO_ACTION, KeyEvent.VK_S, Event.CTRL_MASK);
		savePortfolioItem.setEnabled(false);
		
		closePortfolioItem = createMenuItem(LANGUAGE.getString("close_portfolio_label"), "close", 
				LANGUAGE.getString("close_portfolio_tooltip"), KeyEvent.VK_C, 
				CLOSE_PORTFOLIO_ACTION);
		closePortfolioItem.setEnabled(false);
		
		exitProgramItem = createMenuItem(LANGUAGE.getString("exit_program_label"), "exit", 
				LANGUAGE.getString("exit_program_tooltip"), KeyEvent.VK_X, 
				EXIT_PROGRAM_ACTION);
		
		languageMenuItem = createMenuItem(LANGUAGE.getString("language_menu_label"), "language",
				LANGUAGE.getString("language_menu_tooltip"), KeyEvent.VK_L,
				LANGUAGE_MENU_ACTION);
		
		debugMenuItem = createMenuItem(LANGUAGE.getString("debug_menu_label"), "debug",
				LANGUAGE.getString("debug_menu_tooltip"), KeyEvent.VK_D,
				DEBUG_MENU_ACTION);
		
		aboutProgramItem = createMenuItem(LANGUAGE.getString("about_program_label"), "about", 
				LANGUAGE.getString("about_program_tooltip"), KeyEvent.VK_A, 
				ABOUT_PROGRAM_ACTION);
	}
	
	/**
	 * Assemble the menubar.
	 */
	private void assembleMenuBar(){
		assembleFileMenu();
		menuBar.add(fileMenu);
		
		assembleOptionsMenu();
		menuBar.add(optionsMenu);
		
		assembleHelpMenu();
		menuBar.add(helpMenu);
	}
	
	/**
	 * Assemble the file menu.
	 */
	private void assembleFileMenu(){
		fileMenu.add(newPortfolioItem);
		fileMenu.add(openPortfolioItem);
		fileMenu.add(savePortfolioItem);
		fileMenu.add(closePortfolioItem);
		fileMenu.add(exitProgramItem);
	}
	
	/**
	 * Assemble the options menu.
	 */
	private void assembleOptionsMenu(){
		optionsMenu.add(languageMenuItem);
		optionsMenu.add(debugMenuItem);
	}
	
	/**
	 * Assemble the help menu.
	 */
	private void assembleHelpMenu(){
		helpMenu.add(aboutProgramItem);
	}
	
	/**
	 * Return the menubar created by this class.
	 * 
	 * @return the menubar created by this class.
	 */
	public JMenuBar getMenuBar(){
		return menuBar;
	}
	
	/**
	 * Utility method for creating menu items based on the specified
	 * parameters. The menu items created by this method have accelerator
	 * keyboard shortcuts.
	 * 
	 * @param text the menu item's text.
	 * @param iconName the menu item's icon.
	 * @param toolTip the menu item's tool tip text.
	 * @param mnemonicKeycode the menu item's mnemonic keycode.
	 * @param actionCommand the menu item's action command.
	 * @param acceleratorKeycode the menu item's accelerator keycode.
	 * @param acceleratorModifiers the menu item's accelerator modifiers.
	 * @return the created menu item.
	 */
	private JMenuItem createMenuItem(String text, String iconName, String toolTip,
			int mnemonicKeycode, String actionCommand, int acceleratorKeycode, 
			int acceleratorModifiers){
		JMenuItem item = createMenuItem(text, iconName, toolTip, mnemonicKeycode, actionCommand);
		item.setAccelerator(KeyStroke.getKeyStroke(acceleratorKeycode, acceleratorModifiers));
		
		return item;
	}
	
	/**
	 * Utility method for creating menu items based on the specified
	 * parameters.
	 * 
	 * @param text the menu item's text.
	 * @param iconName the menu item's icon.
	 * @param toolTip the menu item's tool tip text.
	 * @param mnemonicKeycode the menu item's mnemonic keycode.
	 * @param actionCommand the menu item's action command.
	 * @return the created menu item.
	 */
	private JMenuItem createMenuItem(String text, String iconName, String toolTip, 
			int mnemonicKeycode, String actionCommand){
		JMenuItem item = new JMenuItem(text, new ImageIcon(
				this.getClass().getClassLoader().getResource("15p/" + iconName + ".png")));
		item.setMnemonic(mnemonicKeycode);
		item.setToolTipText(toolTip);
		item.setActionCommand(actionCommand);
		item.addActionListener(this);
		
		return item;
	}
	

	@Override
	public void changeProperty(PropertyChangeEvent event) {
		if(event.getPropertyName() == COMPONENTS_ENABLED_PROPERTY){
			LOGGER.logp(Level.FINEST, this.getClass().getName(), 
					"changeProperty", "Changing Property", 
					new Object[]{"Property: " + event.getPropertyName()});
			
			changeComponentsEnabled((Integer) event.getNewValue());
		}

	}
	
	/**
	 * Change which components are enabled in this class.
	 * 
	 * @param componentsEnabledState the state of the program to enable
	 * components for.
	 */
	public void changeComponentsEnabled(int componentsEnabledState){
		LOGGER.logp(Level.FINEST, this.getClass().getName(), 
				"changeComponentsEnabled", "Entering method", 
				new Object[] {"Enable State: " + componentsEnabledState});
		
		if(componentsEnabledState == ENABLE_NO_PORTFOLIO_OPEN){
			savePortfolioItem.setEnabled(false);
			closePortfolioItem.setEnabled(false);
		}
		else if(componentsEnabledState == ENABLE_NO_STOCK_LOADED){
			savePortfolioItem.setEnabled(true);
			closePortfolioItem.setEnabled(true);
		}
		else if(componentsEnabledState == ENABLE_LOOKUP_STOCK_LOADED){
			
		}
		else if(componentsEnabledState == ENABLE_OWNED_STOCK_LOADED){
			
		}
	}

	@Override
	public Object getValueForAction(String valueToGet) {
		//LOGGER.logp(Level.FINEST, this.getClass().getName(), "getValue", 
				//"Entering method", new Object[] {"Command: " + valueToGet});
		
		// TODO Auto-generated method stub
		return null;
	}

}
