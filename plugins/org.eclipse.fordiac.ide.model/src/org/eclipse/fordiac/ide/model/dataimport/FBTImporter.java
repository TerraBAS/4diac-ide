/********************************************************************************
 * Copyright (c) 2008 - 2017  Profactor GmbH, TU Wien ACIN, fortiss GmbH
 * 				 2018 TU Wien/ACIN
 * 				 2020 Johannes Kepler University, Linz
 *               2021 Primetals Technologies Austria GmbH
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *  Gerhard Ebenhofer, Ingo Hegny, Alois Zoitl, Martin Jobst
 *    - initial API and implementation and/or initial documentation
 *  Peter Gsellmann - incorporating simple fb
 *  Alois Zoitl - Changed XML parsing to Staxx cursor interface for improved
 *  			  parsing performance
 *  Martin Melik Merkumians - added import of internal FBs
 ********************************************************************************/
package org.eclipse.fordiac.ide.model.dataimport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.EList;
import org.eclipse.fordiac.ide.model.LibraryElementTags;
import org.eclipse.fordiac.ide.model.Messages;
import org.eclipse.fordiac.ide.model.Palette.AdapterTypePaletteEntry;
import org.eclipse.fordiac.ide.model.Palette.FBTypePaletteEntry;
import org.eclipse.fordiac.ide.model.Palette.Palette;
import org.eclipse.fordiac.ide.model.dataimport.exceptions.TypeImportException;
import org.eclipse.fordiac.ide.model.libraryElement.AdapterDeclaration;
import org.eclipse.fordiac.ide.model.libraryElement.AdapterEvent;
import org.eclipse.fordiac.ide.model.libraryElement.AdapterFB;
import org.eclipse.fordiac.ide.model.libraryElement.AdapterType;
import org.eclipse.fordiac.ide.model.libraryElement.Algorithm;
import org.eclipse.fordiac.ide.model.libraryElement.BaseFBType;
import org.eclipse.fordiac.ide.model.libraryElement.BasicFBType;
import org.eclipse.fordiac.ide.model.libraryElement.CompositeFBType;
import org.eclipse.fordiac.ide.model.libraryElement.ECAction;
import org.eclipse.fordiac.ide.model.libraryElement.ECC;
import org.eclipse.fordiac.ide.model.libraryElement.ECState;
import org.eclipse.fordiac.ide.model.libraryElement.ECTransition;
import org.eclipse.fordiac.ide.model.libraryElement.Event;
import org.eclipse.fordiac.ide.model.libraryElement.FB;
import org.eclipse.fordiac.ide.model.libraryElement.FBNetwork;
import org.eclipse.fordiac.ide.model.libraryElement.FBType;
import org.eclipse.fordiac.ide.model.libraryElement.InputPrimitive;
import org.eclipse.fordiac.ide.model.libraryElement.InterfaceList;
import org.eclipse.fordiac.ide.model.libraryElement.LibraryElement;
import org.eclipse.fordiac.ide.model.libraryElement.LibraryElementFactory;
import org.eclipse.fordiac.ide.model.libraryElement.OtherAlgorithm;
import org.eclipse.fordiac.ide.model.libraryElement.OutputPrimitive;
import org.eclipse.fordiac.ide.model.libraryElement.PositionableElement;
import org.eclipse.fordiac.ide.model.libraryElement.Primitive;
import org.eclipse.fordiac.ide.model.libraryElement.STAlgorithm;
import org.eclipse.fordiac.ide.model.libraryElement.ServiceInterface;
import org.eclipse.fordiac.ide.model.libraryElement.ServiceInterfaceFBType;
import org.eclipse.fordiac.ide.model.libraryElement.ServiceSequence;
import org.eclipse.fordiac.ide.model.libraryElement.ServiceTransaction;
import org.eclipse.fordiac.ide.model.libraryElement.SimpleFBType;
import org.eclipse.fordiac.ide.model.libraryElement.SubAppType;
import org.eclipse.fordiac.ide.model.libraryElement.TextAlgorithm;
import org.eclipse.fordiac.ide.model.libraryElement.VarDeclaration;
import org.eclipse.fordiac.ide.model.libraryElement.With;
import org.eclipse.fordiac.ide.model.typelibrary.EventTypeLibrary;

/**
 * Managing class for importing *.fbt files
 *
 */

public class FBTImporter extends TypeImporter {

	/** The variables. */
	private final Map<String, VarDeclaration> variables = new HashMap<>();

	/** The input events. */
	private final Map<String, Event> inputEvents = new HashMap<>();

	/** The output events. */
	private final Map<String, Event> outputEvents = new HashMap<>();

	/** The adapters. */
	private final Map<String, AdapterDeclaration> adapters = new HashMap<>();
	private final Map<String, PositionableElement> adapterPositions = new HashMap<>();

	/** The algorithm name ec action mapping. */
	private final Map<String, List<ECAction>> algorithmNameECActionMapping = new HashMap<>();

	/** The ec states. */
	private final Map<String, ECState> ecStates = new HashMap<>();

