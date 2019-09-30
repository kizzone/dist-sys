public class Messages {

    private double price;
    private Integer typeoff;
    private Integer pid;

    public Messages(Integer pid , Integer typeoff, double price) {
        this.price = price;
        this.typeoff = typeoff;
        this.pid = pid;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Integer getTypeoff() {
        return typeoff;
    }

    public void setTypeoff(Integer typeoff) {
        this.typeoff = typeoff;
    }


    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }


    public static Messages createMessagesFromBestOffer(bestOffer best){
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
