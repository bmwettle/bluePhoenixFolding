package origamiProject;

import java.awt.Color;

import javax.swing.*;
public class editor extends JFrame {
	JComboBox size;
	public editor() {
		setSize(600,600);
		setTitle("editor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//this.setBackground(Color.RED);
		size= new JComboBox();
		
		
	}
public static void main(String[] args) {
	editor edit= new editor();
	edit.setVisible(true);
}
}
