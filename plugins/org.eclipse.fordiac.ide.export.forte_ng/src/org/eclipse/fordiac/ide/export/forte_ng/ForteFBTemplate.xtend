/*******************************************************************************
 * Copyright (c) 2019 fortiss GmbH
 * 				 2020 Johannes Kepler Unviersity Linz
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
 *******************************************************************************/
package org.eclipse.fordiac.ide.export.forte_ng

import java.nio.file.Path
import java.util.List
import org.eclipse.fordiac.ide.model.libraryElement.AdapterDeclaration
import org.eclipse.fordiac.ide.model.libraryElement.BaseFBType
import org.eclipse.fordiac.ide.model.libraryElement.FBType
import org.eclipse.fordiac.ide.model.libraryElement.INamedElement
import org.eclipse.fordiac.ide.model.libraryElement.VarDeclaration
import org.eclipse.fordiac.ide.model.libraryElement.With

abstract class ForteFBTemplate extends ForteLibraryElementTemplate {

	new(String name, Path prefix) {
		super(name, prefix) 
	}

	override protected FBType getType()

	def protected generateHeaderIncludes() '''
		«(type.interfaceList.inputVars + type.interfaceList.outputVars).generateTypeIncludes»
		«(type.interfaceList.sockets + type.interfaceList.plugs).generateAdapterIncludes»
		
		«type.compilerInfo?.header»
	'''

	def protected generateImplIncludes() '''
		#include "«type.name».h"
		#ifdef FORTE_ENABLE_GENERATED_SOURCE_CPP
		#include "«type.name»_gen.cpp"
		#endif

		«type.compilerInfo?.header»
	'''


	def protected generateAdapterIncludes(Iterable<AdapterDeclaration> vars) '''
		«FOR include : vars.map[it.typeName].sort.toSet»
			#include "«include».h"
		«ENDFOR»
	'''

	def protected generateFBDeclaration() '''
		DECLARE_FIRMWARE_FB(«FBClassName»)
	'''

	def protected generateFBDefinition() '''
		DEFINE_FIRMWARE_FB(«FBClassName», «type.name.FORTEString»)
	'''

	def protected generateFBInterfaceDeclaration() '''
		«IF !type.interfaceList.inputVars.empty»
			static const CStringDictionary::TStringId scm_anDataInputNames[];
			static const CStringDictionary::TStringId scm_anDataInputTypeIds[];
		«ENDIF»

		«IF !type.interfaceList.outputVars.empty»
			static const CStringDictionary::TStringId scm_anDataOutputNames[];
			static const CStringDictionary::TStringId scm_anDataOutputTypeIds[];
		«ENDIF»

		«IF !type.interfaceList.eventInputs.empty»
			«FOR event : type.interfaceList.eventInputs»
				static const TEventID scm_nEvent«event.name»ID = «type.interfaceList.eventInputs.indexOf(event)»;
			«ENDFOR»

			static const TDataIOID scm_anEIWith[];
			static const TForteInt16 scm_anEIWithIndexes[];
			static const CStringDictionary::TStringId scm_anEventInputNames[];
		«ENDIF»

		«IF !type.interfaceList.eventOutputs.empty»
			«FOR event : type.interfaceList.eventOutputs»
				static const TEventID scm_nEvent«event.name»ID = «type.interfaceList.eventOutputs.indexOf(event)»;
			«ENDFOR»

			static const TDataIOID scm_anEOWith[];
			static const TForteInt16 scm_anEOWithIndexes[];
			static const CStringDictionary::TStringId scm_anEventOutputNames[];
		«ENDIF»
		
		«IF !type.interfaceList.sockets.empty || !type.interfaceList.plugs.empty»
			«FOR adapter : type.interfaceList.sockets»
				static const int scm_n«adapter.name»AdpNum = «type.interfaceList.sockets.indexOf(adapter)»;
			«ENDFOR»
			«FOR adapter : type.interfaceList.plugs»
				static const int scm_n«adapter.name»AdpNum = «type.interfaceList.sockets.size + type.interfaceList.plugs.indexOf(adapter)»;
			«ENDFOR»

			static const SAdapterInstanceDef scm_astAdapterInstances[];
		«ENDIF»
	'''

