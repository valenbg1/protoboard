package protoboard.swing;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Font;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import protoboard.Constants.MainIfaceC.AboutDialogC;

/**
 * Implements the about Swing window
 *
 */
class AboutDialog extends JDialog {
	private static final long serialVersionUID = -260010151490757326L;

	public AboutDialog() {
		setTitle(AboutDialogC.title);
		setBounds(100, 100, 554, 417);
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		JEditorPane dtrpnHola = new JEditorPane();
		dtrpnHola.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
		dtrpnHola.setBackground(UIManager.getColor("Menu.background"));
		dtrpnHola.setText(AboutDialogC.main_text);
		dtrpnHola.setEditable(false);
		dtrpnHola.addHyperlinkListener(new HyperlinkListener() {
		    @Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
		    	if(Desktop.isDesktopSupported() && (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)) {
		    	    try {
						Desktop.getDesktop().browse(e.getURL().toURI());
					} catch (IOException | URISyntaxException e1) {
						// Do nothing...
					}
		    	}
		    }
		});
		getContentPane().add(dtrpnHola, BorderLayout.CENTER);
		
		JLabel lblNewLabel = new JLabel(AboutDialogC.pro_name);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblNewLabel, BorderLayout.NORTH);
		
		JLabel lblcomplutenseUniversityOf = new JLabel(AboutDialogC.univers_name);
		lblcomplutenseUniversityOf.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblcomplutenseUniversityOf.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblcomplutenseUniversityOf, BorderLayout.SOUTH);
	}
}