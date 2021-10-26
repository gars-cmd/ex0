package ex0.algo;
import ex0.Building;
import ex0.CallForElevator;
import ex0.Duo;
import ex0.Elevator;
import java.util.*;


public class MyAlgo implements ElevatorAlgo {
    public static final int UP = 1, DOWN = -1;
    private ArrayList<Duo> bank = new ArrayList<>();
    private Building _building;
    private int[] ElevList;


    public MyAlgo(Building b) {
        _building = b;
        ElevList = new int[_building.numberOfElevetors()];
        for (int i = 0; i < _building.numberOfElevetors(); i++) {
            ElevList[i] = i;
        }
    }


    @Override
    public Building getBuilding() {
        return _building;
    }

    @Override
    public String algoName() {
        return "Ex0_OOP_My_Algo";
    }

    public int allocateAnElevator(CallForElevator c) {
        int ans = 0;
        if (ElevList.length == 1) {
            ans = 0;
        } else {
            if (bank.size() == 0) {
                ans = closerOne(ElevList, c);
                bank.add(new Duo(c, ans));
            } else {
                if (WhosResting() == null) {
                    if (isThereSameDirection(ElevList, c)) {
                        if (c.getType() == DOWN) {
                            ans = closerOne(WhosAbove(WhoTheSameDirection(ElevList, c), c), c);
                            bank.add(new Duo(c, ans));
                        } else {
                            ans = closerOne(WhosBellow(WhoTheSameDirection(ElevList, c), c), c);
                            bank.add(new Duo(c, ans));
                        }
                    } else {
                        ans = closerOne(ElevList, c);
                        bank.add(new Duo(c, ans));
                    }
                } else {
                    ans = closerOne(WhosResting(), c);
                    bank.add(new Duo(c, ans));
                }
            }
        }
        return ans;
    }

    @Override
    public void cmdElevator(int elev) {
        Elevator curr = _building.getElevetor(elev);
        if (curr.getState() == DOWN) {
            int next = nexStop(DOWN, elev);
            curr.stop(next);
        } else if (curr.getState() == Elevator.LEVEL) {
            for (int i = 0; i < bank.size(); i++) {
                if (bank.get(i).getCall().getState() == CallForElevator.DONE) ;
                bank.remove(i);
            }
            ///////// FIND THE NEXT THING TO DO FOR THE ELEVATOR AT LEVEL STATE ////////////
            int nextfloor = closestFloor(elev);
            curr.goTo(nextfloor);
        } else {
            int next = nexStop(UP, elev);
            curr.stop(next);
        }
    }


    private int closerOne(int[] list, CallForElevator c) {
        int minDist = Integer.MAX_VALUE;    //creating the maximal distance possible
        int choosen = 0;

        for (int i = 0; i < list.length; i++) { //we will check over all the elevators
            int distance = Math.abs(_building.getElevetor(list[i]).getState() - c.getSrc()); //distance = abs(distance between elevator and src call)
            if (distance < minDist) { // if there is a smaller distance replace it
                minDist = distance;
                choosen = list[i];
            }
        }
        return choosen;
    } //return the index of the closest elevator

    private boolean isThereSameDirection(int[] elevList, CallForElevator c) {
        int direction = c.getType();
        boolean check = false;
        for (int i = 0; i < _building.numberOfElevetors(); i++) { //let check over all the elevator if there is elevator in the same direction as the call
            if (_building.getElevetor(i).getState() == direction) check = true; //if we find it just make check true
        }
        return check;
    } //return if there are elevator in the same direction as the call

    private int[] WhosBellow(int[] list, CallForElevator c) {
        ArrayList<Integer> who = new ArrayList<>(1); //let create an arraylist of the wanted elevator
        for (int i = 0; i < _building.numberOfElevetors(); i++) {
            if (_building.getElevetor(i).getPos() < c.getSrc()) { // if we find an elevator bellow the src call let add it to the arraylist
                who.add(i);
            }
        }
        int[] res = who.stream().mapToInt(i -> i).toArray(); // transform the arraylist to a simple array of integer
        return res;
    } // return a list of the elevator under the call src

    private int[] WhosAbove(int[] list, CallForElevator c) {
        ArrayList<Integer> who = new ArrayList<>(1);

        for (int i = 0; i < _building.numberOfElevetors(); i++) {
            if (_building.getElevetor(i).getPos() > c.getSrc()) { // if there is an elevator above the src call add it to the list
                who.add(i);
            }
        }
        int[] res = who.stream().mapToInt(i -> i).toArray();
        return res;
    } // return a list of the elevator above the call src

    private int[] WhoTheSameDirection(int[] elevList, CallForElevator c) {
        ArrayList<Integer> list = new ArrayList<>(1); //creat an arraylist to stock the elevator
        int direction = c.getType(); // get the direction of the call UP or DOWN
        for (int i = 0; i < _building.numberOfElevetors(); i++) {
            if (_building.getElevetor(i).getState() == direction) { // if there is an elevator in the same direction add it to the list
                list.add(i);
            }
        }
        int[] res = list.stream().mapToInt(i -> i).toArray();
        return res;
    } //return a list of the elevator in the same direction

    private int[] WhosResting() {
        boolean ans = false;
        ArrayList<Integer> list = new ArrayList<>(1);
        for (int i = 0; i < ElevList.length; i++) { //let check over the list of the elevator
            if (_building.getElevetor(i).getState() == Elevator.LEVEL) { //if there an elevator at level then it resting let add it to the list
                list.add(i);
                ans = true; // there are at least one resting
            }
        }
        if (ans == false) return null; // if there no elevator resting return null
        else {
            int[] res = list.stream().mapToInt(i -> i).toArray();
            return res;
        }
    } // return a list of elevator that are at LEVEL state

