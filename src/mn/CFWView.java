package mn;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPanel;

import eg.CFWMath;
import eg.CFWPoint;
import eg.CFWPool;
import eg.CFWScene;
import eg.CFWSysRender;
import eg.IFWDevice;
import eg.IFWMesh;
import eg.IFWObject;
import eg.IFWSkin;

@SuppressWarnings("serial")
public class CFWView extends JPanel implements MouseWheelListener, MouseMotionListener, MouseListener	 {
	private CFWJDevice m_devCur;
	private CFWFCamera m_camCur;
	private CFWScene m_sceDraw;
	
	private int m_iEnterKey;
	private CFWPoint m_poiMOrg;
	private CFWPoint m_poiROrg;
	
	public CFWView()	{
		m_sceDraw = null;
		m_poiMOrg = new CFWPoint();
		m_poiROrg = new CFWPoint();
		m_iEnterKey = MouseEvent.NOBUTTON;
	}
	
	public CFWJDevice getCurrentDev()	{
		return(m_devCur);
	}
	
	public void paint(Graphics g)	{
		synchronized(this)	{
			//1.call the father's function
			super.paint(g);
			
			//2.check the device whether can be used
			if(null == m_devCur)	{
				float fBufWidth = 800;
				float fBufHeight = 600;
				
				m_devCur = new CFWJDevice();
				m_devCur.setScnSize( this.getWidth(), this.getHeight());
				m_devCur.setBkBufSize(
						(int)CFWMath.regulateFloat(fBufWidth),
						(int)CFWMath.regulateFloat(fBufHeight));
				m_devCur.setBackGround(Color.darkGray);
				
				m_sceDraw = new CFWScene(m_devCur);
				
				try	{
					m_camCur = new CFWFCamera();
					m_camCur.lookAt( new CFWPoint( 130.0f, 100.0f, 130.0f), new CFWPoint( 0.0f, 0.0f, 0.0f));
					m_camCur.setViewRect( this.getWidth(), this.getHeight());
					m_camCur.setProjectPlane( new CFWPoint( -fBufWidth/2, -fBufHeight/2, 150),  new CFWPoint( fBufWidth/2, fBufHeight/2, 150));
				}
				catch(Exception e)	{
					e.printStackTrace();
					System.exit(-1);
				}
				
				IFWSkin sknTmp = null;
				String strSknTmp = "";
				try {
					sknTmp = new CFWCSkin("C:\\Users\\Public\\Pictures\\Sample Pictures\\Penguins.jpg");
					strSknTmp = CFWPool.getInstance().addObjToPool((IFWObject)sknTmp);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(0);
				}
				
				IFWDevice.SFWLight lgtNew = new IFWDevice.SFWLight();
				lgtNew.m_colAmb = Color.white;
				lgtNew.m_colDif = Color.white;
				lgtNew.m_colSpc = Color.white;
				lgtNew.m_fAttenuation0 = 0.0001f;
				lgtNew.m_fAttenuation1 = 0.0001f;
				lgtNew.m_fAttenuation2 = 0.0001f;
				lgtNew.m_iType = IFWDevice.SFWLight.s_iLgtPoi;
				lgtNew.m_poiPos = new CFWPoint( 0.0f, 200.0f, 0.0f);
				try {
					m_sceDraw.addLight( "point_test", lgtNew);
				} catch (Exception e1) {
					e1.printStackTrace();
					System.exit(-1);
				}
				
				CFWBox boxTmp = new CFWBox();
				try {
					boxTmp.create( new CFWPoint( 0.0f, 90.0f, 90.0f),
								   new CFWPoint( 90.0f, 0.0f, 0.0f), strSknTmp);
					//m_sceDraw.addRenderObj( "tst_box", boxTmp);
				} catch (Exception e) {
					e.printStackTrace();
					System.exit(-1);
				}
				
				CFWSphere sphTmp = new CFWSphere();
				try	{
					sphTmp.create( new CFWPoint( 0.0f, 0.0f, 0.0f), 100, 12);
					m_sceDraw.addRenderObj( "tst_sphere", sphTmp);
				}
				catch(Exception e)	{
					e.printStackTrace();
					System.exit(-1);
				}
				
				IFWMesh tglTstA = CFWCMesh.createTriangle(
						new IFWMesh.SFWAttribute( new CFWPoint( 100.0f, 100.0f, 0.0f), Color.red),
						new IFWMesh.SFWAttribute( new CFWPoint( 0.0f, 100.0f, 0.0f), Color.red),
						new IFWMesh.SFWAttribute( new CFWPoint( 0.0f, 100.0f, 100.0f), Color.red));
				m_sceDraw.addRenderObj( "taiangleA", tglTstA);
				
				IFWMesh tglTstB = CFWCMesh.createTriangle(
						new IFWMesh.SFWAttribute( new CFWPoint( 150.0f, 120.0f, 90.0f), Color.green),
						new IFWMesh.SFWAttribute( new CFWPoint( 0.0f, 120.0f, 0.0f), Color.green),
						new IFWMesh.SFWAttribute( new CFWPoint( 0.0f, 120.0f, 90.0f), Color.green));
				m_sceDraw.addRenderObj( "taiangleB", tglTstB);

				IFWMesh mshRect = CFWCMesh.createRectangle(
						new IFWMesh.SFWAttribute( new CFWPoint( -150.0f, 0.0f, -150.0f), Color.white),
						new IFWMesh.SFWAttribute( new CFWPoint( 150.0f, 0.0f, -150.0f), Color.white),
						new IFWMesh.SFWAttribute( new CFWPoint( -150.0f, 0.0f, 150.0f), Color.white),
						new IFWMesh.SFWAttribute( new CFWPoint( 150.0f, 0.0f, 150.0f), Color.white));
				//mshRect.setSkin(strSknTmp);
				m_sceDraw.addRenderObj( "rect", mshRect);
				
				CFWSysRender.getInstance( m_devCur, m_camCur);
			}
			
			//3.if the window's size changed, device's back buffer should change, too
			if(this.getWidth() != m_devCur.getScnWidth())	{
				m_devCur.setScnSize( this.getWidth(), m_devCur.getScnHeight());
				//m_camCur.setViewRect( this.getWidth(), m_devCur.getScnHeight());
			}
			if(this.getHeight() != m_devCur.getScnHeight())	{
				m_devCur.setScnSize( m_devCur.getScnWidth(), this.getHeight());
				//m_camCur.setViewRect( m_devCur.getScnWidth(), this.getHeight());
			}
			
			//4.update graphics device
			m_devCur.setBaseDev(g);
			
			//5.update
			CFWSysRender.getInstance().update(m_sceDraw);
			
			//5.render
			m_devCur.render();
			
			this.repaint();
			try {
				Thread.sleep(1000/m_devCur.getRefreshRate());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void mouseWheelMoved(MouseWheelEvent arg0) {
		//1.data check
		if(null == m_camCur)	{
			System.out.println("the camera hasn't initlized!");
			return;
		}
		//2.move
		int iDir = arg0.getWheelRotation();
		
		if(1 == iDir)	{
			m_camCur.moveByZ(-5);
		}
		if(-1 == iDir)	{
			m_camCur.moveByZ(5);
		}
	}
	
	public void mouseDragged(MouseEvent arg0) {
		//1.data check
		if(null == m_camCur)	{
			System.out.println("the camera hasn't initlized!");
			return;
		}
		
		if(m_iEnterKey == MouseEvent.BUTTON2)	{
			float fDisX = arg0.getX() - m_poiMOrg.m_fX;
			float fDisY = arg0.getY() - m_poiMOrg.m_fY;
			
			m_camCur.moveByX(fDisX);
			m_camCur.moveByY(fDisY);
			
			m_poiMOrg.m_fX = arg0.getX();
			m_poiMOrg.m_fY = arg0.getY();
		}
		else if(m_iEnterKey == MouseEvent.BUTTON3)	{
			if(!m_camCur.checkFocusMode())	{
				m_camCur.enableFocus(true);
			}
			float fDisX = arg0.getX() - m_poiROrg.m_fX;
			float fDisY = arg0.getY() - m_poiROrg.m_fY;
			
			if(Math.abs(fDisX) > Math.abs(fDisY))	{
				m_camCur.rollByY(fDisX/5);
			}
			else	{
				m_camCur.rollByX(-fDisY/5);
			}
			m_camCur.enableFocus(false);
			
			m_poiROrg.m_fX = arg0.getX();
			m_poiROrg.m_fY = arg0.getY();
		}
		
		this.repaint();
		
	}

	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mousePressed(MouseEvent arg0) {
		if(arg0.getButton() == MouseEvent.BUTTON2)	{
			m_poiMOrg.m_fX = arg0.getX();
			m_poiMOrg.m_fY = arg0.getY();
			m_iEnterKey = MouseEvent.BUTTON2;
		}
		else if(arg0.getButton() == MouseEvent.BUTTON3)	{
			m_poiROrg.m_fX = arg0.getX();
			m_poiROrg.m_fY = arg0.getY();
			m_iEnterKey = MouseEvent.BUTTON3;
		}
	}

	public void mouseReleased(MouseEvent arg0) {
		m_iEnterKey = MouseEvent.NOBUTTON;
	}
}
