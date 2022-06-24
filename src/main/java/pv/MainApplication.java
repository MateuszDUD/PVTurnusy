package pv;

import pv.alg.dijkstra.Dijkstra;
import pv.alg.lp.ExampleLp;
import pv.alg.lp.LpSolver;
import pv.alg.lp.LpTurnus;
import pv.alg.lp.LpTurnusMinT;
import pv.bean.Spoj;
import pv.util.DataReader;

import java.util.List;
import java.util.Map;
import pv.alg.lp.LpTurnusMinTGarage;

public class MainApplication {

    public static void main(String[] args) {

//        ExampleLp.solveExample1();
        Dijkstra dijkstra = new Dijkstra();
        Map m = dijkstra.solve(DataReader.readNodes(), DataReader.readEdges());

//        LpTurnus lp = new LpTurnus();
//
//        lp.solve(m, DataReader.readSpoje());
        LpTurnusMinT lp2 = new LpTurnusMinT();

        lp2.solve(m, DataReader.readSpoje());
//        LpTurnusMinTGarage lp3 = new LpTurnusMinTGarage();
//        lp3.solve(m, DataReader.readSpoje(), 470);

    }
}
