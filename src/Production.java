import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// keeps track of every individual production from the GrammarList.txt
public class Production
{
    // variables of the Production object class
    public String left = new String();
    public List<String> right = new ArrayList<String>();

    // constructor for the Production object
    public Production (String left, List<String> right)
    {
        this.left = new String(left);	// keeps track of the nonterminal on the left
        this.right = new ArrayList<String>(right);	// keeps track of the rules on the right
    }

    // constructor for the Production object
    public Production(String left, String right)
    {
        this.left = new String(left);	// keeps track of the nonterminal on the left
        this.right = new ArrayList<String>(Arrays.asList(right));	// keeps track of the rules on the right
    }

    // constructor to create an object from a string read in thought the GrammarList.txt
    public Production(String line)
    {
        //splits the string into a string array
        ArrayList<String> aList = new ArrayList<String>(Arrays.asList(line.split(" ")));
        left = aList.get(0).toString();

        // splits up the leftover rules into separate strings
        for (int i = 2; i < aList.size(); i++)
        {
            right.add(aList.get(i).toString());
        }
    }

    public Production(Production temp)
    {
        this.left = new String(temp.left);	// keeps track of the nonterminal on the left
        this.right = new ArrayList<String>(temp.right);	// keeps track of the rules on the right
    }

    // prints out the production with appropriate formatting for debugging
    public void print()
    {
        System.out.print(left + " ::");
        for (String temp : right)
        {
            System.out.print(" " + temp);
        }
        System.out.println();
    }

    public boolean isReduced()
    {
        for (int i = 0; i < right.size(); i++)
        {
            if (right.get(i).equals(Grammar.DOT) && i != right.size()-1)
            {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Production production)
    {
        if (!left.equals(production.left))
        {
            return false;
        }

        boolean containsEmptyException = false;

        if (right.size() == 0 || production.right.size() == 0)
        {
            containsEmptyException = true;
        }

        if (containsEmptyException)
        {
            if ((right.isEmpty() || right.get(0).contains(Grammar.EPSILON)) &&
                    (production.right.isEmpty() || production.right.get(0).contains(Grammar.EPSILON))){
                return true;
            }
            else{
                return false;
            }
        }
        else
        {
            if (right.size() != production.right.size())
            {
                return false;
            }

            for (int i = 0; i < right.size(); i++)
            {
                if (!right.get(i).equals(production.right.get(i)))
                {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isInside(List<Production> nextState)
    {
        for (Production production : nextState)
        {
            if (this.equals(production))
            {
//				this.print();
//				System.out.println("EQUALS");
//				production.print();
//				System.out.println();
                return true;
            }
        }
        return false;
    }

    public void removeDot()
    {
        right.remove(Grammar.DOT);
//		for (int i = 0; i < right.size(); i++)
//		{
//			if (right.get(i).equals(Grammar.DOT))
//			{
//				right.remove
//			}
//		}
    }
}