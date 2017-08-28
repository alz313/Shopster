package ua.in.out.shopster;


public class Purchase {

    private String mName;
    private String mDesc;


    public Purchase() {
        // Needed for Firebase
    }

    public Purchase(String name, String desc) {
        mName = name;
        mDesc = desc;
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

}
