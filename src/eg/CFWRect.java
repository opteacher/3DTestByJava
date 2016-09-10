package eg;

public class CFWRect {
	//	*---------------------> X
	//	| 1 |  2  | 3
	//	| -----------
	//	| 8 |  9  | 4
	//	| -----------
	//	| 7 |  6  | 5
	//	|
	//	- Y
	static public final int s_iAreaLT = 1;
	static public final int s_iAreaMT = 2;
	static public final int s_iAreaRT = 3;
	static public final int s_iAreaRM = 4;
	static public final int s_iAreaRB = 5;
	static public final int s_iAreaMB = 6;
	static public final int s_iAreaLB = 7;
	static public final int s_iAreaLM = 8;
	static public final int s_iAreaM = 9;
	
	public CFWPoint m_poiLT;
	public CFWPoint m_poiRB;
	
	public CFWRect()	{
		m_poiLT = new CFWPoint();
		m_poiRB = new CFWPoint();
	}
	
	public CFWRect( CFWPoint poiLTI, CFWPoint poiRBI)	{
		m_poiLT = poiLTI;
		m_poiRB = poiRBI;
	}
	
	public float getWidth()	{
		return(Math.abs(m_poiLT.m_fX - m_poiRB.m_fX));
	}
	
	public float getHeight()	{
		return(Math.abs(m_poiLT.m_fY - m_poiRB.m_fY));
	}
	
