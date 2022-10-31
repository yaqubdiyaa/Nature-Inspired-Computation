
/**
Wrapper for the main method that takes parameters from the argument and 
calls on Swarm class and creates a Swarm object to start the program 

@author Souleman Toure, Jigyasa Subedi, and Diyaa Yaqub

*/
public class Main
{
    public static void main (String[] args) {
    	
    	if (args.length != 0) {
			String name = args[0];
			String[] fileName = name.split("-c");
			int variables = Integer.parseInt(fileName[0].substring(1));
			int clauses = Integer.parseInt(fileName[1].substring(0, fileName[1].length() - 4));
			int iterations = Integer.parseInt(args[1]);
			int particles = Integer.parseInt(args[2]);
			String topology = args[3];
			
			Swarm swarm = new Swarm(name, variables, clauses, iterations, particles, topology);

			
    	} else {

			System.out.println("No command line arguments found.");
		}
        
    }
}
