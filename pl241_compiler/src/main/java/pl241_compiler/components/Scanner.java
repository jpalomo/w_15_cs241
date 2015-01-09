package pl241_compiler.components;

import java.io.FileNotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class Scanner
{
	static Logger LOGGER = LoggerFactory.getLogger(Scanner.class);

	private FileReader fileReader;

	private int sym;
	public Token token;
	public char inputSym;

	public Scanner(String fileName) throws FileNotFoundException
	{
		Preconditions.checkArgument(StringUtils.isNotEmpty(fileName), "the input file loction cannot be empty or null");
		fileReader = new FileReader(fileName);
	}

	public int getToken()
	{
		if(inputSym == FileReader.EOF)
		{
			sym = Token.EOF_TOKEN.getValue();
		}
		else if(inputSym == FileReader.ERROR)
		{
			sym = Token.ERR_TOKEN.getValue();
		}

		return sym;
	}

	public void error(String errorMsg)
	{
		fileReader.error(errorMsg);
	}

	private void next() 
	{
		LOGGER.debug("Getting next input character...");
		inputSym = fileReader.getSym();
	}
}