	def protected generateFBInterfaceDefinition() {
		val inputWith = newArrayList
		val inputWithIndexes = newArrayList
		type.interfaceList.eventInputs.forEach[event | {
			if(event.with.empty) {
				inputWithIndexes.add(-1)
			} else {
				inputWithIndexes.add(inputWith.size)
				for (With with : event.with) {
					inputWith.add(type.interfaceList.inputVars.indexOf(with.variables))
				}
				inputWith.add(255)
			}
		}]
		val outputWith = newArrayList
		val outputWithIndexes = newArrayList
		type.interfaceList.eventOutputs.forEach[event | {
			if(event.with.empty) {
				outputWithIndexes.add(-1)
			} else {
				outputWithIndexes.add(outputWith.size)
				for (With with : event.with) {
					outputWith.add(type.interfaceList.outputVars.indexOf(with.variables))
				}
				outputWith.add(255)
			}
		}]
		'''
			«IF !type.interfaceList.inputVars.empty»
				const CStringDictionary::TStringId «FBClassName»::scm_anDataInputNames[] = {«type.interfaceList.inputVars.FORTENameList»};

				const CStringDictionary::TStringId «FBClassName»::scm_anDataInputTypeIds[] = {«type.interfaceList.inputVars.FORTETypeList»};
			«ENDIF»

			«IF !type.interfaceList.outputVars.empty»
				const CStringDictionary::TStringId «FBClassName»::scm_anDataOutputNames[] = {«type.interfaceList.outputVars.FORTENameList»};

				const CStringDictionary::TStringId «FBClassName»::scm_anDataOutputTypeIds[] = {«type.interfaceList.outputVars.FORTETypeList»};
			«ENDIF»

			«IF !type.interfaceList.eventInputs.empty»
				const TDataIOID «FBClassName»::scm_anEIWith[] = {«inputWith.join(", ")»};
				const TForteInt16 «FBClassName»::scm_anEIWithIndexes[] = {«inputWithIndexes.join(", ")»};
				const CStringDictionary::TStringId «FBClassName»::scm_anEventInputNames[] = {«type.interfaceList.eventInputs.FORTENameList»};
			«ENDIF»

			«IF !type.interfaceList.eventOutputs.empty»
				const TDataIOID «FBClassName»::scm_anEOWith[] = {«outputWith.join(", ")»};
				const TForteInt16 «FBClassName»::scm_anEOWithIndexes[] = {«outputWithIndexes.join(", ")»};
				const CStringDictionary::TStringId «FBClassName»::scm_anEventOutputNames[] = {«type.interfaceList.eventOutputs.FORTENameList»};
			«ENDIF»

			«IF !type.interfaceList.sockets.empty || !type.interfaceList.plugs.empty»
				const SAdapterInstanceDef «FBClassName»::scm_astAdapterInstances[] = {
				  «FOR adapter : (type.interfaceList.sockets + type.interfaceList.plugs) SEPARATOR ",\n"»{«adapter.typeName.FORTEString», «adapter.name.FORTEString», «!adapter.isInput»}«ENDFOR»
				};
			«ENDIF»
		'''
	}

	def protected generateFBInterfaceSpecDeclaration() '''
		static const SFBInterfaceSpec scm_stFBInterfaceSpec;
	'''

	def protected generateFBInterfaceSpecDefinition() '''
		const SFBInterfaceSpec «FBClassName»::scm_stFBInterfaceSpec = {
		  «type.interfaceList.eventInputs.size», «IF type.interfaceList.eventInputs.empty»nullptr, nullptr, nullptr«ELSE»scm_anEventInputNames, scm_anEIWith, scm_anEIWithIndexes«ENDIF»,
		  «type.interfaceList.eventOutputs.size», «IF type.interfaceList.eventOutputs.empty»nullptr, nullptr, nullptr«ELSE»scm_anEventOutputNames, scm_anEOWith, scm_anEOWithIndexes«ENDIF»,
		  «type.interfaceList.inputVars.size», «IF type.interfaceList.inputVars.empty»nullptr, nullptr«ELSE»scm_anDataInputNames, scm_anDataInputTypeIds«ENDIF»,
		  «type.interfaceList.outputVars.size», «IF type.interfaceList.outputVars.empty»nullptr, nullptr«ELSE»scm_anDataOutputNames, scm_anDataOutputTypeIds«ENDIF»,
		  «type.interfaceList.plugs.size + type.interfaceList.sockets.size», «IF !type.interfaceList.sockets.empty || !type.interfaceList.plugs.empty»scm_astAdapterInstances«ELSE»nullptr«ENDIF»
		};
	'''

	def protected generateInternalVarDelcaration(BaseFBType baseFBType) '''
	   «IF !baseFBType.internalVars.isEmpty»
	     static const CStringDictionary::TStringId scm_anInternalsNames[];
	     static const CStringDictionary::TStringId scm_anInternalsTypeIds[];
	     static const SInternalVarsInformation scm_stInternalVars;
	   «ENDIF»
	'''

	def protected generateInternalVarDefinition(BaseFBType baseFBType) '''
       «IF !baseFBType.internalVars.isEmpty»
         const CStringDictionary::TStringId «FBClassName»::scm_anInternalsNames[] = {«baseFBType.internalVars.FORTENameList»};
         const CStringDictionary::TStringId «FBClassName»::scm_anInternalsTypeIds[] = {«baseFBType.internalVars.FORTETypeList»};
         const SInternalVarsInformation «FBClassName»::scm_stInternalVars = {«baseFBType.internalVars.size», scm_anInternalsNames, scm_anInternalsTypeIds};
       «ENDIF»
    '''

	def protected generateAccessors(List<AdapterDeclaration> adapters) '''
		«FOR adapter : adapters»
			FORTE_«adapter.typeName»& «adapter.name»() {
			  return (*static_cast<FORTE_«adapter.typeName»*>(m_apoAdapters[«adapters.indexOf(adapter)»]));
			};

		«ENDFOR»
	'''

	def protected getFBClassName() '''FORTE_«type.name»'''

}
