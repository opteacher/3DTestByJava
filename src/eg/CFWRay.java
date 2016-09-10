package eg;

public class CFWRay extends CFWVector {
	protected CFWPoint m_poiBeg;
	
	public CFWRay()	{
		super();
	}
	
	public CFWRay( CFWPoint poiBegI, CFWPoint poiTmpI)	{
		super( poiBegI, poiTmpI);
		m_poiBeg = poiBegI;
	}
	
	public CFWRay( CFWPoint poiBegI, CFWVector vecDirI)	{
		m_poiBeg = poiBegI;
		m_fX = vecDirI.m_fX;
		m_fY = vecDirI.m_fY;
		m_fZ = vecDirI.m_fZ;
		m_fH = vecDirI.m_fH;
	}
	
	/**
	 * distance between point to line
	 * @param poiI
	 * @return
	 */
	public float distanceFromPoi(CFWPoint poiI)	{
		CFWVector vecDirLn = this;
		CFWVector vecDirPoi = new CFWVector( poiI, m_poiBeg);
		CFWVector vecDirLnToPoi = new CFWVector(vecDirLn.plus(vecDirPoi));
		if(!vecDirLnToPoi.isVertical(vecDirLn))	{
			vecDirLn = vecDirLn.getNegVec();
			vecDirLnToPoi = new CFWVector(vecDirLn.plus(vecDirPoi));
			if(!vecDirLnToPoi.isVertical(vecDirLn))	{
				System.out.println("point ont the line!");
				return(0);
			}
		}
		vecDirLn = vecDirLn.getNegVec();
		float fCos = vecDirLn.cosWithVec(vecDirPoi);
		if(fCos < 0)	{
			vecDirLn = vecDirLn.getNegVec();
			fCos = vecDirLn.cosWithVec(vecDirPoi);
		}
		float fDis = vecDirPoi.getLength();
		float fSin = (float)Math.sqrt(1 - Math.pow( fCos, 2));
		return(fDis*fSin);
	}
	
	/**
	 * the project point of the point
	 * @param poiI
	 * @return
	 */
	public CFWPoint poiProjOnLn(CFWPoint poiI)	{
		CFWVector vecDirPoi = new CFWVector( m_poiBeg, poiI);
		CFWVector vecDirLn = this;
		float fLenPoiToLn = vecDirPoi.getLength();
		if(!vecDirPoi.isSameDir_VerticalIn(vecDirLn))	{
			vecDirLn = this.getNegVec();
		}
		float fCos = vecDirLn.cosWithVec(vecDirPoi);
		float fLenOnLn = fLenPoiToLn*fCos;
		vecDirLn.nor();
		CFWPoint poiRet = new CFWPoint();
		poiRet = m_poiBeg.plus(vecDirLn.multi(fLenOnLn));
		return(poiRet);
	}
	
	public CFWPoint intersectsLn(CFWSegLn lnI) throws Exception	{
		CFWVector vecLn = lnI.getDirVector();
		if(this.isParallel(vecLn))	{
			throw new Exception("W:the ray is parallel to the segment line!");
		}
		//use the direct vector of ray and line and this ray's start point to make out a plane
		CFWVector vecPlnNor = cross(vecLn);
		float fD = vecPlnNor.dot(new CFWVector(m_poiBeg)).sun();
		if(CFWMath.equals( vecPlnNor.dot(new CFWVector(lnI.m_poiBeg)).sun(), fD)
		&& CFWMath.equals( vecPlnNor.dot(new CFWVector(lnI.m_poiEnd)).sun(), fD))	{
			//calculate the cross point
			//use the beg/end point to project to the ray
			CFWPoint poiProj = this.poiProjOnLn(lnI.m_poiBeg);
			//calculate distance of beg/end with the project point
			float fDisProj = new CFWVector( lnI.m_poiBeg, poiProj).getLength();
			this.nor();
			vecLn.nor();
			//make out the distance between beg/end point and the cross point
			float fCosAng = this.cosWithVec(vecLn);
			float fSinAng = (float)Math.sqrt(1 - Math.pow( fCosAng, 2));
			float fDisTar = fDisProj/CFWMath.regulateFloat(fSinAng);
			CFWPoint vecAdd = vecLn.multi(fDisTar);
			CFWVector poiCros = new CFWVector(lnI.m_poiBeg.plus(vecAdd));
			//unequal to the positive direction, check another direction
			CFWVector vecBegCros = new CFWVector(poiCros.sub(m_poiBeg));
			vecBegCros.nor();
			if(!CFWMath.equals( vecPlnNor.dot(poiCros).sun(), fD)
			|| !this.equals(vecBegCros))	{
				CFWPoint vecNegAdd = vecLn.getNegVec().multi(fDisTar);
				poiCros = new CFWVector(lnI.m_poiBeg.plus(vecNegAdd));
				if(!CFWMath.equals( vecPlnNor.dot(poiCros).sun(), fD))	{
					throw new Exception("W:can't find cross point of ray and segment line");
				}
			}
			//check the cross point between two points of the segment line
			if(!CFWMath.isBetweenTwoNum( poiCros.m_fX, lnI.m_poiBeg.m_fX, lnI.m_poiEnd.m_fX, true)
			|| !CFWMath.isBetweenTwoNum( poiCros.m_fY, lnI.m_poiBeg.m_fY, lnI.m_poiEnd.m_fY, true))	{
				throw new Exception("W:the cross point isn't inside the segment line!");
			}
			else	{
				return(poiCros);
			}
		}
		throw new Exception("W:there is no cross point between the ray and segment line");
	}
	
