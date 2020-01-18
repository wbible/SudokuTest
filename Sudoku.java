package sudoku;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

class History{
	NumLabel lb;
	String old_value;
	public History(NumLabel lb, String old_value) {
		this.lb = lb;
		this.old_value = old_value;
	}
	public NumLabel getLb() {
		return lb;
	}
	public void setLb(NumLabel lb) {
		this.lb = lb;
	}
	public String getOld_value() {
		return old_value;
	}
	public void setOld_value(String old_value) {
		this.old_value = old_value;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof NumLabel) {
			NumLabel lb = (NumLabel) obj;
			if(getLb().getId() == lb.getId()) return true;
		}
		return false;
	}
	
}
//---------------------
public class Sudoku extends JFrame implements ActionListener, Runnable {
	public static final int FRAME_WIDTH = 800;
	public static final int FRAME_HEIGHT = 500;
	
	public enum Level{EASY, MEDIUM, HARD}
	public static Level level;
	
	private JRadioButton level_easy, level_normal, level_hard;
	private JLabel lb_time_check = new JLabel("EASY 00:00", JLabel.CENTER);
	
	private List<History> history = new ArrayList<History>();
	private boolean playing;
	private Thread t;
	
	private NumLabel[][] p_ar;
	private ArrayList<NumLabel> list_all = new ArrayList<NumLabel>();
	private ArrayList<NumLabel>[] list_v = new ArrayList[9];
	private ArrayList<NumLabel>[] list_h = new ArrayList[9];
	private ArrayList<NumLabel>[] list_s = new ArrayList[9];
	private ArrayList<String>[] answer_v = new ArrayList[9];
	private ArrayList<String>[] answer_h = new ArrayList[9];
	private ArrayList<String>[] answer_s = new ArrayList[9];

	private Set<NumLabel> group_active;

