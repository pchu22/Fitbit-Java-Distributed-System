import Cluster_Management.*;
import Comparison_Methods.Hamming;
import Comparison_Methods.Minkowski;
import Dataset.Dataset;
import Comparison_Methods.KNearestNeighbours;
import Comparison_Methods.Manhattan;
import Multicast.*;
import Unicast.*;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class Element extends Thread {
    private final int elementOwnPort;
    private int leaderUnicastPort;
    private MulticastSender multicastSender;
    private final String role;
    private final String method;
    private StringBuilder messageToBeSent = new StringBuilder();
    private ConcurrentHashMap<UUID, Dataset> datasetsToBeEvaluated = new ConcurrentHashMap<>();
    private ArrayList<Voting> votesToAddInDataset = new ArrayList<>();
    private ArrayList<Voting> votesToIncludeElement = new ArrayList<>();
    private ArrayList<Voting> votesToRemoveElement = new ArrayList<>();
    private ArrayList<Participation> allParticipants = new ArrayList<>();
    private ArrayList<Dataset> data;
    private boolean belongsToGroup;
    private boolean isCandidate;
    public Element(int _port, String _role, String _method) {
        this.elementOwnPort = _port;
        this.role = _role;
        this.method = _method;
        this.belongsToGroup = false;
    }

    public void run() {
        if(Objects.equals(role, "L")){
            leaderProcess();
        } else if (Objects.equals(role, "E")) {
            followerProcess();
        } else {
            System.out.println("Invalid role!");
        }
    }

    public void startMulticast(int _port){
        this.multicastSender = new MulticastSender("230.0.0.0", elementOwnPort, _port);
        this.multicastSender.start();
        this.multicastSender.addMessage("COMMIT", UUID.randomUUID() + ";" + new Dataset(
                70, 320, 6, 520, 130,
                420, 2100, 90, 45, 7,
                8500, 11000, 55, 11, 30,
                110, 7500, 13000, 2500, 85, 22,
                75, 300, 75).getString());
    }

    public void startUnicast(BlockingQueue<String> _messageQueue, int _port){
        try {
            DatagramSocket unicastSocket = new DatagramSocket(_port);
            UnicastReceiver receiver = new UnicastReceiver(unicastSocket , _messageQueue);
            receiver.start();
        }catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    private int numberOfAliveElements(){
        return (int) allParticipants.stream()
                .filter(Participation::getStatus)
                .count();
    }

    private boolean votingToAddToDataset(String _command, String _uuid) {
        for (Iterator<Voting> i = votesToAddInDataset.iterator(); i.hasNext();) {
            Voting vote = i.next();

            if (vote.getUUID().equals(_uuid)) {
                vote.addVote();

                if (_command.equals("ADD")){
                    vote.addApproval();
                }

                boolean approvalConditionMet = vote.getVotes() == numberOfAliveElements() && vote.getApprovals() == numberOfAliveElements()/2;
                this.multicastSender.addMessage(approvalConditionMet ? "ADD" : "IGNORE", _uuid);
                i.remove();
                return true;
            }
        }
        return false;
    }

    public void processVotingToIncludeInDataset(String _command, String _uuid) {
        if (!votingToAddToDataset(_command, _uuid)){ //Executes this if there is no votings
            if(numberOfAliveElements() == 1){
                 this.multicastSender.addMessage(_command.equals("ADD") ? "ADD" : "IGNORE", _uuid);
            } else {
                int approvalCount = _command.equals("ADD") ? 1 : 0;
                votesToAddInDataset.add(new Voting(_uuid, approvalCount, 1));
            }
        }
    }

    public String listAllParticipants(){
        return allParticipants.stream()
                .map(Participation::toString)
                .collect(Collectors.joining(";"));
    }

    private boolean votingToAddFollower(String _command, String _uuid) {
        for  (Iterator<Voting> i = votesToIncludeElement.iterator(); i.hasNext();) {
            Voting vote = i.next();

            if (vote.getUUID().equals(_uuid)) {
                vote.addVote();

                if(_command.equals("GROUP-ADD")){
                    vote.addApproval();
                }

                if (vote.getVotes() == numberOfAliveElements()){
                    if (vote.getApprovals() >= numberOfAliveElements()/2){
                        this.multicastSender.addMessage("GROUP-UPDATE;",listAllParticipants() + _uuid);
                        allParticipants.add(new Participation(_uuid));
                    }else{
                        this.multicastSender.addMessage("GROUP-RECUSE;", _uuid);
                    }
                    i.remove();
                    return true;
                }
            }
        }
        return false;
    }

    private boolean votingToRemoveFollower(String _command, String _uuid) {
        for (Iterator<Voting> i = votesToRemoveElement.iterator(); i.hasNext();) {
            Voting vote = i.next();

            if (vote.getUUID().equals(_uuid)) {
                vote.addVote();

                if(_command.equals("REMOVE")){
                    vote.addApproval();
                }

                if (vote.getVotes() == numberOfAliveElements()){
                    if (vote.getApprovals() >= numberOfAliveElements()/2){
                        this.multicastSender.addMessage("REMOVE", _uuid);
                        allParticipants.add(new Participation(_uuid));
                    }
                    i.remove();
                    return true;
                }
            }
        }

        return false;
    }

    public void processVotingToAddFollower(String _command, String _uuid) {
        if (!votingToAddFollower(_command, _uuid)){
            if(numberOfAliveElements() == 1){
                    this.multicastSender.addMessage(_command.equals("GROUP-ADD") ? "GROUP-UPDATE" : "GROUP-RECUSE", listAllParticipants() + _uuid);
            }else {
                int approvalCount = _command.equals("GROUP-ADD") ? 1 : 0;
                votesToIncludeElement.add(new Voting(_uuid, approvalCount, 1));
            }
        }
    }

    public void processVotingToRemoveFollower(String _command, String _uuid) {
        if (!votingToRemoveFollower(_command, _uuid)){
            if(numberOfAliveElements() == 1 && _command.equals("REMOVE")){
                this.multicastSender.addMessage("REMOVE", _uuid);
            } else {
                int approvalCount = _command.equals("REMOVE") ? 1 : 0;
                votesToRemoveElement.add(new Voting(_uuid, approvalCount, 1));
            }
        }
    }

    public void updateElement(String _uuid){
        allParticipants.stream()
                .filter(participation -> participation.getID().equals(_uuid))
                .findFirst()
                .ifPresent(participation -> {
                    participation.setLastResponseTime();
                    participation.setStatus(true);
                });
    }

    private void processJoinGroup(String payload) {
        if (allParticipants.isEmpty()) {
            this.multicastSender.addMessage("GROUP-UPDATE", payload);
            allParticipants.add(new Participation(payload));
        } else {
            this.multicastSender.addMessage("GROUP-COMMIT", listAllParticipants() + "-" + payload);
        }
    }

    public void processLeaderMessage(String msg){
        int index = msg.indexOf(";");
        String _command = msg.substring(0, index);
        String payload = msg.substring(index + 1);

        switch (_command){
            case "MY-ADDRESS":
                updateElement(payload);
                break;
            case "ADD":
            case "IGNORE":
                processVotingToIncludeInDataset(_command, payload);
                break;
            case "GROUP-ADD":
            case "GROUP-RECUSE":
                processVotingToAddFollower(_command, payload);
                break;
            case "JOIN":
                processJoinGroup(payload);
                break;
            case "REMOVE":
            case "NOT-REMOVE":
                processVotingToRemoveFollower(_command, payload);
                break;
            default:
                throw new IllegalArgumentException("Unknown command: " + _command);
        }
    }

    public void checkAliveNodes(){
        Iterator<Participation> i = allParticipants.iterator();

        while (i.hasNext()){
            Participation participant = i.next();

            if (!participant.hasBeenUpdated() && participant.getStatus()){
                participant.setStatus(false);
                this.multicastSender.addMessage("GROUP-REMOVE", listAllParticipants() + "-" + participant.getID());
                i.remove();
            }
        }
    }

    public void splitsLeaderMessages(String _str, String _del){
        Arrays.stream(_str.split(_del))
                .forEach(this::processLeaderMessage);
    }

    public void runLeaderLoop(BlockingQueue<String> messageQueue ){
        while (true){
            try {
                String receivedMessage = messageQueue.take(); // Leader waits for followers to send something

                if(!receivedMessage.isEmpty()){
                    splitsLeaderMessages(receivedMessage, "&");
                }

                checkAliveNodes();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void leaderProcess(){
        BlockingQueue<String> messageQueue = new ArrayBlockingQueue<>(256);
        startMulticast(Integer.parseInt(method));
        startUnicast(messageQueue, Integer.parseInt(method));
        runLeaderLoop(messageQueue);
    }

    public ArrayList<Dataset> loadData(){
        ArrayList<Dataset> data = new ArrayList<>();

        data.add(new Dataset(70, 320, 6, 520, 130,
                420, 2100, 90, 45, 7, 8500,
                11000, 55, 11, 30, 110, 7500,
                13000, 2500, 85, 22, 75, 300, 75));
        data.add(new Dataset(55, 270, 5, 480, 100,
                370, 2000, 70, 32, 6, 8000,
                10500, 50, 10, 28, 100, 7200,
                12500, 2200, 90, 18, 70, 270, 70));
        data.add(new Dataset(80, 420, 8, 620, 160,
                530, 2600, 110, 55, 10, 9500,
                13000, 70, 15, 45, 140, 8200,
                16000, 3300, 105, 32, 85, 380, 95));
        data.add(new Dataset(35, 250, 4, 450, 70,
                310, 1600, 50, 28, 5, 7000,
                9000, 40, 8, 22, 80, 5500,
                9000, 1800, 70, 15, 55, 230, 65));
        data.add(new Dataset(55, 380, 7, 600, 120,
                470, 2400, 80, 38, 8, 9000,
                12000, 65, 13, 38, 130, 7800,
                14000, 2700, 100, 28, 80, 360, 85));
        data.add(new Dataset(70, 400, 6, 550, 140,
                500, 2300, 100, 48, 9, 9200,
                12500, 80, 16, 42, 130, 8000,
                15000, 3000, 95, 30, 82, 410, 90));
        data.add(new Dataset(45, 320, 5, 510, 100,
                420, 1900, 60, 35, 6, 7700,
                10000, 55, 12, 25, 110, 6800,
                12000, 2400, 80, 20, 65, 310, 80));

        return data;
    }

    public boolean compareParticipantsLists(ArrayList<Participation> tempList) {
        if (allParticipants.size() != tempList.size()) {
            return false;
        }

        ArrayList<Participation> copy = new ArrayList<>(allParticipants);

        return copy.stream()
                .allMatch(participation -> tempList.removeIf(
                        tempParticipant -> tempParticipant.getID(). equals(participation.getID())))
                && tempList.isEmpty();
    }

    private void processFollowerCommit(String payload){
        int index = payload.indexOf(";");
        UUID followerID = UUID.fromString(payload.substring(0, index));
        String followerDataset = payload.substring(index + 1);
        this.datasetsToBeEvaluated.put(followerID, new Dataset(followerDataset));
    }

    private void processFollowerAddToDataset(String payload){
        UUID followerID = UUID.fromString(payload);
        data.add(datasetsToBeEvaluated.get(followerID));
        datasetsToBeEvaluated.remove(followerID);
    }

    private void processFollowerRemoveFromDatasetsToBeEvaluated(String payload){
        UUID followerID = UUID.fromString(payload);
        datasetsToBeEvaluated.remove(followerID);
    }

    private void processFollowerAddFollowerToGroup(String payload){
        int index = payload.lastIndexOf("-");
        String participantsToString = payload.substring(0, index);
        String newFollower = payload.substring(index + 1);

        ArrayList<Participation> receivedParticipants = Arrays
                .stream(participantsToString.split(";"))
                .map(Participation::new)
                .collect(Collectors.toCollection(ArrayList::new));

        if(compareParticipantsLists(receivedParticipants)) {
            messageToBeSent.append("GROUP-ADD;").append(newFollower).append("&");
        } else {
            messageToBeSent.append("GROUP-RECUSE;").append(newFollower).append("&");
        }
    }

    private void processFollowerRemoveFollowerFromGroup(String payload){
        int index = payload.lastIndexOf("-");
        String participantsToString = payload.substring(0, index);
        String followerToBeRemoved = payload.substring(index + 1);

        ArrayList<Participation> receivedParticipants = Arrays
                .stream(participantsToString.split(";"))
                .map(Participation::new)
                .collect(Collectors.toCollection(ArrayList::new));

        if(compareParticipantsLists(receivedParticipants)) {
            messageToBeSent.append("REMOVE;").append(followerToBeRemoved).append("&");
        } else {
            messageToBeSent.append("NOT-REMOVE;").append(followerToBeRemoved).append("&");
        }
    }

    private void processFollowerUpdateGroup(String payload){
        allParticipants.clear();
        Arrays.stream(payload.split(";"))
                .map(Participation::new)
                .forEach(allParticipants::add);
    }

    private void processFollowerMessage(String msg){
        String [] parts = msg.split(";", 2);
        String _command = parts[0];
        String payload = parts.length > 1 ? parts[1] : "";

        switch (_command){
            case "LEADER":
                leaderUnicastPort = Integer.parseInt(payload);
                break;
            case "COMMIT":
                processFollowerCommit(payload);
                break;
            case "ADD":
                processFollowerAddToDataset(payload);
                break;
            case "IGNORE":
                processFollowerRemoveFromDatasetsToBeEvaluated(payload);
                break;
            case "GROUP-COMMIT":
                processFollowerAddFollowerToGroup(payload);
                break;
            case "GROUP-REMOVE":
                processFollowerRemoveFollowerFromGroup(payload);
                break;
            case "GROUP-UPDATE":
                processFollowerUpdateGroup(payload);
                break;
            default:
                throw new IllegalArgumentException("Unknown command: " + _command);
        }
    }

    public void splitFollowerMessage(String _str, String _del){
        Arrays.stream(_str.split(_del))
                .forEach(this::processFollowerMessage);
    }

    public void sendFollowerMessages() {
        messageToBeSent.append("MY-ADDRESS;")
                .append(elementOwnPort)
                .append("&");

        for (UUID datasetUUID : datasetsToBeEvaluated.keySet()) {
            String value = "IGNORE";

            switch (method) {
                case "KNN":
                    KNearestNeighbours KNN = new KNearestNeighbours();
                    value = KNN.KNearestNeighbours(datasetsToBeEvaluated.get(datasetUUID), data);
                    break;

                case "Manhattan":
                    Manhattan manhattan = new Manhattan();
                    value = manhattan.Manhattan(datasetsToBeEvaluated.get(datasetUUID), data);
                    break;

                case "Minkowski":
                    Minkowski minkowski = new Minkowski();
                    value = minkowski.Minkowski(datasetsToBeEvaluated.get(datasetUUID), data, 2);
                    break;

                case "Hamming":
                    Hamming hamming = new Hamming();
                    value = hamming.Hamming(datasetsToBeEvaluated.get(datasetUUID), data);
                    break;

                default:
                    throw new IllegalArgumentException("Unknown method: " + method);
            }

            messageToBeSent.append(value)
                    .append(";")
                    .append(datasetUUID)
                    .append("&");
        }

        UnicastSender unicastSender = new UnicastSender(messageToBeSent.toString(), leaderUnicastPort, elementOwnPort);
        unicastSender.start();
        messageToBeSent = new StringBuilder();
    }

    public void processJoinRequest(String msg){
        String[] messages = msg.split("&");
        for (String message : messages) {
            int index = message.indexOf(";");
            String _command = message.substring(0, index);
            String payload = message.substring(index + 1);

            if (_command.equals("LEADER")) {
                try {
                    leaderUnicastPort = Integer.parseInt(payload);
                    UnicastSender unicastSender = new UnicastSender("MY-ADDRESS;" + elementOwnPort + "&JOIN;" + elementOwnPort, leaderUnicastPort, elementOwnPort);
                    unicastSender.start();
                } catch (NumberFormatException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public boolean verifyIfElementHasBeenAddedToGroup(String msg){
        String[] messages = msg.split("&");
        AtomicBoolean waitForResponse = new AtomicBoolean(true);

        Arrays.stream(messages).forEachOrdered(message -> {
            int index = message.indexOf(";");
            String _command = message.substring(0, index);
            String payload = message.substring(index + 1);

            if (_command.equals("GROUP-UPDATE")){
                String[] allElements = payload.split(";");

                Arrays.stream(allElements).forEachOrdered(element -> {
                    this.allParticipants.add(new Participation(element));

                    if(element.equals(String.valueOf(elementOwnPort))){
                        this.belongsToGroup = true;
                        waitForResponse.set(false);
                    }
                });
            }else if (_command.equals("GROUP-RECUSE")){
                if(payload.equals(String.valueOf(elementOwnPort))){
                    waitForResponse.set(false);
                }
            }
        });
        return waitForResponse.get();
    }

    public void voteToBecomeLeader(String msg){
        int index = msg.indexOf(";");
        String _command = msg.substring(0, index);
        String payload = msg.substring(index + 1);

        if (_command.equals("WANTS-TO-BECOME-LEADER")){
            UnicastSender unicastSender = new UnicastSender("CONFIRM-LEADER", Integer.parseInt(payload), elementOwnPort);
            unicastSender.start();
        }
    }

    public void voteToRefuseAsLeader(String msg){
        int index = msg.indexOf(";");
        String _command = msg.substring(0, index);
        String payload = msg.substring(index + 1);

        if (_command.equals("WANTS-TO-BECOME-LEADER")){
            UnicastSender unicastSender = new UnicastSender("IGNORE-LEADER", Integer.parseInt(payload), elementOwnPort);
            unicastSender.start();
        }
    }

    private int findAvailablePort(int initialValue, int finalValue) {
        for (int port = initialValue; port <= finalValue; port++) {
            if (isPortAvailable(port)) {
                return port;
            }
        }
        return -1;
    }

    private boolean isPortAvailable(int _port) {
        try (DatagramSocket serverSocket = new DatagramSocket (_port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean processVotingToBecomeLeader(String _command, Voting _voting) {
        _voting.addVote();

        if(_command.equals("CONFIRM-LEADER")){
            _voting.addApproval();
        }

        return _voting.getVotes() != allParticipants.size();
    }

    private void becomeLeader(int _unicastPort){
        this.multicastSender = new MulticastSender("230.0.0.0", elementOwnPort, _unicastPort);
        this.multicastSender.start();
        BlockingQueue<String> messageQueue = new ArrayBlockingQueue<>(256);

        startUnicast(messageQueue, _unicastPort);
        runLeaderLoop(messageQueue);
    }

    private void processToChooseTheLeader(int _unicastPort){
        this.multicastSender = new MulticastSender("230.0.0.0", elementOwnPort, _unicastPort);
        BlockingQueue<String> messageQueue = new ArrayBlockingQueue<>(256);

        try {
            startUnicast(messageQueue, _unicastPort);
        }catch (RuntimeException e){
            throw new RuntimeException(e);
        }

        this.multicastSender.addMessage("WANTS-TO-BECOME-LEADER",elementOwnPort + ";");

        AtomicBoolean waitForVotes = new AtomicBoolean(true);
        Voting voting = new Voting("" + elementOwnPort, 0, 1);

        while (waitForVotes.get()){
            String receivedMessages = null;
            try {
                receivedMessages = messageQueue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            String[] messages = receivedMessages.split("&");
            Arrays.stream(messages).forEachOrdered(message -> {

                int index = message.indexOf(";");
                String _command = message.substring(0, index);

                if (_command.equals("CONFIRM-LEADER") || _command.equals("IGNORE-LEADER") ){
                    waitForVotes.set(processVotingToBecomeLeader(_command, voting));
                }
            });
        }

        if (voting.getApprovals() >= allParticipants.size()/2){
            runLeaderLoop(messageQueue);
        }
    }

    public void becomeCandidate(){
        int unicastPort = findAvailablePort(4450, 8080);
        if (unicastPort == -1){
            System.out.println("No available port found. Cannot become a candidate.");
        }else{
            if(allParticipants.isEmpty()){
                // If there are no participants, the element instantly becomes leader!
                becomeLeader(unicastPort);
            }else{
                //If there are more participants, there is a need for a voting
                processToChooseTheLeader(unicastPort);
            }
        }
    }

    public void waitForLeadershipOpportunity(BlockingQueue<String> messageQueue){
        Random rand = new Random();
        int waitingTimeForLeaderAnswer = rand.nextInt(60);

        try {
            String receivedMessage = messageQueue.poll(waitingTimeForLeaderAnswer, TimeUnit.SECONDS);

            if (receivedMessage != null){
                voteToBecomeLeader(receivedMessage);

                LocalDateTime startTime = LocalDateTime.now();
                LocalDateTime threshold = startTime.plusSeconds(30);

                while (LocalDateTime.now().isBefore(threshold)) {
                    // After waiting for 30secs, since already exists a candidate to become leader, a refuse message
                    // will be sent to the rest of the followers that wanted to candidate themselves to become leader
                    receivedMessage = messageQueue.poll(waitingTimeForLeaderAnswer, TimeUnit.SECONDS);
                    voteToRefuseAsLeader(receivedMessage);
                }
            }else {
                // None sent a message to become candidate, so the follower immediately becomes one
                becomeCandidate();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void followerProcess(){
        BlockingQueue<String> messageQueue = new ArrayBlockingQueue<>(256);
        MulticastReceiver multicastReceiver = new MulticastReceiver("230.0.0.0", elementOwnPort, messageQueue);
        multicastReceiver.start();

        data = loadData();
        isCandidate = false;
        int waitingTimeForLeaderAnswer = 60;
        boolean isWaitingForLeaderResponse = false;
        String receivedMessage;

        try {
            receivedMessage = messageQueue.poll(waitingTimeForLeaderAnswer, TimeUnit.SECONDS);

            if (receivedMessage != null){
                // Will ask to join the group and then will go to wait for leader to answer the request (in the while loop)
                processJoinRequest(receivedMessage);
                isWaitingForLeaderResponse = true;
            }else {
                // Leader hasn't answered in the stipulated time (60), an election for a new leader will start
                waitForLeadershipOpportunity(messageQueue);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        while (isWaitingForLeaderResponse){
            try {
                receivedMessage = messageQueue.poll(waitingTimeForLeaderAnswer, TimeUnit.SECONDS);

                if (receivedMessage != null) {
                    // Leader will verify if the rest of the element have voted the new one in or out
                    isWaitingForLeaderResponse = verifyIfElementHasBeenAddedToGroup(receivedMessage);
                    // If the new element is added to the group, the rest of the process will occur in the next while loop
                }else {
                    // Leader hasn't answered in the stipulated time (60), an election for a new leader will start
                    waitForLeadershipOpportunity(messageQueue);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        while (belongsToGroup){
            isCandidate = false;

            try {
                receivedMessage = messageQueue.poll(waitingTimeForLeaderAnswer, TimeUnit.SECONDS);
                if (receivedMessage != null) {
                    // Splits the messages follower wants to send and proceeds to send them
                    splitFollowerMessage(receivedMessage,"&");
                    sendFollowerMessages();
                }else {
                    // Leader hasn't answered in the stipulated time (60), an election for a new leader will start
                    waitForLeadershipOpportunity(messageQueue);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}