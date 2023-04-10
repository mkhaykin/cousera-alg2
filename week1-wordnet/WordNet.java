import edu.princeton.cs.algs4.LinearProbingHashST;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

public class WordNet {

    private LinearProbingHashST<String, SET<Integer>> words;
    private String[] synset;
    private SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException();

        readSynsets(synsets);       // synsets
        readHypernyms(hypernyms);   // hypernyms
    }

    private void readSynsets(String fileName) {
        In in;
        String[] lines;

        in = new In(fileName);
        lines = in.readAllLines();
        in.close();

        words = new LinearProbingHashST<>();
        synset = new String[lines.length];

        for (String line: lines) {
            String[] str = line.split(",");

            int id = Integer.parseInt(str[0]);
            String[] nouns = str[1].split(" ");

            synset[id] = str[1];

            for (String word: nouns) {
                if (!this.words.contains(word))
                    this.words.put(word, new SET<>());
                this.words.get(word).add(id);
            }
        }
    }
    private void readHypernyms(String fileName) {
        assert (this.synset != null) : "call readSynsets() first";

        In in;
        String[] lines;

        in = new In(fileName);
        lines = in.readAllLines();
        in.close();

        Digraph dg = new Digraph(synset.length);

        for (String line: lines) {
            String[] items = line.split(",");
            int v = Integer.parseInt(items[0]);
            for (int i = 1; i < items.length; i++)
                dg.addEdge(v, Integer.parseInt(items[i]));
        }

        if (hasCycle(dg) || rootCount(dg) != 1)
            throw new IllegalArgumentException();

        sap = new SAP(dg);
    }

    private boolean hasCycle(Digraph graph) {
        DirectedCycle cycle = new DirectedCycle(graph);
        return cycle.hasCycle();
    }

    private int rootCount(Digraph graph) {
        // считаем количество вершин, которые никуда не ведут
        int count = 0;
        for (int i = 0; i < graph.V(); i++)
            if (!graph.adj(i).iterator().hasNext())
                count++;
        return count;
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        ArrayList<String> arr = new ArrayList<>();
        for (String word: words.keys())
            arr.add(word);
        return arr;
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null)
            throw new IllegalArgumentException();

        return words.contains(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException();

        if (!isNoun(nounA) || !isNoun(nounA))
            throw new IllegalArgumentException();

        return sap.length(words.get(nounA), words.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException();

        if (!isNoun(nounA) || !isNoun(nounA))
            throw new IllegalArgumentException();

        int id = sap.ancestor(words.get(nounA), words.get(nounB));
        return (id >= 0 && id < synset.length) ? synset[id] : "";
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wn = new WordNet("synsets.txt", "hypernyms.txt");
        StdOut.println(wn.sap("cat", "dog"));
    }
}
