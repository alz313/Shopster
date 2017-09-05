package ua.in.out.shopster;


public class Purchase {

    private String mName;
    private String mDesc;
    private Double mQty;
    private Boolean mIsBought;


    public Purchase() {
        // Needed for Firebase
    }

    public Purchase(String name, String desc, Double qty, Boolean isBought) {
        mName = name;
        mDesc = desc;
        mQty = qty;
        mIsBought = isBought;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        mDesc = desc;
    }

    public Double getQty() {
        return mQty;
    }

    public void setQty(Double qty) {
        mQty = qty;
    }

    public Boolean getBought() {
        return mIsBought;
    }

    public void setBought(Boolean bought) {
        mIsBought = bought;
    }

}
