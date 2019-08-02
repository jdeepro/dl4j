package cn.edu.njupt.datatype;

public class Array {

    public static void define() {
        // 定义的时候初始化
        int[] numbers1 = {1,4,5,7,2};
        int[] numbers2 = numbers1; // 与number1共享数据

        numbers2[0] = 11;

        int[] number3 = new int[]{3,5,6,1}; // 显式创建数组并初始化

        int[] number4; // 定以后初始化
        number4 = new int[]{4,7,1,6};

        int number5[] = number4; // 中括号写后面不建议！

        // length是数组长度
        System.out.println("numbers[0]=" + numbers1[0]); // 输出11
        System.out.println(numbers1.length); // 长度
    }

    public static void main(String[] args) {
        define();
    }
}
