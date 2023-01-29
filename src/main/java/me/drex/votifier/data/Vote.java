package me.drex.votifier.data;

public class Vote {

    private final String username;
    private final String serviceName;
    private final String timeStamp;
    private final String address;

    public Vote(String username, String serviceName, String timeStamp, String address) {
        this.username = username;
        this.serviceName = serviceName;
        this.timeStamp = timeStamp;
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getAddress() {
        return address;
    }

    public String toString() {
        return String.join("", "Vote[userName=" , username,
                ", serviceName=", serviceName,
                ", timeStamp=", timeStamp,
                ", address=", address, "]");
    }
}