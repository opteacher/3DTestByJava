package eg;

public class CFWMatrix {
	static public final int s_iDim3D = 4;
	static public final int s_iDim2D = 3;
	public float m_fVal[][];
	private int m_iNumDim;
	public CFWMatrix()	{
		m_fVal = new float[s_iDim3D][s_iDim3D];
		m_iNumDim = s_iDim3D;
		//make it to a identity matrix
		for( int i = 0; i < m_iNumDim; ++i)
			for( int j = 0; j < m_iNumDim; ++j)	{
				if(i == j)
					m_fVal[i][j] = 1.0f;
				else
					m_fVal[i][j] = 0.0f;
			}
	}
	public CFWMatrix(int iNumDimI)	{
		m_fVal = new float[iNumDimI][iNumDimI];
		m_iNumDim = iNumDimI;
		//make it to a identity matrix
		for( int i = 0; i < m_iNumDim; ++i)
			for( int j = 0; j < m_iNumDim; ++j)	{
				if(i == j)
					m_fVal[i][j] = 1.0f;
				else
					m_fVal[i][j] = 0.0f;
			}
	}
	public CFWMatrix resetDim(int iDimI)	{
		CFWMatrix matRet = new CFWMatrix(iDimI);
		int iDimLess = 0;
		if(iDimI < m_iNumDim)	{
			System.out.println("define dimension is less than this one"
					+"data may be lost!");
			//give the warning, but continue implement
			iDimLess = iDimI;
		}
		else	{
			iDimLess = m_iNumDim;
		}
		
		for( int i = 0; i < iDimLess; ++i)
			for( int j = 0; j < iDimLess; ++j)
				matRet.m_fVal[i][j] = m_fVal[i][j];
		return(matRet);
	}
	public CFWMatrix plus(CFWMatrix matI)	{
		CFWMatrix matRet = new CFWMatrix();
		for( int i = 0; i < m_iNumDim; ++i)
			for( int j = 0; j < m_iNumDim; ++j)
				matRet.m_fVal[j][i] = m_fVal[j][i] + matI.m_fVal[j][i];
		return(matRet);
	}
	public CFWMatrix sub(CFWMatrix matI)	{
		CFWMatrix matRet = new CFWMatrix();
		for( int i = 0; i < m_iNumDim; ++i)
			for( int j = 0; j < m_iNumDim; ++j)
				matRet.m_fVal[j][i] = m_fVal[j][i] - matI.m_fVal[j][i];
		return(matRet);
	}
	public CFWMatrix multi(float fValI)	{
		CFWMatrix matRet = new CFWMatrix();
		for( int i = 0; i < m_iNumDim; ++i)
			for( int j = 0; j < m_iNumDim; ++j)
				matRet.m_fVal[j][i] = fValI * m_fVal[j][i];
		return(matRet);
	}
	public CFWMatrix dotLeft(CFWMatrix matI)	{
		if(m_iNumDim != matI.m_iNumDim)	{
			System.out.println("two difference dimension can't do multiplication");
			return(new CFWMatrix());
		}
		CFWMatrix matRet = new CFWMatrix(m_iNumDim);
		float fValSum	= 0;
		for( int i = 0; i < m_iNumDim; ++i)
			for( int j = 0; j < m_iNumDim; ++j)	{
				for( int n = 0; n < m_iNumDim; ++n)
					fValSum += m_fVal[n][j] * matI.m_fVal[i][n];
				matRet.m_fVal[i][j] = fValSum;
				fValSum	= 0.0f;
			}
		return(matRet);
	}
	public CFWMatrix dotRight(CFWMatrix matI)	{
		if(m_iNumDim != matI.m_iNumDim)	{
			System.out.println("two difference dimension can't do multiplication");
			return(new CFWMatrix());
		}
		CFWMatrix matRet = new CFWMatrix(m_iNumDim);
		float fValSum	= 0;
		for( int i = 0; i < m_iNumDim; ++i)	{
			for( int j = 0; j < m_iNumDim; ++j)	{
				for( int n = 0; n < m_iNumDim; ++n)
					fValSum	+= m_fVal[i][n] * matI.m_fVal[n][j];
				matRet.m_fVal[i][j]	= fValSum;
				fValSum	= 0.0f;
			}//end for j
		}//end for i
		return(matRet);
	}
	/**
	 * [point][matrix]
	 * @param poiI
	 * @return
	 */
	public CFWPoint multiPoiRight(CFWPoint poiI)	{
		CFWPoint poiRet = new CFWPoint();
		
		if(m_iNumDim <= s_iDim2D)	{
			if(poiI.m_fZ == 0.0f)	{
				poiI.m_fZ	= 1.0f;
			}//if z == 0.0f
			else if(poiI.m_fY == 0.0f)	{
				poiI.m_fY	= 1.0f;
			}//else if y == 0.0f
			
			poiRet.m_fX	= poiI.m_fX*m_fVal[0][0] + poiI.m_fY*m_fVal[0][1] + poiI.m_fH*m_fVal[0][2];
			poiRet.m_fY	= poiI.m_fX*m_fVal[1][0] + poiI.m_fY*m_fVal[1][1] + poiI.m_fH*m_fVal[1][2];
			poiRet.m_fH	= poiI.m_fX*m_fVal[2][0] + poiI.m_fY*m_fVal[2][1] + poiI.m_fH*m_fVal[2][2];
		}
		else	{
			poiRet.m_fX	= poiI.m_fX*m_fVal[0][0] + poiI.m_fY*m_fVal[0][1] + poiI.m_fZ*m_fVal[0][2] + poiI.m_fH*m_fVal[0][3];
			poiRet.m_fY	= poiI.m_fX*m_fVal[1][0] + poiI.m_fY*m_fVal[1][1] + poiI.m_fZ*m_fVal[1][2] + poiI.m_fH*m_fVal[1][3];
			poiRet.m_fZ	= poiI.m_fX*m_fVal[2][0] + poiI.m_fY*m_fVal[2][1] + poiI.m_fZ*m_fVal[2][2] + poiI.m_fH*m_fVal[2][3];
			poiRet.m_fH	= poiI.m_fX*m_fVal[3][0] + poiI.m_fY*m_fVal[3][1] + poiI.m_fZ*m_fVal[3][2] + poiI.m_fH*m_fVal[3][3];
		}

		poiRet	= poiRet.divi(poiRet.m_fH);
		
		poiRet.regulate();

		return(poiRet);
	}
	/**
	 * [matrix][point]
	 * @param poiI
	 * @return
	 */
	public CFWPoint multiPoiLeft(CFWPoint poiI)	{
		CFWPoint poiRet = new CFWPoint();
		
		if(m_iNumDim <= s_iDim2D)	{
			if(poiI.m_fZ == 0.0f)	{
				poiI.m_fZ	= 1.0f;
			}//if z == 0.0f
			else if(poiI.m_fY == 0.0f)	{
				poiI.m_fY	= 1.0f;
			}//else if y == 0.0f
			
			poiRet.m_fX	= poiI.m_fX*m_fVal[0][0] + poiI.m_fY*m_fVal[1][0] + poiI.m_fH*m_fVal[2][0];
			poiRet.m_fY	= poiI.m_fX*m_fVal[0][1] + poiI.m_fY*m_fVal[1][1] + poiI.m_fH*m_fVal[2][1];
			poiRet.m_fH	= poiI.m_fX*m_fVal[0][2] + poiI.m_fY*m_fVal[1][2] + poiI.m_fH*m_fVal[2][2];
		}
		else	{
			poiRet.m_fX	= poiI.m_fX*m_fVal[0][0] + poiI.m_fY*m_fVal[1][0] + poiI.m_fZ*m_fVal[2][0] + poiI.m_fH*m_fVal[3][0];
			poiRet.m_fY	= poiI.m_fX*m_fVal[0][1] + poiI.m_fY*m_fVal[1][1] + poiI.m_fZ*m_fVal[2][1] + poiI.m_fH*m_fVal[3][1];
			poiRet.m_fZ	= poiI.m_fX*m_fVal[0][2] + poiI.m_fY*m_fVal[1][2] + poiI.m_fZ*m_fVal[2][2] + poiI.m_fH*m_fVal[3][2];
			poiRet.m_fH	= poiI.m_fX*m_fVal[0][3] + poiI.m_fY*m_fVal[1][3] + poiI.m_fZ*m_fVal[2][3] + poiI.m_fH*m_fVal[3][3];
		}

		if(0 == poiRet.m_fH)	{
			poiRet.m_fH = 1.0f;
		}
		else	{
			poiRet = poiRet.divi(poiRet.m_fH);
		}
		
		poiRet.regulate();

		return(poiRet);
	}
	
