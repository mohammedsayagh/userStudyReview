/*  Copyright (C) 2003-2015 JabRef contributors.
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

import java.awt.Color;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import Configuration.Annotations.Config;
import net.sf.jabref.export.CustomExportList;
import net.sf.jabref.export.ExportComparator;
import net.sf.jabref.external.DroppedFileHandler;
import net.sf.jabref.external.ExternalFileType;
import net.sf.jabref.external.UnknownExternalFileType;
import net.sf.jabref.gui.CleanUpAction;
import net.sf.jabref.gui.PersistenceTableColumnListener;
import net.sf.jabref.imports.CustomImportList;
import net.sf.jabref.labelPattern.LabelPattern;
import net.sf.jabref.remote.RemotePreferences;
import net.sf.jabref.specialfields.SpecialFieldsUtils;
import net.sf.jabref.util.StringUtil;
import net.sf.jabref.util.Util;

public class JabRefPreferences {

	private static final Log LOGGER = LogFactory.getLog(JabRefPreferences.class);

	/**
	 * HashMap that contains all preferences which are set by default
	 */
	public final HashMap<String, Object> defaults = new HashMap<String, Object>();

	/* contents of the defaults HashMap that are defined in this class. 
	 * There are more default parameters in this map which belong to separate preference classes.
	 */
	public static final String EMACS_PATH = "emacsPath";
	public static final String EMACS_ADDITIONAL_PARAMETERS = "emacsParameters";
	public static final String EMACS_23 = "emacsUseV23InsertString";

	@Config(name = "fontFamily", 
			defaultValue = "SansSerif")
	private static String fontFamily;


	@Config (name = "lookAndFeel", 
			defaultValue = "com.apple.laf.AquaLookAndFeel")
	private static String lookAndFeel;

	public static final String LATEX_EDITOR_PATH = "latexEditorPath";
	public static final String WIN_EDT_PATH = "winEdtPath";
	public static final String SHOW_SHORT = "showShort";
	public static final String LANGUAGE = "language";
	@Config(name = "namesLastOnly", 
			defaultValue = "true")
	private static boolean namesLastOnly;



	@Config (name = "abbrAuthorNames",
			defaultValue = "true")
	private static boolean abbrAuthorNames;

	@Config (name = "namesNatbib", 
			defaultValue = "true")
	private static boolean namesNatbib;

	public static final String NAMES_LAST_FIRST = "namesLf";

	@Config (name = "namesFf", 
			defaultValue = "false")
	private static boolean namesFf; 


	@Config (name = "namesAsIs",
			defaultValue = "false")
	private static boolean namesAsIs;

	@Config (name = "tableColorCodesOn", 
			defaultValue = "true")
	private static boolean tableColorCodesOn;

	public static final String ENTRY_EDITOR_HEIGHT = "entryEditorHeight";
	public static final String PREVIEW_PANEL_HEIGHT = "previewPanelHeight";

	@Config (name = "autoResizeMode", 
			defaultValue = "4")
	private static int autoResizeMode;


	@Config(name ="windowMaximised", 
			defaultValue = "false")
	private static boolean windowMaximised;


	@Config (name = "sizeX", 
			defaultValue = "840")
	private int size_x;

	@Config(name ="sizeY",
			defaultValue = "680")
	private int size_y;


	@Config (name = "posY", 
			defaultValue = "23")
	private static int posY;

	@Config (name = "posX", 
			defaultValue = "0")
	private static int posX;

	public static final String VIM_SERVER = "vimServer";
	public static final String VIM = "vim";
	public static final String LYXPIPE = "lyxpipe";

	@Config (name = "useDefaultLookAndFeel", 
			defaultValue = "true")
	private static boolean useDefaultLookAndFeel;

	@Config(name = "proxyPort", 
			defaultValue = "my proxy port")
	private static String proxyPort;

	@Config (name = "proxyHostname", 
			defaultValue = "my proxy host")
	private static String proxyHostname;

	@Config (name = "useProxy", 
			defaultValue = "false")
	private static boolean useProxy;


	@Config (name = "priSort", 
			defaultValue = "author")
	private static String priSort;

	@Config (name = "priDescending", 
			defaultValue = "false")
	private static boolean priDescending;

	@Config(name="secSort",
			defaultValue = "year")
	private static String secondary_sort_field;

	@Config(name = "secDescending", 
			defaultValue = "true")
	private static boolean secDescending;

	@Config(name = "terSort", 
			defaultValue = "author")
	private static String terSort;

	@Config (name = "terDescending", 
			defaultValue = "false")
	private static boolean tertiarySortDescending;

	@Config (name = "saveInOriginalOrder", 
			defaultValue = "false")
	private static boolean saveInOriginalOrder;

	@Config (name = "saveInSpecifiedOrder", 
			defaultValue = "false")
	private static boolean saveInSpecifiedOrder;

	@Config(name = "savePriSort",
			defaultValue = "bibtexkey")
	private static String savePriSort;

	@Config (name = "savePriDescending", 
			defaultValue = "false")
	private static boolean savePriDescending;

	@Config (name = "saveSecSort", 
			defaultValue = "author")
	private static String saveSecSort;

	@Config(name = "saveSecDescending",
			defaultValue = "true")
	private static boolean saveSecDescending;

	@Config(name = "saveTerSort",
			defaultValue= "")
	private static String saveTerSort;

	@Config (name = "saveTerDescending", 
			defaultValue = "true")
	private static boolean saveTerDescending;

	@Config(name = "exportInOriginalOrder", 
			defaultValue = "false")
	private static boolean exportInOriginalOrder;

	@Config (name = "exportInSpecifiedOrder",
			defaultValue = "false")
	private static boolean exportInSpecifiedOrder;

	@Config (name = "exportPriSort",
			defaultValue = "bibtexkey")
	private static String exportPriSort;

	@Config(name = "exportPriDescending", 
			defaultValue = "false")
	private static boolean exportPriDescending;

	@Config (name = "exportSecSort", 
			defaultValue = "author")
	private static String exportSecSort;

	@Config(name = "exportSecDescending", 
			defaultValue = "true")
	private static boolean exportSecDescending;

	@Config (name = "exportTerSort", 
			defaultValue = "")
	private static String exportTerSort;

	@Config (name = "exportTerDescending", 
			defaultValue = "true")
	private static boolean exportTerDescending;

	@Config (name = "newline",
			defaultValue = "&#10;")
	private static String newline;

	public static final String COLUMN_WIDTHS = "columnWidths";
	public static final String COLUMN_NAMES = "columnNames";
	public static final String SIDE_PANE_COMPONENT_PREFERRED_POSITIONS = "sidePaneComponentPreferredPositions";
	public static final String SIDE_PANE_COMPONENT_NAMES = "sidePaneComponentNames";
	public static final String XMP_PRIVACY_FILTERS = "xmpPrivacyFilters";

	@Config (name = "useXmpPrivacyFilter", 
			defaultValue = "false")
	private static boolean useXmpPrivacyFilter;


	public static final String SEARCH_AUTO_COMPLETE = "searchAutoComplete";
	public static final String INCREMENT_S = "incrementS";
	public static final String SEARCH_ALL = "searchAll";
	public static final String SEARCH_GEN = "searchGen";
	public static final String SEARCH_OPT = "searchOpt";
	public static final String SEARCH_REQ = "searchReq";
	public static final String CASE_SENSITIVE_SEARCH = "caseSensitiveSearch";
	public static final String DEFAULT_AUTO_SORT = "defaultAutoSort";

	@Config(name = "showSource",
			defaultValue = "true")
	private static boolean showSource;

	public static final String DEFAULT_SHOW_SOURCE = "defaultShowSource";
	public static final String STRINGS_SIZE_Y = "stringsSizeY";
	public static final String STRINGS_SIZE_X = "stringsSizeX";
	public static final String STRINGS_POS_Y = "stringsPosY";
	public static final String STRINGS_POS_X = "stringsPosX";
	public static final String LAST_EDITED = "lastEdited";

	@Config(name = "openLastEdited", 
			defaultValue = "true")
	private static boolean openLastEdited;

	@Config(name = "backup", 
			defaultValue = "true")
	private static boolean backup;

	public static final String ENTRY_TYPE_FORM_WIDTH = "entryTypeFormWidth";
	public static final String ENTRY_TYPE_FORM_HEIGHT_FACTOR = "entryTypeFormHeightFactor";

	@Config (name = "autoOpenForm",
			defaultValue = "true")
	private static boolean autoOpenForm;

	public static final String FILE_WORKING_DIRECTORY = "fileWorkingDirectory";
	public static final String IMPORT_WORKING_DIRECTORY = "importWorkingDirectory";
	public static final String EXPORT_WORKING_DIRECTORY = "exportWorkingDirectory";
	public static final String WORKING_DIRECTORY = "workingDirectory";
	public static final String NUMBER_COL_WIDTH = "numberColWidth";

	@Config ( name = "shortestToComplete", 
			defaultValue = "2")
	private static int shortestToComplete;

	@Config (name = "autoCompFirstNameMode", 
			defaultValue = "both")
	private static String autoCompFirstNameMode;

	public final static String AUTOCOMPLETE_FIRSTNAME_MODE_BOTH = "both"; // here are the possible values for _MODE:

	@Config (name = "autoCompLF", 
			defaultValue = "false")
	private static boolean autoCompLF;

	@Config(name = "autoCompFF", 
			defaultValue = "false")
	private static boolean autoCompFF;

	@Config (name = "autoCompleteFields", 
			defaultValue = "author;editor;title;journal;publisher;keywords;crossref")
	private static String autoCompleteFields;

	@Config (name = "autoComplete",
			defaultValue = "true")
	private static boolean autoComplete;

	public static final String SEARCH_PANE_POS_Y = "searchPanePosY";
	public static final String SEARCH_PANE_POS_X = "searchPanePosX";
	public static final String HIGH_LIGHT_WORDS = "highLightWords";
	public static final String REG_EXP_SEARCH = "regExpSearch";
	public static final String SELECT_S = "selectS";
	public static final String EDITOR_EMACS_KEYBINDINGS = "editorEMACSkeyBindings";
	public static final String EDITOR_EMACS_KEYBINDINGS_REBIND_CA = "editorEMACSkeyBindingsRebindCA";
	public static final String EDITOR_EMACS_KEYBINDINGS_REBIND_CF = "editorEMACSkeyBindingsRebindCF";
	public static final String GROUP_SHOW_NUMBER_OF_ELEMENTS = "groupShowNumberOfElements";

	@Config (name = "groupAutoHide", 
			defaultValue = "true")
	private static boolean groupAutoHide;

	@Config (name = "groupAutoShow", 
			defaultValue = "true")
	private static boolean groupAutoShow;

	@Config (name = "groupExpandTree",
			defaultValue = "true")
	private static boolean groupExpandTree;

	@Config(name="groupShowDynamic", 
			defaultValue = "true")
	private static boolean groupShowDynamic;

	@Config (name = "groupShowIcons", 
			defaultValue = "true")
	private static boolean groupShowIcons;

	@Config(name = "groupsDefaultField", 
			defaultValue = "keywords")
	private static String groupsDefaultField;


	public static final String GROUP_SELECT_MATCHES = "groupSelectMatches";
	public static final String GROUP_SHOW_OVERLAPPING = "groupShowOverlapping";
	public static final String GROUP_INVERT_SELECTIONS = "groupInvertSelections";
	public static final String GROUP_INTERSECT_SELECTIONS = "groupIntersectSelections";
	public static final String GROUP_FLOAT_SELECTIONS = "groupFloatSelections";
	public static final String GROUP_SELECTOR_VISIBLE = "groupSelectorVisible";
	public static final String EDIT_GROUP_MEMBERSHIP_MODE = "groupEditGroupMembershipMode";

	@Config (name = "groupKeywordSeparator", 
			defaultValue = ", ")
	private static String groupKeywordSeparator;

	@Config(name="autoAssignGroup",
			defaultValue = "true")
	private static boolean autoAssignGroup;

	@Config (name = "listOfFileColumns", 
			defaultValue = "")
	private static String listOfFileColumns;

	@Config (name = "extraFileColumns", 
			defaultValue = "false")
	private static boolean extraFileColumns;

	@Config(name = "arxivColumn", 
			defaultValue = "false")
	private static boolean arxivColumn;

	@Config (name = "fileColumn", 
			defaultValue = "true")
	private static boolean fileColumn;

	@Config (name = "preferUrlDoi", 
			defaultValue = "false")
	private static boolean preferUrlDoi;

	@Config(name = "urlColumn",
			defaultValue = "true")
	private static boolean urlColumn;

	@Config (name = "pdfColumn", 
			defaultValue = "false")
	private static boolean pdfColumn;

	@Config (name = "disableOnMultipleSelection"
			, defaultValue = "false")
	private static boolean disableOnMultipleSelection;

	@Config (name = "ctrlClick", 
			defaultValue = "false")
	private static boolean ctrlClick;

	public static final String ANTIALIAS = "antialias";

	@Config (name = "incompleteEntryBackground", 
			defaultValue = "250:175:175")
	private static String incompleteEntryBackground;
	

	@Config (name = "fieldEditorTextColor", 
			defaultValue = "0:0:0")
	private static String fieldEditorTextColor;

	@Config (name = "activeFieldEditorBackgroundColor", 
			defaultValue = "220:220:255")
	private static String activeFieldEditorBackgroundColor;

	@Config (name = "invalidFieldBackgroundColor", 
			defaultValue = "255:0:0")
	private static String invalidFieldBackgroundColor;

	@Config (name = "validFieldBackgroundColor", 
			defaultValue = "255:255:255")
	private static String validFieldBackgroundColor;

	@Config (name = "markedEntryBackground5", 
			defaultValue = "220:255:220")
	private static String markedEntryBackground5;

	@Config (name = "markedEntryBackground4", 
			defaultValue = "255:75:75")
	private static String markedEntryBackground4;

	@Config (name = "markedEntryBackground3", 
			defaultValue = "255:120:120")
	private static String markedEntryBackground3;

	@Config(name="markedEntryBackground2", 
			defaultValue = "255:180:160")
	private static String markedEntryBackground2;

	@Config (name = "markedEntryBackground1", 
			defaultValue = "255:220:180")
	private static String markedEntryBackground1;

	@Config (name = "markedEntryBackground0", 
			defaultValue = "255:255:180")
	private static String markedEntryBackground0;

	public static final String VERY_GRAYED_OUT_TEXT = "veryGrayedOutText";
	public static final String VERY_GRAYED_OUT_BACKGROUND = "veryGrayedOutBackground";
	public static final String GRAYED_OUT_TEXT = "grayedOutText";
	public static final String GRAYED_OUT_BACKGROUND = "grayedOutBackground";

	@Config (name = "gridColor", 
			defaultValue = "210:210:210")
	private static String gridColor;

	@Config (name = "tableText", 
			defaultValue = "0:0:0")
	private static String tableText;

	@Config (name = "tableOptFieldBackground" , 
			defaultValue = "230:255:230")
	private static String tableOptFieldBackground;

	@Config (name = "tableReqFieldBackground", 
			defaultValue = "230:235:255")
	private static String tableReqFieldBackground;

	@Config (name = "tableBackground", 
			defaultValue = "255:255:255")
	private static String tableBackground;

	@Config (name = "tableShowGrid",
			defaultValue = "false")
	private static boolean tableShowGrid; 

	@Config (name = "tableRowPadding",
			defaultValue = "8")
	private static int tableRowPadding;

	public static final String MENU_FONT_SIZE = "menuFontSize";
	public static final String MENU_FONT_STYLE = "menuFontStyle";
	public static final String MENU_FONT_FAMILY = "menuFontFamily";

	@Config (name = "overrideDefaultFonts", 
			defaultValue = "false")
	private static boolean overrideDefaultFonts;

	@Config (name = "fontSize",
			defaultValue = "12")
	private static int fontSize;

	@Config (name = "fontStyle", 
			defaultValue = "0")
	private static int fontStyle;

	public static final String HISTORY_SIZE = "historySize";
	public static final String CUSTOM_ICON_THEME_FILE = "customIconThemeFile";
	public static final String USE_CUSTOM_ICON_THEME = "useCustomIconTheme";
	public static final String GENERAL_FIELDS = "generalFields";
	public static final String RENAME_ON_MOVE_FILE_TO_FILE_DIR = "renameOnMoveFileToFileDir";

	@Config(name="memoryStickMode",
			defaultValue = "false")
	private static boolean memoryStickMode;


	public static final String PRESERVE_FIELD_FORMATTING = "preserveFieldFormatting";
	public static final String DEFAULT_OWNER = "defaultOwner";
	public static final String GROUPS_VISIBLE_ROWS = "groupsVisibleRows";

	@Config (name = "defaultEncoding", 
			defaultValue = "ISO8859_1")
	private static String defaultEncoding; 

	@Config (name = "searchPanelVisible", 
			defaultValue = "false")
	private static boolean searchPanelVisible;

	@Config(name = "toolbarVisible", 
			defaultValue = "true")
	private static boolean toolbarVisible;

	public static final String HIGHLIGHT_GROUPS_MATCHING_ALL = "highlightGroupsMatchingAll";
	public static final String HIGHLIGHT_GROUPS_MATCHING_ANY = "highlightGroupsMatchingAny";

	@Config (name = "showOneLetterHeadingForIconColumns", 
			defaultValue = "false")
	private static boolean showOneLetterHeadingForIconColumns;

	@Config (name = "updateTimestamp", 
			defaultValue = "false")
	private static boolean updateTimestamp;

	@Config (name = "timeStampField", 
			defaultValue = "timestamp")
	private static String timeStampField;

	@Config(name = "timeStampFormat",
			defaultValue = "yyyy.MM.dd")
	private static String timeStampFormat;

	@Config (name = "overwriteTimeStamp", 
			defaultValue = "false")
	private static boolean overwriteTimeStamp;

	@Config (name = "useTimeStamp", 
			defaultValue = "false")
	private static boolean useTimeStamp; 

	@Config (name = "warnAboutDuplicatesInInspection", 
			defaultValue = "true")
	private static boolean warnAboutDuplicatesInInspection;

	@Config (name = "unmarkAllEntriesBeforeImporting" , 
			defaultValue = "true")
	private static boolean unmarkAllEntriesBeforeImporting;

	@Config (name = "markImportedEntries", 
			defaultValue = "true")
	private static boolean markImportedEntries;

	@Config (name = "generateKeysAfterInspection", 
			defaultValue = "true")
	private static boolean generateKeysAfterInspection; 

	@Config (name = "useImportInspectionDialogForSingle", 
			defaultValue = "true")
	private static boolean useImportInspectionDialogForSingle;

	@Config (name = "useImportInspectionDialog", 
			defaultValue = "true")
	private static boolean useImportInspectionDialog;

	public static final String NON_WRAPPABLE_FIELDS = "nonWrappableFields";
	public static final String PUT_BRACES_AROUND_CAPITALS = "putBracesAroundCapitals";

	@Config(name = "resolveStringsAllFields", 
			defaultValue = "false")
	private static boolean resolveStringsAllFields;

	@Config (name = "doNotResolveStringsFor", 
			defaultValue = "url")
	private static String doNotResolveStringsFor;

	@Config (name = "autoDoubleBraces",
			defaultValue = "false")
	private static boolean autoDoubleBraces;

	public static final String PREVIEW_PRINT_BUTTON = "previewPrintButton";
	public static final String PREVIEW_1 = "preview1";
	public static final String PREVIEW_0 = "preview0";
	public static final String ACTIVE_PREVIEW = "activePreview";
	public static final String PREVIEW_ENABLED = "previewEnabled";

	@Config (name = "defaultLabelPattern", 
			defaultValue = "[auth][year]")
	private static String defaultLabelPattern;

	public static final String SEARCH_ALL_BASES = "searchAllBases";
	public static final String SHOW_SEARCH_IN_DIALOG = "showSearchInDialog";
	public static final String FLOAT_SEARCH = "floatSearch";
	public static final String GRAY_OUT_NON_HITS = "grayOutNonHits";

	@Config (name = "confirmDelete", 
			defaultValue = "true")
	private static boolean confirmDelete;


	@Config(name="warnBeforeOverwritingKey",
			defaultValue="true")
	private static boolean warnBeforeOverwritingKey;


	@Config(name="avoidOverwritingKey",
			defaultValue="false")
	private static boolean avoidOverwritingKey;

	public static final String DISPLAY_KEY_WARNING_DIALOG_AT_STARTUP = "displayKeyWarningDialogAtStartup";

	@Config (name ="dialogWarningForEmptyKey",
			defaultValue = "true")
	private static boolean dialogWarningForEmptyKey;



	@Config ( name = "dialogWarningForDuplicateKey", 
			defaultValue = "true")
	private static boolean dialogWarningForDuplicateKey;

	@Config(name = "allowTableEditing", 
			defaultValue = "false")
	private static boolean allowTableEditing; 


	@Config (name = "overwriteOwner", 
			defaultValue = "false")
	private static boolean overwriteOwner;


	@Config(name = "useOwner", 
			defaultValue = "false")
	private static boolean useOwner;


	@Config(name = "writeFieldAddSpaces", 
			defaultValue = "true")
	private static boolean writeFieldAddSpaces;


	@Config(name = "writeFieldCamelCase", 
			defaultValue = "true")
	private static boolean writeFieldCamelCase;


	@Config (name = "writefieldSortStyle", 
			defaultValue = "0")
	private static int writefieldSortStyle;

	@Config (name = "writefieldUserdefinedOrder", 
			defaultValue = "author;title;journal;year;volume;number;pages;month;note;volume;pages;part;eid")
	private static String writefieldUserdefinedOrder;

	@Config(name = "wrapFieldLine", 
			defaultValue = "false")
	private static boolean wrapFieldLine;

	@Config (name = "autolinkExactKeyOnly", 
			defaultValue = "true")
	private static boolean autolinkExactKeyOnly;

	public static final String SHOW_FILE_LINKS_UPGRADE_WARNING = "showFileLinksUpgradeWarning";
	public static final String SEARCH_DIALOG_HEIGHT = "searchDialogHeight";
	public static final String SEARCH_DIALOG_WIDTH = "searchDialogWidth";
	public static final String IMPORT_INSPECTION_DIALOG_HEIGHT = "importInspectionDialogHeight";
	public static final String IMPORT_INSPECTION_DIALOG_WIDTH = "importInspectionDialogWidth";
	public static final String SIDE_PANE_WIDTH = "sidePaneWidth";
	public static final String LAST_USED_EXPORT = "lastUsedExport";

	@Config (name = "filechooserDisableRename", 
			defaultValue = "true")
	private static boolean filechooserDisableRename;

	@Config (name = "useNativeFileDialogOnMac", 
			defaultValue = "false")
	private static boolean useNativeFileDialogOnMac;

	@Config(name = "floatMarkedEntries", 
			defaultValue = "true")
	private static boolean floatMarkedEntries;

	public static final String CITE_COMMAND_LED = "citeCommandLed";
	public static final String CITE_COMMAND_WIN_EDT = "citeCommandWinEdt";
	public static final String CITE_COMMAND_EMACS = "citeCommandEmacs";
	public static final String CITE_COMMAND_VIM = "citeCommandVim";
	public static final String CITE_COMMAND = "citeCommand";
	public static final String EXTERNAL_JOURNAL_LISTS = "externalJournalLists";
	public static final String PERSONAL_JOURNAL_LIST = "personalJournalList";

	@Config (name = "generateKeysBeforeSaving", 
			defaultValue = "false")
	private static boolean generateKeysBeforeSaving;

	@Config(name = "emailSubject", 
			defaultValue = "References")
	private static String emailSubject;

	@Config (name = "openFoldersOfAttachedFiles", 
			defaultValue = "false")
	private static boolean openFoldersOfAttachedFiles;

	@Config (name = "keyGenAlwaysAddLetter", 
			defaultValue = "false") 
	private static boolean keyGenAlwaysAddLetter;

	@Config (name = "keyGenFirstLetterA", 
			defaultValue = "true")
	private static boolean keyGenFirstLetterA;

	@Config(name = "includeEmptyFields", 
			defaultValue = "false")
	private static boolean includeEmptyFields;

	@Config (name = "valueDelimiters", 
			defaultValue = "1")
	private static int valueDelimiters;

	@Config (name = "biblatexMode",
			defaultValue = "false")
	private static boolean biblatexMode;

	@Config (name = "enforceLegalBibtexKey", 
			defaultValue = "true")
	private static boolean enforceLegalBibtexKey;

	public static final String DELETE_PLUGINS = "deletePlugins";

	@Config (name = "promptBeforeUsingAutosave", 
			defaultValue = "true")
	private static boolean promptBeforeUsingAutosave;

	@Config(name = "autoSaveInterval", 
			defaultValue = "5")
	private static int autoSaveInterval;

	@Config (name = "autoSave", 
			defaultValue = "true")
	private static boolean autoSave;

	public static final String USE_LOCK_FILES = "useLockFiles";

	@Config(name = "runAutomaticFileSearch", 
			defaultValue = "false")
	private static boolean runAutomaticFileSearch;

	public static final String NUMERIC_FIELDS = "numericFields";
	public static final String DEFAULT_REG_EXP_SEARCH_EXPRESSION_KEY = "defaultRegExpSearchExpression";
	public static final String REG_EXP_SEARCH_EXPRESSION_KEY = "regExpSearchExpression";

	@Config (name = "useRegExpSearch", 
			defaultValue = "false")
	private static boolean useRegExpSearch;

	public static final String DB_CONNECT_USERNAME = "dbConnectUsername";
	public static final String DB_CONNECT_DATABASE = "dbConnectDatabase";
	public static final String DB_CONNECT_HOSTNAME = "dbConnectHostname";
	public static final String DB_CONNECT_SERVER_TYPE = "dbConnectServerType";

	@Config(name = "bibLocAsPrimaryDir",
			defaultValue = "false")
	private static boolean bib_loc_as_primary_dir;

	@Config (name = "bibLocationAsFileDir", 
			defaultValue = "true")
	private static boolean bibLocationAsFileDir;

	public static final String SELECTED_FETCHER_INDEX = "selectedFetcherIndex";
	public static final String WEB_SEARCH_VISIBLE = "webSearchVisible";

	@Config(name = "allowFileAutoOpenBrowse", 
			defaultValue = "true")
	private static boolean allowFileAutoOpenBrowse;

	public final static String CUSTOM_TAB_NAME = "customTabName_";
	public final static String CUSTOM_TAB_FIELDS = "customTabFields_";
	public static final String USER_FILE_DIR_INDIVIDUAL = "userFileDirIndividual";
	public static final String USER_FILE_DIR_IND_LEGACY = "userFileDirInd_Legacy";
	public static final String USER_FILE_DIR = "userFileDir";

	@Config(name = "useUnitFormatterOnSearch" , 
			defaultValue = "true")
	private static boolean useUnitFormatterOnSearch;

	@Config (name = "useCaseKeeperOnSearch", 
			defaultValue = "true")
	private static boolean useCaseKeeperOnSearch;

	@Config (name = "useConvertToEquation",
			defaultValue = "false")
	private static boolean useConvertToEquation;

	@Config(name = "useIEEEAbrv", 
			defaultValue = "false")
	private static boolean useIEEEAbrv;
	
	@Config (name = "emptyField", 
			defaultValue = "NA", 
			constraint = "NA|Na|na|-",
			comment = "The text that should be printed if "
					+ "Owner or TimeStamp is empty")
	private static String emptyField;

	//non-default preferences
	private final static String CUSTOM_TYPE_NAME = "customTypeName_";
	private final static String CUSTOM_TYPE_REQ = "customTypeReq_";
	private final static String CUSTOM_TYPE_OPT = "customTypeOpt_";
	private final static String CUSTOM_TYPE_PRIOPT = "customTypePriOpt_";

	@Config(name = "pdfPreview", 
			defaultValue = "false")
	private static boolean pdfPreview;

	public final static String AUTOCOMPLETE_FIRSTNAME_MODE_ONLY_FULL = "fullOnly";
	public final static String AUTOCOMPLETE_FIRSTNAME_MODE_ONLY_ABBR = "abbrOnly";

	// This String is used in the encoded list in prefs of external file type
	// modifications, in order to indicate a removed default file type:
	private static final String FILE_TYPE_REMOVED_FLAG = "REMOVED";

	private static final char[][] VALUE_DELIMITERS = new char[][] { {'"', '"'}, {'{', '}'}};

	public String WRAPPED_USERNAME;
	public final String MARKING_WITH_NUMBER_PATTERN;

	private final Preferences prefs;

	private KeyBinds keyBinds = new KeyBinds();
	private KeyBinds defaultKeyBinds = new KeyBinds();

	private final HashSet<String> putBracesAroundCapitalsFields = new HashSet<String>(4);
	private final HashSet<String> nonWrappableFields = new HashSet<String>(5);
	private static LabelPattern keyPattern;

	// Object containing custom export formats:
	public final CustomExportList customExports;

	/**
	 * Set with all custom {@link net.sf.jabref.imports.ImportFormat}s
	 */
	public final CustomImportList customImports;

	// Object containing info about customized entry editor tabs.
	private EntryEditorTabList tabList = null;
	// Map containing all registered external file types:
	private final TreeSet<ExternalFileType> externalFileTypes = new TreeSet<ExternalFileType>();

	private final ExternalFileType HTML_FALLBACK_TYPE = new ExternalFileType("URL", "html", "text/html", "", "www");

	// The following field is used as a global variable during the export of a database.
	// By setting this field to the path of the database's default file directory, formatters
	// that should resolve external file paths can access this field. This is an ugly hack
	// to solve the problem of formatters not having access to any context except for the
	// string to be formatted and possible formatter arguments.
	public String[] fileDirForDatabase = null;

	// Similarly to the previous variable, this is a global that can be used during
	// the export of a database if the database filename should be output. If a database
	// is tied to a file on disk, this variable is set to that file before export starts:
	public File databaseFile = null;

	// The following field is used as a global variable during the export of a database.
	// It is used to hold custom name formatters defined by a custom export filter.
	// It is set before the export starts:
	public HashMap<String, String> customExportNameFormatters = null;

	// The only instance of this class:
	private static JabRefPreferences singleton = null;


	public static JabRefPreferences getInstance() {
		if (JabRefPreferences.singleton == null) {
			JabRefPreferences.singleton = new JabRefPreferences();
		}
		return JabRefPreferences.singleton;
	}

	// Upgrade the preferences for the current version
	// The old preference is kept in case an old version of JabRef is used with 
	// these preferences, but it is only used when the new preference does not 
	// exist
	private void upgradeOldPreferences() {
		if (prefs.getBoolean("saveInStandardOrder", false)) {
			setSaveInSpecifiedOrder(true);
			setSavePriSort("author");
			setSaveSecSort("editor");
			setSaveTerSort("year");
			setSavePriDescending(false);
			setSaveSecDescending(false);
			setSaveTerDescending(false);
		} else if (prefs.getBoolean("saveInTitleOrder", false)) {
			// saveInTitleOrder => title, author, editor
			setSaveInSpecifiedOrder(true);
			setSavePriSort("title");
			setSaveSecSort("author");
			setSaveTerSort("editor");
			setSavePriDescending(false);
			setSaveSecDescending(false);
			setSaveTerDescending(false);
		}
		if (prefs.getBoolean("exportInStandardOrder", false)) {
			setExportInSpecifiedOrder(true);
			setExportPriSort("author");
			setExportTerSort("year");
			setExportPriDescending(false);
			setExportSecDescending(false);
			setExportTerDescending(false);
		} else if (prefs.getBoolean("exportInTitleOrder", false)) {
			// exportInTitleOrder => title, author, editor
			setExportInSpecifiedOrder(true);
			setExportPriSort("title");
			setExportTerSort("editor");
			setExportPriDescending(false);
			setExportSecDescending(false);
			setExportTerDescending(false);
		}
	}

	// The constructor is made private to enforce this as a singleton class:
	private JabRefPreferences() {

		try {
			if (new File("jabref.xml").exists()) {
				importPreferences("jabref.xml");
			}
		} catch (IOException e) {
			LOGGER.info("Could not import preferences from jabref.xml:" + e.getLocalizedMessage(), e);
		}

		// load user preferences 
		prefs = Preferences.userNodeForPackage(JabRef.class);
		upgradeOldPreferences();

		if (Globals.osName.equals(Globals.MAC)) {
			defaults.put(EMACS_PATH, "emacsclient");
			defaults.put(EMACS_23, true);
			defaults.put(EMACS_ADDITIONAL_PARAMETERS, "-n -e");

		} else if (Globals.osName.toLowerCase().startsWith("windows")) {
			defaults.put(WIN_EDT_PATH, "C:\\Program Files\\WinEdt Team\\WinEdt\\WinEdt.exe");
			defaults.put(LATEX_EDITOR_PATH, "C:\\Program Files\\LEd\\LEd.exe");
			defaults.put(EMACS_PATH, "emacsclient.exe");
			defaults.put(EMACS_23, true);
			defaults.put(EMACS_ADDITIONAL_PARAMETERS, "-n -e");
		} else {
			// linux
			defaults.put(EMACS_PATH, "gnuclient");
			defaults.put(EMACS_23, false);
			defaults.put(EMACS_ADDITIONAL_PARAMETERS, "-batch -eval");
		}
		defaults.put(LYXPIPE, System.getProperty("user.home") + File.separator + ".lyx/lyxpipe");
		defaults.put(VIM, "vim");
		defaults.put(VIM_SERVER, "vim");
		defaults.put(PREVIEW_PANEL_HEIGHT, 200);
		defaults.put(ENTRY_EDITOR_HEIGHT, 400);
		defaults.put(NAMES_LAST_FIRST, Boolean.FALSE); // "Show 'Lastname, Firstname'"
		defaults.put(LANGUAGE, "en");
		defaults.put(SHOW_SHORT, Boolean.TRUE);


		defaults.put(SIDE_PANE_COMPONENT_NAMES, "");
		defaults.put(SIDE_PANE_COMPONENT_PREFERRED_POSITIONS, "");

		defaults.put(COLUMN_NAMES, "entrytype;author;title;year;journal;owner;timestamp;bibtexkey");
		defaults.put(COLUMN_WIDTHS, "75;280;400;60;100;100;100;100");
		defaults.put(PersistenceTableColumnListener.ACTIVATE_PREF_KEY,
				PersistenceTableColumnListener.DEFAULT_ENABLED);
		defaults.put(XMP_PRIVACY_FILTERS, "pdf;timestamp;keywords;owner;note;review");
		defaults.put(NUMBER_COL_WIDTH, GUIGlobals.NUMBER_COL_LENGTH);
		defaults.put(WORKING_DIRECTORY, System.getProperty("user.home"));
		defaults.put(EXPORT_WORKING_DIRECTORY, System.getProperty("user.home"));
		defaults.put(IMPORT_WORKING_DIRECTORY, System.getProperty("user.home"));
		defaults.put(FILE_WORKING_DIRECTORY, System.getProperty("user.home"));
		defaults.put(ENTRY_TYPE_FORM_HEIGHT_FACTOR, 1);
		defaults.put(ENTRY_TYPE_FORM_WIDTH, 1);
		defaults.put(LAST_EDITED, null);
		defaults.put(STRINGS_POS_X, 0);
		defaults.put(STRINGS_POS_Y, 0);
		defaults.put(STRINGS_SIZE_X, 600);
		defaults.put(STRINGS_SIZE_Y, 400);
		defaults.put(DEFAULT_SHOW_SOURCE, Boolean.FALSE);
		defaults.put(DEFAULT_AUTO_SORT, Boolean.FALSE);
		defaults.put(CASE_SENSITIVE_SEARCH, Boolean.FALSE);
		defaults.put(SEARCH_REQ, Boolean.TRUE);
		defaults.put(SEARCH_OPT, Boolean.TRUE);
		defaults.put(SEARCH_GEN, Boolean.TRUE);
		defaults.put(SEARCH_ALL, Boolean.FALSE);
		defaults.put(INCREMENT_S, Boolean.FALSE);
		defaults.put(SEARCH_AUTO_COMPLETE, Boolean.TRUE);

		defaults.put(SELECT_S, Boolean.FALSE);
		defaults.put(REG_EXP_SEARCH, Boolean.TRUE);
		defaults.put(HIGH_LIGHT_WORDS, Boolean.TRUE);
		defaults.put(SEARCH_PANE_POS_X, 0);
		defaults.put(SEARCH_PANE_POS_Y, 0);
		defaults.put(EDITOR_EMACS_KEYBINDINGS, Boolean.FALSE);
		defaults.put(EDITOR_EMACS_KEYBINDINGS_REBIND_CA, Boolean.TRUE);
		defaults.put(EDITOR_EMACS_KEYBINDINGS_REBIND_CF, Boolean.TRUE);
		defaults.put(GROUP_SELECTOR_VISIBLE, Boolean.TRUE);
		defaults.put(GROUP_FLOAT_SELECTIONS, Boolean.TRUE);
		defaults.put(GROUP_INTERSECT_SELECTIONS, Boolean.TRUE);
		defaults.put(GROUP_INVERT_SELECTIONS, Boolean.FALSE);
		defaults.put(GROUP_SHOW_OVERLAPPING, Boolean.FALSE);
		defaults.put(GROUP_SELECT_MATCHES, Boolean.FALSE);
		defaults.put(GROUP_SHOW_NUMBER_OF_ELEMENTS, Boolean.FALSE);
		defaults.put(EDIT_GROUP_MEMBERSHIP_MODE, Boolean.FALSE);
		defaults.put(HIGHLIGHT_GROUPS_MATCHING_ANY, Boolean.FALSE);
		defaults.put(HIGHLIGHT_GROUPS_MATCHING_ALL, Boolean.FALSE);
		defaults.put(GROUPS_VISIBLE_ROWS, 8);
		defaults.put(DEFAULT_OWNER, System.getProperty("user.name"));
		defaults.put(PRESERVE_FIELD_FORMATTING, Boolean.FALSE);
		defaults.put(RENAME_ON_MOVE_FILE_TO_FILE_DIR, Boolean.TRUE);

		// The general fields stuff is made obsolete by the CUSTOM_TAB_... entries.
		defaults.put(GENERAL_FIELDS, "crossref;keywords;file;doi;url;urldate;"
				+ "pdf;comment;owner");

		defaults.put(USE_CUSTOM_ICON_THEME, Boolean.FALSE);
		defaults.put(CUSTOM_ICON_THEME_FILE, "/home/alver/div/crystaltheme_16/Icons.properties");

		defaults.put(HISTORY_SIZE, 8);
		defaults.put(MENU_FONT_FAMILY, "Times");
		defaults.put(MENU_FONT_STYLE, java.awt.Font.PLAIN);
		defaults.put(MENU_FONT_SIZE, 11);
		defaults.put(GRAYED_OUT_BACKGROUND, "210:210:210");
		defaults.put(GRAYED_OUT_TEXT, "40:40:40");
		defaults.put(VERY_GRAYED_OUT_BACKGROUND, "180:180:180");
		defaults.put(VERY_GRAYED_OUT_TEXT, "40:40:40");

		defaults.put(ANTIALIAS, Boolean.FALSE);

		defaults.put(SpecialFieldsUtils.PREF_SPECIALFIELDSENABLED, SpecialFieldsUtils.PREF_SPECIALFIELDSENABLED_DEFAULT);
		defaults.put(SpecialFieldsUtils.PREF_SHOWCOLUMN_PRIORITY, SpecialFieldsUtils.PREF_SHOWCOLUMN_PRIORITY_DEFAULT);
		defaults.put(SpecialFieldsUtils.PREF_SHOWCOLUMN_QUALITY, SpecialFieldsUtils.PREF_SHOWCOLUMN_QUALITY_DEFAULT);
		defaults.put(SpecialFieldsUtils.PREF_SHOWCOLUMN_RANKING, SpecialFieldsUtils.PREF_SHOWCOLUMN_RANKING_DEFAULT);
		defaults.put(SpecialFieldsUtils.PREF_RANKING_COMPACT, SpecialFieldsUtils.PREF_RANKING_COMPACT_DEFAULT);
		defaults.put(SpecialFieldsUtils.PREF_SHOWCOLUMN_RELEVANCE, SpecialFieldsUtils.PREF_SHOWCOLUMN_RELEVANCE_DEFAULT);
		defaults.put(SpecialFieldsUtils.PREF_SHOWCOLUMN_PRINTED, SpecialFieldsUtils.PREF_SHOWCOLUMN_PRINTED_DEFAULT);
		defaults.put(SpecialFieldsUtils.PREF_SHOWCOLUMN_READ, SpecialFieldsUtils.PREF_SHOWCOLUMN_READ_DEFAULT);
		defaults.put(SpecialFieldsUtils.PREF_AUTOSYNCSPECIALFIELDSTOKEYWORDS, SpecialFieldsUtils.PREF_AUTOSYNCSPECIALFIELDSTOKEYWORDS_DEFAULT);
		defaults.put(SpecialFieldsUtils.PREF_SERIALIZESPECIALFIELDS, SpecialFieldsUtils.PREF_SERIALIZESPECIALFIELDS_DEFAULT);

		// defaults.put(SHOW_ONE_LETTER_HEADING_FOR_ICON_COLUMNS, Boolean.FALSE);

		defaults.put(DISPLAY_KEY_WARNING_DIALOG_AT_STARTUP, Boolean.TRUE);
		defaults.put(GRAY_OUT_NON_HITS, Boolean.TRUE);
		defaults.put(FLOAT_SEARCH, Boolean.TRUE);
		defaults.put(SHOW_SEARCH_IN_DIALOG, Boolean.FALSE);
		defaults.put(SEARCH_ALL_BASES, Boolean.FALSE);
		defaults.put(PREVIEW_ENABLED, Boolean.TRUE);
		defaults.put(ACTIVE_PREVIEW, 0);
		defaults.put(PREVIEW_0, "<font face=\"arial\">"
				+ "<b><i>\\bibtextype</i><a name=\"\\bibtexkey\">\\begin{bibtexkey} (\\bibtexkey)</a>"
				+ "\\end{bibtexkey}</b><br>__NEWLINE__"
				+ "\\begin{author} \\format[Authors(LastFirst,Initials,Semicolon,Amp),HTMLChars]{\\author}<BR>\\end{author}__NEWLINE__"
				+ "\\begin{editor} \\format[Authors(LastFirst,Initials,Semicolon,Amp),HTMLChars]{\\editor} "
				+ "<i>(\\format[IfPlural(Eds.,Ed.)]{\\editor})</i><BR>\\end{editor}__NEWLINE__"
				+ "\\begin{title} \\format[HTMLChars]{\\title} \\end{title}<BR>__NEWLINE__"
				+ "\\begin{chapter} \\format[HTMLChars]{\\chapter}<BR>\\end{chapter}__NEWLINE__"
				+ "\\begin{journal} <em>\\format[HTMLChars]{\\journal}, </em>\\end{journal}__NEWLINE__"
				// Include the booktitle field for @inproceedings, @proceedings, etc.
				+ "\\begin{booktitle} <em>\\format[HTMLChars]{\\booktitle}, </em>\\end{booktitle}__NEWLINE__"
				+ "\\begin{school} <em>\\format[HTMLChars]{\\school}, </em>\\end{school}__NEWLINE__"
				+ "\\begin{institution} <em>\\format[HTMLChars]{\\institution}, </em>\\end{institution}__NEWLINE__"
				+ "\\begin{publisher} <em>\\format[HTMLChars]{\\publisher}, </em>\\end{publisher}__NEWLINE__"
				+ "\\begin{year}<b>\\year</b>\\end{year}\\begin{volume}<i>, \\volume</i>\\end{volume}"
				+ "\\begin{pages}, \\format[FormatPagesForHTML]{\\pages} \\end{pages}__NEWLINE__"
				+ "\\begin{abstract}<BR><BR><b>Abstract: </b> \\format[HTMLChars]{\\abstract} \\end{abstract}__NEWLINE__"
				+ "\\begin{review}<BR><BR><b>Review: </b> \\format[HTMLChars]{\\review} \\end{review}"
				+ "</dd>__NEWLINE__<p></p></font>");
		defaults.put(PREVIEW_1, "<font face=\"arial\">"
				+ "<b><i>\\bibtextype</i><a name=\"\\bibtexkey\">\\begin{bibtexkey} (\\bibtexkey)</a>"
				+ "\\end{bibtexkey}</b><br>__NEWLINE__"
				+ "\\begin{author} \\format[Authors(LastFirst,Initials,Semicolon,Amp),HTMLChars]{\\author}<BR>\\end{author}__NEWLINE__"
				+ "\\begin{editor} \\format[Authors(LastFirst,Initials,Semicolon,Amp),HTMLChars]{\\editor} "
				+ "<i>(\\format[IfPlural(Eds.,Ed.)]{\\editor})</i><BR>\\end{editor}__NEWLINE__"
				+ "\\begin{title} \\format[HTMLChars]{\\title} \\end{title}<BR>__NEWLINE__"
				+ "\\begin{chapter} \\format[HTMLChars]{\\chapter}<BR>\\end{chapter}__NEWLINE__"
				+ "\\begin{journal} <em>\\format[HTMLChars]{\\journal}, </em>\\end{journal}__NEWLINE__"
				// Include the booktitle field for @inproceedings, @proceedings, etc.
				+ "\\begin{booktitle} <em>\\format[HTMLChars]{\\booktitle}, </em>\\end{booktitle}__NEWLINE__"
				+ "\\begin{school} <em>\\format[HTMLChars]{\\school}, </em>\\end{school}__NEWLINE__"
				+ "\\begin{institution} <em>\\format[HTMLChars]{\\institution}, </em>\\end{institution}__NEWLINE__"
				+ "\\begin{publisher} <em>\\format[HTMLChars]{\\publisher}, </em>\\end{publisher}__NEWLINE__"
				+ "\\begin{year}<b>\\year</b>\\end{year}\\begin{volume}<i>, \\volume</i>\\end{volume}"
				+ "\\begin{pages}, \\format[FormatPagesForHTML]{\\pages} \\end{pages}"
				+ "</dd>__NEWLINE__<p></p></font>");

		// TODO: Currently not possible to edit this setting:
		defaults.put(PREVIEW_PRINT_BUTTON, Boolean.FALSE);
		defaults.put(PUT_BRACES_AROUND_CAPITALS, "");//"title;journal;booktitle;review;abstract");
		defaults.put(NON_WRAPPABLE_FIELDS, "pdf;ps;url;doi;file");

		defaults.put(RemotePreferences.USE_REMOTE_SERVER, Boolean.FALSE);
		defaults.put(RemotePreferences.REMOTE_SERVER_PORT, 6050);

		defaults.put(PERSONAL_JOURNAL_LIST, null);
		defaults.put(EXTERNAL_JOURNAL_LISTS, null);
		defaults.put(CITE_COMMAND, "cite"); // obsoleted by the app-specific ones
		defaults.put(CITE_COMMAND_VIM, "\\cite");
		defaults.put(CITE_COMMAND_EMACS, "\\cite");
		defaults.put(CITE_COMMAND_WIN_EDT, "\\cite");
		defaults.put(CITE_COMMAND_LED, "\\cite");

		defaults.put(LAST_USED_EXPORT, null);
		defaults.put(SIDE_PANE_WIDTH, -1);

		defaults.put(IMPORT_INSPECTION_DIALOG_WIDTH, 650);
		defaults.put(IMPORT_INSPECTION_DIALOG_HEIGHT, 650);
		defaults.put(SEARCH_DIALOG_WIDTH, 650);
		defaults.put(SEARCH_DIALOG_HEIGHT, 500);
		defaults.put(SHOW_FILE_LINKS_UPGRADE_WARNING, Boolean.TRUE);
		defaults.put(NUMERIC_FIELDS, "mittnum;author");
		defaults.put(USE_LOCK_FILES, Boolean.TRUE);
		defaults.put(DELETE_PLUGINS, "");
		defaults.put(WEB_SEARCH_VISIBLE, Boolean.FALSE);
		defaults.put(SELECTED_FETCHER_INDEX, 0);
		defaults.put(DB_CONNECT_SERVER_TYPE, "MySQL");
		defaults.put(DB_CONNECT_HOSTNAME, "localhost");
		defaults.put(DB_CONNECT_DATABASE, "jabref");
		defaults.put(DB_CONNECT_USERNAME, "root");
		CleanUpAction.putDefaults(defaults);

		// defaults for DroppedFileHandler UI
		defaults.put(DroppedFileHandler.DFH_LEAVE, Boolean.FALSE);
		defaults.put(DroppedFileHandler.DFH_COPY, Boolean.TRUE);
		defaults.put(DroppedFileHandler.DFH_MOVE, Boolean.FALSE);
		defaults.put(DroppedFileHandler.DFH_RENAME, Boolean.FALSE);

		defaults.put(ImportSettingsTab.PREF_IMPORT_ALWAYSUSE, Boolean.FALSE);
		defaults.put(ImportSettingsTab.PREF_IMPORT_DEFAULT_PDF_IMPORT_STYLE, ImportSettingsTab.DEFAULT_STYLE);
		defaults.put(ImportSettingsTab.PREF_IMPORT_FILENAMEPATTERN, ImportSettingsTab.DEFAULT_FILENAMEPATTERNS[0]);

		restoreKeyBindings();

		customExports = new CustomExportList(new ExportComparator());
		customImports = new CustomImportList(this);

		//defaults.put("oooWarning", Boolean.TRUE);
		updateSpecialFieldHandling();
		WRAPPED_USERNAME = '[' + get(DEFAULT_OWNER) + ']';
		MARKING_WITH_NUMBER_PATTERN = "\\[" + get(DEFAULT_OWNER).replaceAll("\\\\", "\\\\\\\\") + ":(\\d+)\\]";

		String defaultExpression = "**/.*[bibtexkey].*\\\\.[extension]";
		defaults.put(DEFAULT_REG_EXP_SEARCH_EXPRESSION_KEY, defaultExpression);
		defaults.put(REG_EXP_SEARCH_EXPRESSION_KEY, defaultExpression);

		defaults.put(USER_FILE_DIR, GUIGlobals.FILE_FIELD + "Directory");
		try {
			defaults.put(USER_FILE_DIR_IND_LEGACY, GUIGlobals.FILE_FIELD + "Directory" + '-' + get(DEFAULT_OWNER) + '@' + InetAddress.getLocalHost().getHostName()); // Legacy setting name - was a bug: @ not allowed inside BibTeX comment text. Retained for backward comp.
			defaults.put(USER_FILE_DIR_INDIVIDUAL, GUIGlobals.FILE_FIELD + "Directory" + '-' + get(DEFAULT_OWNER) + '-' + InetAddress.getLocalHost().getHostName()); // Valid setting name
		} catch (UnknownHostException ex) {
			LOGGER.info("Hostname not found.", ex);
			defaults.put(USER_FILE_DIR_IND_LEGACY, GUIGlobals.FILE_FIELD + "Directory" + '-' + get(DEFAULT_OWNER));
			defaults.put(USER_FILE_DIR_INDIVIDUAL, GUIGlobals.FILE_FIELD + "Directory" + '-' + get(DEFAULT_OWNER));
		}
	}

	public void setLanguageDependentDefaultValues() {

		// Entry editor tab 0:
		defaults.put(CUSTOM_TAB_NAME + "_def0", Globals.lang("General"));
		defaults.put(CUSTOM_TAB_FIELDS + "_def0", "crossref;keywords;file;doi;url;"
				+ "comment;owner;timestamp");

		// Entry editor tab 1:
		defaults.put(CUSTOM_TAB_FIELDS + "_def1", "abstract");
		defaults.put(CUSTOM_TAB_NAME + "_def1", Globals.lang("Abstract"));

		// Entry editor tab 2: Review Field - used for research comments, etc.
		defaults.put(CUSTOM_TAB_FIELDS + "_def2", "review");
		defaults.put(CUSTOM_TAB_NAME + "_def2", Globals.lang("Review"));

	}

	public boolean putBracesAroundCapitals(String fieldName) {
		return putBracesAroundCapitalsFields.contains(fieldName);
	}

	public void updateSpecialFieldHandling() {
		putBracesAroundCapitalsFields.clear();
		String fieldString = get(PUT_BRACES_AROUND_CAPITALS);
		if (!fieldString.isEmpty()) {
			String[] fields = fieldString.split(";");
			for (String field : fields) {
				putBracesAroundCapitalsFields.add(field.trim());
			}
		}
		nonWrappableFields.clear();
		fieldString = get(NON_WRAPPABLE_FIELDS);
		if (!fieldString.isEmpty()) {
			String[] fields = fieldString.split(";");
			for (String field : fields) {
				nonWrappableFields.add(field.trim());
			}
		}

	}

	public char getValueDelimiters(int index) {
		return getValueDelimiters()[index];
	}

	private char[] getValueDelimiters() {
		return JabRefPreferences.VALUE_DELIMITERS[getValueDelimiters2()];
	}

	/**
	 * Check whether a key is set (differently from null).
	 *
	 * @param key The key to check.
	 * @return true if the key is set, false otherwise.
	 */
	public boolean hasKey(String key) {
		return prefs.get(key, null) != null;
	}

	public String get(String key) {
		return prefs.get(key, (String) defaults.get(key));
	}

	public String get(String key, String def) {
		return prefs.get(key, def);
	}

	public boolean getBoolean(String key) {
		return prefs.getBoolean(key, getBooleanDefault(key));
	}

	private boolean getBooleanDefault(String key) {
		return (Boolean) defaults.get(key);
	}

	public double getDouble(String key) {
		return prefs.getDouble(key, getDoubleDefault(key));
	}

	private double getDoubleDefault(String key) {
		return (Double) defaults.get(key);
	}

	public int getInt(String key) {
		return prefs.getInt(key, getIntDefault(key));
	}

	public int getIntDefault(String key) {
		return (Integer) defaults.get(key);
	}

	public byte[] getByteArray(String key) {
		return prefs.getByteArray(key, getByteArrayDefault(key));
	}

	private byte[] getByteArrayDefault(String key) {
		return (byte[]) defaults.get(key);
	}

	public void put(String key, String value) {
		prefs.put(key, value);
	}

	public void putBoolean(String key, boolean value) {
		prefs.putBoolean(key, value);
	}

	public void putDouble(String key, double value) {
		prefs.putDouble(key, value);
	}

	public void putInt(String key, int value) {
		prefs.putInt(key, value);
	}

	public void putByteArray(String key, byte[] value) {
		prefs.putByteArray(key, value);
	}

	public void remove(String key) {
		prefs.remove(key);
	}

	/**
	 * Puts a string array into the Preferences, by linking its elements with ';' into a single string. Escape
	 * characters make the process transparent even if strings contain ';'.
	 */
	public void putStringArray(String key, String[] value) {
		if (value == null) {
			remove(key);
			return;
		}

		if (value.length > 0) {
			StringBuilder linked = new StringBuilder();
			for (int i = 0; i < (value.length - 1); i++) {
				linked.append(makeEscape(value[i]));
				linked.append(';');
			}
			linked.append(makeEscape(value[value.length - 1]));
			put(key, linked.toString());
		} else {
			put(key, "");
		}
	}

	public String putStringArray(String[] value) {
		if (value == null) {
			return "";
		}

		if (value.length > 0) {
			StringBuilder linked = new StringBuilder();
			for (int i = 0; i < (value.length - 1); i++) {
				linked.append(makeEscape(value[i]));
				linked.append(';');
			}
			linked.append(makeEscape(value[value.length - 1]));
			return linked.toString();
		} else {
			return "";
		}
	}

	/**
	 * Returns a String[] containing the chosen columns.
	 */
	public String[] getStringArray(String key, String value) {
		String names = null;
		if (key != null) {
			names = get(key);
		} else {
			names = value;
		}
		if (names == null) {
			return null;
		}

		StringReader rd = new StringReader(names);
		Vector<String> arr = new Vector<String>();
		String rs;
		try {
			while ((rs = getNextUnit(rd)) != null) {
				arr.add(rs);
			}
		} catch (IOException ignored) {
		}
		String[] res = new String[arr.size()];
		for (int i = 0; i < res.length; i++) {
			res[i] = arr.elementAt(i);
		}

		return res;
	}

	/**
	 * Looks up a color definition in preferences, and returns the Color object.
	 *
	 * @param key The key for this setting.
	 * @return The color corresponding to the setting.
	 */
	public Color getColor(String key, String value) {
		if (key != null) {
			value = get(key);
		}
		int[] rgb = getRgb(value);
		return new Color(rgb[0], rgb[1], rgb[2]);
	}

	public Color getDefaultColor(String key) {
		String value = (String) defaults.get(key);
		int[] rgb = getRgb(value);
		return new Color(rgb[0], rgb[1], rgb[2]);
	}

	/**
	 * Set the default value for a key. This is useful for plugins that need to add default values for the prefs keys
	 * they use.
	 *
	 * @param key The preferences key.
	 * @param value The default value.
	 */
	public void putDefaultValue(String key, Object value) {
		defaults.put(key, value);
	}

	/**
	 * Stores a color in preferences.
	 *
	 * @param key The key for this setting.
	 * @param color The Color to store.
	 */
	public void putColor(String key, Color color) {
		String rgb = String.valueOf(color.getRed()) + ':' + String.valueOf(color.getGreen()) + ':' + String.valueOf(color.getBlue());
		put(key, rgb);
	}

	/**
	 * Looks up a color definition in preferences, and returns an array containing the RGB values.
	 *
	 * @param value The key for this setting.
	 * @return The RGB values corresponding to this color setting.
	 */
	private int[] getRgb(String value) {
		String[] elements = value.split(":");
		int[] values = new int[3];
		values[0] = Integer.parseInt(elements[0]);
		values[1] = Integer.parseInt(elements[1]);
		values[2] = Integer.parseInt(elements[2]);
		return values;
	}

	/**
	 * Returns the KeyStroke for this binding, as defined by the defaults, or in the Preferences.
	 */
	public KeyStroke getKey(String bindName) {

		String s = keyBinds.get(bindName);
		// If the current key bindings don't contain the one asked for,
		// we fall back on the default. This should only happen when a
		// user has his own set in Preferences, and has upgraded to a
		// new version where new bindings have been introduced.
		if (s == null) {
			s = defaultKeyBinds.get(bindName);
			if (s == null) {
				// there isn't even a default value
				// Output error
				LOGGER.info("Could not get key binding for \"" + bindName + '"');
				// fall back to a default value
				s = "Not associated";
			}
			// So, if there is no configured key binding, we add the fallback value to the current
			// hashmap, so this doesn't happen again, and so this binding
			// will appear in the KeyBindingsDialog.
			keyBinds.put(bindName, s);
		}

		if (Globals.ON_MAC) {
			return getKeyForMac(KeyStroke.getKeyStroke(s));
		} else {
			return KeyStroke.getKeyStroke(s);
		}
	}

	/**
	 * Returns the KeyStroke for this binding, as defined by the defaults, or in the Preferences, but adapted for Mac
	 * users, with the Command key preferred instead of Control.
	 */
	private KeyStroke getKeyForMac(KeyStroke ks) {
		if (ks == null) {
			return null;
		}
		int keyCode = ks.getKeyCode();
		if ((ks.getModifiers() & InputEvent.CTRL_MASK) == 0) {
			return ks;
		} else {
			int modifiers = 0;
			if ((ks.getModifiers() & InputEvent.SHIFT_MASK) != 0) {
				modifiers = modifiers | InputEvent.SHIFT_MASK;
			}
			if ((ks.getModifiers() & InputEvent.ALT_MASK) != 0) {
				modifiers = modifiers | InputEvent.ALT_MASK;
			}

			return KeyStroke.getKeyStroke(keyCode, Globals.getShortcutMask() + modifiers);
		}
	}

	/**
	 * Returns the HashMap containing all key bindings.
	 */
	public HashMap<String, String> getKeyBindings() {
		return keyBinds.getKeyBindings();
	}

	/**
	 * Returns the HashMap containing default key bindings.
	 */
	public HashMap<String, String> getDefaultKeys() {
		return defaultKeyBinds.getKeyBindings();
	}

	/**
	 * Clear all preferences.
	 *
	 * @throws BackingStoreException
	 */
	public void clear() throws BackingStoreException {
		prefs.clear();
	}

	public void clear(String key) {
		prefs.remove(key);
	}

	/**
	 * Calling this method will write all preferences into the preference store.
	 */
	public void flush() {
		if (memoryStickMode) {
			try {
				exportPreferences("jabref.xml");
			} catch (IOException e) {
				LOGGER.info("Could not save preferences for memory stick mode: " + e.getLocalizedMessage(), e);
			}
		}
		try {
			prefs.flush();
		} catch (BackingStoreException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Stores new key bindings into Preferences, provided they actually differ from the old ones.
	 */
	public void setNewKeyBindings(HashMap<String, String> newBindings) {
		if (!newBindings.equals(keyBinds)) {
			// This confirms that the bindings have actually changed.
			String[] bindNames = new String[newBindings.size()], bindings = new String[newBindings.size()];
			int index = 0;
			for (String nm : newBindings.keySet()) {
				String bnd = newBindings.get(nm);
				bindNames[index] = nm;
				bindings[index] = bnd;
				index++;
			}
			putStringArray("bindNames", bindNames);
			putStringArray("bindings", bindings);
			keyBinds.overwriteBindings(newBindings);
		}
	}

	/**
	 * Fetches key patterns from preferences Not cached
	 *
	 * @return LabelPattern containing all keys. Returned LabelPattern has no parent
	 */
	public LabelPattern getKeyPattern() {
		JabRefPreferences.keyPattern = new LabelPattern();
		Preferences pre = Preferences.userNodeForPackage(net.sf.jabref.labelPattern.LabelPattern.class);
		try {
			String[] keys = pre.keys();
			if (keys.length > 0) {
				for (String key : keys) {
					JabRefPreferences.keyPattern.addLabelPattern(key, pre.get(key, null));
				}
			}
		} catch (BackingStoreException ex) {
			LOGGER.info("BackingStoreException in JabRefPreferences.getKeyPattern", ex);
		}
		return JabRefPreferences.keyPattern;
	}

	/**
	 * Adds the given key pattern to the preferences
	 *
	 * @param pattern the pattern to store
	 */
	public void putKeyPattern(LabelPattern pattern) {
		JabRefPreferences.keyPattern = pattern;

		// Store overridden definitions to Preferences.
		Preferences pre = Preferences.userNodeForPackage(net.sf.jabref.labelPattern.LabelPattern.class);
		try {
			pre.clear(); // We remove all old entries.
		} catch (BackingStoreException ex) {
			LOGGER.info("BackingStoreException in JabRefPreferences.putKeyPattern", ex);
		}

		for (Map.Entry<String, ArrayList<String>> stringArrayListEntry : pattern.entrySet()) {
			ArrayList<String> value = stringArrayListEntry.getValue();
			if (value != null) {
				// no default value
				// the first entry in the array is the full pattern
				// see net.sf.jabref.labelPattern.LabelPatternUtil.split(String)
				pre.put(stringArrayListEntry.getKey(), value.get(0));
			}
		}
	}

	private void restoreKeyBindings() {
		// Define default keybindings.
		defaultKeyBinds = new KeyBinds();

		// First read the bindings, and their names.
		String[] bindNames = getStringArray("bindNames", null),
				bindings = getStringArray("bindings", null);

		// Then set up the key bindings HashMap.
		if ((bindNames == null) || (bindings == null)
				|| (bindNames.length != bindings.length)) {
			// Nothing defined in Preferences, or something is wrong.
			keyBinds = new KeyBinds();
			return;
		}

		for (int i = 0; i < bindNames.length; i++) {
			keyBinds.put(bindNames[i], bindings[i]);
		}
	}

	private String getNextUnit(Reader data) throws IOException {
		// character last read
		// -1 if end of stream
		// initialization necessary, because of Java compiler
		int c = -1;

		// last character was escape symbol
		boolean escape = false;

		// true if a ";" is found
		boolean done = false;

		StringBuilder res = new StringBuilder();
		while (!done && ((c = data.read()) != -1)) {
			if (c == '\\') {
				if (!escape) {
					escape = true;
				} else {
					escape = false;
					res.append('\\');
				}
			} else {
				if (c == ';') {
					if (!escape) {
						done = true;
					} else {
						res.append(';');
					}
				} else {
					res.append((char) c);
				}
				escape = false;
			}
		}
		if (res.length() > 0) {
			return res.toString();
		} else if (c == -1) {
			// end of stream
			return null;
		} else {
			return "";
		}
	}

	private String makeEscape(String s) {
		StringBuilder sb = new StringBuilder();
		int c;
		for (int i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			if ((c == '\\') || (c == ';')) {
				sb.append('\\');
			}
			sb.append((char) c);
		}
		return sb.toString();
	}

	/**
	 * Stores all information about the entry type in preferences, with the tag given by number.
	 */
	public void storeCustomEntryType(CustomEntryType tp, int number) {
		String nr = "" + number;
		put(JabRefPreferences.CUSTOM_TYPE_NAME + nr, tp.getName());
		put(JabRefPreferences.CUSTOM_TYPE_REQ + nr, tp.getRequiredFieldsString());
		putStringArray(JabRefPreferences.CUSTOM_TYPE_OPT + nr, tp.getOptionalFields());
		putStringArray(JabRefPreferences.CUSTOM_TYPE_PRIOPT + nr, tp.getPrimaryOptionalFields());
	}

	/**
	 * Retrieves all information about the entry type in preferences, with the tag given by number.
	 */
	public CustomEntryType getCustomEntryType(int number) {
		String nr = "" + number;
		String name = get(JabRefPreferences.CUSTOM_TYPE_NAME + nr);
		String[] req    = getStringArray(JabRefPreferences.CUSTOM_TYPE_REQ + nr, null);
		String[] opt    = getStringArray(JabRefPreferences.CUSTOM_TYPE_OPT + nr, null);
		String[] priOpt = getStringArray(JabRefPreferences.CUSTOM_TYPE_PRIOPT + nr, null);
		if (name == null) {
			return null;
		}
		if (priOpt == null) {
			return new CustomEntryType(StringUtil.nCase(name), req, opt);
		}
		String[] secOpt = Util.getRemainder(opt, priOpt);
		return new CustomEntryType(StringUtil.nCase(name), req, priOpt, secOpt);

	}

	public List<ExternalFileType> getDefaultExternalFileTypes() {
		List<ExternalFileType> list = new ArrayList<ExternalFileType>();
		list.add(new ExternalFileType("PDF", "pdf", "application/pdf", "evince", "pdfSmall"));
		list.add(new ExternalFileType("PostScript", "ps", "application/postscript", "evince", "psSmall"));
		list.add(new ExternalFileType("Word", "doc", "application/msword", "oowriter", "openoffice"));
		list.add(new ExternalFileType("Word 2007+", "docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "oowriter", "openoffice"));
		list.add(new ExternalFileType("OpenDocument text", "odt", "application/vnd.oasis.opendocument.text", "oowriter", "openoffice"));
		list.add(new ExternalFileType("Excel", "xls", "application/excel", "oocalc", "openoffice"));
		list.add(new ExternalFileType("Excel 2007+", "xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "oocalc", "openoffice"));
		list.add(new ExternalFileType("OpenDocument spreadsheet", "ods", "application/vnd.oasis.opendocument.spreadsheet", "oocalc", "openoffice"));
		list.add(new ExternalFileType("PowerPoint", "ppt", "application/vnd.ms-powerpoint", "ooimpress", "openoffice"));
		list.add(new ExternalFileType("PowerPoint 2007+", "pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation", "ooimpress", "openoffice"));
		list.add(new ExternalFileType("OpenDocument presentation", "odp", "application/vnd.oasis.opendocument.presentation", "ooimpress", "openoffice"));
		list.add(new ExternalFileType("Rich Text Format", "rtf", "application/rtf", "oowriter", "openoffice"));
		list.add(new ExternalFileType("PNG image", "png", "image/png", "gimp", "picture"));
		list.add(new ExternalFileType("GIF image", "gif", "image/gif", "gimp", "picture"));
		list.add(new ExternalFileType("JPG image", "jpg", "image/jpeg", "gimp", "picture"));
		list.add(new ExternalFileType("Djvu", "djvu", "", "evince", "psSmall"));
		list.add(new ExternalFileType("Text", "txt", "text/plain", "emacs", "emacs"));
		list.add(new ExternalFileType("LaTeX", "tex", "application/x-latex", "emacs", "emacs"));
		list.add(new ExternalFileType("CHM", "chm", "application/mshelp", "gnochm", "www"));
		list.add(new ExternalFileType("TIFF image", "tiff", "image/tiff", "gimp", "picture"));
		list.add(new ExternalFileType("URL", "html", "text/html", "firefox", "www"));
		list.add(new ExternalFileType("MHT", "mht", "multipart/related", "firefox", "www"));
		list.add(new ExternalFileType("ePUB", "epub", "application/epub+zip", "firefox", "www"));

		// On all OSes there is a generic application available to handle file opening,
		// so we don't need the default application settings anymore:
		for (ExternalFileType type : list) {
			type.setOpenWith("");
		}

		return list;
	}

	public ExternalFileType[] getExternalFileTypeSelection() {
		return externalFileTypes.toArray(new ExternalFileType[externalFileTypes.size()]);
	}

	/**
	 * Look up the external file type registered with this name, if any.
	 *
	 * @param name The file type name.
	 * @return The ExternalFileType registered, or null if none.
	 */
	public ExternalFileType getExternalFileTypeByName(String name) {
		for (ExternalFileType type : externalFileTypes) {
			if (type.getName().equals(name)) {
				return type;
			}
		}
		// Return an instance that signifies an unknown file type:
		return new UnknownExternalFileType(name);
	}

	/**
	 * Look up the external file type registered for this extension, if any.
	 *
	 * @param extension The file extension.
	 * @return The ExternalFileType registered, or null if none.
	 */
	public ExternalFileType getExternalFileTypeByExt(String extension) {
		for (ExternalFileType type : externalFileTypes) {
			if ((type.getExtension() != null) && type.getExtension().equalsIgnoreCase(extension)) {
				return type;
			}
		}
		return null;
	}

	/**
	 * Look up the external file type registered for this filename, if any.
	 *
	 * @param filename The name of the file whose type to look up.
	 * @return The ExternalFileType registered, or null if none.
	 */
	public ExternalFileType getExternalFileTypeForName(String filename) {
		int longestFound = -1;
		ExternalFileType foundType = null;
		for (ExternalFileType type : externalFileTypes) {
			if ((type.getExtension() != null) && filename.toLowerCase().
					endsWith(type.getExtension().toLowerCase())) {
				if (type.getExtension().length() > longestFound) {
					longestFound = type.getExtension().length();
					foundType = type;
				}
			}
		}
		return foundType;
	}

	/**
	 * Look up the external file type registered for this MIME type, if any.
	 *
	 * @param mimeType The MIME type.
	 * @return The ExternalFileType registered, or null if none. For the mime type "text/html", a valid file type is
	 *         guaranteed to be returned.
	 */
	public ExternalFileType getExternalFileTypeByMimeType(String mimeType) {
		for (ExternalFileType type : externalFileTypes) {
			if ((type.getMimeType() != null) && type.getMimeType().equals(mimeType)) {
				return type;
			}
		}
		if (mimeType.equals("text/html")) {
			return HTML_FALLBACK_TYPE;
		} else {
			return null;
		}
	}

	/**
	 * Reset the List of external file types after user customization.
	 *
	 * @param types The new List of external file types. This is the complete list, not just new entries.
	 */
	public void setExternalFileTypes(List<ExternalFileType> types) {

		// First find a list of the default types:
		List<ExternalFileType> defTypes = getDefaultExternalFileTypes();
		// Make a list of types that are unchanged:
		List<ExternalFileType> unchanged = new ArrayList<ExternalFileType>();

		externalFileTypes.clear();
		for (ExternalFileType type : types) {
			externalFileTypes.add(type);

			// See if we can find a type with matching name in the default type list:
			ExternalFileType found = null;
			for (ExternalFileType defType : defTypes) {
				if (defType.getName().equals(type.getName())) {
					found = defType;
					break;
				}
			}
			if (found != null) {
				// Found it! Check if it is an exact match, or if it has been customized:
				if (found.equals(type)) {
					unchanged.add(type);
				} else {
					// It was modified. Remove its entry from the defaults list, since
					// the type hasn't been removed:
					defTypes.remove(found);
				}
			}
		}

		// Go through unchanged types. Remove them from the ones that should be stored,
		// and from the list of defaults, since we don't need to mention these in prefs:
		for (ExternalFileType type : unchanged) {
			defTypes.remove(type);
			types.remove(type);
		}

		// Now set up the array to write to prefs, containing all new types, all modified
		// types, and a flag denoting each default type that has been removed:
		String[][] array = new String[types.size() + defTypes.size()][];
		int i = 0;
		for (ExternalFileType type : types) {
			array[i] = type.getStringArrayRepresentation();
			i++;
		}
		for (ExternalFileType type : defTypes) {
			array[i] = new String[] {type.getName(), JabRefPreferences.FILE_TYPE_REMOVED_FLAG};
			i++;
		}
		//System.out.println("Encoded: '"+Util.encodeStringArray(array)+"'");
		put("externalFileTypes", Util.encodeStringArray(array));
	}

	/**
	 * Set up the list of external file types, either from default values, or from values recorded in Preferences.
	 */
	public void updateExternalFileTypes() {
		// First get a list of the default file types as a starting point:
		List<ExternalFileType> types = getDefaultExternalFileTypes();
		// If no changes have been stored, simply use the defaults:
		if (prefs.get("externalFileTypes", null) == null) {
			externalFileTypes.clear();
			externalFileTypes.addAll(types);
			return;
		}
		// Read the prefs information for file types:
		String[][] vals = Util.decodeStringDoubleArray(prefs.get("externalFileTypes", ""));
		for (String[] val : vals) {
			if ((val.length == 2) && (val[1].equals(JabRefPreferences.FILE_TYPE_REMOVED_FLAG))) {
				// This entry indicates that a default entry type should be removed:
				ExternalFileType toRemove = null;
				for (ExternalFileType type : types) {
					if (type.getName().equals(val[0])) {
						toRemove = type;
						break;
					}
				}
				// If we found it, remove it from the type list:
				if (toRemove != null) {
					types.remove(toRemove);
				}
			} else {
				// A new or modified entry type. Construct it from the string array:
				ExternalFileType type = new ExternalFileType(val);
				// Check if there is a default type with the same name. If so, this is a
				// modification of that type, so remove the default one:
				ExternalFileType toRemove = null;
				for (ExternalFileType defType : types) {
					if (type.getName().equals(defType.getName())) {
						toRemove = defType;
						break;
					}
				}
				// If we found it, remove it from the type list:
				if (toRemove != null) {
					types.remove(toRemove);
				}

				// Then add the new one:
				types.add(type);
			}
		}

		// Finally, build the list of types based on the modified defaults list:
		for (ExternalFileType type : types) {
			externalFileTypes.add(type);
		}
	}

	/**
	 * Removes all information about custom entry types with tags of
	 *
	 * @param number or higher.
	 */
	public void purgeCustomEntryTypes(int number) {
		purgeSeries(JabRefPreferences.CUSTOM_TYPE_NAME, number);
		purgeSeries(JabRefPreferences.CUSTOM_TYPE_REQ, number);
		purgeSeries(JabRefPreferences.CUSTOM_TYPE_OPT, number);
		purgeSeries(JabRefPreferences.CUSTOM_TYPE_PRIOPT, number);
	}

	/**
	 * Removes all entries keyed by prefix+number, where number is equal to or higher than the given number.
	 *
	 * @param number or higher.
	 */
	public void purgeSeries(String prefix, int number) {
		while (get(prefix + number) != null) {
			remove(prefix + number);
			number++;
		}
	}

	public EntryEditorTabList getEntryEditorTabList() {
		if (tabList == null) {
			updateEntryEditorTabList();
		}
		return tabList;
	}

	public void updateEntryEditorTabList() {
		tabList = new EntryEditorTabList();
	}

	/**
	 * Exports Preferences to an XML file.
	 *
	 * @param filename String File to export to
	 */
	public void exportPreferences(String filename) throws IOException {
		File f = new File(filename);
		OutputStream os = new FileOutputStream(f);
		try {
			prefs.exportSubtree(os);
		} catch (BackingStoreException ex) {
			throw new IOException(ex);
		} finally {
			if (os != null) {
				os.close();
			}
		}
	}

	/**
	 * Imports Preferences from an XML file.
	 *
	 * @param filename String File to import from
	 */
	public void importPreferences(String filename) throws IOException {
		File f = new File(filename);
		InputStream is = new FileInputStream(f);
		try {
			Preferences.importPreferences(is);
		} catch (InvalidPreferencesFormatException ex) {
			throw new IOException(ex);
		}
	}

	/**
	 * Determines whether the given field should be written without any sort of wrapping.
	 *
	 * @param fieldName The field name.
	 * @return true if the field should not be wrapped.
	 */
	public boolean isNonWrappableField(String fieldName) {
		return nonWrappableFields.contains(fieldName);
	}

	public int getSize_x() {
		return size_x;
	}

	public int getSize_y() {
		return size_y;
	}

	public static boolean isBib_loc_as_primary_dir() {
		return bib_loc_as_primary_dir;
	}

	public static String getMarkedEntryBackground2() {
		return markedEntryBackground2;
	}

	public static boolean isWarnBeforeOverwritingKey() {
		return warnBeforeOverwritingKey;
	}

	public static void setWarnBeforeOverwritingKey(boolean warnBeforeOverwritingKey) {
		JabRefPreferences.warnBeforeOverwritingKey = warnBeforeOverwritingKey;
	}

	public static boolean isAvoidOverwritingKey() {
		return avoidOverwritingKey;
	}

	public static boolean isMemoryStickMode() {
		return memoryStickMode;
	}

	public static void setMemoryStickMode(boolean memoryStickMode) {
		JabRefPreferences.memoryStickMode = memoryStickMode;
	}

	public static String getSecondary_sort_field() {
		return secondary_sort_field;
	}

	public static void setSecondray_sort_field(String secondary_sort_field) {
		JabRefPreferences.secondary_sort_field = secondary_sort_field;
	}

	public static boolean isAutoAssignGroup() {
		return autoAssignGroup;
	}

	public static void setAutoAssignGroup(boolean autoAssignGroup) {
		JabRefPreferences.autoAssignGroup = autoAssignGroup;
	}

	public static boolean isDisableOnMultipleSelection() {
		return disableOnMultipleSelection;
	}

	public static void setDisableOnMultipleSelection(
			boolean disableOnMultipleSelection) {
		JabRefPreferences.disableOnMultipleSelection = disableOnMultipleSelection;
	}

	public static boolean isGroupShowDynamic() {
		return groupShowDynamic;
	}

	public static void setGroupShowDynamic(boolean groupShowDynamic) {
		JabRefPreferences.groupShowDynamic = groupShowDynamic;
	}

	public static boolean isAutoDoubleBraces() {
		return autoDoubleBraces;
	}

	public static void setAutoDoubleBraces(boolean autoDoubleBraces) {
		JabRefPreferences.autoDoubleBraces = autoDoubleBraces;
	}

	public static boolean isUseTimeStamp() {
		return useTimeStamp;
	}

	public static void setUseTimeStamp(boolean useTimeStamp) {
		JabRefPreferences.useTimeStamp = useTimeStamp;
	}

	public static boolean isExportInSpecifiedOrder() {
		return exportInSpecifiedOrder;
	}

	public static void setExportInSpecifiedOrder(boolean exportInSpecifiedOrder) {
		JabRefPreferences.exportInSpecifiedOrder = exportInSpecifiedOrder;
	}

	public static boolean isAutoComplete() {
		return autoComplete;
	}

	public static void setAutoComplete(boolean autoComplete) {
		JabRefPreferences.autoComplete = autoComplete;
	}

	public static boolean isWindowMaximised() {
		return windowMaximised;
	}

	public static void setWindowMaximised(boolean windowMaximised) {
		JabRefPreferences.windowMaximised = windowMaximised;
	}

	public static boolean isAutoCompLF() {
		return autoCompLF;
	}

	public static void setAutoCompLF(boolean autoCompLF) {
		JabRefPreferences.autoCompLF = autoCompLF;
	}

	public static boolean isNamesLastOnly() {
		return namesLastOnly;
	}

	public static void setNamesLastOnly(boolean namesLastOnly) {
		JabRefPreferences.namesLastOnly = namesLastOnly;
	}

	public static String getSaveTerSort() {
		return saveTerSort;
	}

	public static void setSaveTerSort(String saveTerSort) {
		JabRefPreferences.saveTerSort = saveTerSort;
	}

	public static boolean isAllowTableEditing() {
		return allowTableEditing;
	}

	public static void setAllowTableEditing(boolean allowTableEditing) {
		JabRefPreferences.allowTableEditing = allowTableEditing;
	}

	public static boolean isFloatMarkedEntries() {
		return floatMarkedEntries;
	}

	public static void setFloatMarkedEntries(boolean floatMarkedEntries) {
		JabRefPreferences.floatMarkedEntries = floatMarkedEntries;
	}

	public static String getTimeStampFormat() {
		return timeStampFormat;
	}

	public static void setTimeStampFormat(String timeStampFormat) {
		JabRefPreferences.timeStampFormat = timeStampFormat;
	}

	public static boolean isSaveSecDescending() {
		return saveSecDescending;
	}

	public static void setSaveSecDescending(boolean saveSecDescending) {
		JabRefPreferences.saveSecDescending = saveSecDescending;
	}

	public static String getProxyPort() {
		return proxyPort;
	}

	public static void setProxyPort(String proxyPort) {
		JabRefPreferences.proxyPort = proxyPort;
	}

	public static String getSavePriSort() {
		return savePriSort;
	}

	public static void setSavePriSort(String savePriSort) {
		JabRefPreferences.savePriSort = savePriSort;
	}

	public static boolean isExportInOriginalOrder() {
		return exportInOriginalOrder;
	}

	public static void setExportInOriginalOrder(boolean exportInOriginalOrder) {
		JabRefPreferences.exportInOriginalOrder = exportInOriginalOrder;
	}

	public static String getPriSort() {
		return priSort;
	}

	public static void setPriSort(String priSort) {
		JabRefPreferences.priSort = priSort;
	}

	public static boolean isUseProxy() {
		return useProxy;
	}

	public static void setUseProxy(boolean useProxy) {
		JabRefPreferences.useProxy = useProxy;
	}

	public static boolean isAutolinkExactKeyOnly() {
		return autolinkExactKeyOnly;
	}

	public static void setAutolinkExactKeyOnly(boolean autolinkExactKeyOnly) {
		JabRefPreferences.autolinkExactKeyOnly = autolinkExactKeyOnly;
	}

	public static boolean isTableShowGrid() {
		return tableShowGrid;
	}

	public static void setTableShowGrid(boolean tableShowGrid) {
		JabRefPreferences.tableShowGrid = tableShowGrid;
	}

	public static boolean isNamesAsIs() {
		return namesAsIs;
	}

	public static void setNamesAsIs(boolean namesAsIs) {
		JabRefPreferences.namesAsIs = namesAsIs;
	}

	public static String getLookAndFeel() {
		return lookAndFeel;
	}

	public static void setLookAndFeel(String lookAndFeel) {
		JabRefPreferences.lookAndFeel = lookAndFeel;
	}

	public static boolean isUseUnitFormatterOnSearch() {
		return useUnitFormatterOnSearch;
	}

	public static void setUseUnitFormatterOnSearch(boolean useUnitFormatterOnSearch) {
		JabRefPreferences.useUnitFormatterOnSearch = useUnitFormatterOnSearch;
	}

	public static String getDefaultEncoding() {
		return defaultEncoding;
	}

	public static void setDefaultEncoding(String defaultEncoding) {
		JabRefPreferences.defaultEncoding = defaultEncoding;
	}

	public static int getFontSize() {
		return fontSize;
	}

	public static void setFontSize(int fontSize) {
		JabRefPreferences.fontSize = fontSize;
	}

	public static boolean isAutoOpenForm() {
		return autoOpenForm;
	}

	public static void setAutoOpenForm(boolean autoOpenForm) {
		JabRefPreferences.autoOpenForm = autoOpenForm;
	}

	public static boolean isArxivColumn() {
		return arxivColumn;
	}

	public static void setArxivColumn(boolean arxivColumn) {
		JabRefPreferences.arxivColumn = arxivColumn;
	}

	public static boolean isToolbarVisible() {
		return toolbarVisible;
	}

	public static void setToolbarVisible(boolean toolbarVisible) {
		JabRefPreferences.toolbarVisible = toolbarVisible;
	}

	public static int getAutoResizeMode() {
		return autoResizeMode;
	}

	public static void setAutoResizeMode(int autoResizeMode) {
		JabRefPreferences.autoResizeMode = autoResizeMode;
	}

	public static String getProxyHostname() {
		return proxyHostname;
	}

	public static void setProxyHostname(String proxyHostname) {
		JabRefPreferences.proxyHostname = proxyHostname;
	}

	public static boolean isGroupExpandTree() {
		return groupExpandTree;
	}

	public static void setGroupExpandTree(boolean groupExpandTree) {
		JabRefPreferences.groupExpandTree = groupExpandTree;
	}

	public static int getTableRowPadding() {
		return tableRowPadding;
	}

	public static void setTableRowPadding(int tableRowPadding) {
		JabRefPreferences.tableRowPadding = tableRowPadding;
	}

	public static boolean isBiblatexMode() {
		return biblatexMode;
	}

	public static void setBiblatexMode(boolean biblatexMode) {
		JabRefPreferences.biblatexMode = biblatexMode;
	}

	public static boolean isGroupShowIcons() {
		return groupShowIcons;
	}

	public static void setGroupShowIcons(boolean groupShowIcons) {
		JabRefPreferences.groupShowIcons = groupShowIcons;
	}

	public static boolean isGroupAutoShow() {
		return groupAutoShow;
	}

	public static void setGroupAutoShow(boolean groupAutoShow) {
		JabRefPreferences.groupAutoShow = groupAutoShow;
	}

	public static boolean isUseImportInspectionDialog() {
		return useImportInspectionDialog;
	}

	public static void setUseImportInspectionDialog(
			boolean useImportInspectionDialog) {
		JabRefPreferences.useImportInspectionDialog = useImportInspectionDialog;
	}

	public static boolean isConfirmDelete() {
		return confirmDelete;
	}

	public static void setConfirmDelete(boolean confirmDelete) {
		JabRefPreferences.confirmDelete = confirmDelete;
	}

	public static boolean isUseNativeFileDialogOnMac() {
		return useNativeFileDialogOnMac;
	}

	public static void setUseNativeFileDialogOnMac(boolean useNativeFileDialogOnMac) {
		JabRefPreferences.useNativeFileDialogOnMac = useNativeFileDialogOnMac;
	}

	public static boolean isShowSource() {
		return showSource;
	}

	public static void setShowSource(boolean showSource) {
		JabRefPreferences.showSource = showSource;
	}

	public static String getTimeStampField() {
		return timeStampField;
	}

	public static void setTimeStampField(String timeStampField) {
		JabRefPreferences.timeStampField = timeStampField;
	}

	public static boolean isResolveStringsAllFields() {
		return resolveStringsAllFields;
	}

	public static void setResolveStringsAllFields(boolean resolveStringsAllFields) {
		JabRefPreferences.resolveStringsAllFields = resolveStringsAllFields;
	}

	public static boolean isWriteFieldAddSpaces() {
		return writeFieldAddSpaces;
	}

	public static void setWriteFieldAddSpaces(boolean writeFieldAddSpaces) {
		JabRefPreferences.writeFieldAddSpaces = writeFieldAddSpaces;
	}

	public static boolean isDialogWarningForEmptyKey() {
		return dialogWarningForEmptyKey;
	}

	public static void setDialogWarningForEmptyKey(boolean dialogWarningForEmptyKey) {
		JabRefPreferences.dialogWarningForEmptyKey = dialogWarningForEmptyKey;
	}

	public static boolean isUseOwner() {
		return useOwner;
	}

	public static void setUseOwner(boolean useOwner) {
		JabRefPreferences.useOwner = useOwner;
	}

	public static boolean isAutoSave() {
		return autoSave;
	}

	public static void setAutoSave(boolean autoSave) {
		JabRefPreferences.autoSave = autoSave;
	}

	public static boolean isBackup() {
		return backup;
	}

	public static void setBackup(boolean backup) {
		JabRefPreferences.backup = backup;
	}

	public static boolean isAutoCompFF() {
		return autoCompFF;
	}

	public static void setAutoCompFF(boolean autoCompFF) {
		JabRefPreferences.autoCompFF = autoCompFF;
	}

	public static boolean isIncludeEmptyFields() {
		return includeEmptyFields;
	}

	public static void setIncludeEmptyFields(boolean includeEmptyFields) {
		JabRefPreferences.includeEmptyFields = includeEmptyFields;
	}

	public static boolean isPdfPreview() {
		return pdfPreview;
	}

	public static void setPdfPreview(boolean pdfPreview) {
		JabRefPreferences.pdfPreview = pdfPreview;
	}

	public static boolean isUpdateTimestamp() {
		return updateTimestamp;
	}

	public static void setUpdateTimestamp(boolean updateTimestamp) {
		JabRefPreferences.updateTimestamp = updateTimestamp;
	}

	public static String getFontFamily() {
		return fontFamily;
	}

	public static void setFontFamily(String fontFamily) {
		JabRefPreferences.fontFamily = fontFamily;
	}

	public static boolean isWrapFieldLine() {
		return wrapFieldLine;
	}

	public static void setWrapFieldLine(boolean wrapFieldLine) {
		JabRefPreferences.wrapFieldLine = wrapFieldLine;
	}

	public static boolean isMarkImportedEntries() {
		return markImportedEntries;
	}

	public static void setMarkImportedEntries(boolean markImportedEntries) {
		JabRefPreferences.markImportedEntries = markImportedEntries;
	}

	public static boolean isSecDescending() {
		return secDescending;
	}

	public static void setSecDescending(boolean secDescending) {
		JabRefPreferences.secDescending = secDescending;
	}

	public static boolean isUseIEEEAbrv() {
		return useIEEEAbrv;
	}

	public static void setUseIEEEAbrv(boolean useIEEEAbrv) {
		JabRefPreferences.useIEEEAbrv = useIEEEAbrv;
	}

	public static boolean isUnmarkAllEntriesBeforeImporting() {
		return unmarkAllEntriesBeforeImporting;
	}

	public static void setUnmarkAllEntriesBeforeImporting(
			boolean unmarkAllEntriesBeforeImporting) {
		JabRefPreferences.unmarkAllEntriesBeforeImporting = unmarkAllEntriesBeforeImporting;
	}

	public static String getSaveSecSort() {
		return saveSecSort;
	}

	public static void setSaveSecSort(String saveSecSort) {
		JabRefPreferences.saveSecSort = saveSecSort;
	}

	public static String getExportSecSort() {
		return exportSecSort;
	}

	public static void setExportSecSort(String exportSecSort) {
		JabRefPreferences.exportSecSort = exportSecSort;
	}

	public static boolean isSearchPanelVisible() {
		return searchPanelVisible;
	}

	public static void setSearchPanelVisible(boolean searchPanelVisible) {
		JabRefPreferences.searchPanelVisible = searchPanelVisible;
	}

	public static boolean isOpenFoldersOfAttachedFiles() {
		return openFoldersOfAttachedFiles;
	}

	public static void setOpenFoldersOfAttachedFiles(
			boolean openFoldersOfAttachedFiles) {
		JabRefPreferences.openFoldersOfAttachedFiles = openFoldersOfAttachedFiles;
	}

	public static boolean isShowOneLetterHeadingForIconColumns() {
		return showOneLetterHeadingForIconColumns;
	}

	public static void setShowOneLetterHeadingForIconColumns(
			boolean showOneLetterHeadingForIconColumns) {
		JabRefPreferences.showOneLetterHeadingForIconColumns = showOneLetterHeadingForIconColumns;
	}

	public static boolean isUrlColumn() {
		return urlColumn;
	}

	public static void setUrlColumn(boolean urlColumn) {
		JabRefPreferences.urlColumn = urlColumn;
	}

	public static String getEmailSubject() {
		return emailSubject;
	}

	public static void setEmailSubject(String emailSubject) {
		JabRefPreferences.emailSubject = emailSubject;
	}

	public static boolean isRunAutomaticFileSearch() {
		return runAutomaticFileSearch;
	}

	public static void setRunAutomaticFileSearch(boolean runAutomaticFileSearch) {
		JabRefPreferences.runAutomaticFileSearch = runAutomaticFileSearch;
	}

	public static boolean isEnforceLegalBibtexKey() {
		return enforceLegalBibtexKey;
	}

	public static void setEnforceLegalBibtexKey(boolean enforceLegalBibtexKey) {
		JabRefPreferences.enforceLegalBibtexKey = enforceLegalBibtexKey;
	}

	public static boolean isUseImportInspectionDialogForSingle() {
		return useImportInspectionDialogForSingle;
	}

	public static void setUseImportInspectionDialogForSingle(
			boolean useImportInspectionDialogForSingle) {
		JabRefPreferences.useImportInspectionDialogForSingle = useImportInspectionDialogForSingle;
	}

	public static String getNewline() {
		return newline;
	}

	public static void setNewline(String newline) {
		JabRefPreferences.newline = newline;
	}

	public static boolean isNamesFf() {
		return namesFf;
	}

	public static void setNamesFf(boolean namesFf) {
		JabRefPreferences.namesFf = namesFf;
	}

	public static boolean isExportPriDescending() {
		return exportPriDescending;
	}

	public static void setExportPriDescending(boolean exportPriDescending) {
		JabRefPreferences.exportPriDescending = exportPriDescending;
	}

	public static String getMarkedEntryBackground5() {
		return markedEntryBackground5;
	}

	public static void setMarkedEntryBackground5(String markedEntryBackground5) {
		JabRefPreferences.markedEntryBackground5 = markedEntryBackground5;
	}

	public static String getExportPriSort() {
		return exportPriSort;
	}

	public static void setExportPriSort(String exportPriSort) {
		JabRefPreferences.exportPriSort = exportPriSort;
	}

	public static boolean isSaveInOriginalOrder() {
		return saveInOriginalOrder;
	}

	public static void setSaveInOriginalOrder(boolean saveInOriginalOrder) {
		JabRefPreferences.saveInOriginalOrder = saveInOriginalOrder;
	}

	public static boolean isAllowFileAutoOpenBrowse() {
		return allowFileAutoOpenBrowse;
	}

	public static void setAllowFileAutoOpenBrowse(boolean allowFileAutoOpenBrowse) {
		JabRefPreferences.allowFileAutoOpenBrowse = allowFileAutoOpenBrowse;
	}

	public static boolean isUseXmpPrivacyFilter() {
		return useXmpPrivacyFilter;
	}

	public static void setUseXmpPrivacyFilter(boolean useXmpPrivacyFilter) {
		JabRefPreferences.useXmpPrivacyFilter = useXmpPrivacyFilter;
	}

	public static boolean isOverwriteOwner() {
		return overwriteOwner;
	}

	public static void setOverwriteOwner(boolean overwriteOwner) {
		JabRefPreferences.overwriteOwner = overwriteOwner;
	}

	public static boolean isUseRegExpSearch() {
		return useRegExpSearch;
	}

	public static void setUseRegExpSearch(boolean useRegExpSearch) {
		JabRefPreferences.useRegExpSearch = useRegExpSearch;
	}

	public static boolean isFilechooserDisableRename() {
		return filechooserDisableRename;
	}

	public static void setFilechooserDisableRename(boolean filechooserDisableRename) {
		JabRefPreferences.filechooserDisableRename = filechooserDisableRename;
	}

	public static int getPosX() {
		return posX;
	}

	public static void setPosX(int posX) {
		JabRefPreferences.posX = posX;
	}

	public static boolean isTertiarySortDescending() {
		return tertiarySortDescending;
	}

	public static void setTertiarySortDescending(boolean tertiarySortDescending) {
		JabRefPreferences.tertiarySortDescending = tertiarySortDescending;
	}

	public static boolean isPromptBeforeUsingAutosave() {
		return promptBeforeUsingAutosave;
	}

	public static void setPromptBeforeUsingAutosave(
			boolean promptBeforeUsingAutosave) {
		JabRefPreferences.promptBeforeUsingAutosave = promptBeforeUsingAutosave;
	}

	public static boolean isGroupAutoHide() {
		return groupAutoHide;
	}

	public static void setGroupAutoHide(boolean groupAutoHide) {
		JabRefPreferences.groupAutoHide = groupAutoHide;
	}

	public static boolean isPriDescending() {
		return priDescending;
	}

	public static void setPriDescending(boolean priDescending) {
		JabRefPreferences.priDescending = priDescending;
	}

	public static boolean isSaveInSpecifiedOrder() {
		return saveInSpecifiedOrder;
	}

	public static void setSaveInSpecifiedOrder(boolean saveInSpecifiedOrder) {
		JabRefPreferences.saveInSpecifiedOrder = saveInSpecifiedOrder;
	}

	public static boolean isExtraFileColumns() {
		return extraFileColumns;
	}

	public static void setExtraFileColumns(boolean extraFileColumns) {
		JabRefPreferences.extraFileColumns = extraFileColumns;
	}

	public static boolean isPdfColumn() {
		return pdfColumn;
	}

	public static void setPdfColumn(boolean pdfColumn) {
		JabRefPreferences.pdfColumn = pdfColumn;
	}

	public static int getPosY() {
		return posY;
	}

	public static void setPosY(int posY) {
		JabRefPreferences.posY = posY;
	}

	public static boolean isNamesNatbib() {
		return namesNatbib;
	}

	public static void setNamesNatbib(boolean namesNatbib) {
		JabRefPreferences.namesNatbib = namesNatbib;
	}

	public static boolean isKeyGenAlwaysAddLetter() {
		return keyGenAlwaysAddLetter;
	}

	public static void setKeyGenAlwaysAddLetter(boolean keyGenAlwaysAddLetter) {
		JabRefPreferences.keyGenAlwaysAddLetter = keyGenAlwaysAddLetter;
	}

	public static boolean isUseConvertToEquation() {
		return useConvertToEquation;
	}

	public static void setUseConvertToEquation(boolean useConvertToEquation) {
		JabRefPreferences.useConvertToEquation = useConvertToEquation;
	}

	public static String getTerSort() {
		return terSort;
	}

	public static void setTerSort(String terSort) {
		JabRefPreferences.terSort = terSort;
	}

	public static boolean isBibLocationAsFileDir() {
		return bibLocationAsFileDir;
	}

	public static void setBibLocationAsFileDir(boolean bibLocationAsFileDir) {
		JabRefPreferences.bibLocationAsFileDir = bibLocationAsFileDir;
	}

	public static boolean isOverwriteTimeStamp() {
		return overwriteTimeStamp;
	}

	public static void setOverwriteTimeStamp(boolean overwriteTimeStamp) {
		JabRefPreferences.overwriteTimeStamp = overwriteTimeStamp;
	}

	public static boolean isWriteFieldCamelCase() {
		return writeFieldCamelCase;
	}

	public static void setWriteFieldCamelCase(boolean writeFieldCamelCase) {
		JabRefPreferences.writeFieldCamelCase = writeFieldCamelCase;
	}

	public static boolean isOpenLastEdited() {
		return openLastEdited;
	}

	public static void setOpenLastEdited(boolean openLastEdited) {
		JabRefPreferences.openLastEdited = openLastEdited;
	}

	public static boolean isUseDefaultLookAndFeel() {
		return useDefaultLookAndFeel;
	}

	public static void setUseDefaultLookAndFeel(boolean useDefaultLookAndFeel) {
		JabRefPreferences.useDefaultLookAndFeel = useDefaultLookAndFeel;
	}

	public static boolean isPreferUrlDoi() {
		return preferUrlDoi;
	}

	public static void setPreferUrlDoi(boolean preferUrlDoi) {
		JabRefPreferences.preferUrlDoi = preferUrlDoi;
	}

	public static int getShortestToComplete() {
		return shortestToComplete;
	}

	public static void setShortestToComplete(int shortestToComplete) {
		JabRefPreferences.shortestToComplete = shortestToComplete;
	}

	public static boolean isOverrideDefaultFonts() {
		return overrideDefaultFonts;
	}

	public static void setOverrideDefaultFonts(boolean overrideDefaultFonts) {
		JabRefPreferences.overrideDefaultFonts = overrideDefaultFonts;
	}

	public static boolean isUseCaseKeeperOnSearch() {
		return useCaseKeeperOnSearch;
	}

	public static void setUseCaseKeeperOnSearch(boolean useCaseKeeperOnSearch) {
		JabRefPreferences.useCaseKeeperOnSearch = useCaseKeeperOnSearch;
	}

	public static int getAutoSaveInterval() {
		return autoSaveInterval;
	}

	public static void setAutoSaveInterval(int autoSaveInterval) {
		JabRefPreferences.autoSaveInterval = autoSaveInterval;
	}

	public static int getWritefieldSortStyle() {
		return writefieldSortStyle;
	}

	public static void setWritefieldSortStyle(int writefieldSortStyle) {
		JabRefPreferences.writefieldSortStyle = writefieldSortStyle;
	}

	public static boolean isDialogWarningForDuplicateKey() {
		return dialogWarningForDuplicateKey;
	}

	public static void setDialogWarningForDuplicateKey(
			boolean dialogWarningForDuplicateKey) {
		JabRefPreferences.dialogWarningForDuplicateKey = dialogWarningForDuplicateKey;
	}

	public static String getMarkedEntryBackground1() {
		return markedEntryBackground1;
	}

	public static void setMarkedEntryBackground1(String markedEntryBackground1) {
		JabRefPreferences.markedEntryBackground1 = markedEntryBackground1;
	}

	public static boolean isGenerateKeysAfterInspection() {
		return generateKeysAfterInspection;
	}

	public static void setGenerateKeysAfterInspection(
			boolean generateKeysAfterInspection) {
		JabRefPreferences.generateKeysAfterInspection = generateKeysAfterInspection;
	}

	public static boolean isCtrlClick() {
		return ctrlClick;
	}

	public static void setCtrlClick(boolean ctrlClick) {
		JabRefPreferences.ctrlClick = ctrlClick;
	}

	public static boolean isAbbrAuthorNames() {
		return abbrAuthorNames;
	}

	public static void setAbbrAuthorNames(boolean abbrAuthorNames) {
		JabRefPreferences.abbrAuthorNames = abbrAuthorNames;
	}

	public static boolean isExportTerDescending() {
		return exportTerDescending;
	}

	public static void setExportTerDescending(boolean exportTerDescending) {
		JabRefPreferences.exportTerDescending = exportTerDescending;
	}

	public static boolean isFileColumn() {
		return fileColumn;
	}

	public static void setFileColumn(boolean fileColumn) {
		JabRefPreferences.fileColumn = fileColumn;
	}

	public static String getExportTerSort() {
		return exportTerSort;
	}

	public static void setExportTerSort(String exportTerSort) {
		JabRefPreferences.exportTerSort = exportTerSort;
	}

	public static boolean isWarnAboutDuplicatesInInspection() {
		return warnAboutDuplicatesInInspection;
	}

	public static void setWarnAboutDuplicatesInInspection(
			boolean warnAboutDuplicatesInInspection) {
		JabRefPreferences.warnAboutDuplicatesInInspection = warnAboutDuplicatesInInspection;
	}

	public static boolean isKeyGenFirstLetterA() {
		return keyGenFirstLetterA;
	}

	public static void setKeyGenFirstLetterA(boolean keyGenFirstLetterA) {
		JabRefPreferences.keyGenFirstLetterA = keyGenFirstLetterA;
	}

	public static boolean isGenerateKeysBeforeSaving() {
		return generateKeysBeforeSaving;
	}

	public static void setGenerateKeysBeforeSaving(boolean generateKeysBeforeSaving) {
		JabRefPreferences.generateKeysBeforeSaving = generateKeysBeforeSaving;
	}

	public static boolean isTableColorCodesOn() {
		return tableColorCodesOn;
	}

	public static void setTableColorCodesOn(boolean tableColorCodesOn) {
		JabRefPreferences.tableColorCodesOn = tableColorCodesOn;
	}

	public static String getAutoCompFirstNameMode() {
		return autoCompFirstNameMode;
	}

	public static void setAutoCompFirstNameMode(String autoCompFirstNameMode) {
		JabRefPreferences.autoCompFirstNameMode = autoCompFirstNameMode;
	}

	public static int getFontStyle() {
		return fontStyle;
	}

	public static void setFontStyle(int fontStyle) {
		JabRefPreferences.fontStyle = fontStyle;
	}

	public static boolean isSaveTerDescending() {
		return saveTerDescending;
	}

	public static void setSaveTerDescending(boolean saveTerDescending) {
		JabRefPreferences.saveTerDescending = saveTerDescending;
	}

	public static boolean isSavePriDescending() {
		return savePriDescending;
	}

	public static void setSavePriDescending(boolean savePriDescending) {
		JabRefPreferences.savePriDescending = savePriDescending;
	}

	public static boolean isExportSecDescending() {
		return exportSecDescending;
	}

	public static void setExportSecDescending(boolean exportSecDescending) {
		JabRefPreferences.exportSecDescending = exportSecDescending;
	}

	public static String getDoNotResolveStringsFor() {
		return doNotResolveStringsFor;
	}

	public static void setDoNotResolveStringsFor(String doNotResolveStringsFor) {
		JabRefPreferences.doNotResolveStringsFor = doNotResolveStringsFor;
	}

	public static String getGroupKeywordSeparator() {
		return groupKeywordSeparator;
	}

	public static void setGroupKeywordSeparator(String groupKeywordSeparator) {
		JabRefPreferences.groupKeywordSeparator = groupKeywordSeparator;
	}

	public static String getTableOptFieldBackground() {
		return tableOptFieldBackground;
	}

	public static void setTableOptFieldBackground(
			String tableOptFieldBackground) {
		JabRefPreferences.tableOptFieldBackground = tableOptFieldBackground;
	}

	public static String getGroupsDefaultField() {
		return groupsDefaultField;
	}

	public static void setGroupsDefaultField(String groupsDefaultField) {
		JabRefPreferences.groupsDefaultField = groupsDefaultField;
	}

	public static String getMarkedEntryBackground0() {
		return markedEntryBackground0;
	}

	public static void setMarkedEntryBackground0(String markedEntryBackground0) {
		JabRefPreferences.markedEntryBackground0 = markedEntryBackground0;
	}

	public static String getInvalidFieldBackgroundColor() {
		return invalidFieldBackgroundColor;
	}

	public static void setInvalidFieldBackgroundColor(
			String invalidFieldBackgroundColor) {
		JabRefPreferences.invalidFieldBackgroundColor = invalidFieldBackgroundColor;
	}

	public static String getGridColor() {
		return gridColor;
	}

	public static void setGridColor(String gridColor) {
		JabRefPreferences.gridColor = gridColor;
	}

	public static String getValidFieldBackgroundColor() {
		return validFieldBackgroundColor;
	}

	public static void setValidFieldBackgroundColor(
			String validFieldBackgroundColor) {
		JabRefPreferences.validFieldBackgroundColor = validFieldBackgroundColor;
	}

	public static String getTableBackground() {
		return tableBackground;
	}

	public static void setTableBackground(String tableBackground) {
		JabRefPreferences.tableBackground = tableBackground;
	}

	public static String getFieldEditorTextColor() {
		return fieldEditorTextColor;
	}

	public static void setFieldEditorTextColor(String fieldEditorTextColor) {
		JabRefPreferences.fieldEditorTextColor = fieldEditorTextColor;
	}

	public static String getTableText() {
		return tableText;
	}

	public static void setTableText(String tableText) {
		JabRefPreferences.tableText = tableText;
	}

	public static String getAutoCompleteFields() {
		return autoCompleteFields;
	}

	public static void setAutoCompleteFields(String autoCompleteFields) {
		JabRefPreferences.autoCompleteFields = autoCompleteFields;
	}

	public static String getMarkedEntryBackground4() {
		return markedEntryBackground4;
	}

	public static void setMarkedEntryBackground4(String markedEntryBackground4) {
		JabRefPreferences.markedEntryBackground4 = markedEntryBackground4;
	}

	public static String getListOfFileColumns() {
		return listOfFileColumns;
	}

	public static void setListOfFileColumns(String listOfFileColumns) {
		JabRefPreferences.listOfFileColumns = listOfFileColumns;
	}

	public static String getTableReqFieldBackground() {
		return tableReqFieldBackground;
	}

	public static void setTableReqFieldBackground(
			String tableReqFieldBackground) {
		JabRefPreferences.tableReqFieldBackground = tableReqFieldBackground;
	}

	public static String getActiveFieldEditorBackgroundColor() {
		return activeFieldEditorBackgroundColor;
	}

	public static void setActiveFieldEditorBackgroundColor(
			String activeFieldEditorBackgroundColor) {
		JabRefPreferences.activeFieldEditorBackgroundColor = activeFieldEditorBackgroundColor;
	}

	public static String getWritefieldUserdefinedOrder() {
		return writefieldUserdefinedOrder;
	}

	public static void setWritefieldUserdefinedOrder(
			String writefieldUserdefinedOrder) {
		JabRefPreferences.writefieldUserdefinedOrder = writefieldUserdefinedOrder;
	}

	public static String getDefaultLabelPattern() {
		return defaultLabelPattern;
	}

	public static void setDefaultLabelPattern(String defaultLabelPattern) {
		JabRefPreferences.defaultLabelPattern = defaultLabelPattern;
	}

	public static String getIncompleteEntryBackground() {
		return incompleteEntryBackground;
	}

	public static void setIncompleteEntryBackground(
			String incompleteEntryBackground) {
		JabRefPreferences.incompleteEntryBackground = incompleteEntryBackground;
	}

	public static String getMarkedEntryBackground3() {
		return markedEntryBackground3;
	}

	public static void setMarkedEntryBackground3(String markedEntryBackground3) {
		JabRefPreferences.markedEntryBackground3 = markedEntryBackground3;
	}

	public static int getValueDelimiters2() {
		return valueDelimiters;
	}

	public static void setValueDelimiters2(int valueDelimiters) {
		JabRefPreferences.valueDelimiters = valueDelimiters;
	}

	public static String getEmptyField() {
		return emptyField;
	}
}
