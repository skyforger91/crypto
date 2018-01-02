// -----BEGIN DISCLAIMER-----
/*******************************************************************************
 * Copyright (c) 2017 JCrypTool Team and Contributors
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
// -----END DISCLAIMER-----
package org.jcryptool.visual.he.ui;

import java.math.BigInteger;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.jcryptool.core.util.colors.ColorService;
import org.jcryptool.core.util.fonts.FontService;
import org.jcryptool.visual.he.Messages;
import org.jcryptool.visual.he.algo.Paillier;
import org.jcryptool.visual.he.algo.PaillierData;
import org.jcryptool.visual.he.algo.RSAData;
import org.jcryptool.visual.he.rsa.Action;
import org.jcryptool.visual.he.wizards.PaillierInitialTextWizard;
import org.jcryptool.visual.he.wizards.PaillierKeySelectionWizard;
import org.jcryptool.visual.he.wizards.PaillierOperationTextWizard;
import org.jcryptool.visual.he.wizards.RSAInitialTextWizard;
import org.jcryptool.visual.he.wizards.RSAKeySelectionWizard;
import org.jcryptool.visual.he.wizards.RSAOperationTextWizard;

/**
 * Composite to display homomorphic encryption schemes
 *
 * @author Coen Ramaekers
 */

public class HEComposite extends Composite {
	/** Yellow */
	public Color YELLOW;

	/** Holds the tab choice */
	public int tabChoice;

	/** Handles the tab choice */
	public final int RSA = 2, PAILLIER = 3;

	/** Button for running the key selection wizard */
	private Button keySel;

	/** Button for running initial text selection wizard */
	private Button initTextSel;

	/** Buttons to run homomorphic operations */
	private Button homomorphMult, homomorphAdd;

	/** Decrypt button */
	private Button decryptButton;

	/** Reset buttons */
	private Button resetNumButton, resetAllButton;

	/** Textboxes to display key info for RSA*/
	private Text eText, nText;

	/** Textboxes to display key info for Paillier */
	private Text gText;

	/** Will hold RSA data in wizards */
	private RSAData rsaEncData = new RSAData(Action.EncryptAction), rsaDecData = new RSAData(Action.DecryptAction);

	/** Will hold Paillier data in wizards */
	private PaillierData paillierData = new PaillierData();

	/** Textboxes to display initial number and encryption */
	private Text initialPlain, initialEncryptedBits;

	/** Textboxes to display homomorphic number and encryption */
	private Text homomorphPlain, homomorphEncryptedBits;

	/** Textboxes to display results of homomorphic operation */
	private Text homomorphResultPlain, homomorphResultEncryptedBits;

	/** Textboxes to display plain operations */
	private Text plainOperations, plainResult;

	/** RSA Result number */
	private BigInteger rsaResult;

	/** Paillier Result number */
	private BigInteger paillierResult;
	
	/** Initial number encrypted */
	private BigInteger[] initialEncrypted;

	/** Homomorphic number encrypted */
	private BigInteger[] homomorphEncrypted;

	/** Result number encrypted */
	private BigInteger[] homomorphResultEncrypted;

	/** Check whether the operation is the first, for parentheses */
	private boolean first = true;
	
	/**The width of the labels left to the text boxes */
	private int lblWidth = 120;

	public HEComposite(final Composite parent, final int tabChoice, final int style) {
		super(parent,style);
		this.tabChoice = tabChoice;
		this.initialize();
		YELLOW = HEComposite.this.getDisplay().getSystemColor(SWT.COLOR_YELLOW);
	}

	/**
	 * Creates the layout and calls the creation of the head, which holds the title and description,
	 * and the main composite, which holds the visualization
	 */
	private void initialize() {
		this.setLayout(new GridLayout());
		this.createHead();
		this.createMain();
	}

	/**
	 * Creates the head of the screen, holds the title and description
	 */
	private void createHead() {
		final Composite head = new Composite(this, SWT.NONE);
        head.setBackground(ColorService.WHITE);
        head.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
        head.setLayout(new GridLayout());

        final Label label = new Label(head, SWT.NONE);
        label.setFont(FontService.getHeaderFont());
        label.setBackground(ColorService.WHITE);

        /** Deals with the choice of scheme */
        switch(tabChoice) {
	        case RSA:  label.setText(Messages.HEComposite_RSA_Title); break;
	        case PAILLIER:  label.setText(Messages.HEComposite_Paillier_Title); break;
        }

        final StyledText stDescription = new StyledText(head, SWT.READ_ONLY);
        switch(tabChoice) {
	        case RSA:  stDescription.setText(Messages.HEComposite_RSA_Description); break;
	        case PAILLIER:  stDescription.setText(Messages.HEComposite_Paillier_Description); break;
        }
        stDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
	}