	private final Map<Event, List<String>> withList = new HashMap<>();

	@Override
	public FBType getElement() {
		return (FBType) super.getElement();
	}

	public FBTImporter(final IFile typeFile) {
		super(typeFile);
	}

	protected FBTImporter(final CommonElementImporter importer) {
		super(importer);
	}

	@Override
	protected LibraryElement createRootModelElement() {
		final FBType newType = LibraryElementFactory.eINSTANCE.createFBType();
		newType.setService(LibraryElementFactory.eINSTANCE.createService());
		return newType;
	}

	@Override
	protected String getStartElementName() {
		return LibraryElementTags.FBTYPE_ELEMENT;
	}

	@Override
	protected IChildHandler getBaseChildrenHandler() {
		return name -> {
			switch (name) {
			case LibraryElementTags.IDENTIFICATION_ELEMENT:
				parseIdentification(getElement());
				break;
			case LibraryElementTags.VERSION_INFO_ELEMENT:
				parseVersionInfo(getElement());
				break;
			case LibraryElementTags.COMPILER_INFO_ELEMENT:
				getElement().setCompilerInfo(parseCompilerInfo());
				break;
			case LibraryElementTags.INTERFACE_LIST_ELEMENT:
				getElement().setInterfaceList(parseInterfaceList(LibraryElementTags.INTERFACE_LIST_ELEMENT));
				break;
			case LibraryElementTags.BASIC_F_B_ELEMENT:
				setElement(convertToBasicType(getElement()));
				parseBasicFB((BasicFBType) getElement());
				break;
			case LibraryElementTags.SIMPLE_F_B_ELEMENT:
				setElement(convertToSimpleType(getElement()));
				parseSimpleFB((SimpleFBType) getElement());
				break;
			case LibraryElementTags.FBNETWORK_ELEMENT:
				// parse the composite FBs as last
				setElement(convertToCompositeType(getElement()));
				parseFBNetwork((CompositeFBType) getElement());
				break;
			case LibraryElementTags.SERVICE_ELEMENT:
				parseService(getElement());
				break;
			default:
				return false;
			}
			return true;
		};
	}

	@Override
	public void loadElement() {
		super.loadElement();

		final FBType newType = getElement();

		if ((newType instanceof BasicFBType) || (newType instanceof CompositeFBType)
				|| (newType instanceof ServiceInterfaceFBType) || (newType instanceof SimpleFBType)
				|| (newType instanceof SubAppType)) {
			return;
		}
		setElement(convertToServiceInterfaceType(newType));
	}

	/**
	 * This method parses the DTD of a ServiceInterfaceFBType.
	 *
	 * @param type - The ServiceInterfaceFBType that is being parsed
	 *
	 * @throws TypeImportException the FBT import exception
	 * @throws XMLStreamException
	 */
	protected void parseService(final FBType type) throws TypeImportException, XMLStreamException {

		final String rightInterface = getAttributeValue(LibraryElementTags.RIGHT_INTERFACE_ATTRIBUTE);
		if (null != rightInterface) {
			final ServiceInterface rightInter = LibraryElementFactory.eINSTANCE.createServiceInterface();
			rightInter.setName(rightInterface);
			type.getService().setRightInterface(rightInter);
		} else {
			throw new TypeImportException(Messages.FBTImporter_SERVICE_INTERFACE_RIGHTINTERFACE_EXCEPTION);
		}
		final String leftInterface = getAttributeValue(LibraryElementTags.LEFT_INTERFACE_ATTRIBUTE);
		if (null != leftInterface) {
			final ServiceInterface leftInter = LibraryElementFactory.eINSTANCE.createServiceInterface();
			leftInter.setName(leftInterface);
			type.getService().setLeftInterface(leftInter);
		} else {
			throw new TypeImportException(Messages.FBTImporter_SERVICE_INTERFACE_LEFTINTERFACE_EXCEPTION);
		}
		final String comment = getAttributeValue(LibraryElementTags.COMMENT_ATTRIBUTE);
		if (null != comment) {
			type.setComment(comment);
		}

		processChildren(LibraryElementTags.SERVICE_ELEMENT, name -> {
			if (LibraryElementTags.SERVICE_SEQUENCE_ELEMENT.equals(name)) {
				parseServiceSequence(type);
				return true;
			}
			return false;
		});
	}

	/**
	 * This method parses the ServiceSequence of a ServiceInterfaceFBType.
	 *
	 * @param type - The ServiceInterfaceFBType from which the ServiceSequence will
	 *             be parsed
	 *
	 * @throws TypeImportException the FBT import exception
	 * @throws XMLStreamException
	 */
	private void parseServiceSequence(final FBType type) throws TypeImportException, XMLStreamException {
		final ServiceSequence serviceSequence = LibraryElementFactory.eINSTANCE.createServiceSequence();
		readNameCommentAttributes(serviceSequence);

		processChildren(LibraryElementTags.SERVICE_SEQUENCE_ELEMENT, name -> {
			if (LibraryElementTags.SERVICE_TRANSACTION_ELEMENT.equals(name)) {
				parseServiceTransaction(serviceSequence, type);
				return true;
			}
			return false;
		});
		type.getService().getServiceSequence().add(serviceSequence);
	}