    private ArrayList<Duo> sortBankSrc(ArrayList<Duo> bank, int direction) {
        if (direction == UP) Collections.sort(bank, new Duo.floorComparatorSrc()); // if the diection is UP then sort the call's according to there src floor from the smaller to the bigger
        else if (direction == DOWN) Collections.sort(bank, new Duo.floorComparatorSrc().reversed()); // if the direction is DOWN then sort from  the bigger to the smaller
        return bank;
    } //return the list of src 's calls sorted  according to the direction of the elevator

    private ArrayList<Duo> sortBankDest(ArrayList<Duo> bank, int direction) {
        if (direction == UP) Collections.sort(bank, new Duo.floorComparatorDest()); // same as sortBankSrc but for the destination
        else if (direction == DOWN) Collections.sort(bank, new Duo.floorComparatorDest().reversed());
        return bank;
    } // return the list of dest 's calls sorted according to the direction

    private int nexStop(int direction, int elev) {
        if (direction == UP) { // if the direction is up
            Duo minSrc = null;  //let initiate the minimal src floor
            bank = sortBankSrc(bank, UP); // let sort the the src call of the bank of calls
            for (int i = 0; i < bank.size(); i++) {
                if (bank.get(i).getCall().getState() == CallForElevator.GOING2SRC) { // if the call is going2src
                    if (bank.get(i).getAllocated() == elev && bank.get(i).getCall().getType() == UP && bank.get(i).getCall().getSrc() > _building.getElevetor(elev).getPos()) {
                        // if the call is allocated to the elevator & in direction up & the src floor above the elevator pos then minsrc is the call
                        minSrc = bank.get(i);
                        continue; // if we find one no need to check other
                    }
                    continue;   //the same
                }
            }
            bank = sortBankDest(bank, UP); //same as above but for the destination
            Duo minDest = null;
            for (int i = 0; i < bank.size(); i++) {
                if (bank.get(i).getCall().getState() == CallForElevator.GOIND2DEST) {
                    if (bank.get(i).getAllocated() == elev && bank.get(i).getCall().getType() == UP && bank.get(i).getCall().getDest() > _building.getElevetor(elev).getPos()) {
                        minDest = bank.get(i);
                        continue;
                    }
                    continue;
                }
            }
            if (minDest==null&& minSrc==null) return (getBuilding().maxFloor()/(elev+1)); //if we didn't find no src floor and no dest floor as we wanted then return the floor maxFloor/elev+1
            else if (minSrc == null) return minDest.getCall().getDest(); // if there no min src floor then return min dest floor
            else if (minDest == null) return minSrc.getCall().getSrc(); // if there no min dest floor then return min src floor
            else if (minSrc.getCall().getSrc() < minDest.getCall().getDest()) return minSrc.getCall().getSrc(); //if min src floor is smaller than min dest floor return it
            else return minDest.getCall().getDest(); // then return min dest floor


        } else { // if the direction is down is the same as for up but the inverse

            bank = sortBankSrc(bank, DOWN);
            Duo maxSrc = null;
            for (int i = 0; i < bank.size(); i++) {
                if (bank.get(i).getCall().getState() == CallForElevator.GOING2SRC) {
                    if (bank.get(i).getAllocated() == elev && bank.get(i).getCall().getType() == DOWN && bank.get(i).getCall().getSrc() < _building.getElevetor(elev).getPos()) {
                        maxSrc = bank.get(i);
                        continue;
                    }
                    continue;
                }
            }
            bank = sortBankDest(bank, DOWN);
            Duo maxDest = null;
            for (int i = 0; i < bank.size(); i++) {
                if (bank.get(i).getCall().getState() == CallForElevator.GOIND2DEST) {
                    if (bank.get(i).getAllocated() == elev && bank.get(i).getCall().getType() == DOWN && bank.get(i).getCall().getDest() < _building.getElevetor(elev).getPos()) {
                        maxDest = bank.get(i);
                        continue;
                    }
                    continue;
                }
            }
            if (maxDest== null&& maxSrc==null) return (getBuilding().maxFloor()/(elev+1));
            else if (maxSrc == null) return maxDest.getCall().getDest();
            else if (maxDest == null) return maxSrc.getCall().getSrc();
            else if (maxSrc.getCall().getSrc() < maxDest.getCall().getDest()) return maxSrc.getCall().getSrc();
            else return maxDest.getCall().getDest();
        }
    } // return the better next floor to stop at according to the state of the elevator
    // if the elevator is at level state then we cannot tell it to stop then he need to go
    private int closestFloor(int elev) {
        int distance = Integer.MAX_VALUE;
        int floor = 0;
        for (int i = 0; i < bank.size(); i++) { //we will check over the bank of calls if there
            int temp = Math.abs(bank.get(i).getCall().getSrc() - _building.getElevetor(elev).getPos()); //get the distance from the elevator to the actual call
            if (temp < distance && bank.get(i).getAllocated()==elev) { // if the distance is smaller than the reference and allocated to the elevator
                distance = temp; //replace it
                floor = bank.get(i).getCall().getSrc(); //get is src call floor
            } //
        }
        for (int i = 0; i < bank.size(); i++) { // the same as above but for the destination
            int temp = Math.abs(bank.get(i).getCall().getDest() - _building.getElevetor(elev).getPos());
            if (temp < distance && bank.get(i).getAllocated()==elev) {
                distance = temp;
                floor = bank.get(i).getCall().getDest();
            }
        }
        return floor;
    } // return the closest floor to go according to the position of the elevator
}






