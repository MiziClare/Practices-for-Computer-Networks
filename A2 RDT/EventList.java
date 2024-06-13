import java.util.Vector;

public class EventList {
    private Vector<Event> data;

    public EventList() {
        data = new Vector<Event>();
    }

    public int countInterrupt() {
        int count = 0;

        for (int i = 0; i < data.size(); i++) {
            if (((Event) (data.elementAt(i))).getType() == NetworkSimulator.TIMERINTERRUPT) {
                count++;
            }
        }
        return count;
    }

    public int countInTransit() {
        int count = 0;

        for (int i = 0; i < data.size(); i++) {
            if (((Event) (data.elementAt(i))).getType() == NetworkSimulator.FROMNETWORK) {
                count++;
            }
        }
        return count;
    }

    public boolean add(Event e) {
        data.addElement(e);
        return true;
    }

    public Event removeNext() {
        if (data.isEmpty()) {
            return null;
        }

        int firstIndex = 0;
        double first = ((Event) data.elementAt(firstIndex)).getTime();
        for (int i = 0; i < data.size(); i++) {
            if (((Event) data.elementAt(i)).getTime() < first) {
                first = ((Event) data.elementAt(i)).getTime();
                firstIndex = i;
            }
        }

        Event next = (Event) data.elementAt(firstIndex);
        data.removeElement(next);

        return next;
    }

    public String toString() {
        return data.toString();
    }

    public Event removeTimer(int entity) {
        int timerIndex = -1;
        Event timer = null;

        for (int i = 0; i < data.size(); i++) {
            if ((((Event) (data.elementAt(i))).getType() == NetworkSimulator.TIMERINTERRUPT) &&
                    (((Event) (data.elementAt(i))).getEntity() == entity)) {
                timerIndex = i;
                break;
            }
        }

        if (timerIndex != -1) {
            timer = (Event) (data.elementAt(timerIndex));
            data.removeElement(timer);
        }

        return timer;

    }

    public double getLastPacketTime(int entityTo) {
        double time = 0.0;
        for (int i = 0; i < data.size(); i++) {
            if ((((Event) (data.elementAt(i))).getType() == NetworkSimulator.FROMNETWORK) &&
                    (((Event) (data.elementAt(i))).getEntity() == entityTo)) {
                time = ((Event) (data.elementAt(i))).getTime();
            }
        }

        return time;
    }
}
