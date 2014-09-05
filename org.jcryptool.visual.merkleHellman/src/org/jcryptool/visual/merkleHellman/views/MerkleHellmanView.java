package org.jcryptool.visual.merkleHellman.views;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.jcryptool.visual.merkleHellman.algorithm.MerkleHellman;
import org.eclipse.swt.graphics.Point;

/**
 * 
 * @author Ferit Dogan
 * 
 */
public class MerkleHellmanView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "org.jcryptool.visual.merkleHellman.views.MerkleHellmanView"; //$NON-NLS-1$
	private Composite compositePrivateKey;
	private ScrolledComposite scrolledCompositePrivateKey;
	private Text textM;
	private Text textSumA;
	private Text textW;
	private Text textU;
	private ScrolledComposite scrolledCompositePublicKey;
	private Composite compositePublicKey;

	private ArrayList<Text> privateKeyFields = new ArrayList<Text>();
	private ArrayList<Text> publicKeyFields = new ArrayList<Text>();
	private MerkleHellman privKey;
	private Button btnCreateKeys;
	private Button btnGenerateNewKey;
	private Combo comboKeyElements;
	private Combo comboStartValue;
	private Text textP_encryption;
	private Text textMax;
	private Text textBinary;
	private Text textC_decryption;
	private Text textCC;

	private VerifyListener vlPlainText = new VerifyListener() {

		@Override
		public void verifyText(VerifyEvent e) {
			Text textField = null;
			e.doit = true;

			if (e.getSource() instanceof Text) {
				textField = (Text) e.getSource();
			}

			if (textField == null
					|| ((textField.getText().length() == 0 && e.text.compareTo("0") == 0) || (textField.getSelection().x == 0 && e.keyCode == 48))) { //$NON-NLS-1$
				e.doit = false;
				return;
			}

			String text = e.text;

			char[] chars = text.toCharArray();

			for (int i = 0; i < chars.length; i++) {
				if (chars.length > 1 && i == 0 && chars[i] == '0') {
					e.doit = false;
					break;
				}

				if (!Character.isDigit(chars[i])) {
					e.doit = false;
					break;
				}
			}
			for (int j = 0; j < e.text.toCharArray().length; j++) {
				if (Character.isDigit(e.text.toCharArray()[j])) {
					if ((textField.getText().length() != 0 && (new BigInteger(textField.getText() + e.text)
							.compareTo(new BigInteger(textMax.getText())) >= 0))) {
						e.doit = false;
						return;
					}
				}
			}
		}
	};

	private VerifyListener vlNumber = new VerifyListener() {

		@Override
		public void verifyText(VerifyEvent e) {
			Text textField = null;
			e.doit = true;

			if (e.getSource() instanceof Text) {
				textField = (Text) e.getSource();
			}

			if (textField == null
					|| ((textField.getText().length() == 0 && e.text.compareTo("0") == 0) || (textField.getSelection().x == 0 && e.keyCode == 48))) { //$NON-NLS-1$
				e.doit = false;
				return;
			}

			String text = e.text;

			char[] chars = text.toCharArray();

			for (int i = 0; i < chars.length; i++) {
				if (chars.length > 1 && i == 0 && chars[i] == '0') {
					e.doit = false;
					break;
				}

				if (!Character.isDigit(chars[i])) {
					e.doit = false;
					break;
				}
			}
		}
	};
	private Button btnEncrypt;
	private Button btnDecrypt;
	private Table tableEncrypt;
	private Text textC_encryption;
	private Text textBinary_decrypted;
	private Table tableDecrypt;
	private Label lblPublicKeyB;
	private StyledText styledTextDescription;
	private StyleRange header;

	/**
	 * The constructor.
	 */
	public MerkleHellmanView() {
	}

	@Override
	public void createPartControl(Composite parent) {

		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite compositeMain = new Composite(scrolledComposite, SWT.NONE);
		compositeMain.setLayout(new GridLayout(2, false));

		styledTextDescription = new StyledText(compositeMain, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP);
		styledTextDescription.setEditable(false);
		GridData gd_styledTextDescription = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		gd_styledTextDescription.widthHint = 300;
		gd_styledTextDescription.heightHint = 60;
		styledTextDescription.setLayoutData(gd_styledTextDescription);
		styledTextDescription.setText(Messages.MerkleHellmanView_0000 + Messages.MerkleHellmanView_0);
		
		header = new StyleRange();
		header.start = 0;
//		if (System.getProperties().get("osgi.nl").toString().compareToIgnoreCase("de") == 0) {
//			gd_styledTextDescription.heightHint = 110;
//		} else if (System.getProperties().get("osgi.nl").toString().compareToIgnoreCase("en") == 0) {
//			gd_styledTextDescription.heightHint = 95;
//		}
		
		
		header.length = Messages.MerkleHellmanView_0000.length();
		header.fontStyle = SWT.BOLD;
		styledTextDescription.setStyleRange(header);

		Group grpGroup = new Group(compositeMain, SWT.NONE);
		grpGroup.setLayout(new GridLayout(1, false));
		grpGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		grpGroup.setText(Messages.MerkleHellmanView_1);

		Composite compositeSelection = new Composite(grpGroup, SWT.NONE);
		compositeSelection.setLayout(new GridLayout(5, false));
		compositeSelection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblNumberOfElementsInKey = new Label(compositeSelection, SWT.NONE);
		lblNumberOfElementsInKey.setText(Messages.MerkleHellmanView_2);

		comboKeyElements = new Combo(compositeSelection, SWT.READ_ONLY);
		comboKeyElements.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MerkleHellmanView.this.resetView();

				createPrivateKey();
				lblPublicKeyB.setText(Messages.MerkleHellmanView_6 + comboKeyElements.getText());

			}
		});
		comboKeyElements.setItems(new String[] { "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
				"16", "17", "18", "19", "20" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		comboKeyElements.select(0);

		Label lblStartValue = new Label(compositeSelection, SWT.NONE);
		lblStartValue.setText(Messages.MerkleHellmanView_3);

		comboStartValue = new Combo(compositeSelection, SWT.READ_ONLY);
		comboStartValue.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				MerkleHellmanView.this.resetView();
				createPrivateKey();
			}
		});
		comboStartValue.setItems(new String[] { "32", "64", "128", "256", "512", "1024", "2048", "4096", "8192", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$
				"16384", "32786", "65536", "131072" }); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		comboStartValue.select(0);

		btnGenerateNewKey = new Button(compositeSelection, SWT.NONE);
		GridData gd_btnGenerateNewKey = new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1);
		gd_btnGenerateNewKey.widthHint = 180;
		btnGenerateNewKey.setLayoutData(gd_btnGenerateNewKey);
		btnGenerateNewKey.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				createPrivateKey();

				Control[] content = compositePublicKey.getChildren();
				for (Control c : content) {
					c.dispose();
				}
				publicKeyFields.clear();
				btnCreateKeys.setEnabled(true);

			}
		});
		btnGenerateNewKey.setText(Messages.MerkleHellmanView_4);

		Group grpPrivateKey = new Group(grpGroup, SWT.NONE);
		grpPrivateKey.setText(Messages.MerkleHellmanView_5);
		grpPrivateKey.setLayout(new GridLayout(1, false));
		GridData gd_grpPrivateKey = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
		gd_grpPrivateKey.heightHint = 71;
		grpPrivateKey.setLayoutData(gd_grpPrivateKey);
		scrolledCompositePrivateKey = new ScrolledComposite(grpPrivateKey, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledCompositePrivateKey.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledCompositePrivateKey.setExpandHorizontal(true);
		scrolledCompositePrivateKey.setExpandVertical(true);
		scrolledCompositePrivateKey.getVerticalBar().setIncrement(10);
		scrolledCompositePrivateKey.getHorizontalBar().setIncrement(10);

		compositePrivateKey = new Composite(scrolledCompositePrivateKey, SWT.NONE);
		compositePrivateKey.setLayout(new GridLayout(10, false));

		scrolledCompositePrivateKey.setContent(compositePrivateKey);
		scrolledCompositePrivateKey.setMinSize(compositePrivateKey.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrolledCompositePrivateKey.layout();

		createPrivateKeyFields(Integer.parseInt(comboKeyElements.getText()));

		Composite compositeParameter = new Composite(grpGroup, SWT.NONE);
		compositeParameter.setLayout(new GridLayout(7, false));
		compositeParameter.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		Label lblM = new Label(compositeParameter, SWT.NONE);
		lblM.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblM.setText("M ="); //$NON-NLS-1$

		textM = new Text(compositeParameter, SWT.BORDER);
		textM.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textM.addVerifyListener(vlNumber);

		Label lblSumai = new Label(compositeParameter, SWT.NONE);
		lblSumai.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSumai.setText("> SUM (A(i)) = "); //$NON-NLS-1$

		textSumA = new Text(compositeParameter, SWT.BORDER);
		textSumA.setEditable(false);
		textSumA.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textSumA.addVerifyListener(vlNumber);

		Label lblW = new Label(compositeParameter, SWT.NONE);
		lblW.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblW.setText("W ="); //$NON-NLS-1$

		textW = new Text(compositeParameter, SWT.BORDER);
		textW.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textW.addVerifyListener(vlNumber);

		Label lblGCD = new Label(compositeParameter, SWT.NONE);
		lblGCD.setText(Messages.MerkleHellmanView_31);

		Label lblU = new Label(compositeParameter, SWT.NONE);
		lblU.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblU.setText("U ="); //$NON-NLS-1$

		textU = new Text(compositeParameter, SWT.BORDER);
		textU.setEditable(false);
		textU.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblEquation = new Label(compositeParameter, SWT.NONE);
		lblEquation.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblEquation.setText("=> W * U = 1 mod M"); //$NON-NLS-1$

		lblPublicKeyB = new Label(compositeParameter, SWT.NONE);
		GridData gd_lblPublicKeyB = new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1);
		gd_lblPublicKeyB.widthHint = 297;
		lblPublicKeyB.setLayoutData(gd_lblPublicKeyB);
		lblPublicKeyB.setText(Messages.MerkleHellmanView_6 + comboKeyElements.getText());

		btnCreateKeys = new Button(compositeParameter, SWT.NONE);
		btnCreateKeys.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (!textM.getText().isEmpty() && !textW.getText().isEmpty()) {
					if (privateKeyValuesChanged() && !mAndWValuesChanged()) {
						BigInteger[] keys = new BigInteger[privateKeyFields.size()];
						for (int i = 0; i < keys.length; i++) {
							BigInteger k = new BigInteger(privateKeyFields.get(i).getText());
							keys[i] = k;
						}

						privKey.updatePrivateKey(keys);
						textM.setText(privKey.getM().toString());
						textSumA.setText(privKey.getSum().toString());
						textU.setText(privKey.getU().toString());
						textW.setText(privKey.getW().toString());

					} else if (!privateKeyValuesChanged() && mAndWValuesChanged()) {
						BigInteger m = new BigInteger(textM.getText());
						BigInteger w = new BigInteger(textW.getText());

						BigInteger sum = privKey.getSum();

						if (m.compareTo(sum) <= 0) {
							MessageDialog
									.openInformation(
											null,
											"Info", Messages.MerkleHellmanView_20 + privKey.getSum() + Messages.MerkleHellmanView_21); //$NON-NLS-1$
							return;
						}

						if (w.compareTo(m) >= 0) {
							MessageDialog
									.openInformation(
											null,
											Messages.MerkleHellmanView_35, Messages.MerkleHellmanView_22 + m.toString() + Messages.MerkleHellmanView_23);
							return;
						}

						if (m.gcd(w).compareTo(BigInteger.ONE) != 0) {
							MessageDialog.openError(null,
									"Info", Messages.MerkleHellmanView_32 + m.gcd(w).toString() + Messages.MerkleHellmanView_24); //$NON-NLS-1$
							return;
						}

						privKey.setM(m);
						privKey.setW(w);
						textU.setText(privKey.getU().toString());
					} else if (privateKeyValuesChanged() && mAndWValuesChanged()) {
						BigInteger[] keys = new BigInteger[privateKeyFields.size()];
						for (int i = 0; i < keys.length; i++) {
							BigInteger k = new BigInteger(privateKeyFields.get(i).getText());
							keys[i] = k;
						}

						privKey.updatePrivateKey(keys);

						BigInteger m = new BigInteger(textM.getText());
						BigInteger w = new BigInteger(textW.getText());

						BigInteger sum = privKey.getSum();
						textSumA.setText(privKey.getSum().toString());

						if (m.compareTo(sum) <= 0) {
							MessageDialog
									.openInformation(
											null,
											"Info", Messages.MerkleHellmanView_25 + privKey.getSum() + Messages.MerkleHellmanView_26); //$NON-NLS-1$
							return;
						}

						if (w.compareTo(m) >= 0) {
							MessageDialog
									.openInformation(
											null,
											"Info", Messages.MerkleHellmanView_27 + m.toString() + Messages.MerkleHellmanView_28); //$NON-NLS-1$
							return;
						}

						if (m.gcd(w).compareTo(BigInteger.ONE) != 0) {
							MessageDialog.openError(null,
									Messages.MerkleHellmanView_33, Messages.MerkleHellmanView_34 + m.gcd(w).toString() + Messages.MerkleHellmanView_29);
							return;
						}

						privKey.setM(m);
						privKey.setW(w);
						textU.setText(privKey.getU().toString());
					}
					createPublicKeyFields(Integer.parseInt(comboKeyElements.getText()));
					scrolledCompositePublicKey.setMinSize(compositePublicKey.computeSize(SWT.DEFAULT, SWT.DEFAULT));
					btnCreateKeys.setEnabled(false);
					btnGenerateNewKey.setEnabled(false);
					textP_encryption.setEnabled(true);
					textP_encryption.setEditable(true);
					textBinary.setEnabled(true);
					for (Text t : privateKeyFields) {
						t.setEditable(false);
					}
					for (Text t : publicKeyFields) {
						t.setEditable(false);
					}
					textM.setEditable(false);
					textW.setEditable(false);
					styledTextDescription.setText(Messages.MerkleHellmanView_0000 + Messages.MerkleHellmanView_00);
					styledTextDescription.setStyleRange(header);
				}
			}
		});
		GridData gd_btnCreateKeys = new GridData(SWT.CENTER, SWT.CENTER, false, false, 1, 1);
		gd_btnCreateKeys.widthHint = 180;
		btnCreateKeys.setLayoutData(gd_btnCreateKeys);
		btnCreateKeys.setText(Messages.MerkleHellmanView_7);

		Group grpPublicKey = new Group(grpGroup, SWT.NONE);
		grpPublicKey.setText(Messages.MerkleHellmanView_8);
		GridData gd_grpPublicKey = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_grpPublicKey.heightHint = 71;
		grpPublicKey.setLayoutData(gd_grpPublicKey);
		grpPublicKey.setLayout(new GridLayout(1, false));

		scrolledCompositePublicKey = new ScrolledComposite(grpPublicKey, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledCompositePublicKey.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		scrolledCompositePublicKey.setExpandHorizontal(true);
		scrolledCompositePublicKey.setExpandVertical(true);
		scrolledCompositePublicKey.getVerticalBar().setIncrement(10);
		scrolledCompositePublicKey.getHorizontalBar().setIncrement(10);

		compositePublicKey = new Composite(scrolledCompositePublicKey, SWT.NONE);
		compositePublicKey.setLayout(new GridLayout(10, false));

		scrolledCompositePublicKey.setContent(compositePublicKey);
		scrolledCompositePublicKey.setMinSize(compositePublicKey.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		Group grpEncryption = new Group(compositeMain, SWT.NONE);
		grpEncryption.setLayout(new GridLayout(4, false));
		grpEncryption.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpEncryption.setText(Messages.MerkleHellmanView_9);

		Composite compositeEquation = new Composite(grpEncryption, SWT.NONE);
		compositeEquation.setLayout(new GridLayout(4, false));
		compositeEquation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 4, 1));

		Label lblP_encryption = new Label(compositeEquation, SWT.NONE);
		lblP_encryption.setText("m ="); //$NON-NLS-1$

		textP_encryption = new Text(compositeEquation, SWT.BORDER);
		textP_encryption.setEnabled(false);

		textP_encryption.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (e.getSource() instanceof Text) {
					Text tmp = (Text) e.getSource();

					if (tmp.getText().compareTo("") != 0) { //$NON-NLS-1$
						BigInteger numberOfElements = new BigInteger(comboKeyElements.getText());

						BigInteger a = new BigInteger(tmp.getText());
						String bitRepresentation = a.toString(2);

						StringBuilder sb = new StringBuilder();
						if (bitRepresentation.length() <= numberOfElements.intValue()) {
							int counter = numberOfElements.intValue() - bitRepresentation.length();

							for (int i = 0; i < counter; i++) {
								sb.append("0"); //$NON-NLS-1$
							}
							sb.append(bitRepresentation);
						}

						MerkleHellmanView.this.textBinary.setText(sb.toString());
						btnEncrypt.setEnabled(true);
					} else {
						MerkleHellmanView.this.textBinary.setText(""); //$NON-NLS-1$
						btnEncrypt.setEnabled(false);
					}
				}
			}
		});

		textP_encryption.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textP_encryption.addVerifyListener(vlPlainText);

		Label lblRange = new Label(compositeEquation, SWT.NONE);
		lblRange.setText("0 < m <"); //$NON-NLS-1$

		textMax = new Text(compositeEquation, SWT.BORDER | SWT.READ_ONLY);
		textMax.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textMax.setText(new BigInteger("2").pow(Integer.parseInt(comboKeyElements.getText())).toString()); //$NON-NLS-1$

		Label lblBinaryRepresentation = new Label(grpEncryption, SWT.NONE);
		lblBinaryRepresentation.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblBinaryRepresentation.setText(Messages.MerkleHellmanView_10);

		textBinary = new Text(grpEncryption, SWT.BORDER | SWT.READ_ONLY);
		textBinary.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));

		tableEncrypt = new Table(grpEncryption, SWT.BORDER | SWT.FULL_SELECTION);
		tableEncrypt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 4, 1));
		tableEncrypt.setHeaderVisible(true);
		tableEncrypt.setLinesVisible(true);

		TableColumn tblclmnIteration_e = new TableColumn(tableEncrypt, SWT.NONE);
		tblclmnIteration_e.setWidth(60);
		tblclmnIteration_e.setText(Messages.MerkleHellmanView_11);

		TableColumn tblclmnBit = new TableColumn(tableEncrypt, SWT.NONE);
		tblclmnBit.setWidth(34);
		tblclmnBit.setText("m(i)"); //$NON-NLS-1$

		TableColumn tblclmnEquation_e = new TableColumn(tableEncrypt, SWT.NONE);
		tblclmnEquation_e.setWidth(350);
		tblclmnEquation_e.setText(Messages.MerkleHellmanView_12);

		Label lblC_encryption = new Label(grpEncryption, SWT.NONE);
		lblC_encryption.setText("c ="); //$NON-NLS-1$

		textC_encryption = new Text(grpEncryption, SWT.BORDER);
		textC_encryption.setEnabled(false);
		textC_encryption.setEditable(false);
		textC_encryption.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		btnEncrypt = new Button(grpEncryption, SWT.NONE);
		btnEncrypt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int numberOfElement = Integer.parseInt(comboKeyElements.getText());
				String bitRepresentation = textBinary.getText();

				for (int i = 0; i < numberOfElement; i++) {
					StringBuilder sb = new StringBuilder();
					sb.append(String.valueOf(bitRepresentation.charAt(i)) + " * " + privKey.getPublicKeyElement(i) //$NON-NLS-1$
							+ " mod " + privKey.getM() + " = "); //$NON-NLS-1$ //$NON-NLS-2$ 
					sb.append(new BigInteger(String.valueOf(bitRepresentation.charAt(i))).multiply(privKey.getPublicKeyElement(i).mod(privKey.getM())));

					TableItem tmp = new TableItem(tableEncrypt, SWT.BORDER);
					tmp.setText(0, String.valueOf(i));
					tmp.setText(1, String.valueOf(bitRepresentation.charAt(i)));
					tmp.setText(2, sb.toString());
				}
				
