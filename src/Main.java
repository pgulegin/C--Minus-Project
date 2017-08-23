import java.io.IOException;

public class Main
{
    public static void main(String[] args) throws IOException {

        // select which grammar you'd like to run here
//      Grammar grammar = new Grammar("GrammarFiles/GrammarList1.txt");
//		Grammar grammar = new Grammar("GrammarFiles/GrammarList2.txt");
//		Grammar grammar = new Grammar("GrammarFiles/GrammarList3.txt");
//		Grammar grammar = new Grammar("GrammarFiles/GrammarList4.txt");
//		Grammar grammar = new Grammar("GrammarFiles/GrammarList5.txt");
//		Grammar grammar = new Grammar("GrammarFiles/GrammarList6.txt");
//		Grammar grammar = new Grammar("GrammarFiles/GrammarList7.txt");
		Grammar grammar = new Grammar("GrammarFiles/GrammarList8.txt");

        // creates a Parser based on the Grammar
        Parser parser = new Parser(grammar);

        // generates an SLRState from the Grammar
        SLRState slrstate = new SLRState(grammar, parser);

        // generates an SLRTable from the SLRState
        SLRTable slrtable = new SLRTable(grammar, parser, slrstate);

        // view the terminal, non-terminal and production lists in the console
        grammar.print();

        // prints first and follow to file
//		parser.printFirstToFile();
//		parser.printFollowToFile();

        // prints all states to file
//		slrstate.printToFile();

        // prints slr table to file
//		slrtable.printToFile();

        // prints first and follow to console
        parser.printFirstToConsole();
        parser.printFollowToConsole();

        // prints all states to console
        slrstate.printToConsole();

        // prints slr table to console
        slrtable.printToConsole();
    }
}