/*  Copyright (C) 2012, 2015 JabRef contributors.
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package net.sf.jabref.imports.fetcher;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import javax.swing.JPanel;

import net.sf.jabref.BibtexEntry;
import net.sf.jabref.Globals;
import net.sf.jabref.JabRefPreferences;
import net.sf.jabref.OutputPrinter;
import net.sf.jabref.imports.BibtexParser;
import net.sf.jabref.imports.CaseKeeper;
import net.sf.jabref.imports.EntryFetcher;
import net.sf.jabref.imports.ImportInspector;
import net.sf.jabref.imports.UnitFormatter;

/**
 * This class uses ebook.de's ISBN to BibTeX Converter to convert an ISBN to a BibTeX entry <br />
 * There is no separate web-based converter available, just that API
 */
public class ISBNtoBibTeXFetcher implements EntryFetcher {

    private static final String URL_PATTERN = "http://www.ebook.de/de/tools/isbn2bibtex?isbn=%s";
    private final CaseKeeper caseKeeper = new CaseKeeper();
    private final UnitFormatter unitFormatter = new UnitFormatter();


    @Override
    public void stopFetching() {
        // nothing needed as the fetching is a single HTTP GET
    }

    @Override
    public boolean processQuery(String query, ImportInspector inspector, OutputPrinter status) {
        String q;
        try {
            q = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // this should never happen
            status.setStatus(Globals.lang("Error"));
            e.printStackTrace();
            return false;
        }

        String urlString = String.format(ISBNtoBibTeXFetcher.URL_PATTERN, q);

        // Send the request
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }

        InputStream source;
        try {
            source = url.openStream();
        } catch (FileNotFoundException e) {
            // invalid ISBN --> 404--> FileNotFoundException
            status.showMessage(Globals.lang("Invalid ISBN"));
            return false;
        } catch (java.net.UnknownHostException e) {
            // It is very unlikely that ebook.de is an unknown host
            // It is more likely that we don't have an internet connection
            status.showMessage(Globals.lang("No_Internet_Connection."));
            return false;
        } catch (Exception e) {
            status.showMessage(e.toString());
            return false;
        }

        String bibtexString = new Scanner(source).useDelimiter("\\A").next();

        BibtexEntry entry = BibtexParser.singleFromString(bibtexString);
        if (entry != null) {
            // Optionally add curly brackets around key words to keep the case
            String title = entry.getField("title");
            if (title != null) {
                // Unit formatting
                if (JabRefPreferences.getInstance().isUseUnitFormatterOnSearch()) {
                    title = unitFormatter.format(title);
                }

                // Case keeping
                if (Globals.prefs.isUseCaseKeeperOnSearch()) {
                    title = caseKeeper.format(title);
                }
                entry.setField("title", title);
            }

            inspector.addEntry(entry);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getTitle() {
        return "ISBN to BibTeX";
    }

    @Override
    public String getKeyName() {
        return "ISBNtoBibTeX";
    }

    @Override
    public String getHelpPage() {
        return "ISBNtoBibTeXHelp.html";
    }

    @Override
    public JPanel getOptionsPanel() {
        // no additional options available
        return null;
    }

}