//				StringBuilder sb = new StringBuilder();
//				StringTokenizer st = new StringTokenizer(textP_encryption.getText(), " ,;|");
//				
//				while (st.hasMoreElements()) {
//					String s = (String) st.nextElement();
//					sb.append(privKey.encrypt(new BigInteger(s)));
//					sb.append(" ");
//				}
//				
//				System.out.println(sb.toString());
				
				String result = String.valueOf(privKey.encrypt(new BigInteger(textP_encryption.getText())));

				tableEncrypt.setSelection(numberOfElement - 1);
				tableEncrypt.showSelection();

				textC_encryption.setText(result);
				textC_encryption.setEnabled(true);
				textC_decryption.setText(result);
				textC_decryption.setEnabled(true);
				textP_encryption.setEditable(false);
				textP_encryption.setFocus();
				btnEncrypt.setEnabled(false);
				btnDecrypt.setEnabled(true);
				
				styledTextDescription.setText(Messages.MerkleHellmanView_0000 + Messages.MerkleHellmanView_000);
				styledTextDescription.setStyleRange(header);
				
			}
		});
		GridData gd_btnEncrypt = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnEncrypt.widthHint = 180;
		btnEncrypt.setLayoutData(gd_btnEncrypt);
		btnEncrypt.setText(Messages.MerkleHellmanView_13);

		Group grpDecryption = new Group(compositeMain, SWT.NONE);
		grpDecryption.setLayout(new GridLayout(4, false));
		grpDecryption.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpDecryption.setText(Messages.MerkleHellmanView_14);

		Label lblC_decryption = new Label(grpDecryption, SWT.NONE);
		lblC_decryption.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblC_decryption.setText("c ="); //$NON-NLS-1$

		textC_decryption = new Text(grpDecryption, SWT.BORDER | SWT.READ_ONLY);
		textC_decryption.setEnabled(false);
		textC_decryption.setEditable(true);
		textC_decryption.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblCC = new Label(grpDecryption, SWT.NONE);
		lblCC.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCC.setText("c' = c * U mod M ="); //$NON-NLS-1$

		textCC = new Text(grpDecryption, SWT.BORDER | SWT.READ_ONLY);
		textCC.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		tableDecrypt = new Table(grpDecryption, SWT.BORDER | SWT.FULL_SELECTION);
		tableDecrypt.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		tableDecrypt.setHeaderVisible(true);
		tableDecrypt.setLinesVisible(true);

		TableColumn tblclmnIteration_d = new TableColumn(tableDecrypt, SWT.NONE);
		tblclmnIteration_d.setWidth(60);
		tblclmnIteration_d.setText(Messages.MerkleHellmanView_15);

		TableColumn tblclmnEquation_d = new TableColumn(tableDecrypt, SWT.NONE);
		tblclmnEquation_d.setWidth(350);
		tblclmnEquation_d.setText(Messages.MerkleHellmanView_16);

		Label lblP_decryption = new Label(grpDecryption, SWT.NONE);
		lblP_decryption.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblP_decryption.setText("m ="); //$NON-NLS-1$

		textBinary_decrypted = new Text(grpDecryption, SWT.BORDER);
		textBinary_decrypted.setEditable(false);
		textBinary_decrypted.setEnabled(false);
		textBinary_decrypted.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));

		btnDecrypt = new Button(grpDecryption, SWT.NONE);
		btnDecrypt.setEnabled(false);
		btnDecrypt.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