	public CFWVector multiVecLeft(CFWVector vecI)	{
		CFWPoint poiTmp = new CFWPoint();
		poiTmp.m_fX = vecI.m_fX;
		poiTmp.m_fY = vecI.m_fY;
		poiTmp.m_fZ = vecI.m_fZ;
		poiTmp.m_fH = vecI.m_fH;
		return(new CFWVector(multiPoiLeft(poiTmp)));	
	}
	
	public CFWVector multiVecRight(CFWVector vecI)	{
		CFWPoint poiTmp = new CFWPoint();
		poiTmp.m_fX = vecI.m_fX;
		poiTmp.m_fY = vecI.m_fY;
		poiTmp.m_fZ = vecI.m_fZ;
		poiTmp.m_fH = vecI.m_fH;
		return(new CFWVector(multiPoiRight(poiTmp)));	
	}
		
	public boolean equals(CFWMatrix matI)	{
		for( int i = 0; i < m_iNumDim; ++i)
			for( int j = 0; j < m_iNumDim; ++j)
				if(!CFWMath.equals(m_fVal[i][j], matI.m_fVal[i][j]))
					return false;
		return true;
	}
	
	/**
	 * calculate the result of determinant
	 * @return
	 */
	public float getDetVal()	{
		//compose positive number
		float fPosVal	= 0;
		for( int i = 0; i < m_iNumDim; ++i)	{
			float fValMulti = 1;
			for( int j = 0; j < m_iNumDim; ++j)	{
				int iValTemp = i + j;
				if(iValTemp >= m_iNumDim)
					iValTemp -= m_iNumDim;
				float fValTmp = m_fVal[j][iValTemp];
				fValMulti *= fValTmp;
			}//end for 2
			fPosVal += fValMulti;
		}
		//compose negative number
		float fNegVal	= 0;
		for( int i = m_iNumDim - 1; i >= 0; --i)	{
			float fValMulti = 1;
			for( int j = 0; j < m_iNumDim; ++j)	{
				int iValTemp = i - j;
				if(iValTemp < 0)
					iValTemp += m_iNumDim;
				float fValTmp = m_fVal[j][iValTemp];
				fValMulti *= fValTmp;
			}//end for 2
			fNegVal += fValMulti;
		}
		return(fPosVal - fNegVal);
	}
	
