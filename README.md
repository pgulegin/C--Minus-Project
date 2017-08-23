# C- Minus Project

This was a project for my Compilers class at CSULA. 
It mirrors the C- Minus Project and incorporates the grammar rules established from the project.

## Table of Contents
[Key Features](#key-features)
[Installation](#installation)
[Functionality](#functionality)
   [Input](#input)
   [Output](#output)
   [Writing To File](#writing-to-file)
[Support](#support)

## Key Features

- Takes in a grammar, but must have a specific format (detailed below)
- Handles epsilons
- Option to output to console or file
- Outputs:
   - Terminals
   - Non-Terminals
   - First Set
   - Follow Set
   - State Set

## Installation

## Functionality

### Input

The input for the Grammar must be formatted as such, where the rule can be separated by '::' or '->':
```
A :: S A S
A :: A S A
A :: b
A -> a
S -> A S A
S -> empty
```

Within the actual code itself, there are already 8 available test Grammars. You can either copy the instantiation line to create a 9th Grammar and include the appropraite file in the 'GrammarFiles' folder, or simply edit an existing one.
```Java
// select which grammar you'd like to run here
// Grammar grammar = new Grammar("GrammarFiles/GrammarList1.txt");
// Grammar grammar = new Grammar("GrammarFiles/GrammarList2.txt");
.....
Grammar grammar = new Grammar("GrammarFiles/GrammarList8.txt");
```

### Output

For the output, in the state generation section, commands G, S and R stand for goto, state and reduce, respectfully.
The output will print out the following information in this format: 
```
========== TERMINALS =============
a
b
========== NONTERMINALS ==========
A
S
========== PRODUCTION ============
A :: S A S
A :: A S A
A :: b
A :: a
S :: A S A
S :: empty
========== AUGMENTED PRODUCTION ==========
A' :: A
A :: S A S
A :: A S A
A :: b
A :: a
S :: A S A
S ::
========== FIRST SET  ==========
A :: a b empty
S :: a b empty
========== FOLLOW SET ==========
A :: a b $
S :: a b $
========== STATE SET ==========
----------------- State 0 --------------------
A' :: # A
A :: # S A S
A :: # A S A
A :: # b
A :: # a
S :: # A S A
S :: #

----------------- State 1 --------------------
A' :: A #
A :: A # S A
S :: A # S A
S :: #

.....
.....
.....
.....
.....

----------------- State 9 --------------------
A :: S A S #
A :: A S # A
S :: # A S A
S :: #

---------- State 0 ----------
Symbol  A
Command G
Number  1

Symbol  $
Command R
Number  6

---------- State 1 ----------
Symbol  S
Command G
Number  5

Symbol  $
Command accept

---------- State 2 ----------
Symbol  A
Command G
Number  7

Symbol  S
Command G
Number  2

.....
.....
.....
.....
.....

---------- State 9 ----------
Symbol  A
Command G
Number  8

Symbol  $
Command R
Number  6
```

### Writing to File

Be default, these lines of code will print everything to the console: 
```
// prints first and follow to console
parser.printFirstToConsole();
parser.printFollowToConsole();

// prints all states to console
slrstate.printToConsole();

// prints slr table to console
slrtable.printToConsole();
```

However, if you uncomment these lines, it will also print to file:
```
// prints first and follow to file
// parser.printFirstToFile();
// parser.printFollowToFile();

// prints all states to file
// slrstate.printToFile();

// prints slr table to file
// slrtable.printToFile();
```

## Support

More comments and efficiency updates.

But, if it breaks, you can keep both pieces.