	/**
	 * This method parses the ServiceTransaction of a ServiceSequence.
	 *
	 * @param serviceSequence - The serviceSequence containing the
	 *                        serviceTransaction that is being parsed
	 * @param type            - The serviceInterfaceFBType containing the
	 *                        serviceTransaction
	 *
	 * @throws TypeImportException the FBT import exception
	 * @throws XMLStreamException
	 */
	private void parseServiceTransaction(final ServiceSequence serviceSequence, final FBType type)
			throws TypeImportException, XMLStreamException {
		final ServiceTransaction serviceTransaction = LibraryElementFactory.eINSTANCE.createServiceTransaction();

		processChildren(LibraryElementTags.SERVICE_TRANSACTION_ELEMENT, name -> {
			switch (name) {
			case LibraryElementTags.INPUT_PRIMITIVE_ELEMENT:
				parseInputPrimitive(serviceTransaction, type);
				break;
			case LibraryElementTags.OUTPUT_PRIMITIVE_ELEMENT:
				parseOutputPrimitive(serviceTransaction, type);
				break;
			default:
				return false;
			}
			return true;
		});

		serviceSequence.getServiceTransaction().add(serviceTransaction);
	}

	/**
	 * This method parses the OutputPrimitive of a ServiceTransaction.
	 *
	 * @param serviceTransaction - The serviceTransaction containing the
	 *                           OutputPrimitive that is being parsed
	 * @param type               - the serviceInterfaceFBType containing the
	 *                           OutputPrimitive
	 *
	 * @throws TypeImportException the FBT import exception
	 * @throws XMLStreamException
	 */
	private void parseOutputPrimitive(final ServiceTransaction serviceTransaction, final FBType type)
			throws TypeImportException, XMLStreamException {
		final OutputPrimitive outputPrimitive = LibraryElementFactory.eINSTANCE.createOutputPrimitive();

		parsePrimitive(type, outputPrimitive);
		proceedToEndElementNamed(LibraryElementTags.OUTPUT_PRIMITIVE_ELEMENT);
		serviceTransaction.getOutputPrimitive().add(outputPrimitive);
	}

	private void parsePrimitive(final FBType type, final Primitive outputPrimitive) throws TypeImportException {
		final String interFace = getAttributeValue(LibraryElementTags.INTERFACE_ATTRIBUTE);
		if (null != interFace) {
			if (interFace.equals(type.getService().getLeftInterface().getName())) {
				outputPrimitive.setInterface(type.getService().getLeftInterface());
			} else if (interFace.equals(type.getService().getRightInterface().getName())) {
				outputPrimitive.setInterface(type.getService().getRightInterface());
			}
		} else {
			throw new TypeImportException(Messages.FBTImporter_OUTPUT_PRIMITIVE_EXCEPTION);
		}
		final String event = getAttributeValue(getEventElement());
		if (null != event) {
			outputPrimitive.setEvent(event);
		} else {
			throw new TypeImportException(Messages.FBTImporter_OUTPUT_PRIMITIVE_EVENT_EXCEPTION);
		}
		final String parameters = getAttributeValue(LibraryElementTags.PARAMETERS_ATTRIBUTE);
		if (null != parameters) {
			outputPrimitive.setParameters(parameters);
		}
	}

	/**
	 * This method parses the InputPrimitive of a ServiceTransaction.
	 *
	 * @param serviceTransaction - The serviceTransaction containing the
	 *                           InputPrimitive that is being parsed
	 * @param type               - the serviceInterfaceFBType containing the
	 *                           InputPrimitive
	 *
	 * @throws TypeImportException the FBT import exception
	 * @throws XMLStreamException
	 */
	private void parseInputPrimitive(final ServiceTransaction serviceTransaction, final FBType type)
			throws TypeImportException, XMLStreamException {
		final InputPrimitive inputPrimitive = LibraryElementFactory.eINSTANCE.createInputPrimitive();

		parsePrimitive(type, inputPrimitive);
		proceedToEndElementNamed(LibraryElementTags.INPUT_PRIMITIVE_ELEMENT);
		serviceTransaction.setInputPrimitive(inputPrimitive);
	}

	/**
	 * This method converts a FBType to a ServiceInterfaceFBType.
	 *
	 * @param type - The FBType that is being converted to ServiceInterfaceFBType
	 *
	 * @return - A FBType that is converted
	 */
	private static FBType convertToServiceInterfaceType(final FBType type) {
		final ServiceInterfaceFBType serviceType = LibraryElementFactory.eINSTANCE.createServiceInterfaceFBType();
		copyGeneralTypeInformation(serviceType, type);
		return serviceType;
	}

