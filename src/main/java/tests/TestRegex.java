package tests;

/**
 * @author qcqcqc
 */
public class TestRegex {
    public static void main(String[] args) {
        String multiLineCommentRegex = "/\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/";
        String singleLineCommentRegex = "//.*\n";
        String javaDocRegex = "/\\*\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/";

        String test1 = """
                /*
                 123
                 123
                 123
                 123
                 */
                // 123123
                /**
                 * 123123
                 */
                 """;
        System.out.println(test1.replaceAll(multiLineCommentRegex, ""));
        System.out.println(test1.replaceAll(singleLineCommentRegex, ""));
        System.out.println(test1.replaceAll(javaDocRegex, ""));
    }
}