	/**
	 * return a line segment, CFWRect.m_poiLT and CFWRect.m_poiRB are the two points of line
	 * @param poiBegI
	 * @param poiEndI
	 * @return
	 */
	public CFWRect intersectsLine( CFWPoint poiBegI, CFWPoint poiEndI)	{
		//1.rejust rect
		//correct: (m_poiLT.m_fX < m_poiRB.m_fX && m_poiLT.m_fY < m_poiRB.m_fY)
		if(m_poiLT.m_fX > m_poiRB.m_fX && m_poiLT.m_fY > m_poiRB.m_fY)	{
			//exchange all
			CFWPoint poiTmp = m_poiLT;
			m_poiLT = m_poiRB;
			m_poiRB = poiTmp;
		}
		else if(m_poiLT.m_fX > m_poiRB.m_fX && m_poiLT.m_fY < m_poiRB.m_fY)	{
			//exchange X
			float fXTmp = m_poiLT.m_fX;
			m_poiLT.m_fX = m_poiRB.m_fX;
			m_poiRB.m_fX = fXTmp;
		}
		else if(m_poiLT.m_fX < m_poiRB.m_fX && m_poiLT.m_fY > m_poiRB.m_fY)	{
			//exchange Y
			float fYTmp = m_poiLT.m_fY;
			m_poiLT.m_fY = m_poiRB.m_fY;
			m_poiRB.m_fY = fYTmp;
		}
		//2.intersect test
		CFWPoint poiOutOfRect = new CFWPoint();
		boolean bPoiInsideRect = false;
		CFWRect rectRet = new CFWRect();
		if(!checkPoiInside( poiBegI, poiEndI))	{
			//System.out.println("the line has no cross point with the rectangle!");
			return(rectRet);
		}
		else if(!checkPoiInside( poiEndI, poiBegI))	{
			//System.out.println("the line has no cross point with the rectangle!");
			return(rectRet);
		}
		else	{
			if(isInsideArea( 9, poiBegI))	{
				if(isInsideArea( 9, poiEndI))	{
					//System.out.println("the line has no cross point with the rectangle!");
					return(rectRet);
				}
				rectRet.m_poiLT = poiBegI;
				poiOutOfRect = poiEndI;
				bPoiInsideRect = true;
			}
			if(isInsideArea( 9, poiEndI))	{
				if(isInsideArea( 9, poiBegI))
					return(rectRet);
				rectRet.m_poiLT = poiEndI;
				poiOutOfRect = poiBegI;
				bPoiInsideRect = true;
			}
		}
		
		//3.intersect
		//if the line is vertical to X/Y, return the line segment
		if(poiEndI.m_fX == poiBegI.m_fX)	{
			if(!bPoiInsideRect)	{
				rectRet.m_poiLT.m_fX = poiEndI.m_fX;
				//less than the m_poiLT'Y, use the m_poiLT'Y
				rectRet.m_poiLT.m_fY = Math.max( Math.min(poiBegI.m_fY, poiEndI.m_fY), m_poiLT.m_fY);
				//larger than m_poiRB's Y, use the m_poiRB's Y
				rectRet.m_poiRB.m_fY = Math.min( Math.max(poiBegI.m_fY, poiEndI.m_fY), m_poiRB.m_fY);
			}
			else	{
				if(poiOutOfRect.m_fY < m_poiLT.m_fY)
					rectRet.m_poiRB.m_fY = m_poiLT.m_fY;
				else if(poiOutOfRect.m_fY > m_poiRB.m_fY)
					rectRet.m_poiRB.m_fY = m_poiRB.m_fY;
			}
			
			rectRet.m_poiRB.m_fX = poiEndI.m_fX;
			
		}
		else if(poiEndI.m_fY == poiBegI.m_fY)	{
			if(!bPoiInsideRect)	{
				rectRet.m_poiLT.m_fX = Math.max( Math.min( poiBegI.m_fX, poiEndI.m_fX), m_poiLT.m_fX);
				rectRet.m_poiLT.m_fY = poiEndI.m_fY;
				rectRet.m_poiRB.m_fX = Math.min( Math.max( poiBegI.m_fX, poiEndI.m_fX), m_poiRB.m_fX);
			}
			else	{
				if(poiOutOfRect.m_fX < m_poiLT.m_fX)
					rectRet.m_poiRB.m_fX = m_poiLT.m_fX;
				else if(poiOutOfRect.m_fX > m_poiRB.m_fX)
					rectRet.m_poiRB.m_fX = m_poiRB.m_fX;
			}
			
			rectRet.m_poiRB.m_fY = poiEndI.m_fY;
		}
		else	{
			//build line function
			double dK = (poiEndI.m_fY - poiBegI.m_fY)/(poiEndI.m_fX - poiBegI.m_fX);
			double dB = poiBegI.m_fY - dK*poiBegI.m_fX;
			//find intersect points
			CFWPoint poiIntersA = new CFWPoint( m_poiLT.m_fX - 1.0f, m_poiLT.m_fY - 1.0f, 0.0f);
			CFWPoint poiIntersB = new CFWPoint( m_poiLT.m_fX - 1.0f, m_poiLT.m_fY - 1.0f, 0.0f);
			float fXTmpA = (float)((m_poiLT.m_fY - dB)/dK);
			float fXTmpB = (float)((m_poiRB.m_fY - dB)/dK);
			float fYTmpA = (float)(dK*m_poiLT.m_fX + dB);
			float fYTmpB = (float)(dK*m_poiRB.m_fX + dB);
			if(CFWMath.isBetweenTwoNum( fXTmpA, m_poiLT.m_fX, m_poiRB.m_fX, true))	{
				if(poiIntersA.m_fX < m_poiLT.m_fX && poiIntersA.m_fY < m_poiLT.m_fY)	{
					poiIntersA.m_fX = fXTmpA;
					poiIntersA.m_fY = m_poiLT.m_fY;
				}
				else	{
					poiIntersB.m_fX = fXTmpA;
					poiIntersB.m_fY = m_poiLT.m_fY;
				}
			}
			if(CFWMath.isBetweenTwoNum( fXTmpB, m_poiLT.m_fX, m_poiRB.m_fX, true))	{
				if(poiIntersA.m_fX < m_poiLT.m_fX && poiIntersA.m_fY < m_poiLT.m_fY)	{
					poiIntersA.m_fX = fXTmpB;
					poiIntersA.m_fY = m_poiRB.m_fY;
				}
				else	{
					poiIntersB.m_fX = fXTmpB;
					poiIntersB.m_fY = m_poiRB.m_fY;
				}
			}
			
			if(CFWMath.isBetweenTwoNum( fYTmpA, m_poiLT.m_fY, m_poiRB.m_fY, true))	{
				if(poiIntersA.m_fX < m_poiLT.m_fX && poiIntersA.m_fY < m_poiLT.m_fY)	{
					poiIntersA.m_fX = m_poiLT.m_fX;
					poiIntersA.m_fY = fYTmpA;
				}
				else	{
					poiIntersB.m_fX = m_poiLT.m_fX;
					poiIntersB.m_fY = fYTmpA;
				}
			}
			if(CFWMath.isBetweenTwoNum( fYTmpB, m_poiLT.m_fY, m_poiRB.m_fY, true))	{
				if(poiIntersA.m_fX < m_poiLT.m_fX && poiIntersA.m_fY < m_poiLT.m_fY)	{
					poiIntersA.m_fX = m_poiRB.m_fX;
					poiIntersA.m_fY = fYTmpB;
				}
				else	{
					poiIntersB.m_fX = m_poiRB.m_fX;
					poiIntersB.m_fY = fYTmpB;
				}
			}
			
			if(bPoiInsideRect)	{
				CFWVector vecBE = new CFWVector(poiOutOfRect.sub(rectRet.m_poiLT));
				CFWVector vecBA = new CFWVector(poiIntersA.sub(rectRet.m_poiLT));
				CFWVector vecBB = new CFWVector(poiIntersB.sub(rectRet.m_poiLT));
				if(vecBE.isSameDir_VerticalIn(vecBA))	{
					rectRet.m_poiRB = poiIntersA;
				}
				else if(vecBE.isSameDir_VerticalIn(vecBB))	{
					rectRet.m_poiRB = poiIntersB;
				}
			}
			else	{
				rectRet.m_poiLT = poiIntersA;
				rectRet.m_poiRB = poiIntersB;
			}
		}
		
		//last check : intersected line segment whether inside rectangle
		if(!isInsideArea( 9, rectRet.m_poiLT) || !isInsideArea( 9, rectRet.m_poiRB))	{
			System.out.println("generated line segment isn't inside rectangle!");
			return null;
		}
		return(rectRet);
	}
	
