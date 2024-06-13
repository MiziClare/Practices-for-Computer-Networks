import java.io.*;

public class Assignment2 {
    public final static void main(String[] argv) {
        NetworkSimulator simulator;

        int nsim = -1;
        double loss = -1.0;
        double corrupt = -1.0;
        double delay = -1.0;
        int trace = -1;
        long seed = -1;
        String buffer = "";

        BufferedReader stdIn = new BufferedReader(
                new InputStreamReader(System.in));

        System.out.println("Network Simulator v1.1");

        while (nsim < 1) {
            System.out.print("Enter number of messages to simulate (> 0): " +
                    "[10] ");
            try {
                buffer = stdIn.readLine();
            } catch (IOException ioe) {
                System.out.println("IOError reading your input!");
                System.exit(1);
            }

            if (buffer.equals("")) {
                nsim = 10;
            } else {
                try {
                    nsim = Integer.parseInt(buffer);
                } catch (NumberFormatException nfe) {
                    nsim = -1;
                }
            }
        }

        while (loss < 0.0) {
            System.out.print("Enter the packet loss probability (0.0 for no " +
                    "loss): [0.0] ");
            try {
                buffer = stdIn.readLine();
            } catch (IOException ioe) {
                System.out.println("IOError reading your input!");
                System.exit(1);
            }

            if (buffer.equals("")) {
                loss = 0.0;
            } else {
                try {
                    loss = (Double.valueOf(buffer)).doubleValue();
                } catch (NumberFormatException nfe) {
                    loss = -1.0;
                }
            }
        }

        while (corrupt < 0.0) {
            System.out.print("Enter the packet corruption probability (0.0 " +
                    "for no corruption): [0.0] ");
            try {
                buffer = stdIn.readLine();
            } catch (IOException ioe) {
                System.out.println("IOError reading your input!");
                System.exit(1);
            }

            if (buffer.equals("")) {
                corrupt = 0.0;
            } else {
                try {
                    corrupt = (Double.valueOf(buffer)).doubleValue();
                } catch (NumberFormatException nfe) {
                    corrupt = -1.0;
                }
            }
        }

        while (delay <= 0.0) {
            System.out.print("Enter the average time between messages from " +
                    "sender's layer 5 (> 0.0): [10000] ");
            try {
                buffer = stdIn.readLine();
            } catch (IOException ioe) {
                System.out.println("IOError reading your input!");
                System.exit(1);
            }

            if (buffer.equals("")) {
                delay = 10000.0;
            } else {
                try {
                    delay = (Double.valueOf(buffer)).doubleValue();
                } catch (NumberFormatException nfe) {
                    delay = -1.0;
                }
            }
        }

        while (trace < 0) {
            System.out.print("Enter trace level (>= 0): [0] ");
            try {
                buffer = stdIn.readLine();
            } catch (IOException ioe) {
                System.out.println("IOError reading your input!");
                System.exit(1);
            }

            if (buffer.equals("")) {
                trace = 0;
            } else {
                try {
                    trace = Integer.parseInt(buffer);
                } catch (NumberFormatException nfe) {
                    trace = -1;
                }
            }
        }

        while (seed < 1) {
            System.out.print("Enter random seed: [random] ");
            try {
                buffer = stdIn.readLine();
            } catch (IOException ioe) {
                System.out.println("IOError reading your input!");
                System.exit(1);
            }

            if (buffer.equals("")) {
                seed = System.currentTimeMillis() % 10000;
            } else {
                try {
                    seed = (Long.valueOf(buffer)).longValue();
                } catch (NumberFormatException nfe) {
                    seed = -1;
                }
            }
        }

        System.out.println("******************************************************************************");
        System.out.println("******** Running network simulator with parameters *********");
        System.out.println("******************************************************************************");
        System.out.println("nsim=" + nsim);
        System.out.println("loss=" + loss);
        System.out.println("corrupt=" + corrupt);
        System.out.println("delay=" + delay);
        System.out.println("trace=" + trace);
        System.out.println("seed=" + seed);

        simulator = new NetworkSimulator(nsim, loss, corrupt, delay,
                trace, seed);

        simulator.runSimulator();

        System.out.println("******* End of Simulation with parameters *********");
        System.out.println("nsim=" + nsim);
        System.out.println("loss=" + loss);
        System.out.println("corrupt=" + corrupt);
        System.out.println("delay=" + delay);
        System.out.println("trace=" + trace);
        System.out.println("seed=" + seed);
        System.out.println("*********************************************");
        System.out.println("Sent Data ***********************************");
        String dSent = new String(simulator.getSentData());
        System.out.println(dSent);
        System.out.println("*********************************************");
        System.out.println("Received Data *******************************");
        String dReceived = new String(simulator.getReceivedData());
        System.out.println(dReceived);
        System.out.println("Number of Sent and Delivered Messages ******************************");
        System.out.println("Messages Sent     :" + nsim);
        System.out.println("Messages Delivered:" + simulator.getNumberDelivered());
        System.out.println("*********************************************");
        System.out.println("Simulation time:" + simulator.getTime());
        System.out.println("Last Event time:" + simulator.getLastEventTime());
        System.out.println("******************************************************************************");
        if (simulator.getNumberDelivered() < nsim) {
            System.out.println("Looks like we lost some packages!\n0.0");
        } else if (simulator.getNumberDelivered() > nsim) {
            System.out.println("Make sure to deliver each message only once!\n0.0");
        } else {
            System.out.println("Correct number of messages delivered!");
            if (dSent.equals(dReceived)) {
                System.out.println("All data delivered correctly!\n1.0");
            } else {
                System.out.println("Sent and delivered data are NOT the same!\n0.0");
            }
        }

    }
}
