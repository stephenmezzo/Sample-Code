from sets import Set
import sys

#  The Rule class.  
#
#  It stores a simple CFG rule composed of a left hand side (lhs string) and a right hand side (rhs list of strs)

class Rule:

    #  Constructor
    def __init__(self, lhs, rhs, predictsyms):  
        self.lhs = lhs
        self.rhs = rhs
        self.predictsyms = predictsyms
        ##add predictsysms = 
    #  Accessor functions
    def getlhs(self):
        return self.lhs
    def getrhs(self):
        return self.rhs
    def getrhs_filtered(self):
        return [symb for symb in self.rhs if symb != '<epsilon>']
 

    #  Format the rule as a string, e.g.:  "S -> U op V"
    def toString(self):
        s = self.lhs + " -> " + " ".join(self.rhs) + "    { " + " ".join(self.predictsyms) + " } "
        return s

    def toStringMinusPredict(self): 

        s = self.lhs + " -> " + " ".join(self.rhs)
        return s

#  Grammar class
#
#  It stores a whole LL(1) grammar: rules, predict table, start symbol.
#
class Grammar:

    #  Constructor has no arguments
    def __init__(self):
        self.rules = {}          # rules[ruleid] = Rule()
        self.table = {}          # table[nonterm][term] = ruleid
        self.nonterms = Set()    # set of non-terminals
        self.terms    = Set()    # set of terminals
        self.startsymbol = None  # start symbol

    #  Read grammar from file.
    def readgrammar(self, filename):
        with open(filename, "r") as f:
            for line in f:
                tokens = line.split()
                ruleno = tokens[0]
                lhs = tokens[1]
                self.nonterms.add(lhs)
                if self.startsymbol == None:
                    self.startsymbol = lhs
                rhs = tokens[3:]   #rhs and predict set
                pos = rhs.index('{')
                predictsyms = rhs[pos+1:-1]  ###predict set
                
                rhs = rhs[:pos]
                rule = Rule(lhs,rhs, predictsyms)
                self.rules[ruleno] = rule
                for sym in predictsyms:
                    self.table[lhs+'@'+sym] = ruleno
                    self.terms.add(sym)
                 
           

            
        


    #  Look up one entry in the prediction table,
    #   returns the relevant Rule object if found (or None if not)
    #
    def predict(self, nonterm, term):
        key = nonterm+ '@' + term
        if key in self.table:
            ruleid = self.table[nonterm+'@'+term]
            rule=self.rules[ruleid]
            print rule.toStringMinusPredict()
            return rule.getrhs_filtered()
        return
        
    

    #  Format the list of grammar rules as a printable string, 
    #  one line per rule.  Also include start symbol.
    #
    def toString(self):

        s=""
        for ruleno, r in self.rules.items():
            s = s + ruleno + " " + r.toString() + "\n"      ###to string predict
        return s
        
        

    # Tests whether a symbol is a nonterminal
    #
    def isNonterm(self, symb):
        
        return symb in self.nonterms

    # Tests whether a symbol is a terminal
    #
    def isTerm(self, symb):

        return symb in self.terms

    # Accessor function to fetch the start symbol
    #
    def startsym(self):
        return self.startsymbol

#  This simple code runs when the module is loaded as main,
#    it prompts for a file name, reads the grammar file, and
#    prints it back out.
if __name__ == "__main__":
    filename = raw_input("Grammar file name: ").strip()
    g = Grammar()
    g.readgrammar(filename)
    sys.stdout.write(g.toString())


