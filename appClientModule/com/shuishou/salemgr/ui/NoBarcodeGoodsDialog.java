package com.shuishou.salemgr.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import org.apache.log4j.Logger;

import com.shuishou.salemgr.Messages;
import com.shuishou.salemgr.beans.Category2;
import com.shuishou.salemgr.beans.Goods;
import com.shuishou.salemgr.ui.components.CommonDialog;
import com.shuishou.salemgr.ui.components.JBlockedButton;
import com.shuishou.salemgr.ui.uibean.ChoosedGoods;

public class NoBarcodeGoodsDialog extends CommonDialog implements ActionListener{
	private final Logger logger = Logger.getLogger(NoBarcodeGoodsDialog.class.getName());
	
	private MainFrame mainFrame;
	
	private JButton btnRemove = new JButton(Messages.getString("Delete"));
	private JButton btnClose = new JButton(Messages.getString("CloseDialog"));
	private JBlockedButton btnConfirm = new JBlockedButton(Messages.getString("ConfirmDialog"), null);
	private JPanel pGoods = new JPanel(new GridBagLayout());
	private JList<ChoosedGoods> listChoosedGoods = new JList<>();
	private ListModel<ChoosedGoods> listModelChoosedGoods = new ListModel<>();
	private ArrayList<Category2> listCategory2 = new ArrayList<>();
	
	public boolean isCanceled = false;
	public NoBarcodeGoodsDialog(MainFrame mainFrame,String title){
		super(mainFrame, title, true);
		this.mainFrame = mainFrame;
		initUI();
	}
	
