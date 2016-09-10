package eg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.Map.Entry;

public abstract class CFWRigid {
	protected Vector<CFWPlane> m_aPlnSet;
	
	public CFWRigid()	{
		m_aPlnSet = new Vector<CFWPlane>();
	}
	
	public void insertPln( int iIndI, CFWPlane plnI)	{
		if(!this.isPlnExist(plnI))	{
			m_aPlnSet.add( iIndI, plnI);
		}
	}
	
	public void insertPln(CFWPlane plnI)	{
		if(!this.isPlnExist(plnI))	{
			m_aPlnSet.add(plnI);
		}
	}
	
	protected boolean isPlnExist(CFWPlane plnI)	{
		for( int i = 0; i < m_aPlnSet.size(); ++i)	{
			if(plnI.equals(m_aPlnSet.get(i)))	{
				return true;
			}
		}
		return false;
	}
	
	public void buildSpace(HashMap<Integer, CFWPoint> mpIndPoiI)	{
		Iterator<Entry<Integer, CFWPoint>> iter = mpIndPoiI.entrySet().iterator();
		while(iter.hasNext())	{
			Entry<Integer, CFWPoint> iterCur = iter.next();
			m_aPlnSet.get(iterCur.getKey()).mkFunType(iterCur.getValue());
		}
	}
	
	public boolean isInside(CFWPoint poiI)	{
		for( int i = 0; i < m_aPlnSet.size(); ++i)	{
			if(!m_aPlnSet.get(i).isFillToFun( poiI.m_fX,
											  poiI.m_fY,
											  poiI.m_fZ))	{
				return false;
			}
		}
		
		return true;
	}
}
