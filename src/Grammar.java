import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// object which keeps track of the entire grammar of the grammarList.txt
public class Grammar
{
    // permanent symbol for production rules
    public static final String EPSILON = "empty";
    public static final String DOT = "#";

    // variables of the Grammar object class
    public Set<String> terminals = new HashSet<String>();
    public Set<String> nonterminals = new HashSet<String>();
    public List<Production> productions  = new ArrayList<Production>();
    public List<Production> augmentedProductions  = new ArrayList<Production>();

    // constructor for the Grammar object with zero inputs
    public Grammar()
    {
        // do nothing
    }

    // constructor for the Grammar object with multiple file string location inputs
    public Grammar(String terminalListTxt, String nonTerminalListTxt, String grammarListTxt) throws IOException
    {
        // temporary buffer reader to input the terminal, nonterminal and production lists
        BufferedReader temp = null;

        // reads in from the terminalList.txt and places strings in terminal list
        temp = new BufferedReader(new FileReader(terminalListTxt));
        for(String line; (line = temp.readLine()) != null; ) {
            terminals.add(line);
        }
        temp.close();

        // reads in from the nonTerminalList.txt and places strings in nonterminal list
        temp = new BufferedReader(new FileReader(nonTerminalListTxt));
        for(String line; (line = temp.readLine()) != null; ) {
            nonterminals.add(line);
        }
        temp.close();

        // reads in from the GrammarList.txt and places strings in productions list
        temp = new BufferedReader(new FileReader(grammarListTxt));
        for(String line; (line = temp.readLine()) != null; ) {
            productions.add(new Production(line));
        }
        temp.close();

        augmentProduction();
    }

    // constructor for the Grammar object with single file string location input
    public Grammar(String grammarListTxt) throws IOException
    {
        // temporary buffer reader to input the terminal, nonterminal and production lists
        BufferedReader temp = null;

        // reads in from the GrammarList.txt and places strings in productions list
        temp = new BufferedReader(new FileReader(grammarListTxt));
        for(String line; (line = temp.readLine()) != null; ) {

            //adds the production line
            productions.add(new Production(line));

            // finds the terminals and nonterminals
            String[] splitLine = line.split(" ");
            for(int i = 0; i < splitLine.length; i++){
                // skip this line because it is the arrow in the production (i.e. "::" or "->")
                // skip this because it is the empty terminal
                if(i == 1 || splitLine[i].equals(Grammar.EPSILON)){
                    continue;
                }
                else if(containsTerminal(splitLine[i])){
                    terminals.add(splitLine[i]);
                }
                else if(containsNonterminal(splitLine[i])){
                    nonterminals.add(splitLine[i]);
                }
            }
        }
        temp.close();

        // creates the augmented production
        augmentProduction();
    }

    // returns true if the string is a terminal
    public boolean containsTerminal(String string)
    {
        if(string.toCharArray()[0] <= 64 || string.toCharArray()[0] >= 91){
            return true;
        }
        return false;
    }

    // returns true if the string is a nonterminal
    public boolean containsNonterminal(String string)
    {
        if(string.toCharArray()[0] >= 65 || string.toCharArray()[0] <= 90){
            return true;
        }
        return false;
    }

    // create an augmented production list
    public void augmentProduction()
    {
        augmentedProductions.add(new Production(productions.get(0).left + "'", productions.get(0).left));

        for (Production production : productions)
        {
            if (!production.right.get(0).equals(Grammar.EPSILON))
            {
                augmentedProductions.add(new Production(production));
            }
            else
            {
                Production temp = new Production(production);
                temp.right.remove(Grammar.EPSILON);
                augmentedProductions.add(new Production(temp));
            }
        }
    }

    // prints a list of all of the terminals for debugging
    // or, prints statement saying the terminals list is empty
    public void printTerminals()
    {
        // if terminals list is not empty, print all of the terminals
        if (!terminals.isEmpty())
        {
            System.out.println("========== TERMINALS ==========");
            for (String terminal : terminals)
            {
                System.out.println(terminal);
            }
        }
        // if terminals list is empty, print statement
        else
        {
            System.out.println("========== NO TERMINALS ==========");
        }
    }

    // prints a list of all the nonterminals for debugging
    // or, prints statement saying the nonterminals list is empty
    public void printNonTerminals()
    {
        // if the nonterminals list is not empty, print all of the nonterminals
        if (!nonterminals.isEmpty())
        {
            System.out.println("========== NONTERMINALS ==========");
            for (String nonterminal : nonterminals)
            {
                System.out.println(nonterminal);
            }
        }
        // if nonterminals list is empty, print statement
        else
        {
            System.out.println("========== NO NONTERMINALS ==========");
        }
    }

    // prints a list of all the productions for debugging
    // or, prints a statement saying the productions list is empty
    public void printProductions()
    {
        // if the productions list is not empty, print all of the productions
        if (!productions.isEmpty())
        {
            System.out.println("========== PRODUCTION ==========");
            for (Production production : productions)
            {
                // call the specific printing action found in Production class for format
                production.print();
            }
        }
        // if productions list is empty, print statement
        else
        {
            System.out.println("========== NO PRODUCTIONS ==========");
        }
    }

    // prints a list of all the augmented productions for debugging
    // or, prints a statement saying the augmented productions list is empty
    public void printAugmentedProductions()
    {
        // if the augmented productions list is not empty, print all of the augmented productions
        if (!augmentedProductions.isEmpty())
        {
            System.out.println("========== AUGMENTED PRODUCTION ==========");
            for (Production production : augmentedProductions)
            {
                // call the specific printing action found in Production class for format
                production.print();
            }
        }
        // if augmented productions list is empty, print statement
        else
        {
            System.out.println("========== NO AUGMENTED PRODUCTIONS ==========");
        }
    }

    // prints all of the print statements in the Grammar class
    public void print()
    {
        printTerminals();
        printNonTerminals();
        printProductions();
        printAugmentedProductions();
    }
}