package shizm.elasticsearchtest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import shizm.model.Product;

/**
 * Hello world!
 *
 */
public class App {
	static EsTool esTool = new EsTool();

	public static void main(String[] args) throws IOException {
//		esTool.delIndex();
//		esTool.createIndex();
//		addProduct();
//		delProduct();
		 search();
		esTool.close();
	}

	public static void addProduct() throws IOException {
		for (int i = 0, j = 200; i < j; i++) {
			Product p = new Product();
			p.id=Long.getLong(String.valueOf(i));
			p.cName=i + "name" + i ;
			p.cCode=i + "code" + i;
			p.cClassName=i + "cClassName" + i;
			Random ra = new Random();
			p.cAuth="$Test" + (ra.nextInt(10)+1) + "$" + "$Test" + (ra.nextInt(10)+1) + "$";
			p.cTitle=" Test" + (ra.nextInt(10)+1)+ " " + "Test" + (ra.nextInt(10)+1);
			esTool.addDocument(p);
		}

	}

	public static void delProduct() throws IOException {
		esTool.delDocument("AVdMkiF9OvFahRVz3V6R");
	}

	public static void search() throws IOException {
		// List<Product> p=esTool.match("5code","5name");
		// System.out.println(p);
		List<Product> p1 = esTool.query("5code", "5name");
		System.out.println(p1);
	}
}