	private static void copyGeneralTypeInformation(final FBType dstType, final FBType srcType) {
		dstType.setName(srcType.getName());
		dstType.setComment(srcType.getComment());
		dstType.setCompilerInfo(srcType.getCompilerInfo());
		dstType.setInterfaceList(srcType.getInterfaceList());
		dstType.setIdentification(srcType.getIdentification());
		dstType.getVersionInfo().addAll(srcType.getVersionInfo());
		dstType.setService(srcType.getService());
	}

	/**
	 * This method parses a compositeFBType.
	 *
	 * @param type - the CompositeFBType that is being parsed
	 *
	 * @throws TypeImportException the FBT import exception
	 * @throws XMLStreamException
	 */
	private void parseFBNetwork(final CompositeFBType type) throws TypeImportException, XMLStreamException {
		final FBNetwork fbNetwork = LibraryElementFactory.eINSTANCE.createFBNetwork();
		type.setFBNetwork(fbNetwork);
		adapters.values().forEach(adapter -> addAdapterFB(fbNetwork, adapter, getPalette()));
		final FBNetworkImporter fbnInmporter = new FBNetworkImporter(this, fbNetwork, type.getInterfaceList());
		fbnInmporter.parseFBNetwork(LibraryElementTags.FBNETWORK_ELEMENT);
	}

	private void addAdapterFB(final FBNetwork fbNetwork, final AdapterDeclaration adapter, final Palette palette) {
		final AdapterFB aFB = LibraryElementFactory.eINSTANCE.createAdapterFB();
		aFB.setPaletteEntry(palette.getAdapterTypeEntry(adapter.getTypeName()));
		aFB.setAdapterDecl(adapter);

		aFB.setName(adapter.getName());
		final PositionableElement position = adapterPositions.get(adapter.getName());
		if (position == null) {
			aFB.setPosition(LibraryElementFactory.eINSTANCE.createPosition());
		} else {
			aFB.setPosition(position.getPosition());
		}
		if (null != aFB.getType() && null != aFB.getType().getInterfaceList()) {
			aFB.setInterface(aFB.getType().getInterfaceList().copy());
		} else {
			// if we don't have a type or interface list set an empty interface list to
			// adapter
			aFB.setInterface(LibraryElementFactory.eINSTANCE.createInterfaceList());
		}
		fbNetwork.getNetworkElements().add(aFB);
	}

	/**
	 * This method converts a FBType to a CompositeFBType.
	 *
	 * @param type - The FBType that is being converted to CompositeFBType
	 *
	 * @return - A FBType that is converted
	 */
	private static FBType convertToCompositeType(final FBType type) {
		final CompositeFBType compositeType = LibraryElementFactory.eINSTANCE.createCompositeFBType();
		copyGeneralTypeInformation(compositeType, type);
		return compositeType;
	}

	/**
	 * This method parses a BasicFBType.
	 *
	 * @param type - the basicFBType that is being parsed
	 *
	 * @throws TypeImportException the FBT import exception
	 * @throws XMLStreamException
	 */
	private void parseBasicFB(final BasicFBType type) throws TypeImportException, XMLStreamException {
		processChildren(LibraryElementTags.BASIC_F_B_ELEMENT, name -> {
			switch (name) {
			case LibraryElementTags.INTERNAL_VARS_ELEMENT:
				parseInternalVars(type);
				break;
			case LibraryElementTags.ECC_ELEMENT:
				parseECC(type);
				break;
			case LibraryElementTags.ALGORITHM_ELEMENT:
				final Algorithm alg = parseAlgorithm();
				if (null != alg) {
					type.getAlgorithm().add(alg);
					final List<ECAction> list = algorithmNameECActionMapping.get(alg.getName());
					if (null != list) {
						for (final ECAction action : list) {
							action.setAlgorithm(alg);
						}
					}
				}
				break;
			default:
				return false;
			}
			return true;
		});

	}

	/**
	 * This method parses a SimpleFBType.
	 *
	 * @param type - the simpleFBType that is being parsed
	 *
	 * @throws TypeImportException the FBT import exception
	 * @throws XMLStreamException
	 */
	private void parseSimpleFB(final SimpleFBType type) throws TypeImportException, XMLStreamException {
		processChildren(LibraryElementTags.SIMPLE_F_B_ELEMENT, name -> {
			switch (name) {
			case LibraryElementTags.INTERNAL_VARS_ELEMENT:
				parseInternalVars(type);
				break;
			case LibraryElementTags.ALGORITHM_ELEMENT:
				type.setAlgorithm(parseAlgorithm());
				break;
			default:
				return false;
			}
			return true;
		});
	}

