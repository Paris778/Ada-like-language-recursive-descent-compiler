# Ada-like-language-recursive-descent-compiler
An implementation of a syntax analyzer (SA) for an Ada-like language using a recursive descent parser. The analyzer’s sole function is to make sure a user’s source program is syntactically correct. The program also generates helpful recursive error massages in a stack trace like most modern compilers.

## Demo

### This is an example program (porgram 2) in an ADA - like language serving as the input to this project
![Alt text](https://github.com/Paris778/Ada-like-language-recursive-descent-compiler/blob/main/compiler_screenshots/Capture3.JPG "Title")

### The program iterates through the input and identifies the objects passed through the parser 
![Alt text](https://github.com/Paris778/Ada-like-language-recursive-descent-compiler/blob/main/compiler_screenshots/Capture.JPG "Title")

### If a mistake is found, the program throws an exception with a stack trace and helpful error messages for the user including line numbers and expected tokens
![Alt text](https://github.com/Paris778/Ada-like-language-recursive-descent-compiler/blob/main/compiler_screenshots/Capture2.JPG "Title")

## How to run

#### Windows: 
```bash
makeClean.bat
compile.bat
execute.bat
```

##
