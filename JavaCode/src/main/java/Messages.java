/**
 * Incapsulating the message exchanged
 * @author Domenico Santoro, Mirko Marasco
 *
 */

public class Messages {


    /**
     *  Price proposed by the bidder
     */
    private double price;

    /**
     * Type of the offer: 1 = auction not closed
     *                    2 = auction closed
     */
    private Integer typeoff;

    /**
     *  Pid of the bidder
     */
    private Integer pid;

    /**
     * Public constructor
     * @param pid
     * @param typeoff
     * @param price
     */
    public Messages(Integer pid , Integer typeoff, double price) {
        this.price = price;
        this.typeoff = typeoff;
        this.pid = pid;
    }


    /**
     * Getter for price field
     */
    public double getPrice() {
        return price;
    }

    /**
     * Setter for price field
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Getter for typeoff field
     */
    public Integer getTypeoff() {
        return typeoff;
    }

    /**
     * Setter for typeoff field
     */
    public void setTypeoff(Integer typeoff) {
        this.typeoff = typeoff;
    }


    /**
     * Getter for pid field
     */
    public Integer getPid() {
        return pid;
    }


    /**
     * Setter for pid field
     */
    public void setPid(Integer pid) {
        this.pid = pid;
    }


    /**
     * Creating Message object from the BestOffer object
     */

    public static Messages createMessagesFromBestOffer(BestOffer best){
        Messages m = new Messages(best.getPid(),1,best.getOffer());
        return m;
    }

    @Override
    public String toString() {
        return "Messages {" +
                "price=" + price +
                ", typeoff=" + typeoff +
                ", pid=" + pid +
                '}';
    }
}