	/**
	 * This method parses an Algorithm.
	 *
	 * @throws TypeImportException the FBT import exception
	 * @throws XMLStreamException
	 */
	private Algorithm parseAlgorithm() throws TypeImportException, XMLStreamException {
		final String name = getAttributeValue(LibraryElementTags.NAME_ATTRIBUTE);
		final String comment = getAttributeValue(LibraryElementTags.COMMENT_ATTRIBUTE);

		Algorithm retVal = null;
		while (getReader().hasNext()) {
			final int event = getReader().next();
			if (XMLStreamConstants.START_ELEMENT == event) {

				switch (getReader().getLocalName()) {
				case LibraryElementTags.FBD_ELEMENT:
					throw new TypeImportException("Algorithm: Unsupported Algorithmtype (only ST and Other possible)!");
				case LibraryElementTags.ST_ELEMENT:
					retVal = LibraryElementFactory.eINSTANCE.createSTAlgorithm();
					parseST((STAlgorithm) retVal);
					break;
				case LibraryElementTags.LD_ELEMENT:
					throw new TypeImportException("Algorithm: Unsupported Algorithmtype (only ST and Other possible)!");
				case LibraryElementTags.OTHER_ELEMENT:
					retVal = LibraryElementFactory.eINSTANCE.createOtherAlgorithm();
					parseOtherAlg((OtherAlgorithm) retVal);
					break;
				default:
					throw new XMLStreamException("Unexpected xml child (" + getReader().getLocalName() + ") found!"); //$NON-NLS-1$ //$NON-NLS-2$
				}

			} else if (XMLStreamConstants.END_ELEMENT == event) {
				if (!getReader().getLocalName().equals(LibraryElementTags.ALGORITHM_ELEMENT)) {
					throw new XMLStreamException("Unexpected xml end tag found in " //$NON-NLS-1$
							+ LibraryElementTags.ALGORITHM_ELEMENT + ": " + getReader().getLocalName()); //$NON-NLS-1$
				}
				// we came to the end
				break;
			}
		}

		if (null != retVal) {
			retVal.setName(name);
			retVal.setComment(comment);
		}
		return retVal;
	}

	/**
	 * Parses the other alg.
	 *
	 * @param alg the other
	 *
	 * @throws TypeImportException the FBT import exception
	 * @throws XMLStreamException
	 */
	private void parseOtherAlg(final OtherAlgorithm alg) throws TypeImportException, XMLStreamException {
		final String language = getAttributeValue(LibraryElementTags.LANGUAGE_ATTRIBUTE);
		if (null != language) {
			alg.setLanguage(language);
		} else {
			throw new TypeImportException(Messages.FBTImporter_OTHER_ALG_MISSING_LANG_EXCEPTION);
		}

		parseAlgorithmText(alg);
		proceedToEndElementNamed(LibraryElementTags.OTHER_ELEMENT);
	}

	/**
	 * This method parses a STAlgorithm.
	 *
	 * @param st - the STAlgorithm being parsed
	 *
	 * @throws TypeImportException the FBT import exception
	 * @throws XMLStreamException
	 */
	private void parseST(final STAlgorithm st) throws XMLStreamException {
		parseAlgorithmText(st);
		proceedToEndElementNamed(LibraryElementTags.ST_ELEMENT);
	}

	private void parseAlgorithmText(final TextAlgorithm alg) throws XMLStreamException {
		final String text = getAttributeValue(LibraryElementTags.TEXT_ATTRIBUTE);
		if (null != text) {
			alg.setText(text);
		} else {
			final StringBuilder algText = new StringBuilder();
			// the parser my split the content of several parts therefore this while loop
			while ((getReader().hasNext()) && (XMLStreamConstants.CHARACTERS == getReader().next())) {
				algText.append(getReader().getText());
			}
			alg.setText(algText.toString());
		}
	}

	/**
	 * This method parses an ECC.
	 *
	 * @param type - the BasicFBType containing the ECC being parsed
	 *
	 * @throws TypeImportException the FBT import exception
	 * @throws XMLStreamException
	 */
	private void parseECC(final BasicFBType type) throws TypeImportException, XMLStreamException {
		final ECC ecc = LibraryElementFactory.eINSTANCE.createECC();

		processChildren(LibraryElementTags.ECC_ELEMENT, name -> {
			switch (name) {
			case LibraryElementTags.ECSTATE_ELEMENT:
				parseECState(ecc); // IEC 61499 ->
				// "START" state is the first in the list
				break;
			case LibraryElementTags.ECTRANSITION_ELEMENT:
				parseECTransition(ecc);
				break;
			default:
				return false;
			}
			return true;
		});
		type.setECC(ecc);
	}

	/**
	 * This method parses an ECTransition.
	 *
	 * @param ecc - the ECC containing the ECTransition being parsed
	 *
	 * @throws TypeImportException the FBT import exception
	 * @throws XMLStreamException
	 */
	private void parseECTransition(final ECC ecc) throws TypeImportException, XMLStreamException {
		final ECTransition ecTransition = LibraryElementFactory.eINSTANCE.createECTransition();
		final String source = getAttributeValue(LibraryElementTags.SOURCE_ATTRIBUTE);
		if (null != source) {
			final ECState state = ecStates.get(source);
			if (state != null) {
				ecTransition.setSource(state);
			}
		} else {
			throw new TypeImportException(Messages.FBTImporter_ECTRANSITION_SOURCE_EXCEPTION);
		}
		final String destination = getAttributeValue(LibraryElementTags.DESTINATION_ATTRIBUTE);
		if (null != destination) {
			final ECState state = ecStates.get(destination);
			if (state != null) {
				ecTransition.setDestination(state);
			}
		} else {
			throw new TypeImportException(Messages.FBTImporter_ECTRANSITION_DEST_EXCEPTION);
		}
		final String condition = getAttributeValue(LibraryElementTags.CONDITION_ATTRIBUTE);
		if (null != condition) {
			validateTransitionCondition(ecTransition, condition);
		} else {
			throw new TypeImportException(Messages.FBTImporter_ECTRANASITION_CONDITION_EXCEPTION);
		}
		final String comment = getAttributeValue(LibraryElementTags.COMMENT_ATTRIBUTE);
		if (null != comment) {
			ecTransition.setComment(comment);
		}
		getXandY(ecTransition);
		proceedToEndElementNamed(LibraryElementTags.ECTRANSITION_ELEMENT);
		ecc.getECTransition().add(ecTransition);
	}

