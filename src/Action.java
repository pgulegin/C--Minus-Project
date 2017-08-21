import java.util.ArrayList;
import java.util.Set;

public class Action {

    // default values for Action object
    public String symbol = "default";
    public String command = "default";
    public int number = -404;

    // constructor to create an Action from another Action
    // necessary when duplicate, non-associated copy is needed
    public Action(Action action) {
        symbol = action.symbol;
        command = action.command;
        number = action.number;
    }

    // constructor to create Action from State, SLRTable and Grammar
    public Action(State state, SLRTable slrTable, Grammar grammar) {
        symbol = state.under.toString();
        symbol = symbol.substring(1, symbol.length()-1);
        number = state.goesTo;

        if (grammar.nonterminals.contains(symbol)) {
            command = "G";
        }
        else {
            command = "S";
        }

        save(state.comesFrom, this, slrTable);
    }

    // constructor for new Action to add to the SLRTable
    public Action(Set<String> follow, String inputCommand, int inputNumber, int stateNumber, SLRTable table) {
        for (String inputSymbol : follow) {
            Action tempAction = new Action(inputSymbol, inputCommand, inputNumber);
            save(stateNumber, tempAction, table);
        }
    }

    // main constructor to create an Action
    public Action(String inputSymbol, String inputCommand, int inputNumber) {
        symbol = inputSymbol;
        command = inputCommand;
        number = inputNumber;
    }

    // saves the Action to the SLRTable
    public void save(int comesFrom, Action newAction, SLRTable slrTable) {
        ArrayList<Action> tempList = new ArrayList<Action>();

        if (slrTable.table.containsKey(comesFrom)) {
            for (Action action : slrTable.table.get(comesFrom)) {
                tempList.add(new Action(action));
            }
            tempList.add(new Action(newAction));

            slrTable.table.put(comesFrom, tempList);
        }
        else {
            tempList.add(new Action(newAction));
            slrTable.table.put(comesFrom, tempList);
        }
    }

    // prints Action to console
    public void print() {
        System.out.println("Symbol  " + symbol);
        System.out.println("Command " + command);

        if (number != -404) {
            System.out.println("Number  " + number);
        }
        System.out.println();
    }
}
