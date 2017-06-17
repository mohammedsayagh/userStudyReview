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

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;

import net.sf.jabref.util.StringUtil;
import net.sf.jabref.util.Util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is used to represent customized entry types.
 *
 */
public class CustomEntryType extends BibtexEntryType {

    private final String name;
    private String[] req;
    private final String[] opt;
    private String[] priOpt;
    private String[][] reqSets = null; // Sets of either-or required fields, if any
    
    private static final Log LOGGER = LogFactory.getLog(CustomEntryType.class);


    public CustomEntryType(String name_, String[] req_, String[] priOpt_, String[] secOpt_) {
        name = StringUtil.nCase(name_);
        parseRequiredFields(req_);
        priOpt = priOpt_;
        opt = Util.arrayConcat(priOpt_, secOpt_);
    }

    public CustomEntryType(String name_, String[] req_, String[] opt_) {
        this(name_, req_, opt_, new String[0]);
    }

    private CustomEntryType(String name_, String reqStr, String optStr) {
        name = StringUtil.nCase(name_);
        if (reqStr.isEmpty()) {
            req = new String[0];
        } else {
            parseRequiredFields(reqStr);

        }
        if (optStr.isEmpty()) {
            opt = new String[0];
        } else {
            opt = optStr.split(";");
        }
    }

    private void parseRequiredFields(String reqStr) {
        String[] parts = reqStr.split(";");
        parseRequiredFields(parts);
    }

    private void parseRequiredFields(String[] parts) {
        ArrayList<String> fields = new ArrayList<String>();
        ArrayList<String[]> sets = new ArrayList<String[]>();
        for (String part : parts) {
            String[] subParts = part.split("/");
            Collections.addAll(fields, subParts);
            // Check if we have either/or fields:
            if (subParts.length > 1) {
                sets.add(subParts);
            }
        }
        req = fields.toArray(new String[fields.size()]);
        if (!sets.isEmpty()) {
            reqSets = sets.toArray(new String[sets.size()][]);
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String[] getOptionalFields() {
        return opt;
    }

    @Override
    public String[] getRequiredFields() {
        return req;
    }

    @Override
    public String[] getPrimaryOptionalFields() {
        return priOpt;
    }
    
    @Override
    public String[] getSecondaryOptionalFields() {
    	return Util.getRemainder(opt, priOpt);
    }

    @Override
    public String[] getRequiredFieldsForCustomization() {
        return getRequiredFieldsString().split(";");
    }

    //    public boolean isTemporary

    @Override
    public String describeRequiredFields() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < req.length; i++) {
            sb.append(req[i]);
            sb.append(((i <= (req.length - 1)) && (req.length > 1)) ? ", " : "");
        }
        return sb.toString();
    }

    public String describeOptionalFields() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < opt.length; i++) {
            sb.append(opt[i]);
            sb.append(((i <= (opt.length - 1)) && (opt.length > 1)) ? ", " : "");
        }
        return sb.toString();
    }

    /**
     * Check whether this entry's required fields are set, taking crossreferenced entries and
     * either-or fields into account:
     * @param entry The entry to check.
     * @param database The entry's database.
     * @return True if required fields are set, false otherwise.
     */
    @Override
    public boolean hasAllRequiredFields(BibtexEntry entry, BibtexDatabase database) {
        // First check if the bibtex key is set:
        if (entry.getField(BibtexFields.KEY_FIELD) == null) {
            return false;
        }
        // Then check other fields:
        boolean[] isSet = new boolean[req.length];
        // First check for all fields, whether they are set here or in a crossref'd entry:
        for (int i = 0; i < req.length; i++) {
            isSet[i] = BibtexDatabase.getResolvedField(req[i], entry, database) != null;
        }
        // Then go through all fields. If a field is not set, see if it is part of an either-or
        // set where another field is set. If not, return false:
        for (int i = 0; i < req.length; i++) {
            if (!isSet[i]) {
                if (!isCoupledFieldSet(req[i], entry, database)) {
                    return false;
                }
            }
        }
        // Passed all fields, so return true:
        return true;
    }

    private boolean isCoupledFieldSet(String field, BibtexEntry entry, BibtexDatabase database) {
        if (reqSets == null) {
            return false;
        }
        for (String[] reqSet : reqSets) {
            boolean takesPart = false, oneSet = false;
            for (String aReqSet : reqSet) {
                // If this is the field we're looking for, note that the field is part of the set:
                if (aReqSet.equalsIgnoreCase(field)) {
                    takesPart = true;
                } else if (BibtexDatabase.getResolvedField(aReqSet, entry, database) != null) {
                    oneSet = true;
                }
            }
            // Ths the field is part of the set, and at least one other field is set, return true:
            if (takesPart && oneSet) {
                return true;
            }
        }
        // No hits, so return false:
        return false;
    }

    /**
     * Get a String describing the required field set for this entry type.
     * @return Description of required field set for storage in preferences or bib file.
     */
    public String getRequiredFieldsString() {
        StringBuilder sb = new StringBuilder();
        int reqSetsPiv = 0;
        for (int i = 0; i < req.length; i++) {
            if ((reqSets == null) || (reqSetsPiv == reqSets.length)) {
                sb.append(req[i]);
            }
            else if (req[i].equals(reqSets[reqSetsPiv][0])) {
                for (int j = 0; j < reqSets[reqSetsPiv].length; j++) {
                    sb.append(reqSets[reqSetsPiv][j]);
                    if (j < (reqSets[reqSetsPiv].length - 1)) {
                        sb.append('/');
                    }
                }
                // Skip next n-1 fields:
                i += reqSets[reqSetsPiv].length - 1;
                reqSetsPiv++;
            } else {
                sb.append(req[i]);
            }
            if (i < (req.length - 1)) {
                sb.append(';');
            }

        }
        return sb.toString();
    }

    public void save(Writer out) throws IOException {
        out.write("@comment{");
        out.write(GUIGlobals.ENTRYTYPE_FLAG);
        out.write(getName());
        out.write(": req[");
        out.write(getRequiredFieldsString());
        /*StringBuffer sb = new StringBuffer();
        for (int i=0; i<req.length; i++) {
            sb.append(req[i]);
            if (i<req.length-1)
        	sb.append(";");
        }
        out.write(sb.toString());*/
        out.write("] opt[");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < opt.length; i++) {
            sb.append(opt[i]);
            if (i < (opt.length - 1)) {
                sb.append(';');
            }
        }
        out.write(sb.toString());
        out.write("]}" + Globals.NEWLINE);
    }

    public static CustomEntryType parseEntryType(String comment) {
        try {
            //if ((comment.length() < 9+GUIGlobals.ENTRYTYPE_FLAG.length())
            //	|| comment
            //System.out.println(">"+comment+"<");
            String rest;
            rest = comment.substring(GUIGlobals.ENTRYTYPE_FLAG.length());
            int nPos = rest.indexOf(':');
            String name = rest.substring(0, nPos);
            rest = rest.substring(nPos + 2);

            int rPos = rest.indexOf(']');
            if (rPos < 4) {
                throw new IndexOutOfBoundsException();
            }
            String reqFields = rest.substring(4, rPos);
            //System.out.println(name+"\nr '"+reqFields+"'");
            int oPos = rest.indexOf(']', rPos + 1);
            String optFields = rest.substring(rPos + 6, oPos);
            //System.out.println("o '"+optFields+"'");
            return new CustomEntryType(name, reqFields, optFields);
        } catch (IndexOutOfBoundsException ex) {
            LOGGER.info("Ill-formed entrytype comment in BibTeX file.", ex);
            return null;
        }

    }
}
