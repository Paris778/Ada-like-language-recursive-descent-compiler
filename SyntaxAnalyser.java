import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

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
  * @exception IOException : Input Output Exception. Signals that an IO Exception of some sort has occured
  * @exception CompilationException : Signals that an exception in the compialtion process has occured.
  */


 /////////////////////////////////////////////////////////
 //This class is the implementation of a Syntax Analyser
 //It Makes sure that the syntax of the user is correct 
 //and generates the appropriate error messages and stack traces
 //-- extends  AbstractSyntaxAnalyser 
public class SyntaxAnalyser extends AbstractSyntaxAnalyser {

    //////////////////////////////////////////////////////
    // CONVENIENT CONSTANTS
    private final String STATEMENT_PART = "StatementPart";
    private final String STATEMENT_LIST = "StatementList";
    private final String STATEMENT = "Statement";
    private final String ASSIGNMENT_STATEMENT = "AssignmentStatement";
    private final String IF_STATEMENT = "IfStatement";
    private final String CONDITION = "Condition";
    private final String WHILE_STATEMENT = "WhileStatement";
    private final String PROCEDURE_STATEMENT = "ProcedureStatement";
    private final String ARGUMENT_LIST = "ArgumentList";
    private final String UNTIL_STATEMENT = "UntilStatement";
    private final String FOR_STATEMENT = "ForStatement";
    private final String CONDITIONAL_OPERATOR = "ConditionalOperator";
    //
    private final String EXPRESSION = "Expression";
    private final String TERM = "Term";
    private final String FACTOR = "Factor";

    private String file_Name;