	private void validateTransitionCondition(final ECTransition ecTransition, final String condition) {
		Event event;
		String expression;

		// first, try splitting according to 1st edition
		String[] split = condition.split("&", 2); //$NON-NLS-1$
		event = inputEvents.get(split[0].trim());
		if (event != null) {
			// remainder is expression
			expression = split.length > 1 ? split[1].trim() : ""; //$NON-NLS-1$
		} else { // otherwise, try splitting according to 2nd edition
			split = condition.split("\\[", 2); //$NON-NLS-1$
			event = inputEvents.get(split[0].trim());
			if (event != null) {
				// remainder is expression (except trailing ']')
				expression = split.length > 1 ? split[1].substring(0, split[1].lastIndexOf(']')).trim() : ""; //$NON-NLS-1$
			} else {
				// no match (all is expression)
				if (condition.startsWith("[")) { //$NON-NLS-1$
					expression = condition.substring(1, condition.lastIndexOf(']'));
				} else {
					expression = condition;
				}
			}
		}

		ecTransition.setConditionEvent(event);
		ecTransition.setConditionExpression(expression);
	}

	/**
	 * This method parses an ECState.
	 *
	 * @param ecc - the ECC containing the ECState being parsed
	 *
	 * @throws TypeImportException the FBT import exception
	 * @throws XMLStreamException
	 */
	private void parseECState(final ECC ecc) throws TypeImportException, XMLStreamException {
		final ECState state = LibraryElementFactory.eINSTANCE.createECState();
		readNameCommentAttributes(state);
		getXandY(state);

		processChildren(LibraryElementTags.ECSTATE_ELEMENT, name -> {
			if (LibraryElementTags.ECACTION_ELEMENT.equals(name)) {
				parseECAction(state);
				return true;
			}
			return false;
		});

		if (ecc.getECState().isEmpty()) {
			ecc.setStart(state);
		}
		ecc.getECState().add(state);
		ecStates.put(state.getName(), state);
	}

	/**
	 * This method parses an ECAction.
	 *
	 * @param type - the ECState belonging to the ECAction being parsed
	 * @throws XMLStreamException
	 */
	private void parseECAction(final ECState type) throws XMLStreamException {
		final ECAction ecAction = LibraryElementFactory.eINSTANCE.createECAction();
		final String algorithm = getAttributeValue(LibraryElementTags.ALGORITHM_ELEMENT);
		if (null != algorithm) {
			if (algorithmNameECActionMapping.containsKey(algorithm)) {
				algorithmNameECActionMapping.get(algorithm).add(ecAction);
			} else {
				final List<ECAction> temp = new ArrayList<>();
				temp.add(ecAction);
				algorithmNameECActionMapping.put(algorithm, temp);
			}
		}
		final String output = getAttributeValue(LibraryElementTags.OUTPUT_ATTRIBUTE);
		if (null != output) {
			final Event outp = outputEvents.get(output);
			if (null != outp) {
				ecAction.setOutput(outp);
			}
		}
		proceedToEndElementNamed(LibraryElementTags.ECACTION_ELEMENT);
		type.getECAction().add(ecAction);
	}

	/**
	 * This method parses Internal Variables of a BaseFBType.
	 *
	 * @param type - the BaseFBType of which the Internal Variables will be parsed
	 *
	 * @throws TypeImportException the FBT import exception
	 * @throws XMLStreamException
	 */
	private void parseInternalVars(final BaseFBType type) throws TypeImportException, XMLStreamException {
		processChildren(LibraryElementTags.INTERNAL_VARS_ELEMENT, name -> {
			if (LibraryElementTags.VAR_DECLARATION_ELEMENT.equals(name)) {
				final VarDeclaration v = parseVarDeclaration();
				type.getInternalVars().add(v);
				return true;
			}
			if (LibraryElementTags.FB_ELEMENT.equals(name)) {
				final FB fb = LibraryElementFactory.eINSTANCE.createFB();
				readNameCommentAttributes(fb);
				final String typeFbElement = getAttributeValue(LibraryElementTags.TYPE_ATTRIBUTE);
				final FBTypePaletteEntry entry = getTypeEntry(typeFbElement);
				if (null != entry) {
					fb.setPaletteEntry(entry);
					fb.setInterface(fb.getType().getInterfaceList().copy());
					parseFBChildren(fb, LibraryElementTags.FB_ELEMENT);
					type.getInternalFbs().add(fb);
					return true;
				}
			}
			return false;
		});
	}

