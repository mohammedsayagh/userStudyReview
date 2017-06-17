package spl.gui;

import com.jgoodies.forms.builder.ButtonBarBuilder;
import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import net.sf.jabref.Globals;
import net.sf.jabref.ImportSettingsTab;
import spl.listener.LabelLinkListener;
import spl.localization.LocalizationSupport;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class ImportDialog extends JDialog {

    public final static int NOMETA = 0;
    public final static int XMP = 1;
    public final static int CONTENT = 2;
    public final static int MRDLIB = 3;
    public final static int ONLYATTACH = 4;
    public final static int UPDATEEMPTYFIELDS = 5;

    private final JPanel contentPane;
    private final JCheckBox checkBoxDoNotShowAgain;
    private final JCheckBox useDefaultPDFImportStyle;
    private final JRadioButton radioButtonXmp;
    private final JRadioButton radioButtonPDFcontent;
    private final JRadioButton radioButtonMrDlib;
    private final JRadioButton radioButtonNoMeta;
    private final JRadioButton radioButtononlyAttachPDF;
    private final JRadioButton radioButtonUpdateEmptyFields;
    private int result;


    public ImportDialog(boolean targetIsARow, String fileName) {
        Boolean targetIsARow1 = targetIsARow;
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);
        JPanel panel3 = new JPanel();
        panel3.setBackground(new Color(-1643275));
        JLabel labelHeadline = new JLabel(Globals.lang("Import_Metadata_from:"));
        labelHeadline.setFont(new Font(labelHeadline.getFont().getName(), Font.BOLD, 14));
        JLabel labelSubHeadline = new JLabel(Globals.lang("Choose_the_source_for_the_metadata_import"));
        labelSubHeadline.setFont(new Font(labelSubHeadline.getFont().getName(), labelSubHeadline.getFont().getStyle(), 13));
        JLabel labelFileName = new JLabel();
        labelFileName.setFont(new Font(labelHeadline.getFont().getName(), Font.BOLD, 14));
        JPanel headLinePanel = new JPanel();
        headLinePanel.add(labelHeadline);
        headLinePanel.add(labelFileName);
        headLinePanel.setBackground(new Color(-1643275));
        GridLayout gl = new GridLayout(2, 1);
        gl.setVgap(10);
        gl.setHgap(10);
        panel3.setLayout(gl);
        panel3.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel3.add(headLinePanel);
        panel3.add(labelSubHeadline);
        radioButtonNoMeta = new JRadioButton(Globals.lang("Create_blank_entry_linking_the_PDF"));
        radioButtonXmp = new JRadioButton(Globals.lang("Create_entry_based_on_XMP_data"));
        radioButtonPDFcontent = new JRadioButton(Globals.lang("Create_entry_based_on_content"));
        radioButtonMrDlib = new JRadioButton(Globals.lang("Create_entry_based_on_data_fetched_from"));
        radioButtononlyAttachPDF = new JRadioButton(Globals.lang("Only_attach_PDF"));
        radioButtonUpdateEmptyFields = new JRadioButton(Globals.lang("Update_empty_fields_with_data_fetched_from"));
        JLabel labelMrDlib1 = new JLabel("Mr._dLib");
        labelMrDlib1.setFont(new Font(labelMrDlib1.getFont().getName(), Font.BOLD, 13));
        labelMrDlib1.setForeground(new Color(-16776961));
        JLabel labelMrDlib2 = new JLabel("Mr._dLib");
        labelMrDlib2.setFont(new Font(labelMrDlib1.getFont().getName(), Font.BOLD, 13));
        labelMrDlib2.setForeground(new Color(-16776961));
        JButton buttonOK = new JButton(Globals.lang("Ok"));
        JButton buttonCancel = new JButton(Globals.lang("Cancel"));
        checkBoxDoNotShowAgain = new JCheckBox(Globals.lang("Do not show this box again for this import"));
        useDefaultPDFImportStyle = new JCheckBox(Globals.lang("Always use this PDF import style (and do not ask for each import)"));
        DefaultFormBuilder b = new DefaultFormBuilder(new FormLayout("left:pref, 5dlu, left:pref:grow", ""));
        b.appendSeparator(Globals.lang("Create New Entry"));
        b.append(radioButtonNoMeta, 3);
        b.append(radioButtonXmp, 3);
        b.append(radioButtonPDFcontent, 3);
        b.append(radioButtonMrDlib);
        b.append(labelMrDlib1);
        b.appendSeparator(Globals.lang("Update_Existing_Entry"));
        b.append(radioButtononlyAttachPDF, 3);
        b.append(radioButtonUpdateEmptyFields);
        b.append(labelMrDlib2);
        b.nextLine();
        b.append(checkBoxDoNotShowAgain);
        b.append(useDefaultPDFImportStyle);
        b.getPanel().setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        ButtonBarBuilder bb = new ButtonBarBuilder();
        bb.addGlue();
        bb.addButton(buttonOK);
        bb.addButton(buttonCancel);
        bb.addGlue();
        bb.getPanel().setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        contentPane.add(panel3, BorderLayout.NORTH);
        contentPane.add(b.getPanel(), BorderLayout.CENTER);
        contentPane.add(bb.getPanel(), BorderLayout.SOUTH);

        if (!targetIsARow1) {
            this.radioButtononlyAttachPDF.setEnabled(false);
            this.radioButtonUpdateEmptyFields.setEnabled(false);
            labelMrDlib2.setEnabled(false);
        }
        String name = new File(fileName).getName();
        if (name.length() < 34) {
            labelFileName.setText(name);
        } else {
            labelFileName.setText(new File(fileName).getName().substring(0, 33) + "...");
        }
        labelMrDlib1.addMouseListener(new LabelLinkListener(labelMrDlib1, "www.mr-dlib.org/docs/pdf_metadata_extraction.php"));
        labelMrDlib2.addMouseListener(new LabelLinkListener(labelMrDlib2, "www.mr-dlib.org/docs/pdf_metadata_extraction.php"));
        this.setTitle(LocalizationSupport.message("Import_Metadata_From_PDF"));

        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        // only one of the radio buttons may be selected.
        ButtonGroup bg = new ButtonGroup();
        bg.add(radioButtonNoMeta);
        bg.add(radioButtonXmp);
        bg.add(radioButtonPDFcontent);
        bg.add(radioButtonMrDlib);
        bg.add(radioButtononlyAttachPDF);
        bg.add(radioButtonUpdateEmptyFields);

        buttonOK.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        switch (Globals.prefs.getInt(ImportSettingsTab.PREF_IMPORT_DEFAULT_PDF_IMPORT_STYLE)) {
        case NOMETA:
            radioButtonNoMeta.setSelected(true);
            break;
        case XMP:
            radioButtonXmp.setSelected(true);
            break;
        case CONTENT:
            radioButtonPDFcontent.setSelected(true);
            break;
        case MRDLIB:
            radioButtonMrDlib.setSelected(true);
            break;
        case ONLYATTACH:
            radioButtononlyAttachPDF.setSelected(true);
            break;
        case UPDATEEMPTYFIELDS:
            radioButtonUpdateEmptyFields.setSelected(true);
            break;
        default:
            // fallback
            radioButtonPDFcontent.setSelected(true);
            break;
        }

        this.setSize(555, 371);
    }

    private void onOK() {
        this.result = JOptionPane.OK_OPTION;
        Globals.prefs.putInt(ImportSettingsTab.PREF_IMPORT_DEFAULT_PDF_IMPORT_STYLE, this.getChoice());
        if (useDefaultPDFImportStyle.isSelected()) {
            Globals.prefs.putBoolean(ImportSettingsTab.PREF_IMPORT_ALWAYSUSE, true);
        }
        // checkBoxDoNotShowAgain handled by local variable
        dispose();
    }

    private void onCancel() {
        this.result = JOptionPane.CANCEL_OPTION;
        dispose();
    }

    public void showDialog() {
        this.pack();
        this.setVisible(true);
    }

    public int getChoice() {
        if (radioButtonXmp.isSelected()) {
            return ImportDialog.XMP;
        } else if (radioButtonPDFcontent.isSelected()) {
            return ImportDialog.CONTENT;
        } else if (radioButtonMrDlib.isSelected()) {
            return ImportDialog.MRDLIB;
        } else if (radioButtonNoMeta.isSelected()) {
            return ImportDialog.NOMETA;
        } else if (radioButtononlyAttachPDF.isSelected()) {
            return ImportDialog.ONLYATTACH;
        } else if (radioButtonUpdateEmptyFields.isSelected()) {
            return ImportDialog.UPDATEEMPTYFIELDS;
        } else {
            throw new IllegalStateException();
        }
    }

    public boolean getDoNotShowAgain() {
        return this.checkBoxDoNotShowAgain.isSelected();
    }

    public int getResult() {
        return result;
    }

    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }

    public void disableXMPChoice() {
        this.radioButtonXmp.setEnabled(false);
    }
}