	public boolean intersectsTgl( CFWPoint poiAI, CFWPoint poiBI, CFWPoint poiCI)	{
		//1.check if not insert plane of the triangle
		CFWVector vecEdgeA = new CFWVector(poiAI.sub(poiCI));
		vecEdgeA.nor();
		CFWVector vecEdgeB = new CFWVector(poiBI.sub(poiCI));
		vecEdgeB.nor();
		
		nor();
		CFWVector vecChk = cross(vecEdgeB);
		//if close to 0, ray is parallel
		float fDet = vecEdgeA.dot(vecChk).sun();
		if(Math.abs(fDet) < CFWMath.s_fValMin
		&& Math.abs(fDet) > -CFWMath.s_fValMin)	{
			return false;
		}
		
		//distance to plane, <0 means ray behind the plane
		CFWVector vecTA = new CFWVector(m_poiBeg.sub(poiAI));
		vecTA.nor();
		if(isSameDir_VerticalIn(vecTA))
			return false;
		
		/*CFWVector vecTB = vecTA.cross(vecEdgeB);
		float fDirB = dot(vecTB).sun();
		if(fDirB < 0.0f || fDirB+fDirA > fDet)
			return false;*/
		
		//2.project the triangle to plane XY, follow the ray
		float fNumOffA = -poiAI.m_fZ/m_fZ;
		CFWPoint poiAProj = new CFWPoint(
				poiAI.m_fX + m_fX*fNumOffA,
				poiAI.m_fY + m_fY*fNumOffA,
				0.0f);
		float fNumOffB = -poiBI.m_fZ/m_fZ;
		CFWPoint poiBProj = new CFWPoint(
				poiBI.m_fX + m_fX*fNumOffB,
				poiBI.m_fY + m_fY*fNumOffB,
				0.0f);
		float fNumOffC = -poiCI.m_fZ/m_fZ;
		CFWPoint poiCProj = new CFWPoint(
				poiCI.m_fX + m_fX*fNumOffC,
				poiCI.m_fY + m_fY*fNumOffC,
				0.0f);
		//3.check the three points are locate on a line
		CFWVector vecTmpA = new CFWVector(poiAProj.sub(poiCProj));
		CFWVector vecTmpB = new CFWVector(poiBProj.sub(poiCProj));
		float fNumOffRay = 0.0f;
		CFWPoint poiRayProj;
		if(vecTmpA.isParallel(vecTmpB) || 0 == m_fZ)	{
			//three points in a same line
			//choise another plane (XZ) for projecting
			fNumOffA = -poiAI.m_fY/m_fY;
			poiAProj = new CFWPoint(
					poiAI.m_fX + m_fX*fNumOffA,
					poiAI.m_fZ + m_fZ*fNumOffA,
					0.0f);
			fNumOffB = -poiBI.m_fY/m_fY;
			poiBProj = new CFWPoint(
					poiBI.m_fX + m_fX*fNumOffB,
					poiBI.m_fZ + m_fZ*fNumOffB,
					0.0f);
			fNumOffC = -poiCI.m_fY/m_fY;
			poiCProj = new CFWPoint(
					poiCI.m_fX + m_fX*fNumOffC,
					poiCI.m_fZ + m_fZ*fNumOffC,
					0.0f);
			//check the three points are locate on a line
			vecTmpA = new CFWVector(poiAProj.sub(poiCProj));
			vecTmpB = new CFWVector(poiBProj.sub(poiCProj));
			if(vecTmpA.isParallel(vecTmpB) || 0 == m_fY)	{
				//three points in a same line
				//choise the last plane (YZ) for projecting
				fNumOffA = -poiAI.m_fX/m_fX;
				poiAProj = new CFWPoint(
						poiAI.m_fY + m_fY*fNumOffA,
						poiAI.m_fZ + m_fZ*fNumOffA,
						0.0f);
				fNumOffB = -poiBI.m_fX/m_fX;
				poiBProj = new CFWPoint(
						poiBI.m_fY + m_fY*fNumOffB,
						poiBI.m_fZ + m_fZ*fNumOffB,
						0.0f);
				fNumOffC = -poiCI.m_fX/m_fX;
				poiCProj = new CFWPoint(
						poiCI.m_fY + m_fY*fNumOffC,
						poiCI.m_fZ + m_fZ*fNumOffC,
						0.0f);
				//check the three points are locate on a line
				vecTmpA = new CFWVector(poiAProj.sub(poiCProj));
				vecTmpB = new CFWVector(poiBProj.sub(poiCProj));
				if(vecTmpA.isParallel(vecTmpB) || 0 == m_fX)	{
					System.out.println("the triangle's three points are all zero points!");
					return false;
				}
				else	{
					//calculate the point that the ray insert
					fNumOffRay = -m_poiBeg.m_fX/m_fX;
					poiRayProj = new CFWPoint(
							m_poiBeg.m_fY + m_fY*fNumOffRay,
							m_poiBeg.m_fZ + m_fZ*fNumOffRay,
							0.0f);
				}
			}
			else	{
				//calculate the point that the ray insert
				fNumOffRay = -m_poiBeg.m_fY/m_fY;
				poiRayProj = new CFWPoint(
						m_poiBeg.m_fX + m_fX*fNumOffRay,
						m_poiBeg.m_fZ + m_fZ*fNumOffRay,
						0.0f);
			}
		}
		else	{
			//calculate the point that the ray insert
			fNumOffRay = -m_poiBeg.m_fZ/m_fZ;
			poiRayProj = new CFWPoint(
					m_poiBeg.m_fX + m_fX*fNumOffRay,
					m_poiBeg.m_fY + m_fY*fNumOffRay,
					0.0f);
		}
		//4.check point inside
		//use three projected points to build 3 line function
		//AB
		float fLnABK = 0.0f;
		float fLnABD = 0.0f;
		CFWPoint poiTmp = poiBProj.sub(poiAProj);
		boolean bABVerticalX = false;
		if(0 == poiTmp.m_fX)	{
			fLnABK = 1.0f;
			fLnABD = poiBProj.m_fX;
			bABVerticalX = true;
		}
		else	{
			fLnABK = poiTmp.m_fY/poiTmp.m_fX;
			fLnABD = poiAProj.m_fY - fLnABK*poiAProj.m_fX;
		}
		//BC
		float fLnBCK = 0.0f;
		float fLnBCD = 0.0f;
		poiTmp = poiCProj.sub(poiBProj);
		boolean bBCVerticalX = false;
		if(0 == poiTmp.m_fX)	{
			fLnBCK = 1.0f;
			fLnBCD = poiCProj.m_fX;
			bBCVerticalX = true;
		}
		else	{
			fLnBCK = poiTmp.m_fY/poiTmp.m_fX;
			fLnBCD = poiBProj.m_fY - fLnBCK*poiBProj.m_fX;
		}
		//AC
		float fLnACK = 0.0f;
		float fLnACD = 0.0f;
		poiTmp = poiAProj.sub(poiCProj);
		boolean bACVerticalX = false;
		if(0 == poiTmp.m_fX)	{
			fLnACK = 1.0f;
			fLnACD = poiAProj.m_fX;
			bACVerticalX = true;
		}
		else	{
			fLnACK = poiTmp.m_fY/poiTmp.m_fX;
			fLnACD = poiAProj.m_fY - fLnACK*poiAProj.m_fX;
		}
		//5.check the point that the ray inserted whether insert the triangle
		//AB
		if(bABVerticalX)	{
			//check which side is inside
			int iFlag = 0;
			if(poiCProj.m_fX > fLnABD)
				iFlag = 1;
			else
				iFlag = -1;
			//check the true ray projecting point
			if(poiRayProj.m_fX == fLnABD)	{
				return true;
			}
			switch(iFlag)	{
			case 1:
				if(poiRayProj.m_fX < fLnABD)	{
					return false;
				}
				break;
			case -1:
				if(poiRayProj.m_fX > fLnABD)	{
					return false;
				}
				break;
			}
		}
		else	{
			//check which side is inside
			int iFlag = 0;
			float fResult = poiCProj.m_fX*fLnABK + fLnABD;
			if(fResult > poiCProj.m_fY)
				iFlag = 1;
			else
				iFlag = -1;
			//check the true ray projecting point
			fResult = poiRayProj.m_fX*fLnABK + fLnABD;
			if(fResult == poiRayProj.m_fY)	{
				return true;
			}
			switch(iFlag)	{
			case 1:	
				if(fResult < poiRayProj.m_fY)	{
					return false;
				}
				break;
			case -1:
				if(fResult > poiRayProj.m_fY)	{
					return false;
				}
				break;
			}
		}
		//BC
		if(bBCVerticalX)	{
			//check which side is inside
			int iFlag = 0;
			if(poiAProj.m_fX > fLnBCD)
				iFlag = 1;
			else
				iFlag = -1;
			//check the true ray projecting point
			if(poiRayProj.m_fX == fLnBCD)	{
				return true;
			}
			switch(iFlag)	{
			case 1:
				if(poiRayProj.m_fX < fLnBCD)	{
					return false;
				}
				break;
			case -1:
				if(poiRayProj.m_fX > fLnBCD)	{
					return false;
				}
				break;
			}
		}
		else	{
			//check which side is inside
			int iFlag = 0;
			float fResult = poiAProj.m_fX*fLnBCK + fLnBCD;
			if(fResult > poiAProj.m_fY)
				iFlag = 1;
			else
				iFlag = -1;
			//check the true ray projecting point
			fResult = poiRayProj.m_fX*fLnBCK + fLnBCD;
			if(fResult == poiRayProj.m_fY)	{
				return true;
			}
			switch(iFlag)	{
			case 1:	
				if(fResult < poiRayProj.m_fY)	{
					return false;
				}
				break;
			case -1:
				if(fResult > poiRayProj.m_fY)	{
					return false;
				}
				break;
			}
		}
		//AC
		if(bACVerticalX)	{
			//check which side is inside
			int iFlag = 0;
			if(poiBProj.m_fX > fLnACD)
				iFlag = 1;
			else
				iFlag = -1;
			//check the true ray projecting point
			if(poiRayProj.m_fX == fLnACD)	{
				return true;
			}
			switch(iFlag)	{
			case 1:
				if(poiRayProj.m_fX < fLnACD)	{
					return false;
				}
				break;
			case -1:
				if(poiRayProj.m_fX > fLnACD)	{
					return false;
				}
				break;
			}
		}
		else	{
			//check which side is inside
			int iFlag = 0;
			float fResult = poiBProj.m_fX*fLnACK + fLnACD;
			if(fResult > poiBProj.m_fY)
				iFlag = 1;
			else
				iFlag = -1;
			//check the true ray projecting point
			fResult = poiRayProj.m_fX*fLnACK + fLnACD;
			if(fResult == poiRayProj.m_fY)	{
				return true;
			}
			switch(iFlag)	{
			case 1:	
				if(fResult < poiRayProj.m_fY)	{
					return false;
				}
				break;
			case -1:
				if(fResult > poiRayProj.m_fY)	{
					return false;
				}
				break;
			}
		}
		return true;
	}
	
