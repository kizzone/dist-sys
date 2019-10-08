/**
 * Incapsulating the best received offer from clients, this class is a Singleton
 * @author Domenico Santoro, Mirko Marasco
 *
 */

public class BestOffer {


    /**
     *  Price of the offer
     */
    private double offer;

    /**
     *  Pid of the bidder
     */
    private int pid;

    /**
     * instance for Singleton class
     */
    private static BestOffer single_instance = null;

    /**
     * Private constructor restricted to this class itself
     */
    private BestOffer() {}

    /**
     * Public constructor
     * @param pid
     * @param offer
     */
    public BestOffer(double offer, int pid) {
        this.offer = offer;
        this.pid = pid;
    }

    /**
     * Getter for offer field
     */
    public double getOffer() {
        return offer;
    }

    /**
     * Setter for offer field
     */
    public void setOffer(double offer) {
        this.offer = offer;
    }

    /**
     * Getter for pid field
     */
    public int getPid() {
        return pid;
    }

    /**
     * Setter for offer field
     */
    public void setPid(int pid) {
        this.pid = pid;
    }

    /**
     * Getting the instance of the class
     * @return the single instance of the class
     */
    public static BestOffer getInstance()
    {
        if (single_instance == null)
            single_instance = new BestOffer();

        return single_instance;
    }

    /**
     * Synchronized setter for object instance
     *
     */
    public synchronized void setBestOffer(double off, int pid){
        this.offer = off;
        this.pid = pid;
    }

    /**
     * Getter for object instance
     * @return object instance
     */
    public BestOffer getBestOffer(){
        return this;
    }

    @Override
    public String toString() {
        return "BestOffer is {" +
                "offer=" + offer +
                ", pid=" + pid +
                '}';
    }
}
