import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    // constructor takes a WordNet object
    private final WordNet wordnet;
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        int[] ds = new int[nouns.length];
        for (int i = 0; i < ds.length; i++) {
            int di = 0;
            for (String noun : nouns)
                if (!noun.equals(nouns[i])) {
                    int dist = wordnet.distance(nouns[i], noun);
                    di += dist == -1 ? Integer.MAX_VALUE : dist;
                }
            ds[i] = di;
        }

        // TODO перенести в верхний цикл!
        int maxIdx = -1;
        int maxValue = -1;
        for (int i = 0; i < ds.length; i++) {
            if (ds[i] > maxValue) {
                maxIdx = i;
                maxValue = ds[i];
            }
        }

        return nouns[maxIdx];
    }

    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = args.length == 2 ?
                new WordNet(args[0], args[1]) :
                new WordNet("synsets.txt", "hypernyms.txt");

        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}