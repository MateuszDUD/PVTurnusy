package pv.alg.lp;

import java.util.List;
import java.util.Map;
import pv.alg.bean.Spoj2;
import pv.alg.others.NodesConnectionsCreator;
import pv.bean.Spoj;

/**
 * Abstraktna trieda ktorej dedenim sa vytvaraju konkretne lp modely.
 * @author Ja
 */
public abstract class LpModelWriter {

    protected final int max_c = 400;
    protected final String lp_model;
    protected final Map<Integer, Map<Integer, Integer>> distances;
    protected final List<Spoj> spojeSimple;
    protected final List<Spoj2> spoje2;

    /**
     * Konstruktor.
     * @param distances matica vzdialenosti
     * @param spojeSimple zoznam spojov
     * @param filename nazov suboru
     */
    public LpModelWriter(Map<Integer, Map<Integer, Integer>> distances, List<Spoj> spojeSimple, String filename) {
        this.lp_model = "src/main/resources/lp/" + filename;
        this.distances = distances;
        this.spojeSimple = spojeSimple;
        this.spoje2 = NodesConnectionsCreator.createPossibleConnections(spojeSimple, distances);
    }

    /**
     * Metoda vytvori a zapise lp model.
     */
    public abstract void createModel();

    public String getLp_model() {
        return lp_model;
    }

}
