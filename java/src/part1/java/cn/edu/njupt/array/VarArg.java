package cn.edu.njupt.array;

public class VarArg {

    static int sum(int...numbers) {
        System.out.println(numbers);

        for (int i = 1; i < numbers.length; i++) {
            numbers[0] += numbers[i];
        }
        return numbers[0];
    }

    public static void main(String[] args) {
        System.out.println(sum(new int[]{1,2,3,4,5,6}));
        System.out.println(sum(1,2,3,4,5,6));
    }
}
