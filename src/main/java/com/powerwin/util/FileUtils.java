package com.powerwin.util;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * @Porject lando
 * @author JunWu.zhu
 * @date:Apr 17, 2014 3:16:47 PM
 * @version : 1.0
 * @email : icerivercomeon@gmail.com
 * @desciption : 文件工具
 */
public class FileUtils {
	/**
	 * 默认缓冲区大小
	 */
	public static final int BUFFER_1024_SIZE = 1024;

	/**
	 * 读取文件内容:行形式读取
	 * 
	 * @param String
	 *            filePathAndName 完整绝对路径文件名
	 * @return String 返回文本文件的内容
	 */
	public static String readFile(String filePathAndName) throws IOException {
		return readFile(filePathAndName, "utf-8", null, BUFFER_1024_SIZE);
	}

	/**
	 * 读取文件内容
	 * 
	 * @param String
	 *            filePathAndName 完整绝对路径文件名
	 * @param String
	 *            encoding 文件打开编码方式 例如 GBK,UTF-8
	 * @param String
	 *            sep 返回内容分隔符 例如：#，默认为\n;
	 * @param int
	 *            bufLen 缓冲区大小
	 * @return String 返回文件内容
	 */
	public static String readFile(String absPath, String encoding, String sep,
			int bufLen) throws IOException {
		
		// 文件路径是否为空 , 是否存在该文件
		if (null == absPath || absPath.trim().equals("")) {
			return "";
		}
		File file = new File(absPath);
		if (!file.exists()) {
			return "";
		}
		
		// 内容分隔符
		if (sep == null || sep.equals("")) {
			// 默认以行分隔
			sep = "\n";
		}

		// 返回内容对象
		StringBuffer rtnFileContent = new StringBuffer();
		FileInputStream fs = null;
		InputStreamReader isr = null;
		LineNumberReader br = null;
		try {
			// 构造文件流
			fs = new FileInputStream(file);
			// 文件流编码
			if (encoding == null || encoding.trim().equals("")) {
				isr = new InputStreamReader(fs);
			} else {
				isr = new InputStreamReader(fs, encoding.trim());
			}
			// 读取文件
			br = new LineNumberReader(isr, bufLen);
			String data = "";
			while ((data = br.readLine()) != null) {
				// 拼接
				rtnFileContent.append(data).append(sep);
			}
		} catch (IOException e) {
			System.err
					.print(".readFile(String filePathAndName,String encoding, String sep, int bufLen) read file content error !");
			throw e;
		} finally {
			try {
				if (null != br) {
					br.close();
				}
				if (null != isr) {
					isr.close();
				}
				if (null != fs) {
					fs.close();
				}
			} catch (IOException e) {
				System.err
						.print(".readFile(String filePathAndName,String encoding, String sep, int bufLen) Close file stream error !");
				throw e;
			}
		}
		return rtnFileContent.toString();
	}

	/**
	 * 新建一个文件并写入内容
	 * 
	 * @param String
	 *            filePath 文件所属文件夹路径
	 * @param String
	 *            fileName 文件名
	 * @param String
	 *            fileContent 内容
	 * @return boolean 写入文件是否成功
	 * @throws IOException
	 */
	public static boolean createFile(String filePath, String fileName,
			String fileContent) throws IOException {
		return createFile(filePath, fileName, fileContent, BUFFER_1024_SIZE,
				false);
	}

	/**
	 * 新建一个文件并写入内容
	 * 
	 * @param String
	 *            filePath 文件所属文件夹路径
	 * @param String
	 *            fileName 文件名
	 * @param String
	 *            fileContent 内容
	 * @param int
	 *            bufLen 设置缓冲区大小
	 * @param boolean
	 *            isWrite 是否追加写入文件
	 * @return boolean 写入文件是否成功
	 * @throws IOException
	 */
	public static boolean createFile(String filePath, String fileName,
			String fileContent, int bufLen, boolean isWrite) throws IOException {

		// 该文件是否存在，如不存在，则创建该文件
		File file = new File(filePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		// 写入文件是否成功
		boolean flag = false;
		FileWriter fw = null;
		BufferedWriter bw = null;
		try {
			// 构建文件写入流
			fw = new FileWriter(filePath + File.separator + fileName, isWrite);
			// 构建写入流
			bw = new BufferedWriter(fw, bufLen);
			// 写入文件内容
			bw.write(fileContent);
			flag = true;
		} catch (IOException e) {
			System.err.println(".createFile() is error !");
			flag = false;
			throw e;
		} finally {
			if (bw != null) {
				bw.flush();
				bw.close();
			}
			if (fw != null){
				fw.close();
			}
		}

		return flag;
	}
	
	public static void createLockFile(String fileName , String content) throws IOException{
		
		// 该文件是否存在，如不存在，则创建该文件
		File file = new File(fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		
		//对该文件加锁
        FileOutputStream out = null;
        FileChannel fcout = null;
        FileLock  flout = null;
		try {
			out = new FileOutputStream(file);
	        fcout = out.getChannel();
	        flout = fcout.tryLock();
	        
	        out.write(content.getBytes("utf-8"));
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			if(flout != null) {
				flout.release();
			}
			if(fcout != null){
				fcout.close();
			}
			if(out != null){
				out.flush();
				out.close();
			}
		}
	}
	
	/**
	 * 将内容写入文件
	 * @param filePath : 文件路径
	 * @param fileContent : 文件内容
	 * @return
	 * @throws Exception
	 */
	public static void writeFile(String filePath, String fileContent)
			throws Exception {
		// 源文件和新文件路径不可为空
		FileOutputStream fos = new FileOutputStream(filePath); // 构造文件输出流
		if (null != fos) {
			fos.write(fileContent.getBytes());
			fos.flush();
			fos.close();
		}
	}
	
	/***
	 * 追加文件内容
	 * */
	public static void appendFile(String fileName, String content) throws IOException {   
        FileWriter writer = null;  
        try {     
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件     
            writer = new FileWriter(fileName, true);     
            writer.write(content);       
        } catch (IOException e) {     
            throw e;     
        } finally {     
            try {     
                if(writer != null){  
                    writer.close();     
                }  
            } catch (IOException e) {     
            	throw e;   
            }     
        }   
    } 
	
	public static void main(String[] args) {
		try {
			createLockFile("/Users/liuhuiya/Documents/data/test.txt", "tewqfeafaf3vew");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
