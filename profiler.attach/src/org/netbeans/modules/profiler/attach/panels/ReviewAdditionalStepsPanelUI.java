/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
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
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.profiler.attach.panels;

import java.text.MessageFormat;

/**
 *
 * @author  Jaroslav Bachorik
 */
public class ReviewAdditionalStepsPanelUI extends javax.swing.JPanel {
  private final String checkAutomationText = java.util.ResourceBundle.getBundle("org/netbeans/modules/profiler/attach/wizard/Bundle").getString("AdditionalStepsWizardPanelUI_StartAutomaticallyString"); // NOI18N

  private ReviewAdditionalStepsPanel.Model model = null;

  /**
   * Creates new form ReviewAdditionalStepsPanelUI
   */
  ReviewAdditionalStepsPanelUI(ReviewAdditionalStepsPanel.Model model) {
    this.model = model;
    initComponents();
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

        checkAutomation = new javax.swing.JCheckBox();
        additionalSteps = new org.netbeans.modules.profiler.attach.panels.components.StepsPanelComponent();

        setMaximumSize(new java.awt.Dimension(800, 600));
        setMinimumSize(new java.awt.Dimension(400, 300));
        setPreferredSize(new java.awt.Dimension(500, 300));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("org/netbeans/modules/profiler/attach/panels/Bundle"); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(checkAutomation, bundle.getString("AdditionalStepsWizardPanelUI_StartAutomaticallyString")); // NOI18N
        checkAutomation.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        checkAutomation.setMargin(new java.awt.Insets(0, 0, 0, 0));
        checkAutomation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkAutomationActionPerformed(evt);
            }
        });

        additionalSteps.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        additionalSteps.setHintText(org.openide.util.NbBundle.getMessage(ReviewAdditionalStepsPanelUI.class, "ReviewAdditionalStepsPanelUI.additionalSteps.hintText")); // NOI18N
        additionalSteps.setPreferredSize(new java.awt.Dimension(10, 10));
        additionalSteps.setTitle(bundle.getString("AttachWizard_ReviewAdditionalStepsMsg")); // NOI18N

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(additionalSteps, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 480, Short.MAX_VALUE)
                    .add(checkAutomation, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 330, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(additionalSteps, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(checkAutomation)
                .addContainerGap())
        );

        checkAutomation.getAccessibleContext().setAccessibleDescription(org.openide.util.NbBundle.getMessage(ReviewAdditionalStepsPanelUI.class, "ReviewAdditionalStepsPanelUI.checkAutomation.AccessibleContext.accessibleDescription")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents
    
    private void checkAutomationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_checkAutomationActionPerformed
      this.model.setAutomaticStart(this.checkAutomation.isSelected());
//      this.additionalSteps.setSteps(this.model.getAdditionalSteps());
    }//GEN-LAST:event_checkAutomationActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.netbeans.modules.profiler.attach.panels.components.StepsPanelComponent additionalSteps;
    private javax.swing.JCheckBox checkAutomation;
    // End of variables declaration//GEN-END:variables
    
    public void refresh() {
      checkAutomation.setText(MessageFormat.format(checkAutomationText, new Object[]{model.getProviderName()}));
      this.additionalSteps.setSteps(this.model.getAdditionalSteps());
      this.checkAutomation.setSelected(this.model.getAutomaticStart());
    }
}
