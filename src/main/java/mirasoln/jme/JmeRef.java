
/**
 * @author Nick Mirasol
 */

package mirasoln.jme;

public class JmeRef
{
	public static final String MAP_API = "https://maps.googleapis.com/maps/api/geocode/json?";
	
	public static final String FLAG_BONUS_INFO = "-b";

	public static final String JPG_TAG_LAT = "[GPS] GPS Latitude - ";
	public static final String JPG_TAG_LNG = "[GPS] GPS Longitude - ";
	
	public static final String API_TAG_ADDR_COMP = "address_components";
	public static final String API_TAG_POSTAL_CODE = "[\"postal_code\"]";
	
	public static final String[] VALID_JPEG_EXT = { "jpg", "jpeg", "jpe", "jif", "jfif", "jfi"};
	
	public static final String EXE_TITLE = "JME - JPEG Metadata Extractor";
	public static final String EXE_MENU_TITLE_FILE = "File";
	public static final String EXE_SUBMENU_OPEN = "Open                          (Ctrl + O)";
	public static final String EXE_SUBMENU_DUMP = "Dump metadata      (Ctrl + D)";
	public static final String EXE_SUBMENU_EXIT = "Exit                             (Ctrl + Esc)";
}
