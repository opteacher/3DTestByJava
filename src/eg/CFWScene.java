package eg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import eg.IFWDevice.SFWLight;
import eg.IFWMesh.SFWFace;

public class CFWScene {
	public class SFWRndrObj	{
		public IFWMesh m_msh;
		public CFWMatrix m_matTrsf;//the location of scene
		
		public SFWRndrObj(IFWMesh mshI)	{
			m_msh = mshI;
			m_matTrsf = new CFWMatrix();
		}
	}
	
	protected IFWDevice m_devCur;
	protected HashMap<String, SFWRndrObj> m_mpRndrObj;
	protected Vector<String> m_aLights;
	protected CFWMatrix m_matTrsf;
	
	public CFWScene(IFWDevice devI)	{
		m_mpRndrObj = new HashMap<String, SFWRndrObj>();
		m_aLights = new Vector<String>();
		m_matTrsf = new CFWMatrix();
		m_devCur = devI;
	}
	
	public IFWMesh getMshOfRObj(String strNamI)	{
		if(m_mpRndrObj.containsKey(strNamI))	{
			return(m_mpRndrObj.get(strNamI).m_msh);
		}
		
		return null;
	}
	
	public void addRenderObj( String strNamI, IFWMesh mshI)	{
		if(!m_mpRndrObj.containsKey(strNamI))	{
			m_mpRndrObj.put( strNamI, new SFWRndrObj(mshI));
		}
	}
	
	public void addLight(String strNmLgtI, IFWDevice.SFWLight lgtI) throws Exception	{
		//1)data check
		if((0 == lgtI.m_fAttenuation0
		&& 0 == lgtI.m_fAttenuation1
		&& 0 == lgtI.m_fAttenuation2)
		|| 0 == lgtI.m_fRange)	{
			throw new Exception("W:the light has error param, can't be used!");
		}

		if(lgtI.m_iType == IFWDevice.SFWLight.s_iLgtDir)	{
			if(lgtI.m_vecDir.isZeroVec()
			|| lgtI.m_fPhi < 0 || lgtI.m_fPhi >= 90.0f
			|| lgtI.m_fTheta < 0 ||lgtI.m_fTheta >= 90.0f
			|| lgtI.m_fTheta > lgtI.m_fPhi
			|| lgtI.m_fFalloff < 0 || lgtI.m_fFalloff > 1)	{
				throw new Exception("W:the direction light has error param, can't be used!");
			}
		}
		
		//2)add
		CFWPool.getInstance().addObjToPool( strNmLgtI, lgtI);
		m_aLights.add(strNmLgtI);
		m_devCur.setLight(strNmLgtI);
		m_devCur.enableLgt( strNmLgtI, true);
	}
	
	public boolean isEmpty()	{
		return(0 == m_mpRndrObj.size());
	}
	
	public CFWMatrix getMatSceTrsf()	{
		return(m_matTrsf);
	}
	public CFWMatrix getMatMshTrsf(String strNamMshI)	{
		//1.check data
		if(m_mpRndrObj.containsKey(strNamMshI))
			return(new CFWMatrix());
		
		//2.return the matrix
		return(((SFWRndrObj)(m_mpRndrObj.get(strNamMshI))).m_matTrsf);
	}
	
	public class SFWInfoFace	{
		public String m_strMshBelog;
		public Vector<SFWFace> m_aInfoFc;
	}
	
	public Vector<SFWInfoFace> getAllFacesFromSce()	{
		//1.data check
		if(m_mpRndrObj.isEmpty())	{
			System.out.println("there's no render object in the scene");
			return null;
		}
		//2.collect all face, and fill into return set
		Vector<SFWInfoFace> vecRet = new Vector<SFWInfoFace>();
		//traversal the map
		Set<String> colcTmp = m_mpRndrObj.keySet();
		Iterator<String>  iter = colcTmp.iterator();
		while(iter.hasNext())	{
			String strNamTmp = iter.next().toString();
			SFWRndrObj objTmp = (SFWRndrObj)m_mpRndrObj.get(strNamTmp);
			IFWMesh mshTmp = objTmp.m_msh;
			//build the information of each face
			SFWInfoFace infoFcTmp = new SFWInfoFace();
			infoFcTmp.m_strMshBelog = strNamTmp;
			infoFcTmp.m_aInfoFc = mshTmp.getEveFace();
			
			vecRet.addElement(infoFcTmp);
		}
		
		return(vecRet);
	}
	
	public SFWLight getLight(String strNamLgtI)	{
		if(!m_aLights.contains(strNamLgtI))	{
			System.out.println("W:no such light be used in this device");
			return null;
		}
		IFWObject objChg = null;
		try	{
			objChg = CFWPool.getInstance().getObjFmPool(strNamLgtI);
		}
		catch(Exception e)	{
			e.printStackTrace();
			return null;
		}
		if(!objChg.getClass().equals(IFWDevice.SFWLight.class))	{
			System.out.print("W:getted object from pool is not light!");
			return null;
		}
		return((SFWLight)objChg);
	}
	
	public HashMap<String, SFWLight> getAllLights()	{
		
		HashMap<String, SFWLight> mpLgtRet = new HashMap<String, SFWLight>();
		
		for( int i = 0; i < m_aLights.size(); ++i)	{
			IFWObject objTmp = null;
			try	{
				objTmp = CFWPool.getInstance().getObjFmPool(m_aLights.get(i));
				if(!objTmp.getClass().equals(IFWDevice.SFWLight.class))	{
					System.out.print("W:getted object from pool is not light!");
					continue;
				}
			}
			catch(Exception e)	{
				e.printStackTrace();
				continue;
			}
			
			mpLgtRet.put( m_aLights.get(i), (IFWDevice.SFWLight)objTmp);
		}
		
		return(mpLgtRet);
	}
}