	/**
	 * check line has cross point with the rectangle
	 * +_+:don't check two point both inside condition
	 * @param poiBegI
	 * @param poiEndI
	 * @return
	 */
	protected boolean checkPoiInside( CFWPoint poiBegI, CFWPoint poiEndI)	{
		//[begin point in area 1] unintersect: 1,2,3,7,8
		//[begin point in area 2] unintersect: 1,2,3
		//[begin point in area 3] unintersect: 1,2,3,4,5
		//[begin point in area 4] unintersect: 3,4,5
		//[begin point in area 5] unintersect: 3,4,5,6,7
		//[begin point in area 6] unintersect: 5,6,7
		//[begin point in area 7] unintersect: 5,6,7,8,1
		//[begin point in area 8] unintersect: 7,8,1
		//[begin point in area 9] unintersect: 9
		if(isInsideArea( 1, poiBegI))	{
			if(isInsideArea( 1, poiEndI))
				return(false);
			else if(isInsideArea( 2, poiEndI))
				return(false);
			else if(isInsideArea( 3, poiEndI))
				return(false);
			else if(isInsideArea( 7, poiEndI))
				return(false);
			else if(isInsideArea( 8, poiEndI))
				return(false);
		}
		if(isInsideArea( 2, poiBegI))	{
			if(isInsideArea( 1, poiEndI))
				return(false);
			else if(isInsideArea( 2, poiEndI))
				return(false);
			else if(isInsideArea( 3, poiEndI))
				return(false);
		}
		if(isInsideArea( 3, poiBegI))	{
			if(isInsideArea( 1, poiEndI))
				return(false);
			else if(isInsideArea( 2, poiEndI))
				return(false);
			else if(isInsideArea( 3, poiEndI))
				return(false);
			else if(isInsideArea( 4, poiEndI))
				return(false);
			else if(isInsideArea( 5, poiEndI))
				return(false);
		}
		if(isInsideArea( 4, poiBegI))	{
			if(isInsideArea( 3, poiEndI))
				return(false);
			else if(isInsideArea( 4, poiEndI))
				return(false);
			else if(isInsideArea( 5, poiEndI))
				return(false);
		}
		if(isInsideArea( 5, poiBegI))	{
			if(isInsideArea( 3, poiEndI))
				return(false);
			else if(isInsideArea( 4, poiEndI))
				return(false);
			else if(isInsideArea( 5, poiEndI))
				return(false);
			else if(isInsideArea( 6, poiEndI))
				return(false);
			else if(isInsideArea( 7, poiEndI))
				return(false);
		}
		if(isInsideArea( 6, poiBegI))	{
			if(isInsideArea( 5, poiEndI))
				return(false);
			else if(isInsideArea( 6, poiEndI))
				return(false);
			else if(isInsideArea( 7, poiEndI))
				return(false);
		}
		if(isInsideArea( 7, poiBegI))	{
			if(isInsideArea( 5, poiEndI))
				return(false);
			else if(isInsideArea( 6, poiEndI))
				return(false);
			else if(isInsideArea( 7, poiEndI))
				return(false);
			else if(isInsideArea( 8, poiEndI))
				return(false);
			else if(isInsideArea( 1, poiEndI))
				return(false);
		}
		if(isInsideArea( 8, poiBegI))	{
			if(isInsideArea( 7, poiEndI))
				return(false);
			else if(isInsideArea( 8, poiEndI))
				return(false);
			else if(isInsideArea( 1, poiEndI))
				return(false);
		}
		return true;
	}
	
