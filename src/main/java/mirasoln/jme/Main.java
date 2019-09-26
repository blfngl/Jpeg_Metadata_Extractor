
/**
 * JME - JPEG Metadata Extractor
 * @author Nick Mirasol
 * @date 9/26/2019
 * @description A tool to extract metadata from a jpeg or set of jpegs
 */

package mirasoln.jme;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

public class Main
{
	public static void main(String args[])
	{
		// The window for the program
		JFrame frame = new JFrame("JME - JPEG Metadata Extractor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 600);

		// Menu bar
		JMenuBar mb = new JMenuBar();
		JMenu menuFile = new JMenu("File");
		JMenu menuOptions = new JMenu("Options");
		JMenu menuHelp = new JMenu("Help");

		JMenuItem menuFile_Open = new JMenuItem("Open");
		JMenuItem menuFile_Dump = new JMenuItem("Dump metadata");
		JMenuItem menuFile_Exit = new JMenuItem("Exit");

		menuFile_Open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.out.println("Opening file...");
			}
		});

		menuFile_Dump.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) { 
				System.out.println("Dumping metadata");
			}
		});

		menuFile_Exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.out.println("Exiting JME");
				System.exit(0);
			}
		});

		mb.add(menuFile);
		mb.add(menuOptions);
		mb.add(menuHelp);

		menuFile.add(menuFile_Open);
		menuFile.add(menuFile_Dump);
		menuFile.add(menuFile_Exit);

		frame.getContentPane().add(BorderLayout.NORTH, mb);

		// ZIP code display
		JPanel panelZip = new JPanel();
		JLabel labelPanelZip = new JLabel("ZIP code:");

		panelZip.add(labelPanelZip);
		frame.getContentPane().add(BorderLayout.SOUTH, panelZip);

		// Display frame
		System.out.println("Opening frame");
		frame.setVisible(true);
	}
}
