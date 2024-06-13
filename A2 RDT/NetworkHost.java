import java.util.Random;

public class NetworkHost {
    // This constant controls the maximum size of the buffer in a Message
    // and in a Packet
    public static final int MAXDATASIZE = 20;

    private int entity;
    private int traceLevel;
    private EventList eventList;
    private Random rand;
    private double time;
    private double lossProb;
    private double corruptProb;
    private String receivedData;
    private int numberDelivered;

    // Default versions of methods to implement.
    protected void Output(Message message) {
        System.out.println("Output method called for entity" + entity
                + "but not implemented.");
    }

    protected void Input(Packet packet) {
        System.out.println("Input method called for entity" + entity
                + "but not implemented.");
    }

    protected void TimerInterrupt() {
        System.out.println("TimerInterupt method called for entity" + entity
                + "but not implemented.");
    }

    protected void Init() {
        System.out.println("Init method called for entity" + entity
                + "but not implemented.");
    }

    public NetworkHost(int entityName,
            EventList events,
            double pLoss,
            double pCorrupt,
            int trace,
            Random random) {
        entity = entityName;
        eventList = events;
        lossProb = pLoss;
        corruptProb = pCorrupt;
        traceLevel = trace;
        rand = random;
        time = 0.0;
        receivedData = "";
        numberDelivered = 0;
    }

    protected final void stopTimer() {
        if (traceLevel > 0) {
            System.out.println("stopTimer: stopping timer at " + time);
        }

        Event timer = eventList.removeTimer(entity);

        // Let the student know they are attempting to cancel a non-existant
        // timer
        if (timer == null) {
            System.out.println("stopTimer: Warning: Unable to cancel your " +
                    "timer");
        }
    }

    protected final void startTimer(double increment) {
        if (traceLevel > 0) {
            System.out.println("startTimer: starting timer at " + time);
        }

        Event t = eventList.removeTimer(entity);

        if (t != null) {
            System.out.println("startTimer: Warning: Attempting to start a " +
                    "timer that is already running");
            eventList.add(t);
            return;
        } else {
            Event timer = new Event(time + increment,
                    NetworkSimulator.TIMERINTERRUPT, entity);
            eventList.add(timer);
        }
    }

    protected final void udtSend(Packet p) {
        int destination;
        double arrivalTime;
        Packet packet = new Packet(p);

        if (traceLevel > 0) {
            System.out.println("udtSend: " + packet);
        }

        // Set our destination
        if (entity == NetworkSimulator.A) {
            destination = NetworkSimulator.B;
        } else if (entity == NetworkSimulator.B) {
            destination = NetworkSimulator.A;
        } else {
            System.out.println("udtSend: Warning: invalid packet sender");
            return;
        }

        // Simulate losses
        if (rand.nextDouble() < lossProb) {
            if (traceLevel > 0) {
                System.out.println("udtSend: packet being lost");
            }

            return;
        }

        // Simulate corruption
        if (rand.nextDouble() < corruptProb) {
            double x = rand.nextDouble();
            if (x < 0.75) {
                String payload = packet.getPayload();

                if (payload.length() < 2) {
                    payload = "=";
                } else {
                    char[] ch = new char[payload.length()];

                    for (int i = 0; i < payload.length(); i++) {
                        ch[i] = payload.charAt(i);
                    }
                    // pick random character to corrupt
                    int cci = rand.nextInt(ch.length);
                    // ch[cci] = (char)(ch[cci]-32);
                    ch[cci] = (char) (rand.nextInt(26) + 65);

                    // payload = "?" + payload.substring(payload.length() - 1);
                    // payload = "=" + payload.substring(1);
                    payload = new String(ch);

                }

                packet.setPayload(payload);
            } else if (x < 0.875) {
                packet.setSeqnum(Math.abs(rand.nextInt()));
            } else {
                packet.setAcknum(Math.abs(rand.nextInt()));
            }

            if (traceLevel > 0) {
                System.out.println("udtSend: packet being corrupted");
                System.out.println("after corruption: " + packet);
            }

        }

        // Decide when the packet will arrive. Since the medium cannot
        // reorder, the packet will arrive 1 to 10 time units after the
        // last packet sent by this sender
        arrivalTime = eventList.getLastPacketTime(destination);

        if (arrivalTime <= 0.0) {
            arrivalTime = time;
        }

        // arrivalTime = arrivalTime + 1.0 + (rand.nextDouble() * 19.0);
        arrivalTime = arrivalTime + 1.0 + Math.abs(5.0 * rand.nextGaussian() + 9.0);

        // Finally, create and schedule this event
        if (traceLevel > 2) {
            System.out.println("udtSend: Scheduling arrival on other side");
        }
        Event arrival = new Event(arrivalTime, NetworkSimulator.FROMNETWORK, destination, packet);
        eventList.add(arrival);
    }

    protected final void deliverData(String dataSent) {
        // FOR TESTING
        if (entity == NetworkSimulator.B) {
            receivedData = receivedData + dataSent + "\n";
            numberDelivered = numberDelivered + 1;
        }
        if (traceLevel > 0) {
            System.out.print("deliverData: data received at " + entity + ":");
            System.out.println(dataSent);

        }
    }

    protected void setTime(double newtime) {
        time = newtime;
    }

    protected double getTime() {
        return time;
    }

    protected String getReceivedData() {
        return receivedData;
    }

    protected int getNumberDelivered() {
        return numberDelivered;
    }

    protected void printEventList() {
        System.out.println(eventList.toString());
    }

}
