package cn.edu.njupt.nd4j;

/**
 * 线性回归
 * BGD（Batch Gradient Descent）
 * h(x) = theta_0 * x_0 + theta_1* x_1 + theta_2 * x_2
 * @author simbaba
 */
public class LinearRegression {
    public LinearRegression(double[][] data, double alpha, int iteration) {
        ///TODO: 加载数据
    }

    /**
     * default set to be 1
     */
    private void initialize_theta() {
        ///TODO: 初始化
    }

    public void train() {
        ///TODO: 训练
    }

    private double[] partial_derivative() {
        return null;
    }

    private double partial_derivative_of_theta(int j) {
        ///TODO: 偏导数
        return 0;
    }

    private double h_theta_x_i_minus_y_i_times_x_j_i(int i, int j) {
        ///TODO: 梯度下降
        return 0;
    }
}
