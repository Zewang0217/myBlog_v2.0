import java.util.Scanner;

/**
 * @author "Zewang"
 * @version 1.0
 * @description: TODO (这里用一句话描述这个类的作用)
 * @email "Zewang0217@outlook.com"
 * @date 2025/09/23 18:40
 */

public class test {

    public static void main(String[] args) {
        int n;
        System.out.println("请输入一个整数： ");
        Scanner scanner = new Scanner(System.in);
        n = scanner.nextInt();

        int num = 1;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(num++ + " ");
            }
            System.out.println();
        }
    }

}