    /////////////////////////////////////////////////////
    /** Class Constructor 
    ** @author Paraskevas Solomou
    ** @param fileName : The string path of the file the Syntax Analyser is reading from
    **/
    public SyntaxAnalyser(String fileName){
        this.file_Name = fileName;
        //System.out.println("Syntax Analyser Made !");
        try {
            this.lex = new LexicalAnalyser(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Start of main statement
    @Override
    public void _statementPart_() throws IOException, CompilationException {      //Checked with grammar
        this.myGenerate.commenceNonterminal(STATEMENT_PART);
        this.acceptTerminal(Token.beginSymbol);
        try {
            this.statement_List();
        } catch (CompilationException e) {
           throw new CompilationException(this.STATEMENT_LIST, e);
        }
        
        this.acceptTerminal(Token.endSymbol);
        this.myGenerate.finishNonterminal(STATEMENT_PART);
    }

    //Method to Accept a Terminal Symbol 
    @Override
    public void acceptTerminal(int symbol) throws IOException, CompilationException {        
        if(nextToken.symbol == symbol){
            this.myGenerate.insertTerminal(nextToken);
            this.nextToken = this.lex.getNextToken();
          }
          else{            
            
            this.myGenerate.reportError(nextToken, 
            "\n\t\t- Error (Bottom of Stack) in file: "
            +  this.file_Name + "\n\t\t\t- On Line: " + nextToken.lineNumber 
            +  "\n\t\t\t- Reason : \n\t\t\t\t- Expected : " 
            +  Token.getName(symbol) + "\n\t\t\t\t- But got : " 
            +  Token.getName(nextToken.symbol)
            +  this.makeStackTrace()

            );
          }
    }

    // Statement List Method. 
    //Can be a single statement or a series of statements
    private void statement_List() throws IOException, CompilationException{  //checked with grammar
        this.myGenerate.commenceNonterminal(STATEMENT_LIST);

        try {
            this.statement();
        } catch (CompilationException e) {
           throw new CompilationException(this.makeErrorMsg(this.STATEMENT,this.nextToken), e);
        }
        

        while(nextToken.symbol==(Token.semicolonSymbol)){
            this.acceptTerminal(Token.semicolonSymbol);
            try {
                this.statement_List();
            } catch (CompilationException e) {
               throw new CompilationException(this.makeErrorMsg(this.STATEMENT_LIST,this.nextToken), e);
            }
        } 
    
        //
        myGenerate.finishNonterminal(STATEMENT_LIST);
    }

    // Statement Method
    private void statement() throws IOException, CompilationException{  //Checked with grammar
        myGenerate.commenceNonterminal(STATEMENT);

        switch (nextToken.symbol){
            case Token.identifier  -> { try{this.assignmentStatement();} catch(CompilationException e){ throw new CompilationException(this.makeErrorMsg(this.ASSIGNMENT_STATEMENT,this.nextToken),e);}}
            case Token.ifSymbol    -> { try{this.ifStatement();}         catch(CompilationException e){ throw new CompilationException(this.makeErrorMsg(this.IF_STATEMENT,this.nextToken),e);}}
            case Token.whileSymbol -> { try{this.whileStatement();}      catch(CompilationException e){ throw new CompilationException(this.makeErrorMsg(this.WHILE_STATEMENT,this.nextToken),e);}}
            case Token.callSymbol  -> { try{this.procedureStatement();}  catch(CompilationException e){ throw new CompilationException(this.makeErrorMsg(this.PROCEDURE_STATEMENT,this.nextToken),e);}}
            case Token.doSymbol    -> { try{this.untilStatement();}      catch(CompilationException e){ throw new CompilationException(this.makeErrorMsg(this.UNTIL_STATEMENT,this.nextToken),e);}}  
            case Token.forSymbol   -> { try{this.for_Statement();}       catch(CompilationException e){ throw new CompilationException(this.makeErrorMsg(this.FOR_STATEMENT,this.nextToken),e);}}
            //Throw Exception With iNformation
            default                ->  this.myGenerate.reportError(nextToken, 
                                        "\n\t\t-Error in file: "+ this.file_Name 
                                        + "\n\t\tOn Line: " + nextToken.lineNumber 
                                        + "\n\t\tReason : \n\t\t\tExpected : " 
                                        +  "Expressions: <assignment statement> | <if statement> | <while statement> | <procedure statement> | <until statement> | <for statement> "
                                        + "\n\t\t\tBut got : " + Token.getName(nextToken.symbol)
                                        + "\n\t\t* Google it if you're not sure buddy !!!"
                                        +  this.makeStackTrace()
                                        );
        }

        myGenerate.finishNonterminal(STATEMENT);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    //Assignment sStatement Method
    private void assignmentStatement() throws IOException, CompilationException { //Checked with grammar
        this.myGenerate.commenceNonterminal(ASSIGNMENT_STATEMENT);

        this.acceptTerminal(Token.identifier);
        this.acceptTerminal(Token.becomesSymbol);

        if (nextToken.symbol == Token.stringConstant) { this.acceptTerminal(Token.stringConstant); }
        else { 
            try {
                this.expression();
            } catch (CompilationException e) {
               throw new CompilationException(this.makeErrorMsg(this.EXPRESSION,this.nextToken), e);
            }
        }

        this.myGenerate.finishNonterminal(ASSIGNMENT_STATEMENT);
    }


    //If Statement Method
    private void ifStatement() throws IOException, CompilationException{  //Checked with grammar
        this.myGenerate.commenceNonterminal(IF_STATEMENT);
        
        if(this.nextToken.symbol == Token.ifSymbol){
            this.acceptTerminal(Token.ifSymbol);           
            try {
                this.condition();
            } catch (CompilationException e) {
               throw new CompilationException(this.makeErrorMsg(this.CONDITION,this.nextToken), e);
            }
            this.acceptTerminal(Token.thenSymbol);
            //
            try {
                this.statement_List();
            } catch (CompilationException e) {
               throw new CompilationException(this.makeErrorMsg(this.STATEMENT_LIST,this.nextToken), e);
            }
            //
            if(this.nextToken.symbol == Token.elseSymbol){
                this.acceptTerminal(Token.elseSymbol);
                //
                try {
                    this.statement_List();
                } catch (CompilationException e) {
                   throw new CompilationException(this.makeErrorMsg(this.STATEMENT_LIST,this.nextToken), e);
                }
            }
            //
            this.acceptTerminal(Token.endSymbol);
            this.acceptTerminal(Token.ifSymbol);
        }

        this.myGenerate.finishNonterminal(IF_STATEMENT);
    }

    //For loops statement Method
    private void for_Statement() throws IOException, CompilationException { //checked with grammar
        this.myGenerate.commenceNonterminal(FOR_STATEMENT);

        if(this.nextToken.symbol == Token.forSymbol){
            this.acceptTerminal(Token.forSymbol);
            this.acceptTerminal(Token.leftParenthesis);
            //
            
            try {
                this.assignmentStatement();
            } catch (CompilationException e) {
               throw new CompilationException(this.makeErrorMsg(this.ASSIGNMENT_STATEMENT,this.nextToken), e);
            }
            //
            this.acceptTerminal(Token.semicolonSymbol);
            //            
            try {
                this.condition();
            } catch (CompilationException e) {
               throw new CompilationException(this.makeErrorMsg(this.CONDITION,this.nextToken), e);
            }
            //
            this.acceptTerminal(Token.semicolonSymbol);
            //
            try {
                this.assignmentStatement();
            } catch (CompilationException e) {
               throw new CompilationException(this.makeErrorMsg(this.ASSIGNMENT_STATEMENT,this.nextToken), e);
            }
            //
            this.acceptTerminal(Token.rightParenthesis);
            this.acceptTerminal(Token.doSymbol);
            //
            try {
                this.statement_List();
            } catch (CompilationException e) {
               throw new CompilationException(this.makeErrorMsg(this.STATEMENT_LIST,this.nextToken), e);
            }
            //
            this.acceptTerminal(Token.endSymbol);
            this.acceptTerminal(Token.loopSymbol);
        }
        else{
            //Error ? 
        }

        this.myGenerate.finishNonterminal(FOR_STATEMENT);
    }

    //While Loop statement method
    private void whileStatement() throws IOException, CompilationException{ //Checked with grammar
        this.myGenerate.commenceNonterminal(WHILE_STATEMENT);

        if(this.nextToken.symbol == Token.whileSymbol){
            this.acceptTerminal(Token.whileSymbol);
            try {
                this.condition();
            } catch (CompilationException e) {
               throw new CompilationException(this.makeErrorMsg(this.CONDITION,this.nextToken), e);
            }
            this.acceptTerminal(Token.loopSymbol);
            //
            try {
                this.statement_List();
            } catch (CompilationException e) {
               throw new CompilationException(this.makeErrorMsg(this.STATEMENT_LIST,this.nextToken), e);
            }
            //
            this.acceptTerminal(Token.endSymbol);
            this.acceptTerminal(Token.loopSymbol);
        }
        else{
            // Error ? 
        }

        this.myGenerate.finishNonterminal(WHILE_STATEMENT);
    }

    // Procedure statement method
    // Also known as CALL
    private void procedureStatement() throws IOException, CompilationException{  //Checked with grammar
        this.myGenerate.commenceNonterminal(PROCEDURE_STATEMENT);

        if(this.nextToken.symbol == Token.callSymbol){
            this.acceptTerminal(Token.callSymbol);
            this.acceptTerminal(Token.identifier);
            this.acceptTerminal(Token.leftParenthesis);
            //
            try {
                this.argument_List();
            } catch (CompilationException e) {
               throw new CompilationException(this.makeErrorMsg(this.ARGUMENT_LIST,this.nextToken), e);
            }
            //
            this.acceptTerminal(Token.rightParenthesis);
        }
        else{
            //Error ? 
        }

        this.myGenerate.finishNonterminal(PROCEDURE_STATEMENT);
    }

    //Until Statement Method
    private void untilStatement() throws IOException, CompilationException{  //Checked with grammar
        myGenerate.commenceNonterminal(UNTIL_STATEMENT);

        if(nextToken.symbol == Token.doSymbol){
          this.acceptTerminal(Token.doSymbol);
          try {
            this.statement_List();
            } catch (CompilationException e) {
                throw new CompilationException(this.makeErrorMsg(this.STATEMENT_LIST,this.nextToken), e);
            }
          this.acceptTerminal(Token.untilSymbol);
          try {
            this.condition();
            } catch (CompilationException e) {
                throw new CompilationException(this.makeErrorMsg(this.CONDITION,this.nextToken), e);
            }
        }
        else {
          //Error ? 
        }
        myGenerate.finishNonterminal(UNTIL_STATEMENT);
    }

    //Condition Method
    private void condition() throws IOException, CompilationException {   // CHecked with grammar
        this.myGenerate.commenceNonterminal(CONDITION);

        if(nextToken.symbol == Token.identifier){
            this.acceptTerminal(Token.identifier);
            //
            try {
                this.conditional_Operator();
            } catch (CompilationException e) {
               throw new CompilationException(this.makeErrorMsg(this.CONDITIONAL_OPERATOR,this.nextToken), e);
            }
            //
      
            switch(nextToken.symbol){
              case Token.identifier     ->  this.acceptTerminal(Token.identifier);
              case Token.numberConstant ->  this.acceptTerminal(Token.numberConstant);
              case Token.stringConstant ->  this.acceptTerminal(Token.stringConstant);
              //default -> System.out.println("----------Report error"); //report error
            }

        }
        else {
            // Error ? 
        }
        myGenerate.finishNonterminal(CONDITION);
    }

    //Argument List Method
    private void argument_List() throws IOException, CompilationException{  //checked with grammar
        myGenerate.commenceNonterminal(ARGUMENT_LIST);

        if(nextToken.symbol == Token.identifier){
            this.acceptTerminal(Token.identifier);

            while(nextToken.symbol == Token.commaSymbol){
                this.acceptTerminal(Token.commaSymbol);
                try {
                    this.argument_List();
                } catch (CompilationException e) {
                    throw new CompilationException(this.makeErrorMsg(this.ARGUMENT_LIST,this.nextToken), e);
                }
            } 
        }
        else {
            // Error ? 
        }

        myGenerate.finishNonterminal(ARGUMENT_LIST);
    }

    //Conditional Operator Method
    //Conditionals are greater than and equals for example
    private void conditional_Operator() throws IOException, CompilationException{  // Checked with grammar
        this.myGenerate.commenceNonterminal(CONDITIONAL_OPERATOR);
        switch(nextToken.symbol){
            case Token.greaterThanSymbol   ->  acceptTerminal(Token.greaterThanSymbol);           
            case Token.greaterEqualSymbol  ->  acceptTerminal(Token.greaterEqualSymbol);          
            case Token.equalSymbol         ->  acceptTerminal(Token.equalSymbol);        
            case Token.notEqualSymbol      ->  acceptTerminal(Token.notEqualSymbol);      
            case Token.lessThanSymbol      ->  acceptTerminal(Token.lessThanSymbol);
            case Token.lessEqualSymbol     ->  acceptTerminal(Token.lessEqualSymbol);
            default                        ->    this.myGenerate.reportError(nextToken, 
                                                "\n\t\tError in file: "+ this.file_Name 
                                                + "\n\t\t On Line: " + nextToken.lineNumber 
                                                + "\n\t\t Reason : \n\t\t\tExpected : " 
                                                +  "Conditional symbols:  > , < , >=, <= , == , != "
                                                + "\n\t\t\tBut got : " + Token.getName(nextToken.symbol)
                                                +  this.makeStackTrace()
                                                );

          }
        this.myGenerate.finishNonterminal(CONDITIONAL_OPERATOR);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    //Expression Method
    private void expression() throws IOException, CompilationException{  //checked with grammar
        this.myGenerate.commenceNonterminal(EXPRESSION);

        try {
            this.term();
            } catch (CompilationException e) {
                throw new CompilationException(this.makeErrorMsg(this.TERM,this.nextToken), e);
            } 

        if ( nextToken.symbol == Token.plusSymbol || nextToken.symbol == Token.minusSymbol ) 
        {
            switch(this.nextToken.symbol){
                case Token.plusSymbol    -> { acceptTerminal(Token.plusSymbol);   
                                                    try {
                                                        this.expression();
                                                    } catch (CompilationException e) {
                                                        throw new CompilationException(this.makeErrorMsg(this.EXPRESSION,this.nextToken), e);
                                                    } 
                                            }
                case Token.minusSymbol   -> { acceptTerminal(Token.minusSymbol);  
                                                    try {
                                                        this.expression();
                                                    } catch (CompilationException e) {
                                                        throw new CompilationException(this.makeErrorMsg(this.EXPRESSION,this.nextToken), e);
                                                    } 
                                            }
                default                  ->    this.myGenerate.reportError(nextToken, 
                                                "\n\t\tError in file: "+ this.file_Name 
                                                + "\n\t\t On Line: " + nextToken.lineNumber 
                                                + "\n\t\t Reason : \n\t\t\tExpected : " 
                                                +  "Symbols: + or -"
                                                + "\n\t\t\tBut got : " + Token.getName(nextToken.symbol)
                                                +  this.makeStackTrace()
                                                );
            }
        }

        this.myGenerate.finishNonterminal(EXPRESSION);
    }

    private void factor() throws IOException, CompilationException{  //checked with grammar
        this.myGenerate.commenceNonterminal(FACTOR);

        switch(this.nextToken.symbol){
            case Token.identifier       ->   acceptTerminal(Token.identifier); 
            case Token.numberConstant   ->   acceptTerminal(Token.numberConstant); 
            case Token.leftParenthesis  -> { acceptTerminal(Token.leftParenthesis);  
                                                try {
                                                    this.expression();
                                                } catch (CompilationException e) {
                                                    throw new CompilationException(this.makeErrorMsg(this.EXPRESSION,this.nextToken), e);
                                                } 
                                                acceptTerminal(Token.rightParenthesis); 
                                            }
           
        }

        this.myGenerate.finishNonterminal(FACTOR);
    }

  
    private void term() throws IOException, CompilationException {  //Checked with grammar
        this.myGenerate.commenceNonterminal(TERM);
        this.factor();

        while( nextToken.symbol == Token.divideSymbol || nextToken.symbol == Token.timesSymbol) { 
            switch(this.nextToken.symbol){
                case Token.timesSymbol   ->  { acceptTerminal(Token.timesSymbol);   
                                                try {
                                                    this.term();
                                                    } catch (CompilationException e) {
                                                        throw new CompilationException(this.makeErrorMsg(this.TERM,this.nextToken), e);
                                                    } 
                                             }
                case Token.divideSymbol  ->  { acceptTerminal(Token.divideSymbol);    
                                                try {
                                                    this.term();
                                                    } catch (CompilationException e) {
                                                        throw new CompilationException(this.makeErrorMsg(this.TERM,this.nextToken), e);
                                                } 
                                             }
               
            }
        }

        this.myGenerate.finishNonterminal(TERM);
    }


    /**Method to generate recursive error messages
    ** @author Paraskevas Solomou
    ** @param expected : A String of what is normally expected according to the grammar
    ** @param nextToken : The next token in the buffer
    **/
    private String makeErrorMsg(String expected , Token nextToken){

        String msg = ("\n\t\t- Stack Trace  in file: "+ this.file_Name 
                       + " | Line: " + nextToken.lineNumber 
                       + "\n\t\t\t- Expected : " + expected
                       + "\n\t\t\t- Got : " + Token.getName(nextToken.symbol));
        return msg;
    }

    //Method to generate additional stack trace exception messages
    private String makeStackTrace(){
        StringWriter writ = new StringWriter();
        new Exception().printStackTrace(new PrintWriter(writ));

        return "\n\t\t-----------------------------\n" + writ.toString() + "\n\t\t--------------------------------\n";
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////
    //End of Syntax Analyser Class
}
