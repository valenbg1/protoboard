package protoboard.swing;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;

import protoboard.Constants.PrincipalIfaceC;
import protoboard.Main;

/**
 * Implements the principal Swing interface.
 *
 */
public class PrincipalIface extends JFrame {
	private static final long serialVersionUID = -7522084622403945355L;
	
	private JPanel contentPane;
	
	private final JToggleButton btnBlackboardMode = new JToggleButton(PrincipalIfaceC.blackboard_text_button);
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
		
		btnBlackboardMode.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent me) {
				if (me.getButton() == MouseEvent.BUTTON1) {
					if (!btnBlackboardMode.isSelected())
						Main.stopBlackboardMode();
					else
						Main.runBlackboardMode();
				}
			}
		});
		contentPane.add(btnBlackboardMode);
	}

	public void deselectBlackboardButton() {
		btnBlackboardMode.setSelected(false);
	}
	
	public void deselectInputButton() {
		tglbtnInputMode.setSelected(false);
	}
	
	public void selectBlackboardButton() {
		btnBlackboardMode.setSelected(true);
	}
	
	public void selectInputButton() {
		tglbtnInputMode.setSelected(true);
	}
}