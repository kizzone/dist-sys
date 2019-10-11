import json
import os
import zmq


#   Server endpoint for sending REQUEST messages
zmq_request_endpoint = "tcp://localhost:5556"


#   Resetting REQUEST socket 
def reset_my_socket(zmq_req_socket):
  zmq_req_socket.close()
  context = zmq.Context.instance() 
  zmq_req_socket = context.socket(zmq.REQ)
  zmq_req_socket.setsockopt( zmq.RCVTIMEO, 500 ) # milliseconds
  zmq_req_socket.connect( zmq_request_endpoint )
  return zmq_req_socket


#   Thread function for receiving auctioner update on auction status
def auctioner_update():
    #creating subscriber endpoint
    context = zmq.Context.instance() 
    soc = context.socket(zmq.SUB)
    soc.connect("tcp://localhost:6666")
    soc.setsockopt_string(zmq.SUBSCRIBE,"")

    exit = False
    #  Get the reply.
    while not exit:
        msg = soc.recv()    
        msg = json.loads(msg) 
        message = Messages.returnObjfromDict(msg)
        print("\nResponse:  ", message)
        if message.typeoff == 2:
            if message.pid == os.getpid():
                print("congratulation you win the auction")
            else:
                print("You didn't win the auction")
            exit = True

    soc.close()
    os._exit(1)



#   Class messages for specifing Messages exchanged between client and server
class Messages:
    def __init__ (self, pid, typeoff, price):
        self.pid = pid
        self.typeoff = typeoff
        self.price = price

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__, sort_keys=True, indent=4)

    def __repr__(self):
        return "Pid: " + str(self.pid) + " Typeoff: " + str(self.typeoff) + "Price: " + str(self.price) + ""

    def returnObjfromDict(dictionary):
        return Messages(dictionary["pid"], dictionary["typeoff"],dictionary["price"])



#   Class messages for specifing Messages exchanged between client and server
class DutchMessage:
    def __init__ (self, pid, acceptOffer, price):
        self.pid = pid
        self.acceptOffer = acceptOffer
        self.price = price

    def toJSON(self):
        return json.dumps(self, default=lambda o: o.__dict__, sort_keys=True, indent=4)

    def __repr__(self):
        return "Pid: " + str(self.pid) + " acceptOffer: " + str(self.acceptOffer) + "Price: " + str(self.price) + ""

    def returnObjfromDict(dictionary):
        return Messages(dictionary["pid"], dictionary["acceptOffer"],dictionary["price"])

