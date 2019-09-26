
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
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class Main
{
	private static JFrame frame;

	public static void main(String args[])
	{
		createGui();

		// Metadata test
		try {
			readMeta();
		} catch (Exception e) {
			
		}

		// Display frame
		System.out.println("Opening frame");
		frame.setVisible(true);
	}

	private static void createGui()
	{
		// The window for the program
		frame = new JFrame("JME - JPEG Metadata Extractor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 600);

		// Menu bar
		JMenuBar mb = new JMenuBar();
		JMenu menuFile = new JMenu("File");
		JMenu menuOptions = new JMenu("Options");
		JMenu menuHelp = new JMenu("Help");

		JMenuItem menuFile_Open = new JMenuItem("Open                          (Ctrl + O)");
		JMenuItem menuFile_Dump = new JMenuItem("Dump metadata      (Ctrl + D)");
		JMenuItem menuFile_Exit = new JMenuItem("Exit                             (Ctrl + Esc)");

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
	}

	private static void readMeta() throws ImageProcessingException, IOException
	{
		System.out.println("Reading an image");

		File fileJpeg = new File("image.jpg");
		Metadata meta = ImageMetadataReader.readMetadata(fileJpeg);

		for (Directory dir : meta.getDirectories())
		{
			for (Tag tag : dir.getTags())
			{
				System.out.println(tag);
			}
		}
	}
}
