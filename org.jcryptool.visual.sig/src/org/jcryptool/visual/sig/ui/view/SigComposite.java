//This class contains all the code required for the GUI

package org.jcryptool.visual.sig.ui.view;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.jcryptool.visual.sig.SigPlugin;
import org.jcryptool.visual.sig.Messages;
import org.jcryptool.visual.sig.ui.wizards.HashWizard;
import org.eclipse.jface.resource.ImageDescriptor;

public class SigComposite extends Composite implements PaintListener {//,ActionListener{
	private Text txtHash;
	private Text txtGeneralDescription;
	private Text txtSignature;
	private Canvas canvas1;
	private Text txtDescriptionOfStep1;
	private Text txtDescriptionOfStep2;
	private Text txtDescriptionOfStep3;
	private Text txtDescriptionOfStep4;
	TabFolder tabDescription;
	private Button btnHash;
	private Button btnSignature;
	private Button btnOpenInEditor;
	private Button btnOpenDocumentTemp;
	SigComposite sc = this;

	//Generates all Elements of the GUI
	public SigComposite(Composite parent, int style, SigView view) {
		super(parent, style);
		sc = this;
		//The color for the textboxes
		Color grey = new Color(Display.getCurrent(), 220, 220, 220);
		
		txtGeneralDescription = new Text(this, SWT.MULTI);
    	txtGeneralDescription.setEditable(false);
		txtGeneralDescription.setBounds(10, 10, 699, 45);
		txtGeneralDescription.setText(Messages.SigComposite_description);
		
		Group grpSignatureGeneration = new Group(this, SWT.NONE);
		grpSignatureGeneration.setText(Messages.SigComposite_grpSignatureGeneration); 
		grpSignatureGeneration.setBounds(10, 61, 699, 529);
		
		btnOpenDocumentTemp = new Button(grpSignatureGeneration, SWT.NONE);
		btnOpenDocumentTemp.setBounds(34, 64, 136, 41);
		btnOpenDocumentTemp.setText(Messages.SigComposite_btnDocumentTemp);
		
		Label lblHash = new Label(grpSignatureGeneration, SWT.NONE);
		lblHash.setBounds(34, 246, 136, 14);
		lblHash.setText(Messages.SigComposite_lblHash); 
		
		btnHash = new Button(grpSignatureGeneration, SWT.NONE);
		btnHash.setEnabled(false);
		btnHash.setBounds(34, 184, 136, 60);
		btnHash.setText(Messages.SigComposite_btnHash);
		
		txtHash = new Text(grpSignatureGeneration, SWT.BORDER);
		txtHash.setBounds(34, 365, 136, 56);
		txtHash.setEditable(false);
		txtHash.setText("<Hash>");
		
		btnSignature = new Button(grpSignatureGeneration, SWT.NONE);
		btnSignature.setEnabled(false);
		btnSignature.setBounds(248, 365, 136, 60);
		btnSignature.setText(Messages.SigComposite_btnSignature);
		
		Group grpSignedDoc = new Group(grpSignatureGeneration, SWT.NONE);
		grpSignedDoc.setBounds(463, 207, 212, 261);
		grpSignedDoc.setText(Messages.SigComposite_grpSignedDoc); 
		
		txtSignature = new Text(grpSignedDoc, SWT.BORDER);
		txtSignature.setText(""); 
		txtSignature.setBounds(10, 144, 188, 56);
		
		btnOpenInEditor = new Button(grpSignedDoc, SWT.NONE);
		btnOpenInEditor.setEnabled(false);
		btnOpenInEditor.setBounds(10, 206, 109, 28);
		btnOpenInEditor.setText(Messages.SigComposite_btnOpenInEditor);
		
		tabDescription = new TabFolder(grpSignatureGeneration, SWT.NONE);
		tabDescription.setBounds(187, 10, 488, 191);
		
		TabItem tbtmStep1 = new TabItem(tabDescription, SWT.NONE);
		tbtmStep1.setText(Messages.SigComposite_tbtmNewItem_0); 
		
		txtDescriptionOfStep1 = new Text(tabDescription, SWT.MULTI);
		txtDescriptionOfStep1.setBackground(grey);
		txtDescriptionOfStep1.setEditable(false);
		txtDescriptionOfStep1.setText(Messages.SigComposite_txtDescriptionOfStep1);
		tbtmStep1.setControl(txtDescriptionOfStep1);
		
		TabItem tbtmStep2 = new TabItem(tabDescription, SWT.NONE);
		tbtmStep2.setText(Messages.SigComposite_tbtmNewItem_1); 
		
		txtDescriptionOfStep2 = new Text(tabDescription, SWT.NONE);
		txtDescriptionOfStep2.setBackground(grey);
		txtDescriptionOfStep2.setEditable(false);
		txtDescriptionOfStep2.setText(Messages.SigComposite_txtDescriptionOfStep2);
		tbtmStep2.setControl(txtDescriptionOfStep2);
		
		TabItem tbtmStep3 = new TabItem(tabDescription, SWT.NONE);
		tbtmStep3.setText(Messages.SigComposite_tbtmNewItem_2); 
		
		txtDescriptionOfStep3 = new Text(tabDescription, SWT.NONE);
		txtDescriptionOfStep3.setBackground(grey);
		txtDescriptionOfStep3.setEditable(false);
		txtDescriptionOfStep3.setText(Messages.SigComposite_txtDescriptionOfStep3);
		tbtmStep3.setControl(txtDescriptionOfStep3);
		
		TabItem tbtmStep4 = new TabItem(tabDescription, SWT.NONE);
		tbtmStep4.setText(Messages.SigComposite_tbtmNewItem_3);
		
		txtDescriptionOfStep4 = new Text(tabDescription, SWT.NONE);
		txtDescriptionOfStep4.setBackground(grey);
		txtDescriptionOfStep4.setEditable(false);
		txtDescriptionOfStep4.setText(Messages.SigComposite_txtDescriptionOfStep4);
		tbtmStep4.setControl(txtDescriptionOfStep4);
		
		Button btnReset = new Button(grpSignatureGeneration, SWT.NONE);
		btnReset.setBounds(581, 474, 94, 28);
		btnReset.setText(Messages.SigComposite_btnReset);
		
		Label lblProgress = new Label(grpSignatureGeneration, SWT.NONE);
		lblProgress.setBounds(492, 481, 83, 14);
		lblProgress.setText(Messages.SigComposite_lblProgress);  
		
		Label lblSignature = new Label(grpSignatureGeneration, SWT.NONE);
		lblSignature.setText(Messages.SigComposite_lblSignature);
		lblSignature.setBounds(248, 431, 136, 14);
		
		canvas1 = new Canvas(grpSignatureGeneration, SWT.NONE);
		canvas1.setBounds(34, 21, 628, 392);

		createEvents();
		
		//Adding the PantListener to all the canvas so the arrows can be drawn
		canvas1.addPaintListener(this);
		//canvas2.addPaintListener(this);
 
	}

