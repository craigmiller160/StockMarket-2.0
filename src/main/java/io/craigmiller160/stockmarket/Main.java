package io.craigmiller160.stockmarket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import io.craigmiller160.mvp.listener.ListenerDialog;
import io.craigmiller160.stockmarket.gui.dialog.DialogFactory;
import io.craigmiller160.stockmarket.util.Language;
import net.jcip.annotations.NotThreadSafe;


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
 * @version 2.3
 */
@NotThreadSafe
public class Main {

	
	//TODO hibernate seems to be keeping sessions/connections open when it should be
	//closing them
	
	//TODO if the log file is open for the program, it crashes at startup
	
	//TODO if a stock has been completely sold, Hibernate doesn't remove it
	//from the database when the stock is saved
	
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
	
	
	/**
	 * Main method to begin running the <tt>StockMarket</tt> program. Calls 
	 * on static methods in this class to initialize and assemble the various
	 * components of the program. When everything is ready to run, it initializes
	 * the GUI.
	 * 
	 * @param args command line arguments that may be passed to this program.
	 */
	public static void main(String[] args){
		try{
			//Check and set up the program's save directory
			boolean saveLocationExists = verifyDirectory();
			if(!saveLocationExists){
				//The error message here is hard-coded and not locale-based because
				//it occurs before the language module has a chance to load.
				String errorText = "StockMarket was unable to properly read "
						+ "your filesystem and was unable to initialize. "
						+ "Please contact the developer at <u>craigmiller160@gmail.com";
				
				displayExceptionDialog("Critical Error!", errorText);
				System.exit(1);
			}
			
			//Configure the program's logger
			//configureLogger();
			
			//Load the default properties file as a 
			//resource stream from the jar
			loadDefaultProperties();
			
			//Separate try-catch block because an exception here
			//shouldn't kill the whole program.
			try{
				//Load the user properties file, or create
				//it if the program hasn't been run before
				loadUserProperties();
			}
			catch(IOException ex){
				displayExceptionDialog(ex);
			}
			
			//Sets the language based on user.properties
			configureLanguageModule();
			
			//Set the LookAndFeel of the GUI
			setLookAndFeel();
			
			//Initialize the gui and link to the controller
			initGUI();
		}
		catch(Exception ex){
			displayExceptionDialog(ex);
			System.exit(1);
		}
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
		File logDirectory = new File(LOG_DIRECTORY);
		File propertiesDirectory = new File(PROPERTIES_DIRECTORY);
		
		boolean logDirectoryExists = true;
		boolean propertiesDirectoryExists = true;
		
		if(! logDirectory.exists()){
			logDirectoryExists = logDirectory.mkdirs();
		}
		
		if(! propertiesDirectory.exists()){
			propertiesDirectoryExists = propertiesDirectory.mkdirs();
		}
		
		return logDirectoryExists && propertiesDirectoryExists;
	}
	
	/**
	 * Loads the default properties file as a resource stream from the classloader.
	 * 
	 * @throws NullPointerException if the file doesn't load properly and has
	 * no contents.
	 * @throws IOException if the default properties fail to load properly.
	 */
	private static void loadDefaultProperties() throws IOException{
		defaultProperties = new Properties();
		defaultProperties.load(
				Main.class.getClassLoader().getResourceAsStream(
						"default.properties"));
		
		if(defaultProperties.size() == 0){
			throw new IOException();
		}
	}
	
	/**
	 * Load the user-set properties for the program, which are saved
	 * so they can persist between program runs.
	 * @throws IOException if the default properties fail to load properly.
	 */
	private static void loadUserProperties() throws IOException{
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
			}
		}
		else{
			createUserPropertiesFile(userPropertiesFile);
		}
	}
	
	//TODO at the moment, user.properties are not altered during runtime.
	//If that feature is added later on, a method saving them will need to
	//be executed at that time.
	/**
	 * Creates and saves a brand new user.properties file to the save directory.<br><br> 
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
	 * @throws IOException if unable to save the user.properties file.
	 */
	private static void createUserPropertiesFile(File userPropertiesFile) throws IOException{
		if(userProperties == null){
			if(defaultProperties != null){
				userProperties = new Properties(defaultProperties);
			}
			else{
				userProperties = new Properties();
			}
		}
		
		try(FileOutputStream userPropertiesStream = new FileOutputStream(userPropertiesFile)){
			userProperties.store(userPropertiesStream, null);
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
	 * @throws Exception if the look-and-feel is unable to be applied.
	 */
	private static void setLookAndFeel() throws Exception{
		String lookAndFeelName = "Nimbus";
		for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
			if(lookAndFeelName.equals(info.getName())){
				UIManager.setLookAndFeel(info.getClassName());
			}
		}
    }
	
	/**
	 * Initialize the program's GUI.
	 */
	private static void initGUI(){
		SwingUtilities.invokeLater(new Runnable(){
			@Override
			public void run(){
				Thread.currentThread().setUncaughtExceptionHandler(
						new GUIUncaughtExceptionHandler());
				
				@SuppressWarnings("resource") //Shutdown hook is registered, it'll be closed when VM shuts down
				ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
				
				((AbstractApplicationContext)context).registerShutdownHook();
			}
		});
	}
	
	
	/**
	 * Display the appropriate exception dialog for any exceptions
	 * that occur.
	 * 
	 * @param t the exception that occurred.
	 */
	private static void displayExceptionDialog(final Throwable t){
		//Check if on EDT before displaying the dialog.
		if(SwingUtilities.isEventDispatchThread()){
			ListenerDialog exceptionDialog = DialogFactory.createExceptionDialog(null, t);
			exceptionDialog.showDialog();
		}
		else{
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run() {
					ListenerDialog exceptionDialog = DialogFactory.createExceptionDialog(null, t);
					exceptionDialog.showDialog();
				}
			});
		}
	}
	
	private static void displayExceptionDialog(final String title, final String text){
		//Check if on EDT before displaying the dialog.
		if(SwingUtilities.isEventDispatchThread()){
			ListenerDialog exceptionDialog = DialogFactory.createExceptionDialog(null, title, text);
			exceptionDialog.showDialog();
		}
		else{
			SwingUtilities.invokeLater(new Runnable(){
				@Override
				public void run() {
					ListenerDialog exceptionDialog = DialogFactory.createExceptionDialog(null, title, text);
					exceptionDialog.showDialog();
				}
			});
		}
	}
	
	/**
	 * The uncaught exception handler for the GUI. This class is added
	 * to the <tt>EventDispatchThread</tt> so that any exceptions that 
	 * occur on that thread are properly logged at runtime.
	 * 
	 * @author craig
	 * @version 2.0
	 */
	private static class GUIUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler{

		@Override
		public void uncaughtException(Thread thread, Throwable throwable) {
			displayExceptionDialog(throwable);
			//TODO add additional handling for specific RuntimeExceptions that
			//should result in system shutdown
			if(throwable instanceof Error){
				System.exit(1);
			}
		}
		
	}
	
}
