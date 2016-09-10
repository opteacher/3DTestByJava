package eg;

public class CFWVector extends CFWPoint {
	public CFWVector()	{
		super();
	}
	
	public CFWVector(CFWPoint poiI)	{
		super( poiI.m_fX, poiI.m_fY, poiI.m_fZ);
		m_fH = poiI.m_fH;
	}
	
	public CFWVector( float fXI, float fYI, float fZI)	{
		super( fXI, fYI, fZI);
	}
	
	public CFWVector( CFWPoint poiBegI, CFWPoint poiEndI)	{
		CFWPoint poiTmp = poiEndI.sub(poiBegI);
		m_fX = poiTmp.m_fX;
		m_fY = poiTmp.m_fY;
		m_fZ = poiTmp.m_fZ;
		m_fH = poiTmp.m_fH;
	}
	
	public CFWVector plus(CFWVector vecI)	{
		return(new CFWVector(super.plus(vecI)));
	}
	/**
	 * this + fValI
	 * @param fValI
	 * @return
	 */
	public CFWPoint plus(float fValI)	{
		CFWPoint poiRet = new CFWPoint();
		poiRet.m_fX = this.m_fX + fValI;
		poiRet.m_fY = this.m_fY + fValI;
		poiRet.m_fZ = this.m_fZ + fValI;
		return(poiRet);
	}

	public CFWVector dot(CFWVector vecI)	{
		CFWVector vecRet = new CFWVector();
		
		vecRet.m_fX	= m_fX * vecI.m_fX;
		vecRet.m_fY	= m_fY * vecI.m_fY;
		vecRet.m_fZ	= m_fZ * vecI.m_fZ;
		vecRet.m_fH	= m_fH * vecI.m_fH;

		return(vecRet);
	}
	
	public CFWVector cross(CFWVector vecI)	{
		CFWVector vecRet = new CFWVector();

		vecRet.m_fX	= m_fY * vecI.m_fZ - m_fZ * vecI.m_fY;
		vecRet.m_fY	= m_fZ * vecI.m_fX - m_fX * vecI.m_fZ;
		vecRet.m_fZ	= m_fX * vecI.m_fY - m_fY * vecI.m_fX;

		vecRet.m_fH	= m_fH * vecI.m_fH;
		
		vecRet.regulate();

		return(vecRet);
	}
	
	/**
	 * x+y+z
	 * @return
	 */
	public float sun()	{
		return(m_fX + m_fY + m_fZ);
	}
	
	public float getLength()	{
		return((float)Math.sqrt(m_fX*m_fX + m_fY*m_fY + m_fZ*m_fZ));
	}
	/**
	 * get negative vector
	 * @return
	 */
	public CFWVector getNegVec()	{
		return(new CFWVector( -m_fX, -m_fY, -m_fZ));
	}
	/**
	 * make this vector to normalize vector
	 */
	public CFWVector nor()	{
		//1.make out length of the vector
		double dLength = Math.sqrt((double)(m_fX*m_fX + m_fY*m_fY + m_fZ*m_fZ));
		//2.if length is zero, it means the vector is a zero vector
		if(0 == dLength)	{
			throw new ArithmeticException("divisor is zero!");
		}
		m_fX /= dLength;
		m_fY /= dLength;
		m_fZ /= dLength;
		m_fH = 1.0f;
		
		return this;
	}
	
	public boolean equals(CFWVector vecI)	{
		return(CFWMath.equals( this.m_fX, vecI.m_fX)
			&& CFWMath.equals( this.m_fY, vecI.m_fY)
			&& CFWMath.equals( this.m_fZ, vecI.m_fZ));
	}
	
	public boolean isNorVec()	{
		//1.make out length of the vector
		float fLength = m_fX*m_fX + m_fY*m_fY + m_fZ*m_fZ;
		
		return(CFWMath.equals(fLength, 1));
	}
	
	public boolean isZeroVec()	{
		return(Math.abs(m_fX) < CFWMath.s_fValMin
			 && Math.abs(m_fY) < CFWMath.s_fValMin
			 && Math.abs(m_fZ) < CFWMath.s_fValMin);
	}
	
	public boolean isParallel(CFWVector vecI)	{
		nor();
		vecI.nor();
		if(equals(vecI))
			return true;
		else if(equals(vecI.getNegVec()))
			return true;
		return false;
	}
	
	public boolean isVertical(CFWVector vecI)	{
		if(CFWMath.s_fValMin > Math.abs(dot(vecI).sun()))
			return true;
		else
			return false;
	}
	
	/**
	 * calculate the angle with another vector
	 * @param vecI
	 * @param bWantRad: identify whether angle you want
	 * @return
	 */
	public float angWithVec( CFWVector vecI, boolean bWantRad)	{
		//CFWProgChk pgChk = new CFWProgChk();
		//pgChk.funStart(this.getClass() + "::angWithVec( CFWVector vecI, boolean bWantRad)");
		//1.check the length, if equals to zero, return 0
		float fLenSelf = this.dot(this).sun();
		float fLenVec = vecI.dot(vecI).sun();
		double dLenSelf = fLenSelf;
		double dLenVec = fLenVec;
		if( 0.0f == fLenSelf || 0.0f == fLenVec)	{
			return(0.0f);
		}
		if(!CFWMath.equals( fLenSelf, 1))	{
			dLenSelf = Math.sqrt(fLenSelf);
		}
		if(!CFWMath.equals( fLenVec, 1))	{
			dLenVec = Math.sqrt(fLenVec);
		}
		//2.calculate the cos
		double dAngCos	= dot(vecI).sun() / ( dLenSelf*dLenVec);
		//3.calculate the angle
		double dAngRad	= Math.acos(dAngCos);
		//pgChk.funEnd();
		//4.depend on the user choice, return
		return( (bWantRad) ? CFWMath.regulateFloat((float)dAngRad) :
			CFWMath.regulateFloat((float)((dAngRad * 180) / Math.PI)));
	}
	
	/**
	 * calculate the cos with another vector
	 * @param vecI
	 * @return
	 */
	public float cosWithVec(CFWVector vecI)	{
		//1.check the length, if equals to zero, return 0
		double dLenSelf = 1;
		double dLenVec = 1;
		if(!this.isNorVec())	{
			dLenSelf = Math.sqrt((double)(m_fX*m_fX + m_fY*m_fY + m_fZ*m_fZ));
		}
		if(!vecI.isNorVec())	{
			dLenVec = Math.sqrt((double)(vecI.m_fX*vecI.m_fX + vecI.m_fY*vecI.m_fY + vecI.m_fZ*vecI.m_fZ));
		}
		if( 0.0f == dLenSelf || 0.0f == dLenVec)
			return(0.0f);
		//2.calculate the cos
		double dAngCos	= (m_fX*vecI.m_fX + m_fY*vecI.m_fY + m_fZ*vecI.m_fZ) / ( dLenSelf*dLenVec);
		float fCosRet = CFWMath.regulateFloat((float)dAngCos);
		return(fCosRet);
	}
	
	/**
	 * check the angle between this and another vector less than 90^
	 * @param vecI
	 * @return
	 */
	public boolean isSameDir_VerticalIn(CFWVector vecI)	{
		float fDot = this.dot(vecI).sun();
		if(0 == fDot)	{ return true; }
		return(0 < Math.signum(fDot));
	}
	
	public boolean isSameDir_VerticalOut(CFWVector vecI)	{
		float fDot = this.dot(vecI).sun();
		if(0 == fDot)	{ return false; }
		return(0 < Math.signum(fDot));
	}
}