	/**
	 * Creates the main area, subdivided into the button area and the algorithm area
	 */
	private void createMain() {
		final Group g = new Group(this, SWT.NONE);
		g.setText(Messages.HEComposite_Scheme);
		final GridLayout gl = new GridLayout(2, false);
        g.setLayout(gl);
        g.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        this.createButtonArea(g);
        this.createAlgoArea(g);
	}

	/**
	 * Creates the button area
	 * @param parent the composite in which it is created
	 */
	private void createButtonArea(final Composite parent) {
		final Composite mainComposite = new Composite(parent, SWT.SHADOW_NONE);
		mainComposite.setLayout(new GridLayout());
		GridData gd_mainComposite = new GridData(SWT.FILL, SWT.FILL, false, true);
		gd_mainComposite.widthHint = 180;
		mainComposite.setLayoutData(gd_mainComposite);

		Group subComposite1= new Group(mainComposite, SWT.SHADOW_NONE);
		subComposite1.setText(Messages.HEComposite_Key);
		FillLayout fl_subComposite1 = new FillLayout(SWT.HORIZONTAL);
		fl_subComposite1.marginHeight = 2;
		fl_subComposite1.marginWidth = 2;
		subComposite1.setLayout(fl_subComposite1);
		subComposite1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        this.keySel = new Button(subComposite1, SWT.PUSH);
        this.keySel.setBackground(ColorService.RED);
        this.keySel.setEnabled(true);
        this.keySel.setText(Messages.HEComposite_Keysel);
        this.keySel.setToolTipText(Messages.HEComposite_Key_Tooltip);
        this.keySel.addSelectionListener(new SelectionAdapter() {
        	@Override
			public void widgetSelected(final SelectionEvent e) {
        		/** Every scheme has a different type of key, so requires his own wizard */
        		switch(tabChoice) {
        			case RSA:	if (new WizardDialog(getShell(),
        					new RSAKeySelectionWizard(rsaEncData, false)).open() == Window.OK) keySelected(); break;
        			case PAILLIER:	if (new WizardDialog(getShell(),
        					new PaillierKeySelectionWizard(paillierData, HEComposite.this.getDisplay())).open() == Window.OK) keySelected(); break;
        		}
        	}
        });

        Composite spacerComposite = new Composite(mainComposite, SWT.NONE);
        Label spacerLabel = new Label(spacerComposite, SWT.NONE);
        spacerLabel.setSize(130, 17);

        Group subComposite2 = new Group(mainComposite, SWT.SHADOW_NONE);
		subComposite2.setText(Messages.HEComposite_Initial_Text);
		subComposite2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		FillLayout fl_subComposite2 = new FillLayout(SWT.HORIZONTAL);
		fl_subComposite2.marginHeight = 2;
		fl_subComposite2.marginWidth = 2;
		subComposite2.setLayout(fl_subComposite2);
		this.initTextSel = new Button(subComposite2, SWT.PUSH);
		this.initTextSel.setToolTipText(Messages.HEComposite_Initial_Tooltip);
        this.initTextSel.setBackground(ColorService.RED);
        this.initTextSel.setEnabled(false);
        this.initTextSel.setText(Messages.HEComposite_Initial_Text_Select);
        this.initTextSel.addSelectionListener(new SelectionAdapter() {

        	@Override
			public void widgetSelected(final SelectionEvent e) {
        		/** Since the modulus is different for each scheme, they each require an own wizard*/
        		switch(tabChoice) {
        		case RSA: if (new WizardDialog(HEComposite.this.getShell(),
        				new RSAInitialTextWizard(rsaEncData)).open() == Window.OK) initialTextSelected(); break;
        		case PAILLIER: if (new WizardDialog(HEComposite.this.getShell(),
        				new PaillierInitialTextWizard(paillierData)).open() == Window.OK) initialTextSelected(); break;
        		}
        	}
        });

        spacerComposite = new Composite(mainComposite, SWT.NONE);
        spacerLabel = new Label(spacerComposite, SWT.NONE);
        spacerLabel.setSize(130, 111);

        Group subComposite4 = new Group(mainComposite, SWT.SHADOW_NONE);
		subComposite4.setText(Messages.HEComposite_Homomorphic_Text);
		subComposite4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		FillLayout fl_subComposite4 = new FillLayout(SWT.VERTICAL);
		fl_subComposite4.marginHeight = 2;
		fl_subComposite4.marginWidth = 2;
        subComposite4.setLayout(fl_subComposite4);

		/** Only RSA does not have homomorphic addition */
		if (tabChoice != RSA) {
	        this.homomorphAdd = new Button(subComposite4, SWT.PUSH);
	        this.homomorphAdd.setToolTipText(Messages.HEComposite_Homomorphic_Tooltip_Add);
	        this.homomorphAdd.setEnabled(false);
	        this.homomorphAdd.setText(Messages.HEComposite_Homomorphic_Add_Select);
	        this.homomorphAdd.addSelectionListener(new SelectionAdapter() {
	        	@Override
				public void widgetSelected(final SelectionEvent e) {
	        		if (tabChoice == PAILLIER && new WizardDialog(HEComposite.this.getShell(),
	        				new PaillierOperationTextWizard(paillierData)).open() == Window.OK) 
	        			addTextSelected();
	        	}
	        });
        }

		/** Only Paillier does not have homomorphic multiplication */
		if (tabChoice != PAILLIER) {
	        this.homomorphMult = new Button(subComposite4, SWT.PUSH);
	        this.homomorphMult.setToolTipText(Messages.HEComposite_Homomorphic_Tooltip_Multiply);
	        this.homomorphMult.setEnabled(false);
	        this.homomorphMult.setText(Messages.HEComposite_Homomorphic_Mult_Select);
	        this.homomorphMult.addSelectionListener(new SelectionAdapter() {
	        	@Override
				public void widgetSelected(final SelectionEvent e) {
        			if (tabChoice == RSA && new WizardDialog(HEComposite.this.getShell(),
        					new RSAOperationTextWizard(rsaEncData)).open() == Window.OK) multTextSelected();
	        	}
	        });
		}

		spacerLabel = new Label(subComposite4, SWT.NONE);
		spacerLabel.setSize(130,10);

        this.decryptButton = new Button(subComposite4, SWT.PUSH);
        this.decryptButton.setToolTipText(Messages.HEComposite_Decrypt_Tooltip);
        this.decryptButton.setEnabled(false);
        this.decryptButton.setText(Messages.HEComposite_Decrypt_Select);
        this.decryptButton.addSelectionListener(new SelectionAdapter() {
        	@Override
			public void widgetSelected(final SelectionEvent e) {
        		/**
        		 * For Pallier the decryption key is simultaneously generated,
        		 * for RSA it needs to be selected
        		 */
        		switch(tabChoice) {
	        		case RSA: {
	        			if (rsaDecData.getD() == null) {
	        				if (new WizardDialog(getShell(),
	        					new RSAKeySelectionWizard(rsaDecData, false)).open() == Window.OK) {
	        						//dText.setText(rsaDecData.getD().toString());
	        						decryptResult();
	        				}
	        			} else {
	        				decryptResult();
	        			}
        			} break;
	        		case PAILLIER: decryptResult(); break;
        		}
        	}
        });

        spacerComposite = new Composite(mainComposite, SWT.NONE);
        spacerLabel = new Label(spacerComposite, SWT.NONE);
        spacerLabel.setSize(130, 266); 

        Group subComposite5 = new Group(mainComposite, SWT.SHADOW_NONE);
		subComposite5.setText(Messages.HEComposite_Reset_Text);
		subComposite5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		FillLayout fl_subComposite5 = new FillLayout(SWT.VERTICAL);
		fl_subComposite5.marginHeight = 2;
		fl_subComposite5.marginWidth = 2;
		subComposite5.setLayout(fl_subComposite5);

		this.resetNumButton = new Button(subComposite5, SWT.PUSH);
		this.resetNumButton.setToolTipText(Messages.HEComposite_Reset_Numbers_Tooltip);
		this.resetNumButton.setEnabled(false);
		this.resetNumButton.setText(Messages.HEComposite_Reset_Numbers);
		this.resetNumButton.addSelectionListener(new SelectionAdapter() {
        	@Override
			public void widgetSelected(final SelectionEvent e) {
        		resetNumbers();
        	}
        });

		this.resetAllButton = new Button(subComposite5, SWT.PUSH);
		this.resetAllButton.setToolTipText(Messages.HEComposite_Reset_All_Tooltip);
		this.resetAllButton.setEnabled(false);
		this.resetAllButton.setText(Messages.HEComposite_Reset_All);
		this.resetAllButton.addSelectionListener(new SelectionAdapter() {
        	@Override
			public void widgetSelected(final SelectionEvent e) {
        		resetAll();
        	}
        });
	}