	private void initUI(){
		JLabel lbSearchCode = new JLabel(Messages.getString("OpenTableDialog.SearchCode"));
		listChoosedGoods.setModel(listModelChoosedGoods);
		listChoosedGoods.setCellRenderer(new ChoosedGoodsRenderer());
		listChoosedGoods.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listChoosedGoods.setFixedCellHeight(50);
		btnRemove.setPreferredSize(new Dimension(150, 50));
		btnClose.setPreferredSize(new Dimension(150, 50));
		btnConfirm.setPreferredSize(new Dimension(100, 50));
		
		JScrollPane jspChooseGoods = new JScrollPane(listChoosedGoods, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		pGoods.setBorder(BorderFactory.createTitledBorder("Goods"));
		pGoods.setBackground(Color.white);
		JScrollPane jspGoods = new JScrollPane(pGoods, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JPanel pGoodsDishplay = new JPanel(new GridBagLayout());
		
		JPanel pCategory2 = generateCategory2Panel();
		
		pGoodsDishplay.add(pCategory2, 	new GridBagConstraints(0, 0, 1, 1, 1, 0.2, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		pGoodsDishplay.add(jspGoods, 	new GridBagConstraints(0, 1, 1, 1, 1, 0.5, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		
		JPanel pChoosedGoods = new JPanel(new GridBagLayout());
		pChoosedGoods.add(jspChooseGoods, 	new GridBagConstraints(0, 2, 2, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
		pChoosedGoods.add(btnClose,	 		new GridBagConstraints(0, 4, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pChoosedGoods.add(btnRemove,	 		new GridBagConstraints(1, 4, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		pChoosedGoods.add(btnConfirm,		new GridBagConstraints(0, 6, 2, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
		
		Container c = this.getContentPane();
		c.setLayout(new BorderLayout());
		c.add(pChoosedGoods, BorderLayout.WEST);
		c.add(pGoodsDishplay,BorderLayout.CENTER);
		btnClose.addActionListener(this);
		btnConfirm.addActionListener(this);
		btnRemove.addActionListener(this);
		this.setSize(new Dimension(MainFrame.WINDOW_WIDTH, MainFrame.WINDOW_HEIGHT));
		this.setLocation((int)(mainFrame.getWidth() / 2 - this.getWidth() /2 + mainFrame.getLocation().getX()), 
				(int)(mainFrame.getHeight() / 2 - this.getHeight() / 2 + mainFrame.getLocation().getY()));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnClose){
			isCanceled = true;
			setVisible(false);
		} else if (e.getSource() == btnConfirm){
			setVisible(false);
		} else if (e.getSource() == btnRemove){
			doRemoveGoods();
		} 
	}
	
	public void doEnterClick(){
		setVisible(false);
	}
	
	public ArrayList<ChoosedGoods> getChoosedGoods(){
		ArrayList<ChoosedGoods> listcg = new ArrayList<>();
		for (int j = 0; j < listModelChoosedGoods.size(); j++) {
			listcg.add(listModelChoosedGoods.get(j));
		}
		return listcg;
	}
	
	private JPanel generateCategory2Panel(){
		ArrayList<Goods> listNoBarcode = mainFrame.getListNoBarcodeGoods();
		for (int i = 0; i < listNoBarcode.size(); i++) {
			if (!listCategory2.contains(listNoBarcode.get(i).getCategory2())){
				listCategory2.add(listNoBarcode.get(i).getCategory2());
			}
		}
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
		for (int i = 0; i < listCategory2.size(); i++) {
			Category2Button btn = new Category2Button(listCategory2.get(i));
			p.add(btn);
		}
		return p;
	}
	
	private void doRemoveGoods(){
		if (listChoosedGoods.getSelectedIndex() < 0)
			return;
		listModelChoosedGoods.removeElementAt(listChoosedGoods.getSelectedIndex());
	}
	
	private void doCategory2ButtonClick(Category2 c2){
		int amountPerRow = 4;
		pGoods.removeAll();
		if (c2.getGoods() == null)
			return;
		for(int i = 0; i < c2.getGoods().size(); i++){
			Goods g = c2.getGoods().get(i);
			if (g.getBarcode() == null || g.getBarcode().length() == 0){
				GoodsButton btn = new GoodsButton(g);
				pGoods.add(btn, new GridBagConstraints(i % amountPerRow, (int) i / amountPerRow, 1, 1, 1, 0.2, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			}
			
		}
		pGoods.updateUI();
	}
	
	private void doGoodsButtonClick(Goods goods){
		ChoosedGoods cg = new ChoosedGoods();
		cg.goods = goods;
		cg.amount = 1;
		
		boolean foundexist = false;
		for (int i = 0; i < this.listModelChoosedGoods.size(); i++) {
			if (listModelChoosedGoods.getElementAt(i).goods.getId() == goods.getId()) {
				listModelChoosedGoods.getElementAt(i).amount = listModelChoosedGoods.getElementAt(i).amount + 1;
				listChoosedGoods.updateUI();
				foundexist = true;
				break;
			}
		}
		if (!foundexist) {
			listModelChoosedGoods.insertElementAt(cg, 0);
		}
	}
	
	class Category2Button extends JButton{
		private final Category2 c2;
		public Category2Button(Category2 category2){
			this.c2 = category2;
			this.setText(c2.getName());
			this.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					
					doCategory2ButtonClick(c2);
				}
				
			});
			Dimension d = this.getPreferredSize();
			double width = d.getWidth();
			if (width < 100)
				width = 100;
			d.setSize(width, 50);
			this.setPreferredSize(d);
//			this.setPreferredSize(buttonsize);
		}
	}
	
	class GoodsButton extends JButton{
		private final Goods goods;
		
		public GoodsButton(final Goods goods){
			this.goods = goods;
			this.setText(goods.getName());
			this.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					
					doGoodsButtonClick(goods);
				}
				
			});
			this.setPreferredSize(buttonsize);
		}
	}
	
	class ListModel<ChoosedGoods> extends DefaultListModel<ChoosedGoods>{
		public void refreshData(ChoosedGoods cg, int start, int end){
			super.fireContentsChanged(cg, start, end);
		}
	}
	
	class ChoosedGoodsRenderer extends JPanel implements ListCellRenderer{
		private JLabel lbGoods = new JLabel();
		private JLabel lbAmount = new JLabel();
		public ChoosedGoodsRenderer(){
			setLayout(new GridBagLayout());
			add(lbGoods, 	new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
			add(lbAmount, 	new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
		}
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
				boolean cellHasFocus) {
			if (isSelected) {
	            setBackground(list.getSelectionBackground());
	            setForeground(list.getSelectionForeground());
	        } else {
	            setBackground(list.getBackground());
	            setForeground(list.getForeground());
	        }
			ChoosedGoods cg = (ChoosedGoods)value;
			Goods goods = cg.goods;
			String txt = goods.getName();
			if (txt.length() > 17)
				txt = txt.substring(0, 17) + "...";
			lbGoods.setText(txt);
			lbAmount.setText(cg.amount+" / $"+goods.getSellPrice());
			return this;
		}
		
	}
	
	private final static Dimension buttonsize = new Dimension(180, 50);
}
