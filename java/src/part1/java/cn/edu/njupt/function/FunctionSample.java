package cn.edu.njupt.function;

public class FunctionSample {
    static int power(int n, int exp) {
        int b = n;
        while (0 < --exp) {
            b *= n;
        }
        return b;
    }

    static boolean isPrime(int N) {
        int i = 2;
        int top = (int) Math.sqrt(N);

        while (i <= top) {
            if (N % i == 0) {
                return false;
            }
            i++;
        }
        return true;
    }

    public static void main(String[] args) {
        for (int i = 10; i < 30; i++) {
            System.out.printf("isPrime(%d)=%b\n", i, isPrime(i));
        }
    }
}
