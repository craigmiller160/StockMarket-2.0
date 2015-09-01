package io.craigmiller160.stockmarket.util;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

/**
 * A wrapper class to allow multiple GUI classes to share a <tt>ResourceBundle</tt>
 * that contains the program's language file. It uses thread-safe singleton 
 * instantiation to allow itself to be accessed by all classes in the program.<br><br>
 * 
 * For maximum effectiveness, this class should be instantiated for the first time in
 * the Main thread prior to GUI creation. This allows the program to have an opportunity
 * to set the language based on past user preference. Unless otherwise instructed, this 
 * class calls on the default language resource file after its first instantiation.<br><br>
 * 
 * <b>THREAD SAFETY:</b> This class, as annotated below, is thread-safe. The <tt>ResourceBundle</tt> is
 * synchronized on this class's intrinsic lock. Future edits and any client-side
 * locking should maintain this synchronization policy.
 * 
 * @author Craig
 * @version 2.0
 */
@ThreadSafe
public class Language{

	/**
	 * A tool to provide translated Strings wherever they are
	 * needed in the program. All access to this field must
	 * be synchronized on this class's intrinsic lock.
	 */
	@GuardedBy("this")
	private ResourceBundle localeText;
	
	/**
	 * The Logger for this class. It passes log records
	 * up the logging hierarchy to the "stockmarket" 
	 * logger. No Handlers should be assigned to this 
	 * logger, it should just pass the logs along.
	 */
	private static final Logger LOGGER = Logger.getLogger(
			"stockmarket.util.Language");
	
	/**
	 * Returns a shared instance of this <tt>Language</tt> module
	 * using a thread-safe Singleton instantiation. 
	 * 
	 * @return the shared instance of this class.
	 */
	public static Language getInstance(){
		return LanguageHolder.INSTANCE;
	}
	
	/**
	 * Creates a <tt>ResourceBundle</tt> set to the default language
	 * text properties file.
	 */
	private Language(){
		localeText = ResourceBundle.getBundle("LocaleText");
	}
	
	/**
	 * Sets the <tt>Locale</tt> of the <tt>ResourceBundle</tt> based on a 
	 * chosen language and country. If a user properties file has been
	 * linked to this class, the file is updated with the <tt>Locale</tt>
	 * information.
	 * 
	 * @param language the language of the <tt>Locale</tt>.
	 * @param country the country of the <tt>Locale</tt>.
	 */
	public void setLocale(String language, String country){
		Locale locale = new Locale(language, country);
		setLocale(locale);
	}
	
	/**
	 * Sets the <tt>Locale</tt> of the <tt>ResourceBundle</tt> based on a 
	 * chosen language and country. If a user properties file has been
	 * linked to this class, the file is updated with the <tt>Locale</tt>
	 * information.
	 * 
	 * @param locale the new <tt>Locale</tt> to be set.
	 */
	public void setLocale(Locale locale){
		synchronized(this){
			localeText = ResourceBundle.getBundle("LocaleText", locale);
		}
		
		LOGGER.logp(Level.INFO, this.getClass().getName(),
				"setLocale()", "Language set as: " + locale.getLanguage(), 
				new Object[]{"Locale: " + locale});
	}
	
	/**
	 * Wrapper method for the <tt>getString()</tt> method for <tt>ResourceBundle</tt>,
	 * which returns the <tt>String</tt> from the resource properties file that matches 
	 * the key parameter. 
	 * 
	 * @param key the property key of the <tt>String</tt> to be returned from the resource.
	 * @return the <tt>String</tt> to be returned from the resource.
	 */
	public synchronized String getString(String key){
		return localeText.getString(key);
	}
	
	/**
	 * Holder class to allow for a thread-safe lazy Singleton instantiation
	 * of the <tt>Language</tt> instance.
	 * 
	 * @author Craig
	 * @version 2.0
	 */
	private static class LanguageHolder{
		
		/**
		 * Instance of the enclosing <tt>Language</tt> class, used
		 * to facilitate Singleton implementation for the enclosing
		 * class.
		 */
		private static final Language INSTANCE = new Language();
		
	}
	
}

