/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s):
 *
 * Portions Copyrighted 2009 Sun Microsystems, Inc.
 */

package org.netbeans.modules.profiler;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.netbeans.lib.profiler.client.AppStatusHandler;
import org.netbeans.lib.profiler.common.Profiler;
import org.netbeans.lib.profiler.results.ExportDataDumper;
import org.netbeans.modules.profiler.api.icons.GeneralIcons;
import org.netbeans.modules.profiler.api.icons.Icons;
import org.netbeans.modules.profiler.api.ProfilerDialogs;
import org.netbeans.modules.profiler.utilities.ProfilerUtils;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 *
 * @author cyhelsky
 */
public final class ExportAction extends AbstractAction {
    private static final Logger LOGGER = Logger.getLogger(ExportAction.class.getName());
    
//~ Inner Interfaces ---------------------------------------------------------------------------------------------------------

    public static interface ExportProvider {
        //~ Methods --------------------------------------------------------------------------------------------------------------

        public void exportData(int exportedFileType, ExportDataDumper eDD);

        public String getViewName();

        public boolean hasExportableView();

        public boolean hasLoadedSnapshot();
        
    }

    //~ Inner Classes ------------------------------------------------------------------------------------------------------------


    private static class SelectedFile {
        //~ Instance fields ------------------------------------------------------------------------------------------------------

        File folder;
        String fileExt;
        String fileName;

        //~ Constructors ---------------------------------------------------------------------------------------------------------

        SelectedFile(File folder, String fileName, String fileExt) {
            this.folder = folder;
            this.fileName = fileName;
            this.fileExt = fileExt;
        }

        //~ Methods --------------------------------------------------------------------------------------------------------------

        File getSelectedFile() {
            return new File(folder + File.separator + fileName+ "." + fileExt);
        }
    }

    //~ Static fields/initializers -----------------------------------------------------------------------------------------------

    // -----
    // I18N String constants
    private static final String EXPORT_ACTION_NAME = NbBundle.getMessage(ExportAction.class, "ExportAction_ExportActionName"); //NOI18N
    private static final String EXPORT_ACTION_DESCRIPTION = NbBundle.getMessage(ExportAction.class, "ExportAction_ExportActionDescr"); //NOI18N
    private static final String OVERWRITE_FILE_CAPTION = NbBundle.getMessage(ExportAction.class, "ExportAction_OverwriteFileCaption"); //NOI18N
    private static final String OVERWRITE_FILE_MSG = NbBundle.getMessage(ExportAction.class, "ExportAction_OverwriteFileMsg"); //NOI18N
    private static final String INVALID_LOCATION_FOR_FILE_MSG = NbBundle.getMessage(ExportAction.class, "ExportAction_InvalidLocationForFileMsg"); //NOI18N
    private static final String CANNOT_OVERWRITE_FILE_MSG = NbBundle.getMessage(ExportAction.class, "ExportAction_CannotOverwriteFileMsg"); //NOI18N
    private static final String EXPORT_DIALOG_TITLE = NbBundle.getMessage(ExportAction.class, "ExportAction_ExportDialogTitle"); //NOI18N
    private static final String EXPORT_DIALOG_BUTTON = NbBundle.getMessage(ExportAction.class, "ExportAction_ExportDialogButton"); //NOI18N
    private static final String EXPORT_DIALOG_CSV_FILTER = NbBundle.getMessage(ExportAction.class, "ExportAction_ExportDialogCSVFilter"); //NOI18N
    private static final String EXPORT_DIALOG_EXCEL_FILTER = NbBundle.getMessage(ExportAction.class, "ExportAction_ExportDialogExcelFilter"); //NOI18N
    private static final String EXPORT_DIALOG_XML_FILTER = NbBundle.getMessage(ExportAction.class, "ExportAction_ExportDialogXMLFilter"); //NOI18N
    private static final String EXPORT_DIALOG_HTML_FILTER = NbBundle.getMessage(ExportAction.class, "ExportAction_ExportDialogHTMLFilter"); //NOI18N
    private static final String EXPORT_DIALOG_NPS_FILTER = NbBundle.getMessage(ExportAction.class, "ExportAction_ExportDialogNPSFilter"); //NOI18N
    private static final String NO_VIEW_MSG = NbBundle.getMessage(ExportAction.class, "ExportAction_NoViewMsg"); //NOI18N
    private static final String EXPORTING_VIEW_MSG = NbBundle.getMessage(ExportAction.class, "ExportAction_ExportingViewMsg"); //NOI18N
    private static final String OOME_EXPORTING_MSG = NbBundle.getMessage(ExportAction.class, "ExportAction_OomeExportingMsg"); //NOI18N
    private static final String IOEXCEPTION_EXPORTING_MSG = NbBundle.getMessage(ExportAction.class, "ExportAction_IOException_Exporting_Msg"); //NOI18N
    private static final String SNAPSHOT_CREATE_FAILED_MSG = NbBundle.getMessage(ResultsManager.class,"ResultsManager_SnapshotCreateFailedMsg"); // NOI18N
    private static final Icon ICON = Icons.getIcon(GeneralIcons.EXPORT);
    private static final String FILE_EXTENSION_CSV = "csv"; // NOI18N
    private static final String FILE_EXTENSION_XML = "xml"; // NOI18N
    private static final String FILE_EXTENSION_HTML = "html"; // NOI18N
    private static final String FILE_EXTENSION_NPS = "nps"; // NOI18N
    public static final int MODE_CSV = 1;
    public static final int MODE_EXCEL = 2;
    public static final int MODE_XML = 3;
    public static final int MODE_HTML = 4;
    public static final int MODE_NPS = 5;
    private static File exportDir;


