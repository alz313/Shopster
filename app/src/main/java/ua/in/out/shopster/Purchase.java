package ua.in.out.shopster;


public class Purchase {

    private String mName;
    private Double mQty;
    private String mUnit;
    private Boolean mIsBought;


    public Purchase() {
        // Needed for Firebase
    }

    public Purchase(String name, Double qty, String unit, Boolean isBought) {
        mName = name;
        mQty = qty;
        mUnit = unit;
        mIsBought = isBought;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Double getQty() {
        return mQty;
    }

    public void setQty(Double qty) {
        mQty = qty;
    }

    public String getUnit() {
        return mUnit;
    }

    public void setUnit(String unit) {
        mUnit = unit;
    }

    public Boolean getBought() {
        return mIsBought;
    }

    public void setBought(Boolean bought) {
        mIsBought = bought;
    }

}