	public boolean isInsideArea( int iIdAreaI, CFWPoint poiI)	{
		switch(iIdAreaI)	{
		case 1:
			return(poiI.m_fX <= m_poiLT.m_fX
				 && poiI.m_fY <= m_poiLT.m_fY);
		case 2:
			return(poiI.m_fX >= m_poiLT.m_fX
				 && poiI.m_fX <= m_poiRB.m_fX
				 && poiI.m_fY < m_poiLT.m_fY);
		case 3:
			return(poiI.m_fX >= m_poiRB.m_fX
				 && poiI.m_fY <= m_poiLT.m_fY);
		case 4:
			return(poiI.m_fY >= m_poiLT.m_fY
				 && poiI.m_fY <= m_poiRB.m_fY
				 && poiI.m_fX > m_poiRB.m_fX);
		case 5:
			return(poiI.m_fX >= m_poiRB.m_fX
				 && poiI.m_fY >= m_poiRB.m_fY);
		case 6:
			return(poiI.m_fX >= m_poiLT.m_fX
				 && poiI.m_fX <= m_poiRB.m_fX
				 && poiI.m_fY > m_poiRB.m_fY);
		case 7:
			return(poiI.m_fX <= m_poiLT.m_fX
				 && poiI.m_fY >= m_poiRB.m_fY);
		case 8:
			return(poiI.m_fY >= m_poiLT.m_fY
				 && poiI.m_fY <= m_poiRB.m_fY
				 && poiI.m_fX < m_poiLT.m_fX);
		case 9:
			return(poiI.m_fX >= m_poiLT.m_fX
				 && poiI.m_fX <= m_poiRB.m_fX
				 && poiI.m_fY >= m_poiLT.m_fY
				 && poiI.m_fY <= m_poiRB.m_fY);
		}
		return false;
	}
	
	public boolean equals(CFWRect rectI)	{
		return((this.m_poiLT.equals(rectI.m_poiLT) && this.m_poiRB.equals(rectI.m_poiRB))
			|| (this.m_poiRB.equals(rectI.m_poiLT) && this.m_poiLT.equals(rectI.m_poiRB)));
	}
}
