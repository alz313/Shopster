package ua.in.out.shopster;

public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);
    boolean onItemMoved(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
