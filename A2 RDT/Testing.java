import java.io.*;
import java.lang.Object;

public class Testing
{
    public final static void main(String[] argv)
    {
        NetworkSimulator simulator;
        
        int nsim = Integer.parseInt(argv[0]);
        double loss = Double.parseDouble(argv[1]);
        double corrupt = Double.parseDouble(argv[2]);
        double delay = Double.parseDouble(argv[3]);
        int trace = Integer.parseInt(argv[4]);
        long seed = Long.parseLong(argv[5]);
    
                                   
        System.out.println("******************************************************************************");
        System.out.println("******** Running network simulator with parameters *********");
        System.out.println("******************************************************************************");
        System.out.println("nsim="+nsim);
        System.out.println("loss="+loss);
        System.out.println("corrupt="+corrupt);
        System.out.println("delay="+delay);
        System.out.println("trace="+trace);
        System.out.println("seed="+seed);
         
        simulator = new NetworkSimulator(nsim, loss, corrupt, delay,
                                                trace, seed);
                                                
        simulator.runSimulator();

        System.out.println("******* End of Simulation with parameters *********");
        System.out.println("nsim="+nsim);
        System.out.println("loss="+loss);
        System.out.println("corrupt="+corrupt);
        System.out.println("delay="+delay);
        System.out.println("trace="+trace);
        System.out.println("seed="+seed);
        System.out.println("*********************************************");
        System.out.println("Sent Data ***********************************");
        String dSent = new String(simulator.getSentData());
        System.out.println(dSent);
        System.out.println("*********************************************");
        System.out.println("Received Data *******************************");
        String dReceived = new String(simulator.getReceivedData());
        System.out.println(dReceived);
        System.out.println("Number of Sent and Delivered Messages ******************************");
        System.out.println("Messages Sent     :"+nsim);
        System.out.println("Messages Delivered:"+simulator.getNumberDelivered());
        System.out.println("*********************************************");
        System.out.println("Simulation time:"+String.format("%.0f",simulator.getTime()));
        System.out.println("Last Event time:"+String.format("%.0f",simulator.getLastEventTime()));
        System.out.println("******************************************************************************");
        if (simulator.getNumberDelivered() < nsim){
            System.out.println("Looks like we lost some messages!\n0.0");
        }
        else if (simulator.getNumberDelivered() > nsim){
            System.out.println("Too many messages delivered.\nMake sure to deliver each message only once!\n0.0");
        }
	else{
            System.out.println("Correct number of messages delivered!");
            if (dSent.equals(dReceived)) {
                System.out.println("All data delivered correctly!\n1.0");
            } else {
                System.out.println("Sent and delivered data are NOT the same!\n0.0");
            }
	}
    }
}