	/**
	 * If the key is selected this function is called,
	 * the key is displayed in the algorithm area,
	 * the reset all button is enabled,
	 * the next necessary user entry is enabled
	 */
	private void keySelected() {
		this.keySel.setBackground(ColorService.GREEN);
		this.keySel.setForeground(ColorService.RED);
		this.resetAllButton.setEnabled(true);
		switch(tabChoice) {
			case RSA: {
		        this.initTextSel.setEnabled(true);
		        if (rsaEncData.getE() != null) {
		            eText.setText(rsaEncData.getE().toString());
		        }
		        /*if (rsaEncData.getD() != null) {
		            dText.setText(rsaEncData.getD().toString());
		        }*/
		        if (rsaEncData.getN() != null) {
		            nText.setText(rsaEncData.getN().toString());
		        }
		        eText.setBackground(YELLOW);
		        nText.setBackground(YELLOW);

			} break;
			case PAILLIER: {
		        this.initTextSel.setEnabled(true);
		        nText.setText(paillierData.getPubKey()[0].toString());
		        gText.setText(paillierData.getPubKey()[1].toString());
		        //lText.setText(paillierData.getPrivKey()[0].toString());
		        //mText.setText(paillierData.getPrivKey()[1].toString());

		        nText.setBackground(YELLOW);
		        gText.setBackground(YELLOW);
			} break;
		}
		initialPlain.setBackground(ColorService.LIGHTGRAY);
		initialEncryptedBits.setBackground(ColorService.LIGHTGRAY);
		homomorphPlain.setBackground(ColorService.LIGHTGRAY);
		homomorphEncryptedBits.setBackground(ColorService.LIGHTGRAY);
		homomorphResultPlain.setBackground(ColorService.LIGHTGRAY);
		homomorphResultEncryptedBits.setBackground(ColorService.LIGHTGRAY);
		plainResult.setBackground(ColorService.LIGHTGRAY);
		plainOperations.setBackground(ColorService.LIGHTGRAY);

	}

