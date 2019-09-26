
/**
 * JME - JPEG Metadata Extractor
 * @author Nick Mirasol
 * @date 9/26/2019
 * @description A tool to extract metadata from a jpeg or set of jpegs
 */

package mirasoln.jme;

import javax.swing.JFrame;

public class Main
{
	public static void main(String args[])
	{
		// The window for the program
		JFrame frame = new JFrame("JME - JPEG Metadata Extractor");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 600);

		System.out.println("Opening frame");
		frame.setVisible(true);
	}
}
