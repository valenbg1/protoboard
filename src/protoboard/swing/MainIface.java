package protoboard.swing;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import protoboard.Constants.BlackboardC;
import protoboard.Constants.MainIfaceC;
import protoboard.Main;

/**
 * Implements the principal Swing interface.
 *
 */
public class MainIface extends JFrame {
	private static final long serialVersionUID = -7522084622403945355L;
	
	private JPanel contentPane;
	
	private final JToggleButton tglbtnBlackboardMode;
	private final JToggleButton tglbtnInputMode;
	
	private final JMenuBar menuBar;
	private final JMenu mnNewMenu;
	private final JMenu mnNewMenu_1;
	private final JMenuItem mntmAbout;
	private final JMenuItem mntmInputMode;
	private final JMenuItem mntmBlackboardMode;
	private final JMenu mnFile;
	private final JMenuItem mntmLoadSavedImages;
	
	private final JFileChooser file_chooser;
	
	private JPanel panel;
	private JLabel lblNewLabel;
	private JMenuItem mntmExit;
	private JMenuItem mntmSaveCurrentImages;
	
	public MainIface() {
		setTitle(MainIfaceC.title);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 427, 118);
		
		menuBar = new JMenuBar();
		mnNewMenu = new JMenu(MainIfaceC.config_menu);
		mnNewMenu_1 = new JMenu(MainIfaceC.help_menu);
		mntmAbout = new JMenuItem(MainIfaceC.about_opt);
		mntmInputMode = new JMenuItem(MainIfaceC.config_input_menu);
		mntmBlackboardMode = new JMenuItem(MainIfaceC.config_black_menu);
		mnFile = new JMenu(MainIfaceC.file_menu);
		mntmLoadSavedImages = new JMenuItem(MainIfaceC.load_images_opt);
		
		file_chooser = new JFileChooser(System.getProperty("user.dir"));
		
		setJMenuBar(menuBar);
		
		menuBar.add(mnFile);
		mntmLoadSavedImages.addActionListener(mntmLoadSavedImages_action());
		
		mnFile.add(mntmLoadSavedImages);
		
		mntmSaveCurrentImages = new JMenuItem(MainIfaceC.save_images_opt);
		mntmSaveCurrentImages.addActionListener(mntmSaveCurrentImages_action());
		mnFile.add(mntmSaveCurrentImages);
		mnFile.addSeparator();
		
		mntmExit = new JMenuItem(MainIfaceC.exit_opt);
		mntmExit.addActionListener(mntmExit_action());
		mnFile.add(mntmExit);
		
		menuBar.add(mnNewMenu);
		
		mnNewMenu.add(mntmBlackboardMode);
		
		mnNewMenu.add(mntmInputMode);
		
		menuBar.add(mnNewMenu_1);
		mntmAbout.addActionListener(mntmAbout_action());
		
		mnNewMenu_1.add(mntmAbout);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		
		tglbtnBlackboardMode = new JToggleButton(MainIfaceC.blackboard_text_button);
		panel.add(tglbtnBlackboardMode);
		tglbtnInputMode= new JToggleButton(MainIfaceC.input_text_button);
		panel.add(tglbtnInputMode);
		
		lblNewLabel = new JLabel("");
		lblNewLabel.setEnabled(false);
		contentPane.add(lblNewLabel, BorderLayout.WEST);
		
		tglbtnInputMode.addActionListener(tglbtnInputMode_action());
		
		tglbtnBlackboardMode.addActionListener(tglbtnBlackboardMode_action());
	}
	
	public void clean_LoadSelectLabel() {
		setSelectedToLoadLabelText("");
	}
	
	public void deselectBlackboardButton() {
		tglbtnBlackboardMode.setSelected(false);
	}

	public void deselectInputButton() {
		tglbtnInputMode.setSelected(false);
	}
	
	private ActionListener mntmAbout_action() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				EventQueue.invokeLater(new Runnable() {
					@Override
					public void run() {
						new AboutDialog().setVisible(true);
					}
				});
			}
		};
	}
	
	private ActionListener mntmExit_action() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				System.exit(0);
			}
		};
	}
	
	private ActionListener mntmLoadSavedImages_action() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				String ext = BlackboardC.save_extension;

				file_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				file_chooser.setMultiSelectionEnabled(true);
				file_chooser.setAcceptAllFileFilterUsed(false);

				FileNameExtensionFilter filter = new FileNameExtensionFilter(
						ext.toUpperCase() + " files (*." + ext + ")", ext);
				file_chooser.removeChoosableFileFilter(file_chooser.getFileFilter());
				file_chooser.setFileFilter(filter);

				if (file_chooser.showOpenDialog(mntmLoadSavedImages) == JFileChooser.APPROVE_OPTION) {
					Main.setLoadFolder_blckbrdMode(file_chooser
							.getSelectedFiles());
					setSelectedToLoadLabelText(MainIfaceC.load_select_label
							+ file_chooser.getSelectedFile().getParent());
				}
			}
		};	
	}
	
	private ActionListener mntmSaveCurrentImages_action() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				file_chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				file_chooser.setMultiSelectionEnabled(false);
				file_chooser.removeChoosableFileFilter(file_chooser.getFileFilter());

				if (file_chooser.showSaveDialog(mntmLoadSavedImages) == JFileChooser.APPROVE_OPTION) {
					Main.saveImagesBlackboard(file_chooser.getSelectedFile());
					setSelectedToLoadLabelText(MainIfaceC.save_to_label
							+ file_chooser.getSelectedFile().getPath());
				}
			}
		};	
	}
	
	public void selectBlackboardButton() {
		tglbtnBlackboardMode.setSelected(true);
	}
	
	public void selectInputButton() {
		tglbtnInputMode.setSelected(true);
	}
	
	private void setSelectedToLoadLabelText(String text) {
		lblNewLabel.setText(text);
	}
	
	private ActionListener tglbtnBlackboardMode_action() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (!tglbtnBlackboardMode.isSelected())
					Main.stopBlackboardMode();
				else
					Main.runBlackboardMode();
			}
		};
	}
	
	private ActionListener tglbtnInputMode_action() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (!tglbtnInputMode.isSelected())
					Main.stopInputMode();
				else
					Main.runInputMode();
			}
		};
	}
}