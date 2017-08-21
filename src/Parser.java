import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

// Parser class which will parse the grammar for the First
public class Parser
{
    // an object which will track the firsts' and follow's by mapping them to a main key
    public Map<String, Set<String>> first = new LinkedHashMap<String, Set<String>>();
    public Map<String, Set<String>> follow = new LinkedHashMap<String, Set<String>>();

    // calls this method to create the first from the grammar
    public Parser(Grammar grammar)
    {
        createFirst(grammar);
        createFollow(grammar);
    }

//    public Parser()
//    {
//        // TODO Auto-generated constructor stub
//    }

    // creates the first from the grammar passed in
    public void createFirst(Grammar grammar)
    {

        // pulls all the nonterminals from the nonTerminalList.txt and
        // places them in the first object as a map key
        for (String nonterminal : grammar.nonterminals)
        {
            // places empty set as the set for the main key
            first.put(nonterminal, new HashSet<String>());
        }

        // goes through the entire production and places the first element from the right side of the
        // production into the first
        for (Production production : grammar.productions)
        {
            first.get(production.left).add(production.right.get(0));
        }

        // filters through the premature first set to check for empty's
        for (Entry<String, Set<String>> entry : first.entrySet())
        {
            // executes when an empty is found
            if (entry.getValue().contains(Grammar.EPSILON))
            {
                // executes when empty is found, it will find all the productions with the same nonterminal and
                // add the rest of the productions into the premature first list
                for (Production production : grammar.productions)
                {
                    // executes when it finds a matching rule
                    if (production.left.equals(entry.getKey()))
                    {
                        // adds in the rest of the nonterminals
                        for (String set : production.right)
                        {
                            // adds in the current production right string
                            entry.getValue().add(set);

                            // exits loop once it finds a terminal that doesn't end in empty otherwise it reaches the end
                            if (set.equals(Grammar.EPSILON) ||
                                    first.get(set) == null ||
                                    !first.get(set).contains(Grammar.EPSILON))
                            {
                                break;
                            }
                        }
                    }
                }
            }
        }

        // finalizes the first list
        finalize(grammar, first);
    }

