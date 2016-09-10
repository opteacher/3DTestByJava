package mn;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import eg.CFWLine2D;
import eg.CFWMath;
import eg.CFWPoint;
import eg.CFWPool;
import eg.CFWRect;
import eg.CFWScene;
import eg.CFWSysRender;
import eg.CFWVector;
import eg.IFWDevice;
import eg.IFWObject;

public class CFWJDevice implements IFWDevice {
	static private final float s_fUnDispDepth = 1;
	private Graphics m_devGraph;
	private int m_iSzPixel;
	private int m_iRefshRate;
	private int m_iWidth;
	private int m_iHeight;
	
	
	private Color[][] m_aBackBuf;
	private Color m_colBackGd;
	private int m_iBufWidth;
	private int m_iBufHeight;
	private float[][] m_aDeepBuf;
	
	protected HashMap<String, Boolean> m_mpLights;
	protected HashMap<String, float[][]> m_mpShadowBuf;
	
	public CFWJDevice()	{
		m_iSzPixel = 1;
		m_iRefshRate = 60;
		m_colBackGd = Color.white;
		m_devGraph = null;
		m_mpLights = new HashMap<String, Boolean>();
		
	}
	public CFWJDevice(Graphics g)	{
		m_iSzPixel = 1;
		m_iRefshRate = 60;
		m_devGraph = g;
		m_colBackGd = Color.white;
		m_mpLights = new HashMap<String, Boolean>();
	}
	
	/**
	 * get refresh rate, to define 1 second how many times refreshing
	 */
	public int getRefreshRate()	{
		return(m_iRefshRate);
	}
	/**
	 * get inside device
	 */
	public Object getBaseDev()	{
		return(m_devGraph);
	}
	
	/**
	 * get/set back buffer W&H
	 */
	public void setBkBufSize( int iWidthI, int iHeightI)	{
		if(0 == iWidthI || 0 == iHeightI)	{
			System.out.println("input back buffer's W&H are zero!");
			return;
		}
		m_aBackBuf = new Color[iWidthI][iHeightI];
		m_aDeepBuf = new float[iWidthI][iHeightI];
		m_iBufWidth = iWidthI;
		m_iBufHeight = iHeightI;
	}
	public int getBkBufWidth()	{
		return(m_iBufWidth);
	}
	public int getBkBufHeight()	{
		return(m_iBufHeight);
	}
	/**
	 * get/set screen W&H
	 */
	public void setScnSize( int iWidthI, int iHeightI)	{
		if(0 == iWidthI || 0 == iHeightI)	{
			System.out.println("input screen's W&H are zero!");
			return;
		}
		m_iWidth = iWidthI;
		m_iHeight = iHeightI;
	}
	public int getScnWidth()	{
		return(m_iWidth);
	}
	public int getScnHeight()	{
		return(m_iHeight);
	}
	/**
	 * clear the screen
	 */
	protected void clsScreen()	{
		//1.check render data
		if(0 == m_iWidth || 0 == m_iHeight)	{
			System.out.println("no data for render! or error data!");
			return;
		}
		//2.fill the whole screen array
		m_devGraph.setColor(m_colBackGd);
		for( int i = 0; i < m_iWidth; ++i)
			for( int j = 0; j < m_iHeight; ++j)	{
				m_devGraph.fillRect( i, j, m_iSzPixel, m_iSzPixel);
			}
	}
	/**
	 * clear back buffer
	 */
	public void clsBackBuf()	{
		//1.check data
		if(0 == m_aBackBuf.length
		|| 0 == m_aBackBuf[0].length
		|| m_aBackBuf.length != m_iBufWidth
		|| m_aBackBuf[0].length != m_iBufHeight)	{
			System.out.println("back buffer size error!");
			return;
		}
		//2.clear up
		for( int i = 0; i < m_iBufWidth; ++i)
			for( int j = 0; j < m_iBufHeight; ++j)	{
				m_aBackBuf[i][j] = m_colBackGd;
				m_aDeepBuf[i][j] = s_fUnDispDepth;	
			}
		
		//3.clear up shadow buffer
		if(m_mpShadowBuf == null)
		{
			return;
		}
		Iterator<Entry<String, float[][]>> iterTmp = m_mpShadowBuf.entrySet().iterator();
		while(iterTmp.hasNext())	{
			Entry<String, float[][]> entyCur = iterTmp.next();
			
			float[][] aShdoCur = entyCur.getValue();
			if(null == aShdoCur
			|| aShdoCur.length != m_iBufWidth
			|| aShdoCur[0].length != m_iBufHeight)	{
				aShdoCur = new float[m_iBufWidth][m_iBufHeight];
			}
			
			for( int i = 0; i < m_iBufWidth; ++i)
				for( int j = 0; j < m_iBufHeight; ++j)	{
					aShdoCur[i][j] = s_fShdwDisp;
				}
		}
	}
	/**
	 * get/set back ground color
	 */
	public void setBackGround(Color colBkI)	{
		m_colBackGd = colBkI;
	}
	public Color getBackGround()	{
		return(m_colBackGd);
	}
	
