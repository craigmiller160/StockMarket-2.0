package stockmarket.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.jcip.annotations.ThreadSafe;
import stockmarket.gui.ProperyChangeView;
import stockmarket.model.AbstractPropertyModel;

/**
 * Base controller API for building a program based on the Model-View-Controller
 * design pattern. It provides methods to abstractly handle as many models and views
 * as it needs to, storing them in synchronized lists until they are needed.
 * <p>
 * <b>IMPLEMENTATION NOTES:</b>
 * <p>
 * <b>1)</b> Controller classes should access and modify model properties through
 * the protected <tt>setModelProperty()</tt> and <tt>getModelProperty()</tt> methods,
 * which use reflection to invoke the corresponding methods in the model.
 * <p>
 * <b>THREAD SAFETY:</b> Thread safety is delegated to the synchronized list 
 * collection. The lists of models and views contained in this class are both
 * synchronized lists, so any add/remove/set operations will be thread-safe.
 * However, subclasses should be careful about implementing any iteration of
 * these lists, as that will require additional client-side locking. 
 * <p>
 * <b>NOTE:</b> As of Version 2.0, the <tt>setModelProperty(String,Object)</tt> 
 * method will only work with model setter methods that accept a single parameter.
 * Multiple-parameter methods will fail at this time.
 * 
 * @author Craig
 * @version 2.0
 * @see stockmarket.gui.ProperyChangeView PropertyChangeView
 * @see stockmarket.model.AbstractPropertyModel AbstractPropertyModel
 */