    //~ Instance fields ----------------------------------------------------------------------------------------------------------

    private JFileChooser fileChooser;
    private ExportProvider exportProvider;
    private int exportedFileType;
    private LoadedSnapshot snapshot;

    //~ Constructors -------------------------------------------------------------------------------------------------------------

    public ExportAction(ExportProvider exportProvider, LoadedSnapshot loadedSnapshot) {
        putValue(Action.NAME, EXPORT_ACTION_NAME);
        putValue(Action.SHORT_DESCRIPTION, EXPORT_ACTION_DESCRIPTION);
        putValue(Action.SMALL_ICON, ICON);
        putValue("iconBase", Icons.getResource(GeneralIcons.EXPORT)); // NOI18N
        this.exportProvider = exportProvider;
        if (!(loadedSnapshot==null)) {
            this.snapshot=loadedSnapshot;
        }
    }

    //~ Methods ------------------------------------------------------------------------------------------------------------------
    private void setFilters() {
        fileChooser.removeChoosableFileFilter(fileChooser.getAcceptAllFileFilter());
        if (exportProvider.hasExportableView()) {
            fileChooser.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(FILE_EXTENSION_XML);
                }

                @Override
                public String getDescription() {
                    return EXPORT_DIALOG_XML_FILTER;
                }
            });
            fileChooser.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(FILE_EXTENSION_HTML);
                }

                @Override
                public String getDescription() {
                    return EXPORT_DIALOG_HTML_FILTER;
                }
            });
            fileChooser.addChoosableFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(FILE_EXTENSION_CSV);
                }

                @Override
                public String getDescription() {
                    return EXPORT_DIALOG_EXCEL_FILTER;
                }
            });
            fileChooser.addChoosableFileFilter(new FileFilter() {

                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(FILE_EXTENSION_CSV);
                }

                @Override
                public String getDescription() {
                    return EXPORT_DIALOG_CSV_FILTER;
                }
            });
        }
        if (exportProvider.hasLoadedSnapshot()) {
            fileChooser.addChoosableFileFilter(new FileFilter() {

                @Override
                public boolean accept(File f) {
                    return f.isDirectory() || f.getName().toLowerCase().endsWith(FILE_EXTENSION_NPS);
                }

                @Override
                public String getDescription() {
                    return EXPORT_DIALOG_NPS_FILTER;
                }
            });
            // If there is snapshot, .nps must be selected as file filter
            FileFilter[] currentFilters = fileChooser.getChoosableFileFilters();
            for (int i = 0; i < currentFilters.length; i++) {
                if (currentFilters[i].getDescription().equals(EXPORT_DIALOG_NPS_FILTER)) {
                    fileChooser.setFileFilter(currentFilters[i]);
                }
            }
        }
    }

    private JFileChooser getFileChooser() {
        if (fileChooser == null) {
            // File chooser
            fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.setMultiSelectionEnabled(false);
            fileChooser.setDialogTitle(EXPORT_DIALOG_TITLE);
            fileChooser.setApproveButtonText(EXPORT_DIALOG_BUTTON);
        }
        fileChooser.resetChoosableFileFilters();
        setFilters();
        return fileChooser;
    }

    private boolean checkFileExists(File file) {
        if (file.exists()) {
            if (!ProfilerDialogs.displayConfirmation(MessageFormat.format(
                    OVERWRITE_FILE_CAPTION,new Object[] { file.getName() }),
                    OVERWRITE_FILE_CAPTION))
                return false; // cancelled by the user

            if (!file.delete()) {
                ProfilerDialogs.displayError(MessageFormat.format(CANNOT_OVERWRITE_FILE_MSG, new Object[] { file.getName() }));
                return false; // Insufficient rights to overwrite file
            }
        }

        return true;
    }

    private SelectedFile selectExportTargetFile(final ExportProvider exportProvider) {
        File targetDir;
        String targetName;
        String defaultName = exportProvider.getViewName();

        // 1. let the user choose file or directory
        final JFileChooser chooser = getFileChooser();
        if (exportDir != null) {
            chooser.setCurrentDirectory(exportDir);
        }
        int result = chooser.showSaveDialog(WindowManager.getDefault().getMainWindow());
        if (result != JFileChooser.APPROVE_OPTION) {
            return null; // cancelled by the user
        }

        // 2. process both cases and extract file name and extension to use and set exported file type
        File file = chooser.getSelectedFile();
        String targetExt;
        if (chooser.getFileFilter().getDescription().equals(EXPORT_DIALOG_XML_FILTER)) {
            targetExt=FILE_EXTENSION_XML;
            exportedFileType=MODE_XML;
        } else if (chooser.getFileFilter().getDescription().equals(EXPORT_DIALOG_HTML_FILTER)) {
            targetExt=FILE_EXTENSION_HTML;
            exportedFileType=MODE_HTML;
        } else if (chooser.getFileFilter().getDescription().equals(EXPORT_DIALOG_EXCEL_FILTER)) {
            targetExt = FILE_EXTENSION_CSV;
            exportedFileType=MODE_EXCEL;
        } else if (chooser.getFileFilter().getDescription().equals(EXPORT_DIALOG_NPS_FILTER)) {
            targetExt = FILE_EXTENSION_NPS;
            exportedFileType=MODE_NPS;
        } else { // CSV is default
            targetExt = FILE_EXTENSION_CSV;
            exportedFileType=MODE_CSV;
        }

        exportDir = chooser.getCurrentDirectory();
        if (file.isDirectory()) { // save to selected directory under default name
            targetDir = file;
            targetName = defaultName;
        } else { // save to selected file
            targetDir = exportDir;
            String fName = file.getName();

            // divide the file name into name and extension
            int idx = fName.lastIndexOf("."); // NOI18N

            if (idx == -1) { // no extension
                targetName = fName; // extension from source file
            } else { // extension exists
                targetName = fName.substring(0, idx);
                targetExt = fName.substring(idx + 1);
            }
        }

        // 3. set type of exported file and return a newly created FileObject

        return new SelectedFile(targetDir, targetName, targetExt);
    }



    public void actionPerformed(ActionEvent evt) {
        if (!exportProvider.hasExportableView() && !exportProvider.hasLoadedSnapshot()) { // nothing to export
            ProfilerDialogs.displayError(NO_VIEW_MSG);
            return;
        }

        final LiveResultsWindow lrw = (exportProvider instanceof LiveResultsWindow) ? (LiveResultsWindow) exportProvider : null;
        final AppStatusHandler statusHandler = Profiler.getDefault().getTargetAppRunner().getAppStatusHandler();

        if (lrw != null) {
            statusHandler.pauseLiveUpdates();
        }

        SelectedFile saveFile = selectExportTargetFile(exportProvider);

        if (saveFile == null) {
            if (lrw != null) {
                statusHandler.resumeLiveUpdates();
            }
            return; // cancelled
        }

        if (exportedFileType==MODE_NPS) {
            final File file = saveFile.getSelectedFile();
            if (!checkFileExists(file)) {
                return; // user doesn't want to overwrite existing file or it can't be overwritten
            }
            try {
                FileObject fo = FileUtil.toFileObject(FileUtil.normalizeFile(saveFile.folder));
                if (fo==null) {
                    ProfilerDialogs.displayError(INVALID_LOCATION_FOR_FILE_MSG);
                    return;
                }
                fo.createData(saveFile.fileName, saveFile.fileExt);
                saveFile=null;
                ResultsManager.getDefault().saveSnapshot(snapshot, fo);
            } catch (IOException e1) {
                LOGGER.log(Level.SEVERE, MessageFormat.format(SNAPSHOT_CREATE_FAILED_MSG, new Object[] { e1.getMessage() }), e1);
            }
        } else {
            final File file = saveFile.getSelectedFile();
            saveFile = null;

            if (!checkFileExists(file)) {
                if (lrw != null) {
                    statusHandler.resumeLiveUpdates();
                }
                return; // user doesn't want to overwrite existing file or it can't be overwritten
            }

            ProfilerUtils.runInProfilerRequestProcessor(new Runnable() {
                    public void run() {
                        ProgressHandle pHandle = null;
                        pHandle = ProgressHandleFactory.createHandle(EXPORTING_VIEW_MSG);
                        pHandle.setInitialDelay(500);
                        pHandle.start();

                        try {
                            FileOutputStream fo;
                            fo = new FileOutputStream(file);
                            ExportDataDumper eDD = new ExportDataDumper(fo);
                            exportProvider.exportData(exportedFileType, eDD);
                            if (eDD.getCaughtException()!=null) {
                                ProfilerDialogs.displayError(eDD.getNumExceptions()+IOEXCEPTION_EXPORTING_MSG);
                            }
                        } catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                        } catch (OutOfMemoryError e) {
                            ProfilerDialogs.displayError(OOME_EXPORTING_MSG+e.getMessage());
                        } finally {
                            if (pHandle != null) {
                                pHandle.finish();
                            }
                            if (lrw != null) {
                                statusHandler.resumeLiveUpdates();
                            }
                        }
                    }
                });
        }
    }
}
