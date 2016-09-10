package eg;

import java.awt.Color;
import java.util.Random;
import java.util.Vector;

public class CFWMath {
	static public final float s_fValMin = 0.01f;
	static public final int s_iColDif = 0x88;
	
	static public int geneRandValue( int iValAI, int iValBI)	{
		return((int)Math.random()*Math.abs(iValAI - iValBI) + Math.min( iValAI, iValBI));
	}
	
	static public boolean isSameColor( Color colAI, Color colBI)	{
		return(Math.abs(colAI.getRed() - colBI.getRed()) < s_iColDif
			&& Math.abs(colAI.getGreen() - colBI.getGreen()) < s_iColDif
			&& Math.abs(colAI.getBlue() - colBI.getBlue()) < s_iColDif);
	}
	
	static public Vector<Integer> geneRandValue( int iNumGeneI, int iValBegI, int iValEndI, boolean bRepeatI)	{
		int iValMax = Math.max( iValBegI, iValEndI);
		int iValMin = Math.min( iValBegI, iValEndI);
		Random rdmGener = new Random();
		Vector<Integer> vecRet = new Vector<Integer>();
		for( int i = 0; i < iNumGeneI; ++i)	{
			int iValTmp = rdmGener.nextInt(iValMax - iValMin) + iValMin;
			if(!bRepeatI){
				int iNumLoop = 0;
				while(vecRet.contains(iValTmp) && iNumLoop < 20000)	{
					iValTmp = rdmGener.nextInt(iValMax - iValMin) + iValMin;
					++iNumLoop;
				}
				if(20000 == iNumLoop)	{
					return(vecRet);
				}
				
				vecRet.add(iValTmp);
			}
		}
		return(vecRet);
	}
	
	static public Color mixTwoColor( Color colAI, Color colBI)	{
		int iColHlfAlpA = (int)((float)colAI.getAlpha()*0.5);
		int iColHlfAlpB = (int)((float)colBI.getAlpha()*0.5);
		
		int iColRed		= (colAI.getRed()*iColHlfAlpA	+ colBI.getRed()*iColHlfAlpB)	/(iColHlfAlpA + iColHlfAlpB);
		int iColGreen	= (colAI.getGreen()*iColHlfAlpA	+ colBI.getGreen()*iColHlfAlpB)	/(iColHlfAlpA + iColHlfAlpB);
		int iColBlue	= (colAI.getBlue()*iColHlfAlpA	+ colBI.getBlue()*iColHlfAlpB)	/(iColHlfAlpA + iColHlfAlpB);

		return(new Color( iColRed, iColGreen, iColBlue));
	}
	
	static public Color colMultiVal( Color colI, float fValI)	{
		int iRed = colI.getRed();
		int iGreen = colI.getGreen();
		int iBlue = colI.getBlue();
		
		//int i = (int)(1.0/(1.0 - fValI));
		if ( iRed == 0 && iGreen == 0 && iBlue == 0) {
           return(colI);
        }
        //if ( r > 0 && r < i ) r = i;
        //if ( g > 0 && g < i ) g = i;
        //if ( b > 0 && b < i ) b = i;

        return(new Color(Math.min((int)(iRed*fValI), 255),
        				  Math.min((int)(iGreen*fValI), 255),
        				  Math.min((int)(iBlue*fValI), 255)));
	}
	
	static public boolean isBetweenTwoNum( float fValChkI, float fValAI, float fValBI, boolean bIncludeEqualI)	{
		float fValMax = Math.max( fValAI, fValBI);
		float fValMin = Math.min( fValAI, fValBI);
		if(bIncludeEqualI)
			return(fValChkI >= fValMin && fValChkI <= fValMax);
		else
			return(fValChkI > fValMin && fValChkI < fValMax);
	}
	
	static public int getSign(float fValI)	{
		return((int)(fValI/Math.abs(fValI)));
	}
	
	static public float regulateFloat(float fValI)	{
		int iValInt = (int)fValI;
		//check whether equal some value
		if(fValI > 0 && (int)(fValI + 0.001f) != iValInt)	{
			return((int)(fValI + 0.001f));
		}
		else if(fValI < 0 && (int)(fValI - 0.001f) != iValInt)	{
			return((int)(fValI - 0.001f));
		}
		//check whether equal to zero
		if(Math.abs(fValI) < s_fValMin)
			return(0);
			
		return(fValI);
	}
	
	static public int tunToInt(float fValI)	{
		return((int)regulateFloat(fValI));
	}
	
	static public boolean equals( float fValAI, float fValBI)	{
		return(Math.abs(fValAI - fValBI) < s_fValMin);
	}
	
	static public Color getNegCol(Color colI)	{
		if(Color.gray == colI)
			return(Color.black);
		return(new Color(~colI.getRGB()));
	}
}
