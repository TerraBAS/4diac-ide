/*******************************************************************************
 * Copyright (c) 2019 fortiss GmbH
 * 				 2020 Johannes Kepler Unviersity Linz
 *               2023 Martin Erich Jobst
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *   Martin Jobst - initial API and implementation and/or initial documentation
 *   Alois Zoitl  - extracted base class for all types from fbtemplate
 *   Martin Jobst - generate variable default values with constant expressions
 *******************************************************************************/
package org.eclipse.fordiac.ide.export.forte_ng

import java.nio.file.Path
import java.util.List
import java.util.Map
import java.util.Set
import org.eclipse.fordiac.ide.export.language.ILanguageSupport
import org.eclipse.fordiac.ide.export.language.ILanguageSupportFactory
import org.eclipse.fordiac.ide.model.libraryElement.INamedElement
import org.eclipse.fordiac.ide.model.libraryElement.LibraryElement
import org.eclipse.fordiac.ide.model.libraryElement.VarDeclaration
import org.eclipse.xtend.lib.annotations.Accessors

import static extension org.eclipse.fordiac.ide.export.forte_ng.util.ForteNgExportUtil.*
import static extension org.eclipse.xtext.EcoreUtil2.*

abstract class ForteLibraryElementTemplate<T extends LibraryElement> extends ForteNgExportTemplate {

	public static final CharSequence EXPORT_PREFIX = "st_"
	
	@Accessors(PROTECTED_GETTER) final T type
	final Map<VarDeclaration, ILanguageSupport> variableLanguageSupport

	new(T type, String name, Path prefix) {
		super(name, prefix)
		this.type = type;
		variableLanguageSupport = type.getAllContentsOfType(VarDeclaration).toInvertedMap [
			ILanguageSupportFactory.createLanguageSupport("forte_ng", it)
		]
	}

	def protected getExportPrefix() {
		return EXPORT_PREFIX
	}

	def protected generateHeader() '''
		/*************************************************************************
		 *** FORTE Library Element
		 ***
		 *** This file was generated using the 4DIAC FORTE Export Filter V1.0.x NG!
		 ***
		 *** Name: «type.name»
		 *** Description: «type.comment»
		 *** Version:
		«FOR info : type.versionInfo»
			***     «info.version»: «info.date»/«info.author» - «info.organization» - «info.remarks»
		«ENDFOR»
		 *************************************************************************/
	'''

	def protected generateIncludeGuardStart() '''
		#ifndef _«type.name.toUpperCase»_H_
		#define _«type.name.toUpperCase»_H_
	'''

	def protected generateIncludeGuardEnd() '''
		#endif // _«type.name.toUpperCase»_H_
	'''

	def protected generateAccessors(List<VarDeclaration> vars, String function) '''
		«FOR v : vars»
			«v.generateInterfaceTypeName» &«exportPrefix»«v.name»() {
			  return *static_cast<«v.generateInterfaceTypeName»*>(«function»(«vars.indexOf(v)»));
			}
			
		«ENDFOR»
	'''

	def protected CharSequence generateInterfaceTypeName(VarDeclaration variable) //
	'''«IF variable.array»CIEC_ARRAY<«ENDIF»«variable.type.generateTypeName»«IF variable.array»>«ENDIF»'''

	def protected CharSequence generateName(VarDeclaration variable) '''var_«variable.name»'''

	def CharSequence generateVariableDefaultValue(VarDeclaration decl) {
		variableLanguageSupport.get(decl)?.generate(emptyMap)
	}

	def protected getFORTEStringId(String s) '''g_nStringId«s»'''

	def protected getFORTENameList(List<? extends INamedElement> elements) {
		elements.map[name.FORTEStringId].join(", ")
	}

	def protected getFORTETypeList(List<? extends VarDeclaration> elements) {
		elements.map['''«IF it.array»«"ARRAY".FORTEStringId», «it.arraySize», «ENDIF»«it.type.generateTypeNamePlain.FORTEStringId»'''].join(", ")
	}

	override getErrors() {
		(super.getErrors + variableLanguageSupport.values.filterNull.flatMap[getErrors].toSet).toList
	}

	override getWarnings() {
		(super.getWarnings + variableLanguageSupport.values.filterNull.flatMap[getWarnings].toSet).toList
	}

	override getInfos() {
		(super.getInfos + variableLanguageSupport.values.filterNull.flatMap[getInfos].toSet).toList
	}
	
	def Set<INamedElement> getDependencies(Map<?, ?> options) {
		variableLanguageSupport.values.filterNull.flatMap[getDependencies(options)].toSet
	}
}