	/**
	 * The initial number is selected and encrypted, both being displayed in the algorithm area
	 */
	private void initialTextSelected() {
		this.initTextSel.setBackground(ColorService.GREEN);
		this.resetNumButton.setEnabled(true);
		switch(tabChoice) {
			case RSA: {
				this.homomorphMult.setEnabled(true);
				this.rsaResult = (new BigInteger(rsaEncData.getPlainText())).mod(rsaEncData.getN());
				this.initialPlain.setText(rsaEncData.getPlainText());
				this.initialEncrypted = new BigInteger[]{
						new BigInteger(rsaEncData.getPlainText()).modPow(rsaEncData.getE(), rsaEncData.getN())};
				this.initialEncryptedBits.setText(this.initialEncrypted[0].toString());
				this.homomorphResultEncrypted = initialEncrypted;
				this.plainOperations.setText(rsaEncData.getPlainText());
		        eText.setBackground(ColorService.LIGHTGRAY);
		        nText.setBackground(ColorService.LIGHTGRAY);
			} break;
			case PAILLIER: {
				this.homomorphAdd.setEnabled(true);
				this.paillierResult = paillierData.getPlain().mod(paillierData.getPubKey()[0]);
				this.initialPlain.setText(paillierData.getPlain().toString());
				Paillier.encrypt(paillierData);
				this.initialEncrypted = new BigInteger[]{paillierData.getCipher()};
				this.initialEncryptedBits.setText(this.initialEncrypted[0].toString());
				this.homomorphResultEncrypted = this.initialEncrypted;
				this.plainOperations.setText(paillierData.getPlain().toString());
		        nText.setBackground(ColorService.LIGHTGRAY);
		        gText.setBackground(ColorService.LIGHTGRAY);
			} break;
		}
		initialPlain.setBackground(YELLOW);
		initialEncryptedBits.setBackground(YELLOW);
		homomorphPlain.setBackground(ColorService.LIGHTGRAY);
		homomorphEncryptedBits.setBackground(ColorService.LIGHTGRAY);
		homomorphResultPlain.setBackground(ColorService.LIGHTGRAY);
		homomorphResultEncryptedBits.setBackground(ColorService.LIGHTGRAY);
		plainResult.setBackground(ColorService.LIGHTGRAY);
		plainOperations.setBackground(ColorService.LIGHTGRAY);
	}

