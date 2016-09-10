package eg;

import java.util.Vector;

/**
 * +_+:only can proccess convex shape
 * @author zhaojiachend5
 *
 */
public class CFWPolyg2D {
	protected Vector<CFWPoint> m_vecPois;
	
	public void addPoi(CFWPoint poiI)	{
		m_vecPois.add(new CFWPoint(poiI));
	}
	
	public Vector<CFWPoint> getEvePoisInside() throws Exception	{
		//1.data check
		if(0 == m_vecPois.size())	{
			throw new Exception("W:the polygon has no shape!");
		}
		if(1 == m_vecPois.size())	{
			return(m_vecPois);
		}
		Vector<CFWPoint> vecRet = new Vector<CFWPoint>();
		if(2 == m_vecPois.size())	{
			CFWPoint poiBeg = m_vecPois.get(0);
			CFWPoint poiEnd = m_vecPois.get(1);
			CFWVector vecDir = new CFWVector( poiBeg, poiEnd);
			vecDir.nor();
			
			CFWPoint poiTmp = new CFWPoint(poiBeg);
			while(
					((Math.signum(vecDir.m_fX) == 1.0f && poiTmp.m_fX < poiEnd.m_fX)
				||   (Math.signum(vecDir.m_fX) == -1.0f && poiTmp.m_fX > poiEnd.m_fX))
				&&  ((Math.signum(vecDir.m_fY) == 1.0f && poiTmp.m_fY < poiEnd.m_fY)
				||   (Math.signum(vecDir.m_fY) == -1.0f && poiTmp.m_fY > poiEnd.m_fY)))	{
				
				vecRet.add(new CFWPoint(poiTmp));
				poiTmp.m_fX += vecDir.m_fX;
				poiTmp.m_fY += vecDir.m_fY;
			}
			
			return(vecRet);
		}
		
		
		//2.build line function group
		Vector<CFWLine2D> vecLnFun = new Vector<CFWLine2D>();
		CFWPoint poiOther = m_vecPois.get(2);
		for( int i = 0; i < m_vecPois.size() - 1; ++i)	{
			CFWPoint poiBeg = m_vecPois.get(i);
			CFWPoint poiEnd = m_vecPois.get(i + 1);
			
			CFWLine2D lnCur = new CFWLine2D( poiBeg, poiEnd);
			lnCur.mkFunType( poiOther.m_fX, poiOther.m_fY);
			
			vecLnFun.add(lnCur);
		}
		
		//3.designate range
		float fXMin = m_vecPois.get(0).m_fX;
		float fYMin = m_vecPois.get(0).m_fY;
		float fXMax = fXMin;
		float fYMax = fYMin;
		
		for( int i = 0; i < m_vecPois.size(); ++i)	{
			CFWPoint poiCur = m_vecPois.get(i);
			if(poiCur.m_fX < fXMin)	{
				fXMin = poiCur.m_fX;
			}
			else if(poiCur.m_fX > fXMax)	{
				fXMax = poiCur.m_fX;
			}
			else if(poiCur.m_fY < fYMin)	{
				fYMin = poiCur.m_fY;
			}
			else if(poiCur.m_fY > fYMax)	{
				fYMax = poiCur.m_fY;
			}
		}
		
		//4.collection
		CFWPoint poiTmp = new CFWPoint();
		for( poiTmp.m_fX = fXMin; poiTmp.m_fX < fXMax; ++poiTmp.m_fX)	{
			for( poiTmp.m_fY = fYMin; poiTmp.m_fY < fYMax; ++poiTmp.m_fY)	{
				
				boolean bInside = true;
				for( int i = 0; i < vecLnFun.size(); ++i)	{
					if(!vecLnFun.get(i).isFillToFun( poiTmp.m_fX, poiTmp.m_fY, poiTmp.m_fZ))	{
						bInside = false;
						break;
					}
				}
				
				if(bInside)	{
					vecRet.add(new CFWPoint(poiTmp));
				}
			}
		}
		
		return(vecRet);
	}
}
