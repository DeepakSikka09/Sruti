package in.ecomexpress.sruti.utils.rx;


import java.util.ArrayList;
import java.util.List;

public class CheckFinal {
    public static final List<String> list;

    static {
        list = new ArrayList<String>();
        list.add("Sonu");
        list.add("Monu");
    }

    void test(String s, List list) {
        merge(s, list);
        System.out.println(s);
        System.out.println(list);
    }

    void merge(String s, List list) {
        s.concat("Sikka");
        list.add("Sikka");
    }

    static void addLong(List list) {
        list.add(123);
    }

    public static void main(String... strings) {
        CheckFinal checkFinal = new CheckFinal();
        // checkFinal.test(new String("Deepak"));

        checkFinal.test("Mannu", list);
        list.add("kumar");
        System.out.println(list);

        if (10 - 0 > 0) {
            System.out.println(true);
        }

        List<String> l = new ArrayList<>();
        l.add("Abc");
        addLong(l);
        System.out.println(l);

    }
}