## Jpeg_Metadata_Extractor - EVT Technical Challenge

This program takes in a set of files and provides a zip code based on the GPS metadata attached to the file(s).  
The file(s) must have a valid JPEG file extension in order to be processed.

### Usage

Executing the .jar will scan all files within the working directory.

Otherwise, you may run the application via the scripts provided (Start.bat | Start.sh). Edit these if needed!

Finally, to run the application from the command line:  
```java -cp [jar to run] mirasoln.jme.Main [args]```

### Command line arguments

#### -l  
Generates a link to view the location found on google maps.

#### -a  
The tool will process all available files found in the directory.

#### -b  
WIP
Prints/dumps other tags found on the JPEG.
