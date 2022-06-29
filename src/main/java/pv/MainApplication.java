package pv;

import java.util.Arrays;
import pv.alg.dijkstra.Dijkstra;
import pv.alg.lp.LpSolver;
import pv.util.DataReader;

import java.util.Map;
import pv.alg.lp.LpModelBus;
import pv.alg.lp.LpModelMinT;
import pv.alg.lp.LpModelMinTGaraz;
import pv.alg.lp.LpModelMinTGaraze;
import pv.alg.lp.LpModelWriter;

public class MainApplication {

    public static void main(String[] args) {

        // vytvori maticu vzdialenosti pomocou Dijkstrovho algoritmu
        Dijkstra dijkstra = new Dijkstra();
        Map m = dijkstra.solve(DataReader.readNodes(), DataReader.readEdges());

        //pre vyriesenie konkretneho modelu je dany podel potrebne odkomentovat
        
        //LpModelWriter writer = new LpModelBus(m, DataReader.readSpoje());
        //LpModelWriter writer = new LpModelMinT(m, DataReader.readSpoje());
        //LpModelWriter writer = new LpModelMinTGaraz(m, DataReader.readSpoje(), 470);
        LpModelWriter writer = new LpModelMinTGaraze(m, DataReader.readSpoje(), Arrays.asList(470, 471));
        writer.createModel();

        LpSolver lp = new LpSolver();
        lp.solve(writer.getLp_model());
    }
}
