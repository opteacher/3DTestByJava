package eg;

import java.awt.Color;

public class CFWSegLn {
	public CFWPoint m_poiBeg;
	public CFWPoint m_poiEnd;
	
	public CFWSegLn( CFWPoint poiBegI, CFWPoint poiEndI)	{
		m_poiBeg = poiBegI;
		m_poiEnd = poiEndI;
	}
	
	public CFWVector getDirVector()	{
		return(new CFWVector( m_poiBeg, m_poiEnd));
	}
	
	public int getMixColorFromSegLn( CFWPoint poiOnLnI, Color colBegI, Color colEndI)	{
		
		int iColRet = 0;
		CFWPoint poiValTmp = m_poiEnd.sub(m_poiBeg);
		if(0 == poiValTmp.m_fX && 0 == poiValTmp.m_fY)	{
			iColRet = CFWMath.mixTwoColor( colBegI, colEndI).getRGB();
		}
		else if(0 == poiValTmp.m_fX)	{
			int iDisBC = (int)Math.abs(poiValTmp.m_fY);
			int iDisToB = (int)Math.abs(m_poiBeg.m_fY - poiOnLnI.m_fY);
			int iDisToC = (int)Math.abs(m_poiEnd.m_fY - poiOnLnI.m_fY);
			
			iColRet = (iDisToB*colBegI.getRGB() + iDisToC*colEndI.getRGB()) / iDisBC;
		}
		else if(0 == poiValTmp.m_fY)	{
			int iDisBC = (int)Math.abs(poiValTmp.m_fX);
			int iDisToB = (int)Math.abs(m_poiBeg.m_fX - poiOnLnI.m_fX);
			int iDisToC = (int)Math.abs(m_poiEnd.m_fX - poiOnLnI.m_fX);
			
			iColRet = (iDisToB*colBegI.getRGB() + iDisToC*colEndI.getRGB()) / iDisBC;
		}
		else	{
			float fKBC = Math.abs(poiValTmp.m_fY / poiValTmp.m_fX);
			if(fKBC > 1)	{
				iColRet = (int)((poiOnLnI.m_fY - m_poiBeg.m_fY)/poiValTmp.m_fY*colEndI.getRGB()) + 
							(int)((m_poiEnd.m_fY - poiOnLnI.m_fY)/poiValTmp.m_fY*colBegI.getRGB());
			}
			else	{
				iColRet = (int)((poiOnLnI.m_fX - m_poiBeg.m_fX)/poiValTmp.m_fX*colEndI.getRGB()) + 
							(int)((m_poiEnd.m_fX - poiOnLnI.m_fX)/poiValTmp.m_fX*colBegI.getRGB());
			}
		}
		
		return(iColRet);
	}
}
