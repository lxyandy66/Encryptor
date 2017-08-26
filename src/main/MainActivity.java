package main;

import java.io.File;
import tool.crypto.Encryptor;
import tool.crypto.HashProcessor;

public class MainActivity {

	private static final String hint = "Usage: [-Algorithm][-Encrypt/Decrypt][-key][-fileName/Information]"
			+ "||[-HashAlgorithm][-fileName/Information]\ne.g. -AES -Encrypt -thisisakey -inputfile.jpg";

	private static void inputProcess(String[] args) throws Exception {
		File f;
		switch (args.length) {
		case 2:
			if (!HashProcessor.isLegalArg(args[0])) {
				System.out.println("Unsupport algorithm.");
				return;
			}
			f = new File(args[1]);
			if (f.exists()) {
				if (f.isDirectory()) {
					System.out.println("Input is a Directory,process as String,maybe take long time...");
					System.out.println(HashProcessor.getHash(args[1], args[0]));
					System.out.println("------Finish-----");
				} else {
					System.out.println("Input is a file,maybe take long time...");
					System.out.println(HashProcessor.getHash(f, args[0]));
					System.out.println("------Finish-----");
				}
			} else {
				System.out.println("Input is String,maybe take long time...");
				System.out.println(HashProcessor.getHash(args[1], args[0]));
				System.out.println("------Finish-----");
			}
			break;
		case 4:
			if (!Encryptor.isLeagallyAlgorithm(args[0])) {
				System.out.println("Unsupport algorithm.");
				return;
			}
			if (!args[1].equals("Encrypt") && !args[1].equals("Decrypt")) {
				System.out.println("Illegal process mode.");
				return;
			}
			f = new File(args[3]);
			if (f.exists()) {
				if (f.isDirectory()) {//是一个路径，按照纯文字进行操作
					System.out.println("Input is a Directory,process as String,maybe take long time...");
					System.out.println(Encryptor.encrypt_Process(args[3], args[2], args[1].equals("Encrypt"),false, args[0]));
					System.out.println("------Finish-----");
				} else {
					System.out.println("Input is a file,maybe take long time...");
					Encryptor.encrypt_Process(f, args[2], args[1].equals("Encrypt"), args[0]);
					System.out.println("------Finish-----");
				}
			} else {
				System.out.println("Input is String,maybe take long time...");
				Encryptor.encrypt_Process(args[3], args[2], args[1].equals("Encrypt"),false, args[0]);
				System.out.println("------Finish-----");
			}
			break;

		default:
			System.out.println(hint);
			break;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Input " + args.length + " Arg(s):  ");
		for (String temp : args)
			System.out.print(temp + "\t");
		System.out.println();
		if (args == null || args.length == 0) {
			Layout_Main gui = new Layout_Main(0, 0, 594, 420);
			gui.setVisible(true);
		}
		try {
			inputProcess(args);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
