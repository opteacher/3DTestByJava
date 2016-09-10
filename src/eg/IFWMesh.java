package eg;

import java.awt.Color;
import java.awt.Point;
import java.util.Vector;

public interface IFWMesh {
	static public final int s_iTriangle = 0;
	static public final int s_iPoint = 1;
	static public final int s_iLine = 2;
	static public final int s_iPlane = 3;
	
	static public final int s_iVertex = 0;
	static public final int s_iVerCol = 1;
	static public final int s_iVerUV = 2;
	
	static public final int s_iTexPln = 0;
	static public final int s_iTexBox = 1;
	static public final int s_iTexBoll = 2;
	/**
	 * location vertex
	 * @author zhaojiachend5
	 *
	 */
	public class SFWVertex {
		public float m_fX;
		public float m_fY;
		public float m_fZ;
		public CFWVector m_vecNor;
		
		public SFWVertex()	{
			m_fX = 0.0f;
			m_fY = 0.0f;
			m_fZ = 0.0f;
			m_vecNor = new CFWVector();
		}
		
		public SFWVertex( float fXI, float fYI, float fZI)	{
			m_fX = fXI;
			m_fY = fYI;
			m_fZ = fZI;
			m_vecNor = new CFWVector();
		}
		
		public CFWPoint getLoc()	{
			return(new CFWPoint( m_fX, m_fY, m_fZ));
		}
	}
	
	/**
	 * color vertex
	 * @author Administrator
	 *
	 */
	public class SFWVerCol extends SFWVertex	{
		public Color m_color;
		
		public SFWVerCol( float fXI, float fYI, float fZI, Color colI)	{
			super( fXI, fYI, fZI);
			m_color = colI;
		}
	}
	
	/**
	 * skin vertex
	 * @author zhaojiachend5
	 *
	 */
	public class SFWVerUV extends SFWVertex {
		public float m_fU;
		public float m_fV;
		
		public SFWVerUV()	{
			super();
			m_fU = -1;
			m_fV = -1;
		}
		
		public SFWVerUV( float fXI, float fYI, float fZI, float fUI, float fVI)	{
			super( fXI, fYI, fZI);
			m_fU = fUI;
			m_fV = fVI;
		}
	}
	
	/**
	 * create attribute class
	 * @author zhaojiachend5
	 *
	 */
	public class SFWAttribute	{
		public CFWPoint m_poiLoc;
		public int m_iPoiType;
		
		public Color m_color;
		public float m_fU;
		public float m_fV;
		
		public SFWAttribute(CFWPoint poiI)	{
			m_poiLoc = poiI;
			m_iPoiType = s_iVertex;
			m_color = Color.black;
			m_fU = -1; m_fV = -1;
		}
		
		public SFWAttribute( CFWPoint poiI, Color colI)	{
			m_poiLoc = poiI;
			m_iPoiType = s_iVerCol;
			m_color = colI;
			m_fU = -1; m_fV = -1;
		}
		
		public SFWAttribute( CFWPoint poiI, float fUI, float fVI)	{
			m_poiLoc = poiI;
			m_iPoiType = s_iVerUV;
			m_fU = fUI; m_fV = fVI;
		}
	}
	
	/**
	 * face data
	 * @author zhaojiachend5
	 *
	 */
	public class SFWFace	{
		public String m_strMshBelog;
		public SFWVertex m_verA;
		public SFWVertex m_verB;
		public SFWVertex m_verC;
		public SFWFace m_fcAB;
		public SFWFace m_fcBC;
		public SFWFace m_fcCA;
		public CFWVector m_vecNor;
		public boolean m_bDrawAB;
		public boolean m_bDrawBC;
		public boolean m_bDrawCA;
		public int m_iPoiType;
		
		public SFWFace(int iPoiTI)	{
			m_strMshBelog = "";
			m_iPoiType = iPoiTI;
			m_fcAB = null;
			m_fcBC = null;
			m_fcCA = null;
			m_bDrawAB = true;
			m_bDrawBC = true;
			m_bDrawCA = true;
		}
		
		public SFWFace( SFWVertex verAI, SFWVertex verBI, SFWVertex verCI, int iPoiTI)	{
			m_verA = verAI;
			m_verB = verBI;
			m_verC = verCI;
			m_fcAB = null;
			m_fcBC = null;
			m_fcCA = null;
			m_iPoiType = iPoiTI;
			m_bDrawAB = true;
			m_bDrawBC = true;
			m_bDrawCA = true;
		}
		
