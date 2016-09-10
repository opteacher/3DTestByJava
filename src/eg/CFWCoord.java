package eg;

public class CFWCoord {
	static public final int s_iNumDim = 3;
	static public final int s_iAxisX = 0;
	static public final int s_iAxisY = 1;
	static public final int s_iAxisZ = 2;
	public CFWVector m_vecAxis[];
	public CFWPoint m_poiCenter;
	private boolean m_bLeftH;
	private int m_iNumDim;
	
	public CFWCoord()	{
		m_iNumDim = s_iNumDim;
		m_vecAxis = new CFWVector[m_iNumDim];
		m_vecAxis[0] = new CFWVector( 1.0f, 0.0f, 0.0f);
		m_vecAxis[1] = new CFWVector( 0.0f, 1.0f, 0.0f);
		m_vecAxis[2] = new CFWVector( 0.0f, 0.0f, 1.0f);
		m_poiCenter = new CFWPoint();
		m_bLeftH = false;
	}
	
	/**
	 * use center and the direction of z, camera's creating way
	 * @param poiCtI
	 * @param vecLookAtI
	 */
	public void createCoord( CFWPoint poiCtI, CFWVector vecLookAtI)	{
		m_poiCenter	= poiCtI;
		vecLookAtI.nor();
		//look to the top
		CFWVector vecUp = new CFWVector();
		if(vecLookAtI.m_fY == 1.0f && vecLookAtI.m_fX == 0.0f && vecLookAtI.m_fZ == 0.0f)
			vecUp.m_fZ	= 1.0f;
		else
			vecUp.m_fY	= 1.0f;
		//
		CFWVector vecRight	= vecLookAtI.cross(vecUp);
		m_vecAxis[0] = vecRight;
		m_vecAxis[1] = vecRight.cross(vecLookAtI);
		m_vecAxis[2] = vecLookAtI;
		
		m_bLeftH = true;
	}
	
	/**
	 * use world's direction
	 * @param poiCtI
	 */
	public void createCoord(CFWPoint poiCtI)	{
		m_poiCenter	= poiCtI;
	}
	
	/**
	 * revise the coord
	 */
	public void regulate()	{
		CFWVector vecYTmp = new CFWVector( 0.0f, 1.0f, 0.0f);
		if(1 == Math.abs(m_vecAxis[s_iAxisZ].m_fY))
			vecYTmp = new CFWVector( 1.0f, 0.0f, 0.0f);
		m_vecAxis[s_iAxisX] = vecYTmp.cross(m_vecAxis[s_iAxisZ]);
		m_vecAxis[s_iAxisY] = m_vecAxis[s_iAxisZ].cross(m_vecAxis[s_iAxisX]);
		m_vecAxis[s_iAxisY].nor();
		m_vecAxis[s_iAxisX] = m_vecAxis[s_iAxisY].cross(m_vecAxis[s_iAxisZ]);
		m_vecAxis[s_iAxisX].nor();
	}

