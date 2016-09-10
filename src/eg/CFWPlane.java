package eg;

public class CFWPlane implements IFWLineEqu {
	protected int m_iTypeFun;
	protected boolean m_bEnableEquals;
	
	protected CFWVector m_vecNor;
	protected float m_fD;
	
	//m_vecNor.m_fX*x + m_vecNor.m_fY*y + m_vecNor.m_fZ*z = m_fD
	public CFWPlane( CFWPoint poiAI, CFWPoint poiBI, CFWPoint poiCI) throws Exception	{
		m_iTypeFun = s_iEqual;
		m_bEnableEquals = true;
		if(poiAI.equals(poiBI) || poiBI.equals(poiCI) || poiCI.equals(poiAI))	{
			throw new Exception("W:three points equals to each other! can't build triangle");
		}
		CFWVector vecAB = new CFWVector( poiAI, poiBI);
		CFWVector vecAC = new CFWVector( poiAI, poiCI);
		vecAB.nor();	vecAC.nor();
		if(vecAB.isZeroVec() || vecAC.isZeroVec()
		|| vecAB.equals(vecAC) || vecAB.getNegVec().equals(vecAC))	{
			throw new IllegalArgumentException("three points equals to each other or in a line!");
		}
		
		CFWVector vecNor = vecAB.cross(vecAC);
		if(!vecNor.isNorVec())	{
			vecNor.nor();
		}
		m_vecNor = new CFWVector();
		m_vecNor.m_fX = vecNor.m_fX;
		m_vecNor.m_fY = vecNor.m_fY;
		m_vecNor.m_fZ = vecNor.m_fZ;
		m_fD = m_vecNor.dot(new CFWVector(poiAI)).sun();
	}

	public CFWPlane( CFWPoint poiI, CFWVector vecNorI)	{
		m_iTypeFun = s_iEqual;
		m_bEnableEquals = true;
		if(!vecNorI.isNorVec())	{
			vecNorI.nor();
		}
		
		m_vecNor = new CFWVector();
		m_vecNor.m_fX = vecNorI.m_fX;
		m_vecNor.m_fY = vecNorI.m_fY;
		m_vecNor.m_fZ = vecNorI.m_fZ;
		m_fD = m_vecNor.dot(new CFWVector(poiI)).sun();
	}
	
	public float getXOfPln( float fYI, float fZI)	{
		return(m_fD - m_vecNor.m_fY*fYI - m_vecNor.m_fZ*fZI);
	}
	
	public float getYOfPln( float fXI, float fZI)	{
		return(m_fD - m_vecNor.m_fX*fXI - m_vecNor.m_fZ*fZI);
	}
	
	public float getZOfPln( float fXI, float fYI)	{
		return(m_fD - m_vecNor.m_fX*fXI - m_vecNor.m_fY*fYI);
	}
	
	public CFWVector getNorVec()	{
		return(m_vecNor);
	}
	
	public float getD()	{
		return(m_fD);
	}
	
	public void setFunType(int iFunTpI)	{
		if(CFWMath.isBetweenTwoNum( iFunTpI, s_iLess, s_iBigger, true))	{
			m_iTypeFun = iFunTpI;
		}
	}
	
	public int mkFunType(CFWPoint poiI)	{
		float fValChk = this.calculate( poiI.m_fX, poiI.m_fY, poiI.m_fZ);
		if(CFWMath.equals( fValChk, m_fD))	{
			m_iTypeFun = s_iEqual;
		}
		else if(fValChk < m_fD)	{
			m_iTypeFun = s_iLess;
		}
		else	{
			m_iTypeFun = s_iBigger;
		}
		return(m_iTypeFun);
	}
	
	public void enableEquals(boolean bEnableI)	{
		m_bEnableEquals = bEnableI;
	}
	
	public boolean isFillToFun( float fXI, float fYI, float fZI)	{
		float fSun = m_vecNor.m_fX*fXI + m_vecNor.m_fY*fYI + m_vecNor.m_fZ*fZI;
		switch(m_iTypeFun)	{
		case s_iLess:
			if(m_bEnableEquals)	{
				return(m_fD >= fSun);
			}
			else	{
				return(m_fD > fSun);
			}
		case s_iBigger:
			if(m_bEnableEquals)	{
				return(m_fD <= fSun);
			}
			else	{
				return(m_fD < fSun);
			}
		}
		return(CFWMath.equals( m_fD, fSun));
	}
	
	public float calculate( float fXI, float fYI, float fZI)	{
		return(m_vecNor.m_fX*fXI + m_vecNor.m_fY*fYI + m_vecNor.m_fZ*fZI);
	}
	
