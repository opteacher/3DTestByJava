package eg;

import java.util.Vector;

public class CFWTriangle extends CFWPlane {
	static public final int s_iPlnXY = 0;
	static public final int s_iPlnYZ = 1;
	static public final int s_iPlnXZ = 2;
	protected CFWPoint m_poiA;
	protected CFWPoint m_poiB;
	protected CFWPoint m_poiC;
	protected int m_iProjablePln;//identify which plane can be projected
	
	public CFWTriangle( CFWPoint poiAI, CFWPoint poiBI, CFWPoint poiCI) throws Exception	{
		super( poiAI, poiBI, poiCI);
		//1.data check
		if(poiAI.equals(poiBI) || poiBI.equals(poiCI) || poiCI.equals(poiAI))	{
			throw new Exception("E:the three points equals to each other, can't compose to a triangle!");
		}
		CFWVector vecAB = new CFWVector( poiAI, poiBI);	vecAB.nor();
		CFWVector vecAC = new CFWVector( poiAI, poiCI);	vecAC.nor();
		if(vecAB.equals(vecAC) || vecAB.getNegVec().equals(vecAC))	{
			throw new Exception("E:three points in a line, can't compose to a triangle!");
		}
		
		m_poiA = poiAI;	m_poiB = poiBI;	m_poiC = poiCI;
		if(m_vecNor.isSameDir_VerticalIn(new CFWVector( 0.0f, 0.0f, 1.0f))
		|| m_vecNor.isSameDir_VerticalIn(new CFWVector( 0.0f, 0.0f, -1.0f)))	{
			m_iProjablePln = s_iPlnXY;
		}
		else if(m_vecNor.isSameDir_VerticalIn(new CFWVector( 1.0f, 0.0f, 0.0f))
			 || m_vecNor.isSameDir_VerticalIn(new CFWVector( -1.0f, 0.0f, 0.0f)))	{
			m_iProjablePln = s_iPlnYZ;
		}
		else if(m_vecNor.isSameDir_VerticalIn(new CFWVector( 0.0f, 1.0f, 0.0f))
		 	 || m_vecNor.isSameDir_VerticalIn(new CFWVector( 0.0f, -1.0f, 0.0f)))	{
			m_iProjablePln = s_iPlnXZ;
		}
		else	{
			throw new Exception("E:three points in a point, can't compose to a triangle!");
		}
	}
	
	public CFWTriangle( CFWPoint poiAI, CFWPoint poiBI, CFWPoint poiCI, int iProjablePlnI) throws Exception	{
		super( poiAI, poiBI, poiCI);
		//1.data check
		if(poiAI.equals(poiBI) || poiBI.equals(poiCI) || poiCI.equals(poiAI))	{
			throw new Exception("E:the three points equals to each other, can't compose to a triangle!");
		}
		CFWVector vecAB = new CFWVector( poiAI, poiBI);	vecAB.nor();
		CFWVector vecAC = new CFWVector( poiAI, poiCI);	vecAC.nor();
		if(vecAB.equals(vecAC) || vecAB.getNegVec().equals(vecAC))	{
			throw new Exception("E:three points in a line, can't compose to a triangle!");
		}
		
		m_poiA = poiAI;	m_poiB = poiBI;	m_poiC = poiCI;
		
		switch(iProjablePlnI)	{
		case s_iPlnXY:
			if(this.m_vecNor.isVertical(new CFWVector( 0.0f, 0.0f, 1.0f))
			|| this.m_vecNor.isVertical(new CFWVector( 0.0f, 0.0f, -1.0f)))	{
				throw new Exception("W:triangle is Vertical to the identify plane!");
			}
			break;
		case s_iPlnYZ:
			if(this.m_vecNor.isVertical(new CFWVector( 1.0f, 0.0f, 0.0f))
			|| this.m_vecNor.isVertical(new CFWVector( -1.0f, 0.0f, 0.0f)))	{
				throw new Exception("W:triangle is Vertical to the identify plane!");
			}
			break;
		case s_iPlnXZ:
			if(this.m_vecNor.isVertical(new CFWVector( 0.0f, 1.0f, 0.0f))
			|| this.m_vecNor.isVertical(new CFWVector( 0.0f, -1.0f, 0.0f)))	{
				throw new Exception("W:triangle is Vertical to the identify plane!");
			}
			break;
		default:
			throw new Exception("W:identify plane is error");
		}
		
		m_iProjablePln = iProjablePlnI;
	}
	
