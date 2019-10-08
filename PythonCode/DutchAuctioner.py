import zmq
import time
import sys
import json
import os
from utilities import Messages, reset_my_socket, DutchMessage
import threading


#   Thread function for sending closing messages to clints
def closing_auction_management():

    #creating a reply socket for receving accepted message from a client
    context = zmq.Context.instance() 
    soc = context.socket(zmq.REP)
    soc.bind("tcp://127.0.0.1:9998")
    
    #creating  a publisher socket for sending closing message to all the other clients
    context2    = zmq.Context()
    publisher = context2.socket(zmq.PUB)
    publisher.bind("tcp://127.0.0.1:5583")

    exit = False
    #  Get the reply.
    while not exit:

        msg = soc.recv()    
        # parsing the message
        msg = json.loads(msg) 
        message = DutchMessage.returnObjfromDict(msg)
        print(" \n Pid " + str(message.pid) + "  accepted the offer at price of " + str(message.price))
        
        #notify the winning client
        soc.send_string("Congratulation you win the auction")

        #send closing message to all the other clients        
        publisher.send_string("Auciton closed")

        os._exit(1)
        break
        
        
def main():
    

    print("Starting server")
    olderOffer = sys.maxsize

    # Prepare our context and publisher
    context    = zmq.Context()
    publisher = context.socket(zmq.PUB)
    publisher.bind("tcp://127.0.0.1:5563")

    #starting the 3d
    thread = threading.Thread(target=closing_auction_management,)
    thread.daemon = True
    thread.start()

    while True:

        #slot time for the auctioner 
        time.sleep(10)

        newOffer =   int(input("Insert a new price\n0 for exit:    "))
        if newOffer == 0:
            #notify the client that the actioner aborted di auction
            publisher.send_string("aborted")
            publisher.close()
            context.term()
            sys.exit()
        elif (newOffer >=  olderOffer):
            print("Insert a lower price! \nWainting for a new time slot")
            continue

        #creating and sending the massage
        msg = Messages(os.getpid(),1,newOffer)
        publisher.send_string(msg.toJSON()) 
        olderOffer = newOffer

    publisher.close()
    context.term()

if __name__ == "__main__":
    main()
