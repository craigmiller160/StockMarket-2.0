package stockmarket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import net.jcip.annotations.NotThreadSafe;
import stockmarket.controller.StockMarketController;
import stockmarket.gui.BuySellPanel;
import stockmarket.gui.Frame;
import stockmarket.gui.MenuBar;
import stockmarket.gui.PortfolioPanel;
import stockmarket.gui.SearchPanel;
import stockmarket.gui.StockDetailsPanel;
import stockmarket.gui.StockDisplayPanel;
import stockmarket.gui.StockHistoryPanel;
import stockmarket.gui.ToolBar;
import stockmarket.gui.dialog.DialogFactory;
import stockmarket.gui.dialog.ListenerDialog;
import stockmarket.model.GUIStateModel;
import stockmarket.model.StockDisplayModel;
import stockmarket.util.Language;
import stockmarket.util.LoggerCSVFormat;


/**
 * A class to initialize and run the program. Upon initialization, this class
 * loads properties files, sets the <tt>LookAndFeel</tt> of the GUI, initializes 
 * models, controllers, and other critical elements, and starts the GUI on the
 * <tt>EventDispatchThread</tt>. When the program is finished, it saves any 
 * property changes so they are available for future runs.<br><br>
 * 
 * This class is not thread-safe. It should only be run as the Main thread for
 * the program, and should not be accessed by any other threads during runtime.
 * 
 * @author Craig
 * @version 2.0
 */
@NotThreadSafe
public class Main {
	
	//TODO making a conflicting commit with different branches
	
	//TODO when everything else is done, create a splash image
	
	//TODO the early error dialogs, redo them to fit the new dialog API
	
	/**
	 * The logger for this program. With the exception of the default
	 * root logger, this is the top of the logger hierarchy. It needs
	 * to be configured prior to the launch of the program to have the
	 * necessary <tt>Level</tt>s and <tt>Handler</tt>s set.<br><br>
	 */
	private static final Logger LOGGER = Logger.getLogger("stockmarket");
	
	/**
	 * Constant properties of this program that never change.
	 */
	private static Properties defaultProperties;
	
	/**
	 * Properties of this program that the user can change based
	 * on personal preference.
	 */
	private static Properties userProperties;
	
	/**
	 * Utility object for managing the language setting of this program.
	 */
	private static final Language LANGUAGE = Language.getInstance();
	
	
	//TODO remove these constants from this class and store them in
	//an I/O utility class
	
	/**
	 * Constant value for the file path of the program directory.
	 */
	private static final String PROGRAM_DIRECTORY = System.getProperty("user.home") 
												+ "/StockMarket";
	
	/**
	 * Constant value for the file path of the log directory of
	 * the program.
	 */
	private static final String LOG_DIRECTORY = PROGRAM_DIRECTORY + "/Log";
	
	/**
	 * Constant value for the file path of the properties directory of
	 * the program.
	 */
	private static final String PROPERTIES_DIRECTORY = PROGRAM_DIRECTORY + "/Properties";
	
	//TODO document this
	private static final StockMarketController stockMarketController = new StockMarketController();
	
	/**
	 * Main method to begin running the <tt>StockMarket</tt> program. Calls 
	 * on static methods in this class to initialize and assemble the various
	 * components of the program. When everything is ready to run, it initializes
	 * the GUI.
	 * 
	 * @param args command line arguments that may be passed to this program.
	 */
	public static void main(String[] args){
		//Check and set up the program's save directory
		boolean saveLocationExists = verifyDirectory();
		if(!saveLocationExists){
			
			filesystemConnectionFailPopup();
			
		}
		
		//Configure the program's logger
		configureLogger();
		
		//Load the default properties file as a 
		//resource stream from the jar
		loadDefaultProperties();
		
		//Load the user properties file, or create
		//it if the program hasn't been run before
		loadUserProperties();
		
		//Sets the language based on user.properties
		configureLanguageModule();
		
		//Set the LookAndFeel of the GUI
		setLookAndFeel();
		
		//Initialize the models and configure the controller
		init();
		
		//Initialize the gui and link to the controller
		initGUI();
		
	}
	