	/**
	 * This method paints the arrows used to indicate the steps. They are painted in light grey and are later changed
	 * to a darker grey (stepFinished()).
	 * 
	 * @param e 
	 */
	public void paintControl(PaintEvent e) {
		//Set the used colors
		Color lightgrey = new Color(Display.getCurrent(), 192, 192, 192);
	    Color darkgrey = new Color(Display.getCurrent(), 128, 128, 128);
	    Rectangle clientArea;
	    int width;
	    int height;
	    GC gc;
	    
		gc = e.gc;
        //Get the size of the canvas area
        clientArea = canvas1.getClientArea(); 
        width = clientArea.width; 
        height = clientArea.height; 
        
        //Insert the image of the document
        ImageDescriptor id = SigPlugin.getImageDescriptor("icons/Document_brown.png"); 
        ImageData imD = id.getImageData();
        imD.transparentPixel = 16777215; // white
        Image img = new Image(Display.getCurrent(), imD);
        gc.drawImage(img, 20, 0);
        gc.drawImage(img, width-140, height-180);
        
        gc.setBackground(lightgrey); 
        //Color the all the areas in lightgrey
        //Draw shaft
        gc.fillRectangle(55,50,20,height);
        gc.fillRectangle(0,height-30,width-220,20);
        //Draw head (x1, y1, x2, y2, x3, y3)
    	gc.fillPolygon(new int[] {width-220,height-40, width-220, height, width-200, height-20});
        gc.setBackground(darkgrey);
        
        //Color the specified areas in darkgrey
        if (btnHash.getEnabled() == false) {
        	//color nothing
        }else if (btnSignature.getEnabled() == false) { //Step 2
        	//draw the first part of the arrow (from Document to btnHash)
        	gc.fillRectangle(55,50,20,height/2-30);
        }
        else if (btnOpenInEditor.getEnabled() == false){ //Step 3
        	//draw another part of the arrow (from btnHash to btnSignature)
        	gc.fillRectangle(55,50,20,height);
        	gc.fillRectangle(0,height-30,width/2,20);
        }
        else { //Step 4
        	gc.fillRectangle(55,50,20,height);
        	gc.fillRectangle(0,height-30,width-220,20);
        	gc.fillPolygon(new int[] {width-220,height-40, width-220, height, width-200, height-20});
        }
       
        gc.dispose(); 
        
	}//end paintControl
	
	/**
	 * Adds SelectionListeners to the Controls that need them
	 */
	public void createEvents() {
		//Adds a Listener for the document
				btnOpenDocumentTemp.addSelectionListener(new SelectionListener() {
		            public void widgetDefaultSelected(SelectionEvent e) {
		            }
		            public void widgetSelected(SelectionEvent e) {
		                try {
		                    //Enable to select the hash method
		                    btnHash.setEnabled(true); 
		                    //Activate the second tab of the description
		                    tabDescription.setSelection(1);
		                    canvas1.redraw();
		                } catch (Exception ex) {
		                }
		            }
		        });
				
		//Adds a Listener for the signature select button
		btnHash.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }
            public void widgetSelected(SelectionEvent e) {
                try {
                    //Create the HashWirard
                    HashWizard wiz = new HashWizard();
                    //Display it
                    WizardDialog dialog = new WizardDialog(new Shell(Display.getCurrent()), wiz);
                    if (dialog.open() == Window.OK) {
                    	//Enable to select the signature method
                        btnSignature.setEnabled(true); 
                        //Activate the third tab of the description
                        tabDescription.setSelection(2);
                        canvas1.redraw();
                    }
                } catch (Exception ex) {
                }
            }
        });
		
		//Adds a Listener for the Signature select button
		btnSignature.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
            }
            public void widgetSelected(SelectionEvent e) {
                try {
                    
                    HashWizard wiz = new HashWizard();
                    WizardDialog dialog = new WizardDialog(new Shell(Display.getCurrent()), wiz);
                    if (dialog.open() == Window.OK) {
                    	btnOpenInEditor.setEnabled(true);
                    	//Activate the second tab of the description
                    	tabDescription.setSelection(3);
                    	canvas1.redraw();
                    }
                } catch (Exception ex) {
                    //LogUtil.logError(SigPlugin.PLUGIN_ID, ex);
                }
            }
        });
	}//end createEvents
	
}
