package pl241_compiler.components;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileReader
{
	static Logger LOGGER = LoggerFactory.getLogger(FileReader.class);

	public static final char ERROR = (char) 0;
	public static final char EOF = (char) 255;

	private Reader streamReader;
	private boolean validState = true;

	private char sym; 

	public FileReader(String fileName) throws FileNotFoundException
	{
		InputStream inputStream = new FileInputStream(new File(fileName));  //file becomes an input stream
		streamReader = new BufferedReader(new InputStreamReader(inputStream));  //read the file as a buffered input stream for efficiency

		try {
			do{
				sym = (char) streamReader.read();  //initialize symbol to first char in input
			} while(Character.isWhitespace(sym));

			if(sym == (char) -1)  //EOF
			{
				sym = EOF;
				validState = false;
			}
				
		} catch (IOException e) {
			error(e.toString());
		}
	}

	public void error(String errorMsg)
	{
		sym = ERROR;
        validState = false;
	}

	public char getSym()
	{
        char current = sym;
		if(validState)
		{
			try {
				sym = (char) streamReader.read();
			} catch (IOException e) {
				error(e.toString());
			}
		} 
		return current;
	} 

	/*
	private enum ReaderSymbol
	{
		ERROR(0), EOF(255);
		final int value;

		ReaderSymbol(int value)
		{
			this.value = value;
		}
	}
	*/
}