	public CFWMatrix getDecip()	{
		CFWMatrix matRet = new CFWMatrix();
		for( int i = 0; i < m_iNumDim; ++i)
			for( int j = 0; j < m_iNumDim; ++j)
				if(0 != m_fVal[j][i])
					matRet.m_fVal[j][i]	= 1.0f / m_fVal[j][i];
		return(matRet);
	}
	/**
	 * adj matrix
	 * @return
	 */
	public CFWMatrix getAdjMat()	{
		CFWMatrix matRet = new CFWMatrix(m_iNumDim);
		for( int i = 0; i < m_iNumDim; ++i)
			for( int j = 0; j < m_iNumDim; ++j)	{
				
				CFWMatrix matTmp = new CFWMatrix(m_iNumDim - 1);
				for( int iT = 0, iIndRow = 0; iT < m_iNumDim && iIndRow < m_iNumDim - 1; ++iT)	{
					for( int jT = 0, iIndCol = 0; jT < m_iNumDim && iIndCol < m_iNumDim - 1; ++jT)	{
						if(iT != i && jT != j)	{
							matTmp.m_fVal[iIndCol][iIndRow] = this.m_fVal[jT][iT];
							++iIndCol;
						}
					}
					if(iT != i)	{
						++iIndRow;
					}
				}
				
				float fResDet = matTmp.getDetVal();
				matRet.m_fVal[j][i] = (float)Math.pow( -1, j+i)*fResDet;
			}
		return(matRet);
	}
	/**
	 * negative matrix
	 * @return
	 * @throws Exception 
	 */
	public CFWMatrix getNegMat() throws Exception	{
		float fDetRes = getDetVal();
		if(0 == fDetRes)	{
			throw new Exception("W:the matrix's det is zero");
		}

		CFWMatrix matAdj = this.getAdjMat();
		CFWMatrix matRet = new CFWMatrix(m_iNumDim);
		for( int i = 0; i < m_iNumDim; ++i)
			for( int j = 0; j < m_iNumDim; ++j)	{
				matRet.m_fVal[j][i]	= matAdj.m_fVal[j][i] / fDetRes;
			}
		return(matRet);
	}
	
