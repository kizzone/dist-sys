import zmq
from utilities import Messages
import os
import json
import threading
import logging
import time

zmq_endpoint = "tcp://localhost:5556"


def returnObjfromDict(dictionary):
    return Messages(dictionary["pid"], dictionary["typeoff"],dictionary["price"])

def reset_my_socket(zmq_req_socket):
  zmq_req_socket.close()
  context = zmq.Context.instance() 
  zmq_req_socket = context.socket(zmq.REQ)
  zmq_req_socket.setsockopt( zmq.RCVTIMEO, 500 ) # milliseconds
  zmq_req_socket.connect( zmq_endpoint )
  return zmq_req_socket


def subscriber_routine():
    print("\nThread start", os.getpid())
    #creating subscriber endpoint
    context = zmq.Context.instance() 
    soc = context.socket(zmq.SUB)
    soc.connect("tcp://localhost:6666")
    soc.setsockopt_string(zmq.SUBSCRIBE,"")

    exit = False
    #  Get the reply.
    while not exit:
        print(" \nThread waiting for update")
        msg = soc.recv()    
        print(" \nReceived reply")
        msg = json.loads(msg) 
        message = returnObjfromDict(msg)
        print(" \nResponse", message)
        if message.typeoff == 2:
            if message.pid == os.getpid():
                print("  congratulation you win the auction")
            else:
                print("  You didn't win the auction")
            exit = True

    soc.close()
    os._exit(1)



def subscriber_routine_2():
    print("\n Receiving Thread start", os.getpid())
    #creating subscriber endpoint
    context = zmq.Context.instance() 
    soc = context.socket(zmq.SUB)
    soc.connect("tcp://localhost:6667")
    soc.setsockopt_string(zmq.SUBSCRIBE,"")

    exit = False
    #  Get the reply.
    while not exit:
        print(" \nThread2  waiting for update")
        msg = soc.recv()    
        print(" \nReceived reply2")
        msg = json.loads(msg) 
        message = returnObjfromDict(msg)
        print(" \nResponse2", message)
        


def main():

    #  Socket to send REQUEST to server
    contextRequest = zmq.Context.instance() 
    print("Connecting to response server…")
    socket = contextRequest.socket(zmq.REQ)
    socket.connect(zmq_endpoint)
    exit = False
    pid = os.getpid()
    print("creating thread")
    thread = threading.Thread(target=subscriber_routine)
    thread2 = threading.Thread(target=subscriber_routine_2)
    thread.start()
    thread2.start()
    print("entering while loop of main…")
    while not exit :
        print("Sending request a message …")
        offer = int (input("Enter an offer: ") )
        print(offer)
        msg = Messages(pid,1,offer)
        print("Sending …",msg.toJSON())

        socket.send_string(msg.toJSON())
        socket = reset_my_socket(socket)

if __name__ == "__main__":
    main()