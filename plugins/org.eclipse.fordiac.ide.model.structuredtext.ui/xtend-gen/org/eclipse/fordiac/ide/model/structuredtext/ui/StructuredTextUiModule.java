/**
 * generated by Xtext
 */
package org.eclipse.fordiac.ide.model.structuredtext.ui;

import com.google.inject.Binder;
import com.google.inject.Provider;
import org.eclipse.fordiac.ide.model.structuredtext.converter.StructuredTextValueConverterService;
import org.eclipse.fordiac.ide.model.structuredtext.resource.StructuredTextResource;
import org.eclipse.fordiac.ide.model.structuredtext.ui.AbstractStructuredTextUiModule;
import org.eclipse.fordiac.ide.model.structuredtext.ui.EmptyAutoEditStrategyProvider;
import org.eclipse.fordiac.ide.model.structuredtext.ui.ExtendedStructuredTextActivator;
import org.eclipse.fordiac.ide.model.structuredtext.ui.preferences.PreferenceInitializer;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtext.conversion.IValueConverterService;
import org.eclipse.xtext.resource.IContainer;
import org.eclipse.xtext.resource.IResourceDescriptions;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.containers.IAllContainersState;
import org.eclipse.xtext.resource.impl.ResourceSetBasedResourceDescriptions;
import org.eclipse.xtext.resource.impl.SimpleResourceDescriptionsBasedContainerManager;
import org.eclipse.xtext.ui.editor.autoedit.AbstractEditStrategyProvider;
import org.eclipse.xtext.ui.editor.autoedit.DefaultAutoEditStrategyProvider;
import org.eclipse.xtext.ui.editor.model.IResourceForEditorInputFactory;
import org.eclipse.xtext.ui.editor.model.ResourceForIEditorInputFactory;
import org.eclipse.xtext.ui.resource.IResourceSetProvider;
import org.eclipse.xtext.ui.resource.SimpleResourceSetProvider;
import org.eclipse.xtext.ui.shared.Access;

/**
 * Use this class to register components to be used within the IDE.
 */
@SuppressWarnings("all")
public class StructuredTextUiModule extends AbstractStructuredTextUiModule {
  public StructuredTextUiModule(final AbstractUIPlugin plugin) {
    super(plugin);
  }
  
  public Class<? extends XtextResource> bindXtextResource() {
    return StructuredTextResource.class;
  }
  
  public Class<? extends IContainer.Manager> bindIContainer$Manager() {
    return SimpleResourceDescriptionsBasedContainerManager.class;
  }
  
  public void configureIResourceDescriptions(final Binder binder) {
    binder.<IResourceDescriptions>bind(IResourceDescriptions.class).to(ResourceSetBasedResourceDescriptions.class);
  }
  
  @Override
  public Provider<? extends IAllContainersState> provideIAllContainersState() {
    return Access.getWorkspaceProjectsState();
  }
  
  public Class<? extends IValueConverterService> bindIValueConverterService() {
    return StructuredTextValueConverterService.class;
  }
  
  @Override
  public Class<? extends IResourceForEditorInputFactory> bindIResourceForEditorInputFactory() {
    return ResourceForIEditorInputFactory.class;
  }
  
  @Override
  public Class<? extends IResourceSetProvider> bindIResourceSetProvider() {
    return SimpleResourceSetProvider.class;
  }
  
  @Override
  public Class<? extends AbstractEditStrategyProvider> bindAbstractEditStrategyProvider() {
    final boolean autoInsert = ExtendedStructuredTextActivator.getInstance().getPreferenceStore().getBoolean(PreferenceInitializer.AUTO_INSERT);
    if ((autoInsert && (autoInsert == true))) {
      return DefaultAutoEditStrategyProvider.class;
    }
    return EmptyAutoEditStrategyProvider.class;
  }
}
