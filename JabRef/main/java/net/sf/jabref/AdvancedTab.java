/*  Copyright (C) 2003-2011 JabRef contributors.
    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*/
package net.sf.jabref;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.common.base.Optional;

import net.sf.jabref.help.HelpAction;
import net.sf.jabref.help.HelpDialog;
import net.sf.jabref.journals.logic.JournalAbbreviationRepository;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.layout.FormLayout;

import net.sf.jabref.remote.RemotePreferences;
import net.sf.jabref.remote.RemoteUtil;
import net.sf.jabref.remote.JabRefMessageHandler;

public class AdvancedTab extends JPanel implements PrefsTab {

    private final JabRefPreferences preferences;
    HelpDialog helpDiag;
    JPanel pan = new JPanel();
    JLabel lab;
    private final JCheckBox useDefault;
    private final JCheckBox useRemoteServer;
    private final JCheckBox useNativeFileDialogOnMac;
    private final JCheckBox filechooserDisableRename;
    private final JCheckBox useIEEEAbrv;
    private final JCheckBox biblatexMode;
    private final JComboBox className;
    private final JTextField remoteServerPort;
    JPanel p1 = new JPanel();
    private String oldLnf = "";
    private boolean oldUseDef;
    private boolean oldBiblMode = false;
    private int oldPort = -1;

    public final static String PREF_IMPORT_CONVERT_TO_EQUATION = "importFileConvertToEquation";
    public final static String PREF_IMPORT_FILENAMEPATTERN = "importFileNamePattern";

    private final JCheckBox useConvertToEquation;
    private final JCheckBox useCaseKeeperOnSearch;
    private final JCheckBox useUnitFormatterOnSearch;
    private final JabRef jabRef;
    private RemotePreferences remotePreferences;


