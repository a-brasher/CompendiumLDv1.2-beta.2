package com.compendium.learningdesign.ui.svggen;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.ImageObserver;
import java.util.Map;

import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DRuntimeException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.compendium.learningdesign.io.svg.SvgExport;
import com.compendium.ui.UINode;
import com.compendium.ui.plaf.NodeUI;

public class SVGLDGraphics2D extends SVGGraphics2D {

	
	public SVGLDGraphics2D(SVGGeneratorContext generatorCtx, boolean textAsShapes) {
		super(generatorCtx, false);
	}
	
	/** This method makes use of SVG code to draw UINode icon images.	**/
	public boolean drawImage(Image img, int x, int y,
			ImageObserver observer) {
		if (observer instanceof UINode)	{
			Document oDoc = this.getDOMFactory();
			UINode oNode = (UINode)observer;
			NodeUI oNodeUI = oNode.getUI();
			int iXpos = oNode.getX();
			int iYpos = oNode.getY();
			String sNodeClass = SvgExport.sGeneralClass;
			try	{
				sNodeClass = SvgExport.hmNodeClasses.get(oNode.getNode().getType());
			}
			catch (Exception e) {
				System.out.print("Array SvgExport.sStandardNodeClasses out of bounds: index accessed = "  + oNode.getNode().getType());
				}
			double iconRXpos =  oNodeUI.getIconRectangle().getX() + iXpos;
			double iconRYpos =  oNodeUI.getIconRectangle().getY() + iYpos;
			Element oUse = oDoc.createElementNS(SVGGraphics2D.SVG_NAMESPACE_URI, SVGGraphics2D.SVG_USE_TAG);
			oUse.setAttribute(SVGGraphics2D.XLINK_HREF_QNAME, org.apache.batik.svggen.SVGSyntax.SIGN_POUND + sNodeClass); 
			oUse.setAttribute("x", Double.toString(iconRXpos));
			oUse.setAttribute("y", Double.toString(iconRYpos));

		}
		else	{
			Element imageElement =
				getGenericImageHandler().createElement(getGeneratorContext());
			AffineTransform xform = getGenericImageHandler().handleImage(
					img, imageElement,
					x, y,
					img.getWidth(null),
					img.getHeight(null),
					getGeneratorContext());

			if (xform == null) {
				domGroupManager.addElement(imageElement);
			} else {
				AffineTransform inverseTransform = null;
				try {
					inverseTransform = xform.createInverse();
				} catch(NoninvertibleTransformException e) {
					// This should never happen since handleImage
					// always returns invertible transform
					throw new SVGGraphics2DRuntimeException(ERR_UNEXPECTED);
				}
				gc.transform(xform);
				domGroupManager.addElement(imageElement);
				gc.transform(inverseTransform);
			}
		}
		return true;
	}
/**	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		
		this.getRenderingHints().putAll(hints);

	}
**/
}
