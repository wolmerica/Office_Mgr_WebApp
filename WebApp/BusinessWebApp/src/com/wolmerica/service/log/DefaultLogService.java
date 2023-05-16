package com.wolmerica.service.log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * @author  Richard Wolschlager
 * @date    July 31, 2010
 */

public class DefaultLogService implements LogService {

  Logger cat = Logger.getLogger("WOWAPP");

//---------------------------------------------------------------------------
// Fetch the entire contents of a text file, and return it in a String.
// This style of implementation does not throw Exceptions to the caller.
//
// @param aFile is a file which already exists and can be read.
//---------------------------------------------------------------------------
  public String getContents(String logFileName)
   throws IOException {

    StringBuffer contents = new StringBuffer();

    BufferedReader input = null;
    try {
      File logFile = new File(logFileName);

      cat.debug(this.getClass().getName() + ": logFileName = " + logFileName);

//---------------------------------------------------------------------------
// use buffering, reading one line at a time
// FileReader always assumes default encoding is OK!
//---------------------------------------------------------------------------
      input = new BufferedReader( new FileReader(logFile) );
      String line = null; //not declared within while loop
//---------------------------------------------------------------------------
// readLine is a bit quirky :
// it returns the content of a line MINUS the newline.
// it returns null only for the END of the stream.
// it returns an empty String if two newlines appear in a row.
//---------------------------------------------------------------------------
      while (( line = input.readLine()) != null){
        contents.append(line);
        contents.append(System.getProperty("line.separator"));
      }
    }
    catch (FileNotFoundException ex) {
      ex.printStackTrace();
    }
    catch (IOException ex){
      ex.printStackTrace();
    }
    finally {
      try {
        if (input!= null) {
//---------------------------------------------------------------------------
// flush and close both "input" and its underlying FileReader
//---------------------------------------------------------------------------
          input.close();
        }
      }
      catch (IOException ex) {
        ex.printStackTrace();
      }
    }
    cat.debug(this.getClass().getName() + ": contents = " + contents.toString());
    return contents.toString();
  }

}