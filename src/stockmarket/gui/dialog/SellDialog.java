package stockmarket.gui.dialog;

import java.awt.Frame;

import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import stockmarket.stock.OwnedStock;
import stockmarket.util.Language;

public class SellDialog extends TransactionDialog {

	//TODO delete this method after testing is done
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable(){

			@Override
			public void run() {
				
			}
			
		});
	}
	
	private static final Language LANGUAGE = Language.getInstance();
	
	public SellDialog(OwnedStock stock) {
		super(stock);
	}

	public SellDialog(Frame owner, OwnedStock stock) {
		super(owner, stock);
	}

	public SellDialog(Frame owner, boolean modal, OwnedStock stock) {
		super(owner, modal, stock);
	}

	@Override
	protected String transactionType() {
		return LANGUAGE.getString("sell_label");
	}

	@Override
	protected ImageIcon createIcon() {
		return new ImageIcon(
				this.getClass().getClassLoader().getResource(
						"96p/sell.png"));
	}

	@Override
	protected String limitLabelText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String limitValueText() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int maxShareLimit() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getValue(String valueToGet) {
		// TODO Auto-generated method stub
		return null;
	}

}