	public CFWMatrix transform(CFWCoord codTarI)	{
		//1.check the dimensions
		if(m_iNumDim != codTarI.m_iNumDim)	{
			System.out.println("the number of dimensions");
			return(new CFWMatrix());
		}
		//2.regulate
		regulate();
		codTarI.regulate();
		//3.get the transfer matrix
		CFWMatrix matTrans = CFWMatrix.transfer3D(
				codTarI.m_poiCenter.m_fX - m_poiCenter.m_fX,
				codTarI.m_poiCenter.m_fY - m_poiCenter.m_fY,
				codTarI.m_poiCenter.m_fZ - m_poiCenter.m_fZ);
		//4.get the rotate matrix
		CFWMatrix matRot = new CFWMatrix(m_iNumDim + 1);
		for( int i = m_iNumDim - 1; i > 0 ; --i)   {
			CFWVector vecAxisT = new CFWVector();
			//if different hand mode, exchange first axis
			if(m_bLeftH && !codTarI.m_bLeftH && i == m_iNumDim - 1)	{
				vecAxisT = matRot.multiVecLeft(m_vecAxis[i].getNegVec());
			}
			else	{
				vecAxisT = matRot.multiVecLeft(m_vecAxis[i]);
			}
			CFWVector vecTmp = codTarI.m_vecAxis[i].cross(vecAxisT);
			float fAngTmp = (float)codTarI.m_vecAxis[i].angWithVec(vecAxisT, false);
			CFWMatrix matTmp = CFWMatrix.rotate3D( fAngTmp, vecTmp);
			matRot = matRot.dotRight(matTmp);
		}
		//5.check left mode or right mode
		CFWVector vecLast = matRot.multiVecLeft(m_vecAxis[0]);
		if(!vecLast.isSameDir_VerticalIn(codTarI.m_vecAxis[0]))	{
			CFWMatrix matNeg = new CFWMatrix(m_iNumDim + 1);
			matNeg.m_fVal[0][0] = -1;
			matRot = matRot.dotRight(matNeg);
		}
		
		return(matTrans.dotRight(matRot));
	}

