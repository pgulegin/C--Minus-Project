import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.Map.Entry;


public class SLRTable
{
    Map<Integer, ArrayList<Action>> table = new HashMap<Integer, ArrayList<Action>>();

    public SLRTable(Grammar grammar, Parser parser, SLRState slrstate)
    {
        // finds the shifts and gotos in the main states
        for (State state : slrstate.states.values())
        {
            // eliminates the starting variable
            if (state.goesTo != -1)
            {
                new Action(state, this, grammar);
            }

        }
        // finds the shifts and gotos in the main states
        for (State state : slrstate.duplicateStates.values())
        {
            new Action(state, this, grammar);
        }
        // finds the reductions by going through the main states
        for (State state : slrstate.states.values())
        {
            for (Production production : state.masterList)
            {
                if (production.isReduced())
                {
                    Production tempProduction = new Production(production);
                    tempProduction.removeDot();

                    if (parser.follow.get(tempProduction.left) != null)
                    {
                        new Action(parser.follow.get(tempProduction.left), "R", findProduction(tempProduction, grammar), state.stateNumber, this);
                    }
                    else
                    {
                        Set<String> tempSetString = new HashSet<String>();
                        tempSetString.add("$");
                        new Action(tempSetString, "accept", -404, state.stateNumber, this);
                    }
                }
            }
        }

//		organize(table, grammar);
    }

    public int findProduction(Production tempProduction, Grammar grammar)
    {
        for (int i = 0; i < grammar.productions.size(); i++)
        {
            if (grammar.productions.get(i).equals(tempProduction))
            {
                return i+1;
            }
        }
        return -1;
    }

    public void printToConsole()
    {
        for (Entry<Integer, ArrayList<Action>> entry : table.entrySet())
        {
            System.out.println("---------- " + "State " + entry.getKey() + " ----------");
            for  (Action action : entry.getValue())
            {
                action.print();
            }
        }
    }

    public void printToFile() throws FileNotFoundException
    {
        // sets Sytem.out to file
        System.setOut(new PrintStream(new FileOutputStream("SLRTable.txt")));

        for (Entry<Integer, ArrayList<Action>> entry : table.entrySet())
        {
            System.out.println("---------- " + "State " + entry.getKey() + " ----------");
            for  (Action action : entry.getValue())
            {
                action.print();
            }
        }

        // sets System.out to console
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
    }
}