	/**
	 * The number to be added is entered, encrypted and the addition is evaluated both homomorphically
	 * and plain, the results appear in the text boxes in the algorithm area
	 */
	private void addTextSelected() {
		this.homomorphResultPlain.setText("");
		paillierData.setOperation(paillierData.getOperation().mod(paillierData.getPubKey()[0]));
		this.homomorphPlain.setText(paillierData.getOperation().toString());
		Paillier.encryptOperation(paillierData);
		this.homomorphEncrypted = new BigInteger[]{paillierData.getOperationCipher()};
		this.homomorphEncryptedBits.setText(this.homomorphEncrypted[0].toString());
		this.homomorphResultEncrypted[0] =
			this.homomorphResultEncrypted[0].multiply(this.homomorphEncrypted[0])
			.mod(paillierData.getPubKey()[0].pow(2));
		this.paillierData.setResultCipher(this.homomorphResultEncrypted[0]);
		this.homomorphResultEncryptedBits.setText(this.homomorphResultEncrypted[0].toString());
		this.decryptButton.setEnabled(true);
		if (first) {
			first = false;
		}
		this.plainOperations.setText(this.plainOperations.getText() + " + " + paillierData.getOperation());
		paillierResult = paillierResult.add(paillierData.getOperation()).mod(paillierData.getPubKey()[0].pow(2));
		this.plainResult.setText(paillierResult.toString());

        nText.setBackground(ColorService.LIGHTGRAY);
        gText.setBackground(ColorService.LIGHTGRAY);
		initialPlain.setBackground(ColorService.LIGHTGRAY);
		initialEncryptedBits.setBackground(ColorService.LIGHTGRAY);
		homomorphPlain.setBackground(YELLOW);
		homomorphEncryptedBits.setBackground(YELLOW);
		homomorphResultPlain.setBackground(ColorService.LIGHTGRAY);
		homomorphResultEncryptedBits.setBackground(YELLOW);
		plainResult.setBackground(YELLOW);
		plainOperations.setBackground(YELLOW);
	}

	/**
	 * The number to be multiplied with is entered, encrypted and the multiplication is evaluated both
	 * homomorphically and plain, the results appear in the text boxes in the algorithm area
	 */
	private void multTextSelected() {
		this.homomorphResultPlain.setText("");
		this.homomorphPlain.setText(rsaEncData.getOperation());
		this.homomorphEncrypted = new BigInteger[]{
				new BigInteger(rsaEncData.getOperation()).modPow(rsaEncData.getE(), rsaEncData.getN())};
		this.homomorphEncryptedBits.setText(this.homomorphEncrypted[0].toString());
		this.homomorphResultEncrypted[0] =
			this.homomorphResultEncrypted[0].multiply(this.homomorphEncrypted[0]).mod(rsaEncData.getN());
		this.homomorphResultEncryptedBits.setText(this.homomorphResultEncrypted[0].toString());
		this.decryptButton.setEnabled(true);
		if (first) {
			first = false;
		}
		this.plainOperations.setText(this.plainOperations.getText() + " * " + rsaEncData.getOperation());
		rsaResult = rsaResult.multiply(new BigInteger(rsaEncData.getOperation()));
		this.plainResult.setText(rsaResult.toString());

		eText.setBackground(ColorService.LIGHTGRAY);
        nText.setBackground(ColorService.LIGHTGRAY);
		initialPlain.setBackground(ColorService.LIGHTGRAY);
		initialEncryptedBits.setBackground(ColorService.LIGHTGRAY);
		homomorphPlain.setBackground(YELLOW);
		homomorphEncryptedBits.setBackground(YELLOW);
		homomorphResultPlain.setBackground(ColorService.LIGHTGRAY);
		homomorphResultEncryptedBits.setBackground(YELLOW);
		plainResult.setBackground(YELLOW);
		plainOperations.setBackground(YELLOW);
	}

	/**
	 * Decrypts the homomorphic result
	 */
	private void decryptResult() {
		switch(tabChoice) {
			case RSA: {
				this.homomorphResultPlain.setText(
				this.homomorphResultEncrypted[0].modPow(rsaDecData.getD(), rsaDecData.getN()).toString());

				eText.setBackground(ColorService.LIGHTGRAY);
		        nText.setBackground(ColorService.LIGHTGRAY);
			} break;
			case PAILLIER: {
				Paillier.decryptResult(paillierData);
				this.homomorphResultPlain.setText(paillierData.getResultPlain().toString());

		        nText.setBackground(ColorService.LIGHTGRAY);
		        gText.setBackground(ColorService.LIGHTGRAY);
			} break;
		}

		initialPlain.setBackground(ColorService.LIGHTGRAY);
		initialEncryptedBits.setBackground(ColorService.LIGHTGRAY);
		homomorphPlain.setBackground(ColorService.LIGHTGRAY);
		homomorphEncryptedBits.setBackground(ColorService.LIGHTGRAY);
		homomorphResultPlain.setBackground(YELLOW);
		homomorphResultEncryptedBits.setBackground(ColorService.LIGHTGRAY);
		plainResult.setBackground(ColorService.LIGHTGRAY);
		plainOperations.setBackground(ColorService.LIGHTGRAY);
	}

