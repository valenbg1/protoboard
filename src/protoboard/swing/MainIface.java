package protoboard.swing;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;

import protoboard.Constants.MainIfaceC;
import protoboard.Main;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Implements the principal Swing interface.
 *
 */
public class MainIface extends JFrame {
	private static final long serialVersionUID = -7522084622403945355L;
	
	private JPanel contentPane;
	
	private final JToggleButton tglbtnBlackboardMode = new JToggleButton(MainIfaceC.blackboard_text_button);
	private final JToggleButton tglbtnInputMode= new JToggleButton(MainIfaceC.input_text_button);
	
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu mnNewMenu = new JMenu(MainIfaceC.config_menu);
	private final JMenu mnNewMenu_1 = new JMenu(MainIfaceC.help_menu);
	private final JMenuItem mntmAbout = new JMenuItem(MainIfaceC.about_opt);
	private final JMenuItem mntmInputMode = new JMenuItem(MainIfaceC.config_input_menu);
	private final JMenuItem mntmBlackboardMode = new JMenuItem(MainIfaceC.config_black_menu);
	
	public MainIface() {
		setTitle(MainIfaceC.title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 384, 112);
		
		setJMenuBar(menuBar);
		
		menuBar.add(mnNewMenu);
		
		mnNewMenu.add(mntmBlackboardMode);
		
		mnNewMenu.add(mntmInputMode);
		
		menuBar.add(mnNewMenu_1);
		mntmAbout.addActionListener(mntmAbout_action());
		
		mnNewMenu_1.add(mntmAbout);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		tglbtnBlackboardMode.addMouseListener(tglbtnBlackboardMode_action());
		contentPane.add(tglbtnBlackboardMode);
		
		tglbtnInputMode.addMouseListener(tglbtnInputMode_action());
		contentPane.add(tglbtnInputMode);
	}
	
	public void deselectBlackboardButton() {
		tglbtnBlackboardMode.setSelected(false);
	}
	
	public void deselectInputButton() {
		tglbtnInputMode.setSelected(false);
	}

	public void selectBlackboardButton() {
		tglbtnBlackboardMode.setSelected(true);
	}
	
	public void selectInputButton() {
		tglbtnInputMode.setSelected(true);
	}
	
	private ActionListener mntmAbout_action() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						new AboutDialog().setVisible(true);
					}
				});
			}
		};
	}
	
	private MouseAdapter tglbtnBlackboardMode_action() {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if (me.getButton() == MouseEvent.BUTTON1) {
					if (!tglbtnBlackboardMode.isSelected())
						Main.stopBlackboardMode();
					else
						Main.runBlackboardMode();
				}
			}
		};
	}
	
	private MouseAdapter tglbtnInputMode_action() {
		return new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if (me.getButton() == MouseEvent.BUTTON1) {
					if (!tglbtnInputMode.isSelected())
						Main.stopInputMode();
					else
						Main.runInputMode();
				}
			}
		};
	}
}