    // creates the follow
    public void createFollow(Grammar grammar)
    {
        // pulls all the nonterminals from the nonTerminalList.txt and
        // places them in the first object as a map key
        for (String nonterminal : grammar.nonterminals)
        {
            // places empty set as the set for the main key
            follow.put(nonterminal, new HashSet<String>());
        }

        // place the $ in the starter symbol
        follow.get(grammar.productions.get(0).left).add("$");

        // goes through the entire production and places the last element from the right side of the
        // production into the first if it is a nonterminal
        for (Production production : grammar.productions)
        {
            // cycles through the right side of the production
            for (int mainIndex = 0; mainIndex < production.right.size(); mainIndex++)
            {
                // skip all empty's and terminals
                if (production.right.get(mainIndex).contains(Grammar.EPSILON) || grammar.terminals.contains(production.right.get(mainIndex)))
                {
                    continue;
                }

                // executes when the current element is at the end of the production rule
                if (mainIndex == production.right.size()-1)
                {
                    follow.get(production.right.get(mainIndex)).add(production.left);
                }
                // executes when the current element's next element is a terminal
                else if (grammar.terminals.contains(production.right.get(mainIndex+1)))
                {
                    follow.get(production.right.get(mainIndex)).add(production.right.get(mainIndex+1));
                }
                // executes when the current element's next element is a nonterminal
                else
                {
                    // adds in the first(next element)
                    follow.get(production.right.get(mainIndex)).addAll(first.get(production.right.get(mainIndex+1)));

                    // executes if it sees that a first was added which contains an empty and continues the loop
                    if (follow.get(production.right.get(mainIndex)).contains(Grammar.EPSILON))
                    {
                        // removes the empty from the follow set
                        follow.get(production.right.get(mainIndex)).remove(Grammar.EPSILON);

                        for (int emptyIndex = mainIndex+1; emptyIndex < production.right.size(); emptyIndex++)
                        {
                            // executes when the current element is at the end of the production rule
                            if (emptyIndex == production.right.size()-1)
                            {
                                follow.get(production.right.get(mainIndex)).add(production.left);
                            }
                            // executes when the current element's next element is a terminal
                            else if (grammar.terminals.contains(production.right.get(emptyIndex+1)))
                            {
                                follow.get(production.right.get(mainIndex)).add(production.right.get(emptyIndex+1));
                            }
                            // executes when the current element's next element is a nonterminal
                            else
                            {
                                // adds in the first(next element)
                                follow.get(production.right.get(mainIndex)).addAll(first.get(production.right.get(emptyIndex+1)));

                                // executes if it sees that a first was added which contains an empty and continues the loop
                                if (follow.get(production.right.get(mainIndex)).contains(Grammar.EPSILON))
                                {
                                    follow.get(production.right.get(mainIndex)).remove(Grammar.EPSILON);
                                }
                                // breaks out of the loop if a first was added without an empty
                                else
                                {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        // finalizes the follow list
        finalize(grammar, follow);
    }

    // finalizes the set that is passed into it by finding appropriate replacements
    public void finalize(Grammar grammar, Map<String, Set<String>> set)
    {
        // takes the first list and finds the firsts of all the terminals
        // executes until the first list contains only nonterminals
        while (!containsAllTerminals(grammar, set))
        {
            // cycles through every entry in the first list
            for (Entry<String, Set<String>> entry : set.entrySet())
            {
                // executes if current first(nonterminal) is finished
                if (containsAllTerminals(grammar, entry))
                {
                    // continues to the next nonterminal
                    continue;
                }

                // creates a temporary set to hold the current nonterminal's nonterminals
                Set<String> temp = new HashSet<String>();
                temp.addAll(entry.getValue());
                // clears out entry's list to make room for new variables
                entry.getValue().clear();

                // cycles through every element in the temporary list
                for (String value : temp)
                {
                    // executes if element is a terminal or '$'
                    if (grammar.terminals.contains(value) || value.equals("$"))
                    {
                        // adds the terminal back into the first and continues
                        entry.getValue().add(value);
                    }
                    // executes if element is an empty set
                    else if (value.equals(Grammar.EPSILON))
                    {
                        entry.getValue().add(Grammar.EPSILON);
                    }
                    // executes if empty is a nonterminal
                    else
                    {
                        entry.getValue().addAll(set.get(value));
                        entry.getValue().remove(value);
                    }
                }
            }
        }
    }

    // checks if a single entry contains all terminals
    boolean containsAllTerminals(Grammar grammar, Entry<String, Set<String>> entry)
    {
        // cycles through every element in an entry
        for (String value : entry.getValue())
        {
            if (!grammar.terminals.contains(value) &&
                    !value.equals(Grammar.EPSILON) &&
                    !value.equals("$"))
            {
                return false;
            }
        }
        return true;
    }

    // checks if first contains all terminals
    boolean containsAllTerminals(Grammar grammar, Map<String, Set<String>> map)
    {
        // cycles through every entry
        for (Entry<String, Set<String>> entry : map.entrySet())
        {
            if (!containsAllTerminals(grammar, entry))
            {
                return false;
            }
        }
        return true;
    }

    // prints out the entire first list with proper formatting to file
    public void printFirstToFile() throws FileNotFoundException
    {
        // sets System.out to file
        System.setOut(new PrintStream(new FileOutputStream("First.txt")));

        for (Entry<String, Set<String>> entry : first.entrySet())
        {
            System.out.print(entry.getKey() + " ::");
            for (String set : entry.getValue())
            {
                // only uncomment the following lines if you do not want to print empty
                //if (!set.contains(Grammar.EPSILON))
                //{
                System.out.print(" " + set);
                //}
            }
            System.out.println();
        }

        // sets System.out to console
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    }

    // prints out the entire first list with proper formatting to file
    public void printFollowToFile() throws FileNotFoundException
    {
        // sets Sytem.out to file
        System.setOut(new PrintStream(new FileOutputStream("Follow.txt")));

        for (Entry<String, Set<String>> entry : follow.entrySet())
        {
            System.out.print(entry.getKey() + " ::");
            for (String set : entry.getValue())
            {
                System.out.print(" " + set);
            }
            System.out.println();
        }

        // sets System.out to console
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    }

    // prints the first to the console
    public void printFirstToConsole()
    {
        System.out.println("========== FIRST SET  ==========");

        for (Entry<String, Set<String>> entry : first.entrySet())
        {
            System.out.print(entry.getKey() + " ::");
            for (String set : entry.getValue())
            {
                // only uncomment the following lines if you do not want to print empty
                //if (!set.contains(Grammar.EPSILON))
                //{
                System.out.print(" " + set);
                //}
            }
            System.out.println();
        }
    }

    // prints the follow to console
    public void printFollowToConsole()
    {
        System.out.println("========== FOLLOW SET ==========");

        for (Entry<String, Set<String>> entry : follow.entrySet())
        {
            System.out.print(entry.getKey() + " ::");
            for (String set : entry.getValue())
            {
                System.out.print(" " + set);
            }
            System.out.println();
        }
    }
}