public class bestOffer {

    public bestOffer(double offer, int pid) {
        this.offer = offer;
        this.pid = pid;
    }

    public double getOffer() {
        return offer;
    }

    public void setOffer(double offer) {
        this.offer = offer;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    private double offer;
    private int pid;

    @Override
    public String toString() {
        return "bestOffer is {" +
                "offer=" + offer +
                ", pid=" + pid +
                '}';
    }
}
