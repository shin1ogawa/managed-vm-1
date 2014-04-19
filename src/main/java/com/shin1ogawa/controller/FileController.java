package com.shin1ogawa.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.slim3.controller.Controller;
import org.slim3.controller.Navigation;
import org.slim3.util.StringUtil;

/**
 * @author shin1ogawa
 */
public class FileController extends Controller {

	static final Logger logger = Logger.getLogger(FileController.class.getName());


	@Override
	protected Navigation run() throws Exception {
		String nowString = new Date().toString();

		String folder = request.getParameter("folder");
		if (StringUtil.isEmpty(folder)) {
			folder = "/home/vm-runtime-app/";
		}

		String file = request.getParameter("folder");
		if (StringUtil.isEmpty(file)) {
			file = "/.managed-vm-test.txt";
		}

		File file_ = new File(folder + file);
		try (PrintWriter writerForFile = new PrintWriter(new FileOutputStream(file_))) {
			writerForFile.println(nowString);
			writerForFile.flush();
		} catch (Exception e) {
			logger.log(Level.WARNING, "fail to write file:" + file_, e);
		}

		return null;
	}
}
