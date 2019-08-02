package cn.edu.njupt.io;

import java.io.*;
import java.util.stream.Stream;

public class BIO {
    static void testFileOutputStream() {
        File file = new File("/Users/simbaba/Desktop/012345.txt");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(new byte[]{0, 1, 2, 3, 4, 5});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void testByteArrayInputStream() {
        byte[] b = new byte[]{100, 2, 3, 4};
        ByteArrayInputStream bInput = new ByteArrayInputStream(b);
        int n1 = bInput.read();
        System.out.println("n1=" + n1);
        int n2 = bInput.read();
        System.out.println("n2=" + n2);
    }

    static void testStringBufferInputstream() {
        StringBufferInputStream bInput = new StringBufferInputStream("123456");
        int n1 = bInput.read() - '0';
        System.out.println("n1=" + n1);
        char n2 = (char) bInput.read();
        System.out.println("n2=" + n2);
    }

    static class Student implements Serializable{
        static final long serialVersionUID = 1L;
        int age;
        String name;
        transient boolean sex;
        Student(int age, String name){
            this.age = age;
            this.name = name;
            this.sex = true;
        }
    }

    static void testObjectInputStream() throws IOException, ClassNotFoundException {
        Student student = new Student(10, "jim");

        ByteArrayOutputStream bai = new ByteArrayOutputStream(100);
        ObjectOutputStream oos = new ObjectOutputStream(bai);
        oos.writeObject(student);
        oos.close();
        byte[] dst = bai.toByteArray();

        ByteArrayInputStream bais = new ByteArrayInputStream(dst);
        ObjectInputStream bInput = new ObjectInputStream(bais);
        Student object = (Student) bInput.readObject();
        System.out.printf("name=%s, age=%d", object.name, object.age);
    }

    static void testSequenceInputStream() throws IOException {
        byte[] b1 = new byte[]{1, 2, 3, 4};
        ByteArrayInputStream bis1 = new ByteArrayInputStream(b1);
        byte[] b2 = new byte[]{10, 20, 30, 40};
        ByteArrayInputStream bis2 = new ByteArrayInputStream(b2);

        SequenceInputStream sis = new SequenceInputStream(bis1, bis2);
        System.out.println("a = " + sis.available());

        int b;
        while ((b=sis.read()) != -1) {
            System.out.println(b);
        }

    }

    static void testObjectInputStream2() throws IOException, ClassNotFoundException {
//        Student student = new Student(10, "jim");

//        FileOutputStream fos = new FileOutputStream("/Users/simbaba/Desktop/student.txt");
//        ObjectOutputStream oos = new ObjectOutputStream(fos);
//        oos.writeObject(student);
//        oos.close();

        FileInputStream fis = new FileInputStream("/Users/simbaba/Desktop/student.txt");
        ObjectInputStream bInput = new ObjectInputStream(fis);
        Student object = (Student) bInput.readObject();
        System.out.printf("name=%s, age=%d, sex=%b", object.name, object.age, object.sex);
    }


    static void testWriter() throws IOException {
        FileWriter fw = new FileWriter("/Users/simbaba/Desktop/student.txt");
        fw.write("张三，10岁");
        fw.close();

        FileReader fr = new FileReader("/Users/simbaba/Desktop/student.txt");
        BufferedReader br = new BufferedReader(fr);
        String text = br.readLine();
        System.out.println(text);
        fr.close();
    }
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        testWriter();
    }
}