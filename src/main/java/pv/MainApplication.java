package pv;

import gurobi.GRBEnv;
import gurobi.GRBModel;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.Triple;
import pv.alg.bean.Spoj2;
import pv.alg.dijkstra.Dijkstra;
import pv.alg.gurobimodel.LpTurnusFuzzy;
import pv.alg.gurobimodel.LpTurnusFuzzyHV;
import pv.alg.gurobimodel.ModelMode;
import pv.alg.gurobimodelmintime.LpTurnusFuzzyMinimizeTransitTime;
import pv.alg.lp.*;
import pv.alg.others.NodesConnectionsCreator;
import pv.alg.others.RandomTriangularTimeEnricher;
import pv.bean.Spoj;
import pv.util.DataReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainApplication {

    @SneakyThrows
    public static void main(String[] args) {

        Dijkstra dijkstra = new Dijkstra();
        Map<Integer,Map<Integer, Integer>> m = dijkstra.solve(DataReader.readNodes(), DataReader.readEdges());
        List<Spoj> spojList = DataReader.readSpoje();

        RandomTriangularTimeEnricher en = new RandomTriangularTimeEnricher(1);
        en.enrich(spojList);
        Map<Integer,Map<Integer, Triple<Long, Long, Long>>> mf = en.createFuzzyDistanceMatrix(m);



        // FUZZY min bus model
        LpTurnusFuzzy lp = new LpTurnusFuzzy();
        lp.createModel(NodesConnectionsCreator.createPossibleConnections(spojList, m), mf, ModelMode.PESSIMISTIC);
        lp.solveModel();

        int pessimisticObjValue = (int) lp.getObj_val();

        lp = new LpTurnusFuzzy();
        lp.createModel(NodesConnectionsCreator.createPossibleConnections(spojList, m), mf, ModelMode.OPTIMISTIC);
        lp.solveModel();

        int optimisticObjValue = (int) lp.getObj_val();

        System.out.println("opt model:");
        System.out.println(pessimisticObjValue);
        System.out.println(optimisticObjValue);


        double maxH = 1.0;
        double minH = 0.0;
        double currentH = 0.0;


        List<Double> pslist = new ArrayList<>();
        List<Double> hlist = new ArrayList<>();

        LpTurnusFuzzyHV lph;
        while (true) {
            System.out.println("******************************");
            System.out.println(currentH);
            System.out.println("******************************");

            lph = new LpTurnusFuzzyHV();
            lph.createModel(NodesConnectionsCreator.createPossibleConnections(spojList, m), mf, pessimisticObjValue, optimisticObjValue);
            lph.solveModel(currentH);




            if (!lph.isFeasible()) {
                break;
            }

            hlist.add(currentH);
            pslist.add(883 - lph.getObj_val());

            currentH += 0.05;
//            currentH = Math.round(currentH * 100) / 100;

//            if (lph.isFeasible()) {
//                minH = currentH;
//                currentH = Math.round(((maxH - minH) / 2.0) * 100) / 100.0 + minH;
//            } else {
//                maxH = currentH;
//                currentH = Math.round(((maxH - minH) / 2.0) * 100) / 100.0 + minH;
//            }
//
//            if (currentH == minH || currentH == maxH) {
//                break;
//            }
        }

        for (int i = 0; i < pslist.size(); i++) {
            System.out.println(hlist.get(i) + "   " + pslist.get(i));
        }

        System.out.println(minH);
        System.out.println(maxH);
        System.out.println(currentH);
        System.out.println(lph.getObj_val());
//
//        int optBusCount = 769;
//        optBusCount = 777;
//
//        LpTurnusFuzzyMinimizeTransitTime lp = new LpTurnusFuzzyMinimizeTransitTime();
//        lp.createModel(NodesConnectionsCreator.createPossibleConnections(spojList, m), mf, optBusCount, ModelMode.OPTIMISTIC);
//        lp.solveModel();
//
//        int pessimisticObjValue = (int) lp.getObj_val();
//
////        lp = new LpTurnusFuzzyMinimizeTransitTime();
////        lp.createModel(NodesConnectionsCreator.createPossibleConnections(spojList, m), mf, optBusCount, ModelMode.OPTIMISTIC);
////        lp.solveModel();
//
//        int optimisticObjValue = (int) lp.getObj_val();
//
//        System.out.println(pessimisticObjValue);
//        System.out.println(optimisticObjValue);


    }
}