	/**
	 * deepth test
	 * +_+:the deepth should subtract to the distance with view location and project plane first!
	 * 	PS: camCur.m_fDisScn
	 */
	public boolean checkPixelDeepth( int iXI, int iYI, float fDeepI)	{
		//1.check render data
		if(!CFWMath.isBetweenTwoNum( iXI, 0, m_iBufWidth, true)
		|| !CFWMath.isBetweenTwoNum( iYI, 0, m_iBufHeight, true))	{
			System.out.println("pixel location is out of deepth buffer!");
			return false;
		}
		if(!CFWSysRender.getInstance().isEnableDepth())	{
			return true;
		}
		if(0 == m_aDeepBuf.length
		|| 0 == m_aDeepBuf[0].length)	{
			System.out.println("deepth buffer is empty!");
			return false;
		}
		
		//2.if the deepth is uninitlize, update
		if(s_fUnDispDepth == m_aDeepBuf[iXI][iYI] && fDeepI <= s_fDefDepth)	{
			m_aDeepBuf[iXI][iYI] = fDeepI;
			return true;
		}
		
		//3.deepth equals to zero, mean the pixel on the project plane
		// display no doubt
		if(m_aDeepBuf[iXI][iYI] < fDeepI && fDeepI <= s_fDefDepth)	{
			m_aDeepBuf[iXI][iYI] = fDeepI;
			return true;
		}
		
		return false;
	}
	
	/**
	 * set pixel(deepth)
	 */
	public void setPixel( int iXI, int iYI, float fZI, Color colSetI)	{
		//1.check render data
		if(0 == m_aBackBuf.length
		|| 0 == m_aBackBuf[0].length)	{
			System.out.println("back buffer is empty!");
			return;
		}
		if(!CFWMath.isBetweenTwoNum( iXI, 0, m_iBufWidth, false)
		|| !CFWMath.isBetweenTwoNum( iYI, 0, m_iBufHeight, false)
		&& (0 != iXI && 0 != iYI))	{
			System.out.println("pixel location is out of back buffer!");
			return;
		}
		//2.fill the screen array
		if(checkPixelDeepth( iXI, iYI, fZI))	{
			if(1 == m_iSzPixel)	{
				m_aBackBuf[iXI][iYI] = colSetI;
			}
			else	{
				int iNumBlank = 0;
				for( int i = iYI - m_iSzPixel; i <= iYI + m_iSzPixel; ++i)	{
					
					for( int j = iXI - iNumBlank; j <= iXI + iNumBlank; ++j)	{
						if(!CFWMath.isBetweenTwoNum( j, 0, m_iBufWidth, false)
						|| !CFWMath.isBetweenTwoNum( i, 0, m_iBufHeight, false)
						&& (0 != j && 0 != i))	{
							continue;
						}
						
						m_aBackBuf[j][i] = colSetI;
					}
					
					if(i < iYI)	{
						++iNumBlank;
					}
					else	{
						--iNumBlank;
					}
				}
			}
		}
	}
	
