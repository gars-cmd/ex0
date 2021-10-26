package ex0;

import java.util.Comparator;

public class Duo {
    public static void main(String[] args) {

    }
private CallForElevator call;
private final int allocated;

    public Duo(CallForElevator c , int allocated){
        this.call = c;
        this.allocated = allocated;
    }
public CallForElevator getCall(){
        return this.call;
}
public int getAllocated(){
        return this.allocated;
}

public static class floorComparatorSrc implements Comparator<Duo>{
        public int compare (Duo d1 , Duo d2){
            if (d1.getCall().getSrc()==d2.getCall().getSrc())return 0;
            else if (d1.getCall().getSrc()>d2.getCall().getSrc()) return 1;
            else return -1;
        }
}
public static class floorComparatorDest implements Comparator<Duo>{
        public int compare (Duo d1 , Duo d2){
            if (d1.getCall().getSrc()==d2.getCall().getSrc())return 0;
            else if (d1.getCall().getSrc()>d2.getCall().getSrc()) return 1;
            else return -1;
                 }
}
}
