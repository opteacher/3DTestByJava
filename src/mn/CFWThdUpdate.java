package mn;

import eg.CFWScene;
import eg.IFWCamera;
import eg.IFWDevice;
import eg.CFWSysRender;

public class CFWThdUpdate extends Thread {
	private CFWScene m_sceDisp;
	private IFWDevice m_devCur;
	private IFWCamera m_camCur;
	
	static private CFWThdUpdate s_Instance = null;
	static public CFWThdUpdate getInstance()	{
		if(null == s_Instance)	{
			System.out.println("the update thread hasn't ready for rendering");
			return null;
		}
		return(s_Instance);
	}
	static public CFWThdUpdate getInstance(IFWDevice devCurI)	{
		if(null == s_Instance)	{
			s_Instance = new CFWThdUpdate(devCurI);
		}
		return(s_Instance);
	}
	
	private CFWThdUpdate(IFWDevice devCurI)	{
		m_devCur = devCurI;
	}
	
	public void setCamera(IFWCamera camI)	{
		m_camCur = camI;
		CFWSysRender.getInstance( m_devCur, m_camCur);
	}
	
	public void setScene(CFWScene sceI)	{
		m_sceDisp = sceI;
	}
	
	public void run()	{
		while(true)	{
			//1.data check
			if(null == m_sceDisp || null == m_camCur)	{
				System.out.println("no Scene for displaying, continue");
				continue;
			}
			if(null == m_devCur)	{
				System.out.println("no device for use");
				break;
			}
			
			synchronized(this)	{
				//2.update
				CFWSysRender.getInstance().update(m_sceDisp);
			}
		}
	}
}
