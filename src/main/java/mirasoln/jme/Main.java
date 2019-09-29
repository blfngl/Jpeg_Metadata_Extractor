
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
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

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
	private static final String API_KEY = "";

	private static JFrame frame;
	private static Logger logger;

	private static boolean flagBonusInfo = false;
	private static boolean flagAllFiles = false;
	private static boolean flagGenerateLink = false;

	/**
	 * The main method (really???). If executed from the command line simply separate all
	 * files desired to be processed, such as:
	 * 
	 * java -cp JME-0.1a-jar-with-dependencies.jar mirasoln.jme.Main DSCN0010.jpg DSCN0012.jpg
	 * 
	 * This will process files DSCN0010.jpg and DSCN0012.jpg.
	 * 
	 * @param args The desired files to process
	 */
	public static void main(String args[])
	{
		initLogger();
		logger.info(JmeRef.HEADER);
		logger.info("  - JPEG Metadata Extractor -");
		logger.info("  - EVT Technical Challenge -");
		logger.info("  -      Nick Mirasol       -\n");

		// If there are command line args
		if (args.length > 0)
		{
			for (String arg : args)
			{
				if (arg.substring(0, 1).equals("-"))
				{
					// The way this is currently set up allows a user to turn the flag on and off
					// for specific files by surrounding them with the flag, inefficient compared
					// to simply appending a file with a tag; maybe I'll get around to that if I
					// have the time.
					if (arg.equals(JmeRef.FLAG_BONUS_INFO))
					{
						flagBonusInfo = !flagBonusInfo;
						logger.info("Displaying bonus info");
					}

					else if (arg.equals(JmeRef.FLAG_GENERATE_LINK))
					{
						flagGenerateLink = !flagGenerateLink;
						logger.info("Generating google maps links of locations found.");
					}

					else if (arg.equals(JmeRef.FLAG_ALL_FILES))
						processDirectory();

					// TODO add more commands?
				}

				else
					processFile(arg);
			}

			System.out.println("\nOutput printed to jme_logs/" + logger.getName() + ".");
			System.out.println("Press enter to exit.");
			Scanner scan = new Scanner(System.in);
			scan.nextLine();
		}

		// If there are no cmdline args, then open the gui
		else
		{
			createGui();
			frame.setVisible(true);
			// TODO make button functionality
		}
	}

	/**
	 * Processes all files within the working directory of the jar.
	 */
	private static void processDirectory()
	{
		File folder = new File(System.getProperty("user.dir"));

		logger.info("Processing all files in working directory:");
		logger.info(folder.getAbsolutePath() + "\n");

		for (File file : folder.listFiles())
			processFile(file.getName());
	}

	/**
	 * Initializes logging
	 */
	private static void initLogger()
	{
		String currentDate = new Date(System.currentTimeMillis()).toString();
		currentDate = currentDate.replace(":", "-");

		logger = Logger.getLogger("JME " + currentDate);

		try {
			FileHandler fh = new FileHandler("jme_logs/" + currentDate + ".txt");
			fh.setFormatter(new JmeLoggerFormatter());
			logger.addHandler(fh);
			logger.setUseParentHandlers(false);
		}

		catch (SecurityException e) {
			logger.log(Level.SEVERE, e.getMessage());
			logger.info(e.getStackTrace().toString());
			e.printStackTrace();
		}

		catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
			logger.info(e.getStackTrace().toString());
			e.printStackTrace();
		}
	}

	/**
	 * Processes a file, first confirming it is a jpeg and then looking for location metadata.
	 * @param filePath The file to process
	 */
	private static void processFile(String filePath)
	{
		if (confirmJpeg(filePath))
		{
			logger.info("\n========== Processing file: " + filePath + " ==========\n");

			try
			{
				String coords;
				coords = getCoordsFromImage(filePath);

				// If no GPS data is found, don't build the url!
				if (coords == null || coords.equals(""))
					logger.log(Level.WARNING, "No GPS data found!");

				else
				{
					if (flagGenerateLink)
						logger.info("Google maps link: " + JmeRef.MAP_LINK + coords);

					JSONObject response;
					response = getLocationData(coords);

					logger.info("ZIP code: " + getZIPFromJson(response));
				}

				logger.info("\nFinished processing file: " + filePath + ".\n");
			}

			catch (Exception e)
			{
				logger.log(Level.SEVERE, "Error: " + e.getMessage());
				logger.log(Level.SEVERE, e.getStackTrace().toString());
			}
		}

		else
			logger.info("Skipping " + filePath);
	}

	/**
	 * Returns true if a file has a valid jpeg file extension.
	 * @param filePath The file to check
	 * @return True if the file has a valid jpeg extension
	 */
	private static boolean confirmJpeg(String filePath)
	{
		String fileName = new File(filePath).getName();
		int indexExtension = fileName.lastIndexOf(".");
		String fileExtension = fileName.substring(indexExtension + 1);

		for (String extension : JmeRef.VALID_JPEG_EXT)
			if (extension.equals(fileExtension))
				return true;

		return false;
	}

	/**
	 * Gets the ZIP code from a json from the google maps api.
	 * @param json The json from the google api to parse
	 * @throws ParseException
	 */
	private static String getZIPFromJson(JSONObject json) throws ParseException
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
				Map.Entry pair = address_components.next();

				if (pair.getKey().equals(JmeRef.API_TAG_ADDR_COMP))
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

						// There's probably a better way to find the ZIP than doing this
						// If the flag is on, the last 'type' was 'postal_code', meaning
						// the next value in the json is most likely the ZIP code
						if (flagZIPFound)
						{
							flagZIPFound = false;
							return value;
						}

						if (value.equals(JmeRef.API_TAG_POSTAL_CODE))
							flagZIPFound = true;
					}
				}
			}
		}

		return "No zip could be associated with the coordinates attached to this image!";
	}

	/**
	 * Gets a json file of information about a location specified by lat and long coords.
	 * @param coords the lat and long coords, separated by a ','
	 * @return the json containing information about the location
	 * @throws ParseException
	 */
	private static JSONObject getLocationData(String coords) throws ParseException
	{
		// Build the url
		String webAddress = buildUrl(coords);
		String content = null;

		try
		{
			// Make request
			URL url = new URL(webAddress);
			// TCP get request
			InputStream stream = url.openStream();

			try
			{
				// Turn the result into a json
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

			// Don't cross the streams
			finally {
				stream.close();
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
			logger.log(Level.SEVERE, e.getStackTrace().toString());
			throw new RuntimeException(e);
		}
	}

	/**
	 * Builds a url from coordinates provided by the image(s).
	 * @param coords latitude and longitude coordinates separated by a ','
	 * @return the https request
	 */
	private static String buildUrl(String coords)
	{
		StringBuilder b = new StringBuilder();

		b.append(JmeRef.MAP_API);
		b.append("address=");
		// Coordinates shouldn't have any spaces but in the event that they do
		// they shall be replaced.
		b.append(coords.replaceAll(" ", "+"));
		b.append("&sensor=false");
		// This is needed for the program to work!
		b.append("&key=" + API_KEY);

		return b.toString();
	}

	/**
	 * This method runs through all the tags found in the metadata of a file and
	 * looks for the GPS information attached, if any.
	 * @param filePath the image
	 * @return the coordinates
	 * @throws IOException 
	 * @throws ImageProcessingException 
	 */
	private static String getCoordsFromImage(String filePath) throws ImageProcessingException, IOException
	{
		StringBuilder coords = new StringBuilder();

		File file = new File(filePath);
		Metadata meta = ImageMetadataReader.readMetadata(file);

		for (Directory dir : meta.getDirectories())
		{
			for (Tag tag : dir.getTags())
			{
				String theTag = tag.toString();

				if (theTag.contains(JmeRef.JPG_TAG_LAT))
				{
					theTag = theTag.substring(JmeRef.JPG_TAG_LAT.length());
					theTag = convertDMStoDecimal(theTag);
					coords.append(theTag);
					coords.append(",");
				}

				else if (theTag.contains(JmeRef.JPG_TAG_LNG))
				{
					theTag = theTag.substring(JmeRef.JPG_TAG_LNG.length());
					theTag = convertDMStoDecimal(theTag);
					coords.append(theTag);
				}
			}
		}

		return coords.toString();
	}

	/**
	 * Converts a DMS (degrees, minutes, seconds) variable to decimal format.
	 * @return the decimal format of a DMS coordinate string
	 */
	private static String convertDMStoDecimal(String coordsDMS)
	{
		// Split the string into decimals, minutes and seconds
		// There are three parts to a DMS ordinate, hopefully we always get all three
		String[] listCoordsDec = new String[3];
		String[] listCoordsDMS = coordsDMS.split(" ");

		// Replace all non-digit characters with blanks
		for (int i = 0; i < 3; i++)
			listCoordsDec[i] = listCoordsDMS[i].replaceAll("[\\D]", "");

		// Convert to decimal
		double degree = Double.parseDouble(listCoordsDec[0]);
		double minute = Double.parseDouble(listCoordsDec[1]);
		double second = Double.parseDouble(listCoordsDec[2]);

		double convertedCoords = degree + minute / 60d + second / 3600d;

		return "" + convertedCoords;
	}

	/**
	 * Creates the GUI of the program.
	 */
	private static void createGui()
	{
		// The window for the program
		frame = new JFrame(JmeRef.EXE_TITLE);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 600);

		// Menu bar
		JMenuBar mb = new JMenuBar();
		JMenu menuFile = new JMenu(JmeRef.EXE_MENU_TITLE_FILE);
		JMenu menuOptions = new JMenu("Options");
		JMenu menuHelp = new JMenu("Help");

		JMenuItem menuFile_Open = new JMenuItem(JmeRef.EXE_SUBMENU_OPEN);
		JMenuItem menuFile_Dump = new JMenuItem(JmeRef.EXE_SUBMENU_DUMP);
		JMenuItem menuFile_Exit = new JMenuItem(JmeRef.EXE_SUBMENU_EXIT);

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
	 * Reads metadata information from a given jpeg.
	 * @throws ImageProcessingException
	 * @throws IOException
	 */
	private static void readMeta(String filePath) throws ImageProcessingException, IOException
	{
		logger.info("Getting metadata from " + filePath);

		File fileJpeg = new File(filePath);
		Metadata meta = ImageMetadataReader.readMetadata(fileJpeg);

		for (Directory dir : meta.getDirectories())
			for (Tag tag : dir.getTags())
				logger.info(tag.toString());
	}
}