	/**
	 * This method parses a FBType to a BasicFBType.
	 *
	 * @param type - the FBType being parsed to BasicFBType
	 *
	 * @return the basicFBType
	 */
	private static FBType convertToBasicType(final FBType type) {
		final BasicFBType basicType = LibraryElementFactory.eINSTANCE.createBasicFBType();
		copyGeneralTypeInformation(basicType, type);
		return basicType;
	}

	/**
	 * This method parses a FBType to a SimpleFBType.
	 *
	 * @param type - the FBType being parsed to SimpleFBType
	 *
	 * @return the simpleFBType
	 */
	private static FBType convertToSimpleType(final FBType type) {
		final SimpleFBType simpleType = LibraryElementFactory.eINSTANCE.createSimpleFBType();
		copyGeneralTypeInformation(simpleType, type);
		return simpleType;
	}

	/**
	 * This method parses the InterfaceList of a FBType.
	 *
	 * @param interfaceListName
	 *
	 * @throws TypeImportException the FBT import exception
	 * @throws XMLStreamException
	 */
	protected InterfaceList parseInterfaceList(final String interfaceListName)
			throws TypeImportException, XMLStreamException {
		final InterfaceList interfaceList = LibraryElementFactory.eINSTANCE.createInterfaceList();
		final String inputEventListName = getEventInputElement();
		final String outputEventListName = getEventOutputElement();

		processChildren(interfaceListName, name -> {
			if (inputEventListName.equals(name)) {
				parseEventList(interfaceList.getEventInputs(), inputEventListName, true);
			} else if (outputEventListName.equals(name)) {
				parseEventList(interfaceList.getEventOutputs(), outputEventListName, false);
			} else {
				switch (name) {
				case LibraryElementTags.INPUT_VARS_ELEMENT:
					parseVariableList(LibraryElementTags.INPUT_VARS_ELEMENT, interfaceList.getInputVars());
					interfaceList.getInputVars().forEach(v -> {
						variables.put(v.getName(), v);
						v.setIsInput(true);
					});
					break;
				case LibraryElementTags.OUTPUT_VARS_ELEMENT:
					parseVariableList(LibraryElementTags.OUTPUT_VARS_ELEMENT, interfaceList.getOutputVars());
					interfaceList.getOutputVars().forEach(v -> variables.put(v.getName(), v));
					break;
				case LibraryElementTags.SOCKETS_ELEMENT:
					parseAdapterList(interfaceList.getSockets(), LibraryElementTags.SOCKETS_ELEMENT, true);
					break;
				case LibraryElementTags.PLUGS_ELEMENT:
					parseAdapterList(interfaceList.getPlugs(), LibraryElementTags.PLUGS_ELEMENT, false);
					break;
				default:
					return false;
				}
			}
			return true;
		});

		processWiths();
		return interfaceList;
	}

	@SuppressWarnings("static-method")
	protected String getEventOutputElement() {
		return LibraryElementTags.EVENT_OUTPUTS;
	}

	@SuppressWarnings("static-method")
	protected String getEventInputElement() {
		return LibraryElementTags.EVENT_INPUTS_ELEMENT;
	}

	private void parseVariableList(final String nodeName, final EList<VarDeclaration> varList)
			throws TypeImportException, XMLStreamException {
		processChildren(nodeName, name -> {
			if (name.equals(LibraryElementTags.VAR_DECLARATION_ELEMENT)) {
				varList.add(parseVarDeclaration());
				return true;
			}
			return false;
		});
	}

	/**
	 * This method parses Plugs of a FBType.
	 *
	 * @param adpaterListName
	 * @param isInput
	 *
	 * @throws TypeImportException the FBT import exception
	 * @throws XMLStreamException
	 */
	private void parseAdapterList(final EList<AdapterDeclaration> adpaterList, final String adpaterListName,
			final boolean isInput) throws TypeImportException, XMLStreamException {
		processChildren(adpaterListName, name -> {
			if (LibraryElementTags.ADAPTER_DECLARATION_ELEMENT.equals(name)) {
				final AdapterDeclaration a = parseAdapterDeclaration();
				a.setIsInput(isInput);
				adapters.put(a.getName(), a);
				adpaterList.add(a);
				if ((null != a.getType()) && (null != a.getType().getInterfaceList())) {
					if (isInput) {
						addAdapterEventInputs(a.getType().getInterfaceList().getEventOutputs(), a);
						addAdapterEventOutputs(a.getType().getInterfaceList().getEventInputs(), a);
					} else {
						addAdapterEventInputs(a.getType().getInterfaceList().getEventInputs(), a);
						addAdapterEventOutputs(a.getType().getInterfaceList().getEventOutputs(), a);
					}
				}
				return true;
			}
			return false;
		});
	}

