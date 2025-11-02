package test;

import java.util.*;
import neuronx.supervised.regression.*;
import neuronx.utils.FileUtils;

public class TestSLR {

    public static void main(String[] args) {

        // === 1️⃣ Load Dataset ===
        String datasetPath = "dataset_SLR.csv"; // Make sure this file exists
        boolean supervised = true;

        System.out.println("📂 Loading dataset from: " + datasetPath);
        List<String[]> data = FileUtils.loadCSV(datasetPath);

        if (data.isEmpty()) {
            System.out.println("❌ No data found! Check path or file format.");
            return;
        }

        // === 2️⃣ Extract X, Y ===
        Map<String, List<double[]>> xy = FileUtils.extractXY(data, supervised);
        List<double[]> X = xy.get("X");
        List<double[]> Y = xy.get("Y");

        // === 3️⃣ Split into train and test ===
        double testSize = 0.3; // 30% test data
        Map<String, List<double[]>> split = FileUtils.trainTestSplit(X, Y, testSize);

        List<double[]> X_train = split.get("X_train");
        List<double[]> X_test = split.get("X_test");
        List<double[]> Y_train = split.get("Y_train");
        List<double[]> Y_test = split.get("Y_test");

        System.out.println("\n✅ Train size: " + X_train.size());
        System.out.println("✅ Test size: " + X_test.size());

        // === 4️⃣ Convert List<double[]> → double[] for regression ===
        double[] xTrainArr = new double[X_train.size()];
        double[] yTrainArr = new double[Y_train.size()];
        double[] xTestArr = new double[X_test.size()];
        double[] yTestArr = new double[Y_test.size()];

        for (int i = 0; i < X_train.size(); i++) xTrainArr[i] = X_train.get(i)[0];
        for (int i = 0; i < Y_train.size(); i++) yTrainArr[i] = Y_train.get(i)[0];
        for (int i = 0; i < X_test.size(); i++) xTestArr[i] = X_test.get(i)[0];
        for (int i = 0; i < Y_test.size(); i++) yTestArr[i] = Y_test.get(i)[0];

        // === 5️⃣ Train Model ===
        SimpleLinearRegression slr = new SimpleLinearRegression();
        slr.fit(xTrainArr, yTrainArr);

        // === 6️⃣ Predict test samples ===
        double[] predictions = slr.predict(xTestArr);

        // === 7️⃣ Print Predictions ===
        System.out.println("\n--- Test Predictions ---");
        for (int i = 0; i < xTestArr.length; i++) {
            System.out.printf("X=%.2f → Predicted=%.2f | Actual=%.2f%n",
                    xTestArr[i], predictions[i], yTestArr[i]);
        }

        // === 8️⃣ Evaluate Model ===
        double mse = slr.meanSquaredError(yTestArr, predictions);
        double r2 = slr.score(yTestArr, predictions);

        System.out.printf("%nMSE: %.4f%n", mse);
        System.out.printf("R² Score: %.4f%n", r2);
    }
}
