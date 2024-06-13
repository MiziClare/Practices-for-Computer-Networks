import java.util.Random;

public class NetworkSimulator {
    // This constant controls the maximum size of the buffer in a Message
    // and in a Packet
    public static final int MAXDATASIZE = 20;

    // These constants are possible events
    public static final int TIMERINTERRUPT = 0;
    public static final int FROMAPP = 1;
    public static final int FROMNETWORK = 2;
    public static final int ENDSIMULATION = 3;

    // These constants represent our sender and receiver
    public static final int A = 12345;
    public static final int B = 67890;

    private Sender sender;
    private Receiver receiver;

    // FOR TESTING
    private String receivedData;
    private String sentData;

    private int maxMessages;
    private double lossProb;
    private double corruptProb;
    private double avgMessageDelay;
    private int traceLevel;
    private EventList eventList;

    private Random rand;

    private int nSim;
    private double time;
    private double lastEventTime;

    public NetworkSimulator(int numMessages,
            double loss,
            double corrupt,
            double avgDelay,
            int trace,
            long seed) {
        maxMessages = numMessages;
        lossProb = loss;
        corruptProb = corrupt;
        avgMessageDelay = avgDelay;
        traceLevel = trace;
        eventList = new EventList();

        rand = new Random(seed);

        nSim = 0;
        time = 0.0;
        lastEventTime = 0.0;

        receivedData = "";
        sentData = "";

        sender = new Sender(A, eventList, lossProb, corruptProb,
                traceLevel, rand);
        receiver = new Receiver(B, eventList, lossProb, corruptProb,
                traceLevel, rand);

    }

    public void runSimulator() {
        Event next;

        // Perform any student-required initialization
        sender.Init();
        receiver.Init();

        // Start the whole thing off by scheduling some data arrival
        // from sending process
        generateNextArrival();

        // Begin the main loop
        outer: while (true) {
            // Get our next event
            next = eventList.removeNext();
            if (next == null) {
                break;
            }

            if (traceLevel >= 2) {
                System.out.println();
                System.out.print("EVENT time: " + next.getTime());
                System.out.print("  type: " + next.getType());
                System.out.println("  entity: " + next.getEntity());
            }

            // Advance the simulator's time
            time = next.getTime();
            sender.setTime(time);
            receiver.setTime(time);

            // Perform the appropriate action based on the event
            switch (next.getType()) {
                case TIMERINTERRUPT:
                    if (next.getEntity() == A) {
                        sender.TimerInterrupt();
                    } else {
                        System.out.println("INTERNAL PANIC: Timeout for " +
                                "invalid entity");
                    }
                    break;

                case FROMNETWORK:
                    if (next.getEntity() == A) {
                        sender.Input(next.getPacket());
                    } else if (next.getEntity() == B) {
                        receiver.Input(next.getPacket());
                    } else {
                        System.out.println("INTERNAL PANIC: Packet has " +
                                "arrived for unknown entity");
                    }

                    break;

                case FROMAPP:

                    // If a message has arrived from sending process, we need to
                    // schedule the arrival of the next message
                    if (nSim < maxMessages - 1) {
                        generateNextArrival();
                    }
                    char[] nextMessage = new char[MAXDATASIZE];

                    // Now, let's generate the contents of this message
                    char j = (char) ((nSim % 26) + 97);
                    for (int i = 0; i < MAXDATASIZE; i++) {
                        nextMessage[i] = j;
                    }
                    for (int i = 1; i < MAXDATASIZE; i++) {
                        nextMessage[i] = (char) (rand.nextInt(26) + 97);
                    }

                    // Increment the message counter
                    nSim++;

                    // If we've reached the maximum message count, generate ENDSIMULATION event
                    if (nSim == maxMessages) {
                        Event endsim = new Event(time + 10000, ENDSIMULATION, A);
                        eventList.add(endsim);

                    }

                    String messageContent = new String(nextMessage);
                    sentData = sentData + messageContent + "\n";
                    // Let the student handle the new message
                    sender.Output(new Message(messageContent));
                    break;

                case ENDSIMULATION:
                    if (traceLevel > 1) {
                        System.out.println("END OF SIMULATION REACHED");
                        System.out.println(eventList.countInTransit() + " messages still in transit");
                        System.out.println(eventList.countInterrupt() + " outstanding timer interrupts");
                        if (traceLevel > 2) {
                            System.out.println("Printing EVENTLIST:");
                            System.out.println(eventList);
                        }
                    }

                    break outer;

                default:
                    System.out.println("INTERNAL PANIC: Unknown event type");
            }
            // update the time of the last handled event
            lastEventTime = time;
        }

    }

    /* Generate the next arrival and add it to the event list */
    private void generateNextArrival() {
        if (traceLevel > 2) {
            System.out.println("generateNextArrival(): called");
        }

        // arrival time 'x' is uniform on [0.5*avgMessageDelay, 1.5*avgMessageDelay]
        // having mean of avgMessageDelay. Should this be made
        // into a Gaussian distribution?
        double x = 0.5 * avgMessageDelay + avgMessageDelay * rand.nextDouble();

        Event next = new Event(time + x, FROMAPP, A);

        eventList.add(next);
        if (traceLevel > 2) {
            System.out.println("generateNextArrival(): time is " + time);
            System.out.println("generateNextArrival(): future time for " +
                    "event " + next.getType() + " at entity " +
                    next.getEntity() + " will be " +
                    next.getTime());
        }

    }

    protected double getTime() {
        return time;
    }

    protected double getLastEventTime() {
        return lastEventTime;
    }

    protected String getReceivedData() {
        return receiver.getReceivedData();
    }

    protected String getSentData() {
        return sentData;
    }

    protected int getNumberDelivered() {
        return receiver.getNumberDelivered();
    }

    protected void printEventList() {
        System.out.println(eventList.toString());
    }

}
