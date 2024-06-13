
/*************************************
 * Filename:  Receiver.java
 *************************************/
import java.util.Random;

public class Receiver extends NetworkHost

{
    /*
     * Predefined Constants (static member variables):
     *
     * int MAXDATASIZE : the maximum size of the Message data and
     * Packet payload
     *
     *
     * Predefined Member Methods:
     *
     * void startTimer(double increment):
     * Starts a timer, which will expire in
     * "increment" time units, causing the interrupt handler to be
     * called. You should only call this in the Sender class.
     * void stopTimer():
     * Stops the timer. You should only call this in the Sender class.
     * void udtSend(Packet p)
     * Puts the packet "p" into the network to arrive at other host
     * void deliverData(String dataSent)
     * Passes "dataSent" up to app layer. You should only call this in the
     * Receiver class.
     * double getTime()
     * Returns the current time in the simulator. Might be useful for
     * debugging.
     * void printEventList()
     * Prints the current event list to stdout. Might be useful for
     * debugging, but probably not.
     *
     *
     * Predefined Classes:
     *
     * Message: Used to encapsulate a message coming from app layer
     * Constructor:
     * Message(String inputData):
     * creates a new Message containing "inputData"
     * Methods:
     * boolean setData(String inputData):
     * sets an existing Message's data to "inputData"
     * returns true on success, false otherwise
     * String getData():
     * returns the data contained in the message
     * Packet: Used to encapsulate a packet
     * Constructors:
     * Packet (Packet p):
     * creates a new Packet, which is a copy of "p"
     * Packet (int seq, int ack, int check, String newPayload)
     * creates a new Packet with a sequence field of "seq", an
     * ack field of "ack", a checksum field of "check", and a
     * payload of "newPayload"
     * Packet (int seq, int ack, int check)
     * chreate a new Packet with a sequence field of "seq", an
     * ack field of "ack", a checksum field of "check", and
     * an empty payload
     * Methods:
     * boolean setSeqnum(int n)
     * sets the Packet's sequence field to "n"
     * returns true on success, false otherwise
     * boolean setAcknum(int n)
     * sets the Packet's ack field to "n"
     * returns true on success, false otherwise
     * boolean setChecksum(int n)
     * sets the Packet's checksum to "n"
     * returns true on success, false otherwise
     * boolean setPayload(String newPayload)
     * sets the Packet's payload to "newPayload"
     * returns true on success, false otherwise
     * int getSeqnum()
     * returns the contents of the Packet's sequence field
     * int getAcknum()
     * returns the contents of the Packet's ack field
     * int getChecksum()
     * returns the checksum of the Packet
     * String getPayload()
     * returns the Packet's payload
     *
     */

    // Add any necessary class variables here. They can hold
    // state information for the receiver.
    // ! Add class variables
    private int expectedSeqNum = 0;

    // Also add any necessary methods (e.g. for checksumming)
    // ! Used to calculate the checksum of a packet
    private int calculateChecksum(Packet p) {
        return p.getSeqnum() + p.getAcknum() + p.getPayload().chars().sum();
    }

    // This is the constructor. Don't touch!!!
    public Receiver(int entityName,
            EventList events,
            double pLoss,
            double pCorrupt,
            int trace,
            Random random) {
        super(entityName, events, pLoss, pCorrupt, trace, random);
    }

    // This routine will be called whenever a packet sent from the sender
    // (i.e. as a result of a udtSend() being done by a Sender procedure)
    // arrives at the receiver. "packet" is the (possibly corrupted) packet
    // sent from the sender.
    protected void Input(Packet packet) {
        // ! Check if the packet is corrupted or not
        int checkSum = calculateChecksum(packet);

        // ! If the packet is not corrupted and the sequence number is the expected one
        if (checkSum == packet.getChecksum() && packet.getSeqnum() == expectedSeqNum) {
            deliverData(packet.getPayload());
            Packet ackPacket = new Packet(0, expectedSeqNum, 0);
            ackPacket.setChecksum(calculateChecksum(ackPacket));
            udtSend(ackPacket);
            expectedSeqNum = 1 - expectedSeqNum; // toggle expected sequence number
        }

    }

    // This routine will be called once, before any of your other receiver-side
    // routines are called. It can be used to do any required
    // initialization (e.g. of member variables you add to control the state
    // of the receiver).

    protected void Init() {
        // ! Initialize the expected sequence number
        expectedSeqNum = 0;
    }

}
