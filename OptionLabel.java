package sudoku;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import sudoku.Sudoku.Level;

public class OptionLabel extends JLabel implements MouseListener {
	
	private Font font_num = new Font("맑은 고딕", Font.BOLD, 25);
	private Sudoku sudoku;
	private static int hint_count;
	
	public OptionLabel(Sudoku sudoku) {
		this.sudoku = sudoku;
		setBorder(BorderFactory.createLineBorder(NumLabel.color_main, 1));
		setOpaque(true);
		setBackground(Color.WHITE);
		setForeground(NumLabel.color_main);
		setFont(font_num);
		addMouseListener(this);
	}
	
	public OptionLabel(Sudoku sudoku, String text, int horizontalAlignment) {
		this(sudoku);
		setText(text);
		setHorizontalAlignment(horizontalAlignment);
	}
	
	public void hint() {
		if(sudoku.level == Level.HARD) {
			JOptionPane.showMessageDialog(sudoku, "'어려움'모드에서는 힌트 사용이 불가능합니다. ^^;");
			return;
		}
		if(!NumLabel.lb_active.isEditable()) return;
		
		if(hint_count > 4 && sudoku.level == Level.MEDIUM) {
			JOptionPane.showMessageDialog(sudoku, "'일반'모드 최대 힌트 사용 횟수를 초과했습니다. ^^;");
			return;
		}
		sudoku.setNumber(NumLabel.lb_active, NumLabel.lb_active.getAnswer()+"", false);
		NumLabel.lb_active.setEditable(false);
		NumLabel.lb_active.setForeground(Color.BLACK);
		while(sudoku.getHistory().contains(NumLabel.lb_active)){
			sudoku.getHistory().remove(NumLabel.lb_active);
		}
		hint_count++;
		if(sudoku.level == Level.MEDIUM) {
			JOptionPane.showMessageDialog(sudoku, "힌트를 사용했습니다. (남은 힌트  : " + (5 - hint_count) + "개)");
		}
	}
	
	public static void hint_reset() {
		hint_count = 0;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		OptionLabel lb = (OptionLabel) e.getSource();
		if(lb.getText().equals("노트")) {
			
		}else if(lb.getText().equals("힌트")) hint();
		else if(lb.getText().equals("실행취소")) {
			List<History> history = sudoku.getHistory();
			if(history.size()!=0) {
				History last_move = history.get(history.size()-1);
				sudoku.setNumber(sudoku.getList_all().get(last_move.getLb().getId()), last_move.getOld_value(), false);
				sudoku.getHistory().remove(history.size()-1);
			}
		}else if(lb.getText().equals("지우기")) {
			sudoku.setNumber(NumLabel.lb_active, "", true);
		}else sudoku.setNumber(NumLabel.lb_active, getText(), true);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		OptionLabel lb = (OptionLabel) e.getSource();
		lb.setBackground(NumLabel.color_mouseover);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		OptionLabel lb = (OptionLabel) e.getSource();
			lb.setBackground(Color.WHITE);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		OptionLabel lb = (OptionLabel) e.getSource();
		lb.setBackground(NumLabel.color_clicked);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		OptionLabel lb = (OptionLabel) e.getSource();
		lb.setBackground(NumLabel.color_mouseover);
	}
	
}