	/**
	 * get transpose matrix
	 * @return
	 */
	public CFWMatrix getTrspMat()	{
		CFWMatrix matRet = new CFWMatrix();
		int t = 0;
		for( int i = 0; i < m_iNumDim; ++i, ++t)	{
			for( int j = t; j < m_iNumDim; ++j)	{
				if(i != j)	{
					matRet.m_fVal[i][j] = m_fVal[j][i];
					matRet.m_fVal[j][i] = m_fVal[i][j];
				}
			}
		}
		return(matRet);
	}
	
	public CFWMatrix getIdentityMat()	{
		return(new CFWMatrix(m_iNumDim));
	}
	
	public boolean isIdentityMat()	{
		for( int i = 0; i < m_iNumDim; ++i)
			for( int j = 0; j < m_iNumDim; ++j)	{
				if(i == j)	{
					if(m_fVal[i][j] != 1.0f)
						return false;
				}
				else	{
					if(m_fVal[i][j] != 0.0f)
						return false;
				}
			}
		return true;
	}
	static public CFWMatrix rotate2D( float fAngI)	{
		//1.the angle turn to radian
		double dAng = (Math.PI/180.0f)*fAngI;
		//2.set matrix value
		CFWMatrix matRet = new CFWMatrix(s_iDim2D);
		matRet.m_fVal[0][0]	= (float)Math.cos(dAng);
		matRet.m_fVal[1][0]	= -(float)Math.sin(dAng);
		matRet.m_fVal[0][1]	= -matRet.m_fVal[1][0];
		matRet.m_fVal[1][1]	= matRet.m_fVal[0][0];
		matRet.m_iNumDim = s_iDim2D;
		return(matRet);
	}
	static public CFWMatrix rotate2D( float fAngI, CFWPoint poiCtI)	{
		//1.create two matrix to transfer and reset
		CFWMatrix matTransA = transfer2D( poiCtI.m_fX, poiCtI.m_fY);
		CFWMatrix matTransB = transfer2D( -poiCtI.m_fX, -poiCtI.m_fY);
		//2.create rotate matrix
		CFWMatrix matRet = rotate2D(fAngI);
		//3.compose multiply: matTransA * matRet * matTransB
		matRet = matRet.dotLeft(matTransA);
		matRet = matRet.dotRight(matTransB);
		
		return(matRet);
	}
	static public CFWMatrix transfer2D( float fXOffI, float fYOffI)	{
		CFWMatrix matRet = new CFWMatrix(s_iDim2D);
		matRet.m_fVal[2][0]	= fXOffI;
		matRet.m_fVal[2][1]	= fYOffI;
		matRet.m_iNumDim = s_iDim2D;
		return(matRet);
	}
	static public CFWMatrix scale2D( float fXDiffI, float fYDiffI)	{
		CFWMatrix matRet = new CFWMatrix(s_iDim2D);
		matRet.m_fVal[0][0]	= fXDiffI;
		matRet.m_fVal[1][1]	= fYDiffI;
		matRet.m_iNumDim = s_iDim2D;
		return(matRet);
	}
	static public CFWMatrix scale2D( float fXDiffI, float fYDiffI, CFWPoint poiCtI)	{
		//1.create two matrix to transfer and reset
		CFWMatrix matTransA = transfer2D( -poiCtI.m_fX, -poiCtI.m_fY);
		CFWMatrix matTransB = transfer2D( poiCtI.m_fX, poiCtI.m_fY);
		//2.create scale matrix
		CFWMatrix matRet = scale2D( fXDiffI, fYDiffI);
		//3.compose multiply: matTransA * matRet * matTransB
		matRet = matRet.dotLeft(matTransA);
		matRet = matRet.dotRight(matTransB);
		
		return(matRet);
	}
	static public CFWMatrix transfer3D( float fXOffI, float fYOffI, float fZOffI)	{
		CFWMatrix matRet = new CFWMatrix();
		matRet.m_fVal[3][0] = fXOffI;
		matRet.m_fVal[3][1] = fYOffI;
		matRet.m_fVal[3][2] = fZOffI;
		return(matRet);
	}
	static public CFWMatrix rotate3D( float fAngI, CFWVector vecAxisI)	{
		//1.turn angle to radian
		double dAngRad	= Math.PI*fAngI/180.0f;
		//2.compose the rotate Quaternion
		CFWVector poiQuaternion = new CFWVector();
		try	{
			vecAxisI.nor();
			float fSinVal	= (float)Math.sin(dAngRad/2.0f);
			poiQuaternion.m_fX	= vecAxisI.m_fX * fSinVal;
			poiQuaternion.m_fY	= vecAxisI.m_fY * fSinVal;
			poiQuaternion.m_fZ	= vecAxisI.m_fZ * fSinVal;
			//poiQuaternion.nor();
			poiQuaternion.m_fH	= (float)Math.cos(dAngRad/2.0f);
		}
		catch(Exception e)	{
			e.printStackTrace();
			return(new CFWMatrix());
		}
		//3.compose the matrix
		CFWMatrix matRet = new CFWMatrix();
		matRet.m_fVal[0][0]	= 1.0f - 2*poiQuaternion.m_fY*poiQuaternion.m_fY - 2.0f*poiQuaternion.m_fZ*poiQuaternion.m_fZ;
		matRet.m_fVal[0][1]	= 2.0f*poiQuaternion.m_fX*poiQuaternion.m_fY - 2.0f*poiQuaternion.m_fH*poiQuaternion.m_fZ;
		matRet.m_fVal[0][2]	= 2.0f*poiQuaternion.m_fX*poiQuaternion.m_fZ + 2.0f*poiQuaternion.m_fH*poiQuaternion.m_fY;

		matRet.m_fVal[1][0]	= 2.0f*poiQuaternion.m_fX*poiQuaternion.m_fY + 2.0f*poiQuaternion.m_fH*poiQuaternion.m_fZ;
		matRet.m_fVal[1][1]	= 1.0f - 2*poiQuaternion.m_fX*poiQuaternion.m_fX - 2.0f*poiQuaternion.m_fZ*poiQuaternion.m_fZ;
		matRet.m_fVal[1][2]	= 2.0f*poiQuaternion.m_fY*poiQuaternion.m_fZ - 2.0f*poiQuaternion.m_fH*poiQuaternion.m_fX;

		matRet.m_fVal[2][0]	= 2.0f*poiQuaternion.m_fX*poiQuaternion.m_fZ - 2.0f*poiQuaternion.m_fH*poiQuaternion.m_fY;
		matRet.m_fVal[2][1]	= 2.0f*poiQuaternion.m_fY*poiQuaternion.m_fZ + 2.0f*poiQuaternion.m_fH*poiQuaternion.m_fX;
		matRet.m_fVal[2][2]	= 1.0f - 2*poiQuaternion.m_fX*poiQuaternion.m_fX - 2.0f*poiQuaternion.m_fY*poiQuaternion.m_fY;
		
		return(matRet);
	}
	
