import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import org.zeromq.SocketType;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class for auction management
 * @author Domenico Santoro, Mirko Marasco
 *
 */

public class EnglishAuctioner {

    /**
     *  Code for colored print
     */

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_YELLOW = "\u001B[33m";


    @SuppressWarnings("deprecation")

    /**
     * Starting EnglishAuctioner
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        System.out.println("Starting server...");

        try {

            ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            /**
             * Schedule a Runnable object at fixed rate, this object creates the Publisher socket and takes caring of asking to the auctioner if he wants to close the auction, Prints in this thread are Yellow
             */
            exec.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    BestOffer bO = BestOffer.getInstance();

                    //creating zmq context and publisher type socket
                    ZContext contextPublisher = new ZContext(); // for publisher subscribe
                    ZMQ.Socket publisher = contextPublisher.createSocket(SocketType.PUB);
                    publisher.bind("tcp://*:6666");

                    //creating object for json parsing
                    Gson gson = new Gson();

                    // Asking for auction closuring
                    System.out.println( "\t" + ANSI_YELLOW +" Do you want to close the auction?" + ANSI_RESET );
                    Scanner scan= new Scanner(System.in);
                    String closing = scan.nextLine();

                    //close checking
                    if ( closing.equals("yes") || closing.equals("Yes")){
                        // sending a closing message ( typeoff:2 ) on publisher socket to all waiting clients
                        Messages auctionEnd = new Messages(bO.getPid(), 2, bO.getOffer());
                        //System.out.println( "\t" + ANSI_YELLOW + " Sending ending response on publisher endpoint" + auctionEnd.toString() + ANSI_RESET  );
                        publisher.send(gson.toJson(auctionEnd).getBytes(ZMQ.CHARSET), 0);
                        System.exit(0);
                    }else{
                        // sending a Non closing message to all waiting clients
                        Messages auctionEnd = new Messages(bO.getPid(), 1, bO.getOffer());
                        publisher.send(gson.toJson(auctionEnd).getBytes(ZMQ.CHARSET), 0);
                    }

                    publisher.close();

                }
            }, 10, 20, TimeUnit.SECONDS );



            // Creating Reply socket
            ZContext context = new ZContext();
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            socket.bind("tcp://*:5556");

            // getting best offer instance
            BestOffer best = BestOffer.getInstance();

            //main loop
            while (!Thread.currentThread().isInterrupted()) {

                // Creating a publisher socket for send the current best offer value to all partecipating clients, every new offer received
                ZContext contextPublisher = new ZContext(); // for publisher subscribe
                ZMQ.Socket publisher = contextPublisher.createSocket(SocketType.PUB);
                publisher.bind("tcp://*:6667");

                // Block until a message on REQUEST socket is received
                byte[] response = socket.recv();

                // converting and printing  the message
                String responseString = new String(response);

                Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                JsonObject object = (JsonObject) parser.parse(responseString);// response will be the json String
                Messages p = gson.fromJson(object, Messages.class);
                System.out.println("Current best offer is " + best.toString() + " \nOffer received is " + p.toString()  );

                if ( p.getPrice() > best.getOffer() ){
                    best.setBestOffer(p.getPrice(),p.getPid());
                    //System.out.println("Updating " + best.toString());
                    System.out.println("Sending current best offer to all client");
                    Messages msg = Messages.createMessagesFromBestOffer(best);
                    publisher.send(gson.toJson(msg).getBytes(ZMQ.CHARSET), 0);
                    publisher.close();
                }
            }
        }catch (Exception e) {

            /* This is a generic Exception handler which means it can handle
             * all the exceptions. This will execute if the exception is not
             * handled by previous catch blocks.
             */
            System.out.println("Exception occurred on auctioner starting");
        }
    }
}
