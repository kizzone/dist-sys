public class bestOffer {

    private double offer;
    private int pid;
    private static bestOffer single_instance = null;



    private bestOffer() {}

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

    public static bestOffer getInstance()
    {
        if (single_instance == null)
            single_instance = new bestOffer();

        return single_instance;
    }


    public synchronized void setBestOffer(double off, int pid){
        this.offer = off;
        this.pid = pid;
    }

    public bestOffer getBestOffer(){
        return this;
    }

    @Override
    public String toString() {
        return "bestOffer is {" +
                "offer=" + offer +
                ", pid=" + pid +
                '}';
    }
}
