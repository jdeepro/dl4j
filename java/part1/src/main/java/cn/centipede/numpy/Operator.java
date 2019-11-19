package cn.centipede.numpy;


public class Operator {
    static Object add(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) return (int)left+(int)right;
        else return (double)left+(double)right;
    }

    static Object subtract(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) return (int)left-(int)right;
        else return (double)left-(double)right;
    }

    /** force to double */
    static Double divide(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) return (double)left/(int)right;
        else return (double)left/(double)right;
    }

    static Object multiply(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) return (int)left*(int)right;
        else return (double)left*(double)right;
    }

    static Double exp(Object z) {
        double Z = (double)z;
        return Math.exp(Z);
    }

    static Double log(Object z) {
        double Z = (double)z;
        return Math.log(Z);
    }

    static Double reciprocal(Object z) {
        double Z = (double)z;
        return 1/Z;
    }
}