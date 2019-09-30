import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Auctioner implements Runnable {

    @Override
    public void run() {

        bestOffer bO = bestOffer.getInstance();
        ZContext contextPublisher = new ZContext(); // for publisher subscribe
        ZMQ.Socket publisher = contextPublisher.createSocket(SocketType.PUB);
        publisher.bind("tcp://*:6666");
        System.out.println("Creating publisher end point");

        Gson gson = new Gson();
        JsonParser parser = new JsonParser();

        // Send a response on publisher end point
        System.out.println("You want to close the auction?");
        Scanner scan= new Scanner(System.in);
        String closing = scan.nextLine();
        System.out.println(closing);

        if ( closing.equals("yes") || closing.equals("Yes")){
            Messages auctionEnd = new Messages(bO.getPid(), 2, bO.getOffer());
            System.out.println("sending ending response on publisher endpoint" + auctionEnd.toString());
            //socket.send(gson.toJson(auctionEnd).getBytes(ZMQ.CHARSET), 0);
            publisher.send(gson.toJson(auctionEnd).getBytes(ZMQ.CHARSET), 0);
            System.exit(0);
        }else{
            Messages auctionEnd = new Messages(bO.getPid(), 1, bO.getOffer());
            publisher.send(gson.toJson(auctionEnd).getBytes(ZMQ.CHARSET), 0);
            //socket.send(gson.toJson(auctionEnd).getBytes(ZMQ.CHARSET), 0);
        }




    }
}