    public AdvancedTab(JabRefPreferences prefs, HelpDialog diag, JabRef jabRef) {
        this.jabRef = jabRef;
        preferences = prefs;
        this.remotePreferences = new RemotePreferences(preferences);

        HelpAction remoteHelp = new HelpAction(diag, GUIGlobals.remoteHelp, "Help",
                GUIGlobals.getIconUrl("helpSmall"));
        useDefault = new JCheckBox(Globals.lang("Use other look and feel"));
        useRemoteServer = new JCheckBox(Globals.lang("Listen for remote operation on port") + ':');
        useNativeFileDialogOnMac = new JCheckBox(Globals.lang("Use native file dialog"));
        filechooserDisableRename = new JCheckBox(Globals.lang("Disable file renaming in non-native file dialog"));
        useIEEEAbrv = new JCheckBox(Globals.lang("Use IEEE LaTeX abbreviations"));
        biblatexMode = new JCheckBox(Globals.lang("BibLaTeX mode"));
        remoteServerPort = new JTextField();
        String[] possibleLookAndFeels = {
                UIManager.getSystemLookAndFeelClassName(),
                UIManager.getCrossPlatformLookAndFeelClassName(),
                "com.jgoodies.plaf.plastic.Plastic3DLookAndFeel",
                "com.sun.java.swing.plaf.motif.MotifLookAndFeel",
                "javax.swing.plaf.mac.MacLookAndFeel"
        };
        // Only list L&F which are available
        List<String> lookAndFeels = new ArrayList<String>();
        for (String lf : possibleLookAndFeels) {
            try {
                // Try to find L&F, throws exception if not successful
                Class.forName(lf);
                lookAndFeels.add(lf);
            } catch (ClassNotFoundException ignored) {
            }
        }
        className = new JComboBox(lookAndFeels.toArray(new String[lookAndFeels.size()]));
        className.setEditable(true);
        final JComboBox clName = className;
        useDefault.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                clName.setEnabled(((JCheckBox) e.getSource()).isSelected());
            }
        });
        useConvertToEquation = new JCheckBox(Globals.lang("Prefer converting subscripts and superscripts to equations rather than text"));
        useCaseKeeperOnSearch = new JCheckBox(Globals.lang("Add {} to specified title words on search to keep the correct case"));
        useUnitFormatterOnSearch = new JCheckBox(Globals.lang("Format units by adding non-breaking separators and keeping the correct case on search"));

        FormLayout layout = new FormLayout
                ("1dlu, 8dlu, left:pref, 4dlu, fill:3dlu",//, 4dlu, fill:pref",// 4dlu, left:pref, 4dlu",
                "");
        DefaultFormBuilder builder = new DefaultFormBuilder(layout);
        JPanel pan = new JPanel();

        if (!Globals.ON_MAC) {
            builder.appendSeparator(Globals.lang("Look and feel"));
            JLabel lab = new JLabel(Globals.lang("Default look and feel") + ": " + UIManager.getSystemLookAndFeelClassName());
            builder.nextLine();
            builder.append(pan);
            builder.append(lab);
            builder.nextLine();
            builder.append(pan);
            builder.append(useDefault);
            builder.nextLine();
            builder.append(pan);
            JPanel pan2 = new JPanel();
            lab = new JLabel(Globals.lang("Class name") + ':');
            pan2.add(lab);
            pan2.add(className);
            builder.append(pan2);
            builder.nextLine();
            builder.append(pan);
            lab = new JLabel(Globals.lang("Note that you must specify the fully qualified class name for the look and feel,"));
            builder.append(lab);
            builder.nextLine();
            builder.append(pan);
            lab = new JLabel(Globals.lang("and the class must be available in your classpath next time you start JabRef."));
            builder.append(lab);
            builder.nextLine();
        }
        builder.appendSeparator(Globals.lang("Remote operation"));
        builder.nextLine();
        builder.append(new JPanel());
        builder.append(new JLabel("<html>" + Globals.lang("This feature lets new files be opened or imported into an "
                + "already running instance of JabRef<BR>instead of opening a new instance. For instance, this "
                + "is useful when you open a file in JabRef<br>from your web browser."
                + "<BR>Note that this will prevent you from running more than one instance of JabRef at a time.") + "</html>"));
        builder.nextLine();
        builder.append(new JPanel());

        JPanel p = new JPanel();
        p.add(useRemoteServer);
        p.add(remoteServerPort);
        p.add(remoteHelp.getIconButton());
        builder.append(p);

        //if (Globals.ON_MAC) {
        builder.nextLine();
        builder.appendSeparator(Globals.lang("File dialog"));
        builder.nextLine();
        builder.append(new JPanel());
        builder.append(useNativeFileDialogOnMac);
        builder.nextLine();
        builder.append(new JPanel());
        builder.append(filechooserDisableRename);
        //}
        // IEEE
        builder.nextLine();
        builder.appendSeparator(Globals.lang("Search IEEEXplore"));
        builder.nextLine();
        builder.append(new JPanel());
        builder.append(useIEEEAbrv);

        builder.nextLine();
        builder.appendSeparator(Globals.lang("BibLaTeX mode"));
        builder.append(new JPanel());
        builder.append(biblatexMode);

        builder.nextLine();
        builder.appendSeparator(Globals.lang("Import conversions"));
        builder.nextLine();
        builder.append(new JPanel());
        builder.append(useConvertToEquation);
        builder.nextLine();
        builder.append(pan);
        builder.append(useCaseKeeperOnSearch);
        builder.nextLine();
        builder.append(pan);
        builder.append(useUnitFormatterOnSearch);

        pan = builder.getPanel();
        pan.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new BorderLayout());
        add(pan, BorderLayout.CENTER);

    }

    @Override
    public void setValues() {
        oldUseDef = preferences.isUseDefaultLookAndFeel();
        oldLnf = JabRefPreferences.getInstance().getLookAndFeel();
        useDefault.setSelected(!oldUseDef);
        className.setSelectedItem(oldLnf);
        className.setEnabled(!oldUseDef);
        useRemoteServer.setSelected(remotePreferences.useRemoteServer());
        oldPort = remotePreferences.getPort();
        remoteServerPort.setText(String.valueOf(oldPort));
        useNativeFileDialogOnMac.setSelected(Globals.prefs.isUseNativeFileDialogOnMac());
        filechooserDisableRename.setSelected(Globals.prefs.isFilechooserDisableRename());
        useIEEEAbrv.setSelected(Globals.prefs.isUseIEEEAbrv());
        oldBiblMode = Globals.prefs.isBiblatexMode();
        biblatexMode.setSelected(oldBiblMode);
        useConvertToEquation.setSelected(Globals.prefs.isUseConvertToEquation());
        useCaseKeeperOnSearch.setSelected(Globals.prefs.isUseCaseKeeperOnSearch());
        useUnitFormatterOnSearch.setSelected(JabRefPreferences.getInstance().isUseUnitFormatterOnSearch());
    }

    @Override
    public void storeSettings() {
        preferences.setUseDefaultLookAndFeel(!useDefault.isSelected());
        JabRefPreferences.getInstance().setLookAndFeel(className.getSelectedItem().toString());
        preferences.setUseNativeFileDialogOnMac(useNativeFileDialogOnMac.isSelected());
        preferences.setFilechooserDisableRename(filechooserDisableRename.isSelected());
        UIManager.put("FileChooser.readOnly", filechooserDisableRename.isSelected());
        preferences.setUseIEEEAbrv(useIEEEAbrv.isSelected());
        if (useIEEEAbrv.isSelected()) {
            Globals.journalAbbrev = new JournalAbbreviationRepository();
            Globals.journalAbbrev.readJournalListFromResource(Globals.JOURNALS_IEEE_INTERNAL_LIST);
        }
        storeRemoteSettings();

        preferences.setBiblatexMode(biblatexMode.isSelected());

        if ((useDefault.isSelected() == oldUseDef) ||
                !oldLnf.equals(className.getSelectedItem().toString())) {
            JOptionPane.showMessageDialog(null,
                    Globals.lang("You have changed the look and feel setting.")
                            .concat(" ")
                            .concat(Globals.lang("You must restart JabRef for this to come into effect.")),
                    Globals.lang("Changed look and feel settings"),
                    JOptionPane.WARNING_MESSAGE);
        }

        if (biblatexMode.isSelected() != oldBiblMode) {
            JOptionPane.showMessageDialog(null,
                    Globals.lang("You have toggled the BibLaTeX mode.")
                            .concat(" ")
                            .concat("You must restart JabRef for this change to come into effect."),
                    Globals.lang("BibLaTeX mode"), JOptionPane.WARNING_MESSAGE);
        }

        preferences.setUseConvertToEquation(useConvertToEquation.isSelected());
        preferences.setUseCaseKeeperOnSearch(useCaseKeeperOnSearch.isSelected());
        JabRefPreferences.getInstance().setUseUnitFormatterOnSearch(useUnitFormatterOnSearch.isSelected());
    }

    public void storeRemoteSettings() {
        Optional<Integer> newPort = getPortAsInt();
        if(newPort.isPresent()) {
            if (remotePreferences.isDifferentPort(newPort.get())) {
                remotePreferences.setPort(newPort.get());

                if(remotePreferences.useRemoteServer()) {
                    JOptionPane.showMessageDialog(null,
                            Globals.lang("Remote server port")
                                    .concat(" ")
                                    .concat("You must restart JabRef for this change to come into effect."),
                            Globals.lang("Remote server port"), JOptionPane.WARNING_MESSAGE);
                }
            }
        }

        remotePreferences.setUseRemoteServer(useRemoteServer.isSelected());
        if (remotePreferences.useRemoteServer()) {
            Globals.remoteListener.openAndStart(new JabRefMessageHandler(jabRef), remotePreferences.getPort());
        } else {
            Globals.remoteListener.stop();
        }
    }

    public Optional<Integer> getPortAsInt() {
        try {
            return Optional.of(Integer.parseInt(remoteServerPort.getText()));
        } catch (NumberFormatException ex) {
            return Optional.absent();
        }
    }

    @Override
    public boolean readyToClose() {

        try {
            int portNumber = Integer.parseInt(remoteServerPort.getText());
            if (RemoteUtil.isValidPartNumber(portNumber)) {
                return true; // Ok, the number was legal.
            } else {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog
                    (null, Globals.lang("You must enter an integer value in the interval 1025-65535 in the text field for") + " '" +
                            Globals.lang("Remote server port") + '\'', Globals.lang("Remote server port"),
                            JOptionPane.ERROR_MESSAGE);
            return false;
        }

    }

    @Override
    public String getTabName() {
        return Globals.lang("Advanced");
    }

}
