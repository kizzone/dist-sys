import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.zeromq.ZMQ;
import org.zeromq.ZContext;
import org.zeromq.SocketType;
import org.zeromq.ZMQ.Socket;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class hwserver {


    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";


    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws Exception {

        System.out.println("Starting server");

        try {

            ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            exec.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {

                    bestOffer bO = bestOffer.getInstance();
                    System.out.println("\t" + ANSI_RED + bO.toString() + ANSI_RESET );

                    ZContext contextPublisher = new ZContext(); // for publisher subscribe
                    ZMQ.Socket publisher = contextPublisher.createSocket(SocketType.PUB);

                    publisher.bind("tcp://*:6666");
                    System.out.println("\t" + ANSI_BLUE + bO.toString() + ANSI_RESET );
                    System.out.println("Creating publisher end point");

                    Gson gson = new Gson();
                    JsonParser parser = new JsonParser();

                    // Send a response on publisher end point
                    System.out.println( "\t" + ANSI_YELLOW +"You want to close the auction?" + ANSI_RESET );
                    Scanner scan= new Scanner(System.in);
                    String closing = scan.nextLine();
                    System.out.println(closing);

                    if ( closing.equals("yes") || closing.equals("Yes")){
                        Messages auctionEnd = new Messages(bO.getPid(), 2, bO.getOffer());
                        System.out.println( "\t" + ANSI_YELLOW + "sending ending response on publisher endpoint" + auctionEnd.toString() + ANSI_RESET  );
                        //socket.send(gson.toJson(auctionEnd).getBytes(ZMQ.CHARSET), 0);
                        publisher.send(gson.toJson(auctionEnd).getBytes(ZMQ.CHARSET), 0);
                        System.exit(0);
                    }else{
                        Messages auctionEnd = new Messages(bO.getPid(), 1, bO.getOffer());
                        publisher.send(gson.toJson(auctionEnd).getBytes(ZMQ.CHARSET), 0);
                        //socket.send(gson.toJson(auctionEnd).getBytes(ZMQ.CHARSET), 0);
                    }

                    // TODO: togliere sto schifo e sitemanre in un altro modo
                    publisher.close();

                }
            }, 10, 20, TimeUnit.SECONDS );



            // Socket to talk to clients
            ZContext context = new ZContext();  //for request reply
            //TODO: forse conviene avere un altro pattern di comunicazione tra i client e il server: separare in 2 thread (quello che chiude l'asta, si attiva ogni tot secondi e fa scegliere al banditore se chiudere o meno l'asta), l'altro thread riceve tutte le offerte e aggiorna un oggetto condiviso che rappresenta la best offer
            ZMQ.Socket socket = context.createSocket(ZMQ.REP);
            System.out.println("Creating Reply end point");
            socket.bind("tcp://*:5556");

            bestOffer best = bestOffer.getInstance();

            while (!Thread.currentThread().isInterrupted()) {


                ZContext contextPublisher = new ZContext(); // for publisher subscribe
                ZMQ.Socket publisher = contextPublisher.createSocket(SocketType.PUB);
                publisher.bind("tcp://*:6667");

                System.out.println("dentro il while");
                // Block until a message is received
                byte[] response = socket.recv();
                System.out.println("Dopo il receive");

                // Print the message
                String responseString = new String(response);
                System.out.println(responseString);
                Gson gson = new Gson();
                JsonParser parser = new JsonParser();
                JsonObject object = (JsonObject) parser.parse(responseString);// response will be the json String
                Messages p = gson.fromJson(object, Messages.class);
                System.out.println("Offer received is " + p.toString());
                System.out.println("Actual best offer is " + best.toString());

                if ( p.getPrice() > best.getOffer() ){
                    best.setBestOffer(p.getPrice(),p.getPid());
                    System.out.println("New best offer is " + best.toString());
                }
                //TODO: avvisare i processi sulla socket subscribe che è giunta una nuova offerta
                //STA ROBA NON FUNZIONA, mi sa che dal lato di python non leggo niente
                System.out.println("sending data on publish endpoint " + best.toString());
                Messages msgcrociere = Messages.createMessagesFromBestOffer(best);
                publisher.send(gson.toJson(msgcrociere).getBytes(ZMQ.CHARSET), 0);
                System.out.println("sended data on publish ");
                publisher.close();

            }

        }catch (Exception e) {
            /* This is a generic Exception handler which means it can handle
             * all the exceptions. This will execute if the exception is not
             * handled by previous catch blocks.
             */
            System.out.println("Exception occurred");
        }
    }
}
