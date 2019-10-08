import com.google.gson.Gson;
import com.google.gson.JsonParser;
import jdk.nashorn.internal.parser.JSONParser;
import org.zeromq.SocketType;
import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Socket;
import org.zeromq.ZContext;

import java.io.IOException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
 * This class implements the client of the dutch auctioner
 */

public class DutchBidder {


    /**
     * Method for getting  a Pid of the process
     * @return Pid
     */
    public static long getPID() {
        String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
        return Long.parseLong(processName.split("@")[0]);
    }

    public static void main(String[] args) throws Exception {

        Gson gson = new Gson();
        System.out.println("Starting Dutch client, pid: " + DutchBidder.getPID());

        //This thread received the message of closing auction
        Runnable runnable =   () -> {
            try (ZContext context = new ZContext()) {
                Socket subscriber = context.createSocket(SocketType.SUB);
                subscriber.connect("tcp://127.0.0.1:5583");
                subscriber.subscribe("".getBytes(ZMQ.CHARSET));
                String contents = subscriber.recvStr();
                System.out.println(contents);
                System.exit(0);
            }
        };
        //starting the 3d
        Thread thread = new Thread(runnable);
        thread.start();

        try (ZContext context = new ZContext()) {

            // Prepare our context and subscriber
            Socket subscriber = context.createSocket(SocketType.SUB);
            subscriber.connect("tcp://localhost:5563");
            subscriber.subscribe("".getBytes(ZMQ.CHARSET));


            while (!Thread.currentThread().isInterrupted()) {

                // Waiting for auctioner offer
                String contents = subscriber.recvStr();

                //check if the auctioner aborted the auction
                if ( contents.equals("aborted") ) {
                    System.out.println("Server aborted the auction");
                    System.exit(0);
                }

                //printing the offer
                System.out.println("EnglishAuctioner offer" + contents);

                //converting  contents into Message obj
                Messages msg =  gson.fromJson(contents,Messages.class);

                // Scanning user response
                System.out.println("Do you accept?");
                Scanner s = new Scanner(System.in);
                String accept = s.nextLine();

                // Sending client accept message to auctioner and waiting for response
                if (accept.equals("yes") || accept.equals("Yes") || accept.equals("y")) {
                    DutchAcceptMessage msgDutch = new DutchAcceptMessage(true, (int) DutchBidder.getPID(), (int) msg.getPrice());

                    try (ZContext context2 = new ZContext()) {
                        //  Socket to talk to server
                        Socket requester = context2.createSocket(SocketType.REQ);
                        requester.connect("tcp://127.0.0.1:9998");
                        requester.send(gson.toJson(msgDutch));
                        String ktm = requester.recvStr();
                        System.out.println(ktm);
                        System.exit(0);
                    }
                }
            }
        }
    }
}