	public boolean isPoiInside(CFWPoint poiI) throws Exception	{
		//1.check if on the plane
		CFWVector vecAB = new CFWVector( m_poiA, m_poiB);	vecAB.nor();
		CFWVector vecAC = new CFWVector( m_poiA, m_poiC);	vecAC.nor();
		if(vecAB.isZeroVec() || vecAC.isZeroVec()
		|| vecAB.equals(vecAC) || vecAB.getNegVec().equals(vecAC))	{
			throw new Exception("E:the triangle has uninitlized, or data error!");
		}
		//2.points not on the plane of triangle
		if(!this.isFillToFun( poiI.m_fX, poiI.m_fY, poiI.m_fZ))	{
			return false;
		}
		//3.project to each coord plane, and check the point whether inside the triangle
		//XY plane
		CFWPoint poiAOnXY = new CFWPoint( m_poiA.m_fX, m_poiA.m_fY, 0);
		CFWPoint poiBOnXY = new CFWPoint( m_poiB.m_fX, m_poiB.m_fY, 0);
		CFWPoint poiCOnXY = new CFWPoint( m_poiC.m_fX, m_poiC.m_fY, 0);
		//check whether in a line
		CFWVector vecABOnXY = new CFWVector( poiAOnXY, poiBOnXY);	vecABOnXY.nor();
		CFWVector vecACOnXY = new CFWVector( poiAOnXY, poiCOnXY);	vecACOnXY.nor();
		if(vecABOnXY.equals(vecACOnXY) || vecABOnXY.getNegVec().equals(vecACOnXY))	{
			//YZ plane
			CFWPoint poiAOnYZ = new CFWPoint( m_poiA.m_fY, m_poiA.m_fZ, 0);
			CFWPoint poiBOnYZ = new CFWPoint( m_poiB.m_fY, m_poiB.m_fZ, 0);
			CFWPoint poiCOnYZ = new CFWPoint( m_poiC.m_fY, m_poiC.m_fZ, 0);
			
			CFWVector vecABOnYZ = new CFWVector( poiAOnYZ, poiBOnYZ);	vecABOnYZ.nor();
			CFWVector vecACOnYZ = new CFWVector( poiAOnYZ, poiCOnYZ);	vecACOnYZ.nor();
			if(vecABOnYZ.equals(vecACOnYZ) || vecABOnYZ.getNegVec().equals(vecACOnYZ))	{
				//XZ plane
				CFWPoint poiAOnXZ = new CFWPoint( m_poiA.m_fX, m_poiA.m_fZ, 0);
				CFWPoint poiBOnXZ = new CFWPoint( m_poiB.m_fX, m_poiB.m_fZ, 0);
				CFWPoint poiCOnXZ = new CFWPoint( m_poiC.m_fX, m_poiC.m_fZ, 0);
				
				CFWVector vecABOnXZ = new CFWVector( poiAOnXZ, poiBOnXZ);	vecABOnXZ.nor();
				CFWVector vecACOnXZ = new CFWVector( poiAOnXZ, poiCOnXZ);	vecACOnXZ.nor();
				
				if(vecABOnXZ.equals(vecACOnXZ) || vecABOnXZ.getNegVec().equals(vecACOnXZ))	{
					throw new IllegalArgumentException("E:three points in a point!");
				}
				else	{
					CFWLine2D lnABOnXZ = new CFWLine2D( poiAOnXZ, poiBOnXZ);
					CFWLine2D lnBCOnXZ = new CFWLine2D( poiBOnXZ, poiCOnXZ);
					CFWLine2D lnCAOnXZ = new CFWLine2D( poiCOnXZ, poiAOnXZ);
					
					lnABOnXZ.mkFunType( poiCOnXZ.m_fX, poiCOnXZ.m_fZ);
					lnBCOnXZ.mkFunType( poiAOnXZ.m_fX, poiAOnXZ.m_fZ);
					lnCAOnXZ.mkFunType( poiBOnXZ.m_fX, poiBOnXZ.m_fZ);
					
					if(lnABOnXZ.isFillToFun( poiI.m_fX, poiI.m_fZ, 0)
					&& lnBCOnXZ.isFillToFun( poiI.m_fX, poiI.m_fZ, 0)
					&& lnCAOnXZ.isFillToFun( poiI.m_fX, poiI.m_fZ, 0))	{
						return true;
					}
				}
			}
			else	{
				CFWLine2D lnABOnYZ = new CFWLine2D( poiAOnYZ, poiBOnYZ);
				CFWLine2D lnBCOnYZ = new CFWLine2D( poiBOnYZ, poiCOnYZ);
				CFWLine2D lnCAOnYZ = new CFWLine2D( poiCOnYZ, poiAOnYZ);
				
				lnABOnYZ.mkFunType( poiCOnYZ.m_fY, poiCOnYZ.m_fZ);
				lnBCOnYZ.mkFunType( poiAOnYZ.m_fY, poiAOnYZ.m_fZ);
				lnCAOnYZ.mkFunType( poiBOnYZ.m_fY, poiBOnYZ.m_fZ);
				
				if(lnABOnYZ.isFillToFun( poiI.m_fY, poiI.m_fZ, 0)
				&& lnBCOnYZ.isFillToFun( poiI.m_fY, poiI.m_fZ, 0)
				&& lnCAOnYZ.isFillToFun( poiI.m_fY, poiI.m_fZ, 0))	{
					return true;
				}
			}
		}
		else	{
			//compose three line of points ABC, use their function to check the point inside or not
			CFWLine2D lnABOnXY = new CFWLine2D( poiAOnXY, poiBOnXY);
			CFWLine2D lnBCOnXY = new CFWLine2D( poiBOnXY, poiCOnXY);
			CFWLine2D lnCAOnXY = new CFWLine2D( poiCOnXY, poiAOnXY);
			//use another point to check which side is inside the triangle
			lnABOnXY.mkFunType( poiCOnXY.m_fX, poiCOnXY.m_fY);
			lnBCOnXY.mkFunType( poiAOnXY.m_fX, poiAOnXY.m_fY);
			lnCAOnXY.mkFunType( poiBOnXY.m_fX, poiBOnXY.m_fY);
			//check whether the point follow the function
			if(lnABOnXY.isFillToFun( poiI.m_fX, poiI.m_fY, 0)
			&& lnBCOnXY.isFillToFun( poiI.m_fX, poiI.m_fY, 0)
			&& lnCAOnXY.isFillToFun( poiI.m_fX, poiI.m_fY, 0))	{
				return true;
			}
		}
		return false;
	}
	