//				tableDecrypt.clearAll();
				tableDecrypt.removeAll();
				
				BigInteger c = new BigInteger(textC_decryption.getText());
				BigInteger U = privKey.getU();
				BigInteger M = privKey.getM();

				BigInteger cc = c.multiply(U).mod(M);
				textCC.setText(String.valueOf(cc));

				int numberOfElement = Integer.parseInt(comboKeyElements.getText());

				BigInteger tmpCC = cc;
				StringBuilder binResult = new StringBuilder();
				for (int i = numberOfElement - 1; i >= 0; i--) {
					StringBuilder sb = new StringBuilder();
					BigInteger tmpA = privKey.getPrivateKeyElement(i);

					if (tmpCC.compareTo(tmpA) >= 0) {
						sb.append("c' = " + tmpCC + " >= " + tmpA + " = A(" + i + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						sb.append(" ==> p(" + i + ") = 1, "); //$NON-NLS-1$ //$NON-NLS-2$
						sb.append("c' = " + tmpCC + " - " + tmpA + " = " + (tmpCC = tmpCC.subtract(tmpA))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

						binResult.insert(0, "1"); //$NON-NLS-1$
					} else {
						sb.append("c' = " + tmpCC + "  < " + tmpA + " = A(" + i + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						sb.append(" ==> p(" + i + ") = 0, "); //$NON-NLS-1$ //$NON-NLS-2$
						sb.append("c' = " + tmpCC + " - 0" + " = " + (tmpCC = tmpCC.subtract(BigInteger.ZERO))); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

						binResult.insert(0, "0"); //$NON-NLS-1$
					}

					TableItem tmp = new TableItem(tableDecrypt, SWT.BORDER);
					tmp.setText(0, String.valueOf(i));
					tmp.setText(1, sb.toString());

				}

				tableDecrypt.setSelection(numberOfElement - 1);
				textBinary_decrypted.setText(binResult.toString());
				textBinary_decrypted.setEnabled(true);
//				btnDecrypt.setEnabled(false);

				if (textBinary.getText().compareTo(textBinary_decrypted.getText()) == 0) {
					textBinary.setBackground(new Color(null, new RGB(0, 255, 0)));
					textBinary_decrypted.setBackground(new Color(null, new RGB(0, 255, 0)));
					MessageDialog.openInformation(null, Messages.MerkleHellmanView_18, Messages.MerkleHellmanView_19 + textBinary.getText() + " = " + binResult); //$NON-NLS-1$
				} else {
					textBinary.setBackground(new Color(null, new RGB(255, 0, 0)));
					textBinary_decrypted.setBackground(new Color(null, new RGB(255, 0, 0)));
					MessageDialog.openError(null, Messages.MerkleHellmanView_18, Messages.MerkleHellmanView_30 + textBinary.getText() + " = " + binResult);  //$NON-NLS-1$
					
				}
			}
		});
		GridData gd_btnDecrypt = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
		gd_btnDecrypt.widthHint = 180;
		btnDecrypt.setLayoutData(gd_btnDecrypt);
		btnDecrypt.setText(Messages.MerkleHellmanView_17);
		scrolledComposite.setContent(compositeMain);
		scrolledComposite.setMinSize(new Point(1010, 566));

		int numberOfElements = Integer.parseInt(comboKeyElements.getText());
		int startValue = Integer.parseInt(comboStartValue.getText());

		generatePrivateKey(numberOfElements, startValue);
	}

	protected boolean mAndWValuesChanged() {
		BigInteger m = new BigInteger(textM.getText());
		BigInteger w = new BigInteger(textW.getText());
		BigInteger tmpM = new BigInteger(privKey.getM().toString());
		BigInteger tmpW = new BigInteger(privKey.getW().toString());

		if (m.compareTo(tmpM) != 0 || w.compareTo(tmpW) != 0)
			return true;
		else
			return false;
	}

	protected boolean privateKeyValuesChanged() {
		for (int i = 0; i < privateKeyFields.size(); i++) {
			BigInteger a = new BigInteger(privateKeyFields.get(i).getText());
			BigInteger b = privKey.getPrivateKeyElement(i);
			
			if (a.compareTo(b) != 0)
				return true;
		}
				
		return false;		
	}

	private void generatePrivateKey(int numberOfElements, int startValue) {
		BigInteger startVal = BigInteger.valueOf(startValue);
		privKey = new MerkleHellman(numberOfElements);
		privKey.createPrivateKeys(MerkleHellman.randomNumber(startVal.shiftRight(1), startVal));

		for (int i = 0; i < privateKeyFields.size(); i++) {
			privateKeyFields.get(i).setText(String.valueOf(privKey.getPrivateKeyElement(i)));
		}

		textM.setText(String.valueOf(privKey.getM()));
		textSumA.setText(String.valueOf(privKey.getSum()));
		textU.setText(String.valueOf(privKey.getU()));
		textW.setText(String.valueOf(privKey.getW()));

	}

	private void createPrivateKeyFields(int parseInt) {
		Control[] content = compositePrivateKey.getChildren();
		for (Control c : content) {
			c.dispose();
		}
		privateKeyFields.clear();

		for (int i = 0; i < parseInt; i++) {
			Label lblA = new Label(compositePrivateKey, SWT.NONE);
			lblA.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
			lblA.setText("A(" + (i + 1) + "):"); //$NON-NLS-1$ //$NON-NLS-2$
			Text text = new Text(compositePrivateKey, SWT.BORDER);
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			text.addVerifyListener(vlNumber);
			privateKeyFields.add(text);
		}

		compositePrivateKey.layout();
		scrolledCompositePrivateKey.setMinSize(compositePrivateKey.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	private void createPublicKeyFields(int parseInt) {
		Control[] content = compositePublicKey.getChildren();
		for (Control c : content) {
			c.dispose();
		}
		publicKeyFields.clear();

		for (int i = 0; i < parseInt; i++) {
			Label lblA = new Label(compositePublicKey, SWT.NONE);
			lblA.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
			lblA.setText("B(" + (i + 1) + "):"); //$NON-NLS-1$ //$NON-NLS-2$
			Text text = new Text(compositePublicKey, SWT.BORDER);
			text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

			publicKeyFields.add(text);
		}

		privKey.createPublicKeys();

		for (int i = 0; i < publicKeyFields.size(); i++) {
			publicKeyFields.get(i).setText(String.valueOf(privKey.getPublicKeyElement(i)));
		}

		compositePublicKey.layout();
		scrolledCompositePublicKey.setMinSize(compositePublicKey.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	@Override
	public void setFocus() {
		btnCreateKeys.setFocus();
	}

	public void resetView() {
		/*
		 * reset public key area
		 */
		Control[] content = compositePublicKey.getChildren();
		for (Control c : content) {
			c.dispose();
		}
		styledTextDescription.setText(Messages.MerkleHellmanView_0000 + Messages.MerkleHellmanView_0);
		styledTextDescription.setStyleRange(header);
		compositePublicKey.layout();
		scrolledCompositePublicKey.setMinSize(compositePublicKey.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		privateKeyFields.clear();
		publicKeyFields.clear();

		btnGenerateNewKey.setEnabled(true);
		btnCreateKeys.setEnabled(true);
		textP_encryption.setEnabled(false);
		textCC.setEnabled(false);

		textM.setEditable(true);
		textW.setEditable(true);

		textM.setText(""); //$NON-NLS-1$
		textW.setText(""); //$NON-NLS-1$
		textSumA.setText(""); //$NON-NLS-1$
		textU.setText(""); //$NON-NLS-1$
		textP_encryption.setText(""); //$NON-NLS-1$
		textMax.setText(""); //$NON-NLS-1$
		textBinary.setText(""); //$NON-NLS-1$
		textC_decryption.setText(""); //$NON-NLS-1$
		textC_decryption.setEnabled(false);
		textC_encryption.setText(""); //$NON-NLS-1$
		textCC.setText(""); //$NON-NLS-1$
		textBinary_decrypted.setText(""); //$NON-NLS-1$
		textBinary_decrypted.setEnabled(false);

		btnEncrypt.setEnabled(false);
		btnDecrypt.setEnabled(false);

		tableEncrypt.removeAll();
		tableDecrypt.removeAll();

		textBinary.setBackground(new Color(null, new RGB(240, 240, 240)));
		textBinary_decrypted.setBackground(new Color(null, new RGB(240, 240, 240)));
	}

	public void init() {
		comboKeyElements.select(0);
		comboStartValue.select(0);
		lblPublicKeyB.setText(Messages.MerkleHellmanView_6 + comboKeyElements.getText());
		createPrivateKey();

	}

	private void createPrivateKey() {
		int numberOfElements = Integer.parseInt(comboKeyElements.getText());
		int startValue = Integer.parseInt(comboStartValue.getText());

		createPrivateKeyFields(numberOfElements);
		generatePrivateKey(numberOfElements, startValue);

		textMax.setText(new BigInteger("2").pow(Integer.parseInt(comboKeyElements.getText())).toString()); //$NON-NLS-1$
	}
}