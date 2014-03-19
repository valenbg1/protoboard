package protoboard.swing;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;

import protoboard.Main;
import protoboard.Constants.PrincipalIfaceC;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Implements the principal Swing interface.
 *
 */
public class PrincipalIface extends JFrame {
	private static final long serialVersionUID = -7522084622403945355L;
	
	private JPanel contentPane;
	
	private final JButton btnBlackboardMode = new JButton(PrincipalIfaceC.blackboard_text_button);
	private final JToggleButton tglbtnInputMode= new JToggleButton(PrincipalIfaceC.input_text_button);
	
	public PrincipalIface() {
		setResizable(false);
		setTitle(PrincipalIfaceC.title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 333, 71);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		tglbtnInputMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if (me.getButton() == MouseEvent.BUTTON1) {
					if (!tglbtnInputMode.isSelected())
						Main.stopInputMode();
					else
						Main.runInputMode();
				}
			}
		});
		contentPane.add(tglbtnInputMode);
		
		btnBlackboardMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Main.runBlackboardMode();
				btnBlackboardMode.setEnabled(false);
			}
		});
		contentPane.add(btnBlackboardMode);
	}

	public void deselectInputButton() {
		tglbtnInputMode.setSelected(false);
	}
	
	public void disableBlackboardButton() {
		btnBlackboardMode.setEnabled(false);
	}
	
	public void enableBlackboardButton() {
		btnBlackboardMode.setEnabled(true);
	}
	
	public void selectInputButton() {
		tglbtnInputMode.setSelected(true);
	}
}