	public CFWPoint intersectsTgl(CFWTriangle tglI) throws Exception	{
		CFWPoint poiRet = intersectsPln(tglI);
		
		//3.check inside the triangle
		if(tglI.isPoiInside(poiRet))	{
			return(poiRet);
		}
		
		throw new Exception("W:there's no intersection point on the triangle!");
	}
	
	public CFWPoint intersectsRay(CFWRay rayI) throws Exception	{
		this.nor();
		rayI.nor();
		if(this.isParallel(rayI))	{
			throw new Exception("W:parallel each other! has not intersection point!");
		}
		
		float fK = 0;
		float fValUp = 0;
		float fValDn = rayI.m_fX*this.m_fY - this.m_fX*rayI.m_fY;
		if(0 == fValDn)	{
			fValDn = rayI.m_fY*this.m_fZ - this.m_fY*rayI.m_fZ;
			if(0 == fValDn)	{
				fValDn = rayI.m_fX*this.m_fZ - this.m_fX*rayI.m_fZ;
				if(0 == fValDn)	{
					throw new Exception("W:there no intersection point between the two points!");
				}
				else	{
					fValUp = this.m_fZ*this.m_poiBeg.m_fX - this.m_fZ*rayI.m_poiBeg.m_fX + 
							 this.m_fX*rayI.m_poiBeg.m_fZ - this.m_fX*this.m_poiBeg.m_fZ;
				}
			}
			else	{
				fValUp = this.m_fZ*this.m_poiBeg.m_fY - this.m_fZ*rayI.m_poiBeg.m_fY + 
						 this.m_fY*rayI.m_poiBeg.m_fZ - this.m_fY*this.m_poiBeg.m_fZ;
			}
		}
		else	{
			fValUp = this.m_fY*this.m_poiBeg.m_fX - this.m_fY*rayI.m_poiBeg.m_fX + 
					 this.m_fX*rayI.m_poiBeg.m_fY - this.m_fX*this.m_poiBeg.m_fY;
		}
		fK = fValUp/fValDn;
		
		CFWPoint poiRet = new CFWPoint();
		poiRet = rayI.multi(fK).plus(rayI.m_poiBeg);
		poiRet.regulate();
		
		//check on the other ray
		/*CFWPoint poiTst = poiRet.sub(this.m_poiBeg);
		if(0 != this.m_fX)
			poiTst.m_fX = poiTst.m_fX/this.m_fX;
		if(0 != this.m_fY)
			poiTst.m_fY = poiTst.m_fY/this.m_fY;
		if(0 != this.m_fZ)
			poiTst.m_fZ = poiTst.m_fZ/this.m_fZ;
		if(!CFWMath.equals( poiTst.m_fX, poiTst.m_fY) || !CFWMath.equals( poiTst.m_fY, poiTst.m_fZ))	{
			throw new Exception("E:generated point isn't on the ray!");
		}*/
		
		return(poiRet);
	}
	