	public Vector<CFWPoint> getEvePoisInside()	{
		
		CFWPoint poiAProj = null;
		CFWPoint poiBProj = null;
		CFWPoint poiCProj = null;
		
		switch(m_iProjablePln)	{
		case s_iPlnXY:
			poiAProj = new CFWPoint( m_poiA.m_fX, m_poiA.m_fY, 0);
			poiBProj = new CFWPoint( m_poiB.m_fX, m_poiB.m_fY, 0);
			poiCProj = new CFWPoint( m_poiC.m_fX, m_poiC.m_fY, 0);
			break;
		case s_iPlnYZ:
			poiAProj = new CFWPoint( m_poiA.m_fY, m_poiA.m_fZ, 0);
			poiBProj = new CFWPoint( m_poiB.m_fY, m_poiB.m_fZ, 0);
			poiCProj = new CFWPoint( m_poiC.m_fY, m_poiC.m_fZ, 0);
			break;
		case s_iPlnXZ:
			poiAProj = new CFWPoint( m_poiA.m_fX, m_poiA.m_fZ, 0);
			poiBProj = new CFWPoint( m_poiB.m_fX, m_poiB.m_fZ, 0);
			poiCProj = new CFWPoint( m_poiC.m_fX, m_poiC.m_fZ, 0);
			break;
		}
		
			
		CFWLine2D lnAB = null;
		CFWLine2D lnBC = null;
		CFWLine2D lnCA = null;
		try	{
			lnAB = new CFWLine2D( poiAProj, poiBProj);
			lnBC = new CFWLine2D( poiBProj, poiCProj);
			lnCA = new CFWLine2D( poiCProj, poiAProj);
		}
		catch(Exception e)	{
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		
		lnAB.mkFunType( poiCProj.m_fX, poiCProj.m_fY);
		lnBC.mkFunType( poiAProj.m_fX, poiAProj.m_fY);
		lnCA.mkFunType( poiBProj.m_fX, poiBProj.m_fY);
		
		float fMaxI = 0;
		float fMaxJ = 0;
		float fMinI = 0;
		float fMinJ = 0;
		
		switch(m_iProjablePln)	{
		case s_iPlnXY:
			fMaxI = Math.max( m_poiA.m_fX, m_poiB.m_fX);
			fMaxI = Math.max( fMaxI, m_poiC.m_fX);
			fMaxJ = Math.max( m_poiA.m_fY, m_poiB.m_fY);
			fMaxJ = Math.max( fMaxJ, m_poiC.m_fY);
			
			fMinI = Math.min( m_poiA.m_fX, m_poiB.m_fX);
			fMinI = Math.min( fMinI, m_poiC.m_fX);
			fMinJ = Math.min( m_poiA.m_fY, m_poiB.m_fY);
			fMinJ = Math.min( fMinJ, m_poiC.m_fY);
			
			break;
		case s_iPlnYZ:
			fMaxI = Math.max( m_poiA.m_fY, m_poiB.m_fY);
			fMaxI = Math.max( fMaxI, m_poiC.m_fY);
			fMaxJ = Math.max( m_poiA.m_fZ, m_poiB.m_fZ);
			fMaxJ = Math.max( fMaxJ, m_poiC.m_fZ);
			
			fMinI = Math.min( m_poiA.m_fY, m_poiB.m_fY);
			fMinI = Math.min( fMinI, m_poiC.m_fY);
			fMinJ = Math.min( m_poiA.m_fZ, m_poiB.m_fZ);
			fMinJ = Math.min( fMinJ, m_poiC.m_fZ);
			
			break;
		case s_iPlnXZ:
			fMaxI = Math.max( m_poiA.m_fX, m_poiB.m_fX);
			fMaxI = Math.max( fMaxI, m_poiC.m_fX);
			fMaxJ = Math.max( m_poiA.m_fZ, m_poiB.m_fZ);
			fMaxJ = Math.max( fMaxJ, m_poiC.m_fZ);
			
			fMinI = Math.min( m_poiA.m_fX, m_poiB.m_fX);
			fMinI = Math.min( fMinI, m_poiC.m_fX);
			fMinJ = Math.min( m_poiA.m_fZ, m_poiB.m_fZ);
			fMinJ = Math.min( fMinJ, m_poiC.m_fZ);

			break;
		}
		
		Vector<CFWPoint> vecPoisRet = new Vector<CFWPoint>();
		for( float fIndI = fMinI; fIndI < fMaxI; ++fIndI)
			for( float fIndJ = fMinJ; fIndJ < fMaxJ; ++fIndJ)	{
				if(lnAB.isFillToFun( fIndI, fIndJ, 0)
				&& lnBC.isFillToFun( fIndI, fIndJ, 0)
				&& lnCA.isFillToFun( fIndI, fIndJ, 0))	{
					switch(m_iProjablePln)	{
					case s_iPlnXY:
						float fZ = this.getZOfPln( fIndI, fIndJ);
						vecPoisRet.add(new CFWPoint( fIndI, fIndJ, fZ));
						break;
					case s_iPlnYZ:
						float fX = this.getXOfPln( fIndI, fIndJ);
						vecPoisRet.add(new CFWPoint( fX, fIndI, fIndJ));
						break;
					case s_iPlnXZ:
						float fY = this.getYOfPln( fIndI, fIndJ);
						vecPoisRet.add(new CFWPoint( fIndI, fY, fIndJ));
						break;
					}
				}
			}
		return(vecPoisRet);
	}
	
	public CFWPoint getPoiCen() throws Exception	{
		CFWPoint poiABCen = m_poiA.plus(m_poiB).divi(2);
		CFWPoint poiBCCen = m_poiB.plus(m_poiC).divi(2);
		
		CFWVector vecAB = new CFWVector( m_poiA, m_poiB); vecAB.nor();
		CFWVector vecBC = new CFWVector( m_poiB, m_poiC); vecBC.nor();
		CFWVector vecTglNor = this.m_vecNor;
		
		CFWVector vecRayVerToAB = vecAB.cross(vecTglNor);
		if(!vecRayVerToAB.isSameDir_VerticalIn(new CFWVector( poiABCen, m_poiC)))	{
			vecRayVerToAB = vecRayVerToAB.getNegVec();
		}
		vecRayVerToAB.nor();
		
		CFWVector vecRayVerToBC = vecBC.cross(vecTglNor);
		if(!vecRayVerToBC.isSameDir_VerticalIn(new CFWVector( poiBCCen, m_poiA)))	{
			vecRayVerToBC = vecRayVerToBC.getNegVec();
		}
		vecRayVerToBC.nor();
		
		CFWRay rayVerToAB = new CFWRay( poiABCen, vecRayVerToAB);
		CFWRay rayVerToBC = new CFWRay( poiBCCen, vecRayVerToBC);
		
		CFWPoint poiRet = new CFWPoint();
		try	{
			poiRet = rayVerToAB.intersectsRay(rayVerToBC);
		}
		catch(Exception e)	{
			throw new Exception(e.getMessage());
		}
		
		return(poiRet);
	}
	
	/**
	 * get the point of this triangle and equals to another triangle's location
	 * @param poiInTglI 
	 * @param tglI 
	 * @return
	 * @throws Exception
	 */
	public CFWPoint getSameLocFmTgl( CFWPoint poiTarI, CFWTriangle tglI) throws Exception	{
		if(!tglI.isPoiInside(poiTarI))	{
			throw new Exception("W:given point should inside this triangle!");
		}
		
		CFWRay rayAP = new CFWRay( tglI.m_poiA, poiTarI);
		CFWSegLn lnBC = new CFWSegLn( tglI.m_poiB, tglI.m_poiC);
		CFWPoint poiSOnBC = rayAP.intersectsLn(lnBC);
		if(null == poiSOnBC)	{
			throw new Exception("E:ray and segment line has no cross point!");
		}
		// target triangle
		float fLenBS = new CFWVector( tglI.m_poiB, poiSOnBC).getLength();
		float fLenBC = new CFWVector( tglI.m_poiB, tglI.m_poiC).getLength();
		
		float fRateBSToBC = fLenBS/fLenBC;
		
		float fLenAP = new CFWVector( tglI.m_poiA, poiTarI).getLength();
		float fLenAS = new CFWVector( tglI.m_poiA, poiSOnBC).getLength();
		
		float fRateAPToAS = fLenAP/fLenAS;
		// this triangle
		float fLenBCTs = new CFWVector( this.m_poiB, this.m_poiC).getLength();
		CFWVector vecBCTsNor = new CFWVector( this.m_poiB, this.m_poiC).nor();
		CFWPoint poiTmpInTs = this.m_poiB.plus(vecBCTsNor.multi(fLenBCTs*fRateBSToBC));
		
		float fLenASTs = new CFWVector( this.m_poiA, poiTmpInTs).getLength();
		CFWVector vecASTsNor = new CFWVector( this.m_poiA, poiTmpInTs).nor();
		CFWPoint poiTarInTs = this.m_poiA.plus(vecASTsNor.multi(fLenASTs*fRateAPToAS));
		
		return(poiTarInTs);
	}
}
