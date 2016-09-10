package eg;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

public class CFWPool {
	static private CFWPool s_Instance = null;
	static public CFWPool getInstance()	{
		if(null == s_Instance)	{
			s_Instance = new CFWPool();
		}
		return(s_Instance);
	}
	
	private CFWPool()	{
		m_mpPool = new HashMap<String, IFWObject>();
		m_mpNamToHsh = new HashMap<String, String>();
		Vector<Integer> vecNumMap = CFWMath.geneRandValue( 10, 'A', 'Z'+1, false);
		for( int i = 0; i < vecNumMap.size(); ++i)	{
			m_strNumMap += vecNumMap.get(i);
		}
	}
	
	private String m_strNumMap;
	private HashMap<String, IFWObject> m_mpPool;
	private HashMap<String, String> m_mpNamToHsh;
	
	public String addObjToPool(IFWObject objI) throws Exception	{
		if(null == objI)	{
			throw new Exception("W:need be given actival data!");
		}
		
		Set<Map.Entry<String, IFWObject>> setTmp = m_mpPool.entrySet();
		Iterator<Map.Entry<String, IFWObject>> iterSet = setTmp.iterator();
		while(iterSet.hasNext())	{
			Map.Entry<String, IFWObject> iterCur = iterSet.next();
			if(iterCur.getValue().equals(objI))	{
				return(iterCur.getKey());
			}
		}
		
		String strHashId = geneRandObjHash();
		while(m_mpPool.containsKey(strHashId))	{
			strHashId = geneRandObjHash();
		}
		
		m_mpPool.put( strHashId, objI);
		return(strHashId);
	}
	
	public String addObjToPool( String strGivenNamI, IFWObject objI) throws Exception	{
		//1)data check
		if(null == objI)	{
			throw new Exception("W:need be given actival data!");
		}
		//2)generate rand hash
		String strHashId = geneRandObjHash();
		while(m_mpPool.containsKey(strHashId))	{
			strHashId = geneRandObjHash();
		}
		
		//3)add to pool
		m_mpPool.put( strHashId, objI);
		if(!"".equals(strGivenNamI))	{
			m_mpNamToHsh.put( strGivenNamI, strHashId);
		}
		return(strGivenNamI);
	}
	
	private String geneRandObjHash()	{
		long iCurMillTime = System.currentTimeMillis();
		String strHashId = String.valueOf(iCurMillTime);
		Random rdmGener = new Random();
		int iNumChg = rdmGener.nextInt()%5 + 2;
		Vector<Integer> vecChgVal = CFWMath.geneRandValue( iNumChg, 0, 10, false);
		for( int i = 0; i < vecChgVal.size(); ++i)	{
			Integer iIndNumMp = (Integer)vecChgVal.get(i);
			if(iIndNumMp >= 0 && iIndNumMp < m_strNumMap.length())	{
				strHashId.replace( iIndNumMp.toString().charAt(0), m_strNumMap.charAt(iIndNumMp));
			}
		}
		return(strHashId);
	}
	
	public IFWObject getObjFmPool(String strObjI) throws Exception	{
		if(m_mpPool.containsKey(strObjI))	{
			return(m_mpPool.get(strObjI));
		}
		
		if(m_mpNamToHsh.containsKey(strObjI))	{
			return(m_mpPool.get((String)m_mpNamToHsh.get(strObjI)));
		}
		
		throw new Exception("W:no such object in the pool!");
	}
}