	private void addAdapterEventOutputs(final EList<Event> eventOutputs, final AdapterDeclaration a) {
		for (final Event event : eventOutputs) {
			final AdapterEvent ae = createAdapterEvent(event, a);
			outputEvents.put(ae.getName(), ae);
		}

	}

	private void addAdapterEventInputs(final EList<Event> eventInputs, final AdapterDeclaration a) {
		for (final Event event : eventInputs) {
			final AdapterEvent ae = createAdapterEvent(event, a);
			inputEvents.put(ae.getName(), ae);
		}
	}

	private static AdapterEvent createAdapterEvent(final Event event, final AdapterDeclaration a) {
		final AdapterEvent ae = LibraryElementFactory.eINSTANCE.createAdapterEvent();
		ae.setName(event.getName());
		ae.setComment(event.getComment());
		ae.setAdapterDeclaration(a);

		return ae;
	}

	/**
	 * This method parses AdapterDeclaration.
	 *
	 * @param node - the node in the DTD of the AdapterDeclaration being parsed
	 *
	 * @return a - the AdapterDeclaration
	 *
	 * @throws TypeImportException the FBT import exception
	 * @throws XMLStreamException
	 */
	private AdapterDeclaration parseAdapterDeclaration() throws TypeImportException, XMLStreamException {
		final AdapterDeclaration a = LibraryElementFactory.eINSTANCE.createAdapterDeclaration();
		readNameCommentAttributes(a);
		final String typeName = getAttributeValue(LibraryElementTags.TYPE_ATTRIBUTE);
		if (null != typeName) {
			final AdapterTypePaletteEntry entry = getPalette().getAdapterTypeEntry(typeName);
			a.setPaletteEntry(entry);
			AdapterType dataType = null;
			if (null != entry) {
				dataType = entry.getType();
			}
			a.setTypeName(typeName);
			if (null != dataType) {
				a.setType(dataType);
			}
		} else {
			throw new TypeImportException(Messages.FBTImporter_ADAPTER_DECLARATION_TYPE_EXCEPTION);
		}

		final PositionableElement pos = LibraryElementFactory.eINSTANCE.createPositionableElement();
		getXandY(pos);
		adapterPositions.put(a.getName(), pos);

		processChildren(LibraryElementTags.ADAPTER_DECLARATION_ELEMENT, name -> {
			if (LibraryElementTags.ATTRIBUTE_ELEMENT.equals(name)) {
				parseGenericAttributeNode(a);
				proceedToEndElementNamed(LibraryElementTags.ATTRIBUTE_ELEMENT);
				return true;
			}
			return false;
		});

		proceedToEndElementNamed(LibraryElementTags.ADAPTER_DECLARATION_ELEMENT);
		return a;
	}

	protected void processWiths() {
		withList.entrySet().forEach(entry -> {
			final Event e = entry.getKey();
			entry.getValue().forEach(varName -> {
				final VarDeclaration v = variables.get(varName);
				if (null != v) {
					final With withConstruct = LibraryElementFactory.eINSTANCE.createWith();
					withConstruct.setVariables(v);
					e.getWith().add(withConstruct);
				}
			});
		});
	}

	/**
	 * This method parses EventInputs of FBTypes.
	 *
	 * @param isInput
	 *
	 * @throws TypeImportException the FBT import exception
	 * @throws XMLStreamException
	 */
	private void parseEventList(final EList<Event> eventList, final String eventListName, final boolean isInput)
			throws TypeImportException, XMLStreamException {
		final String eventName = getEventElement();

		processChildren(eventListName, name -> {
			if (eventName.equals(name)) {
				final Event e = parseEvent(eventName);
				e.setIsInput(isInput);
				if (isInput) {
					inputEvents.put(e.getName(), e);
				} else {
					outputEvents.put(e.getName(), e);
				}
				eventList.add(e);
				return true;
			}
			return false;
		});
	}

	@SuppressWarnings("static-method")
	protected String getEventElement() {
		return LibraryElementTags.EVENT_ELEMENT;
	}

	private Event parseEvent(final String eventName) throws TypeImportException, XMLStreamException {
		final Event e = LibraryElementFactory.eINSTANCE.createEvent();
		e.setType(EventTypeLibrary.getInstance().getType(null));

		readNameCommentAttributes(e);
		final List<String> withVars = new ArrayList<>();

		processChildren(eventName, name -> {
			switch (name) {
			case LibraryElementTags.WITH_ELEMENT:
				final String val = getAttributeValue(LibraryElementTags.VAR_ATTRIBUTE);
				if (null != val) {
					withVars.add(val);
				}
				proceedToEndElementNamed(LibraryElementTags.WITH_ELEMENT);
				return true;
			case LibraryElementTags.ATTRIBUTE_ELEMENT:
				parseGenericAttributeNode(e);
				proceedToEndElementNamed(LibraryElementTags.ATTRIBUTE_ELEMENT);
				return true;
			default:
				return false;
			}
		});

		if (!withVars.isEmpty()) {
			withList.put(e, withVars);
		}

		return e;
	}
}
