package origamiProject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class Oeditor extends JFrame implements ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 42871189832605926L;
	ButtonGroup options;
	JRadioButton  cut;
	JRadioButton  newNode;
	JRadioButton  select;
	JTextField size;
public Oeditor(){
	super();
	setLayout(new GridLayout(4,1));
	this.setSize(100, 100);
	this.setBackground(Color.GREEN);
	cut= new JRadioButton("cut",false);
	cut.addActionListener(this);
	this.add(cut);
	newNode= new JRadioButton("new node",true);
	newNode.addActionListener(this);
	this.add(newNode);
	select= new JRadioButton("select",false);
	select.addActionListener(this);
	this.add(select);
	options=new ButtonGroup();
	options.add(cut);
	options.add(newNode);
	options.add(select);
	size= new JTextField();
	add(size);
}
@Override
public void actionPerformed(ActionEvent arg0) {
	// TODO Auto-generated method stub
	
}
//public int getSize() {
	//return Integer.parseInt(size.getText());
//}
public boolean isNewNode() {
	//JOptionPane.showMessageDialog(this,"finding nodes");

	return options.isSelected(newNode.getModel());
}
public int getNodeSize() {
	return Integer.parseInt(size.getText());
}
}