	/**
	 * Verifies that the program is able to properly access a save
	 * location on the filesystem before running. This method should 
	 * be run before any other when the program is initialized.<br><br>
	 * 
	 * 99.9% of the time this method will return true, and the program
	 * can proceed normally from here.<br><br>
	 * 
	 * In the rare case that it returns false, it likely means that 
	 * there is some issue with the JVM's ability to read the 
	 * filesystem of the OS this program is running on. The program
	 * should response to this method returning false by giving the 
	 * user an error message and shutting down.
	 * 
	 * @return whether the program could establish a save directory on 
	 * the hosts filesystem. 
	 */
	private static boolean verifyDirectory(){
		//TODO gotta get these directories in sync with
		//the properties file
		File logDirectory = new File(LOG_DIRECTORY);
		File propertiesDirectory = new File(PROPERTIES_DIRECTORY);
		
		boolean logDirectoryExists = true;
		boolean saveDirectoryExists = true;
		boolean propertiesDirectoryExists = true;
		
		if(! logDirectory.exists()){
			logDirectoryExists = logDirectory.mkdirs();
		}
		
		if(! propertiesDirectory.exists()){
			propertiesDirectoryExists = propertiesDirectory.mkdirs();
		}
		
		return logDirectoryExists && saveDirectoryExists 
				&& propertiesDirectoryExists;
	}
	
