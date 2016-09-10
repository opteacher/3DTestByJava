package eg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Calendar;

public class CFWProgChk {
	
	static public final String s_strLogNam = "function.log";
	protected String m_strClsFun;
	protected long m_lastTime;
	
	public void funStart(String strClsFunI)	{
		try	{
			Calendar calCur = Calendar.getInstance();
			long lMillis = calCur.getTimeInMillis();
			m_lastTime = lMillis;
			File file = new File(s_strLogNam);
			if(!file.exists())	{
				file.createNewFile();
			}
			FileOutputStream foStrm = new FileOutputStream(file);
			PrintStream pStrm = new PrintStream(foStrm);
			
			pStrm.println('B' + "	"
						+ calCur.get(Calendar.YEAR) + '/'
						+ calCur.get(Calendar.MONTH) + '/'
						+ calCur.get(Calendar.DAY_OF_MONTH) + "	"
						+ calCur.get(Calendar.HOUR) + ':'
						+ calCur.get(Calendar.MINUTE) + ':'
						+ calCur.get(Calendar.SECOND) + "	"
						+ strClsFunI);
			m_strClsFun = strClsFunI;
			foStrm.close();
		}
		catch(FileNotFoundException e)	{
			System.out.println("file can't be found!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("file create error!");
			e.printStackTrace();
		}
	}
	
	public void funEnd()	{
		try	{
			Calendar calCur = Calendar.getInstance();
			long lMillis = calCur.getTimeInMillis();
			long lPassTime = lMillis - m_lastTime;
			File file = new File(s_strLogNam);
			if(!file.exists())	{
				file.createNewFile();
			}
			FileOutputStream foStrm = new FileOutputStream(file, true);
			PrintStream pStrm = new PrintStream(foStrm);
			
			pStrm.println('E' + "	"
					+ calCur.get(Calendar.YEAR) + '/'
					+ calCur.get(Calendar.MONTH) + '/'
					+ calCur.get(Calendar.DAY_OF_MONTH) + "	"
					+ calCur.get(Calendar.HOUR) + ':'
					+ calCur.get(Calendar.MINUTE) + ':'
					+ calCur.get(Calendar.SECOND) + "	"
					+ m_strClsFun + "	"
					+ "counts:" + lPassTime);
			foStrm.close();
		}
		catch(FileNotFoundException e)	{
			System.out.println("file can't be found!");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("file create error!");
			e.printStackTrace();
		}
	}
}