@ThreadSafe
public abstract class AbstractController 
implements PropertyChangeListener{

	/**
	 * A list of <tt>JavaBean</tt> bound property models that
	 * this controller manages.
	 */
	protected List<AbstractPropertyModel> modelList;
	
	/**
	 * A list of GUI classes implementing the <tt>PropertyChangeView</tt>
	 * interface that this controller manages.
	 */
	protected List<ProperyChangeView> viewList;
	
	/**
	 * Create the controller.
	 */
	public AbstractController(){
		initLists();
	}
	
	/**
	 * Initialize the synchronized lists to store the models
	 * and views.
	 */
	private void initLists(){
		modelList = Collections.synchronizedList(new ArrayList<>(10));
		viewList = Collections.synchronizedList(new ArrayList<>(10));
	}
	
	/**
	 * Add a model, and set this controller as a <tt>PropertyChangeListener</tt>
	 * on it.
	 * 
	 * @param model the model to be added to this controller.
	 */
	public void addPropertyModel(AbstractPropertyModel model){
		modelList.add(model);
		model.addPropertyChangeListener(this);
	}
	
	/**
	 * Remove a model, and remove this controller as a <tt>PropertyChangeListener</tt>
	 * from it.
	 * 
	 * @param model the model to be removed from this controller.
	 */
	public void removePropertyModel(AbstractPropertyModel model){
		modelList.remove(model);
		model.removePropertyChangeListener(this);
	}
	
	/**
	 * Add a view.
	 * 
	 * @param view the view to be added to this controller.
	 */
	public void addView(ProperyChangeView view){
		viewList.add(view);
	}
	
	/**
	 * Remove a view.
	 * 
	 * @param view the view to be removed from this controller.
	 */
	public void removeView(ProperyChangeView view){
		viewList.remove(view);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt){
		for(ProperyChangeView view : viewList){
			view.changeProperty(evt);
		}
	}
	
	/**
	 * Retrieves the value of a property from a model this controller owns.
	 * It does this by using  Reflection to try calling a getter method for 
	 * the requested property on all models until either it gets the value
	 * or throws an <tt>Exception</tt>.
	 * <p>
	 * <b>NOTE:</b> As of Version 2.0, this method only works with single-parameter
	 * setter methods. Multiple parameters will need to explicitly be passed in
	 * the form of an array.
	 * 
	 * @param propertyName the name of the property to be retrieved.
	 * @return the value of the property to be retrieved.
	 * @throws NoSuchMethodException if no model has the requested getter method.
	 * @throws IllegalAccessException if a matching getter method is found, but 
	 * the access modifier won't permit invocation.
	 * @throws Exception if an <tt>InvocationTargetException</tt> occurs because
	 * of a checked exception.
	 * @throws RuntimeException if an <tt>InvocationTargetException</tt> occurs because
	 * of an unchecked exception.
	 * @throws Error if an <tt>InvocationTargetException</tt> occurs because
	 * of an error.
	 */
	protected final Object getModelProperty(String propertyName) 
			throws IllegalAccessException, NoSuchMethodException, Exception{
		boolean success = false;
		boolean illegalAccess = false;
		//For the moment, allowing invocationtargetexception to fly
		//It'll only get thrown if the method is located, and it throws
		//an exception, so it's fine if the loop stops.
		Object result = null;
		synchronized(modelList){
			for(AbstractPropertyModel model : modelList){
				try{
					Method method = model.getClass().getMethod(
							"get" + propertyName);
					result = method.invoke(model);
					success = true;
				}
				catch(IllegalAccessException ex){
					//Access modifier doesn't allow invocation from this class
					illegalAccess = true;
				}
				catch(NoSuchMethodException ex){
					//Method doesn't exist
					//gets rethrown if this loop fails to succeed
				}
				catch(InvocationTargetException ex){
					Throwable t = ex.getCause();
					launderThrowable(t);
				}
			}
		}
		
		if(!success){
			if(illegalAccess == true){
				//Will rarely happen, because NoSuchMethod will be triggered
				//by the earlier method.
				throw new IllegalAccessException("set" + propertyName);
			}
			else{
				throw new NoSuchMethodException("set" + propertyName);
			}
		}
		
		return result;
	}
	
	/**
	 * Sets the value of a property from a model this controller owns.
	 * It does this by using  Reflection to try calling a setter method for 
	 * the requested property on all models until either it sets the value
	 * or throws an <tt>Exception</tt>.
	 * 
	 * @param propertyName the name of the property to be retrieved.
	 * @param newValue the value to assign to the property.
	 * @throws NoSuchMethodException if no model has the requested getter method.
	 * @throws Exception if an <tt>InvocationTargetException</tt> occurs because
	 * of a checked exception.
	 * @throws RuntimeException if an <tt>InvocationTargetException</tt> occurs because
	 * of an unchecked exception.
	 * @throws Error if an <tt>InvocationTargetException</tt> occurs because
	 * of an error.
	 */
	protected final void setModelProperty(String propertyName, Object newValue) 
			throws NoSuchMethodException, Exception{
		//TODO this currently only works with single parameter methods. Work on
		//getting flexibility with multiple parameter methods.
		
		//Find models with matching method signature
		String methodSig = "set" + propertyName;
		List<AbstractPropertyModel> modelsWithMethod = new ArrayList<>();
		synchronized(modelList){
			for(AbstractPropertyModel model : modelList){
				//Get all methods for the model
				Method[] methods = model.getClass().getMethods();
				//Loop through methods and check if the signature matches
				for(Method m : methods){
					if(m.getName().equals(methodSig)){
						//If the signature matches, now check the method's parameter types
						//Confirm that the newValue param can be assigned to the method
						Class<?>[] paramList = m.getParameterTypes();
						boolean assignable = false;
						for(Class<?> param : paramList){
							//If the param is assignable, set assignable to true
							if(param.isAssignableFrom(newValue.getClass())){
								assignable = true;
							}
						}
						
						//If assignable is true, add this model to the list
						if(assignable){
							modelsWithMethod.add(model);
						}
					}
				}
			}
		}
		
		//Get the current type class of the newValue parameter
		Class<?> typeClass = newValue.getClass();
		//boolean value to record if there was a successful method invocation
		boolean success = false;
		
		//Try and invoke the method on each model, while adjusting the parameter type
		for(AbstractPropertyModel model : modelsWithMethod){
			//Run this loop until typeClass == null.
			//Each time, typeClass is reassigned to be its own superclass
			//This allows for polymorphism, methods can be invoked that have
			//a param that's a superclass of newValue
			while(typeClass != null){
				try {
					Method m = model.getClass().getMethod(methodSig, typeClass);
					m.invoke(model, newValue);
					success = true;
					break;
				}
				catch(IllegalAccessException ex){
					//TODO this exception doesn't matter, since no method
					//that's not public can be found by Class.getMethods()
				}
				catch(NoSuchMethodException ex){
					//TODO this exception doesn't matter. It'll happen
					//several times, but ultimately it should be caught
					//and ignored to the loop can keep going.
				}
				catch(InvocationTargetException ex){
					Throwable t = ex.getCause();
					launderThrowable(t);
				}
				
				//If the code reaches this, a NoSuchMethodException occurred on the first attempt
				//Now we attempt to try any interfaces as a parameter.
				Class<?>[] interfaces = typeClass.getInterfaces();
				for(Class<?> c : interfaces){
					try{
						Method m = model.getClass().getMethod(methodSig, c);
						m.invoke(model, newValue);
						success = true;
						break;
					}
					catch(IllegalAccessException ex){
						//TODO this exception doesn't matter, since no method
						//that's not public can be found by Class.getMethods()
					}
					catch(NoSuchMethodException ex){
						//TODO this exception doesn't matter. It'll happen
						//several times, but ultimately it should be caught
						//and ignored to the loop can keep going.
					}
					catch(InvocationTargetException ex){
						Throwable t = ex.getCause();
						launderThrowable(t);
					}
					
				}
				
				//If an interface succeeded, end the main loop
				if(success){
					break;
				}
				
				//If after all that, no success, typeClass becomes its immediate superclass
				typeClass = typeClass.getSuperclass();
			}
		}
		
		//If there is no successful invocation, throw the NoSuchMethodException
		if(!success){
			throw new NoSuchMethodException("set" + propertyName 
					+ "(" + newValue.getClass().getName() + ")");
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	/*protected final void setModelProperty(String propertyName, Object newValue) 
			throws NoSuchMethodException, Exception{
		//TODO this currently only works with single parameter methods. Work on
		//getting flexibility with multiple parameter methods.
		
		//Find models with matching method signature
		String methodSig = "set" + propertyName;
		List<AbstractPropertyModel> modelsWithMethod = new ArrayList<>();
		synchronized(modelList){
			for(AbstractPropertyModel model : modelList){
				//Get all methods for the model
				Method[] methods = model.getClass().getMethods();
				//Loop through methods and check if the signature matches
				for(Method m : methods){
					if(m.getName().equals(methodSig)){
						//If the signature matches, now check the method's parameter types
						//Confirm that the newValue param can be assigned to the method
						Class<?>[] paramList = m.getParameterTypes();
						boolean assignable = false;
						for(Class<?> param : paramList){
							//If the param is assignable, set assignable to true
							if(param.isAssignableFrom(newValue.getClass())){
								assignable = true;
							}
						}
						
						//If assignable is true, add this model to the list
						if(assignable){
							modelsWithMethod.add(model);
						}
					}
				}
			}
		}
		
		//Get the current type class of the newValue parameter
		Class<?> typeClass = newValue.getClass();
		//boolean value to record if there was a successful method invocation
		boolean success = false;
		
		//Try and invoke the method on each model, while adjusting the parameter type
		for(AbstractPropertyModel model : modelsWithMethod){
			//Run this loop until typeClass == null.
			//Each time, typeClass is reassigned to be its own superclass
			//This allows for polymorphism, methods can be invoked that have
			//a param that's a superclass of newValue
			while(typeClass != null){
				try {
					Method m = model.getClass().getMethod(methodSig, typeClass);
					m.invoke(model, newValue);
					success = true;
					break;
				}
				catch(IllegalAccessException ex){
					//TODO this exception doesn't matter, since no method
					//that's not public can be found by Class.getMethods()
				}
				catch(NoSuchMethodException ex){
					//TODO this exception doesn't matter. It'll happen
					//several times, but ultimately it should be caught
					//and ignored to the loop can keep going.
				}
				catch(InvocationTargetException ex){
					Throwable t = ex.getCause();
					launderThrowable(t);
				}
				
				//typeClass is now its superclass
				typeClass = typeClass.getSuperclass();
			}
		}
		
		//If there is no successful invocation, throw the NoSuchMethodException
		if(!success){
			throw new NoSuchMethodException("set" + propertyName 
					+ "(" + newValue.getClass().getName() + ")");
		}
		
	}*/
	
	/**
	 * Parses an array of objects and returns an array of the <tt>Class</tt>
	 * types of those objects.
	 * 
	 * @param obArr the array to parse.
	 * @return an array of <tt>Class</tt> types.
	 * @throws ClassNotFoundException if the class of an object cannot be found.
	 */
	/*private Class<?>[] getArrayElementClasses(Object[] obArr) throws ClassNotFoundException{
		Class<?>[] classArr = new Class<?>[obArr.length];
		for(int i = 0; i < classArr.length; i++){
			classArr[i] = Class.forName(obArr[i].getClass().getName());
		}
		return classArr;
	}*/
	
	/**
	 * Parses a throwable that's a cause from an <tt>InvocationTargetException</tt>
	 * that is thrown by one of the reflective methods in this class. Rethrows it
	 * as either an <tt>Exception</tt>, <tt>RuntimeException</tt>, or <tt>Error</tt>.
	 * 
	 * @param t the throwable being checked.
	 * @throws Exception if the throwable is a subclass of <tt>Exception</tt>.
	 * @throws RuntimeException if the throwable is a subclass of <tt>RuntimeException</tt>.
	 */
	private void launderThrowable(Throwable t) throws Exception{
		if(t instanceof RuntimeException){
			throw (RuntimeException) t;
		}
		else if(t instanceof Error){
			throw (Error) t;
		}
		else{
			throw (Exception) t;
		}
	}
	
}
