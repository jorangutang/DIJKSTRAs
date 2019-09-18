
/**
 * @author Jesse Smart
 * SMRJES001
 */


import java.util.List;
import java.util.Map;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.StringTokenizer;


    class GraphException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public GraphException( String name ) {
            super( name );
        }
    }


    class Edge {
        public Node dest;   // Second vertex in Edge
        public double cost;   // Edge cost

        public Edge( Node d, double c ){
            dest = d;
            cost = c;
        }
    }

    class Path implements Comparable<Path> {
        public Node     dest;   // w
        public double     cost;   // d(w)

        public Path( Node d, double c ) {
            dest = d;
            cost = c;
        }

        public int compareTo( Path rhs ) {
            double otherCost = rhs.cost;

            if (cost < otherCost){
                return -1;
            }
            else if ( cost > otherCost){
                return 1;
            }
            else return 0;

        }
    }

    class Node {
        public String     name;   // Node 
        public List<Edge> adj;    // Adjacent vertices
        private double     dist;   // Cost
        public Node     prev;   // Previous vertex on shortest path
        public boolean    scratch;// visited variable for
        public boolean    tf;

        public Node( String nm )
        {tf = false; name = nm; adj = new LinkedList<Edge>( ); reset( ); }

        public void reset( )
        { setDist(SimulatorOne.INFINITY); prev = null; scratch = false; }

        public double getDist() {
            return dist;
        }

        public void setDist(double dist) {
            this.dist = dist;
        }
    }

    public class SimulatorOne {
        boolean out = false;
        public static final double INFINITY = Double.MAX_VALUE;
        private Map<String, Node> vertexMap = new HashMap<String, Node>();


        public void addEdge(String sourceName, String destName, double cost) {
            Node v = getVertex(sourceName);
            Node w = getVertex(destName);
            v.adj.add(new Edge(w, cost));
        }

        public void printPath(String destName) {
            Node w = vertexMap.get(destName);
            if (w == null)
                throw new NoSuchElementException("Destination vertex not found");
            else if (w.getDist() == INFINITY)
                System.out.println("cannot be helped");
            else {
                //System.out.print( "(Cost is: " + w.dist + ") " );
                printPath(w);
                System.out.println();
            }
        }

        private Node getVertex(String vertexName) {
            Node v = vertexMap.get(vertexName);
            if (v == null) {
                v = new Node(vertexName);
                vertexMap.put(vertexName, v);
            }
            return v;
        }

        private void printPath(Node dest) {
            if (dest.prev != null) {
                printPath(dest.prev);
                System.out.print(" ");
            }
            System.out.print(dest.name);
        }

        private void clearAll() {
            for (Node v : vertexMap.values())
                v.reset();
        }


        public void dijkstra(String source) throws NullPointerException {
            PriorityQueue<Path> Queue = new PriorityQueue<Path>();

            Node start = vertexMap.get(source);
            if (start == null) {
                System.out.println("cannot be helped");
                out = true;
                //throw new NoSuchElementException("Source not found");
            }
            if (this.out == false){
            clearAll();
            final boolean add = Queue.add(new Path(start, 0));
            assert start != null;
            start.setDist(0);

            int nodesSeen = 0;
            while (!Queue.isEmpty() && nodesSeen < vertexMap.size()) {
                Path vrec = Queue.remove();
                Node v = vrec.dest;
                if (v.scratch != false) // already processed v
                    continue;

                v.scratch = true;
                nodesSeen++;

                for (Edge e : v.adj) {
                    Node w = e.dest;
                    double cvw = e.cost;

                    if (cvw < 0)
                        throw new GraphException("Graph has negative edges");


                    if (w.getDist() > v.getDist() + cvw) {
                        w.setDist(v.getDist() + cvw);
                        w.prev = v;
                        Queue.add(new Path(w, w.getDist()));
                        w.tf = false;
                    } else if (w.getDist() == v.getDist() + cvw) {
                        w.tf = true;

                    }
                }
                }
            }
        }

        public static void main(String[] args) {
            SimulatorOne sim1 = new SimulatorOne();// truck to drop off
            SimulatorOne sim2 = new SimulatorOne();// truck to pick up
            SimulatorOne sim3 = new SimulatorOne();// truck going home

            boolean noCanDo = false;

            Scanner Jim = new Scanner(System.in);//Declare scanner

            String lineOne = Jim.nextLine(); //Get total number of nodes
            int numNodes = Integer.parseInt(lineOne); //change to int

            // Read the edges and insert
            String line;
            for (int i = 0; i < numNodes; i++) //loop through all the destinations and distances
            {
                line = Jim.nextLine();
                StringTokenizer st = new StringTokenizer(line); // Splits line by spaces

                String source = st.nextToken();

                while (st.countTokens() != 0) {  // Add nodes to the Graphs 
                    String dest = st.nextToken(); // get destination node
                    int cost = Integer.parseInt(st.nextToken()); // get the cost of the destination
                    sim1.addEdge(source, dest, cost); // add the edge to the graphs
                    sim2.addEdge(source, dest, cost);
                    sim3.addEdge(source, dest, cost);
                }
            }

            int numDrivers = Integer.parseInt(Jim.nextLine()); //change to int
            String DriverHomes = Jim.nextLine(); //Keeps the line containing info on their homes


            //String NumRequests = Jim.nextLine(); //take in number of NumRequests
            int numReq = Integer.parseInt(Jim.nextLine()); //Number of deliveries
            String RequestRoutes = Jim.nextLine();//take in the routes

            StringTokenizer routes = new StringTokenizer(RequestRoutes);

            System.out.println();
            String[] sources = new String[numReq];
            String[] destinations = new String[numReq];

            // iterate over the requests
            //stores the cost of all three sections for all drivers
            // work out cheapest cost
            for (int j = 0; j < numReq; j++) { // loop through shop node numbers line

                double pickup = 0;
                double deliver = 0;
                double homecost = 0;
                String source = routes.nextToken();
                sources[j] = source;
                String destination = routes.nextToken();
                destinations[j] = destination;

                System.out.println("client " + source + " " + destination);
                if (source == null) {
                    System.out.println("cannot be helped 5");
                    break;
                }

                // Truck's
                StringTokenizer Homes = new StringTokenizer(DriverHomes);

                double lowest = 99999; // Declaring the lowest Truck
                String driverNum = "";
                String destName = "";
                boolean multi = false;

                for (int i = 0; i < numDrivers; i++) { // loop through shop node numbers line
                    destName = Homes.nextToken(); // where they come from and where they need to be going
                    String Driverhome = destName;

                    sim2.dijkstra(Driverhome); // works out the cheapest paths to all nodes from the driver home
                    if (sim2.out){
                        continue;
                    }
                    Node pickUpNode = sim2.getVertex(source); // node of pickup
                    pickup = pickUpNode.getDist(); //pick Up Cost


                    //now for the return trip
                    sim3.dijkstra(destination);
                    if (sim3.out){
                        continue;
                    }
                    Node endNode = sim3.getVertex(Driverhome);
                    homecost = endNode.getDist();

                    double driverCost = homecost + pickup;

                    if (driverCost < lowest) {
                        lowest = driverCost;
                        driverNum = Driverhome;
                    } else if (driverCost == lowest) { // if more than one are the same it stores the one with the smallest home number
                        multi = true;
                        if (Integer.parseInt(Driverhome) < Integer.parseInt(driverNum)) {
                            driverNum = Driverhome;
                        }
                    }

                    // Printing the Truck and route outside of the for loop
                }


                sim2.dijkstra(driverNum); // cheapest driver to pick up algorithm
                if (sim2.out){
                    continue;
                }

                sim3.dijkstra(destination); // cheapest route home
                if (sim3.out){
                    continue;
                }
                sim1.dijkstra(source); // find the cheapest delivery route
                if (sim1.out){
                    continue;
                }
                if (!(sim1.out == true || sim2.out == true || sim3.out == true)) {
                    System.out.println("truck " + driverNum);
                    Node sourceNode = sim2.getVertex(source);
                    if (sourceNode.tf == true) {
                        System.out.println("multiple solutions cost " + (int) sourceNode.getDist());
                    } else {
                        sim2.printPath(source); //prints from driver home to source of pickup
                    }

                    Node DropNode = sim1.getVertex(destination);
                    int dropcost = (int) DropNode.getDist();
                    System.out.println("pickup " + source);
                    if (DropNode.tf == true) {
                        System.out.println("multiple solutions cost " + dropcost);
                    } else {
                        sim1.printPath(destination);
                    }

                    System.out.println("dropoff " + destination);
                    Node destNode = sim3.getVertex(destination);
                    if (destNode.tf == true) {
                        System.out.println("multiple solutions cost " + (int) destNode.getDist());
                    } else if (sim3.out == false) {
                        sim3.printPath(driverNum);
                    }
                    //System.out.println(sim3.out);
                }
            }
        }
    }
