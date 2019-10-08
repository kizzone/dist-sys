public class DutchAcceptMessage {

    private boolean acceptOffer;
    private int pid;
    private int price;

    public boolean isAcceptOffer() {
        return acceptOffer;
    }

    public void setAcceptOffer(boolean acceptOffer) {
        this.acceptOffer = acceptOffer;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public DutchAcceptMessage(boolean acceptOffer, int pid, int price) {
        this.acceptOffer = acceptOffer;
        this.pid = pid;
        this.price = price;
    }

    public DutchAcceptMessage(int pid) {
        this.pid = pid;
    }


    @Override
    public String toString() {
        return "DutchAcceptMessage{" +
                "acceptOffer=" + acceptOffer +
                ", pid=" + pid +
                ", price=" + price +
                '}';
    }
}
