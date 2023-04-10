import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.stream.IntStream;

public class SAP {

    private final Digraph dg;
    // TODO drop
//    public String[] noans;
    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        if (G == null)
            throw new IllegalArgumentException();

        dg = new Digraph(G);
    }

    private void validateV(int v) {
        if (v < 0 || v >= dg.V())
            throw new IllegalArgumentException();
    }

    private void validateV(Iterable<Integer> v) {
        if (v == null)
            throw new IllegalArgumentException();
        for (Integer vertex: v)
            if (vertex == null || vertex < 0 || vertex >= dg.V())
                throw new IllegalArgumentException();
    }

    private int[] makeDist(int size) {
        int[] distance = new int[size];
        Arrays.fill(distance, Integer.MAX_VALUE);
        return distance;
    }

    /**
     * @param graph  directed graph
     * @param v      start vertexes for calc the distance
     * @return       distance array with start point v in graph
     */
    private int[] bfs(Digraph graph, Iterable<Integer> v) {
        Queue<Integer> que = new Queue<Integer>();
        boolean[] marked = new boolean[graph.V()];
        int[] distance = makeDist(graph.V());

        for (Integer vertex: v) {
            que.enqueue(vertex);
            distance[vertex] = 0;
            marked[vertex] = true;
        }

        while (!que.isEmpty()) {
            int vertex = que.dequeue();
            for (int parent: graph.adj(vertex))
                if (!marked[parent]) {
                    que.enqueue(parent);

                    distance[parent] = distance[vertex] + 1;
                    marked[parent] = true;
                }
        }
        return distance;
    }

    private class LA {
        private final int length;
        private final int ancestor;
        LA(int length, int ancestor) {
            this.length = length;
            this.ancestor = ancestor;
        }
        public int getLength() {
            return length;
        }
        public int getAncestor() {
            return ancestor;
        }
    }
    private LA findLA(int[] distA, int[] distB) {
        int minValue = Integer.MAX_VALUE;
        int minIndex = -1;

        for (int i = 0; i < Math.min(distA.length, distB.length); i++) {
            int dist = (distA[i] == Integer.MAX_VALUE || distB[i] == Integer.MAX_VALUE) ?
                    Integer.MAX_VALUE : distA[i] + distB[i];
            if (minValue > dist) {
                minValue = dist;
                minIndex = i;
            }
        }

        return new LA(minValue == Integer.MAX_VALUE ? -1 : minValue, minIndex);
    }

    private LA bfsLA(Digraph graph, Iterable<Integer> v, Iterable<Integer> w) {
        // поиск в ширину из вершин v (ищем прям все!)
        // поиск в ширину из вершин w (ищем прям все!)
        // складываем пути и ищем минимальный (первый пойдет)
        return findLA(bfs(graph, v), bfs(graph, w));
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        validateV(v);
        validateV(w);

        if (v == w)
            return 0;

        return bfsLA(dg,
                () -> IntStream.of(new int[] {v}).iterator(),
                () -> IntStream.of(new int[] {w}).iterator()).getLength();
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        validateV(v);
        validateV(w);

        if (v == w)
            return v;

        return bfsLA(dg,
                () -> IntStream.of(new int[] {v}).iterator(),
                () -> IntStream.of(new int[] {w}).iterator()).getAncestor();
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validateV(v);
        validateV(w);

        return bfsLA(dg, v, w).getLength();
    }


    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validateV(v);
        validateV(w);

        return bfsLA(dg, v, w).getAncestor();
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args.length > 0 ? args[0] : "digraph25.txt");
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }

        StdOut.println(sap.ancestor(() -> IntStream.of(new int[] {13, 23, 24}).iterator(),
                () -> IntStream.of(new int[] {6, 16, 17}).iterator()));
    }
}