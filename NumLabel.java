package sudoku;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

public class NumLabel extends JLabel implements MouseListener, KeyListener {
	
	private int group_s, group_h, group_v, id;
	private NumLabel[][] p_ar;
	private int answer;
	private boolean editable = false, wrong = false;
	private Set<String> note = new HashSet<String>();
	
	public static Color color_main = new Color(52,72,97);
	public static Color color_mouseover = new Color(221,238,255);
	public static Color color_active = new Color(187,222,251);
	public static Color color_sameGroup = new Color(226,231,237);
	public static Color color_sameNum = new Color(203,219,237);
	public static Color color_wrong = new Color(247,207,214);
	public static Color color_clicked = new Color(230,234,240);
	
	private Font font_num = new Font("¸¼Àº °íµñ", Font.BOLD, 25);
	
	public static NumLabel lb_active;
	private Sudoku sudoku;
	
	public NumLabel() {}
	
	public NumLabel(Sudoku sudoku, int group_s, int group_h, int group_v, int id) {
		this.sudoku = sudoku;
		p_ar = sudoku.getP_ar();
		this.group_s = group_s;
		this.group_h = group_h;
		this.group_v = group_v;
		this.id = id;
		setBorder(BorderFactory.createLineBorder(color_main, 1));
		setOpaque(true);
		setBackground(Color.WHITE);
		setForeground(color_main);
		setFont(font_num);
		addMouseListener(this);
		addKeyListener(this);
	}
	
	
	public int getGroup_s() {
		return group_s;
	}

	public int getGroup_h() {
		return group_h;
	}

	public int getGroup_v() {
		return group_v;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean isEditable() {
		return editable;
	}
	
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	public void setWrong(boolean wrong) {
		this.wrong = wrong;
	}
	
	public boolean isWrong() {
		return wrong;
	}
	
	public void setAnswer(int answer) {
		this.answer = answer;
	}
	
	public int getAnswer() {
		return answer;
	}
	
	public Set<String> getNote(){
		return note;
	}
	
	public void printGroup(NumLabel lb) {
		System.out.printf("h: %d, v: %d, s: %d\n", lb.getGroup_h(), lb.getGroup_v(), lb.getGroup_s());
	}
	
	public boolean isSameGroup(NumLabel lb) {
		if (lb == null) return false; 
		return group_h == lb.getGroup_h() || group_s == lb.getGroup_s() || group_v == lb.getGroup_v()
				? true : false;
	}
	
	public void wrong_check() {
		NumLabel cell;
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				cell = p_ar[i][j];
				if(cell.getGroup_h() == group_h || cell.getGroup_s() == group_s || cell.getGroup_v() == group_v)
					cell.setBackground(color_sameGroup);
				else cell.setBackground(Color.WHITE);
				if(lb_active.getText().length()!=0 && cell.getText().equals(lb_active.getText())) cell.setBackground(color_sameNum);
			}
		}
		lb_active.setBackground(color_active);
	}
	
	public void select(NumLabel lb_selected) {
		lb_active = lb_selected;
		wrong_check();
		for(NumLabel lb : sudoku.getList_all()) {
			if(lb.wrong && lb.getId()!=lb_active.id) lb.setBackground(color_wrong);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		lb_active = (NumLabel) e.getSource();
		select(lb_active);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		NumLabel lb = (NumLabel) e.getSource();
		if(lb != lb_active && !lb.isSameGroup(lb_active)) {
			lb.setBackground(color_mouseover);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		NumLabel lb = (NumLabel) e.getSource();
		if(lb != lb_active && !lb.isSameGroup(lb_active))
			lb.setBackground(Color.WHITE);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int width = getWidth();
		int height = getHeight();
		g.setColor(Color.BLACK);
		g.setFont(new Font("¸¼Àº °íµñ", Font.PLAIN, 13));
		for(String s : note) {
			for(int i = 7; i <= 9; i++) {
				if(s.equals(i+"")) {
					g.drawString(s, (int)(width/7*(i-6)*1.7), (int)(height/7*2));
				};
			}
			for(int i = 4; i <= 6; i++) {
				if(s.equals(i+"")) {
					g.drawString(s, (int)(width/7*(i-3)*1.7), (int)(height/7*4));
				};
			}
			for(int i = 1; i <= 3; i++) {
				if(s.equals(i+"")) {
					g.drawString(s, (int)(width/7*(i-0)*1.7), (int)(height/7*6));
				};
			}
		}
		
	}
	
}
