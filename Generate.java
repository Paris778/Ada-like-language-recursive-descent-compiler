
/*******************************************************
 * Title: 
 * A Syntax Analyser Implementation for SCC-312
 * Academic Year: 2020-2021
 * 
 * @author Paraksevas Solomou | 34838805
 * @version Final as of 11/03/2021 01:17
 ******************************************************/


 /**
  * IMPORTANT EXCEPTIONS THROUGHOUT THIS CLASS :
  * @exception CompilationException : Signals that an exception in the compialtion process has occured.
  */

// This class is responsible for generating the ERRORS 
// detected by the syntax analyser class
public class Generate extends AbstractGenerate{

    //Constructor
    public Generate(){
    }

    /**
     * @author Paraskevas Solomou
     * @param token : The next token in the buffer
     * @param explanatoryMessage : The message associated with the current error detected during runtime
     */
    @Override
    public void reportError(Token token, String explanatoryMessage) throws CompilationException {
        throw new CompilationException(explanatoryMessage);
    }
    
}
