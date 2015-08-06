// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.client.youngandroid;

import com.google.appinventor.client.Ode;
import static com.google.appinventor.client.Ode.MESSAGES;

import com.google.appinventor.client.editor.ProjectEditor;
import com.google.appinventor.client.editor.simple.SimpleComponentDatabase;
import com.google.appinventor.client.editor.simple.SimpleEditor;
import com.google.appinventor.client.editor.youngandroid.YaProjectEditor;
import com.google.appinventor.client.explorer.project.Project;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;

import java.util.Arrays;
import java.util.List;

/**
 */
public final class TextValidators {

  private static final int MAX_FILENAME_SIZE = 100;
  private static final int MIN_FILENAME_SIZE = 1;

  protected static final List<String> YAIL_NAMES = Arrays.asList("CsvUtil", "Double", "Float",
          "Integer", "JavaCollection", "JavaIterator", "KawaEnvironment", "Long", "Short",
          "SimpleForm", "String", "Pattern", "YailList", "YailNumberToString", "YailRuntimeError");

  // This class should never be instantiated.
  private TextValidators() {}

  /**
   * Determines whether the given project name is valid, displaying an alert
   * if it is not.  In order to be valid, the project name must satisfy
   * {@link #isValidIdentifier(String)} and not be a duplicate of an existing
   * project name for the same user.
   *
   * @param projectName the project name to validate
   * @return {@code true} if the project name is valid, {@code false} otherwise
   */
  public static boolean checkNewProjectName(String projectName) {

    // Check the format of the project name
    if (!isValidIdentifier(projectName)) {
      Window.alert(MESSAGES.malformedProjectNameError());
      return false;
    }

    // Check that project does not already exist
    if (Ode.getInstance().getProjectManager().getProject(projectName) != null) {
      Window.alert(MESSAGES.duplicateProjectNameError(projectName));
      return false;
    }

    return true;
  }

  public static boolean checkNewComponentName(String componentName) {

    // Check that it meets the formatting requirements.
    if (!TextValidators.isValidComponentIdentifier(componentName)) {
      Window.alert(MESSAGES.malformedComponentNameError());
      return false;
    }

    long projectId = Ode.getInstance().getCurrentYoungAndroidProjectId();
    if ( projectId == 0) { // Check we have a current Project
      return false;
    }

    YaProjectEditor editor = (YaProjectEditor) Ode.getInstance().getEditorManager().getOpenProjectEditor(projectId);

    // Check that it's unique.
    final List<String> names = editor.getComponentInstances();
    if (names.contains(componentName)) {
      Window.alert(MESSAGES.sameAsComponentInstanceNameError());
      return false;
    }

    // Check that it is a variable name used in the Yail code
    if (YAIL_NAMES.contains(componentName)) {
      Window.alert(MESSAGES.badComponentNameError());
      return false;
    }

    //Check that it is not a Component type name
    SimpleComponentDatabase COMPONENT_DATABASE = SimpleComponentDatabase.getInstance(projectId);
    if (COMPONENT_DATABASE.isComponent(componentName)) {
      Window.alert(MESSAGES.duplicateComponentNameError());
      return false;
    }

    return true;
  }

  /**
   * Checks whether the argument is a legal identifier, specifically,
   * a non-empty string starting with a letter and followed by any number of
   * (unaccented English) letters, digits, or underscores.
   *
   * @param text the proposed identifier
   * @return {@code true} if the argument is a legal identifier, {@code false}
   *         otherwise
   */
  public static boolean isValidIdentifier(String text) {
    return text.matches("^[a-zA-Z]\\w*$");
  }

  /**
   * Checks whether the argument is a legal component identifier; please check
   * Blockly.LexicalVariable.checkIdentifier for the regex reference
   *
   * @param text the proposed identifier
   * @return {@code true} if the argument is a legal identifier, {@code false}
   *         otherwise
   */
  public static boolean isValidComponentIdentifier(String text) {
	return text.matches("^[^-0-9!&%^/>=<`'\"#:;,\\\\^\\*\\+\\.\\(\\)\\|\\{\\}\\[\\]\\ ]" +
			"[^-!&%^/>=<'\"#:;,\\\\^\\*\\+\\.\\(\\)\\|\\{\\}\\[\\]\\ ]*$");
  }

  /**
   * Checks whether the argument is a legal filename, meaning
   * it is unchanged by URL encoding and it meets the aapt
   * requirements as follows:
   * - all characters must be 7-bit printable ASCII
   * - none of { '/' '\\' ':' }
   * @param filename The filename (not path) of uploaded file
   * @return {@code true} if the argument is a legal filename, {@code false}
   *         otherwise
   */
  public static boolean isValidCharFilename(String filename){
    return !filename.contains("'") && filename.equals(URL.encodePathSegment(filename));
  }
  
  /**
   * Checks whether the argument is a filename which meets the length
   * requirement imposed by aapt, which is:
   * - the filename length must be less than kMaxAssetFileName bytes long
   *   (and can't be empty)
   * where kMaxAssetFileName is defined to be 100.
   * (A legal name, therefore, has length <= kMaxAssetFileNames)
   * @param filename The filename (not path) of uploaded file
   * @return {@code true} if the length of the argument is legal, {@code false}
   *         otherwise
   */
  public static boolean isValidLengthFilename(String filename){
    return !(filename.length() > MAX_FILENAME_SIZE || filename.length() < MIN_FILENAME_SIZE);
  }
}
