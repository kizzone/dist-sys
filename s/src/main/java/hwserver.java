import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import org.zeromq.SocketType;
import org.zeromq.ZMQ.Socket;

import java.util.Scanner;



public class hwserver {

    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws Exception {
        bestOffer best = new bestOffer(0.0, 0);
        System.out.println("Starting server");

        try {

            ZContext contextPublisher = new ZContext(); // for publisher subscribe
            ZMQ.Socket publisher = contextPublisher.createSocket(SocketType.PUB);
            publisher.bind("tcp://*:6666");
            System.out.println("Creating publisher end point");
            Thread.sleep(10000);

            // Socket to talk to clients
            ZContext context = new ZContext();  //for request reply
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            System.out.println("Creating Reply end point");
            socket.bind("tcp://*:5556");

            while (!Thread.currentThread().isInterrupted()) {
                // Block until a message is received
                byte[] response = socket.recv();
                // Print the message
                String responseString = new String(response);
                System.out.println(responseString);
                Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                JsonObject object = (JsonObject) parser.parse(responseString);// response will be the json String
                Messages p = gson.fromJson(object, Messages.class);
                if ( p.getPrice() > best.getOffer() ){
                    best.setOffer(p.getPrice());
                    best.setPid(p.getPid());
                    System.out.println("New best offer is " + best.toString());
                }

                // Send a response on publisher end point
                System.out.println("You want to close the auction?");
                Scanner scan= new Scanner(System.in);
                String closing = scan.nextLine();
                System.out.println(closing);

                if ( closing.equals("yes") || closing.equals("Yes")){
                    Messages auctionEnd = new Messages(best.getPid(), 2, best.getOffer());
                    System.out.println("sending ending response on publisher endpoint" + auctionEnd.toString());
                    //socket.send(gson.toJson(auctionEnd).getBytes(ZMQ.CHARSET), 0);
                    publisher.send(gson.toJson(auctionEnd).getBytes(ZMQ.CHARSET), 0);
                    System.exit(0);
                }else{
                    Messages auctionEnd = new Messages(best.getPid(), 1, best.getOffer());
                    publisher.send(gson.toJson(auctionEnd).getBytes(ZMQ.CHARSET), 0);
                    //socket.send(gson.toJson(auctionEnd).getBytes(ZMQ.CHARSET), 0);
                }
            }
        }catch (Exception e) {
            /* This is a generic Exception handler which means it can handle
             * all the exceptions. This will execute if the exception is not
             * handled by previous catch blocks.
             */
            System.out.println("Exception occurred");
        }

        /*
        try ( ZContext contextPublisher = new ZContext() ) {
            ZMQ.Socket publisher = contextPublisher.createSocket(SocketType.PUB);
            publisher.bind("tcp://*:6666");
            System.out.println("ooooooo");
            Thread.sleep(10000);
            while (!Thread.currentThread().isInterrupted()) {
                System.out.println("sending data");
                Thread.sleep(5000);
                publisher.send("hello subscribers".getBytes(ZMQ.CHARSET), 0);
            }
        }

        */

    }
}