	/**
	 * This method is only invoked in the extremely rare case that the 
	 * program fails to set up a save location on the file system. It
	 * creates a popup window with an error message to inform the user
	 * that such a serious issue has occurred and the program will now
	 * terminate.<br><br>
	 * 
	 * Because this message can appear so early in the program, it is
	 * locked as English-Only because the user will not have had an 
	 * opportunity to set an alternate language yet.
	 */
	private static void filesystemConnectionFailPopup(){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				JLabel errorText = new JLabel(
						"<html>StockMarket was unable to properly read your filesystem "
						+ "and was unable to initialize.<br><br>Please contact the developer "
						+ "at <u>craigmiller160@gmail.com</u>");
				
				JOptionPane.showMessageDialog(null, 
						errorText, 
						"Program Failed to Initialize!",
						JOptionPane.ERROR_MESSAGE);
				
				System.exit(1);
			}
		});
	}
	
	/**
	 * Configure the properties of the main logger. Because the logger
	 * in the <tt>Main</tt> class will be at the root of the hierarchy, 
	 * all logging events will ultimately be passed to it. The settings
	 * on this logger determine how all logging will be recorded for the 
	 * entire program.
	 */
	private static void configureLogger(){
		//TODO set level to INFO as the default setting
		LOGGER.setLevel(Level.FINEST);
		
		try{
			FileHandler fileHandler = new FileHandler(
					LOG_DIRECTORY + "/StockMarketLog.csv");
			fileHandler.setFormatter(new LoggerCSVFormat());
			fileHandler.setLevel(Level.FINEST);
			
			LOGGER.addHandler(fileHandler);
			LOGGER.setUseParentHandlers(false);
		}
		catch(IOException ex){
			loggingWontSavePopup();
		}
	}
	
	/**
	 * If the logger's <tt>FileHandler</tt> fails to properly initialize
	 * (most likely due to an <tt>IOException</tt>), this popup appears to 
	 * warn the user that Debugging Mode will not work.<br><br>
	 * 
	 * Because this message can appear so early in the program, it is
	 * locked as English-Only because the user will not have had an 
	 * opportunity to set an alternate language yet.
	 */
	private static void loggingWontSavePopup(){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run() {
				JLabel errorText = new JLabel(
						"<html>StockMarket was unable to properly configure "
						+ "the program's logger. Debugging Mode will not work.<br>"
						+ "If this problem persists, please contact the developer at "
						+ "<u>craigmiller160@gmail.com</u>.");
				
				JOptionPane.showMessageDialog(null, 
						errorText, 
						"Program Failed to Initialize!",
						JOptionPane.ERROR_MESSAGE);
			}
		});
	}
	
	/**
	 * Loads the default properties file as a resource stream from the classloader.
	 * 
	 * @throws NullPointerException if the file doesn't load properly and has
	 * no contents.
	 */
	private static void loadDefaultProperties(){
		defaultProperties = new Properties();
		
		try {
			//TODO find out if you need to use a finally block to kill
			//this stream after loading it
			defaultProperties.load(
					Main.class.getClassLoader().getResourceAsStream(
							"default.properties"));
			
			if(defaultProperties.size() == 0){
				//TODO come up with better way to convey this exception.
				//TODO change this to throwing an IOException so it just
				//gets caught in the catch block below.
				NullPointerException exception = new NullPointerException(
						"Default Properties file didn't load properly");
				
				LOGGER.logp(Level.SEVERE, Main.class.getName(), 
						"loadDefaultProperties()",
						"Default Properties file has no contents",
						exception);
				throw exception;
			}
			
			LOGGER.logp(Level.INFO, Main.class.getName(),
					"loadDefaultProperties()",
					"Default Properties file loaded successfully");
			
		}
		catch (IOException ex){
			LOGGER.logp(Level.SEVERE, Main.class.getName(), 
					"loadDefaultProperties()", 
					"Failed to load Default Properties", ex);
		}
	}
	
	//TODO document this
	private static void loadUserProperties(){
		if(defaultProperties != null){
			userProperties = new Properties(defaultProperties);
		}
		else{
			userProperties = new Properties();
		}
		
		File userPropertiesFile = new File(PROPERTIES_DIRECTORY + "/user.properties");
		
		if(userPropertiesFile.exists()){
			try(FileInputStream userPropertiesStream = new FileInputStream(userPropertiesFile)){
				userProperties.load(userPropertiesStream);
				
				LOGGER.logp(Level.INFO, Main.class.getName(), "loadUserProperties()",
						"User properties loaded from file");
			}
			catch(IOException ex){
				LOGGER.logp(Level.SEVERE, Main.class.getName(), "loadUserProperties()",
						"User properties file failed to load", ex);
			}
			
		}
		else{
			LOGGER.logp(Level.INFO, Main.class.getName(), "loadUserProperties()",
					"User properties file didn't exist, creating new one");
			
			saveUserProperties(userPropertiesFile);
		}
	}
	
	/**
	 * Saves a brand new user.properties file to the save directory.<br><br> 
	 * 
	 * If the user.properties file had to be created for the first time
	 * when the <tt>loadUserProperties()</tt> method ran, this method should
	 * be called to save the file for future use.<br><br>
	 * 
	 * If the <tt>userProperties</tt> field has not yet been instantiated, this 
	 * method will do nothing, as it has no function as long as that field has
	 * a <tt>null</tt> value.
	 * 
	 * @param userPropertiesFile the filepath to save the user.properties file to.
	 */
	private static void saveUserProperties(File userPropertiesFile){
		if(userProperties != null){
			try(FileOutputStream userPropertiesStream = new FileOutputStream(userPropertiesFile)){
				
				userProperties.store(userPropertiesStream, null);
				
				LOGGER.logp(Level.INFO, Main.class.getName(), "saveUserProperties()", 
						"New user properties file saved successfully");
			}
			catch(IOException ex){
				LOGGER.logp(Level.SEVERE, Main.class.getName(), "saveUserProperties()",
						"User properties file failed to save", ex);
			}
		}
	}
	
	/**
	 * Configures the <tt>Language</tt> module with a <tt>Locale</tt> based
	 * on the saved language setting in the user.properties file.<br><br>
	 * 
	 * If the <tt>userProperties</tt> field has not been instantiated yet,
	 * this method will do nothing, as it has no function as long as that field
	 * is <tt>null</tt>.
	 */
	private static void configureLanguageModule(){
		if(userProperties != null){
			String language = userProperties.getProperty("language");
			String country = userProperties.getProperty("country");
			
			Locale locale = new Locale(language, country);
			
			LANGUAGE.setLocale(locale);
		}
	}
	
	/**
	 * Set the <tt>LookAndFeel</tt> for the <tt>UIManager</tt>. Must be
	 * invoked prior to initializing the GUI. 
	 */
	private static void setLookAndFeel(){
		String lookAndFeelName = "Nimbus";
		
		try{
			for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
				if(lookAndFeelName.equals(info.getName())){
					UIManager.setLookAndFeel(info.getClassName());
    			}
    		}
    	}catch(Exception ex){
    		LOGGER.logp(Level.SEVERE, Main.class.getName(), 
    				"setLookAndFeel()", "Look and Feel was not set", ex);
    	}
	}
	
	//TODO document this
	private static void init(){
		GUIStateModel guiStateModel = new GUIStateModel();
		StockDisplayModel stockDisplayModel = new StockDisplayModel();
		
		stockMarketController.setThreadPoolProperties(6, 20, 60L, 
				TimeUnit.MILLISECONDS);
		
		stockMarketController.addPropertyModel(guiStateModel);
		//stockMarketController.addPropertyModel(portfolioModel);
		stockMarketController.addPropertyModel(stockDisplayModel);
	}
	
	//TODO document this
	private static void initGUI(){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				Thread.currentThread().setUncaughtExceptionHandler(
						new GUIUncaughtExceptionHandler());
				
				SearchPanel searchPanel = new SearchPanel();
				StockDetailsPanel stockDetailsPanel = new StockDetailsPanel();
				StockHistoryPanel stockHistoryPanel = new StockHistoryPanel();
				BuySellPanel buySellPanel = new BuySellPanel();
				
				StockDisplayPanel stockDisplayPanel = new StockDisplayPanel();
				stockDisplayPanel.setAllComponents(searchPanel.getPanel(), 
						stockDetailsPanel.getPanel(), stockHistoryPanel.getPanel(), 
						buySellPanel.getPanel());
				
				MenuBar menuBar = new MenuBar();
				ToolBar toolBar = new ToolBar();
				PortfolioPanel portfolioPanel = new PortfolioPanel();
				
				Frame frame = new Frame();
				frame.setAllComponents(menuBar.getMenuBar(), toolBar.getToolBar(), 
						portfolioPanel.getPanel(), stockDisplayPanel.getPanel());
				
				stockMarketController.addView(frame);
				stockMarketController.addView(menuBar);
				stockMarketController.addView(toolBar);
				stockMarketController.addView(portfolioPanel);
				stockMarketController.addView(stockDisplayPanel);
				stockMarketController.addView(searchPanel);
				stockMarketController.addView(buySellPanel);
				stockMarketController.addView(stockDetailsPanel);
				stockMarketController.addView(stockHistoryPanel);
			}
		});
	}
	
	//TODO document this stuff below
	
	private static void displayExceptionDialog(final Throwable t){
		ListenerDialog exceptionDialog = DialogFactory.createExceptionDialog(null, t);
		exceptionDialog.showDialog();
	}
	
	private static class GUIUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler{

		@Override
		public void uncaughtException(Thread thread, Throwable throwable) {
			if(SwingUtilities.isEventDispatchThread()){
				displayExceptionDialog(throwable);
			}
			else{
				SwingUtilities.invokeLater(new Runnable(){
					@Override
					public void run() {
						displayExceptionDialog(throwable);
					}
				});
			}
			
			LOGGER.logp(Level.SEVERE, this.getClass().getName(), "uncaughtException()", 
					"Uncaught Exception: " + thread.getName() + "." + thread.getId(), throwable);
		}
		
	}
	
}