	public Sudoku() {
		// 프레임과 컨테이너 설정
		super("스도쿠 게임");
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int) ((screen.getWidth() - FRAME_WIDTH) / 2), (int) ((screen.getHeight() - FRAME_HEIGHT) / 2),
				FRAME_WIDTH, FRAME_HEIGHT);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container c = getContentPane();
		c.setLayout(new BorderLayout(20, 0));

		// 폰트 설정
		Font font_numpad = new Font("맑은 고딕", Font.BOLD, 25);
		Font font_option = new Font("맑은 고딕", Font.BOLD, 15);

		// 패널 초기화
		JPanel p_main = new JPanel();
		p_main.setLayout(new GridLayout(3, 3, 0, 0));
		c.add("Center", p_main);
		JPanel p_east = new JPanel();
		p_east.setLayout(new BorderLayout(0, 10));
		p_east.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 18));
		c.add("East", p_east);

		// 메인 숫자 입력 패널
		JPanel[] p_con = new JPanel[9];
		for (int i = 0; i < 9; i++) {
			p_con[i] = new JPanel();
			p_con[i].setBorder(BorderFactory.createLineBorder(NumLabel.color_main, 2));
			p_con[i].setLayout(new GridLayout(3, 3, 0, 0));
			p_main.add(p_con[i]);

			list_v[i] = new ArrayList<NumLabel>();
			list_h[i] = new ArrayList<NumLabel>();
			list_s[i] = new ArrayList<NumLabel>();
		}

		p_ar = new NumLabel[9][9];
		int group_s, group_h, group_v;
		int id = 0;
		for (int i = 0; i < 9; i++) {
			group_s = i + 1;
			for (int j = 0; j < 9; j++) {
				group_h = j / 3 + (i / 3) * 3 + 1;
				group_v = (j) % 3 + (i % 3) * 3 + 1;
				p_ar[i][j] = new NumLabel(this, group_s, group_h, group_v, id++);
				p_ar[i][j].setHorizontalAlignment(JLabel.CENTER);
				p_ar[i][j].setForeground(Color.BLACK);
				p_con[i].add(p_ar[i][j]);
				
				list_s[i].add(p_ar[i][j]);
				list_v[group_v-1].add(p_ar[i][j]);
				list_h[group_h-1].add(p_ar[i][j]);
				
				list_all.add(p_ar[i][j]);
			}
		}

		// 오른쪽 상단 패널
		JPanel p_right_top = new JPanel();
		p_right_top.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
		p_right_top.setLayout(new GridLayout(3,1));

		lb_time_check.setFont(new Font("맑은 고딕", Font.BOLD, 30));
		lb_time_check.setForeground(new Color(100,100,100));
		JPanel p_level = new JPanel();
		
		lb_time_check.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		p_level.add(lb_time_check);
		
		JLabel lb_level = new JLabel("게임 난이도 : ", JLabel.RIGHT);
		lb_level.setVerticalAlignment(JLabel.CENTER);
		p_level.add(lb_level);
		level_easy = new JRadioButton("쉬움");
		level_normal = new JRadioButton("보통");
		level_hard = new JRadioButton("어려움");
		JRadioButton[] levels = {level_easy, level_normal, level_hard};

		ButtonGroup bg = new ButtonGroup();
		for(JRadioButton btn : levels) {
			p_level.add(btn);
			bg.add(btn);
		}
		level_easy.setSelected(true);
		
		JButton btn_new = new JButton("새로운 게임");
		btn_new.addActionListener(this);
		btn_new.setPreferredSize(new Dimension(300, 35));
		btn_new.setFocusable(false);
		
		p_right_top.add(lb_time_check); p_right_top.add(p_level); p_right_top.add(btn_new);
		
		restart();
		setLevel();
		
		t = new Thread(this);

		// 번호 입력부
		JPanel p_num = new JPanel();
		p_num.setLayout(new GridLayout(3, 3, 0, 0));
		OptionLabel[] num = new OptionLabel[9];
		for (int i = 0; i < 9; i++) {
			num[i] = new OptionLabel(this, i+1 + "", JLabel.CENTER);
			num[i].setBorder(BorderFactory.createLineBorder(NumLabel.color_main, 1));
			num[i].setFont(font_numpad);
			num[i].setForeground(NumLabel.color_main);
			p_num.add(num[i]);
		}

		JPanel p_option = new JPanel();
		p_option.setLayout(new GridLayout(2, 2, 0, 0));
		OptionLabel[] option = new OptionLabel[4];
		String[] option_name = { "노트", "힌트", "실행취소", "지우기" };
		for (int i = 0; i < 4; i++) {
			option[i] = new OptionLabel(this, option_name[i], JLabel.CENTER);
			option[i].setBorder(BorderFactory.createLineBorder(NumLabel.color_main, 1));
			option[i].setFont(font_option);
			option[i].setForeground(NumLabel.color_main);
			option[i].setPreferredSize(new Dimension(10,30));
			p_option.add(option[i]);
		}

		p_east.add("North", p_right_top);
		p_east.add("Center", p_num);
		p_east.add("South", p_option);

		setVisible(true);
	}
	
	public void answer_check(NumLabel lb_active, boolean record) {
		Set<String> check  = new HashSet<String>();
		for(NumLabel lb : list_v[lb_active.getGroup_v()-1]) {
			if(!lb.getText().equals("")) check.add(lb.getText());
		}
		if(check.size() == 9) {
			Thread effect = new Thread() {
				@Override
				public void run() {
					for(int i = 0; i < 9; i++) {						
						try {
							list_v[lb_active.getGroup_v()-1].get(i).setBackground(NumLabel.color_clicked);
							Thread.sleep(50);
							list_v[lb_active.getGroup_v()-1].get(i).setBackground(Color.WHITE);
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};
			if(record) effect.start();
		}
		
	}
	
	public void setNumber(NumLabel lb_active, String num, boolean record) {
		if(!lb_active.isEditable()) return;
		if(lb_active.getText().equals(num)) return;
		if(record) history.add(new History(lb_active, lb_active.getText()));
		lb_active.setText(num);
		colorReset();
		for(NumLabel lb : list_all) setColor(lb);
		lb_active.setBackground(NumLabel.color_active);
		if(!playing) {
			playing = true;
			(t = new Thread(this)).start();
		}
		lb_active.select(lb_active);
		answer_check(lb_active, record);
	}
	
	public void colorReset() {
		for(NumLabel lb : list_all) {
			lb.setWrong(false);
			lb.setBackground(Color.WHITE);
			if(!lb.isEditable()) { 
				lb.setForeground(Color.BLACK);
			}
			else {
				lb.setForeground(NumLabel.color_main);
			}
		}
	}
	
	public void setColor(NumLabel lb_active) {
		String num = lb_active.getText();
		for(NumLabel lb : getSet_sameGroup(lb_active)) {
			if(lb.getText().equals(num) && !num.equals("") && !num.equals(" ") && lb.getId() != lb_active.getId()) {
				if(lb.isEditable()) lb.setForeground(Color.RED);
				lb.setBackground(NumLabel.color_wrong);
				lb.setWrong(true);
			}
		}	
	}
	
	public Set<NumLabel> getSet_sameGroup(NumLabel lb) {
		Set<NumLabel> setSameGroup = new HashSet<NumLabel>();
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(p_ar[i][j].getGroup_h() == lb.getGroup_h() ||
				p_ar[i][j].getGroup_s() == lb.getGroup_s() || 
				p_ar[i][j].getGroup_v() == lb.getGroup_v())  setSameGroup.add(p_ar[i][j]);
			}
		}
		return setSameGroup;
	}

	private void setLevel(Level selectedLevel) {
		int blank=0, cell=0;
		if(selectedLevel == Level.EASY) {
			lb_time_check.setText("EASY 00:00");
			blank = 4;
			level = Level.EASY;
		}else if(selectedLevel == Level.MEDIUM) {
			lb_time_check.setText("NORMAL 00:00");
			blank = 5;
			level = Level.MEDIUM;
		}else if(selectedLevel == Level.HARD) {
			lb_time_check.setText("HARD 00:00");
			blank = 6;
			level = Level.HARD;
		}
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < blank; j++) {
				cell = (int) (Math.random()*9);
				if(list_v[i].get(cell).getText().length()==0) {
					j--;
				}else {					
					list_v[i].get(cell).setText("");
					list_v[i].get(cell).setEditable(true);
				}
			}
		}
	}
	
	private void setLevel() {
		if (level_easy.isSelected()) setLevel(Level.EASY);
		else if (level_normal.isSelected()) setLevel(Level.MEDIUM);
		else if (level_hard.isSelected()) setLevel(Level.HARD);
	}

	public void restart() {
		String num;
		int group_h, group_s;
		int count = 0;
		OptionLabel.hint_reset();
		resetAnswer();
		playing = false;
		RESTART: for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				num = answer_v[i].get((int) (Math.random() * answer_v[i].size()));
				group_h = list_v[i].get(j).getGroup_h() - 1;
				group_s = list_v[i].get(j).getGroup_s() - 1;
				
				list_v[i].get(j).setForeground(Color.BLACK);
				list_v[i].get(j).setBackground(Color.WHITE);
				list_v[i].get(j).setWrong(false);

				if (answer_h[group_h].contains(num) && answer_s[group_s].contains(num)) {
					list_v[i].get(j).setText(num);
					list_v[i].get(j).setAnswer(Integer.parseInt(num));
					answer_v[i].remove(answer_v[i].indexOf(num));
					answer_h[group_h].remove(answer_h[group_h].indexOf(num));
					answer_s[group_s].remove(answer_s[group_s].indexOf(num));
				} else {
					j--;
					count++;
				}
				if (count > 300) {
					i = -1;
					resetAnswer();
					count = 0;
					continue RESTART;
				}
			}
		}
		
		history.clear();
	}

	public void resetAnswer() {
		for(int i = 0; i < 9; i++) {
			answer_v[i] = new ArrayList<String>();
			answer_h[i] = new ArrayList<String>();
			answer_s[i] = new ArrayList<String>();
		}
		for(int i = 0; i < 9; i++) {
			for (int j = 1; j < 10; j++) {
				answer_v[i].add(j + "");
				answer_h[i].add(j + "");
				answer_s[i].add(j + "");
			}
		}
	}
	
	public ArrayList<NumLabel> getList_all() {
		return list_all;
	}

	public NumLabel[][] getP_ar() {
		return p_ar;
	}
	
	public List<History> getHistory() {
		return history;
	}

	public static void main(String[] args) {
		new Sudoku();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("새로운 게임")) {
			restart();
			if(level_easy.isSelected()) setLevel(Level.EASY);
			else if(level_normal.isSelected()) setLevel(Level.MEDIUM);
			else if(level_hard.isSelected()) setLevel(Level.HARD);
		}
	}

	@Override
	public void run() {
		int min = 0, sec = 0;
		String strLevel = "";
		if(level == Level.EASY) strLevel = "EASY";
		else if(level == Level.MEDIUM) strLevel = "NORMAL";
		else if(level == Level.HARD) strLevel = "HARD";
		
		while(playing) {
			try {
				Thread.sleep(100);
				sec+=1;
				if(sec == 600) {sec = 0; min += 1;}
				if(min == 60) min = 0;
				
				
				if(playing) lb_time_check.setText(String.format("%s %02d:%02d", strLevel, min, sec/10));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
}
