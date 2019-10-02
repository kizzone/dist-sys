import zmq
import utilities 
import json
import threading
import os

#   Shared variable among threads
currentBestValue = 0



#   Thread function for reading from server the other offers  
def offers_from_others():

    global currentBestValue
    #creating subscriber endpoint
    context = zmq.Context.instance() 
    soc = context.socket(zmq.SUB)
    soc.connect("tcp://localhost:6667")
    soc.setsockopt_string(zmq.SUBSCRIBE,"")

    exit = False
    #  Get the reply.
    while not exit:
        msg = soc.recv()    
        msg = json.loads(msg) 
        message = utilities.Messages.returnObjfromDict(msg)
        if message.pid != os.getpid():
            print(" \n Pid " + str(message.pid) + "  proposes an offer of " + str(message.price))
        if currentBestValue < message.price: 
            currentBestValue = message.price
        


def main():

    global currentBestValue
    exit = False
    pid = os.getpid()
    
    print("Hi i'm ", pid)

    #  Socket to send REQUEST to server
    contextRequest = zmq.Context.instance() 
    socket = contextRequest.socket(zmq.REQ)
    socket.connect(utilities.zmq_request_endpoint)
    
    #   Creating receiving data threads
    thread = threading.Thread(target=utilities.auctioner_update)
    thread2 = threading.Thread(target=offers_from_others)
    thread.start()
    thread2.start()

    #   Main loop
    while not exit :
        
        offer = int (input("Enter an offer: ") )
        if offer < currentBestValue:
            print("Your offer must be higher then current one ")
            continue
        
        #Creating the message object with Messages.typeoff set to 1 (it means that the auction is not over)
        msg = utilities.Messages(pid,1,offer)
        #print("Sending " + str(msg.toJSON()) + " to auctioner" )

        #Sending message on socked and reset it
        socket.send_string(msg.toJSON())
        socket = utilities.reset_my_socket(socket)







if __name__ == "__main__":
    main()