	static public CFWMatrix rotate3D( float fAngI, CFWRay rayAxisI)	{
		CFWVector vecAxis = (CFWVector)rayAxisI;
		CFWMatrix matRot = rotate3D( fAngI, vecAxis);
		CFWPoint poiDisTrsf = rayAxisI.poiProjOnLn(new CFWPoint());
		CFWMatrix matNegTrans = transfer3D( -poiDisTrsf.m_fX, -poiDisTrsf.m_fY, -poiDisTrsf.m_fZ);
		CFWMatrix matPosTrans = transfer3D( poiDisTrsf.m_fX, poiDisTrsf.m_fY, poiDisTrsf.m_fZ);
		
		CFWMatrix matRet = new CFWMatrix();
		matRet = matRet.dotRight(matNegTrans);
		matRet = matRet.dotRight(matRot);
		matRet = matRet.dotRight(matPosTrans);
		return(matRet);
	}
	
	static public CFWMatrix rotate3D( CFWVector vecBegI, CFWVector vecEndI)	{
		vecBegI.nor();	vecEndI.nor();
		CFWVector vecRotAxis = vecEndI.cross(vecBegI);
		//the same vector, return Identity Matrix
		if(vecRotAxis.isZeroVec())	{
			return(new CFWMatrix());
		}
		float fAngTmp = (float)vecEndI.angWithVec( vecBegI, false);
		CFWMatrix matRot = CFWMatrix.rotate3D( fAngTmp, vecRotAxis);
		return(matRot);
	}
	
	static public CFWMatrix scale3D( float fXDiffI, float fYDiffI, float fZDiffI)	{
		CFWMatrix matRet = new CFWMatrix();
		matRet.m_fVal[0][0]	= fXDiffI;
		matRet.m_fVal[1][1]	= fYDiffI;
		matRet.m_fVal[2][2]	= fZDiffI;
		return(matRet);
	}
}