	public CFWPoint intersectsPln(CFWPlane plnI) throws Exception	{
		if(this.isZeroVec())	{
			throw new IllegalArgumentException("W:ray's direction is zero vector!");
		}
		
		this.nor();
		this.regulate();
		//if close to 0, ray is parallel
		float fDet = this.dot(plnI.getNorVec()).sun();
		if(CFWMath.equals( Math.abs(fDet), 0))	{
			throw new Exception("W:there's no intersection point on the triangle!");
		}
		
		//distance to plane, <0 means ray behind the plane
		CFWVector vecNor = plnI.getNorVec();
		float fValCalc = plnI.calculate( this.m_poiBeg.m_fX, this.m_poiBeg.m_fY, this.m_poiBeg.m_fZ);
		if((this.isSameDir_VerticalIn(vecNor) && fValCalc > plnI.m_fD)
		|| (!this.isSameDir_VerticalIn(vecNor) && fValCalc < plnI.m_fD))	{
			throw new Exception("W:there's no intersection point on the triangle!");
		}
		/*CFWVector vecTA = new CFWVector(m_poiBeg.sub(tglI.m_poiA));
		vecTA.nor();
		if(this.isSameDir_VerticalIn(vecTA))	{
			throw new Exception("W:there's no intersection point on the triangle!");
		}*/
		
		/*float fAngCos = Math.abs(this.cosWithVec(vecNor));
		//ray is vertical to the plane
		CFWPoint poiRet = new CFWPoint();
		float fValLeft = plnI.calculate( m_poiBeg.m_fX, m_poiBeg.m_fY, m_poiBeg.m_fZ);
		float fValRight = plnI.getD();
		float fValDown = (float)Math.sqrt(this.dot(this).sun());
		float fDisBegPoiToPln = Math.abs(fValLeft - fValRight)/fValDown;
		if(0 == fAngCos)	{
			CFWPoint vecAdd = this.multi(fDisBegPoiToPln);
			poiRet = m_poiBeg.plus(vecAdd);
		}
		else	{
			float fOffAdd = fDisBegPoiToPln/fAngCos;
			CFWPoint vecAdd = this.multi(fOffAdd);
			poiRet = m_poiBeg.plus(vecAdd);
		}*/
		
		float fValLeft = plnI.getD();
		float fValRight = plnI.calculate( m_poiBeg.m_fX, m_poiBeg.m_fY, m_poiBeg.m_fZ);
		float fValDown = plnI.calculate( m_fX, m_fY, m_fZ);
		
		if(0 == fValDown)	{
			throw new Exception("W:no intersect points");
		}
		float fK = (fValLeft - fValRight)/fValDown;
		CFWPoint poiRet = this.m_poiBeg.plus(this.multi(fK));
		
		return(poiRet);
	}
}
