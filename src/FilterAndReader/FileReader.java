package FilterAndReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FileReader {


	public List<String> fileReader (String s) {

		String stringa;

		List<String> listaStringhe = new ArrayList<String>();

		try {

			File file = new File(s);

			Scanner myReader = new Scanner(file);

			while (myReader.hasNextLine()) {

				stringa = myReader.nextLine();

				listaStringhe.add(stringa);

			}

			myReader.close();

		} catch (FileNotFoundException e) {

			System.out.println("File not found!");

			e.printStackTrace();

		}

		return listaStringhe;

	}

	public String fileReader2 (String s) {

		String stringa = "";

		try {

			File file = new File(s);

			Scanner myReader = new Scanner(file);

			while (myReader.hasNextLine()) {

				stringa = myReader.nextLine();

			}

			myReader.close();

		} catch (FileNotFoundException e) {

			System.out.println("File not found!");

			e.printStackTrace();

		}

		return stringa;

	}

}
