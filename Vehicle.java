package za.accput.t6project.doa;

import java.io.Serializable;

public class Vehicle implements Serializable {

    private String name;
    private int vote;

    public Vehicle(String name, int vote) {
        this.name = name;
        this.vote = vote;

    }

    public String getName() {
        return name;
    }

    public int getVote() {
        return vote;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public void incrementVote() {
        this.vote++;
    }

    @Override
    public String toString() {
        return "Vehicle{" + "name=" + name + ", vote=" + vote + '}';
    }

}
