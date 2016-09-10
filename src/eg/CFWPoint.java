package eg;

public class CFWPoint {
	public float m_fX;
	public float m_fY;
	public float m_fZ;
	public float m_fH;
	
	public CFWPoint()	{
		m_fX = 0.0f;
		m_fY = 0.0f;
		m_fZ = 0.0f;
		m_fH = 1.0f;
	}
	
	public CFWPoint(CFWPoint poiI)	{
		m_fX = poiI.m_fX;
		m_fY = poiI.m_fY;
		m_fZ = poiI.m_fZ;
		m_fH = poiI.m_fH;
	}
	
	public CFWPoint( float fXI, float fYI, float fZI)	{
		m_fX = fXI;
		m_fY = fYI;
		m_fZ = fZI;
		m_fH = 1.0f;
	}
	
	/**
	 * this + poiI
	 * @param poiI
	 * @return
	 */
	public CFWPoint plus(CFWPoint poiI)	{
		CFWPoint poiRet = new CFWPoint();
		poiRet.m_fX = this.m_fX + poiI.m_fX;
		poiRet.m_fY = this.m_fY + poiI.m_fY;
		poiRet.m_fZ = this.m_fZ + poiI.m_fZ;
		return(poiRet);
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
	
	/**
	 * this - poiI
	 * @param poiI
	 * @return
	 */
	public CFWPoint sub(CFWPoint poiI)	{
		CFWPoint poiRet = new CFWPoint();
		poiRet.m_fX = this.m_fX - poiI.m_fX;
		poiRet.m_fY = this.m_fY - poiI.m_fY;
		poiRet.m_fZ = this.m_fZ - poiI.m_fZ;
		return(poiRet);
	}
	
	/**
	 * this * fValI
	 * @param fValI
	 * @return
	 */
	public CFWPoint multi(float fValI)	{
		CFWPoint poiRet = new CFWPoint();
		poiRet.m_fX = this.m_fX * fValI;
		poiRet.m_fY = this.m_fY * fValI;
		poiRet.m_fZ = this.m_fZ * fValI;
		poiRet.m_fH = this.m_fH * fValI;
		return(poiRet);
	}

	/**
	 * this / fValI
	 * @param fValI
	 * @return
	 */
	public CFWPoint divi(float fValI)	{
		CFWPoint poiRet = new CFWPoint();
		if(fValI == 0)	{
			throw new ArithmeticException("divisor is zero!");
		}
		poiRet.m_fX = CFWMath.regulateFloat(this.m_fX / fValI);
		poiRet.m_fY = CFWMath.regulateFloat(this.m_fY / fValI);
		poiRet.m_fZ = CFWMath.regulateFloat(this.m_fZ / fValI);
		poiRet.m_fH = CFWMath.regulateFloat(this.m_fH / fValI);
		return(poiRet);
	}
	
	/**
	 * x=x, y=y, z=z
	 * @param poiI
	 * @return
	 */
	public boolean equals(Object poiI)	{
		if(null == poiI || !poiI.getClass().equals(CFWPoint.class))	{
			return false;
		}
		CFWPoint poiTmp = (CFWPoint)poiI;
		return(CFWMath.equals( this.m_fX, poiTmp.m_fX)
			&& CFWMath.equals( this.m_fY, poiTmp.m_fY)
			&& CFWMath.equals( this.m_fZ, poiTmp.m_fZ));
	}
	
	/**
	 * x<x, y<y, z<=z
	 * @param poiI
	 * @return
	 */
	public boolean less(CFWPoint poiI)	{
		return(this.m_fX < poiI.m_fX
			&& this.m_fY < poiI.m_fY
			&& this.m_fZ <= poiI.m_fZ);
	}
	
	/**
	 * x>x, y>y, z>=z
	 * @param poiI
	 * @return
	 */
	public boolean bigger(CFWPoint poiI)	{
		return(this.m_fX > poiI.m_fX
			&& this.m_fY > poiI.m_fY
			&& this.m_fZ >= poiI.m_fZ);
	}
	
	public void regulate()	{
		m_fX = CFWMath.regulateFloat(m_fX);
		m_fY = CFWMath.regulateFloat(m_fY);
		m_fZ = CFWMath.regulateFloat(m_fZ);
	}
	
	public boolean isZeroPoi()	{
		return(Math.abs(m_fX) < CFWMath.s_fValMin
			 &&Math.abs(m_fY) < CFWMath.s_fValMin
			 &&Math.abs(m_fZ) < CFWMath.s_fValMin);
	}
}
