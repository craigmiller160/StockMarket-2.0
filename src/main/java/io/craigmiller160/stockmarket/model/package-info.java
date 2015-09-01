/**
 * 
 */
/**
 * Contains the models that store mutable data values for this program.
 * As the user interacts with the GUI, information about the program's 
 * state is stored in the models in this class. Models pass updated 
 * state information to the view via <tt>PropertyChangeEvent</tt>s,
 * following the JavaBean bound property format.
 * <p>
 * All models extend <tt>AbstractPropertyModel</tt> from the MVP
 * Framework. The framework handles updating and listening to these
 * models, ensuring a dependency inverse relationship between them
 * and the other elements of this program.
 * <p>
 * There are three main models in this package. <tt>GUIStateModel</tt>
 * focuses on tracking the current state which UI elements are 
 * enabled/displayed at any given time. <tt>StockDisplayModel</tt>
 * tracks which stock is current selected to be displayed in the 
 * main part of the window. And the <tt>PortfolioModel</tt> tracks
 * the current contents of the user's stock portfolio.
 * 
 * @author Craig
 * @since Version 2.0
 */
package io.craigmiller160.stockmarket.model;