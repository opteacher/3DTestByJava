package eg;

public class CFWLine2D implements IFWLineEqu {
	protected int m_iTypeFun;
	protected boolean m_bEnableEquals;
	
	protected float m_fA;
	protected float m_fB;
	protected float m_fD;
	
	//m_fA*x + m_fB*y = m_fD
	public CFWLine2D( CFWPoint poiAI, CFWPoint poiBI) throws Exception	{
		m_iTypeFun = s_iEqual;
		m_bEnableEquals = true;
		if(poiAI.m_fZ == 0.0f && poiBI.m_fZ == 0)	{
			if(CFWMath.equals( poiBI.m_fX, poiAI.m_fX))	{
				m_fA = 1.0f;
				m_fB = 0.0f;
				m_fD = CFWMath.regulateFloat(poiBI.m_fX);
			}
			else if(CFWMath.equals( poiBI.m_fY, poiAI.m_fY))	{
				m_fA = 0.0f;
				m_fB = 1.0f;
				m_fD = CFWMath.regulateFloat(poiBI.m_fY);
			}
			else	{
				float fDetX = poiBI.m_fX - poiAI.m_fX;
				float fDetY = poiBI.m_fY - poiAI.m_fY;
				m_fA = -fDetY;
				m_fB = fDetX;
				m_fD = fDetX*poiAI.m_fY - fDetY*poiAI.m_fX;
			}
		}
		else	{
			throw new Exception("two points create way should be only used to 2D");
		}
	}
	
	public float getSlope() throws Exception	{
		if(0 == m_fB)	{
			throw new Exception("the slope is zero, error!");
		}
		
		return(-m_fA/m_fB);
	}
	
	public float getInterc() throws Exception	{
		if(0 == m_fB)	{
			throw new Exception("the slope is zero, error!");
		}
		
		return(m_fD/m_fB);
	}
	
	public void setFunType(int iFunTpI)	{
		if(CFWMath.isBetweenTwoNum( iFunTpI, s_iLess, s_iBigger, true))	{
			m_iTypeFun = iFunTpI;
		}
	}
	
	public void enableEquals(boolean bEnableI)	{
		m_bEnableEquals = bEnableI;
	}
	
	public float mkOutY(float fXI)	{
		if(CFWMath.equals( m_fB, 0))	{
			System.out.println("the line vertical to the X axis!");
			return(m_fD/m_fA);
		}
		
		return((m_fD - m_fA*fXI)/m_fB);
	}
	
	public int mkFunType( float fXI, float fYI)	{
		float fSunTmp = m_fA*fXI + m_fB*fYI;
		if(fSunTmp > m_fD)	{
			m_iTypeFun = s_iBigger;
		}
		else if(fSunTmp == m_fD)	{
			m_iTypeFun = s_iEqual;
		}
		else	{
			m_iTypeFun = s_iLess;
		}
		return(m_iTypeFun);
	}
	
	public boolean isFillToFun( float fXI, float fYI, float fZI)	{
		float fValSun = m_fA*fXI + m_fB*fYI;
		switch(m_iTypeFun)	{
		case s_iLess:
			if(m_bEnableEquals)	{
				return(m_fD >= fValSun);
			}
			else	{
				return(m_fD > fValSun);
			}
		case s_iBigger:
			if(m_bEnableEquals)	{
				return(m_fD <= fValSun);
			}
			else	{
				return(m_fD < fValSun);
			}
		}
		return(CFWMath.equals( fValSun, m_fD));
	}
	
	public float calculate( float fXI, float fYI, float fZI)	{
		return(m_fA*fXI + m_fB*fYI);
	}
}
