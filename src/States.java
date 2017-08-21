import java.util.*;

public class States
{
    // keeps track of all of the states
    Map<Integer, List<Production>> states = new LinkedHashMap<Integer, List<Production>>();

    public States(Grammar grammar, Parser parser)
    {
        // grabs the starting production
        Production firstProduction = new Production(grammar.augmentedProductions.get(0));
//		Production firstProduction = new Production("L :: L # D");

        // adds a # to the beginning of the starting production
        List<Production> startingState = new ArrayList<Production>();
        startingState.add(firstProduction);

        saveNewState(insertDotAtBeginnings(startingState));

        printToConsole();
        System.out.println();

        // cycles through every state
        for (int i = 0; i < states.entrySet().size(); i++)
        {
            System.out.println("BEGIN 1st for");
            // expands on the state which was just passed in, if possible
            List<Production> currentState = new ArrayList<Production>();
            currentState.addAll(states.get(i));
            currentState.addAll(insertDotAtBeginnings(findAllProduction(states.get(i), grammar)));
            if (!stateExists(currentState))
            {
                saveCurrentState(currentState, i);
            }

            // lines which will be skipped due to matches
            Set<Integer> linesToSkip = new HashSet<Integer>();

            for (int j = 0; j < states.get(i).size(); j++)
            {
                System.out.println("BEGIN 2nd for");
                System.out.println(states.get(i).get(j).left + states.get(i).get(j).right);

                // skipping these productions because they were already passed in via matches
                if (linesToSkip.contains(j))
                {
                    System.out.println("Skipped due to already matched");
                    continue;
                }
                // skipping these production because they have already been completed
                else if (dotsAtEnd(Arrays.asList(states.get(i).get(j))))
                {
                    System.out.println("Skipped due to # at end");
                    continue;
                }
                // executes if the dot is in front of a terminal or nonterminal
                else
                {
                    System.out.println("BEGIN else statement");
                    List<Production> nextState = new ArrayList<Production>();
                    nextState.add(new Production(states.get(i).get(j)));
                    nextState.addAll(findAllMatchingProductions(states.get(i).get(j), states.get(i).subList(j, states.get(i).size())));
                    linesToSkip.addAll(findAllMatchingLineNumbers(i, j));
                    nextState = moveAllDotsRight(nextState);

                    if (!stateExists(nextState))
                    {
                        saveNewState(nextState);
                        System.out.println("nextState:");
                        printproductionlist(nextState);
                        System.out.println("END else statement");
                    }
                }

                printToConsole();
            }
            System.out.println("End 1st for at index: " + i);

//			System.out.println((moveAllDotsRight(Arrays.asList(new Production("Fn :: Ts id ( # Ps ) Cs")))).get(0).left);
//			System.out.println((moveAllDotsRight(Arrays.asList(new Production("Fn :: Ts id ( # Ps ) Cs")))).get(0).right);

            //testing reasons
//			if (i == 10)
//			{
//
//				break;
//			}

            linesToSkip.clear();
        }
    }

    public boolean stateExists(List<Production> currentState)
    {
        System.out.println("BEGIN stateExists");
        for (int i = 0; i < states.entrySet().size(); i++)
        {

            if (equalProductions(currentState, states.get(i)))
            {
                System.out.println("returns TRUE");
                return true;
            }

        }
        System.out.println("End stateExists");
        System.out.println("returns FALSE");
        return false;
    }

    public boolean equalProductions(List<Production> currentState, List<Production> list)
    {
        System.out.println("BEGIN equalProductions");
        System.out.println("========== Elements Passed IN =============");
        System.out.println("currentState:");
        printproductionlist(currentState);
        System.out.println("list:");
        printproductionlist(list);
        System.out.println("===========================================");

        if (currentState.size() != list.size())
        {
            System.out.println("returns FALSE1");
            return false;
        }

        for (int i = 0; i < currentState.size(); i++)
        {
            if (!currentState.get(i).left.contains(list.get(i).left))
            {
                System.out.println("returns FALSE2");
                return false;
            }
            else if (currentState.get(i).right.size() != list.get(i).right.size())
            {
                System.out.println("returns FALSE3");
                return false;
            }
            else
            {
                for (int j = 0; j < currentState.get(i).right.size(); j++)
                {
                    if (!currentState.get(i).right.get(j).contains(list.get(i).right.get(j)))
                    {
                        System.out.println("returns FALSE4");
                        return false;
                    }
                }
            }
        }
        System.out.println("END equalProductions");
        System.out.println("returns TRUE");
        return true;
    }

