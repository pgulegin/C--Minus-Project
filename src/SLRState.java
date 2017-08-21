import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;

public class SLRState
{
    // keeps track of all of the states
    Map<Integer, State> states = new HashMap<Integer, State>();
    // keeps track of all of duplicate states
    Map<Integer, State> duplicateStates = new HashMap<Integer, State>();

    public SLRState(Grammar grammar, Parser parser)
    {
        Production firstStateProduction = new Production(grammar.augmentedProductions.get(0));
        State firstState = new State(firstStateProduction, grammar);
        states.put(states.size(), firstState);

        for (int i = 0; i < states.entrySet().size(); i++)
        {
            Set<Integer> linesToSkip = new HashSet<Integer>();
            for (int j = 0; j < states.get(i).size(); j++)
            {
                if (linesToSkip.contains(j))
                {
                    continue;
                }
                else if (states.get(i).get(j).isReduced())
                {
                    continue;
                }
                else
                {
                    List<Production> nextState = new ArrayList<Production>();
                    nextState.add(new Production(states.get(i).get(j)));
                    linesToSkip.addAll(findNextState(nextState, states.get(i).masterList.subList(j, states.get(i).masterList.size()), states.get(i).masterList));
                    new State(i, nextState, this, grammar);
                }
            }
        }
    }

    public Set<Integer> findNextState(List<Production> nextState, List<Production> subList, List<Production> masterList)
    {
        Set<Integer> returnSet = new HashSet<Integer>();

        String currentLeft = findCurrentLeft(nextState);

        for (Production production : subList)
        {
            if (nextState.get(0).equals(production))
            {
                continue;
            }
            for (int i = 0; i < production.right.size()-1; i++)
            {
                if (production.right.get(i).equals(Grammar.DOT) && production.right.get(i+1).equals(currentLeft))
                {
                    nextState.add(new Production(production));
                }
            }
        }

        for (int i = 0; i < masterList.size(); i++)
        {
            if (masterList.get(i).isInside(nextState))
            {
                returnSet.add(i);
            }
        }

        return returnSet;
    }

    public String findCurrentLeft(List<Production> nextState)
    {
        String returnString = "";

        for (int i = 0; i < nextState.get(0).right.size(); i++)
        {
            String current = nextState.get(0).right.get(i);
            if (current.equals(Grammar.DOT))
            {
                returnString = nextState.get(0).right.get(i+1);
            }
        }

        return returnString;
    }
    public void printToFile() throws FileNotFoundException
    {
        // sets Sytem.out to file
        System.setOut(new PrintStream(new FileOutputStream("ItemList.txt")));

        for (int key : states.keySet())
        {
            states.get(key).print();
        }

        // sets System.out to console
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    }
    public void printToConsole()
    {
        System.out.println("========== STATE SET ==========");
        for (int key : states.keySet())
        {
            states.get(key).print();
        }
    }
    public void printDuplicatesToConsolde()
    {
        System.out.println("========== DUPLICATE STATE SET ==========");
        for (int key : duplicateStates.keySet())
        {
            duplicateStates.get(key).printDebug();
        }
    }
}