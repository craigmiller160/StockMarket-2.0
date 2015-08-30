package io.craigmiller160.stockmarket.gui.dialog;

import java.awt.Frame;
import java.util.List;

import mvp.listener.ListenerDialog;

/**
 * Factory class for creating dialogs in the program.
 * 
 * @author craig
 * @version 2.0
 */
public class DialogFactory {

	/**
	 * Private constructor prevents this class from being instantiated.
	 */
	private DialogFactory() {}
	
	/**
	 * Create a dialog for setting the name of the portfolio.
	 * 
	 * @param parentFrame the frame that own's this dialog.
	 * @return the portfolio name dialog.
	 */
	public static ListenerDialog createPortfolioNameDialog(Frame parentFrame){
		PortfolioNameDialog dialog = new PortfolioNameDialog(parentFrame, true);
		
		return dialog;
	}
	
	/**
	 * Create a dialog for setting the name of the portfolio, with the 
	 * currently set portfolio name already in the text field.
	 * 
	 * @param parentFrame the frame that own's this dialog.
	 * @param currentPortfolioName the current name of the portfolio.
	 * @return the portfolio name dialog.
	 */
	public static ListenerDialog createPortfolioNameDialog(Frame parentFrame, 
			String currentPortfolioName){
		PortfolioNameDialog dialog = new PortfolioNameDialog(parentFrame, true);
		dialog.setPortfolioNameFieldText(currentPortfolioName);
		
		return dialog;
	}
	
	/**
	 * Create a dialog for exceptions that are the result of errors in the program. The
	 * exceptions this dialog is intended for are problems with the program that generally
	 * should not occur and need to be resolved. Detailed information about the exception
	 * is derived from the <tt>Throwable</tt> parameter, and a full stack trace is provided
	 * in the dialog.
	 * <p>
	 * For exceptions caused by user error, that will occur regularly during the normal 
	 * operation of the program, use <tt>createExceptionDialog(Frame,String,String)</tt>
	 * instead.
	 * 
	 * @param parentFrame the frame that owns this dialog.
	 * @param t the exception this dialog is displaying.
	 * @return the exception dialog.
	 */
	public static ListenerDialog createExceptionDialog(Frame parentFrame, Throwable t){
		ExceptionDialog dialog = new ExceptionDialog(parentFrame, true);
		dialog.setThrowable(t);
		
		return dialog;
	}
	
	/**
	 * Create a dialog for exceptions that can occur regularly during the operation of
	 * the program. These should be used for normal problems that can occur due to user 
	 * error, and as such should be described in a user-friendly way by the title and
	 * message parameters. This should NOT be used for exceptions that are the result
	 * of errors in the program, and as such a full stack trace is not provided. 
	 * 
	 * @param parentFrame the frame that owns this dialog.
	 * @param title the title of the dialog.
	 * @param message the message of the dialog.
	 * @return the exception dialog.
	 */
	public static ListenerDialog createExceptionDialog(Frame parentFrame, String title, String message){
		ExceptionDialog dialog = new ExceptionDialog(parentFrame, true);
		dialog.setExceptionTitle(title);
		dialog.setExceptionMessage(message);
		
		return dialog;
	}
	
	/**
	 * Create a dialog for displaying a list of the names of all saved portfolios,
	 * allowing the user to select one to open in the program.
	 * 
	 * @param parentFrame the frame that own's this dialog.
	 * @param portfolioNameList the list of portfolio names to display.
	 * @return the open portfolio dialog.
	 */
	public static ListenerDialog createOpenPortfolioDialog(Frame parentFrame, 
			List<String> portfolioNameList){
		OpenPortfolioDialog dialog = new OpenPortfolioDialog(parentFrame, true);
		dialog.setSavedPortfolioList(portfolioNameList);
		
		return dialog;
	}

}