    private List<Production> moveAllDotsRight(List<Production> list)
    {
        List<Production> tempList = new ArrayList<Production>();

        for (Production production : list)
        {
            for (int i = 0; i < production.right.size()-1; i++)
            {
                if (production.right.get(i).equals(Grammar.DOT))
                {
                    Collections.swap(production.right, i, i+1);
                    tempList.add(new Production(production));
                    Collections.swap(production.right, i, i+1);
                }
            }
        }

        return tempList;
    }
    public List<Integer> findAllMatchingLineNumbers(int stateKey, int productionLine)
    {
        List<Integer> tempList = new ArrayList<Integer>();

        for (int j = productionLine+1; j < states.get(stateKey).size(); j++)
        {
            if (findCurrentProduction(Arrays.asList(states.get(stateKey).get(productionLine))).equals(findCurrentProduction(Arrays.asList(states.get(stateKey).get(j)))))
            {
                tempList.add(j);
            }
        }

        return tempList;
    }
    private List<Production> findAllMatchingProductions(Production mainProduction, List<Production> list)
    {
        System.out.println("BEGINNING findAllMatchingProductions");
        System.out.println("========== Elements Passed IN =============");
        System.out.println("mainProduction");
        System.out.println(mainProduction.left + mainProduction.right);
        System.out.println("list");
        printproductionlist(list);
        System.out.println("===========================================");
        List<Production> tempList = new ArrayList<Production>();

        for (Production production : list)
        {
            for (int i = 0; i < production.right.size()-1; i++)
            {
                if (mainProduction.equals(production))
                {
                    continue;
                }
                if (production.right.get(i).equals(Grammar.DOT) && findCurrentProduction(Arrays.asList(mainProduction)).contains(production.right.get(i+1)))
                {
                    tempList.add(new Production (production));
                }
            }
        }
        System.out.println("========== Elements Passed OUT ============");
        System.out.println("tempList");
        printproductionlist(tempList);
        System.out.println("===========================================");
        System.out.println("END findAllMatchingProductions");
        return tempList;
    }







    public String findCurrentProduction(Production mainProduction)
    {
        // TODO Auto-generated method stub
        return null;
    }

    // checks to see if # is at the end of every production of the list passed in
    public boolean dotsAtEnd(List<Production> list)
    {
        for (Production production : list)
        {
            for (int i = 0; i < production.right.size(); i++)
            {
                if (production.right.get(i).equals(Grammar.DOT) && i != production.right.size()-1)
                {
                    return false;
                }
            }
        }
        return true;
    }

    // returns all the productions found with the matching elements in the list
    public List<Production> findAllProduction(List<Production> list, Grammar grammar)
    {
        System.out.println("BEGIN findAllProduction");
        System.out.println("========== Elements Passed IN =============");
        System.out.println("list: ");
        printproductionlist(list);
        System.out.println("===========================================");

        Set<String> currentLefts = new HashSet<String>();
        currentLefts.addAll(findCurrentProduction(list));


        // this is where my problem is because of the unstructed list status of the grammar // note to self
        for (Production augmentedProduction : grammar.augmentedProductions)
        {
            if (currentLefts.contains(augmentedProduction.left))
            {
                currentLefts.add(augmentedProduction.right.get(0));
            }
        }

        List<Production> tempList = new ArrayList<Production>();

        for (Production augmentedProduction : grammar.augmentedProductions)
        {
            if (currentLefts.contains(augmentedProduction.left))
            {
                tempList.add(augmentedProduction);
            }
        }

        System.out.println("========== Elements Passed OUT ============");
        System.out.println("tempList: ");
        printproductionlist(tempList);
        System.out.println("===========================================");
        System.out.println("END findAllProduction");

        return tempList;
    }

    // finds the next element after the #
    public Set<String> findCurrentProduction(List<Production> list)
    {
        Set<String> tempList = new HashSet<String>();

        for (Production production : list)
        {
            for (int i = 0; i < production.right.size(); i++)
            {
                if (Grammar.DOT.equals(production.right.get(i)) && i != production.right.size()-1)
                {
                    tempList.add(production.right.get(i+1));
                }
            }
        }
        return tempList;
    }

    // adds a # at the beginning of any production lists passed in
    public List<Production> insertDotAtBeginnings(List<Production> startingState)
    {
        List<Production> tempList = new ArrayList<Production>();

        for (Production production : startingState)
        {
            Production tempProduction = new Production(production);
            tempProduction.right.add(0, Grammar.DOT);
            tempList.add(tempProduction);
        }

        return tempList;
    }

    // saves the passed in state into global states variable
    public void saveCurrentState(List<Production> productions, int stateNumber)
    {
        List<Production> productionsToSave = new ArrayList<Production>();

        for (Production temp : productions)
        {
            productionsToSave.add(new Production(temp));
        }

        states.put(stateNumber, productionsToSave);
    }

    // saves the passed in state into global states variable
    public void saveNewState(List<Production> productions)
    {
        List<Production> productionsToSave = new ArrayList<Production>();

        for (Production temp : productions)
        {
            productionsToSave.add(new Production(temp));
        }

        states.put(states.size(), productionsToSave);
    }

    // prints the current status of the states map to console
    public void printToConsole()
    {
        System.out.println("========== STATE SET ==========");

        for (int key : states.keySet())
        {
            System.out.println("---------- " + "State " + key + " ----------");
            for (Production values : states.get(key))
            {
                System.out.print(values.left + " :: ");
                for (String value : values.right)
                {
                    System.out.print(value + " ");
                }
                System.out.println();
            }
        }
    }

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
