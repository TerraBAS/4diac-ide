/**
 * generated by Xtext 2.25.0
 */
package org.eclipse.fordiac.ide.model.structuredtext.structuredText;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Call</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * </p>
 * <ul>
 *   <li>{@link org.eclipse.fordiac.ide.model.structuredtext.structuredText.Call#getFunc <em>Func</em>}</li>
 *   <li>{@link org.eclipse.fordiac.ide.model.structuredtext.structuredText.Call#getArgs <em>Args</em>}</li>
 * </ul>
 *
 * @see org.eclipse.fordiac.ide.model.structuredtext.structuredText.StructuredTextPackage#getCall()
 * @model
 * @generated
 */
public interface Call extends Statement, Expression
{
  /**
   * Returns the value of the '<em><b>Func</b></em>' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Func</em>' attribute.
   * @see #setFunc(String)
   * @see org.eclipse.fordiac.ide.model.structuredtext.structuredText.StructuredTextPackage#getCall_Func()
   * @model
   * @generated
   */
  String getFunc();

  /**
   * Sets the value of the '{@link org.eclipse.fordiac.ide.model.structuredtext.structuredText.Call#getFunc <em>Func</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param value the new value of the '<em>Func</em>' attribute.
   * @see #getFunc()
   * @generated
   */
  void setFunc(String value);

  /**
   * Returns the value of the '<em><b>Args</b></em>' containment reference list.
   * The list contents are of type {@link org.eclipse.fordiac.ide.model.structuredtext.structuredText.Argument}.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the value of the '<em>Args</em>' containment reference list.
   * @see org.eclipse.fordiac.ide.model.structuredtext.structuredText.StructuredTextPackage#getCall_Args()
   * @model containment="true"
   * @generated
   */
  EList<Argument> getArgs();

} // Call
