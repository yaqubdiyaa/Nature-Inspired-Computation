# Title 

* A Comparison of PBIL and PSO on MAX-SAT Problems

## About 

* Although our project is a comparison of PBIL and PSO, for this project, we only implemented the PSO algorithm to to solve the MAX-SAT problems. The program contains three classes. Main.java reads in the user provided arguments and creates the necessary Swarm object to run the program. The Swarm.java creates and initialises the particles in the swarm. It also reads the CNF files and stores the literals. It also contains other functionalities of the algorithm such as creating neighborhoods, finding neighborhood best solution and printing the best fitness percentage at the end of the specified number of iterations. The Particle.java represents one particle in the swarm and contains attributes like position, velocity, neighbors as well as methods to find personal best solution, find best fitness and normalize the outputs. 

## Installation

* Download the following files:
1) Main.java
2) Particle.java
3) Swarm.java

## Usage

You can run the program on command line using the following structure:

* Switch into the directory that has the programs and the input files
* Type javac *.java to compile any changes to the code
* Type: java Main {filename} {iterations} {particles} {topology}
<br> Example: java Main v8385-c21736.cnf, 10, 16, ra </br>

## Authors

* *Souleman Toure*
* *Jigyasa Subedi*
* *Diyaa Yaqub*
