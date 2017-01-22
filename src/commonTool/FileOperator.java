package commonTool;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FileOperator {
	Scanner kb_input = new Scanner(System.in);
	final String CREATE_HINT = "input record,split with ','";

	protected void createFile(String fileName) throws Exception {
		byte[] content = new byte[1024];
		try {
			FileWriter fw = new FileWriter(fileName);

			System.out.println(CREATE_HINT);
			do {
				int length = System.in.read(content);
				if (length < 2)// 环境为Mac OS X
					break;
				fw.write(new String(content, 0, length - 1));// 环境为Mac OS X
				fw.write("\n");
			} while (true);
			fw.flush();
			fw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception("Create File File");
		}
	}

	private void contentReader(String content) {
		System.out.println(content);
	}

	protected void readFile(String fileName) {
		try {
			BufferedReader bf = new BufferedReader(new FileReader(fileName));
			String line_content = bf.readLine();
			while (line_content != null) {
				contentReader(line_content);
				line_content = bf.readLine();
			}
			bf.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	protected static byte[] readFileToByte(File fileName) throws IOException {
			FileInputStream fis=new FileInputStream(fileName);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);  
            byte[] b = new byte[1000];  
            int n;  
            while ((n = fis.read(b)) != -1) {  
                bos.write(b, 0, n);  
            }  
            fis.close();  
            bos.close();  
            return bos.toByteArray(); 
	}

	protected boolean searchFile(String fileName, String searchContent) {
		try {
			BufferedReader bf = new BufferedReader(new FileReader(fileName));
			String line_origin = bf.readLine();
			while (line_origin != null) {
				String line_process[] = line_origin.split(",", -1);
				if (line_process[0].trim().equals(searchContent)) {

					for (String temp : line_process) {
						System.out.print(temp + "\t");
					}
					bf.close();
					return true;
				}
				line_origin = bf.readLine();
			}
			bf.close();
			return false;
		} catch (Exception e) {
			return false;
			// TODO: handle exception
		}
	}

	protected void appendFile(String fileName) {
		byte[] content = new byte[1024];
		try {
			File f = new File(fileName);
			FileWriter fw = null;
			if (f.exists()) {
				fw = new FileWriter(fileName, true);
				System.out.println("file will append");
			} else
				fw = new FileWriter(fileName, false);
			System.out.println(CREATE_HINT);
			do {
				int length = System.in.read(content);
				if (length < 2)// 环境为Mac OS X
					break;
				fw.write(new String(content, 0, length - 1));// 环境为Mac OS X
				fw.write("\n");
			} while (true);
			fw.flush();
			fw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void deleteRecord(String record, String fileName) {
		try {
			File fnew = new File(fileName);
			if (!fnew.exists()) {
				System.out.println("file not exist");
				return;
			}
			if (searchFile(fileName, record)) {
				File fold = new File(fileName + ".bak");
				fnew.renameTo(fold);
				FileWriter fw = new FileWriter(fileName);// 要在renameTo后面，在前面居然没报空指针？！
				// byte[] content = new byte[1024];
				// int length=System.in.read(content);
				BufferedReader buf = new BufferedReader(new FileReader(fileName + ".bak"));
				String fromOld = buf.readLine();
				while (fromOld != null) {
					String[] oldContent = fromOld.split(",", -1);
					if (!oldContent[0].equals(record)) {
						fw.write(fromOld);
						fw.write("\r\n");
					}
					fromOld = buf.readLine();
				}
				fw.flush();
				fw.close();
				buf.close();
			} else
				System.out.println("record not exist");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
}