		public void chkBeside(SFWFace fcI)	{
			CFWPoint aPoiTs[] = new CFWPoint[3];
			aPoiTs[0] = new CFWPoint( m_verA.m_fX, m_verA.m_fY, m_verA.m_fZ);
			aPoiTs[1] = new CFWPoint( m_verB.m_fX, m_verB.m_fY, m_verB.m_fZ);
			aPoiTs[2] = new CFWPoint( m_verC.m_fX, m_verC.m_fY, m_verC.m_fZ);
			
			CFWPoint aPoiPm[] = new CFWPoint[3];
			aPoiPm[0] = new CFWPoint( fcI.m_verA.m_fX, fcI.m_verA.m_fY, fcI.m_verA.m_fZ);
			aPoiPm[1] = new CFWPoint( fcI.m_verB.m_fX, fcI.m_verB.m_fY, fcI.m_verB.m_fZ);
			aPoiPm[2] = new CFWPoint( fcI.m_verC.m_fX, fcI.m_verC.m_fY, fcI.m_verC.m_fZ);
			
			//normally it should be two, if they are beside to each other
			//but they may be completely same, that means the two face(triangle) are in the same location
			Vector<Point> vecEqlPair = new Vector<Point>();
			for( int i = 0; i < 3; ++i)	{
				for( int j = 0; j < 3; ++j)	{
					if(aPoiTs[i].equals(aPoiPm[j]))	{
						vecEqlPair.add(new Point( i, j));
					}
				}
			}
			
			switch(vecEqlPair.size())	{
			case 3:
				this.m_fcAB = fcI;
				this.m_fcBC = fcI;
				this.m_fcCA = fcI;
				
				fcI.m_fcAB = this;
				fcI.m_fcBC = this;
				fcI.m_fcCA = this;
				
				break;
			case 2://0 = A, 1 = B, 2 = C
				Point poiFst = vecEqlPair.get(0);
				Point poiSec = vecEqlPair.get(1);
				
				if((poiFst.x == 0 && poiSec.x == 1)
				|| (poiFst.x == 1 && poiSec.x == 0))	{
					this.m_fcAB = fcI;
				}
				else if((poiFst.x == 1 && poiSec.x == 2)
					||  (poiFst.x == 2 && poiSec.x == 1))	{
					this.m_fcBC = fcI;
				}
				else if((poiFst.x == 2 && poiSec.x == 0)
					||  (poiFst.x == 0 && poiSec.x == 2))	{
					this.m_fcCA = fcI;
				}
				
				if((poiFst.y == 0 && poiSec.y == 1)
				|| (poiFst.y == 1 && poiSec.y == 0))	{
					fcI.m_fcAB = this;
				}
				else if((poiFst.y == 1 && poiSec.y == 2)
					||  (poiFst.y == 2 && poiSec.y == 1))	{
					fcI.m_fcBC = this;
				}
				else if((poiFst.y == 2 && poiSec.y == 0)
					||  (poiFst.y == 0 && poiSec.y == 2))	{
					fcI.m_fcCA = this;
				}
				
				break;
			}
		}
		
		/**
		 * after transfer by the matrix, the point is inside the face triangle
		 * @param poiI
		 * @param matI
		 * @return
		 * @throws Exception 
		 */
		public CFWVector getPoiNor( CFWPoint poiI, CFWMatrix matI) throws Exception	{
			//check data
			CFWTriangle tglCur = null;
			CFWPoint poiA = matI.multiPoiLeft(m_verA.getLoc());
			CFWPoint poiB = matI.multiPoiLeft(m_verB.getLoc());
			CFWPoint poiC = matI.multiPoiLeft(m_verC.getLoc());
			tglCur = new CFWTriangle( poiA, poiB, poiC);
			if(!tglCur.isPoiInside(poiI))	{
				throw new Exception("W:point is not inside face!");
			}
			
			CFWVector vecRet = null;
			
			CFWVector vecNorA = matI.multiVecLeft(m_verA.m_vecNor);	vecNorA.nor();
			CFWVector vecNorB = matI.multiVecLeft(m_verB.m_vecNor);	vecNorB.nor();
			CFWVector vecNorC = matI.multiVecLeft(m_verC.m_vecNor);	vecNorC.nor();
			if(poiA.equals(poiI))	{
				vecRet = vecNorA;
			}
			else if(poiB.equals(poiI))	{
				vecRet = vecNorB;
			}
			else if(poiC.equals(poiI))	{
				vecRet = vecNorC;
			}
			else	{
				CFWVector vecAToCur = new CFWVector( poiA, poiI);	vecAToCur.nor();
				CFWVector vecBToCur = new CFWVector( poiB, poiI);	vecBToCur.nor();
				CFWVector vecCToCur = new CFWVector( poiC, poiI);	vecCToCur.nor();

				CFWVector vecAddA = vecAToCur.plus(vecNorA);
				CFWVector vecAddB = vecBToCur.plus(vecNorB);
				CFWVector vecAddC = vecCToCur.plus(vecNorC);
				
				vecRet = vecAddA.plus(vecAddB.plus(vecAddC));
				vecRet.nor();
			}
			
			return(vecRet);
		}
	}
	
	/**
	 * get all faces of the mesh
	 * +_+:normal vector should be calculate and fill in
	 * @return
	 */
	public Vector<SFWFace> getEveFace();
	public int getMshType();
	public String getSknId();
	public void setSkin(String strSknI);
	public void setColor(Color colI);
	public void generPoiNor();
	public void generAdjInfo();
}
