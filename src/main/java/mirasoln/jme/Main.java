
/**
 * JME - JPEG Metadata Extractor
 * @author Nick Mirasol
 * @date 9/26/2019
 * @description A tool to extract metadata from a jpeg or set of jpegs
 */

package mirasoln.jme;

import java.awt.BorderLayout;

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

		mb.add(menuFile);
		mb.add(menuOptions);
		mb.add(menuHelp);

		JMenuItem menuFile_Open = new JMenuItem("Open");
		JMenuItem menuFile_Dump = new JMenuItem("Dump metadata");

		menuFile.add(menuFile_Open);
		menuFile.add(menuFile_Dump);

		frame.getContentPane().add(BorderLayout.NORTH, mb);

		// ZIP code display
		JPanel panelZip = new JPanel();
		JLabel labelPanelZip = new JLabel("ZIP code:");
		panelZip.add(labelPanelZip);

		frame.getContentPane().add(BorderLayout.SOUTH, panelZip);

		System.out.println("Opening frame");
		frame.setVisible(true);
	}
}
