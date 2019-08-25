/** 
 * Copyright (c) 2016 fortiss GmbH.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 * Contributors:
 * Alois Zoitl - initial API and implementation and/or initial documentation
 */
package org.eclipse.fordiac.ide.model.structuredtext.ui

import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory
import org.osgi.framework.Bundle
import com.google.inject.Injector

/** 
 * Factory for generating the injection needed for Expressions
 */
class ExpressionFactory extends AbstractGuiceAwareExecutableExtensionFactory {
	override protected Bundle getBundle() {
		return ExtendedStructuredTextActivator.getInstance().getBundle()
	}

	override protected Injector getInjector() {
		return ExtendedStructuredTextActivator.getInstance().getInjector(
			ExtendedStructuredTextActivator.ORG_FORDIAC_IDE_MODEL_STRUCTUREDTEXT_EXPRESSION)
	}
}
