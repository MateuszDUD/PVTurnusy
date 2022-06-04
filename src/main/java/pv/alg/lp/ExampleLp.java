package pv.alg.lp;

public class ExampleLp {

    private final static String LP_MODEL1 = "src/main/resources/lp/exampleModel.txt";

    public static void solveExample1() {
        LpSolver ls = new LpSolver();

        ls.solve(LP_MODEL1);
    }
}