	/**
	 * draw line
	 */
	public void setLine( int iXPoiBegI, int iYPoiBegI, float fZPoiBegI,
			int iXPoiEndI, int iYPoiEndI, float fZPoiEndI,  Color colSetI) throws Exception	{
		//1.check data
		if(0 == m_aBackBuf.length
		|| 0 == m_aBackBuf[0].length
		|| m_aBackBuf.length != m_iBufWidth
		|| m_aBackBuf[0].length != m_iBufHeight)	{
			System.out.println("back buffer size error!");
			return;
		}
		if((iXPoiBegI < 0 || iXPoiBegI > m_iBufWidth)
		|| (iYPoiBegI < 0 || iYPoiBegI > m_iBufHeight)
		|| (iXPoiEndI < 0 || iXPoiEndI > m_iBufWidth)
		|| (iYPoiEndI < 0 || iYPoiEndI > m_iBufHeight))	{
			//System.out.println("data of the line is out of the screen!");
			CFWPoint poiBeg = new CFWPoint( iXPoiBegI, iYPoiBegI, 0);
			CFWPoint poiEnd = new CFWPoint( iXPoiEndI, iYPoiEndI, 0);
			CFWRect rectScn = new CFWRect(
					new CFWPoint( 0, 0, 0),
					new CFWPoint( m_iBufWidth, m_iBufHeight, 0));
			
			CFWRect rectResult = rectScn.intersectsLine( poiBeg, poiEnd);
			iXPoiBegI = (int)rectResult.m_poiLT.m_fX;
			iYPoiBegI = (int)rectResult.m_poiLT.m_fY;
			iXPoiEndI = (int)rectResult.m_poiRB.m_fX;
			iYPoiEndI = (int)rectResult.m_poiRB.m_fY;
		}
		//2.calculate the different between begin and end
		float fDisX = iXPoiEndI - iXPoiBegI;
		float fDisY = iYPoiEndI - iYPoiBegI;
		float fSecX = 1;
		float fSecY = 1;
		if(iYPoiEndI == iYPoiBegI)	{
			//test the deepth
			CFWLine2D lnOnXZ = new CFWLine2D(
					new CFWPoint( iXPoiBegI, fZPoiBegI, 0),
					new CFWPoint( iXPoiEndI, fZPoiEndI, 0));
			for( int iIndX = Math.min( iXPoiBegI, iXPoiEndI);
			iIndX < Math.max( iXPoiBegI, iXPoiEndI); ++iIndX)	{
				this.setPixel( iIndX, iYPoiEndI, lnOnXZ.mkOutY(iIndX), colSetI);
			}
			return;
		}
		else if(iXPoiEndI == iXPoiBegI)	{
			CFWLine2D lnOnYZ = new CFWLine2D(
					new CFWPoint( iYPoiBegI, fZPoiBegI, 0),
					new CFWPoint( iYPoiEndI, fZPoiEndI, 0));
			for( int iIndY = Math.min( iYPoiBegI, iYPoiEndI);
			iIndY < Math.max( iYPoiBegI, iYPoiEndI); ++iIndY)	{
				if(checkPixelDeepth( iXPoiEndI, iIndY, lnOnYZ.mkOutY(iIndY)))	{
					this.setPixel( iXPoiEndI, iIndY, lnOnYZ.mkOutY(iIndY), colSetI);
				}
			}
			return;
		}
		else	{
			if(fDisY < 0)
				fSecY = -1;
			if(fDisX < 0)
				fSecX = -1;
		}
		//4.draw
		CFWVector vecDir = new CFWVector();
		vecDir.m_fX = iXPoiEndI - iXPoiBegI;
		vecDir.m_fY = iYPoiEndI - iYPoiBegI;
		vecDir.m_fZ = fZPoiEndI - fZPoiBegI;
		vecDir.nor();
		m_aBackBuf[iXPoiBegI][iYPoiBegI] = colSetI;
		float fK = fDisY/fDisX;
		float fB = iYPoiBegI - fK*iXPoiBegI;
		if(Math.abs(fDisX) < Math.abs(fDisY))	{
			for( int j = iYPoiBegI; j != iYPoiEndI && (j < m_iBufHeight && j >=0); j += fSecY)	{
				float fXTmp = (j - fB)/fK;
				float fDecPart = fXTmp - (int)fXTmp;
				float fZCur = vecDir.m_fZ*(j - iYPoiBegI)/vecDir.m_fY + fZPoiBegI;
				if(Math.abs(fDecPart) > 0.5)	{
					this.setPixel( (int)(fXTmp + 1), j, fZCur, colSetI);
				}
				else	{
					this.setPixel( (int)fXTmp, j, fZCur, colSetI);
				}
			}
		}
		else	{
			for( int i = iXPoiBegI; i != iXPoiEndI && (i < m_iBufWidth && i >= 0); i += fSecX)	{
				float fYTmp = fK*i + fB;
				float fDecPart = fYTmp - (int)fYTmp;
				float fZCur = vecDir.m_fZ*(i - iXPoiBegI)/vecDir.m_fX + fZPoiBegI;
				if(Math.abs(fDecPart) > 0.5)	{
					this.setPixel( i, (int)(fYTmp + 1), fZCur, colSetI);
				}
				else	{
					this.setPixel( i, (int)fYTmp, fZCur, colSetI);
				}
			}
		}
	}
	
