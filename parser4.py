import sys
from Grammarskel import *

if __name__ == "__main__":
    filename = raw_input("Grammar file name: ").strip()
    g = Grammar()
    g.readgrammar(filename)
    print "----- GRAMMER -----"
    sys.stdout.write(g.toString())

print "------ INPUT --------"

rawInput = raw_input('')
input = rawInput.split()
input.append('$$')


start = g.startsymbol   #Start symbol
stack = ['$$', start]         #Stack is initialized with startsymbol <E>
print "   ",start
answer = []
done = True


i = 0
while (i<len(input) and len(stack)>0):        
    
    currentStack = stack.pop() ##Start symbol    
    
    if (currentStack == input[i]):
        i = i+1
       
        answer.append(currentStack)
        
    else: 
        prediction = g.predict(currentStack, input[i])  ###Symbols to put in stack
        
        if (prediction == None):
            print "ERROR"
            done = False 
            break        
        
        for symb in reversed(prediction):
             stack.append(symb)       ###stack with prediction appended
        if (len(stack)>1 and len(answer)>0):
            print "    ", answer, stack[1:]
        elif (len(stack)>1):
            print "    ", stack[1:]
      
if done == True:        
    answer.pop()
    print "    ",answer
    print "Successfully Parsed!"


