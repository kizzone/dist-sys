import zmq
from utilities import Messages
import os
import json

def returnObjfromDict(dictionary):
    return Messages(dictionary["pid"], dictionary["typeoff"],dictionary["price"])



#creating subscriber endpoint
context = zmq.Context.instance() 
soc = context.socket(zmq.SUB)
soc.connect("tcp://localhost:6666")
soc.setsockopt_string(zmq.SUBSCRIBE,"")

#  Socket to send REQUEST to server
print("Connecting to response server…")
socket = context.socket(zmq.REQ)
socket.connect("tcp://localhost:5556")


exit = False
pid = os.getpid()

while not exit :
    print("Sending request a message …")
    offer = int (input("Enter an offer: ") )
    print(offer)


    msg = Messages(pid,1,offer)
    print("Sending …",msg.toJSON())
    socket.send_string(msg.toJSON())

    #  Get the reply.
    #msg = soc.recv_multipart()    
    msg = soc.recv()    
    print("Received reply")

    #message = socket.recv()
    #print("Received reply", message)

    msg = json.loads(msg) 
    message = returnObjfromDict(msg)
    print("Response", message)

    
    if message.typeoff == 2:
        if message.pid == pid:
            print("congratulation you win the auction")
        else:
            print("You didn't win the auction")
        
        exit = True
    
    """
    else:
        exitcode = int (input("Exit (0-1) ?: ") )
        if exitcode == 1:
            exit = True
    """