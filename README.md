## Jpeg_Metadata_Extractor - EVT Technical Challenge

This program takes in a set of files and provides a zip code based on the GPS metadata attached to the file(s).  
The file(s) must have a valid JPEG file extension in order to be processed.

### Usage

Executing the .jar will open a GUI (WIP).  

Otherwise, run the application via the scripts provided (Start.bat | Start.sh). Edit these if needed!

Finally, to run from the command line:  
```java -cp [jar to run] mirasoln.jme.Main [args]```

### Command line arguments

If you desire to use any of these args, make sure to include them before providing any files to be read.

#### -l  
Generates a link to view the location found on google maps.

#### -a  
The tool will process all available files found in the directory.

#### -b  
Prints/dumps other tags found on the JPEG.