	/**
	 * draw arrow
	 * @throws Exception 
	 */
	public void setArrow( int poiBegXI, int poiBegYI, int poiEndXI, int poiEndYI, int iLenI, Color colSetI) throws Exception	{
		setLine( poiBegXI, poiBegYI, s_fDefDepth, poiEndXI, poiEndYI, s_fDefDepth, colSetI);

		float fDis = (float)Math.sqrt((poiEndXI - poiBegXI)*(poiEndXI - poiBegXI)
									 + (poiEndYI - poiBegYI)*(poiEndYI - poiBegYI));
		float fLength = iLenI;
		if(0 == iLenI)	{
			fLength = fDis / 10.0f;
		}

		float fpoiX = (poiBegXI - poiEndXI) / fDis * fLength + poiEndXI;
		float fpoiY = (poiBegYI - poiEndYI) / fDis * fLength + poiEndYI;

		float fCos = (float)Math.sqrt(3)/2;
		float fSin = 0.5f;
		float fTarX = poiEndXI + (fpoiX - poiEndXI)*fCos - (fpoiY - poiEndYI)*fSin;
		float fTarY = poiEndYI + (fpoiX - poiEndXI)*fSin + (fpoiY - poiEndYI)*fCos;
		setLine( poiEndXI, poiEndYI, s_fDefDepth, (int)fTarX, (int)fTarY, s_fDefDepth, colSetI);

		fSin = -fSin;
		fTarX = poiEndXI + (fpoiX - poiEndXI)*fCos - (fpoiY - poiEndYI)*fSin;
		fTarY = poiEndYI + (fpoiX - poiEndXI)*fSin + (fpoiY - poiEndYI)*fCos;
		setLine( poiEndXI, poiEndYI, s_fDefDepth, (int)fTarX, (int)fTarY, s_fDefDepth, colSetI);
	}
	/**
	 * render pixel array
	 */
	public void render()	{
		//1.check render data
		if(null == m_devGraph)	{
			System.out.println("graphics hasn't be given");
			return;
		}
		if(0 == m_iWidth && 0 == m_iHeight)	{
			System.out.println("no data for render! or error data!");
			return;
		}
		if(0 == m_aBackBuf.length
		&& 0 == m_aBackBuf[0].length
		&& 0 == m_iBufWidth
		&& 0 == m_iBufHeight
		&& m_aBackBuf.length != m_iBufWidth
		&& m_aBackBuf[0].length != m_iBufHeight)	{
			System.out.println("no data for render! or error data!");
			return;
		}
		
		//2.rester screen
		//this.rester();

		//3.scale the pixel array, print all to the screen
		for( int i = 0; i < m_iBufWidth; ++i)
			for( int j = 0; j < m_iBufHeight; ++j)	{
				if(null != m_aBackBuf[i][j])	{
					Color colTmp = m_devGraph.getColor();
					m_devGraph.setColor(m_aBackBuf[i][j]);
					m_devGraph.fillRect( i, j, m_iSzPixel, m_iSzPixel);
					m_devGraph.setColor(colTmp);
				}
				else	{
					m_devGraph.setColor(m_colBackGd);
					m_devGraph.fillRect( i, j, m_iSzPixel, m_iSzPixel);
				}
			}
	}
	/**
	 * set base device,basically there are always another device inside class
	 * depend on which base graphics API be used to.
	 */
	public void setBaseDev(Object objI)	{
		//1.check render data
		/*if(!Graphics.class.equals(objI.getClass()))	{
			System.out.println("given device unable to be used for JDevice");
			return;
		}*/
		//2.fill into the variable
		m_devGraph = (Graphics)objI;
	}
	
	public boolean equals(IFWObject objI) {
		if(!objI.getClass().equals(CFWJDevice.class))	{
			return false;
		}
		
		CFWJDevice devTmp = (CFWJDevice)objI;
		return(/*this.m_devGraph.equals(devTmp.m_devGraph)
			&&*/ this.m_iRefshRate == devTmp.m_iRefshRate);
	}
	
