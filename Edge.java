public class Edge {
    private final Coordinate to;
    private final double cost;

    public Edge(Coordinate to, double cost) {
        this.to = to;
        this.cost = cost;
    }

    public Coordinate getTo() { return to; }
    public double getCost() { return cost; }
}