	public CFWMatrix transformToWld3D()	{
		//1.check the dimensions
		if(m_iNumDim != 3)	{
			System.out.println("the number of dimensions isn't 3");
			return(new CFWMatrix());
		}
		//2.get the transfer matrix
		CFWMatrix matTrans = CFWMatrix.transfer3D(
				-m_poiCenter.m_fX,
				-m_poiCenter.m_fY,
				-m_poiCenter.m_fZ);
		//3.get the rotate matrix
		double dLenTmp = Math.sqrt(Math.pow( m_poiCenter.m_fX, 2) + Math.pow( m_poiCenter.m_fY, 2));
		float fCosO = 0;
		if(0 == dLenTmp)	{
			dLenTmp = Math.sqrt(Math.pow( m_vecAxis[s_iAxisZ].m_fX, 2) + Math.pow( m_vecAxis[s_iAxisZ].m_fY, 2));
			if(0 != dLenTmp)	{
				fCosO = m_vecAxis[s_iAxisZ].m_fX/(float)dLenTmp;
			}
		}
		else	{
			fCosO = m_poiCenter.m_fX/(float)dLenTmp;
		}
		float fSinO = CFWMath.getSign(m_poiCenter.m_fY)*(float)Math.sqrt(1 - Math.pow( fCosO, 2));
		CFWVector vecZ = new CFWVector( 0.0f, 0.0f, 1.0f);
		CFWVector vecCt = new CFWVector(m_poiCenter);
		vecCt.nor();
		float fCosP = vecZ.dot(vecCt).sun();
		//@_@:not sure positive or negative
		float fSinP = (float)Math.sqrt(1 - Math.pow( fCosP, 2));
		
		CFWMatrix matRot1 = new CFWMatrix();
		matRot1.m_fVal[0][0] = fSinO;
		matRot1.m_fVal[0][1] = fCosO;
		matRot1.m_fVal[1][0] = -fCosO;
		matRot1.m_fVal[1][1] = fSinO;
		
		CFWMatrix matRot2 = new CFWMatrix();
		matRot2.m_fVal[1][1] = -fCosP;
		matRot2.m_fVal[1][2] = -fSinP;
		matRot2.m_fVal[2][1] = fSinP;
		matRot2.m_fVal[2][2] = -fCosP;
		
		CFWMatrix matRot3 = new CFWMatrix();
		matRot3.m_fVal[0][0] = -1;
		
		matTrans = matTrans.dotRight(matRot1);
		matTrans = matTrans.dotRight(matRot2);
		matTrans = matTrans.dotRight(matRot3);
		
		return(matTrans);
	}
	public void transfromByMat(CFWMatrix matI)	{
		m_poiCenter	= matI.multiPoiLeft(m_poiCenter);
		for( int i = 0; i < m_iNumDim; ++i)
			m_vecAxis[i] = (CFWVector)matI.multiPoiLeft((CFWPoint)m_vecAxis[i]);
	}
	public int getDim()	{
		return(m_iNumDim);
	}
	public void tunLeftHand()	{
		m_bLeftH = true;
	}
	public boolean isLeftHand()	{
		return(m_bLeftH);
	}
	public CFWMatrix transfer( CFWPoint poiTarI, boolean bEffSelfI)	{
		//1.calculate diff of center and target
		CFWPoint poiTmp	= poiTarI.sub(m_poiCenter);
		//2.calculate the transfer matrix
		CFWMatrix matRet = CFWMatrix.transfer3D( poiTmp.m_fX, poiTmp.m_fY, poiTmp.m_fZ);
		//3.if user want effect coord self, do it
		if(bEffSelfI)
			m_poiCenter = matRet.multiPoiLeft(m_poiCenter);

		return(matRet);
	}
	public CFWMatrix transfer( int iIndAxisI, float fOffsetI, boolean bEffSelfI)	{
		//1.check data
		if(iIndAxisI < 0 || iIndAxisI >= m_iNumDim)	{
			System.out.println("the Axis index is error!");
			return(new CFWMatrix());
		}
		//2.calculate transfer offset vector
		m_vecAxis[iIndAxisI].nor();
		CFWVector vecOffset = (CFWVector)m_vecAxis[iIndAxisI].multi(fOffsetI);
		//3.calculate transfer matrix
		CFWMatrix matRet = CFWMatrix.transfer3D( vecOffset.m_fX, vecOffset.m_fY, vecOffset.m_fZ);
		//4.if user want effect coord self, do it
		if(bEffSelfI)
			m_poiCenter	= matRet.multiPoiLeft(m_poiCenter);

		return(matRet);
	}
	public CFWMatrix rotate( int iIndAxisI, float fAngOffI, boolean bEffSelfI)	{
		//1.check data
		if(iIndAxisI < 0 || iIndAxisI >= m_iNumDim)	{
			System.out.println("the Axis index is error!");
			return(new CFWMatrix());
		}
		//2.calculate transfer matrix
		float fAng	= ((float)Math.PI/180.0f) * fAngOffI;
		CFWMatrix matRet = new CFWMatrix(m_iNumDim);
		if(2 == m_iNumDim)
			matRet = CFWMatrix.rotate2D( fAng, m_poiCenter);
		else if(3 == m_iNumDim)
			matRet = CFWMatrix.rotate3D( fAng, m_vecAxis[iIndAxisI]);
		//3.if user want effect coord self, do it
		if(bEffSelfI)	{
			for( int i = 0; i < m_iNumDim; ++i)
				m_vecAxis[i] = (CFWVector)matRet.multiPoiLeft(m_vecAxis[i]);
		}//if bAffi

		return(matRet);
	}
	public CFWMatrix scale( int iIndAxisI, float fSclOffI)	{
		//1.check data
		if(iIndAxisI < 0 || iIndAxisI >= m_iNumDim)	{
			System.out.println("the Axis index is error!");
			return(new CFWMatrix());
		}
		//2.calculate transfer matrix
		m_vecAxis[iIndAxisI].nor();
		CFWVector vecTmp = (CFWVector)m_vecAxis[iIndAxisI].multi(fSclOffI);
		CFWMatrix matRet = CFWMatrix.scale3D( vecTmp.m_fX, vecTmp.m_fY, vecTmp.m_fZ);

		return(matRet);
	}
	public boolean equals(CFWCoord codI)	{
		for( int i = 0; i < m_iNumDim; ++i)
			if(m_vecAxis[i].equals((CFWVector)codI.m_vecAxis[i]))
				return false;
		return((m_poiCenter.equals(codI.m_poiCenter))
			&& (m_bLeftH == codI.m_bLeftH));
	}
}