	/**
	 * make out the cross point of the three planes
	 * @param plnAI
	 * @param plnBI
	 * @param plnCI
	 * @return
	 * @throws Exception
	 */
	static public CFWPoint calPoiOfThreePln( CFWPlane plnAI, CFWPlane plnBI, CFWPlane plnCI) throws Exception	{
		if(plnAI.m_vecNor.isZeroVec()
		|| plnBI.m_vecNor.isZeroVec()
		|| plnCI.m_vecNor.isZeroVec())	{
			throw new Exception("E:error plane as the param");
		}
		if(plnAI.m_fD == 0
		&& plnBI.m_fD == 0
		&& plnCI.m_fD == 0)	{
			//return world center
			return(new CFWPoint());
		}
		
		CFWMatrix matTmp = new CFWMatrix(3);
		matTmp.m_fVal[0][0] = plnAI.m_vecNor.m_fX;	matTmp.m_fVal[0][1] = plnAI.m_vecNor.m_fY;	matTmp.m_fVal[0][2] = plnAI.m_vecNor.m_fZ;
		matTmp.m_fVal[1][0] = plnBI.m_vecNor.m_fX;	matTmp.m_fVal[1][1] = plnBI.m_vecNor.m_fY;	matTmp.m_fVal[1][2] = plnBI.m_vecNor.m_fZ;
		matTmp.m_fVal[1][0] = plnCI.m_vecNor.m_fX;	matTmp.m_fVal[1][1] = plnCI.m_vecNor.m_fY;	matTmp.m_fVal[1][2] = plnCI.m_vecNor.m_fZ;
		float fValDn = matTmp.getDetVal();
		
		CFWPoint poiRet = new CFWPoint();
		if(0 == fValDn)	{
			matTmp = new CFWMatrix(2);
			if(plnAI.m_vecNor.m_fX == 0
			&& plnBI.m_vecNor.m_fX == 0
			&& plnCI.m_vecNor.m_fX == 0)	{
				matTmp = new CFWMatrix(2);
				matTmp.m_fVal[0][0] = plnAI.m_vecNor.m_fY;	matTmp.m_fVal[0][1] = plnAI.m_vecNor.m_fZ;
				matTmp.m_fVal[1][0] = plnBI.m_vecNor.m_fY;	matTmp.m_fVal[1][1] = plnBI.m_vecNor.m_fZ;
				fValDn = matTmp.getDetVal();
				
				if(0 == fValDn)	{
					throw new Exception("W:no point of the three planes");
				}
				
				matTmp = new CFWMatrix(2);
				matTmp.m_fVal[0][0] = plnAI.m_fD;	matTmp.m_fVal[0][1] = plnAI.m_vecNor.m_fZ;
				matTmp.m_fVal[1][0] = plnBI.m_fD;	matTmp.m_fVal[1][1] = plnBI.m_vecNor.m_fZ;
				poiRet.m_fY = matTmp.getDetVal()/fValDn;
				
				matTmp = new CFWMatrix(2);
				matTmp.m_fVal[0][0] = plnAI.m_vecNor.m_fY;	matTmp.m_fVal[0][1] = plnAI.m_fD;
				matTmp.m_fVal[1][0] = plnBI.m_vecNor.m_fY;	matTmp.m_fVal[1][1] = plnBI.m_fD;
				poiRet.m_fZ = matTmp.getDetVal()/fValDn;
				
				return(poiRet);
			}
			else if(plnAI.m_vecNor.m_fY == 0
				 && plnBI.m_vecNor.m_fY == 0
				 && plnCI.m_vecNor.m_fY == 0)	{
				matTmp = new CFWMatrix(2);
				matTmp.m_fVal[0][0] = plnAI.m_vecNor.m_fX;	matTmp.m_fVal[0][1] = plnAI.m_vecNor.m_fZ;
				matTmp.m_fVal[1][0] = plnBI.m_vecNor.m_fX;	matTmp.m_fVal[1][1] = plnBI.m_vecNor.m_fZ;
				fValDn = matTmp.getDetVal();
				
				if(0 == fValDn)	{
					throw new Exception("W:no point of the three planes");
				}
				
				matTmp = new CFWMatrix(2);
				matTmp.m_fVal[0][0] = plnAI.m_fD;	matTmp.m_fVal[0][1] = plnAI.m_vecNor.m_fZ;
				matTmp.m_fVal[1][0] = plnBI.m_fD;	matTmp.m_fVal[1][1] = plnBI.m_vecNor.m_fZ;
				poiRet.m_fX = matTmp.getDetVal()/fValDn;
				
				matTmp = new CFWMatrix(2);
				matTmp.m_fVal[0][0] = plnAI.m_vecNor.m_fX;	matTmp.m_fVal[0][1] = plnAI.m_fD;
				matTmp.m_fVal[1][0] = plnBI.m_vecNor.m_fX;	matTmp.m_fVal[1][1] = plnBI.m_fD;
				poiRet.m_fZ = matTmp.getDetVal()/fValDn;
				
				return(poiRet);
			}
			else if(plnAI.m_vecNor.m_fZ == 0
				 && plnBI.m_vecNor.m_fZ == 0
				 && plnCI.m_vecNor.m_fZ == 0)	{
				matTmp = new CFWMatrix(2);
				matTmp.m_fVal[0][0] = plnAI.m_vecNor.m_fX;	matTmp.m_fVal[0][1] = plnAI.m_vecNor.m_fY;
				matTmp.m_fVal[1][0] = plnBI.m_vecNor.m_fX;	matTmp.m_fVal[1][1] = plnBI.m_vecNor.m_fY;
				fValDn = matTmp.getDetVal();
				
				if(0 == fValDn)	{
					throw new Exception("W:no point of the three planes");
				}
				
				matTmp = new CFWMatrix(2);
				matTmp.m_fVal[0][0] = plnAI.m_fD;	matTmp.m_fVal[0][1] = plnAI.m_vecNor.m_fY;
				matTmp.m_fVal[1][0] = plnBI.m_fD;	matTmp.m_fVal[1][1] = plnBI.m_vecNor.m_fY;
				poiRet.m_fX = matTmp.getDetVal()/fValDn;
				
				matTmp = new CFWMatrix(2);
				matTmp.m_fVal[0][0] = plnAI.m_vecNor.m_fX;	matTmp.m_fVal[0][1] = plnAI.m_fD;
				matTmp.m_fVal[1][0] = plnBI.m_vecNor.m_fX;	matTmp.m_fVal[1][1] = plnBI.m_fD;
				poiRet.m_fY = matTmp.getDetVal()/fValDn;
				
				return(poiRet);
			}
			else	{
				throw new Exception("E:unknow error, can't calculate cross point");
			}
		}
		
		matTmp = new CFWMatrix(3);
		matTmp.m_fVal[0][0] = plnAI.m_fD;	matTmp.m_fVal[0][1] = plnAI.m_vecNor.m_fY;	matTmp.m_fVal[0][2] = plnAI.m_vecNor.m_fZ;
		matTmp.m_fVal[1][0] = plnBI.m_fD;	matTmp.m_fVal[1][1] = plnBI.m_vecNor.m_fY;	matTmp.m_fVal[1][2] = plnBI.m_vecNor.m_fZ;
		matTmp.m_fVal[1][0] = plnCI.m_fD;	matTmp.m_fVal[1][1] = plnCI.m_vecNor.m_fY;	matTmp.m_fVal[1][2] = plnCI.m_vecNor.m_fZ;
		poiRet.m_fX = matTmp.getDetVal()/fValDn;
	
		matTmp = new CFWMatrix(3);
		matTmp.m_fVal[0][0] = plnAI.m_vecNor.m_fX;	matTmp.m_fVal[0][1] = plnAI.m_fD;	matTmp.m_fVal[0][2] = plnAI.m_vecNor.m_fZ;
		matTmp.m_fVal[1][0] = plnBI.m_vecNor.m_fX;	matTmp.m_fVal[1][1] = plnBI.m_fD;	matTmp.m_fVal[1][2] = plnBI.m_vecNor.m_fZ;
		matTmp.m_fVal[1][0] = plnCI.m_vecNor.m_fX;	matTmp.m_fVal[1][1] = plnCI.m_fD;	matTmp.m_fVal[1][2] = plnCI.m_vecNor.m_fZ;
		poiRet.m_fY = matTmp.getDetVal()/fValDn;
	
		matTmp = new CFWMatrix(3);
		matTmp.m_fVal[0][0] = plnAI.m_vecNor.m_fX;	matTmp.m_fVal[0][1] = plnAI.m_vecNor.m_fY;	matTmp.m_fVal[0][2] = plnAI.m_fD;
		matTmp.m_fVal[1][0] = plnBI.m_vecNor.m_fX;	matTmp.m_fVal[1][1] = plnBI.m_vecNor.m_fY;	matTmp.m_fVal[1][2] = plnBI.m_fD;
		matTmp.m_fVal[1][0] = plnCI.m_vecNor.m_fX;	matTmp.m_fVal[1][1] = plnCI.m_vecNor.m_fY;	matTmp.m_fVal[1][2] = plnCI.m_fD;
		poiRet.m_fZ = matTmp.getDetVal()/fValDn;
		
		return(poiRet);
	}
	
	public boolean equals(CFWPlane plnI)	{
		return(this.m_vecNor.equals(plnI.m_vecNor)
			&&  this.m_fD == plnI.m_fD
			&&  this.m_iTypeFun == plnI.m_iTypeFun
			&&  this.m_bEnableEquals == plnI.m_bEnableEquals);
	}
	
	public int tunOtherSide()	{
		if(IFWLineEqu.s_iBigger == m_iTypeFun
		|| IFWLineEqu.s_iLess == m_iTypeFun)	{
			m_iTypeFun *= -1;
		}
		
		return(m_iTypeFun);
	}
}