	public void rester() {
		for( int i = 0; i < m_iBufWidth; ++i)	{
			
			for( int j = 0; j < m_iBufHeight; ++j)	{
				
				Color colCur = this.m_aBackBuf[i][j];
				if(i > 0 && i < m_iBufWidth - 1)	{
					if(!m_aBackBuf[i - 1][j].equals(m_colBackGd)
					&& !m_aBackBuf[i + 1][j].equals(m_colBackGd)
					&& colCur.equals(m_colBackGd))	{
						m_aBackBuf[i][j] = CFWMath.mixTwoColor( m_aBackBuf[i - 1][j], m_aBackBuf[i + 1][j]);
					}
				}
				if(j > 0 && j < m_iBufHeight - 1)	{
					if(!m_aBackBuf[i][j - 1].equals(m_colBackGd)
					&& !m_aBackBuf[i][j + 1].equals(m_colBackGd)
					&& colCur.equals(m_colBackGd))	{
						m_aBackBuf[i][j] = CFWMath.mixTwoColor( m_aBackBuf[i][j - 1], m_aBackBuf[i][j + 1]);
					}
				}
				
				if(i > 0)	{
					if((colCur.equals(m_colBackGd) && !m_aBackBuf[i - 1][j].equals(m_colBackGd))
					&& (!colCur.equals(m_colBackGd) && m_aBackBuf[i - 1][j].equals(m_colBackGd)))	{
						colCur = CFWMath.mixTwoColor( colCur, m_aBackBuf[i - 1][j]);
					}
				}
				if(i < m_iBufWidth - 1)	{
					if((colCur.equals(m_colBackGd) && !m_aBackBuf[i + 1][j].equals(m_colBackGd))
					&& (!colCur.equals(m_colBackGd) && m_aBackBuf[i + 1][j].equals(m_colBackGd)))	{
						colCur = CFWMath.mixTwoColor( colCur, m_aBackBuf[i + 1][j]);
					}
				}
				
				if(j > 0)	{
					if((colCur.equals(m_colBackGd) && !m_aBackBuf[i][j - 1].equals(m_colBackGd))
					&& (!colCur.equals(m_colBackGd) && m_aBackBuf[i][j - 1].equals(m_colBackGd)))	{
						colCur = CFWMath.mixTwoColor( colCur, m_aBackBuf[i][j - 1]);
					}
				}
				if(j < m_iBufHeight - 1)	{
					if((colCur.equals(m_colBackGd) && !m_aBackBuf[i][j + 1].equals(m_colBackGd))
					&& (!colCur.equals(m_colBackGd) && m_aBackBuf[i][j + 1].equals(m_colBackGd)))	{
						colCur = CFWMath.mixTwoColor( colCur, m_aBackBuf[i][j + 1]);
					}
				}
			}
		}
	}
	public int setPixelSize(int iSzPixelI) {
		if(iSzPixelI <= 0)	{
			return(m_iSzPixel);
		}
		
		int iSzTmp = m_iSzPixel;
		this.m_iSzPixel = iSzPixelI;
		
		return(iSzTmp);
	}
	
	public void setLight(String strNamLgtI)	{
		try	{
			CFWPool.getInstance().getObjFmPool(strNamLgtI);
			this.m_mpLights.put( strNamLgtI, false);
		}
		catch(Exception e)	{
			e.printStackTrace();
			return;
		}
	}
	
	public void enableLgt( String strNamLgtI, boolean bEnableI)	{
		if(m_mpLights.containsKey(strNamLgtI))	{
			m_mpLights.put( strNamLgtI, bEnableI);
		}
	}
	
	public boolean isLgtEnable(String strNamLgtI)	{
		if(m_mpLights.containsKey(strNamLgtI))	{
			return(m_mpLights.get(strNamLgtI));
		}
		
		return false;
	}
	
	public void buildShadow(CFWScene sceCurI)	{
		m_mpShadowBuf.clear();
		Iterator<Entry<String, IFWDevice.SFWLight>> iterTmp = sceCurI.getAllLights().entrySet().iterator();
		while(iterTmp.hasNext())	{
			Entry<String, IFWDevice.SFWLight> iterCur = iterTmp.next();
			
			try	{
				if(this.m_mpLights.get(iterCur.getKey()))	{
					//float[][] aShadowBuf = new float[m_iBufWidth][m_iHeight];
					IFWDevice.SFWLight lgtCur = iterCur.getValue();
					
					switch(lgtCur.m_iType)	{
					case IFWDevice.SFWLight.s_iLgtDir:
					case IFWDevice.SFWLight.s_iLgtPoi:
					case IFWDevice.SFWLight.s_iLgtPll:
					}
				}
			}
			catch(Exception e)	{
				e.printStackTrace();
				continue;
			}
		}
	}
}
