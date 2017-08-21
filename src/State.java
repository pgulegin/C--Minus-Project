import java.util.*;

public class State
{
    public int stateNumber = 0;
    public int comesFrom = 0;
    public Set<String> under = new HashSet<String>();
    public int goesTo = 0;

    public List<Production> startingList = new ArrayList<Production>();
    public List<Production> spawnList = new ArrayList<Production>();
    public List<Production> masterList = new ArrayList<Production>();

    // constructor for the very first state created
    public State(Production production, Grammar grammar)
    {
        stateNumber = 0;
        comesFrom = -1;
        under.add("");
        goesTo = -1;
        startingList = insertBeginningDot(production);
        spawnList = findSpawn(startingList, grammar);
        masterList = findMasterList(startingList, spawnList);
    }

    public State(int from, List<Production> nextState, SLRState slrState, Grammar grammar)
    {
        startingList = moveAllDotsRight(nextState);
        spawnList = findSpawn(startingList, grammar);
        masterList = findMasterList(startingList, spawnList);

        if (stateExists(masterList, slrState))
        {
            stateNumber = -2;
            comesFrom = from;
            under = findCurrentProduction(nextState);
            goesTo = findMatchingState(masterList, slrState);

            slrState.duplicateStates.put(slrState.duplicateStates.size(), this);
        }
        else
        {
            stateNumber = slrState.states.size();
            comesFrom = from;
            under = findCurrentProduction(nextState);
            goesTo = slrState.states.size();

            slrState.states.put(slrState.states.size(), this);
        }
    }

    public int findMatchingState(List<Production> masterList, SLRState slrState)
    {
        int returnNumber = -1;

        for (int i = 0; i < slrState.states.entrySet().size(); i++)
        {
            if (equalProductions(masterList, slrState.states.get(i).masterList))
            {
                return slrState.states.get(i).stateNumber;
            }
//			System.out.println("MASTERLIST: ");
//			printproductionlist(masterList);
//			System.out.println("COMPARLIST: ");
//			printproductionlist(slrState.states.get(i).masterList);
//			System.out.println();
//			System.out.println();
//			System.out.println();
        }

        return returnNumber;
    }

    public List<Production> moveAllDotsRight(List<Production> list)
    {
        List<Production> returnList = new ArrayList<Production>();

        for (Production production : list)
        {
            for (int i = 0; i < production.right.size()-1; i++)
            {
                if (production.right.get(i).equals(Grammar.DOT))
                {
                    Collections.swap(production.right, i, i+1);
                    returnList.add(new Production(production));
                    Collections.swap(production.right, i, i+1);
                }
            }
        }

        return returnList;
    }

    public boolean stateExists(List<Production> nextState, SLRState slrState)
    {
        for (int i = 0; i < slrState.states.entrySet().size(); i++)
        {
            if (equalProductions(nextState, slrState.states.get(i).masterList))
            {
                return true;
            }
        }
        return false;
    }

    public boolean equalProductions(List<Production> nextState, List<Production> masterList)
    {
        if (nextState.size() != masterList.size())
        {
            return false;
        }
        for (int i = 0; i < nextState.size(); i++)
        {
            if (!nextState.get(i).equals(masterList.get(i)))
            {
                return false;
            }
        }
        return true;
    }

    public List<Production> findMasterList(List<Production> startingList2, List<Production> spawnList2)
    {
        List<Production> returnList = new ArrayList<Production>();

        for (Production production : startingList)
        {
            returnList.add(new Production(production));
        }
        for (Production production : spawnList)
        {
            returnList.add(new Production(production));
        }

        return returnList;
    }

    public List<Production> findSpawn(List<Production> inputList, Grammar grammar)
    {
        List<Production> returnList = new ArrayList<Production>();

        Set<String> currentProductionLefts = new HashSet<String>();
        currentProductionLefts.addAll(findCurrentProduction(inputList));
        findProductionLefts(currentProductionLefts, grammar);
        returnList.addAll(insetBeginningDot(findProductions(currentProductionLefts, grammar)));

        return returnList;
    }
    public void findProductionLefts(Set<String> currentProductionLefts, Grammar grammar)
    {
        Set<String> cleanSet = new HashSet<String>();
        for (String clean : currentProductionLefts)
        {
            if (grammar.terminals.contains(clean))
            {
                cleanSet.add(clean);
            }
        }
        currentProductionLefts.removeAll(cleanSet);




        boolean hasListChanged = false;

        if (currentProductionLefts.isEmpty())
        {
            hasListChanged = false;
        }

        do
        {
            hasListChanged = false;
            for (Production production : grammar.augmentedProductions)
            {
                if (production.right.isEmpty())
                {
                    continue;
                }
                if (currentProductionLefts.contains(production.left) && grammar.nonterminals.contains(production.right.get(0)))
                {
                    if (currentProductionLefts.add(production.right.get(0)))
                    {
                        hasListChanged = true;
                    }
                }
            }
        } while (hasListChanged);
    }
    public List<Production> findProductions(Set<String> currentProductionLefts, Grammar grammar)
    {
        List<Production> returnList = new ArrayList<Production>();

        for (Production production : grammar.augmentedProductions)
        {
            if (currentProductionLefts.contains(production.left))
            {
                returnList.add(new Production(production));
            }
        }

        return returnList;
    }
    public Set<String> findCurrentProduction(List<Production> inputList)
    {
        Set<String> returnSet = new HashSet<String>();

        for (Production production : inputList)
        {
            for (int i = 0; i < production.right.size(); i++)
            {
                if (Grammar.DOT.equals(production.right.get(i)) && i != production.right.size()-1)
                {
                    returnSet.add(production.right.get(i+1));
                }
            }
        }

        return returnSet;
    }
    public List<Production> insetBeginningDot(List<Production> productions)
    {
        List<Production> returnList = new ArrayList<Production>();

        for (Production production : productions)
        {
            Production tempProduction = new Production(production);
            tempProduction.right.add(0, Grammar.DOT);
            returnList.add(tempProduction);
        }

        return returnList;
    }
    public List<Production> insertBeginningDot(Production production)
    {
        List<Production> returnList = new ArrayList<Production>();

        Production tempProduction = new Production(production);
        tempProduction.right.add(0, Grammar.DOT);
        returnList.add(tempProduction);

        return returnList;
    }
    public void print()
    {
        System.out.println("----------------- " + "State " + stateNumber + " --------------------");
        for (Production production : startingList)
        {
            production.print();
        }
        for (Production production : spawnList)
        {
            production.print();
        }
        System.out.println();
    }

    public void printDebug()
    {
        System.out.println("---------- " + "State " + stateNumber + " ----------");
        System.out.println("ComesFrom: " + comesFrom);
        System.out.println("Under:     " + under);
        System.out.println("GoesTo:    " + goesTo);
        for (Production production : startingList)
        {
            production.print();
        }
        for (Production production : spawnList)
        {
            production.print();
        }
        System.out.println();
    }

    public int size()
    {
        return startingList.size() + spawnList.size();
    }

    public Production get(int index)
    {
        List<Production> tempList = new ArrayList<Production>();
        tempList.addAll(startingList);
        tempList.addAll(spawnList);
        return tempList.get(index);
    }
    // used for debug
    // prints out a production list
    public void printproductionlist(List<Production> tempList)
    {
        if (tempList == null || tempList.isEmpty())
        {
            System.out.println("Production list is empty!");
        }
        else
        {
            for (Production production : tempList)
            {
                System.out.println(production.left + production.right);
            }
        }
    }
}
