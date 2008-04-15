package org.alfresco.tools;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.servlet.jsp.JspWriter;

public class FakeJspWriter 
	extends JspWriter
{
	private Writer myWriter = null;
	private PrintWriter printWriter = null;

	public FakeJspWriter() {
		super(0, false);
		
		myWriter = new StringWriter();
		printWriter = new PrintWriter(myWriter);
	}

	public FakeJspWriter(Writer myWriter) {
		super(0, false);

		this.myWriter = myWriter;
		printWriter = new PrintWriter(myWriter);
	}
	
	public FakeJspWriter(int arg0, boolean arg1) {
		super(arg0, arg1);

		myWriter = new StringWriter();
		printWriter = new PrintWriter(myWriter);		
	}

	public PrintWriter getMyPrintWriter() {
		return printWriter;
	}

	public Writer getMyWriter() {
		return myWriter;
	}

	@Override
	public void newLine() throws IOException {
		printWriter.println();
	}

	@Override
	public void print(boolean arg0) throws IOException {
		printWriter.print(arg0);
	}

	@Override
	public void print(char arg0) throws IOException {
		printWriter.print(arg0);
	}

	@Override
	public void print(int arg0) throws IOException {
		printWriter.print(arg0);
	}

	@Override
	public void print(long arg0) throws IOException {
		printWriter.print(arg0);
	}

	@Override
	public void print(float arg0) throws IOException {
		printWriter.print(arg0);
	}

	@Override
	public void print(double arg0) throws IOException {
		printWriter.print(arg0);
	}

	@Override
	public void print(char[] arg0) throws IOException {
		printWriter.print(arg0);
	}

	@Override
	public void print(String arg0) throws IOException {
		printWriter.print(arg0);
	}

	@Override
	public void print(Object arg0) throws IOException {
		printWriter.print(arg0);
	}

	@Override
	public void println() throws IOException {
		printWriter.println();
	}

	@Override
	public void println(boolean arg0) throws IOException {
		printWriter.println(arg0);
	}

	@Override
	public void println(char arg0) throws IOException {
		printWriter.println(arg0);
	}

	@Override
	public void println(int arg0) throws IOException {
		printWriter.println(arg0);
	}

	@Override
	public void println(long arg0) throws IOException {
		printWriter.println(arg0);
	}

	@Override
	public void println(float arg0) throws IOException {
		printWriter.println(arg0);
	}

	@Override
	public void println(double arg0) throws IOException {
		printWriter.println(arg0);
	}

	@Override
	public void println(char[] arg0) throws IOException {
		printWriter.println(arg0);
	}

	@Override
	public void println(String arg0) throws IOException {
		printWriter.println(arg0);
	}

	@Override
	public void println(Object arg0) throws IOException {
		printWriter.println(arg0);
	}

	@Override
	public void clear() throws IOException {
	}

	@Override
	public void clearBuffer() throws IOException {
	}

	@Override
	public void flush() throws IOException {
		printWriter.flush();
	}

	@Override
	public void close() throws IOException {
		printWriter.close();
	}

	@Override
	public int getRemaining() {
		return 0;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		printWriter.write(cbuf, off, len);
	}

}