	/**
	 * Resets all numbers and disables all buttons such that a new initial text must be entered
	 */
	private void resetNumbers() {
		switch(tabChoice) {
			case RSA: {
				this.initialPlain.setText("");
				this.initialEncryptedBits.setText("");
				this.homomorphPlain.setText("");
				this.homomorphEncryptedBits.setText("");
				this.homomorphResultEncryptedBits.setText("");
				this.homomorphResultPlain.setText("");
				this.plainOperations.setText("");
				this.plainResult.setText("");
				this.first = true;
				this.resetNumButton.setEnabled(false);
				this.decryptButton.setEnabled(false);
				this.homomorphMult.setEnabled(false);
				this.initTextSel.setBackground(ColorService.RED);
			} break;
			case PAILLIER: {
				this.initialPlain.setText("");
				this.initialEncryptedBits.setText("");
				this.homomorphPlain.setText("");
				this.homomorphEncryptedBits.setText("");
				this.homomorphResultEncryptedBits.setText("");
				this.homomorphResultPlain.setText("");
				this.plainOperations.setText("");
				this.plainResult.setText("");
				this.first = true;
				this.resetNumButton.setEnabled(false);
				this.decryptButton.setEnabled(false);
				this.homomorphAdd.setEnabled(false);
				this.initTextSel.setBackground(ColorService.RED);
			}
		}


		initialPlain.setBackground(ColorService.LIGHTGRAY);
		initialEncryptedBits.setBackground(ColorService.LIGHTGRAY);
		homomorphPlain.setBackground(ColorService.LIGHTGRAY);
		homomorphEncryptedBits.setBackground(ColorService.LIGHTGRAY);
		homomorphResultPlain.setBackground(ColorService.LIGHTGRAY);
		homomorphResultEncryptedBits.setBackground(ColorService.LIGHTGRAY);
		plainResult.setBackground(ColorService.LIGHTGRAY);
		plainOperations.setBackground(ColorService.LIGHTGRAY);
	}

	/**
	 * Resets everything
	 */
	private void resetAll() {
		resetNumbers();
		switch(tabChoice) {
			case RSA: {
				this.resetAllButton.setEnabled(false);
				this.keySel.setBackground(ColorService.RED);
				this.initTextSel.setEnabled(false);
				this.rsaEncData = new RSAData(Action.EncryptAction);
				this.rsaDecData = new RSAData(Action.DecryptAction);
				//this.dText.setText("");
				this.eText.setText("");
				this.nText.setText("");

				eText.setBackground(ColorService.LIGHTGRAY);
		        nText.setBackground(ColorService.LIGHTGRAY);
			} break;
			case PAILLIER: {
				this.resetAllButton.setEnabled(false);
				this.keySel.setBackground(ColorService.RED);
				this.initTextSel.setEnabled(false);
				this.paillierData = new PaillierData();
				this.nText.setText("");
				this.gText.setText("");
				//this.lText.setText("");
				//this.mText.setText("");

		        nText.setBackground(ColorService.LIGHTGRAY);
		        gText.setBackground(ColorService.LIGHTGRAY);
			}
		}
	}

