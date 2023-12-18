package tests;

public class testRegex {
    public static void main(String[] args) {
        String mutiLineCommentRegex = "/\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/";
        String singleLineCommentRegex = "//.*";
        String javaDocRegex = "/\\*\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/";

        String test1 = "/*\n" +
                       " 123\n" +
                       " 123\n" +
                       " 123\n" +
                       " 123\n" +
                       " */\n" +
                       "// 123123\n" +
                       "/**\n" +
                       " * 123123\n" +
                       " */";
        System.out.println(test1.replaceAll(mutiLineCommentRegex, ""));
        System.out.println(test1.replaceAll(singleLineCommentRegex, ""));
        System.out.println(test1.replaceAll(javaDocRegex, ""));
    }
}
