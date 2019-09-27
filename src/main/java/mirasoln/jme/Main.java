
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
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

public class Main
{
	private static final String GEO_SERVER = "https://maps.googleapis.com/maps/api/geocode/json?";

	private static JFrame frame;
	private static JSONParser jsonParser;

	public static void main(String args[])
	{
		createGui();

		jsonParser = new JSONParser();

		// Metadata test
		/*try {
			readMeta("image.jpg");
		} catch (Exception e) {
			System.out.println("error");
		}*/

		JSONObject response;

		try {
			response = getLocation("31.8114097, -106.7047003");
			getZIPFromJson(response);
		} catch (ParseException e) {
			System.out.println("ParseError\n" + e.getMessage());
		}

		// Display frame
		System.out.println("Opening frame");
		frame.setVisible(true);

		//TODO attach functionality to buttons
	}

	/**
	 * Gets the ZIP code from a json from the google maps api
	 * @param json the json from the google api to parse
	 * @throws ParseException
	 */
	private static void getZIPFromJson(JSONObject json) throws ParseException
	{
		// Get the list of results from the json returned
		JSONArray results = (JSONArray) json.get("results");
		Iterator resultsIterator = results.iterator();

		// While there are entries
		while(resultsIterator.hasNext())
		{
			// Create an iterator for the mapped result (denoted in a json by '[ ]')
			// Each result is split into 'address_components', containing tons of data
			// on the location in question.
			Iterator<Map.Entry> address_components = ((Map) resultsIterator.next()).entrySet().iterator();

			// Iterate over the map
			while (address_components.hasNext())
			{
				Map.Entry  pair = address_components.next();

				if (pair.getKey().equals("address_components"))
				{
					// This flag determines if the 'postal_code' tag has been found
					boolean flagZIPFound = false;
					JSONArray component = (JSONArray) pair.getValue();
					// Iterate over the values in the component
					Iterator componentIterator = component.iterator();
					Iterator<Map.Entry> nextItrl = ((Map) componentIterator.next()).entrySet().iterator();

					while (nextItrl.hasNext())
					{
						Map.Entry nextPair = nextItrl.next();
						String value = nextPair.getValue().toString();

						// There's probably a better way to find the ZIP than doing this.
						// If the flag is on, the last 'type' was 'postal_code', meaning
						// the next value in the json is most likely the ZIP code.
						if (flagZIPFound)
						{
							System.out.println("ZIP found: " + value);
							flagZIPFound = false;
							break;
						}

						if (value.equals("[\"postal_code\"]"))
							flagZIPFound = true;
					}
				}
			}
		}
	}

	/**
	 * Gets a json file of information about a location specified by lat and long coords
	 * @param coords the lat and long coords, separated by a ','
	 * @return the json containing information about the location
	 * @throws ParseException
	 */
	private static JSONObject getLocation(String coords) throws ParseException
	{
		String webAddress = buildUrl(coords);
		String content = null;

		try
		{
			URL url = new URL(webAddress);
			InputStream stream = url.openStream();

			try
			{
				BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
				StringBuffer buffer = new StringBuffer();
				int read;
				char[] chars = new char[1024];
				while ((read = reader.read(chars)) != -1)
					buffer.append(chars, 0, read);

				JSONParser parser = new JSONParser();
				JSONObject json = (JSONObject) parser.parse(buffer.toString());

				return json;
			}

			finally {
				stream.close();
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * Builds a url from coordinates provided by the image(s)
	 * @param coords latitude and longitude coordinates separated by a ','
	 * @return the https request
	 */
	private static String buildUrl(String coords)
	{
		StringBuilder b = new StringBuilder();

		b.append(GEO_SERVER);
		b.append("address=");
		// Coordinates shouldn't have any spaces but in the event that they do
		// they shall be replaced
		b.append(coords.replaceAll(" ", "+"));
		b.append("&sensor=false");
		b.append("&key=" + JmeRef.apiKey);

		System.out.println("URL built: " + b.toString());
		return b.toString();
	}

	/**
	 * Creates the GUI of the program
	 */
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

	/**
	 * Reads metadata information from a given jpeg
	 * @throws ImageProcessingException
	 * @throws IOException
	 */
	private static void readMeta(String filePath) throws ImageProcessingException, IOException
	{
		System.out.println("Reading an image");

		File fileJpeg = new File(filePath);
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