	/**
	 * Creates the algorithm area
	 * @param parent the composite in which the algorithm area is created
	 */
	private void createAlgoArea(final Composite parent) {
		final Composite g = new Composite(parent, SWT.SHADOW_NONE);
		g.setLayout(new GridLayout());
		g.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));		
		
		this.createKeyArea(g);
		Group mainComposite = new Group(g, SWT.SHADOW_NONE);
		mainComposite.setText(Messages.HEComposite_HomomorphicArea);
		mainComposite.setLayout(new GridLayout());
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		this.createInitialArea(mainComposite);
		this.createHomomorphicArea(mainComposite);
		this.createPlainArea(g);
	}

	/**
	 * Creates the key area
	 * @param parent the composite in which it is created
	 */
	private void createKeyArea(final Composite parent) {
		Group mainComposite = new Group(parent, SWT.SHADOW_NONE);
		mainComposite.setText(org.jcryptool.visual.he.Messages.HEComposite_KeyArea_Public_Key);
		mainComposite.setLayout(new GridLayout(1, false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite subComposite = new Composite(mainComposite, SWT.NONE);
		subComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_subComposite = new GridLayout(2, true);
		gl_subComposite.marginHeight = 0;
		gl_subComposite.marginWidth = 0;
		subComposite.setLayout(gl_subComposite);

		if (tabChoice == RSA) {
			Composite composite_e = new Composite(subComposite, SWT.NONE);
			composite_e.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
			GridLayout gl_composite_e = new GridLayout(2, false);
			gl_composite_e.marginHeight = 0;
			gl_composite_e.marginWidth = 0;
			composite_e.setLayout(gl_composite_e);
	        Label lbl_e = new Label(composite_e, SWT.RIGHT);
	        lbl_e.setText("e  "); //$NON-NLS-1$
			GridData gd_lbl_e = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
			gd_lbl_e.widthHint = lblWidth;
	        lbl_e.setLayoutData(gd_lbl_e);
	        eText = new Text(composite_e, SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL);
	        eText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		}
        
		Composite composite_N = new Composite(subComposite, SWT.NONE);
		composite_N.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		GridLayout gl_composite_N = new GridLayout(2, false);
		gl_composite_N.marginHeight = 0;
		gl_composite_N.marginWidth = 0;
		composite_N.setLayout(gl_composite_N);
        Label lbl_N = new Label(composite_N, SWT.RIGHT);
        lbl_N.setText("N  "); //$NON-NLS-1$
        GridData gd_lbl_N = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
        gd_lbl_N.widthHint = lblWidth;
        lbl_N.setLayoutData(gd_lbl_N);
        nText = new Text(composite_N, SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL);
        nText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        
        if (tabChoice == PAILLIER) {
    		Composite composite_g = new Composite(subComposite, SWT.NONE);
    		composite_g.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
    		GridLayout gl_composite_g = new GridLayout(2, false);
    		gl_composite_g.marginHeight = 0;
    		gl_composite_g.marginWidth = 0;
    		composite_g.setLayout(gl_composite_g);
            Label lbl_g = new Label(composite_g, SWT.RIGHT);
            lbl_g.setText("g  "); //$NON-NLS-1$
            GridData gd_lbl_g = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
            gd_lbl_g.widthHint = lblWidth;
            lbl_g.setLayoutData(gd_lbl_g);
            gText = new Text(composite_g, SWT.READ_ONLY | SWT.BORDER | SWT.H_SCROLL);
            gText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        }
    }

	/**
	 * Creates the area in which the initial number and its encryption are shown
	 * @param parent the composite in which it is created
	 */
	private void createInitialArea(final Composite parent) {		
		Group mainComposite = new Group(parent, SWT.SHADOW_NONE);
		mainComposite.setText(Messages.HEComposite_Initial_Data);
		mainComposite.setLayout(new GridLayout(1, false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Composite subComposite = new Composite(mainComposite, SWT.NONE);
		subComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_subComposite = new GridLayout(2, false);
		gl_subComposite.marginHeight = 0;
		gl_subComposite.marginWidth = 0;
		subComposite.setLayout(gl_subComposite);
        Label labelDecimal = new Label(subComposite, SWT.RIGHT);
        labelDecimal.setText(Messages.HEComposite_Initial_Number);
        GridData gd_labelDecimal = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
        gd_labelDecimal.widthHint = lblWidth;
        labelDecimal.setLayoutData(gd_labelDecimal);
        initialPlain = new Text(subComposite, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
        initialPlain.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        ((GridData)initialPlain.getLayoutData()).heightHint = 50;

        Composite subComposite2 = new Composite(mainComposite, SWT.NONE);
        subComposite2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_subComposite2 = new GridLayout(2, false);
		gl_subComposite2.marginHeight = 0;
		gl_subComposite2.marginWidth = 0;
		subComposite2.setLayout(gl_subComposite2);
        Label labelCipher = new Label(subComposite2, SWT.RIGHT);
        labelCipher.setText(Messages.HEComposite_Initial_Number_As_Enc);
        GridData gd_labelCipher = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
        gd_labelCipher.widthHint = lblWidth;
        labelCipher.setLayoutData(gd_labelCipher);
        initialEncryptedBits = new Text(subComposite2, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
        initialEncryptedBits.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        ((GridData)initialEncryptedBits.getLayoutData()).heightHint = 50;
    }

	/**
	 * Creates the area in which the operation number, its encryption, the result and the decryption are shown
	 * @param parent the composite in which it is created
	 */
	private void createHomomorphicArea(final Composite parent) {		
		Group mainComposite = new Group(parent, SWT.SHADOW_NONE);
		mainComposite.setText(Messages.HEComposite_Operation_Area);
		mainComposite.setLayout(new GridLayout(1, false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite subComposite = new Composite(mainComposite, SWT.NONE);
		subComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_subComposite = new GridLayout(2, false);
		gl_subComposite.marginHeight = 0;
		gl_subComposite.marginWidth = 0;
		subComposite.setLayout(gl_subComposite);		
        Label labelDecimal = new Label(subComposite, SWT.RIGHT);
        labelDecimal.setText(Messages.HEComposite_Operation_Number);
	    GridData gd_labelDecimal = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
	    gd_labelDecimal.widthHint = lblWidth;
	    labelDecimal.setLayoutData(gd_labelDecimal);
        homomorphPlain = new Text(subComposite, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
        homomorphPlain.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        ((GridData)homomorphPlain.getLayoutData()).heightHint = 50;

        Composite subComposite2 = new Composite(mainComposite, SWT.NONE);
        subComposite2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_subComposite2 = new GridLayout(2, false);
		gl_subComposite2.marginHeight = 0;
		gl_subComposite2.marginWidth = 0;
		subComposite2.setLayout(gl_subComposite2);
		Label labelCipher = new Label(subComposite2, SWT.RIGHT);
		labelCipher.setText(Messages.HEComposite_Operation_Number_As_Enc);
		GridData gd_labelCipher = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_labelCipher.widthHint = lblWidth;
		labelCipher.setLayoutData(gd_labelCipher);
        homomorphEncryptedBits = new Text(subComposite2, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
        homomorphEncryptedBits.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        ((GridData)homomorphEncryptedBits.getLayoutData()).heightHint = 50;

		Group mainComposite2 = new Group(parent, SWT.SHADOW_NONE);
		mainComposite2.setText(Messages.HEComposite_Result_Area);
		mainComposite2.setLayout(new GridLayout(1, false));
		mainComposite2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite subComposite3 = new Composite(mainComposite2, SWT.NONE);
		subComposite3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_subComposite3 = new GridLayout(2, false);
		gl_subComposite3.marginHeight = 0;
		gl_subComposite3.marginWidth = 0;
		subComposite3.setLayout(gl_subComposite3);	
        Label labelResultEnc = new Label(subComposite3, SWT.RIGHT);
        labelResultEnc.setText(Messages.HEComposite_Result_Number_As_Enc);
	    GridData gd_labelResultEnc = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
	    gd_labelResultEnc.widthHint = lblWidth;
	    labelResultEnc.setLayoutData(gd_labelResultEnc);
        homomorphResultEncryptedBits = new Text(subComposite3, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
        homomorphResultEncryptedBits.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        ((GridData)homomorphResultEncryptedBits.getLayoutData()).heightHint = 50;

		Composite subComposite4 = new Composite(mainComposite2, SWT.NONE);
		subComposite4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_subComposite4 = new GridLayout(2, false);
		gl_subComposite4.marginHeight = 0;
		gl_subComposite4.marginWidth = 0;
		subComposite4.setLayout(gl_subComposite4);
        Label labelResultNumber = new Label(subComposite4, SWT.RIGHT);
        labelResultNumber.setText(Messages.HEComposite_Result_Number);
	    GridData gd_labelResultNumber = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
	    gd_labelResultNumber.widthHint = lblWidth;
        labelResultNumber.setLayoutData(gd_labelResultNumber);
        homomorphResultPlain = new Text(subComposite4, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
        homomorphResultPlain.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        ((GridData)homomorphResultPlain.getLayoutData()).heightHint = 50;
	}

	/**
	 * Creates the area in which the operations in plaintext are shown
	 * @param parent the composite in which it is created
	 */
	private void createPlainArea(final Composite parent) {
		Group mainComposite = new Group(parent, SWT.SHADOW_NONE);
		mainComposite.setText(Messages.HEComposite_Plain_Data);
		mainComposite.setLayout(new GridLayout(1, false));
		mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Composite subComposite = new Composite(mainComposite, SWT.NONE);
		subComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_subComposite = new GridLayout(2, false);
		gl_subComposite.marginHeight = 0;
		gl_subComposite.marginWidth = 0;
		subComposite.setLayout(gl_subComposite);
		Label labelOperations = new Label(subComposite, SWT.RIGHT);
		GridData gd_labelOperations = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_labelOperations.widthHint = lblWidth;
	    labelOperations.setLayoutData(gd_labelOperations);
		labelOperations.setText(Messages.HEComposite_Plain_Operations);
		plainOperations = new Text(subComposite, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		plainOperations.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		((GridData)plainOperations.getLayoutData()).heightHint = 50;

        Composite subComposite2 = new Composite(mainComposite, SWT.NONE);
        subComposite2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl_subComposite2 = new GridLayout(2, false);
		gl_subComposite2.marginHeight = 0;
		gl_subComposite2.marginWidth = 0;
		subComposite2.setLayout(gl_subComposite2);
        Label labelResult = new Label(subComposite2, SWT.RIGHT);
        GridData gd_labelResult = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
        gd_labelResult.widthHint = lblWidth;
		labelResult.setLayoutData(gd_labelResult);
        labelResult.setText(Messages.HEComposite_Result);
		plainResult = new Text(subComposite2, SWT.BORDER | SWT.MULTI | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL);
		plainResult.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		((GridData)plainResult.getLayoutData()).heightHint = 50;
    }
}
